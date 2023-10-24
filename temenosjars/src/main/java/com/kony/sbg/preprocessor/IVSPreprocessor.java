package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class IVSPreprocessor extends SbgBasePreProcessor{
	private static final Logger logger = Logger.getLogger(IVSPreprocessor.class);
	
	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
			DataControllerResponse response, Result result) throws Exception {
		try {
			String opName=request.getServicesManager().getOperationData().getOperationId();	
			Result result1 = SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			String authVal = result1.getParamValueByName("Authorization");
			//logger.debug("Authorization value from IvsArmAPIPreprocessor: " + authVal);
			String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			//logger.debug("IvsArmAPIPreprocessor::clientID: " + clientID + " clientSecret: " + clientSecret);
			request.addRequestParam_("Authorization",authVal);
			request.addRequestParam_("X-IBM-Client-Id",clientID);
			request.addRequestParam_("X-IBM-Client-Secret",clientSecret);
			request.addRequestParam_("x-fapi-interaction-id",(String) SBGCommonUtils.generateRandomUUID());
			
			logger.debug("IvsArmAPIPreprocessor::opName: " + opName);
			params.put("DateTime", java.time.LocalDateTime.now().toString());
			params.put("CustomerClientNumber",request.getParameter("ResidentEntityCCN").toString());
			String ResidentInvCCN =request.getParameter("ResidentInvCCN").toString();
			String ResidentEntityCCN =request.getParameter("ResidentEntityCCN").toString();
			logger.debug("IVSPreprocessor::ResidentInvCCN: " + ResidentInvCCN);
			logger.debug("IVSPreprocessor::ResidentEntityCCN: " + ResidentEntityCCN);			
			if (StringUtils.isNotBlank(ResidentInvCCN) && StringUtils.isNotBlank(ResidentEntityCCN)) {
					params.put("CustomerClientNumber", ResidentInvCCN);
				} else if (StringUtils.isNotBlank(ResidentInvCCN)) {
					params.put("CustomerClientNumber", ResidentInvCCN);
				} else if (StringUtils.isNotBlank(ResidentEntityCCN)) {
					params.put("CustomerClientNumber",ResidentEntityCCN);
				}						
	
		} catch (Exception e) {
			logger.error(" fetch token preprocessor fails :" + e.getMessage());
		}
		return true;
	}
}