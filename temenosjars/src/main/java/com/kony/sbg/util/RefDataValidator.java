package com.kony.sbg.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class RefDataValidator {

	private static final Logger LOG = LogManager.getLogger(RefDataValidator.class);
	
	private	static final SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
	private	static final SimpleDateFormat dayFormat = new SimpleDateFormat(SBGConstants.DAY_FORMAT);
	
	public	static void main(String[] s) {
		System.setProperty("log4j.configurationFile","resources/log4console.xml");
		String refDataStr = "{\"currencyHolidaysList\":[{\"HolidayDescription\":\"NewYear'sDay\",\"HolidayDate\":\"2022-01-01\"},{\"HolidayDescription\":\"GoodFriday\",\"HolidayDate\":\"2022-04-15\"},{\"HolidayDescription\":\"EasterMonday\",\"HolidayDate\":\"2022-04-18\"},{\"HolidayDescription\":\"LabourDay\",\"HolidayDate\":\"2022-05-01\"},{\"HolidayDescription\":\"ChristmasDay\",\"HolidayDate\":\"2022-12-25\"},{\"HolidayDescription\":\"ChristmasHoliday\",\"HolidayDate\":\"2022-12-26\"},{\"HolidayDescription\":\"NewYear'sDay\",\"HolidayDate\":\"2023-01-01\"},{\"HolidayDescription\":\"GoodFriday\",\"HolidayDate\":\"2023-04-07\"},{\"HolidayDescription\":\"EasterMonday\",\"HolidayDate\":\"2023-04-10\"},{\"HolidayDescription\":\"LabourDay\",\"HolidayDate\":\"2023-05-01\"},{\"HolidayDescription\":\"ChristmasDay\",\"HolidayDate\":\"2023-12-25\"},{\"HolidayDescription\":\"ChristmasHoliday\",\"HolidayDate\":\"2023-12-26\"}],\"Monday\":\"WORKINGDAY:FULL\",\"opstatus_RefData-readPublicHolidays\":0,\"Saturday\":\"WEEKLYOFF\",\"publicHolidaysList\":[{\"HolidayDescription\":\"NewYear'sDay\",\"HolidayDate\":\"2022-01-01\"},{\"HolidayDescription\":\"HumanRightsDay\",\"HolidayDate\":\"2022-03-21\"},{\"HolidayDescription\":\"GoodFriday\",\"HolidayDate\":\"2022-04-15\"},{\"HolidayDescription\":\"FamilyDay(EasterMonday)\",\"HolidayDate\":\"2022-04-18\"},{\"HolidayDescription\":\"FreedomDay\",\"HolidayDate\":\"2022-04-27\"},{\"HolidayDescription\":\"Workers'DayOBS\",\"HolidayDate\":\"2022-05-02\"},{\"HolidayDescription\":\"YouthDay\",\"HolidayDate\":\"2022-06-16\"},{\"HolidayDescription\":\"NationalWomen'sDay\",\"HolidayDate\":\"2022-08-09\"},{\"HolidayDescription\":\"HeritageDay\",\"HolidayDate\":\"2022-09-24\"},{\"HolidayDescription\":\"DayofReconciliation\",\"HolidayDate\":\"2022-12-16\"},{\"HolidayDescription\":\"ChristmasDay\",\"HolidayDate\":\"2022-12-25\"},{\"HolidayDescription\":\"DayofGoodwill\",\"HolidayDate\":\"2022-12-26\"},{\"HolidayDescription\":\"DAYOFGOODWILL(ROLLON)\",\"HolidayDate\":\"2022-12-27\"},{\"HolidayDescription\":\"ChristmasHoliday\",\"HolidayDate\":\"2022-12-27\"},{\"HolidayDescription\":\"NewYear'sDayOBS\",\"HolidayDate\":\"2023-01-02\"},{\"HolidayDescription\":\"HumanRightsDay\",\"HolidayDate\":\"2023-03-21\"},{\"HolidayDescription\":\"GoodFriday\",\"HolidayDate\":\"2023-04-07\"},{\"HolidayDescription\":\"FamilyDay(EasterMonday)\",\"HolidayDate\":\"2023-04-10\"},{\"HolidayDescription\":\"FreedomDay\",\"HolidayDate\":\"2023-04-27\"},{\"HolidayDescription\":\"Workers'Day\",\"HolidayDate\":\"2023-05-01\"},{\"HolidayDescription\":\"YouthDay\",\"HolidayDate\":\"2023-06-16\"},{\"HolidayDescription\":\"NationalWomen'sDay\",\"HolidayDate\":\"2023-08-09\"},{\"HolidayDescription\":\"HeritageDayOBS\",\"HolidayDate\":\"2023-09-25\"},{\"HolidayDescription\":\"DayofReconciliation\",\"HolidayDate\":\"2023-12-16\"},{\"HolidayDescription\":\"ChristmasDay\",\"HolidayDate\":\"2023-12-25\"},{\"HolidayDescription\":\"DayofGoodwill\",\"HolidayDate\":\"2023-12-26\"}],\"Thursday\":\"WORKINGDAY:FULL\",\"Friday\":\"WORKINGDAY:FULL\",\"Sunday\":\"WEEKLYOFF\",\"Wednesday\":\"WORKINGDAY:FULL\",\"Days\":[{\"TimeRange\":{\"EndTime\":\"22:30:00\",\"StartTime\":\"00:00:00\"},\"TimeZone\":\"Africa/Harare\",\"LeadDays\":0,\"Country\":\"EUR\",\"Day\":\"MONDAY\"},{\"TimeRange\":{\"EndTime\":\"22:30:00\",\"StartTime\":\"00:00:00\"},\"TimeZone\":\"Africa/Harare\",\"LeadDays\":0,\"Country\":\"EUR\",\"Day\":\"TUESDAY\"},{\"TimeRange\":{\"EndTime\":\"22:30:00\",\"StartTime\":\"00:00:00\"},\"TimeZone\":\"Africa/Harare\",\"LeadDays\":0,\"Country\":\"EUR\",\"Day\":\"WEDNESDAY\"},{\"TimeRange\":{\"EndTime\":\"22:30:00\",\"StartTime\":\"00:00:00\"},\"TimeZone\":\"Africa/Harare\",\"LeadDays\":0,\"Country\":\"EUR\",\"Day\":\"THURSDAY\"},{\"TimeRange\":{\"EndTime\":\"22:30:00\",\"StartTime\":\"00:00:00\"},\"TimeZone\":\"Africa/Harare\",\"LeadDays\":0,\"Country\":\"EUR\",\"Day\":\"FRIDAY\"}],\"opstatus\":0,\"Tuesday\":\"WORKINGDAY:FULL\",\"opstatus_RefData-readBuisnessDays\":0,\"opstatus_RefData-readCurrencyHolidays\":0,\"opstatus_RefData-readCutoffTimes\":0,\"httpStatusCode\":200}";
		
		JSONObject refDataJson = new JSONObject(refDataStr);
		Date scheduledDate 	= null;
		Date tranxDate 		= null;
		try { 
			tranxDate 		= dateFormat.parse("2023-05-30");
			//tranxDate 		= new Date();
			scheduledDate 	= dateFormat.parse("2023-06-01");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		LOG.info("=========== START =========" );
//		JSONObject sbgValidationsResponseObj = new JSONObject();
//		LOG.info("Public Holiday Check: "+validatePublicHolidays(refDataJson, sbgValidationsResponseObj, dateFormat, scheduledDate));
//		LOG.info("Non Business Day Check: "+validateNonBusinessDays(refDataJson, sbgValidationsResponseObj, dateFormat, dayOfTheWeek));
//		LOG.info("Currency Holiday Check: "+validateCurrencyHolidays(refDataJson, sbgValidationsResponseObj, dateFormat, scheduledDate));
//		LOG.info("CutOff Time Check: "+validateCutOffTime(refDataJson, sbgValidationsResponseObj, dateFormat, scheduledDate, dayFormat, dayOfTheWeek));
//		LOG.info("Reference Data Check using validate: "+validateRefData(refDataJson, scheduledDate, dayOfTheWeek, "USD"));
		LOG.info("Reference Data Check using verify: "+verifyRefData(refDataJson, tranxDate, scheduledDate, "EUR"));
		LOG.info("============ END ==========" );
	}
	
	public	static JSONObject verifyRefData(JSONObject responseObj, Date tranxDate, Date valueDate, String tranxCurrency) {
		
		JSONObject sbgValidationsResponseObj = new JSONObject();
		 JSONObject cutOffTimeResponse = new JSONObject();

		LOG.info("RefDataValidator.verifyRefData :: BEFORE tranxDate: " + tranxDate + "; valueDate: "+valueDate);
		try {
			tranxDate 	= dateFormat.parse(dateFormat.format(tranxDate));
			valueDate 	= dateFormat.parse(dateFormat.format(valueDate));
		}catch(Exception e) {
			LOG.error("RefDataValidator.verifyRefData :: ERROR PARSING DATE ");
			return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_DATE_FORMAT);
		}
		LOG.info("RefDataValidator.verifyRefData :: AFTER tranxDate: " + tranxDate + "; valueDate: "+valueDate);
		
		JSONObject validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat, valueDate);
		if(!SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
			//First check: if the value date is falling on holiday
			LOG.info("RefDataValidator.verifyRefData :: VALUE DATE IS FALLING DURING HOLIDAY ");
			return validateHolidaysResponse;
		}
		//Verify the scenario  for current day with value date
		 if (tranxDate.equals(valueDate)) {
			 cutOffTimeResponse = validateCutOffTime(responseObj, sbgValidationsResponseObj, dateFormat, tranxDate, dayFormat, dayFormat
			          .format(tranxDate));
		     LOG.info("RefDataValidator.verifyRefData :: tranxDate is same as valuedate: ; cutOffTimeResponse: " + cutOffTimeResponse);
		     int leadDays = cutOffTimeResponse.getInt("LeadDays");
		      if (leadDays > 0) {
		        return setErrorResult(sbgValidationsResponseObj, "The selected value date is invalid for %CURRENCY_CODE%. Please select the next business day as your value date."
		            .replace("%CURRENCY_CODE%", tranxCurrency)); 
		      }
		      return cutOffTimeResponse;
		   }
		//Verify the scenario for next day (only) with value date
	        {
	           cutOffTimeResponse = validateCutOffTime(responseObj, sbgValidationsResponseObj, dateFormat,
	                    tranxDate, dayFormat, dayFormat.format(tranxDate));
	            int leadDays = cutOffTimeResponse.getInt("LeadDays");
	            if(leadDays > 0) {
	                Date checkDate = new Date(tranxDate.getTime() + (SBGConstants.MILLIS_IN_A_DAY));
	                if(checkDate.equals(valueDate)) {
	                    if(!SBGConstants.TRUE.equalsIgnoreCase(cutOffTimeResponse.optString(SBGConstants.IS_VALID))) {
	                        return setErrorResult(sbgValidationsResponseObj,
	                                SBGConstants.REFDATA_CUTOFF_TIME_ERROR_MESSAGE);
	                    }
	                }
	            }
	        }
		validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat, tranxDate);
		if (SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
			//check for cutoff
			Date checkDate = tranxDate;
			 cutOffTimeResponse = validateCutOffTime(responseObj, sbgValidationsResponseObj, dateFormat,
					tranxDate, dayFormat, dayFormat.format(tranxDate));
			LOG.info("RefDataValidator.verifyRefData :: tranxDate: "+tranxDate+"; cutOffTimeResponse: "+cutOffTimeResponse);
			
			if (SBGConstants.TRUE.equalsIgnoreCase(cutOffTimeResponse.optString(SBGConstants.IS_VALID))) {
				LOG.info("RefDataValidator.verifyRefData :: TRANX DATE IS SAME AS VALUE DATE ");
				return validateHolidaysResponse;
			} else {
				int leadDays = cutOffTimeResponse.getInt("LeadDays");
				checkDate = new Date(tranxDate.getTime() + (SBGConstants.MILLIS_IN_A_DAY));
				LOG.info("RefDataValidator.verifyRefData :: leadDays: "+leadDays+"; checkDate: "+checkDate);

				JSONObject nextWorkingDay = findNextWorkingDay(checkDate, valueDate, responseObj, sbgValidationsResponseObj, tranxCurrency, leadDays);
				LOG.info("RefDataValidator.verifyRefData :: nextWorkingDay "+nextWorkingDay);
				
				return nextWorkingDay;
			}
		} else {
			//do not check for cutoff if today is falling on holiday
			Date checkDate = tranxDate;
			checkDate = new Date(tranxDate.getTime() + (SBGConstants.MILLIS_IN_A_DAY * 1));
			LOG.info("RefDataValidator.verifyRefData :: checkDate: "+checkDate);
			
			JSONObject nextWorkingDay = findNextWorkingDay(checkDate, valueDate, responseObj, sbgValidationsResponseObj, tranxCurrency, 0);
			LOG.info("RefDataValidator.verifyRefData :: nextWorkingDay "+nextWorkingDay);
			
			return nextWorkingDay;
		}
	}
	
	private static JSONObject validateHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date date) {
		JSONObject publicHolidaysResponse = null;
		JSONObject nonBusinessDaysResponse = null;
		JSONObject currencyHolidaysResponse = null;
		String dayOfWeek = dayFormat.format(date);
		try {

			publicHolidaysResponse = validatePublicHolidays(responseObj, sbgValidationsResponseObj, dateFormat, date);
			LOG.info("RefDataValidator.validateHolidays :: date: "+date+"; publicHolidaysResponse: "+publicHolidaysResponse);
			
			if (SBGConstants.TRUE.equalsIgnoreCase(publicHolidaysResponse.optString(SBGConstants.IS_VALID))) {
				nonBusinessDaysResponse = validateNonBusinessDays(responseObj, sbgValidationsResponseObj, dateFormat, dayOfWeek);
				LOG.info("RefDataValidator.validateHolidays :: dayOfWeek: "+dayOfWeek+"; date: "+date+"; nonBusinessDaysResponse: "+nonBusinessDaysResponse);
				
				if (SBGConstants.TRUE.equalsIgnoreCase(nonBusinessDaysResponse.optString(SBGConstants.IS_VALID))) {
					currencyHolidaysResponse = validateCurrencyHolidays(responseObj, sbgValidationsResponseObj, dateFormat, date);
					LOG.info("RefDataValidator.validateHolidays :: date: "+date+"; currencyHolidaysResponse: "+currencyHolidaysResponse);
					
					if (SBGConstants.TRUE.equalsIgnoreCase(currencyHolidaysResponse.optString(SBGConstants.IS_VALID))) {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					} else {
						return currencyHolidaysResponse;
					}

				} else {
					return nonBusinessDaysResponse;
				}
			} else {
				return publicHolidaysResponse;
			}
		} catch (Exception e) {
			LOG.error("RefDataValidator.validateHolidays ::: Error: " + e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}
	}
	
	private	static JSONObject validatePublicHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate) 
	{
		String strPublicHolidayDate = null;
		Date publicHolidayDate = null;
		List<Date> publicHolidayDateList = new ArrayList<Date>();
		
		try {
			
			if(responseObj == null || 
				!responseObj.has("publicHolidaysList") || 
				responseObj.getJSONArray("publicHolidaysList") == null) 
			{
				return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
			}
			
			JSONArray publicHolidaysList = responseObj.getJSONArray("publicHolidaysList");
			for (Object publicHolidaysObj : publicHolidaysList) {
				JSONObject phJsonObj = (JSONObject) publicHolidaysObj;
				if (phJsonObj != null && phJsonObj.has("HolidayDate")) {
					strPublicHolidayDate = phJsonObj.getString("HolidayDate");
					publicHolidayDate = dateFormat.parse(strPublicHolidayDate);
					publicHolidayDateList.add(publicHolidayDate);
				} else {
					return setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
				}
			} // for loop
			if (publicHolidayDateList.contains(scheduledDate)) {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_PUBLIC_HOLIDAY_ERROR_MESSAGE);
			} else {
				return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
			}
		} catch (Exception e) {
			LOG.error("RefDataValidator.validatePublicHolidays(-) Error: " + e.getMessage());
			return setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
		}
	}

	private	static JSONObject validateNonBusinessDays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, String day) 
	{
		String workingDayOrWeekOff = null;
		try {
			if (responseObj != null && responseObj.has(day)) {
				workingDayOrWeekOff = responseObj.getString(day);
				if (StringUtils.isNotBlank(workingDayOrWeekOff)) {
					//LOG.info("RefDataValidator.validateNonBusinessDays --> workingDayOrWeekOff: " + workingDayOrWeekOff);
					if (SBGConstants.REFDATA_WEEK_OFF.equalsIgnoreCase(workingDayOrWeekOff)) {
						return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_WEEKEND_ERROR_MESSAGE);
					} else {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					}
				} 
			} 
		} catch (Exception e) {
			LOG.error("RefDataValidator.validateNonBusinessDays::: Error: " + e.getMessage());
		}
		return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE);
	}

	private	static JSONObject validateCurrencyHolidays(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate) {
		String strCurrencyHolidayDate = null;
		Date currencyHolidayDate = null;
		List<Date> currencyHolidayDateList = new ArrayList<Date>();
		try {
			if (responseObj != null && responseObj.has("currencyHolidaysList")) {
				JSONArray currencyHolidaysList = responseObj.getJSONArray("currencyHolidaysList");
				if (currencyHolidaysList != null) {
					for (Object currencyHolidaysObj : currencyHolidaysList) {
						JSONObject chJsonObj = (JSONObject) currencyHolidaysObj;
						if (chJsonObj != null) {
							if (chJsonObj.has("HolidayDate")) {
								strCurrencyHolidayDate = chJsonObj.getString("HolidayDate");
								currencyHolidayDate = dateFormat.parse(strCurrencyHolidayDate);
								currencyHolidayDateList.add(currencyHolidayDate);
							} else {
								return setErrorResult(sbgValidationsResponseObj,
										SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
							}
						} else {
							return setErrorResult(sbgValidationsResponseObj,
									SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
						}
					} // for loop

					if (currencyHolidayDateList.contains(scheduledDate)) {
						return setErrorResult(sbgValidationsResponseObj,
								SBGConstants.REFDATA_CURRENCY_HOLIDAY_ERROR_MESSAGE);
					} else {
						return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
					}
				} else {
					return setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
				}
			} else {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			LOG.error("RefDataValidator.validateCurrencyHolidays(-) Error: " + e.getMessage());
			return setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE);
		}

	}
	
	private	static JSONObject validateCutOffTime(JSONObject responseObj, JSONObject sbgValidationsResponseObj,
			SimpleDateFormat dateFormat, Date scheduledDate, SimpleDateFormat dayFormat, String day) 
	{
		boolean isCutOffTime = false;
		String strStartTime = null;
		String strEndTime = null;
		String timeZone = null;
		int leadDays = 0;
		JSONObject cutOffTimeResponse = new JSONObject();
		try {
			
			if(responseObj == null || !responseObj.has("Days") || responseObj.getJSONArray("Days") == null) {
				cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
				cutOffTimeResponse.put("LeadDays", leadDays);
				return cutOffTimeResponse;
			}
			
			JSONArray daysArray = responseObj.getJSONArray("Days");
			for (Object daysObj : daysArray) {
				JSONObject jsonDaysObj = (JSONObject) daysObj;
				
				if(jsonDaysObj == null || !jsonDaysObj.has("TimeRange") || 
					!jsonDaysObj.has("Day") || SBGCommonUtils.isStringEmpty(jsonDaysObj.getString("Day")))
				{
					cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
							SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
					cutOffTimeResponse.put("LeadDays", leadDays);
					return cutOffTimeResponse;
				}

				if (jsonDaysObj.getString("Day").equalsIgnoreCase(day)) {
					if (jsonDaysObj.has("LeadDays")) {
						leadDays = jsonDaysObj.getInt("LeadDays");
					} else {
						leadDays = 0;
					}

					JSONObject jsonTimeRangeObj = jsonDaysObj.getJSONObject("TimeRange");

					if (jsonTimeRangeObj != null && jsonTimeRangeObj.has("StartTime")
							&& jsonTimeRangeObj.has("EndTime")) {
						strStartTime = jsonTimeRangeObj.getString("StartTime");
						strEndTime = jsonTimeRangeObj.getString("EndTime");

						if (jsonDaysObj.has("TimeZone")) {
							timeZone = jsonDaysObj.getString("TimeZone");
						}
					} else {
						cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
								SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
						cutOffTimeResponse.put("LeadDays", leadDays);
						return cutOffTimeResponse;
					}

					if (StringUtils.isNotBlank(strStartTime) && StringUtils.isNotBlank(strEndTime)) {
						LocalTime startTime = LocalTime.parse(strStartTime);
						LocalTime endTime = LocalTime.parse(strEndTime);

						isCutOffTime = isCutOffTime(startTime, endTime, timeZone);
						//LOG.info("RefDataValidator.validateCutOffTime:::" + isCutOffTime);

						if (isCutOffTime) {

							cutOffTimeResponse = setSuccessResult(sbgValidationsResponseObj,
									SBGConstants.SUCCESS);
							cutOffTimeResponse.put("LeadDays", leadDays);
							return cutOffTimeResponse;

						} else {
							cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
									SBGConstants.REFDATA_CUTOFF_TIME_ERROR_MESSAGE);
							cutOffTimeResponse.put("LeadDays", leadDays);
							return cutOffTimeResponse;
						}
					} else {
						cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
								SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
						cutOffTimeResponse.put("LeadDays", leadDays);
						return cutOffTimeResponse;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("RefDataValidator.validateCutOffTime Error: "
					+ e.getMessage());
			cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
					SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
			cutOffTimeResponse.put("LeadDays", leadDays);
			return cutOffTimeResponse;
		}
		cutOffTimeResponse = setErrorResult(sbgValidationsResponseObj,
				SBGConstants.REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE);
		cutOffTimeResponse.put("LeadDays", leadDays);
		return cutOffTimeResponse;
	}

	private static JSONObject setErrorResult(JSONObject sbgValidationsResponseObj, String errorMessage) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.FALSE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, errorMessage);
		return sbgValidationsResponseObj;
	}

	private static JSONObject setSuccessResult(JSONObject sbgValidationsResponseObj, String message) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.TRUE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, message);
		return sbgValidationsResponseObj;
	}

	private static boolean isCutOffTime(LocalTime startTime, LocalTime endTime, String ZoneID) {
		ZoneId zone = ZoneId.of(ZoneID);
		LocalDateTime zoneDateTime = LocalDateTime.now(zone);
		LocalDateTime paymentstarttime = zoneDateTime.withHour(startTime.getHour()).withMinute(startTime.getMinute())
				.withSecond(0).withNano(0);
		LocalDateTime paymentendtime = zoneDateTime.withHour(endTime.getHour()).withMinute(endTime.getMinute())
				.withSecond(0).withNano(0);
		return (!zoneDateTime.isBefore(paymentstarttime)) && (!zoneDateTime.isAfter(paymentendtime)); // Inclusive.
	}
	
	private static JSONObject findNextWorkingDay(Date checkDate, Date valueDate, JSONObject responseObj, 
			JSONObject sbgValidationsResponseObj, String tranxCurrency, int leadDays) {
		
		JSONObject validateHolidaysResponse = null;
		
		while(checkDate.before(valueDate)) {
			//String checkDay = dayFormat.format(checkDate);
			validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat, checkDate);
			if (SBGConstants.TRUE.equalsIgnoreCase(validateHolidaysResponse.optString(SBGConstants.IS_VALID))) {
				return setSuccessResult(sbgValidationsResponseObj, SBGConstants.SUCCESS);
			} else {
				Date nextDate 	= new Date(checkDate.getTime() + SBGConstants.MILLIS_IN_A_DAY);
				return findNextWorkingDay(nextDate, valueDate, responseObj, sbgValidationsResponseObj, tranxCurrency, leadDays);
			}
		}
		
		if(checkDate.equals(valueDate)) {
			Date leadDate 	= new Date(checkDate.getTime() + (SBGConstants.MILLIS_IN_A_DAY * leadDays));
			if(leadDate.after(valueDate)) {
				return setErrorResult(sbgValidationsResponseObj,
						SBGConstants.REFDATA_LEAD_DAYS_ERROR_MESSAGE.replace("%CURRENCY_CODE%", tranxCurrency));
			}
			validateHolidaysResponse = validateHolidays(responseObj, sbgValidationsResponseObj, dateFormat, leadDate);
			LOG.info("RefDataValidator.findNextWorkingDay :: validateHolidaysResponse: "+validateHolidaysResponse);
			return validateHolidaysResponse;
		}
		
		return setErrorResult(sbgValidationsResponseObj,
				SBGConstants.REFDATA_LEAD_DAYS_ERROR_MESSAGE.replace("%CURRENCY_CODE%", tranxCurrency));
	}
}
