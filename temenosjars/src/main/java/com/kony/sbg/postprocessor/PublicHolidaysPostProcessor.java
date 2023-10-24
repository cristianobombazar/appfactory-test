package com.kony.sbg.postprocessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

public class PublicHolidaysPostProcessor implements ObjectServicePostProcessor {

    private static final Logger logger = LogManager.getLogger(DomesticBanksPostProcessor.class);

    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {

        JsonObject jsonObject = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        JsonObject constructedNewJsonObj = new JsonObject();
        JsonArray constructedNewJsonArray = new JsonArray();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        try {
            if (!jsonObject.isJsonNull()) {
                JsonArray holidays = jsonObject.getAsJsonArray("publicHolidaysList");

                if (holidays != null) {
                    for (int i = 0; i < holidays.size(); i++) {
                        JsonElement jsonElement = holidays.get(i);
                        JsonObject jsonHolidayObj = (JsonObject) jsonElement;
                        String holidayDate = jsonHolidayObj.get("HolidayDate").getAsString();
                        Date newDate = simpleDateFormat.parse(holidayDate);
                        String formattedDate = new SimpleDateFormat("dd-M-yyyy").format(newDate);
                        jsonHolidayObj.addProperty("HolidayDate", formattedDate);
                        constructedNewJsonArray.add(jsonHolidayObj);
                    }
                }

                JsonArray weekendArray = findWeekendsList();

                constructedNewJsonObj.addProperty("opstatus", jsonObject.get("opstatus").getAsInt());
                constructedNewJsonObj.addProperty("httpStatusCode", jsonObject.get("httpStatusCode").getAsInt());
                constructedNewJsonObj.add("publicHolidaysList", constructedNewJsonArray);
                constructedNewJsonObj.add("weekendList", weekendArray);

                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(constructedNewJsonObj);
            }

        } catch (JSONException exception) {
            logger.error("Exception occured in DomesticBanksPostProcessor: result in json" + jsonObject);
            logger.error("Exception occured in DomesticBanksPostProcessor: ", exception);
        } catch (Exception exception) {
            logger.error("Exception occured in DomesticBanksPostProcessor: ", exception);
        }
    }

    private static JsonArray findWeekendsList() {

        JsonArray weekendJsonArray = new JsonArray();
       
        Calendar calendar = null;
        calendar = Calendar.getInstance();
        // The while loop ensures that you are only checking dates in the current year
        while (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            // The switch checks the day of the week for Saturdays and Sundays
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                //case Calendar.SATURDAY:
                case Calendar.SUNDAY:
                    JsonObject jsonHolidayObj = new JsonObject();
                    String formattedDate = new SimpleDateFormat("dd-M-yyyy").format(calendar.getTime());
                    jsonHolidayObj.addProperty("WeekendDate", formattedDate);
                    weekendJsonArray.add(jsonHolidayObj);
                    // weekendList.add(calendar.getTime());
                    break;
            }
            // Increment the day of the year for the next iteration of the while loop
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return weekendJsonArray;
    }



}
