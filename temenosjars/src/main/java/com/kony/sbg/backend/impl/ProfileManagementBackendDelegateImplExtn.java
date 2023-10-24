package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.util.ServiceCallHelper;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.MemberSearchBean;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.CommunicationBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.impl.ProfileManagementBackendDelegateImpl;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.InfinityConstants;
public class ProfileManagementBackendDelegateImplExtn extends ProfileManagementBackendDelegateImpl{
	  LoggerUtil logger = new LoggerUtil(ProfileManagementBackendDelegateImplExtn.class);
    @Override
    public DBXResult searchCustomer(Map<String, String> configurations, MemberSearchBean memberSearchBean,
            Map<String, Object> headerMap) {
        DBXResult dbxResult = new DBXResult();
        JsonObject processedResult = new JsonObject();
        JsonObject searchResults = new JsonObject();
        String IS_Integrated = Boolean.toString(IntegrationTemplateURLFinder.isIntegrated);
        boolean isCustomerSearch = false;
        if (StringUtils.isNotBlank(memberSearchBean.getCustomerId())) {
            memberSearchBean.setMemberId(memberSearchBean.getCustomerId());
            isCustomerSearch = true;
            memberSearchBean.setCustomerId(null);
        }

        if (isCustomerSearch && StringUtils.isNotBlank(memberSearchBean.getMemberId())
                && !IntegrationTemplateURLFinder.isIntegrated
                && !memberPresent(memberSearchBean.getMemberId(), headerMap)) {
            //dbxResult.setError(ErrorCodeEnum.ERR_10335);
            //return dbxResult;
        	//SBG-38
        } else if (!isCustomerSearch && StringUtils.isNotBlank(memberSearchBean.getMemberId())
                && !IntegrationTemplateURLFinder.isIntegrated
                && memberPresent(memberSearchBean.getMemberId(), headerMap)) {
            dbxResult.setError(ErrorCodeEnum.ERR_10335);
            return dbxResult;
        }

        if (memberSearchBean.getSearchType().equalsIgnoreCase(DTOConstants.APPLICANT_SEARCH)) {
            processedResult.addProperty("TotalResultsFound", 0);
            searchResults = searchCustomers(headerMap, memberSearchBean.getSearchType(), memberSearchBean);
        }
        if (memberSearchBean.getSearchType().equalsIgnoreCase(DTOConstants.GROUP_SEARCH)) {
            searchResults = searchCustomers(headerMap, memberSearchBean.getSearchType(), memberSearchBean);
            if (searchResults.has("records1") && searchResults.get("records1").getAsJsonArray().size() > 0) {
                if (searchResults.get("records1").getAsJsonArray().get(0).getAsJsonObject().has("SearchMatchs")) {
                    processedResult.addProperty("TotalResultsFound", searchResults.get("records1").getAsJsonArray()
                            .get(0).getAsJsonObject().get("SearchMatchs").getAsInt());
                } else {
                    processedResult.addProperty("TotalResultsFound", 0);
                }
            }
        }
        if (memberSearchBean.getSearchType().equalsIgnoreCase(DTOConstants.CUSTOMER_SEARCH)) {
            searchResults = searchCustomers(headerMap, memberSearchBean.getSearchType(), memberSearchBean);
            if (searchResults.has("records1") && searchResults.get("records1").getAsJsonArray().size() > 0) {
                if (searchResults.get("records1").getAsJsonArray().get(0).getAsJsonObject().has("SearchMatchs")) {
                    processedResult.addProperty("TotalResultsFound", searchResults.get("records1").getAsJsonArray()
                            .get(0).getAsJsonObject().get("SearchMatchs").getAsInt());
                } else {
                    processedResult.addProperty("TotalResultsFound", "0");
                }
            }
        }

        JsonArray recordsArray = new JsonArray();
        if (searchResults.has("records")) {

            recordsArray = searchResults.get("records").getAsJsonArray();
            processedResult.add("records", recordsArray);

        }

        if ((StringUtils.isNotBlank(IS_Integrated) && IS_Integrated.equalsIgnoreCase("true")
                && (StringUtils.isBlank(memberSearchBean.getMemberId()) || isCustomerSearch))
                && !memberSearchBean.getIsMicroServiceFlow()) {
            processedResult = searchCustomerinT24(recordsArray, configurations, memberSearchBean, headerMap,
                    isCustomerSearch);
            mergeResults(recordsArray, headerMap);
            mergeResultsFromLeadMS(recordsArray, headerMap);
        }

        processedResult.addProperty("Status", "Records returned: " + recordsArray.size());

        processedResult.addProperty("TotalResultsFound", recordsArray.size());

        processedResult.add("records", recordsArray);

        if (recordsArray.size() == 1
                && memberSearchBean.getSearchType().equalsIgnoreCase(DTOConstants.CUSTOMER_SEARCH)
                && !memberSearchBean.getIsMicroServiceFlow()) {

            String customerId = recordsArray.get(0).getAsJsonObject().has("id")
                    ? recordsArray.get(0).getAsJsonObject().get("id").getAsString()
                    : recordsArray.get(0).getAsJsonObject().get(InfinityConstants.primaryCustomerId).getAsString();
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(customerId);
            dbxResult = getBasicInformation(configurations, customerDTO, headerMap, isCustomerSearch);

            if (dbxResult.getResponse() != null) {
                JsonObject jsonObject = (JsonObject) dbxResult.getResponse();

                for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    processedResult.add(entry.getKey(), entry.getValue());
                }
            }

        }

