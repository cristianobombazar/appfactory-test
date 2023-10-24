package com.kony.sbg.resources.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.business.api.SBGTransactionLimitsBusinessDelegate;
import com.kony.sbg.resources.api.TransactionResourceLimits;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class TransactionResourceLimitsImpl implements TransactionResourceLimits {

	private static final Logger LOG = Logger.getLogger(TransactionResourceLimitsImpl.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	SBGTransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(SBGTransactionLimitsBusinessDelegate.class);
	AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(AccountBusinessDelegate.class);

	@Override
	public Result validateLimits(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();		
		Double amount = null;
		String contractId = "", coreCustomerId = "", companyId = "", createdby = "";
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = inputParams.get("featureActionId") != null ? inputParams.get("featureActionId").toString(): null;
		String amountValue = inputParams.get("amount").toString();
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		String fromAccountCurrency = inputParams.get("fromAccountCurrency").toString();
		String scheduledDate = inputParams.get("scheduledDate").toString();
		try {
			CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
			contractId = account.getContractId();
			coreCustomerId = account.getCoreCustomerId();
			companyId = account.getOrganizationId();
		} catch (Exception exp) {
			LOG.error("Failed to fetch customer account details : " + exp.getMessage());
			result.addParam("limitsStatus", "false");
			return SbgErrorCodeEnum.ERR_100019.setErrorCode(result);
		}
		String baseCurrency = application.getBaseCurrencyFromCache();
		String transactionCurrency = inputParams.get("transactionCurrency") != null
				? inputParams.get("transactionCurrency").toString()
				: baseCurrency;
		String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString()
				: null;
		if (SBGCommonUtils.isStringEmpty(amountValue)) {
			result.addParam("limitsStatus", "false");
			return ErrorCodeEnum.ERR_12031.setErrorCode(result);
		}

		if(StringUtils.isEmpty(featureActionId)){
			featureActionId = FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE;
		}
		
		try {
			amount = Double.parseDouble(amountValue);
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			result.addParam("limitsStatus", "false");
			return ErrorCodeEnum.ERR_10624.setErrorCode(result);
		}
		inputParams.put("featureActionId", featureActionId);
		inputParams.put("companyId", companyId);
		inputParams.put("createdby", createdby);
		String date = scheduledDate == null ? application.getServerTimeStamp() : scheduledDate;
		TransactionStatusDTO transactionStatusDTO;
		try {
			transactionStatusDTO = limitsDelegate.validateForLimitsSBG(createdby, companyId, fromAccountNumber,
					featureActionId, amount, TransactionStatusEnum.NEW, date, transactionCurrency, serviceCharge,
					request);
			if (transactionStatusDTO == null) {
				result.addParam("limitsStatus", "false");
				return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
			}
			if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				result.addParam("limitsStatus", "false");
				return result;
			}
		} catch (Exception exp) {
			LOG.error("Error occured while in validateForLimitsSBG: " + exp.getMessage());
			result.addParam("limitsStatus", "false");
			return SbgErrorCodeEnum.ERR_100018.setErrorCode(result);
		}
		result.addParam("limitsStatus", "success");
		LOG.debug("Converted amount using indicative rates: " + transactionStatusDTO.getAmount());
		LOG.debug("TransactionResourceLimitsImpl::ValidateFXLimits::Result:: " + ResultToJSON.convert(result));
		return result;
	}

}
