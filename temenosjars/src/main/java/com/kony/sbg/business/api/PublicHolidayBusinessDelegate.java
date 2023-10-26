package com.kony.sbg.business.api;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface PublicHolidayBusinessDelegate extends BusinessDelegate {
	
	Result getPublicHolidays(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws Exception;


}
