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

public class SubmitDocsToFileNetService implements JavaService2 {
    private static final Logger LOGGER = Logger.getLogger(SubmitDocsToFileNetService.class);


    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        try {
            Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
            SubmitDocToFileNetResource resource = DBPAPIAbstractFactoryImpl.getResource(SubmitDocToFileNetResource.class);
            Result result = resource.submitDocsToFileNet(dataControllerRequest, inputParams);
            return result;
        } catch (Exception e) {
            LOGGER.error("SubmitDocToFileNetService error = " + e.getMessage());
        }

        return null;
    }
}
