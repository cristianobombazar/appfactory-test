package com.kony.sbg.postprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.kony.sbg.dto.PingTokenDTO;
import com.kony.sbg.util.AuthCacheManager;
import com.kony.sbg.util.PingTokenCacheHelper;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.temenos.dbx.product.utils.InfinityConstants;

import com.dbp.core.constants.DBPConstants;
import com.kony.dbputilities.util.*;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.kony.sbg.util.PingTokenValidator;

public class GetAccessTokenPostProcessor implements DataPostProcessor2 {
	private static final Logger logger = LogManager.getLogger(GetAccessTokenPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		logger.debug("#################### GetAccessTokenPostProcessor : BEGIN");

		String rawResponse = response.getResponse();
		JSONObject jsonObj = new JSONObject(rawResponse);
		logger.debug("#################### GetAccessTokenPostProcessor : jsonObj: " + jsonObj);

		if (!jsonObj.has("access_token")) {

			logger.debug("#################### GetAccessTokenPostProcessor : empty access token");
			logger.debug(jsonObj.toString());

			return result;
		}

		String access_token = jsonObj.getString("access_token");
		String refresh_token = jsonObj.getString("refresh_token");
		String expires_in = jsonObj.get("expires_in").toString();
		String idToken = jsonObj.getString("id_token");
		logger.debug("#################### GetAccessTokenPostProcessor : refreshToken: " + refresh_token
				+ "; accessToken: " + access_token);
		
		Boolean isTokenValid = true;
		String endPoint = "/ext/sbg/customer/oauth/jwks";
		isTokenValid = PingTokenValidator.isValidToken(access_token, request, endPoint);
		if (!isTokenValid) {
			logger.debug("GetAccessTokenPostProcessor::execute::validateIdToken");
			result = ErrorCodeEnum.ERR_10083.setErrorCode(result);
			result.addOpstatusParam(ErrorCodeEnum.ERR_10083.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		}
		
		isTokenValid = true;
		endPoint = "/pf/JWKS";
		isTokenValid = PingTokenValidator.isValidToken(idToken, request, endPoint);
		if (!isTokenValid) {
			logger.debug("GetAccessTokenPostProcessor::execute::validateIdToken");
			result = ErrorCodeEnum.ERR_10083.setErrorCode(result);
			result.addOpstatusParam(ErrorCodeEnum.ERR_10083.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		}		

		if (validateIdToken(idToken, access_token, request)) {

			// String email = extractEmailFromAccessToken(accessToken);
			JSONObject jobj = decodeTokenPayload(access_token);
			String email = jobj.getString("sub");
			String strongAuthMethod = jobj.getString("amr");
			String strongAuth = jobj.get("strongauthlogin").toString();
			logger.debug("#################### GetAccessTokenPostProcessor : strongAuthMethod: " + strongAuthMethod
					+ "; strongAuth: " + strongAuth);

			result.addParam(new Param("email", email, ""));
			result.addParam(new Param("strongAuth", strongAuth, ""));
			result.addParam(new Param("strongAuthMethod", strongAuthMethod, ""));

			PingTokenDTO dto = new PingTokenDTO();
			dto.setUsername(email);
			dto.setRefreshtoken(refresh_token);
			dto.setAccesstoken(access_token);
			dto.setExpiresin(expires_in);
			logger.debug("GetAccessTokenPostProcessor : saving PingTokenDTO: " + dto);

			PingTokenCacheHelper.storeToken(request, dto);

			try {
				request.getSession(true).setAttribute(email, dto);
			} catch (Exception e) {
				logger.error(
						"GetAccessTokenPostProcessor : saving PingTokenDTO in session Exception: " + e.getMessage());
			}

			// result.addParam(new Param("access_token", accessToken, ""));
			// result.addParam(new Param("refresh_token", refreshToken, ""));
			// Result customer = retrieveCustomerByEmail(request, email);

			// addFeaturesAndPermissionsToResult(request, result, customer);

			logger.debug("#################### GetAccessTokenPostProcessor : END");

			return result;
		} else {
			logger.debug("GetAccessTokenPostProcessor::execute::validateIdToken");
			result = ErrorCodeEnum.ERR_10083.setErrorCode(result);
			result.addOpstatusParam(ErrorCodeEnum.ERR_10083.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		}
	}

	private void addFeaturesAndPermissionsToResult(DataControllerRequest request, Result result, Result customer) {

		if (HelperMethods.hasRecords(customer)) {
			String customerId = HelperMethods.getFieldValue(customer, "id");
			String customerTypeId = HelperMethods.getFieldValue(customer, "CustomerType_id");
			boolean isProspect = HelperMethods.getCustomerTypes().get("Prospect").equals(customerTypeId);

			try {
				Set<String> features;
				Set<String> actions;

				CustomerActionsBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(CustomerActionsBusinessDelegate.class);
				Map<String, Object> map = request.getHeaderMap();
				map.put(InfinityConstants.isProspectFlow, isProspect);
				logger.debug("******* addFeaturesAndPermissionsToResult businessDelegate:" + businessDelegate
						+ " customerId:" + customerId + " map:" + map);
				Map<String, Set<String>> securityAttributes = businessDelegate.getSecurityAttributes(customerId, map);

				actions = securityAttributes.get("actions");
				features = securityAttributes.get("features");

				if (null == actions || null == features) {

					logger.debug("#################### addFeaturesAndPermissionsToResult : empty actions or features");

					actions = new HashSet<>();
					features = new HashSet<>();
				}

				result.addParam(new Param("permissions", getJSONString(actions), MWConstants.STRING));
				result.addParam(new Param("features", getJSONString(features), MWConstants.STRING));
				result.addParam(new Param("session_ttl", "-1", MWConstants.STRING));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				result.addParam(new Param("permissions", "[]", MWConstants.STRING));
				result.addParam(new Param("features", "[]", MWConstants.STRING));
			}
		}
	}

	private Result retrieveCustomerByEmail(DataControllerRequest request, String email) {

		Map<String, String> input = new HashMap<>();

		try {
			input.put("$filter", "UserName eq '" + email + "'");

			return HelperMethods.callApi(request, input, HelperMethods.getHeaders(request), URLConstants.CUSTOMER_GET);

		} catch (HttpCallException e) {
			logger.error("Caught exception while Customer Create :", e);
		}

		return null;
	}

	private boolean validateIdToken(String idToken, String accessToken, DataControllerRequest dcRequest) {

		logger.debug("#### Starting --> GetAccessTokenPostProcessor::execute::validateIdToken");

		try {

			JSONObject idTokenPayload = decodeTokenPayload(idToken);
			JSONObject idTokenHeader = decodeTokenHeaders(idToken);
			logger.debug(
					"Enty --> GetAccessTokenPostProcessor::execute::validateIdToken idTokenPayload" + idTokenPayload);
			logger.debug(
					"Enty --> GetAccessTokenPostProcessor::execute::validateIdToken idTokenHeader" + idTokenHeader);
			String iss = idTokenPayload.getString("iss");
			String client_id = idTokenPayload.getString("aud");
			String emailIdToken = idTokenPayload.getString("sub");
			String idTokenAlg = idTokenHeader.getString("alg");

			String issFromAppServer = SBGCommonUtils.getServerPropertyValue("PING_ISS_URL", dcRequest);
			String client_idFromAppServer = SBGCommonUtils.getServerPropertyValue("PING_OPEN_CLIENT_ID", dcRequest);
			String idTokenAlgProp = SBGCommonUtils.getServerPropertyValue("PING_TOKEN_ALG", dcRequest);

			if (StringUtils.compare(iss, issFromAppServer) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken iss failed");
				return false;
			}

			if (StringUtils.compare(client_id, client_idFromAppServer) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken client_id failed");
				return false;
			}

			if (StringUtils.compare(idTokenAlg, idTokenAlgProp) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken idTokenAlg failed");
				return false;
			}

			long exp = idTokenPayload.getLong("exp") * 1000;// Ex: 1668519688
			Date dateI = convertToUTCDate(exp);
			if (!SBGCommonUtils.timeStampCompare(dateI)) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken exp failed");
				return false;
			}

			long issuedTime = idTokenPayload.getLong("iat") * 1000;// Ex: 1668519688
			logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken issuedTime:" + issuedTime);
			Date issuedTimeInD = convertToUTCDate(issuedTime);
			logger.debug(
					"Enty --> GetAccessTokenPostProcessor::execute::validateIdToken issuedTimeInD:" + issuedTimeInD);
			if (!timeStampCurrentTimeCompare(issuedTimeInD)) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken auth_time iat failed");
				return false;
			}

			long authTime = idTokenPayload.getLong("auth_time") * 1000;// Ex: 1668519688
			Date authTimeInD = convertToUTCDate(authTime);
			if (!timeStampCurrentTimeCompare(authTimeInD)) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken auth_time failed");
				return false;
			}

			Result userInfoResult = getUserInfo(accessToken, dcRequest);
			String emailPingUser = null;
			logger.debug("#### GetAccessTokenPostProcessor::execute::validateIdToken getUserInfoOpenID Rsp--->"
					+ userInfoResult);

			if (userInfoResult != null) {

				emailPingUser = userInfoResult.getParamValueByName("sub");
				logger.debug("#### GetAccessTokenPostProcessor::execute::validateIdToken email--->" + emailPingUser);
				if (!emailPingUser.equalsIgnoreCase(emailIdToken)) {
					return false;
				}
			} else {
				return false;
			}

			if (!validateAccessToken(emailIdToken, emailPingUser, accessToken, dcRequest)) {
				logger.debug(
						"Enty --> GetAccessTokenPostProcessor::execute::validateIdToken access token validation failed");
				return false;
			}

		} catch (Exception e) {
			logger.debug("GetAccessTokenPostProcessor::execute::validateIdToken--->", e);
			return false;
		}
		return true;

	}

