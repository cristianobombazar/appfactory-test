package com.kony.sbg.resources.impl;

import com.auth0.jwt.JWT;
import com.dbp.core.constants.DBPConstants;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.api.RetrieveDocumentResource;
import com.kony.sbg.util.*;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RetrieveDocumentResourceImpl implements RetrieveDocumentResource {

    private static final Logger LOG = Logger.getLogger(RetrieveDocumentResourceImpl.class);

    @Override
    public Result readDocumentDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws ApplicationException, HttpCallException, DBPApplicationException {

        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::readDocumentDetails()");
        Result result = new Result();

        Boolean validateAuthToken = validateToken(inputArray, dcRequest, dcResponse);
        if (!validateAuthToken) {
            LOG.debug("RetrieveDocumentResourceImpl::readDocumentDetails::validateAuthToken::" + validateAuthToken);
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100034.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100034.getMessage());
            return result;
        }

        Map<String, String> inputParamMap = HelperMethods.getInputParamMap(inputArray);
        String transactionId = inputParamMap.get("transactionId");
        String documentId = inputParamMap.get("documentId");

        LOG.debug("RetrieveDocumentResourceImpl::readDocumentDetails::transactionId::" + transactionId + " documentId::" + documentId);
        if (StringUtils.isNotBlank(transactionId)) {

            Result documentMetaDataByTransactionId = getDocumentMetaDataByTransactionId(transactionId, dcRequest);
            LOG.debug("RetrieveDocumentResourceImpl::readDocumentDetails::documentMetaDataByTransactionId::" + documentMetaDataByTransactionId);
            return documentMetaDataByTransactionId;

        } else if (StringUtils.isNotBlank(documentId)) {

            Result documentByDocumentId = getDocumentByDocumentId(documentId, dcRequest);
            LOG.debug("RetrieveDocumentResourceImpl::readDocumentDetails::documentByDocumentId::" + documentByDocumentId);
            return documentByDocumentId;
        } else {
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100036.getErrorCode()));
            result.addParam("errMsg", "Requested Payment Reference Id or document id not found.");
        }

        return result;
    }

    private Result getDocumentByDocumentId(String documentId, DataControllerRequest dcRequest) throws HttpCallException {
        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentByDocumentId() documentId:" + documentId);
        StringBuilder sb = new StringBuilder();
        String transactionIdFilter = sb.append("id").append(DBPUtilitiesConstants.EQUAL).append("'").append(documentId).append("'").toString();
        LOG.debug("### RetrieveDocumentResourceImpl::getDocumentByDocumentId() filter:" + transactionIdFilter);
        Map<String, String> input = new HashMap<>();
        input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
        LOG.debug("### RetrieveDocumentResourceImpl::getDocumentByDocumentId() input:" + input);

        return HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), SBGConstants.DOCUMENT_INFO_WITH_DOC);

    }

    private Result getDocumentMetaDataByTransactionId(String transactionId, DataControllerRequest dcRequest) throws HttpCallException, DBPApplicationException {
        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId()");

        String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
        String operationName = SbgURLConstants.OPERATION_DOCUMENT_WITH_CONTENT;

        StringBuilder sb = new StringBuilder();
        String transactionIdFilter = sb.append("transactionId").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();
        LOG.debug("### RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() filter:" + transactionIdFilter);

        Map<String, Object> input = new HashMap<>();
        input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
        input.put(DBPUtilitiesConstants.SELECT, "id,transactionId");

        HashMap<String, Object> fundRequestHeaders = new HashMap<>();

        String docWithoutContentResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                .withOperationId(operationName).withRequestParameters(input)
                .withRequestHeaders(fundRequestHeaders).build().getResponse();
        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:" + docWithoutContentResponse);
        //return HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), SBGConstants.DOCUMENT_INFO_WITHOUT_DOC);
        Result result = new Result();
        result.appendJson(docWithoutContentResponse);
        return result;
    }

    public Boolean validateToken(Object[] inputArray, DataControllerRequest dcRequest,
                                 DataControllerResponse dcResponse) {
        LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::validateToken()");
        try {
            String header = SBGCommonUtils.getServerPropertyValue("PING_AUTHORIZATION", dcRequest);
            LOG.debug("### RetrieveDocumentResourceImpl::validateToken() header:" + header);
            String authToken = dcRequest.getHeader(header);
            LOG.debug("### RetrieveDocumentResourceImpl::validateToken() authToken:" + authToken);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(authToken)) {
                if (authToken.contains("Bearer")) {
                    authToken = authToken.split(" ")[1];
                } else {
                    return false;
                }

                Date exp = JWT.decode(authToken).getExpiresAt();
                if (!SBGCommonUtils.timeStampCompare(exp)) {
                    return false;
                }

                Boolean isTokenValid = true;
                String endPoint = "/ext/sbg/customer/oauth/jwks";
                isTokenValid = PingTokenValidator.isValidToken(authToken, dcRequest, endPoint);
                return isTokenValid;
            } else {
                return false;
            }

        } catch (Exception e) {
            LOG.debug("RetrieveDocumentResourceImpl::validateToken::TokenIsInvalid", e);
            return false;
        }
    }
}

