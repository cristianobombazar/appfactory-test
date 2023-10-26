package com.kony.sbg.javaservices;

import com.kony.sbg.resources.impl.SbgAccountSortingResourceImpl;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SbgAccountSortingOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(SbgAccountSortingOperation.class);

    @Override
    public Object invoke(String accountID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) {

        Result result = new Result();
        try {
            LOG.debug("Starter point of SbgAccountSortingOperation:");
            SbgAccountSortingResourceImpl accountSortingResource = new SbgAccountSortingResourceImpl();
            String lastUpdateTimeOfThisAccount = accountSortingResource.fetchAccountList(accountID);
            result.addParam("updateTime", lastUpdateTimeOfThisAccount);
            LOG.debug("End point of SbgAccountSortingOperation:");
        } catch (Exception e) {
            LOG.error("Caught exception at invoke of SbgAccountSortingOperation: ", e);
            return SbgErrorCodeEnum.ERR_100005.setErrorCode(new Result());
        }

        return result;
    }
}
