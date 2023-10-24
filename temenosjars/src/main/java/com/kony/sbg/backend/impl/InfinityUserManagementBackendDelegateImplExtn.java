package com.kony.sbg.backend.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractBackendDelegate;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractCoreCustomerBackendDelegate;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.CoreCustomerBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.InfinityUserManagementBackendDelegateImpl;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.ContractCoreCustomersDTO;
import com.temenos.dbx.product.dto.CustomerAddressDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerPreferenceDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.FeatureActionLimitsDTO;
import com.temenos.dbx.product.dto.MembershipDTO;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerPreferenceBusinessDelegate;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.InfinityConstants;

public class InfinityUserManagementBackendDelegateImplExtn extends InfinityUserManagementBackendDelegateImpl{
	
	LoggerUtil logger = new LoggerUtil(InfinityUserManagementBackendDelegateImplExtn.class);
	@Override
    public DBXResult createInfinityUser(JsonObject jsonObject, Map<String, Object> headerMap) {

        DBXResult dbxResult = new DBXResult();
        JsonObject result = new JsonObject();
        dbxResult.setResponse(result);
        JsonObject userDetails = jsonObject.get(InfinityConstants.userDetails).getAsJsonObject();

        String coreCustomerId = userDetails.has(InfinityConstants.coreCustomerId)
                && !userDetails.get(InfinityConstants.coreCustomerId).isJsonNull()
                        ? userDetails.get(InfinityConstants.coreCustomerId).getAsString()
                        : null;
        if (!createCustomer(userDetails, headerMap, result, coreCustomerId)) {
            return dbxResult;
        }

        String customerId = result.get(InfinityConstants.id).getAsString();

        String companyId = userDetails.has(InfinityConstants.companyId)
                && !userDetails.get(InfinityConstants.companyId).isJsonNull()
                        ? userDetails.get(InfinityConstants.companyId).getAsString()
                        : "";
        createCustomerPreference(customerId, headerMap);
        createCustomerAction(customerId, jsonObject, headerMap, coreCustomerId, companyId, true);

        return dbxResult;
    }

	 private Boolean createCustomer(JsonObject userDetails, Map<String, Object> headerMap, JsonObject result,
	            String coreCustomerId) {

	        CustomerDTO coreCustomerDTO = null;
	        if (!IntegrationTemplateURLFinder.isIntegrated && StringUtils.isNotBlank(coreCustomerId)) {
	            coreCustomerDTO = new CustomerDTO();
	            coreCustomerDTO.setId(coreCustomerId);
	            coreCustomerDTO = (CustomerDTO) coreCustomerDTO.loadDTO();
	        }

	        String id = userDetails.has(InfinityConstants.id) && !userDetails.get(InfinityConstants.id).isJsonNull()
	                ? userDetails.get(InfinityConstants.id).getAsString()
	                : null;

	        if (StringUtils.isNotBlank(id)) {
	            result.addProperty(InfinityConstants.id, id);
	            return true;
	        }

	        CustomerDTO customerDTO = new CustomerDTO();

	        CustomerCommunicationDTO phoneCommunicationDTO = new CustomerCommunicationDTO();
	        CustomerCommunicationDTO emailCommunicationDTO = new CustomerCommunicationDTO();
	        customerDTO.setId(HelperMethods.getUniqueNumericString(8));
	        customerDTO.setUserName(customerDTO.getId());
	        String firstName = JSONUtil.getString(userDetails, InfinityConstants.firstName);
	        String lastName = JSONUtil.getString(userDetails, InfinityConstants.lastName);
	        String middleName = JSONUtil.getString(userDetails, InfinityConstants.middleName);
	        String ssn = JSONUtil.getString(userDetails, InfinityConstants.ssn);
	        String email = JSONUtil.getString(userDetails, InfinityConstants.email);
	        String dob = JSONUtil.getString(userDetails, InfinityConstants.dob);
	        String drivingLicense = JSONUtil.getString(userDetails, InfinityConstants.drivingLicenseNumber);
	        String phoneCountryCode = JSONUtil.getString(userDetails, "phoneCountryCode");
	        String phoneNumber = JSONUtil.getString(userDetails, "phoneNumber");
	        String taxid = JSONUtil.getString(userDetails, "taxid");
	        // if (firstName != null && firstName.length() > 0) {
	        // if (!validateRegex("[A-Za-z0-9]{1,51}$", firstName))
	        // return false;
	        // }
	        // if (lastName != null && lastName.length() > 0) {
	        // if (!validateRegex("[A-Za-z0-9]{1,51}$", lastName))
	        // return false;
	        // }
	        // if (middleName != null && middleName.length() > 0) {
	        // if (!validateRegex("[A-Za-z0-9]{1,51}$", middleName))
	        // return false;
	        // }
	        // if (ssn != null && ssn.length() > 0) {
	        // if (!validateRegex("^[^-_][a-zA-Z0-9\\s-]*[a-zA-Z0-9\\s]{1,51}$", ssn))
	        // return false;
	        // }
	        // if (email != null && email.length() > 0) {
	        // if (!validateRegex("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]{1,51}+$", email))
	        // return false;
	        // }
	        // if (phoneCountryCode != null && phoneCountryCode.length() > 0) {
	        // phoneCountryCode = phoneCountryCode.trim();
	        // if (!validateRegex("^\\+{1}[0-9]+$", phoneCountryCode))
	        // return false;
	        // }
	        // if (phoneNumber != null && phoneNumber.length() > 0) {
	        // phoneNumber = phoneNumber.trim();
	        // if (!validateRegex("^([0-9]){7,15}$", phoneNumber))
	        // return false;
	        // }
	        customerDTO.setFirstName(StringUtils.isNotBlank(firstName) ? firstName : null);
	        customerDTO.setLastName(StringUtils.isNotBlank(lastName) ? lastName : null);
	        customerDTO.setMiddleName(StringUtils.isNotBlank(middleName) ? middleName : null);
	        customerDTO.setDateOfBirth(StringUtils.isNotBlank(dob) ? dob : null);
	        customerDTO.setTaxID(StringUtils.isNotBlank(taxid) ? taxid : null);
	        phoneCommunicationDTO.setId(HelperMethods.getNewId());

	        if (StringUtils.isNotBlank(phoneCountryCode))
	            phoneCountryCode = phoneCountryCode.trim();
	        if (!phoneCountryCode.contains("+"))
	            phoneCountryCode = "+" + phoneCountryCode;
	        phoneCommunicationDTO.setPhoneCountryCode(phoneCountryCode);
	        phoneCommunicationDTO.setValue(phoneCountryCode + "-" + phoneNumber);
	        emailCommunicationDTO.setId(HelperMethods.getNewId());
	        emailCommunicationDTO.setValue(email);
	        if (coreCustomerDTO == null) {
	            customerDTO.setSsn(StringUtils.isNotBlank(ssn) ? ssn : null);
	        } else {
	            customerDTO.setSsn(coreCustomerDTO.getSsn());
	        }
	        customerDTO.setDrivingLicenseNumber(StringUtils.isNotBlank(drivingLicense) ? drivingLicense : null);
	        customerDTO.setStatus_id(HelperMethods.getCustomerStatus().get("NEW"));
	        customerDTO.setIsNew(true);
	        customerDTO.setIsEnrolledFromSpotlight("1");
	        if (DTOUtils.persistObject(customerDTO, headerMap)) {
	            if (!IntegrationTemplateURLFinder.isIntegrated || StringUtils.isBlank(coreCustomerId)) {
	                phoneCommunicationDTO.setCustomer_id(customerDTO.getId());
	                emailCommunicationDTO.setCustomer_id(customerDTO.getId());
	                phoneCommunicationDTO.setExtension(InfinityConstants.Mobile);
	                emailCommunicationDTO.setExtension(InfinityConstants.Personal);
	                phoneCommunicationDTO.setType_id(HelperMethods.getCommunicationTypes().get("Phone"));
	                emailCommunicationDTO.setType_id(HelperMethods.getCommunicationTypes().get("Email"));
	                phoneCommunicationDTO.setIsPrimary(true);
	                emailCommunicationDTO.setIsPrimary(true);
	                phoneCommunicationDTO.setIsNew(true);
	                emailCommunicationDTO.setIsNew(true);
	                DTOUtils.persistObject(phoneCommunicationDTO, headerMap);
	                DTOUtils.persistObject(emailCommunicationDTO, headerMap);
	            }

	            if (!IntegrationTemplateURLFinder.isIntegrated && StringUtils.isNotBlank(coreCustomerId)) {
	                String customerId = customerDTO.getId();

	                CustomerAddressDTO customerAddressDTO = new CustomerAddressDTO();
	                customerAddressDTO.setCustomer_id(coreCustomerId);
	                List<CustomerAddressDTO> corecustomerAddresses =
	                        (List<CustomerAddressDTO>) customerAddressDTO.loadDTO();
	                for (CustomerAddressDTO corecustomerAddressDTO : corecustomerAddresses) {
	                    corecustomerAddressDTO.setIsdeleted(true);
	                    corecustomerAddressDTO.setIsChanged(false);
	                    corecustomerAddressDTO.setIsNew(false);
	                    DTOUtils.persistObject(corecustomerAddressDTO, headerMap);
	                    customerAddressDTO = new CustomerAddressDTO();
	                    customerAddressDTO.setIsNew(true);
	                    customerAddressDTO.setCustomer_id(customerId);
	                    customerAddressDTO.setAddress_id(corecustomerAddressDTO.getAddress_id());
	                    customerAddressDTO.setType_id(corecustomerAddressDTO.getType_id());
	                    customerAddressDTO.setIsPrimary(corecustomerAddressDTO.getIsPrimary());
	                    DTOUtils.persistObject(customerAddressDTO, headerMap);
	                }

	                CustomerCommunicationDTO customerCommunicationDTO = new CustomerCommunicationDTO();
	                customerCommunicationDTO.setCustomer_id(coreCustomerId);
	                List<CustomerCommunicationDTO> customerCommunicationDTOs =
	                        (List<CustomerCommunicationDTO>) customerCommunicationDTO.loadDTO();
	                for (CustomerCommunicationDTO customerCommunication : customerCommunicationDTOs) {
	                    customerCommunication.setIsdeleted(true);
	                    customerCommunication.setIsChanged(false);
	                    customerCommunication.setIsNew(false);
	                    DTOUtils.persistObject(customerCommunication, headerMap);
	                }
	                if (coreCustomerDTO != null) {
	                    coreCustomerDTO.setUserName(coreCustomerDTO.getUserName() + 1);
	                    coreCustomerDTO.setLastName(coreCustomerDTO.getFirstName());
	                    coreCustomerDTO.setSsn(coreCustomerDTO.getSsn() + 1);
	                    coreCustomerDTO.setDateOfBirth(HelperMethods.getCurrentDate());
	                    coreCustomerDTO.setIsChanged(true);
	                    coreCustomerDTO.persist(DTOUtils.getParameterMap(coreCustomerDTO, true), headerMap);
	                }
	            }

	            result.addProperty(InfinityConstants.id, customerDTO.getId());
	        } else {
	            ErrorCodeEnum.ERR_10050.setErrorCode(result);
	            return false;
	        }

	        return true;
	    }
	 private void createCustomerPreference(String customerId, Map<String, Object> headersMap) {
	        CustomerPreferenceDTO customerPreferenceDTO = new CustomerPreferenceDTO();
	        customerPreferenceDTO.setId(HelperMethods.getNewId());
	        customerPreferenceDTO.setCustomer_id(customerId);
	        customerPreferenceDTO.setIsNew(true);

	        CustomerPreferenceBusinessDelegate customerPreferenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
	                .getFactoryInstance(BusinessDelegateFactory.class)
	                .getBusinessDelegate(CustomerPreferenceBusinessDelegate.class);

	        customerPreferenceBusinessDelegate.update(customerPreferenceDTO, headersMap);

	    }
	 
