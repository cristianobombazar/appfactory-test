package com.kony.sbg.resources.api;

import java.util.Map;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface RFQResource extends Resource {
	Result fetchTradableAccounts(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception;
	Result fetchQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception;
	Result acceptQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest,DataControllerResponse dcResponse) throws Exception;
	Result rejectQuote(Map<String, String> inputParams, DataControllerRequest dcRequest,DataControllerResponse dcResponse) throws Exception;
	Result fetchPreBookedDeals(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception;
}
