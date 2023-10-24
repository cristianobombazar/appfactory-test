package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.product.usermanagement.businessdelegate.impl.CustomerActionsBusinessDelegateImpl;
public class CustomerActionsBusinessDelegateImplExtn extends CustomerActionsBusinessDelegateImpl{
	 @Override
	    public Map<String, Set<String>> getSecurityAttributes(String customerId, Map<String, Object> headersMap)
	            throws ApplicationException {
	        Map<String, Set<String>> securityAttributesMap = new HashMap<>();
	        final String DATASET_RECORDS1 = "records1";
	        final String ACTIONS = "actions";
	        final String FEATURES = "features";

	        Map<String, Object> inputParams = new HashMap<>();
	        try {
	            inputParams.put("_userId", customerId);

	            JsonObject securityAttributesJson = new JsonObject();
	            
	            /*if((boolean) headersMap.get(InfinityConstants.isProspectFlow)) {
	            	securityAttributesJson = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
	                        URLConstants.PROSPECT_SECURITYATTRIBUTES_GET_PROC);
	            }
	            else {
	            	securityAttributesJson = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
	                    URLConstants.USER_SECURITYATTRIBUTES_GET_PROC);
	            }*/
	            //SBG Fix for Prospect flow
	            securityAttributesJson = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
	                    URLConstants.USER_SECURITYATTRIBUTES_GET_PROC);
	            //
	            if (null != securityAttributesJson
	                    && JSONUtil.hasKey(securityAttributesJson, DBPDatasetConstants.DATASET_RECORDS)
	                    && securityAttributesJson.get(DBPDatasetConstants.DATASET_RECORDS).isJsonArray()
	                    && JSONUtil.hasKey(securityAttributesJson, DATASET_RECORDS1)
	                    && securityAttributesJson.get(DATASET_RECORDS1).isJsonArray()) {
	                JsonArray actionsArray =
	                        securityAttributesJson.get(DBPDatasetConstants.DATASET_RECORDS).getAsJsonArray();
	                JsonArray featuresArray = securityAttributesJson.get(DATASET_RECORDS1).getAsJsonArray();

	                JsonElement actionsElement =
	                        actionsArray.size() > 0 ? actionsArray.get(0) : new JsonObject();
	                JsonElement featuresElement =
	                        featuresArray.size() > 0 ? featuresArray.get(0) : new JsonObject();
	                if (actionsElement.isJsonObject()) {
	                    JsonObject object = actionsElement.getAsJsonObject();
	                    if (null != object && JSONUtil.hasKey(object, ACTIONS)) {

	                        securityAttributesMap.put(ACTIONS,
	                                HelperMethods.splitString(object.get(ACTIONS).getAsString(),
	                                        DBPUtilitiesConstants.COMMA_SEPERATOR));
	                    }
	                }

	                if (featuresElement.isJsonObject()) {
	                    JsonObject object = featuresElement.getAsJsonObject();
	                    if (null != object && JSONUtil.hasKey(object, FEATURES)) {

	                        securityAttributesMap.put(FEATURES,
	                                HelperMethods.splitString(object.get(FEATURES).getAsString(),
	                                        DBPUtilitiesConstants.COMMA_SEPERATOR));
	                    }
	                }
	            }

	        } catch (Exception e) {
	            throw new ApplicationException(ErrorCodeEnum.ERR_10391);
	        }
	        return securityAttributesMap;
	    }

}