	 private DBXResult getCustomRole(String customRoleName, String id, Map<String, Object> headerMap) {
	        DBXResult customRoleDTO = new DBXResult();
	        customRoleDTO.setResponse(null);
	        Map<String, Object> inputParams = new HashMap<>();
	        StringBuilder sb = new StringBuilder();
	        if (StringUtils.isNoneBlank(customRoleName)) {
	            sb.append(InfinityConstants.name).append(DBPUtilitiesConstants.EQUAL).append("\'").append(customRoleName)
	                    .append("\'");
	        }

	        if (StringUtils.isNoneBlank(id)) {
	            sb.append(InfinityConstants.id).append(DBPUtilitiesConstants.EQUAL).append("\'").append(id).append("\'");
	        }

	        if (StringUtils.isNoneBlank(sb.toString())) {
	            inputParams.put(DBPUtilitiesConstants.FILTER, sb.toString());
	        }
	        JsonObject response = new JsonObject();
	        try {
	            response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headerMap, URLConstants.CUSTOM_ROLE_GET);

	            if (JSONUtil.isJsonNotNull(response) && JSONUtil.hasKey(response, DBPDatasetConstants.DATASET_CUSTOMROLE)
	                    && response.get(DBPDatasetConstants.DATASET_CUSTOMROLE).isJsonArray()) {
	                JsonArray responseArray = response.get(DBPDatasetConstants.DATASET_CUSTOMROLE).getAsJsonArray();
	                if (responseArray.size() > 0) {
	                    JsonObject jsonObject = responseArray.get(0).getAsJsonObject();
	                    jsonObject.add(InfinityConstants.customRoleName, jsonObject.get(InfinityConstants.name));
	                    customRoleDTO.setResponse(jsonObject);
	                }
	            }

	        } catch (Exception e) {
	            logger.error("Exception occured while fetching data from custom roles", e);
	        }

