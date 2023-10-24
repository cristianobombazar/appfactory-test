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
import com.kony.dbputilities.util.BCrypt;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.sideloading.utils.JsonHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.contract.businessdelegate.api.ContractAccountsBusinessDelegate;
import com.temenos.dbx.product.dto.AddressDTO;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.ContractAccountsDTO;
import com.temenos.dbx.product.dto.ContractCustomersDTO;
import com.temenos.dbx.product.dto.CustomerAddressDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerGroupDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.MembershipDTO;
import com.temenos.dbx.product.dto.PasswordHistoryDTO;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerAccountsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerGroupBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.PasswordHistoryBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.UserManagementBusinessDelegate;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.HTTPOperations;
import com.temenos.dbx.product.utils.InfinityConstants;

public class CreateSbgInfinityUsers implements JavaService2 {
	
	private	LoggerUtil logger = new LoggerUtil(CreateSbgInfinityUsers.class);	
	
	private	static int RETRY_COUNTER 	= 0;
	private	static int RETRY_ATTEMPTS	= 5;

	SimpleDateFormat idFormatter = new SimpleDateFormat("yyMMddHHmmssSSS");
    String userName;
    MembershipDTO membershipDTO = null;
    String companyId = "GB0010001";
    
    private	String	contractId4mInput	= "";
    private	String	partyId4mInput		= ""; 
    private	String	coreCustId4mInput	= "";
    private	Map<String, String> userDataPayload = null;

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
        logger.debug("CreateSbgInfinityUsers.invoke() ---> START ");
        try {
            Map inputParams = HelperMethods.getInputParamMap((Object[])inputArray);
            
            String contractPayloadStr 	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("contractPayload"))) ? (String)inputParams.get("contractPayload") : request.getParameter("contractPayload");
            contractId4mInput			= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("contractId"))) ? (String)inputParams.get("contractId") : request.getParameter("contractId");
            partyId4mInput 				= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("partyId"))) ? (String)inputParams.get("partyId") : request.getParameter("partyId");
            coreCustId4mInput 			= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("coreCustomerNo"))) ? (String)inputParams.get("coreCustomerNo") : request.getParameter("coreCustomerNo");
            String userDataPayloadStr 	= StringUtils.isNotBlank((CharSequence)((CharSequence)inputParams.get("userDataPayload"))) ? (String)inputParams.get("userDataPayload") : request.getParameter("userDataPayload");
            
            logger.debug("invoke() ---> 1 contractPayloadStr: "+contractPayloadStr);
            logger.debug("invoke() ---> 1 contractIdStr: "+contractId4mInput+"; partyIdStr: "+partyId4mInput+"; coreCustomerNoStr: "+coreCustId4mInput);
            logger.debug("invoke() ---> 1 userDataPayloadStr: "+userDataPayloadStr);
            
            boolean isRequestWithParty = true;
            
            String decContractPayloadStr = new String(Base64.getDecoder().decode(contractPayloadStr));
            logger.debug("invoke() ---> decContractPayloadStr: "+decContractPayloadStr);
            
            JsonObject contractPayloadObj = (JsonObject)new JsonParser().parse(decContractPayloadStr);
            logger.debug("invoke() ---> contractPayloadObj: "+contractPayloadObj);
            
            JsonObject userDataPayloadObj = (JsonObject)new JsonParser().parse(userDataPayloadStr);
            logger.debug("invoke() ---> userDataPayloadObj: "+userDataPayloadObj);
            
            Map<String, String> contractPayload = JsonHelper.convertJson2Map4Contract(contractPayloadObj);
            logger.debug("invoke() ---> converted payload converted to map contractPayload: "+contractPayload);
            
            userDataPayload 					= JsonHelper.convertJson2Map4Users(userDataPayloadObj);
            logger.debug("invoke() ---> userdata payload converted to map userDataPayload: "+userDataPayload);
            
            String coreCustomerIdorPartyId = coreCustId4mInput;
            String password = "Kony@1234";
            String companyId = "GB0010001";
            String userPartyId = userDataPayload.get("userPartyId");
            userName	= userDataPayload.get("username");
            logger.debug("invoke() ---> userName: "+userName);
            
            request.addRequestParam_("isDefaultActionsEnabled", "true");
            
            String userCreateStatus = this.createAUserAndAssignTOGivenContract(contractPayload, coreCustomerIdorPartyId, 
            		userName, password, request, methodId, response, isRequestWithParty, userPartyId, companyId);
            logger.debug("invoke() ---> userCreateStatus: "+userCreateStatus);

            result.addParam(new Param("status", "success"));
            result.addParam(new Param("userCreated", userCreated.toString()));
            logger.debug("invoke() ---> END");
        }
        catch (Exception e) {
            //this.logger.error("Error occured", e);
        	logger.error("invoke() --->2. Exception: "+e.getMessage());
            ErrorCodeEnum.ERR_10390.setErrorCode(result);
        }
        return result;
	}
    
    private Map<String, Object> getHeadersMap(DataControllerRequest request) {
        Map headers = request.getHeaderMap();
        headers.put("companyId", this.getCompanyId());
        return headers;
    }
    
    private	Map<String, Set<ContractAccountsDTO>> getCustomerContractAccounts(DataControllerRequest request, String contractId, String customerId) {
    	logger.debug("getCustomerContractAccounts() ---> START ===> contractId: "+contractId+"; customerId: "+customerId);
    	Map<String, Set<ContractAccountsDTO>> customerAccountsMap = new HashMap<>();
    	
    	ContractAccountsBusinessDelegate contractAccountsBD =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractAccountsBusinessDelegate.class);
    	
    	try {
			List<ContractAccountsDTO> contractAccounts = contractAccountsBD.getContractCustomerAccounts(contractId, customerId, request.getHeaderMap());
			logger.debug("getCustomerContractAccounts() ---> contractAccounts: "+contractAccounts);
			
			Set<ContractAccountsDTO> accounts = new HashSet<>();
			
			int count = contractAccounts == null ? 0 : contractAccounts.size();
			logger.debug("getCustomerContractAccounts() ---> count: "+count);
			
			for(int i=0 ; i<count ; ++i) {
				accounts.add(contractAccounts.get(i));
			}
			
			customerAccountsMap.put(customerId, accounts);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			logger.error("getCustomerContractAccounts() ---> EXCEPTION: "+e.getMessage());
		}
    	logger.debug("getCustomerContractAccounts() ---> END ");
    	return customerAccountsMap;
    }

    private String createAUserAndAssignTOGivenContract(Map<String, String> contractPayload, String coreCustomerId, 
    		String userName, String password, DataControllerRequest request, String methodId, DataControllerResponse response, 
    		boolean isRequestedwithParty, String userPartyId, String companyIdPassed) throws ApplicationException 
    {
        
    	logger.debug("createAUserAndAssignTOGivenContract() ---> START ");
    	
    	Set<String> createdValidContractCoreCustomers = new HashSet<>();
    	createdValidContractCoreCustomers.add(coreCustomerId);
    	logger.debug("createAUserAndAssignTOGivenContract() ---> createdValidContractCoreCustomers: "+createdValidContractCoreCustomers);
    	
    	Map<String, Set<ContractAccountsDTO>> createdCoreCustomerAccounts = getCustomerContractAccounts(request, contractId4mInput, coreCustomerId);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createdCoreCustomerAccounts: "+createdCoreCustomerAccounts);
        
        String createdServiceType = "TYPE_ID_RETAIL";
        logger.debug("createAUserAndAssignTOGivenContract() ---> createdServiceType: "+createdServiceType);
        
        String contractId = contractId4mInput;
        logger.debug("createAUserAndAssignTOGivenContract() ---> contractId: "+contractId);
        
        String authorizedSignatory = contractPayload.get("authorizedSignatory");
        logger.debug("createAUserAndAssignTOGivenContract() ---> authorizedSignatory: "+authorizedSignatory);
        
        String authorizedSignatoryRoles = contractPayload.get("authorizedSignatoryRoles");
        logger.debug("createAUserAndAssignTOGivenContract() ---> authorizedSignatoryRoles: "+authorizedSignatoryRoles);
        
        List authorizedSignatoryList = DTOUtils.getDTOList((String)authorizedSignatory, CustomerDTO.class);
        logger.debug("createAUserAndAssignTOGivenContract() ---> authorizedSignatoryList: "+authorizedSignatoryList);
        
        if (authorizedSignatoryList == null || authorizedSignatoryList.isEmpty()) {
        	authorizedSignatoryList = new ArrayList<>();
        	CustomerDTO customerDTO = new CustomerDTO();
        	authorizedSignatoryList.add(customerDTO);
            //throw new ApplicationException(ErrorCodeEnum.ERR_10385);
        }
        
    	logger.debug("createAUserAndAssignTOGivenContract() ---> authorizedSignatoryList: "+((CustomerDTO)authorizedSignatoryList.get(0)).toString());

        //Map<String, String> userToCoreCustomerRoles1 = this.getUserToCoreCustomerRoles(authorizedSignatoryRoles);
        //logger.debug("createAUserAndAssignTOGivenContract() ---> userToCoreCustomerRoles1: "+userToCoreCustomerRoles1);
        
    	Map<String, String> userToCoreCustomerRoles = new HashMap<>();
    	userToCoreCustomerRoles.put(coreCustomerId, userDataPayload.get("roleId"));
        logger.debug("createAUserAndAssignTOGivenContract() ---> userToCoreCustomerRoles: "+userToCoreCustomerRoles);
        
        //String userId = this.createUser((CustomerDTO)authorizedSignatoryList.get(0), createdServiceType, userName, password, request);
        String userId = this.createInfinityUser((CustomerDTO)authorizedSignatoryList.get(0), createdServiceType, userName, password, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> userId: "+userId);
        
        this.createUserRoles(userId, contractId, userToCoreCustomerRoles, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserRoles completed ... ");
        
        this.assignUserToContractCustomers(userId, contractId, createdValidContractCoreCustomers, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> assignUserToContractCustomers completed ... ");
        
        this.createUserAccounts(userId, contractId, createdCoreCustomerAccounts, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserAccounts completed ... ");
        
        this.createUserActionLimits(userId, contractId, createdValidContractCoreCustomers, userToCoreCustomerRoles, request);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createUserActionLimits completed ... ");
        
        this.createBackendIdentifierEntry(coreCustomerId, userId, contractId, request, companyIdPassed, methodId, response, isRequestedwithParty, userPartyId);
        logger.debug("createAUserAndAssignTOGivenContract() ---> createBackendIdentifierEntry completed ... ");

    	logger.debug("createAUserAndAssignTOGivenContract() ---> END ");
    	return userId;
    }

    private Map<String, String> getUserToCoreCustomerRoles(String authorizedSignatoryRoles) throws ApplicationException {
    	logger.debug("getUserToCoreCustomerRoles() ---> START authorizedSignatoryRoles: "+authorizedSignatoryRoles);
    	
        if (StringUtils.isBlank((CharSequence)authorizedSignatoryRoles)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10389);
        }
        HashMap<String, String> coreCustomerRoles = new HashMap<String, String>();
        JsonParser parser = new JsonParser();
        JsonArray rolesArray = (JsonArray)parser.parse(authorizedSignatoryRoles);
        logger.debug("getUserToCoreCustomerRoles() ---> rolesArray: "+rolesArray);
        
        if (rolesArray.size() == 0) {
            //throw new ApplicationException(ErrorCodeEnum.ERR_10389);
        	logger.error("getUserToCoreCustomerRoles() ---> rolesArray is ZERO: ");
        }
        for (JsonElement element : rolesArray) {
            String roleId;
            JsonObject roleObject = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
            String coreCustomerId = roleObject.has("coreCustomerId") ? roleObject.get("coreCustomerId").getAsString() : "";
            String string = roleId = roleObject.has("authorizedSignatoryRoleId") ? roleObject.get("authorizedSignatoryRoleId").getAsString() : "";
            if (StringUtils.isBlank((CharSequence)coreCustomerId) || StringUtils.isBlank((CharSequence)roleId)) continue;
            coreCustomerRoles.put(coreCustomerId, roleId);
        }
        
        logger.debug("getUserToCoreCustomerRoles() ---> END coreCustomerRoles: "+coreCustomerRoles);
        return coreCustomerRoles;
    }
    
    private String createInfinityUser(CustomerDTO customerDTO, String createdServiceType, String userName, String password, DataControllerRequest request) throws ApplicationException {
    	String customerId = null;
    	logger.debug("createInfinityUser() ---> START");
    	
        customerDTO.setId(HelperMethods.getUniqueNumericString(8));
        customerDTO.setUserName(this.userName.toLowerCase());
        customerDTO.setFirstName(userDataPayload.get("firstName"));
        customerDTO.setMiddleName(userDataPayload.get("middleName"));
        customerDTO.setLastName(userDataPayload.get("lastName"));
        customerDTO.setDateOfBirth(userDataPayload.get("dob"));
        customerDTO.setSsn(userDataPayload.get("ssn"));
        customerDTO.setDrivingLicenseNumber(userDataPayload.get("dlno"));
        customerDTO.setStatus_id("SID_CUS_ACTIVE");
        customerDTO.setIsNew(true);
        customerDTO.setIsEnrolledFromSpotlight("1");
        
        customerDTO.setCustomerType_id(createdServiceType);
        customerDTO.setIsEnrolled(Boolean.valueOf(true));

        String salt = BCrypt.gensalt((int)11);
        String hashedPassword = BCrypt.hashpw((String)password, (String)salt);
        customerDTO.setPassword(hashedPassword);
    	
    	UserManagementBusinessDelegate customerBD = (UserManagementBusinessDelegate)((BusinessDelegateFactory)DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)).getBusinessDelegate(UserManagementBusinessDelegate.class);
        DBXResult customerResult = customerBD.update(customerDTO, this.getHeadersMap(request));
        logger.debug("createInfinityUser() ---> customer created with result: "+customerResult);
        
    	customerId = (String)customerResult.getResponse();
    	logger.debug("createInfinityUser() ---> customerId: "+customerId);
        if (StringUtils.isBlank((CharSequence)customerId)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10386);
        }
   	
        PasswordHistoryDTO passwordHistoryDTO = new PasswordHistoryDTO();
        passwordHistoryDTO.setId(this.idFormatter.format(new Date()));
        passwordHistoryDTO.setCustomer_id(customerId);
        passwordHistoryDTO.setPreviousPassword(hashedPassword);
        PasswordHistoryBusinessDelegate passwordHistoryBD = (PasswordHistoryBusinessDelegate)((BusinessDelegateFactory)DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)).getBusinessDelegate(PasswordHistoryBusinessDelegate.class);
        passwordHistoryBD.update(passwordHistoryDTO, this.getHeadersMap(request));
        logger.debug("createInfinityUser() ---> passwordHistoryDTO updated: "+passwordHistoryDTO);

        CustomerCommunicationDTO phoneCommunicationDTO = new CustomerCommunicationDTO();
        phoneCommunicationDTO.setId(HelperMethods.getNewId());
        
        String phoneCountryCode = userDataPayload.get("phoneCountryCode");
        if (StringUtils.isNotBlank(phoneCountryCode))
            phoneCountryCode = phoneCountryCode.trim();
        
        if (!phoneCountryCode.contains("+"))
            phoneCountryCode = "+" + phoneCountryCode;
        
        phoneCommunicationDTO.setPhoneCountryCode(phoneCountryCode);
        phoneCommunicationDTO.setValue(userDataPayload.get("phoneNumber"));
        phoneCommunicationDTO.setCustomer_id(customerId);
        phoneCommunicationDTO.setExtension(InfinityConstants.Mobile);
        phoneCommunicationDTO.setType_id(HelperMethods.getCommunicationTypes().get("Phone"));
        phoneCommunicationDTO.setIsPrimary(true);
        phoneCommunicationDTO.setIsNew(true);
        DTOUtils.persistObject(phoneCommunicationDTO, this.getHeadersMap(request));
        logger.debug("createInfinityUser() ---> phoneCommunicationDTO persisted: "+phoneCommunicationDTO);
        
        CustomerCommunicationDTO emailCommunicationDTO = new CustomerCommunicationDTO();
        emailCommunicationDTO.setId(HelperMethods.getNewId());
        emailCommunicationDTO.setValue(userDataPayload.get("email"));
        emailCommunicationDTO.setCustomer_id(customerId);
        emailCommunicationDTO.setExtension("Personal");
        emailCommunicationDTO.setType_id(HelperMethods.getCommunicationTypes().get("Email"));
        emailCommunicationDTO.setIsPrimary(true);
        emailCommunicationDTO.setIsNew(true);
        DTOUtils.persistObject(emailCommunicationDTO, this.getHeadersMap(request));
        logger.debug("createInfinityUser() ---> emailCommunicationDTO persisted: "+emailCommunicationDTO);

        String addressId = HelperMethods.getNewId();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressLine1(userDataPayload.get("addressline1"));
        addressDTO.setAddressLine2(userDataPayload.get("addressline2"));
        addressDTO.setAddressLine3(userDataPayload.get("addressline3"));
        addressDTO.setCityName(userDataPayload.get("city"));
        addressDTO.setState(userDataPayload.get("state"));
        //addressDTO.setRegion_id(userDataPayload.get("v"));
        addressDTO.setCountry(userDataPayload.get("country"));
        addressDTO.setZipCode(userDataPayload.get("zipcode"));
        addressDTO.setId(addressId);
        DTOUtils.persistObject(addressDTO, this.getHeadersMap(request)); 
        logger.debug("createInfinityUser() ---> addressDTO persisted: "+addressDTO);

        CustomerAddressDTO customerAddressDTO = new CustomerAddressDTO();
        customerAddressDTO.setIsNew(true);
        customerAddressDTO.setCustomer_id(customerId);
        customerAddressDTO.setAddress_id(addressId);
        customerAddressDTO.setType_id("ADR_TYPE_HOME");
        customerAddressDTO.setIsPrimary(true);
        customerAddressDTO.setAddressDTO(addressDTO);
        DTOUtils.persistObject(customerAddressDTO, this.getHeadersMap(request)); 
        logger.debug("createInfinityUser() ---> customerAddressDTO persisted: "+customerAddressDTO);
        
//        CustomerDTO coreCustomerDTO = new CustomerDTO();
//        coreCustomerDTO.setId(coreCustId4mInput);
//        coreCustomerDTO = (CustomerDTO) coreCustomerDTO.loadDTO();
//        coreCustomerDTO.setUserName(coreCustomerDTO.getUserName() + 1);
//        coreCustomerDTO.setLastName(coreCustomerDTO.getFirstName());
//        coreCustomerDTO.setSsn(coreCustomerDTO.getSsn() + 1);
//        coreCustomerDTO.setDateOfBirth(HelperMethods.getCurrentDate());
//        coreCustomerDTO.setIsChanged(true);

        return customerId;
    }

    private String createUser(CustomerDTO customerDTO, String createdServiceType, String userName, String password, DataControllerRequest request) throws ApplicationException {
        
    	logger.debug("createUser() ---> START customerDTO: "+customerDTO);
    	
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
        createdServiceType = "TYPE_ID_BUSINESS";
        customerDTO.setCustomerType_id(createdServiceType);
        customerDTO.setIsEnrolled(Boolean.valueOf(true));
        customerDTO.setPassword(hashedPassword);
        customerDTO.setDateOfBirth(userDataPayload.get("dob"));
        customerDTO.setSsn(userDataPayload.get("ssn"));
        customerDTO.setDrivingLicenseNumber(userDataPayload.get("dlno"));
        
        customerDTO.setFirstName(userDataPayload.get("firstName"));
        customerDTO.setMiddleName(userDataPayload.get("middleName"));
        customerDTO.setLastName(userDataPayload.get("lastName"));
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

        logger.debug("createUser() ---> END customerId: "+customerId);
        return customerId;
    }

    private void createUserRoles(String userId, String contractId, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
        logger.debug("createUserRoles() ---> START :: userToCoreCustomerRoles: "+userToCoreCustomerRoles);

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
        logger.debug("createUserRoles() ---> END ");
    }

    private void assignUserToContractCustomers(String userId, String contractId, Set<String> createdValidContractCoreCustomers, DataControllerRequest request) {
        logger.debug("assignUserToContractCustomers() ---> START createdValidContractCoreCustomers: "+createdValidContractCoreCustomers);

        for (String customerId : createdValidContractCoreCustomers) {
            ContractCustomersDTO contractCustomerDTO = new ContractCustomersDTO();
            contractCustomerDTO.setId(this.idFormatter.format(new Date()));
            contractCustomerDTO.setContractId(contractId);
            contractCustomerDTO.setCoreCustomerId(customerId);
            contractCustomerDTO.setCustomerId(userId);
            Map inputParams = DTOUtils.getParameterMap((Object)contractCustomerDTO, (boolean)false);
            contractCustomerDTO.persist(inputParams, this.getHeadersMap(request));
        }
        logger.debug("assignUserToContractCustomers() ---> END ");
    }

    private void createUserAccounts(String userId, String contractId, Map<String, Set<ContractAccountsDTO>> createdCoreCustomerAccounts, DataControllerRequest request) throws ApplicationException {
        logger.debug("createUserAccounts() ---> createdCoreCustomerAccounts: "+createdCoreCustomerAccounts);
        logger.debug("createUserAccounts() ---> allowedAccounts: "+userDataPayload.get("allowedAccounts"));
        CustomerAccountsBusinessDelegate customerAccountsBD = (CustomerAccountsBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerAccountsBusinessDelegate.class);
        for (Map.Entry<String, Set<ContractAccountsDTO>> entry : createdCoreCustomerAccounts.entrySet()) {
            String coreCustomerId = entry.getKey();
            HashSet<String> accounts = new HashSet<String>();
            Set<ContractAccountsDTO> coreCustomerAccounts = entry.getValue();
            for (ContractAccountsDTO dto : coreCustomerAccounts) {
            	logger.debug("createUserAccounts() ---> dto.accountid: "+dto.getAccountId());
            	if(allowAccountAccess(dto.getAccountId())) {
                    accounts.add(dto.getAccountId());
            	}
                //accounts.add(dto.getAccountId());
            }
            customerAccountsBD.createCustomerAccounts(userId, contractId, coreCustomerId, accounts, this.getHeadersMap(request));
        }
        logger.debug("createUserAccounts() ---> END ");
    }
    
    private	boolean allowAccountAccess(String accId) {
    	String allowedAccounts = userDataPayload.get("allowedAccounts");
    	logger.debug("allowAccountAccess() ---> START :: allowedAccounts: "+allowedAccounts);
    	if(SBGCommonUtils.isStringEmpty(allowedAccounts)) {
    		return false;
    	}
    	
    	String accounts[] = allowedAccounts.split("\\|");
    	int count = accounts == null ? 0 : accounts.length;
    	logger.debug("allowAccountAccess() ---> count: "+count);
    	for(int i=0 ;i<count ; ++i) {
    		if(accId != null && accId.equals(accounts[i]))	{
    			return true;
    		}
    	}
    	return false;
    }

    private void createUserActionLimits(String userId, String contractId, Set<String> createdValidContractCoreCustomers, Map<String, String> userToCoreCustomerRoles, DataControllerRequest request) throws ApplicationException {
        logger.debug("createUserActionLimits() ---> START :: createdValidContractCoreCustomers: "+createdValidContractCoreCustomers);
        logger.debug("createUserActionLimits() ---> userToCoreCustomerRoles: "+userToCoreCustomerRoles);
        CustomerActionsBusinessDelegate customerActionsBD = (CustomerActionsBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerActionsBusinessDelegate.class);
        for (String coreCustomerId : createdValidContractCoreCustomers) {
            customerActionsBD.createCustomerActions(userId, contractId, coreCustomerId, userToCoreCustomerRoles.get(coreCustomerId), new HashSet(), this.getHeadersMap(request));
            customerActionsBD.createCustomerLimitGroupLimits(userId, contractId, coreCustomerId, this.getHeadersMap(request));
        }
        logger.debug("createUserActionLimits() ---> END ");
    }

    private void createBackendIdentifierEntry(String backendId, String userId, String contractId, DataControllerRequest request, 
    		String companyId, String methodId, DataControllerResponse response, boolean isRequestedwithParty, String userPartyId) throws ApplicationException 
    {
        logger.debug("createBackendIdentifierEntry() ---> START :: backendId: "+backendId);
        
        if (StringUtils.isBlank((CharSequence)backendId)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
        
		BackendIdentifierDTO custBackendIdentifierDTO = new BackendIdentifierDTO();
		custBackendIdentifierDTO.setId(UUID.randomUUID().toString());
		custBackendIdentifierDTO.setIsNew(true);
		custBackendIdentifierDTO.setCustomer_id(userId);
		custBackendIdentifierDTO.setBackendId(partyId4mInput);
		custBackendIdentifierDTO.setBackendType(SBGConstants.COMPANYPARTYID);
		custBackendIdentifierDTO.setSequenceNumber("1");
		custBackendIdentifierDTO.setContractId(contractId);
		custBackendIdentifierDTO.setIdentifier_name("customer_id");
		custBackendIdentifierDTO.setCompanyId(companyId);
		if (StringUtils.isBlank(companyId)) {
			custBackendIdentifierDTO
					.setCompanyId(EnvironmentConfigurationsHandler.getValue(DBPUtilitiesConstants.BRANCH_ID_REFERENCE));
		}
		logger.debug("createBackendIdentifierEntry() ---> 1. custBackendIdentifierDTO: "+custBackendIdentifierDTO);
		Map<String, Object> input1 = DTOUtils.getParameterMap(custBackendIdentifierDTO, true);
		custBackendIdentifierDTO.persist(input1, request.getHeaderMap());
		logger.debug("createBackendIdentifierEntry() ---> 2. custBackendIdentifierDTO: "+custBackendIdentifierDTO);

        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        String partyId = userPartyId;
        logger.debug("createBackendIdentifierEntry() ---> partyId: "+partyId);
        
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
            logger.debug("createBackendIdentifierEntry() ---> 1. backendIdentifierDTO: "+backendIdentifierDTO);

            if (StringUtils.isBlank((CharSequence)companyId)) {
                backendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler.getValue((String)"BRANCH_ID_REFERENCE"));
            }
            Map<String, Object> input = DTOUtils.getParameterMap((Object)backendIdentifierDTO, (boolean)true);
            backendIdentifierDTO.persist(input, new HashMap());
            logger.debug("createBackendIdentifierEntry() ---> 2. backendIdentifierDTO: "+backendIdentifierDTO);
        }
        if (null == backendIdentifierDTO || StringUtils.isBlank((CharSequence)backendIdentifierDTO.getId())) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10397);
        }
        logger.debug("createBackendIdentifierEntry() ---> END ");
    }

    private String getPartyId(String backendId, DataControllerRequest request) {
        logger.debug("getPartyId() ---> START :: backendId: "+backendId);

        String companyId = EnvironmentConfigurationsHandler.getServerProperty((String)"BRANCH_ID_REFERENCE");
        String id = companyId + "-" + backendId;
        Map<String, Object> headers = CreateSbgDemoUsers.addJWTAuthHeader(request, request.getHeaderMap(), "PreLogin");
        String queryParams = "";
        queryParams = queryParams + "alternateIdentifierNumber=" + id;
        queryParams = queryParams + "&";
        queryParams = queryParams + "alternateIdentifierType=BackOfficeIdentifier";
        logger.debug("getPartyId() ---> queryParams: "+queryParams);

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
        logger.debug("getPartyId() ---> END :: partyJson:"+partyJson);
        return JSONUtil.getString((JsonObject)partyJson, (String)"partyId");
    }
    
