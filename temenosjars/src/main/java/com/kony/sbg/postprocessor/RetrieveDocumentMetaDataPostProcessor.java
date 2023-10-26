package com.kony.sbg.postprocessor;

import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RetrieveDocumentMetaDataPostProcessor implements DataPostProcessor2 {

    private static final Logger logger = LogManager.getLogger(RetrieveDocumentMetaDataPostProcessor.class);

    @Override
    public Object execute(Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        logger.debug("#################### RetrieveDocumentMetaDataPostProcessor : BEGIN");

        String rawResponse = ResultToJSON.convert(result);
        try {
            if (StringUtils.isNotBlank(rawResponse)) {
                JSONObject jsonObj = new JSONObject(rawResponse);

                JSONArray documentArray = jsonObj.getJSONArray("documents");

                JSONObject constructedNewJsonObj = new JSONObject();
                JSONArray constructedNewJsonArray = new JSONArray();
                String paymentId = null;

                if (documentArray != null) {

                    for (int i = 0; i < documentArray.length(); i++) {
                        JSONObject newJsonObject = new JSONObject();
                        String documentId = documentArray.getJSONObject(i).getString("id");
                        newJsonObject.put("documentId", documentId);

                        constructedNewJsonArray.put(newJsonObject);
                        if (i == documentArray.length() - 1) {
                            paymentId = documentArray.getJSONObject(i).getString("transactionId");
                        }
                    }

                } else {
                    logger.error("Exception occured in RetrieveDocumentPostProcessor: ");
                    result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
                    result.addParam("errMsg", "An unknown error occurred while processing the response.");
                    return result;
                }

                constructedNewJsonObj.put("opstatus", (int) jsonObj.get("opstatus"));
                constructedNewJsonObj.put("httpStatusCode", (int) jsonObj.get("httpStatusCode"));
                constructedNewJsonObj.put("documents", constructedNewJsonArray);
                constructedNewJsonObj.put("paymentId", paymentId);
                logger.debug("#################### RetrieveDocumentMetaDataPostProcessor : constructedNewJsonObj: " + constructedNewJsonObj);

                Result resultNewResponse = new Result();
                resultNewResponse.appendJson(constructedNewJsonObj.toString());

                logger.debug("#################### RetrieveDocumentMetaDataPostProcessor : jsonObj: " + jsonObj);
                return resultNewResponse;
            } else {
                logger.error("Exception occured in RetrieveDocumentPostProcessor: ");
                result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
                result.addParam("errMsg", "An unknown error occurred while processing the response.");
                return result;
            }
        } catch (JSONException exception) {
            logger.error("Exception occured in RetrieveDocumentPostProcessor: result in json" + rawResponse);
            logger.error("Exception occured in RetrieveDocumentPostProcessor: ", exception);
            return result;
        } catch (Exception exception) {
            logger.error("Exception occured in RetrieveDocumentPostProcessor: ", exception);
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
            return result;
        }

    }
}