	        return customRoleDTO;
	    }
	 
		@Override
		public DBXResult getCustomRole(JsonObject jsonObject, Map<String, Object> headerMap) {
			DBXResult dbxResult = new DBXResult();
			dbxResult.setResponse(jsonObject);

			String id = jsonObject.get(InfinityConstants.id).getAsString();

			String customerId = jsonObject.get(InfinityConstants.customerId).getAsString();

			DBXResult customerResponseObject = getCustomRole(null, id, headerMap);

			if (customerResponseObject.getResponse() == null) {
				return dbxResult;
			}

			JsonArray jsonArray = new JsonArray();
			jsonObject.add(InfinityConstants.customRoleDetails, (JsonObject) customerResponseObject.getResponse());

			Map<String, Map<String, Map<String, Map<String, String>>>> map = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();

			Map<String, FeatureActionLimitsDTO> featureActionDTOs = new HashMap<String, FeatureActionLimitsDTO>();

			Map<String, String> companyMap = new HashMap<String, String>();

			Map<String, Map<String, Map<String, String>>> accountMap = new HashMap<String, Map<String, Map<String, String>>>();

			Map<String, JsonObject> contracts = new HashMap<String, JsonObject>();
			Map<String, JsonObject> contractCoreCustomers = new HashMap<String, JsonObject>();
			Map<String, JsonObject> coreCustomerGroupInfo = new HashMap<String, JsonObject>();

			JsonArray actions = new JsonArray();

			Map<String, Object> input = new HashMap<String, Object>();
			String filter = InfinityConstants.customRole_id + DBPUtilitiesConstants.EQUAL + id;
			input.put(DBPUtilitiesConstants.FILTER, filter);
			JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
					URLConstants.CUSTOMROLEACTIONLIMITS_GET);

			if (response.has(DBPDatasetConstants.DATASET_CUSTOMROLEACTIONLIMITS)) {
				JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CUSTOMROLEACTIONLIMITS);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					actions = jsonElement.getAsJsonArray();
				}
			}

			JsonArray accounts = new JsonArray();
			input = new HashMap<String, Object>();
			filter = InfinityConstants.customRoleId + DBPUtilitiesConstants.EQUAL + id;
			input.put(DBPUtilitiesConstants.FILTER, filter);
			response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
					URLConstants.CUSTOM_ROLE_ACCOUNTS_GET);
			if (response.has(DBPDatasetConstants.DATASET_CUSTOMROLEACCOUNTS)) {
				JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CUSTOMROLEACCOUNTS);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					accounts = jsonElement.getAsJsonArray();
				}
			}

			jsonArray = getCompanyList(coreCustomerGroupInfo, contracts, contractCoreCustomers, id, headerMap, map,
					companyMap, accountMap, featureActionDTOs, customerId, accounts, false, true);

			jsonObject.add(InfinityConstants.companyList, jsonArray);

			addPermissions(coreCustomerGroupInfo, contracts, contractCoreCustomers, id, headerMap, map, jsonObject,
					accountMap, companyMap, featureActionDTOs, actions);

			dbxResult.setResponse(jsonObject);
			return dbxResult;
		}
		
		private void getAccountsForCustomer(String customerId, Map<String, Set<String>> customerAccounts,
	            Map<String, Object> headerMap) {

	        Map<String, Object> map = new HashMap<String, Object>();
	        if (StringUtils.isNotBlank(customerId)) {
	            String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerId;
	            map.put(DBPUtilitiesConstants.FILTER, filter);

	            JsonObject jsonObject =
	                    ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, URLConstants.CUSTOMERACCOUNTS_GET);

	            if (jsonObject.has(DBPDatasetConstants.CUSTOMER_ACCOUNTS_DATASET)) {
	                JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.CUSTOMER_ACCOUNTS_DATASET);
	                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
	                    JsonArray jsonArray = jsonElement.getAsJsonArray();
	                    for (int i = 0; i < jsonArray.size(); i++) {
	                        JsonObject account = jsonArray.get(i).getAsJsonObject();
	                        String coreCustomerId = account.has(InfinityConstants.coreCustomerId)
	                                && !account.get(InfinityConstants.coreCustomerId).isJsonNull()
	                                        ? account.get(InfinityConstants.coreCustomerId).getAsString()
	                                        : null;

	                        if (!customerAccounts.containsKey(coreCustomerId)) {
	                            customerAccounts.put(coreCustomerId, new HashSet<String>());
	                        }
	                        customerAccounts.get(coreCustomerId)
	                                .add(account.get("Account_id").getAsString());
	                    }
	                }
	            }
	        }

	    }
		
		private void getContractCIFs(Map<String, Set<String>> contractCIFs,
	            Map<String, Object> headerMap) {
	        Map<String, Object> map = new HashMap<String, Object>();
	        JsonObject jsonObject =
	                ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, URLConstants.CONTRACTCORECUSTOMER_GET);
	        if (jsonObject.has(DBPDatasetConstants.CONTRACT_CORE_CUSTOMERS)) {
	            JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.CONTRACT_CORE_CUSTOMERS);
	            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
	                JsonArray jsonArray = jsonElement.getAsJsonArray();
	                for (int i = 0; i < jsonArray.size(); i++) {
	                    JsonObject contractCoreCustomer = jsonArray.get(i).getAsJsonObject();
	                    String contractId = contractCoreCustomer.has(InfinityConstants.contractId)
	                            && !contractCoreCustomer.get(InfinityConstants.contractId).isJsonNull()
	                                    ? contractCoreCustomer.get(InfinityConstants.contractId).getAsString()
	                                    : null;
	                    String coreCustomerId = contractCoreCustomer.has(InfinityConstants.coreCustomerId)
	                            && !contractCoreCustomer.get(InfinityConstants.coreCustomerId).isJsonNull()
	                                    ? contractCoreCustomer.get(InfinityConstants.coreCustomerId).getAsString()
	                                    : null;
	                    if (!contractCIFs.containsKey(contractId)) {
	                        contractCIFs.put(contractId, new HashSet<String>());
	                    }
	                    contractCIFs.get(contractId).add(coreCustomerId);
	                }
	            }
	        }
	    }
		
		 private void getAccountsForContract(Map<String, String> accountOwnership,
		            Map<String, Map<String, Map<String, JsonObject>>> contractAccounts,
		            Map<String, Object> headerMap) {

		        Map<String, Object> map = new HashMap<String, Object>();

		        JsonObject jsonObject =
		                ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, URLConstants.CONTRACTACCOUNT_GET);

		        if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACTACCOUNT)) {
		            JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACTACCOUNT);
		            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
		                JsonArray jsonArray = jsonElement.getAsJsonArray();
		                for (int i = 0; i < jsonArray.size(); i++) {
		                    JsonObject account = jsonArray.get(i).getAsJsonObject();
		                    accountOwnership.put(JSONUtil.getString(account, "accountId"),
		                            JSONUtil.getString(account, "ownerType"));
		                    String contractId = account.has(InfinityConstants.contractId)
		                            && !account.get(InfinityConstants.contractId).isJsonNull()
		                                    ? account.get(InfinityConstants.contractId).getAsString()
		                                    : null;

		                    String coreCustomerId = account.has(InfinityConstants.coreCustomerId)
		                            && !account.get(InfinityConstants.coreCustomerId).isJsonNull()
		                                    ? account.get(InfinityConstants.coreCustomerId).getAsString()
		                                    : null;

		                    if (!contractAccounts.containsKey(contractId)) {
		                        contractAccounts.put(contractId, new HashMap<String, Map<String, JsonObject>>());
		                    }

		                    if (!contractAccounts.get(contractId).containsKey(coreCustomerId)) {
		                        contractAccounts.get(contractId).put(coreCustomerId, new HashMap<String, JsonObject>());
		                    }
		                    contractAccounts.get(contractId).get(coreCustomerId)
		                            .put(account.get(InfinityConstants.accountId).getAsString(), account);
		                }
		            }
		        }
		    }
		 
		 private boolean isViewPermissionEnbled(JsonObject companyJsonObject, String cifEntry, String loggedInUserId,
		            boolean isSuperAdmin, Map<String, Object> headerMap) {

		        boolean isCreatePermissionEnabled = false;
		        boolean isEditPermissionEnabled = false;
		        boolean isViewPermissionEnabled = false;

		        if (isSuperAdmin) {
		            companyJsonObject.addProperty("isCreatePermissionEnabled", isCreatePermissionEnabled);
		            companyJsonObject.addProperty("isEditPermissionEnabled", isEditPermissionEnabled);
		            companyJsonObject.addProperty("isViewPermissionEnabled", isViewPermissionEnabled);
		            return true;
		        }

		        String filter =
		                InfinityConstants.Customer_id + DBPUtilitiesConstants.EQUAL + loggedInUserId + DBPUtilitiesConstants.AND
		                        + InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cifEntry
		                        + DBPUtilitiesConstants.AND +
		                        InfinityConstants.featureId + DBPUtilitiesConstants.EQUAL + "USER_MANAGEMENT";

		        Map<String, Object> input = new HashMap<String, Object>();

		        input.put(DBPUtilitiesConstants.FILTER, filter);
		        JsonObject response =
		                ServiceCallHelper.invokeServiceAndGetJson(input, headerMap, URLConstants.CUSTOMER_ACTION_LIMITS_GET);
		        JsonArray actions = new JsonArray();
		        if (response.has(DBPDatasetConstants.DATASET_CUSTOMERACTION)) {
		            JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CUSTOMERACTION);
		            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
		                actions = jsonElement.getAsJsonArray();
		                for (int i = 0; i < actions.size(); i++) {
		                    JsonObject jsonObject2 = actions.get(i).getAsJsonObject();
		                    String coreCustomerId = jsonObject2.has(InfinityConstants.coreCustomerId)
		                            && !jsonObject2.get(InfinityConstants.coreCustomerId).isJsonNull()
		                                    ? jsonObject2.get(InfinityConstants.coreCustomerId).getAsString()
		                                    : null;
		                    String featureId = jsonObject2.has(InfinityConstants.featureId)
		                            && !jsonObject2.get(InfinityConstants.featureId).isJsonNull()
		                                    ? jsonObject2.get(InfinityConstants.featureId).getAsString().trim()
		                                    : null;
		                    String actionId = jsonObject2.has(InfinityConstants.Action_id)
		                            && !jsonObject2.get(InfinityConstants.Action_id).isJsonNull()
		                                    ? jsonObject2.get(InfinityConstants.Action_id).getAsString().trim()
		                                    : null;
		                    if (StringUtils.isBlank(actionId)) {
		                        actionId = jsonObject2.has(InfinityConstants.action_id)
		                                && !jsonObject2.get(InfinityConstants.action_id).isJsonNull()
		                                        ? jsonObject2.get(InfinityConstants.action_id).getAsString().trim()
		                                        : null;
		                    }

		                    if (cifEntry.equals(coreCustomerId) && "USER_MANAGEMENT".equals(featureId)) {
		                        if ("USER_MANAGEMENT_CREATE".equals(actionId)) {
		                            isCreatePermissionEnabled = true;
		                        }

		                        if ("USER_MANAGEMENT_EDIT".equals(actionId)) {
		                            isEditPermissionEnabled = true;
		                        }

		                        if ("USER_MANAGEMENT_VIEW".equals(actionId)) {
		                            isViewPermissionEnabled = true;
		                        }
		                    }
		                }
		            }
		        }

		        companyJsonObject.addProperty("isCreatePermissionEnabled", isCreatePermissionEnabled);
		        companyJsonObject.addProperty("isEditPermissionEnabled", isEditPermissionEnabled);
		        companyJsonObject.addProperty("isViewPermissionEnabled", isViewPermissionEnabled);

		        return isViewPermissionEnabled;
		    }
		 
		 private JsonArray getGroupForCompany(String id, String contractId, String coreCustomerID,
		            Map<String, Object> headerMap,
		            boolean isCustomRoleFlow) {
		        String filter = InfinityConstants.coreCustomerId
		                + DBPUtilitiesConstants.EQUAL + coreCustomerID
		                + DBPUtilitiesConstants.AND
		                + InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL
		                + contractId;
		        if (!isCustomRoleFlow) {
		            filter += DBPUtilitiesConstants.AND + InfinityConstants.Customer_id + DBPUtilitiesConstants.EQUAL + id;

		            Map<String, Object> input = new HashMap<String, Object>();
		            input.put(DBPUtilitiesConstants.FILTER, filter);

		            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
		                    URLConstants.CUSTOMER_GROUP_GET);

		            if (jsonObject.has(DBPDatasetConstants.DATASET_CUSTOMERGROUP)) {
		                JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CUSTOMERGROUP);
		                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
		                    return jsonElement.getAsJsonArray();
		                }
		            }
		        } else {
		            filter += DBPUtilitiesConstants.AND + InfinityConstants.customRoleId + DBPUtilitiesConstants.EQUAL + id;

		            Map<String, Object> input = new HashMap<String, Object>();
		            input.put(DBPUtilitiesConstants.FILTER, filter);

		            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
		                    URLConstants.CONTRACT_CUSTOM_ROLE_GET);
		            if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACTCUSTOMROLE)) {
		                JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACTCUSTOMROLE);
		                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
		                    return jsonElement.getAsJsonArray();
		                }
		            }
		        }

		        return new JsonArray();
		    }
		
		private JsonArray getCompanyList(Map<String, JsonObject> coreCustomerGroupInfo,
				Map<String, JsonObject> contracts, Map<String, JsonObject> contractCoreCustomers, String id,
				Map<String, Object> headerMap, Map<String, Map<String, Map<String, Map<String, String>>>> map,
				Map<String, String> companyMap, Map<String, Map<String, Map<String, String>>> accountMap,
				Map<String, FeatureActionLimitsDTO> featureActionDTOs, String loggedInUserId, JsonArray jsonArray,
				boolean isSuperAdmin, boolean isCustomRoleFlow) {

			JsonArray companyList = new JsonArray();

			JsonObject companyJsonObject = new JsonObject();

			Map<String, String> accountTypes = new HashMap<>();

			HashMap<String, Object> input = new HashMap<String, Object>();
			JsonObject accoutTypeJson = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
					URLConstants.ACCOUNT_TYPE_GET);

			if (accoutTypeJson.has(DBPDatasetConstants.DATASET_ACCOUNTTYPE)) {
				for (JsonElement jsonelement : accoutTypeJson.get(DBPDatasetConstants.DATASET_ACCOUNTTYPE)
						.getAsJsonArray()) {
					accountTypes.put(JSONUtil.getString(jsonelement.getAsJsonObject(), InfinityConstants.TypeID),
							JSONUtil.getString(jsonelement.getAsJsonObject(), InfinityConstants.displayName));
				}
			}

			Map<String, Set<String>> customerContracts = new HashMap<String, Set<String>>();
			Map<String, Set<String>> customerAccounts = new HashMap<String, Set<String>>();
			Map<String, Boolean> autoSyncAccountsMap = new HashMap<String, Boolean>();

			if (isSuperAdmin) {
				getUserContracts(id, customerContracts, headerMap, autoSyncAccountsMap, false);
				getAccountsForCustomer(id, customerAccounts, headerMap);
			} else {
				getUserContracts(loggedInUserId, customerContracts, headerMap, new HashMap<String, Boolean>(), false);
				getUserContracts(id, new HashMap<String, Set<String>>(), headerMap, autoSyncAccountsMap,
						isCustomRoleFlow);
				getAccountsForCustomer(loggedInUserId, customerAccounts, headerMap);
			}

			Map<String, Set<String>> contractCIFs = new HashMap<String, Set<String>>();

			getContractCIFs(contractCIFs, headerMap);

			Map<String, Map<String, Map<String, JsonObject>>> contractAccounts = new HashMap<String, Map<String, Map<String, JsonObject>>>();

			Map<String, String> accountOwnership = new HashMap<>();
			getAccountsForContract(accountOwnership, contractAccounts, headerMap);

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonObject2 = jsonArray.get(i).getAsJsonObject();
				String contractId = JSONUtil.getString(jsonObject2, InfinityConstants.contractId);
				String coreCustomerId = JSONUtil.getString(jsonObject2, InfinityConstants.coreCustomerId);
				String accountId = JSONUtil.getString(jsonObject2, InfinityConstants.Account_id);
				String accountName = JSONUtil.getString(jsonObject2, InfinityConstants.AccountName);
				String accountType = JSONUtil.getString(jsonObject2, InfinityConstants.accountType);

				Map<String, String> account = new HashMap<String, String>();

				account.put(InfinityConstants.accountName, accountName);
				account.put(InfinityConstants.accountType, accountType);
				if (StringUtils.isNotBlank(accountOwnership.get("accountId")))
					account.put("ownerType", accountOwnership.get(accountId));
				else
					account.put("ownerType", "Owner");
				if (!contractCIFs.containsKey(contractId) || !customerContracts.containsKey(contractId)
						|| !contractAccounts.containsKey(contractId)) {
					continue;
				}

				if (!map.containsKey(contractId)) {
					map.put(contractId, new HashMap<String, Map<String, Map<String, String>>>());
				}

				if (!contractCIFs.get(contractId).contains(coreCustomerId)
						|| !customerContracts.get(contractId).contains(coreCustomerId)
						|| !contractAccounts.get(contractId).containsKey(coreCustomerId)
						|| !customerAccounts.containsKey(coreCustomerId)) {
					continue;
				}

				if (!map.get(contractId).containsKey(coreCustomerId)) {
					map.get(contractId).put(coreCustomerId, new HashMap<String, Map<String, String>>());
				}

				if (!contractAccounts.get(contractId).get(coreCustomerId).containsKey(accountId)
						|| !customerAccounts.get(coreCustomerId).contains(accountId)) {
					continue;
				}

				if (!map.get(contractId).get(coreCustomerId).containsKey(accountId)) {
					map.get(contractId).get(coreCustomerId).put(accountId, account);
				}

			}

			input = new HashMap<String, Object>();
			JsonObject membershipJsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
					URLConstants.MEMBERGROUP_GET);
			Map<String, String> groupsInfo = new HashMap<>();
			if (membershipJsonObject.has(DBPDatasetConstants.DATASET_MEMBERGROUP)) {
				for (JsonElement jsonelement : membershipJsonObject.get(DBPDatasetConstants.DATASET_MEMBERGROUP)
						.getAsJsonArray()) {
					if (InfinityConstants.SID_ACTIVE
							.equals(JSONUtil.getString(jsonelement.getAsJsonObject(), InfinityConstants.Status_id))) {
						groupsInfo.put(JSONUtil.getString(jsonelement.getAsJsonObject(), "id"),
								JSONUtil.getString(jsonelement.getAsJsonObject(), "Name"));
					}
				}
			}

			Map<String, JsonObject> customerGroups = new HashMap<String, JsonObject>();
			Map<String, JsonArray> groupServiceDefinitions = new HashMap<String, JsonArray>();

			ContractBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(ContractBackendDelegate.class);

			Map<String, JsonObject> serviceDefinitions = new HashMap<String, JsonObject>();

			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(new HashMap<String, Object>(), headerMap,
					URLConstants.SERVICEDEFINITION_GET);
			input.clear();

			if (jsonObject.has(DBPDatasetConstants.DATASET_SERVICEDEFINITION)) {
				JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_SERVICEDEFINITION);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					jsonArray = jsonElement.getAsJsonArray();
					for (JsonElement entrySet : jsonArray) {
						serviceDefinitions.put(entrySet.getAsJsonObject().get(InfinityConstants.id).getAsString(),
								entrySet.getAsJsonObject());
					}
				}
			}

			for (String contractEntry : map.keySet()) {

				JsonElement jsonElement = null;
				jsonArray = new JsonArray();
				String contractName = null;
				String serviceDefinitionId = null;
				String contractType = null;

				JsonObject contractJsonObject = new JsonObject();

				if (contracts.containsKey(contractEntry)) {
					contractJsonObject = contracts.get(contractEntry);
				} else {
					String filter = InfinityConstants.id + DBPUtilitiesConstants.EQUAL + contractEntry;
					input.put(DBPUtilitiesConstants.FILTER, filter);
					jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap, URLConstants.CONTRACT_GET);
					input.clear();

					if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACT)) {
						jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT);
						if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
							jsonArray = jsonElement.getAsJsonArray();
							for (Entry<String, JsonElement> entrySet : jsonArray.get(0).getAsJsonObject().entrySet()) {
								contractJsonObject.add(entrySet.getKey(), entrySet.getValue());
							}
							contracts.put(contractEntry, contractJsonObject);
						} else
							continue;
					} else
						continue;
				}

				ContractCoreCustomerBackendDelegate contractCoreCustomerBackendDelegate = DBPAPIAbstractFactoryImpl
						.getBackendDelegate(ContractCoreCustomerBackendDelegate.class);
				List<ContractCoreCustomersDTO> list = new ArrayList<ContractCoreCustomersDTO>();
				try {
					list = contractCoreCustomerBackendDelegate.getContractCoreCustomers(contractEntry, false, true,
							headerMap);
				} catch (ApplicationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				serviceDefinitionId = JSONUtil.getString(contractJsonObject, "servicedefinitionId");
				contractName = JSONUtil.getString(contractJsonObject, InfinityConstants.name);
				contractType = JSONUtil.getString(contractJsonObject, InfinityConstants.serviceType);

				Map<String, Map<String, Map<String, String>>> cifs = map.get(contractEntry);

				for (String cifEntry : cifs.keySet()) {

					companyJsonObject = new JsonObject();

					if (!isViewPermissionEnbled(companyJsonObject, cifEntry, loggedInUserId, isSuperAdmin, headerMap)) {
						continue;
					}

					JsonObject cifJsonObject = new JsonObject();
					String companyName = cifEntry;
					String isPrimary = "false";
					if (contractCoreCustomers.containsKey(cifEntry)) {
						cifJsonObject = contractCoreCustomers.get(cifEntry);
					} else {
						String filter = InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cifEntry
								+ DBPUtilitiesConstants.AND + InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL
								+ contractEntry;

						input.put(DBPUtilitiesConstants.FILTER, filter);

						jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
								URLConstants.CONTRACTCORECUSTOMER_GET);

						if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACTCORECUSTOMERS)) {
							jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACTCORECUSTOMERS);
							if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
								jsonArray = jsonElement.getAsJsonArray();
								cifJsonObject = jsonArray.get(0).getAsJsonObject();
								MembershipDTO membershipDTO = new MembershipDTO();
								membershipDTO.setId(cifEntry);
								CoreCustomerBackendDelegate coreCustomerBackendDelegate = DBPAPIAbstractFactoryImpl
										.getBackendDelegate(CoreCustomerBackendDelegate.class);
								try {
									DBXResult membershipresponse = coreCustomerBackendDelegate
											.searchCoreCustomers(membershipDTO, headerMap);
									if (membershipresponse != null && membershipresponse.getResponse() != null) {
										JsonObject membershipResponse = ((JsonArray) membershipresponse.getResponse())
												.get(0).getAsJsonObject();
										for (Entry<String, JsonElement> entrySet : membershipResponse.entrySet()) {
											cifJsonObject.add(entrySet.getKey(), entrySet.getValue());
										}
									}
								} catch (ApplicationException e) {
								}
								contractCoreCustomers.put(cifEntry, cifJsonObject);
							} else
								continue;
						} else
							continue;
					}

					for (ContractCoreCustomersDTO contractCoreCustomer : list) {
						if (contractCoreCustomer.getCoreCustomerId().equals(cifEntry)) {
							companyJsonObject.addProperty(InfinityConstants.coreCustomerId,
									contractCoreCustomer.getCoreCustomerId());
							companyName = contractCoreCustomer.getCoreCustomerName();
							companyJsonObject.addProperty(InfinityConstants.coreCustomerName, companyName);
							companyJsonObject.addProperty(InfinityConstants.isBusiness,
									contractCoreCustomer.getIsBusiness());
							companyJsonObject.addProperty(InfinityConstants.addressLine1,
									contractCoreCustomer.getAddressLine1());
							companyJsonObject.addProperty(InfinityConstants.addressLine2,
									contractCoreCustomer.getAddressLine2());
							companyJsonObject.addProperty(InfinityConstants.taxId, contractCoreCustomer.getTaxId());
							companyJsonObject.addProperty(InfinityConstants.city, contractCoreCustomer.getCityName());
							companyJsonObject.addProperty(InfinityConstants.state, contractCoreCustomer.getState());
							companyJsonObject.addProperty(InfinityConstants.country,
									contractCoreCustomer.getCityName());
							companyJsonObject.addProperty(InfinityConstants.zipCode,
									contractCoreCustomer.getCityName());
							companyJsonObject.addProperty(InfinityConstants.phone, contractCoreCustomer.getCityName());
							companyJsonObject.addProperty(InfinityConstants.email, contractCoreCustomer.getCityName());
							companyJsonObject.addProperty(InfinityConstants.industry,
									contractCoreCustomer.getCityName());
						}
					}
					BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
					backendIdentifierDTO.setCustomer_id(id);
					backendIdentifierDTO.setBackendType(DTOConstants.CORE);
					if (IntegrationTemplateURLFinder.isIntegrated) {
						backendIdentifierDTO.setBackendType(
								IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
					}
					backendIdentifierDTO = (BackendIdentifierDTO) backendIdentifierDTO.loadDTO();
					if (backendIdentifierDTO != null && cifEntry.equals(backendIdentifierDTO.getBackendId())) {
						isPrimary = "true";
					}

					companyMap.put(cifEntry, companyName);
					accountMap.put(cifEntry, map.get(contractEntry).get(cifEntry));
					companyJsonObject.addProperty(InfinityConstants.companyName, companyName);
					companyJsonObject.addProperty(InfinityConstants.contractId, contractEntry);
					companyJsonObject.addProperty(InfinityConstants.contractName, contractName);
					companyJsonObject.addProperty(InfinityConstants.contractType, contractType);
					companyJsonObject.addProperty(InfinityConstants.serviceDefinition, serviceDefinitionId);
					companyJsonObject.addProperty(InfinityConstants.serviceDefinitionName,
							serviceDefinitions.get(serviceDefinitionId) != null
									? serviceDefinitions.get(serviceDefinitionId).get(InfinityConstants.name)
											.getAsString()
									: "");
					companyJsonObject.addProperty(InfinityConstants.autoSyncAccounts,
							autoSyncAccountsMap.get(cifEntry));
					companyJsonObject.addProperty(InfinityConstants.cif, cifEntry);
					companyJsonObject.addProperty(InfinityConstants.isPrimary, isPrimary);

					JsonObject groupJsonObject = new JsonObject();

					if (customerGroups.containsKey(id.concat(contractEntry.concat(cifEntry)))) {
						cifJsonObject = customerGroups.get(id.concat(contractEntry.concat(cifEntry)));
					} else {

						jsonArray = getGroupForCompany(id, contractEntry, cifEntry, headerMap, isCustomRoleFlow);
						if (jsonArray.size() > 0) {
							groupJsonObject = jsonArray.get(0).getAsJsonObject();
							contractCoreCustomers.put(id.concat(contractEntry.concat(cifEntry)), groupJsonObject);
						} else
							continue;
					}

					String groupId = "";

					if (isCustomRoleFlow) {
						groupId = JSONUtil.getString(groupJsonObject, InfinityConstants.roleId);
					} else {
						groupId = JSONUtil.getString(groupJsonObject, InfinityConstants.Group_id);

					}
					companyJsonObject.addProperty(InfinityConstants.roleId, groupId);
					companyJsonObject.addProperty(InfinityConstants.userRole, groupsInfo.get(groupId));
					JsonObject coreCustomerGroupJsonObject = new JsonObject();
					coreCustomerGroupJsonObject.addProperty(InfinityConstants.roleId, groupId);
					coreCustomerGroupJsonObject.addProperty(InfinityConstants.userRole, groupsInfo.get(groupId));
					coreCustomerGroupInfo.put(cifEntry, coreCustomerGroupJsonObject);
					try {
						FeatureActionLimitsDTO coreCustomerFeatureActionDTO = backendDelegate
								.getRestrictiveFeatureActionLimits(serviceDefinitionId, contractEntry, groupId,
										cifEntry, "", headerMap, false, "");
						featureActionDTOs.put(cifEntry, coreCustomerFeatureActionDTO);
					} catch (ApplicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					JsonArray validRoles = new JsonArray();
					if (groupServiceDefinitions.containsKey(serviceDefinitionId)) {
						validRoles = groupServiceDefinitions.get(serviceDefinitionId);
					} else {
						String filter = "serviceDefinitionId" + DBPUtilitiesConstants.EQUAL + serviceDefinitionId;
						input.put(DBPUtilitiesConstants.FILTER, filter);
						jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
								URLConstants.GROUP_SERVICEDEFINITION);
						if (jsonObject.has(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION)) {
							jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION);
							if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
								jsonArray = jsonElement.getAsJsonArray();
								for (JsonElement jsonelement : jsonArray) {
									JsonObject json = new JsonObject();
									if (groupsInfo.containsKey(
											JSONUtil.getString(jsonelement.getAsJsonObject(), "Group_id"))) {
										json.addProperty(InfinityConstants.roleId,
												JSONUtil.getString(jsonelement.getAsJsonObject(), "Group_id"));
										json.addProperty(InfinityConstants.userRole,
												groupsInfo.containsKey(
														JSONUtil.getString(jsonelement.getAsJsonObject(), "Group_id"))
																? groupsInfo.get(JSONUtil.getString(
																		jsonelement.getAsJsonObject(), "Group_id"))
																: null);
										validRoles.add(json);
									}
								}
								groupServiceDefinitions.put(serviceDefinitionId, validRoles);
							} else
								continue;
						} else
							continue;
					}

					companyJsonObject.add("validRoles", validRoles);

					JsonObject accountObject = new JsonObject();
					JsonArray accounts = new JsonArray();
					JsonArray excludedAccounts = new JsonArray();

					for (Entry<String, Map<String, String>> accountEntry : cifs.get(cifEntry).entrySet()) {
						accountObject = new JsonObject();
						accountObject.addProperty(InfinityConstants.accountId, accountEntry.getKey());
						accountObject.addProperty(InfinityConstants.accountName,
								accountEntry.getValue().get(InfinityConstants.accountName));
						accountObject.addProperty(InfinityConstants.accountType,
								accountEntry.getValue().get(InfinityConstants.accountType));
						accountObject.addProperty(InfinityConstants.isEnabled, "true");
						accountObject.addProperty(InfinityConstants.ownerType,
								accountEntry.getValue().get(InfinityConstants.ownerType));
						accounts.add(accountObject);
					}

					for (Entry<String, JsonObject> cifAccount : contractAccounts.get(contractEntry).get(cifEntry)
							.entrySet()) {
						if (!map.get(contractEntry).get(cifEntry).containsKey(cifAccount.getKey())) {
							accountObject = new JsonObject();
							accountObject.addProperty(InfinityConstants.accountId, cifAccount.getKey());
							accountObject.add(InfinityConstants.accountName,
									cifAccount.getValue().get(InfinityConstants.accountName));
							accountObject.addProperty(InfinityConstants.accountType, accountTypes
									.get(cifAccount.getValue().get(InfinityConstants.typeId).getAsString()));
							accountObject.addProperty(InfinityConstants.isEnabled, "false");
							accountObject.add(InfinityConstants.ownerType,
									cifAccount.getValue().get(InfinityConstants.ownerType));
							excludedAccounts.add(accountObject);
							accounts.add(accountObject);
						}
					}

					companyJsonObject.add(InfinityConstants.accounts, accounts);
					companyJsonObject.add(InfinityConstants.excludedAccounts, excludedAccounts);
					companyList.add(companyJsonObject);
				}
			}

			return companyList;
		}
		
		private boolean isAccountLevel(String actionId, JsonObject jsonObject) {
	        if (jsonObject == null) {
	            return true;
	        }
	        return jsonObject.has(InfinityConstants.isAccountLevel)
	                && !jsonObject.get(InfinityConstants.isAccountLevel).isJsonNull() &&
	                ("1".equals(jsonObject.get(InfinityConstants.isAccountLevel).getAsString())
	                        || Boolean.parseBoolean(jsonObject.get(InfinityConstants.isAccountLevel).getAsString()));
	    }

		private void addPermissions(Map<String, JsonObject> coreCustomerGroupInfo, Map<String, JsonObject> contracts,
				Map<String, JsonObject> contractCoreCustomers, String customerId, Map<String, Object> headerMap,
				Map<String, Map<String, Map<String, Map<String, String>>>> map, JsonObject customerJsonObject,
				Map<String, Map<String, Map<String, String>>> accountMap, Map<String, String> companyMap,
				Map<String, FeatureActionLimitsDTO> featureActionDTOs, JsonArray jsonArray) {

			Map<String, Map<String, Map<String, Boolean>>> globalActions = new HashMap<String, Map<String, Map<String, Boolean>>>();
			Map<String, Map<String, Map<String, Map<String, Boolean>>>> accountActions = new HashMap<String, Map<String, Map<String, Map<String, Boolean>>>>();
			Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>> transactionLimits = new HashMap<String, Map<String, Map<String, Map<String, Map<String, String>>>>>();

			Map<String, Map<String, Map<String, Map<String, Double>>>> limitGroups = new HashMap<String, Map<String, Map<String, Map<String, Double>>>>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonObject2 = jsonArray.get(i).getAsJsonObject();
				String coreCustomerId = jsonObject2.has(InfinityConstants.coreCustomerId)
						&& !jsonObject2.get(InfinityConstants.coreCustomerId).isJsonNull()
								? jsonObject2.get(InfinityConstants.coreCustomerId).getAsString()
								: null;
				String value = jsonObject2.has(InfinityConstants.value)
						&& !jsonObject2.get(InfinityConstants.value).isJsonNull()
								? jsonObject2.get(InfinityConstants.value).getAsString()
								: null;
				String limitTypeId = jsonObject2.has(InfinityConstants.LimitType_id)
						&& !jsonObject2.get(InfinityConstants.LimitType_id).isJsonNull()
								? jsonObject2.get(InfinityConstants.LimitType_id).getAsString()
								: null;
				if (StringUtils.isBlank(limitTypeId)) {
					limitTypeId = jsonObject2.has(InfinityConstants.limitType_id)
							&& !jsonObject2.get(InfinityConstants.limitType_id).isJsonNull()
									? jsonObject2.get(InfinityConstants.limitType_id).getAsString()
									: null;
				}
				String featureId = jsonObject2.has(InfinityConstants.featureId)
						&& !jsonObject2.get(InfinityConstants.featureId).isJsonNull()
								? jsonObject2.get(InfinityConstants.featureId).getAsString().trim()
								: null;
				String actionId = jsonObject2.has(InfinityConstants.Action_id)
						&& !jsonObject2.get(InfinityConstants.Action_id).isJsonNull()
								? jsonObject2.get(InfinityConstants.Action_id).getAsString().trim()
								: null;
				if (StringUtils.isBlank(actionId)) {
					actionId = jsonObject2.has(InfinityConstants.action_id)
							&& !jsonObject2.get(InfinityConstants.action_id).isJsonNull()
									? jsonObject2.get(InfinityConstants.action_id).getAsString().trim()
									: null;
				}
				String accountId = jsonObject2.has(InfinityConstants.Account_id)
						&& !jsonObject2.get(InfinityConstants.Account_id).isJsonNull()
								? jsonObject2.get(InfinityConstants.Account_id).getAsString()
								: null;
				if (StringUtils.isBlank(accountId)) {
					accountId = jsonObject2.has(InfinityConstants.account_id)
							&& !jsonObject2.get(InfinityConstants.account_id).isJsonNull()
									? jsonObject2.get(InfinityConstants.account_id).getAsString()
									: null;
				}

				String limitGroupId = jsonObject2.has(InfinityConstants.limitGroupId)
						&& !jsonObject2.get(InfinityConstants.limitGroupId).isJsonNull()
								? jsonObject2.get(InfinityConstants.limitGroupId).getAsString()
								: null;

				FeatureActionLimitsDTO featureActionDTO = featureActionDTOs.get(coreCustomerId);
				if (featureActionDTO == null) {
					continue;
				}

				limitGroupId = featureActionDTO.getActionsInfo().containsKey(actionId)
						? featureActionDTO.getActionsInfo().get(actionId).get(InfinityConstants.limitGroupId)
								.getAsString()
						: limitGroupId;

				if (StringUtils.isBlank(accountId) && StringUtils.isBlank(limitGroupId)) {
					if (!globalActions.containsKey(coreCustomerId)) {
						globalActions.put(coreCustomerId, new HashMap<String, Map<String, Boolean>>());
					}
					if (!featureActionDTO.getFeatureaction().containsKey(featureId)) {
						continue;
					}
					if (!globalActions.get(coreCustomerId).containsKey(featureId)) {
						globalActions.get(coreCustomerId).put(featureId, new HashMap<String, Boolean>());
					}
					if (!featureActionDTO.getFeatureaction().get(featureId).contains(actionId)) {
						continue;
					}
					globalActions.get(coreCustomerId).get(featureId).put(actionId, true);
				} else {
					if (StringUtils.isNotBlank(accountId)) {
						if (!accountActions.containsKey(coreCustomerId)) {
							accountActions.put(coreCustomerId,
									new HashMap<String, Map<String, Map<String, Boolean>>>());
						}
						if (!featureActionDTO.getFeatureaction().containsKey(featureId)
								&& !featureActionDTO.getMonetaryActionLimits().containsKey(featureId)) {
							continue;
						}
						if (!accountActions.get(coreCustomerId).containsKey(accountId)) {
							accountActions.get(coreCustomerId).put(accountId,
									new HashMap<String, Map<String, Boolean>>());
						}
						if ((featureActionDTO.getFeatureaction().get(featureId) == null
								|| !featureActionDTO.getFeatureaction().get(featureId).contains(actionId))
								&& (featureActionDTO.getMonetaryActionLimits().get(featureId) == null
										|| !featureActionDTO.getMonetaryActionLimits().get(featureId)
												.containsKey(actionId))) {
							continue;
						}
						if (!accountActions.get(coreCustomerId).get(accountId).containsKey(featureId)) {
							accountActions.get(coreCustomerId).get(accountId).put(featureId,
									new HashMap<String, Boolean>());
						}
						accountActions.get(coreCustomerId).get(accountId).get(featureId).put(actionId, true);
					}

					if (StringUtils.isNotBlank(accountId) && StringUtils.isNotBlank(limitTypeId)) {
						if (!transactionLimits.containsKey(coreCustomerId)) {
							transactionLimits.put(coreCustomerId,
									new HashMap<String, Map<String, Map<String, Map<String, String>>>>());
							limitGroups.put(coreCustomerId, new HashMap<String, Map<String, Map<String, Double>>>());
						}

						if (!transactionLimits.get(coreCustomerId).containsKey(accountId)) {
							transactionLimits.get(coreCustomerId).put(accountId,
									new HashMap<String, Map<String, Map<String, String>>>());
							limitGroups.get(coreCustomerId).put(accountId, new HashMap<String, Map<String, Double>>());
						}
						if (!featureActionDTO.getMonetaryActionLimits().containsKey(featureId)) {
							continue;
						}
						if (!transactionLimits.get(coreCustomerId).get(accountId).containsKey(featureId)) {
							transactionLimits.get(coreCustomerId).get(accountId).put(featureId,
									new HashMap<String, Map<String, String>>());
						}
						if (!featureActionDTO.getMonetaryActionLimits().get(featureId).containsKey(actionId)) {
							continue;
						}
						if (!transactionLimits.get(coreCustomerId).get(accountId).get(featureId)
								.containsKey(actionId)) {
							transactionLimits.get(coreCustomerId).get(accountId).get(featureId).put(actionId,
									new HashMap<String, String>());
						}

						Map<String, String> limitMap = featureActionDTO.getMonetaryActionLimits().get(featureId)
								.get(actionId);

						Double limit2 = new Double("0.0");
						if (limitTypeId.equals(InfinityConstants.PRE_APPROVED_DAILY_LIMIT)
								|| limitTypeId.equals(InfinityConstants.AUTO_DENIED_DAILY_LIMIT)
								|| limitTypeId.equals(InfinityConstants.DAILY_LIMIT)) {
							limit2 = Double.parseDouble((limitMap.containsKey(InfinityConstants.DAILY_LIMIT)
									&& StringUtils.isNotBlank(limitMap.get(InfinityConstants.DAILY_LIMIT)))
											? limitMap.get(InfinityConstants.DAILY_LIMIT)
											: "0.0");
							value = Math.min(limit2, Double.parseDouble(value)) + "";

							if (limitTypeId.equals(InfinityConstants.DAILY_LIMIT)) {
								value = limit2 + "";
								if (!limitGroups.get(coreCustomerId).get(accountId).containsKey(limitGroupId)) {
									limitGroups.get(coreCustomerId).get(accountId).put(limitGroupId,
											new HashMap<String, Double>());

								}
								if (!limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
										.containsKey(InfinityConstants.DAILY_LIMIT)) {
									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.DAILY_LIMIT, Double.parseDouble(value));
								} else {
									Double limitValue = limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.get(InfinityConstants.DAILY_LIMIT);

									limitValue = limitValue + Double.parseDouble(value);

									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.DAILY_LIMIT, limitValue);
								}
							}
						} else if (limitTypeId.equals(InfinityConstants.PRE_APPROVED_WEEKLY_LIMIT)
								|| limitTypeId.equals(InfinityConstants.AUTO_DENIED_WEEKLY_LIMIT)
								|| limitTypeId.equals(InfinityConstants.WEEKLY_LIMIT)) {
							limit2 = Double.parseDouble((limitMap.containsKey(InfinityConstants.WEEKLY_LIMIT)
									&& StringUtils.isNotBlank(limitMap.get(InfinityConstants.WEEKLY_LIMIT)))
											? limitMap.get(InfinityConstants.WEEKLY_LIMIT)
											: "0.0");
							value = Math.min(limit2, Double.parseDouble(value)) + "";

							if (limitTypeId.equals(InfinityConstants.WEEKLY_LIMIT)) {

								value = limit2 + "";
								if (!limitGroups.get(coreCustomerId).get(accountId).containsKey(limitGroupId)) {
									limitGroups.get(coreCustomerId).get(accountId).put(limitGroupId,
											new HashMap<String, Double>());
								}

								if (!limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
										.containsKey(InfinityConstants.WEEKLY_LIMIT)) {
									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.WEEKLY_LIMIT, Double.parseDouble(value));
								} else {
									Double limitValue = limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.get(InfinityConstants.WEEKLY_LIMIT);

									limitValue = limitValue + Double.parseDouble(value);

									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.WEEKLY_LIMIT, limitValue);
								}
							}
						} else if (limitTypeId.equals(InfinityConstants.PRE_APPROVED_TRANSACTION_LIMIT)
								|| limitTypeId.equals(InfinityConstants.AUTO_DENIED_TRANSACTION_LIMIT)
								|| limitTypeId.equals(InfinityConstants.MAX_TRANSACTION_LIMIT)) {
							limit2 = Double.parseDouble((limitMap.containsKey(InfinityConstants.MAX_TRANSACTION_LIMIT)
									&& StringUtils.isNotBlank(limitMap.get(InfinityConstants.MAX_TRANSACTION_LIMIT)))
											? limitMap.get(InfinityConstants.MAX_TRANSACTION_LIMIT)
											: "0.0");
							value = Math.min(limit2, Double.parseDouble(value)) + "";
							if (limitTypeId.equals(InfinityConstants.MAX_TRANSACTION_LIMIT)) {

								if (!limitGroups.get(coreCustomerId).get(accountId).containsKey(limitGroupId)) {
									limitGroups.get(coreCustomerId).get(accountId).put(limitGroupId,
											new HashMap<String, Double>());
								}

								value = limit2 + "";
								// if
								// (!limitGroups.get(coreCustomerId).get(accountId).containsKey(limitGroupId)) {
								// limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
								// .put(InfinityConstants.MAX_TRANSACTION_LIMIT, Double.parseDouble(value));
								// }

								if (!limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
										.containsKey(InfinityConstants.MAX_TRANSACTION_LIMIT)) {
									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.MAX_TRANSACTION_LIMIT, Double.parseDouble(value));
								} else {
									Double limitValue = limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.get(InfinityConstants.MAX_TRANSACTION_LIMIT);

									limitValue = Math.max(limitValue, Double.parseDouble(value));

									limitGroups.get(coreCustomerId).get(accountId).get(limitGroupId)
											.put(InfinityConstants.MAX_TRANSACTION_LIMIT, limitValue);
								}
							}
						}

						transactionLimits.get(coreCustomerId).get(accountId).get(featureId).get(actionId)
								.put(limitTypeId, value);
						transactionLimits.get(coreCustomerId).get(accountId).get(featureId).get(actionId)
								.put(InfinityConstants.limitGroupId, limitGroupId);
						transactionLimits.get(coreCustomerId).get(accountId).get(featureId).get(actionId)
								.put(InfinityConstants.isEnabled, "true");

					}
				}

			}
			for (String coreCustomerId : featureActionDTOs.keySet()) {
				FeatureActionLimitsDTO featureActionDTO = featureActionDTOs.get(coreCustomerId);
				if (!globalActions.containsKey(coreCustomerId)) {
					globalActions.put(coreCustomerId, new HashMap<String, Map<String, Boolean>>());
				}

				for (Entry<String, Set<String>> featureEntry : featureActionDTO.getFeatureaction().entrySet()) {
					for (String actionId : featureEntry.getValue()) {
						if ((!globalActions.get(coreCustomerId).containsKey(featureEntry.getKey())
								|| !globalActions.get(coreCustomerId).get(featureEntry.getKey()).containsKey(actionId))
								&& !isAccountLevel(actionId, featureActionDTO.getActionsInfo().get(actionId))) {
							if (!globalActions.get(coreCustomerId).containsKey(featureEntry.getKey())) {
								globalActions.get(coreCustomerId).put(featureEntry.getKey(),
										new HashMap<String, Boolean>());
							}
							globalActions.get(coreCustomerId).get(featureEntry.getKey()).put(actionId, false);
						}
					}
				}

				if (!accountActions.containsKey(coreCustomerId)) {
					accountActions.put(coreCustomerId, new HashMap<String, Map<String, Map<String, Boolean>>>());
					transactionLimits.put(coreCustomerId,
							new HashMap<String, Map<String, Map<String, Map<String, String>>>>());
				}

				if (!limitGroups.containsKey(coreCustomerId)) {
					limitGroups.put(coreCustomerId, new HashMap<String, Map<String, Map<String, Double>>>());
				}

				if (!transactionLimits.containsKey(coreCustomerId)) {
					transactionLimits.put(coreCustomerId,
							new HashMap<String, Map<String, Map<String, Map<String, String>>>>());
				}

				for (String accountId : accountMap.get(coreCustomerId).keySet()) {
					if (!accountActions.get(coreCustomerId).containsKey(accountId)) {
						accountActions.get(coreCustomerId).put(accountId, new HashMap<String, Map<String, Boolean>>());
					}

					if (!transactionLimits.get(coreCustomerId).containsKey(accountId)) {
						transactionLimits.get(coreCustomerId).put(accountId,
								new HashMap<String, Map<String, Map<String, String>>>());
					}

					for (Entry<String, Set<String>> featureEntry : featureActionDTO.getFeatureaction().entrySet()) {
						for (String actionId : featureEntry.getValue()) {
							if ((!accountActions.get(coreCustomerId).get(accountId).containsKey(featureEntry.getKey())
									|| !accountActions.get(coreCustomerId).get(accountId).get(featureEntry.getKey())
											.containsKey(actionId))
									&& isAccountLevel(actionId, featureActionDTO.getActionsInfo().get(actionId))) {
								if (!accountActions.get(coreCustomerId).get(accountId)
										.containsKey(featureEntry.getKey())) {
									accountActions.get(coreCustomerId).get(accountId).put(featureEntry.getKey(),
											new HashMap<String, Boolean>());
								}
								accountActions.get(coreCustomerId).get(accountId).get(featureEntry.getKey())
										.put(actionId, false);
							}
						}
					}
				}

				for (String accountId : accountMap.get(coreCustomerId).keySet()) {
					for (String featureId : featureActionDTO.getMonetaryActionLimits().keySet()) {

						if (!accountActions.get(coreCustomerId).get(accountId).containsKey(featureId)) {
							accountActions.get(coreCustomerId).get(accountId).put(featureId,
									new HashMap<String, Boolean>());
						}

						for (String actionId : featureActionDTO.getMonetaryActionLimits().get(featureId).keySet()) {
							if (!accountActions.get(coreCustomerId).get(accountId).get(featureId)
									.containsKey(actionId)) {
								accountActions.get(coreCustomerId).get(accountId).get(featureId).put(actionId, false);
							}
						}
					}
				}

			}

			JsonArray globalActionLimits = new JsonArray();
			Set<Entry<String, Map<String, Map<String, Boolean>>>> entries = globalActions.entrySet();
			for (Entry<String, Map<String, Map<String, Boolean>>> entry : entries) {
				JsonObject jsonObject = new JsonObject();

				for (Entry<String, JsonElement> entrySet : contractCoreCustomers.get(entry.getKey()).entrySet()) {
					jsonObject.add(entrySet.getKey(), entrySet.getValue());
					if (InfinityConstants.contractId.equalsIgnoreCase(entrySet.getKey())) {
						for (Entry<String, JsonElement> contractEntrySet : contracts
								.get(entrySet.getValue().getAsString()).entrySet()) {
							if ("name".equalsIgnoreCase(contractEntrySet.getKey()))
								jsonObject.add("contractName", contractEntrySet.getValue());
							else
								jsonObject.add(contractEntrySet.getKey(), contractEntrySet.getValue());
						}
					}
				}

				jsonObject.addProperty(InfinityConstants.companyName, companyMap.get(entry.getKey()));
				jsonObject.addProperty(InfinityConstants.cif, entry.getKey());
				if (coreCustomerGroupInfo.containsKey(entry.getKey())
						&& coreCustomerGroupInfo.get(entry.getKey()) != null) {
					for (Entry<String, JsonElement> entrySet : coreCustomerGroupInfo.get(entry.getKey()).entrySet()) {
						jsonObject.add(entrySet.getKey(), entrySet.getValue());
					}
				}
				JsonArray features = new JsonArray();

				FeatureActionLimitsDTO actionLimitsDTO = featureActionDTOs.get(entry.getKey());

				Set<Entry<String, Map<String, Boolean>>> featureEntries = entry.getValue().entrySet();
				for (Entry<String, Map<String, Boolean>> featureEntry : featureEntries) {
					JsonObject featureJsonObject = actionLimitsDTO.getFeatureInfo().get(featureEntry.getKey());
					JsonObject feature = new JsonObject();
					for (Entry<String, JsonElement> featureJsonEntry : featureJsonObject.entrySet()) {
						feature.add(featureJsonEntry.getKey(), featureJsonEntry.getValue());
					}

					JsonArray permissions = new JsonArray();
					for (Entry<String, Boolean> actionentry : featureEntry.getValue().entrySet()) {
						JsonObject permission = new JsonObject();
						permission.addProperty(InfinityConstants.permissionType, actionentry.getKey().trim());
						JsonObject action = actionLimitsDTO.getActionsInfo().get(actionentry.getKey());
						for (Entry<String, JsonElement> actionJsonEntry : action.entrySet()) {
							permission.add(actionJsonEntry.getKey(), actionJsonEntry.getValue());
						}
						permission.addProperty(InfinityConstants.isEnabled, actionentry.getValue().toString());
						permissions.add(permission);
					}
					feature.add(InfinityConstants.permissions, permissions);
					features.add(feature);
				}
				jsonObject.add(InfinityConstants.features, features);
				globalActionLimits.add(jsonObject);
			}
			customerJsonObject.add(InfinityConstants.globalLevelPermissions, globalActionLimits);

			JsonArray accountLevelPermissions = new JsonArray();
			for (Entry<String, Map<String, Map<String, Map<String, Boolean>>>> entry : accountActions.entrySet()) {
				JsonObject jsonObject = new JsonObject();

				for (Entry<String, JsonElement> entrySet : contractCoreCustomers.get(entry.getKey()).entrySet()) {
					jsonObject.add(entrySet.getKey(), entrySet.getValue());
					if (InfinityConstants.contractId.equalsIgnoreCase(entrySet.getKey())) {
						for (Entry<String, JsonElement> contractEntrySet : contracts
								.get(entrySet.getValue().getAsString()).entrySet()) {
							if ("name".equalsIgnoreCase(contractEntrySet.getKey()))
								jsonObject.add("contractName", contractEntrySet.getValue());
							else
								jsonObject.add(contractEntrySet.getKey(), contractEntrySet.getValue());
						}
					}
				}
				jsonObject.addProperty(InfinityConstants.companyName, companyMap.get(entry.getKey()));
				jsonObject.addProperty(InfinityConstants.cif, entry.getKey());
				if (coreCustomerGroupInfo.containsKey(entry.getKey())
						&& coreCustomerGroupInfo.get(entry.getKey()) != null) {
					for (Entry<String, JsonElement> entrySet : coreCustomerGroupInfo.get(entry.getKey()).entrySet()) {
						jsonObject.add(entrySet.getKey(), entrySet.getValue());
					}
				}
				JsonArray accounts = new JsonArray();

				FeatureActionLimitsDTO actionLimitsDTO = featureActionDTOs.get(entry.getKey().trim());

				Set<Entry<String, Map<String, Map<String, Boolean>>>> accountEntries = entry.getValue().entrySet();
				for (Entry<String, Map<String, Map<String, Boolean>>> accountEntry : accountEntries) {
					JsonObject account = new JsonObject();
					if (!accountMap.containsKey(entry.getKey())
							|| !accountMap.get(entry.getKey()).containsKey(accountEntry.getKey())) {
						continue;
					}
					String accountName = accountMap.get(entry.getKey()).get(accountEntry.getKey())
							.get(InfinityConstants.accountName);
					String accountType = accountMap.get(entry.getKey()).get(accountEntry.getKey())
							.get(InfinityConstants.accountType);
					account.addProperty(InfinityConstants.accountName, accountName);
					account.addProperty(InfinityConstants.accountType, accountType);
					account.addProperty(InfinityConstants.accountId, accountEntry.getKey());
					account.addProperty("ownerType",
							accountMap.get(entry.getKey()).get(accountEntry.getKey()).get("ownerType"));
					JsonArray features = new JsonArray();
					Set<Entry<String, Map<String, Boolean>>> featureEntries = accountEntry.getValue().entrySet();
					for (Entry<String, Map<String, Boolean>> featureEntry : featureEntries) {
						JsonObject featureJsonObject = actionLimitsDTO.getFeatureInfo().get(featureEntry.getKey());
						JsonObject feature = new JsonObject();
						for (Entry<String, JsonElement> featureJsonEntry : featureJsonObject.entrySet()) {
							feature.add(featureJsonEntry.getKey(), featureJsonEntry.getValue());
						}

						boolean isEnabled = false;
						JsonArray permissions = new JsonArray();
						for (Entry<String, Boolean> actionEntry : featureEntry.getValue().entrySet()) {
							JsonObject permission = new JsonObject();

							JsonObject action = actionLimitsDTO.getActionsInfo().get(actionEntry.getKey());
							for (Entry<String, JsonElement> actionJsonEntry : action.entrySet()) {
								permission.add(actionJsonEntry.getKey(), actionJsonEntry.getValue());
							}
							permission.addProperty(InfinityConstants.id, actionEntry.getKey());
							if (actionEntry.getValue()) {
								isEnabled = true;
							}
							permission.addProperty(InfinityConstants.isEnabled, actionEntry.getValue().toString());
							permissions.add(permission);
						}

						feature.addProperty(InfinityConstants.isEnabled, "" + isEnabled);
						feature.add(InfinityConstants.permissions, permissions);
						features.add(feature);
					}
					account.add(InfinityConstants.featurePermissions, features);
					accounts.add(account);
				}
				jsonObject.add(InfinityConstants.accounts, accounts);
				accountLevelPermissions.add(jsonObject);
			}
			customerJsonObject.add(InfinityConstants.accountLevelPermissions, accountLevelPermissions);

			JsonArray transactionLimitsJsonArray = new JsonArray();
			Set<Entry<String, Map<String, Map<String, Map<String, Map<String, String>>>>>> transactionEntries = transactionLimits
					.entrySet();
			for (Entry<String, Map<String, Map<String, Map<String, Map<String, String>>>>> entry : transactionEntries) {
				JsonObject jsonObject = new JsonObject();

				for (Entry<String, JsonElement> entrySet : contractCoreCustomers.get(entry.getKey()).entrySet()) {
					jsonObject.add(entrySet.getKey(), entrySet.getValue());
					if (InfinityConstants.contractId.equalsIgnoreCase(entrySet.getKey())) {
						for (Entry<String, JsonElement> contractEntrySet : contracts
								.get(entrySet.getValue().getAsString()).entrySet()) {
							if ("name".equalsIgnoreCase(contractEntrySet.getKey()))
								jsonObject.add("contractName", contractEntrySet.getValue());
							else
								jsonObject.add(contractEntrySet.getKey(), contractEntrySet.getValue());
						}
					}
				}

				jsonObject.addProperty(InfinityConstants.companyName, companyMap.get(entry.getKey()));
				jsonObject.addProperty(InfinityConstants.cif, entry.getKey());
				JsonArray accounts = new JsonArray();
				Set<Entry<String, Map<String, Map<String, Map<String, String>>>>> accountEntries = entry.getValue()
						.entrySet();
				FeatureActionLimitsDTO actionLimitsDTO = featureActionDTOs.get(entry.getKey().trim());

				for (Entry<String, Map<String, Map<String, Map<String, String>>>> accountEntry : accountEntries) {
					JsonObject account = new JsonObject();
					if (!accountMap.containsKey(entry.getKey())
							|| !accountMap.get(entry.getKey()).containsKey(accountEntry.getKey())) {
						continue;
					}
					String accountName = accountMap.get(entry.getKey()).get(accountEntry.getKey())
							.get(InfinityConstants.accountName);
					String accountType = accountMap.get(entry.getKey()).get(accountEntry.getKey())
							.get(InfinityConstants.accountType);
					account.addProperty(InfinityConstants.accountName, accountName);
					account.addProperty(InfinityConstants.accountType, accountType);
					account.addProperty(InfinityConstants.accountId, accountEntry.getKey());
					account.addProperty("ownerType",
							accountMap.get(entry.getKey()).get(accountEntry.getKey()).get("ownerType"));
					JsonArray featurePermissions = new JsonArray();
					Set<Entry<String, Map<String, Map<String, String>>>> featureEntries = accountEntry.getValue()
							.entrySet();
					for (Entry<String, Map<String, Map<String, String>>> featureEntry : featureEntries) {
						Set<Entry<String, Map<String, String>>> actionEntries = featureEntry.getValue().entrySet();
						for (Entry<String, Map<String, String>> actionEntry : actionEntries) {
							JsonObject featureJsonObject = actionLimitsDTO.getFeatureInfo().get(featureEntry.getKey());
							JsonObject feature = new JsonObject();
							for (Entry<String, JsonElement> featureJsonEntry : featureJsonObject.entrySet()) {
								feature.add(featureJsonEntry.getKey(), featureJsonEntry.getValue());
							}
							String actionId = actionEntry.getKey();
							feature.addProperty(InfinityConstants.actionId, actionId);
							JsonObject action = actionLimitsDTO.getActionsInfo().get(actionId);
							for (Entry<String, JsonElement> actionJsonEntry : action.entrySet()) {
								feature.add(actionJsonEntry.getKey(), actionJsonEntry.getValue());
							}

							feature.addProperty(InfinityConstants.limitGroupId,
									actionEntry.getValue().get(InfinityConstants.limitGroupId));
							actionEntry.getValue().remove(InfinityConstants.limitGroupId);

							feature.addProperty(InfinityConstants.isEnabled,
									actionEntry.getValue().get(InfinityConstants.isEnabled));
							actionEntry.getValue().remove(InfinityConstants.isEnabled);

							JsonArray limits = new JsonArray();
							Set<Entry<String, String>> limitEntries = actionEntry.getValue().entrySet();
							for (Entry<String, String> limitEntry : limitEntries) {
								JsonObject limitJson = new JsonObject();
								limitJson.addProperty(InfinityConstants.id, limitEntry.getKey());
								limitJson.addProperty(InfinityConstants.value, limitEntry.getValue());
								limits.add(limitJson);
							}

							feature.add(InfinityConstants.limits, limits);
							featurePermissions.add(feature);
						}
					}
					account.add(InfinityConstants.featurePermissions, featurePermissions);
					accounts.add(account);
				}

				jsonObject.add(InfinityConstants.accounts, accounts);
				transactionLimitsJsonArray.add(jsonObject);
			}

			Map<String, Map<String, Map<String, Map<String, Double>>>> limitsMap = new HashMap<String, Map<String, Map<String, Map<String, Double>>>>();

			for (Entry<String, Map<String, Map<String, Map<String, Double>>>> cifEntry : limitGroups.entrySet()) {
				limitsMap.put(cifEntry.getKey(), new HashMap<String, Map<String, Map<String, Double>>>());
				for (Entry<String, Map<String, Map<String, Double>>> accountEntry : cifEntry.getValue().entrySet()) {
					for (Entry<String, Map<String, Double>> limitGroupEntry : accountEntry.getValue().entrySet()) {
						if (!limitsMap.get(cifEntry.getKey()).containsKey(limitGroupEntry.getKey())) {
							limitsMap.get(cifEntry.getKey()).put(limitGroupEntry.getKey(),
									new HashMap<String, Map<String, Double>>());
						}
						for (Entry<String, Double> limitTypeEntry : limitGroupEntry.getValue().entrySet()) {

							if (!limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
									.containsKey(limitTypeEntry.getKey())) {
								limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.put(limitTypeEntry.getKey(), new HashMap<String, Double>());
								limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.get(limitTypeEntry.getKey())
										.put(InfinityConstants.maxValue, limitTypeEntry.getValue());
							} else if (limitTypeEntry.getKey().equals(InfinityConstants.MAX_TRANSACTION_LIMIT)) {
								Double value = limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.get(limitTypeEntry.getKey()).get(InfinityConstants.maxValue);
								limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.get(limitTypeEntry.getKey())
										.put(InfinityConstants.maxValue, Math.max(value, limitTypeEntry.getValue()));
							} else {
								Double value = limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.get(limitTypeEntry.getKey()).get(InfinityConstants.maxValue);
								limitsMap.get(cifEntry.getKey()).get(limitGroupEntry.getKey())
										.get(limitTypeEntry.getKey())
										.put(InfinityConstants.maxValue, value + limitTypeEntry.getValue());
							}
						}
					}
				}
			}

			for (String cif : limitGroups.keySet()) {

				Map<String, Object> input = new HashMap<String, Object>();
				String filter = InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cif
						+ DBPUtilitiesConstants.AND + InfinityConstants.Customer_id + DBPUtilitiesConstants.EQUAL
						+ customerId;
				input.put(DBPUtilitiesConstants.FILTER, filter);
				JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
						URLConstants.CUSTOMER_LIMIT_GROUP_LIMITS_GET);
				if (response.has(DBPDatasetConstants.DATASET_CUSTOMRLIMITGROUPLIMITS)) {
					JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CUSTOMRLIMITGROUPLIMITS);
					if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
						for (JsonElement element : jsonElement.getAsJsonArray()) {
							String limitTypeId = element.getAsJsonObject().get(InfinityConstants.LimitType_id)
									.getAsString();
							String limitGroupId = element.getAsJsonObject().get(InfinityConstants.limitGroupId)
									.getAsString();
							String value = element.getAsJsonObject().get(InfinityConstants.value).getAsString();
							if (!limitsMap.get(cif).containsKey(limitGroupId)) {
								limitsMap.get(cif).put(limitGroupId, new HashMap<String, Map<String, Double>>());
							}

							if (!limitsMap.get(cif).get(limitGroupId).containsKey(limitTypeId)) {
								limitsMap.get(cif).get(limitGroupId).put(limitTypeId, new HashMap<String, Double>());
							}
							limitsMap.get(cif).get(limitGroupId).get(limitTypeId).put(InfinityConstants.value,
									Double.parseDouble(value));
						}

					}
				}
			}

			String locale = "en-US";
			if (headerMap.containsKey("locale") && headerMap.get("locale") != null
					&& StringUtils.isNotBlank(headerMap.get("locale").toString()))
				locale = headerMap.get("locale").toString();

			String filter = InfinityConstants.localeId + DBPUtilitiesConstants.EQUAL + locale;
			Map<String, JsonObject> limitGroupMap = new HashMap<String, JsonObject>();
			Map<String, Object> input = new HashMap<String, Object>();
			input.put(DBPUtilitiesConstants.FILTER, filter);
			JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
					URLConstants.LIMIT_GROUP_DESCRIPTION_GET);
			if (response.has(DBPDatasetConstants.DATASET_LIMIT_GROUP_DESCRIPTION)) {
				JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_LIMIT_GROUP_DESCRIPTION);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					for (JsonElement element : jsonElement.getAsJsonArray()) {
						JsonObject limitGroupJsonObject = element.getAsJsonObject();
						limitGroupMap.put(limitGroupJsonObject.get(InfinityConstants.limitGroupId).getAsString(),
								limitGroupJsonObject);
					}
				}
			}

			for (Entry<String, Map<String, Map<String, Map<String, Double>>>> cifEntry : limitsMap.entrySet()) {

				JsonObject limitJsonObject = new JsonObject();
				boolean matchFound = false;

				for (JsonElement element : transactionLimitsJsonArray) {
					JsonObject cifJsonObject = element.getAsJsonObject();
					if (cifJsonObject.has(InfinityConstants.cif)
							&& !cifJsonObject.get(InfinityConstants.cif).isJsonNull()
							&& cifJsonObject.get(InfinityConstants.cif).getAsString().equals(cifEntry.getKey())) {
						limitJsonObject = cifJsonObject;
						matchFound = true;
					}
				}
				if (!matchFound) {
					limitJsonObject.addProperty(InfinityConstants.companyName, companyMap.get(cifEntry.getKey()));
					limitJsonObject.addProperty(InfinityConstants.cif, cifEntry.getKey());
				}
				JsonArray limitGroupids = new JsonArray();
				for (Entry<String, Map<String, Map<String, Double>>> limitGroupEntry : cifEntry.getValue().entrySet()) {

					JsonObject limitGroup = new JsonObject();
					JsonArray limits = new JsonArray();
					limitGroup.addProperty(InfinityConstants.limitGroupId, limitGroupEntry.getKey());
					if (limitGroupMap.containsKey(limitGroupEntry.getKey())) {
						limitGroup.add(InfinityConstants.limitGroupName,
								limitGroupMap.get(limitGroupEntry.getKey()).get(InfinityConstants.displayName));
						limitGroup.add(InfinityConstants.limitGroupDescription,
								limitGroupMap.get(limitGroupEntry.getKey()).get(InfinityConstants.displayDescription));
					}
					for (Entry<String, Map<String, Double>> limitsEntry : limitGroupEntry.getValue().entrySet()) {
						JsonObject limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, limitsEntry.getKey());
						limit.addProperty(InfinityConstants.maxValue,
								limitsEntry.getValue().containsKey(InfinityConstants.maxValue)
										? limitsEntry.getValue().get(InfinityConstants.maxValue).toString()
										: "0.0");
						limit.addProperty(InfinityConstants.value,
								limitsEntry.getValue().containsKey(InfinityConstants.value)
										? limitsEntry.getValue().get(InfinityConstants.value).toString()
										: "0.0");
						limit.addProperty(InfinityConstants.minValue, "0.0");
						limits.add(limit);
					}
					limitGroup.add(InfinityConstants.limits, limits);

					limitGroupids.add(limitGroup);
				}

				limitJsonObject.add(InfinityConstants.limitGroups, limitGroupids);
				if (!matchFound) {
					transactionLimitsJsonArray.add(limitJsonObject);
				}
			}
			customerJsonObject.add(InfinityConstants.transactionLimits, transactionLimitsJsonArray);
		}
	 
	private void getUserContracts(String customerId, Map<String, Set<String>> customerContracts,
			Map<String, Object> headerMap, Map<String, Boolean> autoSyncAccountsMap, boolean isCustomRoleFlow) {
		JsonArray jsonArray = getUserContracts(customerId, headerMap, isCustomRoleFlow);

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject customerContract = jsonArray.get(i).getAsJsonObject();
			String contractId = customerContract.has(InfinityConstants.contractId)
					&& !customerContract.get(InfinityConstants.contractId).isJsonNull()
							? customerContract.get(InfinityConstants.contractId).getAsString()
							: null;

			String coreCustomerId = customerContract.has(InfinityConstants.coreCustomerId)
					&& !customerContract.get(InfinityConstants.coreCustomerId).isJsonNull()
							? customerContract.get(InfinityConstants.coreCustomerId).getAsString()
							: null;
			Boolean b = false;
			if (customerContract.has(InfinityConstants.autoSyncAccounts)
					&& !customerContract.get(InfinityConstants.autoSyncAccounts).isJsonNull()) {
				if (customerContract.get(InfinityConstants.autoSyncAccounts).getAsString().equals("1")
						|| customerContract.get(InfinityConstants.autoSyncAccounts).getAsString().equals("true"))
					b = true;
				else
					b = false;
			} else
				b = Boolean.parseBoolean(null);
			autoSyncAccountsMap.put(coreCustomerId, b);
			if (!customerContracts.containsKey(contractId)) {
				customerContracts.put(contractId, new HashSet<String>());
			}

			if (customerContracts.containsKey(contractId)
					&& !customerContracts.get(contractId).contains(coreCustomerId)) {
				customerContracts.get(contractId).add(coreCustomerId);
			}
		}
	}
	
    private JsonArray getUserContracts(String customerId, Map<String, Object> headerMap, boolean isCustomRoleFlow) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(customerId)) {
            if (!isCustomRoleFlow) {
                String filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL + customerId;
                map.put(DBPUtilitiesConstants.FILTER, filter);
                JsonObject jsonObject =
                        ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, URLConstants.CONTRACT_CUSTOMERS_GET);

                if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)) {
                    JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS);
                    if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        return jsonArray;
                    }
                }
            } else {
                String filter = InfinityConstants.customRoleId + DBPUtilitiesConstants.EQUAL + customerId;

                Map<String, Object> input = new HashMap<String, Object>();
                input.put(DBPUtilitiesConstants.FILTER, filter);

                JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                        URLConstants.CONTRACT_CUSTOM_ROLE_GET);
                if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACTCUSTOMROLE)) {
                    JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACTCUSTOMROLE);
                    if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                        return jsonElement.getAsJsonArray();
                    }
                }
            }
        }

        return new JsonArray();
    }	
	 
}
