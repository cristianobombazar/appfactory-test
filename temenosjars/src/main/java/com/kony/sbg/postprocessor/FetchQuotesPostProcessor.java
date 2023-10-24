package com.kony.sbg.postprocessor;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class FetchQuotesPostProcessor  implements DataPostProcessor2{
	private static final Logger logger = Logger.getLogger(GetTradableAccountsPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		logger.error("##post processor: ");
		
		result.removeDatasetById("instrumentation");
		Record rec= new Record();
		rec.addParam("name",request.getParameter("name"));
		rec.addParam("timestamp",request.getParameter("timestamp"));
		Record rec2= new Record();
		rec2.addParam("name","BOP+-RFQ-RES");
		rec2.addParam("timestamp",SBGCommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toString());
		Dataset ds=new Dataset("instrumentation");
		
		ds.addRecord(rec);
		ds.addRecord(rec2);
		result.addDataset(ds);
		//result.addParam("instrumentation", jsonArray.toString());
		return result;
	}
}
