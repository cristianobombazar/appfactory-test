package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.backend.api.RefDataBackendDelegateInterBank;
import com.kony.sbg.dto.InterBankFundTransferRefDataDTO;
import com.kony.sbg.util.ObjectConverter;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class RefDataBackendDelegateInterBankImpl implements RefDataBackendDelegateInterBank {
	private static final Logger LOG = LogManager.getLogger(RefDataBackendDelegateImpl.class);

	@Override
	public InterBankFundTransferRefDataDTO insertAdditionalFields(String refID,
	InterBankFundTransferRefDataDTO interBankFundTransferRefDataDTO) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSREFDATA_CREATE;
		String createResponse = null;
		Map<String, Object> requestParameters;
		requestParameters = ObjectConverter.convertTransferObject2Map(interBankFundTransferRefDataDTO);
		LOG.debug("RefDataBackendDelegateImpl::insertAdditionalFields::RequestParameters: " + requestParameters.toString());
		try {
			createResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSREFDATA_CREATE + ":: "
					+ createResponse);
			JSONObject response = new JSONObject(createResponse);
			JSONArray resposneArray = CommonUtils.getFirstOccuringArray(response);
			interBankFundTransferRefDataDTO = JSONUtils.parse(resposneArray.getJSONObject(0).toString(),
			InterBankFundTransferRefDataDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create interbankfund transaction entry into interbankfundtransfers table: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create interbankfund transaction entry: ", e);
			return null;
		}
		return interBankFundTransferRefDataDTO;
	}

	@Override
	public Result getRFQDetails(String filter, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET;
	
		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		//String filter = "";
		svcParams.put("$filter", filter);
		//requestParameters = ObjectConverter.convertTransferObject2Map(interbankFundTransferRefDatadto);
		LOG.debug("getRFQDetails()========:filter "+filter );
		try {
			if (!SBGCommonUtils.isStringEmpty(filter)) {
				result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, serviceName,
						operationName, false);
			}
			
			LOG.debug("getRFQDetails()========:Result" + result.getDatasetById("interbankfundtransfersRefData").toString());

		} catch (Exception e) {
			LOG.error("Failed to fetch details from interbankfundTransferRefData table:", e);
			return null;
		}
		return result;
	}
	@Override
	public Result getApprovalAndRequestDetails(String filter, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET;
	
		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		//String filter = "";
		svcParams.put("$filter", filter);
		//requestParameters = ObjectConverter.convertTransferObject2Map(interbankFundTransferRefDatadto);
		LOG.debug("getApprovalAndRequestDetails()========:filter "+filter );
		try {
			if (!SBGCommonUtils.isStringEmpty(filter)) {
				result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, serviceName,
						operationName, false);

			}
			LOG.debug("getApprovalAndRequestDetails()========:Result" + result.getDatasetById("interbankfundtransfers").toString());

		} catch (Exception e) {
			LOG.error("Failed to fetch details from interbankfundTransfer table:", e);
			return null;
		}
		return result;
	}

}
