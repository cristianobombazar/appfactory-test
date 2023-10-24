package com.kony.sbg.javaservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.registry.AppRegistryException;

public class FetchPingToken implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(FetchPingToken.class);

	@Override
	public Object invoke(String arg0, Object[] arg1, DataControllerRequest arg2, DataControllerResponse arg3)
			throws Exception {
		Result result = new Result();
//		JsonObject jsonObject = invokeApi(arg2);
//		if (jsonObject == null) {
//			return result;
//		}
//		String auth = "Bearer ";
//		String token = jsonObject.get("access_token").getAsString();
//		String authVal = auth + token;
//		Result result2 = new Result();
//		result2.addParam("Authorization", authVal);
//		cacheInsert("Authorization", result2, 0);
//		return result2;

		String accessToken = processTokenInfo(arg2);
		result.addParam("AuthToken", accessToken);

		return result;
	}

	private static JsonObject invokeApi(DataControllerRequest dcRequest) {
		try {
			String PING_ISS_URL = "https://enterprisestssit.standardbank.co.za";
			String PING_CLIENT_ID = "da3a3aa6-91fd-4f68-8ed8-a5c683e3e7e3";
			String PING_CLIENT_SECRET = "S3cr3t-021f6";
			String PING_X_XSRF_Header = "PingFederate";
			String PING_GRANT_TYPE = "client_credentials";
			try {
				PING_ISS_URL = SBGCommonUtils.getServerPropertyValue("PING_ISS_URL", dcRequest);
				PING_CLIENT_ID = SBGCommonUtils.getServerPropertyValue("PING_CLIENT_ID", dcRequest);
				PING_CLIENT_SECRET = SBGCommonUtils.getServerPropertyValue("PING_CLIENT_SECRET", dcRequest);
				PING_X_XSRF_Header = SBGCommonUtils.getServerPropertyValue("PING_X_XSRF_Header", dcRequest);
				PING_GRANT_TYPE = SBGCommonUtils.getServerPropertyValue("PING_GRANT_TYPE", dcRequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URL url = new URL(PING_ISS_URL + "/as/token.oauth2");
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
			conn.disconnect();
			return jsonObject;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

//	private static void cacheInsert(String key, Result result, int life) throws Exception {
//		if (key == null)
//			throw new Exception("Cache key must be provided");
//		if (life == 0)
//			life = 90;
//		if (result != null) {
//			ResultCache resultCache = ResultCacheImpl.getInstance();
//			resultCache.insertIntoCache(key, result);
//		}
//	}
//
//	private static Result cacheFetch(String key) throws Exception {
//		if (key == null)
//			throw new Exception("Cache key must be provided");
//		ResultCache resultCache = ResultCacheImpl.getInstance();
//		Result result = (Result) resultCache.retrieveFromCache(key);
//		return result;
//	}

	public static String getB2BAccessToken(DataControllerRequest request) {
		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			String accessToken = (String) resultCache.retrieveFromCache(SBGConstants.AUTHORIZATION);

			if (SBGCommonUtils.isStringEmpty(accessToken) || isTokenExpired(request)) {
				accessToken = generateAndSaveB2BToken(request);
			}

			return SBGConstants.BEARER + " " + accessToken;
		} catch (AppRegistryException e) {
		}
		return null;
	}

	private static String generateAndSaveB2BToken(DataControllerRequest request) {
		JsonObject jsonObject = invokeApi(request);
		if (jsonObject != null) {
			String token = jsonObject.get("access_token").getAsString();
			LOG.debug("FetchPingToken.processTokenInfo ---> 1. token: " + token);

			saveTokenInfo(request, token);
			saveB2BPingTokenInDB(request, token);
			return token;
		}
		return null;
	}

	private static String processTokenInfo(DataControllerRequest request) {
		LOG.debug("FetchPingToken.processTokenInfo ---> START");
		try {
			ResultCache resultCache = request.getServicesManager().getResultCache();
			LOG.debug("FetchPingToken.processTokenInfo ---> resultCache: " + resultCache);

			String accessToken = (String) resultCache.retrieveFromCache(SBGConstants.AUTHORIZATION);
			LOG.debug("FetchPingToken.processTokenInfo ---> accessToken: " + accessToken);

			if (SBGCommonUtils.isStringEmpty(accessToken)) {
				LOG.debug("FetchPingToken.processTokenInfo ---> Token not found in cache.. Making server call");
				accessToken = generateAndSaveB2BToken(request);
			} else {
				if (!isTokenExpired(request)) {
					LOG.debug("FetchPingToken.processTokenInfo ---> Token found and not expired yet");
					saveTokenInfo(request, accessToken);
					// saveB2BPingTokenInDB(request, accessToken);
				} else {
					LOG.debug("FetchPingToken.processTokenInfo ---> Token found and expired");
					accessToken = generateAndSaveB2BToken(request);
				}
			}
			return accessToken;
		} catch (AppRegistryException e) {
			LOG.error("FetchPingToken.processTokenInfo ---> Exception: " + e.getMessage());
		}
		LOG.debug("FetchPingToken.processTokenInfo ---> END");
		return null;
	}

	private static void saveB2BPingTokenInDB(DataControllerRequest request, String token) {

		try {
			String option = SBGCommonUtils.getClientAppValue(SBGConstants.PROP_B2BPINGTOKEN_SAVEINDB, request);
			if (SBGCommonUtils.isStringEmpty(option) || !"YES".equals(option)) {
				return;
			}

			HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
			HashMap<String, Object> svcParams = new HashMap<String, Object>();
			svcParams.put("token", token);

			SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_B2BPINGTOKEN_CREATE, false);
		} catch (Exception e) {
			LOG.error("FetchPingToken.saveB2BPingTokenInDB ---> EXCEPTION: " + e.getMessage());
		}
	}

	private static void saveTokenInfo(DataControllerRequest request, String access_token) {
		try {

			DecodedJWT jwt = JWT.decode(access_token);
			long tokenExpTime = jwt.getExpiresAt().getTime();
			LOG.debug("FetchPingToken.saveTokenInfo ---> tokenExpTime: " + tokenExpTime);

			ResultCache resultCache = request.getServicesManager().getResultCache();
			resultCache.insertIntoCache(SBGConstants.AUTHORIZATION, access_token);
			resultCache.insertIntoCache(SBGConstants.AUTHORIZATION_EXP, tokenExpTime + "");
		} catch (AppRegistryException e) {
			LOG.error("FetchPingToken.saveTokenInfo ---> EXCEPTION: " + e.getMessage());
		}
	}

	private static boolean isTokenExpired(DataControllerRequest request) throws AppRegistryException {
		ResultCache resultCache = request.getServicesManager().getResultCache();
		String str = (String) resultCache.retrieveFromCache(SBGConstants.AUTHORIZATION_EXP);
		long expTime = new Long(str);

		long currTime = System.currentTimeMillis();
		int lessDuration = 1000 * 60 * 10;
		if (currTime < expTime - lessDuration) {
			return false;
		}

		return true;
	}

//	private static String generateKeyCloakAccessToken(DataControllerRequest dcRequest) {
//		try {
//			String PING_ISS_URL = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_ENDPOINT_URL", dcRequest);
//			String PING_CLIENT_ID = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_SERVICE_ACCOUNT_CLIENT_ID",
//					dcRequest);
//			String PING_CLIENT_SECRET = SBGCommonUtils.getServerPropertyValue("KEYCLOAK_SERVICE_ACCOUNT_CLIENT_SECRET",
//					dcRequest);
//			String PING_GRANT_TYPE = SBGCommonUtils.getServerPropertyValue("PING_GRANT_TYPE", dcRequest);
//			String PING_X_XSRF_Header = SBGCommonUtils.getServerPropertyValue("PING_X_XSRF_HEADER", dcRequest);
//			URL url = new URL(PING_ISS_URL + "/token");
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setInstanceFollowRedirects(false);
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("charset", "utf-8");
//			conn.setRequestProperty("Accept", "*/*");
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			conn.setRequestProperty("X-XSRF-Header", PING_X_XSRF_Header);
//			conn.setUseCaches(false);
//			String urlParameters = "client_id=" + PING_CLIENT_ID + "&client_secret=" + PING_CLIENT_SECRET
//					+ "&grant_type=" + PING_GRANT_TYPE;
//			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
//			int postDataLength = postData.length;
//			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
//			conn.getOutputStream().write(postData);
//			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
//			}
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//			StringBuffer sb = new StringBuffer();
//			String output = "";
//			while ((output = br.readLine()) != null) {
//				sb.append(output);
//			}
//			JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
//			LOG.debug("FetchPingToken.processTokenInfo ---> jsonObject : " + jsonObject);
//			conn.disconnect();
//			if (jsonObject != null) {
//				String token = jsonObject.get("access_token").getAsString();
//				LOG.debug("FetchPingToken.processTokenInfo ---> 1. token: " + token);
//				return token;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static String getKeyCloakccessToken(DataControllerRequest request) {
//
//		try {
//			String accessToken = generateKeyCloakAccessToken(request);
//			LOG.debug("FetchPingToken.getKeyCloakccessToken ---> accessToken: " + accessToken);
//			LOG.debug("FetchPingToken.getKeyCloakccessToken ---> accessToken:BEARER " + SBGConstants.BEARER + " "
//					+ accessToken);
//			return SBGConstants.BEARER + " " + accessToken;
//		} catch (Exception e) {
//		}
//		return null;
//	}
}
