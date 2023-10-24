package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.constants.DBPConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.PaymentFeedbackBackendDelegate;
import com.kony.sbg.backend.api.RFQBackendDelegate;
import com.kony.sbg.business.api.PaymentFeedbackBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InterBankFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InternationalFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;

public class PaymentFeedbackBusinessDelegateImpl implements PaymentFeedbackBusinessDelegate {

	private static final Logger LOG = Logger.getLogger(PaymentFeedbackBusinessDelegateImpl.class);
	InternationalFundTransferBusinessDelegate internationalFundTransferDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(InternationalFundTransferBusinessDelegate.class);
	PaymentFeedbackBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(PaymentFeedbackBackendDelegate.class);

	InterBankFundTransferBusinessDelegate interbankFundTransferDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);

	@Override
	public Result processPaymentFeedback(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws ApplicationException {
		LOG.debug("Entry --> PaymentFeedbackBusinessDelegateImpl::processPaymentFeedback");
		Result result = new Result();
		String filter = "";
		HashMap<String, Object> requestParameters = new HashMap<>();
		HashMap<String, Object> requestHeaders = new HashMap<>();

		HashMap<String, Object> fundRequestParameters = new HashMap<>();
		HashMap<String, Object> fundRequestHeaders = new HashMap<>();

		filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + requestPayload.getString("orgnlPmtInfId");
		fundRequestParameters.put(SbgURLConstants.FILTER, filter);
		JSONObject internationalFundTransfers = backendDelegate.getInternationalFundTransfers(fundRequestParameters,
				fundRequestHeaders);

		if (internationalFundTransfers != null && internationalFundTransfers.has("internationalfundtransfers")
				&& internationalFundTransfers.getJSONArray("internationalfundtransfers").length() > 0) {
			InternationalFundTransferDTO updateStatus = internationalFundTransferDelegate
					.updateStatus(requestPayload.getString("orgnlPmtInfId"), requestPayload.getString("txSts"), null);
			LOG.info("processPaymentFeedback::updateStatus::ConfirmationNumber: "
					+ updateStatus.getConfirmationNumber());
			result.addParam("status", "success");
			result.addParam("featureActionId", FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);

			requestParameters.put("transactionId", requestPayload.getString("orgnlPmtInfId"));
			requestParameters.put("transferStatus", requestPayload.getString("txSts"));
			requestParameters.put("encodedData", requestPayload.getString("encodedData"));
			requestParameters.put("reasonCode", requestPayload.getString("reasonCode"));

			JSONObject historyResponse = backendDelegate.maintainTranferStatusHistory(requestParameters,
					requestHeaders);
			LOG.debug("processPaymentFeedback::maintainTranferStatusHistory::Response: " + historyResponse);
		} else {
			JSONObject interbankFundTransfers = backendDelegate.getInterBankFundTransfers(fundRequestParameters,
					fundRequestHeaders);

			if (interbankFundTransfers != null && interbankFundTransfers.has("interbankfundtransfers")
					&& interbankFundTransfers.getJSONArray("interbankfundtransfers").length() > 0) {
				InterBankFundTransferDTO updateStatus = interbankFundTransferDelegate.updateStatus(
						requestPayload.getString("orgnlPmtInfId"), requestPayload.getString("txSts"), null);
				LOG.info("processPaymentFeedback::updateStatus::ConfirmationNumber: "
						+ updateStatus.getConfirmationNumber());
				result.addParam("status", "success");
				result.addParam("featureActionId", FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);

				requestParameters.put("transactionId", requestPayload.getString("orgnlPmtInfId"));
				requestParameters.put("transferStatus", requestPayload.getString("txSts"));
				requestParameters.put("encodedData", requestPayload.getString("encodedData"));
				requestParameters.put("reasonCode", requestPayload.getString("reasonCode"));

				JSONObject historyResponse = backendDelegate.maintainDomesticTranferStatusHistory(requestParameters,
						requestHeaders);
				LOG.debug("processPaymentFeedback::maintainDomesticTranferStatusHistory::Response: " + historyResponse);
			} else {
				JSONObject ownAccountFundTransfers = backendDelegate.getOwnAccountFundTransfers(fundRequestParameters,
						fundRequestHeaders);

				if (ownAccountFundTransfers != null && ownAccountFundTransfers.has("ownaccounttransfers")
						&& ownAccountFundTransfers.getJSONArray("ownaccounttransfers").length() > 0) {
					InterBankFundTransferDTO updateStatus = interbankFundTransferDelegate.updateStatus(
							requestPayload.getString("orgnlPmtInfId"), requestPayload.getString("txSts"), null);
					LOG.info("processPaymentFeedback::updateStatus::ConfirmationNumber: "
							+ updateStatus.getConfirmationNumber());
					result.addParam("status", "success");
					result.addParam("featureActionId", FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);

					requestParameters.put("transactionId", requestPayload.getString("orgnlPmtInfId"));
					requestParameters.put("transferStatus", requestPayload.getString("txSts"));
					requestParameters.put("encodedData", requestPayload.getString("encodedData"));
					requestParameters.put("reasonCode", requestPayload.getString("reasonCode"));

					JSONObject historyResponse = backendDelegate.maintainIATStatusHistory(requestParameters,
							requestHeaders);
					LOG.debug("processPaymentFeedback::maintainDomesticTranferStatusHistory::Response: "
							+ historyResponse);
				} else {
					LOG.error("processPaymentFeedback::getInternationalFundTransfers::Confirmation number not found");
					result = SbgErrorCodeEnum.ERR_100032.setErrorCode(result);
					result.addOpstatusParam(SbgErrorCodeEnum.ERR_100032.getErrorCode());
					result.addParam("status", "failed");
					result.addParam("errMsg", result.getParamValueByName(DBPConstants.DBP_ERROR_MESSAGE_KEY));
				}
			}
		}

		return result;
	}

	@Override
	public Result fetchMyAccessGetUser(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws ApplicationException {
		LOG.debug("fetchMyAccessGetUser::::");
		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			result = backendDelegate.getMyAccessAllUsers(svcInputParams, svcHeaders, dcRequest);
			LOG.debug("fetchMyAccessGetUser======result" + result);
		} catch (Exception exp) {
			LOG.debug("Error in fetchMyAccessGetUser: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result updateMyAccessUser(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception {
		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			String userId = inputParams.get("userId");
			String enabled = inputParams.get("enabled");
			svcInputParams.put("userId", userId);
			svcInputParams.put("enabled", enabled);
			LOG.debug("##updateMyAccessUser:svcInputParams " + svcInputParams);
			result = backendDelegate.updateMyAccessUser(svcInputParams, svcHeaders, dcRequest);
			LOG.debug("updateMyAccessUser:::::result" + result);
		} catch (Exception exp) {
			LOG.debug("updateMyAccessUser: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
		}

		return result;
	}

}
