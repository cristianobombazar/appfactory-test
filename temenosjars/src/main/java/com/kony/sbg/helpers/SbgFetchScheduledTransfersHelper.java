package com.kony.sbg.helpers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.dbutil.QueryFormer;
import com.kony.dbputilities.dbutil.SqlQueryEnum;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class SbgFetchScheduledTransfersHelper {

	private static final Logger LOG = LogManager.getLogger(SbgFetchScheduledTransfersHelper.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> START --- methodID: "+methodID);      

        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> serviceName: "+inputParams.get("serviceName")+"; inputParams: "+inputParams);      

        Map<String, String> user = HelperMethods.getUserFromIdentityService(dcRequest);
        LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> user: "+user);      

        if (preProcess(inputParams, dcRequest, result, user)) {
            String url = URLConstants.ACCOUNT_TRANSACTION_PROC;
            LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> url: "+url);      

            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest), url);
            LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> result: "+result);      
        }
        
        if (!HelperMethods.hasError(result)) {
            if (HelperMethods.hasRecords(result)) {
                LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> records found");      
                postProcess(dcRequest, inputParams, result);
            } else {
                LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> no records found ");      
                result.addDataset(new Dataset());
            }
            result.getAllDatasets().get(0).setId("Transactions");
        }

        LOG.debug("SbgFetchScheduledTransfersHelper.invoke() ---> END");
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> user) {
        String sortBy = (String) inputParams.get("sortBy");
        String order = (String) inputParams.get("order");
        String start = (String) inputParams.get("firstRecordNumber");
        String end = (String) inputParams.get("lastRecordNumber");
        LOG.debug("SbgFetchScheduledTransfersHelper.preProcess() ---> sortBy: "+sortBy+"; order:"+order+"; start:"+start+"; end:"+end);      

        inputParams.put("countryCode", user.get("countryCode"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String fromDt = HelperMethods.getFormattedTimeStamp(cal.getTime(), null);
        cal.add(Calendar.DATE, 7);
        String toDt = HelperMethods.getFormattedTimeStamp(cal.getTime(), null);
        String jdbcUrl=QueryFormer.getDBType(dcRequest);
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "scheduledDate";
        }
        if (StringUtils.isBlank(order)) {
            order = "asc";
        }

        String userId = user.get("user_id");
        LOG.debug("SbgFetchScheduledTransfersHelper.preProcess() ---> userId: "+userId+"; fromDt:"+fromDt+"; toDt:"+toDt+"; jdbcUrl:"+jdbcUrl);      

        
        StringBuilder filter = new StringBuilder();
        
        filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTransactionDetails_transactionsType_Payee_ByCustomerId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId).replace("?3", fromDt).replace("?4", toDt).replace("?5", sortBy));
        
        /*if (HelperMethods.isBusinessUserType(user.get("customerType"))) {
			
			 * filter.
			 * append("select t1.*,tt.description as transactionType, IF(p.organizationId or e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from "
			 * + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
			 * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".payee p on (p.Id = t1.Payee_id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".externalaccount e on (e.Id = t1.Payee_id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".customeraccounts a on (a.Account_id = t1.ToAccountNumber)");
			 * filter.append("where (t1.FromAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".customeraccounts where Customer_id = '" + userId + "')");
			 * filter.append(" or t1.ToAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".customeraccounts where Customer_id = '" + userId + "'))");
			 
            
            filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTransactionDetails_transactionsType_Payee_ByCustomerId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId).replace("?3", fromDt).replace("?4", toDt).replace("?5", sortBy));
        
        } else {
			
			 * filter.
			 * append("select t1.*,tt.description as transactionType, IF(p.organizationId or e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from "
			 * + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
			 * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".payee p on (p.Id = t1.Payee_id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".externalaccount e on (e.Id = t1.Payee_id) left join " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".accounts a on (a.Account_id = t1.ToAccountNumber)");
			 * filter.append("where (t1.FromAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".accounts where User_id = '" + userId + "')");
			 * filter.append(" or t1.ToAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".accounts where User_id = '" + userId + "'))");
			 
        	
        	filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTransactionDetails_transactionsType_Payee_ByUserId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId).replace("?3", fromDt).replace("?4", toDt).replace("?5", sortBy));
        }*/

		/*
		 * filter.append(" and t1.isScheduled = '1' and t1.StatusDesc = 'Pending'");
		 * filter.append(" and t1.scheduledDate >= '" + fromDt + "'");
		 * filter.append(" and t1.scheduledDate <= '" + toDt + "'");
		 * filter.append(" order by " + sortBy + " " + order);
		 */

        if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
            filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_Limit").getQuery().replace("?1", start).replace("?2", end));
        }

        LOG.debug("SbgFetchScheduledTransfersHelper.preProcess() ---> filter: "+filter.toString());      
        inputParams.put("transactions_query", filter.toString());

        return true;
    }

    private void postProcess(DataControllerRequest dcRequest, Map<String, String> inputParams, Result result)
            throws HttpCallException {
        Map<String, String> accountTypes = getAccountTypes(dcRequest, inputParams.get("countryCode"));
        LOG.debug("SbgFetchScheduledTransfersHelper.postProcess() ---> accountTypes: "+accountTypes);      

        List<Record> transactions = result.getAllDatasets().get(0).getAllRecords();
        for (Record transaction : transactions) {
            updateToAccountName(dcRequest, transaction, HelperMethods.getUserIdFromSession(dcRequest));
            updateAmount(transaction);
            updateFromAccountDetails(dcRequest, transaction, accountTypes);
            updateToAccountDetails(dcRequest, transaction, accountTypes);
            updatePayeeDetails(dcRequest, transaction);
            updateBillDetails(dcRequest, transaction);
            updatePayPersonDetails(dcRequest, transaction);
            updateCashlessOTPValidDate(transaction);
            updateDateFormat(transaction);
        }
        LOG.debug("SbgFetchScheduledTransfersHelper.postProcess() ---> accountTypes: "+accountTypes);      
    }

    private void updateDateFormat(Record transaction) {
        String scheduledDate = HelperMethods.getFieldValue(transaction, "scheduledDate");
        String transactionDate = HelperMethods.getFieldValue(transaction, "transactionDate");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateDateFormat() ---> scheduledDate: "+scheduledDate+"; transactionDate:"+transactionDate);      

        try {
            if (StringUtils.isNotBlank(scheduledDate)) {
            	String conDate = HelperMethods.convertDateFormat(scheduledDate, "yyyy-MM-dd'T'hh:mm:ss'Z'");
                transaction.addParam(new Param("scheduledDate", conDate, "String"));
                LOG.debug("SbgFetchScheduledTransfersHelper.updateDateFormat() ---> scheduledDate conDate: "+conDate);      
            }
            if (StringUtils.isNotBlank(transactionDate)) {
            	String conDate = HelperMethods.convertDateFormat(transactionDate, "yyyy-MM-dd'T'hh:mm:ss'Z'");
                transaction.addParam(new Param("transactionDate", conDate, "String"));
                LOG.debug("SbgFetchScheduledTransfersHelper.updateDateFormat() ---> transactionDate conDate:"+conDate);      
            }
            String frequencyDate = HelperMethods.getFieldValue(transaction, "frequencyEndDate");
            if (StringUtils.isNotBlank(frequencyDate)) {
                transaction.addParam(new Param("frequencyEndDate",
                        HelperMethods.convertDateFormat(frequencyDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
            }
            frequencyDate = HelperMethods.getFieldValue(transaction, "frequencyStartDate");
            if (StringUtils.isNotBlank(frequencyDate)) {
                transaction.addParam(new Param("frequencyStartDate",
                        HelperMethods.convertDateFormat(frequencyDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
            }
        } catch (Exception e) {
        }
    }

    private void updateAmount(Record transaction) {
        String fee = HelperMethods.getFieldValue(transaction, "fee");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateAmount() ---> fee: "+fee);      

        if (StringUtils.isNotBlank(fee)) {
            String amount = HelperMethods.getFieldValue(transaction, "amount");
            transaction.addParam(new Param("amount", new BigDecimal(amount).subtract(new BigDecimal(fee)).toString(),
                    DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private Map<String, String> getAccountTypes(DataControllerRequest dcRequest, String countryCode)
            throws HttpCallException {
        String filter = "countryCode" + DBPUtilitiesConstants.EQUAL + countryCode;
        LOG.debug("SbgFetchScheduledTransfersHelper.getAccountTypes() ---> filter: "+filter);      

        Result accountTypes = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                URLConstants.ACCOUNTTYPE_GET);
        LOG.debug("SbgFetchScheduledTransfersHelper.getAccountTypes() ---> accountTypes: "+accountTypes);      

        Map<String, String> accountTypeMap = new HashMap<>();
        List<Record> types = accountTypes.getAllDatasets().get(0).getAllRecords();
        for (Record type : types) {
            accountTypeMap.put(HelperMethods.getFieldValue(type, "TypeID"),
                    HelperMethods.getFieldValue(type, "TypeDescription"));
        }
        LOG.debug("SbgFetchScheduledTransfersHelper.getAccountTypes() ---> accountTypeMap: "+accountTypeMap);      
        return accountTypeMap;
    }

    private void updateToAccountName(DataControllerRequest dcRequest, Record transaction, String userId)
            throws HttpCallException {
        String toExtAccountNum = HelperMethods.getFieldValue(transaction, "toExternalAccountNumber");
        String iBAN = HelperMethods.getFieldValue(transaction, DBPInputConstants.IBAN);
        LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountName() ---> toExtAccountNum: "+toExtAccountNum+"; iBAN:"+iBAN);      
        
        String filter = "";
        if (StringUtils.isNotBlank(toExtAccountNum)) {
            filter = "accountNumber" + DBPUtilitiesConstants.EQUAL + toExtAccountNum;
        } else if (StringUtils.isNotBlank(iBAN)) {
            filter = DBPInputConstants.IBAN + DBPUtilitiesConstants.EQUAL + iBAN;
        }
        if (StringUtils.isNotBlank(userId)) {
            filter += DBPUtilitiesConstants.AND;

            filter += "User_id" + DBPUtilitiesConstants.EQUAL + userId;
        }
        LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountName() ---> filter: "+filter);      

        if (StringUtils.isNotBlank(toExtAccountNum) || StringUtils.isNotBlank(iBAN)) {
            Result extAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.EXT_ACCOUNTS_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountName() ---> extAccount: "+extAccount);      

            String toAccountName = HelperMethods.getFieldValue(extAccount, "nickName");
            if (StringUtils.isBlank(toAccountName)) {
                toAccountName = HelperMethods.getFieldValue(extAccount, "beneficiaryName");
            }
            if (StringUtils.isBlank(toAccountName)) {
                toAccountName = HelperMethods.getFieldValue(transaction, "beneficiaryName");
            }
            transaction.addParam(new Param("toAccountName", toAccountName, DBPUtilitiesConstants.STRING_TYPE));
            String isInternationalAccount = HelperMethods.getFieldValue(extAccount, "isInternationalAccount");
            transaction.addParam(
                    new Param("isInternationalAccount", isInternationalAccount, DBPUtilitiesConstants.STRING_TYPE));
            String swiftCode = HelperMethods.getFieldValue(transaction, "swiftCode");
            if (StringUtils.isBlank(swiftCode)) {
                swiftCode = HelperMethods.getFieldValue(extAccount, "swiftCode");
                transaction.addParam(new Param("swiftCode", swiftCode, "String"));
            }
            LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountName() ---> toAccountName: "+toAccountName+"; swiftCode:"+swiftCode);      
        }
    }

    private void updateFromAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String frmAccountNum = HelperMethods.getFieldValue(transaction, "fromAccountNumber");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateFromAccountDetails() ---> frmAccountNum: "+frmAccountNum);      

        if (StringUtils.isNotBlank(frmAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + frmAccountNum;
            Result frmAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.updateFromAccountDetails() ---> frmAccount: "+frmAccount);      

            String typeId = HelperMethods.getFieldValue(frmAccount, "Type_id");
            transaction.addParam(
                    new Param("fromAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(frmAccount, "accountName");
            transaction.addParam(new Param("fromAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
            String nickName = HelperMethods.getFieldValue(frmAccount, "nickName");
            if (StringUtils.isBlank(nickName)) {
                nickName = accountName;
            }
            transaction.addParam(new Param("fromAccountNickName", nickName, DBPUtilitiesConstants.STRING_TYPE));
            LOG.debug("SbgFetchScheduledTransfersHelper.updateFromAccountDetails() ---> typeId: "+typeId+"; accountName:"+accountName+"; nickName:"+nickName);      
        }
    }

    private void updateToAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String toAccountNum = HelperMethods.getFieldValue(transaction, "toAccountNumber");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountDetails() ---> toAccountNum: "+toAccountNum);      
        if (StringUtils.isNotBlank(toAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + toAccountNum;
            Result toAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountDetails() ---> toAccount: "+toAccount);      

            String typeId = HelperMethods.getFieldValue(toAccount, "Type_id");
            transaction
                    .addParam(new Param("toAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(toAccount, "accountName");
            transaction.addParam(new Param("toAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
            LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountDetails() ---> typeId: "+typeId+"; accountName:"+accountName);      
        }
    }

    private void updatePayeeDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payeeId = HelperMethods.getFieldValue(transaction, "payeeId");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateToAccountDetails() ---> payeeId: "+payeeId);      

        Record payee = getPayee(dcRequest, payeeId);
        transaction.addParam(new Param("payeeNickName", HelperMethods.getFieldValue(payee, "nickName"),
                DBPUtilitiesConstants.STRING_TYPE));
    }

    private void updateBillDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String billId = HelperMethods.getFieldValue(transaction, "billid");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateBillDetails() ---> billId: "+billId);      

        if (StringUtils.isNotBlank(billId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + billId;
            Result biller = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BILL_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.updateBillDetails() ---> biller: "+biller);      

            if (HelperMethods.hasRecords(biller)) {
                Record bill = biller.getAllDatasets().get(0).getRecord(0);
                String payeeId = HelperMethods.getFieldValue(bill, "Payee_id");
                Record payee = getPayee(dcRequest, payeeId);
                Param p = transaction.getParamByName("payeeNickName");
                if (p == null || StringUtils.isBlank(p.getValue())) {
                    transaction.addParam(new Param("payeeNickName", HelperMethods.getFieldValue(payee, "nickName"),
                            DBPUtilitiesConstants.STRING_TYPE));
                }

                p = transaction.getParamByName("toAccountName");
                if (p == null || StringUtils.isBlank(p.getValue())) {
                    transaction.addParam(new Param("toAccountName", HelperMethods.getFieldValue(payee, "firstName"),
                            DBPUtilitiesConstants.STRING_TYPE));
                }
                LOG.debug("SbgFetchScheduledTransfersHelper.updateBillDetails() ---> payeeId: "+payeeId);      
            }
        }
    }

    private Record getPayee(DataControllerRequest dcRequest, String payeeId) throws HttpCallException {
        LOG.debug("SbgFetchScheduledTransfersHelper.getPayee() ---> payeeId: "+payeeId);      

        if (StringUtils.isNotBlank(payeeId)) {
            String filter = "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
            Result payees = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYEE_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.getPayee() ---> payees: "+payees);      

            if (HelperMethods.hasRecords(payees)) {
                return payees.getAllDatasets().get(0).getRecord(0);
            }
        }
        return null;
    }

    private void updatePayPersonDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payPersonId = HelperMethods.getFieldValue(transaction, "Person_Id");
        LOG.debug("SbgFetchScheduledTransfersHelper.updatePayPersonDetails() ---> payPersonId: "+payPersonId);      

        if (StringUtils.isNotBlank(payPersonId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + payPersonId;
            Result payPerson = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYPERSON_GET);
            LOG.debug("SbgFetchScheduledTransfersHelper.updatePayPersonDetails() ---> payPerson: "+payPerson);      

            if (HelperMethods.hasRecords(payPerson)) {
                Record person = payPerson.getAllDatasets().get(0).getRecord(0);
                transaction.addParam(new Param("phone", HelperMethods.getFieldValue(person, "phone"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("email", HelperMethods.getFieldValue(person, "email"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("name", HelperMethods.getFieldValue(person, "name"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("personId", payPersonId,
                        DBPUtilitiesConstants.STRING_TYPE));
                Param p = transaction.getParamByName("toAccountName");
                if (p == null || StringUtils.isBlank(p.getValue())) {
                    transaction.addParam(new Param("toAccountName", HelperMethods.getFieldValue(person, "firstName"),
                            DBPUtilitiesConstants.STRING_TYPE));
                }
                LOG.debug("SbgFetchScheduledTransfersHelper.updatePayPersonDetails() ---> p: "+transaction.getParamByName("toAccountName"));      
            }
        }
    }

    private void updateCashlessOTPValidDate(Record transaction) {
        String otpValidDate = HelperMethods.getFieldValue(transaction, "cashlessOTPValidDate");
        LOG.debug("SbgFetchScheduledTransfersHelper.updateCashlessOTPValidDate() ---> otpValidDate: "+otpValidDate);      

        if (StringUtils.isNotBlank(otpValidDate)) {
            long timeDiff = HelperMethods.getFormattedTimeStamp(otpValidDate).getTime() - new Date().getTime();
            transaction.addParam(
                    new Param("cashlessOTPValidDate", String.valueOf(timeDiff), DBPUtilitiesConstants.STRING_TYPE));
        }
    }
}
