package com.kony.sbg.backend.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.backend.api.PublicHolidayBackendDelegate;
import com.kony.sbg.util.RefDataCacheHelperInterBank;
import com.kony.sbg.util.RefDataCacheHelperInterBank;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.ehcache.ResultCacheImpl;
import com.konylabs.middleware.registry.AppRegistryException;

public class PublicHolidayBackendDelegateImpl implements PublicHolidayBackendDelegate {

	private static final Logger LOG = Logger.getLogger(PublicHolidayBackendDelegateImpl.class);
	private static int EXPIRYINSECS = 24 * 60 * 60;

	@Override
	public JSONObject getPublicHolidays(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		JSONObject responseObj = null;
		String PUBLICHOLIDAYCACHE = "PUBLICHOLIDAYCACHE";
		ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
		Result result = (Result) resultCache.retrieveFromCache(PUBLICHOLIDAYCACHE);
		if (result != null && result.hasParamByName(PUBLICHOLIDAYCACHE)) {
			LOG.debug("&&&&&PUBLICHOLIDAYCACHEresultCache: " + resultCache);
			String vendorResponse = result.getParamValueByName(PUBLICHOLIDAYCACHE).toString();
			if (vendorResponse != null) {
				responseObj = new JSONObject(vendorResponse);
			}
		} else {
			String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY;
			String operationName = SbgURLConstants.OPERATION_GETPUBLIC_HOLIDAYS;
			LOG.info("PublicHolidayBackendDelegateImpl::RequestHeaders: " + requestHeaders);
			try {
				String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
						.withOperationId(operationName).withRequestParameters(requestParameters)
						.withRequestHeaders(requestHeaders).build().getResponse();
				LOG.debug("&&&&&PUBLICHOLIDAYCACHEvendorResponse: " + vendorResponse);
				if (vendorResponse != null) {
					responseObj = new JSONObject(vendorResponse);
					if (responseObj.has("opstatus") && (responseObj.getInt("opstatus") == 0)) {
						Result result2 = new Result();
						result2.addParam(PUBLICHOLIDAYCACHE, vendorResponse);
						cacheInsert(PUBLICHOLIDAYCACHE, result2, 0);
					} else {
						return null;
					}
				} else {
					return null;
				}
				LOG.info("##Response from getPublicHolidays: " + responseObj);
			} catch (JSONException e) {
				LOG.error("Failed to fetch public holidays: " + e);
				return null;
			} catch (Exception e) {
				LOG.error("Caught exception while fetching public holidays: " + e);
				return null;
			}
		}
		return responseObj;
	}

	public static void cacheInsert(String key, Result result, int life) throws Exception {
		if (key == null)
			throw new Exception("Cache key must be provided");
		if (life == 0)
			life = 90;
		if (result != null) {
			ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
			resultCache.insertIntoCache(key, result);
		}
	}

