package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface EvaluateTransactionResource extends Resource {

	public Result evaluateTransaction(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException;

}
