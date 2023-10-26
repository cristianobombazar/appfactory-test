package com.kony.sbg.javaservices;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class SbgGetInternationalExternalAccounts implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(SbgGetInternationalExternalAccounts.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
                         DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

        if (preProcess(inputParams, dcRequest, result)) {
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    SbgURLConstants.EXT_ACCOUNTS_GET);
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result) throws HttpCallException {
        HelperMethods.removeNullValues(inputParams);
        String userFilter = "";
        /* fetch from input params */
        String userId = inputParams.get(DBPUtilitiesConstants.CUSTOMERID);
        String companyId = inputParams.get(DBPUtilitiesConstants.COMPANYID);
        String payeeId = inputParams.get(DBPInputConstants.ID);
        /* if input params does not have this fetch from session */
        if(StringUtils.isBlank(userId))
            userId = HelperMethods.getUserIdFromSession(dcRequest);
        if(StringUtils.isBlank(companyId))
            companyId = HelperMethods.getOrganizationIDForUser(userId,dcRequest);

        if(StringUtils.isNotBlank(userId))
            userFilter = DBPUtilitiesConstants.P_USER_ID + DBPUtilitiesConstants.EQUAL + userId;
        else
            return false;/* if user id could not be fetched by any means return false */

        if(StringUtils.isNotBlank(companyId)) {
            if(StringUtils.isNotBlank(userFilter)) {
                userFilter = userFilter + DBPUtilitiesConstants.OR;
            }
            userFilter = userFilter + DBPUtilitiesConstants.P_ORGANIZATION_ID + DBPUtilitiesConstants.EQUAL + companyId;
        }

        userFilter = "(" + userFilter + ")";

        StringBuilder sb = new StringBuilder();
        if(StringUtils.isBlank(payeeId)) {
        	sb.append(userFilter);
        }
        else {
        	sb.append(DBPUtilitiesConstants.ID).append(DBPUtilitiesConstants.EQUAL).append(payeeId);
        }
        sb.append(DBPUtilitiesConstants.AND);
        sb.append(DBPUtilitiesConstants.SOFT_DEL).append(DBPUtilitiesConstants.EQUAL).append("0");
        sb.append(DBPUtilitiesConstants.AND);
        sb.append(DBPUtilitiesConstants.IS_INT_ACCNT).append(DBPUtilitiesConstants.EQUAL).append("1");
        inputParams.put(DBPUtilitiesConstants.FILTER, sb.toString());
        return true;
    }
}
