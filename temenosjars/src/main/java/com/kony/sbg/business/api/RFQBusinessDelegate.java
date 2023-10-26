package com.kony.sbg.business.api;

import java.util.Map;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RFQBusinessDelegate  extends BusinessDelegate {
	
	Result fetchTradableAccounts(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception;
	Result fetchQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception;
	Result acceptQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception;
	Result rejectQuote(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception;
	Result fetchPreBookedDeals(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception;
	
}
