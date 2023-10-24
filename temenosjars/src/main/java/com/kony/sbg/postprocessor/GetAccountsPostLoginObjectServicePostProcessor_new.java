package com.kony.sbg.postprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.dbx.product.utils.InfinityConstants;
import com.temenos.infinity.api.arrangements.constants.ObjectServicesConstants;
import com.temenos.infinity.api.arrangements.utils.ObjectServiceHelperMethods;

public class GetAccountsPostLoginObjectServicePostProcessor_new
		implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

	private static final Logger logger = LogManager.getLogger(GetAccountsPostLoginObjectServicePostProcessor_new.class);

	private static final int CACHE_IN_SECONDS = (1 * 30 * 60); // 30 mins

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		JsonObject responsePayloadJson = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
		logger.debug("&&&&&responsePayloadJson: " + responsePayloadJson);
		JsonObject requestPayloadJson = fabricRequestManager.getPayloadHandler().getPayloadAsJson() != null
				? fabricRequestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject()
				: new JsonObject();
		JsonObject cacheJson = new JsonObject();
		JsonArray accountsCacheArray = new JsonArray();
		Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(fabricRequestManager);
		String loginUserId = null;
		if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
			loginUserId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
		} else {
			loginUserId = requestPayloadJson.has(InfinityConstants.id)
					&& !requestPayloadJson.get(InfinityConstants.id).isJsonArray()
							? requestPayloadJson.get(InfinityConstants.id).getAsString()
							: null;
		}
		logger.debug("&&&&&loginUserIdSave: " + loginUserId);
		if (StringUtils.isNotBlank(loginUserId)) {
			addAccountLevelPermissionsandUpdateCacheArray(loginUserId, responsePayloadJson, fabricRequestManager,
					accountsCacheArray);
			cacheJson.add("Accounts", accountsCacheArray);
			logger.debug("&&&&&finalcacheJson: " + cacheJson);
			if (JSONUtil.isJsonNotNull(cacheJson)) {
				MemoryManager.saveIntoCache(DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + loginUserId,
						cacheJson.toString(), CACHE_IN_SECONDS);
			}
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(responsePayloadJson);
		}
		try {
			execute(fabricRequestManager, fabricResponseManager);
		} catch (Exception e) {
			logger.error("Exception while caching accounts in session", e);
		}
		//getExtensionFromCache(fabricRequestManager);//to test extension from cache, comment if in release
		return true;
	}

	private void getExtensionFromCache(FabricRequestManager fabricRequestManager) {
		Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(fabricRequestManager);
		JsonObject requestPayloadJson = fabricRequestManager.getPayloadHandler().getPayloadAsJson() != null
				? fabricRequestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject()
				: new JsonObject();
		String loginUserId = null;
		if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
			loginUserId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
		} else {
			loginUserId = requestPayloadJson.has(InfinityConstants.id)
					&& !requestPayloadJson.get(InfinityConstants.id).isJsonArray()
							? requestPayloadJson.get(InfinityConstants.id).getAsString()
							: null;
		}
		logger.debug("&&&&&loginUserIdFetch: " + loginUserId);
		String userAccountDetails = (String) MemoryManager
				.getFromCache(DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + loginUserId);
		JsonArray jsonArray = new JsonParser().parse(userAccountDetails).getAsJsonObject()
				.get(DBPUtilitiesConstants.ACCOUNTS).getAsJsonArray();
		if (null == jsonArray || jsonArray.size() == 0) {
			logger.error("&&&&&jsonArraynull");
		}
		for (JsonElement element : jsonArray) {
			JsonObject object = element.getAsJsonObject();
			if (object.has("Account_id")) {
				String accountId = object.get("Account_id").getAsString();
				logger.debug("&&&&&accountIdFromCache: " + accountId);
			}
			if (object.has("extensionData")) {
				String extensionData = object.get("extensionData").getAsString();
				logger.debug("&&&&&extensionDataCache: " + extensionData);
			}
		}
	}

	private void addAccountLevelPermissionsandUpdateCacheArray(String customerId, JsonObject responsePayloadJson,
			FabricRequestManager fabricRequestManager, JsonArray accountsCacheArray) throws Exception {
		Map<String, Set<String>> accountLevelActions = new HashMap<>();
		Set<String> coreCustomers = getCoreCustomersList(responsePayloadJson, customerId, fabricRequestManager);
		Map<String, Map<String, String>> accountsDetails = getCoreCustomerAccountsDetails(coreCustomers, customerId,
				fabricRequestManager);
		Set<String> usedCoreCustomers = new HashSet<>();

		Map<String, String> inputParams = new HashMap<>();
		inputParams.put("_userId", customerId);

		for (String coreCustomerId : coreCustomers) {

			if (!usedCoreCustomers.contains(coreCustomerId)) {
				inputParams.put("_coreCustomerId", coreCustomerId);
				JsonObject resultObject = com.kony.dbputilities.util.HelperMethods.callApiJson(fabricRequestManager,
						inputParams, com.kony.dbputilities.util.HelperMethods.getHeaders(fabricRequestManager),
						URLConstants.USER_ACCOUNTACTIONS_GET_PROC);

				if (JSONUtil.isJsonNotNull(resultObject)
						&& JSONUtil.hasKey(resultObject, DBPDatasetConstants.DATASET_RECORDS)
						&& resultObject.get(DBPDatasetConstants.DATASET_RECORDS).isJsonArray()) {
					usedCoreCustomers.add(coreCustomerId);
					JsonArray actionsArray = JSONUtil.getJsonArrary(resultObject, DBPDatasetConstants.DATASET_RECORDS);
					for (JsonElement element : actionsArray) {
						JsonObject actionRecord = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
						String accountId = actionRecord.has("Account_id") ? actionRecord.get("Account_id").getAsString()
								: "";
						String userId = actionRecord.has("Customer_id") ? actionRecord.get("Customer_id").getAsString()
								: "";
						String actionId = actionRecord.has("Action_id") ? actionRecord.get("Action_id").getAsString()
								: "";

						if (customerId.equalsIgnoreCase(userId) && StringUtils.isNotBlank(accountId)
								&& StringUtils.isNotBlank(actionId) && StringUtils.isNotBlank(userId)) {
							if (accountLevelActions.containsKey(accountId)) {
								accountLevelActions.get(accountId).add(actionId);
							} else {
								Set<String> actions = new HashSet<>();
								actions.add(actionId);
								accountLevelActions.put(accountId, actions);
							}

						}
					}

				}
			}
		}

		addPermissionsToAccountRecords(responsePayloadJson, accountLevelActions, accountsDetails, accountsCacheArray);

	}

	private Map<String, Map<String, String>> getCoreCustomerAccountsDetails(Set<String> coreCustomers,
			String customerId, FabricRequestManager fabricRequestManager) throws HttpCallException {

		Map<String, Map<String, String>> accountDetailsMap = new HashMap<>();
		if (!coreCustomers.isEmpty()) {

			StringBuilder coreCustomersListCSV = new StringBuilder();
			Map<String, Object> input = new HashMap<String, Object>();

			for (String corecustomerId : coreCustomers) {
				if (StringUtils.isBlank(coreCustomersListCSV)) {
					coreCustomersListCSV.append(corecustomerId);
				} else {
					coreCustomersListCSV.append(DBPUtilitiesConstants.COMMA_SEPERATOR).append(corecustomerId);
				}

			}
			input.put("_coreCustomerIdList", coreCustomersListCSV.toString());
			input.put("_customerId", customerId);

			JsonObject response = com.kony.dbputilities.util.HelperMethods.callApiJson(fabricRequestManager, input,
					com.kony.dbputilities.util.HelperMethods.getHeaders(fabricRequestManager),
					URLConstants.CORECUSTOMER_ACCOUNTS_DETAILS_GET_PROC);

			if (JSONUtil.isJsonNotNull(response) && JSONUtil.hasKey(response, DBPDatasetConstants.DATASET_RECORDS)
					&& response.get(DBPDatasetConstants.DATASET_RECORDS).isJsonArray()) {

				JsonArray accountDetails = JSONUtil.getJsonArrary(response, DBPDatasetConstants.DATASET_RECORDS);
				for (JsonElement element : accountDetails) {
					JsonObject accountRecord = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
					String accountId = JSONUtil.getString(accountRecord, InfinityConstants.accountId);
					String coreCustomerId = JSONUtil.getString(accountRecord, InfinityConstants.coreCustomerId);
					String coreCustomerName = JSONUtil.getString(accountRecord, InfinityConstants.coreCustomerName);
					String isBusinessAccount = JSONUtil.getString(accountRecord, InfinityConstants.isBusinessAccount);
					String eStatementEnable = JSONUtil.getString(accountRecord, InfinityConstants.eStatementEnable);
					String favouriteStatus = JSONUtil.getString(accountRecord, InfinityConstants.favouriteStatus);
					String email = JSONUtil.getString(accountRecord, InfinityConstants.email);
					String nickName = JSONUtil.getString(accountRecord, "nickName");

					Map<String, String> detailsMap = new HashMap<String, String>();
					detailsMap.put(InfinityConstants.coreCustomerId, coreCustomerId);
					detailsMap.put(InfinityConstants.coreCustomerName, coreCustomerName);
					detailsMap.put(InfinityConstants.isBusinessAccount, isBusinessAccount);
					detailsMap.put(InfinityConstants.eStatementEnable, eStatementEnable);
					detailsMap.put(InfinityConstants.favouriteStatus, favouriteStatus);
					detailsMap.put(InfinityConstants.email, email);
					if (!nickName.isEmpty()) {
						detailsMap.put("nickName", nickName);
					}
					accountDetailsMap.put(accountId, detailsMap);

				}
			}
		}
		return accountDetailsMap;
	}

	private void addPermissionsToAccountRecords(JsonObject accountsJson, Map<String, Set<String>> accountLevelActions,
			Map<String, Map<String, String>> accountsDetails, JsonArray accountsCacheArray) {
		JsonArray accountsArray = JSONUtil.hasKey(accountsJson, "Accounts")
				? accountsJson.get("Accounts").getAsJsonArray()
				: new JsonArray();
		for (JsonElement accountObject : accountsArray) {
			JsonObject account = accountObject.isJsonObject() ? accountObject.getAsJsonObject() : new JsonObject();
			String accountId = account.has("Account_id") ? account.get("Account_id").getAsString()
					: account.get("account_id").getAsString();

			if (StringUtils.isNotBlank(accountId)) {

				account.addProperty("accountID", accountId);
				account.addProperty("Account_id", accountId);
				account.addProperty("account_id", accountId);

				if (!account.has("nickName") || account.get("nickName").isJsonNull()) {
					account.add("nickName", account.get("displayName"));
				}

				if (accountLevelActions.containsKey(accountId)) {
					String actionsString = convertHasetToJsonArrayString(accountLevelActions.get(accountId));
					account.addProperty("actions", actionsString);
					String extensionData = "";
					if (account.has("extensionData")) {
						extensionData = account.get("extensionData").getAsString();
					}
					updateAccountsCache(accountsCacheArray, accountId, actionsString, extensionData);
				} else if (!JSONUtil.hasKey(account, "actions")) {
					account.addProperty("actions", "[]");
				}

				if (account.has("creditCardNumber")
						&& StringUtils.isNotBlank(account.get("creditCardNumber").getAsString())) {
					String creditcardNumber = getMaskedValue(account.get("creditCardNumber").getAsString());
					account.addProperty("creditCardNumber", creditcardNumber);
				}

				if (accountsDetails != null && accountsDetails.containsKey(accountId)) {
					Map<String, String> details = accountsDetails.get(accountId);

					String membershipId = details.get(InfinityConstants.coreCustomerId);
					String membershipName = details.get(InfinityConstants.coreCustomerName);
					String isBusinessAccount = details.get(InfinityConstants.isBusinessAccount);
					String favouriteStatus = details.get(InfinityConstants.favouriteStatus);
					String email = details.get(InfinityConstants.email);
					String eStatementEnable = details.get(InfinityConstants.eStatementEnable);
					String nickName = details.get("nickName");

					String eStatementFromIntegration = account.has(InfinityConstants.eStatementEnable)
							&& StringUtils.isNotBlank(account.get(InfinityConstants.eStatementEnable).getAsString())
									? account.get(InfinityConstants.eStatementEnable).getAsString()
									: "";

					if (StringUtils.isNotBlank(membershipId)) {
						account.addProperty("Membership_id", membershipId);
						account.addProperty(InfinityConstants.coreCustomerId, membershipId);
					}
					if (StringUtils.isNotBlank(membershipName)) {
						account.addProperty("MembershipName", membershipName);
						account.addProperty(InfinityConstants.coreCustomerName, membershipName);
					}
					if (StringUtils.isNotBlank(isBusinessAccount)) {
						account.addProperty(InfinityConstants.isBusinessAccount, isBusinessAccount);
					}
					if (StringUtils.isNotBlank(favouriteStatus)) {
						account.addProperty(InfinityConstants.favouriteStatus, favouriteStatus);
					}
					if (StringUtils.isNotBlank(nickName)) {
						account.addProperty("nickName", nickName);
					}

					if (StringUtils.isNotBlank(eStatementFromIntegration)) {
						if (StringUtils.isNotBlank(email) && "true".equalsIgnoreCase(eStatementFromIntegration)) {
							account.addProperty(InfinityConstants.email, email);
							account.addProperty(InfinityConstants.eStatementEnable, eStatementFromIntegration);
						} else {
							if (account.has(InfinityConstants.email)) {
								account.remove(InfinityConstants.email);
							}
							account.addProperty(InfinityConstants.eStatementEnable,
									DBPUtilitiesConstants.BOOLEAN_STRING_FALSE);
						}
					} else if (StringUtils.isNotBlank(email) && "true".equalsIgnoreCase(eStatementEnable)) {
						account.addProperty(InfinityConstants.email, email);
						account.addProperty(InfinityConstants.eStatementEnable, eStatementEnable);
					} else {
						if (account.has(InfinityConstants.email)) {
							account.remove(InfinityConstants.email);
						}
						account.addProperty(InfinityConstants.eStatementEnable,
								DBPUtilitiesConstants.BOOLEAN_STRING_FALSE);

					}

				}

			}

		}

	}

	private void updateAccountsCache(JsonArray accountsCacheArray, String accountId, String actionsString,
			String extensionData) {
		if (StringUtils.isNotBlank(accountId) && null != accountsCacheArray) {
			JsonObject object = new JsonObject();
			object.addProperty("Account_id", accountId);
			object.addProperty("accountID", accountId);
			object.addProperty("actions", actionsString);
			object.addProperty("extensionData", extensionData);
			accountsCacheArray.add(object);
		}

	}

	private Set<String> getCoreCustomersList(JsonObject accountsJson, String customerId, FabricRequestManager request)
			throws HttpCallException {
		Set<String> coreCustomers = new HashSet<>();
		JsonArray accountsArray = JSONUtil.hasKey(accountsJson, "Accounts")
				? accountsJson.get("Accounts").getAsJsonArray()
				: new JsonArray();
		for (JsonElement accountObject : accountsArray) {
			JsonObject account = accountObject.isJsonObject() ? accountObject.getAsJsonObject() : new JsonObject();
			if (JSONUtil.hasKey(account, "Account_id") && JSONUtil.hasKey(account, "Membership_id")) {
				String coreCustomerId = account.get("Membership_id").getAsString();

				if (!coreCustomers.contains(coreCustomerId)) {
					coreCustomers.add(coreCustomerId);
				}
			}
		}

		if (coreCustomers.isEmpty()) {

			String filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL + customerId;
			Map<String, Object> input = new HashMap<String, Object>();
			input.put(DBPUtilitiesConstants.FILTER, filter);

			JsonObject response = com.kony.dbputilities.util.HelperMethods.callApiJson(request, input,
					com.kony.dbputilities.util.HelperMethods.getHeaders(request), URLConstants.CONTRACT_CUSTOMERS_GET);

			if (JSONUtil.isJsonNotNull(response)
					&& JSONUtil.hasKey(response, DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)
					&& response.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS).isJsonArray()) {

				JsonArray customers = JSONUtil.getJsonArrary(response, DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS);
				for (JsonElement element : customers) {
					JsonObject actionRecord = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
					String coreCustomerId = JSONUtil.getString(actionRecord, InfinityConstants.coreCustomerId);
					if (StringUtils.isNotBlank(coreCustomerId)) {
						coreCustomers.add(coreCustomerId);
					}
				}
			}
		}

		return coreCustomers;

	}

	private String convertHasetToJsonArrayString(Set<String> addedActions) {
		JsonArray actionsList = new JsonArray();
		for (String action : addedActions) {
			actionsList.add(action);
		}
		return actionsList.toString();
	}

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

		try {

			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
			JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			JsonObject customParams = new JsonObject();

			String opstatus = "";
			String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
					requestManager);
			String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
			String eventType = PARAM_ACCOUNT_ACTION;
			String eventSubType = PARAM_INTERNAL_ACCOUNTS_FETCH;
			String producer = "Accounts/getAccountsPostLogin";
			String statusId = PARAM_SID_EVENT_FAILURE;

			if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
				opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
			}

			if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
				statusId = PARAM_SID_EVENT_SUCCESS;
			}

			if (enableEvents != null && enableEvents.equals(PARAM_TRUE) && responsePayload.has("Accounts")) {
				JsonArray accountsArray = responsePayload.getAsJsonArray("Accounts");
				Integer noOfAccounts = accountsArray.size();
				responsePayload.addProperty("noOfAccounts", noOfAccounts.toString());
				responsePayload.remove("Accounts");
				try {

					ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responsePayload,
							eventType, eventSubType, producer, statusId, null, customerid, customParams));
				} catch (Exception e2) {
					logger.error("Exception Occured while invoking objectServiceHelperMethods", e2);
				}
			}

		} catch (Exception ex) {
			logger.error("exception occured in GetAccountsPostLoginObjectServicePostProcessor_new ", ex);
		}

	}

	private String getMaskedValue(String creditcardNumber) {
		String lastFourDigits;
		if (StringUtils.isNotBlank(creditcardNumber)) {
			if (creditcardNumber.length() > 4) {
				lastFourDigits = creditcardNumber.substring(creditcardNumber.length() - 4);
				creditcardNumber = "XXXXX" + lastFourDigits;
			} else {
				creditcardNumber = "XXXXX" + creditcardNumber;
			}
		}

		return creditcardNumber;
	}

}
