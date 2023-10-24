package com.kony.sbg.business.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.backend.api.CurrencyBackendDelegate;
import com.kony.sbg.business.api.CurrencyBusinessDelegate;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class CurrencyBusinessDelegateImpl implements CurrencyBusinessDelegate {

	private static final Logger LOG = Logger.getLogger(CurrencyBusinessDelegateImpl.class);

	@Override
	public Result getAllowedCurrencies(JSONObject requestPayload, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();
		HashMap<String, Object> requestParameters = new HashMap<String, Object>();
		HashMap<String, Object> requestHeaders = new HashMap<String, Object>();
		try {
			requestHeaders.put("Country", requestPayload.getString("Country"));
			requestHeaders.put("ProductCode", requestPayload.getString("ProductCode"));
			CurrencyBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(CurrencyBackendDelegate.class);
			//JSONObject response = backendDelegate.getAllowedCurrencies(requestParameters, requestHeaders);
			String refResponse =backendDelegate.getRefDataallowedcurrencies(dcRequest, requestParameters, requestHeaders);
			JSONObject response = new JSONObject(refResponse);
			if (response != null && response.has("Currencies") && response.getJSONArray("Currencies").length() > 0) {
				LOG.info("##GetAllowedCurrencies::ServiceResponse:" + response);
				result = JSONToResult.convert(response.toString());
				LOG.info("##GetAllowedCurrencies::convertedToResult:" + result);
			} else {
				LOG.error("Error response received from getAllowedCurrencies");
				result = SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
			}

		} catch (Exception exp) {
			LOG.error("Error in CurrencyBusinessDelegateImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result getDomesticCurrencies(JSONObject requestPayload, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();
		HashMap<String, Object> requestParameters = new HashMap<String, Object>();
		HashMap<String, Object> requestHeaders = new HashMap<String, Object>();
		try {
			requestHeaders.put("Country", requestPayload.getString("Country"));
			requestHeaders.put("ProductCode", requestPayload.getString("ProductCode"));
			CurrencyBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(CurrencyBackendDelegate.class);
			//JSONObject response = backendDelegate.getAllowedCurrencies(requestParameters, requestHeaders);
			String refResponse =backendDelegate.getRefDataDomesticCurrencies(dcRequest, requestParameters, requestHeaders);
			JSONObject response = new JSONObject(refResponse);
			if (response != null && response.has("Currencies") && response.getJSONArray("Currencies").length() > 0) {
				LOG.info("##gGtDomesticCurrencies::ServiceResponse:" + response);
				result = JSONToResult.convert(response.toString());
				LOG.info("##GetDomesticCurrencies::convertedToResult:" + result);
			} else {
				LOG.error("Error response received from getDomesticCurrencies");
				result = SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
			}

		} catch (Exception exp) {
			LOG.error("Error in CurrencyBusinessDelegateImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
		}

		return result;
	}

}
