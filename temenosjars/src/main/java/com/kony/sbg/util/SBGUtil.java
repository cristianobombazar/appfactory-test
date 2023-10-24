package com.kony.sbg.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.dto.PingTokenDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commonsutils.CustomerSession;

import static com.kony.sbg.util.SBGConstants.EMPTY_STRING;

public class SBGUtil {
	
	private static final Logger logger = Logger.getLogger(SBGUtil.class);
	
	public static String getUserAttributeFromIdentity(DataControllerRequest request, String attribute) {
		
		String retval = "";
		try {
			if (request.getServicesManager() != null && request.getServicesManager().getIdentityHandler() != null) {
				Map<String, Object> userMap = request.getServicesManager().getIdentityHandler().getUserAttributes();
				if (userMap.get(attribute) != null) {
					String attributeValue = (new StringBuilder()).append(userMap.get(attribute)).append("").toString();
					retval = attributeValue;
				} else {
					logger.debug("SBGUtil ===> No attribute found for :"+attribute);
				}
			} else {
				logger.debug("SBGUtil ===> IDENTITY HANDLER IS NULL IN SERVICE MANAGER");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retval;
	}
	
	public static String getHeaderAttributeFromIdentity(DataControllerRequest request) throws HttpCallException {
		String pingToken = null; //request.getHeader("x-kony-authorization");
//		String ACCESS_TOKEN = "access_token";
//
//		try {
//			if (request.getServicesManager() != null && request.getServicesManager().getIdentityHandler() != null) {
//				Map<String, Object> userMap = request.getServicesManager().getIdentityHandler().getSecurityAttributes("PINGAuthService", false);
//				logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> Printing all security attributes: "+userMap);
//				
//				if (userMap.get(ACCESS_TOKEN) != null) {
//					String attributeValue = (new StringBuilder()).append(userMap.get(ACCESS_TOKEN)).append("").toString();
//					pingToken = attributeValue;
//				} else {
//					logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> No attribute found FOR access_token in security attributes");
//				}
//			} else {
//				logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> IDENTITY HANDLER IS NULL IN SERVICE MANAGER");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		String username = SBGUtil.getUserAttributeFromIdentity(request, "UserName");
		logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> username: "+username);
		
		PingTokenDTO dto = new PingTokenDTO();
		dto.setUsername(username);
		try {
			pingToken = PingTokenCacheHelper.getValidPingToken(request, dto);
		} catch (Exception e) {
			logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> EXCEPTION: "+e.getMessage());
		}
		
		logger.debug("SBGUtil.getHeaderAttributeFromIdentity ===> pingToken: "+pingToken);
		return pingToken;
	}
	
	public	static boolean validateRegex(String regex, String str) {
		str = str.trim();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.matches();
	}
	public static boolean isValidPingToken(String token, DataControllerRequest dcRequest) {
		try {
			logger.debug("SbgUtil::isValidPingToken::authToken :: " + token);
			
			String pingToken = token;
			
			if (StringUtils.isNotEmpty(token)) {
				if (pingToken.contains("Bearer")) {
					pingToken = pingToken.split(" ")[1];
				}

				Date exp = JWT.decode(pingToken).getExpiresAt();
				if (!SBGCommonUtils.timeStampCompare(exp)) {
					return false;
				}

				String endPoint = "/ext/sbg/customer/oauth/jwks";
				return PingTokenValidator.isValidToken(pingToken, dcRequest, endPoint);
			}
			logger.debug("SbgUtil::isValidPingToken:::empty");
			return false;
		} catch (Exception e) {
			logger.debug("SbgUtil::isValidPingToken::EXCEPTION: " + e.getMessage());
			return false;
		}
	}
	
	public	static JSONObject getCompanyData4mParty(DataControllerRequest request) {
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String custId = CustomerSession.getCustomerId(customer);
		String companyPartyId	= (String)MemoryManager.getFromCache(SBGConstants.COMPANYPARTYID+custId);
		logger.debug("SbgUtil.getCompanyData4mParty --->custId: " + custId+"; companyPartyId: "+companyPartyId);
		
		if(SBGCommonUtils.isStringEmpty(companyPartyId)) {
			companyPartyId		= getCorePartyId4mBackendIdentifier(custId);
			logger.debug("SbgUtil.getCompanyData4mParty ---> FETCHED COMPANYPARTYID FROM DB :: companyPartyId: "+companyPartyId);
			
			if(!SBGCommonUtils.isStringEmpty(companyPartyId)) {
				
				String partyObjStr	= (String)MemoryManager.getFromCache(SBGConstants.COMPANYPARTYID+companyPartyId);
				logger.debug("SbgUtil.getCompanyData4mParty ---> FETCHED partyObjStr FROM CACHE :: partyObjStr: "+partyObjStr);
				
				if(SBGCommonUtils.isStringEmpty(partyObjStr)) {
					JSONObject partyObj = getPartyDetailsById(request, companyPartyId);
					logger.debug("SbgUtil.getCompanyData4mParty ---> FETCHED partyObj FROM MS :: partyObj: "+partyObj);
					
					if(partyObj != null) {
						MemoryManager.saveIntoCache(SBGConstants.COMPANYPARTYID+custId, companyPartyId, SBGConstants.SECONDS_IN_A_DAY);
						MemoryManager.saveIntoCache(SBGConstants.COMPANYPARTYID+companyPartyId, partyObj.toString(), SBGConstants.SECONDS_IN_A_DAY);
						return partyObj;
					}					
				} else {
					return new JSONObject(partyObjStr);
				}
			}
		} else {
			String partyObjStr	= (String)MemoryManager.getFromCache(SBGConstants.COMPANYPARTYID+companyPartyId);
			logger.debug("SbgUtil.getCompanyData4mParty ---> FETCHED partyObjStr FROM CACHE :: partyObjStr: "+partyObjStr);
			
			if(SBGCommonUtils.isStringEmpty(partyObjStr)) {
				JSONObject partyObj = getPartyDetailsById(request, companyPartyId);
				logger.debug("SbgUtil.getCompanyData4mParty ---> FETCHED partyObj FROM MS :: partyObj: "+partyObj);
				
				if(partyObj != null) {
					MemoryManager.saveIntoCache(SBGConstants.COMPANYPARTYID+custId, companyPartyId, SBGConstants.SECONDS_IN_A_DAY);
					MemoryManager.saveIntoCache(SBGConstants.COMPANYPARTYID+companyPartyId, partyObj.toString(), SBGConstants.SECONDS_IN_A_DAY);
					return partyObj;
				}
			} else {
				return new JSONObject(partyObjStr);
			}
		}
		
		return null;
	}

	public	static String getCorePartyId4mBackendIdentifier(String customerId) {
		Map<String, Object> requestParams = new HashMap<>();
		String filter = "", corePartyId = "";
		
		filter = "Customer_id " + DBPUtilitiesConstants.EQUAL + customerId;
		filter = filter + " " + DBPUtilitiesConstants.AND + " ";
		filter = filter + "BackendType " + DBPUtilitiesConstants.EQUAL + SBGConstants.COMPANYPARTYID;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_BACKENDIDENTIFIER_GET;
		
		JSONObject serviceResponse = null;
		logger.debug("SbgUtil.getCorePartyId4mBackendIdentifier --->filter:: " + requestParams);
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			logger.debug("SbgUtil.getCorePartyId4mBackendIdentifier ---> Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET + ":: " + updateResponse);
			serviceResponse = new JSONObject(updateResponse);

			if (serviceResponse != null && serviceResponse.has("backendidentifier")
					&& serviceResponse.getJSONArray("backendidentifier").length() > 0) {
				JSONObject contractCustomer = serviceResponse.getJSONArray("backendidentifier").getJSONObject(0);
				corePartyId = contractCustomer.optString("BackendId");
				logger.debug("SbgUtil.getCorePartyId4mBackendIdentifier ---> corePartyId: " + corePartyId);
			} 
		}

		catch (JSONException jsonExp) {
			logger.error("SbgUtil.getCorePartyId4mBackendIdentifier ---> JSONExcpetion occured while fetching contract customers ", jsonExp);
			return corePartyId;
		} catch (Exception exp) {
			logger.error("SbgUtil.getCorePartyId4mBackendIdentifier ---> Excpetion occured while fetching contract customers: ", exp);
			return corePartyId;
		}
		return corePartyId;
	}
	
	private static JSONObject getPartyDetailsById(DataControllerRequest request, String partyId) {
		logger.debug("SbgUtil.getPartyDetailsById ---> START :: partyId: "+partyId);
		String serviceName = SbgURLConstants.SERVICE_PARTYMSSERVICE;
		String operationName = SbgURLConstants.OPERATION_GETPARTYDETAILSBYID;		
		JSONObject serviceResponse = null;

		try {
			Map<String, Object> requestParameter = new HashMap<>();
			Map<String, Object> requestHeaders = new HashMap<>();		
			String partyAuthKey = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PARTYMS_AUTHORIZATION_KEY, request);
			
			requestParameter.put("partyid", partyId);
			requestHeaders.put("X-API-Key", partyAuthKey);
			
			String partyResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
			.withOperationId(operationName).withRequestParameters(requestParameter)
			.withRequestHeaders(requestHeaders).build().getResponse();
			
			logger.debug("SbgUtil.getPartyDetailsById ---> Response of Service : " + serviceName + " Operation : "
					+ operationName + ":: " + partyResponse);
			serviceResponse = new JSONObject(partyResponse);
		}

		catch (JSONException jsonExp) {
			logger.error("SbgUtil.getPartyDetailsById ---> JSONExcpetion occured while fetching party details: ", jsonExp);
			return null;
		} catch (Exception exp) {
			logger.error("SbgUtil.getPartyDetailsById ---> Excpetion occured while fetching party details: ", exp);
			return null;
		}
		return serviceResponse;
	}

	public static String getString(org.json.JSONObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);
		}
		return EMPTY_STRING;
	}
	public static String getString(Result result, String key) {
		if (result.hasParamByName(key)) {
			return result.getParamValueByName(key);
		}
		return EMPTY_STRING;
	}

	public static String getString(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			return jsonObject.get(key).getAsString();
		}
		return EMPTY_STRING;
	}
	
}
