package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kony.sbg.helpers.SbgDocTransactionRelationHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.PaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegate;
import com.kony.sbg.business.api.EvaluateTransactionBusinessDelegate;
//import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.business.api.SbgInternationalFundTransferBusinessDelegateExtn;
import com.kony.sbg.dto.InternationalFundTransferRefDataDTO;
import com.kony.sbg.helpers.SbgCreateTransferHelper;
import com.kony.sbg.resources.api.SbgInternationalFundTransferResourceExtn;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
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
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.TransactionLimitsBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.AuditLog;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.commonsutils.LogEvents;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InternationalFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InternationalFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;
import com.temenos.dbx.product.transactionservices.resource.impl.InternationalFundTransferResourceImpl;

public class SbgInternationalFundTransferResourceImplExtn extends InternationalFundTransferResourceImpl
		implements SbgInternationalFundTransferResourceExtn {

	private static final Logger LOG = LogManager.getLogger(SbgInternationalFundTransferResourceImplExtn.class);

	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(CustomerBusinessDelegate.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	InternationalFundTransferBackendDelegate internationalfundBackendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(InternationalFundTransferBackendDelegate.class);
	TransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(TransactionLimitsBusinessDelegate.class);
	InternationalFundTransferBusinessDelegate internationalFundTransferBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(InternationalFundTransferBusinessDelegate.class);
	AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(AccountBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	SbgInternationalFundTransferBusinessDelegateExtn sbgInternationalFundTransferBusinessDelegateExtn = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(SbgInternationalFundTransferBusinessDelegateExtn.class);
	PaymentAPIBackendDelegate paymentAPIBackendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(PaymentAPIBackendDelegate.class);
	EvaluateTransactionBusinessDelegate evaluateTransactionBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(EvaluateTransactionBusinessDelegate.class);

	public Result createTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		LOG.debug("Entry --> SbgInternationalFundTransferResourceImpl::createTransaction -- Attempt#1");
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		LOG.debug("Entry --> inputParams" + inputParams.toString());

		InternationalFundTransferDTO internationfundtransferDTO = null;
		Result result = new Result();
		Double amount = null;

		InternationalFundTransferRefDataDTO refDto = setAdditionalTransfersRefData(inputParams);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = null;

		String amountValue = inputParams.get("amount").toString();
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();

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
		String frequencyType = inputParams.get("frequencyType") == null ? null
				: inputParams.get("frequencyType").toString();

		if (SBGCommonUtils.isStringEmpty(amountValue)) {
			return ErrorCodeEnum.ERR_12031.setErrorCode(new Result());
		}

		featureActionId = FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE;

		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

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
			internationfundtransferDTO = JSONUtils.parse(new JSONObject(inputParams).toString(),
					InternationalFundTransferDTO.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}

		LOG.debug("BeneficiaryName: " + internationfundtransferDTO.getBeneficiaryName());

		/* Bank Name not populated cause of field name mismatch, so populating it */
		internationfundtransferDTO.setBankName(inputParams.get("beneficiaryBankName").toString());
		internationfundtransferDTO.setPayeeName(internationfundtransferDTO.getBeneficiaryName());

		String date = internationfundtransferDTO.getScheduledDate() == null
				? (internationfundtransferDTO.getProcessingDate() == null
						? (internationfundtransferDTO.getFrequencyStartDate() == null ? application.getServerTimeStamp()
								: internationfundtransferDTO.getFrequencyStartDate())
						: internationfundtransferDTO.getProcessingDate())
				: internationfundtransferDTO.getScheduledDate();
		String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();

		TransactionStatusDTO transactionStatusDTO = null;

		if (SBGConstants.TRUE.equalsIgnoreCase(validate)) {
			/*
			 * Kumara Swamy : Method call to Validate Ref-Data Validations
			 */
			JSONObject ValidationsResponseObj = new JSONObject();
			ValidationsResponseObj = sbgInternationalFundTransferBusinessDelegateExtn.sbgValidations(request);
			if (SBGConstants.FALSE.equalsIgnoreCase(ValidationsResponseObj.optString(SBGConstants.IS_VALID))) {
				LOG.info("[SbgInternationalFundTransferResourceImpl] RefData Validations Failed.");
				result.addParam("RefDataValidation", SBGConstants.FAILED);
				result.addParam("dbpErrMsg", ValidationsResponseObj.optString(SBGConstants.MESSAGE));
				return result;
			}

			// BoP Form Validation
			LOG.debug("SbgInternationalFundTransferResourceImpl::validateBoPForm::before invocation");
			if (SBGCommonUtils.isStringEmpty(inputParams.get("bopDetails")) == false) {
				LOG.debug("SbgInternationalFundTransferResourceImpl::BopDetails:::" + inputParams.get("bopDetails"));
				LOG.debug("SbgInternationalFundTransferResourceImpl::validateBoPForm::bop is not empty");
				if (evaluateTransactionBusinessDelegate.validateBoPForm(inputParams.get("bopDetails").toString(),
						result, request) == false) {
					LOG.debug("SbgInternationalFundTransferResourceImpl::validateBoPForm::returned failure");
					return result;
				}
			}

			InternationalFundTransferBackendDTO internationalFundTransferBackendDTO = new InternationalFundTransferBackendDTO();
			internationalFundTransferBackendDTO = internationalFundTransferBackendDTO
					.convert(internationfundtransferDTO);

			InternationalFundTransferDTO validateaccountDTO = internationalfundBackendDelegate
					.validateTransaction(internationalFundTransferBackendDTO, request);
			try {
				result = JSONToResult.convert(new JSONObject(validateaccountDTO).toString());
				return result;
			} catch (JSONException e) {
				LOG.error(
						"Error occured while converting the response from Line of Business service for InternationalFund transfer: ",
						e);
				return ErrorCodeEnum.ERR_21217.setErrorCode(new Result());
			}
		}

		/*
		 * Brought down transactionId check as it is not needed at continue call. Also
		 * if validateTransactio extension is not invoked then transactionId is empty,
		 * therefore generating transactionId
		 */
		String transactionId = inputParams.get("transactionId") == null
				|| (StringUtils.isEmpty(inputParams.get("transactionId").toString()))
						? CommonUtils.generateUniqueIDHyphenSeperated(0, 16).toUpperCase()
						: inputParams.get("transactionId").toString();

		transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(companyId);
		transactionStatusDTO.setAccountId(fromAccountNumber);
		transactionStatusDTO.setAmount(amount);
		transactionStatusDTO.setStatus(TransactionStatusEnum.NEW);
		transactionStatusDTO.setDate(date);
		transactionStatusDTO.setTransactionCurrency(transactionCurrency);
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setConfirmationNumber(transactionId);
		transactionStatusDTO.setServiceCharge(serviceCharge);
		LOG.debug("BeforeValidateForApprovals::transactionStatusDTO.getamont()" + transactionStatusDTO.getAmount());
		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);
		if (transactionStatusDTO == null) {
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		}
		LOG.debug("After ValidateForApprovals::transactionStatusDTO.getamont()" + transactionStatusDTO.getAmount());
		if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}

		/* Commenting below line - Changing message for Sent and Pending Status */
		// TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		TransactionStatusEnum transactionStatus = SBGCommonUtils
				.setTransactionStatusMessage(transactionStatusDTO.getStatus());
		boolean isSelfApproved;
		if (authorizationChecksBusinessDelegate
				.isUserAuthorizedForFeatureActionForAccount(createdby,
						"INTERNATIONAL_ACCOUNT_FUND_TRANSFER_SELF_APPROVAL", fromAccountNumber, false)) {
			isSelfApproved = transactionStatusDTO.isSelfApproved();
		} else {
			isSelfApproved = false;
			transactionStatusDTO.setSelfApproved(isSelfApproved);
		}
		// product
		internationfundtransferDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		try {
			internationfundtransferDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		internationfundtransferDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		internationfundtransferDTO.setRequestId(transactionStatusDTO.getRequestId());
		internationfundtransferDTO.setStatus(transactionStatus.getStatus());
		internationfundtransferDTO.setConvertedAmount(transactionStatusDTO.getConvertedAmount());
		LOG.debug("internationfundtransferDTO.getConvertedAmount()" + internationfundtransferDTO.getConvertedAmount());
		String confirmationNumber = (StringUtils.isEmpty(transactionId))
				? Constants.REFERENCE_KEY + transactionStatusDTO.getRequestId()
				: transactionId;
		internationfundtransferDTO.setConfirmationNumber(confirmationNumber);

		InternationalFundTransferDTO internationalFundAccountdbxDTO = internationalFundTransferBusinessDelegate
				.createTransactionAtDBX(internationfundtransferDTO);

		if (internationalFundAccountdbxDTO == null) {
			LOG.debug("Error occured while creating entry into the DBX table: TheOtherOne");
			return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
		}

		if (internationalFundAccountdbxDTO.getDbpErrCode() != null
				|| internationalFundAccountdbxDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", internationalFundAccountdbxDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", internationalFundAccountdbxDTO.getDbpErrMsg()));
			return result;
		}

		refDto.setReferenceId(internationalFundAccountdbxDTO.getTransactionId());
		refDto.setConfirmationNumber(internationalFundAccountdbxDTO.getConfirmationNumber());
		RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate) DBPAPIAbstractFactoryImpl
				.getBackendDelegate(RefDataBackendDelegate.class);
		InternationalFundTransferRefDataDTO resultRefData = refbackendDelegate
				.insertAdditionalFields(internationalFundAccountdbxDTO.getTransactionId(), refDto);
		internationalFundAccountdbxDTO.setValidate(validate);

		InternationalFundTransferBackendDTO internationfundtransferBackendDTO = new InternationalFundTransferBackendDTO();
		internationfundtransferBackendDTO = internationfundtransferBackendDTO.convert(internationalFundAccountdbxDTO);

		String creditValueDate = inputParams.get("creditValueDate") == null ? ""
				: inputParams.get("creditValueDate").toString();
		String totalAmount = inputParams.get("totalAmount") == null ? "" : inputParams.get("totalAmount").toString();
		String exchangeRate = inputParams.get("exchangeRate") == null ? "" : inputParams.get("exchangeRate").toString();
		String intermediaryBicCode = inputParams.get("intermediaryBicCode") == null ? ""
				: inputParams.get("intermediaryBicCode").toString();
		String clearingCode = inputParams.get("clearingCode") == null ? "" : inputParams.get("clearingCode").toString();
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

		internationfundtransferBackendDTO.setCreditValueDate(creditValueDate);
		internationfundtransferBackendDTO.setTotalAmount(totalAmount);
		internationfundtransferBackendDTO.setExchangeRate(exchangeRate);
		internationfundtransferBackendDTO.setIntermediaryBicCode(intermediaryBicCode);
		internationfundtransferBackendDTO.setClearingCode(clearingCode);
		internationfundtransferBackendDTO.setOverrides(overrides);
		internationfundtransferBackendDTO.setBeneficiaryBankName(beneficiaryBankName);
		internationfundtransferBackendDTO.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
		internationfundtransferBackendDTO.setBeneficiaryPhone(beneficiaryPhone);
		internationfundtransferBackendDTO.setBeneficiaryEmail(beneficiaryEmail);
		internationfundtransferBackendDTO.setBeneficiaryState(beneficiaryState);

		String beneficiaryId = inputParams.get("beneficiaryId") == null ? null
				: inputParams.get("beneficiaryId").toString();
		internationfundtransferBackendDTO.setBeneficiaryId(beneficiaryId);

		try {
			internationfundtransferBackendDTO
					.setAmount(Double.parseDouble(internationfundtransferBackendDTO.getTransactionAmount()));
			internationalFundAccountdbxDTO
					.setAmount(Double.parseDouble(internationfundtransferBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}

		try {
			String responseObj = new JSONObject(internationfundtransferBackendDTO).toString();
			result = JSONToResult.convert(responseObj);
		} catch (JSONException e) {
			LOG.error(
					"Error occured while converting the response from Line of Business service for External transfer: ",
					e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		String referenceId = internationalFundAccountdbxDTO.getTransactionId();
		String requestId = "";
		String createWithPaymentId = inputParams.get("createWithPaymentId") == null ? ""
				: inputParams.get("createWithPaymentId").toString();

		LOG.debug("Sending the data further based on " + transactionStatus);
		if (transactionStatus == TransactionStatusEnum.SENT) {
			InternationalFundTransferDTO internationalfundtransferDTO = new InternationalFundTransferDTO();
			if (StringUtils.isEmpty(transactionId)
					|| (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true"))) {
				if (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true")) {
					internationfundtransferBackendDTO
							.setTransactionId(internationalFundAccountdbxDTO.getTransactionId());
					internationfundtransferBackendDTO.setConfirmationNumber(transactionId);
					internationfundtransferBackendDTO.setReferenceId(internationalFundAccountdbxDTO.getTransactionId());
					String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
					internationfundtransferBackendDTO.setCharges(charges);
				} else {
					internationfundtransferBackendDTO.setTransactionId(null);
				}

				try {
					// submits the request to Volante
					LOG.debug("Sending the data to Volante ");
					internationalfundtransferDTO = paymentAPIBackendDelegate
							.createTransactionWithoutApproval(internationfundtransferBackendDTO, request, inputParams);
					if (internationalfundtransferDTO == null) {
						LOG.debug("Sending the data to Volante failed for reference: " + referenceId);
						// setting the error code for any exceptions in volpay payment submit
						internationalfundtransferDTO = new InternationalFundTransferDTO();
						internationalfundtransferDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
						internationalfundtransferDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
					}
				} catch (Exception e) {
					// Applied try/catch to capture the data in further tables like transactions.
					LOG.error("EXCEPTION MAKING CALL TO VOLPAY: " + e.getMessage());
					if (internationalfundtransferDTO == null) {
						// setting the error code for any exceptions in volpay payment submit
						internationalfundtransferDTO = new InternationalFundTransferDTO();
					}
					internationalfundtransferDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
					internationalfundtransferDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
				}

				// persists the data in Dbxdb.Transaction table
				LOG.debug("Sending the data to Dbxdb.Transaction ");
				// internationalfundtransferDTO =
				// updateTransactionTable(internationfundtransferBackendDTO, request);
				// createTransaction(String methodID, Object[] inputArray, DataControllerRequest
				// request, DataControllerResponse response);
				try {
					String tranxStatus = SBGCommonUtils.isStringEmpty(internationalfundtransferDTO.getDbpErrCode())
							? "Pending processing"
							: DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
					LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
							+ internationalfundtransferDTO.getDbpErrCode());
					new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
							confirmationNumber);
				} catch (Exception e) {
					LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
					LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + referenceId);
				}

			} else {
				String frequency = StringUtils.isEmpty(internationfundtransferDTO.getFrequencyTypeId()) ? null
						: internationfundtransferDTO.getFrequencyTypeId();
				internationalfundtransferDTO = internationalFundTransferBusinessDelegate
						.approveTransaction(transactionId, request, frequency);
				if (internationalfundtransferDTO == null) {
					internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					return ErrorCodeEnum.ERR_29020.setErrorCode(result);
				}
			}

			if (internationalfundtransferDTO.getDbpErrCode() != null
					|| internationalfundtransferDTO.getDbpErrMsg() != null) {
				internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
						TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				result.addParam(new Param("errorDetails", internationalfundtransferDTO.getErrorDetails()));
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, internationalfundtransferDTO.getDbpErrMsg());
			}

			String refId = internationalfundtransferDTO.getReferenceId();
			if (refId == null || "".equals(refId)) {
				internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
						TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			/* While updating status passing confirmation number to maintain uniqueness */
			// internationfundtransferDTO = internationalfundtransferDTO;
			internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
					TransactionStatusEnum.EXECUTED.getStatus(), internationalfundtransferDTO.getConfirmationNumber());
			result.addParam(new Param("referenceId", internationalfundtransferDTO.getConfirmationNumber()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
			result.addParam(new Param("message", transactionStatus.getMessage()));
		}

		else if (transactionStatus == TransactionStatusEnum.PENDING) {
			internationalFundAccountdbxDTO.setCreditValueDate(creditValueDate);
			internationalFundAccountdbxDTO.setTotalAmount(totalAmount);
			internationalFundAccountdbxDTO.setExchangeRate(exchangeRate);
			internationalFundAccountdbxDTO.setIntermediaryBicCode(intermediaryBicCode);
			internationalFundAccountdbxDTO.setClearingCode(clearingCode);
			requestId = transactionStatusDTO.getRequestId();
			String pendingrefId = null;
			if (StringUtils.isEmpty(transactionId)
					|| (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true"))) {
				if (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true")) {
					internationalFundAccountdbxDTO.setTransactionId(transactionId);
					String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
					internationalFundAccountdbxDTO.setCharges(charges);
				}
				InternationalFundTransferDTO internationalFundTransferPendingtransactionDTO = internationalFundTransferBusinessDelegate
						.createPendingTransaction(internationalFundAccountdbxDTO, request);
				if (internationalFundTransferPendingtransactionDTO == null) {
					internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					LOG.debug("Error occured while creating entry into the backend table: ");
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
							.isStringEmpty(internationalFundTransferPendingtransactionDTO.getDbpErrCode())
									? DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING
									: DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
					LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
							+ internationalFundTransferPendingtransactionDTO.getDbpErrCode());
					new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
							confirmationNumber);
				} catch (Exception e) {
					LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
					LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + referenceId);
				}

				if (internationalFundTransferPendingtransactionDTO.getDbpErrCode() != null
						|| internationalFundTransferPendingtransactionDTO.getDbpErrMsg() != null) {
					internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
							TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					result.addParam(new Param("errorDetails",
							internationalFundTransferPendingtransactionDTO.getErrorDetails()));
					return ErrorCodeEnum.ERR_00000.setErrorCode(result,
							internationalFundTransferPendingtransactionDTO.getDbpErrMsg());
				}
				transactionId = internationalFundTransferPendingtransactionDTO.getReferenceId();
				// internationfundtransferDTO = internationalFundTransferPendingtransactionDTO;
			}
			pendingrefId = transactionId;
			internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
					transactionStatus.toString(), transactionId);
			transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, transactionId,
					isSelfApproved, featureActionId, request);

			if (transactionStatusDTO == null) {
				internationalfundBackendDelegate.deleteTransactionWithoutApproval(transactionId, null, frequencyType,
						request);
				internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
						TransactionStatusEnum.FAILED.getStatus(), transactionId);
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}

			if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				internationalfundBackendDelegate.deleteTransactionWithoutApproval(transactionId, null, frequencyType,
						request);
				internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
						TransactionStatusEnum.FAILED.getStatus(), transactionId);
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}

			transactionStatus = transactionStatusDTO.getStatus();
			transactionId = transactionStatusDTO.getConfirmationNumber();

			result.addParam(new Param("requestId", requestId));

			if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
				result.addParam(new Param("referenceId", transactionId));
			} else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
				result.addParam(new Param("referenceId", pendingrefId));
				internationalFundTransferBusinessDelegate.updateStatusUsingTransactionId(referenceId,
						transactionStatus.toString(), pendingrefId);
			}
			LOG.debug("before pushAlertsForApprovalRequests==" + inputParams);
			// code snippet being added for alerts on International transfer
			try {
				inputParams.put("toAccountNumber", inputParams.get("toExternalAccountNumber"));
				LogEvents.pushAlertsForApprovalRequests(featureActionId, request, response, inputParams, null,
						transactionId, requestId, CustomerSession.getCustomerName(customer), null);
			} catch (Exception e) {
				LOG.error("Failed at pushAlertsForApprovalRequests " + e);
			}
		}

		else if (transactionStatus == TransactionStatusEnum.APPROVED) {
			result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
			result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
			result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
		}

		if (internationfundtransferDTO.getOverrides() != null) {
			result.addParam(new Param("overrides", internationfundtransferDTO.getOverrides()));
		}
		if (internationfundtransferDTO.getOverrideList() != null) {
			result.addParam(new Param("overrideList", internationfundtransferDTO.getOverrideList()));
		}
		if (internationfundtransferDTO.getCharges() != null) {
			result.addParam(new Param("charges", internationfundtransferDTO.getCharges()));
		}
		if (internationfundtransferDTO.getExchangeRate() != null) {
			result.addParam(new Param("exchangeRate", internationfundtransferDTO.getExchangeRate()));
		}
		if (internationfundtransferDTO.getTotalAmount() != null) {
			result.addParam(new Param("totalAmount", internationfundtransferDTO.getTotalAmount()));
		}
		if (internationfundtransferDTO.getMessageDetails() != null) {
			result.addParam(new Param("messageDetails", internationfundtransferDTO.getMessageDetails()));
		}
		if (internationfundtransferDTO.getQuoteCurrency() != null) {
			result.addParam(new Param("quoteCurrency", internationfundtransferDTO.getQuoteCurrency()));
		}

		try {
			_logTransaction(request, response, inputArray, result, transactionStatus, referenceId,
					internationfundtransferDTO, requestId);

			// Prebooked logs.

			JsonObject customParams = new JsonObject();

			customParams.addProperty("EntityType", "International Fund Transfer");
			customParams.addProperty("DealDeatis", refDto.getRfqDetails());
			customParams.addProperty("Currency", internationfundtransferDTO.getTransactionCurrency());
			customParams.addProperty("DateAndTime",
					SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd HH:mm:ss").toString());
			// customParams.addProperty("EntityType",StringUtils.isNotBlank(request.getParameter("entityType"))?request.getParameter("entityType"):"");
			// customParams.addProperty("DateAndTime",SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd
			// HH:mm:ss").toString());

			_OlbAuditLogsClass(request, response, result, "PAYMENT", "PREBOOK_DEAL_RESULT",
					"services/data/v1/SBGPreBookedDeals/operations/getPreBookedDeals/getDeals", "success",
					customParams);

		} catch (Exception e) {
			LOG.error("Error occured while audit logging.", e);
		}
		if (resultRefData != null) {
			result.addParam("statementReference", resultRefData.getStatementrefno());
			result.addParam("beneficiaryReference", resultRefData.getBenerefeno());
			result.addParam("complianceCode", resultRefData.getCompliancecode());
			result.addParam("purposeCode", resultRefData.getPurposecode());
			result.addParam("rfqDetails", resultRefData.getRfqDetails());

		}
		// update document table
		String documentIdList = (SBGCommonUtils.isStringNotEmpty(inputParams.get("uploadedattachments")))
				? inputParams.get("uploadedattachments").toString()
				: SBGConstants.EMPTY_STRING;
		String transId = result.getParamValueByName("referenceId");
		if (org.apache.commons.lang.StringUtils.isNotBlank(documentIdList)) {
			SbgDocTransactionRelationHelper.updateTransactionIdAgaintDocumentId(documentIdList, transId);
		}
		return result;
	}

	private InternationalFundTransferRefDataDTO setAdditionalTransfersRefData(Map<String, Object> inputParams) {
		LOG.debug("Entry -- > SbgInternationalFundTransferResourceImplExtn::setAdditionalTransfersRefData");
		InternationalFundTransferRefDataDTO refDto = new InternationalFundTransferRefDataDTO();
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
		String beneficiaryCategory = inputParams.get("payeeType") == null ? ""
				: inputParams.get("payeeType").toString();
		String beneCode = inputParams.get("beneCode") == null ? ""
				: inputParams.get("beneCode").toString();
		String fromAccountName = inputParams.get("fromAccountName") == null ? ""
				: inputParams.get("fromAccountName").toString();

		String exconApproval = null;
		if (!SBGCommonUtils.isStringEmpty(compliancecode)) {
			if (SBGConstants.EXCON_APPROVAL.equals(compliancecode)) {
				String applicationNumber = SBGCommonUtils.isStringEmpty(inputParams.get("applicationNumber")) ? ""
						: inputParams.get("applicationNumber").toString();
				String authoriserDealer = SBGCommonUtils.isStringEmpty(inputParams.get("authoriserDealer")) ? ""
						: inputParams.get("authoriserDealer").toString();
				String applicationDate = SBGCommonUtils.isStringEmpty(inputParams.get("applicationDate")) ? ""
						: inputParams.get("applicationDate").toString();

				exconApproval = "{\"applicationNumber\":\"" + applicationNumber + "\",\"authoriserDealer\":\""
						+ authoriserDealer + "\",\"applicationDate\":\"" + applicationDate + "\"}";
			}
		}

		refDto.setPurposecode(purposecode);
		refDto.setCompliancecode(compliancecode);
		refDto.setStatementrefno(statementrefno);
		refDto.setBenerefeno(benerefeno);
		refDto.setRfqDetails(rfqDetails);
		refDto.setBeneficiaryState(beneficiaryState);
		refDto.setBopDetails(bopDetails);
		refDto.setClearingCode(clearingCode);
		refDto.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
		refDto.setExconApproval(exconApproval);
		refDto.setBeneficiaryCategory(setBeneficiaryStatus(beneficiaryCategory));
		refDto.setBeneCode(beneCode);
		refDto.setFromAccountName(fromAccountName);

		return refDto;
	}

	private void _logTransaction(DataControllerRequest request, DataControllerResponse response, Object[] inputArray,
			Result result, TransactionStatusEnum transactionStatus, String referenceId,
			InternationalFundTransferDTO internationfundtransferDTO, String requestId) {

		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
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
					FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
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
						if (internationfundtransferDTO == null
								|| !SBGCommonUtils.isStringEmpty(internationfundtransferDTO.getDbpErrMsg())) {
							statusID = Constants.SID_EVENT_FAILURE;
						}

						if (SBGCommonUtils.isStringEmpty(referenceId)) {
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

	private InternationalFundTransferDTO updateTransactionTable(
			InternationalFundTransferBackendDTO internationalfundtransferbackenddto, DataControllerRequest request) {
		String serviceName = ServiceId.INTERNATIONAL_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTERNATIONAL_ACC_FUND_TRANSFER_BACKEND;
		LOG.debug("updateTransactionTable() ---> serviceName: " + serviceName + "; operationName: " + operationName);

		String createResponse = null;
		InternationalFundTransferDTO internationalfundtransferdto = null;

		Map<String, Object> requestParameters;
		try {
			internationalfundtransferbackenddto.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL);
			requestParameters = JSONUtils.parseAsMap(new JSONObject(internationalfundtransferbackenddto).toString(),
					String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e.getMessage());
			return null;
		}

		LOG.debug("updateTransactionTable() ---> requestParameters: " + requestParameters);
		try {
			createResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(request.getHeaderMap()).withDataControllerRequest(request).build()
					.getResponse();

			LOG.debug("updateTransactionTable() ---> completed call to CreateTransfer: ");
			internationalfundtransferdto = JSONUtils.parse(createResponse, InternationalFundTransferDTO.class);

			LOG.debug("updateTransactionTable() ---> internationalfundtransferdto: " + internationalfundtransferdto);
			if (internationalfundtransferdto.getTransactionId() != null
					&& !"".equals(internationalfundtransferdto.getTransactionId())) {
				internationalfundtransferdto.setReferenceId(internationalfundtransferdto.getTransactionId());
			}
		} catch (JSONException e) {
			LOG.error("Failed to create internationalfund transaction: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create internationalfund transaction: ", e);
			return null;
		}

		return internationalfundtransferdto;
	}

	public static void _OlbAuditLogsClass(DataControllerRequest request, DataControllerResponse response, Result result,
			String eventType, String eventSubType, String producer, String statusId, JsonObject customParams) {
		LOG.debug("entry ------------>_logSignatoryGroupStatus()");
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		customParams.addProperty("customerName", CustomerSession.getCustomerCompleteName(customer));
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;

		EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusId, null,
				CustomerSession.getCustomerId(customer), null, customParams);
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
}
