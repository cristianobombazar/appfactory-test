package com.kony.sbg.resources.api;

import java.util.Map;

import com.dbp.core.api.Resource;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface PaymentFeedbackResource extends Resource {

	public Result processPaymentFeedback(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException;
	Result fetchMyAccessGetUsers(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException;
	Result updateMyAccessUser(Map<String, String> inputParams, DataControllerRequest dcRequest,DataControllerResponse dcResponse) throws Exception;
			

}
