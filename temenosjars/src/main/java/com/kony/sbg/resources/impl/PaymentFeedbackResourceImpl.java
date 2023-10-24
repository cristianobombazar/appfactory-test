package com.kony.sbg.resources.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.kony.sbg.util.*;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.auth0.jwt.JWT;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.infinity.dbx.temenos.transactions.TransactionConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.adminconsole.exception.DBPAuthenticationException;
import com.kony.adminconsole.utilities.DBPServices;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.PaymentFeedbackBackendDelegate;
import com.kony.sbg.business.api.PaymentFeedbackBusinessDelegate;
import com.kony.sbg.helpers.SbgUpdateTransferHelper;
import com.kony.sbg.resources.api.PaymentFeedbackResource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.RequestHistoryDTO;
import com.temenos.dbx.product.commonsutils.AuditHelperMethods;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class PaymentFeedbackResourceImpl implements PaymentFeedbackResource {

	private static final Logger LOG = Logger.getLogger(PaymentFeedbackResourceImpl.class);

	PaymentFeedbackBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(PaymentFeedbackBackendDelegate.class);

	@Override
	public Result processPaymentFeedback(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException {
		LOG.debug("Enty --> PaymentFeedbackResourceImpl::processPaymentFeedback");
		Result result = new Result();
		JSONObject requestPayload = new JSONObject();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

		if (StringUtils.isNotBlank(inputParams.get("data")) == false) {
			LOG.error("PaymentFeedbackResourceImpl::processPaymentFeedback --> data is empty/null");
			result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
			result.addOpstatusParam(SbgErrorCodeEnum.ERR_100010.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		} else {
			LOG.error(
					"PaymentFeedbackResourceImpl::processPaymentFeedback --> request data: " + inputParams.get("data"));
		}

		// PING Token Validation
		Boolean validateToken = ValidateToken(inputParams, dcRequest, dcResponse);
		if (!validateToken) {
			LOG.debug("PaymentFeedbackResourceImpl::processPaymentFeedback::TokenIsInvalid::" + validateToken);
			result = SbgErrorCodeEnum.ERR_100034.setErrorCode(result);
			result.addOpstatusParam(SbgErrorCodeEnum.ERR_100034.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		}

		result = decodeAndParseXML(inputParams.get("data"), result);
		if (result.hasParamByName("dbpErrCode")) {
			result.addOpstatusParam(result.getParamValueByName("dbpErrCode"));
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;
		}

		if (!StringUtils.isNotBlank(result.getParamValueByName("orgnlPmtInfId"))
				|| !StringUtils.isNotBlank(result.getParamValueByName("txSts"))) {
			LOG.error("Mandatory field values in XML are empty/null");
			result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
			result.addOpstatusParam(SbgErrorCodeEnum.ERR_100010.getErrorCode());
			result.addParam("status", "failed");
			result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
			return result;

		}

		String tranxId = result.getParamValueByName("orgnlPmtInfId");
		String volPayStatus = result.getParamValueByName("txSts");
		String Errorcode = result.getParamValueByName("reasonCode");
		String tranxStatus = volPayStatus;

		LOG.debug("Enty --> PaymentFeedbackResourceImpl::processPaymentFeedback1" + "tranxId" + tranxId + "volPayStatus"
				+ volPayStatus + "Errorcode" + Errorcode);

		requestPayload.put("orgnlPmtInfId", tranxId);
		requestPayload.put("txSts", volPayStatus);
		requestPayload.put("reasonCode", result.getParamValueByName("reasonCode"));
		requestPayload.put("encodedData", inputParams.get("data"));
		LOG.debug("PaymentFeedbackResourceImpl::processPaymentFeedback::requestPayload: " + requestPayload.toString());
		PaymentFeedbackBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(PaymentFeedbackBusinessDelegate.class);
		result = businessDelegate.processPaymentFeedback(requestPayload, dcRequest);
		String featureActionId = result.getParamValueByName("featureActionId");
		LOG.debug("PaymentFeedbackResourceImpl::processPaymentFeedback::Result: " + ResultToJSON.convert(result));

		if ("RJCT".equals(volPayStatus)) {
			// tranxStatus = TransactionStatusEnum.REJECTED.getStatus();
			tranxStatus = TransactionStatusEnum.FAILED.getStatus();
		} else if ("ACSC".equals(volPayStatus)) {
			tranxStatus = "Successfully processed";
		} else if ("PDNG".equals(volPayStatus) || "ACCP".equals(volPayStatus)) {
			tranxStatus = "Pending processing";
		} else if ("ACSP".equals(volPayStatus)) {
			tranxStatus = "Processing";
		} else if ("ACWP".equals(volPayStatus)) {
			tranxStatus = "Warehoused";
		}

		inputParams.put("tranxId", tranxId);
		inputParams.put("tranxStatus", tranxStatus);
		inputParams.put("volPayStatus", volPayStatus);
		LOG.debug("PaymentFeedbackResourceImpl.processPaymentFeedback() ---> Updating transaction table for id: "
				+ result.getParamValueByName("orgnlPmtInfId") + "; status: " + result.getParamValueByName("txSts"));
		new SbgUpdateTransferHelper().invoke(methodID, inputArray, dcRequest, dcResponse);
		LOG.debug("PaymentFeedbackResourceImpl:processPaymentFeedback():volPayStatus" + volPayStatus + "tranxId"
				+ tranxId + "Errorcode" + Errorcode);
		
		/*Calling pushAlerts Events for created and approver 
		 * when payment is submitted in payment proxy
		 */
		if ("ACSC".equals(volPayStatus)) {
			// pop volpay alerts
			sendApprovalAlerts(dcRequest,  getTransactionData(tranxId, dcRequest), tranxId, tranxStatus);
			this.PushAlertsForSBGPayment(result, requestPayload, dcRequest, dcResponse, featureActionId);
		}

		LOG.debug("PaymentFeedbackResourceImpl.processPaymentFeedback() --->END");
		return result;
	}

	private void sendApprovalAlerts(DataControllerRequest dcRequest, Result result, String tranxId, String tranxStatus) {
		try {
			String proofOfPayment = SbgAlertUtil.getProofOfPayment(tranxId, dcRequest);
			// email
			SbgAlertUtil.sendPopEmail(dcRequest, proofOfPayment, result);
			// sms
			String message = "SBG Transaction Approval Alert | Transaction Id " + tranxId;
			SbgAlertUtil.sendPopSMS(dcRequest, proofOfPayment, SbgAlertUtil.getPopSmsDataObject(tranxStatus, message));
		} catch (Exception e) {
			LOG.error("Volpay approval POP Alert error " + e.getMessage());
		}
	}


	private Result getTransactionData(String transactionid, DataControllerRequest dcRequest) {
		String filter = "referenceId" + DBPUtilitiesConstants.EQUAL + transactionid;
		try {
			Result approvalAndRequestDetails = getApprovalAndRequestDetails(filter, dcRequest);
			return approvalAndRequestDetails;
		} catch (Exception e) {
			LOG.error("Error when get transaction data:", e);
		}

		return new Result();
	}

	public Result getApprovalAndRequestDetails(String filter, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKTRANSFER_VIEW_GET;

		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		//String filter = "";
		svcParams.put("$filter", filter);
		try {
			if (!SBGCommonUtils.isStringEmpty(filter)) {
				Result resultValue = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, serviceName,
						operationName, false);
				//if (null != resultValue) {
					Record record = resultValue.getDatasetById("interbanktransfer_view").getRecord(0);
					if (null != record) {
						record.getAllParams().forEach(p -> result.addParam(p));
					}
				//}

			}

		} catch (Exception e) {
			LOG.error("Failed to fetch details from interbanktransfer_view table:", e);
			return null;
		}
		return result;
	}

	// Validate Ping Token SBG-306
	public Boolean ValidateToken(Map<String, String> inputParams, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		try {
			
			String header = SBGCommonUtils.getServerPropertyValue("PING_AUTHORIZATION", dcRequest);
			String authToken = dcRequest.getHeader(header);
			//String authToken = dcRequest.getHeader("authToken");
			LOG.debug("PaymentFeedbackResourceImpl::ValidateToken::authToken" + authToken);
			if (StringUtils.isNotEmpty(authToken)) {
				if (authToken.contains("Bearer")) {
					authToken = authToken.split(" ")[1];
				}

				Date exp = JWT.decode(authToken).getExpiresAt();
				if (!SBGCommonUtils.timeStampCompare(exp)) {
					return false;
				}

				Boolean isTokenValid = true;
				String endPoint = "/ext/sbg/customer/oauth/jwks";
				isTokenValid = PingTokenValidator.isValidToken(authToken, dcRequest, endPoint);
				return isTokenValid;
			}
			LOG.debug("PaymentFeedbackResourceImpl::processPaymentFeedback::authToken:::empty");
			return false;
		} catch (Exception e) {
			LOG.debug("PaymentFeedbackResourceImpl::processPaymentFeedback::TokenIsInvalid" + e.getMessage());
			return false;
		}
	}

	private Result decodeAndParseXML(String data, Result result) {
		String infinityPaymentId = "", paymentStatus = "", reasonCode = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			byte[] decodedBytes = Base64.getDecoder().decode(data);
			String decodedBase64 = new String(decodedBytes);
			StringBuilder xmlStringBuilder = new StringBuilder(decodedBase64);
			ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
			document = builder.parse(input);
		} catch (ParserConfigurationException e) {
			LOG.error("ParserConfigurationException occured while parsing XML: " + e.getStackTrace());
			return SbgErrorCodeEnum.ERR_100030.setErrorCode(result);
		} catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException occured while parsing XML: " + e.getStackTrace());
			return SbgErrorCodeEnum.ERR_100030.setErrorCode(result);
		} catch (IOException e) {
			LOG.error("IOException occured while parsing XML: " + e.getStackTrace());
			return SbgErrorCodeEnum.ERR_100030.setErrorCode(result);
		} catch (SAXException e) {
			LOG.error("SAXException occured while parsing XML: " + e.getStackTrace());
			return SbgErrorCodeEnum.ERR_100030.setErrorCode(result);
		}
		Element rootElement = document.getDocumentElement();
		// NodeList orgnlPmtInfIdNodes =
		// rootElement.getElementsByTagName("OrgnlPmtInfId");
		NodeList orgnlPmtInfIdNodes = rootElement.getElementsByTagName("OrgnlInstrId");
		LOG.debug("decodeAndParseXML::Length of Node OrgnlPmtInfAndSts: " + orgnlPmtInfIdNodes.getLength());
		if (orgnlPmtInfIdNodes.getLength() != 0) {
			Node orgnlPmtInfIdItemNode = orgnlPmtInfIdNodes.item(0);
			Element orgnlPmtInfIdElement = (Element) orgnlPmtInfIdItemNode;
			infinityPaymentId = orgnlPmtInfIdElement.getTextContent();
		} else {
			LOG.error("decodeAndParseXML::OrgnlInstrId not found");
		}

		NodeList txStsNodes = rootElement.getElementsByTagName("TxSts");
		LOG.debug("decodeAndParseXML::Length of Node TxSts: " + txStsNodes.getLength());
		if (txStsNodes.getLength() != 0) {
			Node txStsItemNode = txStsNodes.item(0);
			Element txStsItemElement = (Element) txStsItemNode;
			paymentStatus = txStsItemElement.getTextContent();
		} else {
			LOG.error("decodeAndParseXML::TxSts not found");
		}

		NodeList cdNodes = rootElement.getElementsByTagName("Cd");
		LOG.debug("decodeAndParseXML::Length of Node Cd: " + cdNodes.getLength());
		if (cdNodes.getLength() != 0) {
			Node cdItemNode = cdNodes.item(0);
			Element cdItemElement = (Element) cdItemNode;
			reasonCode = cdItemElement.getTextContent();
		} else {
			LOG.error("decodeAndParseXML::Reason Cd not found");
		}

		LOG.debug("orgnlPmtInfId: " + infinityPaymentId + " txSts: " + paymentStatus + " reasonCode: " + reasonCode);
		result.addParam("orgnlPmtInfId", infinityPaymentId);
		result.addParam("txSts", paymentStatus);
		result.addParam("reasonCode", reasonCode);
		return result;
	}

	private void PushAlertsForSBGPayment(Result result, JSONObject requestPayload, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse, String featureActionId) throws ApplicationException {
		try {

			try {
				String dbpServicesClaimsToken = DBPServices.getDBPServicesClaimsToken(dcRequest);
				dcRequest.addRequestParam_("X-Kony-Authorization", dbpServicesClaimsToken);
				dcRequest.getHeaderMap().put("X-Kony-Authorization", dbpServicesClaimsToken);
				LOG.debug("PushAlertsForSBGPayment::dbpServicesClaimsToken:" + dbpServicesClaimsToken);
			} catch (DBPAuthenticationException e) {
				LOG.debug("PushAlertsForSBGPayment::DBPAuthenticationException" + e);
				e.printStackTrace();
			}
			JsonObject customParams = new JsonObject();

			if(featureActionId.equalsIgnoreCase(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE)){
				customParams = this.getInterBankFundTransferData(requestPayload, dcRequest);
			} else if(featureActionId.equalsIgnoreCase(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE)){
				customParams = this.getInternationFundTransferData(requestPayload, dcRequest);
			} else if(featureActionId.equalsIgnoreCase(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)){
				customParams = this.getOWnAccountFundTransferData(requestPayload, dcRequest);
			}
			//fetch  Payment beneficiary Data form InternationFundTransferData
			
			LOG.debug("PushAlertsForSBGPayment::customParams" + customParams.toString());
			Set<String> customerId = new HashSet<String>();
			String requestId = customParams.get("requestId").getAsString();
			LOG.debug("PushAlertsForSBGPayment::requestId" + requestId);
			String createdBy = customParams.get("createdBy").getAsString();
			customerId.add(createdBy);
			if (StringUtils.isNotBlank(requestId)) {
				fetchApprovers(dcRequest, dcResponse, requestId, customerId,createdBy);
			} 
			LOG.debug("PushAlertsForSBGPayment::customerId" + customerId);

			String txtStatus = requestPayload.getString("txSts");
			String eventType = "MAKE_TRANSFER";
			String eventSubType = "SBG_PAYMENTFEEDBACK_FAILED_INITIATOR";
			if ("ACSC".equals(txtStatus)) {
				eventSubType = "SBG_PAYMENTFEEDBACK_SUCESSS_INITIATOR";
			}
			String producer = "SbgPayments/POST(feedback)";
			String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", dcRequest);
			LOG.debug("PushAlertsForSBGPayment --->Start:enableEvents" + enableEvents);
			dcRequest.addRequestParam_("appID", "SBGPaymentFeedback");
			if (enableEvents != null && enableEvents.equalsIgnoreCase("true")) {
				for (String id : customerId) {
					pushEvent(dcRequest, dcResponse, eventType, eventSubType, producer, Constants.SID_EVENT_SUCCESS, id,
							customParams);
				}
			}
		} catch (Exception e2) {
			LOG.debug("Exception Occured while invoking AuditHelperMethods" +e2);
		}

	}
/* Getting Payment Beneficary data by 
 * passing confiramtion number as params*/
	private JsonObject getInternationFundTransferData(JSONObject requestPayload, DataControllerRequest dcRequest) {
		LOG.debug("getInternationFundTransferData->>>");
		String filter = "";
		JsonObject customparams = new JsonObject();
		HashMap<String, Object> fundRequestParameters = new HashMap<>();
		HashMap<String, Object> fundRequestHeaders = new HashMap<>();
		filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + requestPayload.getString("orgnlPmtInfId");
		fundRequestParameters.put(SbgURLConstants.FILTER, filter);
		JSONObject internationalFundTransfersData = backendDelegate.getInternationalFundTransfers(fundRequestParameters,
				fundRequestHeaders);
		LOG.debug("getInternationFundTransferData:TransferData" + internationalFundTransfersData);
		if (internationalFundTransfersData != null && internationalFundTransfersData.has("internationalfundtransfers")
				&& internationalFundTransfersData.getJSONArray("internationalfundtransfers").length() > 0) {
			JSONArray internationalfundtransfers = internationalFundTransfersData
					.getJSONArray("internationalfundtransfers");
			JSONObject fundTransfers = internationalfundtransfers.getJSONObject(0);
			LOG.debug("getInternationFundTransferData:fundTransfers" + fundTransfers);
			String schedulateDt = fundTransfers.optString("scheduledDate");
			String formattedDate = null;
			try {
				formattedDate = SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss",
						SbgURLConstants.PATTERN_YYYY_MM_DD, schedulateDt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String beneficiaryName = fundTransfers.optString("beneficiaryName");
			String transactionCurrency = fundTransfers.optString("transactionCurrency");
		//	String amount = fundTransfers.optString("amount");
			String amount = fundTransfers.optString("transactionAmount");
			// String scheduledDate = formatter.parse(formattedDate);
			String toAccountNumber = fundTransfers.optString("toAccountNumber");
			String referenceNumber = fundTransfers.optString("confirmationNumber");
			String createdBy = fundTransfers.optString("createdby");
			String requestId = fundTransfers.optString("requestId");
			LOG.debug("getInternationFundTransferData:FetchData" + "beneficiaryName" + beneficiaryName
					+ "transactionCurrency" + transactionCurrency + "amount" + amount+ "toAccountNumber" + toAccountNumber
					+ "referenceNumber" + referenceNumber + "createdBy" + createdBy + "requestId" + requestId);
			customparams.addProperty("beneficiaryName", beneficiaryName);
			customparams.addProperty("transactionCurrency", transactionCurrency);
			customparams.addProperty("amount", getTwoDecimalString(Double.parseDouble(amount)));
			customparams.addProperty("ScheduledDate", formattedDate);
			customparams.addProperty("MaskedToAccount",getMaskedValue(toAccountNumber));
			customparams.addProperty("ReferenceID", referenceNumber);
			customparams.addProperty("createdBy", createdBy);
			customparams.addProperty("requestId", requestId);
			String txSts = requestPayload.getString("txSts");
			if ("RJCT".equals(txSts)) {
				getSystemConfigurationData(customparams, requestPayload, dcRequest);
			}

		}
		return customparams;
	}
	
	private static String getTwoDecimalString(double amount) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(amount);
	}

	/*Getting SystemConfiguration SBGPayment Value as property 
	 name of DBP bundle of Error code Message for InApp,Email,SMS*/
	private void getSystemConfigurationData(JsonObject customparams, JSONObject requestPayload,
			DataControllerRequest dcRequest) {

		try {
			JSONObject bundleConfig = TemenosUtils.getBundleConfigurations(TransactionConstants.DBP_BUNDLE,
					"SBG_PAYMENT_VALUES", dcRequest);
			String reasonCode = requestPayload.getString("reasonCode");
			String overrides = "";
			LOG.debug("getSystemConfigurationData:bundleConfig" + bundleConfig);
			JSONObject OVERRIDES_MAP = new JSONObject();
			if (bundleConfig != null) {
				JSONArray configurations = bundleConfig.optJSONArray("configurations");
				if (configurations != null && configurations.length() > 0) {
					JSONObject paymentStatus = configurations.optJSONObject(0);
					if (paymentStatus.has(TransactionConstants.DBP_CONFIG_TABLE_VALUE)) {
						overrides = paymentStatus.getString(TransactionConstants.DBP_CONFIG_TABLE_VALUE);
						LOG.debug("getSystemConfigurationData:overrides" + overrides);
						OVERRIDES_MAP = new JSONObject(overrides);
						LOG.debug("getSystemConfigurationData:OVERRIDES_MAP"
								+ OVERRIDES_MAP);
						JSONObject Data = OVERRIDES_MAP.getJSONObject(reasonCode);
						LOG.debug("getSystemConfigurationData:Data" + Data);
						String InAppData = Data.getString("inApp");
						String smsData = Data.getString("sms");
						String emailData = Data.getString("email");
						LOG.debug("getSystemConfigurationData:InAppData" + InAppData
								+ "smsData" + smsData + "emailData" + emailData);
						customparams.addProperty("InAppData", InAppData);
						customparams.addProperty("smsData", smsData);
						customparams.addProperty("emailData", emailData);
					}

				}
			} else {
				customparams.addProperty("InAppData", "Payment processing failed");
				customparams.addProperty("smsData", "Payment processing failed");
				customparams.addProperty("emailData", "Payment processing failed");
			}
		} catch (Exception e) {
			LOG.debug("Exception Occured getBundleConfigurations:" + e);

		}
	}
  /* calling pushevent for triggering 
   * alerts and pushing audits logs*/
	private static Result pushEvent(DataControllerRequest dcRequest, DataControllerResponse dcResponse,
			String eventType, String eventSubType, String producer, String sidEventSuccess, String customerId,
			JsonObject customParams) {

		try {
			LOG.debug(
					"_PushEventsForSbgPayments --->pushEvent:customerId+" + customerId + "customParams" + customParams);
			String enableEvents = AuditHelperMethods.getConfigurableParameters(Constants.ENABLE_EVENTS, dcRequest);

			if (enableEvents != null && enableEvents.equalsIgnoreCase("true")) {
				if (!eventSubType.equals("")) {
					try {
						EventsDispatcher.dispatch(dcRequest, dcResponse, eventType, eventSubType, producer,
								Constants.SID_EVENT_SUCCESS, null, customerId, "RETAIL_AND_BUSINESS_BANKING",
								customParams);

					} catch (Exception e2) {
						LOG.error("Exception Occured while invoking AuditHelperMethods");
					}
				}
			}

		} catch (Exception ex) {
			LOG.debug("exception occured in pushAlert", ex);
		}
		return new Result();

	}
    /* Getting Appprovers for created 
     * payment  which has request Id*/
	public void fetchApprovers(DataControllerRequest request, DataControllerResponse response, String requestId,
			Set<String> customerId,String CreatedBy) {
		LOG.debug("fetchApprovers-->");
		@SuppressWarnings("unchecked")
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		LOG.debug("fetchApprovers::userId" + userId);
		ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
		LOG.debug("fetchApprovers::requestHistory111:requestId" + requestId +"CreatedBy"+CreatedBy);
		List<RequestHistoryDTO>  requestHistory =approvalQueueDelegate.fetchRequestHistory(requestId,CreatedBy);
		for(RequestHistoryDTO requestHistoryDTO : requestHistory) {
			LOG.debug("fetchApprovers::requestHistory111:requestHistoryDTO" +requestHistoryDTO.getAction());
			customerId.add(requestHistoryDTO.getCreatedby());
		}
		LOG.debug("fetchApprovers:approvers" + customerId);
		

	}
		public String getMaskedValue(String accountNumber) {
        String lastFourDigits;
        if (StringUtils.isNotBlank(accountNumber)) {
            if (accountNumber.length() > 4) {
                lastFourDigits = accountNumber.substring(accountNumber.length() - 4);
                accountNumber = "XXXX" + lastFourDigits;
            } else {
                accountNumber = "XXXX" + accountNumber;
            }
        }
        return accountNumber;
    }

		@Override
		public Result fetchMyAccessGetUsers(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
				DataControllerResponse dcResponse) throws ApplicationException {
			Result result = new Result();
			LOG.debug("fetchApprovers:approvers:fetchMyAccessGetUsers");
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			try {
				//Ping Token
				Boolean validateToken = ValidateToken(inputParams, dcRequest, dcResponse);
				if (!validateToken) {
					LOG.debug("PaymentFeedbackResourceImpl::fetchMyAccessGetUsers::TokenIsInvalid::" + validateToken);
					result = SbgErrorCodeEnum.ERR_100034.setErrorCode(result);
					result.addOpstatusParam(SbgErrorCodeEnum.ERR_100034.getErrorCode());
					result.addParam("status", "failed");
					result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
					return result;
				}
				
				PaymentFeedbackBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(PaymentFeedbackBusinessDelegate.class);
				result = businessDelegate.fetchMyAccessGetUser(inputParams,dcRequest);
				LOG.debug("fetchApprovers:approvers:fetchMyAccessGetUsers"+ result);
			} catch (Exception exp) {
				LOG.debug("Error in fetch quotes: " + exp.getMessage());
				return SbgErrorCodeEnum.ERR_100022.setErrorCode(result);
			}

			return result;
		}

		@Override
		public Result updateMyAccessUser(Map<String, String> inputParams, DataControllerRequest dcRequest,
				DataControllerResponse dcResponse) throws Exception {
			Result result = new Result();
			try {

				if (StringUtils.isNotBlank(inputParams.get("userId")) == false || StringUtils.isNotBlank(inputParams.get("enabled")) == false) {
					LOG.debug("PaymentFeedbackResourceImpl::updateMyAccessUser --> userId or  enabled flag is empty/null");
					result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
					result.addOpstatusParam(SbgErrorCodeEnum.ERR_100010.getErrorCode());
					result.addParam("status", "failed");
					result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
					return result;
				} else {
					LOG.debug(
							"PaymentFeedbackResourceImpl::updateMyAccessUser --> request data: " + inputParams);
				}
				//Ping Token
				Boolean validateToken = ValidateToken(inputParams, dcRequest, dcResponse);
				if (!validateToken) {
					LOG.debug("PaymentFeedbackResourceImpl::updateMyAccessUser::TokenIsInvalid::" + validateToken);
					result = SbgErrorCodeEnum.ERR_100034.setErrorCode(result);
					result.addOpstatusParam(SbgErrorCodeEnum.ERR_100034.getErrorCode());
					result.addParam("status", "failed");
					result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
					return result;
				}
				
				PaymentFeedbackBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(PaymentFeedbackBusinessDelegate.class);
				result = businessDelegate.updateMyAccessUser(inputParams,dcRequest);
			
			}catch (Exception exp) {
				LOG.error("Error in accept quotes: " + exp.getMessage());
				return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
			}

			return result;
		}

		/* Getting Payment Beneficary data by 
 * passing confiramtion number as params*/
	private JsonObject getInterBankFundTransferData(JSONObject requestPayload, DataControllerRequest dcRequest) {
		LOG.debug("getInterBankFundTransferData->>>");
		String filter = "";
		JsonObject customparams = new JsonObject();
		HashMap<String, Object> fundRequestParameters = new HashMap<>();
		HashMap<String, Object> fundRequestHeaders = new HashMap<>();
		filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + requestPayload.getString("orgnlPmtInfId");
		fundRequestParameters.put(SbgURLConstants.FILTER, filter);
		JSONObject interbankFundTransfersData = backendDelegate.getInterBankFundTransfers(fundRequestParameters,
				fundRequestHeaders);
		LOG.debug("getInterBankFundTransferData:TransferData" + interbankFundTransfersData);
		if (interbankFundTransfersData != null && interbankFundTransfersData.has("interbankfundtransfers")
				&& interbankFundTransfersData.getJSONArray("interbankfundtransfers").length() > 0) {
			JSONArray interbankfundtransfers = interbankFundTransfersData
					.getJSONArray("interbankfundtransfers");
			JSONObject fundTransfers = interbankfundtransfers.getJSONObject(0);
			LOG.debug("getInterBankFundTransferData:fundTransfers" + fundTransfers);
			String schedulateDt = fundTransfers.optString("scheduledDate");
			String formattedDate = null;
			try {
				formattedDate = SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss",
						SbgURLConstants.PATTERN_YYYY_MM_DD, schedulateDt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String beneficiaryName = fundTransfers.optString("beneficiaryName");
			String transactionCurrency = fundTransfers.optString("transactionCurrency");
		//	String amount = fundTransfers.optString("amount");
			String amount = fundTransfers.optString("transactionAmount");
			// String scheduledDate = formatter.parse(formattedDate);
			String toAccountNumber = fundTransfers.optString("toAccountNumber");
			String referenceNumber = fundTransfers.optString("confirmationNumber");
			String createdBy = fundTransfers.optString("createdby");
			String requestId = fundTransfers.optString("requestId");
			LOG.debug("getInterBankFundTransferData:FetchData" + "beneficiaryName" + beneficiaryName
					+ "transactionCurrency" + transactionCurrency + "amount" + amount+ "toAccountNumber" + toAccountNumber
					+ "referenceNumber" + referenceNumber + "createdBy" + createdBy + "requestId" + requestId);
			customparams.addProperty("beneficiaryName", beneficiaryName);
			customparams.addProperty("transactionCurrency", transactionCurrency);
			customparams.addProperty("amount", getTwoDecimalString(Double.parseDouble(amount)));
			customparams.addProperty("ScheduledDate", formattedDate);
			customparams.addProperty("MaskedToAccount",getMaskedValue(toAccountNumber));
			customparams.addProperty("ReferenceID", referenceNumber);
			customparams.addProperty("createdBy", createdBy);
			customparams.addProperty("requestId", requestId);
			String txSts = requestPayload.getString("txSts");
			if ("RJCT".equals(txSts)) {
				getSystemConfigurationData(customparams, requestPayload, dcRequest);
			}

		}
		return customparams;
	}

	/* Getting Payment data by 
 * passing confiramtion number as params*/
	private JsonObject getOWnAccountFundTransferData(JSONObject requestPayload, DataControllerRequest dcRequest) {
		LOG.debug("getOwnAccountFundTransfers->>>");
		String filter = "";
		JsonObject customparams = new JsonObject();
		HashMap<String, Object> fundRequestParameters = new HashMap<>();
		HashMap<String, Object> fundRequestHeaders = new HashMap<>();
		filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + requestPayload.getString("orgnlPmtInfId");
		fundRequestParameters.put(SbgURLConstants.FILTER, filter);
		JSONObject ownAccountFundTransfersData = backendDelegate.getOwnAccountFundTransfers(fundRequestParameters,
				fundRequestHeaders);
		LOG.debug("getOwnAccountFundTransfers:TransferData" + ownAccountFundTransfersData);
		if (ownAccountFundTransfersData != null && ownAccountFundTransfersData.has("ownaccounttransfers")
				&& ownAccountFundTransfersData.getJSONArray("ownaccounttransfers").length() > 0) {
			JSONArray ownaccounttransfers = ownAccountFundTransfersData
					.getJSONArray("ownaccounttransfers");
			JSONObject fundTransfers = ownaccounttransfers.getJSONObject(0);
			LOG.debug("getOwnAccountFundTransfers:fundTransfers" + fundTransfers);
			String schedulateDt = fundTransfers.optString("scheduledDate");
			String formattedDate = null;
			try {
				formattedDate = SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss",
						SbgURLConstants.PATTERN_YYYY_MM_DD, schedulateDt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String beneficiaryName = fundTransfers.optString("beneficiaryName");
			String transactionCurrency = fundTransfers.optString("transactionCurrency");
		//	String amount = fundTransfers.optString("amount");
			String amount = fundTransfers.optString("transactionAmount");
			// String scheduledDate = formatter.parse(formattedDate);
			String toAccountNumber = fundTransfers.optString("toAccountNumber");
			String referenceNumber = fundTransfers.optString("confirmationNumber");
			String createdBy = fundTransfers.optString("createdby");
			String requestId = fundTransfers.optString("requestId");
			LOG.debug("getOwnAccountFundTransfers:FetchData" + "beneficiaryName" + beneficiaryName
					+ "transactionCurrency" + transactionCurrency + "amount" + amount+ "toAccountNumber" + toAccountNumber
					+ "referenceNumber" + referenceNumber + "createdBy" + createdBy + "requestId" + requestId);
			customparams.addProperty("beneficiaryName", beneficiaryName);
			customparams.addProperty("transactionCurrency", transactionCurrency);
			customparams.addProperty("amount", getTwoDecimalString(Double.parseDouble(amount)));
			customparams.addProperty("ScheduledDate", formattedDate);
			customparams.addProperty("MaskedToAccount",getMaskedValue(toAccountNumber));
			customparams.addProperty("ReferenceID", referenceNumber);
			customparams.addProperty("createdBy", createdBy);
			customparams.addProperty("requestId", requestId);
			String txSts = requestPayload.getString("txSts");
			if ("RJCT".equals(txSts)) {
				getSystemConfigurationData(customparams, requestPayload, dcRequest);
			}

		}
		return customparams;
	}
}
