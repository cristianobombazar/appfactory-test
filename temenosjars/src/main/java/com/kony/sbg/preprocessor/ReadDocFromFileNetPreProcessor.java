package com.kony.sbg.preprocessor;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.sbg.javaservices.FetchPingToken;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class ReadDocFromFileNetPreProcessor extends SbgBasePreProcessor {
    private static final Logger logger = Logger.getLogger(ReadDocFromFileNetPreProcessor.class);

    @Override
    public boolean execute(@SuppressWarnings("rawtypes") HashMap params, DataControllerRequest request,
                           DataControllerResponse response, Result result) throws Exception {
        try {
            String authToken = FetchPingToken.getB2BAccessToken(request);
            request.addRequestParam_("Authorization", authToken);
            request.addRequestParam_("x-fapi-interaction-id", SBGCommonUtils.getServerPropertyValue("PING_CLIENT_ID", request));
            request.addRequestParam_("X-IBM-Client-Id", SBGCommonUtils.getServerPropertyValue("X-IBM-CLIENT-ID", request));
            request.addRequestParam_("X-IBM-Client-Secret", SBGCommonUtils.getServerPropertyValue("X-IBM-CLIENT-SECRET", request));


            // Set Filenet environment variables

            String sourceRequestId = EnvironmentConfigurationsHandler.getValue("FILENET_FAPI_INTERACTION_ID");
            String channelId = EnvironmentConfigurationsHandler.getValue("FILENET_CHANNEL_ID");
            String sourceSystem = EnvironmentConfigurationsHandler.getValue("FILENET_CHANNEL_ID");
            String objectStore = EnvironmentConfigurationsHandler.getValue("FILENET_OBJECT_STORE");


            params.put("sourceRequestId", sourceRequestId);
            params.put("channelId", channelId);
            params.put("browserType", "ie11");
            params.put("workStationId", "LSBSA-5NKZ9C3");
            params.put("noteOrReason", "View Document");
            params.put("isIncludeContent", "true");
            params.put("isIncludeDocumentProperty", "true");
            params.put("sourceSystem", sourceSystem);
            params.put("objectStore", objectStore);


        } catch (Exception e) {
            logger.error("###### ReadDocFromFileNetPreProcessor failed :", e);
        }
        return true;
    }
}