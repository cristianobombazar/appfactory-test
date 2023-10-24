package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.konylabs.middleware.dataobject.*;
import com.konylabs.middleware.dataobject.Record;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.RefDataBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegateInterBank;
import com.kony.sbg.business.api.OwnAccountFundTransferBusinessDelegateExtn;
import com.kony.sbg.business.api.SbgInterBankFundTransferBusinessDelegateExtn;
import com.kony.sbg.business.api.SbgInternationalFundTransferBusinessDelegateExtn;
import com.kony.sbg.helpers.SbgUpdateTransferHelper;
import com.kony.sbg.resources.api.ApprovalQueueResourceExt;
import com.kony.sbg.util.RefDataCacheHelper;
import com.kony.sbg.util.RefDataCacheHelperInterBank;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.constants.EventSubType;
import com.temenos.dbx.product.achservices.businessdelegate.api.ACHFileBusinessDelegate;
import com.temenos.dbx.product.achservices.businessdelegate.api.ACHTransactionBusinessDelegate;
import com.temenos.dbx.product.achservices.dto.ACHFileDTO;
import com.temenos.dbx.product.achservices.dto.ACHTransactionDTO;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBActedRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.approvalservices.resource.impl.ApprovalQueueResourceImpl;
import com.temenos.dbx.product.bulkpaymentservices.backenddelegate.api.BulkPaymentRecordBackendDelegate;
import com.temenos.dbx.product.bulkpaymentservices.businessdelegate.api.BulkPaymentRecordBusinessDelegate;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;
import com.temenos.dbx.product.bulkpaymentservices.resource.api.BulkPaymentRecordResource;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureActionBusinessDelegate;
import com.temenos.dbx.product.commons.dto.ApplicationDTO;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.AuditLog;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.commonsutils.LogEvents;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.dto.CustomerSignatoryGroupDTO;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.GeneralTransactionsBusinessDelegate;

public class SbgApprovalQueueResourceImplExt extends ApprovalQueueResourceImpl implements ApprovalQueueResourceExt {

	private static final Logger LOG = LogManager.getLogger(SbgApprovalQueueResourceImplExt.class);

