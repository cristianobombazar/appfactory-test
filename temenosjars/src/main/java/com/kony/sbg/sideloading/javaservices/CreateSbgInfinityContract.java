package com.kony.sbg.sideloading.javaservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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
import com.kony.sbg.sideloading.utils.JsonHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.contract.resource.api.ContractResource;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.contract.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.CoreCustomerBusinessDelegate;
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

public class CreateSbgInfinityContract implements JavaService2 {

	private	static int RETRY_COUNTER 	= 0;
	private	static int RETRY_ATTEMPTS	= 5;
	
    LoggerUtil logger = new LoggerUtil(CreateSbgInfinityContract.class);
    SimpleDateFormat idFormatter = new SimpleDateFormat("yyMMddHHmmssSSS");
    String userName;
    MembershipDTO membershipDTO = null;
    private PartyDTO partydetailsDTO = null;
    String companyId = "GB0010001";

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
    
    private	JsonArray getCommunicationArray(Map inputParams, DataControllerRequest request) {
    	JsonArray retval = new JsonArray();
    	logger.debug("getCommunicationArray() ---> START ");
    	
    	JsonObject phoneObj = new JsonObject();
    	String code	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("phoneCountryCode"))) ? (String)inputParams.get("phoneCountryCode") : request.getParameter("phoneCountryCode");
    	String phone	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("phoneNumber"))) ? (String)inputParams.get("phoneNumber") : request.getParameter("phoneNumber");
    	
    	if(!SBGCommonUtils.isStringEmpty(code)) {
    		if(!code.startsWith("+")) {
    			code = "+" + code;
    		}
    	}
    	
    	phoneObj.addProperty("phoneCountryCode", code);
    	phoneObj.addProperty("phoneNumber", phone);
    	retval.add(phoneObj);
    	
    	JsonObject emailObj = new JsonObject();
    	String email	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("email"))) ? (String)inputParams.get("email") : request.getParameter("email");
    	emailObj.addProperty("email", email);
    	retval.add(emailObj);

    	logger.debug("getCommunicationArray() ---> retval: "+retval);
    	return retval;
    }

    private	JsonArray getAddressArray(Map inputParams, DataControllerRequest request) {
    	JsonArray retval = new JsonArray();
    	logger.debug("getAddressArray() ---> START ");
    	    	
    	JsonObject addressObj = new JsonObject();
    	String addressline1	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("addressLine1"))) ? (String)inputParams.get("addressLine1") : request.getParameter("addressLine1");
    	String addressline2	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("addressline2"))) ? (String)inputParams.get("addressline2") : request.getParameter("addressline2");
    	String addressline3	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("addressline3"))) ? (String)inputParams.get("addressline3") : request.getParameter("addressline3");
    	String city			= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("cityName"))) ? (String)inputParams.get("cityName") : request.getParameter("cityName");
    	String state		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("state"))) ? (String)inputParams.get("state") : request.getParameter("state");
    	String country		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("country"))) ? (String)inputParams.get("country") : request.getParameter("country");
    	String zipcode		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("zipCode"))) ? (String)inputParams.get("zipCode") : request.getParameter("zipCode");

        JsonObject postalAddressObj = new JsonObject();
        String postalAddressline1	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalAddressLine1"))) ? (String)inputParams.get("postalAddressLine1") : request.getParameter("postalAddressLine1");
        String postalAddressline2	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalAddressLine2"))) ? (String)inputParams.get("postalAddressLine2") : request.getParameter("postalAddressLine2");
        String postalAddressline3	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalAddressLine3"))) ? (String)inputParams.get("postalAddressLine3") : request.getParameter("postalAddressLine3");
        String postalCity			= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalCityName"))) ? (String)inputParams.get("postalCityName") : request.getParameter("postalCityName");
        String postalState		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalState"))) ? (String)inputParams.get("postalState") : request.getParameter("postalState");
        String postalCountry		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalCountry"))) ? (String)inputParams.get("postalCountry") : request.getParameter("postalCountry");
        String postalZipcode		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("postalZipCode"))) ? (String)inputParams.get("postalZipCode") : request.getParameter("postalZipCode");


        logger.debug("getAddressArray() ---> addressline1: "+addressline1+"; city: "+city+"; state: "+state+"; country: "+country+"; zipcode: "+zipcode +
                "postalAddressLine1: "+postalAddressline1+
                "postalAddressLine2: "+postalAddressline2+
                "postalAddressLine3: "+postalAddressline3+
                "; postalCityName: "+postalCity+"; postalState: "+postalState+"; postalCountry: "+postalCountry+"; postalZipCode: "+postalZipcode);
    	
    	addressObj.addProperty("addressLine1", addressline1);
    	addressObj.addProperty("addressLine2", addressline2);
    	addressObj.addProperty("addressLine3", addressline3);
    	addressObj.addProperty("cityName", city);
    	addressObj.addProperty("state", state);
    	addressObj.addProperty("country", country);
    	addressObj.addProperty("zipCode", zipcode);
    	
    	addressObj.addProperty("isPreferredAddress", "true");

        postalAddressObj.addProperty("addressLine1", postalAddressline1);
        postalAddressObj.addProperty("addressLine2", postalAddressline2);
        postalAddressObj.addProperty("addressLine3", postalAddressline3);
        postalAddressObj.addProperty("cityName", postalCity);
        postalAddressObj.addProperty("state", postalState);
        postalAddressObj.addProperty("country", postalCountry);
        postalAddressObj.addProperty("zipCode", postalZipcode);

        postalAddressObj.addProperty("isPreferredAddress", "false");

    	retval.add(addressObj);
    	retval.add(postalAddressObj);

    	logger.debug("getAddressArray() ---> END retval: "+retval);
    	return retval;
    }

    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();
        logger.debug("CreateSbgInfinityContract.invoke() ---> START ");
    	logger.error("invoke() ---> START - Test Attempt: 8");
    	
    	logger.error("Classloader Name :" + this.getClass().getClassLoader());
    	logger.error("Jar Name :" + this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    	
        try {
            Map inputParams 	= HelperMethods.getInputParamMap((Object[])inputArray);
            String partyId 		= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("partyId"))) ? (String)inputParams.get("partyId") : request.getParameter("partyId");
            String coreCustId 	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("coreCustId"))) ? (String)inputParams.get("coreCustId") : request.getParameter("coreCustId");
            String serviceType 	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("serviceType"))) ? (String)inputParams.get("serviceType") : request.getParameter("serviceType");

            logger.debug("CreateSbgInfinityContract.invoke() ---> inputParams: "+inputParams);
            
            boolean isRequestWithParty = true;
            request.addRequestParam_("isDefaultActionsEnabled", "true");
            
            ContractBusinessDelegate contractBD = (ContractBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
            Map<String, String> contractPayload = new HashMap<>();
            
            try {
                inputArray[1] = contractPayload = this.createContractPayload(partyId, inputParams, request, isRequestWithParty, serviceType);
                logger.debug("invoke() ---> contractPayload: "+contractPayload);
                
                ContractResource resource = (ContractResource)DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
                result = resource.createContract(methodId, inputArray, request, response);
                logger.debug("invoke() ---> result: "+result);
                
                String contractId = result.getParamValueByName("contractId");
                boolean updateStatus = contractBD.updateContractStatus(contractId, "SID_CONTRACT_ACTIVE", this.getHeadersMap(request));
                logger.debug("invoke() ---> updateStatus: "+updateStatus);
                
                //String userCreateStatus = this.createAUserAndAssignTOGivenContract(contractPayload, coreCustId, userName, "Kony@1234", request, methodId, response, isRequestWithParty, partyId, companyId);
                //logger.debug("invoke() ---> userCreateStatus: "+userCreateStatus);
            }
            catch (ApplicationException e) {
                e.getErrorCodeEnum().setErrorCode(result);
            }
            catch (Exception e) {
                //this.logger.error("Error occured", e);
            	logger.error("CreateSbgInfinityContract.invoke() ---> Exception1: "+e.getMessage());
                ErrorCodeEnum.ERR_10390.setErrorCode(result);
            }
            
            JsonObject cPayloadObj =  JsonHelper.convertMap2Json(contractPayload);
            String contractPayloadString = cPayloadObj.toString();
            
            String encContractsCreated = new String(Base64.getEncoder().encode(contractPayloadString.getBytes()));
            logger.debug("invoke() ---> encoded contractsCreated: "+encContractsCreated);
            
            result.addParam(new Param("status", "success"));
            result.addParam(new Param("encContractPayload", encContractsCreated));
        }
        catch (Exception e) {
        	logger.error("CreateSbgInfinityContract.invoke() ---> Exception2: "+e.getMessage());
            //this.logger.error("Error occured", e);
            ErrorCodeEnum.ERR_10390.setErrorCode(result);
        }
        logger.debug("CreateSbgInfinityContract.invoke() ---> END ");
        return result;
    }
    
    
