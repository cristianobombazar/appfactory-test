package com.kony.sbg.javaservices;

import java.util.Map;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthorizationCode implements JavaService2 {

	private	LoggerUtil logger = new LoggerUtil(GetAuthorizationCode.class);
	
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		logger.debug("GetAuthorizationCode.invoke ---> START");
		
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		logger.debug("GetAuthorizationCode.invoke ---> inputParams: "+inputParams);
		
		
		String authCode = inputParams.get("code");
		logger.debug("GetAuthorizationCode.invoke ---> authCode: "+authCode);
		
		result.addParam(new Param("authCode", authCode, ""));
		result.addParam(new Param("access_token",authCode,""));
		
		logger.debug("GetAuthorizationCode.invoke ---> END");
		return result;
	}

}