	public String getRefDataPublicHolidays(DataControllerRequest request, HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {

		try {
			EXPIRYINSECS = Integer.parseInt(SBGCommonUtils.getServerPropertyValue("REFDATA_CACHE_EXPIRY", request));
		} catch (Exception e) {
			LOG.error("Caught exception while fetching getRefDataPublicHolidays: " + e);
		}
		String PUBLICHOLIDAYCACHE = "PUBLICHOLIDAYCACHE";

		String retval = null;
		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			retval = (String) resultCache.retrieveFromCache(PUBLICHOLIDAYCACHE);
			LOG.error("RefDataCacheHelperInterBank.getRefDataPublicHolidays --> " + retval);

		} catch (AppRegistryException e) {
			LOG.error("RefDataCacheHelperInterBank.getRefDataPublicHolidays ---> 1. EXCEPTION: " + e.getMessage());
		}
		if (SBGCommonUtils.isStringEmpty(retval)) {
			// either the cache is empty or cache has invalid reference data
			// invoke the integration service to fetch refdata from DB
			Map<String, String> data4mDB = RefDataCacheHelperInterBank.getRefDataFromDB(request, PUBLICHOLIDAYCACHE);
			LOG.debug("getRefDataPublicHolidays.getRefDataByKey() ---> data4mDB: " + data4mDB);
			if (data4mDB == null) {
				// no data is found in DB for given set of key (country)
				// making the backend call to fetch data
				String latestPublicHolidays = getPublicHolidaysSBG(request, requestParameters, requestHeaders);
				LOG.debug("getRefDataPublicHolidays.getRefDataByKey() ---> latestRefData: " + latestPublicHolidays);
				LOG.debug("getRefDataPublicHolidays.getRefDataByKey() ---> isValidRefData(latestPublicHolidays): "
						+ isValidRefData(latestPublicHolidays));
				if (isValidRefData(latestPublicHolidays)) {
					// if the response is valid then updating the latest response in db and cache
					LOG.debug(
							"getRefDataPublicHolidays.getRefDataByKey() ---> inserting data in DB and updating cache");
					retval = latestPublicHolidays;
					LOG.debug("getRefDataPublicHolidays.retval() ---> retval");
					RefDataCacheHelperInterBank.insertDataInDB(request, PUBLICHOLIDAYCACHE, latestPublicHolidays);
					RefDataCacheHelperInterBank.saveRefDataInCache(request, PUBLICHOLIDAYCACHE, latestPublicHolidays);
				} else {
					// if the response from sbg failed, error will be returned to client
					// since the reference data is not available
					// let the return value be null in this case.
					LOG.debug("getRefDataPublicHolidays.getRefDataByKey() ---> REFERENCE DATA API FAILED ");
				}
			}

			else {
				String updatedAt = data4mDB.get("time");
				String refData = data4mDB.get("data");
				LOG.debug("getRefDataPublicHolidays.updatedAt() ---> " + updatedAt
						+ "getRefDataPublicHolidays.refData() ---> " + refData);
				if (RefDataCacheHelperInterBank.canUpdateRefData(updatedAt)) {
					// if the data saved in DB has crossed more than 24 hours then pulling latest
					// from sbg

					LOG.debug(
							"RefDataCacheHelperInterBank.getRefDataByKey() ---> fetched data from DB and the data is expired");
					String latestRefData = getPublicHolidaysSBG(request, requestParameters, requestHeaders);
					if (isValidRefData(latestRefData)) {
						// latest data is received from sbg, updating latest data at all placess
						LOG.debug(
								"RefDataCacheHelperInterBank.getRefDataByKey() ---> updating data in DB and updating cache");
						retval = latestRefData;
						RefDataCacheHelperInterBank.updateDataInDB(request, PUBLICHOLIDAYCACHE, latestRefData);
						RefDataCacheHelperInterBank.saveRefDataInCache(request, PUBLICHOLIDAYCACHE, latestRefData);
					} else {
						// latest data is not received from sbg, keeping old refdata for usage
						// only updating the cache to extend the cache expiry time
						LOG.debug(
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
					LOG.debug(
							"RefDataCacheHelperInterBank.getRefDataByKey() ---> data from DB is valid. Using the same and updating cache");
					retval = new String(Base64.getDecoder().decode(refData));
					RefDataCacheHelperInterBank.saveRefDataInCache(request, PUBLICHOLIDAYCACHE, retval);
				}
			}

		}
		LOG.debug("RefDataCacheHelperInterBank.getRefDataByKey() ---> END ===> " + retval);
		return retval;
	}

	private static boolean isValidRefData(String latestPublicHolidays) {
		LOG.debug("RefDataCacheHelperInterBank.isValidRefData() ---> refData: " + latestPublicHolidays);

		if (SBGCommonUtils.isStringEmpty(latestPublicHolidays)) {
			return false;
		}
		try {

			JSONObject responseObj = new JSONObject(latestPublicHolidays);
			if (responseObj.getInt("opstatus") != 0) {
				return false;
			}

			if (responseObj.has("opstatus") && (responseObj.getInt("opstatus") == 0)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("RefDataCacheHelperInterBank.isValidRefData() ---> EXCEPTION: " + e.getMessage());
		}
		return false;
	}

	private static String getPublicHolidaysSBG(DataControllerRequest request, HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		LOG.error("RefDataCacheHelperInterBank.getPublicHolidaysSBG ===> START");
		String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY;
		String operationName = SbgURLConstants.OPERATION_GETPUBLIC_HOLIDAYS;
		LOG.info("PublicHolidayBackendDelegateImpl::RequestHeaders: " + requestHeaders);
		String authorization = "";
		String vendorResponse = null;
		try {

			Result resultCache = SBGCommonUtils.cacheFetchPingToken("Authorization", request);
			if (resultCache != null && resultCache.hasParamByName("Authorization")) {
				authorization = resultCache.getParamValueByName("Authorization").toString();
			} else {
				LOG.error("IBM-Gateway authentication failed");
				return vendorResponse;
			}

			requestHeaders.put("X-IBM-Client-Id",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request));
			requestHeaders.put("X-IBM-Client-Secret",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request));
			requestHeaders.put("Authorization", authorization);
			requestHeaders.put("x-fapi-interaction-id", SBGCommonUtils.generateRandomUUID().toString());

			vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			LOG.debug("&&&&&PUBLICHOLIDAYCACHEvendorResponse: " + vendorResponse);

			return vendorResponse;
		} catch (Exception e) {
			LOG.error("RefDataCacheHelperInterBank.getRefData4mSBG ===> EXCEPTION: " + e.getMessage());
		}
		return null;
	}

}
