package com.kony.sbg.postprocessor;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegulatoryDataFilterPostProcessor implements DataPostProcessor2 {

    private static final Logger logger = LogManager.getLogger(RegulatoryDataFilterPostProcessor.class);

    @Override
    public Object execute( Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        String jsonDocStrResponse = ResultToJSON.convert(result);
        try {

            JSONObject jsonObj = new JSONObject(jsonDocStrResponse);

            String code = dataControllerRequest.getParameter("code");

            JSONObject constructedNewJsonObj = new JSONObject();
            JSONArray constructedNewJsonArray = new JSONArray();

            JSONArray accountArray = jsonObj.getJSONArray("Accounts");

            if(StringUtils.isNotBlank(code)) {

                for (int i = 0; i < accountArray.length(); i++) {
                    String codeInRsp = accountArray.getJSONObject(i).getString("code");
                    if (codeInRsp.equalsIgnoreCase(code)) {
                        constructedNewJsonArray.put(accountArray.get(i));
                    }
                }

                constructedNewJsonObj.put("AccountGroupCodes",constructedNewJsonArray);

            }else {
                constructedNewJsonObj.put("AccountGroupCodes",accountArray);
            }


            Result resultNewResponse = new Result();
            resultNewResponse.appendJson(constructedNewJsonObj.toString());

            logger.debug("#################### RegulatoryDataFilterPostProcessor : jsonObj: " + jsonDocStrResponse);
            return resultNewResponse;

        } catch (JSONException exception) {
            logger.error("JSONException occured in RegulatoryDataFilterPostProcessor: result in json" + jsonDocStrResponse);
            logger.error("JSONException occured in RegulatoryDataFilterPostProcessor: ", exception);
            return result;
        } catch (Exception exception) {
            logger.error("General Exception occured in RegulatoryDataFilterPostProcessor: ", exception);
            return result;
        }
    }
}
