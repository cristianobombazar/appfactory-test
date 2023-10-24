package com.kony.sbg.backend.api;

import java.util.HashMap;

import org.json.JSONObject;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;

public interface CurrencyBackendDelegate extends BackendDelegate {
	
	JSONObject getAllowedCurrencies(HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);
	
	String getRefDataallowedcurrencies(DataControllerRequest request,HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);

	String getRefDataDomesticCurrencies(DataControllerRequest request,HashMap<String, Object> requestParameters, HashMap<String, Object> requestHeaders);

}
