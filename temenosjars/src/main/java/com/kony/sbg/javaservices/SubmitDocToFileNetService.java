package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.api.SubmitDocToFileNetResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.Map;

public class SubmitDocToFileNetService implements JavaService2 {
    private static final Logger LOGGER = Logger.getLogger(SubmitDocToFileNetService.class);


    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
/*            // take params

            // call service     HCLDocumentService/submitDocToFileNet
            Map<String, Object> params = new HashMap<>();
            boolean success = false;

            StringBuffer testMsg = new StringBuffer();
            for (int i = 0; i < NUMBER_OF_ATTEMPS; i++) {
                testMsg.append("For loop = " + i);
                Result fileSubmitResult = HelperMethods.callApi(dataControllerRequest, params, HelperMethods.getHeaders(dataControllerRequest),
                        SbgURLConstants.SUBMIT_DOC_TO_FILENET);


                String apiStatus = HelperMethods.getFieldValue(fileSubmitResult, SBGConstants.STATUS);
                if (SBGCommonUtils.isStringNotEmpty(apiStatus) && SBGConstants.SUCCESS.equalsIgnoreCase(apiStatus)) {
                    success = true;
                    testMsg.append("API call success  = " + i);
                    break;

                } else {
                    testMsg.append("API call fail = " + i);

                }
            }


            result.addParam(SBGConstants.STATUS, Boolean.toString(success));
            result.addParam("channelId", testMsg.toString());*/

            Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
            SubmitDocToFileNetResource resource = DBPAPIAbstractFactoryImpl.getResource(SubmitDocToFileNetResource.class);
            Result result = resource.submitDocToFileNet(dataControllerRequest, inputParams);
            return result;
        } catch (Exception e) {
            LOGGER.error("SubmitDocToFileNetService error = " + e.getMessage());
        }

        return null;
    }
}
