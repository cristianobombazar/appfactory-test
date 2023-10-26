package com.kony.sbg.helpers;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.util.SbgURLConstants;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SbgDocTransactionRelationHelper {
    private static final Logger LOG = Logger.getLogger(SbgDocTransactionRelationHelper.class);

    public static void updateTransactionIdAgaintDocumentId(String documentIdList, String referenceId) {
        try {
            String[] documentIdArray = documentIdList.split(",");
            Arrays.stream(documentIdArray).forEach(documentId -> updateTransactionId(documentId, referenceId));
        } catch (Exception e) {
            LOG.error("Error occured while invoking updateTransactionIdAgaintDocumentId(): ", e);
        }
    }

    private static String updateTransactionId(String documentId, String transactionId) {
        LOG.debug("### Starting ---> CreateInternationalAccountTransferOperation.updateTransactionId()");
        String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
        String operationName = SbgURLConstants.OPERATION_SBT_DOCUMENT_UPDATE;
        String sbgDocumentUpdateResponse = null;
        try {
            LOG.debug("transactionId of getAuditDataByTransactionId :- " + transactionId);
            HashMap<String, Object> requestHeaders = new HashMap<>();
            Map<String, Object> input = new HashMap<>();
            input.put("document_id", documentId);
            input.put("transaction_id", transactionId);

            sbgDocumentUpdateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(input)
                    .withRequestHeaders(requestHeaders).build().getResponse();

            LOG.debug("#### Response of sbgDocumentUpdateResponse:- " + sbgDocumentUpdateResponse);
        } catch (Exception e) {
            LOG.debug("Enter to Exception of updateTransactionId:", e);
        }

        return sbgDocumentUpdateResponse;
    }
}
