package com.kony.sbg.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.payeeservices.javaservices.CreateExternalPayeeOperation;
import com.temenos.dbx.product.payeeservices.resource.api.ExternalPayeeResource;

public class SbgCreateExternalPayeeOperation  implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateExternalPayeeOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		try {
			//Initializing of ExternalPayeeResource through Abstract factory method
			ExternalPayeeResource externalPayeeResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ExternalPayeeResource.class);;

					result  = externalPayeeResource.createPayee(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of SbgCreateExternalPayeeOperation: ", e);
			return SbgErrorCodeEnum.ERR_100005.setErrorCode(new Result());
		}

		return result;
	}
}
