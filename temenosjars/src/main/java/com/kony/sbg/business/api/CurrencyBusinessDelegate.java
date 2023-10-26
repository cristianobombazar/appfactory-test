package com.kony.sbg.business.api;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface CurrencyBusinessDelegate extends BusinessDelegate {

	Result getAllowedCurrencies(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws Exception;

	Result getDomesticCurrencies(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws Exception;

}
