package com.kony.sbg.resources.impl;

import java.util.Map;

import com.kony.sbg.resources.api.PublicHolidayResource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.business.api.PublicHolidayBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PublicHolidayResourceImpl implements PublicHolidayResource {

    private static final Logger LOG = Logger.getLogger(PublicHolidayResourceImpl.class);

    @Override
    public Result getPublicHolidays(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        JSONObject requestPayload = new JSONObject();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

        try {
            String country = inputParams.get("countryCode") != null
                    ? inputParams.get("countryCode").toString()
                    : SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE, dcRequest);

            LOG.debug("PublicHolidayResourceImpl:getPublicHolidays->country = " + country);

            requestPayload.put("Country", country);

            PublicHolidayBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(PublicHolidayBusinessDelegate.class);

            result = businessDelegate.getPublicHolidays(requestPayload, dcRequest);
        } catch (Exception e) {
            LOG.error("Error in PublicHolidayResourceImpl: " + e.getMessage());
            return SbgErrorCodeEnum.ERR_100085.setErrorCode(result);
        }

        return result;
    }

}