	private static Date convertToUTCDate(long exp) throws ParseException {
		Date expDate = new Date(exp);
		DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String expDateInput = simple.format(expDate);
		Date dateI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expDateInput);
		return dateI;
	}

	private boolean validateAccessToken(String idTokenEmail, String pingProfileEmail, String accessToken,
			DataControllerRequest dcRequest) {

		logger.debug("#### Starting --> GetAccessTokenPostProcessor::execute::validateAccessToken");

		try {

			JSONObject accessTokenPayload = decodeTokenPayload(accessToken);
			JSONObject accessTokenHeader = decodeTokenHeaders(accessToken);
			logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateAccessToken" + accessTokenPayload);
			logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateAccessToken" + accessTokenHeader);
			String iss = accessTokenPayload.getString("iss");
			String client_id = accessTokenPayload.getString("client_id");
			String emailAccessToken = accessTokenPayload.getString("sub");
			String accessTokenAlg = accessTokenHeader.getString("alg");

			String issFromAppServer = SBGCommonUtils.getServerPropertyValue("PING_ISS_URL", dcRequest);
			String client_idFromAppServer = SBGCommonUtils.getServerPropertyValue("PING_OPEN_CLIENT_ID", dcRequest);
			String accessTokenAlgProp = SBGCommonUtils.getServerPropertyValue("PING_TOKEN_ALG", dcRequest);

			if (StringUtils.compare(iss, issFromAppServer) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken iss failed");
				return false;
			}

			if (StringUtils.compare(client_id, client_idFromAppServer) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken client_id failed");
				return false;
			}

			if (StringUtils.compare(accessTokenAlg, accessTokenAlgProp) != 0) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken idTokenAlg failed");
				return false;
			}

			long exp = accessTokenPayload.getLong("exp") * 1000;// Ex: 1668519688
			Date dateI = convertToUTCDate(exp);
			if (!SBGCommonUtils.timeStampCompare(dateI)) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken exp failed");
				return false;
			}

			long issuedTime = accessTokenPayload.getLong("iat") * 1000;// Ex: 1668519688
			Date issuedTimeInD = convertToUTCDate(issuedTime);
			if (!timeStampCurrentTimeCompare(issuedTimeInD)) {
				logger.debug("Enty --> GetAccessTokenPostProcessor::execute::validateIdToken auth_time iat failed");
				return false;
			}

			if (!pingProfileEmail.equalsIgnoreCase(emailAccessToken)
					|| !idTokenEmail.equalsIgnoreCase(emailAccessToken)) {
				logger.debug(
						"#### GetAccessTokenPostProcessor::execute::validateAccessToken email validation failed pingProfileEmail:"
								+ pingProfileEmail + " emailAccessToken:" + emailAccessToken + " idTokenEmail:"
								+ idTokenEmail);
				return false;
			}

		} catch (Exception e) {
			logger.debug("GetAccessTokenPostProcessor::execute::validateAccessToken", e);
			return false;
		}
		return true;

	}

	private Result getUserInfo(String accessToken, DataControllerRequest dcRequest) throws HttpCallException {
		logger.debug("#### Starting --> GetAccessTokenPostProcessor::execute::validateIdToken::getUserInfo");
		Map<String, Object> tempParam = new HashMap<>();
		String url = SBGConstants.USER_INFO_OPENID;
		Map<String, String> svcHeaders = new HashMap<String, String>();
		svcHeaders.put("Authorization", "Bearer " + accessToken);
		Result result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, tempParam, svcHeaders, url);
		logger.debug("#### ending --> GetAccessTokenPostProcessor::execute::validateIdToken::getUserInfo");
		return result;

	}

	protected JSONObject decodeTokenPayload(String accessToken) {

		String[] split_string = accessToken.split("\\.");
		String base64EncodedBody = split_string[1];
		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));
		JSONObject jsonObject = new JSONObject(body);

		return jsonObject;
	}

	protected JSONObject decodeTokenHeaders(String accessToken) {

		String[] split_string = accessToken.split("\\.");
		String base64EncodedBody = split_string[0];
		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));
		JSONObject jsonObject = new JSONObject(body);

		return jsonObject;
	}

	private String getJSONString(Set<String> set) {
		return (new JSONArray(set.toString())).toString();
	}

	public static boolean timeStampCurrentTimeCompare(Date date) throws ParseException {
		DateFormat formatterUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
		String ExpDate = formatterUTC.format(date);

		Date UTCdate = null;
		String todayDate = null;
		try {
			UTCdate = SBGCommonUtils.getCurrentUtcTime();
			todayDate = formatterUTC.format(UTCdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp ts1 = Timestamp.valueOf(ExpDate);
		logger.debug("###### ExpDate:" + ts1);
		Timestamp ts2 = Timestamp.valueOf(todayDate);
		logger.debug("###### todayDate:" + ts2);
		// compares ts1 with ts2
		int b3 = ts2.compareTo(ts1);
		if (b3 >= 0) {
			return true;
		} else {
			return false;
		}
	}
}
