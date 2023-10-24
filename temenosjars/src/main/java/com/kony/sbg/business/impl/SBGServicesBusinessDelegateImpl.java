package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.sbg.business.api.SBGServicesBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class SBGServicesBusinessDelegateImpl implements SBGServicesBusinessDelegate {

	private static final Logger LOG = Logger.getLogger(SBGServicesBusinessDelegateImpl.class);

	@Override
	public JSONObject getIndicativeRates(DataControllerRequest request) throws ApplicationException {
		LOG.debug("Enrty --> SBGServicesBusinessDelegateImpl::getIndicativeRates");
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY2;
		String operationName = SbgURLConstants.OPERATION_GETINDICATIVERATES;
		JSONObject serviceResponse = null;
		String authorization = "";
		try {

			Result resultCache = SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			if (resultCache != null && resultCache.hasParamByName("Authorization")) {
				authorization = resultCache.getParamValueByName("Authorization").toString();
			} else {
				LOG.error("IBM-Gateway authentication failed");
				return serviceResponse;
			}

			requestParameters.put("priceSegment",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_PRICE_SEGMENT, request));
			requestParameters.put("currencyPairs",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_CURRENCY_PAIR, request));
			requestHeaders.put("x-channel-id",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_CHANNEL_ID, request));
			requestHeaders.put("x-country-code",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE, request));
			requestHeaders.put("x-req-id", SBGCommonUtils.generateRandomUUID());
			requestHeaders.put("x-req-timestamp", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			requestHeaders.put("X-IBM-Client-Id",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request));
			requestHeaders.put("X-IBM-Client-Secret",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request));
			requestHeaders.put("Authorization", authorization);
			requestHeaders.put("x-fapi-interaction-id", SBGCommonUtils.generateRandomUUID().toString());

			LOG.debug("Request Input of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY2 + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + requestParameters.toString());
			LOG.debug("Request Header of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY2 + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + requestHeaders.toString());

			String indicativeRates = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			serviceResponse = new JSONObject(indicativeRates);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY2 + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + indicativeRates);

		} catch (Exception e) {
			LOG.error("Caught exception at  getIndicativeRates: ", e);
		}
		LOG.debug("Exit --> SBGServicesBusinessDelegateImpl::getIndicativeRates");
		return serviceResponse;
	}

	@Override
	public JSONObject getIndicativeRatesFromCache(DataControllerRequest request) throws ApplicationException {
		LOG.debug("Enrty --> SBGServicesBusinessDelegateImpl::getIndicativeRatesFromCache");
		try {
			String indicativeRates = (String) MemoryManager.getFromCache(SBGConstants.INDICATIVE_RATES);
			if (StringUtils.isEmpty(indicativeRates)) {
				LOG.debug("getIndicativeRatesFromCache::indicativeRates is empty");
				JSONObject indicativeRatesRsp = getIndicativeRates(request);
				if (indicativeRatesRsp.has("opstatus") && indicativeRatesRsp.getInt("opstatus") == 0) {
					if (indicativeRatesRsp != null && indicativeRatesRsp.has("indicativeRates")
							&& indicativeRatesRsp.getJSONArray("indicativeRates").length() > 0) {
						LOG.debug("getIndicativeRatesFromCache::indicativeRates response received");
						int cacheTime = getCacheTimeFromServerProperties(request);
						String indicativeRatesStr = indicativeRatesRsp.toString();
						MemoryManager.saveIntoCache(SBGConstants.INDICATIVE_RATES, indicativeRatesStr, cacheTime);
						indicativeRates = (String) MemoryManager.getFromCache(SBGConstants.INDICATIVE_RATES);
						JSONObject resp = new JSONObject(indicativeRates);
						return resp;
					}
				}
			} else {
				LOG.debug("getIndicativeRatesFromCache::indicativeRates is not empty");
				JSONObject resp = new JSONObject(indicativeRates);
				return resp;
			}

		} catch (Exception e) {
			LOG.error("Caught exception at  getIndicativeRatesFromCache: ", e);
		}
		return null;
	}

	private int getCacheTimeFromServerProperties(DataControllerRequest request) {
		int cacheTime = 3600;

		try {
			cacheTime = Integer
					.parseInt(SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_CACHE_TIME, request));
		} catch (Exception e) {
			LOG.error("Caught exception at  getCacheTimeFromServerProperties: ", e);
		}

		LOG.debug("getCacheTimeFromServerProperties::cacheTime: " + cacheTime);
		return cacheTime;
	}

}
