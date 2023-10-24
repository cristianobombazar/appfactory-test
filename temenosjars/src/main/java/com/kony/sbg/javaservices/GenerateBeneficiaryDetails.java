package com.kony.sbg.javaservices;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.ManageBeneficiaryResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GenerateBeneficiaryDetails implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GenerateBeneficiaryDetails.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();		
		try {
			ManageBeneficiaryResource resource = DBPAPIAbstractFactoryImpl.getResource(ManageBeneficiaryResource.class);
			result = resource.generateBeneficiaryDetails(methodId, inputArray, dcRequest, dcResponse);
			LOG.info("GenerateBeneficiaryDetails result: " + result);
		} catch (Exception exp) {
			LOG.error("Error in GenerateBeneficiaryDetails: " + exp);
			return SbgErrorCodeEnum.ERR_100013.setErrorCode(new Result());
		}

		return result;
	}
}
