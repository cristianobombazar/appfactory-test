package com.kony.sbg.business.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.infinity.dbx.temenos.payeeservices.backenddelegate.api.impl.InternationalPayeeBackendDelegateImplExtn;
import com.kony.sbg.business.api.SbgInternationalPayeeBackendDelegate;
import com.kony.sbg.dto.SbgInternationalPayeeBackendDTO;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeBackendDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class SbgInternationalPayeeBackendDelegateImplExtn extends  InternationalPayeeBackendDelegateImplExtn {
	
	private static final Logger LOG = LogManager.getLogger(SbgInternationalPayeeBackendDelegateImplExtn.class);


	@Override
	public List<InternationalPayeeBackendDTO> fetchPayees(Set<String> payeeIds, Map<String, Object> headerParams, DataControllerRequest dcRequest) {

		LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn::fetchPayees");

		List<InternationalPayeeBackendDTO> backendPayeeDTOs = new ArrayList();
		String serviceName = "SbgBackendPayeeServicesOrch";
		String operationName = "sbgBackendInternationalPayeeGetOrch";
		Map<String, Object> requestParameters = new HashMap();
		requestParameters.put("id", String.join(",", payeeIds));
		requestParameters.put("loop_count", String.valueOf(payeeIds.size()));
		Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
		if (CustomerSession.IsBusinessUser(customer)) {
			serviceName = "SbgBackendPayeeServicesOrch";
			operationName = "sbgBackendInternationalPayeeGetOrch";
		}

		String payeeResponse = null;

		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId((String)null).withOperationId(operationName).withRequestParameters(requestParameters).withRequestHeaders(headerParams).withDataControllerRequest(dcRequest).build().getResponse();

			LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn::fetchPayees sbgBackendInternationalPayeeGetOrch response:"+payeeResponse);

			JSONObject responseObj = new JSONObject(payeeResponse);
			JSONArray records = responseObj.getJSONArray("LoopDataset");

			for(int i = 0; i < records.length(); ++i) {
				List<SbgInternationalPayeeBackendDTO> payeeDTOs = new ArrayList();
				responseObj = records.getJSONObject(i);
				if (responseObj.has("errmsg") || responseObj.has("dbpErrMsg") || responseObj.has("errorMessage")) {
					new SbgInternationalPayeeBackendDTO();
					SbgInternationalPayeeBackendDTO payee = (SbgInternationalPayeeBackendDTO)JSONUtils.parse(responseObj.toString(), SbgInternationalPayeeBackendDTO.class);
					backendPayeeDTOs.add(payee);
					return backendPayeeDTOs;
				}

				JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
				if (jsonArray != null) {
					payeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), SbgInternationalPayeeBackendDTO.class);
				}

				backendPayeeDTOs.addAll((Collection)payeeDTOs);
			}

			return backendPayeeDTOs;
		} catch (JSONException var15) {
			LOG.error("###### SbgInternationalPayeeBackendDelegateImplExtn::fetchPayees Failed to fetch international payees: " , var15);
			return null;
		} catch (Exception var16) {
			LOG.error("###### SbgInternationalPayeeBackendDelegateImplExtn::fetchPayees Caught exception at fetchPayeesFromBackend: " , var16);
			return null;
		}
	}

	@Override
	public InternationalPayeeBackendDTO editPayee(InternationalPayeeBackendDTO internationalPayeeBackendDTO,
												  Map<String, Object> headerParams, DataControllerRequest dcRequest) {
		LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn editPayee starting with 05");

		String serviceName = SbgURLConstants.SERVICE_INTERNATIONA_PAYEE;
		String operationName = SbgURLConstants.OPERATION_INTERNATIONA_PAYEE_EDIT;
		SbgInternationalPayeeBackendDTO sbgInternationalPayeeBackendDTO=null;
		//InternationalPayeeBackendDTO internationalPayeeBackendDTOReturned=null;
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(internationalPayeeBackendDTO).toString(), String.class, Object.class);
			LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn editPayee requestParameters:"+requestParameters);
			LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn editPayee 06");
		} catch (IOException e) {
			LOG.error("Error occured while fetching the request params: " + e);
			return null;
		}

		requestParameters.put("IBAN", requestParameters.get("iban"));
		requestParameters.put("Id", requestParameters.get("id"));

		String editResponse = null;
		try {
			editResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(headerParams).
					withDataControllerRequest(dcRequest).
					build().getResponse();

			LOG.debug("###### SbgInternationalPayeeBackendDelegateImplExtn editPayee rsp:"+editResponse);
			//JSONObject response = new JSONObject(editResponse).getJSONArray("externalaccount").getJSONObject(0);
			JSONObject response = new JSONObject(editResponse);
			sbgInternationalPayeeBackendDTO = JSONUtils.parse(response.toString(), SbgInternationalPayeeBackendDTO.class);
		}
		catch (JSONException e) {
			LOG.error("###### Failed to edit payee at payee table: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("######## Caught exception at editPayeeAtBackend: " , e);
			return null;
		}
		
		return sbgInternationalPayeeBackendDTO;
	}
}
	
