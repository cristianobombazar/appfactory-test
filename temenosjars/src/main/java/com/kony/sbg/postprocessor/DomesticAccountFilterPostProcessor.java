package com.kony.sbg.postprocessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.sbg.resources.impl.SbgAccountSortingResourceImpl;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DomesticAccountFilterPostProcessor implements ObjectServicePostProcessor {


    private static final Logger logger = LogManager.getLogger(DomesticAccountFilterPostProcessor.class);

    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        logger.debug("#################### DomesticAccountFilterPostProcessor : BEGIN");
        JsonObject jsonObject = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        JsonObject constructedNewJsonObj = new JsonObject();
        JsonArray constructedNewJsonArray = new JsonArray();
        JsonArray latestAccountJsonArray = new JsonArray();

        logger.debug("#################### DomesticAccountFilterPostProcessor : fabricResponseManager response:" + jsonObject);
//        logger.debug("\nAccount ID of JsonAccount Object:- "+accountID);
        try {
            if (!jsonObject.isJsonNull()) {
                JsonArray accountJsonArray = jsonObject.getAsJsonArray("Accounts");
                HashMap<String, LocalDateTime> map = new HashMap<>();
                logger.debug("#################### DomesticAccountFilterPostProcessor : Accounts response:" + accountJsonArray);

                if (accountJsonArray != null) {

                    for (int i = 0; i < accountJsonArray.size(); i++) {
                        JsonElement jsonElement = accountJsonArray.get(i);
                        JsonObject jsonAccountObj = (JsonObject) jsonElement;
                        String extensionData = jsonAccountObj.get("extensionData").getAsString();
                        String currencyCode = jsonAccountObj.get("currencyCode").getAsString();
                        String accountID = jsonAccountObj.get("accountID").getAsString();
                        JSONObject jsonExtDataObj = new JSONObject(extensionData);
                        String switCode = jsonExtDataObj.getString("swiftcode");
                        if (switCode.substring(4, 6).equalsIgnoreCase("ZA") && currencyCode.equalsIgnoreCase("ZAR")) {
                            SbgAccountSortingResourceImpl accountSortingResource = new SbgAccountSortingResourceImpl();
                            //Get the latest time record of Particular Account
                            String updatedTime = accountSortingResource.fetchAccountList(accountID);
                            if (updatedTime != null) {
                                //Add a new property to JsonObject updated time of particular account
                                jsonAccountObj.addProperty("updatedTime", updatedTime);
                            }
                            //Fill the map with AccountID and Update time
                            map.put(accountID, stringConvertToLocalDateTime(updatedTime));
//                            System.out.println("\nDisplaying Account Number:- " + accountID + "   Time:- " + updatedTime + "\n");
                            logger.debug("\nDisplaying Account Number:- " + accountID + "   Time:- " + updatedTime + "\n");
                            constructedNewJsonArray.add(jsonElement);
                        }

                    }
                    //Get Sorted Account List according to time they updated
                    List<String> latestAccountList = getTheLatestUpdatedAccountIDList(map);

                    //Final Sorted JsonArray.
                    latestAccountJsonArray = getTheJsonArrayOfSortedAccountList(latestAccountList, constructedNewJsonArray);

                } else {
                    logger.error("Account array is null in DomesticAccountFilterPostProcessor: ");
                }

                constructedNewJsonObj.addProperty("opstatus", jsonObject.get("opstatus").getAsInt());
                constructedNewJsonObj.addProperty("httpStatusCode", jsonObject.get("httpStatusCode").getAsInt());
                constructedNewJsonObj.add("Accounts", latestAccountJsonArray);

                logger.debug("#################### DomesticAccountFilterPostProcessor : constructedNewJsonObj: " + constructedNewJsonObj);

                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(constructedNewJsonObj);

            } else {
                logger.debug("initial response is null in DomesticAccountFilterPostProcessor: ");
            }
        } catch (JSONException exception) {
            logger.debug("Exception occured in DomesticAccountFilterPostProcessor: result in json" + jsonObject);
            logger.debug("Exception occured in DomesticAccountFilterPostProcessor: ", exception);
        } catch (Exception exception) {
            logger.debug("Exception occured in DomesticAccountFilterPostProcessor: ", exception);
        }

    }

    public static List<String> getTheLatestUpdatedAccountIDList(Map<String, LocalDateTime> map) {
        //Compare the updatedTime of all accountID and re-arrange the account list order.
        List<String> latestAccountList = new ArrayList<>();

        HashMap<String, LocalDateTime> temp = map.entrySet().stream().sorted((i1, i2)
                -> i2.getValue().compareTo(
                i1.getValue())).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));

        for (Map.Entry<String, LocalDateTime> en : temp.entrySet()) {
            latestAccountList.add(en.getKey());
        }


        return latestAccountList;
    }

    public static JsonArray getTheJsonArrayOfSortedAccountList(List<String> latestAccountList, JsonArray jsonArray) {
        //Sort the JsonArray according to sorted account list
        JsonArray constructedNewJsonArray = new JsonArray();
        for (int init = 0; init < latestAccountList.size(); init++) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement jsonElement = jsonArray.get(i);
                JsonObject jsonAccountObj = (JsonObject) jsonElement;
                String accountID = jsonAccountObj.get("accountID").getAsString();
                String updatedTime = jsonAccountObj.get("updatedTime").getAsString();
                if (latestAccountList.get(init).equals(accountID)) {
                    constructedNewJsonArray.add(jsonElement);
                    logger.debug("\nFinally Added Account Number:- " + accountID + " AND Time:- " + updatedTime);
                }
            }
        }
        return constructedNewJsonArray;
    }

    public LocalDateTime stringConvertToLocalDateTime(String date) {
        String substring = date.substring(0, date.length() - 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(substring, formatter);
        return localDateTime;
    }
}