	FeatureActionBusinessDelegate FeatureActionBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(FeatureActionBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	ApplicationBusinessDelegate applicationBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	BulkPaymentRecordBusinessDelegate bulkPaymentRecordBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(BulkPaymentRecordBusinessDelegate.class);
	GeneralTransactionsBusinessDelegate generalTransactionsBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(GeneralTransactionsBusinessDelegate.class);
	ACHFileBusinessDelegate achFileBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ACHFileBusinessDelegate.class);
	ACHTransactionBusinessDelegate achTransactionBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ACHTransactionBusinessDelegate.class);
	BulkPaymentRecordResource bulkPaymentRecordResource = DBPAPIAbstractFactoryImpl
			.getResource(BulkPaymentRecordResource.class);

	@Override
	public Result approve(String methodID, Object[] inputArray, DataControllerRequest dcr,
			DataControllerResponse response) {
		LOG.debug("Entry --> SbgApprovalQueueResourceImplExt::approve");
		Result result = new Result();
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(dcr);
			String customerId = CustomerSession.getCustomerId(customer);
			String requestId = dcr.getParameter("requestId");
			String comments = "Approved";

			String featureActionId = dcr.getParameter("featureActionId");
			if (StringUtils.isNotEmpty(featureActionId)) {
				featureActionId = FeatureActionBusinessDelegate.getApproveFeatureAction(featureActionId);
			}
			if (StringUtils.isEmpty(featureActionId)) {
				return ErrorCodeEnum.ERR_28025.setErrorCode(result);
			}
			List<String> requiredApproveActionIds = Arrays.asList(featureActionId);
			featureActionId = CustomerSession.getPermittedActionIds(dcr, requiredApproveActionIds);
			if (featureActionId == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			Boolean flag = approvalQueueBusinessDelegate.checkIfUserAlreadyApproved(requestId, customerId);
			if (flag == true) {
				return ErrorCodeEnum.ERR_21010.setErrorCode(result);
			}

			ApplicationDTO applicationDTO = new ApplicationDTO();
			applicationDTO = applicationBusinessDelegate.properties();
			String createdby = null;

			BBRequestDTO requestDTO = approvalQueueBusinessDelegate.getBbRequest(requestId);
			if (requestDTO != null && StringUtils.isNotEmpty(requestDTO.getCreatedby())) {
				LOG.info("Fetch BBRequest details success");
				createdby = requestDTO.getCreatedby();
			} else {
				LOG.info("Error while fetching BBRequest details");
				return ErrorCodeEnum.ERR_14008.setErrorCode(result);
			}
			if (applicationDTO == null) {
				LOG.info("Error while fetching Application record details");
				return ErrorCodeEnum.ERR_12114.setErrorCode(result);
			} else if (!applicationDTO.isSelfApprovalEnabled() && createdby != null
					&& createdby.equalsIgnoreCase(customerId)) {
				return ErrorCodeEnum.ERR_12113.setErrorCode(result);
			}

			if (StringUtils.isNotBlank(createdby) && createdby.equalsIgnoreCase(customerId)) {
				comments = "SelfApproved";
			}
			/*
			 * ApprovalRequestDTO request =
			 * _fetchRequestDetails(requestId,featureActionId,dcr); if(request == null) {
			 * LOG.debug("No request found with given requestId"); return
			 * ErrorCodeEnum.ERR_12000.setErrorCode(result); }
			 */
			LOG.debug("Entry --> SbgApprovalQueueResourceImplExt::featureActionId = " + featureActionId);
			SimpleDateFormat fileFormat = new SimpleDateFormat(Constants.SHORT_DATE_FORMAT);
			fileFormat.setLenient(false);
			String key = "";
			String operationName = "";
			String serviceName = "";
			/* CQL-7332 Validation to check if value date has not expired */
			if (featureActionId.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE) ||
					featureActionId.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE)) {
				if (isTransactionValueDateValid(requestId, FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE,
						"internationalfundtransfers") == false) {
					LOG.debug("approve::Transaction has passed the value date");
					return SbgErrorCodeEnum.ERR_100028.setErrorCode(result);
				}
				key = "internationalfundtransfers";
				operationName = SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET;
				serviceName = SbgURLConstants.SERVICE_SBGCRUD;
			} else if (featureActionId.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE) ||
					featureActionId.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE)) {
				if (isTransactionValueDateValid(requestId, FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE,
						"interbankfundtransfers") == false) {
					LOG.debug("approve::Transaction has passed the value date");
					return SbgErrorCodeEnum.ERR_100028.setErrorCode(result);
				}
				key = "interbankfundtransfers";
				operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET;
				serviceName = SbgURLConstants.SERVICE_SBGCRUD;
			} else if (featureActionId.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE) ||
					featureActionId.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {
				if (isTransactionValueDateValid(requestId, FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE,
						"ownaccounttransfers") == false) {
					LOG.debug("approve::Transaction has passed the value date");
					return SbgErrorCodeEnum.ERR_100028.setErrorCode(result);
				}
				key = "ownaccounttransfers";
				operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET;
				serviceName = SbgURLConstants.SERVICE_SBGCRUD;
			}

			// cutt off time validation
			JSONObject cutOffTimeResponseObj = sbgCutoffAndLeadDaysValidations(dcr, requestDTO.getTransactionId(),
					serviceName, operationName, key);

			LOG.debug("response of cuffdays --> js::dcr" + cutOffTimeResponseObj);
			LOG.debug("response of cuffdays --> js::approve" + cutOffTimeResponseObj.getString("isValid"));
			// LOG.debug("response of cuffdays -->
			// js::approve"+cutOffTimeResponseObj.getString("message"));
			if (SBGConstants.FALSE.equalsIgnoreCase(cutOffTimeResponseObj.getString("isValid"))) { // if
																									// (isTransactionValueDateValid(requestId)
																									// == false) {
				return SbgErrorCodeEnum.ERR_100055.setErrorCode(result);
			}

			Object res = null;
			switch (featureActionId) {
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE:
				case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE:
				case FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE:
				case FeatureAction.BILL_PAY_APPROVE:
				case FeatureAction.P2P_APPROVE:
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE:
					res = approvalQueueBusinessDelegate.approveGeneralTransaction(requestId, customerId, comments,
							requestDTO.getCompanyId());
					break;
				case FeatureAction.ACH_FILE_APPROVE:
					res = approvalQueueBusinessDelegate.approveACHFile(requestId, customerId, comments,
							requestDTO.getCompanyId());
					break;
				case FeatureAction.ACH_COLLECTION_APPROVE:
				case FeatureAction.ACH_PAYMENT_APPROVE:
					res = approvalQueueBusinessDelegate.approveACHTransaction(requestId, customerId, comments,
							requestDTO.getCompanyId());
					break;
				case FeatureAction.BULK_PAYMENT_REQUEST_APPROVE:
					res = bulkPaymentRecordBusinessDelegate.approveBulkPaymentRecord(requestId, customerId, comments,
							requestDTO.getCompanyId());
					break;
				case FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE:
					res = approvalQueueBusinessDelegate.approveChequeBookrequest(requestId, customerId, comments,
							requestDTO.getCompanyId());
					break;
			}

			if (res == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
			} else if (res instanceof BBRequestDTO) {
				LOG.info("##res is an instance of BBRequestDTO");
				result.addStringParam("transactionId", ((BBRequestDTO) res).getTransactionId());
				result.addStringParam("featureActionId", ((BBRequestDTO) res).getFeatureActionId());
				try {
					LogEvents.pushAlertsForApprovalActions(dcr, response, res, featureActionId,
							CustomerSession.getCustomerName(customer), false, "APPROVE", customer);
				} catch (Exception e) {
					LOG.error("Failed at LogEvents.pushAlertsForApprovalActions");
				}
			} else {
				try {
					LogEvents.pushAlertsForApprovalActions(dcr, response, res, featureActionId,
							CustomerSession.getCustomerName(customer), true, "APPROVE", customer);
				} catch (Exception e) {
					LOG.error("Failed at LogEvents.pushAlertsForApprovalActions");
				}
				switch (featureActionId) {
					case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE:
					case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE:
					case FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE:
					case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE:
					case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE:
					case FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE:
					case FeatureAction.BILL_PAY_APPROVE:
					case FeatureAction.P2P_APPROVE:
					case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
					case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
					case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE:
					case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE:
						result.addStringParam("transactionId", ((JSONObject) res).getString("Transaction_id"));
						result.addStringParam("featureActionId", ((JSONObject) res).getString("FeatureAction_id"));
						generalTransactionsBusinessDelegate.executeTransactionAfterApproval(
								((JSONObject) res).getString("Transaction_id"), featureActionId, dcr);
						break;
					case FeatureAction.ACH_FILE_APPROVE:
						result.addStringParam("transactionId", ((ACHFileDTO) res).getAchFile_id());
						result.addStringParam("featureActionId", ((ACHFileDTO) res).getFeatureActionId());
						achFileBusinessDelegate.executeACHFileAfterApproval(((ACHFileDTO) res).getAchFile_id(),
								featureActionId, dcr);
						break;
					case FeatureAction.ACH_COLLECTION_APPROVE:
					case FeatureAction.ACH_PAYMENT_APPROVE:
						result.addStringParam("transactionId", ((ACHTransactionDTO) res).getTransaction_id());
						result.addStringParam("featureActionId", ((ACHTransactionDTO) res).getFeatureActionId());
						achTransactionBusinessDelegate.executeTransactionAfterApproval(
								((ACHTransactionDTO) res).getTransaction_id(), featureActionId, dcr);
						break;
					case FeatureAction.BULK_PAYMENT_REQUEST_APPROVE:
						result.addStringParam("transactionId", ((BulkPaymentRecordDTO) res).getRecordId());
						result.addStringParam("featureActionId", ((BulkPaymentRecordDTO) res).getFeatureActionId());
						BulkPaymentRecordDTO recordDTO = bulkPaymentRecordBusinessDelegate
								.fetchBulkPaymentRecordByRequestId(requestId, dcr);
						((BulkPaymentRecordDTO) res).setConfirmationNumber(recordDTO.getRecordId());
						((BulkPaymentRecordDTO) res).setRequestId(requestId);
						((BulkPaymentRecordDTO) res).setTotalAmount(recordDTO.getTotalAmount());
						((BulkPaymentRecordDTO) res).setFromAccount(recordDTO.getFromAccount());
						((BulkPaymentRecordDTO) res).setPaymentDate(recordDTO.getPaymentDate());
						bulkPaymentRecordBusinessDelegate.executeRecordAfterApproval(
								((BulkPaymentRecordDTO) res).getRecordId(), dcr, response, result);
						break;
					case FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE:
						approvalQueueBusinessDelegate.executeChequeBookRequestAfterApproval(requestId,
								((JSONObject) res).getString("Transaction_id"), featureActionId, dcr);
						break;

				}
			}
			result.addStringParam("success", "Successful");
			result.addStringParam("actedBy", customerId);
			result.addStringParam("status", TransactionStatusEnum.APPROVED.getStatus());
			result.addStringParam("requestId", requestId);

			LOG.debug("Updating transaction table for domesticpaymentid for approve: " + requestDTO.getTransactionId());
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String tranxStatus = GetTransStatusAfterSubmitingPayment(requestDTO.getRequestId());
			inputParams.put("tranxId", requestDTO.getTransactionId());
			inputParams.put("tranxStatus", tranxStatus);
			LOG.debug("Updating transaction table for id: " + requestDTO.getTransactionId() + "; status: "
					+ TransactionStatusEnum.APPROVED.getStatus());
			LOG.debug("inputArray: " + inputParams);

			new SbgUpdateTransferHelper().invoke(methodID, inputArray, dcr, response);

			/* Adding audit logs in separate try catch to avoid showing error on UI */
			try {
				logTransaction(dcr, response, result, res, featureActionId);
			} catch (Exception e) {
				LOG.error("Caught exception while auditing event logs: " + e);
			}

		} catch (Exception e) {
			LOG.error("Caught exception at approve method: " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
		return result;
	}

	private String GetTransStatusAfterSubmitingPayment(String requestId) {

		BBRequestDTO requestDTO = approvalQueueBusinessDelegate.getBbRequest(requestId);

		if (requestDTO == null) {
			return DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
		} else {

			if (requestDTO.getStatus().equalsIgnoreCase(DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED)) {
				return DBPUtilitiesConstants.TRANSACTION_STATUS_FAILED;
			}

			else if (requestDTO.getStatus().equalsIgnoreCase("Pending")) {
				return "Pending";
			}

			else {
				return "Pending processing";
			}

		}
	}

	private boolean isTransactionValueDateValid(String requestId, String featureActionId, String key) {
		LOG.info("Entry --> SbgApprovalQueueResourceImplExt::isTransactionValueDateValid");
		boolean result = false;

		String filter = "requestId" + DBPUtilitiesConstants.EQUAL + requestId;

		JSONObject serviceResponse = null;

		if (featureActionId.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE)) {
			SbgInternationalFundTransferBusinessDelegateExtn sbgInternationalFundTransferDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(SbgInternationalFundTransferBusinessDelegateExtn.class);
			serviceResponse = sbgInternationalFundTransferDelegate.fetchTransactionEntryFiltered(filter);
		} else if (featureActionId.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE)) {
			SbgInterBankFundTransferBusinessDelegateExtn sbgInterbankFundTransferDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(SbgInterBankFundTransferBusinessDelegateExtn.class);
			serviceResponse = sbgInterbankFundTransferDelegate.fetchTransactionEntryFiltered(filter);
		} else if (featureActionId.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE)) {
			OwnAccountFundTransferBusinessDelegateExtn ownAccountFundTransferBusinessDelegateExtn = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(OwnAccountFundTransferBusinessDelegateExtn.class);
			serviceResponse = ownAccountFundTransferBusinessDelegateExtn.fetchTransactionEntryFiltered(filter);
		}

		if (serviceResponse != null && serviceResponse.has(key)
				&& serviceResponse.getJSONArray(key).length() > 0) {
			JSONArray internationalfundtransfers = serviceResponse.getJSONArray(key);
			JSONObject fundTransfers = internationalfundtransfers.getJSONObject(0);
			String scheduledDate = fundTransfers.optString("scheduledDate");
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String formattedDate = SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss",
						SbgURLConstants.PATTERN_YYYY_MM_DD, scheduledDate);
				Date schedulateDt = formatter.parse(formattedDate);
				String currentDateStr = formatter.format(new Date());
				Date currentDate = formatter.parse(currentDateStr);
				LOG.info("Current Date: " + currentDate + " ScheduledDate: " + schedulateDt);
				if (schedulateDt.compareTo(currentDate) >= 0) {
					LOG.info("Transaction has not passed the value date");
					result = true;
				} else {
					LOG.info("Transaction has passed the value date");
					result = false;
				}

			} catch (ParseException e) {
				LOG.error("Error occured while validating dates: " + e);
			}
		}

		return result;
	}

	private void logTransaction(DataControllerRequest dcr, DataControllerResponse response, Result result, Object res,
			String featureActionId) {
		LOG.info("Entry --> SbgApprovalQueueResourceImplExt_logTransaction:: " + featureActionId);
		try {
			switch (featureActionId) {
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE:
				case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE:
				case FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE:
				case FeatureAction.BILL_PAY_APPROVE:
				case FeatureAction.P2P_APPROVE:
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE: {
					String createdBy = "";
					if (res instanceof BBRequestDTO) {
						createdBy = ((BBRequestDTO) res).getCreatedby();
					} else {
						createdBy = ((ApprovalRequestDTO) JSONUtils.parse(res.toString(), ApprovalRequestDTO.class))
								.getSentBy();
					}
					_logGeneralTransactionSBG(dcr, response, result, createdBy);
				}
					break;
				case FeatureAction.ACH_FILE_APPROVE:
					_logACHFile(dcr, response, result, dcr.getParameter(Constants.REQUESTID), "Approved");
					break;
				case FeatureAction.ACH_COLLECTION_APPROVE:
				case FeatureAction.ACH_PAYMENT_APPROVE:
					loggingForACHTransactionsAndTemplates(dcr, response, res, result);
					break;
				case FeatureAction.BULK_PAYMENT_REQUEST_APPROVE:
					bulkPaymentRecordResource.logApproveOrRejectTransaction(dcr, response, res, null, result);
					break;
				default:
					return;
			}
		} catch (IOException e) {
			LOG.error("error while logging the approval of transaction", e);
		}
	}

	void _logGeneralTransactionSBG(DataControllerRequest request, DataControllerResponse response, Result result,
			String createdBy) {
		LOG.info("Entry --> SbgApprovalQueueResourceImplExt__logGeneralTransactionSBG");
		String enableEvents = EnvironmentConfigurationsHandler.getValue(Constants.ENABLE_EVENTS, request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;

		GeneralTransactionsBusinessDelegate generalTransactionsBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(GeneralTransactionsBusinessDelegate.class);

		ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApproversBusinessDelegate.class);

		AuditLog auditLog = new AuditLog();

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String transactionId = "", featureActionId = "";
		LOG.info("_logGeneralTransactionSBG: customer map: " + customer);

		try {

			/*
			 * In certain scenario, result object does not have Transaction_id and
			 * FeatureAction_id, so adding null check
			 */
			if (result.getParamValueByName("Transaction_id") != null
					&& result.getParamValueByName("FeatureAction_id") != null) {
				transactionId = result.getParamValueByName("Transaction_id");
				featureActionId = result.getParamValueByName("FeatureAction_id");
			} else {
				transactionId = result.getParamValueByName("transactionId");
				featureActionId = result.getParamValueByName("featureActionId");
			}

			LOG.info("_logGeneralTransactionSBG:::TransactionID: " + transactionId + " FeatureActionId: "
					+ featureActionId);

			if (StringUtils.isEmpty(createdBy))
				createdBy = "";
			JsonObject customParams = generalTransactionsBusinessDelegate.fetchTransactionEntry(transactionId,
					featureActionId, createdBy);

			if (customParams == null) {
				LOG.info("_logGeneralTransactionSBG:: CUSTOMPARAMS OBJECT IS NULL. CREATING NEW ONE ...");
				customParams = new JsonObject();
			}
			String eventType = Constants.MAKE_TRANSFER;
			String eventSubType = "";
			String producer = "Transactions/POST(createTransfer)";
			String statusID = Constants.SID_EVENT_SUCCESS;
			String userName = CustomerSession.getCustomerName(customer);
			String requestId = request.getParameter("Request_id");
			String action = request.getParameter("Action");

			if (action == null) {
				action = "";
			}

			String fromAccountNumber = "";
			String toAccountNumber = "";

			if (customParams.has("fromAccountNumber")) {
				fromAccountNumber = customParams.get("fromAccountNumber").getAsString();
			}
			if (customParams.has("toAccountNumber")) {
				toAccountNumber = customParams.get("toAccountNumber").getAsString();
			}
			;

			customParams = auditLog.buildCustomParamsForAlertEngine(fromAccountNumber, toAccountNumber, customParams);

			eventSubType = deriveSubType(featureActionId, customParams);
			LOG.info("DerivedSubType: " + eventSubType + " FeatureActionId: " + featureActionId);

			List<String> approvers = approversBusinessDelegate.getRequestApproversList(requestId);
			List<String> approvedBy = approversBusinessDelegate.getRequestActedApproversList(requestId, "Approved");
			if (approvers == null) {
				customParams.addProperty(Constants.APPROVERS, "");
			} else {
				customParams.addProperty(Constants.APPROVERS, approvers.toString());
			}
			if (approvedBy == null) {
				customParams.addProperty("approvedBy", "");
			} else {
				customParams.addProperty("approvedBy", approvedBy.toString());
			}
			List<String> rejectedBy = approversBusinessDelegate.getRequestActedApproversList(requestId, "Rejected");
			if (rejectedBy == null) {
				customParams.addProperty("rejectedBy", "N/A");
			} else {
				customParams.addProperty("rejectedBy", rejectedBy.toString());
			}

			/* Swapping action with Rejected to avoid null pointer exception */
			if (("Rejected").equalsIgnoreCase(action)) {
				eventSubType = Constants.REJECTED_ + eventSubType;
				customParams.addProperty(Constants.STATUS, TransactionStatusEnum.REJECTED.getStatus());
			} else if ("Withdraw".equals(action)) {
				eventSubType = TransactionStatusEnum.WITHDRAWN.getStatus() + "INTERNATIONAL_ACCOUNT_FUND_TRANSFER";
				customParams.addProperty(Constants.STATUS, TransactionStatusEnum.WITHDRAWN.getStatus());
			} else {
				customParams.addProperty(Constants.STATUS, TransactionStatusEnum.APPROVED.getStatus());
			}
			if (customParams.has("transactionType")) {
				customParams.addProperty("transferType", customParams.get("transactionType").getAsString());
				customParams.remove("transactionType");
			}
			customParams.remove(Constants.CREATEDTS);
			String[] dates = { "deliverBy", "lastmodifiedts", "frequencystartdate", "frequencyenddate", "scheduledDate",
					"processingDate", "frequencyStartDate", "frequencyEndDate", "synctimestamp" };
			for (String date : dates) {
				if (customParams.has(date)) {
					customParams.get(date);
					if (date != null) {
						String dateValue = customParams.get(date).getAsString();
						dateValue = HelperMethods.convertDateFormat(dateValue, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						customParams.remove(date);
						customParams.addProperty(date, dateValue);
					}
				}
			}

			AdminUtil.addAdminUserNameRoleIfAvailable(customParams, request);
			LOG.info("##Final EventType: " + eventType + " EventSubType: " + eventSubType);
			EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusID, null, userName,
					customParams);
		} catch (Exception e) {
			LOG.error("Error while pushing to Audit Engine.");
		}
	}

	private String deriveSubType(String featureActionId, JsonObject customParams) {
		AuditLog auditLog = new AuditLog();
		String eventSubType = "";

		String isScheduled = "";
		if (customParams.has(Constants.ISSCHEDULED)) {
			isScheduled = customParams.get(Constants.ISSCHEDULED).getAsString();
		}
		String frequencyType = Constants.ONCE;
		if (customParams.has(Constants.FREQUENCYTYPE)) {
			JsonElement frequencyTypeJson = customParams.get(Constants.FREQUENCYTYPE);
			if (frequencyTypeJson != null)
				frequencyType = customParams.get(Constants.FREQUENCYTYPE).getAsString();
		}
		Boolean payeeId = customParams.has("payeeId");
		String wireAccountType = "";
		String serviceName = customParams.get("featureActionId").getAsString();
		switch (featureActionId) {
			case FeatureAction.BILL_PAY_CREATE:
				eventSubType = auditLog.deriveSubTypeForBillPayment(payeeId);
				break;

			case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE:
			case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL:
				eventSubType = auditLog.deriveSubTypeForExternalTransfer(isScheduled, frequencyType, serviceName);
				break;

			case FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE:
			case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL:
				eventSubType = auditLog.deriveSubTypeForExternalTransfer(isScheduled, frequencyType, serviceName);
				break;

			case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE:
			case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL:
				eventSubType = auditLog.deriveSubTypeForExternalTransfer(isScheduled, frequencyType, serviceName);
				break;

			case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE:
			case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL:
				eventSubType = auditLog.deriveSubTypeForInternalTransfer(isScheduled, frequencyType);
				break;

			case FeatureAction.P2P_CREATE:
				eventSubType = auditLog.deriveSubTypeForP2PTransfer(isScheduled, frequencyType, payeeId);
				break;

			case FeatureAction.DOMESTIC_WIRE_TRANSFER_CREATE:
			case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_CREATE:
				if (customParams.has("wireAccountType")) {
					wireAccountType = customParams.get("wireAccountType").getAsString();
				}
				eventSubType = auditLog.deriveSubTypeForWireTransfer(payeeId, serviceName, wireAccountType);
				break;

			default:
				break;
		}
		return eventSubType;
	}

	@Override
	public Result reject(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String customerId = CustomerSession.getCustomerId(customer);
			String requestId = request.getParameter("requestId");
			String comments = request.getParameter("comments");
			String featureActionId = request.getParameter("featureActionId");

			LOG.info("SbgApprovalQueueResourceImplExt.reject() ===> customerId:" + customerId + "; requestId:"
					+ requestId + "; featureActionId:" + featureActionId);

			if (StringUtils.isNotEmpty(featureActionId)) {
				featureActionId = FeatureActionBusinessDelegate.getApproveFeatureAction(featureActionId);
			}
			if (StringUtils.isEmpty(featureActionId)) {
				return ErrorCodeEnum.ERR_28025.setErrorCode(result);
			}
			LOG.info("reject() ===> featureActionId:" + featureActionId);

			List<String> requiredApproveActionIds = Arrays.asList(featureActionId);
			String approveActionList = CustomerSession.getPermittedActionIds(request, requiredApproveActionIds);
			if (approveActionList == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ContractBusinessDelegate.class);
			List<String> contracts = contractDelegate.fetchContractCustomers(customerId);
			contracts.add(CustomerSession.getCompanyId(customer));

			BBRequestDTO bbrequestObject = approvalQueueBusinessDelegate
					.authorizationCheckForRejectAndWithdrawl(requestId, String.join(",", contracts), approveActionList);
			if (bbrequestObject == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			String groupName = null;
			BBRequestDTO requestDTO = approvalQueueBusinessDelegate.getBbRequest(requestId);
			if (requestDTO == null) {
				LOG.debug("Error while fetching BBRequest details");
				return ErrorCodeEnum.ERR_14008.setErrorCode(result);
			}
			LOG.debug("reject() ===> BBRequestDTO:" + requestDTO.getRequestId() + "; " + requestDTO.getTransactionId()
					+ "; " + requestDTO.getStatus());

			boolean isGroupMatrix = Boolean.parseBoolean(requestDTO.getIsGroupMatrix());

			SignatoryGroupBusinessDelegate sigGrpBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(SignatoryGroupBusinessDelegate.class);
			if (isGroupMatrix) {
				List<CustomerSignatoryGroupDTO> signatoryDTO = sigGrpBusinessDelegate
						.fetchCustomerSignatoryGroups(customerId);
				if (signatoryDTO == null || signatoryDTO.isEmpty())
					return ErrorCodeEnum.ERR_12118.setErrorCode(new Result());

				List<String> groupNames = signatoryDTO.stream().map(CustomerSignatoryGroupDTO::getSignatoryGroupName)
						.collect(Collectors.toList());
				groupName = String.join(",", groupNames);
			}

			if (SBGCommonUtils.isStringEmpty(comments)) {
				comments = "Rejected";
			}

			String status = requestDTO.getStatus();
			if (status == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			} else if (!(status.equals(TransactionStatusEnum.PENDING.getStatus()))) {
				return ErrorCodeEnum.ERR_21012.setErrorCode(new Result());
			}
			status = TransactionStatusEnum.REJECTED.getStatus();
			BBRequestDTO bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, status);
			if (bBRequestDTO == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
			LOG.debug("reject() ===> bBRequestDTO:" + bBRequestDTO.getRequestId() + "; "
					+ bBRequestDTO.getTransactionId() + "; " + bBRequestDTO.getStatus());

			switch (featureActionId) {
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE:
				case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE:
				case FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE:
				case FeatureAction.BILL_PAY_APPROVE:
				case FeatureAction.P2P_APPROVE:
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE:
					generalTransactionsBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getFeatureActionId(), bBRequestDTO.getStatus());
					// generalTransactionsBusinessDelegate.rejectGeneralTransaction(bBRequestDTO.getTransactionId(),
					// bBRequestDTO.getFeatureActionId(),request);
					break;
				case FeatureAction.ACH_FILE_APPROVE:
					achFileBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(), bBRequestDTO.getStatus(), "");
					_logACHFile(request, response, result, requestId, "Rejected");
					break;
				case FeatureAction.ACH_COLLECTION_APPROVE:
				case FeatureAction.ACH_PAYMENT_APPROVE:
					achTransactionBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getStatus(),
							"");
					loggingForACHTransactionsAndTemplates(request, response, bBRequestDTO, result);
					break;
				case FeatureAction.BULK_PAYMENT_REQUEST_APPROVE:
					String rejectionreason = request.getParameter("rejectionreason");
					/*
					 * if (StringUtils.isEmpty(rejectionreason)) {
					 * LOG.debug("rejectionreason is missing"); return
					 * ErrorCodeEnum.ERR_21227.setErrorCode(new Result()); }
					 */
					bulkPaymentRecordBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getStatus(), "", "");
					bulkPaymentRecordResource.logApproveOrRejectTransaction(request, response, bBRequestDTO,
							EventSubType.BULK_PAYMENT_REQUEST_REJECT, result);
					try {
						BulkPaymentRecordBackendDelegate bulkPaymentRecordBackendDelegate = DBPAPIAbstractFactoryImpl
								.getBackendDelegate(BulkPaymentRecordBackendDelegate.class);
						bulkPaymentRecordBackendDelegate.rejectBulkPaymentRecord(bBRequestDTO.getTransactionId(),
								comments,
								rejectionreason, request);
					} catch (Exception e) {
						LOG.error("Error while updating the status of Bulk Payment record", e);
					}
					break;
				case FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE:
					Boolean isRejected = approvalQueueBusinessDelegate
							.rejectChequeBookRequest(bBRequestDTO.getTransactionId(), comments, request);
					if (!isRejected) {
						status = TransactionStatusEnum.PENDING.getStatus();
						bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, status);
						return ErrorCodeEnum.ERR_12116.setErrorCode(new Result());
					}
					break;
			}

			approvalQueueBusinessDelegate.logActedRequest(bBRequestDTO.getRequestId(), bBRequestDTO.getCompanyId(),
					bBRequestDTO.getStatus(), comments, customerId, bBRequestDTO.getStatus(), groupName);

			LOG.debug("reject() ===> logActedRequest complete ");

			try {
				LogEvents.pushAlertsForApprovalActions(request, response, bBRequestDTO, featureActionId,
						CustomerSession.getCustomerName(customer), false, "REJECT", customer);
			} catch (Exception e) {
				LOG.error("Failed at LogEvents.pushAlertsForApprovalActions");
			}
			LOG.debug("reject() ===> pushed alerts ");

			result.addStringParam("transactionId", bBRequestDTO.getTransactionId());
			result.addStringParam("featureActionId", bBRequestDTO.getFeatureActionId());
			result.addStringParam("actedBy", customerId);
			result.addStringParam("status", bBRequestDTO.getStatus());
			result.addStringParam("requestId", requestId);
			LOG.debug("reject() ===> completed");

			try {
				request.setAttribute("Action", "Rejected");
				request.addRequestParam_("Action", "Rejected");
				_logGeneralTransactionSBG(request, response, result, status);
			} catch (Exception e) {
				LOG.error("Error capturing audit logs for reject payment : " + e.getMessage());
			}

			LOG.debug("Updating transaction table for domesticpaymentid for reject: " + requestDTO.getTransactionId());
			Map inputParams = HelperMethods.getInputParamMap(inputArray);
			inputParams.put("tranxId", requestDTO.getTransactionId());
			inputParams.put("tranxStatus", TransactionStatusEnum.REJECTED.getStatus());
			new SbgUpdateTransferHelper().invoke(methodID, inputArray, request, response);
			LOG.debug("Updating transaction table completed");

			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at reject method: " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
	}

	@Override
	public Result withdraw(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> START");
		Result result = new Result();
		try {
			String featureActionId = request.getParameter("featureActionId");
			String requestId = request.getParameter("requestId");
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String customerId = CustomerSession.getCustomerId(customer);
			String createActionList = CustomerSession.getPermittedActionIds(request, Arrays.asList(featureActionId));
			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> requestId:" + requestId + "; customerId:"
					+ customerId);

			if (createActionList == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ContractBusinessDelegate.class);
			List<String> contracts = contractDelegate.fetchContractCustomers(customerId);
			contracts.add(CustomerSession.getCompanyId(customer));

			CustomerBusinessDelegate custDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(CustomerBusinessDelegate.class);
			BBRequestDTO bbrequestObject = approvalQueueBusinessDelegate
					.authorizationCheckForRejectAndWithdrawl(requestId, String.join(",", contracts), createActionList);
			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> checking for authorization");

			if (bbrequestObject != null && !((CustomerSession.IsCombinedUser(customer)
					&& custDelegate.getCombinedUserIds(customerId).contains(bbrequestObject.getCreatedby()))
					|| (customerId.equals(bbrequestObject.getCreatedby())))) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			String comments = request.getParameter("comments");
			if (SBGCommonUtils.isStringEmpty(comments)) {
				comments = "Withdrawn";
			}
			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> comments:" + comments);

			String status = approvalQueueBusinessDelegate.getRequestStatus(requestId);
			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> status:" + status);

			if (status == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			} else if (!(status.equals(TransactionStatusEnum.PENDING.getStatus()))) {
				return ErrorCodeEnum.ERR_21011.setErrorCode(new Result());
			}
			status = TransactionStatusEnum.WITHDRAWN.getStatus();
			BBRequestDTO bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, status);
			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> bbrequest updated ... bBRequestDTO:"
					+ bBRequestDTO);

			if (bBRequestDTO == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}

			switch (featureActionId) {
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE:
				case FeatureAction.INTERNATIONAL_WIRE_TRANSFER_CREATE:
				case FeatureAction.DOMESTIC_WIRE_TRANSFER_CREATE:
				case FeatureAction.BILL_PAY_CREATE:
				case FeatureAction.P2P_CREATE:
				case FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL:
				case FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL:
				case FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL:
				case FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL:
					generalTransactionsBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getFeatureActionId(), bBRequestDTO.getStatus());
					// generalTransactionsBusinessDelegate.withdrawGeneralTransaction(bBRequestDTO.getTransactionId(),
					// bBRequestDTO.getFeatureActionId(), request);
					break;
				case FeatureAction.ACH_FILE_UPLOAD:
					achFileBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(), bBRequestDTO.getStatus(), "");
					break;
				case FeatureAction.ACH_COLLECTION_CREATE:
				case FeatureAction.ACH_PAYMENT_CREATE:
					achTransactionBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getStatus(),
							"");
					break;
				case FeatureAction.BULK_PAYMENT_REQUEST_SUBMIT:
					bulkPaymentRecordBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(),
							bBRequestDTO.getStatus(), "", "");
					break;
				case FeatureAction.CHEQUE_BOOK_REQUEST_CREATE:
					Boolean success = approvalQueueBusinessDelegate.withdrawRequest(bBRequestDTO.getTransactionId(),
							comments, request);
					if (!success) {
						status = TransactionStatusEnum.PENDING.getStatus();
						bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, status);
						return ErrorCodeEnum.ERR_12117.setErrorCode(new Result());
					}
					break;
			}

			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> log acted request");
			approvalQueueBusinessDelegate.logActedRequest(bBRequestDTO.getRequestId(), bBRequestDTO.getCompanyId(),
					bBRequestDTO.getStatus(), comments, customerId, bBRequestDTO.getStatus());

			try {
				LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> pushing alert");
				LogEvents.pushAlertsForApprovalActions(request, response, bBRequestDTO, featureActionId,
						CustomerSession.getCustomerName(customer), false, "WITHDRAW", customer);
			} catch (Exception e) {
				LOG.error("Failed at LogEvents.pushAlertsForApprovalActions");
			}
			result.addStringParam("transactionId", bBRequestDTO.getTransactionId());
			result.addStringParam("featureActionId", bBRequestDTO.getFeatureActionId());
			result.addStringParam("actedBy", customerId);
			result.addStringParam("status", bBRequestDTO.getStatus());
			result.addStringParam("requestId", requestId);

			LOG.debug("SbgApprovalQueueResourceImplExt.withdraw() ---> logging general transaction:");
			try {
				request.setAttribute("Action", "Withdraw");
				request.addRequestParam_("Action", "Withdraw");
				_logGeneralTransactionSBG(request, response, result, status);
			} catch (Exception e) {
				LOG.error("Error capturing audit logs for reject payment : " + e.getMessage());
			}

			LOG.debug("Updating transaction table for domesticpaymentid for withdraw: "
					+ bBRequestDTO.getTransactionId());
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			inputParams.put("tranxId", bBRequestDTO.getTransactionId());
			inputParams.put("tranxStatus", TransactionStatusEnum.CANCELLED.getStatus());
			LOG.debug("Updating transaction table for id: " + bBRequestDTO.getTransactionId() + " as withdrawn");
			new SbgUpdateTransferHelper().invoke(methodID, inputArray, request, response);

			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at withdraw method: " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
	}

	@Override
	public Result fetchAllMyPendingRequests(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws IOException {
		LOG.info("@@fetchAllMyPendingRequests()==");
		Result result = new Result();
		FilterDTO newFilterDTO = new FilterDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
		String sortByValue = "";

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		List<String> requiredActionIds = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_VIEW,
				FeatureAction.BILL_PAY_VIEW_PAYMENTS,
				FeatureAction.P2P_VIEW,
				FeatureAction.ACH_FILE_VIEW,
				FeatureAction.ACH_COLLECTION_VIEW,
				FeatureAction.ACH_PAYMENT_VIEW,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_VIEW,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_VIEW,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_VIEW,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_VIEW,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_VIEW,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_VIEW);

		List<String> requiredActionIdsToFetch = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_APPROVE,
				FeatureAction.BILL_PAY_APPROVE,
				FeatureAction.P2P_APPROVE,
				FeatureAction.ACH_FILE_APPROVE,
				FeatureAction.ACH_COLLECTION_APPROVE,
				FeatureAction.ACH_PAYMENT_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE,
				FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE);

		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		try {
			String removeByValue = inputParams.get("removeByValue") != null
					? inputParams.get("removeByValue").toString()
					: null;

			sortByValue = inputParams.get("sortByParam") != null
					? inputParams.get("sortByParam").toString()
					: null;

			if (StringUtils.isNotEmpty(removeByValue)) {
				inputParams.put("removeByValue", new HashSet<String>(Arrays.asList(removeByValue.split(","))));
			}
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
			LOG.info("@@fetchAllMyPendingRequests()==newFilterDTO=" + newFilterDTO.toString());

			String searchString = newFilterDTO.get_searchString();
			if (StringUtils.isNotBlank(searchString) && "DOMESTIC PAYMENT".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("InterBank");
			} else if (StringUtils.isNotBlank(searchString)
					&& "FOREIGN CURRENCY INTER-ACCOUNT TRANSFER".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("own");
			}

		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "",
				String.join(",", requiredActionIdsToFetch));

		if (mainRequests == null) {
			LOG.error("Error occurred while fetching requests from Approval Queue");
			return ErrorCodeEnum.ERR_21250.setErrorCode(new Result());
		}
		LOG.info("@@fetchAllMyPendingRequests()==mainRequests=" + mainRequests.toString());

		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_filterByParam("status,amICreator");
		filterDTO.set_filterByValue("Pending,true");

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);
		List<ApprovalRequestDTO> records = fetchAllRequests(subRequests, request);
		records = newFilterDTO.filter(records);
		LOG.info("@@fetchAllMyPendingRequests()==newFilterDTO=" + newFilterDTO.toString());
		LOG.info("@@fetchAllMyPendingRequests()==filterRecords=" + records.toString());
		try {

			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			LOG.info("@@fetchAllMyPendingRequests==result ==" + resultRecords.toString());
			result = JSONToResult.convert(resultObject.toString());
			LOG.info("@@fetchAllMyPendingRequests()==JSONToResult.convert(resultObject.toString()=" + result);

			// additional param code
			if (records.size() > 0) {

				Dataset resDs = result.getDatasetById("records");
				List<Record> recs = resDs.getAllRecords();

				Predicate<Record> byFeatureActionInternation = x -> x.getParamValueByName("featureActionId")
						.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);

				List<Record> internationals = recs.stream().filter(byFeatureActionInternation)
						.collect(Collectors.toList());

				if (internationals.size() > 0) {
					getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET,
							"internationalfundtransfers",
							"internationalfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);
				}

				Predicate<Record> byFeatureActionDom = x -> x.getParamValueByName("featureActionId")
						.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);

				List<Record> domestics = recs.stream().filter(byFeatureActionDom)
						.collect(Collectors.toList());

				if (domestics.size() > 0) {
					getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET, "interbankfundtransfers",
							"interbankfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET);
				}

				Predicate<Record> byFeatureActionIAT = x -> x.getParamValueByName("featureActionId")
						.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);

				List<Record> transfers = recs.stream().filter(byFeatureActionIAT)
						.collect(Collectors.toList());

				if (transfers.size() > 0) {
					getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET, "ownaccounttransfers",
							"ownaccounttransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET);
				}

				// Result rfqResult = getRFQDetails(resultRecords, request);
				// Result additionalFieldData = getApprovalAndRequestDetails(resultRecords,
				// request);
				// LOG.info("@@fetchAllMyPendingRequests()==additionalFieldData=" +
				// additionalFieldData.toString());
				// Dataset rfqDs = new Dataset();
				// Dataset resDs = new Dataset();
				// Dataset addfieldsDataSet = new Dataset();
				// addfieldsDataSet =
				// additionalFieldData.getDatasetById("internationalfundtransfers");
				// List<Record> additionalRecords = addfieldsDataSet.getAllRecords();
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=" +
				// addfieldsDataSet.toString());
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=benename=="
				// + additionalRecords.get(0).getParamValueByName("confirmationNumber"));
				// if (result != null && rfqResult != null && additionalFieldData != null) {
				// resDs = result.getDatasetById("records");
				// List<Record> rec = resDs.getAllRecords();
				// rfqDs = rfqResult.getDatasetById("internationalfundtransfersRefData");
				// List<Record> recs = rfqDs.getAllRecords();

				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < recs.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(recs.get(j).getParamValueByName("confirmationNumber").toString())) {
				// LOG.info("@@fetchAllMyPendingRequests()==beneref="
				// + recs.get(j).getParamValueByName("benerefeno"));
				// LOG.info(
				// "@@fetchAllMyPendingRequests()==statementrefno="
				// + recs.get(j).getParamValueByName("statementrefno"));
				// LOG.info("@@fetchAllMyPendingRequests()==rfqDetails="
				// + recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("statementReference",
				// recs.get(j).getParamValueByName("statementrefno"));
				// rec.get(i).addParam("beneficiaryReference",
				// recs.get(j).getParamValueByName("benerefeno"));
				// rec.get(i).addParam("rfqDetails",
				// recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("complianceCode",
				// recs.get(j).getParamValueByName("compliancecode"));
				// rec.get(i).addParam("purposeCode",
				// recs.get(j).getParamValueByName("purposecode"));
				// rec.get(i).addParam("beneficiaryState",
				// recs.get(j).getParamValueByName("beneficiaryState"));
				// rec.get(i).addParam("bopDetails",
				// recs.get(j).getParamValueByName("bopDetails"));
				// }

				// }

				// }
				// // for approvals additional fields calls internationalfundtransfers table
				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < additionalRecords.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(additionalRecords.get(j).getParamValueByName("confirmationNumber")
				// .toString())) {
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==confirmationNumber="
				// + additionalRecords.get(j).getParamValueByName("confirmationNumber"));
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==beneficiaryName="
				// + additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("beneficiaryName",
				// additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("bankName",
				// additionalRecords.get(j).getParamValueByName("bankName"));
				// rec.get(i).addParam("beneficiaryAddressLine1",
				// additionalRecords.get(j).getParamValueByName("beneficiaryAddressLine1"));
				// rec.get(i).addParam("beneficiaryZipcode",
				// additionalRecords.get(j).getParamValueByName("beneficiaryZipcode"));
				// rec.get(i).addParam("beneficiarycountry",
				// additionalRecords.get(j).getParamValueByName("beneficiarycountry"));
				// rec.get(i).addParam("beneficiaryCity",
				// additionalRecords.get(j).getParamValueByName("beneficiaryCity"));
				// }

				// }
				// }
				// }
			}
		} catch (JSONException e) {
			LOG.error("Error occured while converting the JSON to result", e);
			return ErrorCodeEnum.ERR_21217.setErrorCode(result);
		}


		return result;
	}

	// uditha CQL-12882
	public Result fetchRecordsPendingForMyApprovalCount(List<BBRequestDTO> mainRequests, Object[] inputArray,
			DataControllerRequest request,
			DataControllerResponse response) throws IOException {
		LOG.info("@@fetchRecordsPendingForMyApprovalCount==");
		Result result = new Result();
		FilterDTO newFilterDTO = new FilterDTO();

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		try {
			String removeByValue = inputParams.get("removeByValue") != null
					? inputParams.get("removeByValue").toString()
					: null;

			if (StringUtils.isNotEmpty(removeByValue)) {
				inputParams.put("removeByValue", new HashSet<String>(Arrays.asList(removeByValue.split(","))));
			}
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
			LOG.info("@@fetchRecordsPendingForMyApprovalCount()==newFilterDTO=" + newFilterDTO.toString());
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (mainRequests == null) {
			LOG.error("Error occurred while fetching requests from Approval Queue");
			return ErrorCodeEnum.ERR_21248.setErrorCode(new Result());
		}

		ApplicationDTO applicationDTO = applicationBusinessDelegate.properties();
		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_filterByParam("amIApprover,status,actedByMeAlready");
		filterDTO.set_filterByValue("true,Pending,false");

		if (applicationDTO != null && applicationDTO.isSelfApprovalEnabled()) {
			filterDTO.set_removeByParam("amICreator");
			filterDTO.set_removeByValue(new HashSet<String>(Arrays.asList("true")));
		}
		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);
		List<ApprovalRequestDTO> records = fetchAllRequests(subRequests, request);
		records = newFilterDTO.filter(records);
		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
		} catch (Exception e) {
			LOG.error("Error occured while converting the JSON to result", e);
			return ErrorCodeEnum.ERR_21217.setErrorCode(result);
		}

		return result;
	}
	// uditha CQL-12882 end

	private void getdditionalFieldData(JSONArray resultRecords, DataControllerRequest request, Result result,
			String serviceName, String OperationName, String dsName, String dsRefDataName, String refServiceName,
			String refOperationName) {
		Result rfqResult = getRFQDetails(resultRecords, request, refServiceName, refOperationName);
		Result additionalFieldData = getApprovalAndRequestDetails(resultRecords, request, serviceName, OperationName);
		LOG.debug("@@getDomestisAdditionalFieldData()==additionalFieldData=" + additionalFieldData.toString());
		Dataset rfqDs = new Dataset();
		Dataset resDs = new Dataset();
		Dataset addfieldsDataSet = new Dataset();
		addfieldsDataSet = additionalFieldData.getDatasetById(dsName);
		List<Record> additionalRecords = addfieldsDataSet.getAllRecords();
		LOG.debug("@@getDomestisAdditionalFieldData()==additionalfieldsdataset=" + addfieldsDataSet.toString());
		LOG.debug("#################### getdditionalFieldData : additionalRecords: " + ResultToJSON.convert(additionalFieldData));
		// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=benename=="
		// + additionalRecords.get(0).getParamValueByName("confirmationNumber"));
		if (result != null && rfqResult != null && additionalFieldData != null) {
			resDs = result.getDatasetById("records");
			List<Record> rec = resDs.getAllRecords();
			rfqDs = rfqResult.getDatasetById(dsRefDataName);
			List<Record> recs = rfqDs != null ? rfqDs.getAllRecords() : new ArrayList<>();

			for (int i = 0; i < rec.size(); i++) {

				// if(Constants.PENDING.equals(rec.get(i).getParamValueByName("status").toString())){
				// rec.get(i).getParamByName("status")
				// .setValue(SBGConstants.PENDINGAPPROVAL);
				// }

				rec.get(i).getParamByName("status")
						.setValue(SBGCommonUtils.toLowecaseInvariant(rec.get(i).getParamValueByName("status")));

				if (rec.get(i).hasParamByName("featureActionId")) {
					if (rec.get(i).getParamValueByName("featureActionId")
							.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE)) {
						rec.get(i).getParamByName("featureName").setValue(SBGConstants.INTERNATIONAL_PAYMENT);
					} else if (rec.get(i).getParamValueByName("featureActionId")
							.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE)) {
						rec.get(i).getParamByName("featureName").setValue(SBGConstants.DOMESTIC_PAYMENT);
					} else if (rec.get(i).getParamValueByName("featureActionId")
							.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {
						rec.get(i).getParamByName("featureName").setValue(SBGConstants.IATFX_PAYMENT);
					}
				}

				for (int j = 0; j < recs.size(); j++) {
					if (rec.get(i).getParamValueByName("confirmationNumber").replace(Constants.REFERENCE_KEY, "")
							.toString()
							.equals(recs.get(j).getParamValueByName("confirmationNumber")
									.replace(Constants.REFERENCE_KEY, "").toString())) {
						LOG.debug("@@getDomestisAdditionalFieldData()==beneref="
								+ recs.get(j).getParamValueByName("benerefeno"));
						LOG.debug(
								"@@getDomestisAdditionalFieldData()==statementrefno="
										+ recs.get(j).getParamValueByName("statementrefno"));
						LOG.debug("@@getDomestisAdditionalFieldData()==rfqDetails="
								+ recs.get(j).getParamValueByName("rfqDetails"));
						rec.get(i).addParam("statementReference",
								recs.get(j).getParamValueByName("statementrefno"));
						rec.get(i).addParam("beneficiaryReference",
								recs.get(j).getParamValueByName("benerefeno"));
						rec.get(i).addParam("rfqDetails", recs.get(j).getParamValueByName("rfqDetails"));
						rec.get(i).addParam("complianceCode",
								recs.get(j).getParamValueByName("compliancecode"));
						rec.get(i).addParam("purposeCode", recs.get(j).getParamValueByName("purposecode"));
						rec.get(i).addParam("beneficiaryState",
								recs.get(j).getParamValueByName("beneficiaryState"));
						rec.get(i).addParam("bopDetails", recs.get(j).getParamValueByName("bopDetails"));
						rec.get(i).addParam("beneficiaryPhone", recs.get(j).getParamValueByName("beneficiaryPhone"));
						rec.get(i).addParam("beneficiaryEmail", recs.get(j).getParamValueByName("beneficiaryEmail"));
						rec.get(i).addParam("beneficiaryCategory",
								recs.get(j).getParamValueByName("beneficiaryCategory"));
						rec.get(i).addParam("proofOfPayment",
								recs.get(j).getParamValueByName("proofOfPayment"));
						rec.get(i).addParam("beneCode",
								recs.get(j).getParamValueByName("beneCode"));
						rec.get(i).addParam("fromAccountName",
								recs.get(j).getParamValueByName("fromAccountName"));

						// only populate when the paymentType is not null
						if (Objects.nonNull(recs.get(j).getParamValueByName("paymentType"))) {
							rec.get(i).addParam("paymentType",
									recs.get(j).getParamValueByName("paymentType"));
						}
						rec.get(i).addParam("beneficiaryAddressLine2",
								recs.get(j).getParamValueByName("beneficiaryAddressLine2"));
						rec.get(i).addParam("exconApproval",
								recs.get(j).getParamValueByName("exconApproval"));

						Map<String, String> rfqDetails = getForeignExchangeData(recs.get(j).getParamValueByName("rfqDetails"));
						rec.get(i).addParam("exgrate", rfqDetails.get("exgrate"));
						rec.get(i).addParam("fxDealNo", rfqDetails.get("fxDealNo"));

						if (rec.get(i).getParamValueByName("featureActionId")
								.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {

							rec.get(i).addParam("beneficiaryName",
									recs.get(j).getParamValueByName("beneficiaryAddressLine2"));

							if (StringUtils.isNotBlank(recs.get(j).getParamValueByName("paymentType"))) {
								if (SbgURLConstants.IAT_PAYMENT_TYPE_DOM
										.equalsIgnoreCase(recs.get(j).getParamValueByName("paymentType"))) {
									rec.get(i).getParamByName("featureName").setValue(SBGConstants.IAT_PAYMENT);
								} else if (SbgURLConstants.IAT_PAYMENT_TYPE_FX
										.equalsIgnoreCase(recs.get(j).getParamValueByName("paymentType"))) {
									rec.get(i).getParamByName("featureName").setValue(SBGConstants.IATFX_PAYMENT);
								}
							}

						}

					}
				}
			}
			// for approvals additional fields calls interbankfundtransfers table
			for (int i = 0; i < rec.size(); i++) {
				for (int j = 0; j < additionalRecords.size(); j++) {
					if (rec.get(i).getParamValueByName("confirmationNumber").replace(Constants.REFERENCE_KEY, "")
							.toString()
							.equals(additionalRecords.get(j).getParamValueByName("confirmationNumber")
									.replace(Constants.REFERENCE_KEY, "").toString()
									.toString())) {
						LOG.debug("@@getDomestisAdditionalFieldData()==getadditionalresult()==confirmationNumber="
								+ additionalRecords.get(j).getParamValueByName("confirmationNumber"));
						LOG.debug("@@getDomestisAdditionalFieldData()==getadditionalresult()==beneficiaryName="
								+ additionalRecords.get(j).getParamValueByName("beneficiaryName"));

						if (!rec.get(i).getParamValueByName("featureActionId")
								.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {
							rec.get(i).addParam("beneficiaryName",
									additionalRecords.get(j).getParamValueByName("beneficiaryName"));
						}

						rec.get(i).addParam("bankName",
								additionalRecords.get(j).getParamValueByName("bankName"));
						rec.get(i).addParam("beneficiaryAddressLine1",
								additionalRecords.get(j).getParamValueByName("beneficiaryAddressLine1"));
						rec.get(i).addParam("beneficiaryZipcode",
								additionalRecords.get(j).getParamValueByName("beneficiaryZipcode"));
						rec.get(i).addParam("beneficiarycountry",
								additionalRecords.get(j).getParamValueByName("beneficiarycountry"));
						rec.get(i).addParam("beneficiaryCity",
								additionalRecords.get(j).getParamValueByName("beneficiaryCity"));
						rec.get(i).addParam("branchCode", additionalRecords.get(j).getParamValueByName("bicCode"));
						//rec.get(i).addParam("fromAccountName", additionalRecords.get(j).getParamValueByName("fromAccountName"));
						LOG.debug("@@getDomestisAdditionalFieldData()==branchCode="
								+ rec.get(i).getParamValueByName("bicCode"));
					}

				}
			}
		}
	}

	@Override
	public Result fetchRecordsPendingForMyApproval(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws IOException {
		LOG.info("@@fetchRecordsPendingForMyApproval==");
		Result result = new Result();
		FilterDTO newFilterDTO = new FilterDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
		String sortByValue = "";

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		List<String> requiredActionIds = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_APPROVE,
				FeatureAction.BILL_PAY_APPROVE,
				FeatureAction.P2P_APPROVE,
				FeatureAction.ACH_FILE_APPROVE,
				FeatureAction.ACH_COLLECTION_APPROVE,
				FeatureAction.ACH_PAYMENT_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE,
				FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE);

		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "",
				featureActionlist);
		LOG.info("@@fetchRecordsPendingForMyApproval()== mainRequests size => " + mainRequests.size());

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		// uditha start
		boolean isSortByBenCategory = false;
		String benCategorySortByParam = inputParams.get("sortByParam") != null
				? inputParams.get("sortByParam").toString()
				: null;
		String benCategorySortOrder = inputParams.get("sortOrder") != null
				? inputParams.get("sortOrder").toString()
				: null;
		if ("beneficiaryCategory".equalsIgnoreCase(benCategorySortByParam) && SBGCommonUtils.isStringNotEmpty(benCategorySortOrder)) {
/*			inputParams.put("sortByParam", "processingDate");
			inputParams.put("sortOrder", "DESC");*/

			inputParams.put("sortByParam", "");
			inputParams.put("sortOrder", "DESC");

			isSortByBenCategory = true;
		}

		// uditha end

		boolean isSortByBenAccountName = false;
		String benAccountNameSortByParam = inputParams.get("sortByParam") != null
				? inputParams.get("sortByParam").toString()
				: null;
		String benAccountNameSortOrder = inputParams.get("sortOrder") != null
				? inputParams.get("sortOrder").toString()
				: null;
		String inputPageSize = inputParams.get("pageSize") != null
				? inputParams.get("pageSize").toString()
				: null;
		String inputPageOffset = inputParams.get("pageOffset") != null
				? inputParams.get("pageOffset").toString()
				: null;
		if ("beneficiaryName".equalsIgnoreCase(benAccountNameSortByParam) && SBGCommonUtils.isStringNotEmpty(benAccountNameSortOrder)) {
			inputParams.put("sortByParam", "");
			inputParams.put("sortOrder", "DESC");
			isSortByBenAccountName = true;
		}

		try {
	/*		String removeByValue = inputParams.get("sortByParam") != null
					? inputParams.get("sortByParam").toString()
					: null;
*/
			String removeByValue = inputParams.get("removeByValue") != null
					? inputParams.get("removeByValue").toString()
					: null;

			if (StringUtils.isNotEmpty(removeByValue)) {
				inputParams.put("removeByValue", new HashSet<String>(Arrays.asList(removeByValue.split(","))));
			}
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
			LOG.info("@@fetchRecordsPendingForMyApproval() == newFilterDTO => " + inputParams.toString());

			String searchString = newFilterDTO.get_searchString();
			if (StringUtils.isNotBlank(searchString) && "DOMESTIC PAYMENT".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("InterBank");
			} else if (StringUtils.isNotBlank(searchString)
					&& "FOREIGN CURRENCY INTER-ACCOUNT TRANSFER".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("own");
			}

		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (mainRequests == null) {
			LOG.error("Error occurred while fetching requests from Approval Queue");
			return ErrorCodeEnum.ERR_21248.setErrorCode(new Result());
		}

		ApplicationDTO applicationDTO = applicationBusinessDelegate.properties();
		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_filterByParam("amIApprover,status,actedByMeAlready");
		filterDTO.set_filterByValue("true,Pending,false");

		if (applicationDTO != null && applicationDTO.isSelfApprovalEnabled()) {
			filterDTO.set_removeByParam("amICreator");
			filterDTO.set_removeByValue(new HashSet<String>(Arrays.asList("true")));
		}

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);
		
		List<ApprovalRequestDTO> records = fetchAllRequests(subRequests, request);

		if (!isSortByBenAccountName && !isSortByBenCategory) {
			records = newFilterDTO.filter(records);
		}

		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
			// additional params
			if (records.size() > 0) {

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET, "internationalfundtransfers",
						"internationalfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET, "interbankfundtransfers",
						"interbankfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET, "ownaccounttransfers",
						"ownaccounttransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET);
				// Result rfqResult = getRFQDetails(resultRecords, request,
				// SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);

				// Result additionalFieldData = getApprovalAndRequestDetails(resultRecords,
				// request,SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET);
				// Dataset rfqDs = new Dataset();
				// Dataset resDs = new Dataset();
				// Dataset addfieldsDataSet = new Dataset();
				// if (result != null && rfqResult != null && additionalFieldData != null) {
				// // fillter transfer records
				// resDs = result.getDatasetById("records");
				// List<Record> rec = resDs.getAllRecords();
				// // rfq details
				// rfqDs = rfqResult.getDatasetById("internationalfundtransfersRefData");
				// List<Record> recs = rfqDs.getAllRecords();
				// // additonational fields for approvals
				// addfieldsDataSet =
				// additionalFieldData.getDatasetById("internationalfundtransfers");
				// List<Record> additionalRecords = addfieldsDataSet.getAllRecords();
				// for (int i = 0; i < rec.size(); i++) {
				// LOG.info("@@fetchRecordsPendingForMyApproval()=confmnumber="
				// + rec.get(i).getParamValueByName("confirmationNumber"));
				// for (int j = 0; j < recs.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(recs.get(j).getParamValueByName("confirmationNumber").toString())) {
				// LOG.info("@@fetchRecordsPendingForMyApproval()==beneref="
				// + recs.get(j).getParamValueByName("benerefeno"));
				// LOG.info(
				// "@@fetchRecordsPendingForMyApproval()==statementrefno="
				// + recs.get(j).getParamValueByName("statementrefno"));
				// LOG.info("@@fetchRecordsPendingForMyApproval()==rfqDetails="
				// + recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("statementReference",
				// recs.get(j).getParamValueByName("statementrefno"));
				// rec.get(i).addParam("beneficiaryReference",
				// recs.get(j).getParamValueByName("benerefeno"));
				// rec.get(i).addParam("rfqDetails",
				// recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("complianceCode",
				// recs.get(j).getParamValueByName("compliancecode"));
				// rec.get(i).addParam("purposeCode",
				// recs.get(j).getParamValueByName("purposecode"));
				// rec.get(i).addParam("beneficiaryState",
				// recs.get(j).getParamValueByName("beneficiaryState"));
				// rec.get(i).addParam("bopDetails",
				// recs.get(j).getParamValueByName("bopDetails"));
				// }

				// }

				// }
				// // for approvals additional fields calls internationalfundtransfers table
				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < additionalRecords.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(additionalRecords.get(j).getParamValueByName("confirmationNumber")
				// .toString())) {
				// LOG.info(
				// "@@fetchRecordsPendingForMyApproval()==getadditionalresult()==confirmationNumber="
				// + additionalRecords.get(j).getParamValueByName("confirmationNumber"));
				// LOG.info("@@fetchRecordsPendingForMyApproval()==getadditionalresult()==beneficiaryName="
				// + additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("beneficiaryName",
				// additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("bankName",
				// additionalRecords.get(j).getParamValueByName("bankName"));
				// rec.get(i).addParam("beneficiaryAddressLine1",
				// additionalRecords.get(j).getParamValueByName("beneficiaryAddressLine1"));
				// rec.get(i).addParam("beneficiaryZipcode",
				// additionalRecords.get(j).getParamValueByName("beneficiaryZipcode"));
				// rec.get(i).addParam("beneficiarycountry",
				// additionalRecords.get(j).getParamValueByName("beneficiarycountry"));
				// rec.get(i).addParam("beneficiaryCity",
				// additionalRecords.get(j).getParamValueByName("beneficiaryCity"));
				// }

				// }
				// }
				// }
			}
		} catch (JSONException e) {
			LOG.error("Error occured while converting the JSON to result", e);
			return ErrorCodeEnum.ERR_21217.setErrorCode(result);
		}

		// uditha start
		if (isSortByBenCategory) {
			return sortByCategory(result, benCategorySortByParam, benCategorySortOrder, Integer.valueOf(inputPageSize), Integer.valueOf(inputPageOffset));
		}
		if (isSortByBenAccountName) {
			result = sortByCategory(result, benAccountNameSortByParam, benAccountNameSortOrder, Integer.valueOf(inputPageSize), Integer.valueOf(inputPageOffset));
		}

		// uditha end
		return result;
	}

	private Result sortByCategory(Result result, String sortParam, String sortOrder, int pageSize, int pageOffset) {
		Result sortedResult = new Result();
		Map<String, Record> map = new LinkedHashMap<>();
		Dataset records = result.getDatasetById("records");
		LOG.debug("srilanka records = " + records.getAllRecords().size());

		records.getAllRecords().forEach(r -> r.getAllParams().forEach(
				p -> {
					LOG.debug("srilanka param name = " + p.getName());
					LOG.debug("srilanka param value = " + p.getValue());
					if (sortParam.equalsIgnoreCase(p.getName())) {
						LOG.debug("srilanka found beneficiary name = ");
						String value = p.getValue();
						LOG.debug("srilanka  beneficiary value = " + value);
						LOG.debug("srilanka r = " + r.toString());
						//if (null != value && !value.isEmpty()) {
							map.put(value + String.valueOf(Math.random()), r);
						//}
					}
				})

		);
		LOG.debug("srilanka map = " + map.toString());
		if (!map.isEmpty()) {
			LOG.debug("srilanka map not empty");
			LinkedHashMap mapSorted = null;
			if ("DESC".equalsIgnoreCase(sortOrder)) {
				mapSorted = map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
						.collect(Collectors.toMap(
								Map.Entry::getKey, Map.Entry::getValue,
								(e1, e2) -> e1, LinkedHashMap::new));
			}else if ("ASC".equalsIgnoreCase(sortOrder)) {
				mapSorted = map.entrySet().stream().sorted(Map.Entry.comparingByKey())
						.collect(Collectors.toMap(
								Map.Entry::getKey, Map.Entry::getValue,
								(e1, e2) -> e1, LinkedHashMap::new));
			}
			LOG.debug("srilanka sorted map size = " + mapSorted.size());
			Dataset sortedDataSet = new Dataset("records");
			mapSorted.forEach((k, v) -> {
				LOG.debug("srilanka sorted dataset key = " + k);
				sortedDataSet.addRecord((Record) v);
			});
			sortedResult.addDataset(sortedDataSet);
			LOG.debug("srilanka return final sorted");
		}
		List<Record> recordsSublist = paginateBy(sortedResult.getDatasetById("records").getAllRecords(), pageSize, pageOffset);
		Dataset finalDataSet = new Dataset("records");
		finalDataSet.addAllRecords(recordsSublist);
		Result finalResult = new Result();
		finalResult.addDataset(finalDataSet);
		return finalResult;
	}

	private List<Record> paginateBy(List<Record> inputlist, int pageSize, int pageOffset) {
		
		int fromIndex = pageOffset;
		if(pageSize<1 || inputlist == null || inputlist.size()==0 || inputlist.size()<=fromIndex) {
			LOG.error("Invalid filters for pagination.");
			return new ArrayList<Record>();
		}
		int toIndex = pageOffset + pageSize;
		if(toIndex>inputlist.size()) {
			toIndex = inputlist.size();
		}
		return inputlist.subList(fromIndex, toIndex);
	}

	@Override
	public Result fetchMyRequestHistory(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws IOException {

		Result result = new Result();
		FilterDTO newFilterDTO = new FilterDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		List<String> requiredActionIds = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_VIEW,
				FeatureAction.BILL_PAY_VIEW_PAYMENTS,
				FeatureAction.P2P_VIEW,
				FeatureAction.ACH_FILE_VIEW,
				FeatureAction.ACH_COLLECTION_VIEW,
				FeatureAction.ACH_PAYMENT_VIEW,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_VIEW,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_VIEW,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_VIEW,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_VIEW,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_VIEW,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_VIEW,
				FeatureAction.CHEQUE_BOOK_REQUEST_VIEW);

		List<String> requiredIdsToFetch = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_APPROVE,
				FeatureAction.BILL_PAY_APPROVE,
				FeatureAction.P2P_APPROVE,
				FeatureAction.ACH_FILE_APPROVE,
				FeatureAction.ACH_COLLECTION_APPROVE,
				FeatureAction.ACH_PAYMENT_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE,
				FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE);

		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		try {
			String removeByValue = inputParams.get("removeByValue") != null
					? inputParams.get("removeByValue").toString()
					: null;

			if (StringUtils.isNotEmpty(removeByValue)) {
				inputParams.put("removeByValue", new HashSet<String>(Arrays.asList(removeByValue.split(","))));
			}
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
			LOG.info("@@fetchAllMyPendingRequests()==newFilterDTO=" + newFilterDTO.toString());

			String searchString = newFilterDTO.get_searchString();
			if (StringUtils.isNotBlank(searchString) && "DOMESTIC PAYMENT".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("InterBank");
			} else if (StringUtils.isNotBlank(searchString)
					&& "FOREIGN CURRENCY INTER-ACCOUNT TRANSFER".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("own");
			}

		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "",
				String.join(",", requiredIdsToFetch));

		if (mainRequests == null) {
			LOG.error("Error occurred while fetching requests from Approval Queue");
			return ErrorCodeEnum.ERR_21249.setErrorCode(new Result());
		}

		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_removeByParam("status");
		filterDTO.set_removeByValue(new HashSet<>(Arrays.asList(TransactionStatusEnum.PENDING.getStatus())));

		filterDTO.set_filterByParam("amICreator");
		filterDTO.set_filterByValue("true");

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);

		List<ApprovalRequestDTO> records = fetchAllRequests(subRequests, request);

		Set<String> recordSet = new HashSet<String>();

		for (ApprovalRequestDTO record : records) {
			recordSet.add(record.getRequestId());
		}

		List<BBActedRequestDTO> history = approvalQueueBusinessDelegate.fetchRequestHistory(null, recordSet);
		records = (new FilterDTO()).merge(records, history, "requestId=requestId", "actionts");

		records = newFilterDTO.filter(records);

		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
			// additional param code
			if (records.size() > 0) {

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET, "internationalfundtransfers",
						"internationalfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET, "interbankfundtransfers",
						"interbankfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET, "ownaccounttransfers",
						"ownaccounttransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET);

				// Result rfqResult = getRFQDetails(resultRecords, request,
				// SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);
				// Result additionalFieldData = getApprovalAndRequestDetails(resultRecords,
				// request,
				// SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET);
				// LOG.info("@@fetchAllMyPendingRequests()==additionalFieldData=" +
				// additionalFieldData.toString());
				// Dataset rfqDs = new Dataset();
				// Dataset resDs = new Dataset();
				// Dataset addfieldsDataSet = new Dataset();
				// addfieldsDataSet =
				// additionalFieldData.getDatasetById("internationalfundtransfers");
				// List<Record> additionalRecords = addfieldsDataSet.getAllRecords();
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=" +
				// addfieldsDataSet.toString());
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=benename=="
				// + additionalRecords.get(0).getParamValueByName("confirmationNumber"));
				// if (result != null && rfqResult != null && additionalFieldData != null) {
				// resDs = result.getDatasetById("records");
				// List<Record> rec = resDs.getAllRecords();
				// rfqDs = rfqResult.getDatasetById("internationalfundtransfersRefData");
				// List<Record> recs = rfqDs.getAllRecords();

				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < recs.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(recs.get(j).getParamValueByName("confirmationNumber").toString())) {
				// LOG.info("@@fetchAllMyPendingRequests()==beneref="
				// + recs.get(j).getParamValueByName("benerefeno"));
				// LOG.info("@@fetchAllMyPendingRequests()==statementrefno="
				// + recs.get(j).getParamValueByName("statementrefno"));
				// LOG.info("@@fetchAllMyPendingRequests()==rfqDetails="
				// + recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("statementReference",
				// recs.get(j).getParamValueByName("statementrefno"));
				// rec.get(i).addParam("beneficiaryReference",
				// recs.get(j).getParamValueByName("benerefeno"));
				// rec.get(i).addParam("rfqDetails",
				// recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("complianceCode",
				// recs.get(j).getParamValueByName("compliancecode"));
				// rec.get(i).addParam("purposeCode",
				// recs.get(j).getParamValueByName("purposecode"));
				// rec.get(i).addParam("beneficiaryState",
				// recs.get(j).getParamValueByName("beneficiaryState"));
				// rec.get(i).addParam("bopDetails",
				// recs.get(j).getParamValueByName("bopDetails"));
				// }

				// }

				// }
				// // for approvals additional fields calls internationalfundtransfers table
				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < additionalRecords.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber").equals(
				// additionalRecords.get(j).getParamValueByName("confirmationNumber").toString()))
				// {
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==confirmationNumber="
				// + additionalRecords.get(j).getParamValueByName("confirmationNumber"));
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==beneficiaryName="
				// + additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("beneficiaryName",
				// additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("bankName",
				// additionalRecords.get(j).getParamValueByName("bankName"));
				// rec.get(i).addParam("beneficiaryAddressLine1",
				// additionalRecords.get(j).getParamValueByName("beneficiaryAddressLine1"));
				// rec.get(i).addParam("beneficiaryZipcode",
				// additionalRecords.get(j).getParamValueByName("beneficiaryZipcode"));
				// rec.get(i).addParam("beneficiarycountry",
				// additionalRecords.get(j).getParamValueByName("beneficiarycountry"));
				// rec.get(i).addParam("beneficiaryCity",
				// additionalRecords.get(j).getParamValueByName("beneficiaryCity"));
				// }

				// }
				// }
				// }
			}
		} catch (JSONException e) {
			LOG.error("Error occured while converting the JSON to Result", e);
			return ErrorCodeEnum.ERR_21217.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result fetchMyApprovalHistory(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws IOException {

		Result result = new Result();
		FilterDTO newFilterDTO = new FilterDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		List<String> requiredActionIds = Arrays.asList(
				FeatureAction.BULK_PAYMENT_REQUEST_APPROVE,
				FeatureAction.BILL_PAY_APPROVE,
				FeatureAction.P2P_APPROVE,
				FeatureAction.ACH_FILE_APPROVE,
				FeatureAction.ACH_COLLECTION_APPROVE,
				FeatureAction.ACH_PAYMENT_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_APPROVE,
				FeatureAction.DOMESTIC_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_WIRE_TRANSFER_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE,
				FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE,
				FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE,
				FeatureAction.CHEQUE_BOOK_REQUEST_APPROVE);

		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		try {
			String removeByValue = inputParams.get("removeByValue") != null
					? inputParams.get("removeByValue").toString()
					: null;

			if (StringUtils.isNotEmpty(removeByValue)) {
				inputParams.put("removeByValue", new HashSet<String>(Arrays.asList(removeByValue.split(","))));
			}
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
			LOG.info("@@fetchAllMyPendingApprovals()==newFilterDTO=" + newFilterDTO.toString());

			String searchString = newFilterDTO.get_searchString();
			if (StringUtils.isNotBlank(searchString) && "DOMESTIC PAYMENT".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("InterBank");
			} else if (StringUtils.isNotBlank(searchString)
					&& "FOREIGN CURRENCY INTER-ACCOUNT TRANSFER".contains(searchString.toUpperCase())) {
				newFilterDTO.set_searchString("own");
			}

		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "",
				featureActionlist);

		if (mainRequests == null) {
			LOG.error("Error occurred while fetching requests from Approval Queue");
			return ErrorCodeEnum.ERR_21247.setErrorCode(new Result());
		}

		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_removeByParam("status");
		filterDTO.set_removeByValue(new HashSet<>(Arrays.asList(TransactionStatusEnum.WITHDRAWN.getStatus())));
		filterDTO.set_filterByParam("actedByMeAlready");
		filterDTO.set_filterByValue("true");
		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);

		List<ApprovalRequestDTO> records = fetchAllRequests(subRequests, request);

		List<BBActedRequestDTO> history = approvalQueueBusinessDelegate.fetchRequestHistory(customerId,
				new HashSet<String>());
		records = (new FilterDTO()).merge(records, history, "requestId=requestId", "approvalDate");

		records = newFilterDTO.filter(records);

		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
			if (records.size() > 0) {
				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET, "internationalfundtransfers",
						"internationalfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET, "interbankfundtransfers",
						"interbankfundtransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET);

				getdditionalFieldData(resultRecords, request, result, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET, "ownaccounttransfers",
						"ownaccounttransfersRefData", SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET);
				// Result rfqResult = getRFQDetails(resultRecords, request,
				// SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET);
				// Result additionalFieldData = getApprovalAndRequestDetails(resultRecords,
				// request,
				// SbgURLConstants.SERVICE_SBGCRUD,
				// SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET);
				// LOG.info("@@fetchAllMyPendingRequests()==additionalFieldData=" +
				// additionalFieldData.toString());
				// Dataset rfqDs = new Dataset();
				// Dataset resDs = new Dataset();
				// Dataset addfieldsDataSet = new Dataset();
				// addfieldsDataSet =
				// additionalFieldData.getDatasetById("internationalfundtransfers");
				// List<Record> additionalRecords = addfieldsDataSet.getAllRecords();
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=" +
				// addfieldsDataSet.toString());
				// LOG.info("@@fetchAllMyPendingRequests()==additionalfieldsdataset=benename=="
				// + additionalRecords.get(0).getParamValueByName("confirmationNumber"));
				// if (result != null && rfqResult != null && additionalFieldData != null) {
				// resDs = result.getDatasetById("records");
				// List<Record> rec = resDs.getAllRecords();
				// rfqDs = rfqResult.getDatasetById("internationalfundtransfersRefData");
				// List<Record> recs = rfqDs.getAllRecords();

				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < recs.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber")
				// .equals(recs.get(j).getParamValueByName("confirmationNumber").toString())) {
				// LOG.info("@@fetchAllMyPendingRequests()==beneref="
				// + recs.get(j).getParamValueByName("benerefeno"));
				// LOG.info("@@fetchAllMyPendingRequests()==statementrefno="
				// + recs.get(j).getParamValueByName("statementrefno"));
				// LOG.info("@@fetchAllMyPendingRequests()==rfqDetails="
				// + recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("statementReference",
				// recs.get(j).getParamValueByName("statementrefno"));
				// rec.get(i).addParam("beneficiaryReference",
				// recs.get(j).getParamValueByName("benerefeno"));
				// rec.get(i).addParam("rfqDetails",
				// recs.get(j).getParamValueByName("rfqDetails"));
				// rec.get(i).addParam("complianceCode",
				// recs.get(j).getParamValueByName("compliancecode"));
				// rec.get(i).addParam("purposeCode",
				// recs.get(j).getParamValueByName("purposecode"));
				// rec.get(i).addParam("beneficiaryState",
				// recs.get(j).getParamValueByName("beneficiaryState"));
				// rec.get(i).addParam("bopDetails",
				// recs.get(j).getParamValueByName("bopDetails"));
				// }

				// }

				// }
				// // for approvals additional fields calls internationalfundtransfers table
				// for (int i = 0; i < rec.size(); i++) {
				// for (int j = 0; j < additionalRecords.size(); j++) {
				// if (rec.get(i).getParamValueByName("confirmationNumber").equals(
				// additionalRecords.get(j).getParamValueByName("confirmationNumber").toString()))
				// {
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==confirmationNumber="
				// + additionalRecords.get(j).getParamValueByName("confirmationNumber"));
				// LOG.info("@@fetchAllMyPendingRequests()==getadditionalresult()==beneficiaryName="
				// + additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("beneficiaryName",
				// additionalRecords.get(j).getParamValueByName("beneficiaryName"));
				// rec.get(i).addParam("bankName",
				// additionalRecords.get(j).getParamValueByName("bankName"));
				// rec.get(i).addParam("beneficiaryAddressLine1",
				// additionalRecords.get(j).getParamValueByName("beneficiaryAddressLine1"));
				// rec.get(i).addParam("beneficiaryZipcode",
				// additionalRecords.get(j).getParamValueByName("beneficiaryZipcode"));
				// rec.get(i).addParam("beneficiarycountry",
				// additionalRecords.get(j).getParamValueByName("beneficiarycountry"));
				// rec.get(i).addParam("beneficiaryCity",
				// additionalRecords.get(j).getParamValueByName("beneficiaryCity"));
				// }

				// }
				// }
				// }
			}
		} catch (JSONException e) {
			LOG.error("Error occured while converting the JSONObject to result", e);
			return ErrorCodeEnum.ERR_21217.setErrorCode(result);
		}

		return result;
	}

	public Result getRFQDetails(JSONArray records, DataControllerRequest request, String serviceName,
			String operationName) {
		LOG.info("@@getRFQDetails==" + records);
		String filter = "";
		if (records != null) {
			for (int i = 0; i < records.length(); ++i) {
				JSONObject rec = records.getJSONObject(i);
				String confirmNumber = rec.getString("confirmationNumber").replace(Constants.REFERENCE_KEY, "");
				LOG.info("getRFQDetails==confirmationNumber===" + confirmNumber);
				if (filter.isEmpty())
					filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + confirmNumber;
				else
					filter = filter + DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL
							+ confirmNumber;

				// ...
			}
			LOG.info("getRFQDetails()==filter===" + filter);
			RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate) DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RefDataBackendDelegate.class);
			LOG.info("###getRFQDetails()==refbackendDelegate==" + refbackendDelegate);

			Result refDataResult = refbackendDelegate.getRFQDetails(filter, request, serviceName, operationName);
			// LOG.info("###getRFQDetails()===refDataResult=="
			// + refDataResult.getParamValueByName("internationalfundtransfersRefData"));
			return refDataResult;
		} else {
			return null;
		}
	}

	public Result getApprovalAndRequestDetails(JSONArray records, DataControllerRequest request, String serviceName,
			String operationName) {
		LOG.info("@@getApprovalAndRequestDetails==" + records);
		String filter = "";
		if (records != null) {
			for (int i = 0; i < records.length(); ++i) {
				JSONObject rec = records.getJSONObject(i);
				String confirmNumber = rec.getString("confirmationNumber");
				LOG.info("getApprovalAndRequestDetails==confirmationNumber===" + confirmNumber);
				if (filter.isEmpty())
					filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + confirmNumber;
				else
					filter = filter + DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL
							+ confirmNumber;

				// ...
			}
			LOG.info("getApprovalAndRequestDetails==filter===" + filter);
			RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate) DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RefDataBackendDelegate.class);
			Result approvalDataResult = refbackendDelegate.getApprovalAndRequestDetails(filter, request, serviceName,
					operationName);
			// LOG.info("###getApprovalAndRequestDetails==internationalfundtransfers=="
			// + approvalDataResult.getParamValueByName("internationalfundtransfers"));
			return approvalDataResult;
		} else {
			return null;
		}
	}

	public JSONObject sbgCutoffAndLeadDaysValidations(DataControllerRequest request, String transactionid,
			String serviceName, String operationName, String dsName) {
		LOG.debug("##[sbgCutoffAndLeadDaysValidations] Resource sbgValidations");
		LOG.debug("##[sbgCutoffAndLeadDaysValidations]::serviceName = " + serviceName);
		LOG.debug("##[sbgCutoffAndLeadDaysValidations]::operationName = " + operationName);
		LOG.debug("##[sbgCutoffAndLeadDaysValidations]::dsName = " + dsName);
		JSONObject sbgValidationsResponseObj = new JSONObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
		SimpleDateFormat dayFormat = new SimpleDateFormat(SBGConstants.DAY_FORMAT);
		Date scheduledDate = null;
		Date createdDate = null;
		String createdDay = null;
		JSONObject responseObj = null;
		JSONObject cutOffTimeResponse = null;
		String strScheduledDate = null;
		String day = null;
		JSONObject validateHolidaysResponse = null;
		int leadCount = 1;
		int nextWorkingDayComesAfter = 0;
		try {
			// SbgInternationalFundTransferBackendDelegateExtn
			// sbgInternationalFundTransferBackendDelegateExtn = DBPAPIAbstractFactoryImpl
			// .getBackendDelegate(SbgInternationalFundTransferBackendDelegateExtn.class);
			try {
				// headerParams = constructHeaderParams(request, headerParams, transactionid);
				// to call Ref Data Orchestretion Service service call
				// refDataResponse =
				// sbgInternationalFundTransferBackendDelegateExtn.callRefDataOrchService(headerParams,
				// requestParameters, serviceName, operationName);
				String refData = getRefData(request, transactionid, operationName, dsName);

				LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] refData: " + refData);
				if (SBGConstants.FALSE.equalsIgnoreCase(refData)) {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_EXTENSION_DATE_ERROR_MESSAGE);

				}
				responseObj = new JSONObject(refData);
				// getting schedule date from db
				String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + transactionid;

				Result approvalDataResult = null;

				if ("interbankfundtransfers".equalsIgnoreCase(dsName)) {
					RefDataBackendDelegateInterBank refbackendDelegate = (RefDataBackendDelegateInterBank) DBPAPIAbstractFactoryImpl
							.getBackendDelegate(RefDataBackendDelegateInterBank.class);
					approvalDataResult = refbackendDelegate.getApprovalAndRequestDetails(filter, request);
				} else {
					RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate) DBPAPIAbstractFactoryImpl
							.getBackendDelegate(RefDataBackendDelegate.class);
					approvalDataResult = refbackendDelegate.getApprovalAndRequestDetails(filter, request,
							serviceName, operationName);
				}

				LOG.debug("##[approvalDataResult " + approvalDataResult.toString());
				Dataset addfieldsDataSet = new Dataset();
				addfieldsDataSet = approvalDataResult.getDatasetById(dsName);
				List<Record> paymentDetails = addfieldsDataSet.getAllRecords();
				strScheduledDate = paymentDetails.get(0).getParamValueByName("scheduledDate");
				// schedule date ends

				scheduledDate = dateFormat.parse(strScheduledDate);
				LOG.debug("##[scheduledDate " + scheduledDate.toString());
				if (StringUtils.isNotBlank(strScheduledDate)) {
					scheduledDate = dateFormat.parse(strScheduledDate);
					LOG.info("[Sbg" + dsName + "BusinessDelegateImplExtn] scheduledDate: " + scheduledDate);
					day = dayFormat.format(scheduledDate);
					LOG.info("[Sbg" + dsName + "BusinessDelegateImplExtn] Full Day Name: " + day);
				} else {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SCHEDULED_DATE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				LOG.error("[Sbg" + dsName + "BusinessDelegateImplExtn]  Error: " + e.getMessage());
				return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SERVICESDOWN);
			}
			validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat,
					scheduledDate, day);
			/*
			 * Kumara Swamy: if Scheduled date is not falling under any holiday or non
			 * business day then we have to check the same validations for created date
			 */
			if (SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
				validateHolidaysResponse = null;
				createdDate = dateFormat.parse(dateFormat.format(new Date()));
				createdDay = dayFormat.format(createdDate);
				LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] Created Day Name: " + createdDay);
				validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat,
						createdDate, createdDay);
				/*
				 * Kumara Swamy: if created date is not falling under any holiday or non
				 * business day then we have to check theCutoff time
				 */
				if (SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
					cutOffTimeResponse = validateCutOffTime(responseObj, sbgValidationsResponseObj, dateFormat,
							createdDate, dayFormat, createdDay);
					LOG.debug("[cutOffTimeResponse] cutOffTimeResponse: " + cutOffTimeResponse.toString());
				} else {

					JSONObject getNextWorkingDayResponse = getNextWorkingDay(createdDate, responseObj,
							sbgValidationsResponseObj, dateFormat, dayFormat);
					if (getNextWorkingDayResponse.getInt("NextWorkingDay") <= 0) {
						return getNextWorkingDayResponse;
					} else {
						nextWorkingDayComesAfter = getNextWorkingDayResponse.getInt("NextWorkingDay");
					}

					createdDate = new Date(
							createdDate.getTime() + nextWorkingDayComesAfter * SBGConstants.MILLIS_IN_A_DAY);
					createdDay = dayFormat.format(createdDate);
					LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] workingDateAfterCreatedDate: "
							+ createdDate);
					LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] workingDayAfterCreatedDay: "
							+ createdDay);
					cutOffTimeResponse = validateCutOffTime(responseObj, sbgValidationsResponseObj, dateFormat,
							createdDate, dayFormat, createdDay);
					LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] cutOffTimeResponse: "
							+ cutOffTimeResponse);

				} // else transaction made after cutoff time

				if (cutOffTimeResponse.getInt("LeadDays") > 0) {
					int mvdStart = 1;
					// if transaction made within cutoff time
					if (SBGConstants.TRUE.equalsIgnoreCase(cutOffTimeResponse.optString(SBGConstants.IS_VALID))) {
						mvdStart = 1;
					} else if (SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE
							.equals(cutOffTimeResponse.optString(SBGConstants.MESSAGE))) { // if Cutoff time service
																							// got failed
						return cutOffTimeResponse;
					} else { // if the Cutoff time service is success but the transaction made after cuttoff
								// time
						JSONObject getNextWorkingDayResponse = getNextWorkingDay(createdDate, responseObj,
								sbgValidationsResponseObj, dateFormat, dayFormat);
						if (getNextWorkingDayResponse.getInt("NextWorkingDay") < 0) {
							return getNextWorkingDayResponse;
						} else {
							mvdStart = getNextWorkingDayResponse.getInt("NextWorkingDay") + 1;
						}
					}

					/*
					 * Kumara Swamy: Lead days based Minimum Value date (mvd) Validation
					 */

					for (int i = mvdStart; i <= 100; i++) { // 100days HardCoaded
						Date leadDayDate = new Date(createdDate.getTime() + i * SBGConstants.MILLIS_IN_A_DAY);
						String leadDay = dayFormat.format(leadDayDate);

						LOG.error(
								"[Sbg" + dsName + "BusinessDelegateImplExtn] leadDayDate:: " + leadDayDate);
						LOG.error("[Sbg" + dsName + "BusinessDelegateImplExtn] leadDay: " + leadDay);

						validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat,
								leadDayDate, leadDay);
						if (SBGConstants.TRUE
								.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
							if (leadCount >= cutOffTimeResponse.getInt("LeadDays")) {
								if (scheduledDate.compareTo(leadDayDate) >= 0) {
									return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
								} else {
									return setErrorResult(sbgValidationsResponseObj,
											SBGConstants.REFDATA_LEAD_DAYS_ERROR_MESSAGE.replace("%CURRENCY_CODE%",
													request.getParameter("transactionCurrency")));
								}
							}
							leadCount++;
						} else if (SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
								.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
								|| SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE
										.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
								|| SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
										.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))) {
							LOG.error(
									"[SbgInternationalFundTransferBusinessDelegateImplExtn] Holidays service failed while checking Lead days validation");
							return validateHolidaysResponse;
						}

					} // for
				} // leaddays>0
				else {
					if (compareDDMMYY(scheduledDate, createdDate)) {
						if (SBGConstants.TRUE.equalsIgnoreCase(cutOffTimeResponse.optString(SBGConstants.IS_VALID))) {
							return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
						} else {
							return cutOffTimeResponse;
						}
					} else {
						int mvdStart = 1;
						// if transaction made within cutoff time
						if (SBGConstants.TRUE.equalsIgnoreCase(cutOffTimeResponse.optString(SBGConstants.IS_VALID))) {
							mvdStart = 1;
						} else if (SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE
								.equals(cutOffTimeResponse.optString(SBGConstants.MESSAGE))) { // if Cutoff time service
																								// got failed
							return cutOffTimeResponse;
						} else { // if the Cutoff time service is success but the transaction made after cuttoff
									// time
							JSONObject getNextWorkingDayResponse = getNextWorkingDay(createdDate, responseObj,
									sbgValidationsResponseObj, dateFormat, dayFormat);
							if (getNextWorkingDayResponse.getInt("NextWorkingDay") < 0) {
								return getNextWorkingDayResponse;
							} else {
								mvdStart = getNextWorkingDayResponse.getInt("NextWorkingDay");
							}
						}

						/*
						 * Kumara Swamy: Lead days based Minimum Value date (mvd) Validation
						 */

						for (int i = mvdStart; i <= 100; i++) { // 100days HardCoaded
							Date leadDayDate = new Date(createdDate.getTime() + i * SBGConstants.MILLIS_IN_A_DAY);
							String leadDay = dayFormat.format(leadDayDate);

							LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] leadDayDate:: "
									+ leadDayDate);
							LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] leadDay: " + leadDay);

							validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj,
									dateFormat, leadDayDate, leadDay);
							if (SBGConstants.TRUE
									.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
								if (leadCount >= cutOffTimeResponse.getInt("LeadDays")) {
									if (scheduledDate.compareTo(leadDayDate) >= 0) {
										return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
									} else {
										return setErrorResult(sbgValidationsResponseObj,
												SBGConstants.REFDATA_LEAD_DAYS_ERROR_MESSAGE.replace("%CURRENCY_CODE%",
														request.getParameter("transactionCurrency")));
									}
								}
								leadCount++;
							} else if (SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
									.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
									|| SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE
											.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
									|| SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
											.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))) {
								LOG.debug(
										"[SbgInternationalFundTransferBusinessDelegateImplExtn] Holidays service failed while checking Lead days validation");
								return validateHolidaysResponse;
							}

						}
					}
				}

			} // SCheduled day validation
			else {
				return validateHolidaysResponse;
			}

			return validateHolidaysResponse;
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] sbgValidations(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}
	}// sbgValidations(-)

	/*
	 * Kumara Swamy : Method to Construct Header Parameters required for RefData
	 * Validations Orchestration service
	 */

	private String getRefData(DataControllerRequest request, String transactionid, String operationName, String dsName)
			throws AppRegistryException {
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + transactionid;
		RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate) DBPAPIAbstractFactoryImpl
				.getBackendDelegate(RefDataBackendDelegate.class);
		Result approvalDataResult = refbackendDelegate.getApprovalAndRequestDetails(filter, request,
				SbgURLConstants.SERVICE_SBGCRUD, operationName);
		Dataset addfieldsDataSet = new Dataset();
		addfieldsDataSet = approvalDataResult.getDatasetById(dsName);
		List<Record> paymentDetails = addfieldsDataSet.getAllRecords();
		String currency = paymentDetails.get(0).getParamValueByName("transactionCurrency");
		String fromAccountNumber = paymentDetails.get(0).getParamValueByName("fromAccountNumber");
		String country = "ZA";
		String BIC = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from Arrangement Extension
		LOG.debug("&&&&&BICFromExtension: " + BIC);
		if (StringUtils.isBlank(BIC)) {
			/*
			 * ConfigurableParametersHelper configurableParametersHelper =
			 * request.getServicesManager()
			 * .getConfigurableParametersHelper();
			 * Map<String, String> allClientProperties =
			 * configurableParametersHelper.getAllClientAppProperties();
			 * String arrangementCodeForServerProperty = "AE_" + fromAccountNumber +
			 * "_EXTENSIONDATA";
			 * String accountExtensionFromServerProperties =
			 * allClientProperties.get(arrangementCodeForServerProperty);
			 * JSONObject accountExtensionFromServerPropertiesJSON = new
			 * JSONObject(accountExtensionFromServerProperties);
			 * BIC = accountExtensionFromServerPropertiesJSON.getString("bic");
			 * LOG.debug("&&&&&BICFromServerProps: " + BIC);
			 */
			return SBGConstants.FALSE;
		}
		if (BIC != null) {
			country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
					(SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
		}

		if ("interbankfundtransfers".equalsIgnoreCase(dsName)) {
			return RefDataCacheHelperInterBank.getRefDataByKey(request, country,
					currency);
		} else {
			return RefDataCacheHelper.getRefDataByKey(request, country, currency);
		}

	}

	// private Map<String, Object> constructHeaderParams(DataControllerRequest
	// request, Map<String, Object> headerParams,String transactionid)
	// throws Exception {
	// //for accountNumber and transaction currency calling transfer details get
	// call
	// String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL +
	// transactionid;
	// LOG.debug("###constructHeaderParams==filter==" +filter);
	// RefDataBackendDelegate refbackendDelegate = (RefDataBackendDelegate)
	// DBPAPIAbstractFactoryImpl
	// .getBackendDelegate(RefDataBackendDelegate.class);
	// Result approvalDataResult =
	// refbackendDelegate.getApprovalAndRequestDetails(filter, request);
	// Dataset addfieldsDataSet = new Dataset();
	// addfieldsDataSet
	// =approvalDataResult.getDatasetById("internationalfundtransfers");
	// List<Record> paymentDetails= addfieldsDataSet.getAllRecords();
	// String currency =
	// paymentDetails.get(0).getParamValueByName("transactionCurrency");
	// // Get ServicesManager Object
	// ServicesManager servicesManager = request.getServicesManager();
	// // Get ConfigurableParametersHelper Object
	// ConfigurableParametersHelper configurableParametersHelper =
	// servicesManager.getConfigurableParametersHelper();
	// Map<String, String> allServerProperties =
	// configurableParametersHelper.getAllServerProperties();
	//
	// Map<String, String> allClientProperties =
	// configurableParametersHelper.getAllClientAppProperties();
	// String country = "ZA";
	// // Get Arrangements Extension - to be replaced with getList
	// try {
	// String fromAccountNumber
	// =paymentDetails.get(0).getParamValueByName("fromAccountNumber");
	// LOG.debug("[constructHeaderParams] fromAccountNumber: " + fromAccountNumber);
	// String arrangementCodeForServerProperty = "AE_" + fromAccountNumber +
	// "_EXTENSIONDATA";
	// LOG.debug("[constructHeaderParams] fromAccountNumber: " +
	// arrangementCodeForServerProperty);
	// String accountExtensionFromServerProperties =
	// allClientProperties.get(arrangementCodeForServerProperty);
	// JSONObject accountExtensionFromServerPropertiesJSON = new
	// JSONObject(accountExtensionFromServerProperties);
	// String BIC = accountExtensionFromServerPropertiesJSON.getString("bic");
	// if (BIC != null) {
	// country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
	// (SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
	// }
	// } catch (Exception e) {
	// LOG.error("&&&&&constructHeaderParamsBIC : " + e.getMessage());
	// }
	// // Get Arrangements Extension End
	// /*
	// * if (request.containsKeyInRequest("BIC") && request.getParameter("BIC") !=
	// * null && request.getParameter("BIC").length() >
	// * SBGConstants.BIC_COUNTRY_CODE_END_INDEX) { country =
	// * request.getParameter("BIC").substring(SBGConstants.
	// * BIC_COUNTRY_CODE_START_INDEX, SBGConstants.BIC_COUNTRY_CODE_END_INDEX); }
	// * else { country = "ZA"; }
	// */
	// // to get Property from Runtime
	// String sbgHeaderApiProductCode =
	// allServerProperties.get("SBG_HEADER_API_PRODUCT_CODE");
	// // Authorization token,clientID, clientSecret are fetched in SBGBseProcessor
	// // stored in Request object.
	// Result authorizationRresult =
	// SBGCommonUtils.cacheFetchPingToken("Authorization",request);
	// String authVal = authorizationRresult.getParamValueByName("Authorization");
	// String clientID =
	// SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID,
	// request);
	// String clientSecret =
	// SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET,
	// request);
	//
	// headerParams.put("Authorization", authVal);
	// headerParams.put("X-IBM-Client-Id", clientID);
	// headerParams.put("X-IBM-Client-Secret", clientSecret);
	// headerParams.put("Country", country); // Need to take from Swift Code 5th&6th
	// Character of From Account
	// headerParams.put("ProductCode", sbgHeaderApiProductCode);
	// headerParams.put("Currency", currency);
	//
	// LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn]
	// headerParams: " + headerParams.toString());
	// return headerParams;
	// }// constructHeaderParams(-)
	/*
	 * Kumara Swamy : method to Set Error mesage to a JSON Object and return
	 */
	private JSONObject setErrorResult(JSONObject sbgValidationsResponseObj, String errorMessage) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.FALSE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, errorMessage);
		LOG.debug("[errorMessage] setErrorResult(-) sbgValidationsResponseObj: "
				+ errorMessage);
		LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] setErrorResult(-) sbgValidationsResponseObj: "
				+ sbgValidationsResponseObj.toString());

		return sbgValidationsResponseObj;
	}// setErrorResult(-)
	/*
	 * Kumara Swamy : method to Validate Cut-Off time
	 */

	private JSONObject validateCutOffTime(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate, SimpleDateFormat dayFormat, String day) {
		LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn]  validateCutOffTime(-) " + responseObj);
		boolean isCutOffTime = false;
		String strStartTime = null;
		String strEndTime = null;
		String timeZone = null;
		int leadDays = 0;
		JSONObject cutOffTimeResponse = new JSONObject();
		try {
			if (responseObj != null && responseObj.has("Days")) {
				LOG.debug("##[cutOffTimeResponse]in if loop" + responseObj.has("Days"));
				JSONArray daysArray = responseObj.getJSONArray("Days");
				if (daysArray != null) {
					for (Object daysObj : daysArray) {
						JSONObject jsonDaysObj = (JSONObject) daysObj;

						if (jsonDaysObj != null && jsonDaysObj.has("Day")
								&& StringUtils.isNotBlank(jsonDaysObj.getString("Day"))) {
							if (jsonDaysObj.getString("Day").equalsIgnoreCase(day)) {
								if (jsonDaysObj.has("LeadDays")) {
									leadDays = jsonDaysObj.getInt("LeadDays");
								} else {
									leadDays = 0;
								}

								if (jsonDaysObj.has("TimeRange")) {
									JSONObject jsonTimeRangeObj = jsonDaysObj.getJSONObject("TimeRange");

									if (jsonTimeRangeObj != null && jsonTimeRangeObj.has("StartTime")
											&& jsonTimeRangeObj.has("EndTime")) {
										strStartTime = jsonTimeRangeObj.getString("StartTime");
										strEndTime = jsonTimeRangeObj.getString("EndTime");

										if (jsonDaysObj.has("TimeZone")) {
											timeZone = jsonDaysObj.getString("TimeZone");
										}

									} else {
										cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
												SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
										cutOffTimeResponse.put("LeadDays", leadDays);
										return cutOffTimeResponse;
									}
								} else {
									cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
											SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
									cutOffTimeResponse.put("LeadDays", leadDays);
									return cutOffTimeResponse;
								}
								if (StringUtils.isNotBlank(strStartTime) && StringUtils.isNotBlank(strEndTime)) {
									LocalTime startTime = LocalTime.parse(strStartTime);
									LocalTime endTime = LocalTime.parse(strEndTime);
									/*
									 * Kumara Swamy : Method call to Find out the Payment Scheduled time is under
									 * CutOff time or not..
									 */
									isCutOffTime = isCutOffTime(startTime, endTime, timeZone);
									LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] Cut- Off Time:::"
											+ isCutOffTime);

									if (isCutOffTime) {

										cutOffTimeResponse = setSuccessResult(sbgValidationsResponseObj,
												SBGConstants.SUCCESS);
										cutOffTimeResponse.put("LeadDays", leadDays);
										return cutOffTimeResponse;

									} else {
										cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
												SBGConstants.REFDATA_CUTOFF_TIME_ERROR_MESSAGE);
										cutOffTimeResponse.put("LeadDays", leadDays);
										return cutOffTimeResponse;
									}
								} else {
									cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
											SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
									cutOffTimeResponse.put("LeadDays", leadDays);
									return cutOffTimeResponse;
								}
							}
						} else {
							cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
									SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
							cutOffTimeResponse.put("LeadDays", leadDays);
							return cutOffTimeResponse;
						}
					} // for loop

				} else {
					cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
					cutOffTimeResponse.put("LeadDays", leadDays);
					return cutOffTimeResponse;
				}

			} else {
				cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
				cutOffTimeResponse.put("LeadDays", leadDays);
				return cutOffTimeResponse;

			}
		} catch (Exception e) {
			LOG.debug("[validateCutOffTime] validateCutOffTime(-) Error: "
					+ e.getMessage());
			cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
			cutOffTimeResponse.put("LeadDays", leadDays);
			return cutOffTimeResponse;
		}
		cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
				SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
		cutOffTimeResponse.put("LeadDays", leadDays);
		return cutOffTimeResponse;
	}// validateCutOffTime(-)

	private boolean compareDDMMYY(Date scheduledDate, Date createdDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		int formattedCreatedDate = createdDate.getDate();
		int formattedCreatedMonth = createdDate.getMonth();
		int formattedCreatedYear = createdDate.getYear();

		int formattedScheduledDate = scheduledDate.getDate();
		int formattedScheduledMonth = scheduledDate.getMonth();
		int formattedScheduledYear = scheduledDate.getYear();

		if ((formattedCreatedDate == formattedScheduledDate) && (formattedCreatedMonth == formattedScheduledMonth)
				&& (formattedCreatedYear == formattedScheduledYear)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Kumara Swamy : method to Set Success mesage to a JSON Object and return
	 */
	private JSONObject setSuccessResult(JSONObject sbgValidationsResponseObj, String message) {
		LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn]  All Ref Data Validations are Success");
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.TRUE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, message);
		return sbgValidationsResponseObj;
	}// setErrorResult()
	/*
	 * Kumara Swamy : method to check the payment scheduled time is between CutOff
	 * time or not
	 */

	private boolean isCutOffTime(LocalTime startTime, LocalTime endTime, String ZoneID) {

		ZoneId zone = ZoneId.of(ZoneID);
		LocalDateTime zoneDateTime = LocalDateTime.now(zone);
		LocalDateTime paymentstarttime = zoneDateTime.withHour(startTime.getHour()).withMinute(startTime.getMinute())
				.withSecond(0).withNano(0);
		LocalDateTime paymentendtime = zoneDateTime.withHour(endTime.getHour()).withMinute(endTime.getMinute())
				.withSecond(0).withNano(0);

		return (!zoneDateTime.isBefore(paymentstarttime)) && (!zoneDateTime.isAfter(paymentendtime)); // Inclusive.

	}// isCutOffTime(-)

	/*
	 * Kumara Swamy : Method to convert the transaction Scheduled Date to required
	 * Time zone.
	 */

	private JSONObject validateHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate, String day) {
		JSONObject publicHolidaysResponse = null;
		JSONObject nonBusinessDaysResponse = null;
		JSONObject currencyHolidaysResponse = null;
		try {

			publicHolidaysResponse = validatePublicHolidays(responseObj, sbgValidationsResponseObj, dateFormat,
					scheduledDate);
			if (SBGConstants.TRUE.equalsIgnoreCase(publicHolidaysResponse.optString(SBGConstants.IS_VALID))) {
				nonBusinessDaysResponse = validateNonBusinessDays(responseObj, sbgValidationsResponseObj, dateFormat,
						day);
				if (SBGConstants.TRUE.equalsIgnoreCase(nonBusinessDaysResponse.optString(SBGConstants.IS_VALID))) {
					currencyHolidaysResponse = validateCurrencyHolidays(responseObj, sbgValidationsResponseObj,
							dateFormat, scheduledDate);
					if (SBGConstants.TRUE.equalsIgnoreCase(currencyHolidaysResponse.optString(SBGConstants.IS_VALID))) {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					} else {
						return currencyHolidaysResponse;
					}

				} else {
					return nonBusinessDaysResponse;
				}
			} else {
				return publicHolidaysResponse;
			}

		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] sbgValidations(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}

	}// validateHolidays(-)

	/*
	 * Kumara Swamy : Method to Validate publicHolidays
	 */
	private JSONObject validatePublicHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate) {
		String strPublicHolidayDate = null;
		Date publicHolidayDate = null;
		List<Date> publicHolidayDateList = new ArrayList<Date>();
		try {

			if (responseObj != null && responseObj.has("publicHolidaysList")) {
				JSONArray publicHolidaysList = responseObj.getJSONArray("publicHolidaysList");
				if (publicHolidaysList != null) {
					for (Object publicHolidaysObj : publicHolidaysList) {
						JSONObject phJsonObj = (JSONObject) publicHolidaysObj;
						if (phJsonObj != null) {
							if (phJsonObj.has("HolidayDate")) {
								strPublicHolidayDate = phJsonObj.getString("HolidayDate");
								publicHolidayDate = dateFormat.parse(strPublicHolidayDate);
								publicHolidayDateList.add(publicHolidayDate);
							} else {
								return setErrorResult(sbgValidationsResponseObj,
										SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
							}
						} else {
							return setErrorResult(sbgValidationsResponseObj,
									SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
						}
					} // for loop
					if (publicHolidayDateList.contains(scheduledDate)) {
						return setErrorResult(sbgValidationsResponseObj,
								SBGConstants.REFDATA_PUBLIC_HOLIDAY_ERROR_MESSAGE);
					} else {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					}

				} else {
					return setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
				}

			} else {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] validatePublicHolidays(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
		}

	}// validatePublicHolidays(-)

	/*
	 * Kumara Swamy : method to getNextWorkingDay from the Given Date
	 */
	private JSONObject getNextWorkingDay(Date CheckDate, JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, SimpleDateFormat dayFormat) {
		JSONObject validateHolidaysResponse = new JSONObject();
		try {
			int nextWorkingDay = 0;
			for (int i = 1; i <= 100; i++) {
				Date leadDayDate = new Date(CheckDate.getTime() + i * SBGConstants.MILLIS_IN_A_DAY);
				String leadDay = dayFormat.format(leadDayDate);
				validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat,
						leadDayDate, leadDay);
				if (SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
					return validateHolidaysResponse.put("NextWorkingDay", nextWorkingDay + i);

				} else if (SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
						.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
						|| SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE
								.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))
						|| SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE
								.equals(validateHolidaysResponse.optString(SBGConstants.MESSAGE))) {
					validateHolidaysResponse.put("NextWorkingDay", -1);
					return validateHolidaysResponse;
				}

			} // for
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] getNextWorkingDay(-) Error: "
					+ e.getMessage());
			return setErrorResult(validateHolidaysResponse, e.getMessage());
		}
		return validateHolidaysResponse;
	}// getNextWorkingDay(-)

	/*
	 * Kumara Swamy : Code Block to Validate Business Days Or Weekly Off
	 */

	private JSONObject validateNonBusinessDays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, String day) {
		String workingDayOrWeekOff = null;
		try {
			if (responseObj != null && responseObj.has(day)) {
				workingDayOrWeekOff = responseObj.getString(day);
				if (StringUtils.isNotBlank(workingDayOrWeekOff)) {
					LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] workingDayOrWeekOff: "
							+ workingDayOrWeekOff);
					if (SBGConstants.REFDATA_WEEK_OFF.equalsIgnoreCase(workingDayOrWeekOff)) {
						return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_WEEKEND_ERROR_MESSAGE);
					} else {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					}
				} else {
					return setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE);
				}
			} else {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] validateNonBusinessDays(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE);
		}
	}// validateNonBusinessDays(-)
	/*
	 * Kumara Swamy : method to Validate currencyHolidays
	 */

	private JSONObject validateCurrencyHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate) {
		String strCurrencyHolidayDate = null;
		Date currencyHolidayDate = null;
		List<Date> currencyHolidayDateList = new ArrayList<Date>();
		try {
			if (responseObj != null && responseObj.has("currencyHolidaysList")) {
				JSONArray currencyHolidaysList = responseObj.getJSONArray("currencyHolidaysList");
				if (currencyHolidaysList != null) {
					for (Object currencyHolidaysObj : currencyHolidaysList) {
						JSONObject chJsonObj = (JSONObject) currencyHolidaysObj;
						if (chJsonObj != null) {
							if (chJsonObj.has("HolidayDate")) {
								strCurrencyHolidayDate = chJsonObj.getString("HolidayDate");
								currencyHolidayDate = dateFormat.parse(strCurrencyHolidayDate);
								currencyHolidayDateList.add(currencyHolidayDate);
							} else {
								return setErrorResult(sbgValidationsResponseObj,
										SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
							}
						} else {
							return setErrorResult(sbgValidationsResponseObj,
									SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
						}
					} // for loop

					if (currencyHolidayDateList.contains(scheduledDate)) {
						return setErrorResult(sbgValidationsResponseObj,
								SBGConstants.REFDATA_CURRENCY_HOLIDAY_ERROR_MESSAGE);
					} else {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					}
				} else {
					return setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
				}
			} else {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] validateCurrencyHolidays(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
		}

	}// validateCurrencyHolidays(-)

	private Map<String, String> getForeignExchangeData(String foreignExchangeJson) {
		LOG.debug("#### getForeignExchangeData input:"+foreignExchangeJson);
		Map<String, String> foreignExchangeDataMap = new HashMap<>();
		try {
			JSONArray jsonArray = new JSONArray(foreignExchangeJson);
			String foreignExchangeRate = "";
			String foreignExchangeDeal = "";
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (StringUtils.isNotBlank(foreignExchangeRate) && jsonObject != null && StringUtils.isNotBlank(jsonObject.get("exchangeRate").toString())) {
					foreignExchangeRate = foreignExchangeRate + "," + jsonObject.get("exchangeRate").toString();
				} else {
					foreignExchangeRate = jsonObject.get("exchangeRate").toString();
				}

				if (StringUtils.isNotBlank(foreignExchangeDeal) && jsonObject != null && StringUtils.isNotBlank(jsonObject.get("coverNumber").toString())) {
					foreignExchangeDeal = foreignExchangeDeal + "," + jsonObject.get("coverNumber").toString();
				} else {
					foreignExchangeDeal = jsonObject.get("coverNumber").toString();
				}
			}

			foreignExchangeDataMap.put("fxDealNo", foreignExchangeDeal);
			foreignExchangeDataMap.put("exgrate", foreignExchangeRate);
		} catch (Exception ex) {
			LOG.debug("#### getForeignExchangeData failed :",ex);
		}
		return foreignExchangeDataMap;
	}

}
