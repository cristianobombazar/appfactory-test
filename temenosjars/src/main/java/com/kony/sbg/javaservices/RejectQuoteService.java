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

public class RejectQuoteService implements JavaService2 {

	private static final Logger logger = Logger.getLogger(AcceptQuoteService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			Map<String, String> javaInputParams = HelperMethods.getInputParamMap(inputArray);
			RFQResource resource = DBPAPIAbstractFactoryImpl
					.getResource(RFQResource.class);
			result = resource.rejectQuote(javaInputParams,dcRequest,dcResponse);
			logger.info("Reject quote Result:" + result.toString());
		} catch (Exception exp) {
			logger.error("Error in RejectQuote: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100021.setErrorCode(new Result());
		}

		return result;
	}

}
