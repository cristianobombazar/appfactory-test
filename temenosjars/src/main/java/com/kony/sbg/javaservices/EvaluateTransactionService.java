package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.EvaluateTransactionResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class EvaluateTransactionService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(EvaluateTransactionService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		LOG.debug("Enty --> EvaluateTransaction");
		Result result = new Result();
		try {
			EvaluateTransactionResource resource = DBPAPIAbstractFactoryImpl
					.getResource(EvaluateTransactionResource.class);
			result = resource.evaluateTransaction(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception exp) {
			LOG.error("Exception occured in EvaluateTransactionService: ", exp);
			result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
		}
		return result;

	}

}
