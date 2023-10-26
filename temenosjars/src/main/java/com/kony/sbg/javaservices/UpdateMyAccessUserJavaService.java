package com.kony.sbg.javaservices;

import java.util.Map;
import org.apache.log4j.Logger;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.api.PaymentFeedbackResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UpdateMyAccessUserJavaService implements JavaService2 {

	private static final Logger logger = Logger.getLogger(UpdateMyAccessUserJavaService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			logger.debug("UpdateMyAccessUserJavaService:javaInputParams-------->Entry");
			Map<String, String> javaInputParams = HelperMethods.getInputParamMap(inputArray);
			logger.debug("UpdateMyAccessUserJavaService:javaInputParams" + javaInputParams);
			PaymentFeedbackResource resource = DBPAPIAbstractFactoryImpl.getResource(PaymentFeedbackResource.class);		
			result = resource.updateMyAccessUser(javaInputParams, dcRequest, dcResponse);
			logger.info("Accept quote Result:" + result.toString());
		} catch (Exception exp) {
			logger.error("Error in Accept code: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(new Result());
		}

		return result;
	}

}