        for (JsonElement recordElement : recordsArray) {
            isAssociated(recordElement, headerMap);
            if (!Boolean.parseBoolean(
                    recordElement.getAsJsonObject().get(InfinityConstants.isProfileExist).getAsString())) {
                recordElement.getAsJsonObject().addProperty(InfinityConstants.isEnrolled, "false");
            }
        }

        dbxResult.setResponse(processedResult);

        if (recordsArray.size() == 0) {
            dbxResult.setError(ErrorCodeEnum.ERR_10335);
        }

        return dbxResult;
	}
    private boolean memberPresent(String memberId, Map<String, Object> headerMap) {
        String filter = "id" + DBPUtilitiesConstants.EQUAL + memberId;

        Map<String, Object> input = new HashMap<String, Object>();
        input.put(DBPUtilitiesConstants.FILTER, filter);
        JsonObject result = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap, URLConstants.MEMBERSHIP_GET);

        if (result.has(DBPDatasetConstants.DATASET_MEMBERSHIP)) {
            JsonArray jsonArray = result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).isJsonArray()
                    ? result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).getAsJsonArray()
                    : new JsonArray();
            return jsonArray.size() > 0;
        }

        return false;
    }
    private JsonObject searchCustomers(Map<String, Object> headerMap, String searchType,
            MemberSearchBean memberSearchBean) {
        Map<String, Object> searchPostParameters = new HashMap<String, Object>();

        searchPostParameters.put("_id", memberSearchBean.getMemberId());
        searchPostParameters.put("_name", memberSearchBean.getCustomerName());
        searchPostParameters.put("_username", memberSearchBean.getCustomerUsername());
        searchPostParameters.put("_SSN", memberSearchBean.getSsn());
        searchPostParameters.put("_phone", memberSearchBean.getCustomerPhone());
        searchPostParameters.put("_email", memberSearchBean.getCustomerEmail());
        searchPostParameters.put("_IsStaffMember", memberSearchBean.getIsStaffMember());
        searchPostParameters.put("_cardorAccountnumber", memberSearchBean.getCardorAccountnumber());
        searchPostParameters.put("_TIN", memberSearchBean.getTin());
        searchPostParameters.put("_group", memberSearchBean.getCustomerGroup());
        searchPostParameters.put("_IDType", memberSearchBean.getCustomerIDType());
        searchPostParameters.put("_IDValue", memberSearchBean.getCustomerIDValue());
        searchPostParameters.put("_companyId", memberSearchBean.getCustomerCompanyId());
        searchPostParameters.put("_requestID", memberSearchBean.getCustomerRequest());
        searchPostParameters.put("_branchIDS", memberSearchBean.getBranchIDS());
        searchPostParameters.put("_productIDS", memberSearchBean.getProductIDS());
        searchPostParameters.put("_cityIDS", memberSearchBean.getCityIDS());
        searchPostParameters.put("_entitlementIDS", memberSearchBean.getEntitlementIDS());
        searchPostParameters.put("_groupIDS", memberSearchBean.getGroupIDS());
        searchPostParameters.put("_customerStatus", memberSearchBean.getCustomerStatus());
        searchPostParameters.put("_before", memberSearchBean.getBeforeDate());
        searchPostParameters.put("_after", memberSearchBean.getAfterDate());
        searchPostParameters.put("_dateOfBirth", memberSearchBean.getDateOfBirth());
        if (HelperMethods.allEmpty(searchPostParameters)) {
            return new JsonObject();
        }
        searchPostParameters.put("_sortVariable", memberSearchBean.getSortVariable());
        searchPostParameters.put("_sortDirection", memberSearchBean.getSortDirection());
        searchPostParameters.put("_searchType", searchType);
        searchPostParameters.put("_pageOffset", String.valueOf(memberSearchBean.getPageOffset()));
        searchPostParameters.put("_pageSize", String.valueOf(memberSearchBean.getPageSize()));

        return ServiceCallHelper.invokeServiceAndGetJson(searchPostParameters, headerMap,
                URLConstants.CUSTOMER_SEARCH_PROC);
    }
    private void mergeResultsFromLeadMS(JsonArray recordsArray, Map<String, Object> headerMap) {
		for (int i = 0; i < recordsArray.size(); i++) {
			JsonObject jsonObject = recordsArray.get(i).getAsJsonObject();
			String id = jsonObject.has(InfinityConstants.id) && !jsonObject.get(InfinityConstants.id).isJsonNull()
					? jsonObject.get(InfinityConstants.id).getAsString()
					: null;
			if (jsonObject.has("CustomerTypeId") && !jsonObject.get("CustomerTypeId").isJsonNull() && jsonObject.get("CustomerTypeId").getAsString().equalsIgnoreCase("TYPE_ID_PROSPECT")) {
				String leadId = getLeadId(id, headerMap);
				Map<String, Object> mapPayload = new HashMap<String, Object>();
				mapPayload.put("entityDefinitionCode", "lead");
				mapPayload.put("trackingCode", leadId);
				String responseStr = "";
				try {
					responseStr = DBPServiceExecutorBuilder.builder()
							.withServiceId(com.temenos.dbx.eum.product.constants.ServiceId.DBP_DATA_STORAGE_APIS)
							.withOperationId(com.temenos.dbx.eum.product.constants.OperationName.GET_ALL_ENTITY_ITEMS)
							.withRequestParameters(mapPayload).build().getResponse();
				} catch (DBPApplicationException e) {
					e.printStackTrace();
				}

				if (StringUtils.isNotBlank(responseStr)) {
					JSONObject searchRes = new JSONObject(responseStr);
					JSONArray entityItems = searchRes.getJSONArray("entityItems");
					JSONObject leadEntityJSON = getEntityDataByName(entityItems, "Lead");
					JSONObject mobileJSON = getEntityDataByName(entityItems, "Address_Mobile_Home");
					JSONObject emailJSON = getEntityDataByName(entityItems, "Address_Email_Home");
					if (leadEntityJSON.has("firstName"))
						jsonObject.addProperty("FirstName", leadEntityJSON.optString("firstName"));

					if (leadEntityJSON.has("lastName"))
						jsonObject.addProperty("LastName", leadEntityJSON.optString("lastName"));

					if (leadEntityJSON.has("dateOfBirth"))
						jsonObject.addProperty("DateOfBirth", leadEntityJSON.optString("dateOfBirth"));

					if (mobileJSON.has("phoneNo"))
						jsonObject.addProperty("PrimaryPhoneNumber",
								mobileJSON.optString("iddPrefixPhone") + "-" + mobileJSON.optString("phoneNo"));

					if (emailJSON.has("electronicAddress"))
						jsonObject.addProperty("PrimaryEmailAddress", emailJSON.optString("electronicAddress"));

				}
				recordsArray.set(i, jsonObject);
			}
		}
	}
    private void mergeResults(JsonArray recordsArray, Map<String, Object> headerMap) {
        for (int i = 0; i < recordsArray.size(); i++) {
            JsonObject jsonObject = recordsArray.get(i).getAsJsonObject();
            String id = jsonObject.has(InfinityConstants.id) && !jsonObject.get(InfinityConstants.id).isJsonNull()
                    ? jsonObject.get(InfinityConstants.id).getAsString()
                    : null;
            for (int j = i + 1; j < recordsArray.size(); j++) {
                JsonObject t24Object = recordsArray.get(j).getAsJsonObject();
                String primaryCustomerId = t24Object.has(InfinityConstants.primaryCustomerId)
                        && !t24Object.get(InfinityConstants.primaryCustomerId).isJsonNull()
                                ? t24Object.get(InfinityConstants.primaryCustomerId).getAsString()
                                : null;
                if (StringUtils.isNotBlank(primaryCustomerId)) {
                    BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
                    backendIdentifierDTO.setCustomer_id(id);
                    backendIdentifierDTO.setBackendId(primaryCustomerId);
                    backendIdentifierDTO
                            .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
                    DBXResult dbxResult = new DBXResult();
                    try {
                        dbxResult =
                                DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                                        .get(backendIdentifierDTO, headerMap);
                    } catch (ApplicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (dbxResult.getResponse() != null) {
                        for (Entry<String, JsonElement> featureJsonEntry : t24Object.entrySet()) {
                            jsonObject.add(featureJsonEntry.getKey(), featureJsonEntry.getValue());
                        }
                        jsonObject.addProperty(InfinityConstants.id, id);
                        recordsArray.remove(j);
                        break;
                    }
                }

            }
        }
    }
    private void isAssociated(JsonElement recordElement, Map<String, Object> headerMap) {

        if (!recordElement.getAsJsonObject().has(InfinityConstants.isProfileExist)) {
            if (recordElement.getAsJsonObject().has(InfinityConstants.id)
                    && !recordElement.getAsJsonObject().get(InfinityConstants.id).isJsonNull()
                    && recordElement.getAsJsonObject().has(InfinityConstants.Username)
                    && !recordElement.getAsJsonObject().get(InfinityConstants.Username).isJsonNull()) {
                if (recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString()
                        .equals(recordElement.getAsJsonObject().get(InfinityConstants.Username).getAsString())) {
                    recordElement.getAsJsonObject().add(InfinityConstants.primaryCustomerId,
                            recordElement.getAsJsonObject().get(InfinityConstants.id));
                    recordElement.getAsJsonObject().addProperty(InfinityConstants.isProfileExist, "false");
                    recordElement.getAsJsonObject().remove(InfinityConstants.id);
                } else {
                    recordElement.getAsJsonObject().addProperty(InfinityConstants.isProfileExist, "true");
                }
            } else {
                recordElement.getAsJsonObject().addProperty(InfinityConstants.isProfileExist, "false");
            }
        }
        String CustomerType_id = "";
        if (!recordElement.getAsJsonObject().has(InfinityConstants.id)
                || recordElement.getAsJsonObject().get(InfinityConstants.id).isJsonNull()) {
            recordElement.getAsJsonObject().addProperty(InfinityConstants.isAssociated, "false");
            return;
        }

        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();

        backendIdentifierDTO.setCustomer_id(recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString());
        if (IntegrationTemplateURLFinder.isIntegrated) {
            backendIdentifierDTO.setIdentifier_name(
                    IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
            backendIdentifierDTO
                    .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
        } else {
            backendIdentifierDTO.setBackendType(DTOConstants.CORE);
        }

        try {
            DBXResult dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                    .get(backendIdentifierDTO, headerMap);
            if (dbxResult.getResponse() != null) {
                BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                recordElement.getAsJsonObject().addProperty(InfinityConstants.primaryCustomerId,
                        identifierDTO.getBackendId());
            } else {
                recordElement.getAsJsonObject().addProperty(InfinityConstants.primaryCustomerId,
                        backendIdentifierDTO.getCustomer_id());
            }
        } catch (ApplicationException e1) {
            // TODO Auto-generated catch block
            logger.error("Error while fetching backend identifier for backend ID "
                    + recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString());
        }

        String customerId = recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString();
        backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setBackendId(customerId);
        if (IntegrationTemplateURLFinder.isIntegrated) {
            backendIdentifierDTO.setIdentifier_name(
                    IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
            backendIdentifierDTO
                    .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
        } else {
            backendIdentifierDTO.setBackendType(DTOConstants.CORE);
        }

        try {
            DBXResult dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                    .get(backendIdentifierDTO, headerMap);
            if (dbxResult.getResponse() != null) {
                backendIdentifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                recordElement.getAsJsonObject().addProperty(InfinityConstants.id,
                        backendIdentifierDTO.getCustomer_id());
                recordElement.getAsJsonObject().addProperty(InfinityConstants.Customer_id,
                        backendIdentifierDTO.getCustomer_id());
                CustomerDTO userDTO = (CustomerDTO) new CustomerDTO().loadDTO(backendIdentifierDTO.getCustomer_id());
                recordElement.getAsJsonObject().addProperty(InfinityConstants.isEnrolled, "" + userDTO.getIsEnrolled());
                recordElement.getAsJsonObject().addProperty(InfinityConstants.CustomerType_id,
                        userDTO.getCustomerType_id());
                customerId = backendIdentifierDTO.getCustomer_id();

                if (!IntegrationTemplateURLFinder.isIntegrated) {
                    CustomerCommunicationDTO customerCommunicationDTO = new CustomerCommunicationDTO();
                    customerCommunicationDTO.setCustomer_id(customerId);
                    CommunicationBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
                            .getBackendDelegate(CommunicationBackendDelegate.class);
                    dbxResult = backendDelegate.getPrimaryMFACommunicationDetails(customerCommunicationDTO, headerMap);
                    if (dbxResult.getResponse() != null) {
                        JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
                        if (jsonObject.has(DBPDatasetConstants.DATASET_CUSTOMERCOMMUNICATION)) {
                            JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CUSTOMERCOMMUNICATION);
                            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                                JsonArray jsonArray = jsonElement.getAsJsonArray();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    jsonObject = jsonArray.get(i).getAsJsonObject();
                                    if (jsonObject.get("Type_id").getAsString()
                                            .equals(HelperMethods.getCommunicationTypes().get("Phone"))) {
                                        recordElement.getAsJsonObject().addProperty(
                                                InfinityConstants.PrimaryPhoneNumber,
                                                jsonObject.has("Value") && !jsonObject.get("Value").isJsonNull()
                                                        ? jsonObject.get("Value").getAsString()
                                                        : null);
                                    } else if (jsonObject.get("Type_id").getAsString()
                                            .equals(HelperMethods.getCommunicationTypes().get("Email"))) {
                                        recordElement.getAsJsonObject().addProperty(
                                                InfinityConstants.PrimaryEmailAddress,
                                                jsonObject.has("Value") && !jsonObject.get("Value").isJsonNull()
                                                        ? jsonObject.get("Value").getAsString()
                                                        : null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ApplicationException e1) {
            // TODO Auto-generated catch block
            logger.error("Error while fetching backend identifier for backend ID "
                    + recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString());
        }

        String filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL + customerId;
        Map<String, Object> input = new HashMap<String, Object>();
        input.put(DBPUtilitiesConstants.FILTER, filter);
        JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                URLConstants.CONTRACT_CUSTOMERS_GET);
        if (response.has(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)) {
            JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS);
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                    filter = InfinityConstants.id + DBPUtilitiesConstants.EQUAL
                            + arrayElement.getAsJsonObject().get(InfinityConstants.contractId).getAsString();
                    input = new HashMap<String, Object>();
                    input.put(DBPUtilitiesConstants.FILTER, filter);
                    JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                            URLConstants.CONTRACT_GET);
                    input.clear();
                    if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACT)) {
                        jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT);
                        if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            jsonObject = jsonArray.get(0).getAsJsonObject();
                            if (StringUtils.isBlank(CustomerType_id) || !CustomerType_id
                                    .contains(jsonObject.get(InfinityConstants.serviceType).getAsString())) {
                                if (StringUtils.isNotBlank(CustomerType_id)) {
                                    CustomerType_id += ",";
                                }
                                CustomerType_id += jsonObject.get(InfinityConstants.serviceType).getAsString();
                            }
                        }
                    }
                }
                recordElement.getAsJsonObject().addProperty(InfinityConstants.CustomerType_id, CustomerType_id);
                recordElement.getAsJsonObject().addProperty(InfinityConstants.CustomerTypeId, CustomerType_id);
                recordElement.getAsJsonObject().addProperty(InfinityConstants.isAssociated, "true");
            } else {
                recordElement.getAsJsonObject().addProperty(InfinityConstants.isAssociated, "false");
                getCustomerType(recordElement.getAsJsonObject(),
                        recordElement.getAsJsonObject().get(InfinityConstants.id).getAsString(), headerMap);
                recordElement.getAsJsonObject().add(InfinityConstants.CustomerTypeId,
                        recordElement.getAsJsonObject().get(InfinityConstants.CustomerType_id));
            }
        }

    }
    private String getLeadId(String id, Map<String, Object> headerMap) {
		BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
		backendIdentifierDTO.setCustomer_id(id);
		backendIdentifierDTO.setBackendType(DTOConstants.LEAD);
		DBXResult dbxResult = new DBXResult();
		try {
			dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
					.get(backendIdentifierDTO, headerMap);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		if (dbxResult.getResponse() != null) {
			return ((BackendIdentifierDTO) dbxResult.getResponse()).getBackendId();
		}
		return "";
	}
    private JSONObject getEntityDataByName(JSONArray entityItems, String entityName) {
		if (StringUtils.isEmpty(entityName))
			return new JSONObject(); // Entity Name would be empty if entity is not created yet

		Optional<Object> metadataEntity = StreamSupport.stream(entityItems.spliterator(), true)
				.filter(item -> ((JSONObject) item).getString("name").equalsIgnoreCase(entityName)).findFirst();

		JSONObject entityJSON;
		if (metadataEntity.isPresent())
			entityJSON = (JSONObject) metadataEntity.get();
		else
			return new JSONObject(); // Entity may not be created yet

		return new JSONObject(entityJSON.getString("entry"));
	}
    private void getCustomerType(JsonObject json, String id, Map<String, Object> headerMap) {

        String CustomerType_id = "";

        if (IntegrationTemplateURLFinder.isIntegrated) {
            String filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL + id;
            Map<String, Object> input = new HashMap<String, Object>();
            input.put(DBPUtilitiesConstants.FILTER, filter);
            JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                    URLConstants.CONTRACT_CUSTOMERS_GET);
            if (response.has(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)) {
                JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS);
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {

                    for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                        filter = InfinityConstants.id + DBPUtilitiesConstants.EQUAL
                                + arrayElement.getAsJsonObject().get(InfinityConstants.contractId).getAsString();
                        input = new HashMap<String, Object>();
                        input.put(DBPUtilitiesConstants.FILTER, filter);
                        JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                                URLConstants.CONTRACT_GET);
                        input.clear();
                        if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACT)) {
                            jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT);
                            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                                JsonArray jsonArray = jsonElement.getAsJsonArray();
                                jsonObject = jsonArray.get(0).getAsJsonObject();
                                if (StringUtils.isBlank(CustomerType_id) || !CustomerType_id
                                        .contains(jsonObject.get(InfinityConstants.serviceType).getAsString())) {
                                    if (StringUtils.isNotBlank(CustomerType_id)) {
                                        CustomerType_id += ",";
                                    }
                                    CustomerType_id += jsonObject.get(InfinityConstants.serviceType).getAsString();
                                }
                            }
                        }
                    }

                }
            }
        } else {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + id;

            Map<String, Object> input = new HashMap<String, Object>();
            input.put(DBPUtilitiesConstants.FILTER, filter);
            JsonObject result = ServiceCallHelper.invokeServiceAndGetJson(input, headerMap,
                    URLConstants.MEMBERSHIP_GET);

            if (result.has(DBPDatasetConstants.DATASET_MEMBERSHIP)) {
                JsonArray jsonArray = result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).isJsonArray()
                        ? result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).getAsJsonArray()
                        : new JsonArray();
                if (jsonArray.size() > 0) {
                    result = jsonArray.get(0).getAsJsonObject();
                    if (result.has(InfinityConstants.isBusinessType)
                            && !result.get(InfinityConstants.isBusinessType).isJsonNull()) {
                        if (Boolean.parseBoolean(result.get(InfinityConstants.isBusinessType).getAsString())
                                || "1".equals(result.get(InfinityConstants.isBusinessType).getAsString())) {
                            CustomerType_id = HelperMethods.getCustomerTypes().get("Business");

                        } else {
                            CustomerType_id = HelperMethods.getCustomerTypes().get("Retail");
                        }
                    }
                }
            }
        }

        if (StringUtils.isBlank(CustomerType_id) && json.has(InfinityConstants.CustomerType_id)
                && !json.get(InfinityConstants.CustomerType_id).isJsonNull()) {
            CustomerType_id = json.get(InfinityConstants.CustomerType_id).getAsString();
        } else if (StringUtils.isBlank(CustomerType_id) && json.has(InfinityConstants.CustomerType_Id)
                && !json.get(InfinityConstants.CustomerType_Id).isJsonNull()) {
            CustomerType_id = json.get(InfinityConstants.CustomerType_Id).getAsString();
        } else if (StringUtils.isBlank(CustomerType_id) && json.has(InfinityConstants.CustomerTypeId)
                && !json.get(InfinityConstants.CustomerTypeId).isJsonNull()) {
            CustomerType_id = json.get(InfinityConstants.CustomerTypeId).getAsString();
        }
        if (CustomerType_id.contains(HelperMethods.getCustomerTypes().get("Business"))) {
            json.addProperty("isBusiness", "true");
        } else {
            json.addProperty("isBusiness", "false");
        }

        json.addProperty("CustomerType_id", CustomerType_id);

    }

}
