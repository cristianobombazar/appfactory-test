package com.kony.sbg.backend.api;

import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RFQBackendDelegate extends BackendDelegate{
	Result fetchTradableAccounts(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest)
			throws Exception;
	Result fetchQuotes(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest)
			throws Exception;
	Result acceptQuotes(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest)
			throws Exception;
	Result rejectQuote(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest)
			throws Exception;
	Result fetchPreBookedDeals(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest)
			throws Exception;
	
}
