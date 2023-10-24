package com.kony.sbg.postprocessor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.util.SBGConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;

public class EvaluationAPIPostprocessor implements DataPostProcessor2{
	private static final Logger LOG = Logger.getLogger(EvaluationAPIPostprocessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		LOG.debug("###EvaluationAPIPostprocessor==execute()==result"+result);
		Dataset ds=result.getDatasetById("evaluateDecision");
		_logEvaluationResult(request,response,result);
		return result;
		
	}
	private void _logEvaluationResult(DataControllerRequest dcRequest,DataControllerResponse dcResponse ,Result result) {
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", dcRequest);
		LOG.debug("_logEvaluationResult");
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		String eventSubType="";
		String producer="";
		String status="";
		//Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
		
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
			
			String eventType =SBGConstants.BOP_EVENT_TYPE;
			
				eventSubType =SBGConstants.BOP_EVENT_SUBTYPE;
				producer = SBGConstants.BOP_RESULT_PRODUCER;
				status="success";
			if (dcRequest.containsKeyInRequest("validate")) {
				String validate = dcRequest.getParameter("validate");
				LOG.debug("# validate"+validate);
				if (StringUtils.isNotBlank(validate) && validate.equalsIgnoreCase("true"))
					return;
			}

			JsonObject customParams = new JsonObject();
			Dataset ds=result.getDatasetById("evaluateDecision");
			List<Record> params =ds.getAllRecords();
			if(params.size()>0) {
				for(int i=0;i<params.size();i++) {
			List<Param> name =params.get(i).getAllParams();
		    for (Param rec : name) {
		    	customParams.addProperty(rec.getName(), rec.getValue());
		    	LOG.debug("# rec.getName()"+rec.getName());
				}
				}
			}
			customParams.addProperty("customerdetails", customer.toString());
			LOG.debug("# customParams"+customParams.toString());
			AdminUtil.addAdminUserNameRoleIfAvailable(customParams, dcRequest);
			LOG.debug("# after admin customParams"+customParams.toString());
			EventsDispatcher.dispatch(dcRequest, dcResponse, eventType, eventSubType, producer,status, null,
					CustomerSession.getCustomerId(customer), null, customParams);
			
		} catch (Exception e) {
			LOG.debug("Error while pushing to Audit Engine.");
		}
		
	}
}