package com.kony.sbg.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.backend.api.SbgInterBankFundTransferBackendDelegateExtn;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.registry.AppRegistryException;

public class RefDataCacheHelperInterBank {

	private static final Logger logger = LogManager.getLogger(RefDataCacheHelperInterBank.class);
	private static int EXPIRYINSECS = 24 * 60 * 60;

	public static String getRefDataByKey(DataControllerRequest request, String country, String currency) {

		try {
			EXPIRYINSECS = Integer.parseInt(SBGCommonUtils.getServerPropertyValue("REFDATA_CACHE_EXPIRY", request));
		} catch (Exception e) {
		}
		

		String REFDATACACHE = "DOMREFDATA-" + country + "-" + currency;
		logger.debug("RefDataCacheHelperInterBank.getRefDataByKey() ---> START ===> ctrycurr: " + REFDATACACHE);

		String retval = null;

		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			retval = (String) resultCache.retrieveFromCache(REFDATACACHE);

		} catch (AppRegistryException e) {
			logger.error("RefDataCacheHelperInterBank.getRefDataByKey ---> 1. EXCEPTION: " + e.getMessage());
		}

		if (SBGCommonUtils.isStringEmpty(retval)) {
			// either the cache is empty or cache has invalid reference data
			// invoke the integration service to fetch refdata from DB
			Map<String, String> data4mDB = getRefDataFromDB(request, REFDATACACHE);
			logger.debug("RefDataCacheHelperInterBank.getRefDataByKey() ---> data4mDB: " + data4mDB);

			if (data4mDB == null) {
				// no data is found in DB for given set of key (country,currency)
				// making the backend call to fetch data
				String latestRefData = getRefData4mSBG(request, country, currency);
				logger.debug("RefDataCacheHelperInterBank.getRefDataByKey() ---> latestRefData: " + latestRefData);

				if (isValidRefData(latestRefData)) {
					// if the response is valid then updating the latest response in db and cache
					logger.debug(
							"RefDataCacheHelperInterBank.getRefDataByKey() ---> inserting data in DB and updating cache");
					retval = latestRefData;
					insertDataInDB(request, REFDATACACHE, latestRefData);
					saveRefDataInCache(request, REFDATACACHE, latestRefData);
				} else {
					// if the response from sbg failed, error will be returned to client
					// since the reference data is not available
					// let the return value be null in this case.
					logger.error("RefDataCacheHelperInterBank.getRefDataByKey() ---> REFERENCE DATA API FAILED ");
				}
			} else {
				String updatedAt = data4mDB.get("time");
				String refData = data4mDB.get("data");
				if (canUpdateRefData(updatedAt)) {
					// if the data saved in DB has crossed more than 24 hours then pulling latest
					// from sbg
					logger.debug(
							"RefDataCacheHelperInterBank.getRefDataByKey() ---> fetched data from DB and the data is expired");
					String latestRefData = getRefData4mSBG(request, country, currency);
					if (isValidRefData(latestRefData)) {
						// latest data is received from sbg, updating latest data at all placess
						logger.debug(
								"RefDataCacheHelperInterBank.getRefDataByKey() ---> updating data in DB and updating cache");
						retval = latestRefData;
						updateDataInDB(request, REFDATACACHE, latestRefData);
						saveRefDataInCache(request, REFDATACACHE, latestRefData);
					} else {
						// latest data is not received from sbg, keeping old refdata for usage
						// only updating the cache to extend the cache expiry time
						logger.debug(
								"RefDataCacheHelperInterBank.getRefDataByKey() ---> fetching data from sbg failed. using existing data and updating cache");
						// in case if the data is not returned from SBG then we are returning null/empty
						// response.
						// This will fail the ref data api call
						// retval = new String(Base64.getDecoder().decode(refData));
						// saveRefDataInCache(request, REFDATACACHE, retval);
					}
				} else {
					// using the data from DB and just updating the value in Cache to extend time of
					// expiry
					logger.debug(
							"RefDataCacheHelperInterBank.getRefDataByKey() ---> data from DB is valid. Using the same and updating cache");
					retval = new String(Base64.getDecoder().decode(refData));
					saveRefDataInCache(request, REFDATACACHE, retval);
				}
			}
		}

