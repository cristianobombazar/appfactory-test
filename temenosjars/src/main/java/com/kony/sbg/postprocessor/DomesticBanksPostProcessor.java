package com.kony.sbg.postprocessor;

import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class DomesticBanksPostProcessor implements ObjectServicePostProcessor {

    private static final Logger logger = LogManager.getLogger(DomesticBanksPostProcessor.class);

    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
            

        JsonObject jsonObject = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        JsonObject constructedNewJsonObj = new JsonObject();
        JsonObject firstCharJsonObject = new JsonObject();
        JsonObject bankJsonObject = new JsonObject();

        logger.debug("## DomesticBanksPostProcessor::entry:response: " + jsonObject); 

        try {
            if (!jsonObject.isJsonNull()) {
              
                JsonArray banks = jsonObject.getAsJsonArray("sbg_bankBranch");
                banks = sortJson(banks, "INSTITUTIONNAME");

                if (banks != null) {
                    for (int i = 0; i < banks.size(); i++) {
                        JsonElement jsonElement = banks.get(i);
                        JsonObject jsonAccountObj = (JsonObject) jsonElement;
                        String bankName = jsonAccountObj.get("INSTITUTIONNAME").getAsString();

                        String name = bankName.replace("-", " ");
                        if (bankName.contains("BANK")) {
                            name = bankName.substring(0, bankName.indexOf("BANK") + 4);
                        } else if (bankName.contains(" ")) {
                            name = bankName.substring(0, bankName.indexOf(" "));
                        }

                        if (bankJsonObject.get(name.trim()) == null) {
                            bankJsonObject.add(name.trim(), jsonAccountObj);
                    
                            Character firstChar = name.charAt(0);
                            JsonObject obj = new JsonObject();
                            obj.addProperty("BANKNAME", name.trim());
                            obj.addProperty("COUNTRYNAME", jsonAccountObj.get("COUNTRYNAME").getAsString());
                            obj.addProperty("SWIFTCODE", jsonAccountObj.get("SWIFTCODE").getAsString());
                           

                            if (firstCharJsonObject.get(firstChar.toString()) != null) {
                                JsonArray group = firstCharJsonObject.get(firstChar.toString()).getAsJsonArray();
                                group.add(obj);
                            } else {
                                JsonArray array = new JsonArray();
                                array.add(obj);
                                firstCharJsonObject.add(firstChar.toString().toUpperCase(), array);                            
                            }                           
                        }
                    }

                    constructedNewJsonObj.addProperty("opstatus", jsonObject.get("opstatus").getAsInt());
                    constructedNewJsonObj.addProperty("httpStatusCode", jsonObject.get("httpStatusCode").getAsInt());
                    constructedNewJsonObj.add("banks", firstCharJsonObject);

                    fabricResponseManager.getPayloadHandler().updatePayloadAsJson(constructedNewJsonObj);
                } else {
                    logger.error("Exception occured in DomesticBanksPostProcessor: ");
                }
            }
        } catch (JSONException exception) {
            logger.error("Exception occured in DomesticBanksPostProcessor: result in json" + jsonObject);
            logger.error("Exception occured in DomesticBanksPostProcessor: ", exception);
        } catch (Exception exception) {
            logger.error("Exception occured in DomesticBanksPostProcessor: ", exception);
        }
    }

    public static JsonArray sortJson(JsonArray jsonArr, String type) {

        JsonArray sortedJsonArray = new JsonArray();

        List<JsonObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add((JsonObject) jsonArr.get(i));
        }
        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            // You can change "beneficiaryName" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "INSTITUTIONNAME";

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
