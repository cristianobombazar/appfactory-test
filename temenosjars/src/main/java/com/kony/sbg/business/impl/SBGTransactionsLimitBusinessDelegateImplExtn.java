package com.kony.sbg.business.impl;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.transactionslimitengine.businessdelegate.impl.TransactionsLimitBusinessDelegateImpl;
import com.dbp.transactionslimitengine.utils.FEATUREENUM;
import com.dbp.transactionslimitengine.utils.HelperMethods;
import com.dbp.transactionslimitengine.utils.TransactionsLimitErrorCodesEnum;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class SBGTransactionsLimitBusinessDelegateImplExtn  extends TransactionsLimitBusinessDelegateImpl{
	private static final Logger logger = LogManager.getLogger(SBGTransactionsLimitBusinessDelegateImplExtn.class);
	  
	  static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	  
	  private static String schemaname = null;
	  
	  private static JsonObject returnJson(TransactionsLimitErrorCodesEnum trenum) {
	    JsonObject retval = new JsonObject();
	    retval.addProperty("dbpErrCode", trenum.getErrCode());
	    retval.addProperty("dbpErrMsg", trenum.getErrMsg());
	    return retval;
	  }
	  
	  private static JsonObject returnJson(TransactionsLimitErrorCodesEnum trenum, String errmsg) {
	    JsonObject retval = new JsonObject();
	    retval.addProperty("dbpErrCode", trenum.getErrCode());
	    retval.addProperty("dbpErrMsg", errmsg);
	    return retval;
	  }
	  
	  public JsonObject getTransactionLimits(String featureactionid, String companyid, String transactionstartdate, String roleid, String customerid, String accountid, String limitGroup) {
	    JsonObject retval = new JsonObject();
	    try {
	    	 logger.debug("SBGTransactionsLimitBusinessDelegateImplExtn#Start");
	      logger.debug(featureactionid);
	      logger.debug(transactionstartdate);
	      logger.debug(companyid);
	      logger.debug(roleid);
	      logger.debug(customerid);
	      logger.debug(accountid);
	      logger.debug(limitGroup);
	      if (limitGroup == null && (featureactionid == null || transactionstartdate == null))
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_LIMITORFEATUREORDATEMISSING); 
	      if (featureactionid == null && transactionstartdate == null)
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_LIMITORFEATUREORDATEMISSING); 
	      if (companyid == null && accountid == null && customerid == null)
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_COMPCUSTACCMISSING); 
	      if (transactionstartdate.length() < 10)
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_INVALIDDATEFORMAT); 
	      if (limitGroup != null && customerid != null && companyid != null)
	        return processLimitGroupRecords(limitGroup, customerid, companyid, accountid, transactionstartdate, retval); 
	      String operationid = FEATUREENUM.getDBServiceKey(featureactionid);
	      operationid = replaceSchemaName(operationid);
	      logger.debug("operationid" + operationid);
	      if (operationid == null)
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_INVALIDFEATUREID); 
	      transactionstartdate = transactionstartdate.substring(0, 10);
	      transactionstartdate = transactionstartdate + " 00:00:00";
	      LocalDateTime transday = parseDate(transactionstartdate);
	      if (transday == null)
	        return returnJson(TransactionsLimitErrorCodesEnum.ERROR_INVALIDDATEFORMAT); 
	      processDailyTransactions(transday, companyid, roleid, accountid, customerid, retval, operationid, featureactionid);
	      processWeeklyTransactions(transday, companyid, roleid, accountid, customerid, retval, operationid, featureactionid);
	    } catch (Exception e) {
	      logger.debug("Exception ", e);
	      return returnJson(TransactionsLimitErrorCodesEnum.ERROR_EXCEPTION, "Unable to fetch limits, Refer logs for more details");
	    } 
	    return retval;
	  }
	  
	  private static JsonObject processLimitGroupRecords(String limitGroup, String customerid, String companyid, String accountid, String transactionstartdate, JsonObject retval) {
	    JsonArray featureActionIds = getFeatureActionIdsForLimitGroup(limitGroup, retval);
	    if (featureActionIds == null)
	      return retval; 
	    Map<String, Object> inputmap = getInputMapForOrchService(featureActionIds, customerid, accountid, companyid, transactionstartdate);
	    return processOrchServiceRecords(callInternalServiceAndGetString("TransactionsLimitsOrch", "getTransactionsAmountOrch", inputmap), retval);
	  }
	  
	  private static JsonObject processOrchServiceRecords(String response, JsonObject retval) {
	    if (response == null || response.equals("")) {
	      retval.addProperty("dbpErrMsg", "Failed to fetch limits from Orch Service");
	      retval.addProperty("dbpErrCode", TransactionsLimitErrorCodesEnum.ERROR_EXCEPTION
	          .getErrCode());
	      return retval;
	    } 
	    JsonArray orchServiceResponeArray = (new JsonParser()).parse(response).getAsJsonObject().get("LoopDataset").getAsJsonArray();
	    double weeklyAmount = 0.0D;
	    double dailyAmount = 0.0D;
	    boolean isValidOutput = false;
	    for (JsonElement responseObj : orchServiceResponeArray) {
	      if (responseObj.getAsJsonObject().get("Daily") != null && responseObj
	        .getAsJsonObject().get("Weekly") != null) {
	        weeklyAmount += Double.parseDouble(responseObj
	            .getAsJsonObject().get("Weekly").getAsString());
	        dailyAmount += 
	          Double.parseDouble(responseObj.getAsJsonObject().get("Daily").getAsString());
	        if (!isValidOutput)
	          isValidOutput = true; 
	      } 
	    } 
	    if (isValidOutput) {
	      retval.addProperty("Weekly", Double.valueOf(weeklyAmount));
	      retval.addProperty("Daily", Double.valueOf(dailyAmount));
	    } 
	    return retval;
	  }
	  
	  private static Map<String, Object> getInputMapForOrchService(JsonArray featureActionIds, String customerid, String accountid, String companyid, String transactionstartdate) {
	    if (StringUtils.isBlank(accountid))
	      accountid = ""; 
	    Map<String, Object> inputmap = new HashMap<>();
	    String customerIdsString = "";
	    String accountIdsString = "";
	    String featureactionIdsString = "";
	    String companyIdsString = "";
	    String dateString = "";
	    for (JsonElement featureactionobj : featureActionIds) {
	      customerIdsString = customerIdsString.equals("") ? customerid : (customerIdsString + "###" + customerid);
	      accountIdsString = accountIdsString.equals("") ? accountid : (accountIdsString + "###" + accountid);
	      companyIdsString = companyIdsString.equals("") ? companyid : (companyIdsString + "###" + companyid);
	      featureactionIdsString = featureactionIdsString.equals("") ? featureactionobj.getAsJsonObject().get("id").getAsString() : (featureactionIdsString + "###" + featureactionobj.getAsJsonObject().get("id").getAsString());
	      dateString = dateString.equals("") ? transactionstartdate : (dateString + "###" + transactionstartdate);
	    } 
	    inputmap.put("loop_count", Integer.valueOf(featureActionIds.size()));
	    inputmap.put("loop_separator", "###");
	    inputmap.put("customerid", customerIdsString);
	    inputmap.put("companyid", companyIdsString);
	    if (StringUtils.isNotBlank(accountid))
	      inputmap.put("accountid", accountIdsString); 
	    inputmap.put("featureactionid", featureactionIdsString);
	    inputmap.put("date", dateString);
	    return inputmap;
	  }
	  
	  private static JsonArray getFeatureActionIdsForLimitGroup(String limitGroup, JsonObject retval) {
	    Map<String, Object> inputmap = new HashMap<>();
	    String filter = "limitgroupId eq '" + limitGroup + "'";
	    inputmap.put("$select", "id");
	    inputmap.put("$filter", filter);
	    return parseStringDbResponse(
	        callInternalServiceAndGetString("TransactionsLimitDBService", 
	          replaceSchemaName("{schema_name}_featureaction_get"), inputmap), retval);
	  }
	  
	  private static JsonArray parseStringDbResponse(String response, JsonObject retval) {
	    if (response == null || response.equals("")) {
	      retval.addProperty("dbpErrMsg", "Exception occurred while getting featureactions for given limit group");
	      retval.addProperty("dbpErrCode", TransactionsLimitErrorCodesEnum.ERROR_EXCEPTION
	          .getErrCode());
	      return null;
	    } 
	    JsonObject responseObj = (new JsonParser()).parse(response).getAsJsonObject();
	    if (responseObj.get("featureaction") == null || responseObj
	      .get("featureaction").getAsJsonArray().size() == 0) {
	      retval.addProperty("dbpErrMsg", TransactionsLimitErrorCodesEnum.ERROR_NOFEATUREACTIONSFORLIMITGROUP
	          .getErrMsg());
	      retval.addProperty("dbpErrCode", TransactionsLimitErrorCodesEnum.ERROR_NOFEATUREACTIONSFORLIMITGROUP
	          .getErrCode());
	      return null;
	    } 
	    return responseObj.get("featureaction").getAsJsonArray();
	  }
	  
	  private static String replaceSchemaName(String operationid) {
	    if (operationid == null)
	      return operationid; 
	    if (schemaname == null)
	      schemaname = HelperMethods.getConfigProperty("DBX_SCHEMA_NAME"); 
	    if (operationid.contains("{schema_name}"))
	      operationid = operationid.replace("{schema_name}", schemaname); 
	    return operationid;
	  }
	  
	  private static void processDailyTransactions(LocalDateTime transday, String companyid, String roleid, String accountid, String customerid, JsonObject retval, String operationid, String featureactionid) {
	    Map<String, Object> inputmap = new HashMap<>();
	    String transactionstartdate = transday.toLocalDate().toString();
	    String transactionendday = getNdaysAfterDate(transday, 1).toLocalDate().toString();
	    String filter = generateFilter(companyid, transactionstartdate, transactionendday, roleid, accountid, customerid, featureactionid);
	    if (filter == null) {
	      retval.addProperty("dbpErrMsg", TransactionsLimitErrorCodesEnum.ERROR_COMPCUSTACCMISSING
	          .getErrMsg());
	      retval.addProperty("dbpErrCode", TransactionsLimitErrorCodesEnum.ERROR_COMPCUSTACCMISSING
	          .getErrCode());
	      return;
	    } 
	    inputmap.put("$filter", filter);
	    inputmap.put("$select", "amount");
	    logger.debug("inputmap" + inputmap);
	    Result res = callDBService("TransactionsLimitDBService", operationid, inputmap);
	    logger.debug("Result Daily:"+ResultToJSON.convert(res));
	    parseDBResponse(res, retval, "Daily");
	    logger.debug(ResultToJSON.convert(res));
	  }
	  
	  private static void processWeeklyTransactions(LocalDateTime transday, String companyid, String roleid, String accountid, String customerid, JsonObject retval, String operationid, String featureactionid) {
	    Map<String, Object> inputmap = new HashMap<>();
	    DayOfWeek weekstartday = getStartDayOfWeek();
	    DayOfWeek transactionday = transday.getDayOfWeek();
	    LocalDateTime currentransactionweekstartday = getNdaysAfterDate(transday, 
	        noofDaysDifferenceToWeekStartDay(transactionday, weekstartday));
	    LocalDateTime currentransactionweekendday = getNdaysAfterDate(transday, 
	        noofDaysDifferenceToWeekEndDay(transactionday, weekstartday));
	    String filter = generateFilter(companyid, currentransactionweekstartday.toLocalDate().toString(), currentransactionweekendday
	        .toLocalDate().toString(), roleid, accountid, customerid, featureactionid);
	    if (filter == null) {
	      retval.addProperty("dbpErrMsg", TransactionsLimitErrorCodesEnum.ERROR_COMPCUSTACCMISSING
	          .getErrMsg());
	      retval.addProperty("dbpErrCode", TransactionsLimitErrorCodesEnum.ERROR_COMPCUSTACCMISSING
	          .getErrCode());
	      return;
	    } 
	    inputmap.put("$select", "amount");
	    inputmap.put("$filter", filter);
	    logger.debug("inputmap" + inputmap);
	    Result res = callDBService("TransactionsLimitDBService", operationid, inputmap);
	    logger.debug(ResultToJSON.convert(res));
	    parseDBResponse(res, retval, "Weekly");
	  }
	  
	  private static void parseDBResponse(Result res, JsonObject retval, String type) {
	    if (!res.getAllDatasets().isEmpty()) {
	      Dataset ds = res.getAllDatasets().get(0);
	      double returnamount = 0.0D;
	      for (Record rec : ds.getAllRecords()) {
	        String amount = rec.getParamValueByName("amount");
	        if (amount != null)
	          returnamount += Double.parseDouble(amount); 
	      } 
	      if (type.equals("Daily")) {
	        retval.addProperty("Daily", Double.valueOf(returnamount));
	      } else {
	        retval.addProperty("Weekly", Double.valueOf(returnamount));
	      } 
	    } 
	  }
	  
	  private static Result callDBService(String serviceid, String operationid, Map<String, Object> inputmap) {
	    Result res = new Result();
	    try {
	      res = DBPServiceExecutorBuilder.builder().withOperationId(operationid).withRequestParameters(inputmap).withServiceId(serviceid).build().getResult();
	    } catch (Exception e) {
	      logger.debug("Error occured in fetching limits", e);
	      res.addParam(new Param("dbpErrMsg", e.getMessage(), "String"));
	      return res;
	    } 
	    return res;
	  }
	  
	  private static String callInternalServiceAndGetString(String serviceid, String operationid, Map<String, Object> inputmap) {
	    String res = "";
	    try {
	      res = DBPServiceExecutorBuilder.builder().withOperationId(operationid).withRequestParameters(inputmap).withServiceId(serviceid).build().getResponse();
	    } catch (Exception e) {
	      logger.debug("Error occured in fetching limits", e);
	      return res;
	    } 
	    return res;
	  }
	  
	  private static String generateFilter(String companyid, String transactionstartdate, String transactionendday, String roleid, String accountid, String customerid, String featureactionid) {
	    String filter = "featureActionId eq '" + featureactionid + "' and (status eq 'Executed' or status eq 'Sent' or status eq 'Pending' or status eq 'PDNG' or  status eq 'ACSC' or  status eq 'ACSP') and (scheduledDate ge '" + transactionstartdate + "' and scheduledDate lt '" + transactionendday + "')";
	    if (companyid == null) {
	      if (accountid != null && customerid != null) {
	        filter = filter + " and fromAccountNumber eq '" + accountid + "'";
	        filter = filter + " and createdby eq '" + customerid + "'";
	      } else if (customerid != null) {
	        filter = filter + " and createdby eq '" + customerid + "'";
	      } else {
	        return null;
	      } 
	    } else {
	      filter = filter + " and companyId eq '" + companyid + "'";
	      if (roleid != null)
	        filter = filter + " and roleId eq '" + roleid + "'"; 
	      if (customerid != null)
	        filter = filter + " and createdby eq '" + customerid + "'"; 
	      if (accountid != null)
	        filter = filter + " and fromAccountNumber eq '" + accountid + "'"; 
	    } 
	    logger.debug("filter"+filter);
	    return filter;
	  }
	  
	  private static int noofDaysDifferenceToWeekStartDay(DayOfWeek transday, DayOfWeek weekstartday) {
	    int diff = transday.getValue() - weekstartday.getValue();
	    if (diff == 0)
	      return 0; 
	    if (diff < 0)
	      return -(7 + diff); 
	    return -diff;
	  }
	  
	  private static int noofDaysDifferenceToWeekEndDay(DayOfWeek transday, DayOfWeek weekstartday) {
	    int diff = transday.getValue() - weekstartday.getValue();
	    if (diff == 0)
	      return 7; 
	    if (diff < 0)
	      return -diff; 
	    return 7 - diff;
	  }
	  
	  private static LocalDateTime getNdaysAfterDate(LocalDateTime date, int noofdays) {
	    if (date == null)
	      return null; 
	    LocalDateTime t1 = null;
	    t1 = date.plusDays(noofdays);
	    return t1;
	  }
	  
	  private static LocalDateTime parseDate(String date) {
	    LocalDateTime t = null;
	    try {
	      t = LocalDateTime.parse(date, dtf);
	    } catch (Exception e) {
	      logger.debug("Parsing error", e);
	    } 
	    return t;
	  }
	  
	  private static DayOfWeek getStartDayOfWeek() {
	    String day = HelperMethods.getConfigProperty("TRANSACTIONLIMITENGINE_STARTDAY");
	    if (day == null)
	      return DayOfWeek.MONDAY; 
	    for (DayOfWeek dayname : DayOfWeek.values()) {
	      if (day.equalsIgnoreCase(dayname.toString()))
	        return dayname; 
	    } 
	    return DayOfWeek.MONDAY;
	  }
	}
