package com.kony.sbg.business.api;

import java.util.Map;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface PaymentFeedbackBusinessDelegate extends BusinessDelegate {

	Result processPaymentFeedback(JSONObject requestPayload, DataControllerRequest dcRequest) throws ApplicationException;
	
	Result fetchMyAccessGetUser(Map<String, String> inputParams, DataControllerRequest dcRequest) throws ApplicationException;
	
	Result updateMyAccessUser(Map<String, String> inputParams, DataControllerRequest dcRequest)  throws Exception;
	
	

}
