package com.kony.sbg.postprocessor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;

public class PlatformTandCAccessDeniedPostProcessor implements DataPostProcessor2 {

    private static final Logger LOG = LogManager.getLogger(PlatformTandCAccessDeniedPostProcessor.class);

    @Override
    public Object execute(Result result, DataControllerRequest dataControllerRequest,
            DataControllerResponse dataControllerResponse)
            throws Exception {
        String isDesignedPerson = dataControllerRequest.getParameter("isDesignedPerson");
        String isEBTCaccepted = dataControllerRequest.getParameter("isEBTCaccepted");
        String isEBTCacknowledged = dataControllerRequest.getParameter("isEBTCacknowledged");
        String username = dataControllerRequest.getParameter("username");
        String customerId = dataControllerRequest.getParameter("customerId");
        String DESGINDICATOR = dataControllerRequest.getParameter("DESGINDICATOR");
        JsonObject customParams = new JsonObject();
        String eventSubType = null;

        customParams.addProperty("DESGINDICATOR", DESGINDICATOR);
        customParams.addProperty("isDesignedPerson", isDesignedPerson);
        customParams.addProperty("isEBTCaccepted", isEBTCaccepted);
        customParams.addProperty("isEBTCacknowledged", isEBTCacknowledged);
        customParams.addProperty("username", username);
        customParams.addProperty("customerId", customerId);
        customParams.addProperty("DateAndTime",
                SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd HH:mm:ss").toString());

        try {
            if (SBGCommonUtils.isStringEmpty(isDesignedPerson) || SBGCommonUtils.isStringEmpty(DESGINDICATOR)) {

                if (SBGCommonUtils.isStringEmpty(isEBTCaccepted)
                        && SBGCommonUtils.isStringEmpty(isEBTCacknowledged)) {
                    eventSubType = "TERMS_AND_CONDITIONS_ACCESS_DENIED";
                    _OlbAuditLogsClass(dataControllerRequest, dataControllerResponse, result, "TERMS_AND_CONDITIONS",
                            eventSubType,
                            "services/data/v1/SbgEBTCObjects/operations/EBTCObject/getEBTCsData", "success",
                            customParams);
                }
            }
            return result;
        } catch (JSONException exception) {
            LOG.error("JSONException occured in PlatformTandCPostProcessor: ", exception);
            return result;
        } catch (Exception exception) {
            LOG.error("General Exception occured in PlatformTandCPostProcessor: ", exception);
            return result;
        }
    }

    public static void _OlbAuditLogsClass(DataControllerRequest request, DataControllerResponse response, Result result,
            String eventType, String eventSubType, String producer, String statusId, JsonObject customParams) {
        LOG.debug("entry ------------>_logSignatoryGroupStatus()");
        String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        customParams.addProperty("customerName", CustomerSession.getCustomerCompleteName(customer));
        if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
            return;

        EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusId, null,
                CustomerSession.getCustomerId(customer), null, customParams);
    }

}
