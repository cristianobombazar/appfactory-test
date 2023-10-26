package com.kony.sbg.resources.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalmatrixservices.resource.impl.ApprovalMatrixResourceImpl;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;

public class ApprovalMatrixResourceImplProExtn extends ApprovalMatrixResourceImpl {
	private static final Logger LOG = LogManager.getLogger(ApprovalMatrixResourceImplProExtn.class);

	/**
	 * method to disable ApprovalMatrix Details
	 * @param companyId 
	 * 
	 * @param DataControllerRequest
	 *            request
	 * @return Input JSON
	 */
	@Override
	public Result updateApprovalMatrixStatus(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		LOG.debug("entry ------------>updateApprovalMatrixStatus()");
		Result result = new Result();
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
		String disabled = inputParams.get("disable") != null ? inputParams.get("disable").toString() : null;
		if (StringUtils.isEmpty(disabled))
			return ErrorCodeEnum.ERR_29003.setErrorCode(new Result());
		if(disabled != null && "true".equals(disabled.toLowerCase())) {
			result=disableApprovalMatrix(methodID, inputArray, request, response);
			_logMatrixStatus(request,response,result);
		}else if(disabled != null && "false".equals(disabled.toLowerCase())) {
			result=enableApprovalMatrix(methodID, inputArray, request, response);
			_logMatrixStatus(request,response,result);
		}else {
			return ErrorCodeEnum.ERR_29004.setErrorCode(new Result());
		}
		return result;
	}
	
	public void _logMatrixStatus(DataControllerRequest request,DataControllerResponse response,Result result) {
		LOG.debug("entry ------------>_logMatrixStatus()");
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		JsonObject customParams = new JsonObject();
		String eventType = "APPROVAL_MATRIX";
		String eventSubType = "UPDATE_APPROVAL_MATRIX";
		String producer = "ApprovalMatrix/operations/ApprovalMatrix/updateApprovalMatrixStatus";
		String statusId = "SID_EVENT_FAILURE";
		
		EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer,statusId, null,
				CustomerSession.getCustomerId(customer), null, customParams);
	}
}

