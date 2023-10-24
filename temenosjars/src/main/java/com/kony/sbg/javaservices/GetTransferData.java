package com.kony.sbg.javaservices;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.sbg.resources.impl.RetrieveDocumentResourceImpl;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GetTransferData {
    private static final Logger LOG = Logger.getLogger(GetTransferData.class);

    String getTransferDataByTransactionId(String transactionId, DataControllerRequest dcRequest) throws HttpCallException, DBPApplicationException {
//        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId()");
        LOG.debug("Enter to getTransferDataByTransactionId of GetTransferData");
        System.out.println("Entry to GetTransferData");
        String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
        //public static final String SERVICE_PAYMENT_RDB_SERVICE = "PaymentRDBServices";
        String operationName = SbgURLConstants.OPERATION_TRANSACTION_BY_TRANSACTIONID;
        //public static final String OPERATION_TRANSACTION_BY_TRANSACTIONID= "dbxdb_transferactivity_by_transactionId";
        String transactionDataResponse = null;
        try {
            LOG.debug("Enter to try block");
            System.out.println("Enter to try block");
            LOG.debug("transactionId of GetTransferData :- " + transactionId);
            Map<String, Object> input = new HashMap<>();
            input.put("transactionId", transactionId);
            HashMap<String, Object> fundRequestHeaders = new HashMap<>();
            System.out.println("After MAP creation");
            transactionDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(input)
                    .withRequestHeaders(fundRequestHeaders).build().getResponse();
            LOG.debug("Response of transactionDataResponse:- " + transactionDataResponse);
            System.out.println("Response of transactionDataResponse:- " + transactionDataResponse);
            LOG.debug("Exit successfully on getTransferDataByTransactionId of GetTransferData");
        } catch (Exception e) {
            LOG.debug("Enter to Exception of getTransferDataByTransactionId of GetTransferData", e);
            System.out.println("Exception in GetTransferData : " + e.getMessage());
        }
//        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:" + docWithoutContentResponse);

        return transactionDataResponse;

    }
}
