package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PreBookPreProcessor extends SbgBasePreProcessor {
	private static final Logger logger = Logger.getLogger(PreBookPreProcessor.class);

	@Override
	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
			DataControllerResponse response, Result result) throws Exception {
		try {
			logger.debug("Entry --> PreBookPreProcessor");
			Result tokenResult = SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			String authVal = tokenResult.getParamValueByName("Authorization");
			logger.debug("Authorization value from PreBookPreProcessor: " + authVal);
			String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			String channelID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PREBOOK_CHANNEL_ID, request);
			String countryCode = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE, request);
			String reqID = CommonUtils.generateUniqueIDHyphenSeperated(0, 8);
			String timestamp = SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			
			logger.debug("PreBookPreProcessor::clientID: " + clientID + " clientSecret: " + clientSecret);
			request.addRequestParam_("Authorization",authVal);
			request.addRequestParam_("X-IBM-Client-Id",clientID);
			request.addRequestParam_("X-IBM-Client-Secret",clientSecret);
			request.addRequestParam_("x-fapi-interaction-id",(String) SBGCommonUtils.generateRandomUUID());
			request.addRequestParam_("x-channel-id", channelID);//PreBook-Channel-ID
			request.addRequestParam_("X-Req-Id",reqID);//X-REQ-ID		
			request.addRequestParam_("X-Req-Timestamp",timestamp);
			request.addRequestParam_("X-Country-Code",countryCode);
			logger.debug("PreBookPreProcessor::header: " + request.getHeaderMap());
		} catch (Exception e) {
			logger.error(" fPreBookPreProcessor :" + e.getMessage());
		}
		return true;
	}
}