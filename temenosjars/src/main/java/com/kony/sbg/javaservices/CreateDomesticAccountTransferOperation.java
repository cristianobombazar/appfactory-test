package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.helpers.SbgDocTransactionRelationHelper;
import com.kony.sbg.resources.api.SbgDomesticFundTransferResource;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgAlertUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.payeeservices.resource.api.ExternalPayeeResource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class CreateDomesticAccountTransferOperation implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(CreateDomesticAccountTransferOperation.class);
    public static final int FILENAME_INDEX = 0;
    public static final int FILETYPE_INDEX = 1;
    public static final int FILECONTENTS_INDEX = 2;
    public static final int UNIQUE_ID_LENGTH = 32;

    ExternalPayeeResource externalPayeeResource = DBPAPIAbstractFactoryImpl.getInstance()
            .getFactoryInstance(ResourceFactory.class).getResource(ExternalPayeeResource.class);
    ObjectMapper objectMapper = new ObjectMapper();

    public CreateDomesticAccountTransferOperation() {
    }

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        String referenceId = null;
        new Result();
        Result result = null;
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String dbpErrMsg = null;
        boolean savePayee = false;
        // LOG.debug("###################Entered domestic service ----------------");
        try {
            SbgDomesticFundTransferResource domesticFundTransferResource = (SbgDomesticFundTransferResource) ((ResourceFactory) DBPAPIAbstractFactoryImpl
                    .getInstance().getFactoryInstance(ResourceFactory.class))
                    .getResource(SbgDomesticFundTransferResource.class);

            String beneficiaryCategory = inputParams.get("payeeType") == null ? ""
                    : inputParams.get("payeeType").toString();

            if (SBGConstants.SAVE_PAYEE.equals(beneficiaryCategory)) {
                result = beneficiarySave(methodID, inputArray, request, response);
                dbpErrMsg = result.getParamValueByName("dbpErrMsg");
                savePayee = true;
            }

            if (StringUtils.isNotBlank(dbpErrMsg)) {
                return result;
            }
            // LOG.debug("Before createTransaction ----------------");
            String firstArrayToJson = objectMapper.writeValueAsString(inputArray);
            Result transactionResult = domesticFundTransferResource.createTransaction(methodID, inputArray, request,
                    response);
            // LOG.debug("After createTransaction ----------------");
            dbpErrMsg = transactionResult.getParamValueByName("dbpErrMsg");

            if (StringUtils.isNotBlank(dbpErrMsg)) {
                return transactionResult;
            } else {
                referenceId = transactionResult.getParamValueByName("referenceId");

                if (inputParams.containsKey("uploadedattachments") && inputParams.containsKey("validate")) {
                    String isValidate = (String) inputParams.get("validate");
                    if ((StringUtils.isBlank(isValidate)
                            || StringUtils.isNotBlank(isValidate) && isValidate.equalsIgnoreCase("false"))
                            && StringUtils.isNotBlank(referenceId)) {
                        String uploadedAttachments = (String) inputParams.get("uploadedattachments");
                        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
                        String userId = CustomerSession.getCustomerId(customer);
                        if (StringUtils.isNotBlank(uploadedAttachments)) {
                            String[] uploadedAttachmentsArray = uploadedAttachments.split(",");
                            this.parseUploadingAttachments(request, transactionResult, referenceId, userId,
                                    uploadedAttachmentsArray);
                        }
                    }

                    String documentIdList = inputParams.get("uploadedattachments");
                    if (StringUtils.isNotBlank(documentIdList)) {
                        SbgDocTransactionRelationHelper.updateTransactionIdAgaintDocumentId(documentIdList, referenceId);
                    }
                }
                String proofOfPayment = request.getParameter("proofOfPayment");
                SbgAlertUtil.sendPopSMS(request, proofOfPayment, SbgAlertUtil.getPopSmsDataObjectPaymentSubmit(transactionResult));
                SbgAlertUtil.sendPopEmail(request, proofOfPayment, transactionResult);
                if(savePayee){
                    transactionResult.addParam("payeeMessage", SBGConstants.SAVE_PAYEE_MSG);
                }
                return transactionResult;
            }
        } catch (Exception var15) {
            LOG.error("Error occured while invoking CreateDomesticAccountTransferOperation: ", var15);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
    }

    private void parseUploadingAttachments(DataControllerRequest request, Result transactionResult, String referenceId,
            String userId, String[] uploadedAttachmentsArray) {
        String uploadedFileName = null;
        String fileExtension = null;
        String fileContents = null;
        String PATTERN = "^[a-zA-Z0-9]*[.][.a-zA-Z0-9]*[^.]$";
        ArrayList<String> failedUploads = new ArrayList();
        ArrayList<String> successfulUploads = new ArrayList();
        try {
            for (int i = 0; i < uploadedAttachmentsArray.length; ++i) {
                String[] attachmentDetails = uploadedAttachmentsArray[i].split("-");
                if (attachmentDetails.length == 3) {
                    uploadedFileName = attachmentDetails[0];
                    fileExtension = attachmentDetails[1];
                    fileContents = attachmentDetails[2];
                    if (uploadedFileName.matches(PATTERN)) {
                        boolean status = this.uploadAttachments(request, referenceId, userId, uploadedFileName,
                                fileExtension, fileContents);
                        if (status) {
                            successfulUploads.add(uploadedFileName);
                        } else {
                            failedUploads.add(uploadedFileName);
                        }
                    } else {
                        failedUploads.add(uploadedFileName);
                    }
                } else {
                    LOG.error("File input is incorrect for performing upload operation");
                    failedUploads.add(attachmentDetails[0]);
                }
            }
        } catch (Exception var18) {
            LOG.error("Error occured while uploading attachments ", var18);
        } finally {
            transactionResult.addParam("failedUploads", StringUtils.join(failedUploads, ","));
            transactionResult.addParam("successfulUploads", StringUtils.join(successfulUploads, ","));
        }
    }

    private boolean uploadAttachments(DataControllerRequest request, String referenceId, String userId,
            String uploadedFileName, String fileExtension, String fileContents) {
        Map<String, String> dataMap = new HashMap();
        new Result();
        try {
            Result result;
            if (CommonUtils.isDMSIntegrationEnabled()) {
                dataMap.put("documentName", uploadedFileName);
                dataMap.put("category", "payment");
                dataMap.put("content", fileContents);
                dataMap.put("version", "1.0");
                dataMap.put("referenceId", referenceId);
                dataMap.put("userId", userId);
                result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
                        "DocumentStorage.uploadDocument");
            } else {
                String id = CommonUtils.generateUniqueID(32);
                dataMap.put("paymentFileID", id);
                dataMap.put("paymentFileName", uploadedFileName);
                dataMap.put("paymentFileType", fileExtension);
                dataMap.put("paymentFileContents", fileContents);
                dataMap.put("transactionId", referenceId);
                dataMap.put("userId", userId);
                result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
                        "PaymentFiles.createRecord");
            }
            return result.getOpstatusParamValue().equals("0");
        } catch (Exception var10) {
            LOG.error("Error occured while uploading attachments ", var10);
            return false;
        }
    }

    public Result beneficiarySave(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {
        Result payee = null;
        try {
            String arrayToJson = objectMapper.writeValueAsString(inputArray);

            @SuppressWarnings("unchecked")
            Map<String, Object> requestParams = (HashMap<String, Object>) inputArray[1];
            Object[] inputArrayNew = new Object[2];
            String beneficiaryPayload = requestParams.get("beneficiaryPayload") != null
                    ? requestParams.get("beneficiaryPayload").toString()
                    : "";
            JsonObject jsonObject = new JsonParser().parse(beneficiaryPayload).getAsJsonObject();

            @SuppressWarnings("unchecked")
            HashMap<String, Object> hashMap = new Gson().fromJson(jsonObject, HashMap.class);

            // String cif = hashMap.get("cif").toString();
            // JsonObject cifJsonObject = new JsonParser().parse(cif).getAsJsonObject();
            // hashMap.replace("cif", cifJsonObject);

            inputArrayNew[1] = hashMap;
            payee = externalPayeeResource.createPayee(methodID, inputArrayNew, request, response);
            LOG.debug("\nOnce OFF Beneficiary Save:- " + payee);
        } catch (Exception e) {
            LOG.error("Error occured while invoking CreateDomesticAccountTransferOperation in ExternalPayeeResource: ",
                    e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        return payee;
    }
}
