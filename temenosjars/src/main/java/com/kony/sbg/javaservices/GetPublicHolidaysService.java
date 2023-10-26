package com.kony.sbg.javaservices;

import org.apache.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.PublicHolidayResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetPublicHolidaysService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(GetPublicHolidaysService.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			LOG.debug("GetPublicHolidaysService::Entry");
			PublicHolidayResource resource =
			DBPAPIAbstractFactoryImpl.getResource(PublicHolidayResource.class);

			LOG.debug("GetPublicHolidaysService::resource: " + resource);
			result = resource.getPublicHolidays(methodID, inputArray, dcRequest, dcResponse);
			LOG.info("GetPublicHolidaysService Result::" + result.toString());
		} catch (Exception exp) {
			exp.printStackTrace();
			LOG.error("Error in GetPublicHolidaysService: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100084.setErrorCode(new Result());
		}

		return result;

	}

}
