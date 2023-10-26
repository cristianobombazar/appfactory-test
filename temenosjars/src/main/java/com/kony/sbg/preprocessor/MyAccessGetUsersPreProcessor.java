package com.kony.sbg.preprocessor;

public class MyAccessGetUsersPreProcessor {}
//extends SbgBasePreProcessor {
//	private static final Logger logger = Logger.getLogger(MyAccessGetUsersPreProcessor.class);
//
//	@Override
//	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
//			DataControllerResponse response, Result result) throws Exception {
//		try {
//			logger.debug("Entry --> MyAccessGetUsersPreProcessor");
//			Result tokenResult = SBGCommonUtils.GetkeycloakPingToken("Authorization", request);
//			logger.debug("Entry --> MyAccessGetUsersPreProcessor:" + ResultToJSON.convert(tokenResult));
//			String authVal = tokenResult.getParamValueByName("Authorization");
//			String clientID = SBGCommonUtils.getServerPropertyValue("X-IBM-CLIENT-ID", request);
//			String clientSecret = SBGCommonUtils.getServerPropertyValue("X-IBM-CLIENT-SECRET", request);
//
//			logger.debug("Authorization value from MyAccessGetUsersPreProcessor: " + authVal);
//			request.addRequestParam_("Authorization", authVal);
//			logger.debug("MyAccessGetUsersPreProcessor::clientID: " + clientID + " clientSecret: " + clientSecret);
//			request.addRequestParam_("Authorization", authVal);
//			request.addRequestParam_("X-IBM-Client-Id", clientID);
//			request.addRequestParam_("X-IBM-Client-Secret", clientSecret);
//			// request.addRequestParam_("x-fapi-interaction-id",(String)
//			// SBGCommonUtils.generateRandomUUID());
//			logger.debug("MyAccessGetUsersPreProcessor::header: " + request.getHeaderMap());
//		} catch (Exception e) {
//			logger.debug(" MyAccessGetUsersPreProcessor :" + e.getMessage());
//		}
//		return true;
//	}
//
//}