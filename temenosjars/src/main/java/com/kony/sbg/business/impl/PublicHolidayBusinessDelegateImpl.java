package com.kony.sbg.business.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.backend.api.PublicHolidayBackendDelegate;
import com.kony.sbg.business.api.PublicHolidayBusinessDelegate;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class PublicHolidayBusinessDelegateImpl implements PublicHolidayBusinessDelegate {

	private static final Logger LOG = Logger.getLogger(PublicHolidayBusinessDelegateImpl.class);

	@Override
	public Result getPublicHolidays(JSONObject requestPayload, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();
		HashMap<String, Object> requestParameters = new HashMap<String, Object>();
		HashMap<String, Object> requestHeaders = new HashMap<String, Object>();
		try {
			requestHeaders.put("Country", requestPayload.getString("Country"));
			PublicHolidayBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(PublicHolidayBackendDelegate.class);
			String refResponse =backendDelegate.getRefDataPublicHolidays(dcRequest, requestParameters, requestHeaders);
			JSONObject response = new JSONObject(refResponse);
			if (response != null && response.has("publicHolidaysList") && response.getJSONArray("publicHolidaysList").length() > 0) {
				LOG.info("##getPublicHolidays::ServiceResponse:" + response);
				result = JSONToResult.convert(response.toString());
				LOG.info("##getPublicHolidays::convertedToResult:" + result);
			} else {
				LOG.error("Error response received from getPublicHolidays");
				result = SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
			}

		} catch (Exception exp) {
			LOG.error("Error in PublicHolidayBusinessDelegateImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
		}

		return result;
	}

}
