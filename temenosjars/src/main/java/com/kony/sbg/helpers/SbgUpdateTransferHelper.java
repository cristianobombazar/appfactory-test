package com.kony.sbg.helpers;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.ServiceId;

public class SbgUpdateTransferHelper {

	private static final Logger LOG = LogManager.getLogger(SbgUpdateTransferHelper.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Result result 	= new Result();
        Map inputParams = HelperMethods.getInputParamMap(inputArray);
        String tranxId	= (String) inputParams.get("tranxId");
        String status 	= (String) inputParams.get("tranxStatus");
        String volPayStatus 	= (String) inputParams.get("volPayStatus");
        LOG.debug("SbgUpdateTransferHelper.invoke() ---> id: "+tranxId+"; status: "+status);
        
		StringBuilder filter = new StringBuilder();
		HashMap<String, Object> requestParameters = new HashMap<>();
		
		filter.append("DomesticPaymentId").append(DBPUtilitiesConstants.EQUAL).append(tranxId);		
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter.toString());
		
		String serviceName 			= ServiceId.DBPRBLOCALSERVICEDB;
		String operationName_get 	= "dbxdb_transaction_get";
		
		try {
			String response_get = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName_get).
					withRequestParameters(requestParameters).
					build().getResponse();
			LOG.debug("SbgUpdateTransferHelper.invoke() ---> response_get: "+response_get);
			
			JSONArray responseArray_get = CommonUtils.getFirstOccuringArray(new JSONObject(response_get));
			LOG.debug("SbgUpdateTransferHelper.invoke() ---> responseArray_get: "+responseArray_get);
			
			JSONObject responseObj_get = new JSONObject();
			if(responseArray_get != null && responseArray_get.length() > 0)	{
				responseObj_get = responseArray_get.getJSONObject(0);
			}

			requestParameters = new HashMap<>();
			filter = new StringBuilder();
			String id	= responseObj_get.getString(DBPUtilitiesConstants.T_TRANS_ID);
			String statusCurrent	= responseObj_get.getString("statusDesc");
			LOG.debug("SbgUpdateTransferHelper.invoke() ---> id: "+id);
			
	        if(!SBGCommonUtils.isStringEmpty(id) && isNotWarehousedToACCP(volPayStatus, statusCurrent)) {
		        inputParams.put(DBPUtilitiesConstants.T_TRANS_ID, id);
		        inputParams.put(DBPUtilitiesConstants.STATUS_DESC,status);
		        
	            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
	                    URLConstants.ACCOUNT_TRANSACTION_UPDATE);
	            
	            LOG.debug("SbgUpdateTransferHelper.invoke() ---> result: "+result);
			} else {
				LOG.error("SbgUpdateTransferHelper.invoke() --->TRANSACTION RECORD NOT FOUND FOR " + tranxId);
			}
			
		} catch (JSONException e) {
			LOG.error("SbgUpdateTransferHelper.invoke() --->Exception caught at getBbRequest method: " + e.getMessage());
		} catch (Exception e) {
			LOG.error("SbgUpdateTransferHelper.invoke() --->Exception caught at getBbRequest method: " + e.getMessage());
		}
		
        LOG.debug("SbgUpdateTransferHelper.invoke() ---> END"); 
        return result;
    }

	private boolean isNotWarehousedToACCP(String volPayStatus, String statusCurrent) {

		if("ACCP".equals(volPayStatus) && "Warehoused".equalsIgnoreCase(statusCurrent)){
			return false;
		}

		return true;
	}
}
