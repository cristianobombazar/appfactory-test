package com.kony.sbg.resources.impl;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.api.DocumentValidationResource;
import com.kony.sbg.util.Base64StringDecode;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DocumentValidationResourceImpl implements DocumentValidationResource {

    private static final Logger LOG = Logger.getLogger(DocumentValidationResourceImpl.class);

    @Override
    public Result validateDocument(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws ApplicationException, HttpCallException, DBPApplicationException {
        LOG.debug("#### Starting --> DocumentValidationResourceImpl validateDocument");
        Result result = new Result();
        JSONObject jsonObject = new JSONObject();
        JSONArray validDocumentArray = new JSONArray();
        JSONArray inValidDocumentArray = new JSONArray();
        try {
            Map<String, String> documentMap = HelperMethods.getInputParamMap(inputArray);
            LOG.debug("#### DocumentValidationResourceImpl documentMap:" + documentMap);
            JSONObject documentDataStr = new JSONObject(documentMap.get("document_data"));
            LOG.debug("#### DocumentValidationResourceImpl documentDataStr" + documentDataStr);
            //JSONObject documentArrayFromData = documentDataStr.getJSONObject("document_data");
            JSONArray documentArray = documentDataStr.getJSONArray("documentArray");
            LOG.debug("### DocumentValidationResourceImpl documentJsonArray:" + documentArray);

            for (int i = 0; i < documentArray.length(); i++) {

                JSONObject constructedNewJsonObj = new JSONObject();
                String docName = documentArray.getJSONObject(i).getString("doc_name");
                String content = documentArray.getJSONObject(i).getString("content");
                String fileType = documentArray.getJSONObject(i).getString("file_type");

                constructedNewJsonObj.put("doc_name", docName);

                /*Map<String, String> aMalwareContainedDoc = isAMalwareContainedDoc(content);
                LOG.debug("### DocumentValidationResourceImpl isAMalwareContainedDoc response:" + aMalwareContainedDoc);
                if (aMalwareContainedDoc.get("hasmalware").equals("MALWARE_NOT_FOUND")) {
                    Base64StringDecode base64StringDecode = new Base64StringDecode();
                    boolean isPasswordProtected = base64StringDecode.isDocumentPasswordProtected(content, fileType);
                    LOG.debug("### DocumentValidationResourceImpl isDocumentPasswordProtected response:" + isPasswordProtected);
                    if (isPasswordProtected) {
                        constructedNewJsonObj.put("doc_status", "DOC_PASSWORD_PROTECTED");
                        inValidDocumentArray.put(constructedNewJsonObj);
                    } else {
                        constructedNewJsonObj.put("doc_status", "VALID_DOC");
                        validDocumentArray.put(constructedNewJsonObj);
                    }

                } else {
                    constructedNewJsonObj.put("doc_status", aMalwareContainedDoc.get("hasmalware"));
                    inValidDocumentArray.put(constructedNewJsonObj);
                }*/
                Base64StringDecode base64StringDecode = new Base64StringDecode();
                boolean isPasswordProtected = base64StringDecode.isDocumentPasswordProtected(content, fileType);
                LOG.debug("### DocumentValidationResourceImpl isDocumentPasswordProtected response:" + isPasswordProtected);
                if (!isPasswordProtected) {

                    Map<String, String> aMalwareContainedDoc = isAMalwareContainedDoc(content);
                    LOG.debug("### DocumentValidationResourceImpl isAMalwareContainedDoc response:" + aMalwareContainedDoc);
                    if (aMalwareContainedDoc.get("hasmalware").equals("MALWARE_NOT_FOUND")) {
                        constructedNewJsonObj.put("doc_status", "VALID_DOC");
                        validDocumentArray.put(constructedNewJsonObj);
                    } else {
                        constructedNewJsonObj.put("doc_status", aMalwareContainedDoc.get("hasmalware"));
                        inValidDocumentArray.put(constructedNewJsonObj);

                    }

                } else {
                    constructedNewJsonObj.put("doc_status", "DOC_PASSWORD_PROTECTED");
                    inValidDocumentArray.put(constructedNewJsonObj);

                }


            }

            jsonObject.put("valid_document_array", validDocumentArray);
            jsonObject.put("invalid_document_array", inValidDocumentArray);

            LOG.debug("### DocumentValidationResourceImpl final constructed json object:" + jsonObject);

            result.appendJson(String.valueOf(jsonObject));
        } catch (JSONException e) {
            LOG.debug("### exeception occured:", e);
        }

        return result;
    }

    private Map<String, String> isAMalwareContainedDoc(String content) throws DBPApplicationException {
        LOG.debug("### Starting ---> DocumentValidationResourceImpl::isAMalwareContainedDoc()");

        Map<String, String> malwareResult = new HashMap<>();

        String serviceName = SbgURLConstants.SERVICE_DOCUMENT_JSON_SERVICE;
        String operationName = SbgURLConstants.OPERATION_DOCUMENT_MALWARE_SCAN;

        Map<String, Object> input = new HashMap<>();
        input.put("stream", content);

        HashMap<String, Object> fundRequestHeaders = new HashMap<>();

        String documentMalwareApiResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                .withOperationId(operationName).withRequestParameters(input)
                .withRequestHeaders(fundRequestHeaders).build().getResponse();
        LOG.debug("### Starting ---> DocumentValidationResourceImpl::isAMalwareContainedDoc documentMalwareApiResponse:" + documentMalwareApiResponse);

        if (StringUtils.isNotBlank(documentMalwareApiResponse)) {
            JSONObject jsonObj = new JSONObject(documentMalwareApiResponse);
            boolean statusmg = Boolean.parseBoolean(jsonObj.getString("hasmalware"));

            if (statusmg) {
                malwareResult.put("hasmalware", "MALWARE_FOUND");
            } else {
                malwareResult.put("hasmalware", "MALWARE_NOT_FOUND");
            }
            LOG.debug("#################### DocumentValidationResourceImpl::isAMalwareContainedDoc : malwareResult: " + malwareResult);

        } else {
            malwareResult.put("hasmalware", "MALWARE_API_FAILED");
        }
        return malwareResult;
    }
}
