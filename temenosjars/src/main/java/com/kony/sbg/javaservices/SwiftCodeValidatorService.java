package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.resources.api.SwiftCodeUserManagementResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
public class SwiftCodeValidatorService implements JavaService2 {
	//private static final Logger LOG = Logger.getLogger(SwiftCodeValidatorService.class);
	private static LoggerUtil logger = new LoggerUtil(SwiftCodeValidatorService.class);
	public Object invoke(String methodID, Object[] inputArray, 
			  DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception 
	{
		Result result = new Result();
		
	    try {
	    	SwiftCodeUserManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(SwiftCodeUserManagementResource.class);
			logger.debug("@@@ resource @@@");
			result = resource.getSwiftCodeDetails(methodID, inputArray, dcRequest, dcResponse);	
	    	result.addParam("status", "true");
	    	
	    } catch (Exception e) {
	    	result.addParam("status", "false");
	    	result.addParam("errmsg", "Error while getting swift details in java");
	    } 		
	    return result;
	}
}