//    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
//        Result result = new Result();
//        JsonArray contractsCreated = new JsonArray();
//        logger.debug("CreateSbgInfinityContract.invoke() ---> START ");
//        try {
//            Map inputParams = HelperMethods.getInputParamMap((Object[])inputArray);
//            String coreCustomersList = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("coreCustomersList"))) ? (String)inputParams.get("coreCustomersList") : request.getParameter("coreCustomersList");
//            String partyIdList = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("partyIdList"))) ? (String)inputParams.get("partyIdList") : request.getParameter("partyIdList");
//            String serviceType = StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("serviceType"))) ? (String)inputParams.get("serviceType") : request.getParameter("serviceType");
//            boolean isRequestWithParty = false;
//            if (StringUtils.isNotBlank((CharSequence)partyIdList)) {
//                isRequestWithParty = true;
//            }
//            JsonParser parser = new JsonParser();
//            request.addRequestParam_("isDefaultActionsEnabled", "true");
//            ContractBusinessDelegate contractBD = (ContractBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
//            JsonArray coreCustomerorPartyListArray = null;
//            coreCustomerorPartyListArray = isRequestWithParty ? parser.parse(partyIdList).getAsJsonArray() : parser.parse(coreCustomersList).getAsJsonArray();
//            logger.debug("invoke() ---> coreCustomerorPartyListArray: "+coreCustomerorPartyListArray);
//            
//            Map<String, String> contractPayload = new HashMap<>();
//            
//            for (JsonElement element : coreCustomerorPartyListArray) {
//                String coreCustomerOrPartyDetails = element.getAsString();
//                String[] corecustomerOrPartyDetailsArray = StringUtils.split((String)coreCustomerOrPartyDetails, (String)":");
//                String coreCustomerIdorPartyId = corecustomerOrPartyDetailsArray[0];
//
//                try {
//                    
//                    inputArray[1] = contractPayload = this.createContractPayload(coreCustomerIdorPartyId, request, isRequestWithParty, serviceType);
//                    logger.debug("invoke() ---> contractPayload: "+contractPayload);
//                    
//                    ContractResource resource = (ContractResource)DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
//                    result = resource.createContract(methodId, inputArray, request, response);
//                    logger.debug("invoke() ---> result: "+result);
//                    
//                    String contractId = result.getParamValueByName("contractId");
//                    boolean updateStatus = contractBD.updateContractStatus(contractId, "SID_CONTRACT_ACTIVE", this.getHeadersMap(request));
//                    logger.debug("invoke() ---> updateStatus: "+updateStatus);
//                    
//                    String partyid = null;
//                    if (isRequestWithParty) {
//                        partyid = coreCustomerIdorPartyId;
//                    }
//                    if (isRequestWithParty && contractPayload.containsKey("coreCustomerIdFromParty")) {
//                        coreCustomerIdorPartyId = contractPayload.get("coreCustomerIdFromParty");
//                    }
//                    logger.debug("invoke() ---> partyid: "+partyid+"; coreCustomerIdorPartyId: "+coreCustomerIdorPartyId);
//                    
//                }
//                catch (ApplicationException e) {
//                    e.getErrorCodeEnum().setErrorCode(result);
//                }
//                catch (Exception e) {
//                    //this.logger.error("Error occured", e);
//                	logger.error("CreateSbgInfinityContract.invoke() ---> Exception1: "+e.getMessage());
//                    ErrorCodeEnum.ERR_10390.setErrorCode(result);
//                }
//                break;
//            }
//            
//            JsonObject cPayloadObj =  JsonHelper.convertMap2Json(contractPayload);
//            String contractPayloadString = cPayloadObj.toString();
//            
//            String encContractsCreated = new String(Base64.getEncoder().encode(contractPayloadString.getBytes()));
//            logger.debug("invoke() ---> encoded contractsCreated: "+encContractsCreated);
//            
//            result.addParam(new Param("status", "success"));
//            result.addParam(new Param("encContractPayload", encContractsCreated));
//        }
//        catch (Exception e) {
//        	logger.error("CreateSbgInfinityContract.invoke() ---> Exception2: "+e.getMessage());
//            //this.logger.error("Error occured", e);
//            ErrorCodeEnum.ERR_10390.setErrorCode(result);
//        }
//        logger.debug("CreateSbgInfinityContract.invoke() ---> END ");
//        return result;
//    }
	
    private Map<String, String> createContractPayload(String coreCustomerIdorPartyId, Map inputParams, DataControllerRequest dcRequest, boolean isRequestWithParty, String serviceType) throws ApplicationException, HttpCallException {
        
    	logger.debug("createContractPayload() ---> START ");
    	JsonObject resultJson;
        HashMap<String, String> contractPayloadMap = new HashMap<String, String>();
        
        JsonArray contractCustomersJsonArray = new JsonArray();
        JsonObject contractCustomer = new JsonObject();
        JsonArray authorizedSignatoryJsonArray = new JsonArray();
        JsonObject authorizedSignatory = new JsonObject();
        JsonArray authorizedSignatoryRolesJsonArray = new JsonArray();
        JsonObject authorizedSignatoryRole = new JsonObject();
        String serviceDefinitionId = "";
        String serviceDefinitionName = "";
        String coreCustomerId = "";
        //coreCustomerId = isRequestWithParty ? 
        //		this.generatePayloadFromParty(coreCustomerIdorPartyId, contractPayloadMap, dcRequest, contractCustomer, serviceType, authorizedSignatory, authorizedSignatoryRole) : 
        //			this.generatePayloadFromT24(coreCustomerIdorPartyId, contractPayloadMap, dcRequest, contractCustomer, serviceType, authorizedSignatory, authorizedSignatoryRole);

        coreCustomerId = this.generatePayloadFromParty(coreCustomerIdorPartyId, contractPayloadMap, dcRequest, contractCustomer, serviceType, authorizedSignatory, authorizedSignatoryRole);
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
        contractPayloadMap.put("communication", getCommunicationArray(inputParams, dcRequest).toString());
        contractPayloadMap.put("address", getAddressArray(inputParams, dcRequest).toString());
        
        JsonArray cifArray = new JsonArray();
        cifArray.add(coreCustomerId);

        //Internal test data to fetch accounts
        //String arrRes = "[{\"accountName\":\"Current Account\",\"accountId\":\"10764242\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"4242424242\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR222500R33EY04DG\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"Current Account\",\"accountId\":\"90394242\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"4242424242\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR22250B9YC908D8B\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"},{\"accountName\":\"Current Account\",\"accountId\":\"42594242\",\"accountType\":\"Checking\",\"accountHolderName\":\"{\\\"username\\\":\\\"\\\",\\\"fullname\\\":\\\"\\\"}\",\"membershipId\":\"4242424242\",\"typeId\":\"1\",\"taxId\":null,\"membershipName\":null,\"arrangementId\":\"ARR22250OJ2BB20ERY\",\"ownership\":\"Owner\",\"accountStatus\":\"ACTIVE\",\"membershipownerDTO\":null,\"isAssociated\":\"false\",\"isSelected\":\"false\",\"isNew\":\"true\"}]";
        //accountsArray = new JsonParser().parse(arrRes).getAsJsonArray();
        
        JsonArray accountsArray = getArrangementAccounts(dcRequest, cifArray);
        logger.debug("createContractPayload() ---> accountsArray: "+accountsArray);
        
        JsonArray accountsArrayNew = removeDuplicateAccounts(accountsArray);
        logger.debug("createContractPayload() ---> accountsArrayNew: "+accountsArrayNew);
        
        contractCustomer.addProperty("isPrimary", "true");
        contractCustomer.addProperty("coreCustomerId", coreCustomerId);
        contractCustomer.add("accounts", (JsonElement)accountsArrayNew);
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
    
    private	JsonArray getArrangementAccounts(DataControllerRequest dcRequest, JsonArray cifArray) {
    	JsonArray accountsArray = null;

		if(RETRY_COUNTER == RETRY_ATTEMPTS) {
			RETRY_COUNTER = 0;
			logger.debug("getArrangementAccounts() ---> RETRY.FAILURE .. REACHED MAXIMUM LIMIT OF "+RETRY_ATTEMPTS);
			return new JsonArray();
		}

		try {
	    	CoreCustomerBusinessDelegate coreCustomerBD = (CoreCustomerBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CoreCustomerBusinessDelegate.class);
	    	DBXResult accountsResult = coreCustomerBD.getCoreCustomerAccounts(cifArray.toString(), this.getHeadersMap(dcRequest));
	    	logger.debug("getArrangementAccounts() ---> accountsResult: "+accountsResult);
	    	
	    	JsonObject resultJson = null;
	    	
	    	if (accountsResult != null && 
	    			JSONUtil.isJsonNotNull((JsonElement)(resultJson = (JsonObject)accountsResult.getResponse())) && 
	        		JSONUtil.hasKey((JsonObject)resultJson, (String)"coreCustomerAccounts") && 
	        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts") != null && 
	        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").size() > 0 && 
	        		JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").get(0).getAsJsonObject().has("accounts")) 
	        {
	            accountsArray = JSONUtil.getJsonArrary((JsonObject)resultJson, (String)"coreCustomerAccounts").get(0).getAsJsonObject().get("accounts").getAsJsonArray();
	            logger.debug("getArrangementAccounts() ---> accountsArray: "+accountsArray);
	        } else {
	        	if (accountsResult != null) { 
	    			logger.debug("getArrangementAccounts() ---> accountsResult.getResponse(): "+accountsResult.getResponse());
	        	}
	        }

	    	if(accountsArray == null || accountsArray.size() == 0) {
	        	++RETRY_COUNTER;
	        	logger.debug("getArrangementAccounts() ---> RETRY_COUNTER: "+RETRY_COUNTER);
				return getArrangementAccounts(dcRequest, cifArray);
	    	}

		} catch (ApplicationException e) {
			logger.error("getArrangementAccounts() ---> EXCEPTION: "+e.getMessage());
			
			++RETRY_COUNTER;
			logger.debug("getArrangementAccounts() ---> RETRY_COUNTER in EXCEPTION: "+RETRY_COUNTER);
			return getArrangementAccounts(dcRequest, cifArray);
		}
    	
		RETRY_COUNTER = 0;
    	return accountsArray;
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
        String contractName = partyDTO.getEntityName();
        if (StringUtils.isEmpty(contractName)) {
            contractName = partyDTO.getNickName();
        }
        if (StringUtils.isEmpty(contractName)) {
            contractName = partyDTO.getFirstName() +" "+partyDTO.getLastName();
        }
        logger.debug("generatePayloadFromParty() ---> contractName: "+contractName);
        
        contractPayloadMap.put("contractName", contractName);
        contractCustomer.addProperty("isBusiness", Boolean.valueOf(isBusiness));
        contractCustomer.addProperty("coreCustomerName", contractName);
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

    	if(RETRY_COUNTER == RETRY_ATTEMPTS) {
			RETRY_COUNTER = 0;
			logger.debug("getCustomerDataFromParty() ---> RETRY.FAILURE for getCustomerDataFromParty .. REACHED MAXIMUM LIMIT OF "+RETRY_ATTEMPTS);
			return partyDTO;
		}
        
        PartyUtils.addJWTAuthHeader(map, (String)"PreLogin");
        //String partyURL = URLFinder.getServerRuntimeProperty((String)"PARTY_HOST_URL") + PartyURLFinder.getServiceUrl((String)"party.get", (String)partyId);
        String partyURL = URLFinder.getServerRuntimeProperty((String)"PARTY_HOST_URL") + "/v5.0.0/party/parties/"+partyId;
        logger.debug("getCustomerDataFromParty() ---> partyURL: "+partyURL);
        
        
        DBXResult response = HTTPOperations.sendHttpRequest((HTTPOperations.operations)HTTPOperations.operations.GET, (String)partyURL, null, map);
        logger.debug("getCustomerDataFromParty() ---> response: "+response);
        
        JsonObject jsonObject = new JsonParser().parse((String)response.getResponse()).getAsJsonObject();
        logger.debug("getCustomerDataFromParty() ---> jsonObject: "+jsonObject);
        
        partyDTO.loadFromJson(jsonObject);
        
        if(StringUtils.isEmpty(partyDTO.getPartyId())) {
        	++RETRY_COUNTER;
        	logger.debug("getCustomerDataFromParty() ---> RETRY_COUNTER: "+RETRY_COUNTER);
			return getCustomerDataFromParty(map, partyId);
        }

        //SBG Dev data
        //String tempPartyRes	= "{\"partyId\":\"2225039056\",\"defaultLanguage\":\"English\",\"partyType\":\"Organisation\",\"partyStatus\":\"Active\",\"firstName\":\"Coca42 Cola42\",\"firstNamesLanguage\":[],\"middleNamesLanguage\":[],\"lastName\":\"Coca42 Cola42\",\"lastNamesLanguage\":[],\"nickName\":\"Coca42 Cola42\",\"nickNamesLanguage\":[],\"aliasLanguage\":[],\"entityName\":\"Coca42 Cola42\",\"entityNamesLanguage\":[],\"nameStartDate\":\"2022-09-06\",\"organisationLegalType\":\"Private Limited Company\",\"incorporationCountry\":\"ZA\",\"dateOfIncorporation\":\"2022-01-01\",\"nameOfIncorporationAuthority\":\"Internal\",\"legalForm\":\"Listed Company\",\"numberOfEmployees\":10,\"creationDateTime\":\"2022-09-05 20:22:10\",\"nationalities\":[{\"partyId\":\"2225039056\",\"country\":\"ZA\"}],\"partyLanguages\":[{\"partyId\":\"2225039056\",\"language\":\"English\"}],\"citizenships\":[],\"partyNames\":[],\"partyIdentifiers\":[{\"partyId\":\"2225039056\",\"type\":\"Passport\",\"status\":\"New\",\"issuingAuthority\":\"Ministry of Foreign Affairs\",\"identifierNumber\":\"GB0010001\",\"issuedDate\":\"2022-01-01\",\"expiryDate\":\"2050-01-01\",\"issuingCountry\":\"ZA\",\"holderName\":\"Coca42 Cola42\",\"documentTagId\":\"4242424242\",\"primary\":true,\"extensionData\":{}}],\"alternateIdentities\":[{\"partyId\":\"2225039056\",\"identityType\":\"BackOfficeIdentifier\",\"identityNumber\":\"GB0010001-4242424242\",\"identitySource\":\"TransactT24\",\"startDate\":\"2020-01-01\",\"extensionData\":{}}],\"personPositions\":[],\"partyLifeCycles\":[],\"otherRiskIndicators\":[],\"residences\":[{\"partyId\":\"2225039056\",\"type\":\"Residence\",\"country\":\"ZA\",\"status\":\"Owner\",\"region\":\"California\",\"statutoryRequirementMet\":true,\"statusComments\":\"Active\",\"endDate\":\"2050-05-05\",\"extensionData\":{}}],\"vulnerabilities\":[],\"otherNames\":[{\"partyId\":\"2225039056\",\"nameType\":\"LegalName\",\"fromDate\":\"2022-01-01\",\"name\":\"Coca42 Cola42\",\"nameLanguage\":\"English\",\"toDate\":\"2050-05-05\",\"extensionData\":{}}],\"addresses\":[{\"partyId\":\"2225039056\",\"addressesReference\":\"AD222485Y0B7\",\"communicationNature\":\"Phone\",\"communicationType\":\"Mobile\",\"primary\":true,\"phoneNo\":\"116614242\",\"externalReference\":[],\"extensionData\":{}},{\"partyId\":\"2225039056\",\"addressesReference\":\"AD222487QHYD\",\"communicationNature\":\"Physical\",\"communicationType\":\"MailingAddress\",\"addressType\":\"Office\",\"primary\":true,\"countryCode\":\"SA\",\"addressFreeFormat\":[{\"addressLinesLanguage\":[]}],\"floorsLanguage\":[],\"buildingNumber\":\"3 Simmonds Street\",\"buildingNamesLanguage\":[],\"streetName\":\"Marshalltown\",\"streetNamesLanguage\":[],\"town\":\"Johannesburg\",\"townsLanguage\":[],\"countrySubdivision\":\"Gauteng\",\"countrySubdivisionsLanguage\":[],\"postalOrZipCode\":\"420010\",\"usePurpose\":\"Home Address\",\"regionCode\":\"015\",\"district\":\"Oakland\",\"districtsLanguage\":[],\"department\":\"Sales\",\"departmentsLanguage\":[],\"subDepartment\":\"Sales Support\",\"subDepartmentsLanguage\":[],\"landmarksLanguage\":[],\"website\":\"temenos.com\",\"externalReference\":[],\"extensionData\":{}},{\"partyId\":\"2225039056\",\"addressesReference\":\"AD22248O37L5\",\"communicationNature\":\"Electronic\",\"communicationType\":\"Email\",\"primary\":true,\"electronicAddress\":\"coca42.cola42@standardbank.co.za\",\"externalReference\":[],\"extensionData\":{}}],\"contactReferences\":[],\"classifications\":[],\"occupations\":[],\"employments\":[],\"observations\":[],\"partyLegalStatus\":[],\"roles\":[],\"taxDetails\":[],\"partyAssessments\":[],\"extensionData\":{}}";
        //JsonObject tempPartyJsonObject = new JsonParser().parse(tempPartyRes).getAsJsonObject();
        //logger.debug("getCustomerDataFromParty() ---> temp <SBG.DEV> jsonObject: "+tempPartyJsonObject);
        //partyDTO.loadFromJson(tempPartyJsonObject);
        
        //SBG internal Dev data
        //String tempPartyRes	= "{\"partyId\":\"2225066409\",\"defaultLanguage\":\"English\",\"partyType\":\"Organisation\",\"partyStatus\":\"Active\",\"firstName\":\"Coca42 Cola42\",\"firstNamesLanguage\":[],\"middleNamesLanguage\":[],\"lastName\":\"Coca42 Cola42\",\"lastNamesLanguage\":[],\"nickName\":\"Coca42 Cola42\",\"nickNamesLanguage\":[],\"aliasLanguage\":[],\"entityName\":\"Coca42 Cola42\",\"entityNamesLanguage\":[],\"nameStartDate\":\"2022-09-07\",\"organisationLegalType\":\"Private Limited Company\",\"incorporationCountry\":\"ZA\",\"dateOfIncorporation\":\"2022-01-01\",\"nameOfIncorporationAuthority\":\"Internal\",\"legalForm\":\"Listed Company\",\"numberOfEmployees\":10,\"creationDateTime\":\"2022-09-07 18:26:49\",\"nationalities\":[{\"partyId\":\"2225066409\",\"country\":\"ZA\"}],\"partyLanguages\":[{\"partyId\":\"2225066409\",\"language\":\"English\"}],\"citizenships\":[],\"partyNames\":[],\"partyIdentifiers\":[{\"partyId\":\"2225066409\",\"type\":\"Passport\",\"status\":\"New\",\"issuingAuthority\":\"Ministry of Foreign Affairs\",\"identifierNumber\":\"GB0010001\",\"issuedDate\":\"2022-01-01\",\"expiryDate\":\"2050-01-01\",\"issuingCountry\":\"ZA\",\"holderName\":\"Coca42 Cola42\",\"documentTagId\":\"4242424242\",\"primary\":true,\"extensionData\":{}}],\"alternateIdentities\":[{\"partyId\":\"2225066409\",\"identityType\":\"BackOfficeIdentifier\",\"identityNumber\":\"GB0010001-4242424242\",\"identitySource\":\"TransactT24\",\"startDate\":\"2020-01-01\",\"extensionData\":{}}],\"personPositions\":[],\"partyLifeCycles\":[],\"otherRiskIndicators\":[],\"residences\":[{\"partyId\":\"2225066409\",\"type\":\"Residence\",\"country\":\"ZA\",\"status\":\"Owner\",\"region\":\"California\",\"statutoryRequirementMet\":true,\"statusComments\":\"Active\",\"endDate\":\"2050-05-05\",\"extensionData\":{}}],\"vulnerabilities\":[],\"otherNames\":[{\"partyId\":\"2225066409\",\"nameType\":\"LegalName\",\"fromDate\":\"2022-01-01\",\"name\":\"Coca42 Cola42\",\"nameLanguage\":\"English\",\"toDate\":\"2050-05-05\",\"extensionData\":{}}],\"addresses\":[{\"partyId\":\"2225066409\",\"addressesReference\":\"AD222502DMVV\",\"communicationNature\":\"Phone\",\"communicationType\":\"Mobile\",\"primary\":true,\"phoneNo\":\"116614242\",\"extensionData\":{}},{\"partyId\":\"2225066409\",\"addressesReference\":\"AD222506NWNO\",\"communicationNature\":\"Electronic\",\"communicationType\":\"Email\",\"primary\":true,\"electronicAddress\":\"coca42.cola42@standardbank.co.za\",\"extensionData\":{}},{\"partyId\":\"2225066409\",\"addressesReference\":\"AD2225084AKO\",\"communicationNature\":\"Physical\",\"communicationType\":\"MailingAddress\",\"addressType\":\"Office\",\"primary\":true,\"countryCode\":\"SA\",\"addressFreeFormat\":[{\"addressLinesLanguage\":[]}],\"floorsLanguage\":[],\"buildingNumber\":\"3 Simmonds Street\",\"buildingNamesLanguage\":[],\"streetName\":\"Marshalltown\",\"streetNamesLanguage\":[],\"town\":\"Johannesburg\",\"townsLanguage\":[],\"countrySubdivision\":\"Gauteng\",\"countrySubdivisionsLanguage\":[],\"postalOrZipCode\":\"420010\",\"usePurpose\":\"Home Address\",\"regionCode\":\"015\",\"district\":\"Oakland\",\"districtsLanguage\":[],\"department\":\"Sales\",\"departmentsLanguage\":[],\"subDepartment\":\"Sales Support\",\"subDepartmentsLanguage\":[],\"landmarksLanguage\":[],\"website\":\"temenos.com\",\"extensionData\":{}}],\"contactReferences\":[],\"classifications\":[],\"occupations\":[],\"employments\":[],\"observations\":[],\"partyLegalStatus\":[],\"roles\":[],\"taxDetails\":[],\"partyAssessments\":[],\"extensionData\":{}}";
        //JsonObject tempPartyJsonObject = new JsonParser().parse(tempPartyRes).getAsJsonObject();
        //logger.debug("getCustomerDataFromParty() ---> temp <INT.DEV> jsonObject: "+tempPartyJsonObject);        
        //partyDTO.loadFromJson(tempPartyJsonObject);
        
        RETRY_COUNTER = 0;
        return partyDTO;
    }

    public void setPartydetailsDTO(PartyDTO partydetailsDTO) {
        this.partydetailsDTO = partydetailsDTO;
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

    private Map<String, Object> getHeadersMap(DataControllerRequest request) {
        Map headers = request.getHeaderMap();
        headers.put("companyId", this.getCompanyId());
        return headers;
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
   				
   				JsonObject jo 	= ele.getAsJsonObject();
   				String acctName	= jo.get("accountName").getAsString();
   				logger.debug("removeDuplicateAccounts() ---> acctName: "+acctName);
   				
   				if(StringUtils.isEmpty(jo.get("accountName").getAsString())) {
   					jo.remove("accountName");
   					jo.addProperty("accountName", "Current Account");
   				}
   				if(jo.get("ownerType") == null || StringUtils.isEmpty(jo.get("ownerType").getAsString())) {
   					jo.remove("ownerType");
   					jo.addProperty("ownerType", "Owner");
   				}
   				retval.add(jo);
   			}
   		}
    	
    	return retval;
    }
    
    //========COPY FOR CUSTOMER USER CREATION===============//

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
    	logger.debug("getUserToCoreCustomerRoles() ---> START ::: authorizedSignatoryRoles: "+authorizedSignatoryRoles);
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
        logger.debug("getUserToCoreCustomerRoles() ---> END ::: coreCustomerRoles: "+coreCustomerRoles);
        return coreCustomerRoles;
    }

    private String createUser(CustomerDTO customerDTO, String createdServiceType, String userName, String password, DataControllerRequest request) throws ApplicationException {
        
    	logger.debug("createUser() ---> START ::: userName: "+userName+"; customerDTO: "+customerDTO);
    	
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
        logger.debug("createUser() ---> customerResult: "+customerResult);
        
        String customerId = (String)customerResult.getResponse();
        if (StringUtils.isBlank((CharSequence)customerId)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10386);
        }
        PasswordHistoryDTO passwordHistoryDTO = new PasswordHistoryDTO();
        passwordHistoryDTO.setId(this.idFormatter.format(new Date()));
        passwordHistoryDTO.setCustomer_id(customerId);
        passwordHistoryDTO.setPreviousPassword(hashedPassword);
        passwordHistoryBD.update(passwordHistoryDTO, this.getHeadersMap(request));
        logger.debug("createUser() ---> passwordHistoryDTO: "+passwordHistoryDTO);
        return customerId;
    }

    private void createUserRoles(String userId, String contractId, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
        
    	logger.debug("createUserRoles() ---> START ::: userId: "+userId+"; contractId: "+contractId+"; userToCoreCustomerRoles: "+userToCoreCustomerRoles);
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
        logger.debug("createUserRoles() ---> END");
    }

    private void assignUserToContractCustomers(String userId, String contractId, Set<String> createdValidContractCoreCustomers, DataControllerRequest request) {
    	logger.debug("assignUserToContractCustomers() ---> START ::: userId: "+userId+"; contractId: "+contractId+"; createdValidContractCoreCustomers: "+createdValidContractCoreCustomers);
        for (String customerId : createdValidContractCoreCustomers) {
            ContractCustomersDTO contractCustomerDTO = new ContractCustomersDTO();
            contractCustomerDTO.setId(this.idFormatter.format(new Date()));
            contractCustomerDTO.setContractId(contractId);
            contractCustomerDTO.setCoreCustomerId(customerId);
            contractCustomerDTO.setCustomerId(userId);
            Map inputParams = DTOUtils.getParameterMap((Object)contractCustomerDTO, (boolean)false);
            contractCustomerDTO.persist(inputParams, this.getHeadersMap(request));
        }
        logger.debug("assignUserToContractCustomers() --->END");
    }

    private void createUserAccounts(String userId, String contractId, Map<String, Set<ContractAccountsDTO>> createdCoreCustomerAccounts, DataControllerRequest request) throws ApplicationException {
    	logger.debug("createUserAccounts() ---> START ::: userId: "+userId+"; contractId: "+contractId+"; createdCoreCustomerAccounts: "+createdCoreCustomerAccounts);
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
        logger.debug("createUserAccounts() ---> END");
    }

    private void createUserActionLimits(String userId, String contractId, Set<String> createdValidContractCoreCustomers, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
    	logger.debug("createUserActionLimits() ---> START ::: userId: "+userId+"; contractId: "+contractId+"; createdValidContractCoreCustomers: "+createdValidContractCoreCustomers+"; userToCoreCustomerRoles: "+userToCoreCustomerRoles);    	
    	CustomerActionsBusinessDelegate customerActionsBD = (CustomerActionsBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerActionsBusinessDelegate.class);
        for (String coreCustomerId : createdValidContractCoreCustomers) {
            customerActionsBD.createCustomerActions(userId, contractId, coreCustomerId, userToCoreCustomerRoles.get(coreCustomerId), new HashSet(), this.getHeadersMap(request));
            customerActionsBD.createCustomerLimitGroupLimits(userId, contractId, coreCustomerId, this.getHeadersMap(request));
        }
        logger.debug("createUserActionLimits() ---> END");
    }

    private void createBackendIdentifierEntry(String backendId, String userId, String contractId, DataControllerRequest request, String companyId, String methodId, DataControllerResponse response, boolean isRequestedwithParty, String partyIdPassed) throws ApplicationException {
    	logger.debug("createBackendIdentifierEntry() ---> START ::: userId: "+userId+"; contractId: "+contractId+"; backendId: "+backendId+"; partyIdPassed: "+partyIdPassed);
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
        logger.debug("createBackendIdentifierEntry() ---> 1 ::: backendIdentifierDTO: "+backendIdentifierDTO);
        
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
            logger.debug("createBackendIdentifierEntry() ---> 2 ::: backendIdentifierDTO: "+backendIdentifierDTO);
        }
        if (null == backendIdentifierDTO || StringUtils.isBlank((CharSequence)backendIdentifierDTO.getId())) {
        	logger.error("createBackendIdentifierEntry() ---> EXCEPTION 1 : COULD NOT CREATE backendIdentifierDTO ");
            //throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
        String partyId = null;
        if (StringUtils.isBlank((CharSequence)partyIdPassed)) {
            partyId = this.getPartyId(backendId, request);
        	logger.debug("createBackendIdentifierEntry() ---> 1 ::: partyId: "+partyId);
            if (StringUtils.isBlank((CharSequence)partyId)) {
                partyId = this.createParty(methodId, request, response);
            	logger.debug("createBackendIdentifierEntry() ---> 2 ::: partyId: "+partyId);
            }
        } else {
            partyId = partyIdPassed;
        	logger.debug("createBackendIdentifierEntry() ---> 3 ::: partyId: "+partyId);
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
            logger.debug("createBackendIdentifierEntry() ---> 3 ::: backendIdentifierDTO: "+backendIdentifierDTO);
        }
        if (null == backendIdentifierDTO || StringUtils.isBlank((CharSequence)backendIdentifierDTO.getId())) {
        	logger.error("createBackendIdentifierEntry() ---> EXCEPTION 2 : COULD NOT CREATE backendIdentifierDTO ");
            //throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
    }

    private String getPartyId(String backendId, DataControllerRequest request) {
    	logger.debug("getPartyId() ---> START ::: backendId: "+backendId);
    	if(RETRY_COUNTER == RETRY_ATTEMPTS) {
			RETRY_COUNTER = 0;
			logger.debug("getPartyId() ---> RETRY.FAILURE for getPartyId .. REACHED MAXIMUM LIMIT OF "+RETRY_ATTEMPTS);
			return null;
		}
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
        
        String retval = JSONUtil.getString((JsonObject)partyJson, (String)"partyId");
        if(StringUtils.isEmpty(retval)) {
        	++RETRY_COUNTER;
        	logger.debug("getPartyId() ---> RETRY_COUNTER: "+RETRY_COUNTER);
			return getPartyId(backendId, request);
        }
        
        RETRY_COUNTER = 0;
        logger.debug("getPartyId() ---> END");
        return JSONUtil.getString((JsonObject)partyJson, (String)"partyId");
    }

    private String createParty(String methodId, DataControllerRequest request, DataControllerResponse response) {
    	logger.debug("createParty() ---> START ");
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
        logger.debug("createParty() ---> END ");
        return null;
    }
}