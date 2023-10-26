package com.kony.sbg.business.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.sbg.backend.api.SwiftCodeBackendDelegate;
import com.kony.sbg.business.api.SwiftCodeBusinessDelegate;
import com.kony.sbg.dto.SwiftCodeDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;


public class SwiftCodeBusinessDelegateImpl implements SwiftCodeBusinessDelegate{
	private static final Logger logger = Logger.getLogger(SwiftCodeBusinessDelegateImpl.class);
	

	@Override
	public Result getSwiftCodeDetails(SwiftCodeDTO swiftCodeDTO, Map<String, Object> headerMap,DataControllerRequest dcRequest)
			throws ApplicationException {		
		   
		    Result result = new Result();
		    try {
		    	SwiftCodeBackendDelegate backendDelegate = (SwiftCodeBackendDelegate)DBPAPIAbstractFactoryImpl.getBackendDelegate(SwiftCodeBackendDelegate.class);
		      logger.debug("@@@ business:::@@@");
		      result=backendDelegate.getSwiftCodeResponse(swiftCodeDTO, headerMap,dcRequest);
		     
		    } catch (Exception e) {
		    	result.addParam("errmsg", "Error while getting swift details in business layer");
		    } 
		    return result;
		  }
	
		
}
