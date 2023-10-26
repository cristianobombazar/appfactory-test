package com.kony.sbg.javaservices;

import java.util.Map;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.api.RFQResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetFetchQuoteJavaService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(GetFetchQuoteJavaService.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception 
	{

		Result result = new Result();
		try {
			Map<String, String> javaInputParams = HelperMethods.getInputParamMap(inputArray);
			RFQResource resource = DBPAPIAbstractFactoryImpl
					.getResource(RFQResource.class);
			result = resource.fetchQuotes(javaInputParams,request);
			LOG.info("fetch quote Result:" + result.toString());
		} catch (Exception exp) {
			LOG.error("Error in fetch quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100023.setErrorCode(new Result());
		}

		return result;
	
	}
}
