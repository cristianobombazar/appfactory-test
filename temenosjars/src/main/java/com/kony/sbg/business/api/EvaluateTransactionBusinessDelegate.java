package com.kony.sbg.business.api;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface EvaluateTransactionBusinessDelegate extends BusinessDelegate {

	Result evaluateTransaction(JSONObject requestPayload, DataControllerRequest dcRequest) throws ApplicationException;

	boolean validateBoPForm(String bopDetails, Result result, DataControllerRequest dcRequest);

}
