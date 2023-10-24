package com.kony.sbg.backend.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.DomesticPaymentAPIBackendDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SBGUtil;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;

import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;

public class DomesticPaymentAPIBackendDelegateImpl implements DomesticPaymentAPIBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(DomesticPaymentAPIBackendDelegateImpl.class);

    public InterBankFundTransferDTO createTransactionWithoutApproval(InterBankFundTransferBackendDTO input,
            DataControllerRequest request, String... branchCode) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval");
        InterBankFundTransferDTO resultDTO = new InterBankFundTransferDTO();
        resultDTO.setpaymentMethod(input.getPaymentType());
        String branch = branchCode.length > 0 ? branchCode[0] : "";
        Map<String, Object> requestParameters = buildRequestForSubmitPayment(input, request, resultDTO, branch);
        Map<String, Object> requestHeaders = getRequestHeader(request, resultDTO);
        String submitPaymentRsp = null;

        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestParameters: "
                + requestParameters.toString());
        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::RequestHeaders: "
                + requestHeaders.toString());

        if (resultDTO.getDbpErrCode() != null && resultDTO.getDbpErrCode().isEmpty() == false) {
            LOG.error(
                    "DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval --> build Request or Request Failed");
            return resultDTO;
        }

        try {
            submitPaymentRsp = DBPServiceExecutorBuilder.builder()
                    .withServiceId(SbgURLConstants.SERVICE_SBG_DOMESTIC_PAYMENT)
                    .withOperationId(SbgURLConstants.OPERATION_SUBMITDOMESTICPAYMENT)
                    .withRequestParameters(requestParameters).withRequestHeaders(requestHeaders)
                    .withPassThroughOutput(true).build().getResponse();
            LOG.error("Response of Service : " + SbgURLConstants.SERVICE_SBG_DOMESTIC_PAYMENT + " Operation : "
                    + SbgURLConstants.OPERATION_SUBMITDOMESTICPAYMENT + ":: " + submitPaymentRsp);

        } catch (DBPApplicationException e) {
            LOG.error("Error occured while submitting payment: " + e);
            resultDTO = populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
            return resultDTO;
        }

        JSONObject serviceResponse = new JSONObject(submitPaymentRsp);
        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::Response_Json:: "
                + serviceResponse.toString());
        if (serviceResponse != null && serviceResponse.length() > 0) {
            String updateResponse = updateSubmitPaymentResponse(input.getTransactionId(), serviceResponse);
            if (serviceResponse.has("status") && serviceResponse.has("uniqueIdentifier")) {
                LOG.debug("createTransactionWithoutApproval::Payment Submitted Successfully");
            } else {
                if (serviceResponse.has("Err")
                        && serviceResponse.getJSONObject("Err").optJSONArray("Errs").length() > 0) {
                    LOG.error("createTransactionWithoutApproval::Technical error occurred in submit payment API");
                    populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100053);
                } else {
                    LOG.error("createTransactionWithoutApproval::Error occurred in submit payment API");
                    populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100025);
                }
            }
        } else {
            LOG.error("An unknown error occurred while processing the submit payment request");
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100054);
        }
        if (serviceResponse.has("uniqueIdentifier")) {
            String uniqId = serviceResponse.getString("uniqueIdentifier");
            resultDTO.setReferenceId(uniqId);
        } else {
            resultDTO.setReferenceId(input.getTransactionId());
        }

        resultDTO.setConfirmationNumber(input.getConfirmationNumber());
        LOG.debug(
                "DomesticPaymentAPIBackendDelegateImpl::createTransactionWithoutApproval::FinalResultDTO ReferenceID: "
                        + resultDTO.getReferenceId() + " Confirmation Number: " + resultDTO.getConfirmationNumber());
        return resultDTO;
    }

    private Map<String, Object> buildRequestForSubmitPayment(InterBankFundTransferBackendDTO input,
            DataControllerRequest request, InterBankFundTransferDTO resultDTO, String branchCode) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment");
        Map<String, Object> requestParameters = new HashMap<String, Object>();
        String exchangeRate = "", fxCoverNumber = "", multipleDeals = "", bicTransformation = "";

        /*
         * The requestId param is present when the transaction is approved,
         *  so the customer is retrieved from the bbRequest.
         * On transactions without approval the customer is the logged one.
         */
        String requestId = request.getParameter("requestId");
        Map<String, Object> customer = StringUtils.isNotEmpty(requestId) ?
                getCustomerMapByBbRequest(requestId) :
                CustomerSession.getCustomerMap(request);

        requestParameters.put("msgId", CommonUtils.generateUniqueIDHyphenSeperated(0, 20));
        requestParameters.put("createdDtTm", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss"));
        requestParameters.put("customerName", CustomerSession.getCustomerCompleteName(customer));
        requestParameters.put("transactionId", input.getConfirmationNumber());
        requestParameters.put("customerId", CustomerSession.getCustomerId(customer));

        try {
            JSONObject json = SBGUtil.getCompanyData4mParty(request);
            JSONObject extnData = json.getJSONObject("extensionData");

            String country = extnData.getString("legalcountry").length() != 2 ? getCountryCode(extnData.getString("legalcountry")) : extnData.getString("legalcountry");
            
            requestParameters.put("custAddressLine1", extnData.getString("legaladdressLine1"));
            requestParameters.put("custAddressLine2", extnData.getString("legaladdressLine2"));
            requestParameters.put("custAddressLine3", extnData.getString("legaladdressLine3"));
            requestParameters.put("custCountryName", country);
            requestParameters.put("custState", extnData.getString("legalstate"));
            requestParameters.put("custCity", extnData.getString("legalcityName"));
            requestParameters.put("custZipCode", extnData.getString("legalzipCode"));
        } catch (Exception e) {
            LOG.debug("DomesticPaymentAPIBackendDelegateImpl: Failed to get customer address");
        }

        if (SBGConstants.URGENT_PAYMENT_TYPE.equalsIgnoreCase(input.getPaymentType())) {
            requestParameters.put("paymentPriority", "HIGH");
        } else if (SBGConstants.NORMAL_PAYMENT_TYPE.equalsIgnoreCase(input.getPaymentType())) {
            requestParameters.put("paymentPriority", "NORM");
        }

        LOG.debug("Customer Map ->" + customer);

        try {
            bicTransformation = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BIC_TRANSFORMATION, request);
        } catch (Exception e) {
            LOG.error("Exception occured while fetching server properties: " + e.getStackTrace());
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
            return requestParameters;
        }

        try {
            if (input.getScheduledDate() != null && input.getScheduledDate().isEmpty() == false) {
                requestParameters.put("scheduledDate",
                        SBGCommonUtils.getFormattedDate(SbgURLConstants.PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                                SbgURLConstants.PATTERN_YYYY_MM_DD, input.getScheduledDate()));

            } else {
                LOG.error("buildRequestForSubmitPayment::scheduled date not found");
                populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
                return requestParameters;
            }
        } catch (ParseException e) {
            LOG.error("Exception occured while converting date format: " + e);
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100027);
            return requestParameters;
        }

        Map<String, String> userDetails = getUserNameMailId(CustomerSession.getCustomerId(customer), request,
                resultDTO);

        if (resultDTO.getDbpErrCode() != null && resultDTO.getDbpErrCode().isEmpty() == false) {
            LOG.error("buildRequestForSubmitPayment::Error occurred while fetching customer user details");
            return requestParameters;
        }

        requestParameters.put("userName", userDetails.get("userName"));
        requestParameters.put("userMailId", userDetails.get("userEmail"));
        requestParameters.put("fromAccountNumber", input.getFromAccountNumber());
        requestParameters.put("fromAccountCurrency", input.getFromAccountCurrency());

        /*
         * fromAccountswiftcode not coming in payload and not available in table. Need
         * to get this from payload. Currently get it from server properties
         */
        requestParameters.put("fromAccountswiftcode",
                getFromAccountSwiftCode(input.getFromAccountNumber(), request, bicTransformation));
        requestParameters.put("transactionCurrency", input.getTransactionCurrency());

        if (input.getTransactionAmount() != null && input.getTransactionAmount().isEmpty() == false) {
            requestParameters.put("transactionAmount", input.getTransactionAmount());
        } else {
            LOG.error("buildRequestForSubmitPayment::transaction amount not found");
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100010);
            return requestParameters;
        }

        requestParameters.put("beneSwiftCode", getBICTransformedSwiftCode(input.getSwiftCode(), bicTransformation));

        requestParameters.put("beneficiaryName", getBeneName(input, request));

        LOG.debug("buildRequestForSubmitPayment::Input = " + input.toString());

        if (!StringUtils.isEmpty(input.getBeneficiaryZipcode())) {
            requestParameters.put("beneZipCode", input.getBeneficiaryZipcode());
        } else {
            requestParameters.put("beneZipCode", "");
        }

        if (!StringUtils.isEmpty(input.getBeneficiaryCity())) {
            requestParameters.put("beneCity", input.getBeneficiaryCity());
        } else {
            requestParameters.put("beneCity", "");
        }

        // if(input.getClearingCode().isEmpty() || input.getClearingCode() == null){
        // if (StringUtils.isEmpty(input.getClearingCode())) {
        // requestParameters.put("beneBranchCode", "");
        // } else {
        // requestParameters.put("beneBranchCode", branchCode);
        // }
        requestParameters.put("beneBranchCode", branchCode);

        // if (input.getBeneficiaryState() != null &&
        // input.getBeneficiaryState().isEmpty() == false) {
        if (!StringUtils.isEmpty(input.getBeneficiaryState())) {
            requestParameters.put("beneState", input.getBeneficiaryState());
        } else {
            LOG.error("buildRequestForSubmitPayment --> beneficiaryState not found");
            requestParameters.put("beneState", "");
        }

        if (!StringUtils.isEmpty(input.getBeneficiarycountry())) {
            requestParameters.put("beneCountryName", getCountryCode(input.getBeneficiarycountry()));
        } else {
            requestParameters.put("beneCountryName", "");
        }

        if (!StringUtils.isEmpty(input.getBeneficiaryAddressLine1())) {
            requestParameters.put("beneAddressLine1", input.getBeneficiaryAddressLine1());
        } else {
            requestParameters.put("beneAddressLine1", "");
        }

        // if (input.getBeneficiaryAddressLine2() != null &&
        // input.getBeneficiaryAddressLine2().isEmpty() == false) {
        if (!StringUtils.isEmpty(input.getBeneficiaryAddressLine2())) {
            requestParameters.put("beneAddressLine2", input.getBeneficiaryAddressLine2());
        } else {
            requestParameters.put("beneAddressLine2", "");
        }

        requestParameters.put("toAccountNumber", input.getToAccountNumber());

        /* beneficiaryRefCode & statementRefCode are mandatory */
        requestParameters.put("beneficiaryRefCode", request.getParameter("beneficiaryReference"));
        requestParameters.put("statementRefCode", request.getParameter("statementReference"));

        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::buildRequestForSubmitPayment --> finished building request");
        return requestParameters;
    }

    private Map<String, Object> getCustomerMapByBbRequest(String requestId) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::getCustomerMapByBbRequestId");
        Map<String, Object> customerMap = new HashMap<>();

        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getCustomerMapByBbRequestId::requestId: " + requestId);
        ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
        BBRequestDTO requestDTO = approvalQueueBusinessDelegate.getBbRequest(requestId);

        if (requestDTO != null && StringUtils.isNotEmpty(requestDTO.getCreatedby())) {
            String createdBy = requestDTO.getCreatedby();
            customerMap.put("customer_id", createdBy);
            LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getCustomerMapByBbRequestId::createdby: " + createdBy);

            CustomerBusinessDelegate customerBusinessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(CustomerBusinessDelegate.class);
            JSONObject customerJsonObject = customerBusinessDelegate.getUserDetails(createdBy);

            if (customerJsonObject != null) {
                LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getCustomerMapByBbRequestId::customerJsonObject: " + customerJsonObject);
                customerJsonObject.keySet()
                        .forEach(key -> customerMap.put(key, customerJsonObject.get(key)));
            } else {
                LOG.error("getCustomerMapByBbRequestId --> userDetails not found");
            }
        } else {
            LOG.error("getCustomerMapByBbRequestId --> bbRequest not found");
        }
        return customerMap;
    }

    // The below function is used for fetching customers contact name and
    // communication details
    private Map<String, String> getUserNameMailId(String customerId, DataControllerRequest request,
            InterBankFundTransferDTO resultDTO) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::getUserNameMailId");

        Map<String, String> userDetails = new HashMap<>();

        // [AH]The below block is used to fetch customer contact details based on
        // partyId.
        // This change is for SBG-588 to improve the performance to fetch the contact
        // details
        JSONObject custObj = SBGUtil.getCompanyData4mParty(request);
        if (custObj != null) {
            String fname = custObj.getString("firstName");
            String lname = custObj.getString("lastName");
            if ((!SBGCommonUtils.isStringEmpty(fname) || !SBGCommonUtils.isStringEmpty(lname))) {

                String cpe[] = SBGCommonUtils.getCodePhoneAddress(custObj.getJSONArray("addresses"));
                userDetails.put("userName", fname + " " + lname);
                userDetails.put("userEmail", cpe[2]);

                LOG.debug("getUserNameMailId::userDetails: " + userDetails);
                return userDetails;
            }
        }

        // // The below block fetches the customer details based on
        // backofficeidentifier.
        // // Leaving the below code as is to reduce the risk of details not being found
        // // during transfer.
        // // Most of the scenarios, the data should be fetched from above block and the
        // // below code will be redundant.
        // Map<String, Object> requestParams = new HashMap<>();
        // String filter = "", coreCustomerId = "", userName = "", userMailId = "";
        // filter = "customerId " + DBPUtilitiesConstants.EQUAL + customerId;
        // requestParams.put(DBPUtilitiesConstants.FILTER, filter);

        // JSONObject contractCustomerRsp = getContractCustomers(requestParams);

        // if (contractCustomerRsp != null &&
        // contractCustomerRsp.has("contractcustomers")
        // && contractCustomerRsp.getJSONArray("contractcustomers").length() > 0) {
        // JSONObject contractCustomer =
        // contractCustomerRsp.getJSONArray("contractcustomers").getJSONObject(0);
        // coreCustomerId = contractCustomer.optString("coreCustomerId");
        // LOG.debug("getUserNameMailId::coreCustomerId: " + coreCustomerId);
        // } else {
        // LOG.error("getUserNameMailId::Error occurred while fetching contract
        // customer");
        // populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100036);
        // }

        // Map<String, Object> requestParameter = new HashMap<>();
        // Map<String, Object> requestHeaders = new HashMap<>();
        // try {
        // String backOfficeIdentifier =
        // SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BACKOFFICEIDENTIFIER,
        // request);
        // LOG.debug("getUserNameMailId::backOfficeIdentifier: " +
        // backOfficeIdentifier);
        // String branchIdReference =
        // SBGCommonUtils.getServerPropertyValue(SbgURLConstants.BRANCH_ID_REFERENCE,
        // request);
        // String partyAuthKey =
        // SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PARTYMS_AUTHORIZATION_KEY,
        // request);

        // requestParameter.put("alternateIdentifierNumber", branchIdReference + "-" +
        // coreCustomerId);
        // requestParameter.put("alternateIdentifierType", backOfficeIdentifier);
        // requestHeaders.put("X-API-Key", partyAuthKey);
        // LOG.debug("getUserNameMailId::party::requestParameter: " + requestParameter);
        // LOG.debug("getUserNameMailId::party::requestHeaders: " + requestHeaders);
        // } catch (Exception e) {
        // populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
        // LOG.error("getUserNameMailId::Error occurred while fetching server
        // properties: " + e.getStackTrace());
        // }

        // JSONObject partyData = getPartyDetails(requestParameter, requestHeaders);

        // if (partyData != null && partyData.has("parties") &&
        // partyData.getJSONArray("parties").length() > 0) {
        // JSONObject partyDetails = partyData.getJSONArray("parties").getJSONObject(0);
        // String firstName = partyDetails.optString("firstName");
        // String lastName = partyDetails.optString("lastName");
        // if (StringUtils.isNotBlank(firstName)) {
        // userName = firstName;
        // }

        // if (StringUtils.isNotBlank(lastName)) {
        // userName = userName + " " + lastName;
        // }

        // JSONArray addresses = partyDetails.getJSONArray("addresses");
        // Optional<Object> communicationEntity =
        // StreamSupport.stream(addresses.spliterator(), true)
        // .filter(item -> ((JSONObject) item).has("communicationType")
        // && ((JSONObject)
        // item).getString("communicationType").equalsIgnoreCase("Email"))
        // .findFirst();
        // JSONObject emailCommunication;
        // if (communicationEntity.isPresent()) {
        // emailCommunication = (JSONObject) communicationEntity.get();
        // userMailId = emailCommunication.optString("electronicAddress");
        // }
        // } else {
        // populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100037);
        // LOG.error("getUserNameMailId::Error occurred while fetching server
        // properties: ");
        // }

        // LOG.debug("getUserNameMailId::userName: " + userName + " userEmail: " +
        // userMailId);
        // userDetails.put("userName", userName);
        // userDetails.put("userEmail", userMailId);

        return userDetails;
    }

    private JSONObject getPartyDetails(Map<String, Object> requestParams, Map<String, Object> requestHeaders) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::getPartyDetails");
        String serviceName = SbgURLConstants.SERVICE_PARTYMSSERVICE;
        String operationName = SbgURLConstants.OPERATION_GETPARTYDETAILS;
        JSONObject serviceResponse = null;

        try {

            String partyResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(requestParams)
                    .withRequestHeaders(requestHeaders).build().getResponse();
            LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_PARTYMSSERVICE + " Operation : "
                    + SbgURLConstants.OPERATION_GETPARTYDETAILS + ":: " + partyResponse);
            serviceResponse = new JSONObject(partyResponse);
        }

        catch (JSONException jsonExp) {
            LOG.error("JSONExcpetion occured while fetching party details: ", jsonExp);
            return null;
        } catch (Exception exp) {
            LOG.error("Excpetion occured while fetching party details: ", exp);
            return null;
        }

        return serviceResponse;
    }

    private String getFromAccountSwiftCode(String fromAccountNumber, DataControllerRequest request,
            String bicTransformation) {
        String fromAccountSwiftCode = "";
        try {
            // String accountDetails1 = SBGCommonUtils.getServerPropertyValue(key, request);
            String accountDetails = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from
                                                                                             // Arrangement Extension
            LOG.debug("&&&&&BICFromExtension: " + accountDetails);
            if (StringUtils.isBlank(accountDetails)) {
                String key = "AE_" + fromAccountNumber + "_EXTENSIONDATA";
                LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:key: " + key);
                accountDetails = SBGCommonUtils.getClientAppValue(key, request);
                LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:accountDetails: "
                        + accountDetails);
                if (accountDetails != null && accountDetails.isEmpty() == false) {
                    JSONObject accountObj = new JSONObject(accountDetails);
                    if (accountObj.has("bic")) {
                        fromAccountSwiftCode = accountObj.getString("bic");
                        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getFromAccountSwiftCode:before: "
                                + fromAccountSwiftCode);
                        LOG.debug("&&&&&BICFromServerProps: " + fromAccountSwiftCode);
                        fromAccountSwiftCode = getBICTransformedSwiftCode(fromAccountSwiftCode, bicTransformation);
                        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getFromAccountSwiftCode: "
                                + fromAccountSwiftCode);
                    }
                }
            } else {
                fromAccountSwiftCode = getBICTransformedSwiftCode(accountDetails, bicTransformation);
                LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getFromArrangementAccountSwiftCode: "
                        + fromAccountSwiftCode);
            }

        } catch (Exception e) {
            LOG.error("Error occured while fetching environment properties: " + e.getStackTrace());
        }

        return fromAccountSwiftCode;
    }

    private String getBICTransformedSwiftCode(String bic, String bicTransformation) {
        LOG.debug("getBICTransformedSwiftCode::bic::" + bic);
        String bicTransformed = bic;
        if (SBGCommonUtils.isStringEmpty(bicTransformation) == false) {
            if (bicTransformation.equals("true") && (bic.length() >= SbgURLConstants.BIC_TRANSFORMATION_MIN_LENGTH)) {
                LOG.debug("getBICTransformedSwiftCode::bic.length()::" + bic.length());
                StringBuilder swiftCode = new StringBuilder(bic);
                swiftCode.setCharAt(7, '0');
                bicTransformed = swiftCode.toString();
                LOG.debug("getBICTransformedSwiftCode::bicTransformed::" + bicTransformed);
            }
        }
        return bicTransformed;
    }

    private String getCountryCode(String beneficiarycountry) {
        String countryCode = "";
        String countryRsp = "";
        String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
        String operationName = SbgURLConstants.OPERATION_DBXDB_COUNTRY_GET;
        Map<String, Object> requestParams = new HashMap<String, Object>();
        String filter = "";
        filter = "Name " + DBPUtilitiesConstants.EQUAL + "'" + beneficiarycountry + "'";
        requestParams.put(DBPUtilitiesConstants.FILTER, filter);
        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::requestParams:: " + requestParams);
        try {
            countryRsp = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
                    .withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
            JSONObject respObj = new JSONObject(countryRsp);
            if (respObj.has("country")) {
                JSONArray countries = respObj.getJSONArray("country");
                if (countries.length() > 0) {
                    JSONObject countryObj = countries.getJSONObject(0);
                    if (countryObj.has("Code")) {
                        countryCode = countryObj.getString("Code");
                        LOG.debug("DomesticPaymentAPIBackendDelegateImpl::getCountryCode_Value:: " + countryCode);
                    }
                }
            }

        } catch (DBPApplicationException e) {
            LOG.error("Error occured while fetching country: " + e.getStackTrace());
        }
        return countryCode;
    }

    private String getBeneName(InterBankFundTransferBackendDTO input, DataControllerRequest request) {
        String beneName = "";
        if (input.getBeneficiaryName() == null || input.getBeneficiaryName().isEmpty()) {
            if (request.containsKeyInRequest("entityType")) {
                if (SbgURLConstants.BENE_INDIVIDUAL.equals(request.getParameter("entityType"))) {
                    if (request.getParameter("firstName") != null
                            && request.getParameter("firstName").isEmpty() == false) {
                        beneName = request.getParameter("firstName") + " ";
                    }

                    if (request.getParameter("lastName") != null
                            && request.getParameter("lastName").isEmpty() == false) {
                        beneName = beneName + request.getParameter("lastName");
                    }
                    beneName = beneName.trim();
                }
            } else {
                LOG.debug("buildRequestForSubmitPayment::BeneName is empty and entityType also not found");
            }

        } else {
            beneName = input.getBeneficiaryName();
        }

        LOG.debug("buildRequestForSubmitPayment::BeneName: " + beneName);
        return beneName;

    }

    /* Sprint 6 does not support multiple deals. Need to form the string here */
    private String getMultipleDealDetails(JSONArray quotes, String fromAccountCurrency, String toAccountCurrency,
            String amount) {
        String multipleDeals = "";
        StringBuilder multipleDealsSb = new StringBuilder();
        for (Object object : quotes) {
            JSONObject quote = (JSONObject) object;
            String exchangeRate = quote.getString("exchangeRate");
            String coverNumber = quote.getString("coverNumber");
            StringBuilder dealsSb = new StringBuilder();
            dealsSb.append("XchgRate:");
            dealsSb.append(exchangeRate);
            dealsSb.append(toAccountCurrency);
            dealsSb.append("/");
            dealsSb.append(fromAccountCurrency);
            dealsSb.append(" CtrctId:");
            dealsSb.append(coverNumber);
            dealsSb.append(" Amount:");
            dealsSb.append(amount);
            dealsSb.append(toAccountCurrency);
            dealsSb.append("_");
            multipleDealsSb.append(dealsSb);
        }

        if (multipleDealsSb.charAt(multipleDealsSb.length() - 1) == '_') {
            multipleDealsSb.deleteCharAt(multipleDealsSb.length() - 1);
        }
        return multipleDeals;
    }

    private Map<String, Object> getRequestHeader(DataControllerRequest request,
            InterBankFundTransferDTO resultDTO) {
        Map<String, Object> requestHeaders = new HashMap<String, Object>();
        Result resultCache = null;
        String authorization = "";

        try {
            resultCache = SBGCommonUtils.cacheFetchPingToken("Authorization", request);
        } catch (Exception e) {
            LOG.error("Exception occurred while fetching authorization from session: " + e.getStackTrace());
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
            return requestHeaders;
        }

        if (resultCache != null && resultCache.hasParamByName("Authorization")) {
            authorization = resultCache.getParamValueByName("Authorization").toString();
            requestHeaders.put("Authorization", authorization);
        } else {
            LOG.error("DomesticPaymentAPIBackendDelegateImpl::getRequestHeader --> IBM-Gateway authentication failed");
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100024);
            return requestHeaders;
        }

        try {
            String clientId = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.DOM_X_IBM_CLIENT_ID, request);
            String clientSecret = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.DOM_X_IBM_CLIENT_SECRET,
                    request);
            if (clientId != null && clientId.isEmpty() == false && clientSecret != null
                    && clientSecret.isEmpty() == false) {
                requestHeaders.put("X-IBM-Client-Id", clientId);
                requestHeaders.put("X-IBM-Client-Secret", clientSecret);
            } else {
                LOG.error(
                        "DomesticPaymentAPIBackendDelegateImpl::getRequestHeader --> ClientId and Client Secret are null or empty");
                populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
                return requestHeaders;
            }
        } catch (Exception e) {
            LOG.error("Error occured while fetching environment properties: " + e.getStackTrace());
            populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100026);
            return requestHeaders;
        }

        requestHeaders.put("x-fapi-interaction-id", SBGCommonUtils.generateRandomUUID().toString());
        // requestHeaders.put("PaymentPriority", resultDTO.getpaymentMethod());
        requestHeaders.put("transacted", "false");

        return requestHeaders;
    }

    private InterBankFundTransferDTO populateErrorDetails(InterBankFundTransferDTO resultDTO,
            SbgErrorCodeEnum errorCodeEnum) {
        resultDTO.setDbpErrCode(errorCodeEnum.getErrorCodeAsString());
        resultDTO.setDbpErrMsg(errorCodeEnum.getMessage());
        return resultDTO;
    }

    public String updateSubmitPaymentResponse(String referenceId, JSONObject serviceResponse) {
        LOG.error("Entry --> DomesticPaymentAPIBackendDelegateImpl::updateSubmitPaymentResponse entry");
        String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
        String operationName = SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_UPDATE;
        Map<String, Object> requestParams = new HashMap<String, Object>();
        String updateResponse = "";
        String uniqId = "";

        if (serviceResponse.has("uniqueIdentifier")) {
            uniqId = serviceResponse.getString("uniqueIdentifier");
        }

        String response = serviceResponse.toString();
        if (response.length() > 1024) {
            response = response.substring(0, 1000);
        }

        requestParams.put("referenceId", referenceId);
        requestParams.put("submitPaymentId", uniqId);
        requestParams.put("submitPaymentResponse", response);

        try {
            updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
                    .withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
            LOG.debug("updateSubmitPaymentResponse::updateResponse: " + updateResponse);
        }

        catch (JSONException jsonExp) {
            LOG.error("JSONExcpetion occured while updating the InterBankfundtransfersRefData: ", jsonExp);
            return null;
        } catch (Exception exp) {
            LOG.error("Excpetion occured while updating the InterBankfundtransfersRefData: ", exp);
            return null;
        }

        return updateResponse;
    }

    private JSONObject getContractCustomers(Map<String, Object> requestParams) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::getCoreCustomerId");
        String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
        String operationName = SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET;
        JSONObject serviceResponse = null;
        LOG.debug("getUserNameMailId::filter:: " + requestParams);
        try {
            String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
                    .withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
            LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
                    + SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET + ":: " + updateResponse);
            serviceResponse = new JSONObject(updateResponse);
        }

        catch (JSONException jsonExp) {
            LOG.error("JSONExcpetion occured while fetching contract customers ", jsonExp);
            return null;
        } catch (Exception exp) {
            LOG.error("Excpetion occured while fetching contract customers: ", exp);
            return null;
        }

        return serviceResponse;
    }

    private JSONObject getCustomerData4mParty(DataControllerRequest request, String custId) {

        String corePartyId = getCorePartyId4mBackendIdentifier(custId);
        if (!SBGCommonUtils.isStringEmpty(corePartyId)) {
            return getPartyDetailsById(request, corePartyId);
        }

        return null;
    }

    private String getCorePartyId4mBackendIdentifier(String customerId) {
        Map<String, Object> requestParams = new HashMap<>();
        String filter = "", corePartyId = "";

        filter = "Customer_id " + DBPUtilitiesConstants.EQUAL + customerId;
        filter = filter + " " + DBPUtilitiesConstants.AND + " ";
        filter = filter + "BackendType " + DBPUtilitiesConstants.EQUAL + "CORE";
        requestParams.put(DBPUtilitiesConstants.FILTER, filter);

        String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
        String operationName = SbgURLConstants.OPERATION_DBXDB_BACKENDIDENTIFIER_GET;

        JSONObject serviceResponse = null;
        LOG.debug("getCorePartyId4mBackendIdentifier::filter:: " + requestParams);
        try {
            String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
                    .withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
            LOG.debug("getCorePartyId4mBackendIdentifier ::: Response of Service : "
                    + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
                    + SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET + ":: " + updateResponse);
            serviceResponse = new JSONObject(updateResponse);

            if (serviceResponse != null && serviceResponse.has("backendidentifier")
                    && serviceResponse.getJSONArray("backendidentifier").length() > 0) {
                JSONObject contractCustomer = serviceResponse.getJSONArray("backendidentifier").getJSONObject(0);
                corePartyId = contractCustomer.optString("BackendId");
                LOG.debug("getCorePartyId4mBackendIdentifier::corePartyId: " + corePartyId);
            }
        }

        catch (JSONException jsonExp) {
            LOG.error("getCorePartyId4mBackendIdentifier:: JSONExcpetion occured while fetching contract customers ",
                    jsonExp);
            return corePartyId;
        } catch (Exception exp) {
            LOG.error("getCorePartyId4mBackendIdentifier:: Excpetion occured while fetching contract customers: ", exp);
            return corePartyId;
        }
        return corePartyId;
    }

    private JSONObject getPartyDetailsById(DataControllerRequest request, String partyId) {
        LOG.debug("Entry --> DomesticPaymentAPIBackendDelegateImpl::getPartyDetailsById");
        String serviceName = SbgURLConstants.SERVICE_PARTYMSSERVICE;
        String operationName = SbgURLConstants.OPERATION_GETPARTYDETAILSBYID;
        JSONObject serviceResponse = null;

        try {
            Map<String, Object> requestParameter = new HashMap<>();
            Map<String, Object> requestHeaders = new HashMap<>();
            String partyAuthKey = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.PARTYMS_AUTHORIZATION_KEY,
                    request);

            requestParameter.put("partyid", partyId);
            requestHeaders.put("X-API-Key", partyAuthKey);

            String partyResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
                    .withOperationId(operationName).withRequestParameters(requestParameter)
                    .withRequestHeaders(requestHeaders).build().getResponse();

            LOG.debug("getPartyDetailsById ::: Response of Service : " + serviceName + " Operation : "
                    + operationName + ":: " + partyResponse);
            serviceResponse = new JSONObject(partyResponse);
        }

        catch (JSONException jsonExp) {
            LOG.error("JSONExcpetion occured while fetching party details: ", jsonExp);
            return null;
        } catch (Exception exp) {
            LOG.error("Excpetion occured while fetching party details: ", exp);
            return null;
        }

        return serviceResponse;
    }

}
