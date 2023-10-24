package com.kony.sbg.backend.impl;

import java.util.Map;

import com.kony.sbg.util.SBGConstants;
import org.apache.log4j.Logger;

import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.backend.api.RFQBackendDelegate;
import com.kony.sbg.business.impl.RFQBusinessDelegateImpl;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Result;

public class RFQBackendDelegateImpl implements RFQBackendDelegate{

	private static final Logger LOG = Logger.getLogger(RFQBusinessDelegateImpl.class);

	@Override
	public Result fetchQuotes(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest) throws Exception {
		String Url=SbgURLConstants.URL_FETCH_QUOTES;
		LOG.error(" ##Fetch Quote URL :"+Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.error(" ##Fetch Quote :"+result);
			LOG.debug("Backend.fetchQuotes ---> " + ResultToJSON.convert(result));
			
			String errorCode=result.getParamValueByName("errorCode");
			LOG.error("RFQBackendDelegateImpl.fetchQuotes ---> errorCode: "+errorCode);
			
			Result res = verifyAndFillError(errorCode);
			if(res != null) {
				return res;
			}
			
		}catch(Exception e) {
			LOG.error(" Fetch Service call fails");
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result acceptQuotes(Map<String, Object> inputParams,Map<String, String> headers, DataControllerRequest dcRequest) throws Exception {
		String Url=SbgURLConstants.URL_ACCEPT_QUOTES;
		LOG.error(" ##Accept Quote URL :"+Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.error(" ##Accept Quote :"+result);
		}catch(Exception e) {
			LOG.error(" AcceptCode Service call fails");
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result rejectQuote(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest) throws Exception {
		String Url=SbgURLConstants.URL_REJECT_QUOTES;
		LOG.error(" ##Reject Quote URL :"+Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.error(" ##Reject Quote :"+result);
		}catch(Exception e) {
			LOG.error(" Reject Service call fails");
			return SbgErrorCodeEnum.ERR_100021.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result fetchTradableAccounts(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest) throws Exception {
		String Url=SbgURLConstants.URL_GET_TRADABLE_ACCOUNTS;
		LOG.error(" ##Reject Quote URL :"+Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.debug("Backend.fetchTradableAccounts ---> " + ResultToJSON.convert(result));
			LOG.debug("Backend.fetchTradableAccounts ---> opstatus: " + result.getOpstatusParamValue());
			
			if(result == null || !result.getOpstatusParamValue().equals("0")) {
				LOG.debug("Backend.fetchTradableAccounts ---> RESULT IS NULL OR OPSTATUS IS NON-ZERO");
				return SbgErrorCodeEnum.ERR_100053.setErrorCode(new Result());
			}
			
			String httpStatusCode=result.getParamValueByName("httpStatusCode");
			if(Integer.parseInt(httpStatusCode) != 200) {
				LOG.debug("Backend.fetchTradableAccounts ---> HTTP STATUS CODE IS :: "+httpStatusCode);
				return SbgErrorCodeEnum.ERR_100053.setErrorCode(new Result());
			}
			
			String sbgErrCode = result.getParamValueByName("sbgerrcode");
			LOG.debug("Backend.fetchTradableAccounts ---> sbgErrCode :: "+sbgErrCode);
			
			Result res = verifyAndFillError(sbgErrCode);
			if(res != null) {
				return res;
			}
		}catch(Exception e) {
			LOG.error(" fetch account service call fails");
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result fetchPreBookedDeals(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest) throws Exception {
		String Url=SbgURLConstants.URL_PREBOOK_DEALS;
		LOG.debug(" ##fetchPreBookedDeals :"+Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.debug(" ##resultL :"+result);
			String errorCode=result.getParamValueByName("errorCode");

			if (result != null && result.hasParamByName("errorCode")) {
				if (errorCode.equals("0")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100075.setErrorCode(result2);
				} else if (errorCode.equals("1")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100076.setErrorCode(result2);
				} else if (errorCode.equals("3")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100077.setErrorCode(result2);
				} else if (errorCode.equals("7")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100087.setErrorCode(result2);
				} else if (errorCode.equals("17")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100078.setErrorCode(result2);
				} else if (errorCode.equals("19")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100079.setErrorCode(result2);
				} else if (errorCode.equals("31")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100088.setErrorCode(result2);
				} else if (errorCode.equals("38")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100089.setErrorCode(result2);
				} else if (errorCode.equals("39")) {
					Result result2 = new Result();
					return SbgErrorCodeEnum.ERR_100090.setErrorCode(result2);
				} else {
					Result result2 = new Result();
					String defaulterrorcode = SbgErrorCodeEnum.ERR_100080.getMessage() + SBGConstants.SPACE_STRING + errorCode + SBGConstants.FULL_STOP_STRING;
					return SbgErrorCodeEnum.ERR_100080.setErrorMessage(result2, defaulterrorcode);
				}
			}
		}catch(Exception e) {
			LOG.error(" fetchPreBookedDeals");
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}
		return result;
	}

	private	Result verifyAndFillError(String errorCode) {
		if(!SBGCommonUtils.isStringEmpty(errorCode)) {
			Result result2 = new Result();
			if(errorCode.equals("0")) {
				return SbgErrorCodeEnum.ERR_100063.setErrorCode(result2);
			}else if(errorCode.equals("1")){
				return SbgErrorCodeEnum.ERR_100064.setErrorCode(result2);
			}else if(errorCode.equals("2")){
				return SbgErrorCodeEnum.ERR_100065.setErrorCode(result2);
			}else if(errorCode.equals("3")){
				return SbgErrorCodeEnum.ERR_100066.setErrorCode(result2);
			}else if(errorCode.equals("7")){
				return SbgErrorCodeEnum.ERR_100067.setErrorCode(result2);
			}else if(errorCode.equals("9")){
				return SbgErrorCodeEnum.ERR_100068.setErrorCode(result2);
			}else if(errorCode.equals("10")){
				return SbgErrorCodeEnum.ERR_100069.setErrorCode(result2);
			}else if(errorCode.equals("12")){
				return SbgErrorCodeEnum.ERR_100070.setErrorCode(result2);
			}else if(errorCode.equals("16")){
				return SbgErrorCodeEnum.ERR_100071.setErrorCode(result2);
			}else if(errorCode.equals("17")){
				return SbgErrorCodeEnum.ERR_100082.setErrorCode(result2);
			}else if(errorCode.equals("19")){
				return SbgErrorCodeEnum.ERR_100072.setErrorCode(result2);
			}else if(errorCode.equals("29")){
				return SbgErrorCodeEnum.ERR_100073.setErrorCode(result2);
			}else {
				return SbgErrorCodeEnum.ERR_100074.setErrorCode(result2);
			}
		}
		return null;
	}	
}