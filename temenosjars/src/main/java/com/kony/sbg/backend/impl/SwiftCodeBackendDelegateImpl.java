package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ConvertJsonToResult;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.SwiftCodeBackendDelegate;
import com.kony.sbg.dto.SwiftCodeDTO;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.dto.DBXResult;;

public class SwiftCodeBackendDelegateImpl implements SwiftCodeBackendDelegate {
	private static final Logger logger = Logger.getLogger(SwiftCodeBackendDelegateImpl.class);

	@Override
	public Result getSwiftCodeResponse(SwiftCodeDTO swiftCodeDTO, Map<String, Object> headerMap,
			DataControllerRequest dcRequest) {
		logger.debug("##### SwiftCodeBackendDelegateImpl In getSwiftCodeResponse");
		DBXResult dbxResult = new DBXResult();
		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		String filter = "";
		if (StringUtils.isNotBlank(swiftCodeDTO.getSwiftcode())) {
			filter = SBGCommonUtils.buildOdataCondition("SWIFTCODE", " eq ", swiftCodeDTO.getSwiftcode());
			try {
				if (!SBGCommonUtils.isStringEmpty(filter)) {
					svcParams.put("$filter", filter);
					result = SBGCommonUtils.callIntegrationService(dcRequest, svcParams, svcHeaders,
							SbgURLConstants.SERVICE_SBGCRUD,
							SbgURLConstants.OPERATION_DBXDB_SBG_SWIFTCODE_GET, false);
					dbxResult.setResponse(result);
					if (dbxResult != null && dbxResult.getResponse() != null) {
						JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
						result = ConvertJsonToResult.convert(jsonObject);
					}
				}
				logger.debug("@@@ backend result:::@@@" + result);
			} catch (Exception e) {
				logger.error("@@@ error getSwiftCodeResponse result:::@@@" + e);
			}
		} else {
			/*
			 * Modification done for the ODataQuery filter to handle LIKE operator based on
			 * Search functionality
			 */
			if (StringUtils.isNotBlank(swiftCodeDTO.getCountryname())
					&& StringUtils.isNotBlank(swiftCodeDTO.getBankname())) {
				if ("true".equalsIgnoreCase(swiftCodeDTO.getIsDomestic())) {

					// filter = SBGCommonUtils.buildOdataConditionForLikeOperator("COUNTRYNAME",
					// swiftCodeDTO.getCountryname());
					// filter = buildSearchGroupQuery(filter, " or ", SBGCommonUtils
					// .buildOdataCondition("COUNTRYCODE"," eq ",
					// getCountryCode(swiftCodeDTO.getCountryname())),
					// false);
					if ("domestic".equalsIgnoreCase(swiftCodeDTO.getBankname())) {
						// filter = SBGCommonUtils.buildOdataConditionForLikeOperator("INSTITUTIONNAME",
						// swiftCodeDTO.getBankname());
						// filter = buildSearchGroupQuery(filter, " and ", SBGCommonUtils
						// .buildOdataConditionForLikeOperator("COUNTRYNAME",
						// swiftCodeDTO.getCountryname()),
						// false);
						filter = SBGCommonUtils.buildOdataConditionForLikeOperator("COUNTRYNAME",
								swiftCodeDTO.getCountryname());
						filter = buildSearchGroupQuery(filter, " or ", SBGCommonUtils
								.buildOdataConditionForLikeOperator("COUNTRYCODE",
										getCountryCode(swiftCodeDTO.getCountryname())),
								false);
					} else {
						filter = SBGCommonUtils.buildOdataConditionForLikeOperator("INSTITUTIONNAME",
								swiftCodeDTO.getBankname());
						filter = buildSearchGroupQuery(filter, " and ", SBGCommonUtils
								.buildOdataConditionForLikeOperator("COUNTRYNAME", swiftCodeDTO.getCountryname()),
								false);
					}

					svcParams.put("$filter", filter);
					try {
						result = SBGCommonUtils.callIntegrationService(dcRequest, svcParams, svcHeaders,
								SbgURLConstants.SERVICE_SBGCRUD,
								SbgURLConstants.OPERATION_DBXDB_SBG_BANKBRANCH_GET, false);
						dbxResult.setResponse(result);
						if (dbxResult != null && dbxResult.getResponse() != null) {
							JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
							result = ConvertJsonToResult.convert(jsonObject);
						}

					} catch (Exception e) {
						logger.error("Domestic bank branch data failed to fetch");
					}

				} else {
					if (!SBGCommonUtils.isStringEmpty(filter)) {
						filter = buildSearchGroupQuery(filter, " and ", SBGCommonUtils
								.buildOdataConditionForLikeOperator("INSTITUTIONNAME", swiftCodeDTO.getBankname()),
								false);
						filter = buildSearchGroupQuery(filter, " and ", SBGCommonUtils
								.buildOdataConditionForLikeOperator("COUNTRYNAME", swiftCodeDTO.getCountryname()),
								false);
					} else {

						filter = SBGCommonUtils.buildOdataConditionForLikeOperator("INSTITUTIONNAME",
								swiftCodeDTO.getBankname());
						filter = buildSearchGroupQuery(filter, " and ", SBGCommonUtils
								.buildOdataConditionForLikeOperator("COUNTRYNAME", swiftCodeDTO.getCountryname()),
								false);
					}
					if (StringUtils.isNotBlank(swiftCodeDTO.getBranchname())) {
						filter = buildSearchGroupQuery(filter, " and ",
								SBGCommonUtils.buildOdataConditionForLikeOperator("BRANCHINFO",
										swiftCodeDTO.getBranchname()),
								false);
					}
					if (StringUtils.isNotBlank(swiftCodeDTO.getBranchname())) {
						filter = buildSearchGroupQuery(filter, " and ",
								SBGCommonUtils.buildOdataConditionForLikeOperator("LOCATION",
										swiftCodeDTO.getLocation()),
								false);
					}
					if (StringUtils.isNotBlank(swiftCodeDTO.getCityName())) {
						filter = buildSearchGroupQuery(filter, " and ",
								SBGCommonUtils.buildOdataConditionForLikeOperator("CITYNAME",
										swiftCodeDTO.getCityName().trim()),
								false);
					}
					svcParams.put("$filter", filter);
					try {
						if (!SBGCommonUtils.isStringEmpty(filter)) {
							result = SBGCommonUtils.callIntegrationService(dcRequest, svcParams, svcHeaders,
									SbgURLConstants.SERVICE_SBGCRUD,
									SbgURLConstants.OPERATION_DBXDB_SBG_SWIFTCODE_GET, false);
							dbxResult.setResponse(result);
							if (dbxResult != null && dbxResult.getResponse() != null) {
								JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
								result = ConvertJsonToResult.convert(jsonObject);
							}
						}
						logger.debug("@@@ backend result:::@@@" + result);
					} catch (Exception e) {
						// result.addParam("errmsg", "Error while getting swift details in backend");
					}
				}
			} else {
				result.addParam("errmsg", "bankname and country name are mandatory feilds");
			}

		}
		logger.debug("##### SwiftCodeBackendDelegateImpl filter:::" + filter);

		return result;
	}

