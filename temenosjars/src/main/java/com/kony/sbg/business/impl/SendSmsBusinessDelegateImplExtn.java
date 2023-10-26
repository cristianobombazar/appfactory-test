package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.kmsinvoke.businessdelegate.impl.SendSmsBusinessDelegateImpl;
import com.kony.kmsinvoke.util.HelperMethods;
import com.kony.kmsinvoke.util.KmsInvokeEnum;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.dataobject.Result;

public class SendSmsBusinessDelegateImplExtn extends SendSmsBusinessDelegateImpl{
	private static final Logger LOG = LogManager.getLogger(SendSmsBusinessDelegateImplExtn.class);
	  
	public JsonObject sendSms(JsonObject inputparamsObj) {
		LOG.error("## send sms class");
		JsonObject resultObj = new JsonObject();
		if (!HelperMethods.isValidInputParams(inputparamsObj, "smsServiceRequest"))
			return HelperMethods.returnResultJSonObject(false, KmsInvokeEnum.ERROR_SMSEXCEPTION);
		String apiKey = null;
		try {
			apiKey = HelperMethods.getConfigProperty("DBX_KMS_API_KEY");
		} catch (Exception e) {
			LOG.error(e);
		}
		if (StringUtils.isBlank(apiKey))
			return HelperMethods.returnResultJSonObject(false, KmsInvokeEnum.ERROR_APIKEY);
		Map<String, String> headerparams = new HashMap<>();
		// headerparams.put("X-Kony-App-API-Key", apiKey);
		// headerparams.put("Content-Type", "application/json");
		try {
			String url = HelperMethods.getConfigProperty("DBX_CMS_SMS");
			//String mobilenumber = HelperMethods.getConfigProperty("CMS_SMS_MOBILE_NUMBER");
			String senderId = HelperMethods.getConfigProperty("CMS_SMS_SENDERID");
			String sourceCountry = HelperMethods.getConfigProperty("CMS_SOURCE_COUNTRY");
			LOG.debug("## senderId" + senderId);
			LOG.debug("## sourceCountry" + senderId);
			JsonObject inputJson = inputparamsObj.get("smsServiceRequest").getAsJsonObject();
			JsonObject messagesJson = inputJson.get("messages").getAsJsonObject();
			JsonObject messageJson = messagesJson.get("message").getAsJsonObject();
			String content = messageJson.get("content").toString();
			JsonObject recipientsJson = messageJson.get("recipients").getAsJsonObject();
			JsonArray recipientJson = recipientsJson.get("recipient").getAsJsonArray();
			JsonObject mailJsonArray = recipientJson.get(0).getAsJsonObject();
			String mobile = mailJsonArray.get("mobile").toString();
			
			String mobileNumber=mobile.replace("+","");
			LOG.debug("## mobile number" + mobileNumber);
			//LOG.debug("## mobile number" + mobileNumber.replace("+","").substring(mobileNumber.length()-1));
			Map<String, Object> inputparams = new HashMap<>();
			Map<String, String> svcHeaders = new HashMap<>();
			inputparams.put("timestamp", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			inputparams.put("traceMessageId", SBGCommonUtils.generateRandomUUID());
			inputparams.put("content", content);
			inputparams.put("value", mobileNumber);
			inputparams.put("sourceCountry", sourceCountry);
			inputparams.put("senderId", senderId);
			LOG.debug("#sendsms request params" + inputparams.toString());
			resultObj = HelperMethods.callhttpApi(inputparams, svcHeaders, url);
			LOG.debug("#sendsms resultObj" + resultObj.toString());
			if (resultObj.has("opstatus")) {
				int opstatus = resultObj.get("opstatus").getAsInt();
				if (opstatus == 0) {
					resultObj.addProperty("referenceId", (String) SBGCommonUtils.generateRandomUUID());
					return resultObj;
				}
				
			}
			return resultObj;
		} catch (Exception e) {
			LOG.error("Exception occured in submitting sms", e);
			return HelperMethods.returnResultJSonObject(false, KmsInvokeEnum.ERROR_SMSEXCEPTION);
		}
	}
}