//    private String createUsersPartyRecord_nr(String methodId, DataControllerRequest request, DataControllerResponse response) {
//    	logger.debug("createUsersPartyRecord() ---> START");
//    	
//		if(RETRY_COUNTER == RETRY_ATTEMPTS) {
//			RETRY_COUNTER = 0;
//			logger.debug("createUsersPartyRecord() ---> RETRY.FAILURE .. REACHED MAXIMUM LIMIT OF "+RETRY_ATTEMPTS);
//			return null;
//		}
//
//		try {
//        	Map<String, Object> requestParams = new HashMap<>();
//        	
//        	String fullName = userDataPayload.get("firstname")+" "+userDataPayload.get("lastname");
//        	requestParams.put("firstName", userDataPayload.get("firstname"));
//        	requestParams.put("lastName", userDataPayload.get("lastname"));
//        	requestParams.put("nickName", fullName);
//        	requestParams.put("entityName", fullName);
//        	requestParams.put("othername", fullName);
//    		requestParams.put("holderName", fullName);
//    		requestParams.put("documentTagId", coreCustId4mInput);
//    		requestParams.put("nameStartDate", (new SimpleDateFormat("yyyy-MM-dd").format(new Date()))); 
//    		requestParams.put("identityNumber", companyId+"-"+coreCustId4mInput);
//    		requestParams.put("partyType", "Individual");
//    		logger.debug("createUsersPartyRecord() ---> requestParams: "+requestParams);
//    		
//    		String serviceName 		= "PartyMS";
//    		String operationName	= "CreateParty";
//    		
//    		String url = "/services/"+serviceName+"/"+operationName;
//    		Result result = ServiceCallHelper.invokeServiceAndGetResult(request, requestParams, HelperMethods.getHeaders(request), url);
//    		logger.debug("createUsersPartyRecord() ---> result: "+result);
//    		
//    		String partyId = null;
//    		if(result != null) {
//    			List<Param> params = result.getAllParams();
//    			int count = params == null ? 0 : params.size();
//    			for(int i=0 ; i<count ; ++i) {
//    				Param p = params.get(i);
//    				logger.debug("createUsersPartyRecord() ---> name: "+p.getName()+"; value: "+p.getValue());
//    				if(p != null && "id".equals(p.getName())) {
//    					partyId = p.getValue();
//    				}
//    			}
//    		}
//    		
//    		if(!StringUtils.isEmpty(partyId)) {
//    			RETRY_COUNTER = 0;
//    			return partyId;
//    		} else {
//	        	++RETRY_COUNTER;
//	        	logger.debug("createUsersPartyRecord() ---> RETRY_COUNTER: "+RETRY_COUNTER+"; result: "+result);
//				return createUsersPartyRecord_nr(methodId, request, response);
//    		}
//    	}catch(Exception e) {
//    		logger.debug("createUsersPartyRecord() ---> EXCEPTION: "+e.getMessage());
//    	}
//    	
//		RETRY_COUNTER = 0;
//    	logger.debug("createUsersPartyRecord() ---> END");
//    	return null;
//    }

