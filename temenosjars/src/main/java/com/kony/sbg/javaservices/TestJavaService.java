package com.kony.sbg.javaservices;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.sbg.postprocessor.GetAccessTokenPostProcessor;
import com.kony.sbg.util.AuthCacheManager;
import com.kony.sbg.util.KmfUtils;
import com.kony.sbg.util.RefDataCacheHelper;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class TestJavaService implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(TestJavaService.class);
	
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception 
	{
		logger.debug("TestJavaService.invoke () ---> START ");      
		Result result = new Result();
		
		//Object at = request.getServicesManager().getIdentityHandler().getSecurityAttributes().get("MyAT");
		//Object at = request.getServicesManager().getResultCache().retrieveFromCache("MyAT");
		//logger.debug("TestJavaService.invoke () ---> AT: "+at); 
		
		//Object rt = request.getServicesManager().getIdentityHandler().getSecurityAttributes().get("MyRT");
		//Object rt = request.getServicesManager().getResultCache().retrieveFromCache("MyRT");
		//logger.debug("TestJavaService.invoke () ---> RT: "+rt); 
		
//		String username = "hamjad.hussain@standardbank.co.za";
//		logger.debug("TestJavaService.invoke () ---> username: "+username); 
//		
//		String accessToken = AuthCacheManager.refreshAccessToken(request, username);
//		logger.debug("TestJavaService.invoke () ---> accessToken: "+accessToken); 
//		
//		String refreshToken = AuthCacheManager.getRefreshToken(username);
//		logger.debug("TestJavaService.invoke () ---> refreshToken: "+refreshToken); 
//		
//		String aToken = null;
//		if(AuthCacheManager.isTokenExpired(username)) {
//			AuthCacheManager.refreshAccessToken(request, username);
//		} else {
//			AuthCacheManager.getAccessToken(username);
//		}
//		
//		result.addParam("Status", "Tested 1");
//		result.addParam("MyAT", accessToken);
//		result.addParam("MyRT", refreshToken);
		//KmfUtils.printInputs(inputArray, request, response);
		
		String refdata	= RefDataCacheHelper.getRefDataByKey(request, "ZA", "ZAR");
		
		result.addParam("Status", "Tested 1");
		result.addParam("refdata", refdata);
		
		return result;
	}

	public static String getHeaderAttributeFromIdentity(DataControllerRequest request) {
		String pingToken = null;
		String ACCESS_TOKEN = "access_token";

		try {
			if (request.getServicesManager() != null && request.getServicesManager().getIdentityHandler() != null) {
				Map<String, Object> userMap = request.getServicesManager().getIdentityHandler().getSecurityAttributes("PingDbxUserLogin", true);
				logger.debug("TestJavaService.getHeaderAttributeFromIdentity ===> Printing all security attributes: "+userMap);
				
				if (userMap.get(ACCESS_TOKEN) != null) {
					String attributeValue = (new StringBuilder()).append(userMap.get(ACCESS_TOKEN)).append("").toString();
					pingToken = attributeValue;
				} else {
					logger.debug("TestJavaService.getHeaderAttributeFromIdentity ===> No attribute found FOR access_token in security attributes");
				}
			} else {
				logger.debug("TestJavaService.getHeaderAttributeFromIdentity ===> IDENTITY HANDLER IS NULL IN SERVICE MANAGER");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pingToken;
	}}
