package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface ManageBeneficiaryResource extends Resource {
	Result checkBeneCodeExists(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException;
	
	Result generateBeneficiaryDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception;

}
