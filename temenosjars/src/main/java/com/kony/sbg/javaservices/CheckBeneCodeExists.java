package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.ManageBeneficiaryResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CheckBeneCodeExists implements JavaService2 {

	private static final Logger logger = Logger.getLogger(CheckBeneCodeExists.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			ManageBeneficiaryResource resource = DBPAPIAbstractFactoryImpl
					.getResource(ManageBeneficiaryResource.class);
			result = resource.checkBeneCodeExists(methodID, inputArray, dcRequest, dcResponse);
			logger.info("CheckBeneCodeExists Result::" + result.toString());
		} catch (Exception exp) {
			logger.error("Error in CheckBeneCodeExists: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100009.setErrorCode(new Result());
		}

		return result;
	}

}
