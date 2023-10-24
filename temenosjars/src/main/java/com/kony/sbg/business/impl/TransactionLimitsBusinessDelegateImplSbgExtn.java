package com.kony.sbg.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.backend.api.SBGTransactionLimitsBackendDelegate;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.approvalmatrixservices.businessdelegate.api.ApprovalMatrixBusinessDelegate;
import com.temenos.dbx.product.approvalmatrixservices.dto.ApprovalMatrixDTO;
import com.temenos.dbx.product.approvalmatrixservices.dto.ApprovalMatrixStatusDTO;
import com.temenos.dbx.product.approvalmatrixservices.dto.SignatoryGroupMatrixDTO;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureActionBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.LimitGroupBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.UserRoleBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.impl.TransactionLimitsBusinessDelegateImpl;
import com.temenos.dbx.product.commons.dto.ApplicationDTO;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.LimitsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commons.dto.UserLimitsDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class TransactionLimitsBusinessDelegateImplSbgExtn extends TransactionLimitsBusinessDelegateImpl {

	private static final Logger LOG = LogManager.getLogger(TransactionLimitsBusinessDelegateImplSbgExtn.class);

	ApprovalMatrixBusinessDelegate approvalmatrixBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalMatrixBusinessDelegate.class);
	ApplicationBusinessDelegate applicationBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);

	@Override
	public TransactionStatusDTO validateForLimits(String userId, String companyId, String accountId,
			String featureActionID, Double amount, TransactionStatusEnum transactionStatus, String date,
			String transactionCurrency, String serviceCharge, DataControllerRequest request) {
		
		LOG.debug("Entry --> TransactionLimitsBusinessDelegateImplSbgExtn::validateForLimits");

		TransactionStatusDTO result = new TransactionStatusDTO();
		List<String> limitTypes = new ArrayList<String>();

		CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(CustomerBusinessDelegate.class);
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ContractBusinessDelegate.class);
		UserRoleBusinessDelegate userRoleBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(UserRoleBusinessDelegate.class);
		ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApproversBusinessDelegate.class);
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
					LOG.error("validateForLimits::Converted amount is null");
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100053.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100053.getMessage());
					return result;
				}
				
				convertedAmount = (double) Math.round(convertedAmount * 100.0) / 100.0;

				/*
				 * commented "charges", as it will be taken care of in next release, when
				 * totaldebitAmount value is considered.
				 */
				// charges = Double.parseDouble(serviceCharge);

				totalAmount = convertedAmount + charges;
			} catch (Exception e) {
				LOG.error("Failed to fetch converted amount", e);
				result.setDbpErrCode(ErrorCodeEnum.ERR_27016.getErrorCodeAsString());
				result.setDbpErrMsg(ErrorCodeEnum.ERR_27016.getMessage());
				return result;
			}
		} else {
			totalAmount = amount + charges;
		}
		result.setAmount(totalAmount);
    	amount = totalAmount; 
    	LOG.debug("TransactionLimitsBusinessDelegateImplSbgExtn:totalAmount" +totalAmount +"result.getAmount()" +result.getAmount());
    	LOG.debug("TransactionLimitsBusinessDelegateImplSbgExtn:result.getTransactionAmount" +result.getTransactionAmount());
   

		// Validating contract-coreCustomer level limits for users
		if (StringUtils.isNotEmpty(contractId) && StringUtils.isNotEmpty(coreCustomerId)) {

			LimitsDTO contractCutsomerLimitsDTO = contractDelegate.fetchLimits(contractId, coreCustomerId,
					featureActionID);
			LimitsDTO exhaustedDTO = contractDelegate.fetchExhaustedLimits(contractId, coreCustomerId, featureActionID,
					date);

			if (contractCutsomerLimitsDTO == null || exhaustedDTO == null) {
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

			if (Double.compare(amount, perTrLimit) <= 0) {
				if (Double.compare(newDailyValue, dailyLimit) <= 0) {
					if (Double.compare(newWeeklyValue, weeklyLimit) <= 0) {
						// all company level limits are satisfied
					} else {
						result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
						result.setDbpErrCode(SbgErrorCodeEnum.ERR_100039.getErrorCodeAsString());
						result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100039.getMessage());
						return result;
					}
				} else {
					result.setStatus(TransactionStatusEnum.DENIED_DAILY);
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100040.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100040.getMessage());
					return result;
				}
			} else {
				result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100038.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100038.getMessage());
				return result;
			}

			// Validating role level limits for business users

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

			if (Double.compare(amount, perTrLimit) <= 0) {
				if (Double.compare(newDailyValue, dailyLimit) <= 0) {
					if (Double.compare(newWeeklyValue, weeklyLimit) <= 0) {
						// all role level limits are satisfied
					} else {
						result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
						result.setDbpErrCode(SbgErrorCodeEnum.ERR_100043.getErrorCodeAsString());
						result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100043.getMessage());
						return result;
					}
				} else {
					result.setStatus(TransactionStatusEnum.DENIED_DAILY);
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100044.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100044.getMessage());
					return result;
				}
			} else {
				result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100042.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100042.getMessage());
				return result;
			}
		}

		// Validating customer level limits for both retail and business users
		/*
		 * UserLimitsDTO userLimitsDTO = customerDelegate.fetchCustomerLimits(userId,
		 * featureActionID, accountId); LimitsDTO exhaustedLimitsDTO =
		 * customerDelegate.fetchExhaustedLimits(userId, featureActionID, date);
		 * 
		 * if(userLimitsDTO == null || exhaustedLimitsDTO == null ||
		 * exhaustedLimitsDTO.getDbpErrCode() != null ||
		 * exhaustedLimitsDTO.getDbpErrMsg() != null) {
		 * 
		 * LOG.error("Error while fetching user limits"); String dbpErrCode =
		 * exhaustedLimitsDTO.getDbpErrCode() == null ?
		 * ErrorCodeEnum.ERR_12511.getErrorCodeAsString() :
		 * exhaustedLimitsDTO.getDbpErrCode(); String dbpErrMsg =
		 * exhaustedLimitsDTO.getDbpErrMsg() == null ?
		 * ErrorCodeEnum.ERR_12511.getMessage() : exhaustedLimitsDTO.getDbpErrMsg();
		 * result.setDbpErrCode(dbpErrCode); result.setDbpErrMsg(dbpErrMsg); return
		 * result; }
		 * 
		 * if(Double.compare(userLimitsDTO.getMinTransactionLimit(), amount) > 0) {
		 * result.setStatus(TransactionStatusEnum.DENIED_MIN_TRANSACTION);
		 * result.setDbpErrCode(ErrorCodeEnum.ERR_12512.getErrorCodeAsString());
		 * result.setDbpErrMsg(ErrorCodeEnum.ERR_12512.getMessage()); return result; }
		 * 
		 * Double newDailyValue = amount + exhaustedLimitsDTO.getDailyLimit(); Double
		 * newWeeklyValue = amount + exhaustedLimitsDTO.getWeeklyLimit();
		 * 
		 * Double perTrLimit = userLimitsDTO.getMaxTransactionLimit(); Double dailyLimit
		 * = userLimitsDTO.getDailyLimit(); Double weeklyLimit =
		 * userLimitsDTO.getWeeklyLimit();
		 * 
		 * if(Double.compare(amount, perTrLimit) <= 0) {
		 * if(Double.compare(newDailyValue, dailyLimit) <= 0 ) {
		 * if(Double.compare(newWeeklyValue, weeklyLimit) <= 0) { // all user level
		 * limits are satisfied } else {
		 * result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
		 * result.setDbpErrCode(ErrorCodeEnum.ERR_12506.getErrorCodeAsString());
		 * result.setDbpErrMsg(ErrorCodeEnum.ERR_12506.getMessage()); return result; } }
		 * else { result.setStatus(TransactionStatusEnum.DENIED_DAILY);
		 * result.setDbpErrCode(ErrorCodeEnum.ERR_12505.getErrorCodeAsString());
		 * result.setDbpErrMsg(ErrorCodeEnum.ERR_12505.getMessage()); return result; } }
		 * else { result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
		 * result.setDbpErrCode(ErrorCodeEnum.ERR_12504.getErrorCodeAsString());
		 * result.setDbpErrMsg(ErrorCodeEnum.ERR_12504.getMessage()); return result; }
		 */

		// Validating Limit Group limits for both retail and business users

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

		if (Double.compare(amount, limitgroupPerTrLimit) <= 0) {
			if (Double.compare(newLimitGroupDailyValue, limitgroupDailyLimit) <= 0) {
				if (Double.compare(newLimitGroupWeeklyValue, limitgroupWeeklyLimit) <= 0) {
					// all user level limits are satisfied
				} else {
					result.setStatus(TransactionStatusEnum.DENIED_WEEKLY);
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100047.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100047.getMessage());
					return result;
				}
			} else {
				result.setStatus(TransactionStatusEnum.DENIED_DAILY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100048.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100048.getMessage());
				return result;
			}
		} else {
			result.setStatus(TransactionStatusEnum.DENIED_MAX_TRANSACTION);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100046.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100046.getMessage());
			return result;
		}

		// check for Auto denial limits of a user

		UserLimitsDTO userLimitsDTO = customerDelegate.fetchCustomerLimits(userId, featureActionID, accountId);
		LimitsDTO exhaustedDTO = customerDelegate.fetchExhaustedLimits(userId, featureActionID, date, accountId);

		if (exhaustedDTO == null || userLimitsDTO == null) {
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

		if (Double.compare(amount, autoDenialPerTrLimit) <= 0) {
			if (Double.compare(newDailyValue, autoDenialDailyLimit) <= 0) {
				if (Double.compare(newWeeklyValue, autoDenialWeeklyLimit) <= 0) {
					// all auto denial limits are satisfied
				} else {
					result.setStatus(TransactionStatusEnum.DENIED_AD_WEEKLY);
					result.setDbpErrCode(SbgErrorCodeEnum.ERR_100051.getErrorCodeAsString());
					result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100051.getMessage());
					return result;
				}
			} else {
				result.setStatus(TransactionStatusEnum.DENIED_AD_DAILY);
				result.setDbpErrCode(SbgErrorCodeEnum.ERR_100052.getErrorCodeAsString());
				result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100052.getMessage());
				return result;
			}
		} else {
			result.setStatus(TransactionStatusEnum.DENIED_AD_MAX_TRANSACTION);
			result.setDbpErrCode(SbgErrorCodeEnum.ERR_100050.getErrorCodeAsString());
			result.setDbpErrMsg(SbgErrorCodeEnum.ERR_100050.getMessage());
			return result;
		}
		
		LOG.debug("TransactionLimitsBusinessDelegateImplSbgExtn::validateForLimits --> limits check done");

		if (account != null) {
			List<ApprovalMatrixStatusDTO> status = approvalmatrixBusinessDelegate.fetchApprovalMatrixStatus(contractId,
					Arrays.asList(coreCustomerId));

			if (status != null && status.size() > 0 && status.get(0).getIsDisabled()) {
				result.setStatus(TransactionStatusEnum.SENT);
				return result;
			}
		}

		if (transactionStatus.equals(TransactionStatusEnum.NEW)) {
			// check for pre approve limits of a user
			Double apreApprovePerTrLimit = userLimitsDTO.getPreApprovedTransactionLimit();
			Double apreApproveDailyLimit = userLimitsDTO.getPreApprovedDailyLimit();
			Double apreApproveWeeklyLimit = userLimitsDTO.getPreApprovedWeeklyLimit();

			if (Double.compare(amount, apreApprovePerTrLimit) > 0) {
				// needs approval
				limitTypes.add(Constants.MAX_TRANSACTION_LIMIT);
			}
			if (Double.compare(newDailyValue, apreApproveDailyLimit) > 0) {
				// needs approval
				limitTypes.add(Constants.DAILY_LIMIT);
			}
			if (Double.compare(newWeeklyValue, apreApproveWeeklyLimit) > 0) {
				// needs approval
				limitTypes.add(Constants.WEEKLY_LIMIT);
			}
		}
		List<ApprovalMatrixDTO> approvalmatrices = new ArrayList<>();

		if (limitTypes.size() > 0) {
			approvalmatrices.addAll(approvalmatrixBusinessDelegate.fetchApprovalMatrix(contractId, coreCustomerId,
					accountId, featureActionID, ""));
			// if account level rules are not present, then retrieve from the template
			// tables
			Set<String> limits = new HashSet<>();
			for (ApprovalMatrixDTO matrix : approvalmatrices) {
				limits.add(matrix.getLimitTypeId());
			}
			if ((approvalmatrices == null || approvalmatrices.size() == 0)
					|| !limits.contains(Constants.MAX_TRANSACTION_LIMIT) || !limits.contains(Constants.DAILY_LIMIT)
					|| !limits.contains(Constants.WEEKLY_LIMIT)) {

				List<String> limitValues = new ArrayList<>();
				if (!limits.contains(Constants.MAX_TRANSACTION_LIMIT)) {
					limitValues.add(Constants.MAX_TRANSACTION_LIMIT);
				}
				if (!limits.contains(Constants.DAILY_LIMIT)) {
					limitValues.add(Constants.DAILY_LIMIT);
				}
				if (!limits.contains(Constants.WEEKLY_LIMIT)) {
					limitValues.add(Constants.WEEKLY_LIMIT);
				}

				for (String limit : limitValues) {
					approvalmatrices.addAll(approvalmatrixBusinessDelegate.fetchApprovalMatrixTemplate(contractId,
							coreCustomerId, featureActionID, limit));
				}
			}
			List<String> validApprovers = approversBusinessDelegate.getAccountActionApproverList(contractId,
					coreCustomerId, accountId, featureActionID);
			return _fetchStatusFromLimitTypes(amount, limitTypes, approvalmatrices, validApprovers, userId,
					exhaustedDTO, result); // Modified as part of ADP-2810
		} else {
			// all conditions satisfied including pre approval limits if business user
			result.setStatus(TransactionStatusEnum.SENT);
			return result;
		}
	}

	private Double getConvertedAmount(Double amount, String transactionCurrency, String baseCurrency,
			DataControllerRequest request) {
		try {
//			TransactionLimitsBackendDelegate transactionLimitsBackendDelegate = DBPAPIAbstractFactoryImpl
//					.getBackendDelegate(TransactionLimitsBackendDelegate.class);
//			return transactionLimitsBackendDelegate.fetchconvertedAmount(transactionCurrency, amount.toString(),
//					request);
			SBGTransactionLimitsBackendDelegate sbgTransactionLimitsBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(SBGTransactionLimitsBackendDelegate.class);
			return sbgTransactionLimitsBackendDelegate.fetchconvertedAmount(transactionCurrency, amount.toString(),
					request);
		} catch (Exception e) {
			LOG.error("Failed to fetch converted amount", e);
			return null;
		}
	}

	/**
	 * 
	 * @param featureActionID
	 * @param customerId
	 * @param accountId
	 * @param companyId
	 * @param amount
	 * @param limitTypes
	 * @param customerId
	 * @param result
	 * @return
	 * @throws Exception
	 */
	private TransactionStatusDTO _fetchStatusFromLimitTypes(Double amount, List<String> limitTypes,
			List<ApprovalMatrixDTO> approvalmatrices, List<String> actualApprovers, String customerId,
			LimitsDTO exhaustedDTO, TransactionStatusDTO result) {

		HashMap<String, String> groupMatrixmap = new HashMap<String, String>();
		HashMap<String, List<LimitRange>> matrices = new HashMap<String, List<LimitRange>>();
		HashMap<String, Double> exhaustedAmount = new HashMap<String, Double>();
		List<String> approvalMatrixIds = new ArrayList<String>();
		HashMap<String, Boolean> stpConfigMap = _validateSTPForLimits();

		if (CollectionUtils.isEmpty(approvalmatrices)) {
			if (stpConfigMap.containsValue(false)) {
				LOG.error("No approvalMatrix entry found for this featureactionId");
				result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
				result.setDbpErrCode(ErrorCodeEnum.ERR_12520.getErrorCodeAsString());
				result.setDbpErrMsg(ErrorCodeEnum.ERR_12520.getMessage());
				return result;
			} else {
				result.setStatus(TransactionStatusEnum.SENT);
				return result;
			}
		}

		for (ApprovalMatrixDTO approvalmatrix : approvalmatrices) {
			groupMatrixmap.put(approvalmatrix.getId(), approvalmatrix.getIsGroupMatrix());
			String key = approvalmatrix.getLimitTypeId();
			List<LimitRange> limitRanges = matrices.get(key);

			if (limitRanges == null) {
				limitRanges = new ArrayList<LimitRange>();
				switch (key) {
				case Constants.MAX_TRANSACTION_LIMIT:
					matrices.put(Constants.MAX_TRANSACTION_LIMIT, limitRanges);
					exhaustedAmount.put(Constants.MAX_TRANSACTION_LIMIT, amount);
					break;
				case Constants.DAILY_LIMIT:
					matrices.put(Constants.DAILY_LIMIT, limitRanges);
					exhaustedAmount.put(Constants.DAILY_LIMIT, amount + exhaustedDTO.getDailyLimit());
					break;
				case Constants.WEEKLY_LIMIT:
					matrices.put(Constants.WEEKLY_LIMIT, limitRanges);
					exhaustedAmount.put(Constants.WEEKLY_LIMIT, amount + exhaustedDTO.getWeeklyLimit());
					break;
				default:
					break;
				}
			}
			limitRanges.add(new LimitRange(approvalmatrix.getId(), Double.parseDouble(approvalmatrix.getLowerlimit()),
					Double.parseDouble(approvalmatrix.getUpperlimit()), approvalmatrix.getApprovalruleId()));
		}

		Map<String, SignatoryGroupMatrixDTO> matrixmap = new HashMap<String, SignatoryGroupMatrixDTO>();
		for (String limitType : limitTypes) {

			List<LimitRange> limitranges = matrices.get(limitType);
			amount = exhaustedAmount.get(limitType);

			if (CollectionUtils.isEmpty(limitranges)) {
				if (!stpConfigMap.get(limitType)) {
					LOG.error("No limitranges found for featureactionId" + limitType);
					result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
					result.setDbpErrCode(ErrorCodeEnum.ERR_12527.getErrorCodeAsString());
					result.setDbpErrMsg(ErrorCodeEnum.ERR_12527.getMessage());
					return result;
				} else {
					LOG.error("Approval matrix is not set for " + limitType
							+ ", by default the rule is NO_APPROVAL, so we need to process this transaction");
					approvalMatrixIds.add(Constants.NO_APPROVAL);
					continue;
				}
			}

			for (LimitRange limitrange : limitranges) {

				boolean isAlreadyAbove = false;
				boolean isAlreadyUpto = false;
				Double lowerLimit = limitrange.getLowerLimit();
				Double upperLimit = limitrange.getUpperLimit();
				String matrixId = limitrange.getId();

				if (Double.compare(-1, lowerLimit) == 0 && Double.compare(-1, upperLimit) == 0) {
					if (Constants.NO_APPROVAL.equalsIgnoreCase(limitrange.getRuleId())) {
						if (!stpConfigMap.get(limitType)) {
							LOG.error("Approval matrix is not configured, Please reconfigure and try again");
							result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
							result.setDbpErrCode(ErrorCodeEnum.ERR_29033.getErrorCodeAsString());
							result.setDbpErrMsg(ErrorCodeEnum.ERR_29033.getMessage());
							return result;
						} else {
							LOG.error(
									"Approval matrix is not updated yet, by default the rule is NO_APPROVAL, so we need to process this transaction");
							approvalMatrixIds.add(Constants.NO_APPROVAL);
							break;
						}
					} else {
						LOG.error("Approval matrix is not set for this contract-CoreCustomerId, account and actionId");
						result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
						result.setDbpErrCode(ErrorCodeEnum.ERR_12521.getErrorCodeAsString());
						result.setDbpErrMsg(ErrorCodeEnum.ERR_12521.getMessage());
					}
					return result;
				}

				if (Double.compare(-1, lowerLimit) == 0) {
					if (!isAlreadyUpto) {
						lowerLimit = new Double(0);
						isAlreadyUpto = true;
					} else {
						LOG.error(
								"Invalid approval matrix entries for amount ranges, multiple Upto ranges cannot exist");
						result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
						result.setDbpErrCode(ErrorCodeEnum.ERR_12522.getErrorCodeAsString());
						result.setDbpErrMsg(ErrorCodeEnum.ERR_12522.getMessage());
						return result;
					}
				}

				if (Double.compare(-1, upperLimit) == 0) {
					if (!isAlreadyAbove) {
						upperLimit = Double.MAX_VALUE;
						isAlreadyAbove = true;
					} else {
						LOG.error(
								"Invalid approval matrix entries for amount ranges, multiple Above ranges cannot exist");
						result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
						result.setDbpErrCode(ErrorCodeEnum.ERR_12523.getErrorCodeAsString());
						result.setDbpErrMsg(ErrorCodeEnum.ERR_12523.getMessage());
						return result;
					}
				}

				if (Double.compare(amount, lowerLimit) > 0 && Double.compare(amount, upperLimit) <= 0) {

					if (Constants.NO_APPROVAL.equalsIgnoreCase(limitrange.getRuleId())) {
						approvalMatrixIds.add(Constants.NO_APPROVAL);
					} else {

						List<String> approverIds = new ArrayList<>();

						if (groupMatrixmap.get(matrixId).equalsIgnoreCase(Constants.FALSE)) {
							approverIds = approvalmatrixBusinessDelegate.fetchApproverIds(matrixId);
							if (approverIds == null || approverIds.size() == 0)
								approverIds = approvalmatrixBusinessDelegate.fetchApproverIdsFromTemplate(matrixId);
						} else {
							SignatoryGroupMatrixDTO signatoryGroupMatrixDTO = approvalmatrixBusinessDelegate
									.fetchSignatoryGroupMatrix(matrixId);
							if (signatoryGroupMatrixDTO == null)
								signatoryGroupMatrixDTO = approvalmatrixBusinessDelegate
										.fetchSignatoryGroupMatrixFromTemplate(matrixId);

							matrixmap.put(signatoryGroupMatrixDTO.getApprovalMatrixId(), signatoryGroupMatrixDTO);
							approverIds = approvalmatrixBusinessDelegate
									.fetchUserOfGroupList(signatoryGroupMatrixDTO.getGroupList());
							approvalMatrixIds.add(matrixId);

							ApplicationDTO applicationDTO = applicationBusinessDelegate.properties();
							if (approverIds != null && applicationDTO != null && applicationDTO.isSelfApprovalEnabled()
									&& approverIds.contains(customerId)) {
								result.setSelfApproved(true);
							}
							break;
						}

						if (approverIds == null) {
							LOG.error("Failed to fetch approverIds for the respective matrix entry");
							result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
							result.setDbpErrCode(ErrorCodeEnum.ERR_12524.getErrorCodeAsString());
							result.setDbpErrMsg(ErrorCodeEnum.ERR_12524.getMessage());
							return result;
						}

						if (actualApprovers == null) {
							LOG.error("Failed to fetch approverIds for the respective matrix entry");
							result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
							result.setDbpErrCode(ErrorCodeEnum.ERR_12524.getErrorCodeAsString());
							result.setDbpErrMsg(ErrorCodeEnum.ERR_12524.getMessage());
							return result;
						}

						// are approversId still valid
						Set<String> validApprovers = actualApprovers.stream().distinct().filter(approverIds::contains)
								.collect(Collectors.toSet());

						if (validApprovers.size() != approverIds.size()) {
							LOG.error("Fetched approverIds are not valid in the current approval matrix");
							result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
							result.setDbpErrCode(ErrorCodeEnum.ERR_12525.getErrorCodeAsString());
							result.setDbpErrMsg(ErrorCodeEnum.ERR_12525.getMessage());
							return result;
						}

						approvalMatrixIds.add(matrixId);

						ApplicationDTO applicationDTO = applicationBusinessDelegate.properties();
						if (applicationDTO != null && applicationDTO.isSelfApprovalEnabled()
								&& approverIds.contains(customerId)) {
							result.setSelfApproved(true);
						} else {
							LOG.error("Error while fetching Application record");
						}
					}

					break;
				}
			}

		}

		if (approvalMatrixIds.size() == limitTypes.size()) {
			approvalMatrixIds.removeAll(Arrays.asList(Constants.NO_APPROVAL));

			if (approvalMatrixIds.size() > 0) {
				result.setStatus(TransactionStatusEnum.PENDING);
				Map<String, SignatoryGroupMatrixDTO> sigApprovalmatrices = getApprovalMatrices(approvalMatrixIds,
						matrixmap);
				result.setSignatoryGroupMatrices(sigApprovalmatrices);
				result.setApprovalMatrixIds(approvalMatrixIds);
			} else {
				result.setStatus(TransactionStatusEnum.SENT);
			}
			return result;
		} else {
			LOG.error("No approvalMatrix ids found for this ammount");
			result.setStatus(TransactionStatusEnum.DENIED_INVALID_APPROVAL_MATRIX);
			result.setDbpErrCode(ErrorCodeEnum.ERR_12526.getErrorCodeAsString());
			result.setDbpErrMsg(ErrorCodeEnum.ERR_12526.getMessage());
			return result;
		}

	}
	
	private Map<String, SignatoryGroupMatrixDTO>  getApprovalMatrices(List<String> approvalMatrixIds,	Map<String,SignatoryGroupMatrixDTO> matrixmap) {
		
		Map<String,SignatoryGroupMatrixDTO> sigApprovalmatrices= new HashMap<String, SignatoryGroupMatrixDTO>();

		approvalMatrixIds.forEach((matrixId)->{
			
			if(matrixmap.containsKey(matrixId)) {
				matrixmap.get(matrixId).setGroupMatrix(true);
				sigApprovalmatrices.put(matrixId, matrixmap.get(matrixId));
			}else {
				SignatoryGroupMatrixDTO matrixDto = new SignatoryGroupMatrixDTO();
				matrixDto.setGroupMatrix(false);
				sigApprovalmatrices.put(matrixId, matrixDto);
			}
		});
		
		return sigApprovalmatrices;
	}
	
	private class LimitRange{
		
		private double lowerLimit;
		private double upperLimit;
		private String ruleId;
		private String id;
		
		LimitRange(String id, double lowerLimit, double upperLimit, String ruleId) {
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			this.id = id;
			this.ruleId = ruleId;
		}

		public double getLowerLimit() {
			return lowerLimit;
		}

		public double getUpperLimit() {
			return upperLimit;
		}

		public String getId() {
			return id;
		}
		
		public String getRuleId() {
			return ruleId;
		}

	}
	
	private HashMap<String, Boolean> _validateSTPForLimits() {
		HashMap<String, Boolean> stpConfigMap = new HashMap<>();
		String configParamValue = null;		
		configParamValue = EnvironmentConfigurationsHandler.getValue(Constants.AM_MAX_LIMIT_NO_RULES_ALLOW_STP);
		if("false".equalsIgnoreCase(configParamValue)) {
			stpConfigMap.put(Constants.MAX_TRANSACTION_LIMIT, false);
		}
		else {
			stpConfigMap.put(Constants.MAX_TRANSACTION_LIMIT, true);
		}
		configParamValue = EnvironmentConfigurationsHandler.getValue(Constants.AM_DAILY_LIMIT_NO_RULES_ALLOW_STP);
		if("false".equalsIgnoreCase(configParamValue)) {
			stpConfigMap.put(Constants.DAILY_LIMIT, false);
		}
		else {
			stpConfigMap.put(Constants.DAILY_LIMIT, true);
		}
		configParamValue = EnvironmentConfigurationsHandler.getValue(Constants.AM_WEEKLY_LIMIT_NO_RULES_ALLOW_STP);
		if("false".equalsIgnoreCase(configParamValue)) {
			stpConfigMap.put(Constants.WEEKLY_LIMIT, false);
		}
		else {
			stpConfigMap.put(Constants.WEEKLY_LIMIT, true);
		}
		configParamValue = EnvironmentConfigurationsHandler.getValue(Constants.AM_NON_MONETORY_NO_RULES_ALLOW_STP);
		if("false".equalsIgnoreCase(configParamValue)) {
			stpConfigMap.put(Constants.AM_NON_MONETORY_NO_RULES_ALLOW_STP, false);
		}
		else {
			stpConfigMap.put(Constants.AM_NON_MONETORY_NO_RULES_ALLOW_STP, true);
		}
		return stpConfigMap;
	}
	
}
