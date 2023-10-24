package com.kony.sbg.backend.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.OwnAccountPaymentAPIBackendDelegate;
import com.kony.sbg.util.Pain001Executor;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;

public class OwnAccountPaymentAPIBackendDelegateImpl implements OwnAccountPaymentAPIBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(OwnAccountPaymentAPIBackendDelegateImpl.class);

	public OwnAccountFundTransferDTO createTransactionWithoutApproval(OwnAccountFundTransferBackendDTO input,
			DataControllerRequest request) {
		LOG.debug("Entry --> OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval");
		OwnAccountFundTransferDTO resultDTO = new OwnAccountFundTransferDTO();
		Map<String, Object> requestParameters = buildRequestForSubmitPayment(input, request, resultDTO);
		Map<String, Object> requestHeaders = getRequestHeader(request, resultDTO);
		String submitPaymentRsp = null;

		LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestParameters: "
				+ requestParameters.toString());
		LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestHeaders: "
				+ requestHeaders.toString());

		if (resultDTO.getDbpErrCode() != null && resultDTO.getDbpErrCode().isEmpty() == false) {
			LOG.error(
					"OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval --> build Request or Request Failed");
			return resultDTO;
		}

		LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl.createTransactionWithoutApproval ===> Pain001 Request: "
				+ Pain001Executor.createPain001Request(requestParameters));

		try {
			submitPaymentRsp = DBPServiceExecutorBuilder.builder().withServiceId(SbgURLConstants.SERVICE_SBG_PAYMENT)
					.withOperationId(SbgURLConstants.OPERATION_SUBMIT_IAT_PAYMENT)
					.withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).withPassThroughOutput(true).build().getResponse();

			LOG.error("Response of Service : " + SbgURLConstants.SERVICE_SBG_PAYMENT + " Operation : "
					+ SbgURLConstants.OPERATION_SUBMIT_IAT_PAYMENT + ":: " + submitPaymentRsp);

		} catch (DBPApplicationException e) {
			LOG.error("Error occured while submitting payment: " + e);
			resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
			return resultDTO;
		}
		try {
			JSONObject serviceResponse = new JSONObject(submitPaymentRsp);
			LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::Response_Json:: "
					+ serviceResponse.toString());
			if (serviceResponse != null && serviceResponse.length() > 0) {
				String updateResponse = updateSubmitPaymentResponse(input.getTransactionId(), serviceResponse);
				if (serviceResponse.has("status") && serviceResponse.has("uniqueIdentifier")) {
					LOG.debug("createTransactionWithoutApproval::Payment Submitted Successfully");
					resultDTO.setReferenceId(input.getTransactionId());
					resultDTO.setConfirmationNumber(input.getConfirmationNumber());
					LOG.debug(
							"OwnAccountPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::FinalResultDTO ReferenceID: "
									+ resultDTO.getReferenceId() + " Confirmation Number: "
									+ resultDTO.getConfirmationNumber());
					return resultDTO;
				} else {
					if (serviceResponse.has("httpMessage")) {
						LOG.error("createTransactionWithoutApproval::Technical error occurred in submit payment API");
						resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100053);
						return resultDTO;
					} else {
						LOG.error("createTransactionWithoutApproval::Error occurred in submit payment API");
						resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100025);
						return resultDTO;
					}
				}
			} else {
				LOG.error("An unknown error occurred while processing the submit payment request");
				resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
				return resultDTO;
			}

		} catch (Exception e) {
			LOG.debug("Error occured while submitting payment: " + e);
			resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
			return resultDTO;
		}
	}

	private Map<String, Object> buildRequestForSubmitPayment(OwnAccountFundTransferBackendDTO input,
			DataControllerRequest request, OwnAccountFundTransferDTO resultDTO) {
		LOG.debug("Entry --> OwnAccountPaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment");
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String exchangeRate = "", fxCoverNumber = "", multipleDeals = "", bicTransformation = "";

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		requestParameters.put("msgId", CommonUtils.generateUniqueIDHyphenSeperated(0, 20));
		requestParameters.put("createdDtTm", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS"));

		String customerName = getEntityName4Pain001(request);
		if (SBGCommonUtils.isStringEmpty(customerName)) {
			// keeping the customername as old behaviour in case of failure
			customerName = CustomerSession.getCustomerCompleteName(customer);
		}
		requestParameters.put("customerName", customerName);

		if (StringUtils.isNotBlank(request.getParameter("paymentType"))) {
			if (SbgURLConstants.IAT_PAYMENT_TYPE_DOM.equalsIgnoreCase(request.getParameter("paymentType"))) {
				requestParameters.put("productCode", SbgURLConstants.IAT_DOM_PRODUCT_CODE);
			} else if (SbgURLConstants.IAT_PAYMENT_TYPE_FX.equalsIgnoreCase(request.getParameter("paymentType"))) {
				requestParameters.put("productCode", SbgURLConstants.IAT_FX_PRODUCT_CODE);
			}
		} else {
			requestParameters.put("productCode", SbgURLConstants.IAT_FX_PRODUCT_CODE);
		}

		try {
			Result result3 = SBGCommonUtils.cacheFetch("CoreCustomerId");
			LOG.info("@@=CoreCustomerId fetching" + result3.getParamValueByName("CoreCustomerId"));
			requestParameters.put("customerId", result3.getParamValueByName("CoreCustomerId"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String paymentName = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_PAYMENT_NAME, request);
			String paymentProvider = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_PAYMENT_PROVIDER,
					request);
			bicTransformation = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BIC_TRANSFORMATION, request);
			if (paymentName != null && paymentProvider != null) {
				requestParameters.put("paymentName", paymentName);
				requestParameters.put("paymentProvider", paymentProvider);
			} else {
				LOG.error("Payment Name [" + paymentName + "] or Payment Provider [" + paymentProvider + "] is null");
				populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
				return requestParameters;
			}
		} catch (Exception e) {
			LOG.error("Exception occured while fetching server properties: " + e.getStackTrace());
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
			return requestParameters;
		}

		if (input.getConfirmationNumber() != null && input.getConfirmationNumber().isEmpty() == false) {
			requestParameters.put("confirmationNumber", input.getConfirmationNumber());
		}

		try {
			if (input.getScheduledDate() != null && input.getScheduledDate().isEmpty() == false) {
				requestParameters.put("scheduledDate",
						SBGCommonUtils.getFormattedDate(SbgURLConstants.PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
								SbgURLConstants.PATTERN_YYYY_MM_DD, input.getScheduledDate()));

			} else {
				LOG.error("buildRequestForSubmitPayment::scheduled date not found");
				populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
				return requestParameters;
			}
		} catch (ParseException e) {
			LOG.error("Exception occured while converting date format: " + e);
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100027);
			return requestParameters;
		}

		Map<String, String> userDetails = getUserNameMailId(CustomerSession.getCustomerId(customer), request,
				resultDTO);

		if (resultDTO.getDbpErrCode() != null && resultDTO.getDbpErrCode().isEmpty() == false) {
			LOG.error("buildRequestForSubmitPayment::Error occurred while fetching customer user details");
			return requestParameters;
		}

		requestParameters.put("userName", userDetails.get("userName"));
		requestParameters.put("userMailId", userDetails.get("userEmail"));
		requestParameters.put("fromAccountNumber", input.getFromAccountNumber());
		requestParameters.put("fromAccountCurrency", input.getFromAccountCurrency());

		/*
		 * fromAccountswiftcode not coming in payload and not available in table. Need
		 * to get this from payload. Currently get it from server properties
		 */
		String fetchSwiftCode = getFromAccountSwiftCode(input.getFromAccountNumber(), request, bicTransformation);
		if (SBGCommonUtils.isStringEmpty(fetchSwiftCode) || StringUtils.isBlank(fetchSwiftCode)) {
			LOG.error("buildRequestForSubmitPayment::FetchSwitfcodefailed Due to ExtensionData");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100083);
			return requestParameters;

		}

		requestParameters.put("fromAccountswiftcode", fetchSwiftCode);
		requestParameters.put("transactionCurrency", input.getTransactionCurrency());

		if (input.getTransactionAmount() != null && input.getTransactionAmount().isEmpty() == false) {
			requestParameters.put("transactionAmount", input.getTransactionAmount());
		} else {
			LOG.error("buildRequestForSubmitPayment::transaction amount not found");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
			return requestParameters;
		}

		String rfqDetails = request.getParameter("rfqDetails") != null ? request.getParameter("rfqDetails").toString() : null;
		LOG.debug("buildRequestForSubmitPayment::rfqDetails: " + rfqDetails);
		try {
			if (rfqDetails != null && rfqDetails.isEmpty() == false) {
				JSONArray quotes = new JSONArray(rfqDetails);
				if (quotes.length() == 1) {
					JSONObject singleQuote = quotes.getJSONObject(0);
					exchangeRate = singleQuote.optString("exchangeRate");
					fxCoverNumber = singleQuote.optString("coverNumber");
				} else if (quotes.length() > 1) {
					/* Not supported in sprint 6 */
					multipleDeals = getMultipleDealDetails(quotes, input.getFromAccountCurrency(),
							input.getTransactionCurrency(), input.getTransactionAmount());
				}
			} else {
				LOG.debug("buildRequestForSubmitPayment --> Empty RFQ details");
			}
		} catch (Exception e) {
			LOG.error("Exception occurred while parsing rfq details", e);
		}

		LOG.debug("buildRequestForSubmitPayment::exchangeRate: " + exchangeRate + " fxCoverNumber: " + fxCoverNumber);
		requestParameters.put("exchangeRate", exchangeRate);
		requestParameters.put("fxCoverNumber", fxCoverNumber);

		if (input.getPaidBy() != null && input.getPaidBy().isEmpty() == false) {
			requestParameters.put("paidBy", SbgURLConstants.CHARGE_BEARER.get(input.getPaidBy()));
		} else {
			LOG.error("buildRequestForSubmitPayment --> paidby not found");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
			return requestParameters;
		}

		requestParameters.put("beneSwiftCode", getBICTransformedSwiftCode(input.getSwiftCode(), bicTransformation));

		try {
			JSONObject json = SBGUtil.getCompanyData4mParty(request);
			JSONObject extnData = json.getJSONObject("extensionData");

			String country = extnData.getString("legalcountry").length() != 2
					? getCountryCode(extnData.getString("legalcountry"))
					: extnData.getString("legalcountry");

			requestParameters.put("beneBankName", "Standard Bank South Africa");
			requestParameters.put("beneficiaryName", extnData.getString("entityName"));
			requestParameters.put("beneAddressLine1", extnData.getString("legaladdressLine1"));
			requestParameters.put("beneAddressLine2", extnData.getString("legaladdressLine2"));
			requestParameters.put("beneAddressLine3", extnData.getString("legaladdressLine3"));
			requestParameters.put("beneCountryName", country);
			requestParameters.put("beneState", extnData.getString("legalstate"));
			requestParameters.put("beneCity", extnData.getString("legalcityName"));
			requestParameters.put("beneZipCode", extnData.getString("legalzipCode"));
		} catch (Exception e) {
			LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl: Failed to get customer address");
		}

		if (isIBAN(input.getToAccountNumber())) {
			requestParameters.put("iban", input.getToAccountNumber());
			requestParameters.put("toAccountNumber", "");
		} else {
			requestParameters.put("toAccountNumber", input.getToAccountNumber());
			requestParameters.put("iban", "");
		}

		/* purposeCode & complianceType conditional parameters */
		if (request.getParameter("purposeCode") == null) {
			requestParameters.put("purposeCode", "");
		} else {
			String pcode = request.getParameter("purposeCode");
			String updatedcode = StringUtils.substringBetween(pcode, "/", "/");
			requestParameters.put("purposeCode", updatedcode);
		}

		if (request.getParameter("complianceCode") == null) {
			requestParameters.put("complianceType", "");
		} else {
			requestParameters.put("complianceType", request.getParameter("complianceCode"));
		}

		String applicationNo = StringUtils.isNotBlank(request.getParameter("applicationNumber"))
				? request.getParameter("applicationNumber")
				: "";
		String applicationDate = StringUtils.isNotBlank(request.getParameter("applicationDate"))
				? request.getParameter("applicationDate")
				: "";
		String authDealer = StringUtils.isNotBlank(request.getParameter("authoriserDealer"))
				? request.getParameter("authoriserDealer")
				: "";
		requestParameters.put("applicationNumber", applicationNo);
		try {
			requestParameters.put("applicationDate",
					SBGCommonUtils.getFormattedDate("dd-MM-yyyy", "yyyy-MM-dd", applicationDate));
		} catch (ParseException e) {
			requestParameters.put("applicationDate", applicationDate);
			e.printStackTrace();
		}
		requestParameters.put("authoriserDealer", authDealer);

		/* beneficiaryRefCode & statementRefCode are mandatory */
		requestParameters.put("beneficiaryRefCode", request.getParameter("beneficiaryReference"));
		requestParameters.put("statementRefCode", request.getParameter("statementReference"));

		/* set isDocumentRequired as Y if BoP is reportable as per Jason/Rajesh */
		String isDocumentRequired = "N";
		if (request.containsKeyInRequest("bopDetails") && request.getParameter("bopDetails") != null
				&& request.getParameter("bopDetails").isEmpty() == false) {
			requestParameters.put("bopDetails", getDecodedBopDetails(request.getParameter("bopDetails")));
			isDocumentRequired = "Y";
		} else {
			requestParameters.put("bopDetails", "");
		}

		String uploadedAttachments = request.getParameter("uploadedattachments");
		String[] uploadedAttachmentsArray = new String[0];
		if (StringUtils.isNotBlank(uploadedAttachments)) {
			uploadedAttachmentsArray = uploadedAttachments.split(",");
			isDocumentRequired = "Y";
			requestParameters.put("documentList", uploadedAttachments);
		} else {
			requestParameters.put("documentList", "");
		}
		requestParameters.put("isDocumentRequired", isDocumentRequired);
		requestParameters.put("documentCount", uploadedAttachmentsArray.length);

		requestParameters.put("multipleDealsDetails", multipleDeals);
		requestParameters.put("routingNumber", "");
		// if (input.getClearingCode() != null && input.getClearingCode().isEmpty() ==
		// false) {
		// requestParameters.put("routingNumber", input.getClearingCode());
		// } else if (request.getParameter("clearingCode") != null
		// && request.getParameter("clearingCode").isEmpty() == false) {
		// requestParameters.put("routingNumber", request.getParameter("clearingCode"));
		// } else {
		// requestParameters.put("routingNumber", "");
		// }

		LOG.debug(
				"OwnAccountPaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment --> finished building request");
		return requestParameters;
	}

	private String getEntityName4Pain001(DataControllerRequest request) {
		try {
			JSONObject companyData = SBGUtil.getCompanyData4mParty(request);
			JSONObject extnData = companyData.getJSONObject("extensionData");
			return extnData.getString("entityName");
		} catch (Exception e) {
			return null;
		}
	}

	// The below function is used for fetching customers contact name and
	// communication details
	private Map<String, String> getUserNameMailId(String customerId, DataControllerRequest request,
			OwnAccountFundTransferDTO resultDTO) {
		LOG.debug("Entry --> OwnAccountPaymentAPIBackendDelegateImpl::getUserNameMailId");

		Map<String, String> userDetails = new HashMap<>();

		// [AH]The below block is used to fetch customer contact details based on
		// partyId.
		// This change is for SBG-588 to improve the performance to fetch the contact
		// details
		// JSONObject custObj = getCustomerData4mParty(request, customerId);
		JSONObject custObj = SBGUtil.getCompanyData4mParty(request);
		if (custObj != null) {
			String fname = custObj.getString("firstName");
			String lname = custObj.getString("lastName");
			if ((!SBGCommonUtils.isStringEmpty(fname) || !SBGCommonUtils.isStringEmpty(lname))) {

				String cpe[] = SBGCommonUtils.getCodePhoneAddress(custObj.getJSONArray("addresses"));
				userDetails.put("userName", fname + " " + lname);
				userDetails.put("userEmail", cpe[2]);

				LOG.debug("getUserNameMailId::userDetails: " + userDetails);
				return userDetails;
			}
		}

		return userDetails;
	}

	private String getDecodedBopDetails(String bopDetails) {
		LOG.debug("Entry --> OwnAccountPaymentAPIBackendDelegateImpl::getDecodedBopDetails");
		String decodedBopDetails = "";
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(bopDetails);
			String decodedBase64 = new String(decodedBytes);
			decodedBopDetails = URLDecoder.decode(decodedBase64, StandardCharsets.UTF_8.toString());
			LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::getDecodedBopDetails::decodedBopDetails: "
					+ decodedBopDetails);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error occured while decoding bop details", e);
		}

		return decodedBopDetails;
	}

	private boolean isIBAN(String toAccountNumber) {
		boolean result = false;
		if (Character.isDigit(toAccountNumber.charAt(0))) {
			LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::isIBAN: " + result);
		} else {
			IBANCheckDigit iBanDigit = new IBANCheckDigit();
			result = iBanDigit.isValid(toAccountNumber);
			LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::isIBAN:isValid:: " + result);
		}
		return result;
	}

	private String getFromAccountSwiftCode(String fromAccountNumber, DataControllerRequest request,
			String bicTransformation) {
		String fromAccountSwiftCode = "";
		try {
			// String accountDetails1 = SBGCommonUtils.getServerPropertyValue(key, request);
			String accountDetails = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from
																								// Arrangement Extension
			LOG.debug("&&&&&BICFromExtension: " + accountDetails);
			if (StringUtils.isBlank(accountDetails)) {

				return fromAccountSwiftCode;
			} else {
				fromAccountSwiftCode = getBICTransformedSwiftCode(accountDetails, bicTransformation);
				LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::getFromArrangementAccountSwiftCode: "
						+ fromAccountSwiftCode);
			}

		} catch (Exception e) {
			LOG.error("Error occured while fetching environment properties: " + e.getStackTrace());
		}

		return fromAccountSwiftCode;
	}

	private String getBICTransformedSwiftCode(String bic, String bicTransformation) {
		LOG.debug("getBICTransformedSwiftCode::bic::" + bic);
		String bicTransformed = bic;
		if (SBGCommonUtils.isStringEmpty(bicTransformation) == false) {
			if (bicTransformation.equals("true") && (bic.length() >= SbgURLConstants.BIC_TRANSFORMATION_MIN_LENGTH)) {
				LOG.debug("getBICTransformedSwiftCode::bic.length()::" + bic.length());
				StringBuilder swiftCode = new StringBuilder(bic);
				swiftCode.setCharAt(7, '0');
				bicTransformed = swiftCode.toString();
				LOG.debug("getBICTransformedSwiftCode::bicTransformed::" + bicTransformed);
			}
		}
		return bicTransformed;
	}

	/* Sprint 6 does not support multiple deals. Need to form the string here */
	private String getMultipleDealDetails(JSONArray quotes, String fromAccountCurrency, String toAccountCurrency,
			String amount) {
		String multipleDeals = "";
		StringBuilder multipleDealsSb = new StringBuilder();
		for (Object object : quotes) {
			JSONObject quote = (JSONObject) object;
			String exchangeRate = quote.getString("exchangeRate");
			String coverNumber = quote.getString("coverNumber");
			StringBuilder dealsSb = new StringBuilder();
			dealsSb.append("XchgRate:");
			dealsSb.append(exchangeRate);
			dealsSb.append(toAccountCurrency);
			dealsSb.append("/");
			dealsSb.append(fromAccountCurrency);
			dealsSb.append(" CtrctId:");
			dealsSb.append(coverNumber);
			dealsSb.append(" Amount:");
			dealsSb.append(amount);
			dealsSb.append(toAccountCurrency);
			dealsSb.append("_");
			multipleDealsSb.append(dealsSb);
		}

		if (multipleDealsSb.charAt(multipleDealsSb.length() - 1) == '_') {
			multipleDealsSb.deleteCharAt(multipleDealsSb.length() - 1);
		}
		return multipleDeals;
	}

	private Map<String, Object> getRequestHeader(DataControllerRequest request,
			OwnAccountFundTransferDTO resultDTO) {
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		Result resultCache = null;
		String authorization = "";

		try {
			resultCache = SBGCommonUtils.cacheFetchPingToken("Authorization", request);
		} catch (Exception e) {
			LOG.error("Exception occurred while fetching authorization from session: " + e.getStackTrace());
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
			return requestHeaders;
		}

		if (resultCache != null && resultCache.hasParamByName("Authorization")) {
			authorization = resultCache.getParamValueByName("Authorization").toString();
			requestHeaders.put("Authorization", authorization);
		} else {
			LOG.error(
					"OwnAccountPaymentAPIBackendDelegateImpl::getRequestHeader --> IBM-Gateway authentication failed");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
			return requestHeaders;
		}

		try {
			String clientId = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.IAT_X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.IAT_X_IBM_CLIENT_SECRET,
					request);
			if (clientId != null && clientId.isEmpty() == false && clientSecret != null
					&& clientSecret.isEmpty() == false) {
				requestHeaders.put("X-IBM-Client-Id", clientId);
				requestHeaders.put("X-IBM-Client-Secret", clientSecret);
			} else {
				LOG.error(
						"OwnAccountPaymentAPIBackendDelegateImpl::getRequestHeader --> ClientId and Client Secret are null or empty");
				populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
				return requestHeaders;
			}
		} catch (Exception e) {
			LOG.error("Error occured while fetching environment properties: " + e.getStackTrace());
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
			return requestHeaders;
		}

		requestHeaders.put("x-fapi-interaction-id", SBGCommonUtils.generateRandomUUID().toString());
		requestHeaders.put("transacted", "false");

		return requestHeaders;
	}

	private OwnAccountFundTransferDTO populateErrorDetails(OwnAccountFundTransferDTO resultDTO,
			SbgErrorCodeEnum errorCodeEnum) {
		resultDTO.setDbpErrCode(errorCodeEnum.getErrorCodeAsString());
		resultDTO.setDbpErrMsg(errorCodeEnum.getMessage());
		return resultDTO;
	}

	public String updateSubmitPaymentResponse(String referenceId, JSONObject serviceResponse) {
		LOG.error("Entry --> OwnAccountPaymentAPIBackendDelegateImpl::updateSubmitPaymentResponse entry");
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_UPDATE;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String updateResponse = "";
		String uniqId = "";

		if (serviceResponse.has("uniqueIdentifier")) {
			uniqId = serviceResponse.getString("uniqueIdentifier");
		}

		String response = serviceResponse.toString();
		if (response.length() > 1024) {
			response = response.substring(0, 1000);
		}

		requestParams.put("referenceId", referenceId);
		requestParams.put("submitPaymentId", uniqId);
		requestParams.put("submitPaymentResponse", response);

		try {
			updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			LOG.debug("updateSubmitPaymentResponse::updateResponse: " + updateResponse);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while updating the internationalfundtransfersRefData: ", jsonExp);
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while updating the internationalfundtransfersRefData: ", exp);
			return null;
		}

		return updateResponse;
	}

	private String getCountryCode(String beneficiarycountry) {
		String countryCode = "";
		String countryRsp = "";
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_COUNTRY_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "";
		filter = "Name " + DBPUtilitiesConstants.EQUAL + "'" + beneficiarycountry + "'";
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::requestParams:: " + requestParams);
		try {
			countryRsp = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			JSONObject respObj = new JSONObject(countryRsp);
			if (respObj.has("country")) {
				JSONArray countries = respObj.getJSONArray("country");
				if (countries.length() > 0) {
					JSONObject countryObj = countries.getJSONObject(0);
					if (countryObj.has("Code")) {
						countryCode = countryObj.getString("Code");
						LOG.debug("OwnAccountPaymentAPIBackendDelegateImpl::getCountryCode_Value:: " + countryCode);
					}
				}
			}

		} catch (DBPApplicationException e) {
			LOG.error("Error occured while fetching country: " + e.getStackTrace());
		}
		return countryCode;
	}

}