		logger.debug("RefDataCacheHelperInterBank.getRefDataByKey() ---> END ===> " + retval);
		return retval;
	}

	private static boolean isValidRefData(String refData) {
		logger.debug("RefDataCacheHelperInterBank.isValidRefData() ---> refData: " + refData);
		if (SBGCommonUtils.isStringEmpty(refData)) {
			return false;
		}

		try {
			JSONObject responseObj = new JSONObject(refData);
			int opstatus = responseObj.getInt("opstatus");

			if (opstatus != 0) {
				return false;
			}

			int opstatusCutoffTimes = responseObj.getInt("opstatus_RefData-readCutoffTimes");
			int opstatusBuisnessDays = responseObj.getInt("opstatus_RefData-readBuisnessDays");
			int opstatusPublicHolidays = responseObj.getInt("opstatus_RefData-readPublicHolidays");
			int opstatusCurrencyHolidays = responseObj.getInt("opstatus_RefData-readCurrencyHolidays");

			if (opstatusCutoffTimes != 0 || opstatusBuisnessDays != 0 ||
					opstatusPublicHolidays != 0 || opstatusCurrencyHolidays != 0) {
				return false;
			}

			JSONArray daysArray = responseObj.getJSONArray("Days");
			logger.debug("RefDataCacheHelperInterBank.isValidRefData() ---> daysArray: " + daysArray);

			return daysArray != null && daysArray.length() > 0;

		} catch (Exception e) {
			logger.error("RefDataCacheHelperInterBank.isValidRefData() ---> EXCEPTION: " + e.getMessage());
			return false;
		}
	}

	public static Map<String, Object> constructHeaderParams(DataControllerRequest request,
			Map<String, Object> headerParams, String country, String currency)
			throws Exception {
		ConfigurableParametersHelper configurableParametersHelper = request.getServicesManager()
				.getConfigurableParametersHelper();
		Map<String, String> allServerProperties = configurableParametersHelper.getAllServerProperties();
		String productCode = null;

				//Set product code based on payment method 
		String paymentType = request.getParameter("paymentType");
		if(SBGConstants.URGENT_PAYMENT_TYPE.equals(paymentType)){
			productCode = SBGConstants.URGENT_PRODUCT_CODE;
		} else if(SBGConstants.NORMAL_PAYMENT_TYPE.equals(paymentType)){
			productCode = SBGConstants.NORMAL_PRODUCT_CODE;
		}		

		// to get Property from Runtime
		// String sbgHeaderApiProductCode =
		// allServerProperties.get("SBG_HEADER_API_PRODUCT_CODE");
		// Authorization token,clientID, clientSecret are fetched in SBGBseProcessor
		// stored in Request object.
		Result authorizationRresult = SBGCommonUtils.cacheFetchPingToken("Authorization", request);
		String authVal = authorizationRresult.getParamValueByName("Authorization");
		String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
		String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);

		headerParams.put("Authorization", authVal);
		headerParams.put("X-IBM-Client-Id", clientID);
		headerParams.put("X-IBM-Client-Secret", clientSecret);
		headerParams.put("Country", country); // Need to take from Swift Code 5th&6th Character of From Account
		headerParams.put("ProductCode", productCode);
		headerParams.put("Currency", currency);

		logger.info("[RefDataCacheHelperInterBank.constructHeaderParams]: " + headerParams.toString());
		return headerParams;
	}

	public static Map<String, String> getRefDataFromDB(DataControllerRequest request, String ctrycurr) {
		Map<String, String> retval = null;

		try {
			HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
			HashMap<String, Object> svcParams = new HashMap<String, Object>();

			String filter = SBGCommonUtils.buildOdataCondition("ctrycurr", " eq ", ctrycurr);
			svcParams.put("$filter", filter);
			// logger.debug("RefDataCacheHelperInterBank.getRefDataFromDB ===> filter:
			// "+filter);

			Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,
					SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_REFDATACACHE_GET, false);
			logger.debug("RefDataCacheHelperInterBank.getRefDataFromDB() ---> result: " + ResultToJSON.convert(result));

			Dataset ds = result.getDatasetById("SbgRefDataCache");
			if (ds != null && ds.getRecord(0) != null) {
				retval = new HashMap<>();
				Record record = ds.getRecord(0);
				String refData = record.getParamValueByName("refdata");
				String updatedAt = record.getParamValueByName("updateAtInMillis");

				retval.put("data", refData);
				retval.put("time", updatedAt);
			}
		} catch (Exception e) {
			logger.error("RefDataCacheHelperInterBank.getRefDataFromDB ===> EXCEPTION: " + e.getMessage());
		}
		logger.debug("RefDataCacheHelperInterBank.getRefDataFromDB() ---> retval: " + retval);
		return retval;
	}

	public static void insertDataInDB(DataControllerRequest request, String ctrycurr, String data) {
		logger.debug("RefDataCacheHelperInterBank.insertDataInDB() ---> ctrycurr: " + ctrycurr + "; data: " + data);
		try {
			HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
			HashMap<String, Object> svcParams = new HashMap<String, Object>();
			svcParams.put("refdata", Base64.getEncoder().encodeToString(data.getBytes()));
			svcParams.put("updateAtInMillis", System.currentTimeMillis());
			svcParams.put("ctrycurr", ctrycurr);

			Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,
					SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_REFDATACACHE_CREATE, false);
			logger.debug(
					"RefDataCacheHelperInterBank.insertDataInDB ===> Insert result: " + ResultToJSON.convert(result));
		} catch (Exception e) {
			logger.error("RefDataCacheHelperInterBank.insertDataInDB ===> EXCEPTION: " + e.getMessage());
		}
	}

	public static void updateDataInDB(DataControllerRequest request, String ctrycurr, String data) {
		logger.debug("RefDataCacheHelperInterBank.updateDataInDB() ---> ctrycurr: " + ctrycurr + "; data: " + data);
		try {
			HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
			HashMap<String, Object> svcParams = new HashMap<String, Object>();
			svcParams.put("refdata", Base64.getEncoder().encodeToString(data.getBytes()));
			svcParams.put("updateAtInMillis", System.currentTimeMillis());
			svcParams.put("ctrycurr", ctrycurr);

			Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,
					SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_REFDATACACHE_UPDATE, false);
			logger.debug(
					"RefDataCacheHelperInterBank.updateDataInDB ===> Update result: " + ResultToJSON.convert(result));
		} catch (Exception e) {
			logger.error("RefDataCacheHelperInterBank.updateDataInDB ===> EXCEPTION: " + e.getMessage());
		}
	}

	public static void saveRefDataInCache(DataControllerRequest request, String ctrycurr, String data) {
		logger.error("RefDataCacheHelperInterBank.saveRefDataInCache ===> ctrycurr: " + ctrycurr + "; data: " + data);
		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			resultCache.insertIntoCache(ctrycurr, data, EXPIRYINSECS);
		} catch (Exception e) {
			logger.error("RefDataCacheHelperInterBank.saveRefDataInCache ===> EXCEPTION: " + e.getMessage());
		}
		logger.error("RefDataCacheHelperInterBank.saveRefDataInCache ===> END");
	}

	public static boolean canUpdateRefData(String updatedAt) {
		long updateAt = new Long(updatedAt);
		long currTime = System.currentTimeMillis();
		if (updateAt + (1000 * EXPIRYINSECS) < currTime) {
			return true;
		}

		return false;
	}

	private static String getRefData4mSBG(DataControllerRequest request, String country, String currency) {
		logger.error("RefDataCacheHelperInterBank.getRefData4mSBG ===> START");
		try {
			String serviceName = SBGConstants.REFDATA_ORCHSEQ;
			String operationName = SBGConstants.REFDATA_ORCHSEQOPR;

			Map<String, Object> headerParams = new HashMap<String, Object>();
			Map<String, Object> requestParameters = new HashMap<String, Object>();

			headerParams = constructHeaderParams(request, headerParams, country, currency);

			SbgInterBankFundTransferBackendDelegateExtn sbgInterBankFundTransferBackendDelegateExtn = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(SbgInterBankFundTransferBackendDelegateExtn.class);
			String refDataResponse = sbgInterBankFundTransferBackendDelegateExtn.callRefDataOrchService(
					headerParams, requestParameters, serviceName, operationName);

			logger.error("RefDataCacheHelperInterBank.getRefData4mSBG ===> refDataResponse: " + refDataResponse);
			return refDataResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RefDataCacheHelperInterBank.getRefData4mSBG ===> EXCEPTION: " + e.getMessage());
		}
		return null;
	}
}
