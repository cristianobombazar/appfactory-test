package com.kony.sbg.sideloading.javaservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.BCrypt;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.contract.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.CoreCustomerBusinessDelegate;
import com.temenos.dbx.product.contract.resource.api.ContractResource;
import com.temenos.dbx.product.dto.AlternateIdentity;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.ContractAccountsDTO;
import com.temenos.dbx.product.dto.ContractCustomersDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerGroupDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.MembershipDTO;
import com.temenos.dbx.product.dto.PartyDTO;
import com.temenos.dbx.product.dto.PasswordHistoryDTO;
import com.temenos.dbx.product.dto.TaxDetails;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerAccountsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerGroupBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.PasswordHistoryBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.UserManagementBusinessDelegate;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.HTTPOperations;
import com.temenos.dbx.usermanagement.resource.api.PartyUserManagementResource;

public class CreateSbgDemoUsers implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(CreateSbgDemoUsers.class);
    SimpleDateFormat idFormatter = new SimpleDateFormat("yyMMddHHmmssSSS");
    String userName;
    MembershipDTO membershipDTO = null;
    private PartyDTO partydetailsDTO = null;
    String companyId;

    public String getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public MembershipDTO getMembershipDTO() {
        return this.membershipDTO;
    }

    public void setMembershipDTO(MembershipDTO membershipDTO) {
        if (this.membershipDTO == null) {
            this.membershipDTO = new MembershipDTO();
        }
        this.membershipDTO = membershipDTO;
    }

    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();
        JsonArray userCreated = new JsonArray();
        logger.debug("CreateSbgDemoUsers.invoke() ---> START ");
        try {
            Map inputParams = HelperMethods.getInputParamMap((Object[])inputArray);
            String coreCustomersList = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("coreCustomersList"))) ? (String)inputParams.get("coreCustomersList") : request.getParameter("coreCustomersList");
            String partyIdList = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("partyIdList"))) ? (String)inputParams.get("partyIdList") : request.getParameter("partyIdList");
            String serviceType = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("serviceType"))) ? (String)inputParams.get("serviceType") : request.getParameter("serviceType");
            boolean isRequestWithParty = false;
            if (StringUtils.isNotBlank((CharSequence)partyIdList)) {
                isRequestWithParty = true;
            }
            JsonParser parser = new JsonParser();
            request.addRequestParam_("isDefaultActionsEnabled", "true");
            ContractBusinessDelegate contractBD = (ContractBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
            JsonArray coreCustomerorPartyListArray = null;
            coreCustomerorPartyListArray = isRequestWithParty ? parser.parse(partyIdList).getAsJsonArray() : parser.parse(coreCustomersList).getAsJsonArray();
            logger.debug("invoke() ---> coreCustomerorPartyListArray: "+coreCustomerorPartyListArray);
            for (JsonElement element : coreCustomerorPartyListArray) {
                String coreCustomerOrPartyDetails = element.getAsString();
                String[] corecustomerOrPartyDetailsArray = StringUtils.split((String)coreCustomerOrPartyDetails, (String)":");
                String coreCustomerIdorPartyId = corecustomerOrPartyDetailsArray[0];
                String userName = null;
                String password = "Kony@1234";
                String companyId = "GB0010001";
                if (corecustomerOrPartyDetailsArray.length > 1) {
                    userName = corecustomerOrPartyDetailsArray[1];
                }
                if (corecustomerOrPartyDetailsArray.length > 2) {
                    password = corecustomerOrPartyDetailsArray[2];
                }
                if (corecustomerOrPartyDetailsArray.length > 3) {
                    companyId = corecustomerOrPartyDetailsArray[3];
                    this.setCompanyId(companyId);
                }
                try {
                    Map<String, String> contractPayload;
                    inputArray[1] = contractPayload = this.createContractPayload(coreCustomerIdorPartyId, request, isRequestWithParty, serviceType);
                    logger.debug("invoke() ---> contractPayload: "+contractPayload);
                    
                    ContractResource resource = (ContractResource)DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
                    result = resource.createContract(methodId, inputArray, request, response);
                    logger.debug("invoke() ---> result: "+result);
                    
                    boolean updateStatus = contractBD.updateContractStatus(result.getParamValueByName("contractId"), "SID_CONTRACT_ACTIVE", this.getHeadersMap(request));
                    logger.debug("invoke() ---> updateStatus: "+updateStatus);
                    
                    String partyid = null;
                    if (isRequestWithParty) {
                        partyid = coreCustomerIdorPartyId;
                    }
                    if (isRequestWithParty && contractPayload.containsKey("coreCustomerIdFromParty")) {
                        coreCustomerIdorPartyId = contractPayload.get("coreCustomerIdFromParty");
                    }
                    logger.debug("invoke() ---> partyid: "+partyid+"; coreCustomerIdorPartyId: "+coreCustomerIdorPartyId);
                    
                    String userCreateStatus = this.createAUserAndAssignTOGivenContract(contractPayload, coreCustomerIdorPartyId, userName, password, request, methodId, response, isRequestWithParty, partyid, companyId);
                    logger.debug("invoke() ---> userCreateStatus: "+userCreateStatus);
                    
                    userCreated.add(this.userName);
                }
                catch (ApplicationException e) {
                    e.getErrorCodeEnum().setErrorCode(result);
                }
                catch (Exception e) {
                    this.logger.error("Error occured", e);
                    ErrorCodeEnum.ERR_10390.setErrorCode(result);
                }
            }
            result.addParam(new Param("status", "success"));
            result.addParam(new Param("userCreated", userCreated.toString()));
            logger.debug("invoke() ---> END");
        }
        catch (Exception e) {
            this.logger.error("Error occured", e);
            ErrorCodeEnum.ERR_10390.setErrorCode(result);
        }
        return result;
    }

    private Map<String, Object> getHeadersMap(DataControllerRequest request) {
        Map headers = request.getHeaderMap();
        headers.put("companyId", this.getCompanyId());
        return headers;
    }

    private String createAUserAndAssignTOGivenContract(Map<String, String> contractPayload, String coreCustomerId, String userName, String password, DataControllerRequest request, String methodId, DataControllerResponse response, boolean isRequestedwithParty, String partyId, String companyIdPassed) throws ApplicationException {
        
    	logger.debug("createAUserAndAssignTOGivenContract() ---> START ");
    	
    	Set createdValidContractCoreCustomers = (Set)request.getAttribute("createdValidCustomers");
        Map createdCoreCustomerAccounts = (Map)request.getAttribute("createdCustomerAccounts");
        String createdServiceType = (String)request.getAttribute("serviceType");
        String contractId = (String)request.getAttribute("contractId");
        String authorizedSignatory = contractPayload.get("authorizedSignatory");
        String authorizedSignatoryRoles = contractPayload.get("authorizedSignatoryRoles");
        List authorizedSignatoryList = DTOUtils.getDTOList((String)authorizedSignatory, CustomerDTO.class);
        if (authorizedSignatoryList == null || authorizedSignatoryList.isEmpty()) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10385);
        }
        Map<String, String> userToCoreCustomerRoles = this.getUserToCoreCustomerRoles(authorizedSignatoryRoles);
        logger.debug("createAUserAndAssignTOGivenContract() ---> userToCoreCustomerRoles: "+userToCoreCustomerRoles);
        
        String userId = this.createUser((CustomerDTO)authorizedSignatoryList.get(0), createdServiceType, userName, password, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> userId: "+userId);
        
        this.createUserRoles(userId, contractId, userToCoreCustomerRoles, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserRoles completed ... ");
        
        this.assignUserToContractCustomers(userId, contractId, createdValidContractCoreCustomers, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> assignUserToContractCustomers completed ... ");
        
        this.createUserAccounts(userId, contractId, createdCoreCustomerAccounts, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserAccounts completed ... ");
        
        this.createUserActionLimits(userId, contractId, createdValidContractCoreCustomers, userToCoreCustomerRoles, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserActionLimits completed ... ");
        
        this.createBackendIdentifierEntry(coreCustomerId, userId, contractId, request, companyIdPassed, methodId, response, isRequestedwithParty, partyId);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createBackendIdentifierEntry completed ... ");

    	logger.debug("createAUserAndAssignTOGivenContract() ---> END ");
    	return userId;
    }

    private Map<String, String> getUserToCoreCustomerRoles(String authorizedSignatoryRoles) throws ApplicationException {
        JsonArray rolesArray;
        if (StringUtils.isBlank((CharSequence)authorizedSignatoryRoles)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10389);
        }
        HashMap<String, String> coreCustomerRoles = new HashMap<String, String>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = rolesArray = parser.parse(authorizedSignatoryRoles).isJsonArray() ? parser.parse(authorizedSignatoryRoles).getAsJsonArray() : new JsonArray();
        if (rolesArray.size() == 0) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10389);
        }
        for (JsonElement element : rolesArray) {
            String roleId;
            JsonObject roleObject = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            String coreCustomerId = roleObject.has("coreCustomerId") ? roleObject.get("coreCustomerId").getAsString() : "";
            String string = roleId = roleObject.has("authorizedSignatoryRoleId") ? roleObject.get("authorizedSignatoryRoleId").getAsString() : "";
            if (StringUtils.isBlank((CharSequence)coreCustomerId) || StringUtils.isBlank((CharSequence)roleId)) continue;
            coreCustomerRoles.put(coreCustomerId, roleId);
        }
        return coreCustomerRoles;
    }

    private Map<String, String> createContractPayload(String coreCustomerIdorPartyId, DataControllerRequest dcRequest, boolean isRequestWithParty, String serviceType) throws ApplicationException, HttpCallException {
        
    	logger.debug("createContractPayload() ---> START ");
    	JsonObject resultJson;
        HashMap<String, String> contractPayloadMap = new HashMap<String, String>();
        JsonArray accountsArray = new JsonArray();
        JsonArray contractCustomersJsonArray = new JsonArray();
        JsonObject contractCustomer = new JsonObject();
        JsonArray authorizedSignatoryJsonArray = new JsonArray();
        JsonObject authorizedSignatory = new JsonObject();
        JsonArray authorizedSignatoryRolesJsonArray = new JsonArray();
        JsonObject authorizedSignatoryRole = new JsonObject();
        String serviceDefinitionId = "";
        String serviceDefinitionName = "";
        String coreCustomerId = "";
        coreCustomerId = isRequestWithParty ? this.generatePayloadFromParty(coreCustomerIdorPartyId, contractPayloadMap, dcRequest, contractCustomer, serviceType, authorizedSignatory, authorizedSignatoryRole) : this.generatePayloadFromT24(coreCustomerIdorPartyId, contractPayloadMap, dcRequest, contractCustomer, serviceType, authorizedSignatory, authorizedSignatoryRole);
        logger.debug("createContractPayload() ---> coreCustomerId: "+coreCustomerId);
        
        String isWealthRetailCustomer = dcRequest.getParameter("isWealthRetailCustomer");
        logger.debug("createContractPayload() ---> isWealthRetailCustomer: "+isWealthRetailCustomer);
        
        if (StringUtils.isNotBlank((CharSequence)isWealthRetailCustomer)) {
            if (Boolean.valueOf(isWealthRetailCustomer).booleanValue()) {
                serviceDefinitionId = "f85d8392-9afe-4128-b23e-a370f138784f";
                serviceDefinitionName = "Retail and wealth online banking";
            } else {
                serviceDefinitionId = "90356097-7fdf-4b8c-89bd-8a1065338a97";
                serviceDefinitionName = "Wealth Online Banking";
            }
            contractPayloadMap.put("serviceDefinitionName", serviceDefinitionName);
            contractPayloadMap.put("serviceDefinitionId", serviceDefinitionId);
            authorizedSignatoryRole.addProperty("authorizedSignatoryRoleId", this.fetchDefaultRoleId(serviceDefinitionId, dcRequest));
        }
        contractPayloadMap.put("isDefaultActionsEnabled", "true");
        contractPayloadMap.put("communication", "[]");
        contractPayloadMap.put("address", "[]");
        CoreCustomerBusinessDelegate coreCustomerBD = (CoreCustomerBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CoreCustomerBusinessDelegate.class);
        JsonArray cifArray = new JsonArray();
        cifArray.add(coreCustomerId);
        DBXResult accountsResult = null;
        try {
            accountsResult = coreCustomerBD.getCoreCustomerAccounts(cifArray.toString(), this.getHeadersMap(dcRequest));
            logger.debug("createContractPayload() ---> 1. accountsResult: "+accountsResult);
        }
        catch (Exception e) {
            e.getMessage();
            logger.debug("createContractPayload() ---> EXCEPTION: "+e.getMessage());
        }
        if (null == accountsResult) {
            accountsResult = coreCustomerBD.getCoreCustomerAccounts(cifArray.toString(), this.getHeadersMap(dcRequest));
            logger.debug("createContractPayload() ---> 2. accountsResult: "+accountsResult);
        }
        if (JSONUtil.isJsonNotNull((JsonElement)(resultJson = (JsonObject)accountsResult.getResponse())) && 
        		JSONUtil.hasKey((JsonObject)resultJson, (String)"coreCustomerAccounts") && 
        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts") != null && 
        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").size() > 0 && 
        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").get(0).getAsJsonObject().has("accounts")) 
        {
            accountsArray = JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").get(0).getAsJsonObject().get("accounts").getAsJsonArray();
            logger.debug("createContractPayload() ---> accountsArray: "+accountsArray);
        }
        
        JsonArray accountsArrayNew = removeDuplicateAccounts(accountsArray);
        logger.debug("createContractPayload() ---> accountsArrayNew: "+accountsArrayNew);
        
        contractCustomer.addProperty("isPrimary", "true");
        contractCustomer.addProperty("coreCustomerId", coreCustomerId);
        contractCustomer.add("accounts", (JsonElement)accountsArray);
        contractCustomer.add("features", this.getFeaturesList(dcRequest));
        contractCustomersJsonArray.add((JsonElement)contractCustomer);
        contractPayloadMap.put("contractCustomers", contractCustomersJsonArray.toString());
        authorizedSignatoryJsonArray.add((JsonElement)authorizedSignatory);
        contractPayloadMap.put("authorizedSignatory", authorizedSignatoryJsonArray.toString());
        authorizedSignatoryRole.addProperty("coreCustomerId", coreCustomerId);
        authorizedSignatoryRolesJsonArray.add((JsonElement)authorizedSignatoryRole);
        contractPayloadMap.put("authorizedSignatoryRoles", authorizedSignatoryRolesJsonArray.toString());
        
        logger.debug("createContractPayload() ---> END ");
        return contractPayloadMap;
    }
    
    private String generatePayloadFromT24(String coreCustomerIdorPartyId, Map<String, String> contractPayloadMap, DataControllerRequest dcRequest, JsonObject contractCustomer, String serviceType, JsonObject authorizedSignatory, JsonObject authorizedSignatoryRole) {
        CoreCustomerBusinessDelegate coreCustomerBusinessDelegate = (CoreCustomerBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CoreCustomerBusinessDelegate.class);
        MembershipDTO corecustomerDetailsDTO = new MembershipDTO();
        try {
            corecustomerDetailsDTO = coreCustomerBusinessDelegate.getMembershipDetails(coreCustomerIdorPartyId, this.getHeadersMap(dcRequest));
        }
        catch (ApplicationException e) {
            this.logger.error("Error Occured while fetching data from T24");
        }
        this.setMembershipDTO(corecustomerDetailsDTO);
        contractPayloadMap.put("contractName", corecustomerDetailsDTO.getFirstName() + corecustomerDetailsDTO.getLastName());
        String serviceDefinitionId = "";
        String serviceDefinitionName = "";
        if ("true".equalsIgnoreCase(corecustomerDetailsDTO.getIsBusiness())) {
            serviceDefinitionId = "83c9b8d7-3715-480e-8c7d-3d6e61c00035";
            serviceDefinitionName = "Corporate Online Banking";
        } else {
            serviceDefinitionId = "5801fa32-a416-45b6-af01-b22e2de93777";
            serviceDefinitionName = "Retail Online Banking";
        }
        contractCustomer.addProperty("isBusiness", corecustomerDetailsDTO.getIsBusiness());
        contractCustomer.addProperty("coreCustomerName", corecustomerDetailsDTO.getFirstName() + corecustomerDetailsDTO.getLastName());
        authorizedSignatory.addProperty("FirstName", corecustomerDetailsDTO.getFirstName());
        authorizedSignatory.addProperty("LastName", corecustomerDetailsDTO.getLastName());
        authorizedSignatory.addProperty("DateOfBirth", corecustomerDetailsDTO.getDateOfBirth());
        authorizedSignatory.addProperty("Ssn", corecustomerDetailsDTO.getTaxId());
        contractPayloadMap.put("serviceDefinitionName", serviceDefinitionName);
        contractPayloadMap.put("serviceDefinitionId", serviceDefinitionId);
        authorizedSignatoryRole.addProperty("authorizedSignatoryRoleId", this.fetchDefaultRoleId(serviceDefinitionId, dcRequest));
        return coreCustomerIdorPartyId;
    }

    private String generatePayloadFromParty(String coreCustomerIdorPartyId, Map<String, String> contractPayloadMap, DataControllerRequest dcRequest, JsonObject contractCustomer, String serviceType, JsonObject authorizedSignatory, JsonObject authorizedSignatoryRole) {
        
    	logger.debug("generatePayloadFromParty() ---> START ");
    	
    	TaxDetails taxDetails;
        boolean isBusiness;
        String[] custsplitarray;
        AlternateIdentity aIdentity;
        String coreCustomerId = "";
        PartyDTO partyDTO = this.getCustomerDataFromParty(dcRequest.getHeaderMap(), coreCustomerIdorPartyId);
        logger.debug("generatePayloadFromParty() ---> partyDTO: "+partyDTO.toStringJson());
        
        this.setPartydetailsDTO(partyDTO);
        if (partyDTO.getAlternateIdentities() != null && 
        		(coreCustomerId = (aIdentity = (AlternateIdentity)partyDTO.getAlternateIdentities().get(0)).getIdentityNumber()) != null && 
        		(custsplitarray = coreCustomerId.split("-")) != null && custsplitarray.length == 2) 
        {
            coreCustomerId = custsplitarray[1];
            logger.debug("generatePayloadFromParty() ---> coreCustomerId: "+coreCustomerId);
        }
        String serviceDefinitionId = "5801fa32-a416-45b6-af01-b22e2de93777";
        String serviceDefinitionName = "Retail Online Banking";
        boolean bl = isBusiness = serviceType != null && serviceType.equalsIgnoreCase("business");
        logger.debug("generatePayloadFromParty() ---> bl: "+bl);
        
        if (isBusiness) {
            serviceDefinitionId = "707dfea8-d0fe-4154-89c3-e7d7ef2ee16a";
            serviceDefinitionName = "SME online banking";
        }
        contractPayloadMap.put("coreCustomerIdFromParty", coreCustomerId);
        String contractName = "";
        if (partyDTO.getFirstName() != null) {
            contractName = contractName + partyDTO.getFirstName();
        }
        if (partyDTO.getLastName() != null) {
            contractName = contractName + partyDTO.getLastName();
        }
        logger.debug("generatePayloadFromParty() ---> contractName: "+contractName);
        
        contractPayloadMap.put("contractName", contractName);
        contractCustomer.addProperty("isBusiness", Boolean.valueOf(isBusiness));
        contractCustomer.addProperty("coreCustomerName", partyDTO.getFirstName() + partyDTO.getLastName());
        authorizedSignatory.addProperty("FirstName", partyDTO.getFirstName());
        authorizedSignatory.addProperty("LastName", partyDTO.getLastName());
        authorizedSignatory.addProperty("DateOfBirth", partyDTO.getDateOfBirth());
        authorizedSignatoryRole.addProperty("authorizedSignatoryRoleId", this.fetchDefaultRoleId(serviceDefinitionId, dcRequest));
        contractPayloadMap.put("serviceDefinitionName", serviceDefinitionName);
        contractPayloadMap.put("serviceDefinitionId", serviceDefinitionId);
        String ssn = "";
        if (partyDTO.getTaxDetails() != null && (taxDetails = (TaxDetails)partyDTO.getTaxDetails().get(0)) != null) {
            ssn = taxDetails.getTaxId();
        }
        authorizedSignatory.addProperty("Ssn", ssn);
        logger.debug("generatePayloadFromParty() ---> ssn: "+ssn);
        
        logger.debug("generatePayloadFromParty() ---> END ");
        return coreCustomerId;
    }

    private PartyDTO getCustomerDataFromParty(Map<String, Object> map, String partyId) {
        PartyDTO partyDTO = new PartyDTO();
        PartyUtils.addJWTAuthHeader(map, (String)"PreLogin");
        String partyURL = URLFinder.getServerRuntimeProperty((String)"PARTY_HOST_URL") + PartyURLFinder.getServiceUrl((String)"party.get", (String)partyId);
        DBXResult response = HTTPOperations.sendHttpRequest((HTTPOperations.operations)HTTPOperations.operations.GET, (String)partyURL, null, map);
        JsonObject jsonObject = new JsonParser().parse((String)response.getResponse()).getAsJsonObject();
        partyDTO.loadFromJson(jsonObject);
        return partyDTO;
    }

    private String fetchDefaultRoleId(String serviceDefinitionId, DataControllerRequest request) {
        String filter = "serviceDefinitionId eq " + serviceDefinitionId + " and " + "isDefaultGroup" + " eq " + '1';
        HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("$filter", filter);
        JsonObject jsonResponse = ServiceCallHelper.invokeServiceAndGetJson(input, (Map)request.getHeaderMap(), (String)"groupservicedefinition.getRecord");
        if (jsonResponse.has("groupservicedefinition") && jsonResponse.get("groupservicedefinition").isJsonArray() && jsonResponse.get("groupservicedefinition").getAsJsonArray().size() > 0) {
            JsonObject groupServiceDefinition = jsonResponse.get("groupservicedefinition").getAsJsonArray().get(0).getAsJsonObject();
            return JSONUtil.getString((JsonObject)groupServiceDefinition, (String)"Group_id");
        }
        return "";
    }

    private JsonElement getFeaturesList(DataControllerRequest dcRequest) throws HttpCallException {
        JsonArray features = new JsonArray();
        Result result = HelperMethods.callGetApi((DataControllerRequest)dcRequest, null, (Map)HelperMethods.getHeaders((DataControllerRequest)dcRequest), (String)"feature.readRecord");
        for (Record r : result.getDatasetById("feature").getAllRecords()) {
            JsonObject json = new JsonObject();
            json.addProperty("featureId", r.getParamValueByName("id"));
            features.add((JsonElement)json);
        }
        return features;
    }

    private String createUser(CustomerDTO customerDTO, String createdServiceType, String userName, String password, DataControllerRequest request) throws ApplicationException {
        UserManagementBusinessDelegate customerBD = (UserManagementBusinessDelegate)((BusinessDelegateFactory)DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)).getBusinessDelegate(UserManagementBusinessDelegate.class);
        PasswordHistoryBusinessDelegate passwordHistoryBD = (PasswordHistoryBusinessDelegate)((BusinessDelegateFactory)DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)).getBusinessDelegate(PasswordHistoryBusinessDelegate.class);
        String salt = BCrypt.gensalt((int)11);
        String hashedPassword = BCrypt.hashpw((String)password, (String)salt);
        String id = HelperMethods.generateUniqueCustomerId((DataControllerRequest)request);
        this.userName = StringUtils.isBlank((CharSequence)userName) ? HelperMethods.generateUniqueUserName((DataControllerRequest)request) : userName;
        customerDTO.setId(id);
        customerDTO.setUserName(this.userName);
        customerDTO.setIsNew(Boolean.valueOf(true));
        customerDTO.setStatus_id("SID_CUS_ACTIVE");
        createdServiceType = "TYPE_ID_RETAIL";
        customerDTO.setCustomerType_id(createdServiceType);
        customerDTO.setIsEnrolled(Boolean.valueOf(true));
        customerDTO.setPassword(hashedPassword);
        DBXResult customerResult = customerBD.update(customerDTO, this.getHeadersMap(request));
        String customerId = (String)customerResult.getResponse();
        if (StringUtils.isBlank((CharSequence)customerId)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10386);
        }
        PasswordHistoryDTO passwordHistoryDTO = new PasswordHistoryDTO();
        passwordHistoryDTO.setId(this.idFormatter.format(new Date()));
        passwordHistoryDTO.setCustomer_id(customerId);
        passwordHistoryDTO.setPreviousPassword(hashedPassword);
        passwordHistoryBD.update(passwordHistoryDTO, this.getHeadersMap(request));
        return customerId;
    }

    private void createUserRoles(String userId, String contractId, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
        CustomerGroupBusinessDelegate customerGroupBD = (CustomerGroupBusinessDelegate)((BusinessDelegateFactory)DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)).getBusinessDelegate(CustomerGroupBusinessDelegate.class);
        CustomerGroupDTO dto = new CustomerGroupDTO();
        dto.setCustomerId(userId);
        dto.setContractId(contractId);
        for (Map.Entry<String, String> entry : userToCoreCustomerRoles.entrySet()) {
            String coreCustomerId = entry.getKey();
            String roleId = entry.getValue();
            dto.setCoreCustomerId(coreCustomerId);
            dto.setGroupId(roleId);
            customerGroupBD.createCustomerGroup(dto, this.getHeadersMap(request));
        }
    }

    private void assignUserToContractCustomers(String userId, String contractId, Set<String> createdValidContractCoreCustomers, DataControllerRequest request) {
        for (String customerId : createdValidContractCoreCustomers) {
            ContractCustomersDTO contractCustomerDTO = new ContractCustomersDTO();
            contractCustomerDTO.setId(this.idFormatter.format(new Date()));
            contractCustomerDTO.setContractId(contractId);
            contractCustomerDTO.setCoreCustomerId(customerId);
            contractCustomerDTO.setCustomerId(userId);
            Map inputParams = DTOUtils.getParameterMap((Object)contractCustomerDTO, (boolean)false);
            contractCustomerDTO.persist(inputParams, this.getHeadersMap(request));
        }
    }

    private void createUserAccounts(String userId, String contractId, Map<String, Set<ContractAccountsDTO>> createdCoreCustomerAccounts, DataControllerRequest request) throws ApplicationException {
        CustomerAccountsBusinessDelegate customerAccountsBD = (CustomerAccountsBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerAccountsBusinessDelegate.class);
        for (Map.Entry<String, Set<ContractAccountsDTO>> entry : createdCoreCustomerAccounts.entrySet()) {
            String coreCustomerId = entry.getKey();
            HashSet<String> accounts = new HashSet<String>();
            Set<ContractAccountsDTO> coreCustomerAccounts = entry.getValue();
            for (ContractAccountsDTO dto : coreCustomerAccounts) {
                accounts.add(dto.getAccountId());
            }
            customerAccountsBD.createCustomerAccounts(userId, contractId, coreCustomerId, accounts, this.getHeadersMap(request));
        }
    }

    private void createUserActionLimits(String userId, String contractId, Set<String> createdValidContractCoreCustomers, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
        CustomerActionsBusinessDelegate customerActionsBD = (CustomerActionsBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerActionsBusinessDelegate.class);
        for (String coreCustomerId : createdValidContractCoreCustomers) {
            customerActionsBD.createCustomerActions(userId, contractId, coreCustomerId, userToCoreCustomerRoles.get(coreCustomerId), new HashSet(), this.getHeadersMap(request));
            customerActionsBD.createCustomerLimitGroupLimits(userId, contractId, coreCustomerId, this.getHeadersMap(request));
        }
    }

    private void createBackendIdentifierEntry(String backendId, String userId, String contractId, DataControllerRequest request, String companyId, String methodId, DataControllerResponse response, boolean isRequestedwithParty, String partyIdPassed) throws ApplicationException {
        if (StringUtils.isBlank((CharSequence)backendId)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setCustomer_id(userId);
        backendIdentifierDTO.setBackendId(backendId);
        backendIdentifierDTO.setBackendType("CORE");
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setContractId(contractId);
        backendIdentifierDTO.setIdentifier_name("customer_id");
        backendIdentifierDTO.setCompanyId(companyId);
        if (StringUtils.isBlank((CharSequence)companyId)) {
            backendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler.getValue((String)"BRANCH_ID_REFERENCE"));
        }
        Map input = DTOUtils.getParameterMap((Object)backendIdentifierDTO, (boolean)true);
        backendIdentifierDTO.setIsNew(Boolean.valueOf(true));
        backendIdentifierDTO.persist(input, this.getHeadersMap(request));
        String IS_Integrated = Boolean.toString(IntegrationTemplateURLFinder.isIntegrated);
        if (StringUtils.isNotBlank((CharSequence)IS_Integrated) && IS_Integrated.equalsIgnoreCase("true") && !isRequestedwithParty) {
            backendIdentifierDTO = new BackendIdentifierDTO();
            backendIdentifierDTO.setIsNew(Boolean.valueOf(true));
            backendIdentifierDTO.setId(UUID.randomUUID().toString());
            backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL((String)"BackendType"));
            backendIdentifierDTO.setIdentifier_name(IntegrationTemplateURLFinder.getBackendURL((String)"BackendCustomerIdentifierName"));
            backendIdentifierDTO.setCustomer_id(userId);
            backendIdentifierDTO.setContractId(contractId);
            backendIdentifierDTO.setSequenceNumber("1");
            backendIdentifierDTO.setCompanyId(companyId);
            backendIdentifierDTO.setBackendId(backendId);
            if (StringUtils.isBlank((CharSequence)companyId)) {
                backendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler.getValue((String)"BRANCH_ID_REFERENCE"));
            }
            input = DTOUtils.getParameterMap((Object)backendIdentifierDTO, (boolean)true);
            backendIdentifierDTO.persist(input, new HashMap());
        }
        if (null == backendIdentifierDTO || StringUtils.isBlank((CharSequence)backendIdentifierDTO.getId())) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
        String partyId = null;
        if (StringUtils.isBlank((CharSequence)partyIdPassed)) {
            partyId = this.getPartyId(backendId, request);
            if (StringUtils.isBlank((CharSequence)partyId)) {
                partyId = this.createParty(methodId, request, response);
            }
        } else {
            partyId = partyIdPassed;
        }
        if (StringUtils.isNotBlank((CharSequence)partyId)) {
            backendIdentifierDTO = new BackendIdentifierDTO();
            backendIdentifierDTO.setIsNew(Boolean.valueOf(true));
            backendIdentifierDTO.setId(UUID.randomUUID().toString());
            backendIdentifierDTO.setBackendType("PARTY");
            backendIdentifierDTO.setIdentifier_name("customer_id");
            backendIdentifierDTO.setCustomer_id(userId);
            backendIdentifierDTO.setContractId(contractId);
            backendIdentifierDTO.setSequenceNumber("1");
            backendIdentifierDTO.setCompanyId(companyId);
            backendIdentifierDTO.setBackendId(partyId);
            if (StringUtils.isBlank((CharSequence)companyId)) {
                backendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler.getValue((String)"BRANCH_ID_REFERENCE"));
            }
            input = DTOUtils.getParameterMap((Object)backendIdentifierDTO, (boolean)true);
            backendIdentifierDTO.persist(input, new HashMap());
        }
        if (null == backendIdentifierDTO || StringUtils.isBlank((CharSequence)backendIdentifierDTO.getId())) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
    }

    private String createParty(String methodId, DataControllerRequest request, DataControllerResponse response) {
        MembershipDTO dto = this.getMembershipDTO();
        HashMap<String, String> inputParams = new HashMap<String, String>();
        inputParams.put("partyType", "Individual");
        inputParams.put("firstName", dto.getFirstName());
        inputParams.put("lastName", dto.getLastName());
        JsonArray jsonarray = new JsonArray();
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("phoneAddressType", "Home Number");
        String phone = dto.getPhone();
        boolean isFormat = false;
        String[] array = new String[2];
        if (phone.contains("-")) {
            isFormat = true;
            array = phone.split("-");
        }
        jsonobject.addProperty("internationalPhoneNo", isFormat ? array[1] : dto.getPhone());
        jsonobject.addProperty("iddPrefixPhone", isFormat ? array[0] : "+91");
        jsonobject.addProperty("nationalPhoneNo", isFormat ? array[1] : dto.getPhone());
        jsonarray.add((JsonElement)jsonobject);
        inputParams.put("phoneAddress", jsonarray.toString());
        inputParams.put("partyStatus", "Active");
        jsonarray = new JsonArray();
        jsonobject = new JsonObject();
        jsonobject.addProperty("startDate", HelperMethods.getCurrentDate());
        jsonobject.addProperty("firstName", dto.getFirstName());
        jsonobject.addProperty("lastName", dto.getLastName());
        jsonobject.addProperty("middleName", "");
        jsonobject.addProperty("suffix", "");
        jsonarray.add((JsonElement)jsonobject);
        inputParams.put("partyNames", jsonarray.toString());
        jsonarray = new JsonArray();
        jsonobject = new JsonObject();
        jsonobject.addProperty("physicalAddressType", "Office");
        jsonobject.addProperty("startDate", HelperMethods.getCurrentDate());
        jsonobject.addProperty("reliabilityType", "Confirmed");
        jsonobject.addProperty("contactName", dto.getFirstName() + dto.getLastName());
        jsonobject.addProperty("endDate", HelperMethods.getCurrentDate());
        jsonobject.addProperty("countryCode", "US");
        jsonobject.addProperty("town", dto.getCityName());
        jsonobject.addProperty("postalOrZipCode", dto.getZipCode());
        jsonobject.addProperty("addressCountry", dto.getCountry());
        jsonarray.add((JsonElement)jsonobject);
        inputParams.put("contactAddress", jsonarray.toString());
        Object[] inputArray1 = new Object[3];
        inputArray1[1] = inputParams;
        PartyUserManagementResource resource = (PartyUserManagementResource)DBPAPIAbstractFactoryImpl.getResource(PartyUserManagementResource.class);
        Result partyCreateResponse = resource.partyCreate(methodId, inputArray1, request, response);
        return null;
    }

    public static Map<String, Object> addJWTAuthHeader(DataControllerRequest request, Map<String, Object> headers, String flow_type) {
        String authToken = URLFinder.getServerRuntimeProperty((String)"PARTY_AUTH_TOKEN");
        headers.put("Authorization", authToken);
        String deploymentPlatform = URLFinder.getServerRuntimeProperty((String)"PARTYMS_DEPLOYMENT_PLATFORM");
        if (StringUtils.isNotBlank((CharSequence)deploymentPlatform)) {
            if (StringUtils.equals((CharSequence)deploymentPlatform, (CharSequence)"aws")) {
                headers.put("x-api-key", URLFinder.getServerRuntimeProperty((String)"PARTYMS_AUTHORIZATION_KEY"));
            }
            if (StringUtils.equals((CharSequence)deploymentPlatform, (CharSequence)"azure")) {
                headers.put("x-functions-key", URLFinder.getServerRuntimeProperty((String)"PARTYMS_AUTHORIZATION_KEY"));
            }
        }
        return headers;
    }

    private String getPartyId(String backendId, DataControllerRequest request) {
        String companyId = EnvironmentConfigurationsHandler.getServerProperty((String)"BRANCH_ID_REFERENCE");
        String id = companyId + "-" + backendId;
        Map<String, Object> headers = CreateSbgDemoUsers.addJWTAuthHeader(request, request.getHeaderMap(), "PreLogin");
        String queryParams = "";
        queryParams = queryParams + "alternateIdentifierNumber=" + id;
        queryParams = queryParams + "&";
        queryParams = queryParams + "alternateIdentifierType=BackOfficeIdentifier";
        DBXResult dbxResult = new DBXResult();
        JsonObject partyJson = new JsonObject();
        JsonArray partyJsonArray = new JsonArray();
        if (queryParams.length() > 1) {
            try {
                JsonObject partyResponseObject;
                JsonElement partyResponse;
                String partyURL = URLFinder.getServerRuntimeProperty((String)"PARTY_HOST_URL") + "/v4.0.0/party/parties?" + queryParams;
                dbxResult = HTTPOperations.sendHttpRequest((HTTPOperations.operations)HTTPOperations.operations.GET, (String)partyURL, null, headers);
                if (dbxResult.getResponse() != null && (partyResponse = new JsonParser().parse((String)dbxResult.getResponse())).isJsonObject() && (partyResponseObject = partyResponse.getAsJsonObject()).has("parties") && partyResponseObject.get("parties").isJsonArray() && partyResponseObject.get("parties").getAsJsonArray().size() > 0) {
                    partyJsonArray = partyResponseObject.get("parties").getAsJsonArray();
                    partyJson = partyJsonArray.get(0).getAsJsonObject();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return JSONUtil.getString((JsonObject)partyJson, (String)"partyId");
    }

    public PartyDTO getPartydetailsDTO() {
        return this.partydetailsDTO;
    }

    public void setPartydetailsDTO(PartyDTO partydetailsDTO) {
        this.partydetailsDTO = partydetailsDTO;
    }

    private	JsonArray removeDuplicateAccounts(JsonArray accountsArray) {
    	JsonArray retval 	= new JsonArray();
    	List<String> list 	= new ArrayList<>();
    	
   		int count = accountsArray == null ? 0 : accountsArray.size();
   		for(int i=0 ; i<count ; ++i) {
   			JsonElement ele 	= accountsArray.get(i);
   			JsonObject obj 		= ele.getAsJsonObject();
   			String accountid	= obj.get("accountId").getAsString();
   			
   			if(!list.contains(accountid)) {
   				list.add(accountid);
   				retval.add(ele);
   			}
   		}
    	
    	return retval;
    }
    
    public static void main(String s[]) {
    	String str = "[{\"accountName\":\"\",\"accountId\":\"10762020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR222411N353LB9W4\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"\",\"accountId\":\"20592020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR222411Q39FKSYWT\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"\",\"accountId\":\"90392020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR222418E18FLN450\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"\",\"accountId\":\"10762020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR22241G71EDJ29R9\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"\",\"accountId\":\"90392020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR22241LZ37FGRG3K\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"\",\"accountId\":\"20592020\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"2020202020\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR22241X43AAM2KPG\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"}]";
    	JsonArray jobj = new JsonParser().parse(str).getAsJsonArray(); 
    	
    	CreateSbgDemoUsers obj = new CreateSbgDemoUsers();
    	JsonArray retval = obj.removeDuplicateAccounts(jobj);
    	System.out.println(retval);
    }

}
