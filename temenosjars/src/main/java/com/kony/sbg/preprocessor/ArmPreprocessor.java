package com.kony.sbg.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ArmPreprocessor extends SbgBasePreProcessor{
	private static final Logger logger = Logger.getLogger(ArmPreprocessor.class);
	@Override
	public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
			DataControllerResponse response, Result result) throws Exception {
		try {
			String opName=request.getServicesManager().getOperationData().getOperationId();	
			logger.debug("Entry --> IvsArmAPIPreprocessor");
			Result result1 = SBGCommonUtils.cacheFetchPingToken("Authorization",request);
			String authVal = result1.getParamValueByName("Authorization");
			String clientID = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request);
			String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request);
			request.addRequestParam_("Authorization",authVal);
			request.addRequestParam_("X-IBM-Client-Id",clientID);
			request.addRequestParam_("X-IBM-Client-Secret",clientSecret);
			request.addRequestParam_("x-fapi-interaction-id",(String) SBGCommonUtils.generateRandomUUID());
			//logger.debug("IvsArmAPIPreprocessor::header: " + request.getHeaderMap());
			//adding ccn based on resident information
			logger.debug("IvsArmAPIPreprocessor::opName: " + opName);
			params.put("DateTime", java.time.LocalDateTime.now().toString());
			//params.put("CustomerClientNumber", "20686712");
				String RegulatorAuth =(String) params.get("RegulatorAuth");	
				String TotalFlowAmount =(String) params.get("TotalFlowAmount");	
				String InternalAuth = (String) params.get("InternalAuth");
				String RegulatorAuthDate =(String) params.get("RegulatorAuthDate");					
				String InternalAuthNumberDate = (String) params.get("InternalAuthDate");
				String Flow =(String) params.get("Flow");
				params.put("FlowAmount",TotalFlowAmount);
				logger.debug("IvsArmAPIPreprocessor::Flow: " + Flow);
				if(Flow.contains("OUT")) {
					params.put("Flow","Sell");
				}else if(Flow.contains("IN")) {
					params.put("Flow","Buy");
				}
				logger.debug("IvsArmAPIPreprocessor::RegulatorAuth: " + RegulatorAuth);
				logger.debug("IvsArmAPIPreprocessor::InternalAuth: " + InternalAuth);
				logger.debug("IvsArmAPIPreprocessor::AuthDate: " + RegulatorAuthDate);
				logger.debug("IvsArmAPIPreprocessor::InternalAuthNumberDate: " + InternalAuthNumberDate);
					String fieldvalue="";
					
				if (StringUtils.isNotBlank(RegulatorAuth) && StringUtils.isNotBlank(InternalAuth)) {
					fieldvalue="016"+RegulatorAuth;
					params.put("FieldValue",fieldvalue);
					logger.debug("IvsArmAPIPreprocessor::RegulatorAuth: "+StringUtils.isNotBlank(RegulatorAuth));
				} else if (StringUtils.isNotBlank(RegulatorAuth)) {
					fieldvalue="016"+RegulatorAuth;
					params.put("FieldValue", fieldvalue);
					logger.debug("IvsArmAPIPreprocessor::RegulatorAuth: 2"+StringUtils.isNotEmpty(RegulatorAuth));
				} else if (StringUtils.isNotBlank(InternalAuth)) {
					fieldvalue="016"+InternalAuth;
					params.put("FieldValue", fieldvalue);
			   }
				logger.debug("IvsArmAPIPreprocessor::fieldvalue: " + fieldvalue);
				//chedcking auth number
				if (StringUtils.isNotBlank(RegulatorAuthDate)) {
					fieldvalue=fieldvalue+RegulatorAuthDate;
					params.put("FieldValue", fieldvalue);
				} else if (StringUtils.isNotBlank(InternalAuthNumberDate)) {
					fieldvalue=fieldvalue+InternalAuthNumberDate;
					params.put("FieldValue", fieldvalue);
			   }
				logger.debug("IvsArmAPIPreprocessor::fieldvalue: " + fieldvalue);
			
		} catch (Exception e) {
			logger.error(" fetch token preprocessor fails :" + e.getMessage());
		}
		return true;
	}
}
