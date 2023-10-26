package com.kony.sbg.backend.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.backend.api.CurrencyBackendDelegate;
import com.kony.sbg.util.RefDataCacheHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.ehcache.ResultCacheImpl;
import com.konylabs.middleware.registry.AppRegistryException;

public class CurrencyBackendDelegateImpl implements CurrencyBackendDelegate {

	private static final Logger LOG = Logger.getLogger(CurrencyBackendDelegateImpl.class);
	private static int EXPIRYINSECS = 24 * 60 * 60;

	@Override
	public JSONObject getAllowedCurrencies(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		JSONObject responseObj = null;
		String ALLOWEDCURRENCYCACHE = "ALLOWEDCURRENCYCACHE";
		ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
		Result result = (Result) resultCache.retrieveFromCache(ALLOWEDCURRENCYCACHE);
		if (result != null && result.hasParamByName(ALLOWEDCURRENCYCACHE)) {
			LOG.debug("&&&&&ALLOWEDCURRENCYCACHEresultCache: " + resultCache);
			String vendorResponse = result.getParamValueByName(ALLOWEDCURRENCYCACHE).toString();
			if (vendorResponse != null) {
				responseObj = new JSONObject(vendorResponse);
			}
		} else {
			String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY;
			String operationName = SbgURLConstants.OPERATION_GETALLOWEDCURRENCIES;
			LOG.info("CurrencyBackendDelegateImpl::RequestHeaders: " + requestHeaders);
			try {
				String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
						.withOperationId(operationName).withRequestParameters(requestParameters)
						.withRequestHeaders(requestHeaders).build().getResponse();
				LOG.debug("&&&&&ALLOWEDCURRENCYCACHEvendorResponse: " + vendorResponse);
				if (vendorResponse != null) {
					responseObj = new JSONObject(vendorResponse);
					if (responseObj.has("opstatus") && (responseObj.getInt("opstatus") == 0)) {
						Result result2 = new Result();
						result2.addParam(ALLOWEDCURRENCYCACHE, vendorResponse);
						cacheInsert(ALLOWEDCURRENCYCACHE, result2, 0);
					} else {
						return null;
					}
				} else {
					return null;
				}
				LOG.info("##Response from getAllowedCurrencies: " + responseObj);
			} catch (JSONException e) {
				LOG.error("Failed to fetch allowed currencies: " + e);
				return null;
			} catch (Exception e) {
				LOG.error("Caught exception while fetching allowed currencies: " + e);
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

	public String getRefDataallowedcurrencies(DataControllerRequest request, HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {

		try {
			EXPIRYINSECS = Integer.parseInt(SBGCommonUtils.getServerPropertyValue("REFDATA_CACHE_EXPIRY", request));
		} catch (Exception e) {
			LOG.error("Caught exception while fetching allowed getRefDataallowedcurrencies: " + e);
		}
		String ALLOWEDCURRENCYCACHE = "ALLOWEDCURRENCYCACHE";

		String retval = null;
		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			retval = (String) resultCache.retrieveFromCache(ALLOWEDCURRENCYCACHE);
			LOG.error("RefDataCacheHelper.getRefDataallowedcurrencies --> " + retval);

		} catch (AppRegistryException e) {
			LOG.error("RefDataCacheHelper.getRefDataallowedcurrencies ---> 1. EXCEPTION: " + e.getMessage());
		}
		if (SBGCommonUtils.isStringEmpty(retval)) {
			// either the cache is empty or cache has invalid reference data
			// invoke the integration service to fetch refdata from DB
			Map<String, String> data4mDB = RefDataCacheHelper.getRefDataFromDB(request, ALLOWEDCURRENCYCACHE);
			LOG.debug("getRefDataallowedcurrencies.getRefDataByKey() ---> data4mDB: " + data4mDB);
			if (data4mDB == null) {
				// no data is found in DB for given set of key (country,currency)
				// making the backend call to fetch data
				String latestAllowedCurrencies = getAllowedCurrenciesSBG(request, requestParameters, requestHeaders);
				LOG.debug(
						"getRefDataallowedcurrencies.getRefDataByKey() ---> latestRefData: " + latestAllowedCurrencies);
				LOG.debug("getRefDataallowedcurrencies.getRefDataByKey() ---> isValidRefData(latestAllowedCurrencies): "
						+ isValidRefData(latestAllowedCurrencies));
				if (isValidRefData(latestAllowedCurrencies)) {
					// if the response is valid then updating the latest response in db and cache
					LOG.debug(
							"getRefDataallowedcurrencies.getRefDataByKey() ---> inserting data in DB and updating cache");
					retval = latestAllowedCurrencies;
					LOG.debug("getRefDataallowedcurrencies.retval() ---> retval");
					RefDataCacheHelper.insertDataInDB(request, ALLOWEDCURRENCYCACHE, latestAllowedCurrencies);
					RefDataCacheHelper.saveRefDataInCache(request, ALLOWEDCURRENCYCACHE, latestAllowedCurrencies);
				} else {
					// if the response from sbg failed, error will be returned to client
					// since the reference data is not available
					// let the return value be null in this case.
					LOG.debug("getRefDataallowedcurrencies.getRefDataByKey() ---> REFERENCE DATA API FAILED ");
				}
			}

			else {
				String updatedAt = data4mDB.get("time");
				String refData = data4mDB.get("data");
				LOG.debug("getRefDataallowedcurrencies.updatedAt() ---> " + updatedAt
						+ "getRefDataallowedcurrencies.refData() ---> " + refData);
				if (RefDataCacheHelper.canUpdateRefData(updatedAt)) {
					// if the data saved in DB has crossed more than 24 hours then pulling latest
					// from sbg

					LOG.debug("RefDataCacheHelper.getRefDataByKey() ---> fetched data from DB and the data is expired");
					String latestRefData = getAllowedCurrenciesSBG(request, requestParameters, requestHeaders);
					if (isValidRefData(latestRefData)) {
						// latest data is received from sbg, updating latest data at all placess
						LOG.debug("RefDataCacheHelper.getRefDataByKey() ---> updating data in DB and updating cache");
						retval = latestRefData;
						RefDataCacheHelper.updateDataInDB(request, ALLOWEDCURRENCYCACHE, latestRefData);
						RefDataCacheHelper.saveRefDataInCache(request, ALLOWEDCURRENCYCACHE, latestRefData);
					} else {
						// latest data is not received from sbg, keeping old refdata for usage
						// only updating the cache to extend the cache expiry time
						LOG.debug(
								"RefDataCacheHelper.getRefDataByKey() ---> fetching data from sbg failed. using existing data and updating cache");
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
							"RefDataCacheHelper.getRefDataByKey() ---> data from DB is valid. Using the same and updating cache");
					retval = new String(Base64.getDecoder().decode(refData));
					RefDataCacheHelper.saveRefDataInCache(request, ALLOWEDCURRENCYCACHE, retval);
				}
			}

		}
		LOG.debug("RefDataCacheHelper.getRefDataByKey() ---> END ===> " + retval);
		return retval;
	}

	private static boolean isValidRefData(String latestAllowedCurrencies) {
		LOG.debug("RefDataCacheHelper.isValidRefData() ---> refData: " + latestAllowedCurrencies);

		if (SBGCommonUtils.isStringEmpty(latestAllowedCurrencies)) {
			return false;
		}
		try {

			JSONObject responseObj = new JSONObject(latestAllowedCurrencies);
			if (responseObj.getInt("opstatus") != 0) {
				return false;
			}

			if (responseObj.has("sbgerrcode") && !SBGCommonUtils.isStringEmpty(responseObj.get("sbgerrcode"))) {
				return false;
			}

			if (responseObj.has("sbgerrmsg") && !SBGCommonUtils.isStringEmpty(responseObj.get("sbgerrmsg"))) {
				return false;
			}

			if (responseObj.has("opstatus") && (responseObj.getInt("opstatus") == 0)) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("RefDataCacheHelper.isValidRefData() ---> EXCEPTION: " + e.getMessage());
		}
		return false;
	}

	public static void main(String s[]) {
		String res1 = "{\"sbgerrcode\":\"URL Open error\",\"opstatus\":0,\"sbgerrmsg\":\"Internal Error\",\"Currencies\":[],\"httpStatusCode\":500}";
		String res2 = "{\"opstatus\":0,\"Currencies\":[{\"Currency\":\"AED\"},{\"Currency\":\"AUD\"},{\"Currency\":\"ZMW\"}],\"httpStatusCode\":200}";
		System.out.println("Negative Test: " + isValidRefData(res1));
		System.out.println("Positive Test: " + isValidRefData(res2));
	}

	private static String getAllowedCurrenciesSBG(DataControllerRequest request,
			HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		LOG.error("RefDataCacheHelper.getAllowedCurrenciesSBG ===> START");
		String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY;
		String operationName = SbgURLConstants.OPERATION_GETALLOWEDCURRENCIES;
		LOG.info("CurrencyBackendDelegateImpl::RequestHeaders: " + requestHeaders);
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			LOG.debug("&&&&&ALLOWEDCURRENCYCACHEvendorResponse: " + vendorResponse);

			return vendorResponse;
		} catch (Exception e) {
			LOG.error("RefDataCacheHelper.getRefData4mSBG ===> EXCEPTION: " + e.getMessage());
		}
		return null;
	}

	public String getRefDataDomesticCurrencies(DataControllerRequest request, HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {

		String retval = null;

		String latestAllowedCurrencies = getAllowedCurrenciesSBG(request, requestParameters, requestHeaders);
		LOG.debug("getRefDataDomesticCurrencies.getRefDataByKey() ---> latestRefData: " + latestAllowedCurrencies);
		LOG.debug("getRefDataDomesticCurrencies.getRefDataByKey() ---> isValidRefData(latestAllowedCurrencies): "
				+ isValidRefData(latestAllowedCurrencies));
		if (isValidRefData(latestAllowedCurrencies)) {
			// if the response is valid then updating the latest response in db and cache
			LOG.debug("getRefDataDomesticCurrencies.getRefDataByKey() ---> inserting data in DB and updating cache");
			retval = latestAllowedCurrencies;
			LOG.debug("getRefDataDomesticCurrencies.retval() ---> retval");
			// RefDataCacheHelper.insertDataInDB(request, ALLOWEDCURRENCYCACHE,
			// latestAllowedCurrencies);
			// RefDataCacheHelper.saveRefDataInCache(request, ALLOWEDCURRENCYCACHE,
			// latestAllowedCurrencies);
		} else {
			// if the response from sbg failed, error will be returned to client
			// since the reference data is not available
			// let the return value be null in this case.
			LOG.debug("getRefDataDomesticCurrencies.getRefDataByKey() ---> REFERENCE DATA API FAILED ");
		}
		LOG.debug("RefDataCacheHelper.getRefDataByKey() ---> END ===> " + retval);
		return retval;
	}

}
