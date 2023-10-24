package com.kony.sbg.postprocessor;

import org.apache.log4j.Logger;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class BOPValidationsPostProcessor implements DataPostProcessor2{
	private static final Logger logger = Logger.getLogger(BOPValidationsPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		logger.debug("## BOPValidationsPostProcessor: ");
		String opName=request.getServicesManager().getOperationData().getOperationId();		
		
		String status=result.getParamValueByName("Status").toString();
		logger.debug("## status"+status);
		if (opName.contains("validCCN") || opName.contains("importUndertakingCCN") || opName.contains("libraLoanRef")) {
			if (status.contains("Pass")) {
				// result.removeParamByName("Status");
				result.addParam("Status", "PASS");
			} else {
				result.addParam("Status", "FAILURE");

			}
		}
		//rec2.addParam("name",resName);
		
		return result;
	}

}
