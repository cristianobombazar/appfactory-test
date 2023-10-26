package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.TransactionResourceLimits;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ValidateFXLimits implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(ValidateFXLimits.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			TransactionResourceLimits resource = DBPAPIAbstractFactoryImpl.getResource(TransactionResourceLimits.class);
			result = resource.validateLimits(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception exp) {
			LOG.error("Error in ValidateFXLimits: " + exp.getMessage());
			result.addParam("limitsStatus", "false");
			return SbgErrorCodeEnum.ERR_100018.setErrorCode(result);
		}
		return result;

	}

}
