package com.kony.sbg.resources.impl;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.fileutil.GeneratePayment;
import com.kony.sbg.resources.api.GeneratePaymentPdfService;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgAlertUtil;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GeneratePaymentPdfServiceImpl implements GeneratePaymentPdfService {
    private static final Logger LOG = Logger.getLogger(GeneratePaymentPdfServiceImpl.class);

    @Override
    public Result getPaymentPDF(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse)
            throws Exception {

        LOG.debug("Entry Point of getPaymentPDF in GeneratePaymentPdfServiceImpl");
        Result result = new Result();

        if (inputArray != null) {
            Map<String, String> inputParamMap = HelperMethods.getInputParamMap(inputArray);
            String transactionId = inputParamMap.get("transactionId");
//            String transactionId = "tempData";
            LOG.debug("TransactionId Successful:- " + transactionId);

            if (StringUtils.isNotBlank(transactionId)) {
                GeneratePayment generatePayment = new GeneratePayment();
                String transferDataJson = getTransferDataByTransactionId(transactionId);
                String auditDataJson = getAuditDataByTransactionId(transactionId);
                String documentDataJson = getDocumentMetaDataByTransactionId(transactionId, dcRequest);
                String proofOfPayment = SbgAlertUtil.getProofOfPayment(transactionId, dcRequest);
                byte[] bytes = generatePayment.generateFile(result, transferDataJson, auditDataJson, documentDataJson, proofOfPayment);

                String base64StringPDF = Base64.getEncoder().encodeToString(bytes);
                result.addParam("paymentReportPdf", base64StringPDF);

                if (bytes != null) {
                    LOG.debug("IF condition Base64String for generated PDF in GeneratePaymentPdfServiceImpl:- " + base64StringPDF);
                    System.out.println("IF condition Base64String for generated PDF in GeneratePaymentPdfServiceImpl:- " + base64StringPDF);
                } else {
                    LOG.debug("Base64String Null in GeneratePaymentPdfServiceImpl");
                    System.out.println("Base64String Null in GeneratePaymentPdfServiceImpl");
                }
                LOG.debug("Exit successfully on StringUtils.isNotBlank(transactionId) of GeneratePaymentPdfServiceImpl");
                return result;
            } else {
                LOG.debug("TransactionId Null, Fail the Generate PDF");
                result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100036.getErrorCode()));
                result.addParam("errMsg", "Requested Payment Reference Id or document id not found.");
            }
        } else {
            LOG.debug("inputArray Null, Fail the Generate PDF");
        }

        return result;
    }


    private String getTransferDataByTransactionId(String transactionId) throws HttpCallException, DBPApplicationException {
        LOG.error("### Starting ---> GeneratePaymentPdfServiceImpl::getTransferDataByTransactionId()");
        String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
        String operationName = SbgURLConstants.OPERATION_TRANSACTION_BY_TRANSACTIONID;
        String transactionDataResponse = null;
        try {
            LOG.error("transactionId of GetTransferData :- " + transactionId);
            HashMap<String, Object> fundRequestHeaders = new HashMap<>();
            Map<String, Object> input = new HashMap<>();
            input.put("transactionId", transactionId);
            input.put("paymentId", transactionId);
            input.put("transactionId2", transactionId);
            input.put("paymentId2", transactionId);

            transactionDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(input)
                    .withRequestHeaders(fundRequestHeaders).build().getResponse();

            LOG.error("");

            LOG.error("#### Response of transactionDataResponse:- " + transactionDataResponse);
        } catch (Exception e) {
            LOG.error("Enter to Exception of getTransferDataByTransactionId of GetTransferData", e);
        }

        return transactionDataResponse;

    }

    private String getAuditDataByTransactionId(String transactionId) throws HttpCallException, DBPApplicationException {
        LOG.error("### Starting ---> GeneratePaymentPdfServiceImpl::getAuditDataByTransactionId()");
        String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
        String operationName = SbgURLConstants.OPERATION_AUDITDATA_BY_TRANSACTIONID;
        String auditDataResponse = null;
        try {
            LOG.error("transactionId of getAuditDataByTransactionId :- " + transactionId);
            HashMap<String, Object> requestHeaders = new HashMap<>();
            Map<String, Object> input = new HashMap<>();
            input.put("transactionId", transactionId);

            auditDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(input)
                    .withRequestHeaders(requestHeaders).build().getResponse();

            LOG.error("#### Response of transactionDataResponse:- " + auditDataResponse);
        } catch (Exception e) {
            LOG.error("Enter to Exception of getTransferDataByTransactionId of getAuditDataByTransactionId", e);
        }

        return auditDataResponse;

    }

    private String getDocumentMetaDataByTransactionId(String transactionId, DataControllerRequest dcRequest) throws HttpCallException, DBPApplicationException {
        LOG.error("### Starting ---> GeneratePaymentPdfServiceImpl::getDocumentMetaDataByTransactionId()");

        String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
        String operationName = SbgURLConstants.OPERATION_SBT_DOCUMENT_GET;

        StringBuilder sb = new StringBuilder();
        String transactionIdFilter = sb.append("transaction_id").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();
        LOG.error("### GeneratePaymentPdfServiceImpl::getDocumentMetaDataByTransactionId() filter:" + transactionIdFilter);

        Map<String, Object> input = new HashMap<>();
        input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
        input.put(DBPUtilitiesConstants.SELECT, "fileName");

        HashMap<String, Object> fundRequestHeaders = new HashMap<>();

        String docWithoutContentResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                .withOperationId(operationName).withRequestParameters(input)
                .withRequestHeaders(fundRequestHeaders).build().getResponse();
        LOG.error("### Starting ---> GeneratePaymentPdfServiceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:" + docWithoutContentResponse);

        return docWithoutContentResponse;
    }
}
