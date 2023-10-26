package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetTradableAccountsPreProcessor extends SbgBasePreProcessor {
	private static final Logger logger = Logger.getLogger(GetTradableAccountsPreProcessor.class);

	@Override
	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
			DataControllerResponse response, Result result) throws Exception {
		try {
			logger.debug("## Sbg tradable accounts BasePreProcessor");
			Result AuthResult =SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			String authVal = AuthResult.getParamValueByName("Authorization");
			String opName=request.getServicesManager().getOperationData().getOperationId();			
			String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			logger.error("SbgBasePreProcessor::clientID: " + clientID + " clientSecret: " + clientSecret+"authVal"+authVal);
			logger.debug("## authVal == "+authVal);
			request.addRequestParam_("Authorization", authVal);
			request.addRequestParam_("X-IBM-Client-Id", clientID);
			request.addRequestParam_("X-IBM-Client-Secret",clientSecret);
			String channelID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_CHANNEL_ID, request);
			String reqID = CommonUtils.generateUniqueIDHyphenSeperated(0, 8);
			String timestamp = SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			logger.debug("GetTradableAccountsPreProcessor :: x-req-id= "+reqID+"; x-channel-id: "+channelID+"; x-req-timestamp: "+timestamp);
			
			request.addRequestParam_("x-channel-id", channelID);//X-CHANNEL-ID
			request.addRequestParam_("x-req-id",reqID);//X-REQ-ID		
			request.addRequestParam_("x-req-timestamp",timestamp);
			request.addRequestParam_("timestamp",SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString());
			if(opName.contains("AcceptRFQ")) {
				request.addRequestParam_("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.ACC_QUO_REQ_NAME, request));	
			}else if(opName.contains("RejectRFQ")) {
				request.addRequestParam_("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.REJ_QUO_REQ_NAME, request));	
			}else if(opName.contains("GetRFQ")) {
				request.addRequestParam_("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.GET_QUO_REQ_NAME, request));
			}else if(opName.contains("GetTradableAccounts")) {
				request.addRequestParam_("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.TRD_ACC_REQ_NAME, request));
			}
			request.addRequestParam_("x-fapi-interaction-id",(String) SBGCommonUtils.generateRandomUUID());

			//String username = SBGUtil.getUserAttributeFromIdentity(request, "UserName");
			String pingToken	= SBGUtil.getHeaderAttributeFromIdentity(request);
			
			//TODO: Do not pass the x-obo-user as x-obo-token is enough ..
			//request.addRequestParam_("x-obo-user", username);
			request.addRequestParam_("x-obo-token",pingToken);
			
			logger.debug("GetTradableAccountsPreProcessor::header: " + request.getHeaderMap());
			// logger.error("access_token"+authVal);
		} catch (Exception e) {
			logger.error(" Tradable accounts preprocessor fails :" + e.getMessage());
		}
		return true;
	}
}