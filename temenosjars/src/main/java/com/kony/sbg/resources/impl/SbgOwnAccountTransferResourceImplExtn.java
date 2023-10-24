package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.OwnAccountPaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegateOwnAccount;
import com.kony.sbg.business.api.EvaluateTransactionBusinessDelegate;
import com.kony.sbg.business.api.OwnAccountFundTransferBusinessDelegateExtn;
import com.kony.sbg.dto.OwnAccountFundTransferRefDataDTO;
import com.kony.sbg.helpers.SbgCreateTransferHelper;
import com.kony.sbg.resources.api.SbgOwnAccountFundTransferResourceExtn;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.OwnAccountFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.OwnAccountFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;
import com.temenos.dbx.product.transactionservices.resource.impl.OwnAccountFundTransferResourceImpl;
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
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class SbgOwnAccountTransferResourceImplExtn extends OwnAccountFundTransferResourceImpl
        implements SbgOwnAccountFundTransferResourceExtn {
    private static final Logger LOG = LogManager.getLogger(SbgOwnAccountTransferResourceImplExtn.class);

    CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(CustomerBusinessDelegate.class);
    ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(ApplicationBusinessDelegate.class);
    OwnAccountFundTransferBackendDelegate ownAccFundTransferBackendDelegate = DBPAPIAbstractFactoryImpl
            .getBackendDelegate(OwnAccountFundTransferBackendDelegate.class);
    TransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(TransactionLimitsBusinessDelegate.class);
    OwnAccountFundTransferBusinessDelegate ownaccountTransactionDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(OwnAccountFundTransferBusinessDelegate.class);
    AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(AccountBusinessDelegate.class);
    ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
    AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
    OwnAccountFundTransferBusinessDelegateExtn sbgOwnAccountFundTransferBusinessDelegateExtn = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(OwnAccountFundTransferBusinessDelegateExtn.class);
    OwnAccountPaymentAPIBackendDelegate paymentAPIBackendDelegate = DBPAPIAbstractFactoryImpl
            .getBackendDelegate(OwnAccountPaymentAPIBackendDelegate.class);
    EvaluateTransactionBusinessDelegate evaluateTransactionBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(EvaluateTransactionBusinessDelegate.class);

    public Result createTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {
        LOG.debug("Entry --> SbgOwnAccountFundTransferResourceImpl::createTransaction -- Attempt#1");
        @SuppressWarnings("unchecked")
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        LOG.debug("Entry --> inputParams" + inputParams.toString());

        OwnAccountFundTransferDTO ownAccountfundtransferDTO = null;
        Result result = new Result();
        Double amount = null;

        OwnAccountFundTransferRefDataDTO refDto = setAdditionalTransfersRefData(inputParams);

        LOG.debug("After --> SbgOwnAccountFundTransferResourceImpl::setAdditionalTransfersRefData");

        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String createdby = CustomerSession.getCustomerId(customer);
        String featureActionId = null;

        String amountValue = inputParams.get("amount").toString();
        String fromAccountNumber = inputParams.get("fromAccountNumber").toString();

        CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);

        LOG.debug("After --> SbgOwnAccountFundTransferResourceImpl::getAccountDetails");
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

        featureActionId = FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE;

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
            ownAccountfundtransferDTO = JSONUtils.parse(new JSONObject(inputParams).toString(),
                    OwnAccountFundTransferDTO.class);
        } catch (IOException e) {
            LOG.error("Error occured while fetching the input params: ", e);
            return null;
        }

        // /* Bank Name not populated cause of field name mismatch, so populating it */
        // ownAccountfundtransferDTO.setBankName(inputParams.get("beneficiaryBankName").toString());
        // ownAccountfundtransferDTO.setPayeeName(ownAccountfundtransferDTO.getBeneficiaryName());

        String date = ownAccountfundtransferDTO.getScheduledDate() == null
                ? (ownAccountfundtransferDTO.getProcessingDate() == null
                        ? (ownAccountfundtransferDTO.getFrequencyStartDate() == null ? application.getServerTimeStamp()
                                : ownAccountfundtransferDTO.getFrequencyStartDate())
                        : ownAccountfundtransferDTO.getProcessingDate())
                : ownAccountfundtransferDTO.getScheduledDate();
        String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();

        TransactionStatusDTO transactionStatusDTO = null;
        LOG.debug("Before --> SbgOwnAccountFundTransferResourceImpl::sbgValidations");
        if (SBGConstants.TRUE.equalsIgnoreCase(validate)) {
            /*
             * Mashilo Joseph Monene : Method call to Validate Ref-Data Validations
             */
            JSONObject ValidationsResponseObj = new JSONObject();
            ValidationsResponseObj = sbgOwnAccountFundTransferBusinessDelegateExtn.sbgValidations(request);
            if (SBGConstants.FALSE.equalsIgnoreCase(ValidationsResponseObj.optString(SBGConstants.IS_VALID))) {
                LOG.info("[SbgOwnAccountFundTransferResourceImpl] RefData Validations Failed.");
                result.addParam("RefDataValidation", SBGConstants.FAILED);
                result.addParam("dbpErrMsg", ValidationsResponseObj.optString(SBGConstants.MESSAGE));
                return result;
            }

            // BoP Form Validation
            LOG.debug("SbgOwnAccountFundTransferResourceImpl::validateBoPForm::before invocation");
            if (SBGCommonUtils.isStringEmpty(inputParams.get("bopDetails")) == false) {
                LOG.debug("SbgOwnAccountFundTransferResourceImpl::BopDetails:::" + inputParams.get("bopDetails"));
                LOG.debug("SbgOwnAccountFundTransferResourceImpl::validateBoPForm::bop is not empty");
                if (evaluateTransactionBusinessDelegate.validateBoPForm(inputParams.get("bopDetails").toString(),
                        result, request) == false) {
                    LOG.debug("SbgOwnAccountFundTransferResourceImpl::validateBoPForm::returned failure");
                    return result;
                }
            }

            OwnAccountFundTransferBackendDTO ownAccountFundTransferBackendDTO = new OwnAccountFundTransferBackendDTO();
            ownAccountFundTransferBackendDTO = ownAccountFundTransferBackendDTO
                    .convert(ownAccountfundtransferDTO);

            OwnAccountFundTransferDTO validateaccountDTO = ownAccFundTransferBackendDelegate
                    .validateTransaction(ownAccountFundTransferBackendDTO, request);
            try {
                result = JSONToResult.convert(new JSONObject(validateaccountDTO).toString());
                return result;
            } catch (JSONException e) {
                LOG.error(
                        "Error occured while converting the response from Line of Business service for OwnAccountFund transfer: ",
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
                .setTransactionStatusMessageOwnAccount(transactionStatusDTO.getStatus());
        boolean isSelfApproved;
        if (authorizationChecksBusinessDelegate
                .isUserAuthorizedForFeatureActionForAccount(createdby,
                        "TRANSFER_BETWEEN_OWN_ACCOUNT_SELF_APPROVAL", fromAccountNumber, false)) {
            isSelfApproved = transactionStatusDTO.isSelfApproved();
        } else {
            isSelfApproved = false;
            transactionStatusDTO.setSelfApproved(isSelfApproved);
        }

        // product
        ownAccountfundtransferDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
        try {
            ownAccountfundtransferDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
        } catch (NumberFormatException e) {
            LOG.error("Invalid amount value", e);
            return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
        }
        ownAccountfundtransferDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
        ownAccountfundtransferDTO.setRequestId(transactionStatusDTO.getRequestId());
        ownAccountfundtransferDTO.setStatus(transactionStatus.getStatus());
        ownAccountfundtransferDTO.setConvertedAmount(transactionStatusDTO.getConvertedAmount());
        LOG.debug("ownAccountfundtransferDTO.getConvertedAmount() " + ownAccountfundtransferDTO.getConvertedAmount());
        String confirmationNumber = (StringUtils.isEmpty(transactionId))
                ? Constants.REFERENCE_KEY + transactionStatusDTO.getRequestId()
                : transactionId;
        ownAccountfundtransferDTO.setConfirmationNumber(confirmationNumber);

        OwnAccountFundTransferDTO ownAccountFundAccountdbxDTO = ownaccountTransactionDelegate
                .createTransactionAtDBX(ownAccountfundtransferDTO);

        if (ownAccountFundAccountdbxDTO == null) {
            LOG.debug("Error occured while creating entry into the DBX table: TheOtherOne");
            return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
        }

        if (ownAccountFundAccountdbxDTO.getDbpErrCode() != null
                || ownAccountFundAccountdbxDTO.getDbpErrMsg() != null) {
            result.addParam(new Param("dbpErrCode", ownAccountFundAccountdbxDTO.getDbpErrCode()));
            result.addParam(new Param("dbpErrMsg", ownAccountFundAccountdbxDTO.getDbpErrMsg()));
            return result;
        }

        refDto.setReferenceId(ownAccountFundAccountdbxDTO.getTransactionId());
        refDto.setConfirmationNumber(ownAccountFundAccountdbxDTO.getConfirmationNumber());
        RefDataBackendDelegateOwnAccount refbackendDelegate = (RefDataBackendDelegateOwnAccount) DBPAPIAbstractFactoryImpl
                .getBackendDelegate(RefDataBackendDelegateOwnAccount.class);
        LOG.debug("ownAccountfundtransferDTO::Before insertAdditionalFields");
        OwnAccountFundTransferRefDataDTO resultRefData = refbackendDelegate
                .insertAdditionalFields(ownAccountFundAccountdbxDTO.getTransactionId(), refDto);
        ownAccountFundAccountdbxDTO.setValidate(validate);

        OwnAccountFundTransferBackendDTO ownAccountfundtransferBackendDTO = new OwnAccountFundTransferBackendDTO();
        ownAccountfundtransferBackendDTO = ownAccountfundtransferBackendDTO.convert(ownAccountFundAccountdbxDTO);

        String creditValueDate = inputParams.get("creditValueDate") == null ? ""
                : inputParams.get("creditValueDate").toString();
        String totalAmount = inputParams.get("totalAmount") == null ? "" : inputParams.get("totalAmount").toString();
        String exchangeRate = inputParams.get("exchangeRate") == null ? "" : inputParams.get("exchangeRate").toString();
        String intermediaryBicCode = inputParams.get("intermediaryBicCode") == null ? ""
                : inputParams.get("intermediaryBicCode").toString();
        String clearingCode = inputParams.get("clearingCode") == null ? "" : inputParams.get("clearingCode").toString();
        String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
        // String beneficiaryBankName = inputParams.get("beneficiaryBankName") == null ?
        // ""
        // : inputParams.get("beneficiaryBankName").toString();
        // String beneficiaryAddressLine2 = inputParams.get("beneficiaryAddressLine2")
        // == null ? ""
        // : inputParams.get("beneficiaryAddressLine2").toString();
        // String beneficiaryPhone = inputParams.get("beneficiaryPhone") == null ? ""
        // : inputParams.get("beneficiaryPhone").toString();
        // String beneficiaryEmail = inputParams.get("beneficiaryEmail") == null ? ""
        // : inputParams.get("beneficiaryEmail").toString();
        // String beneficiaryState = inputParams.get("beneficiaryState") == null ? ""
        // : inputParams.get("beneficiaryState").toString();

        ownAccountfundtransferBackendDTO.setCreditValueDate(creditValueDate);
        ownAccountfundtransferBackendDTO.setTotalAmount(totalAmount);
        ownAccountfundtransferBackendDTO.setExchangeRate(exchangeRate);
        // ownAccountfundtransferBackendDTO.setIntermediaryBicCode(intermediaryBicCode);
        // ownAccountfundtransferBackendDTO.setClearingCode(clearingCode);
        ownAccountfundtransferBackendDTO.setOverrides(overrides);
        // ownAccountfundtransferBackendDTO.setBeneficiaryBankName(beneficiaryBankName);
        // ownAccountfundtransferBackendDTO.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
        // ownAccountfundtransferBackendDTO.setBeneficiaryPhone(beneficiaryPhone);
        // ownAccountfundtransferBackendDTO.setBeneficiaryEmail(beneficiaryEmail);
        // ownAccountfundtransferBackendDTO.setBeneficiaryState(beneficiaryState);

        String beneficiaryId = inputParams.get("beneficiaryId") == null ? null
                : inputParams.get("beneficiaryId").toString();
        ownAccountfundtransferBackendDTO.setBeneficiaryId(beneficiaryId);

        try {
            ownAccountfundtransferBackendDTO
                    .setAmount(Double.parseDouble(ownAccountfundtransferBackendDTO.getTransactionAmount()));
            ownAccountFundAccountdbxDTO
                    .setAmount(Double.parseDouble(ownAccountfundtransferBackendDTO.getTransactionAmount()));
        } catch (Exception e) {
            LOG.error("Invalid amount value", e);
            return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
        }

        try {
            String responseObj = new JSONObject(ownAccountfundtransferBackendDTO).toString();
            result = JSONToResult.convert(responseObj);
        } catch (JSONException e) {
            LOG.error(
                    "Error occured while converting the response from Line of Business service for External transfer: ",
                    e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }

        String referenceId = ownAccountFundAccountdbxDTO.getTransactionId();
        String requestId = "";
        String createWithPaymentId = inputParams.get("createWithPaymentId") == null ? ""
                : inputParams.get("createWithPaymentId").toString();

        LOG.debug("Sending the data further based on " + transactionStatus);
        if (transactionStatus == TransactionStatusEnum.SENT) {
            OwnAccountFundTransferDTO ownAccountFundTransferDTO = new OwnAccountFundTransferDTO();
            if (StringUtils.isEmpty(transactionId)
                    || (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true"))) {
                if (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true")) {
                    ownAccountfundtransferBackendDTO
                            .setTransactionId(ownAccountFundAccountdbxDTO.getTransactionId());
                    ownAccountfundtransferBackendDTO.setConfirmationNumber(transactionId);
                    ownAccountfundtransferBackendDTO.setReferenceId(ownAccountFundAccountdbxDTO.getTransactionId());
                    String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
                    ownAccountfundtransferBackendDTO.setCharges(charges);
                } else {
                    ownAccountfundtransferBackendDTO.setTransactionId(null);
                }

                try {
                    // submits the request to Volante
                    LOG.debug("Sending the data to Volante ");
                    ownAccountFundTransferDTO = paymentAPIBackendDelegate
                            .createTransactionWithoutApproval(ownAccountfundtransferBackendDTO, request);
                    if (ownAccountFundTransferDTO == null) {
                        LOG.debug("Sending the data to Volante failed for reference: " + referenceId);
                        // setting the error code for any exceptions in volpay payment submit
                        ownAccountFundTransferDTO = new OwnAccountFundTransferDTO();
                        ownAccountFundTransferDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
                        ownAccountFundTransferDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
                    }
                } catch (Exception e) {
                    // Applied try/catch to capture the data in further tables like transactions.
                    LOG.error("EXCEPTION MAKING CALL TO VOLPAY: " + e.getMessage());
                    if (ownAccountFundTransferDTO == null) {
                        // setting the error code for any exceptions in volpay payment submit
                        ownAccountFundTransferDTO = new OwnAccountFundTransferDTO();
                    }
                    ownAccountFundTransferDTO.setDbpErrCode(SbgErrorCodeEnum.ERR_100025.getErrorCodeAsString());
                    ownAccountFundTransferDTO.setDbpErrMsg(SbgErrorCodeEnum.ERR_100025.getMessage());
                }

                // persists the data in Dbxdb.Transaction table
                LOG.debug("Sending the data to Dbxdb.Transaction ");
                // ownAccountFundTransferDTO =
                // updateTransactionTable(ownAccountfundtransferBackendDTO, request);
                // createTransaction(String methodID, Object[] inputArray, DataControllerRequest
                // request, DataControllerResponse response);
                try {
                    String tranxStatus = SBGCommonUtils.isStringEmpty(ownAccountfundtransferDTO.getDbpErrCode())
                            ? "Pending processing"
                            : DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
                    LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
                            + ownAccountfundtransferDTO.getDbpErrCode());
                    new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
                            confirmationNumber);
                } catch (Exception e) {
                    LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
                    LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + referenceId);
                }

            } else {
                String frequency = StringUtils.isEmpty(ownAccountfundtransferDTO.getFrequencyTypeId()) ? null
                        : ownAccountfundtransferDTO.getFrequencyTypeId();
                ownAccountFundTransferDTO = ownaccountTransactionDelegate
                        .approveTransaction(transactionId, request, frequency);
                if (ownAccountFundTransferDTO == null) {
                    ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                            TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
                    return ErrorCodeEnum.ERR_29020.setErrorCode(result);
                }
            }

            if (ownAccountFundTransferDTO.getDbpErrCode() != null
                    || ownAccountFundTransferDTO.getDbpErrMsg() != null) {
                ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                        TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
                result.addParam(new Param("errorDetails", ownAccountFundTransferDTO.getErrorDetails()));
                return ErrorCodeEnum.ERR_00000.setErrorCode(result, ownAccountFundTransferDTO.getDbpErrMsg());
            }

            String refId = ownAccountFundTransferDTO.getReferenceId();
            if (refId == null || "".equals(refId)) {
                ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                        TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
                return ErrorCodeEnum.ERR_12601.setErrorCode(result);
            }
            /* While updating status passing confirmation number to maintain uniqueness */
            // ownAccountfundtransferDTO = ownAccountFundTransferDTO;
            ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                    TransactionStatusEnum.EXECUTED.getStatus(), ownAccountFundTransferDTO.getConfirmationNumber());
            result.addParam(new Param("referenceId", ownAccountFundTransferDTO.getConfirmationNumber()));
            result.addParam(new Param("status", transactionStatus.getStatus()));
            result.addParam(new Param("message", transactionStatus.getMessage()));

        } else if (transactionStatus == TransactionStatusEnum.PENDING) {
            ownAccountFundAccountdbxDTO.setCreditValueDate(creditValueDate);
            ownAccountFundAccountdbxDTO.setTotalAmount(totalAmount);
            ownAccountFundAccountdbxDTO.setExchangeRate(exchangeRate);
            // ownAccountFundAccountdbxDTO.setIntermediaryBicCode(intermediaryBicCode);
            // ownAccountFundAccountdbxDTO.setClearingCode(clearingCode);
            requestId = transactionStatusDTO.getRequestId();
            String pendingrefId = null;

            if (StringUtils.isEmpty(transactionId)
                    || (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true"))) {
                if (StringUtils.isNotBlank(transactionId) && createWithPaymentId.equalsIgnoreCase("true")) {
                    ownAccountFundAccountdbxDTO.setTransactionId(transactionId);
                    String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
                    ownAccountFundAccountdbxDTO.setCharges(charges);
                }
                LOG.debug("ownAccountfundtransferDTO::Before createPendingTransaction");
                OwnAccountFundTransferDTO ownAccountFundTransferPendingtransactionDTO = ownaccountTransactionDelegate
                        .createPendingTransaction(ownAccountFundAccountdbxDTO, request);
                if (ownAccountFundTransferPendingtransactionDTO == null) {
                    ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                            TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
                    LOG.debug("Error occured while creating entry into the backend table: ");
                    // return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
                }

                // persists the data in Dbxdb.Transaction table
                LOG.debug("Sending the data to Dbxdb.Transaction ");
                // ownAccountfundtransferDTO =
                // updateTransactionTable(internationfundtransferBackendDTO, request);
                // createTransaction(String methodID, Object[] inputArray, DataControllerRequest
                // request, DataControllerResponse response);
                try {
                    String tranxStatus = SBGCommonUtils
                            .isStringEmpty(ownAccountFundTransferPendingtransactionDTO.getDbpErrCode())
                                    ? DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING
                                    : DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
                    LOG.debug("tranxStatus: " + tranxStatus + "; errorcode: "
                            + ownAccountFundTransferPendingtransactionDTO.getDbpErrCode());
                    new SbgCreateTransferHelper().invoke(methodID, inputArray, request, response, tranxStatus,
                            confirmationNumber);
                } catch (Exception e) {
                    LOG.error("CreateTransferHelper --- EXCEPTION: " + e.getMessage());
                    LOG.debug("Sending the data to Dbxdb.Transaction failed for reference: " + referenceId);
                }

                if (ownAccountFundTransferPendingtransactionDTO.getDbpErrCode() != null
                        || ownAccountFundTransferPendingtransactionDTO.getDbpErrMsg() != null) {
                    ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                            TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
                    result.addParam(new Param("errorDetails",
                            ownAccountFundTransferPendingtransactionDTO.getErrorDetails()));
                    return ErrorCodeEnum.ERR_00000.setErrorCode(result,
                            ownAccountFundTransferPendingtransactionDTO.getDbpErrMsg());
                }
                transactionId = ownAccountFundTransferPendingtransactionDTO.getReferenceId();
                // ownAccountfundtransferDTO = ownAccountFundTransferPendingtransactionDTO;
            }

            pendingrefId = transactionId;
            ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                    transactionStatus.toString(), transactionId);
            transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, transactionId,
                    isSelfApproved, featureActionId, request);

            if (transactionStatusDTO == null) {
                ownAccFundTransferBackendDelegate.deleteTransactionWithoutApproval(transactionId, null, frequencyType,
                        request);
                ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
                        TransactionStatusEnum.FAILED.getStatus(), transactionId);
                return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
            }

            if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
                ownAccFundTransferBackendDelegate.deleteTransactionWithoutApproval(transactionId, null, frequencyType,
                        request);
                ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
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
                ownaccountTransactionDelegate.updateStatusUsingTransactionId(referenceId,
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

        } else if (transactionStatus == TransactionStatusEnum.APPROVED) {
            result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
            result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
            result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
        }

        if (ownAccountfundtransferDTO.getOverrides() != null) {
            result.addParam(new Param("overrides", ownAccountfundtransferDTO.getOverrides()));
        }
        if (ownAccountfundtransferDTO.getOverrideList() != null) {
            result.addParam(new Param("overrideList", ownAccountfundtransferDTO.getOverrideList()));
        }
        if (ownAccountfundtransferDTO.getCharges() != null) {
            result.addParam(new Param("charges", ownAccountfundtransferDTO.getCharges()));
        }
        if (ownAccountfundtransferDTO.getExchangeRate() != null) {
            result.addParam(new Param("exchangeRate", ownAccountfundtransferDTO.getExchangeRate()));
        }
        if (ownAccountfundtransferDTO.getTotalAmount() != null) {
            result.addParam(new Param("totalAmount", ownAccountfundtransferDTO.getTotalAmount()));
        }
        if (ownAccountfundtransferDTO.getMessageDetails() != null) {
            result.addParam(new Param("messageDetails", ownAccountfundtransferDTO.getMessageDetails()));
        }
        if (ownAccountfundtransferDTO.getQuoteCurrency() != null) {
            result.addParam(new Param("quoteCurrency", ownAccountfundtransferDTO.getQuoteCurrency()));
        }

        try {
            _logTransaction(request, response, inputArray, result, transactionStatus, referenceId,
                    ownAccountfundtransferDTO, requestId);

            // Prebooked logs.

            JsonObject customParams = new JsonObject();

            customParams.addProperty("EntityType", "International Fund Transfer");
            customParams.addProperty("DealDeatis", refDto.getRfqDetails());
            customParams.addProperty("Currency", ownAccountfundtransferDTO.getTransactionCurrency());
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
            result.addParam("fromAccountStatementReference", resultRefData.getStatementrefno());
            result.addParam("toAccountStatementReference", resultRefData.getBenerefeno());
            result.addParam("complianceCode", resultRefData.getCompliancecode());
            result.addParam("purposeCode", resultRefData.getPurposecode());
            result.addParam("rfqDetails", resultRefData.getRfqDetails());

        }
        return result;

    }

    private OwnAccountFundTransferRefDataDTO setAdditionalTransfersRefData(Map<String, Object> inputParams) {
        LOG.debug("Entry -- > SbgOwnAccountFundTransferResourceImplExtn::setAdditionalTransfersRefData");
        OwnAccountFundTransferRefDataDTO refDto = new OwnAccountFundTransferRefDataDTO();
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
        String beneficiaryAddressLine2 = SBGCommonUtils.isStringEmpty(inputParams.get("beneficiaryName")) ? ""
                : inputParams.get("beneficiaryName").toString();
        String beneficiaryState = SBGCommonUtils.isStringEmpty(inputParams.get("beneficiaryState")) ? ""
                : inputParams.get("beneficiaryState").toString();

        String paymentType = SBGCommonUtils.isStringEmpty(inputParams.get("paymentType")) ? ""
                : inputParams.get("paymentType").toString();

        String fromAccountName = SBGCommonUtils.isStringEmpty(inputParams.get("fromAccountName")) ? ""
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

                applicationDate = changeDateFormat(applicationDate);
                exconApproval = "{\"applicationNumber\":\"" + applicationNumber + "\",\"authoriserDealer\":\""
						+ authoriserDealer + "\",\"applicationDate\":\"" + applicationDate + "\"}";
            }
        }

        ;
        refDto.setPurposecode(purposecode);
        refDto.setCompliancecode(compliancecode);
        refDto.setStatementrefno(statementrefno);
        refDto.setBenerefeno(benerefeno);
        refDto.setRfqDetails(rfqDetails);
        refDto.setBeneficiaryState(beneficiaryState);
        refDto.setBopDetails(bopDetails);
        refDto.setClearingCode(clearingCode);
        refDto.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
        refDto.setPaymentType(paymentType);
        refDto.setExconApproval(exconApproval);
        refDto.setFromAccountName(fromAccountName); 

        return refDto;
    }

    private static String changeDateFormat(String applicationDate) {

        try {
            if (!SBGConstants.EMPTY_STRING.equals(applicationDate)) {
                SimpleDateFormat oldFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                return newFormat.format(oldFormat.parse(applicationDate));
            }
        } catch (Exception e) {
            LOG.error("Date format change error");
        }
        return SBGConstants.EMPTY_STRING;
    }

    private void _logTransaction(DataControllerRequest request, DataControllerResponse response, Object[] inputArray,
            Result result, TransactionStatusEnum transactionStatus, String referenceId,
            OwnAccountFundTransferDTO ownAccountfundtransferDTO, String requestId) {

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

            eventSubType = auditLog.deriveSubTypeForInternalTransfer(isScheduled, frequencyType);
            // FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);
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
                        if (ownAccountfundtransferDTO == null
                                || !SBGCommonUtils.isStringEmpty(ownAccountfundtransferDTO.getDbpErrMsg())) {
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
}
