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
import com.kony.sbg.backend.api.PaymentAPIBackendDelegate;
import com.kony.sbg.util.Pain001Executor;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;

public class PaymentAPIBackendDelegateImpl implements PaymentAPIBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(PaymentAPIBackendDelegateImpl.class);

	public InternationalFundTransferDTO createTransactionWithoutApproval(InternationalFundTransferBackendDTO input,
																		 DataControllerRequest request, Map<String, Object> inputParams) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval");
		InternationalFundTransferDTO resultDTO = new InternationalFundTransferDTO();
		Map<String, Object> requestParameters = buildRequestForSubmitPayment(input, request, resultDTO, inputParams);
		Map<String, Object> requestHeaders = getRequestHeader(request, resultDTO);
		String submitPaymentRsp = null;

		LOG.debug("PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestParameters: "
				+ requestParameters.toString());
		LOG.debug("PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestHeaders: "
				+ requestHeaders.toString());

		if (resultDTO.getDbpErrCode() != null && resultDTO.getDbpErrCode().isEmpty() == false) {
			LOG.error("PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval --> build Request or Request Failed");
			return resultDTO;
		}

		LOG.debug("PaymentAPIBackendDelegateImpl.createTransactionWithoutApproval ===> Pain001 Request: "+Pain001Executor.createPain001Request(requestParameters));
		
		try {
			submitPaymentRsp = DBPServiceExecutorBuilder.builder().withServiceId(SbgURLConstants.SERVICE_SBG_PAYMENT)
					.withOperationId(SbgURLConstants.OPERATION_SUBMITPAYMENT).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).withPassThroughOutput(true).build().getResponse();

			LOG.error("Response of Service : " + SbgURLConstants.SERVICE_SBG_PAYMENT + " Operation : "
					+ SbgURLConstants.OPERATION_SUBMITPAYMENT + ":: " + submitPaymentRsp);

		} catch (DBPApplicationException e) {
			LOG.error("Error occured while submitting payment: " + e);
			resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
			return resultDTO;
		}
       try {
		JSONObject serviceResponse = new JSONObject(submitPaymentRsp);
		LOG.debug("PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::Response_Json:: "
				+ serviceResponse.toString());
		if (serviceResponse != null && serviceResponse.length() > 0) {
			String updateResponse = updateSubmitPaymentResponse(input.getTransactionId(), serviceResponse);
			if (serviceResponse.has("status") && serviceResponse.has("uniqueIdentifier")) {
				LOG.debug("createTransactionWithoutApproval::Payment Submitted Successfully");
				resultDTO.setReferenceId(input.getTransactionId());
				resultDTO.setConfirmationNumber(input.getConfirmationNumber());
				LOG.debug("PaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::FinalResultDTO ReferenceID: "
						+ resultDTO.getReferenceId() + " Confirmation Number: " + resultDTO.getConfirmationNumber());
				return resultDTO;
			} else {
				if (serviceResponse.has("httpMessage")) {
					LOG.error("createTransactionWithoutApproval::Technical error occurred in submit payment API");
					resultDTO =	populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100053);
					return resultDTO;
				} else {
					LOG.error("createTransactionWithoutApproval::Error occurred in submit payment API");
					resultDTO =	populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100025);
					return resultDTO;
				}
			}
		} else {
			LOG.error("An unknown error occurred while processing the submit payment request");
			resultDTO =populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
			return resultDTO;
		}

		
       }
       catch (Exception e) {
			LOG.debug("Error occured while submitting payment: " + e);
			resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
			return resultDTO;
		}
	}

	private Map<String, Object> buildRequestForSubmitPayment(InternationalFundTransferBackendDTO input,
															 DataControllerRequest request, InternationalFundTransferDTO resultDTO, Map<String, Object> inputParams) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment");
		LOG.debug("###### buildRequestForSubmitPayment::inputParams: " + inputParams);
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String exchangeRate = "", fxCoverNumber = "", multipleDeals = "", bicTransformation = "";

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		requestParameters.put("msgId", CommonUtils.generateUniqueIDHyphenSeperated(0, 20));
		requestParameters.put("createdDtTm", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		
		String customerName = getEntityName4Pain001(request);
		if(SBGCommonUtils.isStringEmpty(customerName)) {
			//keeping the customername as old behaviour in case of failure
			customerName = CustomerSession.getCustomerCompleteName(customer);
		}
		requestParameters.put("customerName", customerName);
		
		try {
			Result result3=SBGCommonUtils.cacheFetch("CoreCustomerId");
			LOG.info("@@=CoreCustomerId fetching"+result3.getParamValueByName("CoreCustomerId"));
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

		Map<String, String> userDetails = getUserNameMailId(CustomerSession.getCustomerId(customer), request, resultDTO);

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
		String fetchSwiftCode =getFromAccountSwiftCode(input.getFromAccountNumber(), request, bicTransformation);
		if(SBGCommonUtils.isStringEmpty(fetchSwiftCode)|| StringUtils.isBlank(fetchSwiftCode)) {
			LOG.error("buildRequestForSubmitPayment::FetchSwitfcodefailed Due to ExtensionData");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100083);
			return requestParameters;
			
		}
		
		requestParameters.put("fromAccountswiftcode", getFromAccountSwiftCode(input.getFromAccountNumber(), request, bicTransformation));
		requestParameters.put("transactionCurrency", input.getTransactionCurrency());

		if (input.getTransactionAmount() != null && input.getTransactionAmount().isEmpty() == false) {
			requestParameters.put("transactionAmount", input.getTransactionAmount());
		} else {
			LOG.error("buildRequestForSubmitPayment::transaction amount not found");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
			return requestParameters;
		}

		//String rfqDetails = request.getParameter("rfqDetails").toString();
		LOG.debug("###### buildRequestForSubmitPayment::inputParams: " + inputParams);
		Object rfqDetailsObj = inputParams.get("rfqDetails");
		String rfqDetails = rfqDetailsObj != null ? rfqDetailsObj.toString() : null;
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

		if (input.getBeneficiaryBankName() != null && input.getBeneficiaryBankName().isEmpty() == false) {
			LOG.debug("buildRequestForSubmitPayment --> input beneBankName is not empty");
			requestParameters.put("beneBankName", input.getBeneficiaryBankName());
		}

		requestParameters.put("beneficiaryName", getBeneName(input, request));
		requestParameters.put("beneZipCode", input.getBeneficiaryZipcode());
		requestParameters.put("beneCity", input.getBeneficiaryCity());
		if (input.getBeneficiaryState() != null && input.getBeneficiaryState().isEmpty() == false) {
			requestParameters.put("beneState", input.getBeneficiaryState());
		} else {
			LOG.error("buildRequestForSubmitPayment --> beneficiaryState not found");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
			return requestParameters;
		}

		requestParameters.put("beneCountryName", getCountryCode(input.getBeneficiarycountry()));
		requestParameters.put("beneAddressLine1", input.getBeneficiaryAddressLine1());
		if (input.getBeneficiaryAddressLine2() != null && input.getBeneficiaryAddressLine2().isEmpty() == false) {
			requestParameters.put("beneAddressLine2", input.getBeneficiaryAddressLine2());
		} else {
			requestParameters.put("beneAddressLine2", "");
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

		String complianceDate = request.getParameter("complianceDate");
		LOG.info("#### PaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment::complianceDate"+complianceDate);
		if (StringUtils.isNotBlank(complianceDate)) {
			try {
				requestParameters.put("complianceDate", SBGCommonUtils.getFormattedDate("dd/MM/yyyy",
								SbgURLConstants.PATTERN_YYYY_MM_DD, complianceDate));
			} catch (ParseException e) {
				LOG.error("#### PaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment::complianceDate formatting failed",e);
			}
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

		/*set isDocumentRequired as Y if BoP is reportable as per Jason/Rajesh*/
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

		if (input.getClearingCode() != null && input.getClearingCode().isEmpty() == false) {
			requestParameters.put("routingNumber", input.getClearingCode());
		} else if (request.getParameter("clearingCode") != null
				&& request.getParameter("clearingCode").isEmpty() == false) {
			requestParameters.put("routingNumber", request.getParameter("clearingCode"));
		} else {
			requestParameters.put("routingNumber", "");
		}

		LOG.debug("PaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment --> finished building request");
		return requestParameters;
	}
	
	private	String getEntityName4Pain001(DataControllerRequest request) {
		try {
			JSONObject companyData = SBGUtil.getCompanyData4mParty(request);
			JSONObject extnData = companyData.getJSONObject("extensionData");
			return extnData.getString("entityName");
		}catch(Exception e) {
			return null;
		}
	}

	//The below function is used for fetching customers contact name and communication details
	private Map<String, String> getUserNameMailId(String customerId, DataControllerRequest request,
												  InternationalFundTransferDTO resultDTO) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::getUserNameMailId");

		Map<String, String> userDetails = new HashMap<>();

		//[AH]The below block is used to fetch customer contact details based on partyId.
		//This change is for SBG-588 to improve the performance to fetch the contact details
		//JSONObject custObj = getCustomerData4mParty(request, customerId);
		JSONObject custObj = SBGUtil.getCompanyData4mParty(request);
		if(custObj != null) {
			String fname = custObj.getString("firstName");
			String lname = custObj.getString("lastName");
			if((!SBGCommonUtils.isStringEmpty(fname) || !SBGCommonUtils.isStringEmpty(lname))) {

				String cpe[] = SBGCommonUtils.getCodePhoneAddress(custObj.getJSONArray("addresses"));
				userDetails.put("userName", fname +" "+ lname);
				userDetails.put("userEmail", cpe[2]);

				LOG.debug("getUserNameMailId::userDetails: " + userDetails);
				return userDetails;
			}
		}

		//The below block fetches the customer details based on backofficeidentifier.
		//Leaving the below code as is to reduce the risk of details not being found during transfer.
		//Most of the scenarios, the data should be fetched from above block and the below code will be redundant.
//		Map<String, Object> requestParams = new HashMap<>();
//		String filter = "", coreCustomerId = "", userName = "", userMailId = "";
//		filter = "customerId " + DBPUtilitiesConstants.EQUAL + customerId;
//		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
//
//		JSONObject contractCustomerRsp = getContractCustomers(requestParams);
//
//		if (contractCustomerRsp != null && contractCustomerRsp.has("contractcustomers")
//				&& contractCustomerRsp.getJSONArray("contractcustomers").length() > 0) {
//			JSONObject contractCustomer = contractCustomerRsp.getJSONArray("contractcustomers").getJSONObject(0);
//			coreCustomerId = contractCustomer.optString("coreCustomerId");
//			LOG.debug("getUserNameMailId::coreCustomerId: " + coreCustomerId);
//		} else {
//			LOG.error("getUserNameMailId::Error occurred while fetching contract customer");
//			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100036);
//		}
//
//		Map<String, Object> requestParameter = new HashMap<>();
//		Map<String, Object> requestHeaders = new HashMap<>();		
//		try {
//			String backOfficeIdentifier = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BACKOFFICEIDENTIFIER,
//					request);
//			LOG.debug("getUserNameMailId::backOfficeIdentifier: " + backOfficeIdentifier);
//			String branchIdReference = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BRANCH_ID_REFERENCE,
//					request);
//			String partyAuthKey = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PARTYMS_AUTHORIZATION_KEY,
//					request);
//	
//			requestParameter.put("alternateIdentifierNumber", branchIdReference + "-" + coreCustomerId);
//			requestParameter.put("alternateIdentifierType", backOfficeIdentifier);
//			requestHeaders.put("X-API-Key", partyAuthKey);
//			LOG.debug("getUserNameMailId::party::requestParameter: " + requestParameter);
//			LOG.debug("getUserNameMailId::party::requestHeaders: " + requestHeaders);
//		} catch (Exception e) {
//			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
//			LOG.error("getUserNameMailId::Error occurred while fetching server properties: " + e.getStackTrace());
//		}
//					
//		JSONObject partyData = getPartyDetails(requestParameter, requestHeaders);
//		
//		if (partyData != null && partyData.has("parties") && partyData.getJSONArray("parties").length() > 0) {			
//			JSONObject partyDetails = partyData.getJSONArray("parties").getJSONObject(0);			
//			String firstName = partyDetails.optString("firstName");
//			String lastName = partyDetails.optString("lastName");			
//			if (StringUtils.isNotBlank(firstName)) {
//				userName = firstName;
//			}
//			
//			if (StringUtils.isNotBlank(lastName)) {
//				userName = userName + " " + lastName;
//			}
//			
//			JSONArray addresses = partyDetails.getJSONArray("addresses");			
//			Optional<Object> communicationEntity = StreamSupport.stream(addresses.spliterator(), true)
//					.filter(item -> ((JSONObject) item).has("communicationType")
//							&& ((JSONObject) item).getString("communicationType").equalsIgnoreCase("Email"))
//					.findFirst();
//			JSONObject emailCommunication;
//			if (communicationEntity.isPresent()) {				
//				emailCommunication = (JSONObject) communicationEntity.get();				
//				userMailId = emailCommunication.optString("electronicAddress");					
//			}				
//		} else {
//			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100037);
//			LOG.error("getUserNameMailId::Error occurred while fetching server properties: ");				
//		}			
//
//		LOG.debug("getUserNameMailId::userName: " + userName + " userEmail: " + userMailId);
//		userDetails.put("userName", userName);
//		userDetails.put("userEmail", userMailId);
//
		return userDetails;
	}

	private JSONObject getPartyDetails(Map<String, Object> requestParams, Map<String, Object> requestHeaders) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::getPartyDetails");
		String serviceName = SbgURLConstants.SERVICE_PARTYMSSERVICE;
		String operationName = SbgURLConstants.OPERATION_GETPARTYDETAILS;
		JSONObject serviceResponse = null;

		try {

			String partyResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParams)
					.withRequestHeaders(requestHeaders).build().getResponse();
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_PARTYMSSERVICE + " Operation : "
					+ SbgURLConstants.OPERATION_GETPARTYDETAILS + ":: " + partyResponse);
			serviceResponse = new JSONObject(partyResponse);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching party details: ", jsonExp);
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching party details: ", exp);
			return null;
		}

		return serviceResponse;
	}

	private String getDecodedBopDetails(String bopDetails) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::getDecodedBopDetails");
		String decodedBopDetails = "";
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(bopDetails);
			String decodedBase64 = new String(decodedBytes);
			decodedBopDetails = URLDecoder.decode(decodedBase64, StandardCharsets.UTF_8.toString());
			LOG.debug("PaymentAPIBackendDelegateImpl::getDecodedBopDetails::decodedBopDetails: " + decodedBopDetails);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error occured while decoding bop details", e);
		}

		return decodedBopDetails;
	}

	private boolean isIBAN(String toAccountNumber) {
		boolean result = false;
		if (Character.isDigit(toAccountNumber.charAt(0))) {
			LOG.debug("PaymentAPIBackendDelegateImpl::isIBAN: " + result);
		} else {
			IBANCheckDigit iBanDigit = new IBANCheckDigit();
			result = iBanDigit.isValid(toAccountNumber);
			LOG.debug("PaymentAPIBackendDelegateImpl::isIBAN:isValid:: " + result);
		}
		return result;
	}

	private String getFromAccountSwiftCode(String fromAccountNumber, DataControllerRequest request, String bicTransformation) {
		String fromAccountSwiftCode = "";
		try {
			//	String accountDetails1 = SBGCommonUtils.getServerPropertyValue(key, request);
			String accountDetails = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from Arrangement Extension
			LOG.debug("&&&&&BICFromExtension: " + accountDetails);
			if (StringUtils.isBlank(accountDetails)) {
			/*	String key = "AE_" + fromAccountNumber + "_EXTENSIONDATA";
				LOG.debug("PaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:key: " + key);
				accountDetails = SBGCommonUtils.getClientAppValue(key, request);
				LOG.debug("PaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:accountDetails: " + accountDetails);
				if (accountDetails != null && accountDetails.isEmpty() == false) {
					JSONObject accountObj = new JSONObject(accountDetails);
					if (accountObj.has("bic")) {
						fromAccountSwiftCode = accountObj.getString("bic");
						LOG.debug("PaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:before: " + fromAccountSwiftCode);
						LOG.debug("&&&&&BICFromServerProps: " + fromAccountSwiftCode);
						fromAccountSwiftCode = getBICTransformedSwiftCode(fromAccountSwiftCode, bicTransformation);
						LOG.debug("PaymentAPIBackendDelegateImpl::getFromAccountSwiftCode: " + fromAccountSwiftCode);
					}
				}*/
				return fromAccountSwiftCode;
			} else {
				fromAccountSwiftCode = getBICTransformedSwiftCode(accountDetails, bicTransformation);
				LOG.debug("PaymentAPIBackendDelegateImpl::getFromArrangementAccountSwiftCode: " + fromAccountSwiftCode);
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
			if (bicTransformation.equals("true" ) && (bic.length() >= SbgURLConstants.BIC_TRANSFORMATION_MIN_LENGTH)) {
				LOG.debug("getBICTransformedSwiftCode::bic.length()::" + bic.length());
				StringBuilder swiftCode = new StringBuilder(bic);
				swiftCode.setCharAt(7, '0');
				bicTransformed = swiftCode.toString();
				LOG.debug("getBICTransformedSwiftCode::bicTransformed::" + bicTransformed);
			}
		}
		return bicTransformed;
	}

	private String getCountryCode(String beneficiarycountry) {
		String countryCode = "";
		String countryRsp = "";
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_COUNTRY_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "";
		filter = "Name " + DBPUtilitiesConstants.EQUAL + "'"+beneficiarycountry+"'";
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		LOG.debug("PaymentAPIBackendDelegateImpl::requestParams:: " + requestParams);
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
						LOG.debug("PaymentAPIBackendDelegateImpl::getCountryCode_Value:: " + countryCode);
					}
				}
			}

		} catch (DBPApplicationException e) {
			LOG.error("Error occured while fetching country: " + e.getStackTrace());
		}
		return countryCode;
	}

	private String getBeneName(InternationalFundTransferBackendDTO input, DataControllerRequest request) {
		String beneName = "";
		if (input.getBeneficiaryName() == null || input.getBeneficiaryName().isEmpty()) {
			if (request.containsKeyInRequest("entityType")) {
				if (SbgURLConstants.BENE_INDIVIDUAL.equals(request.getParameter("entityType"))) {
					if (request.getParameter("firstName") != null
							&& request.getParameter("firstName").isEmpty() == false) {
						beneName = request.getParameter("firstName") + " ";
					}

					if (request.getParameter("lastName") != null
							&& request.getParameter("lastName").isEmpty() == false) {
						beneName = beneName + request.getParameter("lastName");
					}
					beneName = beneName.trim();
				}
			} else {
				LOG.debug("buildRequestForSubmitPayment::BeneName is empty and entityType also not found");
			}

		} else {
			beneName = input.getBeneficiaryName();
		}

		LOG.debug("buildRequestForSubmitPayment::BeneName: " + beneName);
		return beneName;

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

		if(multipleDealsSb.charAt(multipleDealsSb.length()-1) == '_') {
			multipleDeals = multipleDealsSb.deleteCharAt(multipleDealsSb.length()-1).toString();
		}
		return multipleDeals;
	}

	private Map<String, Object> getRequestHeader(DataControllerRequest request,
												 InternationalFundTransferDTO resultDTO) {
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		Result resultCache = null;
		String authorization = "";

		try {
			resultCache =SBGCommonUtils.cacheFetchPingToken("Authorization",request);
		} catch (Exception e) {
			LOG.error("Exception occurred while fetching authorization from session: " + e.getStackTrace());
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
			return requestHeaders;
		}

		if (resultCache != null && resultCache.hasParamByName("Authorization")) {
			authorization = resultCache.getParamValueByName("Authorization").toString();
			requestHeaders.put("Authorization", authorization);
		} else {
			LOG.error("PaymentAPIBackendDelegateImpl::getRequestHeader --> IBM-Gateway authentication failed");
			populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
			return requestHeaders;
		}

		try {
			String clientId = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			if (clientId != null && clientId.isEmpty() == false && clientSecret != null
					&& clientSecret.isEmpty() == false) {
				requestHeaders.put("X-IBM-Client-Id", clientId);
				requestHeaders.put("X-IBM-Client-Secret", clientSecret);
			} else {
				LOG.error(
						"PaymentAPIBackendDelegateImpl::getRequestHeader --> ClientId and Client Secret are null or empty");
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

	private InternationalFundTransferDTO populateErrorDetails(InternationalFundTransferDTO resultDTO,
															  SbgErrorCodeEnum errorCodeEnum) {
		resultDTO.setDbpErrCode(errorCodeEnum.getErrorCodeAsString());
		resultDTO.setDbpErrMsg(errorCodeEnum.getMessage());
		return resultDTO;
	}

	public String updateSubmitPaymentResponse(String referenceId, JSONObject serviceResponse) {
		LOG.error("Entry --> PaymentAPIBackendDelegateImpl::updateSubmitPaymentResponse entry");
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_INTERNATIONALFUNDTRANSFERSREFDATA_UPDATE;
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

	private JSONObject getContractCustomers(Map<String, Object> requestParams) {
		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::getCoreCustomerId");
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET;
		JSONObject serviceResponse = null;
		LOG.debug("getUserNameMailId::filter:: " + requestParams);
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET + ":: " + updateResponse);
			serviceResponse = new JSONObject(updateResponse);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching contract customers ", jsonExp);
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching contract customers: ", exp);
			return null;
		}

		return serviceResponse;
	}

//	private	JSONObject getCustomerData4mParty(DataControllerRequest request, String custId) {
//		
//		String corePartyId = getCorePartyId4mBackendIdentifier(custId);
//		if(!SBGCommonUtils.isStringEmpty(corePartyId)) {
//			return getPartyDetailsById(request, corePartyId);
//		}
//		
//		return null;
//	}

//	private	String getCorePartyId4mBackendIdentifier(String customerId) {
//		Map<String, Object> requestParams = new HashMap<>();
//		String filter = "", corePartyId = "";
//		
//		filter = "Customer_id " + DBPUtilitiesConstants.EQUAL + customerId;
//		filter = filter + " " + DBPUtilitiesConstants.AND + " ";
//		filter = filter + "BackendType " + DBPUtilitiesConstants.EQUAL + "CORE";
//		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
//		
//		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
//		String operationName = SbgURLConstants.OPERATION_DBXDB_BACKENDIDENTIFIER_GET;
//		
//		JSONObject serviceResponse = null;
//		LOG.debug("getCorePartyId4mBackendIdentifier::filter:: " + requestParams);
//		try {
//			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
//					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
//			LOG.debug("getCorePartyId4mBackendIdentifier ::: Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
//					+ SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET + ":: " + updateResponse);
//			serviceResponse = new JSONObject(updateResponse);
//
//			if (serviceResponse != null && serviceResponse.has("backendidentifier")
//					&& serviceResponse.getJSONArray("backendidentifier").length() > 0) {
//				JSONObject contractCustomer = serviceResponse.getJSONArray("backendidentifier").getJSONObject(0);
//				corePartyId = contractCustomer.optString("BackendId");
//				LOG.debug("getCorePartyId4mBackendIdentifier::corePartyId: " + corePartyId);
//			} 
//		}
//
//		catch (JSONException jsonExp) {
//			LOG.error("getCorePartyId4mBackendIdentifier:: JSONExcpetion occured while fetching contract customers ", jsonExp);
//			return corePartyId;
//		} catch (Exception exp) {
//			LOG.error("getCorePartyId4mBackendIdentifier:: Excpetion occured while fetching contract customers: ", exp);
//			return corePartyId;
//		}
//		return corePartyId;
//	}

//	private JSONObject getPartyDetailsById(DataControllerRequest request, String partyId) {
//		LOG.debug("Entry --> PaymentAPIBackendDelegateImpl::getPartyDetailsById");
//		String serviceName = SbgURLConstants.SERVICE_PARTYMSSERVICE;
//		String operationName = SbgURLConstants.OPERATION_GETPARTYDETAILSBYID;		
//		JSONObject serviceResponse = null;
//
//		try {
//			Map<String, Object> requestParameter = new HashMap<>();
//			Map<String, Object> requestHeaders = new HashMap<>();		
//			String partyAuthKey = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PARTYMS_AUTHORIZATION_KEY, request);
//			
//			requestParameter.put("partyid", partyId);
//			requestHeaders.put("X-API-Key", partyAuthKey);
//			
//			String partyResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
//			.withOperationId(operationName).withRequestParameters(requestParameter)
//			.withRequestHeaders(requestHeaders).build().getResponse();
//			
//			LOG.debug("getPartyDetailsById ::: Response of Service : " + serviceName + " Operation : "
//					+ operationName + ":: " + partyResponse);
//			serviceResponse = new JSONObject(partyResponse);
//		}
//
//		catch (JSONException jsonExp) {
//			LOG.error("JSONExcpetion occured while fetching party details: ", jsonExp);
//			return null;
//		} catch (Exception exp) {
//			LOG.error("Excpetion occured while fetching party details: ", exp);
//			return null;
//		}
//
//		return serviceResponse;
//	}

}
