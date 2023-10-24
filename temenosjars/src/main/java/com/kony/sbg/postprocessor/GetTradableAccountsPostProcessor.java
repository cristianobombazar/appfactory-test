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

public class GetTradableAccountsPostProcessor implements DataPostProcessor2{
	private static final Logger logger = Logger.getLogger(GetTradableAccountsPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		logger.error("##post processor: ");
		String opName=request.getServicesManager().getOperationData().getOperationId();		
		result.removeDatasetById("instrumentation");
		Record rec= new Record();
		rec.addParam("name",request.getParameter("name"));
		rec.addParam("timestamp",request.getParameter("timestamp"));
		Record rec2= new Record();
		
		if(opName.contains("AcceptRFQ")) {			
			rec2.addParam("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.ACC_QUO_RES_NAME, request));	
		}else if(opName.contains("RejectRFQ")) {
			rec2.addParam("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.REJ_QUO_RES_NAME, request));	
		}else if(opName.contains("GetRFQ")) {		
			rec2.addParam("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.GET_QUO_RES_NAME, request));
		}else if(opName.contains("GetTradableAccounts")) {
			rec2.addParam("name",SBGCommonUtils.getServerPropertyValue(SbgURLConstants.TRD_ACC_RES_NAME, request));
		}
		//rec2.addParam("name",resName);
		rec2.addParam("timestamp",SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString());
		Dataset ds=new Dataset("instrumentation");
		
		ds.addRecord(rec);
		ds.addRecord(rec2);
		result.addDataset(ds);
		//result.addParam("instrumentation", jsonArray.toString());
		return result;
	}

}
