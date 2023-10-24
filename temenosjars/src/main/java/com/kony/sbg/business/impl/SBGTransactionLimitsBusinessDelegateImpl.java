package com.kony.sbg.business.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.backend.api.SBGTransactionLimitsBackendDelegate;
import com.kony.sbg.business.api.SBGTransactionLimitsBusinessDelegate;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureActionBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.LimitGroupBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.UserRoleBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.LimitsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commons.dto.UserLimitsDTO;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class SBGTransactionLimitsBusinessDelegateImpl implements SBGTransactionLimitsBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(SBGTransactionLimitsBusinessDelegateImpl.class);

	/*
	 * As base validateForLimits is tightly coupled with approval matrix validation,
	 * which is not needed here as CQL-6126 only talks about limits check, written
	 * new class & function with only limits check logic (copied from base).
	 */
	public TransactionStatusDTO validateForLimitsSBG(String userId, String companyId, String accountId,
			String featureActionID, Double amount, TransactionStatusEnum transactionStatus, String date,
			String transactionCurrency, String serviceCharge, DataControllerRequest request) {
		TransactionStatusDTO result = new TransactionStatusDTO();

		CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(CustomerBusinessDelegate.class);
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ContractBusinessDelegate.class);
		UserRoleBusinessDelegate userRoleBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(UserRoleBusinessDelegate.class);
		AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(AccountBusinessDelegate.class);
		ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApplicationBusinessDelegate.class);
		LimitGroupBusinessDelegate limitGroupBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(LimitGroupBusinessDelegate.class);
		FeatureActionBusinessDelegate featureActionDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(FeatureActionBusinessDelegate.class);
		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(userId, accountId);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String baseCurrency = application.getBaseCurrencyFromCache();
		if (StringUtils.isEmpty(transactionCurrency))
			transactionCurrency = baseCurrency;
		if (StringUtils.isEmpty(serviceCharge))
			serviceCharge = "0.0";

		// we need to fetch the service charge and the converted amount using a validate
		// call and populate here
		Double charges = 0.0;
		Double totalAmount = 0.0;
		Double convertedAmount = 0.0;

		result.setTransactionAmount(amount + "");
		result.setServiceCharge(serviceCharge);

		if (!transactionCurrency.equalsIgnoreCase(baseCurrency)) {
			try {
				convertedAmount = getConvertedAmount(amount, transactionCurrency, baseCurrency, request);
				
				if (convertedAmount == null) {
					LOG.error("validateForLimitsSBG::Converted amount is null");
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100053.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100053.getMessage());
					return result;
				}
				
				convertedAmount = (double) Math.round(convertedAmount * 100.0) / 100.0;
				totalAmount = convertedAmount + charges;
				LOG.info("Total amount after conversion: " + totalAmount);

			} catch (Exception e) {
				LOG.error("Exception in SBGTransactionLimitsBusinessDelegateImpl::validateForLimitsSBG:: "
						+ e.getMessage());
				result.setDbpErrCode(ErrorCodeEnum.ERR_27016.getErrorCodeAsString());
				result.setDbpErrMsg(ErrorCodeEnum.ERR_27016.getMessage());
				return result;
			}
		} else {
			totalAmount = amount + charges;
		}

		result.setAmount(totalAmount);
		amount = totalAmount;
		// Validating contract-coreCustomer level limits for users
		if (StringUtils.isNotEmpty(contractId) && StringUtils.isNotEmpty(coreCustomerId)) {
			LimitsDTO contractCutsomerLimitsDTO = contractDelegate.fetchLimits(contractId, coreCustomerId,
					featureActionID);
			LimitsDTO exhaustedDTO = contractDelegate.fetchExhaustedLimits(contractId, coreCustomerId, featureActionID,
					date);
			
			if (contractCutsomerLimitsDTO == null || exhaustedDTO == null) 
			{
				LOG.error("Failed to fetch organization limits 1");
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100041.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100041.getMessage());
				return result;
			}
			
			if(exhaustedDTO.getDbpErrCode() != null || exhaustedDTO.getDbpErrMsg() != null) {
				LOG.error("Failed to fetch organization limits 2");
				result.setDbpErrCode(exhaustedDTO.getDbpErrCode());
				result.setDbpErrMsg(exhaustedDTO.getDbpErrMsg());
				return result;
			}

			Double newDailyValue = amount + exhaustedDTO.getDailyLimit();
			Double newWeeklyValue = amount + exhaustedDTO.getWeeklyLimit();

			Double perTrLimit = contractCutsomerLimitsDTO.getMaxTransactionLimit();
			Double dailyLimit = contractCutsomerLimitsDTO.getDailyLimit();
			Double weeklyLimit = contractCutsomerLimitsDTO.getWeeklyLimit();

			if(Double.compare(amount, perTrLimit) > 0) {
				LOG.error("Denied max transaction at contract level");
				result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100038.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100038.getMessage());
				return result;
			}
			if (Double.compare(newDailyValue, dailyLimit) > 0) {
				LOG.error("Denied daily at contract level");
				result.setStatus(TransactionStatusEnum.DENIED_DAILY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100040.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100040.getMessage());
				return result;
			}
			if (Double.compare(newWeeklyValue, weeklyLimit) > 0) {
				LOG.error("Denied weekly at contract level");
				result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100039.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100039.getMessage());
				return result;
			}

			// Validating role level limits for business users
			LOG.error("###Validating role level limits");
			String userRole = customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, userId);
			if (userRole == null) {
				LOG.error("Error while fetching user Role");
				String dbpErrCode = SbgErrorCodeEnum.ERR_100045.getErrorCodeAsString();
				String dbpErrMsg = SbgErrorCodeEnum.ERR_100045.getMessage();
				result.setDbpErrCode(dbpErrCode);
				result.setDbpErrMsg(dbpErrMsg);
				return result;
			}

			LimitsDTO roleLimitsDTO = userRoleBusinessDelegate.fetchLimits(userRole, featureActionID);
			exhaustedDTO = userRoleBusinessDelegate.fetchExhaustedLimits(contractId, coreCustomerId, userRole,
					featureActionID, date, userId);

			if (roleLimitsDTO == null || exhaustedDTO == null) {
				LOG.error("Error while fetching role limits 1");
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100045.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100045.getMessage());
				return result;
			}
			if(exhaustedDTO.getDbpErrCode() != null || exhaustedDTO.getDbpErrMsg() != null) {
				LOG.error("Error while fetching role limits 2");
				result.setDbpErrCode(exhaustedDTO.getDbpErrCode());
				result.setDbpErrMsg(exhaustedDTO.getDbpErrMsg());
				return result;
			}

			newDailyValue = amount + exhaustedDTO.getDailyLimit();
			newWeeklyValue = amount + exhaustedDTO.getWeeklyLimit();

			perTrLimit = roleLimitsDTO.getMaxTransactionLimit();
			dailyLimit = roleLimitsDTO.getDailyLimit();
			weeklyLimit = roleLimitsDTO.getWeeklyLimit();
			
			if (Double.compare(amount, perTrLimit) > 0) {
				LOG.error("Denied max transaction at role level");
				result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100042.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100042.getMessage());
				return result;
			}
			if (Double.compare(newDailyValue, dailyLimit) > 0) {
				LOG.error("Denied daily at role level");
				result.setStatus(TransactionStatusEnum.DENIED_DAILY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100044.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100044.getMessage());
				return result;
			}
			if (Double.compare(newWeeklyValue, weeklyLimit) > 0) {
				LOG.error("Denied weekly at role level");
				result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100043.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100043.getMessage());
				return result;
			}
		}

		// Validating Limit Group limits for both retail and business users
		LOG.error("###Validating limit group");
		String limitGroupId = featureActionDelegate.getLimitGroupId(featureActionID);
		if (limitGroupId == null) {
			LOG.error("Error while fetching the limit group id");
			String dbpErrCode = SbgErrorCodeEnum.ERR_100049.getErrorCodeAsString();
			String dbpErrMsg = SbgErrorCodeEnum.ERR_100049.getMessage();
			result.setDbpErrCode(dbpErrCode);
			result.setDbpErrMsg(dbpErrMsg);
			return result;
		}

		LimitsDTO groupLimitsDTO = limitGroupBusinessDelegate.fetchLimits(userId, contractId, coreCustomerId,
				limitGroupId);
		LimitsDTO exhaustedgroupLimitsDTO = limitGroupBusinessDelegate.fetchExhaustedLimits(contractId, coreCustomerId,
				userId, limitGroupId, date);
		
		if (groupLimitsDTO == null || exhaustedgroupLimitsDTO == null) {
			LOG.error("Error while fetching the limits for limit groups 1");
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100049.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100049.getMessage());
			return result;
		}
		if(exhaustedgroupLimitsDTO.getDbpErrCode() != null || exhaustedgroupLimitsDTO.getDbpErrMsg() != null) {
			LOG.error("Error while fetching the limits for limit groups 2");
			result.setDbpErrCode(exhaustedgroupLimitsDTO.getDbpErrCode());
			result.setDbpErrMsg(exhaustedgroupLimitsDTO.getDbpErrMsg());
			return result;
		}

		Double newLimitGroupDailyValue = amount + exhaustedgroupLimitsDTO.getDailyLimit();
		Double newLimitGroupWeeklyValue = amount + exhaustedgroupLimitsDTO.getWeeklyLimit();

		Double limitgroupPerTrLimit = groupLimitsDTO.getMaxTransactionLimit();
		Double limitgroupDailyLimit = groupLimitsDTO.getDailyLimit();
		Double limitgroupWeeklyLimit = groupLimitsDTO.getWeeklyLimit();
		
		if (Double.compare(amount, limitgroupPerTrLimit) > 0) {
			LOG.error("Denied max transaction at group limits level");
			result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100046.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100046.getMessage());
			return result;
		}
		if (Double.compare(newLimitGroupDailyValue, limitgroupDailyLimit) > 0) {
			LOG.error("Denied daily at group limits level");
			result.setStatus(TransactionStatusEnum.DENIED_DAILY);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100048.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100048.getMessage());
			return result;
		}
		if (Double.compare(newLimitGroupWeeklyValue, limitgroupWeeklyLimit) > 0) {
			LOG.error("Denied weekly at group limits level");
			result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100047.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100047.getMessage());
			return result;
		}

		// check for Auto denial limits of a user
		UserLimitsDTO userLimitsDTO = customerDelegate.fetchCustomerLimits(userId, featureActionID, accountId);
		LimitsDTO exhaustedDTO = customerDelegate.fetchExhaustedLimits(userId, featureActionID, date, accountId);

		if (userLimitsDTO == null || exhaustedDTO == null) {
			LOG.error("Error while fetching user limits 1");
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100041.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100041.getMessage());
			return result;
		}
		if(exhaustedDTO.getDbpErrCode() != null || exhaustedDTO.getDbpErrMsg() != null) {
			LOG.error("Error while fetching user limits 2");
			result.setDbpErrCode(exhaustedDTO.getDbpErrCode());
			result.setDbpErrMsg(exhaustedDTO.getDbpErrMsg());
			return result;
		}

		Double newDailyValue = amount + exhaustedDTO.getDailyLimit();
		Double newWeeklyValue = amount + exhaustedDTO.getWeeklyLimit();

		Double autoDenialPerTrLimit = userLimitsDTO.getAutoDeniedTransactionLimit();
		Double autoDenialDailyLimit = userLimitsDTO.getAutoDeniedDailyLimit();
		Double autoDenialWeeklyLimit = userLimitsDTO.getAutoDeniedWeeklyLimit();

		if (Double.compare(amount, autoDenialPerTrLimit) > 0) {
			result.setStatus(TransactionStatusEnum.DENIED_AD_MAX_TRANSACTION);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100050.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100050.getMessage());
			return result;
		}
		if (Double.compare(newDailyValue, autoDenialDailyLimit) > 0) {
			result.setStatus(TransactionStatusEnum.DENIED_AD_DAILY);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100052.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100052.getMessage());
			return result;
		}
		if (Double.compare(newWeeklyValue, autoDenialWeeklyLimit) > 0) {
			result.setStatus(TransactionStatusEnum.DENIED_AD_WEEKLY);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100051.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100051.getMessage());
			return result;
		}

		return result;
	}

	protected Double getConvertedAmount(Double amount, String transactionCurrency, String baseCurrency,
			DataControllerRequest request) {
		try {
			SBGTransactionLimitsBackendDelegate sbgTransactionLimitsBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(SBGTransactionLimitsBackendDelegate.class);
			return sbgTransactionLimitsBackendDelegate.fetchconvertedAmount(transactionCurrency, amount.toString(),
					request);
		} catch (Exception e) {
			LOG.error("Exception in SBGTransactionLimitsBusinessDelegateImpl::getConvertedAmount::" + e.getMessage());
			return null;
		}
	}
}
