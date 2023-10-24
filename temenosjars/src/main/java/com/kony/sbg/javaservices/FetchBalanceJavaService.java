package com.kony.sbg.javaservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FetchBalanceJavaService implements JavaService2 {

	private static final Logger LOG = Logger.getLogger(FetchBalanceJavaService.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception 
	{
		Result result = new Result();

		Map<String, String> javaInputParams = HelperMethods.getInputParamMap(inputArray);
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> inputParams = new HashMap<String, Object>();
		String req=getRequestPayload(javaInputParams);
		inputParams.put("accountsArray", req);
		inputParams.put("timeStamp",SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		inputParams.put("traceMsgId", SBGCommonUtils.generateRandomUUID());
		inputParams.put("enterpriseUUID", SBGCommonUtils.generateRandomUUID());
		inputParams.put("senderId", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_SENDER_ID, request));
		inputParams.put("sourceSystem", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_SOURCE_SYSTEM, request));
		inputParams.put("sourceApplication",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_APPLICATION, request));
		inputParams.put("applicationSessionId", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_APPLICATION_SESSION_ID, request));
		inputParams.put("sourceLocation", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_SOURCE_LOCATION, request));
		inputParams.put("version",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_VERSION, request));
		String Url=SbgURLConstants.URL_GET_BALANCE;

		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(request, inputParams, svcHeaders, Url);

		}catch(Exception e) {
			LOG.error("Service call fails");
			return SbgErrorCodeEnum.ERR_100014.setErrorCode(result);
		}
		LOG.error("JSON service Result :"+result);
		return result;
	}

	public String getRequestPayload(Map<String, String> javaInputParams) {

		LOG.error("getRequestPayload ===> INPUTS :::: ACCOUNT_NUMBERS:"+javaInputParams.get(SbgURLConstants.ACCOUNT_NUMBERS));
		LOG.error("getRequestPayload ===> INPUTS :::: CB_SID:"+javaInputParams.get(SbgURLConstants.CB_SID));
		LOG.error("getRequestPayload ===> INPUTS :::: BIC:"+javaInputParams.get(SbgURLConstants.BIC));
		LOG.error("getRequestPayload ===> INPUTS :::: CURRENCY:"+javaInputParams.get(SbgURLConstants.CURRENCY));
		
		String accounts = StringUtils.isNotBlank(javaInputParams.get(SbgURLConstants.ACCOUNT_NUMBERS)) ? 
				javaInputParams.get(SbgURLConstants.ACCOUNT_NUMBERS) : "";
		
		LOG.error("getRequestPayload ===>  accounts:"+accounts);
		String[] accountsList=accounts.split(",");
		LOG.error("getRequestPayload ===>  accountsList:"+accountsList);
		
		String cbsids = StringUtils.isNotBlank(javaInputParams.get(SbgURLConstants.CB_SID)) ? 
				javaInputParams.get(SbgURLConstants.CB_SID) : "";
		String[] sids=cbsids.split(",");
		LOG.error("getRequestPayload ===>  sids:"+sids);
		
		String bics = StringUtils.isNotBlank(javaInputParams.get(SbgURLConstants.BIC)) ? 
				javaInputParams.get(SbgURLConstants.BIC) : "";
		String[] bicList=bics.split(",");
		LOG.error("getRequestPayload ===>  bicList:"+bicList);
		
		String currency = StringUtils.isNotBlank(javaInputParams.get(SbgURLConstants.CURRENCY)) ? 
				javaInputParams.get(SbgURLConstants.CURRENCY) : "";
		String[] currencyList=currency.split(",");
		LOG.error("getRequestPayload ===>  currency:"+currency);
		
		//ArrayList<String> balanceTypes = new ArrayList<>();//["0001"];		
		//balanceTypes.add('0001');
		//JSONObject mainObj=new JSONObject();
		JSONArray jsonArray =new JSONArray();
		List<JSONObject> accList=new ArrayList<>();
		
		if(accountsList!=null && cbsids!= null && bicList!=null && currencyList!=null) {
			LOG.error("getRequestPayload ===>  length:"+accountsList.length);
			
			for(int i=0;i<accountsList.length;i++) {
				JSONObject accObj=new JSONObject();	
				JSONObject mainObj=new JSONObject();
				
				accObj.put(SbgURLConstants.ACCOUNT_NUMBER,accountsList[i]);
				accObj.put(SbgURLConstants.FETCHBALANCE_ACC_SID,sids[i]);		
				accObj.put(SbgURLConstants.FETCHBALANCE_SWIFTCODE,bicList[i]);
				accObj.put(SbgURLConstants.CURRENCY,currencyList[i]);
				
				mainObj.put(SbgURLConstants.ACCOUNT_IDENTIFICATION, accObj);
				mainObj.put(SbgURLConstants.BALANCE_TYPES, "['1']");
				
				accList.add(mainObj);
				jsonArray.put(mainObj);
				
				LOG.error("getRequestPayload ===>  jsonArray:"+jsonArray);

			}
			LOG.error("@@ ACCOUNTS ARRAY :"+jsonArray);
		}
		return jsonArray.toString();
	}
}
