package com.kony.sbg.business.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.PaymentAPIBackendDelegate;
import com.kony.sbg.business.api.SbgInternationalFundTransferBusinessDelegateExtn;
import com.kony.sbg.util.RefDataCacheHelper;
import com.kony.sbg.util.RefDataValidator;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.UploadedAttachments;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InternationalFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.impl.InternationalFundTransferBusinessDelegateImpl;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;

public class SbgInternationalFundTransferBusinessDelegateImplExtn extends InternationalFundTransferBusinessDelegateImpl
		implements SbgInternationalFundTransferBusinessDelegateExtn {
	private static final Logger LOG = LogManager.getLogger(SbgInternationalFundTransferBusinessDelegateImplExtn.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	InternationalFundTransferBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(InternationalFundTransferBackendDelegate.class);

	@Override
	public InternationalFundTransferDTO createPendingTransaction(InternationalFundTransferDTO input,
			DataControllerRequest request) {

		ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApplicationBusinessDelegate.class);
		if (!application.getIsStateManagementAvailableFromCache()) {
			/*
			 * Appending REF to referenceID is creating issue while fetching pending
			 * approvals as referenceID don't match
			 */
			// input.setReferenceId(Constants.REFERENCE_KEY + input.getTransactionId());
			input.setReferenceId(input.getTransactionId());
			return input;
		}

		/*
		 * Commenting this as it is generating new referenceID
		 * InternationalFundTransferDTO dto = new InternationalFundTransferDTO();
		 * dto.setReferenceId(CommonUtils.generateUniqueIDHyphenSeperated(0, 16));
		 */

		return input;
	}

	
	@Override
	public InternationalFundTransferDTO fetchTransactionById(String referenceId,
			DataControllerRequest dataControllerRequest) {
		InternationalFundTransferDTO filterData= new InternationalFundTransferDTO();
		LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:fetchTransactionById");
		try {
		if (!application.getIsStateManagementAvailableFromCache()) {
			LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:referenceId"+referenceId);
			InternationalFundTransferDTO transEntry =fetchTranscationEntry(referenceId);
			transEntry.setAmount(Double.parseDouble(transEntry.getTransactionAmount()));
			return transEntry;
		}
		
		InternationalFundTransferDTO backendData =backendDelegate.fetchTransactionById(referenceId, dataControllerRequest);
		LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:backendData.getAmount()"+backendData.getAmount() +"backendData.getTransactionAmount()"+ backendData.getTransactionAmount());
		backendData.setAmount(Double.parseDouble(backendData.getTransactionAmount()));
		InternationalFundTransferDTO dbxData = fetchExecutedTranscationEntry(referenceId, null, null);
		LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:dbxData.getAmount()"+dbxData.getAmount() +"dbxData.getTransactionAmount()"+ dbxData.getTransactionAmount());
		dbxData.setAmount(Double.parseDouble(dbxData.getTransactionAmount()));
		if(backendData == null || StringUtils.isEmpty(backendData.getDbpErrMsg())) {
			LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:null");
			return dbxData;
		}
		
		//return (new FilterDTO()).merge( Arrays.asList(dbxData),Arrays.asList(backendData),"confirmationNumber=transactionId", "").get(0);
		 filterData =(new FilterDTO()).merge( Arrays.asList(dbxData),Arrays.asList(backendData),"confirmationNumber=transactionId", "").get(0);
		LOG.debug("SBGInternationalFundTransferBusinessDelegateImpl:filterData.getAmount()"+filterData.getAmount() +"filterData.getTransactionAmount()"+ filterData.getTransactionAmount());
		filterData.setAmount(Double.parseDouble(filterData.getTransactionAmount()));
		return filterData;
		}
		catch (Exception e) {
			LOG.error("SBGInternationalFundTransferBusinessDelegateImpl:fetchTransactionById: "
					+ e.getMessage());
		}
		return filterData;
	}
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
	
	private String getRefData(DataControllerRequest request) throws AppRegistryException {
		String currency = request.getParameter("transactionCurrency");
		String country = "ZA";
		String fromAccountNumber = request.getParameter("fromAccountNumber");
		String BIC = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from Arrangement Extension
		LOG.debug("&&&&&BICFromExtension: " + BIC);
		if (StringUtils.isBlank(BIC)) {
		/*	ConfigurableParametersHelper configurableParametersHelper = request.getServicesManager()
					.getConfigurableParametersHelper();
			Map<String, String> allClientProperties = configurableParametersHelper.getAllClientAppProperties();
			String arrangementCodeForServerProperty = "AE_" + fromAccountNumber + "_EXTENSIONDATA";
			String accountExtensionFromServerProperties = allClientProperties.get(arrangementCodeForServerProperty);
			JSONObject accountExtensionFromServerPropertiesJSON = new JSONObject(accountExtensionFromServerProperties);
			BIC = accountExtensionFromServerPropertiesJSON.getString("bic");
			LOG.debug("&&&&&BICFromServerProps: " + BIC);*/
			
			return SBGConstants.FALSE;
		}
		if (StringUtils.isNotBlank(BIC)) {
			country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
					(SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
		}
		return RefDataCacheHelper.getRefDataByKey(request, country, currency);
	}

//	private String callRefDataOrchService(DataControllerRequest request, Map<String, Object> headerParams,
//			Map<String, Object> requestParameters, String serviceName, String operationName) {
//
//		String refDataResponse = null;
//		String currency = request.getParameter("transactionCurrency");
//		ServicesManager servicesManager;
//
//		try {
//			servicesManager = request.getServicesManager();
//			ConfigurableParametersHelper configurableParametersHelper = servicesManager
//					.getConfigurableParametersHelper();
//			// Map<String, String> allServerProperties =
//			// configurableParametersHelper.getAllServerProperties();
//			Map<String, String> allClientProperties = configurableParametersHelper.getAllClientAppProperties();
//			String country = "ZA";
//			try {
//				String fromAccountNumber = request.getParameter("fromAccountNumber");
//				String arrangementCodeForServerProperty = "AE_" + fromAccountNumber + "_EXTENSIONDATA";
//				String accountExtensionFromServerProperties = allClientProperties.get(arrangementCodeForServerProperty);
//				JSONObject accountExtensionFromServerPropertiesJSON = new JSONObject(
//						accountExtensionFromServerProperties);
//				String BIC = accountExtensionFromServerPropertiesJSON.getString("bic");
//				if (BIC != null) {
//					country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
//							(SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
//				}
//				try {
//					String REFDATACACHE = "REFDATACACHE" + country + currency;
//					LOG.debug("&&&&&REFDATACACHE: " + REFDATACACHE);
//					ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
//					Result result = (Result) resultCache.retrieveFromCache(REFDATACACHE);
//					if (result != null && result.hasParamByName(REFDATACACHE)) {
//						LOG.debug("&&&&&REFDATACACHEresultCache: " + resultCache);
//						refDataResponse = result.getParamValueByName(REFDATACACHE).toString();
//					} else {
//						SbgInternationalFundTransferBackendDelegateExtn sbgInternationalFundTransferBackendDelegateExtn = DBPAPIAbstractFactoryImpl
//								.getBackendDelegate(SbgInternationalFundTransferBackendDelegateExtn.class);
//						try {
//							refDataResponse = sbgInternationalFundTransferBackendDelegateExtn.callRefDataOrchService(
//									headerParams, requestParameters, serviceName, operationName);
//							LOG.debug("&&&&&REFDATACACHErefDataResponse: " + refDataResponse);
//							if (refDataResponse != null) {
//								JSONObject responseObj = new JSONObject(refDataResponse);
//								if (responseObj.has("opstatus")) {
//									int opstatus = responseObj.getInt("opstatus");
//									if (opstatus == 0) {
//										Result result2 = new Result();
//										result2.addParam(REFDATACACHE, refDataResponse);
//										cacheInsert(REFDATACACHE, result2, 0);
//									} else {
//										LOG.error(
//												"[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error");
//										return refDataResponse;
//									}
//								} else {
//									LOG.error(
//											"[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error");
//									return refDataResponse;
//								}
//							}
//						} catch (Exception e) {
//							LOG.error(
//									"[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error: "
//											+ e.getMessage());
//							return refDataResponse;
//						}
//					}
//				} catch (Exception e) {
//					LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error: "
//							+ e.getMessage());
//					return refDataResponse;
//				}
//			} catch (Exception e) {
//				LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error: "
//						+ e.getMessage());
//				return refDataResponse;
//			}
//		} catch (AppRegistryException e) {
//			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] callRefDataOrchService(-) Error: "
//					+ e.getMessage());
//			return refDataResponse;
//		}
//		return refDataResponse;
//	}
	
//	public static void cacheInsert(String key, Result result, int life) throws Exception {
//		if (key == null)
//			throw new Exception("Cache key must be provided");
//		if (life == 0)
//			life = 90;
//		if (result != null) {
//			ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
//			resultCache.insertIntoCache(key, result);
//		}
//	}
	
	public JSONObject sbgValidations(DataControllerRequest request) {
		LOG.info("##[SbgInternationalFundTransferBusinessDelegateImplExtn] Resource sbgValidations");
		String refDataResponse = null;
		JSONObject sbgValidationsResponseObj = new JSONObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
		String strScheduledDate = null;
		Date scheduledDate = null;
		JSONObject responseObj = null;
		try {
			try {
				refDataResponse = getRefData(request);
				LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] refDataResponse: " + refDataResponse);
				if(SBGConstants.FALSE.equalsIgnoreCase(refDataResponse)) {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_EXTENSION_DATE_ERROR_MESSAGE);
				}
				responseObj = new JSONObject(refDataResponse);
				strScheduledDate = request.getParameter("scheduledDate");
				if (StringUtils.isNotBlank(strScheduledDate)) {
					scheduledDate = dateFormat.parse(strScheduledDate);
					LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] scheduledDate: " + scheduledDate);
				} else {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SCHEDULED_DATE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn]  Error: " + e.getMessage());
				return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SERVICESDOWN);
			}

			SimpleDateFormat sdf = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
			return RefDataValidator.verifyRefData(responseObj, sdf.parse(sdf.format(new Date())), scheduledDate, request.getParameter("transactionCurrency"));
		} catch (Exception e) {
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] sbgValidations(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}
	}

	/*
	 * Kumara Swamy : method to Validate Ref Data Validations
	 */
	public JSONObject sbgValidations_OLD(DataControllerRequest request) {
		LOG.info("##[SbgInternationalFundTransferBusinessDelegateImplExtn] Resource sbgValidations");
		String refDataResponse = null;
		JSONObject sbgValidationsResponseObj = new JSONObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
		SimpleDateFormat dayFormat = new SimpleDateFormat(SBGConstants.DAY_FORMAT);
		String strScheduledDate = null;
		Date scheduledDate = null;
		Date createdDate = null;
		String day = null;
		String createdDay = null;
		JSONObject responseObj = null;
		JSONObject cutOffTimeResponse = null;
		JSONObject validateHolidaysResponse = null;
		int leadCount = 1;
		int nextWorkingDayComesAfter = 0;
		try {
			//SbgInternationalFundTransferBackendDelegateExtn sbgInternationalFundTransferBackendDelegateExtn = DBPAPIAbstractFactoryImpl
			//		.getBackendDelegate(SbgInternationalFundTransferBackendDelegateExtn.class);
			try {
				//headerParams = constructHeaderParams(request, headerParams);
				// to call Ref Data Orchestretion Service service call
				//refDataResponse = callRefDataOrchService(request, headerParams, requestParameters, serviceName, operationName);
				refDataResponse = getRefData(request);
				LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] refDataResponse: " + refDataResponse);
				if(SBGConstants.FALSE.equalsIgnoreCase(refDataResponse)) {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_EXTENSION_DATE_ERROR_MESSAGE);
				}
				responseObj = new JSONObject(refDataResponse);
				strScheduledDate = request.getParameter("scheduledDate");
				if (StringUtils.isNotBlank(strScheduledDate)) {
					scheduledDate = dateFormat.parse(strScheduledDate);
					LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] scheduledDate: " + scheduledDate);
					day = dayFormat.format(scheduledDate);
					LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] Full Day Name: " + day);
				} else {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SCHEDULED_DATE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn]  Error: " + e.getMessage());
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
					LOG.debug("[validateCutOffTime] sbgValidationsResponseObj: " + sbgValidationsResponseObj);
					LOG.debug("[validateCutOffTime] dateFormat: " + dateFormat.toString());
					LOG.debug("[validateCutOffTime] createdDate: " + createdDate);
					LOG.debug("[validateCutOffTime] dayFormat: " + dayFormat.toString());
					LOG.debug("[validateCutOffTime] createdDay: " + createdDay);
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
								"[SbgInternationalFundTransferBusinessDelegateImplExtn] leadDayDate:: " + leadDayDate);
						LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] leadDay: " + leadDay);

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
					if (compareDDMMYY(scheduledDate,createdDate)) {
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
	 * Kumara Swamy : Method to Construct Header Parameters required for RefData
	 * Validations Orchestration service
	 */

//	private Map<String, Object> constructHeaderParams(DataControllerRequest request, Map<String, Object> headerParams)
//			throws Exception {
//		String currency = request.getParameter("transactionCurrency");
//		// Get ServicesManager Object
//		ServicesManager servicesManager = request.getServicesManager();
//		// Get ConfigurableParametersHelper Object
//		ConfigurableParametersHelper configurableParametersHelper = servicesManager.getConfigurableParametersHelper();
//		Map<String, String> allServerProperties = configurableParametersHelper.getAllServerProperties();
//		
//		Map<String, String> allClientProperties = configurableParametersHelper.getAllClientAppProperties();
//		String country = "ZA";
//		// Get Arrangements Extension - to be replaced with getList
//		try {
//			String fromAccountNumber = request.getParameter("fromAccountNumber");
//			String arrangementCodeForServerProperty = "AE_" + fromAccountNumber + "_EXTENSIONDATA";
//			String accountExtensionFromServerProperties = allClientProperties.get(arrangementCodeForServerProperty);
//			JSONObject accountExtensionFromServerPropertiesJSON = new JSONObject(accountExtensionFromServerProperties);
//			String BIC = accountExtensionFromServerPropertiesJSON.getString("bic");
//			if (BIC != null) {
//				country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
//						(SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
//			}
//		} catch (Exception e) {
//			LOG.error("&&&&&constructHeaderParamsBIC : " + e.getMessage());
//		}
//		// Get Arrangements Extension End
//		/*
//		 * if (request.containsKeyInRequest("BIC") && request.getParameter("BIC") !=
//		 * null && request.getParameter("BIC").length() >
//		 * SBGConstants.BIC_COUNTRY_CODE_END_INDEX) { country =
//		 * request.getParameter("BIC").substring(SBGConstants.
//		 * BIC_COUNTRY_CODE_START_INDEX, SBGConstants.BIC_COUNTRY_CODE_END_INDEX); }
//		 * else { country = "ZA"; }
//		 */
//		// to get Property from Runtime
//		String sbgHeaderApiProductCode = allServerProperties.get("SBG_HEADER_API_PRODUCT_CODE");
//		// Authorization token,clientID, clientSecret are fetched in SBGBseProcessor
//		// stored in Request object.
//		Result authorizationRresult =SBGCommonUtils.cacheFetchPingToken("Authorization",request);
//		String authVal = authorizationRresult.getParamValueByName("Authorization");
//		String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
//		String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
//
//		headerParams.put("Authorization", authVal);
//		headerParams.put("X-IBM-Client-Id", clientID);
//		headerParams.put("X-IBM-Client-Secret", clientSecret);
//		headerParams.put("Country", country); // Need to take from Swift Code 5th&6th Character of From Account
//		headerParams.put("ProductCode", sbgHeaderApiProductCode);
//		headerParams.put("Currency", currency);
//
//		LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn]  headerParams: " + headerParams.toString());
//		return headerParams;
//	}// constructHeaderParams(-)

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

	/*
	 * Kumara Swamy : method to Validate Cut-Off time
	 */

	private JSONObject validateCutOffTime(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate, SimpleDateFormat dayFormat, String day) {
		LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn]  validateCutOffTime(-) ");
		boolean isCutOffTime = false;
		String strStartTime = null;
		String strEndTime = null;
		String timeZone = null;
		int leadDays = 0;
		JSONObject cutOffTimeResponse = new JSONObject();
		try {
			if (responseObj != null && responseObj.has("Days")) {
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
									LOG.info("[SbgInternationalFundTransferBusinessDelegateImplExtn] Cut- Off Time:::"
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
			LOG.error("[SbgInternationalFundTransferBusinessDelegateImplExtn] validateCutOffTime(-) Error: "
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
	 * Kumara Swamy : method to Set Error mesage to a JSON Object and return
	 */
	private JSONObject setErrorResult(JSONObject sbgValidationsResponseObj, String errorMessage) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.FALSE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, errorMessage);
		LOG.debug("[SbgInternationalFundTransferBusinessDelegateImplExtn] setErrorResult(-) sbgValidationsResponseObj: "
				+ sbgValidationsResponseObj.toString());

		return sbgValidationsResponseObj;
	}// setErrorResult(-)

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
	 * Overridden to disable T24 service call & replaced transactionId with
	 * confirmationNumber in if .. filter
	 */
	@Override
	public List<ApprovalRequestDTO> fetchInternationalTransactionsWithApprovalInfo(List<BBRequestDTO> requests,
			DataControllerRequest dcr) {
		Set<String> internationalTransIds = new HashSet<String>();
		List<ApprovalRequestDTO> transactions = new ArrayList<ApprovalRequestDTO>();

		if (CollectionUtils.isEmpty(requests))
			return transactions;

		for (BBRequestDTO bBRequestDTO : requests) {
			if (StringUtils.isNotBlank(bBRequestDTO.getTransactionId()))
				internationalTransIds.add(bBRequestDTO.getTransactionId());
		}

		String filter = "";

		if (!application.getIsStateManagementAvailableFromCache()) {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL
					+ String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL,
							internationalTransIds);
			transactions = fetchInternationalTransactionsForApprovalInfo(filter, dcr);
			/*
			 * Modified below line by adding & assigning transactionId to confirmationNumber
			 */
			// transactions = (new FilterDTO()).merge(transactions, requests,
			// "transactionId=transactionId",
			// "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
			transactions = (new FilterDTO()).merge(transactions, requests, "confirmationNumber=transactionId",
					"transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
		} else {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL
					+ String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL,
							internationalTransIds);
			transactions = fetchInternationalTransactionsForApprovalInfo(filter, dcr);
			List<ApprovalRequestDTO> backendData = backendDelegate
					.fetchBackendTransactionsForApproval(internationalTransIds, dcr);
			if (CollectionUtils.isNotEmpty(backendData)) {
				transactions = (new FilterDTO()).merge(transactions, backendData, "confirmationNumber=transactionId",
						"");
			}
			transactions = (new FilterDTO()).merge(transactions, requests, "confirmationNumber=transactionId",
					"transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
			// backendDelegate.getBeneBankAddress(transactions, dcr);
		}

		transactions = retrieveAttachments(transactions, dcr);
		return transactions;
	}

	private List<ApprovalRequestDTO> retrieveAttachments(List<ApprovalRequestDTO> transactionsList,
			DataControllerRequest dcr) {

		for (ApprovalRequestDTO transaction : transactionsList) {

			List<UploadedAttachments> filesList = new ArrayList<>();
			String serviceName = ServiceId.DBPNONPRODUCTSERVICES;
			String operationName = OperationName.RETRIEVE_ATTACHMENTS;
			Map<String, Object> requestParameters = new HashMap<>();
			requestParameters.put("transactionId", transaction.getConfirmationNumber());

			String response = null;
			try {

				response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
						.withOperationId(operationName).withRequestParameters(requestParameters)
						.withRequestHeaders(dcr.getHeaderMap()).withDataControllerRequest(dcr).build().getResponse();

				JSONObject jsonRsponse = new JSONObject(response);
				JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
				filesList = JSONUtils.parseAsList(jsonArray.toString(), UploadedAttachments.class);
				transaction.setFileNames(filesList);
			} catch (JSONException e) {
				LOG.error("Unable to retieve attachments", e);
			} catch (Exception e) {
				LOG.error("Unable to retieve attachments", e);
			}

		}
		return transactionsList;
	}

	/*
	 * In bbrequest transaction ID is having value equal to confirmation number of
	 * internationalfundtransfer table. Replacing transactionId with confirmation
	 * number in filter query
	 */
	@Override
	public InternationalFundTransferDTO fetchTranscationEntry(String transactionId) {
		List<InternationalFundTransferDTO> internationalfundtransferdto = null;
		String bankName = "";
		LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn.fetchTranscationEntry() --- START:: transactionId: " + transactionId);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERNATIONALFUNDTRANSFERS_GET;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + transactionId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();

			LOG.debug("fetchTranscationEntry() --- fetchResponse: " + fetchResponse);
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray internationalfundJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);

			internationalfundtransferdto = JSONUtils.parseAsList(internationalfundJsonArray.toString(),
					InternationalFundTransferDTO.class);
			
			if (internationalfundJsonArray.length() > 0) {
				JSONObject obj = internationalfundJsonArray.getJSONObject(0);
				if (obj.has("bankName")) {
					bankName = obj.getString("bankName");
				}				
			}
			LOG.debug("fetchTranscationEntry() --- DTO Created: BankName: " + bankName);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the internationalfundtransaction", jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the internationalfundtransaction", exp.getMessage());
			return null;
		}
		
		/*Modifying Nirthika*/
		if (internationalfundtransferdto != null && internationalfundtransferdto.size() != 0) {
			InternationalFundTransferDTO internationalFundTransferDTO2 = internationalfundtransferdto.get(0);			
			internationalFundTransferDTO2.setBeneficiaryBankName(bankName);
			//return internationalfundtransferdto.get(0);
			return internationalFundTransferDTO2;
		} else {
			LOG.debug("fetchTranscationEntry() --- ZERO records found: ");
		}

		return null;
	}

	@Override
	public InternationalFundTransferDTO updateStatus(String transactionId, String status, String confirmationNumber) {

		LOG.debug("updateStatus() ===> transactionId:" + transactionId + "; status:" + status + "; confirmationNumber:"
				+ confirmationNumber);
		return updateStatusUsingConfirmationNumber(transactionId, status);

	}

	private InternationalFundTransferDTO updateStatusUsingConfirmationNumber(String confirmationNumber, String status) {
		InternationalFundTransferDTO transactionDTO = null;

		LOG.debug("updateStatusUsingConfirmationNumber() ===> status:" + status + "; confirmationNumber:"
				+ confirmationNumber);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_UPDATE_TRANSACTION_STATUS_PROC;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_featureId", "INTERNATIONAL_ACCOUNT_FUND_TRANSFER");
		requestParams.put("_status", status);
		requestParams.put("_confirmationNumber", confirmationNumber);

		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			LOG.debug("updateStatusUsingConfirmationNumber() ===> jsonRsponse:" + jsonRsponse);

			JSONArray jsonArr = CommonUtils.getFirstOccuringArray(jsonRsponse);
			if (jsonArr != null && jsonArr.length() > 0) {
				JSONObject record = jsonArr.getJSONObject(0);
				if (record != null && record.has("isSuccess") && "true".equals(record.get("isSuccess"))) {
					transactionDTO = new InternationalFundTransferDTO();
					transactionDTO.setConfirmationNumber(confirmationNumber);
					transactionDTO.setStatus(status);
					LOG.debug("updateStatusUsingConfirmationNumber() ===> record has success");
				} else {
					LOG.debug("updateStatusUsingConfirmationNumber() ===> record has failure:");
				}
			}
		} catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured in updateStatusUsingConfirmationNumber" + jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured in updateStatusUsingConfirmationNumber" + exp.getMessage());
			return null;
		}

		return transactionDTO;
	}
	
	@Override
	public InternationalFundTransferDTO approveTransaction(String referenceId, DataControllerRequest request,
			String frequency) {
		LOG.debug("Entry --> SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction::referenceId: " + referenceId);
		InternationalFundTransferBackendDTO backendObj = new InternationalFundTransferBackendDTO();
		InternationalFundTransferDTO dbxObj = fetchTransactionById(referenceId, request);		
		backendObj = backendObj.convert(dbxObj);
		
		LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction ConfirmationNumber: "
				+ dbxObj.getConfirmationNumber() + " ReferenceID: " + dbxObj.getReferenceId() + " TransactionID: "
				+ dbxObj.getTransactionId() + " TransactionAmount: " + dbxObj.getTransactionAmount()
				+ " ScheduledDate: " + backendObj.getScheduledDate() + " FromAccountNumber: "
				+ dbxObj.getFromAccountNumber() + " FromAccountCurrency: " + dbxObj.getFromAccountCurrency()
				+ " ToAccountCurrency: " + dbxObj.getTransactionCurrency() + " BeneBankName: "
				+ dbxObj.getBeneficiaryBankName() + " beneficiaryState: " + dbxObj.getBeneficiaryState());
		
		try {
			backendObj.setAmount(Double.parseDouble(backendObj.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return null;
		}
		
		try {
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction::scheduledDate: " + backendObj.getScheduledDate());
			backendObj.setScheduledDate(SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss", SbgURLConstants.PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z, backendObj.getScheduledDate()));
		} catch (ParseException e) {
			LOG.error("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction --> Error occured while formatting date", e);
			return null;
		}
		
		backendObj.setBeneficiaryBankName(dbxObj.getBeneficiaryBankName());
		backendObj.setConfirmationNumber(dbxObj.getConfirmationNumber());
		String confirmationNumber = dbxObj.getConfirmationNumber();
		String referenceIdFilter = "referenceId" + DBPUtilitiesConstants.EQUAL + backendObj.getTransactionId();
		LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction Before 1st fetchTranscationRefData");
		fetchTranscationRefData(backendObj.getTransactionId(), request, referenceIdFilter);

		LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction After 1st fetchTranscationRefData");
		backendObj.setBeneficiaryState(request.getParameter("beneficiaryState"));
		backendObj.setBeneficiaryAddressLine2(request.getParameter("beneficiaryAddressLine2"));

//		request.addRequestParam_("uploadedattachments",getUploadedAttachments(confirmationNumber));
		request.addRequestParam_("uploadedattachments", getDocIdsByTransactionId(confirmationNumber));

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> userData= new HashMap<>();
		try {
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction Before 2nd fetchTranscationRefData");
			String confirmationNumberFilter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + confirmationNumber;
			JSONObject jsonObject = fetchTranscationRefData(confirmationNumber, request,confirmationNumberFilter);
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction 2nd fetchTranscationRefData::jsonObject = " + jsonObject);

			JSONArray arr = jsonObject.getJSONArray("internationalfundtransfersRefData");			
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction 2nd fetchTranscationRefData:: array = " + arr);

			String jsonObjectString = jsonObject !=null ? jsonObject.getJSONArray("internationalfundtransfersRefData").getJSONObject(0).toString() : null;
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction jsonObjectString:"+jsonObjectString);

			userData = objectMapper.readValue(jsonObjectString, new TypeReference<Map<String, Object>>() {});
			LOG.debug("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction userData:"+userData.toString());
		} catch (JsonProcessingException e) {
			LOG.error("SbgInternationalFundTransferBusinessDelegateImplExtn::approveTransaction --> Error occured while converting json to Map", e);
		}

		PaymentAPIBackendDelegate paymentAPIBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(PaymentAPIBackendDelegate.class);
		InternationalFundTransferDTO resultDTO = paymentAPIBackendDelegate.createTransactionWithoutApproval(backendObj, request, userData);
	    
		LOG.debug("resultDTO.getDbpErrCode()" +resultDTO.getDbpErrCode());
		LOG.debug("resultDTO.getDbpErrMsg()" +resultDTO.getDbpErrMsg());
		LOG.debug("resultDTO.getReferenceId()" +resultDTO.getReferenceId());
		LOG.debug("resultDTO.getConfirmationNumber()" +resultDTO.getConfirmationNumber());
		return resultDTO;
	}

	private String getDocIdsByTransactionId(String transactionId) {
		LOG.debug("### Starting ---> getDocIdsByTransactionId");

		try {
			String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
			String operationName = SbgURLConstants.OPERATION_SBT_DOCUMENT_GET;

			StringBuilder sb = new StringBuilder();
			String transactionIdFilter = sb.append("transaction_id").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();

			Map<String, Object> input = new HashMap<>();
			input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
			input.put(DBPUtilitiesConstants.SELECT, "document_id");

			String docResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(input)
					.withRequestHeaders(new HashMap<>()).build().getResponse();
			JSONObject jsonObject = new JSONObject(docResponse);

			JSONArray records = jsonObject.getJSONArray("sbg_documents");
			String docIdListStr = "";
			if (records != null) {

				for (int i = 0; i < records.length(); i++) {
					String docId = records.getJSONObject(i).getString("document_id");

					if (org.apache.commons.lang.StringUtils.isNotBlank(docIdListStr) && org.apache.commons.lang.StringUtils.isNotBlank(docId)) {
						docIdListStr = docIdListStr + "," + docId;
					} else if (org.apache.commons.lang.StringUtils.isBlank(docIdListStr)) {
						docIdListStr = docId;
					}
				}
			}
			LOG.debug("#### getDocIdsByTransactionId getUploadedAttachments docIdListStr:" + docIdListStr);
			return docIdListStr;
		} catch (Exception e) {
			LOG.error("error retrieve doc ids");
			return "";
		}

	}

	private JSONObject fetchTranscationRefData(String referenceId, DataControllerRequest request, String filter) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_INTERNATIONALFUNDTRANSFERSREFDATA_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		JSONObject serviceResponse = null;
		//String filter = "referenceId" + DBPUtilitiesConstants.EQUAL + referenceId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();			
			serviceResponse = new JSONObject(fetchResponse);
			if (serviceResponse != null && serviceResponse.length() > 0) {
				if (serviceResponse.has("internationalfundtransfersRefData") && serviceResponse.getJSONArray("internationalfundtransfersRefData").length() > 0) {					
					JSONArray internationalfundtransfersRefData = serviceResponse.getJSONArray("internationalfundtransfersRefData");					
					JSONObject refDataRsp = internationalfundtransfersRefData.getJSONObject(0);
					request.addRequestParam_("purposeCode", refDataRsp.optString("purposecode"));
					request.setAttribute("purposeCode", refDataRsp.optString("purposecode"));					
					request.addRequestParam_("beneficiaryReference", refDataRsp.optString("benerefeno"));
					request.setAttribute("beneficiaryReference", refDataRsp.optString("benerefeno"));					
					request.addRequestParam_("complianceCode", refDataRsp.optString("compliancecode"));
					request.setAttribute("complianceCode", refDataRsp.optString("compliancecode"));
					request.addRequestParam_("statementReference", refDataRsp.optString("statementrefno"));
					request.setAttribute("statementReference", refDataRsp.optString("statementrefno"));
					request.addRequestParam_("rfqDetails", refDataRsp.optString("rfqDetails"));					
					request.setAttribute("rfqDetails", refDataRsp.optString("rfqDetails"));					
					request.addRequestParam_("bopDetails", refDataRsp.optString("bopDetails"));					
					request.setAttribute("bopDetails", refDataRsp.optString("bopDetails"));		
					request.addRequestParam_("beneficiaryState", refDataRsp.optString("beneficiaryState"));
					request.setAttribute("beneficiaryState", refDataRsp.optString("beneficiaryState"));
					request.addRequestParam_("clearingCode", refDataRsp.optString("clearingCode"));
					request.setAttribute("clearingCode", refDataRsp.optString("clearingCode"));
					request.addRequestParam_("beneficiaryAddressLine2", refDataRsp.optString("beneficiaryAddressLine2"));
					request.setAttribute("beneficiaryAddressLine2", refDataRsp.optString("beneficiaryAddressLine2"));
				}
			} else {
				LOG.debug("fetchTranscationRefData::serviceResponse is empty");
			}
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the internationalfundtransfersRefData",
					jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the internationalfundtransfersRefData", exp.getMessage());
			return null;
		}
		return serviceResponse;
	}
	
	public JSONObject fetchTransactionEntryFiltered(String filter) {
		JSONObject serviceResponse = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERNATIONALFUNDTRANSFERS_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		
		if (filter == null || filter.isEmpty()) {
			return null;
		}
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		String fetchResponse;
		try {
			fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			serviceResponse = new JSONObject(fetchResponse);
		} catch (DBPApplicationException e) {
			LOG.error("SbgInternationalFundTransferBusinessDelegateImplExtn::fetchTransactionEntryFiltered" + e);
			return null;
		}
		return serviceResponse;
	}

	private JSONObject getDocumentMetaDataByTransactionId(String transactionId) throws HttpCallException, DBPApplicationException {
		LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId()");

		String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
		String operationName = SbgURLConstants.OPERATION_DOCUMENT_WITH_CONTENT;

		StringBuilder sb = new StringBuilder();
		String transactionIdFilter = sb.append("transactionId").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();
		LOG.debug("### RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() filter:" + transactionIdFilter);

		Map<String, Object> input = new HashMap<>();
		input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
		input.put(DBPUtilitiesConstants.SELECT, "name");

		String docWithoutContentResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
				.withOperationId(operationName).withRequestParameters(input)
				.withRequestHeaders(new HashMap<>()).build().getResponse();
		LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:" + docWithoutContentResponse);
		//return HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), SBGConstants.DOCUMENT_INFO_WITHOUT_DOC);

		return new JSONObject(docWithoutContentResponse);
	}

	private String getUploadedAttachments(String transactionId) {
		try {
			LOG.debug("#### starting getUploadedAttachments for transactionId:"+transactionId);
			JSONObject jsonObject = getDocumentMetaDataByTransactionId(transactionId);
			JSONArray records = jsonObject.getJSONArray("documents");
			String docNameListStr= "";
			if (records != null) {

				for (int i = 0; i < records.length(); i++) {
					String docName= records.getJSONObject(i).getString("name");

					if(org.apache.commons.lang.StringUtils.isNotBlank(docNameListStr) && org.apache.commons.lang.StringUtils.isNotBlank(docName)){
						docNameListStr = docNameListStr + "," +docName;
					}else if(org.apache.commons.lang.StringUtils.isBlank(docNameListStr)){
						docNameListStr=docName;
					}
				}
			}
			LOG.debug("#### getUploadedAttachments docNameListStr:"+docNameListStr);
			return docNameListStr;
		} catch (Exception e) {
			LOG.debug("#### getUploadedAttachments failed:",e);
			return "";
		}
	}
}
