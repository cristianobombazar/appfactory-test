package com.kony.sbg.postprocessor;

import com.kony.sbg.util.MimeType;
import com.kony.sbg.util.SBGCommonUtils;
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

import java.util.Arrays;

public class RetrieveDocumentPostProcessor implements DataPostProcessor2 {

    private static final Logger logger = LogManager.getLogger(RetrieveDocumentPostProcessor.class);

    @Override
    public Object execute(Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        logger.debug("#################### RetrieveDocumentPostProcessor : BEGIN");

        String jsonDocStrResponse = ResultToJSON.convert(result);
        try {
            if (StringUtils.isNotBlank(jsonDocStrResponse)) {
                JSONObject jsonObj = new JSONObject(jsonDocStrResponse);

                JSONArray documentArray = jsonObj.getJSONArray("documents");
                JSONObject constructedNewJsonObj = new JSONObject();

                int opstatus = (int) jsonObj.get("opstatus");
                int httpStatusCode = (int) jsonObj.get("httpStatusCode");

                if (documentArray != null) {

                    for (int i = 0; i < documentArray.length(); i++) {
                        constructedNewJsonObj.put("opstatus", opstatus);
                        constructedNewJsonObj.put("httpStatusCode", httpStatusCode);
                        constructedNewJsonObj.put("paymentId", documentArray.getJSONObject(i).getString("transactionId"));
                        constructedNewJsonObj.put("documentId", documentArray.getJSONObject(i).getString("id"));
                        String extension=documentArray.getJSONObject(i).getString("extension");
                        String mimeTypeDb = documentArray.getJSONObject(i).getString("mimeType");
                        String mimeTypeStr = null;
                        if (!SBGCommonUtils.isStringEmpty(mimeTypeDb)) {
                            mimeTypeStr = mimeTypeDb;
                        } else {
                            mimeTypeStr = Arrays.stream(MimeType.values())
                                    .filter(mimeType -> mimeType.name().equals(extension))
                                    .map(mimeType -> mimeType.mimeType)
                                    .findAny()
                                    .orElse("");
                        }
                        constructedNewJsonObj.put("mimeType", mimeTypeStr);
                        constructedNewJsonObj.put("documentName", documentArray.getJSONObject(i).getString("name"));
                        constructedNewJsonObj.put("documentSize", documentArray.getJSONObject(i).getString("size"));
                        constructedNewJsonObj.put("content", documentArray.getJSONObject(i).getString("contents"));

                    }
                } else {
                    logger.error("Exception occured in RetrieveDocumentPostProcessor: ");
                    result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
                    result.addParam("errMsg", "An unknown error occurred while processing the response.");
                    return result;
                }
                logger.debug("#################### RetrieveDocumentPostProcessor : constructedNewJsonObj: " + constructedNewJsonObj);

                Result resultNewResponse = new Result();
                resultNewResponse.appendJson(constructedNewJsonObj.toString());

                logger.debug("#################### RetrieveDocumentPostProcessor : jsonObj: " + jsonObj);
                return resultNewResponse;
            } else {
                logger.error("Exception occured in RetrieveDocumentPostProcessor: ");
                result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
                result.addParam("errMsg", "An unknown error occurred while processing the response.");
                return result;
            }

        } catch (JSONException exception) {
            logger.error("JSONException occured in RetrieveDocumentPostProcessor: result in json" + jsonDocStrResponse);
            logger.error("JSONException occured in RetrieveDocumentPostProcessor: ", exception);
            return result;
        } catch (Exception exception) {
            logger.error("General Exception occured in RetrieveDocumentPostProcessor: ", exception);
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
            return result;
        }
    }
}
