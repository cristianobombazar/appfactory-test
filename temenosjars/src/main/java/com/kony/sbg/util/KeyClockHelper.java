package com.kony.sbg.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.controller.DataControllerRequest;

public class KeyClockHelper {
	
	private static final Logger logger = Logger.getLogger(KeyClockHelper.class);

	public static String getKeyCloakccessToken(DataControllerRequest request) {
		String accessToken = generateKeyCloakAccessToken(request);
		logger.debug("FetchPingToken.getKeyCloakccessToken ---> accessToken: " + accessToken);
		return SBGConstants.BEARER + " " + accessToken;
	}

	private static String generateKeyCloakAccessToken(DataControllerRequest dcRequest) {
		try {
			String PING_ISS_URL = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_ENDPOINT_URL", dcRequest);
			String PING_CLIENT_ID = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_SERVICE_ACCOUNT_CLIENT_ID",
					dcRequest);
			String PING_CLIENT_SECRET = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_SERVICE_ACCOUNT_CLIENT_SECRET",
					dcRequest);
			String PING_GRANT_TYPE = SBGCommonUtils.getServerPropertyValue("PING_GRANT_TYPE", dcRequest);
			String PING_X_XSRF_Header = SBGCommonUtils.getServerPropertyValue("PING_X_XSRF_HEADER", dcRequest);
			URL url = new URL(PING_ISS_URL + "/token");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("X-XSRF-Header", PING_X_XSRF_Header);
			conn.setUseCaches(false);
			String urlParameters = "client_id=" + PING_CLIENT_ID + "&client_secret=" + PING_CLIENT_SECRET
					+ "&grant_type=" + PING_GRANT_TYPE;
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			conn.getOutputStream().write(postData);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuffer sb = new StringBuffer();
			String output = "";
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
			logger.debug("FetchPingToken.processTokenInfo ---> jsonObject : " + jsonObject);
			conn.disconnect();
			if (jsonObject != null) {
				String token = jsonObject.get("access_token").getAsString();
				logger.debug("FetchPingToken.processTokenInfo ---> 1. token: " + token);
				return token;
			}
		} catch (Exception e) {
			logger.error("FetchPingToken.processTokenInfo ---> EXCEPTION: "+e.getMessage());
		}
		return null;
	}
}
