package com.kony.sbg.business.api;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;

public interface SBGServicesBusinessDelegate extends BusinessDelegate {
	
	JSONObject getIndicativeRates(DataControllerRequest request) throws ApplicationException;
	
	JSONObject getIndicativeRatesFromCache(DataControllerRequest request) throws ApplicationException;
	
}
