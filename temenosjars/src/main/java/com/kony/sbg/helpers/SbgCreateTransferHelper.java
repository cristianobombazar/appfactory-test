package com.kony.sbg.helpers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;

public class SbgCreateTransferHelper {
	
	private static final Logger LOG = LogManager.getLogger(SbgCreateTransferHelper.class);

	private ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse, String tranxStatus, String transId) throws Exception {
		
		LOG.debug("SbgCreateTransferHelper.invoke() ---> START");
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        boolean status = true;
        status = preProcess(inputParams, dcRequest, result, tranxStatus, transId);
        LOG.debug("SbgCreateTransferHelper.invoke() ---> status: "+status);
        
        if (inputParams.containsKey(DBPUtilitiesConstants.FILTER)) {
        	LOG.debug("SbgCreateTransferHelper.invoke() ---> inputparams contain filter ...");
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    URLConstants.TRANSACTION_GET);
            LOG.debug("SbgCreateTransferHelper.invoke() ---> TRANSACTION_GET result: "+result);
            postProcess(dcRequest, inputParams, result);
        }
        if (status) {
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNT_TRANSACTION_CREATE);
            LOG.debug("SbgCreateTransferHelper.invoke() ---> ACCOUNT_TRANSACTION_CREATE result: "+result);

            status = !HelperMethods.hasError(result);
            LOG.debug("SbgCreateTransferHelper.invoke() ---> ACCOUNT_TRANSACTION_CREATE status: "+status);
            
            if (status) {
            	result = postProcess(dcRequest, result);
            }
        }

        LOG.debug("SbgCreateTransferHelper.invoke() ---> END");
        return result;
    }

    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result, String tranxStatus, String transId)
            throws HttpCallException, ParseException {
        boolean status = false;
        boolean isSchedulingEngine = false;
        Map<String, String> map = HelperMethods.getCustomerFromIdentityService(dcRequest);
        LOG.debug("SbgCreateTransferHelper.preProcess() ---> map:"+map);
        
        if (map.containsKey("isSchedulingEngine") && map.get("isSchedulingEngine") != null
                && map.get("isSchedulingEngine").equals("true")) {
            isSchedulingEngine = true;
        }

        LOG.debug("SbgCreateTransferHelper.preProcess() ---> isSchedulingEngine:"+isSchedulingEngine);
        if (!isSchedulingEngine) {

            status = validations(inputParams, dcRequest, result);

        }

        if (status || isSchedulingEngine) {
            inputParams.put(DBPUtilitiesConstants.CREATED_DATE, application.getServerTimeStamp());
            createNewExternalTransfer(inputParams, dcRequest, result, tranxStatus, transId);
        }
        
        LOG.debug("SbgCreateTransferHelper.preProcess() ---> status:"+status);
        return status;
    }

    private void postProcess(DataControllerRequest dcRequest, Map<String, String> inputParams, Result result)
            throws HttpCallException {
    	LOG.debug("SbgCreateTransferHelper.postProcess3() ---> START:");
        if (HelperMethods.hasRecords(result)) {
            List<Record> transactions = result.getAllDatasets().get(0).getAllRecords();
            LOG.debug("SbgCreateTransferHelper.postProcess3() ---> transactions: " +transactions);
            for (Record transaction : transactions) {
                updateFromAccountDetails(dcRequest, transaction);
                updateToAccountDetails(dcRequest, transaction);
                updatePayPersonDetails(dcRequest, transaction);
                updateBillDetails(dcRequest, transaction);
                updatePayeeDetails(dcRequest, transaction);
                updateDateFormat(transaction);
            }
        }
        LOG.debug("SbgCreateTransferHelper.postProcess3() ---> END");
    }

    private Result postProcess(DataControllerRequest request, Result result) {
        Result retResult = new Result();
        LOG.debug("SbgCreateTransferHelper.postProcess2() ---> START:");
        retResult.addParam(new Param("referenceId", HelperMethods.getFieldValue(result, "Id"), DBPUtilitiesConstants.STRING_TYPE));
        retResult.addParam(new Param("success", "success", DBPUtilitiesConstants.STRING_TYPE));
        retResult.addParam(new Param("status", "success", DBPUtilitiesConstants.STRING_TYPE));
        LOG.debug("SbgCreateTransferHelper.postProcess2() ---> END");
        return retResult;
    }

    private boolean validations(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result)
            throws HttpCallException {
        boolean status = true;
        String transactionType = inputParams.get(DBPUtilitiesConstants.TRANSACTION_TYPE);
        LOG.debug("SbgCreateTransferHelper.validations() ---> transactionType: "+transactionType);

        if (!StringUtils.isNotBlank(transactionType)) {
            HelperMethods.setValidationMsg("Edit operation not permitted for this Transaction type", dcRequest, result);
            status = false;
        }
        return status;
    }

    private void updateFromAccountDetails(DataControllerRequest dcRequest, Record transaction)
            throws HttpCallException {
        String frmAccountNum = HelperMethods.getFieldValue(transaction, "fromAccountNumber");
        LOG.debug("SbgCreateTransferHelper.updateFromAccountDetails() ---> frmAccountNum: "+frmAccountNum);
        
        if (StringUtils.isNotBlank(frmAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + frmAccountNum;
            Result frmAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgCreateTransferHelper.updateFromAccountDetails() ---> frmAccount: "+frmAccount);
            
            String type = HelperMethods.getFieldValue(frmAccount, "typeDescription");
            transaction.addParam(new Param("fromAccountType", type, MWConstants.STRING));
            String accountName = HelperMethods.getFieldValue(frmAccount, "accountName");
            transaction.addParam(new Param("fromAccountName", accountName, MWConstants.STRING));
            String nickName = HelperMethods.getFieldValue(frmAccount, "nickName");
            if (StringUtils.isBlank(nickName)) {
                nickName = accountName;
            }
            transaction.addParam(new Param("fromAccountNickName", nickName, MWConstants.STRING));
        }
        LOG.debug("SbgCreateTransferHelper.updateFromAccountDetails() ---> END: ");
    }

    private void updateToAccountDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String toAccountNum = HelperMethods.getFieldValue(transaction, "toAccountNumber");
        LOG.debug("SbgCreateTransferHelper.updateToAccountDetails() ---> toAccountNum: "+toAccountNum);
        
        if (StringUtils.isNotBlank(toAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + toAccountNum;
            Result toAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            LOG.debug("SbgCreateTransferHelper.updateToAccountDetails() ---> toAccount: "+toAccount);
            
            String type = HelperMethods.getFieldValue(toAccount, "typeDescription");
            transaction.addParam(new Param("toAccountType", type, MWConstants.STRING));
            String accountName = HelperMethods.getFieldValue(toAccount, "accountName");
            transaction.addParam(new Param("toAccountName", accountName, MWConstants.STRING));
        }
        LOG.debug("SbgCreateTransferHelper.updateToAccountDetails() ---> END: ");
    }

    private void updatePayPersonDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payPersonId = HelperMethods.getFieldValue(transaction, "Person_Id");
        LOG.debug("SbgCreateTransferHelper.updatePayPersonDetails() ---> payPersonId: "+payPersonId);
        
        if (StringUtils.isNotBlank(payPersonId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + payPersonId;
            Result payPerson = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYPERSON_GET);
            if (HelperMethods.hasRecords(payPerson)) {
                Record person = payPerson.getAllDatasets().get(0).getRecord(0);
                transaction
                        .addParam(new Param("phone", HelperMethods.getFieldValue(person, "phone"), MWConstants.STRING));
                transaction
                        .addParam(new Param("email", HelperMethods.getFieldValue(person, "email"), MWConstants.STRING));
                transaction
                        .addParam(new Param("name", HelperMethods.getFieldValue(person, "name"), MWConstants.STRING));
            }
        }
        LOG.debug("SbgCreateTransferHelper.updatePayPersonDetails() ---> END: ");
    }

    private void updateBillDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String billId = HelperMethods.getFieldValue(transaction, "Bill_id");
        LOG.debug("SbgCreateTransferHelper.updateBillDetails() ---> billId: "+billId);
        
        if (StringUtils.isNotBlank(billId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + billId;
            Result biller = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BILL_GET);
            LOG.debug("SbgCreateTransferHelper.updateBillDetails() ---> biller: "+biller);
            
            if (HelperMethods.hasRecords(biller)) {
                Record bill = biller.getAllDatasets().get(0).getRecord(0);
                String payeeId = HelperMethods.getFieldValue(bill, "Payee_id");
                fetchAndUpdatePayee(dcRequest, payeeId, transaction);
            }
        }
        LOG.debug("SbgCreateTransferHelper.updateBillDetails() ---> END: ");
    }

    private void fetchAndUpdatePayee(DataControllerRequest dcRequest, String payeeId, Record transaction)
            throws HttpCallException {
    	LOG.debug("SbgCreateTransferHelper.fetchAndUpdatePayee() ---> START: ");
        if (StringUtils.isNotBlank(payeeId)) {
            String filter = "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
            Result payees = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYEE_GET);
            
            LOG.debug("SbgCreateTransferHelper.fetchAndUpdatePayee() ---> payees: "+payees);
            if (HelperMethods.hasRecords(payees)) {
                Record payee = payees.getAllDatasets().get(0).getRecord(0);
                transaction.addParam(
                        new Param("payeeNickName", HelperMethods.getFieldValue(payee, "nickName"), MWConstants.STRING));
            }
        }
    }

    private void updatePayeeDetails(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payeeId = HelperMethods.getFieldValue(transaction, "Payee_Id");
        LOG.debug("SbgCreateTransferHelper.updatePayeeDetails() ---> START: payeeId: "+payeeId);
        if (StringUtils.isNotBlank(payeeId)) {
            String filter = "Id" + DBPUtilitiesConstants.EQUAL + payeeId;
            Result payees = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYEE_GET);
            LOG.debug("SbgCreateTransferHelper.updatePayeeDetails() ---> payees: "+payees);
            
            if (HelperMethods.hasRecords(payees)) {
                Record payee = payees.getAllDatasets().get(0).getRecord(0);
                transaction.addParam(
                        new Param("payeeNickName", HelperMethods.getFieldValue(payee, "nickName"), MWConstants.STRING));
            }
        }
        LOG.debug("SbgCreateTransferHelper.updatePayeeDetails() ---> END ");
    }

    private void updateDateFormat(Record transaction) {
    	LOG.debug("SbgCreateTransferHelper.updateDateFormat() ---> START: ");
        String scheduledDate = HelperMethods.getFieldValue(transaction, "scheduledDate");
        String transactionDate = HelperMethods.getFieldValue(transaction, "transactionDate");
        try {
            if (StringUtils.isNotBlank(scheduledDate)) {
                transaction.addParam(new Param("scheduledDate",
                        HelperMethods.convertDateFormat(scheduledDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
            }
            if (StringUtils.isNotBlank(transactionDate)) {
                transaction.addParam(new Param("transactionDate",
                        HelperMethods.convertDateFormat(transactionDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
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
        LOG.debug("SbgCreateTransferHelper.updateDateFormat() ---> END");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean createNewExternalTransfer(Map inputParams, DataControllerRequest dcRequest, Result result, String tranxStatus, String transId)
            throws HttpCallException, ParseException {
        boolean status = true;
        LOG.debug("SbgCreateTransferHelper.createNewExternalTransfer() ---> START ");
        
        String currentDate = inputParams.get(DBPUtilitiesConstants.CREATED_DATE)!= null ? inputParams.get(DBPUtilitiesConstants.CREATED_DATE).toString() : null;
        String typeId = getTransTypeId(dcRequest, DBPUtilitiesConstants.TRANSACTION_TYPE_EXTERNAL_TRANSFER);
        String toAccountNumber = (String) inputParams.get(DBPUtilitiesConstants.TO_ACCONT);
        String iBAN = (String) inputParams.get(DBPInputConstants.IBAN);
        if (StringUtils.isBlank(iBAN)) {
        	iBAN = (String) inputParams.get(DBPInputConstants.IBAN_SMALL);
        }
        String frmAccountNumber = (String) inputParams.get(DBPUtilitiesConstants.FRM_ACCONT);
        String notes = (String) inputParams.get(DBPInputConstants.TRANS_NOTES);
        String frequencyType = (String) inputParams.get(DBPUtilitiesConstants.FREQUENCY_TYPE);
        String fee = (String) inputParams.get(DBPInputConstants.FEE_AMOUNT);
        String payeeId = (String) inputParams.get("payeeId");

        String nickName = "";
        Map frmAccountDetails = getAccountDetails(dcRequest, frmAccountNumber);
        
        inputParams.put("Payee_id", payeeId);
        inputParams.put("DomesticPaymentId", transId);
        inputParams.put(DBPUtilitiesConstants.TRANS_NOTES, notes);
        inputParams.put(DBPUtilitiesConstants.T_TYPE_ID, typeId);
        inputParams.put(DBPUtilitiesConstants.TRANS_DATE, currentDate);
        if (!inputParams.containsKey("transactionCurrency")) {
            inputParams.put("transactionCurrency", frmAccountDetails.get("transactionCurrency"));
        }
        if (StringUtils.isNotBlank(toAccountNumber)) {
            inputParams.put(DBPUtilitiesConstants.TO_EXT_ACCT_NUM, toAccountNumber);
        } else {
            inputParams.put(DBPInputConstants.IBAN, iBAN);
        }
        inputParams.put(DBPUtilitiesConstants.TO_ACCONT, null);
        if (!"Once".equalsIgnoreCase(frequencyType)) {
            String noOfRecurr = (String) inputParams.get(DBPUtilitiesConstants.NO_OF_RECURRENCES);
            if (StringUtils.isNotBlank(noOfRecurr)) {
                inputParams.put(DBPUtilitiesConstants.RECUR_DESC, "1 of " + noOfRecurr);
            }
        }
        Map extAccountDetails = null;
        String scheduledDt = (String) inputParams.get(DBPUtilitiesConstants.SCHEDULED_DATE);
        LOG.debug("SbgCreateTransferHelper.createNewExternalTransfer() ---> scheduledDt: "+scheduledDt);
        Date scheduledDate = HelperMethods.getFormattedTimeStamp(scheduledDt);
        if (scheduledDate.after(new Date()) || !"Once".equalsIgnoreCase(frequencyType)) {
            inputParams.put("isScheduled", "true");
            //inputParams.put(DBPUtilitiesConstants.STATUS_DESC, DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING);
            inputParams.put(DBPUtilitiesConstants.FRM_ACCNT_BAL, frmAccountDetails.get(DBPUtilitiesConstants.AVAILABLE_BAL_S));
            inputParams.put(DBPUtilitiesConstants.TO_ACCNT_BAL, null);
            extAccountDetails = getExtAccountDetails(dcRequest, toAccountNumber, iBAN);
        } else {
            inputParams.put("isScheduled", "false");
            //String statusDesc = inputParams.get("status")!=null ? (String)inputParams.get("status") : DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL;
            //inputParams.put(DBPUtilitiesConstants.STATUS_DESC, statusDesc);
            BigDecimal transAmnt = new BigDecimal((String) inputParams.get(DBPInputConstants.AMOUNT));
            BigDecimal frmBal = new BigDecimal((String) frmAccountDetails.get(DBPUtilitiesConstants.AVAILABLE_BAL_S));
            inputParams.put(DBPUtilitiesConstants.FRM_ACCNT_BAL, frmBal.subtract(transAmnt).toPlainString());
            inputParams.put(DBPUtilitiesConstants.TO_ACCNT_BAL, null);
            extAccountDetails = getExtAccountDetails(dcRequest, toAccountNumber, iBAN);
        }
        
        inputParams.put(DBPUtilitiesConstants.STATUS_DESC, tranxStatus);

        if (extAccountDetails != null) {
            nickName = (String) extAccountDetails.get(DBPUtilitiesConstants.N_NAME);
        }

        if (StringUtils.isBlank(nickName)) {
            nickName = (String) inputParams.get("beneficiaryName");
        }
        
        if (StringUtils.isNotBlank(fee)) {
            inputParams.put("fee", fee);
        }

        inputParams.put("description", DBPUtilitiesConstants.TRANSFER_MESSAGE + nickName);
        HelperMethods.removeNullValues(inputParams);
        
        LOG.debug("SbgCreateTransferHelper.createNewExternalTransfer() ---> END -- inputParams: "+inputParams);
        return status;
    }

    public String getTransTypeId(DataControllerRequest dcRequest, String transType) throws HttpCallException {
        String filterQuery = DBPUtilitiesConstants.TRANS_TYPE_DESC + DBPUtilitiesConstants.EQUAL + transType;
        Result rs = HelperMethods.callGetApi(dcRequest, filterQuery, HelperMethods.getHeaders(dcRequest),
                URLConstants.TRANSACTION_TYPE_GET);
        
        LOG.debug("SbgCreateTransferHelper.getTransTypeId() ---> rs: "+rs);
        
        return HelperMethods.getFieldValue(rs, DBPUtilitiesConstants.T_TRANS_ID);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getAccountDetails(DataControllerRequest dcRequest, String accountId) throws HttpCallException {
        Map result = new HashMap();
        String filter = DBPUtilitiesConstants.ACCOUNT_ID + DBPUtilitiesConstants.EQUAL + accountId;
        LOG.debug("SbgCreateTransferHelper.getAccountDetails() ---> START --- filter: "+filter);
        
        Result rs = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                URLConstants.ACCOUNTS_GET);
        Dataset ds = rs.getAllDatasets().get(0);
        LOG.debug("SbgCreateTransferHelper.getAccountDetails() ---> START --- ds: "+ds);
        
        if (null != ds && !ds.getAllRecords().isEmpty()) {
            String nickName = HelperMethods.getFieldValue(ds.getRecord(0), DBPUtilitiesConstants.N_NAME);
            String avlblBal = HelperMethods.getFieldValue(ds.getRecord(0), DBPUtilitiesConstants.AVAILABLE_BAL_S);
            String currdue = HelperMethods.getFieldValue(ds.getRecord(0), DBPUtilitiesConstants.CURRENT_AMNT_DUE_S);
            String principalBal = HelperMethods.getFieldValue(ds.getRecord(0), DBPUtilitiesConstants.PRINCIPAL_BAL_S);
            String lateFeesDue = HelperMethods.getFieldValue(ds.getRecord(0), "lateFeesDue");
            String payOffCharge = HelperMethods.getFieldValue(ds.getRecord(0), "payOffCharge");
            result.put(DBPUtilitiesConstants.N_NAME, nickName);
            result.put(DBPUtilitiesConstants.AVAILABLE_BAL_S, StringUtils.isBlank(avlblBal) ? "0" : avlblBal);
            result.put(DBPUtilitiesConstants.CURRENT_AMNT_DUE_S, StringUtils.isBlank(currdue) ? "0" : currdue);
            result.put(DBPUtilitiesConstants.PRINCIPAL_BAL_S, StringUtils.isBlank(principalBal) ? "0" : principalBal);
            result.put("lateFeesDue", StringUtils.isBlank(lateFeesDue) ? "0" : lateFeesDue);
            result.put("payOffCharge", StringUtils.isBlank(payOffCharge) ? "0" : payOffCharge);
            
            LOG.debug("SbgCreateTransferHelper.getAccountDetails() ---> accountId: "+accountId+"; nickName: "+nickName);
        }
        LOG.debug("SbgCreateTransferHelper.getAccountDetails() ---> END ");
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getExtAccountDetails(DataControllerRequest dcRequest, String accountNum, String iBAN)
            throws HttpCallException {
        Map result = new HashMap();
        String filter = "";
        
        LOG.debug("SbgCreateTransferHelper.getExtAccountDetails() ---> START ");

        if (StringUtils.isNotBlank(accountNum)) {
            filter += DBPUtilitiesConstants.ACCT_NUMBER + DBPUtilitiesConstants.EQUAL + accountNum;
        } else {
            filter += DBPInputConstants.IBAN + DBPUtilitiesConstants.EQUAL + iBAN;

            String userID = HelperMethods.getUserIdFromSession(dcRequest);
            if (StringUtils.isNotBlank(userID)) {
                filter += DBPUtilitiesConstants.AND;

                filter += "User_id" + DBPUtilitiesConstants.EQUAL + userID;
            }
        }

        Result rs = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                URLConstants.EXT_ACCOUNTS_GET);
        Dataset ds = rs.getAllDatasets().get(0);
        if (null != ds && !ds.getAllRecords().isEmpty()) {
            String nickName = HelperMethods.getFieldValue(ds.getRecord(0), DBPUtilitiesConstants.N_NAME);
            if (StringUtils.isNotBlank(nickName)) {
                result.put(DBPUtilitiesConstants.N_NAME, nickName);
            } else {
                nickName = HelperMethods.getFieldValue(ds.getRecord(0), "beneficiaryName");
                result.put(DBPUtilitiesConstants.N_NAME, nickName);
            }
        }
        
        LOG.debug("SbgCreateTransferHelper.getExtAccountDetails() ---> END ");
        return result;
    }

//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public void updateAccountBalance(DataControllerRequest dcRequest, String accountId, String balance)
//            throws HttpCallException {
//        Map input = new HashMap();
//        LOG.debug("SbgCreateTransferHelper.updateAccountBalance() ---> START ");
//        input.put(DBPUtilitiesConstants.ACCOUNT_ID, accountId);
//        input.put(DBPUtilitiesConstants.AVAILABLE_BAL, balance);
//        HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_UPDATE);
//        LOG.debug("SbgCreateTransferHelper.updateAccountBalance() ---> END ");
//    }
}
