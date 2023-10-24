package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class MyAccessUpdateUserPreProcessor extends MyAccessAuthPreProcessor {
	private static final Logger logger = Logger.getLogger(MyAccessUpdateUserPreProcessor.class);

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		boolean flag = super.execute(inputMap, request, response, result);
		
		logger.debug("MyAccessUpdateUserPreProcessor::updateMyAccessUser-flag" +flag);

		if (flag) {
			if (SBGCommonUtils.isStringEmpty(inputMap.get("userId"))
					|| SBGCommonUtils.isStringEmpty(inputMap.get("enabled"))) {
				logger.debug(
						"MyAccessUpdateUserPreProcessor::updateMyAccessUser --> userId or  enabled flag is empty/null");
				result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
				result.addOpstatusParam(SbgErrorCodeEnum.ERR_100010.getErrorCode());
				result.addParam("status", SBGConstants.FAILED);
				result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
				return false;
			}

		}

		return flag;
	}

}
