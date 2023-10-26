package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.kony.sbg.util.KeyClockHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class MyAccessAuthPreProcessor implements DataPreProcessor2 {

	private static final Logger logger = Logger.getLogger(MyAccessAuthPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {

		String header = SBGCommonUtils.getServerPropertyValue("PING_AUTHORIZATION", request);
		String pingToken = request.getHeader(header);
		logger.debug("MyAccessAuthPreProcessor::execute --->pingToken" + pingToken);
		if (!SBGUtil.isValidPingToken(pingToken, request)) {
			logger.debug("MyAccessAuthPreProcessor::execute ---> : PING TOKEN IS INVALID :: Value: " + pingToken);
			result = SbgErrorCodeEnum.ERR_100034.setErrorCode(result);
			result.addOpstatusParam(SbgErrorCodeEnum.ERR_100034.getErrorCode());
			result.addParam("status", SBGConstants.FAILED);
			result.addParam("Reason", "Ping Token validation failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return false;
		}

		// code to get key clock access token and set it to header.
		try {
			String keyClockToken = KeyClockHelper.getKeyCloakccessToken(request);
			logger.debug("MyAccessAuthPreProcessor::execute ---> keyClockToken" + keyClockToken);
			if (SBGCommonUtils.isStringEmpty(keyClockToken)) {
				logger.debug(
						"MyAccessAuthPreProcessor::execute ---> : Unable to fetch keyclock token: " + keyClockToken);
				result = SbgErrorCodeEnum.ERR_100034.setErrorCode(result);
				result.addOpstatusParam(SbgErrorCodeEnum.ERR_100034.getErrorCode());
				result.addParam("status", SBGConstants.FAILED);
				result.addParam("Reason", "Keyclock Token validation failed");
				result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
				return false;
			}
			request.addRequestParam_(SBGConstants.AUTHORIZATION, keyClockToken);
			return true;
		} catch (Exception e) {
			logger.debug(" MyAccessAuthPreProcessor::execute:: EXCEPTION: " + e.getMessage());
		}

		logger.debug("MyAccessAuthPreProcessor.execute() ---> END");
		return false;
	}
}
