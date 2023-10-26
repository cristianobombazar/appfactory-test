package com.kony.sbg.resources.impl;

import com.google.gson.Gson;
import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.sbg.resources.api.SubmitDocToFileNetResource;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgApiCallUtil;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubmitDocToFileNetResourceImpl implements SubmitDocToFileNetResource {
    private static final Logger LOGGER = Logger.getLogger(SubmitDocToFileNetResourceImpl.class);
    private static final int NUMBER_OF_ATTEMPS = 3;
    public static final String CHANNEL_ID = "channelId";
    public static final String HTTP_STATUS_CODE = "httpStatusCode";
    public static final String STATUS_DESCRIPTION = "statusDescription";
    public static final String FN_GUIDE = "fnGuide";
    public static final String DOCUMENT_IDS = "documentIds";
    public static final String DESCRIPTION = "description";
    public static final String DOCUMENT_ARRAY = "documentArray";
    public static final String FILE_UPLOAD_PROGRESS = "FileUploadProgress";
    public static final String DOCUMENT_DATA = "document_data";
    public static final String DOCUMENT_ID = "document_id";
    public static final String ERROR_500 = "500";
    public static final String ERROR_401 = "401";
    public static final String ERROR_408 = "408";
    public static final String STATUS_200 = "200";
    public static final String STATUS_400 = "400";
    public static final String STATUS_403 = "403";
    public static final String STATUS_404 = "404";
    public static final String STATUS__502 = "502";
    public static final String STATUS_503 = "503";
    public static final int DELAY_TIME_MILLISECONDS = 4000;

    private Map<String, String> metdataSaveList = new HashMap<String, String>();

    @Override
    public Result submitDocToFileNet(DataControllerRequest dataControllerRequest, Map<String, String> inputParams) {
        Result result = new Result();
        String httpStatusCode = null;
        String fileName = inputParams.get(SbgApiCallUtil.FILE_NAME);
        String fileType = inputParams.get(SbgApiCallUtil.MIME_TYPE);
        try {
            String documentId = "";
            String statusDescription = "";
            String channelId = "";
            String apiStatus = "";
            StringBuffer testMsg = new StringBuffer();
            for (int i = 1; i <= NUMBER_OF_ATTEMPS; i++) {
                if (SBGCommonUtils.isStringEmpty(httpStatusCode) || hasRetryCode(httpStatusCode)) {
                    if (i == NUMBER_OF_ATTEMPS && hasDelayCode(httpStatusCode)) {
                        //Thread.sleep(DELAY_TIME_MILLISECONDS);
                    }
                    /*Result fileSubmitResult = HelperMethods.callApi(dataControllerRequest, inputParams, HelperMethods.getHeaders(dataControllerRequest),
                            SbgURLConstants.SUBMIT_DOC_TO_FILENET);
                    Map<String, String> resultParams = getStringStringMap(fileSubmitResult);
                            */

                    Map<String, String> resultParams = SbgApiCallUtil.callApiGetResult(dataControllerRequest, inputParams);


                    apiStatus = resultParams.get(SBGConstants.STATUS);
                    httpStatusCode = resultParams.get(HTTP_STATUS_CODE);
                    statusDescription = resultParams.get(STATUS_DESCRIPTION);
                    channelId = resultParams.get(CHANNEL_ID);
                    String docId = resultParams.get(FN_GUIDE);
                    documentId = (SBGCommonUtils.isStringNotEmpty(docId)) ? docId.replace("{", "").replace("}", "") : docId;
                    if (SBGConstants.SUCCESS.equalsIgnoreCase(apiStatus) || STATUS_200.equals(httpStatusCode) || STATUS_400.equals(httpStatusCode)) {
                        //metdataSaveStatus = saveDocumentMetadata(dataControllerRequest, documentId, channelId);
                        metdataSaveList.put(documentId, channelId);
                        break;

                    }
                }
            }


            setResponseValues(result, fileName, fileType, apiStatus);
            result.addParam(CHANNEL_ID, channelId);

            result.addParam(DOCUMENT_IDS, documentId);
/*            String description = "Status code = " + SBGCommonUtils.getStringOrEmptyString(httpStatusCode) + " | Status Description = " + SBGCommonUtils.getStringOrEmptyString(statusDescription)
                    + " | Metadata save status = " + SBGCommonUtils.getStringOrEmptyString(metdataSaveStatus);*/
            String description = new StringBuffer("File = ").append(fileName).append(" | Status code = ").append(SBGCommonUtils.getStringOrEmptyString(httpStatusCode)).append(" | Status Description = ").append(SBGCommonUtils.getStringOrEmptyString(statusDescription)).toString();
            result.addParam(DESCRIPTION, description);

        } catch (Exception e) {
            setResponseValues(result, fileName, fileType, "fail");
            result.addParam(DESCRIPTION, new StringBuffer("File = ").append(fileName).append(" | Error = ").append(e.getMessage()).toString());
            LOGGER.error("SubmitDocToFileNetService error = " + e.getMessage());
            return result;
        }

        return result;
    }

    private void setResponseValues(Result result, String fileName, String fileType, String apiStatus) {
        result.addParam(SbgApiCallUtil.FILE_NAME, fileName);
        result.addParam(SbgApiCallUtil.MIME_TYPE, fileType);
        result.addParam(SBGConstants.STATUS, apiStatus);
    }

    private Map<String, String> getStringStringMap(Result fileSubmitResult) {
        Map<String, String> resultParams = new HashMap<String, String>();
        fileSubmitResult.getAllRecords().forEach(
                r ->
                {
                    r.getAllParams().forEach(
                            p -> {
                                resultParams.put(p.getName(), p.getValue());
                            }
                    );

                }
        );

        fileSubmitResult.getAllParams().forEach(
                p -> {
                    resultParams.put(p.getName(), p.getValue());
                }
        );
        return resultParams;
    }

    private boolean hasRetryCode(String httpStatusCode) {
        return ERROR_500.equals(httpStatusCode) || ERROR_401.equals(httpStatusCode) || ERROR_408.equals(httpStatusCode);
    }

    private boolean hasDelayCode(String httpStatusCode) {
        return STATUS_403.equals(httpStatusCode) || STATUS_404.equals(httpStatusCode) || STATUS__502.equals(httpStatusCode) || STATUS_503.equals(httpStatusCode);
    }

    private String saveDocumentMetadata(DataControllerRequest dataControllerRequest, String documentId, String channelId, String fileName) {
        try {
            HashMap<String, Object> requestHeaders = new HashMap<>();
            Map<String, Object> requestParam = new HashMap<>();
            requestParam.put(DOCUMENT_ID, documentId);
            requestParam.put(CHANNEL_ID, channelId);
            requestParam.put(SbgApiCallUtil.FILE_NAME, fileName);
            HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
            Result response = CommonUtils.callIntegrationService(dataControllerRequest, requestParam, svcHeaders, SbgURLConstants.SERVICE_DOCUMENT_STORAGE,
                    SbgURLConstants.DBXDB_SBG_DOCUMENTS_CREATE, false);
            if (null != response) {
                Dataset sbg_documents = response.getDatasetById("sbg_documents");
                if (null != sbg_documents) {
                    return "Metadata_save_status : Success : DocumentID = " + sbg_documents.getRecord(0).getParamValueByName(DOCUMENT_ID);
                }

            } else {
                return "Metadata_save_status : Not success";
            }
        } catch (Exception e) {
            LOGGER.error("saveDocumentMetadata error : " + e.getMessage());
            return "Metadata_save_status : Error : " + e.getMessage();
        }

        return "Metadata_save_status : Nothing";
    }

    @Override
    public Result submitDocsToFileNet(DataControllerRequest dataControllerRequest, Map<String, String> inputParams) {
        String docData = dataControllerRequest.getParameter(DOCUMENT_DATA);

        JSONObject documentDataStr = new JSONObject(docData);
        JSONArray documentArray = documentDataStr.getJSONArray(DOCUMENT_ARRAY);

        Result resultFinal = new Result();
        Dataset dataset = new Dataset(FILE_UPLOAD_PROGRESS);
        for (int i = 0; i < documentArray.length(); i++) {

            JSONObject document = documentArray.getJSONObject(i);
            Map<String, String> params = new Gson().fromJson(document.toString(), HashMap.class);
            String fileName = params.get(SbgApiCallUtil.FILE_NAME);

            Result result = submitDocToFileNet(dataControllerRequest, params);

            saveDocumentMetadata(dataControllerRequest,
                    result.getParamValueByName(DOCUMENT_IDS),
                    result.getParamValueByName(CHANNEL_ID),
                    fileName);

            dataset.addRecord(getAsRecord(result));
        }

        resultFinal.addDataset(dataset);
        return resultFinal;
    }

    private Record getAsRecord(Result result) {
        Record record = new Record();
        String fileName = result.getParamValueByName(SbgApiCallUtil.FILE_NAME);
        String mimeType = result.getParamValueByName(SbgApiCallUtil.MIME_TYPE);
        String description = result.getParamValueByName(DESCRIPTION);
        String status = result.getParamValueByName(SBGConstants.STATUS);
        String documentIds = result.getParamValueByName(DOCUMENT_IDS);
        record.addParam(SbgApiCallUtil.FILE_NAME, fileName);
        record.addParam(SbgApiCallUtil.MIME_TYPE, mimeType);
        record.addParam(DESCRIPTION, description);
        record.addParam(SBGConstants.STATUS, status);
        record.addParam(DOCUMENT_IDS, documentIds);

        return record;
    }
}