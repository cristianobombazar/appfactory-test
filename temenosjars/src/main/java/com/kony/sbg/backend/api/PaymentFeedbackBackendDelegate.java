package com.kony.sbg.backend.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface PaymentFeedbackBackendDelegate extends BackendDelegate {

	JSONObject maintainTranferStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getInternationalFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getStatusHistory(HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);

	Result getMyAccessAllUsers(Map<String, Object> svcInputParams, Map<String, String> svcHeaders,
			DataControllerRequest dcRequest);

	Result updateMyAccessUser(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest)
			throws Exception;

	JSONObject maintainDomesticTranferStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getInterBankFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getDomesticStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject maintainIATStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getOwnAccountFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);

	JSONObject getOwnAccountStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders);
}
