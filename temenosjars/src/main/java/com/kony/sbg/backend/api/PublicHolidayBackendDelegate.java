package com.kony.sbg.backend.api;

import java.util.HashMap;

import org.json.JSONObject;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;

public interface PublicHolidayBackendDelegate extends BackendDelegate {
	
	JSONObject getPublicHolidays(HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);
	
	String getRefDataPublicHolidays(DataControllerRequest request,HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);

}
