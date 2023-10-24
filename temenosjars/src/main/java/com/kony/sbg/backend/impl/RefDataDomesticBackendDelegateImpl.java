package com.kony.sbg.backend.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.backend.api.RefDataDomesticBackendDelegate;
import com.kony.sbg.dto.DomesticFundTransferRefDataDTO;
import com.kony.sbg.util.ObjectConverter;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RefDataDomesticBackendDelegateImpl implements RefDataDomesticBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(RefDataDomesticBackendDelegateImpl.class);

	@Override
	public DomesticFundTransferRefDataDTO insertAdditionalFields(String refID,
																 DomesticFundTransferRefDataDTO domesticFundTransferRefDatadto) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_DOMESTICFUNDTRANSFERSREFDATA_CREATE;
		String createResponse = null;
		Map<String, Object> requestParameters;
		requestParameters = ObjectConverter.convertTransferObject2Map(domesticFundTransferRefDatadto);
		LOG.debug("RefDataBackendDelegateImpl::insertAdditionalFields::RequestParameters: " + requestParameters.toString());
		try {
			createResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_DOMESTICFUNDTRANSFERSREFDATA_CREATE + ":: "
					+ createResponse);
			JSONObject response = new JSONObject(createResponse);
			JSONArray resposneArray = CommonUtils.getFirstOccuringArray(response);
			domesticFundTransferRefDatadto = JSONUtils.parse(resposneArray.getJSONObject(0).toString(),
					DomesticFundTransferRefDataDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create domesticfund transaction entry into domesticfundtransfers table: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create domesticfund transaction entry: ", e);
			return null;
		}
		return domesticFundTransferRefDatadto;
	}

	@Override
	public Result getRFQDetails(String filter, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_DOMESTICFUNDTRANSFERSREFDATA_GET;
	
		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		//String filter = "";
		svcParams.put("$filter", filter);
		//requestParameters = ObjectConverter.convertTransferObject2Map(domesticFundTransferRefDatadto);
		LOG.debug("getRFQDetails()========:filter "+filter );
		try {
			if (!SBGCommonUtils.isStringEmpty(filter)) {
				result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, serviceName,
						operationName, false);
			}
			
			LOG.debug("getRFQDetails()========:Result" + result.getDatasetById("domesticfundtransfersRefData").toString());

		} catch (Exception e) {
			LOG.error("Failed to fetch details from domesticfundTransferRefData table:", e);
			return null;
		}
		return result;
	}
	@Override
	public Result getApprovalAndRequestDetails(String filter, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_DOMESTICFUNDTRANSFERSRER_GET;
	
		Result result = new Result();
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParams = new HashMap<String, Object>();
		//String filter = "";
		svcParams.put("$filter", filter);
		//requestParameters = ObjectConverter.convertTransferObject2Map(domesticFundTransferRefDatadto);
		LOG.debug("getApprovalAndRequestDetails()========:filter "+filter );
		try {
			if (!SBGCommonUtils.isStringEmpty(filter)) {
				result = SBGCommonUtils.callIntegrationService(request, svcParams, svcHeaders, serviceName,
						operationName, false);

			}
			LOG.debug("getApprovalAndRequestDetails()========:Result" + result.getDatasetById("domesticfundtransfers").toString());

		} catch (Exception e) {
			LOG.error("Failed to fetch details from domesticfundTransfer table:", e);
			return null;
		}
		return result;
	}

}
