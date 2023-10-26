package com.kony.sbg.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.backend.api.ManageBeneficiaryBackendDelegate;
import com.kony.sbg.business.api.ManageBeneficiaryBusinessDelegate;
import com.kony.sbg.fileutil.GenerateBeneficiary;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class ManageBeneficiaryBusinessDelegateImpl implements ManageBeneficiaryBusinessDelegate {

	private static final Logger logger = Logger.getLogger(ManageBeneficiaryBusinessDelegateImpl.class);

	@Override
	public Result checkBeneCodeExists(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws ApplicationException, Exception {
		Result result = new Result();
		List<String> payeeIds = new ArrayList<String>();

		try {
			JSONObject payees = getPayeeIdsforCif(requestPayload.getString("corecustomerid"),
					requestPayload.getString("contractId"), false);
			JSONObject payeesDomestic = getPayeeIdsforCif(requestPayload.getString("corecustomerid"),
					requestPayload.getString("contractId"), true);

			if (payees != null && payees.has("internationalpayee")
					&& payees.getJSONArray("internationalpayee").length() > 0) {
				JSONArray internationalpayees = payees.getJSONArray("internationalpayee");
				payeeIds.addAll(StreamSupport.stream(internationalpayees.spliterator(), true)
						.map(item -> ((JSONObject) item).getString("payeeId")).collect(Collectors.toList()));
				logger.info("checkBeneCodeExists::List of payeeIds: " + payeeIds);
			}
			if (payeesDomestic != null && payeesDomestic.has("interbankpayee")
					&& payeesDomestic.getJSONArray("interbankpayee").length() > 0) {
				JSONArray interbankpayee = payeesDomestic.getJSONArray("interbankpayee");
				payeeIds.addAll(StreamSupport.stream(interbankpayee.spliterator(), true)
						.map(item -> ((JSONObject) item).getString("payeeId")).collect(Collectors.toList()));
				logger.info("checkBeneCodeExists::List of payeeIds: " + payeeIds);
			}

			/* First payeeId request for the given contractId */
			if (payeeIds.isEmpty()) {
				result.addParam("isBeneCodeExists", "false");
				return result;
			}

			JSONObject beneCodes = getExternalAccountsforIds(payeeIds);
			/* Minimum 1 payee should be there in external accounts */
			if (beneCodes != null && beneCodes.has("externalaccount")
					&& beneCodes.getJSONArray("externalaccount").length() > 0) {
				JSONArray externalaccounts = beneCodes.getJSONArray("externalaccount");
				long count = StreamSupport.stream(externalaccounts.spliterator(), true)
						.filter(item -> (((JSONObject) item).has("beneCode") && ((JSONObject) item)
								.getString("beneCode").equals(requestPayload.getString("benecode"))))
						.count();
				if (count > 0) {
					result.addParam("isBeneCodeExists", "true");
				} else {
					result.addParam("isBeneCodeExists", "false");
				}
			} else {
				logger.error("Failed to fetch response from fetchAllBeneCodesForPayees");
				return SbgErrorCodeEnum.ERR_100012.setErrorCode(new Result());
			}

		} catch (Exception exp) {
			logger.error("Error in CheckBeneCodeExistsResourceImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100009.setErrorCode(result);
		}
		return result;
	}

	protected JSONObject getPayeeIdsforCif(String cif, String contractId, boolean isDomestic) {
		HashMap<String, Object> requestParameters = new HashMap<String, Object>();
		JSONObject interPayeeRsp;
		String filter = "";
		filter = "cif" + DBPUtilitiesConstants.EQUAL + cif + DBPUtilitiesConstants.AND + "contractId"
				+ DBPUtilitiesConstants.EQUAL + contractId;
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			ManageBeneficiaryBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(ManageBeneficiaryBackendDelegate.class);
			interPayeeRsp = backendDelegate.fetchAllInternationalPayeesForCif(requestParameters, isDomestic);
		} catch (Exception exp) {
			logger.error("Error in getPayeeIdsforCif: " + exp.getMessage());
			return null;
		}
		return interPayeeRsp;
	}

	protected JSONObject getExternalAccountsforIds(List<String> payeeIds) {
		HashMap<String, Object> requestParameters = new HashMap<String, Object>();
		JSONObject externalAccounts;
		String filter = "";
		try {
			ManageBeneficiaryBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(ManageBeneficiaryBackendDelegate.class);
			if (payeeIds != null && payeeIds.size() > 0) {
				for (String payeeId : payeeIds) {
					if (filter.isEmpty())
						filter = "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
					else
						filter = filter + DBPUtilitiesConstants.OR + "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
				}
			}
			logger.info("##filter for external accounts:: " + filter);
			requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
			externalAccounts = backendDelegate.fetchAllBeneCodesForPayees(requestParameters);

		} catch (Exception exp) {
			logger.error("Error in getExternalAccountsforIds: " + exp.getMessage());
			return null;
		}
		return externalAccounts;
	}

	@Override
	public Result generateBeneficiaryDetails(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception {
		final int SIZE_OF_RANDOM_GENERATED_STRING = 10;
		Result result = new Result();
		List<String> payeeIds = null;
		byte[] bytes;

		try {
			String inputPayeeIds = inputParams.get("payeeIds");
			payeeIds = new ArrayList<String>();
			JSONArray jsonArr = new JSONArray(inputPayeeIds);
			Iterator<Object> iterator = jsonArr.iterator();
			while (iterator.hasNext()) {
				payeeIds.add(iterator.next().toString());
			}

			logger.info("##List of payeeIds" + payeeIds);

			if (payeeIds != null && payeeIds.size() > 0) {
				JSONObject externalAccounts = getExternalAccountsforIds(payeeIds);
				if (externalAccounts != null && externalAccounts.has("externalaccount")
						&& externalAccounts.getJSONArray("externalaccount").length() > 0) {
					String headers = inputParams.get("headers");
					List<String> headersList = new ArrayList<String>();
					JSONArray jsonArr1 = new JSONArray(headers);
					Iterator<Object> iterator1 = jsonArr1.iterator();
					while (iterator1.hasNext()) {
						headersList.add(iterator1.next().toString());
					}

					GenerateBeneficiary generator = new GenerateBeneficiary();
					bytes = generator.generateFile(externalAccounts.getJSONArray("externalaccount"), headersList,
							result);
					if (result.hasParamByName(SbgErrorCodeEnum.ERROR_CODE_KEY)) {
						logger.error("Received error response from generate beneficiary pdf");
						return result;
					} else {
						String fileId = HelperMethods.getUniqueNumericString(SIZE_OF_RANDOM_GENERATED_STRING);						
						MemoryManager.saveIntoCache(fileId, bytes, 120);						
						result.addParam("fileId", fileId, DBPUtilitiesConstants.STRING_TYPE);
						logger.info("##Generate Document Result : " + result);
					}

				} else {
					logger.error("Failed to fetch response from fetchAllBeneCodesForPayees");
					return SbgErrorCodeEnum.ERR_100012.setErrorCode(result);
				}
			}

		} catch (Exception exp) {
			logger.error("Error is generateBeneficiaryDetails business delegate: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100013.setErrorCode(result);
		}
		return result;
	}

}
