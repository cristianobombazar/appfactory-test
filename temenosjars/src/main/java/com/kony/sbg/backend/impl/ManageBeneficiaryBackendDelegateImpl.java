package com.kony.sbg.backend.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.backend.api.ManageBeneficiaryBackendDelegate;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

public class ManageBeneficiaryBackendDelegateImpl implements ManageBeneficiaryBackendDelegate {

	private static final Logger logger = Logger.getLogger(ManageBeneficiaryBackendDelegateImpl.class);

	@Override
	public JSONObject fetchAllInternationalPayeesForCif(HashMap<String, Object> requestParameters, boolean isDomestic) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = (isDomestic) ? OperationName.DB_INTERBANKPAYEE_GET : OperationName.DB_INTERNATIONALPAYEE_GET;

		String payeeResponse = null;
		JSONObject responseObj;

		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			responseObj = new JSONObject(payeeResponse);
			logger.info("##Response from fetchAllInternationalPayeesForCif: " + responseObj);
		} catch (JSONException e) {
			logger.error("Failed to fetch international payee Ids: " + e);
			return null;
		} catch (Exception e) {
			logger.error("Caught exception at fetch international payee Ids: " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject fetchAllBeneCodesForPayees(HashMap<String, Object> requestParameters) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_EXTERNALACCOUNT_GET;
		String payeesBeneCodes = null;
		JSONObject responseObj;

		try {
			payeesBeneCodes = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			responseObj = new JSONObject(payeesBeneCodes);
			logger.info("##Response from fetchAllBeneCodesForPayees: " + responseObj);
		} catch (JSONException e) {
			logger.error("Failed to fetch external account benecodes: " + e);
			return null;
		} catch (Exception e) {
			logger.error("Caught exception at fetch external account benecodes: " + e);
			return null;
		}

		return responseObj;
	}

}
