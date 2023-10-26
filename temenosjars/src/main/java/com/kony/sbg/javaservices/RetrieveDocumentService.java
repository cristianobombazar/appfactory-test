package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.constants.DBPConstants;
import com.kony.sbg.resources.api.PaymentFeedbackResource;
import com.kony.sbg.resources.api.RetrieveDocumentResource;
import com.kony.sbg.resources.impl.RetrieveDocumentResourceImpl;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;


public class RetrieveDocumentService implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(RetrieveDocumentService.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {

        LOG.debug("#### Starting --> RetrieveDocumentService");
        Result result = new Result();
        try {
            //RetrieveDocumentResource documentResource = DBPAPIAbstractFactoryImpl.getResource(RetrieveDocumentResource.class);
            RetrieveDocumentResourceImpl documentResource = new RetrieveDocumentResourceImpl();
            result = documentResource.readDocumentDetails(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception exp) {
            LOG.error("Exception occured in RetrieveDocumentService: ", exp);
            LOG.error("Exception occured in RetrieveDocumentService: " + exp.getStackTrace());
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
        }
        return result;
    }

}
