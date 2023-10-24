package com.kony.sbg.resources.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.business.api.RFQBusinessDelegate;
import com.kony.sbg.resources.api.RFQResource;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;

public class RFQResourceImpl implements RFQResource{
	private static final Logger LOG = Logger.getLogger(RFQResourceImpl.class);

	@Override
	public Result fetchQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();

		try {
			RFQBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(RFQBusinessDelegate.class);
			result = businessDelegate.fetchQuotes(inputParams,dcRequest);
		} catch (Exception exp) {
			LOG.error("Error in get quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100023.setErrorCode(result);
		}

		return result;
	}
	
	@Override
	public Result acceptQuotes( Map<String, String> inputParams,DataControllerRequest dcRequest,DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();

		try {
			RFQBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(RFQBusinessDelegate.class);
			result = businessDelegate.acceptQuotes(inputParams,dcRequest);
			if(result!=null) {
				LOG.error("##audit logs");
			try {
				_logQuotes(dcRequest,dcResponse,inputParams, result,"ACCEPT_DEAL");
			} catch (Exception e) {
				LOG.error("Error occured while audit logging.", e);
			}
		} 
		}catch (Exception exp) {
			LOG.error("Error in accept quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
		}

		return result;
	}


	@Override
	public Result rejectQuote(Map<String, String> inputParams, DataControllerRequest dcRequest,DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();

		try {
			RFQBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(RFQBusinessDelegate.class);
			result = businessDelegate.rejectQuote(inputParams,dcRequest);
			if(result!=null) {
				LOG.error("##audit logs");
			try {
				_logQuotes(dcRequest,dcResponse,inputParams, result,"REJECT_DEAL");
			} catch (Exception e) {
				LOG.error("Error occured while audit logging.", e);
			}
		} 
		} catch (Exception exp) {
			LOG.error("Error in reject quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100021.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result fetchTradableAccounts(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception {
		Result result = new Result();

		try {
			LOG.debug("Resource.fetchTradableAccounts ---> START");
			RFQBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(RFQBusinessDelegate.class);
			result = businessDelegate.fetchTradableAccounts(inputParams,dcRequest);
			LOG.debug("Resource.fetchTradableAccounts ---> " + ResultToJSON.convert(result));
		} catch (Exception exp) {
			LOG.error("Error in fetch quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100022.setErrorCode(result);
		}

		return result;
	}

	private void _logQuotes(DataControllerRequest dcRequest,DataControllerResponse dcResponse ,Map<String, String> inputParams, Result result,String subtype) {
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", dcRequest);
		LOG.error("In_logQuotes");
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		String eventSubType="";
		String producer="";
		String status="";
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
			String eventType =SBGConstants.EVENT_TYPE;
			if("ACCEPT_DEAL".equals(subtype)) {
				eventSubType = SBGConstants.ACCEPT_EVENT_SUBTYPE;
				producer = SBGConstants.ACCEPT_PRODUCER;
				status="accepted";
			} else {
				eventSubType =SBGConstants.REJECT_EVENT_SUBTYPE;
				producer = SBGConstants.REJECT_PRODUCER;
				status="rejected";
			}
			
			LOG.error("# eventType"+eventType);
			
			
			if (dcRequest.containsKeyInRequest("validate")) {
				String validate = dcRequest.getParameter("validate");
				LOG.error("# validate"+validate);
				if (StringUtils.isNotBlank(validate) && validate.equalsIgnoreCase("true"))
					return;
			}

			JsonObject customParams = new JsonObject();
				List<Param> params = result.getAllParams();
			for (Param param : params) {
			if(params.contains("tradeReference")) {
				customParams.addProperty(param.getName(), param.getValue());
			}
			}

			LOG.error("# customParams"+customParams.toString());
			AdminUtil.addAdminUserNameRoleIfAvailable(customParams, dcRequest);
			LOG.error("# after admin customParams"+customParams.toString());
			EventsDispatcher.dispatch(dcRequest, dcResponse, eventType, eventSubType, producer,status, null,
					CustomerSession.getCustomerId(customer), null, customParams);
		} catch (Exception e) {
			LOG.error("Error while pushing to Audit Engine.");
		}
		
	}
	@Override
	public Result fetchPreBookedDeals(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();
		LOG.error("fetchPreBookedDeals: in Resource" );
		try {
			RFQBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(RFQBusinessDelegate.class);
			result = businessDelegate.fetchPreBookedDeals(inputParams,dcRequest);
		} catch (Exception exp) {
			LOG.error("Error in FetchPreBookedDeals: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}

		return result;
	}
}
