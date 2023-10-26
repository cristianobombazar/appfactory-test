package com.kony.sbg.preprocessor;

import java.util.Base64;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAutomationAccessTokenPreProcessor implements DataPreProcessor2 {

	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String redirect_uri = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_REDIRECT_URI, request);
		String client_id = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_OPEN_CLIENT_ID, request);
		inputMap.put("redirect_uri", redirect_uri);
		inputMap.put("client_id", client_id);
		String operationId = request.getServicesManager().getOperationData().getOperationId();
		if (StringUtils.equals(operationId, "getAccessToken")) {
			String grant_type = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_AUTHORIZATION_CODE, request);
			inputMap.put("grant_type", grant_type);
		} else if (StringUtils.equals(operationId, "refreshAccessToken")) {
			String grant_type = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_REFRESH_CODE, request);
			inputMap.put("grant_type", grant_type);
		}

		String PING_OPEN_CLIENT_SECRET = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_OPEN_CLIENT_SECRET,
				request);
		String AuthorizationHeader = getBasicAuthenticationHeader(client_id, PING_OPEN_CLIENT_SECRET);
		request.addRequestParam_("Authorization", AuthorizationHeader);
		String PING_CONTENT_TYPE = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PING_CONTENT_TYPE, request);
		request.addRequestParam_("Content-Type", PING_CONTENT_TYPE);

		return true;
	}

	private static final String getBasicAuthenticationHeader(String username, String password) {
		String valueToEncode = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	private static final void decodeBasicAuthenticationHeader(String basic) {
		basic = basic.split(" ")[1];
		byte[] basicbyte = Base64.getDecoder().decode(basic);
		basic = new String(basicbyte);
		System.out.println(basic);
		return;
	}

}
