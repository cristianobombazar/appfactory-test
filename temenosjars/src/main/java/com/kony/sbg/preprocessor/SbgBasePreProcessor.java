package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SbgBasePreProcessor implements DataPreProcessor2 {
	private static final Logger logger = Logger.getLogger(SbgBasePreProcessor.class);

	@Override
	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
			DataControllerResponse response, Result result) throws Exception {
		try {
			logger.debug("Entry --> SbgBasePreProcessor");
			Result result1 =SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			String authVal = result1.getParamValueByName("Authorization");
			logger.debug("Authorization value from SbgBasePreProcessor: " + authVal);
			String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			logger.debug("SbgBasePreProcessor::clientID: " + clientID + " clientSecret: " + clientSecret);
			request.addRequestParam_("Authorization",authVal);
			request.addRequestParam_("X-IBM-Client-Id",clientID);
			request.addRequestParam_("X-IBM-Client-Secret",clientSecret);
			request.addRequestParam_("x-fapi-interaction-id",(String) SBGCommonUtils.generateRandomUUID());
			logger.debug("SbgBasePreProcessor::header: " + request.getHeaderMap());
		} catch (Exception e) {
			logger.error(" fetch token preprocessor fails :" + e.getMessage());
		}
		return true;
	}
}


