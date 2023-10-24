package com.kony.sbg.javaservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.util.SBGUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.dbutil.QueryFormer;
import com.kony.dbputilities.dbutil.SqlQueryEnum;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.config.APIConfigs;
import com.kony.sbg.dto.SbgArrangementsDTO;
import com.kony.sbg.helpers.HelperMethods;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.kony.sbg.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.infinity.api.arrangements.config.ServerConfigurations;
import com.temenos.infinity.api.arrangements.constants.MSCertificateConstants;
import com.temenos.infinity.api.commons.config.InfinityServices;
import com.temenos.infinity.api.commons.invocation.Executor;

public class GetAccounts implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GetAccounts.class);
	private static String AUTH = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGYWJyaWMiLCJleHAiOjE4MTkxNzA1NTUsImlhdCI6MTU4NzU0NzEwNn0.chZWZ4KPduQaATRh3EWKM4pXkk_VpzHnISIkGitb5OAPYYDq740eVdo_aeqyiQbLrzk74JBnMJx7XI4PzrQfW7ZzHeGff4Xkx_7fiKWCuyx0cc_T8f_a2GX9zRibj42ahd1mV7A8neg1HbEAsZS4X2RN_RrLRBf6jduigU2YSIkJhN6wx0XHlzbUryxIZchCKQ74p4q8HOb77XbtToJXfBGRJMwONk1TRObMEbSZJUr488vQlgj6Iq8lCQEY_NMaAI1P-YHGxgD6jLxmkdAYt7ho63B7DhvNCw6kUJjM-zkbJ5sZCPXA-jPE8nbXrLnePvecfej2rqL9LxFJyhaxdA";
	private int ArrangementMaxAttempts = 3;

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		String username = dcRequest.getParameter("userName");
		String did = inputParams.get("deviceID");
		String userId = inputParams.get("userId");
		String customerType = inputParams.get("customerType");

		if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(username)) {
			Result userResult = getUserId(dcRequest, username);
			userId = HelperMethods.getFieldValue(userResult, "id");
			customerType = HelperMethods.getFieldValue(userResult, "CustomerType_id");
			inputParams.put("userId", userId);
			inputParams.put("customerType", customerType);
		}
		if (StringUtils.isNotBlank(did) && StringUtils.isNotBlank(username)) {
			if (!isDeviceRegistered(dcRequest, did, userId)) {
				Dataset ds = new Dataset("Accounts");
				result.addDataset(ds);
				return result;
			}
		}

		if (preProcess(inputParams, dcRequest, result)) {
			result = HelperMethods.callApiSBG(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
					URLConstants.CUSTOMERACCOUNTSVIEW_GET_EXTN);
		}
		if (HelperMethods.hasRecords(result)) {
			result.getAllDatasets().get(0).setId("Accounts");
			postProcess(result, dcRequest);
		}
		if (!HelperMethods.hasRecords(result)) {
			Dataset ds = new Dataset();
			ds.setId("Accounts");
			result.addDataset(ds);
		}
		return result;
	}

	private boolean isDeviceRegistered(DataControllerRequest dcRequest, String deviceId, String userId)
			throws HttpCallException {
		StringBuilder sb = new StringBuilder();
		sb.append("Customer_id").append(DBPUtilitiesConstants.EQUAL).append(userId).append(DBPUtilitiesConstants.AND)
				.append("Device_id").append(DBPUtilitiesConstants.EQUAL).append(deviceId)
				.append(DBPUtilitiesConstants.AND).append("Status_id").append(DBPUtilitiesConstants.EQUAL)
				.append("SID_DEVICE_REGISTERED");
		Result device = HelperMethods.callGetApi(dcRequest, sb.toString(), HelperMethods.getHeaders(dcRequest),
				URLConstants.DEVICEREGISTRATION_GET);
		return HelperMethods.hasRecords(device);
	}

	private void postProcess(Result result, DataControllerRequest dcRequest) throws HttpCallException {
		List<Record> accounts = result.getAllDatasets().get(0).getAllRecords();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap = generateSecurityHeaders(headerMap, dcRequest);

		JSONArray accountGroupCodeArray = getAccountGroupCode(null);
		JSONArray accountStyleCodeArray = getAccountStyleCode(null);

		for (Record account : accounts) {
			updateDateFormat(account);
			account.addParam(new Param("rePaymentFrequency", "Monthly", DBPUtilitiesConstants.STRING_TYPE));
			String accountType = account.getParamValueByName("typeDescription");
			String dueDate = account.getParamValueByName("dueDate");
			account.addParam(new Param("nextPaymentDate", dueDate, DBPUtilitiesConstants.STRING_TYPE));
			String accountId = account.getParamValueByName("Account_id");
			if (StringUtils.isNotBlank(accountType) && accountType.equals("Loan")
					&& StringUtils.isNotBlank(accountId)) {
				updateAccountwithInstallmentCount(account, accountId, dcRequest);
			}
			String creditcardNumber = account.getParamValueByName("creditCardNumber");
			if (StringUtils.isNotBlank(creditcardNumber)) {
				creditcardNumber = getMaskedValue(creditcardNumber);
				account.addParam(new Param("creditCardNumber", creditcardNumber, DBPUtilitiesConstants.STRING_TYPE));
			}

			try {
				ArrangementMaxAttempts = Integer.parseInt(
						SBGCommonUtils.getServerPropertyValue(SbgURLConstants.ARRANGEMENTMAXATTEMPTS, dcRequest));
			} catch (NumberFormatException e) {
				LOG.error("Unable to fetch Arrangements " + e);
			} catch (Exception e) {
				LOG.error("Unable to fetch Arrangements " + e);
			}
			fetchAndAddExtensionDataToAllAccounts(account, dcRequest, headerMap);

			if (StringUtils.isNotEmpty(account.getParamValueByName("extensionData"))) {
				JSONObject extensionData = new JSONObject(account.getParamValueByName("extensionData"));
				String accountGroupCode = SBGUtil.getString(extensionData, "accountGroupCode");
				String accountStyleCode = SBGUtil.getString(extensionData, "accountStyleCode");
				String accountResidencyStatus = SBGUtil.getString(extensionData, "accountResidencyStatus");
				LOG.debug("#### sbgGetList postProcess accountGroupCode:"+accountGroupCode+" /accountStyleCode:"+accountStyleCode+" /accountResidencyStatus:"+accountResidencyStatus);
				appendGroupCodeAndStyleCode(account, accountGroupCode, accountStyleCode,accountResidencyStatus,accountStyleCodeArray,accountGroupCodeArray);
			}
		}
	}

	private void fetchAndAddExtensionDataToAllAccounts(Record account, DataControllerRequest request,
			Map<String, Object> headerMap) {
		String accountId = account.getParamValueByName("arrangementId");
		LOG.debug("&&&&&accountId: " + accountId);
		try {
			fetchAndAddExtensionDataToAllAccountsResource(accountId, request, account, headerMap);
		} catch (Exception e) {
			LOG.error("Unable to fetch Arrangements " + e);
		}
	}

	private void fetchAndAddExtensionDataToAllAccountsResource(String accountId, DataControllerRequest request,
			Record account, Map<String, Object> headerMap) {
		List<SbgArrangementsDTO> accountsDTO = null;
		if (StringUtils.isNotBlank(accountId)) {
			if (StringUtils.isNotBlank(accountId)) {
				accountsDTO = fetchAndAddExtensionDataToAllAccountsBusiness("accountId", accountId, headerMap);
			}
			if (accountsDTO != null && !accountsDTO.isEmpty()) {
				for (SbgArrangementsDTO accountDTO : accountsDTO) {
					String accountNumber = accountDTO.getAccount_id();
					if (StringUtils.isNotBlank(accountNumber)) {
						if (StringUtils.isNotBlank(accountDTO.getextensionData())) {
							LOG.debug("&&&&&extensionData: " + accountDTO.getextensionData());
							account.addParam(new Param("extensionData", accountDTO.getextensionData(),
									DBPUtilitiesConstants.STRING_TYPE));
							JSONObject extensionData = new JSONObject(accountDTO.getextensionData());
							if (extensionData.has("accountName")) {
								account.addParam("accountName", extensionData.getString("accountName"));
							}
						}
					}
				}
			} else {
				LOG.error("Unable to fetch Arrangements");
			}
		} else {
			LOG.error("Unable to fetch Arrangements");
		}
	}

	private List<SbgArrangementsDTO> fetchAndAddExtensionDataToAllAccountsBusiness(String filterKey, String filterValue,
			Map<String, Object> headerMap) {
		List<SbgArrangementsDTO> arrangementsDTO = null;
		arrangementsDTO = fetchAndAddExtensionDataToAllAccountsBackend(filterKey, filterValue, headerMap);
		return arrangementsDTO;
	}

	private List<SbgArrangementsDTO> fetchAndAddExtensionDataToAllAccountsBackend(String filterKey, String filterValue,
			Map<String, Object> headerMap) {
		List<SbgArrangementsDTO> arrangementsDTO = new ArrayList<>();
		Map<String, Object> inputMap = new HashMap<>();
		String ArrangementResponse = null;
		InfinityServices seriveDetails = null;
		try {
			for (int ArrangementMaxAttemptsMade = 0; ArrangementMaxAttemptsMade < ArrangementMaxAttempts; ArrangementMaxAttemptsMade++) {
				Boolean extensionDataFound = false;
				if ("accountId".equals(filterKey)) {
					seriveDetails = APIConfigs.ARRANGEMENTSMICROSERVICESJSON_GETARRANGEMENTSBYACCOUNTID;
				} else {
					break;
				}
				inputMap.put(filterKey, filterValue);
				try {
					ArrangementResponse = Executor.invokePassThroughServiceAndGetString(seriveDetails, inputMap,
							headerMap);
					LOG.debug("&&&&&ArrangementResponse: " + ArrangementResponse);
				} catch (Exception e) {
					LOG.error("Unable to fetch Arrangements " + e);
					continue;
				}
				JSONObject ArrResponse = new JSONObject();
				if (StringUtils.isNotBlank(ArrangementResponse)) {
					ArrResponse = new JSONObject(ArrangementResponse);
				}
				JSONArray arrangements = new JSONArray();
				if (ArrResponse.has("arrangements")) {
					arrangements = ArrResponse.getJSONArray("arrangements");
				} else if (ArrResponse.has("arrangementResponses")) {
					arrangements = ArrResponse.getJSONArray("arrangementResponses");
				}
				for (int i = 0; i < arrangements.length(); i++) {
					SbgArrangementsDTO acDTO = new SbgArrangementsDTO();
					JSONObject arrangement = arrangements.getJSONObject(i);
					if (arrangement.has("extensionData")) {
						if (arrangement.has("arrangementId")) {
							acDTO.setarrangementId(arrangement.getString("arrangementId"));
						}
						if (arrangement.has("linkedReference")) {
							acDTO.setAccount_id(arrangement.getString("linkedReference"));
						}
						acDTO.setextensionData(arrangement.getJSONObject("extensionData").toString());
						arrangementsDTO.add(acDTO);
						LOG.debug("&&&&&ArrangementTry: " + ArrangementMaxAttemptsMade + " : "
								+ arrangement.getJSONObject("extensionData"));
						extensionDataFound = true;
						break;
					}
				}
				if (extensionDataFound) {
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to fetch Arrangements " + e);
		}
		return arrangementsDTO;
	}

	private static Map<String, Object> generateSecurityHeaders(Map<String, Object> headerMap,
			DataControllerRequest dcRequest) {
		try {
			AUTH = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.MSAUTHORIZATION, dcRequest);
		} catch (Exception e) {
			LOG.error("Unable to fetch Arrangements " + e);
		}
		headerMap.put("Authorization", AUTH);
		if (StringUtils.isNotEmpty(ServerConfigurations.AMS_DEPLOYMENT_PLATFORM.getValueIfExists())) {
			if (StringUtils.equalsIgnoreCase(ServerConfigurations.AMS_DEPLOYMENT_PLATFORM.getValueIfExists(),
					MSCertificateConstants.AWS))
				headerMap.put("x-api-key", ServerConfigurations.AMS_AUTHORIZATION_KEY.getValueIfExists());
			else if (StringUtils.equalsIgnoreCase(ServerConfigurations.AMS_DEPLOYMENT_PLATFORM.getValueIfExists(),
					MSCertificateConstants.AZURE))
				headerMap.put("x-functions-key", ServerConfigurations.AMS_AUTHORIZATION_KEY.getValueIfExists());
		}
		headerMap.put("roleId", ServerConfigurations.AMS_ROLE_ID.getValueIfExists());
		return headerMap;

	}

	private String getMaskedValue(String creditcardNumber) {
		String lastFourDigits;
		if (StringUtils.isNotBlank(creditcardNumber)) {
			if (creditcardNumber.length() > 4) {
				lastFourDigits = creditcardNumber.substring(creditcardNumber.length() - 4);
				creditcardNumber = "XXXX" + lastFourDigits;
			} else {
				creditcardNumber = "XXXX" + creditcardNumber;
			}
		}

		return creditcardNumber;
	}

	public void updateAccountwithInstallmentCount(Record account, String accountId, DataControllerRequest dcRequest)
			throws HttpCallException {
		Result installmentsCountResult = new Result();
		Map<String, String> countInputParams = new HashMap<String, String>();
		countInputParams.put("transactions_query", getInstallmentsCountQuery(accountId, dcRequest));
		installmentsCountResult = HelperMethods.callApi(dcRequest, countInputParams,
				HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNT_TRANSACTION_PROC);
		if (installmentsCountResult.getAllDatasets().size() > 0) {
			List<Record> records = installmentsCountResult.getAllDatasets().get(0).getAllRecords();
			for (Record record : records) {
				if (record.getParamValueByName("InstallmentType").equalsIgnoreCase(DBPUtilitiesConstants.FUTURE)) {
					account.addParam(new Param("FutureInstallmentsCount", record.getParamValueByName("count")));
				}
				if (record.getParamValueByName("InstallmentType").equalsIgnoreCase(DBPUtilitiesConstants.DUE)) {
					account.addParam(new Param("OverDueInstallmentsCount", record.getParamValueByName("count")));
				}
				if (record.getParamValueByName("InstallmentType").equalsIgnoreCase(DBPUtilitiesConstants.PAID)) {
					account.addParam(new Param("PaidInstallmentsCount", record.getParamValueByName("count")));
				}
			}
		}
	}

	public String getInstallmentsCountQuery(String accountId, DataControllerRequest dcRequest) {
		String jdbcUrl = QueryFormer.getDBType(dcRequest);
		return SqlQueryEnum.valueOf(jdbcUrl + "_GetInstallmentsCountQuery").getQuery()
				.replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", accountId);

	}

	private void updateDateFormat(Record account) {
		try {
			String scheduledDate = HelperMethods.getFieldValue(account, "dueDate");
			if (StringUtils.isNotBlank(scheduledDate)) {
				account.addParam(new Param("dueDate",
						HelperMethods.convertDateFormat(scheduledDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
			}
			String date = HelperMethods.getFieldValue(account, "closingDate");
			if (StringUtils.isNotBlank(date)) {
				account.addParam(new Param("closingDate",
						HelperMethods.convertDateFormat(date, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
			}
			date = HelperMethods.getFieldValue(account, "openingDate");
			if (StringUtils.isNotBlank(date)) {
				account.addParam(new Param("openingDate",
						HelperMethods.convertDateFormat(date, "yyyy-MM-dd'T'HH:mm:ss"), "String"));
			}
			date = HelperMethods.getFieldValue(account, "lastPaymentDate");
			if (StringUtils.isNotBlank(date)) {
				account.addParam(new Param("lastPaymentDate",
						HelperMethods.convertDateFormat(date, "yyyy-MM-dd'T'HH:mm:ss"), "String"));
			}
		} catch (Exception e) {
		}
	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result)
			throws HttpCallException {
		boolean status = true;
		String accountId = inputParams.get("accountID");
		String deviceId = inputParams.get("deviceID");
		String phone = inputParams.get("phone");
		String userId = inputParams.get("userId");
		String transferType = inputParams.get("transferType") != null ? inputParams.get("transferType").toString() : null;
		String filter = "";
		if (StringUtils.isNotBlank(deviceId)) {
			filter = DBPUtilitiesConstants.UA_USR_ID + DBPUtilitiesConstants.EQUAL + userId;
		} else if (StringUtils.isBlank(accountId)) {
			filter = DBPUtilitiesConstants.UA_USR_ID + DBPUtilitiesConstants.EQUAL + userId;
			if (StringUtils.isNotBlank(phone)) {
				filter = filter + DBPUtilitiesConstants.AND + "phone" + DBPUtilitiesConstants.EQUAL + phone;
			}
			if (!HelperMethods.getCustomerTypes().get("Customer").equals(inputParams.get("customerType"))) {
				filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + userId;
			}
		} else {
			if (StringUtils.isNotBlank(userId)) {
				filter = DBPUtilitiesConstants.UA_USR_ID + DBPUtilitiesConstants.EQUAL + userId;
				if (!HelperMethods.getCustomerTypes().get("Customer").equals(inputParams.get("customerType"))) {
					filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + userId;
				}
				filter += DBPUtilitiesConstants.AND;
			}

			filter += "Acconut_id" + DBPUtilitiesConstants.EQUAL + accountId;
		}
		ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApplicationBusinessDelegate.class);

		String baseCurrency = application.getBaseCurrencyFromCache();

		if(StringUtils.isNotBlank(transferType) && StringUtils.isNotBlank(baseCurrency)){
			if("domestic".equalsIgnoreCase(transferType)){
				filter += DBPUtilitiesConstants.AND + "currencyCode" + DBPUtilitiesConstants.EQUAL + baseCurrency;
			}			
		}
		// inputParams.clear();
		inputParams.put(DBPUtilitiesConstants.FILTER, filter);
		inputParams.put(DBPUtilitiesConstants.ORDERBY, "accountPreference asc");

		return status;
	}

	private Result getUserId(DataControllerRequest dcRequest, String userName) throws HttpCallException {
		String filter = "UserName" + DBPUtilitiesConstants.EQUAL + userName;
		Result user = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
				URLConstants.CUSTOMERVERIFY_GET);
		return user;
	}

	private Record appendGroupCodeAndStyleCode(Record account, String accountGroupCode, String accountStyleCode, String accountResidencyStatus, JSONArray accountStyleCodeArray, JSONArray accountGroupCodeArray) {

		if (StringUtils.isNotBlank(accountGroupCode)) {
			account.addParam(new Param("accountGroupCodes", generateCodesArray(accountGroupCode,accountGroupCodeArray), DBPUtilitiesConstants.STRING_TYPE));
			account.addParam(new Param("accountGroupCode", accountGroupCode, DBPUtilitiesConstants.STRING_TYPE));
		}

		if (StringUtils.isNotBlank(accountStyleCode)) {
			account.addParam(new Param("accountStyleCodes", generateCodesArray( accountStyleCode,accountStyleCodeArray), DBPUtilitiesConstants.STRING_TYPE));
			account.addParam(new Param("accountStyleCode", accountStyleCode, DBPUtilitiesConstants.STRING_TYPE));
		}

		if (StringUtils.isNotBlank(accountResidencyStatus)) {
			account.addParam(new Param("accountResidencyStatus", accountResidencyStatus, DBPUtilitiesConstants.STRING_TYPE));
		}
		return account;
	}

	private String generateCodesArray(String inputCode, JSONArray codeJsonArray) {
		JSONArray constructedJsonArray = new JSONArray();
		try {
			for (Object codeObj : codeJsonArray) {
				JSONObject codeJsonObj = (JSONObject) codeObj;
				if (codeJsonObj.get("code").toString().equalsIgnoreCase(inputCode)) {
					constructedJsonArray.put(codeObj);
				}
			}
		} catch (Exception e) {
			LOG.debug("Enter to Exception of generateCodesArray ", e);
			return "";
		}
		return constructedJsonArray.toString();
	}

	private JSONArray getAccountGroupCode(String accountGroupCode) {
		LOG.debug("### Starting ---> GetAccount::getAccountGroupCode()");
		String serviceName = SbgURLConstants.ACCOUNT_SERVICE;
		String operationName = SbgURLConstants.ACCOUNT_GROUPCODE_SERVICES;
		String accountGroupCodeResponse = null;
		try {
			LOG.debug("accountGroupCode of getAccountGroupCode :- " + accountGroupCode);
			HashMap<String, Object> requestHeaders = new HashMap<>();
			Map<String, Object> input = new HashMap<>();
			//input.put("code", accountGroupCode);

			accountGroupCodeResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(input)
					.withRequestHeaders(requestHeaders).build().getResponse();

			LOG.debug("#### Response of accountGroupCodeResponse:- " + accountGroupCodeResponse);

			if (StringUtils.isNotEmpty(accountGroupCodeResponse)) {
				JSONObject accountGroupCodeJson = new JSONObject(accountGroupCodeResponse);
				if (accountGroupCodeJson.has("AccountGroupCodes")) {
					JSONArray accountGroupCodesObj = accountGroupCodeJson.getJSONArray("AccountGroupCodes");
					LOG.debug("#### Response of accountGroupCodesObj:- " + accountGroupCodesObj);
					return accountGroupCodesObj;
				}
			}

			return new JSONArray();

		} catch (Exception e) {
			LOG.debug("Enter to Exception of getAccountGroupCode ", e);
			return new JSONArray();
		}

	}
	private JSONArray getAccountStyleCode(String accountStyleCode) {
		LOG.debug("### Starting ---> GetAccount::getAccountStyleCode()");
		String serviceName = SbgURLConstants.ACCOUNT_SERVICE;
		String operationName = SbgURLConstants.ACCOUNT_STYLECODE_SERVICES;
		String accountStyleCodeResponse = null;
		try {
			LOG.debug("accountStyleCode of getAccountStyleCode :- " + accountStyleCode);
			HashMap<String, Object> requestHeaders = new HashMap<>();
			Map<String, Object> input = new HashMap<>();
			//input.put("code", accountStyleCode);

			accountStyleCodeResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(input)
					.withRequestHeaders(requestHeaders).build().getResponse();

			LOG.debug("#### Response of accountStyleCodeResponse:- " + accountStyleCodeResponse);

			JSONArray accountStylesObj  = new JSONObject(accountStyleCodeResponse).getJSONArray("AccountStyles");
			LOG.debug("#### Response of accountGroupCodesObj:- " + accountStylesObj);

			return accountStylesObj;
		} catch (Exception e) {
			LOG.debug("Enter to Exception of getAccountStyleCode ", e);
			return null;
		}

	}

}