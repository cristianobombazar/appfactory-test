package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.resources.api.DocumentValidationResource;
import com.kony.sbg.resources.impl.DocumentValidationResourceImpl;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;
import org.json.JSONArray;

public class DocumentValidationService implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(DocumentValidationService.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {

        LOG.debug("#### Starting --> DocumentValidationService");

        JSONArray jsonArray = new JSONArray(inputArray);
        LOG.debug("#### Starting --> DocumentValidationService jsonArray:" + jsonArray);
        LOG.debug("#### Starting --> DocumentValidationService documentArray:" + dcRequest.getAttribute("documentArray"));
        Result result = new Result();
        try {
            DocumentValidationResource documentResource = new DocumentValidationResourceImpl();
//            DocumentValidationResource documentResource = DBPAPIAbstractFactoryImpl.getResource(DocumentValidationResource.class);
            result = documentResource.validateDocument(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception exp) {
            LOG.error("Exception occured in DocumentValidationService: ", exp);
            LOG.error("Exception occured in DocumentValidationService: " + exp.getStackTrace());
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
        }
        return result;
    }

}
