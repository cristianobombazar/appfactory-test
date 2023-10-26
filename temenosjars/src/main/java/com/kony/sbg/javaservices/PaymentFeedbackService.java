package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.constants.DBPConstants;
import com.kony.sbg.resources.api.PaymentFeedbackResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PaymentFeedbackService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(PaymentFeedbackService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		LOG.debug("Enty --> PaymentFeedbackService");
		Result result = new Result();
		try {
			PaymentFeedbackResource resource = DBPAPIAbstractFactoryImpl.getResource(PaymentFeedbackResource.class);			
			result = resource.processPaymentFeedback(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception exp) {
			LOG.error("Exception occured in PaymentFeedbackService: ", exp);
			LOG.error("Exception occured in PaymentFeedbackService: " + exp.getStackTrace());
			result = SbgErrorCodeEnum.ERR_100029.setErrorCode(result);			
			result.addOpstatusParam(SbgErrorCodeEnum.ERR_100029.getErrorCode());
			result.addParam("status","failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
		}
		return result;

	}

}
