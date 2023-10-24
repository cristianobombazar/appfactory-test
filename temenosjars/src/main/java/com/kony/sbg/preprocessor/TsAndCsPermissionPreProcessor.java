package com.kony.sbg.preprocessor;

import com.dbp.core.constants.DBPConstants;
import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.sbg.javaservices.FetchPingToken;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

public class TsAndCsPermissionPreProcessor extends SbgBasePreProcessor {
    private static final Logger logger = Logger.getLogger(TsAndCsPermissionPreProcessor.class);

    @Override
    public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
                           DataControllerResponse response, Result result) throws Exception {
        try {
            Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
            if (userPermissions.contains("TERMS_AND_CONDITIONS_ACCEPT") || userPermissions.contains("TERMS_AND_CONDITIONS_DENY")) {
                request.addRequestParam_("_isDesignedPerson", "ture");
                if (userPermissions.contains("TERMS_AND_CONDITIONS_ACCEPT")) {
                    String isAccepted = request.getParameter("isEBTCaccepted");
                    String isAcknoledged = request.getParameter("isEBTCacknowledged");

                    if (!isAccepted.equalsIgnoreCase("true") && !isAcknoledged.equalsIgnoreCase("true")) {
                        logger.debug("TsAndCsPermissionPreProcessor::execute ---> : input vales are incorrect :: Value: " + isAccepted+"/"+isAcknoledged);
                        result = SbgErrorCodeEnum.ERR_100086.setErrorCode(result);
                        result.addOpstatusParam(SbgErrorCodeEnum.ERR_100086.getErrorCode());
                        result.addParam("status", SBGConstants.FAILED);
                        result.addErrMsgParam(SbgErrorCodeEnum.ERR_100086.getErrorCode()+" - "+SbgErrorCodeEnum.ERR_100086.getMessage());
                        return false;
                    }

                } else if (userPermissions.contains("TERMS_AND_CONDITIONS_DENY")) {

                    String isAccepted = request.getParameter("isEBTCaccepted");
                    String isAcknoledged = request.getParameter("isEBTCacknowledged");

                    if (!isAccepted.equalsIgnoreCase("false") && !isAcknoledged.equalsIgnoreCase("false")) {
                        logger.debug("TsAndCsPermissionPreProcessor::execute ---> : input vales are incorrect :: Value: " + isAccepted+"/"+isAcknoledged);
                        result = SbgErrorCodeEnum.ERR_100086.setErrorCode(result);
                        result.addOpstatusParam(SbgErrorCodeEnum.ERR_100086.getErrorCode());
                        result.addParam("status", SBGConstants.FAILED);
                        result.addErrMsgParam(SbgErrorCodeEnum.ERR_100086.getErrorCode()+" - "+SbgErrorCodeEnum.ERR_100086.getMessage());
                        return false;
                    }
                }
            } else {
                request.addRequestParam_("_isDesignedPerson", "false");
            }

        } catch (Exception e) {
            logger.error("###### TsAndCsPermissionPreProcessor failed :", e);
        }
        return true;
    }
}