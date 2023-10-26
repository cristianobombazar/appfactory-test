package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.backend.api.RFQBackendDelegate;
import com.kony.sbg.business.api.RFQBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Result;

public class RFQBusinessDelegateImpl implements RFQBusinessDelegate{
	private static final Logger LOG = Logger.getLogger(RFQBusinessDelegateImpl.class);

	@Override
	public Result fetchQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {
		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			String accountId=inputParams.get("accountId");
			String dealtCurrency=inputParams.get("dealtCurrency");
			String contraCurrency=inputParams.get("contraCurrency");
			String dealtAmount=inputParams.get("dealtAmount");
			String valueDate=inputParams.get("valueDate");
			String swiftcode=inputParams.get("BankSWIFTCode");
			LOG.error(" ##swiftcode :"+swiftcode);
	        svcHeaders.put("x-country-code", inputParams.get("countryCode"));
	        svcInputParams.put("accountId",accountId);
	        svcInputParams.put("dealtCurrency",dealtCurrency);
	        svcInputParams.put("contraCurrency",contraCurrency);
	        svcInputParams.put("dealtAmount",dealtAmount);
	        svcInputParams.put("valueDate",valueDate);
	        svcInputParams.put("routingBankSWIFTCode",swiftcode);
	        svcInputParams.put("productType",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PROD_TYPE, dcRequest));
			//svcHeaders.put("x-country-code",inputParams.get("x-country-code"));
			JSONObject instObj=new JSONObject();
	        String ts=SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString();
			//String resName = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.RES_NAME, request);		
			instObj.put("name", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.GET_QUO_REQ_NAME,dcRequest));//RES_NAME
			instObj.put("timestamp", ts);
			dcRequest.addRequestParam_("instrumentation", "["+instObj.toString()+"]");
			dcRequest.addRequestParam_("name", SBGCommonUtils.getServerPropertyValue(SbgURLConstants.GET_QUO_REQ_NAME,dcRequest));
			dcRequest.addRequestParam_("timestamp",ts);
			svcInputParams.put("instrumentation", "["+instObj.toString()+"]");
			svcInputParams.put("dealtDirection",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.DEALTA_DIRECTION,dcRequest));
			

			RFQBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RFQBackendDelegate.class);
			result = backendDelegate.fetchQuotes(svcInputParams,svcHeaders, dcRequest);

		} catch (Exception exp) {
			LOG.error("Error in fetch quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100023.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result acceptQuotes(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {

		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			String fxQuote=inputParams.get("fxQuote");
			svcHeaders.put("x-country-code",inputParams.get("x-country-code"));
			JSONObject instObj=new JSONObject();
			instObj.put("name", SbgURLConstants.ACC_QUO_REQ_NAME);//RES_NAME
			instObj.put("timestamp", SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString());
			//inputParams.put("instrumentation", "["+instObj.toString()+"]");
			svcInputParams.put("fxQuote",fxQuote);
			svcInputParams.put("instrumentation", "["+instObj.toString()+"]");
			LOG.error("## fxQuote: " + fxQuote);

			RFQBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RFQBackendDelegate.class);
			result = backendDelegate.acceptQuotes(svcInputParams,svcHeaders, dcRequest);

		} catch (Exception exp) {
			LOG.error("Error in accept quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
		}

		return result;
	}
	@Override
	public Result rejectQuote(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {

		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			String fxQuote=inputParams.get("fxQuote");
			svcHeaders.put("x-country-code",inputParams.get("x-country-code"));
			String ts= SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString();
			dcRequest.addRequestParam_("name", "BOP+-RRFQ-REQ");
			dcRequest.addRequestParam_("timestamp",ts);
			svcInputParams.put("fxQuote",fxQuote);
			LOG.error("## fxQuote in reject quote method: " + fxQuote);

			RFQBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RFQBackendDelegate.class);
			result = backendDelegate.rejectQuote(svcInputParams,svcHeaders, dcRequest);

		} catch (Exception exp) {
			LOG.error("Error in reject quotes: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100021.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result fetchTradableAccounts(Map<String, String> inputParams, DataControllerRequest dcRequest)
			throws Exception {
		LOG.debug("Business.fetchTradableAccounts ---> START");
		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			svcHeaders.put("x-country-code",inputParams.get("countryCode"));			
			RFQBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RFQBackendDelegate.class);
			result = backendDelegate.fetchTradableAccounts(svcInputParams,svcHeaders, dcRequest);
			LOG.debug("Business.fetchTradableAccounts ---> " + ResultToJSON.convert(result));
		} catch (Exception exp) {
			LOG.error("Error in tradable accounts: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100022.setErrorCode(result);
		}

		return result;
	}
	
	@Override
	public Result fetchPreBookedDeals(Map<String, String> inputParams, DataControllerRequest dcRequest) throws Exception {
		 LOG.debug("fetchPreBookedDeals======svcInputParams");
		Result result = new Result();
		Map<String, String> svcHeaders = new HashMap<String, String>();
		Map<String, Object> svcInputParams = new HashMap<String, Object>();
		try {
			String fromDate=inputParams.get("fromDate");
			String toDate=inputParams.get("toDate");
			String includePeriod=inputParams.get("includePeriod");
			String clientCIF=inputParams.get("clientCIF");
			String debitCurrency=inputParams.get("debitCurrency");
			String creditCurrency=inputParams.get("creditCurrency");
			//logger.debug("GetTradableAccountsPreProcessor :: x-req-id= "+reqID+"; x-channel-id: "+channelID+"; x-req-timestamp: "+timestamp);
			
			
			//svcHeaders.put("timestamp",SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString());
		    svcInputParams.put("fromDate",fromDate);
	        svcInputParams.put("toDate",toDate);
	        svcInputParams.put("includePeriod",includePeriod);
	        svcInputParams.put("clientCIF",clientCIF);
	        svcInputParams.put("debitCurrency",debitCurrency);
	        svcInputParams.put("creditCurrency",creditCurrency);
	        LOG.debug("svcInputParams======svcInputParams"+svcInputParams.toString());
			RFQBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(RFQBackendDelegate.class);
			result = backendDelegate.fetchPreBookedDeals(svcInputParams,svcHeaders, dcRequest);
			  LOG.debug("svcInputParams======result"+result);
		} catch (Exception exp) {
			LOG.error("Error in fetchPreBookedDeals: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}

		return result;
	}
}
