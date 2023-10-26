package com.kony.sbg.postprocessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

public class ExternalPayeesPostProcessor implements ObjectServicePostProcessor {

    private static final Logger logger = LogManager.getLogger(DomesticAccountFilterPostProcessor.class);

    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        JsonObject jsonObject = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        JsonObject constructedNewJsonObj = new JsonObject();
        JsonObject firstCharJsonObject = new JsonObject();

        logger.debug("# ExternalPayeesPostProcessor : fabricResponseManager response:" + jsonObject);

        try {
            if (!jsonObject.isJsonNull()) {
                JsonArray payeeArray = jsonObject.getAsJsonArray("ExternalAccounts");
                payeeArray = sortJson(payeeArray, "beneficiaryName");

                if (payeeArray != null) {
                    for (int i = 0; i < payeeArray.size(); i++) {
                        JsonElement jsonElement = payeeArray.get(i);
                        JsonObject jsonAccountObj = (JsonObject) jsonElement;
                        String beneficiaryName = jsonAccountObj.get("beneficiaryName").getAsString();
                        Character firstChar = beneficiaryName.charAt(0);

                        if (firstCharJsonObject.get(firstChar.toString()) != null) {
                            JsonArray group = firstCharJsonObject.get(firstChar.toString()).getAsJsonArray();
                            group.add(jsonElement);
                        } else {
                            JsonArray array = new JsonArray();
                            array.add(jsonElement);
                            firstCharJsonObject.add(firstChar.toString().toUpperCase(), array);                            
                        }
                    }
                } else {
                    logger.error("initial response is null in ExternalPayeesPostProcessor: ");
                }

                constructedNewJsonObj.addProperty("opstatus", jsonObject.get("opstatus").getAsInt());
                constructedNewJsonObj.addProperty("httpStatusCode", jsonObject.get("httpStatusCode").getAsInt());
                constructedNewJsonObj.add("ExternalAccounts", firstCharJsonObject);

                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(constructedNewJsonObj);
            }
        } catch (JSONException exception) {
            logger.error("Exception occured in ExternalPayeesPostProcessor: result in json" + jsonObject);
            logger.error("Exception occured in ExternalPayeesPostProcessor: ", exception);
        } catch (Exception exception) {
            logger.error("Exception occured in ExternalPayeesPostProcessor: ", exception);
        }
    }

    public static JsonArray sortJson(JsonArray jsonArr , String type) {
      
        JsonArray sortedJsonArray = new JsonArray();

        List<JsonObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add((JsonObject)jsonArr.get(i));
        }
        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            // You can change "beneficiaryName" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "beneficiaryName";

            @Override
            public int compare(JsonObject a, JsonObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME).getAsString();
                    valB = (String) b.get(KEY_NAME).getAsString();
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }

                return valA.compareTo(valB);
                // if you want to change the sort order, simply use the following:
                // return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

}