package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.kmsinvoke.businessdelegate.impl.SendEmailBusinessDelegateImpl;
import com.kony.kmsinvoke.util.HelperMethods;
import com.kony.kmsinvoke.util.KmsInvokeEnum;
import com.kony.sbg.util.SBGCommonUtils;

public class SendEmailBusinessDelegateImplExtn extends SendEmailBusinessDelegateImpl {
	private static final Logger LOG = LogManager.getLogger(SendEmailBusinessDelegateImplExtn.class);

	public JsonObject sendEmail(JsonObject inputparamsObj) {
		LOG.debug("## send mail class");
		JsonObject resultObj = new JsonObject();
		if (!HelperMethods.isValidInputParams(inputparamsObj, "emailServiceRequest"))
			return HelperMethods.returnResultJSonObject(false, KmsInvokeEnum.ERROR_EMAILEXCEPTION);
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
			String url = HelperMethods.getConfigProperty("DBX_CMS_EMAIL");
			String sendermailId = HelperMethods.getConfigProperty("CMS_EMAIL_SENDER_ADDRESS");
			String senderId = HelperMethods.getConfigProperty("CMS_SMS_SENDERID");
			String sourceCountry = HelperMethods.getConfigProperty("CMS_SOURCE_COUNTRY");
			// String destinationAddress = HelperMethods.getConfigProperty("");

			LOG.debug("## send email==new CMS url" + url);
			JsonObject inputJson = inputparamsObj.get("emailServiceRequest").getAsJsonObject();
			JsonObject emailsJson = inputJson.get("emails").getAsJsonObject();
			JsonObject emailJson = emailsJson.get("email").getAsJsonObject();
			String content = emailJson.get("content").toString();
			Map<String, Object> inputparams = new HashMap<>();
			LOG.debug("## send email==contentOriginal" + content);
				//LOG.debug("## send email====sign in====if" + content.contains("<div>"));
			//	String contents[] = content.split("<div>");
			//	LOG.debug("## send email====contents====if" +contents[0]);
			//	inputparams.put("SignInMsg",contents[0].substring(1,contents[0].length()-1).trim());
			
             content = content.replaceAll("\\<.*?>", "") ; //Removing HTML tags(like <div>,<br><class> tags) in Content
             LOG.debug("## send email==contentafterReplaceAll" + content);
				String contents[] = content.split("Kind");
				LOG.debug("## send email==content[0]" + contents[0] +"## send email==lentgh"+ contents[0].length());
				LOG.debug("## send email==content" + contents[0].substring(1,contents[0].length()).trim() + "## send email==contentlentgh"+
						contents[0].substring(1,contents[0].length()).trim().length() );
				String text =(contents[0].substring(1,contents[0].length())).trim();
				LOG.debug("## send email==text"+text +"text length" +text.replaceAll("[^\\n\\r\\t\\p{Print}]", "").length() );
				inputparams.put("SignInMsg",text.replaceAll("[^\\n\\r\\t\\p{Print}]", ""));// Remove non-ASCII non-printable characters from a String
				inputparams.put("bank", "Standard Bank");
				inputparams.put("regards","Kind regards");
				content = "SignIn";
			String subject = emailJson.get("subject").toString();
			JsonObject recipientsJson = emailJson.get("recipients").getAsJsonObject();
			JsonArray recipientJson = recipientsJson.get("recipient").getAsJsonArray();
			JsonObject mailJsonArray = recipientJson.get(0).getAsJsonObject();
			String emailId = mailJsonArray.get("emailId").toString();
			LOG.debug("## send email==emailId.toLowerCase()" + emailId.toLowerCase());
			inputparams.put("timestamp", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			inputparams.put("traceMessageId", SBGCommonUtils.generateRandomUUID());
			inputparams.put("sourceCountry", sourceCountry);
			inputparams.put("senderAddress", sendermailId);
			inputparams.put("content", subject);
			inputparams.put("bodycontent", content);
			inputparams.put("destinationAddress",emailId.toLowerCase());
			inputparams.put("senderId", senderId);
			LOG.debug("## email==input params for mail" + inputparamsObj.get("emailServiceRequest").getAsJsonObject());
			LOG.debug("## email==input params" + inputparams.toString());
			resultObj = HelperMethods.callhttpApi(inputparams, headerparams, url);
			LOG.debug("#email===service call result" + resultObj);
			if (resultObj.has("opstatus")) {
				int opstatus = resultObj.get("opstatus").getAsInt();
				if (opstatus == 0) {
					resultObj.addProperty("referenceId", (String) SBGCommonUtils.generateRandomUUID());
					return resultObj;
				}
				
			}
			return resultObj;
		} catch (Exception e) {
			LOG.error("Exception occured in submitting mail", e);
			return HelperMethods.returnResultJSonObject(false, KmsInvokeEnum.ERROR_EMAILEXCEPTION);
		}
	}
}
