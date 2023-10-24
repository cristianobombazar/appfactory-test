package com.kony.sbg.helpers;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.dbutil.QueryFormer;
import com.kony.dbputilities.dbutil.SqlQueryEnum;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.sortutil.SortRecordByParamValue;
import com.kony.dbputilities.util.*;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.backend.api.PaymentFeedbackBackendDelegate;
import com.kony.sbg.util.PaymentFailureEnum;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.*;
import com.konylabs.middleware.dataobject.Record;
import com.temenos.dbx.eum.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SbgFetchTransfersHelper {

    private static final Logger LOG = LogManager.getLogger(SbgFetchTransfersHelper.class);

    PaymentFeedbackBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
            .getBackendDelegate(PaymentFeedbackBackendDelegate.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        LOG.debug("SbgFetchTransfersHelper.invoke() ---> START --- methodID: " + methodID);

        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        LOG.debug("SbgFetchTransfersHelper.invoke() ---> serviceName: " + inputParams.get("serviceName")
                + "; inputParams: " + inputParams);

        Map<String, String> user = HelperMethods.getUserFromIdentityService(dcRequest);
        LOG.debug("SbgFetchTransfersHelper.invoke() ---> user: " + user);

        if (preProcess(inputParams, dcRequest, result, user)) {
            LOG.debug("SbgFetchTransfersHelper.invoke() ---> Fetching data from Transaction table ");
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNT_TRANSACTION_PROC);
            LOG.debug("SbgFetchTransfersHelper.invoke() ---> result: " + result);
        }
        if (HelperMethods.hasRecords(result) && !HelperMethods.hasError(result)) {
            LOG.debug("SbgFetchTransfersHelper.invoke() ---> record created in transaction table");
            result.getAllDatasets().get(0).setId("Transactions");
            if (HelperMethods.hasRecords(result)) {
                postProcess(dcRequest, inputParams, result);
            }
        } else {
            LOG.debug("SbgFetchTransfersHelper.invoke() ---> could not create record in transaction table");
            Dataset accountView = new Dataset();
            accountView.setId("Transactions");
            result.addDataset(accountView);
        }

        LOG.debug("SbgFetchTransfersHelper.invoke() ---> END ");
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> user) {
        boolean status = true;
        inputParams.put("countryCode", user.get("countryCode"));
        String searchType = (String) inputParams.get("searchType");
        String userId = user.get("user_id");
        LOG.debug("SbgFetchTransfersHelper.preProcess() ---> searchType: " + searchType + "; userId: " + userId);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(DBPUtilitiesConstants.FILTER, "Customer_id eq " + userId);

        if ("Search".equalsIgnoreCase(searchType)) {
            searchTransactionsForUser(inputParams, dcRequest, result, user);
        } else {
            getTransactionsForUser(inputParams, dcRequest, result, user);
        }

        return status;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void searchTransactionsForUser(Map inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> user) {
        LOG.debug("SbgFetchTransfersHelper.searchTransactionsForUser() ---> START ");

        String searchMinAmount = (String) inputParams.get("searchMinAmount");
        String searchEndDate = (String) inputParams.get("searchEndDate");
        String searchMaxAmount = (String) inputParams.get("searchMaxAmount");
        String searchStartDate = (String) inputParams.get("searchStartDate");
        String isScheduled2 = (String) inputParams.get("isScheduled");
        String fromCheckNumber = (String) inputParams.get("fromCheckNumber");
        String toCheckNumber = (String) inputParams.get("toCheckNumber");
        String searchType = (String) inputParams.get("searchType");
        String searchDescription = (String) inputParams.get("searchDescription");
        String accountNumber = (String) inputParams.get("accountNumber");
        String lastRecordNum = (String) inputParams.get("lastRecordNumber");
        String firstRecordNum = (String) inputParams.get("firstRecordNumber");
        String searchTransactionType = (String) inputParams.get("searchTransactionType");
        String statusDescription = DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING;
        String jdbcUrl = QueryFormer.getDBType(dcRequest);

        if (StringUtils.isBlank(searchMinAmount)) {
            searchMinAmount = "0";
        }

        if (StringUtils.isBlank(searchMaxAmount)) {
            searchMaxAmount = "2147483647";
        }

        if (StringUtils.isBlank(searchStartDate)) {
            searchStartDate = "1970-01-01";
        }

        if (StringUtils.isBlank(searchEndDate)) {
            searchEndDate = "2100-01-01";
        }

        if (StringUtils.isBlank(isScheduled2)) {
            isScheduled2 = "0";
        }
        if (StringUtils.isBlank(fromCheckNumber)) {
            fromCheckNumber = "-1";
        }
        if (StringUtils.isBlank(toCheckNumber)) {
            toCheckNumber = "2147483647";
        }
        if (StringUtils.isNotBlank(searchDescription)) {
            searchDescription = "'%" + searchDescription + "%'";
        } else {
            searchDescription = "'%'";
        }

        if ("0".equals(isScheduled2)) {
            statusDescription = DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL;
        }
        searchEndDate = searchEndDate + "T23:59:59";

        // security fix
        StringBuilder accountQuery = new StringBuilder();
        StringBuilder filter = new StringBuilder();

        filter.append(SqlQueryEnum.valueOf(jdbcUrl + "_GetUserPendingTransactions_searchTrans_accountquery_IF")
                .getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest))
                .replace("?2", user.get("user_id")).replace("?3", accountNumber));

        /*
         * if (HelperMethods.isBusinessUserType(user.get("customerType"))) {
         * 
         * accountQuery.
         * append("(select account_id from customeraccounts where Customer_id = '" +
         * user.get("user_id") + "'" + " and account_id='" + accountNumber + "')");
         * toAccountQuery.append(URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".customeraccounts a on (a.Account_id = t1.ToAccountNumber),");
         * 
         * filter.append(SqlQueryEnum.valueOf(jdbcUrl+
         * "_GetUserPendingTransactions_searchTrans_accountquery_IF").getQuery().replace
         * ("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest)).replace("?2", user.get("user_id")).replace("?3", accountNumber));
         * } else {
         * 
         * accountQuery.append("(select account_id from accounts where User_id = '" +
         * user.get("user_id") + "'" + " and account_id='" + accountNumber + "')");
         * toAccountQuery.append(URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".accounts a on (a.Account_id = t1.ToAccountNumber),");
         * 
         * filter.append(SqlQueryEnum.valueOf(jdbcUrl+
         * "_GetUserPendingTransactions_searchTrans_accountquery_ELSE").getQuery().
         * replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest)).replace("?2", user.get("user_id")).replace("?3", accountNumber));
         * }
         */

        /*
         * filter.
         * append("select t.*,tt.description as transactionType, IF(p.organizationId or e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from "
         * + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".transaction t left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".payee p on (p.Id = t.Payee_id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".externalaccount e on (e.Id = t.Payee_id) left join " +
         * toAccountQuery.toString() + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".transactiontype tt ");
         *
         * filter.append("where (t.Type_id = tt.id) and (t.description LIKE " +
         * searchDescription + " OR t.Amount LIKE " + searchDescription +
         * " OR t.checkNumber LIKE " + searchDescription + ") AND Amount >= " +
         * searchMinAmount + " AND Amount <= " + searchMaxAmount +
         * " AND t.transactionDate >= '" + searchStartDate + "' ");
         * filter.append("AND t.transactionDate <= '" + searchEndDate +
         * "' AND ((t.checkNumber > " + fromCheckNumber + " AND t.checkNumber < " +
         * toCheckNumber + ") OR t.checkNumber IS NULL) ");
         * filter.append(" AND StatusDesc = '" + statusDescription + "'");
         */

        if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_BOTH.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString() + " or
            // t.toAccountNumber = "
            // + accountQuery.toString() + ") ");

            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + " or"
                    + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = " + accountQuery.toString()
                    + "  )");

        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_DEPOSIT.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.toAccountNumber = " + accountQuery.toString() + ") ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_WITHDRAWAL.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString() + ") ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_P2PCREDITS.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.toAccountNumber = " + accountQuery.toString() + ") ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

            // filter.append(" and tt.description = '" + searchTransactionType + "'");
            filter.append(" and " + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " = '"
                    + searchTransactionType + "')");
        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_TRANSFERS.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString() + " or
            // t.toAccountNumber = "
            // + accountQuery.toString() + ") ");

            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + " or"
                    + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = " + accountQuery.toString()
                    + "  )");

            filter.append(" and ");
            // filter.append("( tt.description = 'InternalTransfer' or tt.description =
            // 'ExternalTransfer') ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " = 'InternalTransfer'"
                    + " or " + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " ='ExternalTransfer')");

        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_CHECKS.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString());
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

            // filter.append(" and tt.description = 'CheckWithdrawal') ");
            filter.append(
                    "and (" + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " = 'CheckWithdrawal')");
        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_P2PDEBITS.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString());
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

            // filter.append(" and tt.description = 'P2P') ");
            filter.append(" and (" + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " = 'P2P')");
        } else if (DBPUtilitiesConstants.TRANSACTION_TYPE_SEARCH_BILLPAY.equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString());
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + ")");

            // filter.append(" and tt.description = 'BillPay') ");
            filter.append("and (" + SqlQueryEnum.valueOf(jdbcUrl + "_DESCRIPTION").getQuery() + " = 'BillPay')");
        } else if ("Search".equalsIgnoreCase(searchTransactionType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString() + " or
            // t.toAccountNumber = "
            // + accountQuery.toString() + ") ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + " or"
                    + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = " + accountQuery.toString()
                    + "  )");

        } else if ("Search".equalsIgnoreCase(searchType)) {
            filter.append(" and ");
            // filter.append("( t.fromAccountNumber = " + accountQuery.toString() + " or
            // t.toAccountNumber = "
            // + accountQuery.toString() + ") ");
            filter.append("(" + SqlQueryEnum.valueOf(jdbcUrl + "_FROMACCOUNTNUMBER").getQuery() + " = "
                    + accountQuery.toString() + " or"
                    + SqlQueryEnum.valueOf(jdbcUrl + "_TOACCOUNTNUMBER").getQuery() + " = " + accountQuery.toString()
                    + "  )");

        }
        String sortBy = (String) inputParams.get("sortBy");
        String order = (String) inputParams.get("order");
        if (StringUtils.isBlank(sortBy)) {
            sortBy = SqlQueryEnum.valueOf(jdbcUrl + "_TRANSACTIONDATE").getQuery();
        }
        if (StringUtils.isBlank(order)) {
            order = "desc";
        }

        if (StringUtils.isNotBlank(sortBy) && StringUtils.isNotBlank(order) && !"toAccountName".equalsIgnoreCase(sortBy)
                && !"nickName".equalsIgnoreCase(sortBy)) {
            // filter.append(" order by " + sortBy + " " + order + " ");
            filter.append(
                    SqlQueryEnum.valueOf(jdbcUrl + "_orderBy").getQuery().replace("?1", sortBy).replace("?2", order));
        }
        if (StringUtils.isNotBlank(firstRecordNum) && StringUtils.isNotBlank(lastRecordNum)) {
            // filter.append(" limit " + firstRecordNum + " , " + lastRecordNum + " ");
            filter.append(SqlQueryEnum.valueOf(jdbcUrl + "_GetTransactionDetails_Limit").getQuery()
                    .replace("?1", firstRecordNum).replace("?2", lastRecordNum));
        }
        LOG.debug("SbgFetchTransfersHelper.searchTransactionsForUser() ---> filter: " + filter);

        inputParams.put("transactions_query", filter.toString());
        LOG.debug("SbgFetchTransfersHelper.searchTransactionsForUser() ---> END ");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void getTransactionsForUser(Map inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> user) {

        LOG.debug("SbgFetchTransfersHelper.getTransactionsForUser() ---> START");

        String sortBy = (String) inputParams.get(DBPUtilitiesConstants.SORTBY);
        String order = (String) inputParams.get(DBPUtilitiesConstants.ORDER);
        String userId = user.get("user_id");
        String isScheduled = (String) inputParams.get(DBPInputConstants.IS_SCHEDULED);
        String firstRecordNum = (String) inputParams.get(DBPUtilitiesConstants.FIRST_RECORD_NUMBER);
        String lastRecordNum = (String) inputParams.get(DBPUtilitiesConstants.LAST_RECORD_NUMBER);
        String statusDescription = DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL;
        String jdbcUrl = QueryFormer.getDBType(dcRequest);
        LOG.debug("SbgFetchTransfersHelper.getTransactionsForUser() ---> jdbcUrl: " + jdbcUrl + "; isScheduled: "
                + isScheduled);

        if (StringUtils.isBlank(sortBy)) {
            sortBy = "createdDate";
        }
        if (StringUtils.isBlank(order)) {
            order = "desc";
        }
        if (!StringUtils.isNotBlank(isScheduled)) {
            isScheduled = "0";
        }

        if (isScheduled.equalsIgnoreCase("1"))
            statusDescription = DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING;

        StringBuilder queryBuf = new StringBuilder();
        // queryBuf.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTransactionDetails_IdentifiedByTransactionType_ByCustomerId").getQuery().replace("?1",
        // URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2",
        // statusDescription).replace("?3", userId).replace("?4", isScheduled));

        String query = "select distinct t1.*,tt.description as transactionType, IF(e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from ?1.transaction t1 left join ?1.transactiontype tt on t1.Type_id = tt.id left join ?1.externalaccount e on e.Id = t1.Payee_id left join ?1.customeraccounts a on a.Account_id = t1.ToAccountNumber and (t1.FromAccountNumber in (select account_id from ?1.customeraccounts where Customer_id = '?3') or t1.ToAccountNumber in (select account_id from ?1.customeraccounts where Customer_id = '?3'))and tt.description in ('InternalTransfer','ExternalTransfer','P2P') where t1.FromAccountNumber in (select account_id from dbxdb.customeraccounts where Customer_id = '?3')";
        query = query.replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?3", userId);
        queryBuf.append(query);
        LOG.debug("SbgFetchTransfersHelper.getTransactionsForUser() ---> query: " + query);

        /*
         * if (HelperMethods.isBusinessUserType(user.get("customerType"))) {
         * 
         * queryBuf.
         * append("select t1.*,tt.description as transactionType, IF(p.organizationId or e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from "
         * + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".payee p on (p.Id = t1.Payee_id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".externalaccount e on (e.Id = t1.Payee_id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".customeraccounts a on (a.Account_id = t1.ToAccountNumber)" );
         * queryBuf.append(" where "); queryBuf.append(" t1.StatusDesc = '");
         * queryBuf.append(statusDescription);
         * queryBuf.append("' and (t1.FromAccountNumber in (select account_id from " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".customeraccounts where Customer_id = '" + userId + "')");
         * queryBuf.append(" or t1.ToAccountNumber in (select account_id from " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".customeraccounts where Customer_id = '" + userId + "') "); queryBuf.
         * append(" ) and tt.description in ('InternalTransfer','ExternalTransfer','P2P')"
         * );
         * 
         * 
         * queryBuf.append(SqlQueryEnum.valueOf(jdbcUrl+
         * "_GetTransactionDetails_IdentifiedByTransactionType_ByCustomerId").getQuery()
         * .replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest)).replace("?2", statusDescription).replace("?3", userId));
         * } else {
         * 
         * queryBuf.
         * append("select t1.*,tt.description as transactionType, IF(p.organizationId or e.organizationId or a.Organization_id, 1, 0) as isBusinessPayee from "
         * + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".payee p on (p.Id = t1.Payee_id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".externalaccount e on (e.Id = t1.Payee_id) left join " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".accounts a on (a.Account_id = t1.ToAccountNumber)" );
         * queryBuf.append(" where "); queryBuf.append(" t1.StatusDesc = '");
         * queryBuf.append(statusDescription);
         * queryBuf.append("' and (t1.FromAccountNumber in (select account_id from " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".accounts where User_id = '" + userId + "')");
         * queryBuf.append(" or t1.ToAccountNumber in (select account_id from " +
         * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
         * ".accounts where User_id = '" + userId + "') "); queryBuf.
         * append(" ) and tt.description in ('InternalTransfer','ExternalTransfer','P2P')"
         * );
         * 
         * queryBuf.append(SqlQueryEnum.valueOf(jdbcUrl+
         * "_GetTransactionDetails_IdentifiedByTransactionType_ByUserId").getQuery().
         * replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
         * dcRequest)).replace("?2", statusDescription).replace("?3", userId));
         * }
         */

        if (!"toAccountName".equalsIgnoreCase(sortBy) && !"nickName".equalsIgnoreCase(sortBy)) {
            // queryBuf.append(" order by " + sortBy + " " + order);
            queryBuf.append(
                    SqlQueryEnum.valueOf(jdbcUrl + "_orderBy").getQuery().replace("?1", sortBy).replace("?2", order));
        }
        if (StringUtils.isNotBlank(firstRecordNum) && StringUtils.isNotBlank(lastRecordNum)) {
            queryBuf.append(SqlQueryEnum.valueOf(jdbcUrl + "_GetTransactionDetails_Limit").getQuery()
                    .replace("?1", firstRecordNum).replace("?2", lastRecordNum));
        }
        inputParams.put("transactions_query", queryBuf.toString());
        LOG.debug("SbgFetchTransfersHelper.getTransactionsForUser() ---> END ... queryBuf: " + queryBuf);
    }

    private void postProcess(DataControllerRequest dcRequest, Map<String, String> inputParams, Result result)
            throws HttpCallException {
        LOG.debug("SbgFetchTransfersHelper.postProcess() ---> START ");

        Map<String, String> accountTypes = getAccountTypes(dcRequest, inputParams.get("countryCode"));
        List<Record> transactions = result.getAllDatasets().get(0).getAllRecords();

        String userId = HelperMethods.getUserIdFromSession(dcRequest);
        for (Record transaction : transactions) {
            updateToAccountName(dcRequest, transaction, userId);
            updateFromAccountDetails(dcRequest, transaction, accountTypes);
            updateToAccountDetails(dcRequest, transaction, accountTypes);
            updatePayeeDetails(dcRequest, transaction);
            updateBillDetails(dcRequest, transaction);
            updatePayPersonDetails(dcRequest, transaction);
            updateCashlessOTPValidDate(transaction);
            updateDateFormat(transaction);
            if (transaction.hasParamByName("isInternationalAccount")) {
                if (transaction.getParamValueByName("isInternationalAccount").equalsIgnoreCase("false")) {
                    transaction.addParam("serviceName", FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                    addFailureReason(transaction, FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                } else if (transaction.getParamValueByName("isInternationalAccount").equalsIgnoreCase("true")) {
                    transaction.addParam("serviceName", FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                    addFailureReason(transaction, FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                } else {
                    if (transaction.hasParamByName("paymentType")) {
                        if (SBGConstants.URGENT_PAYMENT_TYPE.equals(transaction.getParamValueByName("paymentType"))
                                || SBGConstants.NORMAL_PAYMENT_TYPE
                                        .equals(transaction.getParamValueByName("paymentType"))) {
                            transaction.addParam("serviceName", FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                            addFailureReason(transaction, FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                        } else if (SbgURLConstants.IAT_PAYMENT_TYPE_DOM
                                .equals(transaction.getParamValueByName("paymentType")) ||
                                SbgURLConstants.IAT_PAYMENT_TYPE_FX
                                        .equals(transaction.getParamValueByName("paymentType"))) {
                            transaction.addParam("serviceName", FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);
                            addFailureReason(transaction, FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);
                        } else {
                            transaction.addParam("serviceName", FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                            addFailureReason(transaction, FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                        }

                    }
                }
            } else {
                if (transaction.hasParamByName("paymentType")) {
                    if (SBGConstants.URGENT_PAYMENT_TYPE.equals(transaction.getParamValueByName("paymentType"))
                            || SBGConstants.NORMAL_PAYMENT_TYPE
                                    .equals(transaction.getParamValueByName("paymentType"))) {
                        transaction.addParam("serviceName", FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                        addFailureReason(transaction, FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
                    } else if (SbgURLConstants.IAT_PAYMENT_TYPE_DOM
                            .equals(transaction.getParamValueByName("paymentType")) ||
                            SbgURLConstants.IAT_PAYMENT_TYPE_FX
                                    .equals(transaction.getParamValueByName("paymentType"))) {
                        transaction.addParam("serviceName", FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);
                        addFailureReason(transaction, FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE);
                    } else {
                        transaction.addParam("serviceName", FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                        addFailureReason(transaction, FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE);
                    }
                }
            }
        }
        if ("toAccountName".equalsIgnoreCase(inputParams.get("sortBy"))
                || "nickName".equalsIgnoreCase(inputParams.get("sortBy"))) {
            List<Record> sortedList = sortBy(transactions, "toAccountName", inputParams.get("order"));
            String id = result.getAllDatasets().get(0).getId();
            Dataset ds = new Dataset(id);
            ds.addAllRecords(sortedList);
            result.addDataset(ds);
        }
        LOG.debug("SbgFetchTransfersHelper.postProcess() ---> END ");
    }

    private Map<String, String> getAccountTypes(DataControllerRequest dcRequest, String countryCode)
            throws HttpCallException {
        String filter = "countryCode" + DBPUtilitiesConstants.EQUAL + countryCode;
        Result accountTypes = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                URLConstants.ACCOUNTTYPE_GET);
        Map<String, String> accountTypeMap = new HashMap<>();
        List<Record> types = accountTypes.getAllDatasets().get(0).getAllRecords();
        for (Record type : types) {
            accountTypeMap.put(HelperMethods.getFieldValue(type, "TypeID"),
                    HelperMethods.getFieldValue(type, "TypeDescription"));
        }
        return accountTypeMap;
    }

    private void updateToAccountName(DataControllerRequest dcRequest, Record transaction, String userId)
            throws HttpCallException {
        String toExtAccountNum = HelperMethods.getFieldValue(transaction, "toExternalAccountNumber");
        String iBAN = HelperMethods.getFieldValue(transaction, DBPInputConstants.IBAN);
        LOG.debug("SbgFetchTransfersHelper.updateToAccountName() ---> toExtAccountNum: " + toExtAccountNum + "; iBAN: "
                + iBAN);

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
        LOG.debug("SbgFetchTransfersHelper.updateToAccountName() ---> filter: " + filter);

        if (StringUtils.isNotBlank(toExtAccountNum) || StringUtils.isNotBlank(iBAN)) {
            Result extAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.EXT_ACCOUNTS_GET);
            LOG.debug("SbgFetchTransfersHelper.updateToAccountName() ---> extAccount: " + extAccount);

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
            LOG.debug("SbgFetchTransfersHelper.updateToAccountName() ---> toAccountName: " + toAccountName
                    + "; swiftCode: " + swiftCode);
        }
    }

    private void updateFromAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String frmAccountNum = HelperMethods.getFieldValue(transaction, "fromAccountNumber");
        LOG.debug("SbgFetchTransfersHelper.updateFromAccountDetails() ---> frmAccountNum: " + frmAccountNum);

        if (StringUtils.isNotBlank(frmAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + frmAccountNum;
            Result frmAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgFetchTransfersHelper.updateFromAccountDetails() ---> frmAccount: " + frmAccount);

            String typeId = HelperMethods.getFieldValue(frmAccount, "Type_id");
            transaction.addParam(
                    new Param("fromAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(frmAccount, "accountName");
            transaction.addParam(new Param("fromAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
            String nickName = HelperMethods.getFieldValue(frmAccount, "nickName");
            transaction.addParam(new Param("fromAccountNickName", nickName, DBPUtilitiesConstants.STRING_TYPE));
            LOG.debug("SbgFetchTransfersHelper.updateFromAccountDetails() ---> typeId: " + typeId + "; accountName: "
                    + accountName + "; nickName: " + nickName);
        }
    }

    private void addFailureReason(Record transaction, String featureActionId) {

        LOG.debug("#### starting addFailureReason()");

        String transferStatus = HelperMethods.getFieldValue(transaction, "statusDesc");

        if (transferStatus.equalsIgnoreCase("Failed")) {
            HashMap<String, Object> transactionRequestHeaders = new HashMap<>();

            String transactionId = HelperMethods.getFieldValue(transaction, "DomesticPaymentId");
            LOG.debug("#### DomesticPaymentId: " + transactionId);
            HashMap<String, Object> svcParams = new HashMap<>();
            String filter = "transactionId" + DBPUtilitiesConstants.EQUAL + "'" + transactionId + "'";
            svcParams.put("$filter", filter);
            LOG.debug("#### SbgFetchTransfersHelper.addFailureReason() ---> transferStatus: " + transferStatus
                    + " filter:" + filter);

            JSONObject transactionHistory = new JSONObject();

            if (featureActionId.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE)) {
                transactionHistory = backendDelegate.getDomesticStatusHistory(svcParams, transactionRequestHeaders);
            } else if (featureActionId.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE)) {
                transactionHistory = backendDelegate.getStatusHistory(svcParams, transactionRequestHeaders);
            } else if (featureActionId.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {
                transactionHistory = backendDelegate.getOwnAccountStatusHistory(svcParams, transactionRequestHeaders);
            }

            String reasonCode = getFailedReasonCode(transactionHistory, featureActionId);
            String failureReason = "";
            String failureString = "";
            if (StringUtils.isNotBlank(reasonCode)) {
                LOG.debug("#### reasonCode:" + reasonCode);
                Optional<PaymentFailureEnum> failureReasonOptional = Arrays.stream(PaymentFailureEnum.values())
                        .filter(value -> value.getFailureCode().equals(reasonCode))
                        .findAny();
                if (failureReasonOptional.isPresent()) {
                    String failureCode = failureReasonOptional.get().getFailureCode();
                    failureString = failureReasonOptional.get().getFailureReason();
                    String beneficiaryCurrencyCode = HelperMethods.getFieldValue(transaction, "payeeCurrency");
                    String senderCurrencyCode = HelperMethods.getFieldValue(transaction, "transactionCurrency");
                    if (failureCode.equalsIgnoreCase("DC01")) {
                        LOG.debug("###Sender currency code ---->" + senderCurrencyCode);
                        failureReason = String.format(failureString, senderCurrencyCode);
                    } else if (failureCode.equalsIgnoreCase("CC01")) {
                        LOG.debug("###Beneficiary currency code ---->" + beneficiaryCurrencyCode);
                        failureReason = String.format(failureString, beneficiaryCurrencyCode);
                    } else {
                        failureReason = failureString;
                    }
                } else
                    failureReason = failureString;
            }
            LOG.debug("failureReason ------->" + failureReason);

            transaction.addParam(new Param("failureReason", failureReason, DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private static String getFailedReasonCode(JSONObject transactionHistory, String featureActionId) {
        String reasonCode = null;
        try {
            JSONArray jsonArray = new JSONArray();
            if (featureActionId.equals(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE)) {
                jsonArray = transactionHistory.getJSONArray("interbankTransferStatus");
            } else if (featureActionId.equals(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE)) {
                jsonArray = transactionHistory.getJSONArray("internationalTransferStatus");
            } else if (featureActionId.equals(FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE)) {
                jsonArray = transactionHistory.getJSONArray("ownAccountTransferStatus");
            }

            LOG.debug("### getFailedReasonCode jsonArray:" + jsonArray);
            // for (int i = 0; i < jsonArray.length(); i++) {
            // String transferReasonCode =
            // jsonArray.getJSONObject(i).getString("transferStatus");
            // LOG.debug("### getFailedReasonCode transferReasonCode:" +
            // transferReasonCode);
            // if (StringUtils.isNotBlank(transferReasonCode) &&
            // transferReasonCode.equalsIgnoreCase("RJCT")) {
            // reasonCode = jsonArray.getJSONObject(i).getString("reasonCode");
            // if (StringUtils.isNotBlank(reasonCode)) {
            // LOG.debug("%%%%%%%% getFailedReasonCode reasonCode:" + reasonCode);
            // break;
            // }
            // }
            // }

            if (jsonArray.length() > 0) {
                reasonCode = jsonArray.getJSONObject(jsonArray.length() - 1).getString("reasonCode");
                LOG.debug("%%%%%%%% getFailedReasonCode reasonCode:" + reasonCode);
            }
        } catch (Exception e) {
            LOG.debug("### error occurred when trying to get Reasoncode", e);
        }
        return reasonCode;
    }

    private void updateToAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String toAccountNum = HelperMethods.getFieldValue(transaction, "toAccountNumber");
        LOG.debug("SbgFetchTransfersHelper.updateToAccountDetails() ---> toAccountNum: " + toAccountNum);

        if (StringUtils.isNotBlank(toAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + toAccountNum;
            Result toAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgFetchTransfersHelper.updateToAccountDetails() ---> toAccount: " + toAccount);

            String typeId = HelperMethods.getFieldValue(toAccount, "Type_id");
            transaction
                    .addParam(new Param("toAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(toAccount, "accountName");
            transaction.addParam(new Param("toAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
            LOG.debug("SbgFetchTransfersHelper.updateToAccountDetails() ---> typeId: " + typeId + "; accountName: "
                    + accountName);
        } else {
            String toExtAccountNum = HelperMethods.getFieldValue(transaction, "toExternalAccountNumber");
            String iBAN = HelperMethods.getFieldValue(transaction, DBPInputConstants.IBAN);
            LOG.debug("SbgFetchTransfersHelper.updateToAccountDetails() ---> toExtAccountNum: " + toExtAccountNum);

            if (StringUtils.isNotBlank(toExtAccountNum))
                transaction.addParam(new Param("toAccountNumber", toExtAccountNum, DBPUtilitiesConstants.STRING_TYPE));
            else
                transaction.addParam(new Param("toAccountNumber", iBAN, DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private void updatePayeeDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payeeId = HelperMethods.getFieldValue(transaction, "Payee_id");
        LOG.debug("SbgFetchTransfersHelper.updatePayeeDetails() ---> payeeId: " + payeeId);

        transaction.addParam(
                new Param("payeeNickName", getPayeeNickName(dcRequest, payeeId), DBPUtilitiesConstants.STRING_TYPE));
    }

    private void updateBillDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String billId = HelperMethods.getFieldValue(transaction, "billid");
        LOG.debug("SbgFetchTransfersHelper.updateBillDetails() ---> payeeId: " + billId);

        if (StringUtils.isNotBlank(billId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + billId;
            Result biller = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BILL_GET);
            LOG.debug("SbgFetchTransfersHelper.updateBillDetails() ---> biller: " + biller);

            if (HelperMethods.hasRecords(biller)) {
                Record bill = biller.getAllDatasets().get(0).getRecord(0);
                String payeeId = HelperMethods.getFieldValue(bill, "Payee_id");
                String payeeNickName = getPayeeNickName(dcRequest, payeeId);
                transaction.addParam(new Param("payeeNickName", payeeNickName, DBPUtilitiesConstants.STRING_TYPE));
            }
        }
    }

    private void updatePayPersonDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payPersonId = HelperMethods.getFieldValue(transaction, "Person_Id");
        LOG.debug("SbgFetchTransfersHelper.updatePayPersonDetails() ---> payPersonId: " + payPersonId);

        if (StringUtils.isNotBlank(payPersonId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + payPersonId;
            Result payPerson = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYPERSON_GET);
            LOG.debug("SbgFetchTransfersHelper.updatePayPersonDetails() ---> payPerson: " + payPerson);

            if (HelperMethods.hasRecords(payPerson)) {
                Record person = payPerson.getAllDatasets().get(0).getRecord(0);
                LOG.debug("SbgFetchTransfersHelper.updatePayPersonDetails() ---> person: " + person);

                transaction.addParam(new Param("phone", HelperMethods.getFieldValue(person, "phone"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("email", HelperMethods.getFieldValue(person, "email"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("name", HelperMethods.getFieldValue(person, "name"),
                        DBPUtilitiesConstants.STRING_TYPE));
                transaction.addParam(new Param("personId", payPersonId,
                        DBPUtilitiesConstants.STRING_TYPE));
            }
            if (StringUtils.isBlank(transaction.getParamValueByName("toAccountNumber"))) {
                LOG.debug("SbgFetchTransfersHelper.updatePayPersonDetails() ---> toAccountNumber is blank ");
                transaction.removeParamByName("toAccountNumber");
                transaction.addParam(new Param("toAccountNumber", payPersonId, DBPUtilitiesConstants.STRING_TYPE));
            }
        }
    }

    private void updateCashlessOTPValidDate(Record transaction) {
        String otpValidDate = HelperMethods.getFieldValue(transaction, "cashlessOTPValidDate");
        LOG.debug("SbgFetchTransfersHelper.updateCashlessOTPValidDate() ---> otpValidDate: " + otpValidDate);

        if (StringUtils.isNotBlank(otpValidDate)) {
            long timeDiff = HelperMethods.getFormattedTimeStamp(otpValidDate).getTime() - new Date().getTime();
            transaction.addParam(
                    new Param("cashlessOTPValidDate", String.valueOf(timeDiff), DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private void updateDateFormat(Record transaction) {
        String scheduledDate = HelperMethods.getFieldValue(transaction, "scheduledDate");
        String transactionDate = HelperMethods.getFieldValue(transaction, "transactionDate");
        LOG.debug("SbgFetchTransfersHelper.updateCashlessOTPValidDate() ---> scheduledDate: " + scheduledDate
                + "; transactionDate: " + transactionDate);

        try {
            if (StringUtils.isNotBlank(scheduledDate)) {
                String convertedDate = HelperMethods.convertDateFormat(scheduledDate, "yyyy-MM-dd'T'hh:mm:ss'Z'");
                transaction.addParam(new Param("scheduledDate", convertedDate, "String"));
                LOG.debug("SbgFetchTransfersHelper.updateCashlessOTPValidDate() ---> converted scheduledDate: "
                        + convertedDate);
            }
            if (StringUtils.isNotBlank(transactionDate)) {
                String convertedDate = HelperMethods.convertDateFormat(transactionDate, "yyyy-MM-dd'T'hh:mm:ss'Z'");
                transaction.addParam(new Param("transactionDate", convertedDate, "String"));
                LOG.debug("SbgFetchTransfersHelper.updateCashlessOTPValidDate() ---> converted transactionDate: "
                        + convertedDate);
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

    private List<Record> sortBy(List<Record> transactions, String fieldName, String order) {
        boolean asc = true;
        List<Record> mutable = new ArrayList<>();
        mutable.addAll(transactions);
        if (StringUtils.isNotBlank(order) && "desc".equals(order)) {
            asc = false;
        }
        Collections.sort(mutable, new SortRecordByParamValue(fieldName, asc));
        return mutable;
    }

    private String getPayeeNickName(DataControllerRequest dcRequest, String payeeId) throws HttpCallException {
        LOG.debug("SbgFetchTransfersHelper.getPayeeNickName() ---> payeeId: " + payeeId);
        if (StringUtils.isNotBlank(payeeId)) {
            String filter = "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
            Result payees = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BILL_GET);
            if (HelperMethods.hasRecords(payees)) {
                Record payee = payees.getAllDatasets().get(0).getRecord(0);
                return HelperMethods.getFieldValue(payee, "nickName");
            }
        }
        return null;
    }
}
