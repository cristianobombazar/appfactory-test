package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class AuthCacheManager {
	
	private static final Logger logger = LogManager.getLogger(AuthCacheManager.class);
    private static Map<String, PingTokenInfo> userTokens = new ConcurrentHashMap<>();
    
    private	static AuthCacheManager cacheManager = null;

    private	AuthCacheManager() {
    	
    }
    
    public	synchronized static AuthCacheManager getManager() {
    	if(cacheManager == null) {
    		synchronized (AuthCacheManager.class) {
        		cacheManager = new AuthCacheManager();
			}
    	}
    	
    	return cacheManager;
    }
    
    public	static PingTokenInfo getInfo(String username) {
    	return userTokens.get(username.toLowerCase());
    }
    
    public	static void storeToken(String username, String rtoken, String atoken, String expiry) {
    	logger.debug("AuthCacheManager : storeToken === START: username: "+username);
    	PingTokenInfo info = new PingTokenInfo(rtoken, atoken, expiry);
    	userTokens.put(username.toLowerCase(), info);
    	logger.debug("AuthCacheManager : storeToken === info: "+info);
    }
    
    public	static String getAccessToken(String username) {
    	PingTokenInfo info = userTokens.get(username.toLowerCase());
    	String retval = info == null ? null : info.accessToken;
    	
    	logger.debug("AuthCacheManager : getAccessToken === retval: "+retval);
    	return retval;
    }

    public	static String getRefreshToken(String username) {
    	PingTokenInfo info = userTokens.get(username.toLowerCase());
    	return info == null ? null : info.refreshToken;
    }

    public	static boolean isTokenExpired(String username) {
    	PingTokenInfo info = userTokens.get(username.toLowerCase());
    	if(info == null) {
    		return true;
    	}
    	
    	long curr = System.currentTimeMillis()/1000;
    	if(curr > info.tokenExpiryInSec) {
    		return true;
    	}
    	
    	return false;
    }
    
    public	static String refreshAccessToken(DataControllerRequest dcRequest, String username) throws HttpCallException {
    	Map<String, Object> tempParam = new HashMap<>();
    	tempParam.put("refresh_token", getRefreshToken(username.toLowerCase()));
    	String url = "/services/SbgPingAuthJson/refreshAccessToken";
    	
    	Map<String, String> svcHeaders = new HashMap<String, String>();
    	Result result1 = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, tempParam, svcHeaders, url);
    	logger.debug("AuthCacheManager : refreshAccessToken === result1: "+ResultToJSON.convert(result1));
    	
    	String refresh_token 	= result1.getParamValueByName("refresh_token");
    	String access_token 	= result1.getParamValueByName("access_token");
    	String expires_in 		= result1.getParamValueByName("expires_in");
    	logger.debug("AuthCacheManager : refreshAccessToken === refresh_token: "+refresh_token);
    	
    	storeToken(username.toLowerCase(), refresh_token, access_token, expires_in);
    	
    	return access_token;
    }
}


