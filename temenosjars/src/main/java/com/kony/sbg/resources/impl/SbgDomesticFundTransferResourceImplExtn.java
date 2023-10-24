package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.transactionservices.resource.impl.InterBankFundTransferResourceImpl;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.DomesticPaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegateInterBank;
import com.kony.sbg.business.api.SbgInterBankFundTransferBusinessDelegateExtn;
import com.kony.sbg.dto.InterBankFundTransferRefDataDTO;
import com.kony.sbg.helpers.SbgCreateTransferHelper;
import com.kony.sbg.resources.api.SbgDomesticFundTransferResource;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.TransactionLimitsBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.AuditLog;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.commonsutils.LogEvents;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InterBankFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;

public class SbgDomesticFundTransferResourceImplExtn extends InterBankFundTransferResourceImpl
		implements SbgDomesticFundTransferResource {

	private static final Logger LOG = LogManager.getLogger(SbgDomesticFundTransferResourceImplExtn.class);
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(CustomerBusinessDelegate.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	TransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(TransactionLimitsBusinessDelegate.class);
	InterBankFundTransferBusinessDelegate interbanktransferDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);

	InterBankFundTransferBackendDelegate interbankBackendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(InterBankFundTransferBackendDelegate.class);

	DomesticPaymentAPIBackendDelegate domesticBackendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(DomesticPaymentAPIBackendDelegate.class);

	AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(AccountBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class)
			.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

	SbgInterBankFundTransferBusinessDelegateExtn sbgInterBankFundTransferBusinessDelegateExtn = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(SbgInterBankFundTransferBusinessDelegateExtn.class);

	public Result createTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		InterBankFundTransferDTO interbankDTO = null;
		Result result = new Result();
		Double amount = null;

		InterBankFundTransferRefDataDTO refDto = setAdditionalTransfersRefData(inputParams);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = null;

		String amountValue = inputParams.get("amount").toString();
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		String frequencyType = inputParams.get("frequencyType") == null ? null
				: inputParams.get("frequencyType").toString();

		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String companyId = account.getOrganizationId();

		String baseCurrency = application.getBaseCurrencyFromCache();
		String transactionCurrency = inputParams.get("transactionCurrency") != null
				? inputParams.get("transactionCurrency").toString()
				: baseCurrency;
		String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString()
				: null;

		String validate = inputParams.get("validate") == null ? null : String.valueOf(inputParams.get("validate"));

		if (amountValue == null || amountValue == "") {
			return ErrorCodeEnum.ERR_12031.setErrorCode(new Result());
		}

		featureActionId = FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE;

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				fromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}

		try {
			amount = Double.parseDouble(amountValue);
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_10624.setErrorCode(new Result());
		}

		fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		inputParams.put("featureActionId", featureActionId);
		inputParams.put("companyId", companyId);
		inputParams.put("roleId", customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));
		inputParams.put("createdby", createdby);

		try {
			interbankDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), InterBankFundTransferDTO.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return ErrorCodeEnum.ERR_28021.setErrorCode(new Result());
		}

		/* Bank Name not populated cause of field name mismatch, so populating it */
		interbankDTO.setBankName(inputParams.get("beneficiaryBankName").toString());
		interbankDTO.setPayeeName(interbankDTO.getBeneficiaryName());
		String branch = inputParams.get("branchCode") != null ? inputParams.get("branchCode").toString() : "";
		interbankDTO.setBicCode(branch);
		String date = interbankDTO.getScheduledDate() == null
				? (interbankDTO.getProcessingDate() == null
						? (interbankDTO.getFrequencyStartDate() == null ? application.getServerTimeStamp()
								: interbankDTO.getFrequencyStartDate())
						: interbankDTO.getProcessingDate())
				: interbankDTO.getScheduledDate();

		// String backendid = inputParams.get("transactionId") == null
		// || (StringUtils.isEmpty(inputParams.get("transactionId").toString())) ? null
		// : inputParams.get("transactionId").toString();

		String backendid;
		try {
			backendid = inputParams.get("transactionId") == null
					|| (StringUtils.isEmpty(inputParams.get("transactionId").toString()))
							? SBGCommonUtils.generateUniqueIDHyphenSeperated(13, request).toUpperCase()
							: inputParams.get("transactionId").toString();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {		
			e.printStackTrace();
			backendid = inputParams.get("transactionId") == null
				|| (StringUtils.isEmpty(inputParams.get("transactionId").toString()))
						? "BLL" + CommonUtils.generateUniqueIDHyphenSeperated(0, 13).toUpperCase()
						: inputParams.get("transactionId").toString();
		}

		String beneficiaryId = inputParams.get("beneficiaryId") == null ? null
				: inputParams.get("beneficiaryId").toString();
		String requestid = "";

		if ("true".equalsIgnoreCase(validate)) {

			/*
			 * Mashilo Joseph Monene : Method call to Validate Ref-Data Validations(cut off
			 * time, public holidays, etc)
			 */
			JSONObject ValidationsResponseObj = new JSONObject();
			ValidationsResponseObj = sbgInterBankFundTransferBusinessDelegateExtn.sbgValidations(request);
			if (SBGConstants.FALSE.equalsIgnoreCase(ValidationsResponseObj.optString(SBGConstants.IS_VALID))) {
				LOG.info("[SbgInterBankFundTransferResourceImpl] RefData Validations Failed.");
				result.addParam("RefDataValidation", SBGConstants.FAILED);
				result.addParam("dbpErrMsg",
						ValidationsResponseObj.optString(SBGConstants.MESSAGE));
				return result;
			}

			InterBankFundTransferBackendDTO interBankFundTransferBackendDTO = new InterBankFundTransferBackendDTO();
			interBankFundTransferBackendDTO = interBankFundTransferBackendDTO.convert(interbankDTO);

			InterBankFundTransferDTO validateinterbankDTO = interbankBackendDelegate
					.validateTransaction(interBankFundTransferBackendDTO, request);
			try {
				result = JSONToResult.convert(new JSONObject(validateinterbankDTO).toString());
				// return result;
			} catch (JSONException e) {
				LOG.error(
						"Error occured while converting the response from Line of Business service for interbank transfer: ",
						e);
				return ErrorCodeEnum.ERR_21217.setErrorCode(new Result());
			}
		}

		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(companyId);
		transactionStatusDTO.setAccountId(fromAccountNumber);
		transactionStatusDTO.setAmount(amount);
		transactionStatusDTO.setStatus(TransactionStatusEnum.NEW);
		transactionStatusDTO.setDate(date);
		transactionStatusDTO.setTransactionCurrency(transactionCurrency);
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setConfirmationNumber(backendid);
		transactionStatusDTO.setServiceCharge(serviceCharge);

		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);
		if (transactionStatusDTO == null) {
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		}
		if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}
		/* Commenting below line - Changing message for Sent and Pending Status */
		// TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		TransactionStatusEnum transactionStatus = SBGCommonUtils
				.setTransactionStatusMessage(transactionStatusDTO.getStatus(), true);
		boolean isSelfApproved;
		if (authorizationChecksBusinessDelegate
				.isUserAuthorizedForFeatureActionForAccount(createdby,
						"INTER_BANK_ACCOUNT_FUND_TRANSFER_SELF_APPROVAL", fromAccountNumber, false)) {
			isSelfApproved = transactionStatusDTO.isSelfApproved();
		} else {
			isSelfApproved = false;
			transactionStatusDTO.setSelfApproved(isSelfApproved);
		}
		// product
		interbankDTO.setStatus(transactionStatus.getStatus());
		interbankDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		interbankDTO.setRequestId(transactionStatusDTO.getRequestId());
		String confirmationNumber = (StringUtils.isEmpty(backendid))
				? transactionStatusDTO.getRequestId()
				: backendid;

		interbankDTO.setConfirmationNumber(confirmationNumber);
		try {
			interbankDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		interbankDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		// interbankDTO.setTransactionId(null);

		InterBankFundTransferDTO interbankdbxDTO = interbanktransferDelegate.createTransactionAtDBX(interbankDTO);
		if (interbankdbxDTO == null) {
			LOG.error("Error occured while creating entry into the DBX table: ");
			return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
		}
		if (interbankdbxDTO.getDbpErrCode() != null || interbankdbxDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", interbankdbxDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", interbankdbxDTO.getDbpErrMsg()));
			return result;
		}

		refDto.setReferenceId(interbankdbxDTO.getTransactionId());
		refDto.setConfirmationNumber(interbankdbxDTO.getConfirmationNumber());

		Object proofOfPaymentObj = inputParams.get("proofOfPayment");
		LOG.debug("#### proofOfPayment Object:" + proofOfPaymentObj);
		String proofOfPayment = proofOfPaymentObj != null ? proofOfPaymentObj.toString() : null;
		LOG.debug("#### proofOfPayment String:" + proofOfPayment);
		refDto.setProofOfPayment(proofOfPayment);

		RefDataBackendDelegateInterBank refbackendDelegate = (RefDataBackendDelegateInterBank) DBPAPIAbstractFactoryImpl
				.getBackendDelegate(RefDataBackendDelegateInterBank.class);
		InterBankFundTransferRefDataDTO resultRefData = refbackendDelegate
				.insertAdditionalFields(interbankdbxDTO.getTransactionId(), refDto);

		interbankdbxDTO.setValidate(validate);

		InterBankFundTransferBackendDTO interbankBackendDTO = new InterBankFundTransferBackendDTO();
		interbankBackendDTO = interbankBackendDTO.convert(interbankdbxDTO);
		// backendid = StringUtils.isEmpty(backendid) ?
		// interbankdbxDTO.getTransactionId() : backendid;

		String creditValueDate = inputParams.get("creditValueDate") == null ? ""
				: inputParams.get("creditValueDate").toString();
		String totalAmount = inputParams.get("totalAmount") == null ? "" : inputParams.get("totalAmount").toString();
		String exchangeRate = inputParams.get("exchangeRate") == null ? "" : inputParams.get("exchangeRate").toString();
		String intermediaryBicCode = inputParams.get("intermediaryBicCode") == null ? ""
				: inputParams.get("intermediaryBicCode").toString();
		String clearingCode = inputParams.get("clearingCode") == null ? "" : inputParams.get("clearingCode").toString();
		String e2eReference = inputParams.get("e2eReference") == null ? "" : inputParams.get("e2eReference").toString();
		String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
		String beneficiaryBankName = inputParams.get("beneficiaryBankName") == null ? ""
				: inputParams.get("beneficiaryBankName").toString();
		String beneficiaryAddressLine2 = inputParams.get("beneficiaryAddressLine2") == null ? ""
				: inputParams.get("beneficiaryAddressLine2").toString();
		String beneficiaryPhone = inputParams.get("beneficiaryPhone") == null ? ""
				: inputParams.get("beneficiaryPhone").toString();
		String beneficiaryEmail = inputParams.get("beneficiaryEmail") == null ? ""
				: inputParams.get("beneficiaryEmail").toString();
		String beneficiaryState = inputParams.get("beneficiaryState") == null ? ""
				: inputParams.get("beneficiaryState").toString();

		String cell = !StringUtils.isEmpty(beneficiaryPhone)
				? getPhoneCountryCode(interbankBackendDTO.getBeneficiarycountry()) + beneficiaryPhone
				: beneficiaryPhone;

		interbankBackendDTO.setCreditValueDate(creditValueDate);
		interbankBackendDTO.setTotalAmount(totalAmount);
		interbankBackendDTO.setExchangeRate(exchangeRate);
		interbankBackendDTO.setIntermediaryBicCode(intermediaryBicCode);
		interbankBackendDTO.setClearingCode(clearingCode);
		interbankBackendDTO.setE2eReference(e2eReference);
		interbankBackendDTO.setBeneficiaryId(beneficiaryId);
		interbankBackendDTO.setOverrides(overrides);
		interbankBackendDTO.setBeneficiaryBankName(beneficiaryBankName);
		interbankBackendDTO.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
		interbankBackendDTO.setBeneficiaryPhone(cell);
		interbankBackendDTO.setBeneficiaryEmail(beneficiaryEmail);
		interbankBackendDTO.setBeneficiaryState(beneficiaryState);
		interbankBackendDTO.setClearingCode(branch);

		try {
			interbankBackendDTO.setAmount(Double.parseDouble(interbankBackendDTO.getTransactionAmount()));
			interbankdbxDTO.setAmount(Double.parseDouble(interbankBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}

		try {
			String responseObj = new JSONObject(interbankBackendDTO).toString();
			result = JSONToResult.convert(responseObj);
		} catch (JSONException e) {
			LOG.error(
					"Error occured while converting the response from Line of Business service for interbank transfer: ",
					e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		String transactionid = interbankdbxDTO.getTransactionId();
		InterBankFundTransferDTO interbanktransactionDTO = new InterBankFundTransferDTO();

		String createWithPaymentId = inputParams.get("createWithPaymentId") == null ? ""
				: inputParams.get("createWithPaymentId").toString();

		if (transactionStatus == TransactionStatusEnum.SENT) {
			if (StringUtils.isEmpty(backendid)
					|| (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
				if (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")) {

					interbankBackendDTO.setTransactionId(interbankdbxDTO.getTransactionId());
					interbankBackendDTO.setConfirmationNumber(backendid);
					interbankBackendDTO.setReferenceId(interbankdbxDTO.getTransactionId());
					String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
					interbankBackendDTO.setCharges(charges);
				} else {
					interbankBackendDTO.setTransactionId(null);
				}

				try {
					interbanktransactionDTO = domesticBackendDelegate
							.createTransactionWithoutApproval(interbankBackendDTO, request, branch);
					if (interbanktransactionDTO == null) {
						LOG.debug("Sending the data to Volante failed for reference: " + transactionid);
						// interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
						// TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
						// return ErrorCodeEnum.ERR_12601.setErrorCode(result);
						interbanktransactionDTO = new InterBankFundTransferDTO();
						interbanktransactionDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
						interbanktransactionDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
					}
				} catch (Exception e) {
					LOG.error("EXCEPTION MAKING CALL TO VOLPAY: " + e.getMessage());
					if (interbanktransactionDTO == null) {
						// setting the error code for any exceptions in volpay payment submit
						interbanktransactionDTO = new InterBankFundTransferDTO();
					}
					interbanktransactionDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
					interbanktransactionDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
				}

				// persists the data in Dbxdb.Transaction table
				LOG.debug("Sending the data to Dbxdb.Transaction ");
				// internationalfundtransferDTO =
				// updateTransactionTable(internationfundtransferBackendDTO, request);
				// createTransaction(String methodID, Object[] inputArray, DataControllerRequest
				// request, DataControllerResponse response);
				try {
					String tranxStatus = SBGCommonUtils.isStringEmpty(interbanktransactionDTO.getDbpErrCode())
							? "Pending processing"
							: DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
					LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
							+ interbanktransactionDTO.getDbpErrCode());
					new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
							confirmationNumber);
				} catch (Exception e) {
					LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
					LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + transactionid);
				}

			} else {
				String frequency = StringUtils.isEmpty(interbankDTO.getFrequencyTypeId()) ? null
						: interbankDTO.getFrequencyTypeId();
				interbanktransactionDTO = interbanktransferDelegate.approveTransaction(backendid, request, frequency);
				if (interbanktransactionDTO == null) {
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					return ErrorCodeEnum.ERR_29020.setErrorCode(result);
				}
			}

			if (interbanktransactionDTO.getDbpErrCode() != null || interbanktransactionDTO.getDbpErrMsg() != null) {
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
						TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				result.addParam(new Param("errorDetails", interbanktransactionDTO.getErrorDetails()));
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbanktransactionDTO.getDbpErrMsg());
			}

			if (interbanktransactionDTO.getReferenceId() == null
					|| "".equals(interbanktransactionDTO.getReferenceId())) {
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
						TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			interbankDTO = interbanktransactionDTO;
			interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
					TransactionStatusEnum.EXECUTED.getStatus(), interbanktransactionDTO.getConfirmationNumber());

			result.addParam(new Param("referenceId", interbanktransactionDTO.getConfirmationNumber()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
			result.addParam(new Param("message", transactionStatus.getMessage()));
		} else if (transactionStatus == TransactionStatusEnum.PENDING) {
			requestid = transactionStatusDTO.getRequestId();
			String pendingrefId = null;
			interbankdbxDTO.setCreditValueDate(creditValueDate);
			interbankdbxDTO.setTotalAmount(totalAmount);
			interbankdbxDTO.setExchangeRate(exchangeRate);
			interbankdbxDTO.setIntermediaryBicCode(intermediaryBicCode);
			interbankdbxDTO.setClearingCode(clearingCode);
			interbankdbxDTO.setE2eReference(e2eReference);
			if (StringUtils.isEmpty(backendid)
					|| (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
				if (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")) {
					interbankdbxDTO.setTransactionId(backendid);
					String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
					interbankdbxDTO.setCharges(charges);
				}
				InterBankFundTransferDTO interbankpendingtransactionDTO = interbanktransferDelegate
						.createPendingTransaction(interbankdbxDTO, request);
				if (interbankpendingtransactionDTO == null) {
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					LOG.error("Error occured while creating entry into the backend table: ");
					// return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
				}

				// persists the data in Dbxdb.Transaction table
				LOG.debug("Sending the data to Dbxdb.Transaction ");
				// internationalfundtransferDTO =
				// updateTransactionTable(internationfundtransferBackendDTO, request);
				// createTransaction(String methodID, Object[] inputArray, DataControllerRequest
				// request, DataControllerResponse response);
				try {
					String tranxStatus = SBGCommonUtils
							.isStringEmpty(interbankpendingtransactionDTO.getDbpErrCode())
									? DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING
									: DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
					LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
							+ interbankpendingtransactionDTO.getDbpErrCode());
					new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
							confirmationNumber);
				} catch (Exception e) {
					LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
					LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + transactionid);
				}

				if (interbankpendingtransactionDTO.getDbpErrCode() != null
						|| interbankpendingtransactionDTO.getDbpErrMsg() != null) {
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					result.addParam(new Param("errorDetails",
							interbankpendingtransactionDTO.getErrorDetails()));
					return ErrorCodeEnum.ERR_00000.setErrorCode(result,
							interbankpendingtransactionDTO.getDbpErrMsg());
				}
				backendid = interbankpendingtransactionDTO.getReferenceId();

				// if (interbankpendingtransactionDTO.getDbpErrCode() != null
				// || interbankpendingtransactionDTO.getDbpErrMsg() != null) {
				// interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
				// TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				// result.addParam(new Param("errorDetails",
				// interbankpendingtransactionDTO.getErrorDetails()));
				// return ErrorCodeEnum.ERR_00000.setErrorCode(result,
				// interbankpendingtransactionDTO.getDbpErrMsg());
				// }
				// backendid = interbankpendingtransactionDTO.getReferenceId();
				// interbankDTO = interbankpendingtransactionDTO;
			}
			pendingrefId = backendid;
			interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, transactionStatus.toString(),
					backendid);
			transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestid, backendid,
					isSelfApproved, featureActionId, request);
			if (transactionStatusDTO == null) {
				interbankBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
						TransactionStatusEnum.FAILED.getStatus(), backendid);
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}
			if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				interbankBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,
						TransactionStatusEnum.FAILED.getStatus(), backendid);
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}

			transactionStatus = transactionStatusDTO.getStatus();
			backendid = transactionStatusDTO.getConfirmationNumber();

			result.addParam(new Param("requestId", requestid));

			if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
				result.addParam(new Param("referenceId", backendid));
			} else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
				result.addParam(new Param("referenceId", pendingrefId));
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, transactionStatus.toString(),
						pendingrefId);
			}

			// code snippet being added for alerts on inter transfer
			try {
				LogEvents.pushAlertsForApprovalRequests(featureActionId, request, response, inputParams, null,
						backendid, requestid, CustomerSession.getCustomerName(customer), null);
			} catch (Exception e) {
				LOG.error("Failed at pushAlertsForApprovalRequests " + e);
			}

		} else if (transactionStatus == TransactionStatusEnum.APPROVED) {
			result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
			result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
			result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
		}

		if (interbankDTO.getOverrides() != null) {
			result.addParam(new Param("overrides", interbankDTO.getOverrides()));
		}
		if (interbankDTO.getOverrideList() != null) {
			result.addParam(new Param("overrideList", interbankDTO.getOverrideList()));
		}

		if (interbankDTO.getCharges() != null) {
			result.addParam(new Param("charges", interbankDTO.getCharges()));
		}
		if (interbankDTO.getExchangeRate() != null) {
			result.addParam(new Param("exchangeRate", interbankDTO.getExchangeRate()));
		}
		if (interbankDTO.getTotalAmount() != null) {
			result.addParam(new Param("totalAmount", interbankDTO.getTotalAmount()));
		}
		if (interbankDTO.getMessageDetails() != null) {
			result.addParam(new Param("messageDetails", interbankDTO.getMessageDetails()));
		}
		if (interbankDTO.getQuoteCurrency() != null) {
			result.addParam(new Param("quoteCurrency", interbankDTO.getQuoteCurrency()));
		}
		try {
			_logTransaction(request, response, inputArray, result, transactionStatus,
					transactionid, interbankDTO, requestid);
		} catch (Exception e) {
			LOG.error("Error occured while audit logging.", e);
		}

		if (resultRefData != null) {
			result.addParam("statementReference", resultRefData.getStatementrefno());
			result.addParam("beneficiaryReference", resultRefData.getBenerefeno());
			result.addParam("complianceCode", resultRefData.getCompliancecode());
			result.addParam("purposeCode", resultRefData.getPurposecode());
			result.addParam("rfqDetails", resultRefData.getRfqDetails());
			result.addParam("proofOfPaymentObjectCount",
					String.valueOf(coundJsonObjects(resultRefData.getProofOfPayment())));
		}

		return result;
	}

	/**
	 * Logs Other Bank Transfer status in auditactivity
	 * 
	 * @param request
	 * @param response
	 * @param result
	 * @param transactionStatus
	 * @param interbankDTO
	 * @param referenceId
	 * @param requestId
	 */
	private void _logTransaction(DataControllerRequest request, DataControllerResponse response, Object[] inputArray,
			Result result, TransactionStatusEnum transactionStatus, String referenceId,
			InterBankFundTransferDTO interbankDTO, String requestId) {

		String enableEvents = EnvironmentConfigurationsHandler.getValue(Constants.ENABLE_EVENTS, request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		try {
			ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ApproversBusinessDelegate.class);
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);

			AuditLog auditLog = new AuditLog();

			String eventType = Constants.MAKE_TRANSFER;
			String eventSubType = "";
			String producer = "Transactions/POST(createTransfer)";
			String statusID = "";
			String frequencyType = result.getParamValueByName(Constants.FREQUENCYTYPE);
			String isScheduled = result.getParamValueByName(Constants.ISSCHEDULED);
			boolean isSMEUser = CustomerSession.IsBusinessUser(customer);
			String fromAccountNumber = "";
			String toAccountNumber = "";

			if (request.containsKeyInRequest("validate")) {
				String validate = request.getParameter("validate");
				if (StringUtils.isNotBlank(validate) && validate.equalsIgnoreCase("true"))
					return;
			}

			if (request.containsKeyInRequest("fromAccountNumber")) {
				fromAccountNumber = request.getParameter("fromAccountNumber");
			}
			if (request.containsKeyInRequest("toAccountNumber")) {
				toAccountNumber = request.getParameter("toAccountNumber");
			}
			;

			JsonObject customParams = new JsonObject();
			customParams = auditLog.buildCustomParamsForAlertEngine(fromAccountNumber, toAccountNumber, customParams);

			eventSubType = auditLog.deriveSubTypeForExternalTransfer(isScheduled, frequencyType,
					FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
			List<Param> params = result.getAllParams();
			for (Param param : params) {
				if (request.containsKeyInRequest(param.getName())) {
					continue;
				} else {
					customParams.addProperty(param.getName(), param.getValue());
				}
			}
			if (transactionStatus.toString().contains("DENIED")) {
				statusID = Constants.SID_EVENT_FAILURE;
				customParams.addProperty(Constants.REFERENCEID, result.getParamValueByName(Constants.REFERENCEID));
			} else {
				switch (transactionStatus) {
					case SENT:
						referenceId = customParams.get("referenceId").toString();
						if (interbankDTO == null) {
							statusID = Constants.SID_EVENT_FAILURE;
						}
						if (interbankDTO.getDbpErrMsg() != null && !interbankDTO.getDbpErrMsg().isEmpty()) {
							statusID = Constants.SID_EVENT_FAILURE;
						}
						if (referenceId == null || "".equals(referenceId)) {
							statusID = Constants.SID_EVENT_FAILURE;
						} else {
							statusID = Constants.SID_EVENT_SUCCESS;
							customParams.addProperty(Constants.REFERENCEID, referenceId);
							if (isSMEUser) {
								customParams.addProperty(Constants.APPROVERS, "Pre-Approved");
								customParams.addProperty("approvedBy", "Pre-Approved");
							}
						}
						break;
					case PENDING:
						statusID = Constants.SID_EVENT_SUCCESS;
						customParams.addProperty(Constants.REFERENCEID, referenceId);
						eventSubType = Constants.PENDING_APPROVAL_ + eventSubType;
						List<String> approvers = approversBusinessDelegate.getRequestApproversList(requestId);
						if (approvers == null) {
							customParams.addProperty(Constants.APPROVERS, "");
						} else {
							customParams.addProperty(Constants.APPROVERS, approvers.toString());
						}
						break;
					default:
						break;
				}
			}
			if (isSMEUser) {
				customParams.addProperty("approvedBy", "N/A");
				customParams.addProperty("rejectedBy", "N/A");
			}

			AdminUtil.addAdminUserNameRoleIfAvailable(customParams, request);

			EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusID, null,
					CustomerSession.getCustomerId(customer), null, customParams);
		} catch (Exception e) {
			LOG.error("Error while pushing to Audit Engine.");
		}
	}

	private InterBankFundTransferRefDataDTO setAdditionalTransfersRefData(Map<String, Object> inputParams) {
		LOG.debug("Entry -- > SbgInternationalFundTransferResourceImplExtn::setAdditionalTransfersRefData");
		InterBankFundTransferRefDataDTO refDto = new InterBankFundTransferRefDataDTO();
		String purposecode = SBGCommonUtils.isStringEmpty(inputParams.get("purposeCode")) ? ""
				: inputParams.get("purposeCode").toString();
		String compliancecode = SBGCommonUtils.isStringEmpty(inputParams.get("complianceCode")) ? ""
				: inputParams.get("complianceCode").toString();
		String statementrefno = SBGCommonUtils.isStringEmpty(inputParams.get("statementReference")) ? ""
				: inputParams.get("statementReference").toString();
		String benerefeno = SBGCommonUtils.isStringEmpty(inputParams.get("beneficiaryReference")) ? ""
				: inputParams.get("beneficiaryReference").toString();
		String rfqDetails = SBGCommonUtils.isStringEmpty(inputParams.get("rfqDetails")) ? ""
				: inputParams.get("rfqDetails").toString();
		String bopDetails = SBGCommonUtils.isStringEmpty(inputParams.get("bopDetails")) ? ""
				: inputParams.get("bopDetails").toString();
		String clearingCode = SBGCommonUtils.isStringEmpty(inputParams.get("clearingCode")) ? ""
				: inputParams.get("clearingCode").toString();
		String beneficiaryAddressLine2 = SBGCommonUtils.isStringEmpty(inputParams.get("beneficiaryAddressLine2")) ? ""
				: inputParams.get("beneficiaryAddressLine2").toString();
		String beneficiaryState = SBGCommonUtils.isStringEmpty(inputParams.get("beneficiaryState")) ? ""
				: inputParams.get("beneficiaryState").toString();

		String beneficiaryPhone = inputParams.get("beneficiaryPhone") == null ? ""
				: inputParams.get("beneficiaryPhone").toString();

		String beneficiaryEmail = inputParams.get("beneficiaryEmail") == null ? ""
				: inputParams.get("beneficiaryEmail").toString();

		String beneficiaryCategory = inputParams.get("payeeType") == null ? ""
				: inputParams.get("payeeType").toString();

		String beneficiaryCountry = inputParams.get("beneficiarycountry") == null ? ""
				: inputParams.get("beneficiarycountry").toString() + " ";

		String beneCode = inputParams.get("beneCode") == null ? ""
				: inputParams.get("beneCode").toString();

		String phoneCountryCode = getPhoneCountryCode(beneficiaryCountry);

		String fromAccountName = inputParams.get("fromAccountName") == null ? ""
				: inputParams.get("fromAccountName").toString();

		String cell = !StringUtils.isEmpty(beneficiaryPhone) && !beneficiaryPhone.contains(phoneCountryCode)
				? phoneCountryCode + beneficiaryPhone
				: beneficiaryPhone;
		refDto.setPurposecode(purposecode);
		refDto.setCompliancecode(compliancecode);
		refDto.setStatementrefno(statementrefno);
		refDto.setBenerefeno(benerefeno);
		refDto.setRfqDetails(rfqDetails);
		refDto.setBeneficiaryState(beneficiaryState);
		refDto.setBopDetails(bopDetails);
		refDto.setClearingCode(clearingCode);
		refDto.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
		refDto.setBeneficiaryPhone(cell);
		refDto.setBeneficiaryEmail(beneficiaryEmail);
		refDto.setBeneficiaryCategory(setBeneficiaryStatus(beneficiaryCategory));
		refDto.setBeneCode(beneCode);
		refDto.setFromAccountName(fromAccountName);

		return refDto;
	}

	private String setBeneficiaryStatus(String category) {
		String status = null;
		try {
			if ("Existing Payee".equals(category)) {
				status = "Existing beneficiary";
			} else if ("Save Payee".equals(category)) {
				status = "New beneficiary";
			} else if ("New Payee".equals(category)) {
				status = "Once-off beneficiary";
			}
		} catch (Exception e) {
			LOG.error("Error on setBeneficiaryStatus.", e);
		}

		return status;
	}

	private String getPhoneCountryCode(String beneficiarycountry) {
		String phoneCountryCode = "";
		String countryRsp = "";
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_COUNTRY_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "";
		filter = "Name " + DBPUtilitiesConstants.EQUAL + "'" + beneficiarycountry + "'";
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		LOG.debug("SbgDomesticFundTransferResourceImplExtn::requestParams:: " + requestParams);
		try {
			countryRsp = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			JSONObject respObj = new JSONObject(countryRsp);
			if (respObj.has("country")) {
				JSONArray countries = respObj.getJSONArray("country");
				if (countries.length() > 0) {
					JSONObject countryObj = countries.getJSONObject(0);
					if (countryObj.has("Code")) {
						phoneCountryCode = countryObj.getString("phoneCountryCode") + " ";
						LOG.debug("SbgDomesticFundTransferResourceImplExtn::getPhoneCountryCodeValue:: "
								+ phoneCountryCode);
					}
				}
			}

		} catch (DBPApplicationException e) {
			LOG.error("Error occured while fetching country: " + e.getStackTrace());
		}
		return phoneCountryCode;
	}

	private int coundJsonObjects(String jsonArray) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object[] jsonObjectArray = mapper.readValue(jsonArray, Object[].class);
			return jsonObjectArray.length;
		} catch (Exception e) {
			LOG.debug("### coundJsonObjects method failed:", e);
			return 0;
		}
	}
}
