package com.kony.sbg.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.helpers.SbgFetchTransfersHelper;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SbgGetTransfersOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(SbgGetTransfersOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result transactionResult = new Result();
        LOG.debug("SbgGetTransfersOperation.invoke() ---> START");
        
        //transactionResult = SbgFetchTransfersHelper.fetchBackendResponse(serviceName, request, serviceHeaders, params);
        transactionResult = (Result)new SbgFetchTransfersHelper().invoke(methodID, inputArray, request, response);
        if (transactionResult == null) {
            LOG.error("SbgGetTransfersOperation.invoke ---> FAILED TO FETCH TRANSACTIONS ");
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        return transactionResult;
    }

}
