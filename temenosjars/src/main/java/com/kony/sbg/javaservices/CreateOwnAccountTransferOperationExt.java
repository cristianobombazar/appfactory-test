package com.kony.sbg.javaservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.kony.sbg.helpers.SbgDocTransactionRelationHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.transactionservices.resource.api.OwnAccountFundTransferResource;

/**
 * 
 * @author KH1755
 * @version 1.0 implements {@link JavaService2}
 */
public class CreateOwnAccountTransferOperationExt implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(CreateOwnAccountTransferOperationExt.class);
	public static final int FILENAME_INDEX = 0;
	public static final int FILETYPE_INDEX = 1;
	public static final int FILECONTENTS_INDEX = 2;
	public static final int UNIQUE_ID_LENGTH = 32;

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		String referenceId = null;
		Result transactionResult = new Result();

		try {
			// Initializing of TransactionResource through Abstract factory method
			OwnAccountFundTransferResource ownaccountTranscationResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(OwnAccountFundTransferResource.class);

			transactionResult = ownaccountTranscationResource.createTransaction(methodID, inputArray, request,
					response);
			String dbpErrMsg = transactionResult.getParamValueByName("dbpErrMsg");
			if (StringUtils.isNotBlank(dbpErrMsg)) {
				return transactionResult;
			}
			referenceId = transactionResult.getParamValueByName("referenceId");
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			LOG.debug("#### CreateInternationalAccountTransferOperationExt transactionID:" + referenceId +
					" uploadedattachments:" + inputParams.containsKey("uploadedattachments") + " validate:"
					+ inputParams.containsKey("validate"));
			if (inputParams.containsKey("uploadedattachments")) {
/*				if (inputParams.containsKey("validate")) {
					String isValidate = inputParams.get("validate");
					if ((StringUtils.isBlank(isValidate)
							|| (StringUtils.isNotBlank(isValidate) && isValidate.equalsIgnoreCase("false")))
							&& StringUtils.isNotBlank(referenceId)) {
						String uploadedAttachments = inputParams.get("uploadedattachments");
						Map<String, Object> customer = CustomerSession.getCustomerMap(request);
						String userId = CustomerSession.getCustomerId(customer);
						if (StringUtils.isNotBlank(uploadedAttachments)) {
							String uploadedAttachmentsArray[] = uploadedAttachments.split(",");
							parseUploadingAttachments(request, transactionResult, referenceId, userId,
									uploadedAttachmentsArray);
						}
					}
				}*/
				String documentIdList = inputParams.get("uploadedattachments");
				if (StringUtils.isNotBlank(documentIdList)) {
					SbgDocTransactionRelationHelper.updateTransactionIdAgaintDocumentId(documentIdList, referenceId);
				}
			}
		} catch (Exception e) {
			LOG.error("Error occured while invoking CreateOwnAccountTransferOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return transactionResult;
	}

	private String updateTransactionId(String documentId, String transactionId) {
		LOG.debug("### Starting ---> CreateInternationalAccountTransferOperation.updateTransactionId()");
		String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
		String operationName = SbgURLConstants.OPERATION_SBT_DOCUMENT_UPDATE;
		String sbgDocumentUpdateResponse = null;
		try {
			LOG.debug("transactionId of getAuditDataByTransactionId :- " + transactionId);
			HashMap<String, Object> requestHeaders = new HashMap<>();
			Map<String, Object> input = new HashMap<>();
			input.put("document_id", documentId);
			input.put("transactionId", transactionId);

			sbgDocumentUpdateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(input)
					.withRequestHeaders(requestHeaders).build().getResponse();

			LOG.debug("#### Response of sbgDocumentUpdateResponse:- " + sbgDocumentUpdateResponse);
		} catch (Exception e) {
			LOG.debug("Enter to Exception of updateTransactionId:", e);
		}

		return sbgDocumentUpdateResponse;
	}

	private void parseUploadingAttachments(DataControllerRequest request, Result transactionResult, String referenceId,
			String userId, String[] uploadedAttachmentsArray) {
		String uploadedFileName = null;
		String fileExtension = null;
		String fileContents = null;
		String PATTERN = "^[a-zA-Z0-9]*[.][.a-zA-Z0-9]*[^.]$";
		ArrayList<String> failedUploads = new ArrayList<>();
		ArrayList<String> successfulUploads = new ArrayList<>();
		try {
			for (int i = 0; i < uploadedAttachmentsArray.length; i++) {
				String attachmentDetails[] = uploadedAttachmentsArray[i].split("-");
				if (attachmentDetails.length == 3) {
					uploadedFileName = attachmentDetails[FILENAME_INDEX];
					fileExtension = attachmentDetails[FILETYPE_INDEX];
					fileContents = attachmentDetails[FILECONTENTS_INDEX];
					if (uploadedFileName.matches(PATTERN)) {
						boolean status = uploadAttachments(request, referenceId, userId, uploadedFileName,
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
					failedUploads.add(attachmentDetails[FILENAME_INDEX]);
				}
			}
		} catch (Exception e) {
			LOG.error("Error occured while uploading attachments ", e);
		} finally {
			transactionResult.addParam("failedUploads", StringUtils.join(failedUploads, ","));
			transactionResult.addParam("successfulUploads", StringUtils.join(successfulUploads, ","));
		}
	}

	private boolean uploadAttachments(DataControllerRequest request, String referenceId,
			String userId, String uploadedFileName, String fileExtension, String fileContents) {
		Map<String, String> dataMap = new HashMap<>();
		Result result = new Result();
		try {
			if (CommonUtils.isDMSIntegrationEnabled()) {
				dataMap.put("documentName", uploadedFileName);
				dataMap.put("category", "payment");
				dataMap.put("content", fileContents);
				dataMap.put("version", "1.0");
				dataMap.put("referenceId", referenceId);
				dataMap.put("userId", userId);
				result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
						URLConstants.DOCUMENT_STORAGE_UPLOAD);
			} else {
				String id = CommonUtils.generateUniqueID(UNIQUE_ID_LENGTH);
				dataMap.put("paymentFileID", id);
				dataMap.put("paymentFileName", uploadedFileName);
				dataMap.put("paymentFileType", fileExtension);
				dataMap.put("paymentFileContents", fileContents);
				dataMap.put("transactionId", referenceId);
				dataMap.put("userId", userId);
				result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
						URLConstants.PAYMENT_FILES_CREATE);
			}
			if (result.getOpstatusParamValue().equals("0")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error occured while uploading attachments ", e);
			return false;
		}
	}

	private void UploadFileNetAttachments(DataControllerRequest request, Result transactionResult, String referenceId,
			String userId, String[] uploadedAttachmentsArray) {

	}
}
