package com.kony.sbg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.sbg.javaservices.FetchPingToken;
import com.konylabs.middleware.controller.DataControllerRequest;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SbgApiCallUtil {
    private static final Logger LOGGER = Logger.getLogger(SbgApiCallUtil.class);
    public static final String CONTENT_STREAM_PAYLOAD_KEY = "contentStream";
    public static final String DOCUMENTS_PAYLOAD_KEY = "documents";
    public static final String FILENET_FAPI_INTERACTION_ID = "FILENET_FAPI_INTERACTION_ID";
    public static final String FILE_NAME = "fileName";
    public static final String MIME_TYPE = "mimeType";

    public static Map<String, String> callApiGetResult(DataControllerRequest request, Map<String, String> inputParams) throws Exception {
        Map<String, String> result = new HashMap<>();
        URL url = new URL(EnvironmentConfigurationsHandler.getValue("FILENET_UPLOAD_ENDPOINT"));//FILENET_UPLOAD_ENDPOINT
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Content-Type", "application/json");


        conn.setRequestProperty(SbgURLConstants.X_IBM_CLIENT_ID_KEY, EnvironmentConfigurationsHandler.getValue(SbgURLConstants.X_IBM_CLIENT_ID));//X-IBM-CLIENT-ID
        conn.setRequestProperty(SbgURLConstants.X_IBM_CLIENT_SECRET_KEY, EnvironmentConfigurationsHandler.getValue(SbgURLConstants.X_IBM_CLIENT_SECRET)); //X-IBM-CLIENT-SECRET
        conn.setRequestProperty(SbgURLConstants.X_FAPI_INTERACTION_ID_KEY, EnvironmentConfigurationsHandler.getValue(FILENET_FAPI_INTERACTION_ID));//FILENET_FAPI_INTERACTION_ID


        String authToken = FetchPingToken.getB2BAccessToken(request);
        conn.setRequestProperty(SBGConstants.AUTHORIZATION, authToken);

        conn.setDoOutput(true);

        StringBuilder postData = new StringBuilder();

        postData.append(getFileUploadPayload(inputParams));
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        conn.getOutputStream().write(postDataBytes);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        while ((output = bufferedReader.readLine()) != null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> userData = mapper.readValue(
                    output, Map.class);

            setData(userData, result);
            return result;
        }

        conn.disconnect();
        return result;
    }

    private static void setData(Map<String, Object> userData, Map<String, String> result) {
        for (String name : userData.keySet()) {
            String key = name;
            Object value = userData.get(name);
            if (!DOCUMENTS_PAYLOAD_KEY.equalsIgnoreCase(key)) {
                result.put(key, SBGCommonUtils.getValueOrGivenValue(value, SBGConstants.EMPTY_STRING));
            } else {

                if (value instanceof HashMap) {
                    HashMap<String, String> map = (HashMap) value;
                    for (String val : map.keySet()) {
                        result.put(val, SBGCommonUtils.getValueOrGivenValue(map.get(val), SBGConstants.EMPTY_STRING));
                    }
                }
            }

        }
    }

    private static String getFileUploadPayload(Map<String, String> inputParams) throws Exception {

        String sourceRequestId = EnvironmentConfigurationsHandler.getValue("FILENET_FAPI_INTERACTION_ID");
        String channelId = EnvironmentConfigurationsHandler.getValue("FILENET_CHANNEL_ID");
        String channel = EnvironmentConfigurationsHandler.getValue("FILENET_CHANNEL_ID");
        String customerAccountNumber = inputParams.get("CustomerAccountNumber");
        String documentType = inputParams.get("DocumentType");
        String sourceSystem = EnvironmentConfigurationsHandler.getValue("FILENET_CHANNEL_ID");
        String documentReferenceNumber = inputParams.get("DocumentReferenceNumber");
        String fileName = inputParams.get(FILE_NAME);
        String documentTitle = inputParams.get("documentTitle");
        String mimeType = inputParams.get(MIME_TYPE);
        String objectStore = EnvironmentConfigurationsHandler.getValue("FILENET_OBJECT_STORE");
        String documentClassId = EnvironmentConfigurationsHandler.getValue("FILENET_DOCUMENT_CLASS_ID");

        String file = inputParams.get(CONTENT_STREAM_PAYLOAD_KEY).trim();


        String payloadString = "{\n" +
                "   \"sourceRequestId\": \"%s\",\n" +
                "   \"channelId\": \"%s\",\n" +
                "   \"documents\": {\n" +
                "      \"contentStream\": \"" + file + "\",\n" +
                "      \"createDocumentPropertyBo\": {\n" +
                "         \"additionalDocumentProperty\": {\n" +
                "            \"stringMap\": {\n" +
                "               \"channel\": \"%s\",\n" +
                "               \"customerAccountNumber\": \"%s\",\n" +
                "               \"documentType\": \"%s\",\n" +
                "               \"sourceSystem\": \"%s\",\n" +
                "               \"documentReferenceNumber\": \"%s\"\n" +
                "            }\n" +
                "         }\n" +
                "      },\n" +
                "      \"fileName\": \"%s\",\n" +
                "      \"documentTitle\": \"%s\",\n" +
                "      \"mimeType\": \"%s\",\n" +
                "      \"objectStore\": \"%s\",\n" +
                "      \"documentClassId\": \"%s\"\n" +
                "   }\n" +
                "}";


        return String.format(payloadString, sourceRequestId, channelId, channel, customerAccountNumber, documentType, sourceSystem, documentReferenceNumber, fileName, documentTitle, mimeType, objectStore, documentClassId);
    }
}
