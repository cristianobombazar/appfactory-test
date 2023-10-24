package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.CurrencyResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAllowedCurrenciesService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(GetAllowedCurrenciesService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			CurrencyResource resource = DBPAPIAbstractFactoryImpl.getResource(CurrencyResource.class);
			result = resource.getAllowedCurrencies(methodID, inputArray, dcRequest, dcResponse);
			LOG.info("GetAllowedCurrenciesService Result::" + result.toString());
		} catch (Exception exp) {
			LOG.error("Error in GetAllowedCurrenciesService: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(new Result());
		}

		return result;

	}

}