	public static String buildSearchGroupQuery(String left, String operator, String right, Boolean group) {

		StringBuilder sb = new StringBuilder();
		if (group) {
			sb.append("(");
		}
		sb.append(left);
		sb.append(" ");
		sb.append(operator);
		sb.append(" ");
		sb.append(right);
		if (group) {
			sb.append(")");
		}
		return sb.toString();
	}

	private String getCountryCode(String beneficiarycountry) {
		String countryCode = "";
		String countryRsp = "";
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_COUNTRY_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "";
		filter = "Name " + DBPUtilitiesConstants.EQUAL + "'" + beneficiarycountry + "'";
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		logger.debug("PaymentAPIBackendDelegateImpl::requestParams:: " + requestParams);
		try {
			countryRsp = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			JSONObject respObj = new JSONObject(countryRsp);
			if (respObj.has("country")) {
				JSONArray countries = respObj.getJSONArray("country");
				if (countries.length() > 0) {
					JSONObject countryObj = countries.getJSONObject(0);
					if (countryObj.has("Code")) {
						countryCode = countryObj.getString("Code");
						logger.debug("PaymentAPIBackendDelegateImpl::getCountryCode_Value:: " + countryCode);
					}
				}
			}

		} catch (DBPApplicationException e) {
			logger.error("Error occured while fetching country: " + e.getStackTrace());
		}
		return countryCode;
	}

}
