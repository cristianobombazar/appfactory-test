package com.kony.sbg.javaservices;

import com.kony.sbg.util.Base64StringDecode;
import com.kony.sbg.util.DetermineIfFilePasswordProtected;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

public class CheckPasswordProtectedFileService implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(CheckPasswordProtectedFileService.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        DetermineIfFilePasswordProtected base64StringDecode = new DetermineIfFilePasswordProtected();
        try {
            result = base64StringDecode.checkIfFilePasswordProtected(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception ex) {
            LOG.error("Exception occurred in CheckPasswordProtectedFileService: ", ex);
            LOG.error("Exception occurred in CheckPasswordProtectedFileService: " + ex.getStackTrace());
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
        }
        return result;
    }
}
