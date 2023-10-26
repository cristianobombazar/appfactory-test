package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.dto.PingTokenDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class PingTokenCacheHelper {

	private static final Logger logger = LogManager.getLogger(PingTokenCacheHelper.class);
	
	//Stores the data in DB
    public	static void storeToken(DataControllerRequest request, PingTokenDTO dto) throws Exception {
    	logger.debug("PingTokenCacheHelper.storeToken ===> START dto::: "+dto);
    	
		long currTime			= System.currentTimeMillis()/1000;
		long expiresIn 			= Long.parseLong(dto.getExpiresin());
		long actualExpiryInSec	= currTime + expiresIn - 30;
		logger.debug("PingTokenCacheHelper.storeToken ===> START expiresIn: "+expiresIn+"; currTime: "+currTime+"; actualExpiryInSec: "+actualExpiryInSec);
		
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
    	
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		svcParams.put("username", dto.getUsername().toLowerCase());
		svcParams.put("accesstoken", dto.getAccesstoken());
		svcParams.put("refreshtoken", dto.getRefreshtoken());
		svcParams.put("expiresin", actualExpiryInSec);
    	
		PingTokenDTO tempDto = getPingTokenData(request, dto);
		if(tempDto == null) {
			logger.debug("PingTokenCacheHelper.storeToken ===> Inserting the record ");
	    	Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_PINGTOKEN_CREATE, false);
	    	logger.debug("PingTokenCacheHelper.storeToken ===> Insert result: "+ResultToJSON.convert(result));
		} else {
			logger.debug("PingTokenCacheHelper.storeToken ===> Updating the record ");
	    	Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,SbgURLConstants.SERVICE_SBGCRUD,
					SbgURLConstants.OPERATION_DBXDB_SBG_PINGTOKEN_UPDATE, false);
	    	logger.debug("PingTokenCacheHelper.storeToken ===> Update result: "+ResultToJSON.convert(result));
		}
    }
    
    public	static String getValidPingToken(DataControllerRequest request, PingTokenDTO dto) throws Exception {
    	
    	if(request.getSession() == null || request.getSession().getAttribute(dto.getUsername().toLowerCase()) == null) {
    		logger.debug("PingTokenCacheHelper.getValidPingToken ===> session is null or dto is not in session: "+request.getSession());
    		//fetch data from DB
    		PingTokenDTO tempDto = getPingTokenData(request, dto);
    		if(tempDto == null) {
    			logger.error("PingTokenCacheHelper.getValidPingToken ===> Only possible scenario when storing failed during login ");
    			return null;
    		}
    		if(isTokenExpired(tempDto)) {
    			logger.debug("PingTokenCacheHelper.getValidPingToken ===> 1 Token Expired. Refreshing Ping ");
    			return refreshAccessToken(request, tempDto);
    		} else {
    			logger.debug("PingTokenCacheHelper.getValidPingToken ===> 1 Token is Valid ");
    			return tempDto.getAccesstoken();
    		}
    	} else {
    		logger.debug("PingTokenCacheHelper.getValidPingToken ===> data found in session ");
    		PingTokenDTO tempDto = (PingTokenDTO)request.getSession().getAttribute(dto.getUsername().toLowerCase());
    		logger.debug("PingTokenCacheHelper.getValidPingToken ===> tempDto: "+tempDto);
    		if(isTokenExpired(tempDto)) {
    			logger.debug("PingTokenCacheHelper.getValidPingToken ===> 2 Token Expired. Refreshing Ping ");
    			return refreshAccessToken(request, tempDto);
    		} else {
    			logger.debug("PingTokenCacheHelper.getValidPingToken ===> 2 Token is Valid ");
    			return tempDto.getAccesstoken();
    		}
    	}
    }
    
    private	static boolean	isTokenExpired(PingTokenDTO dto) {
    	long currTime = System.currentTimeMillis()/1000;
    	
    	long pingExpiryTime = 0;
    	pingExpiryTime = Long.parseLong(dto.getExpiresin());
    	
    	logger.debug("PingTokenCacheHelper.isTokenExpired ===> currTime: "+currTime+"; pingExpiryTime: "+pingExpiryTime);
    	if(currTime > pingExpiryTime) {
    		return true;
    	}
    	
    	return false;
   	
    }

    private	static PingTokenDTO getPingTokenData(DataControllerRequest request, PingTokenDTO dto) throws Exception {
    	
    	HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();

		String filter = SBGCommonUtils.buildOdataCondition("username", " eq ", dto.getUsername().toLowerCase());
    	svcParams.put("$filter", filter);
    	logger.debug("PingTokenCacheHelper.getPingTokenData ===> filter: "+filter);
    	
    	Result result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders,SbgURLConstants.SERVICE_SBGCRUD,
				SbgURLConstants.OPERATION_DBXDB_SBG_PINGTOKEN_GET, false);
    	logger.debug("PingTokenCacheHelper.getPingTokenData ===> result: "+ResultToJSON.convert(result));
    	
    	//result to be verified
    	
    	Dataset ds = result.getDatasetById("SbgPingTokens");
    	if(ds != null && ds.getRecord(0) != null) {
    		Record record = ds.getRecord(0);
        	PingTokenDTO dtoNew 	= new PingTokenDTO();
        	dtoNew.setUsername(record.getParamValueByName("username"));
        	dtoNew.setRefreshtoken(record.getParamValueByName("refreshtoken"));
        	dtoNew.setAccesstoken(record.getParamValueByName("accesstoken"));
        	dtoNew.setExpiresin(record.getParamValueByName("expiresin"));
        	logger.debug("PingTokenCacheHelper.getPingTokenData ===> dtoNew: "+dtoNew);
        	return dtoNew;
    	} else {
    		logger.error("PingTokenCacheHelper.getPingTokenData ===> Dataset returned from DB is invalid ");
    	}
    	return null;
    }

    private	static String refreshAccessToken(DataControllerRequest dcRequest, PingTokenDTO dto) throws Exception {
    	Map<String, Object> tempParam = new HashMap<>();
    	tempParam.put("refresh_token", dto.getRefreshtoken());
    	String url = "/services/SbgPingAuthJson/refreshAccessToken";
    	
    	Map<String, String> svcHeaders = new HashMap<String, String>();
    	Result result1 = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, tempParam, svcHeaders, url);
    	logger.debug("PingTokenCacheHelper.refreshAccessToken ===> result: "+ResultToJSON.convert(result1));
    	
    	String refresh_token 	= result1.getParamValueByName("refresh_token");
    	String access_token 	= result1.getParamValueByName("access_token");
    	String expires_in 		= result1.getParamValueByName("expires_in");
    	
    	PingTokenDTO dtoNew 	= new PingTokenDTO();
    	dtoNew.setUsername(dto.getUsername());
    	dtoNew.setRefreshtoken(refresh_token);
    	dtoNew.setAccesstoken(access_token);
    	dtoNew.setExpiresin(expires_in);
    	logger.debug("PingTokenCacheHelper.refreshAccessToken ===> dtoNew: "+dtoNew);
    	
    	dcRequest.getSession().setAttribute(dto.getUsername(), dtoNew);
    	logger.debug("PingTokenCacheHelper.refreshAccessToken ===> data updated in session");
    	
    	storeToken(dcRequest, dtoNew);
    	logger.debug("PingTokenCacheHelper.refreshAccessToken ===> data updated in DB");
    	
    	return access_token;
    }
}
