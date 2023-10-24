package com.kony.sbg.resources.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.sbg.resources.api.SbgAccountSortingResource;
import com.kony.sbg.util.SbgURLConstants;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SbgAccountSortingResourceImpl implements SbgAccountSortingResource {
    private static final Logger LOG = Logger.getLogger(SbgAccountSortingResourceImpl.class);

    @Override
    public String fetchAccountList(String accountID) {
        LOG.debug("\n### Starting ---> SbgAccountSortingResourceImpl::fetchAccountList()");
        String serviceName = SbgURLConstants.ACCOUNT_DB_SERVICE;
        String operationName = SbgURLConstants.ACCOUNT_LIST_SORT_BY_USER;
        String transactionDataResponse = null;
        String lastTimeOfUpdated = null;
        try {
            HashMap<String, Object> requestHeaders = new HashMap<>();
            Map<String, Object> requestParam = new HashMap<>();
            requestParam.put("accountList", accountID);

            transactionDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(requestParam)
                    .withRequestHeaders(requestHeaders).build().getResponse();

            JsonObject jsonObject = new JsonParser().parse(transactionDataResponse).getAsJsonObject();
            for (String keyStr : jsonObject.keySet()) {
                JsonElement keyvalue = jsonObject.get(keyStr);
                if (keyStr.equals("records")) {
                    JsonArray innerJsonArray = keyvalue.getAsJsonArray();
                    for (int i = 0; i < innerJsonArray.size(); i++) {
                        JsonObject innerJsonArrayToJsonObject = innerJsonArray.get(i).getAsJsonObject();
                        if (innerJsonArrayToJsonObject.get("lastmodifiedts") != null && innerJsonArrayToJsonObject.get("fromAccountNumber").getAsString().equals(accountID)) {
                            lastTimeOfUpdated = innerJsonArrayToJsonObject.get("lastmodifiedts").getAsString();
                            LOG.debug("\nAccount Number in SbgAccountSortingResourceImpl:- " + accountID + " AND Time:- " + lastTimeOfUpdated);
                        }
                    }

                }
            }

        } catch (Exception e) {
            LOG.debug("Enter to Exception of SbgAccountSortingResourceImpl of fetchAccountList", e);
        }

        return lastTimeOfUpdated;
    }

}