//    private String createParty_nr(String methodId, DataControllerRequest request, DataControllerResponse response) {
//        logger.debug("createParty() ---> START");
//
//        HashMap<String, String> inputParams = new HashMap<String, String>();
//        
//        inputParams.put("partyType", "Individual");
//        inputParams.put("firstName", userDataPayload.get("firstname"));
//        inputParams.put("lastName", userDataPayload.get("lastname"));
//        
//        JsonArray jsonarray = new JsonArray();
//        JsonObject jsonobject = new JsonObject();
//        jsonobject.addProperty("phoneAddressType", "Home Number");
//        
//        jsonobject.addProperty("internationalPhoneNo", userDataPayload.get("phoneNumber"));
//        jsonobject.addProperty("iddPrefixPhone", userDataPayload.get("phoneCountryCode"));
//        jsonobject.addProperty("nationalPhoneNo", userDataPayload.get("phoneNumber"));
//        jsonarray.add((JsonElement)jsonobject);
//        inputParams.put("phoneAddress", jsonarray.toString());
//        inputParams.put("partyStatus", "Active");
//        
//        jsonarray = new JsonArray();
//        jsonobject = new JsonObject();
//        jsonobject.addProperty("startDate", HelperMethods.getCurrentDate());
//        jsonobject.addProperty("firstName", userDataPayload.get("firstname"));
//        jsonobject.addProperty("lastName", userDataPayload.get("lastname"));
//        jsonobject.addProperty("middleName", "");
//        jsonobject.addProperty("suffix", "");
//        jsonarray.add((JsonElement)jsonobject);
//        inputParams.put("partyNames", jsonarray.toString());
//        
//        jsonarray = new JsonArray();
//        jsonobject = new JsonObject();
//        jsonobject.addProperty("physicalAddressType", "Office");
//        jsonobject.addProperty("startDate", HelperMethods.getCurrentDate());
//        jsonobject.addProperty("reliabilityType", "Confirmed");
//        jsonobject.addProperty("contactName", userDataPayload.get("firstname") + userDataPayload.get("lastname"));
//        jsonobject.addProperty("endDate", HelperMethods.getCurrentDate());
//        jsonobject.addProperty("countryCode", userDataPayload.get("CountryCode"));
//        jsonobject.addProperty("town", userDataPayload.get("postalCityName"));
//        jsonobject.addProperty("postalOrZipCode", userDataPayload.get("postalZipCode"));
//        jsonobject.addProperty("addressCountry", userDataPayload.get("postalCountry"));
//        jsonarray.add((JsonElement)jsonobject);
//        inputParams.put("contactAddress", jsonarray.toString());
//
//        logger.debug("createParty() ---> jsonobject: "+jsonobject);
//        
//        Object[] inputArray1 = new Object[3];
//        inputArray1[1] = inputParams;
//        PartyUserManagementResource resource = (PartyUserManagementResource)DBPAPIAbstractFactoryImpl.getResource(PartyUserManagementResource.class);
//        Result partyCreateResponse = resource.partyCreate(methodId, inputArray1, request, response);
//        logger.debug("createParty() ---> END ===> partyCreateResponse: "+partyCreateResponse);
//        
//        return partyCreateResponse.getParamValueByName("partyId");
//    }

}
