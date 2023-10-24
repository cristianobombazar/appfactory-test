package com.kony.sbg.business.impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.UploadedAttachments;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.DomesticPaymentAPIBackendDelegate;
import com.kony.sbg.business.api.SbgInterBankFundTransferBusinessDelegateExtn;
import com.kony.sbg.util.RefDataCacheHelper;
import com.kony.sbg.util.RefDataCacheHelperInterBank;
import com.kony.sbg.util.RefDataValidator;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.impl.InterBankFundTransferBusinessDelegateImpl;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;

public class SbgInterBankFundTransferBusinessDelegateImplExtn extends InterBankFundTransferBusinessDelegateImpl
		implements SbgInterBankFundTransferBusinessDelegateExtn {

	private static final Logger LOG = LogManager.getLogger(SbgInterBankFundTransferBusinessDelegateImplExtn.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	InterBankFundTransferBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(InterBankFundTransferBackendDelegate.class);

	@Override
	public InterBankFundTransferDTO createPendingTransaction(InterBankFundTransferDTO input,
			DataControllerRequest request) {

				if (!application.getIsStateManagementAvailableFromCache()) {
					input.setReferenceId(input.getTransactionId());
					return input;
				}
				
				InterBankFundTransferBackendDTO interBankFundTransferBackendDTO = new InterBankFundTransferBackendDTO();
				interBankFundTransferBackendDTO = interBankFundTransferBackendDTO.convert(input);
				
				return backendDelegate.createPendingTransaction(interBankFundTransferBackendDTO, request);

		// ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
		// 		.getBusinessDelegate(ApplicationBusinessDelegate.class);
		// if (!application.getIsStateManagementAvailableFromCache()) {
		// 	/*
		// 	 * Appending REF to referenceID is creating issue while fetching pending
		// 	 * approvals as referenceID don't match
		// 	 */
		// 	// input.setReferenceId(Constants.REFERENCE_KEY + input.getTransactionId());
		// 	input.setReferenceId(input.getTransactionId());
		// 	return input;
		// }

		// /*
		//  * Commenting this as it is generating new referenceID
		//  * InternationalFundTransferDTO dto = new InternationalFundTransferDTO();
		//  * dto.setReferenceId(CommonUtils.generateUniqueIDHyphenSeperated(0, 16));
		//  */

		// return input;
	}

	public JSONObject sbgValidations(DataControllerRequest request) {

		LOG.info("##[SbgInterBankFundTransferBusinessDelegateImplExtn] Resource sbgValidations");
		String refDataResponse = null;
		JSONObject sbgValidationsResponseObj = new JSONObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
		String strScheduledDate = null;
		Date scheduledDate = null;
		JSONObject responseObj = null;

		try {
			try {
				refDataResponse = getRefData(request);

				LOG.info("[SbgInterBankFundTransferBusinessDelegateImplExtn] refDataResponse: " + refDataResponse);
				if(SBGConstants.FALSE.equalsIgnoreCase(refDataResponse)) {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_EXTENSION_DATE_ERROR_MESSAGE);
				}
				responseObj = new JSONObject(refDataResponse);
				strScheduledDate = request.getParameter("scheduledDate");
				if (StringUtils.isNotBlank(strScheduledDate)) {
					scheduledDate = dateFormat.parse(strScheduledDate);
					LOG.info("[SbgInterBankFundTransferBusinessDelegateImplExtn] scheduledDate: " + scheduledDate);
				} else {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SCHEDULED_DATE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				LOG.error("[SbgInterBankFundTransferBusinessDelegateImplExtn]  Error: " + e.getMessage());
				return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SERVICESDOWN);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
			return RefDataValidator.verifyRefData(responseObj, sdf.parse(sdf.format(new Date())), scheduledDate, request.getParameter("transactionCurrency"));
		} catch (Exception e) {
			LOG.error("[SbgInterBankFundTransferBusinessDelegateImplExtn] sbgValidations(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}
	}

	private String getRefData(DataControllerRequest request) throws AppRegistryException {
		String currency = request.getParameter("transactionCurrency");
		String country = "ZA";
		String fromAccountNumber = request.getParameter("fromAccountNumber");
		String BIC = SBGCommonUtils.getBICFromAE(request, fromAccountNumber); // Fetch BIC from Arrangement Extension
		LOG.debug("&&&&&BICFromExtension: " + BIC);
		if (StringUtils.isBlank(BIC)) {
			
			return SBGConstants.FALSE;
		}
		if (StringUtils.isNotBlank(BIC)) {
			country = BIC.substring((SBGConstants.BIC_COUNTRY_CODE_START_INDEX - 1),
					(SBGConstants.BIC_COUNTRY_CODE_END_INDEX));
		}
		return RefDataCacheHelperInterBank.getRefDataByKey(request, country, currency);
	}

	public JSONObject fetchTransactionEntryFiltered(String filter) {
		JSONObject serviceResponse = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERBANKFUNDTRANSFERS_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		
		if (filter == null || filter.isEmpty()) {
			return null;
		}
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		String fetchResponse;
		try {
			fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			serviceResponse = new JSONObject(fetchResponse);
		} catch (DBPApplicationException e) {
			LOG.error("SbgInterBankFundTransferBusinessDelegateImplExtn::fetchTransactionEntryFiltered" + e);
			return null;
		}
		return serviceResponse;
	}

	/*
	 * Overridden to disable T24 service call & replaced transactionId with
	 * confirmationNumber in if .. filter
	 */
	@Override
	public List<ApprovalRequestDTO> fetchInterBankTransactionsWithApprovalInfo(List<BBRequestDTO> requests,
			DataControllerRequest dcr) {
		Set<String> interBankTransIds = new HashSet<String>();
		List<ApprovalRequestDTO> transactions = new ArrayList<ApprovalRequestDTO>();

		if (CollectionUtils.isEmpty(requests))
			return transactions;

		for (BBRequestDTO bBRequestDTO : requests) {
			if (StringUtils.isNotBlank(bBRequestDTO.getTransactionId())) {
				interBankTransIds.add(bBRequestDTO.getTransactionId());
			}

		}

		String filter = "";

		if (!application.getIsStateManagementAvailableFromCache()) {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL
					+ String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL,
							interBankTransIds);
			transactions = fetchInterBankTransactionsForApprovalInfo(filter, dcr);
			/*
			 * Modified below line by adding & assigning transactionId to confirmationNumber
			 */
			// transactions = (new FilterDTO()).merge(transactions, requests,
			// "transactionId=transactionId",
			// "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
			transactions = (new FilterDTO()).merge(transactions, requests, "confirmationNumber=transactionId",
					"transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
		} else {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL
					+ String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL,
							interBankTransIds);
			transactions = fetchInterBankTransactionsForApprovalInfo(filter, dcr);
			List<ApprovalRequestDTO> backendData = backendDelegate
					.fetchBackendTransactionsForApproval(interBankTransIds, dcr);
			if (CollectionUtils.isNotEmpty(backendData)) {
				transactions = (new FilterDTO()).merge(transactions, backendData, "confirmationNumber=transactionId",
						"");
			}
			transactions = (new FilterDTO()).merge(transactions, requests, "confirmationNumber=transactionId",
					"transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
			// backendDelegate.getBeneBankAddress(transactions, dcr);
		}

		// transactions = retrieveAttachments(transactions, dcr);
		return transactions;
	}

		/*
	 * Mashilo Joseph Monene : method to Set Error mesage to a JSON Object and return
	 */
	private JSONObject setErrorResult(JSONObject sbgValidationsResponseObj, String errorMessage) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.FALSE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, errorMessage);
		LOG.debug("[SbgInterBankFundTransferBusinessDelegateImplExtn] setErrorResult(-) sbgValidationsResponseObj: "
				+ sbgValidationsResponseObj.toString());

		return sbgValidationsResponseObj;
	}// setErrorResult(-)

	private List<ApprovalRequestDTO> retrieveAttachments(List<ApprovalRequestDTO> transactionsList,
			DataControllerRequest dcr) {

		for (ApprovalRequestDTO transaction : transactionsList) {

			List<UploadedAttachments> filesList = new ArrayList<>();
			String serviceName = ServiceId.DBPNONPRODUCTSERVICES;
			String operationName = OperationName.RETRIEVE_ATTACHMENTS;
			Map<String, Object> requestParameters = new HashMap<>();
			requestParameters.put("transactionId", transaction.getConfirmationNumber());

			String response = null;
			try {

				response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
						.withOperationId(operationName).withRequestParameters(requestParameters)
						.withRequestHeaders(dcr.getHeaderMap()).withDataControllerRequest(dcr).build().getResponse();

				JSONObject jsonRsponse = new JSONObject(response);
				JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
				filesList = JSONUtils.parseAsList(jsonArray.toString(), UploadedAttachments.class);
				transaction.setFileNames(filesList);
			} catch (JSONException e) {
				LOG.error("Unable to retieve domestic attachments", e);
			} catch (Exception e) {
				LOG.error("Unable to retieve domestic attachments", e);
			}

		}
		return transactionsList;

	}

	/*
	 * In bbrequest transaction ID is having value equal to confirmation number of
	 * interBankfundtransfer table. Replacing transactionId with confirmation
	 * number in filter query
	 */
	@Override
	public InterBankFundTransferDTO fetchTranscationEntry(String transactionId) {
		// transactionId = Constants.REFERENCE_KEY + transactionId;
		List<InterBankFundTransferDTO> interBankfundtransferdto = null;
		String bankName = "";
		LOG.debug("SbgInterBankFundTransferBusinessDelegateImplExtn.fetchTranscationEntry() --- START:: transactionId: "
				+ transactionId);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERBANKFUNDTRANSFERS_GET;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + transactionId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();

			LOG.debug("fetchTranscationEntry() --- fetchResponse: " + fetchResponse);
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray interBankfundJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);

			interBankfundtransferdto = JSONUtils.parseAsList(interBankfundJsonArray.toString(),
					InterBankFundTransferDTO.class);

			if (interBankfundJsonArray.length() > 0) {
				JSONObject obj = interBankfundJsonArray.getJSONObject(0);
				if (obj.has("bankName")) {
					bankName = obj.getString("bankName");
				}
			}
			LOG.debug("fetchTranscationEntry() --- DTO Created: BankName: " + bankName);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the interBankfundtransaction", jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the interBankfundtransaction", exp.getMessage());
			return null;
		}

		/* Modifying Nirthika */
		if (interBankfundtransferdto != null && interBankfundtransferdto.size() != 0) {
			InterBankFundTransferDTO interBankFundTransferDTO2 = interBankfundtransferdto.get(0);
			interBankFundTransferDTO2.setBeneficiaryBankName(bankName);
			// return interBankfundtransferdto.get(0);
			return interBankFundTransferDTO2;
		} else {
			LOG.debug("fetchTranscationEntry() --- ZERO records found: ");
		}

		return null;
	}

	@Override
	public InterBankFundTransferDTO updateStatus(String transactionId, String status, String confirmationNumber) {

		LOG.debug("updateStatus() ===> transactionId:" + transactionId + "; status:" + status + "; confirmationNumber:"
				+ confirmationNumber);
		// confirmationNumber = Constants.REFERENCE_KEY + transactionId;
		return updateStatusUsingConfirmationNumber(transactionId, status);

	}

	private InterBankFundTransferDTO updateStatusUsingConfirmationNumber(String confirmationNumber, String status) {
		InterBankFundTransferDTO transactionDTO = null;

		LOG.debug("updateStatusUsingConfirmationNumber() ===> status:" + status + "; confirmationNumber:"
				+ confirmationNumber);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_UPDATE_TRANSACTION_STATUS_PROC;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_featureId", "INTER_BANK_ACCOUNT_FUND_TRANSFER");
		requestParams.put("_status", status);
		requestParams.put("_confirmationNumber", confirmationNumber);

		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			LOG.debug("updateStatusUsingConfirmationNumber() ===> jsonRsponse:" + jsonRsponse);

			JSONArray jsonArr = CommonUtils.getFirstOccuringArray(jsonRsponse);
			if (jsonArr != null && jsonArr.length() > 0) {
				JSONObject record = jsonArr.getJSONObject(0);
				if (record != null && record.has("isSuccess") && "true".equals(record.get("isSuccess"))) {
					transactionDTO = new InterBankFundTransferDTO();
					transactionDTO.setConfirmationNumber(confirmationNumber);
					transactionDTO.setStatus(status);
					LOG.debug("updateStatusUsingConfirmationNumber() ===> record has success");
				} else {
					LOG.debug("updateStatusUsingConfirmationNumber() ===> record has failure:");
				}
			}
		} catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured in updateStatusUsingConfirmationNumber" + jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured in updateStatusUsingConfirmationNumber" + exp.getMessage());
			return null;
		}

		return transactionDTO;
	}

	@Override
	public InterBankFundTransferDTO approveTransaction(String referenceId, DataControllerRequest request,
			String frequency) {
		// referenceId = Constants.REFERENCE_KEY + referenceId;
		LOG.debug("Entry --> SbgInterBankFundTransferBusinessDelegateImplExtn::approveTransaction::referenceId: "
				+ referenceId);
		InterBankFundTransferBackendDTO backendObj = new InterBankFundTransferBackendDTO();
		InterBankFundTransferDTO dbxObj = fetchTransactionById(referenceId, request);
		backendObj = backendObj.convert(dbxObj);
		String branchCode = dbxObj.getBicCode();

		LOG.debug("SbgInterBankFundTransferBusinessDelegateImplExtn::approveTransaction ConfirmationNumber: "
				+ dbxObj.getConfirmationNumber() + " ReferenceID: " + dbxObj.getReferenceId() + " TransactionID: "
				+ dbxObj.getTransactionId() + " TransactionAmount: " + dbxObj.getTransactionAmount()
				+ " ScheduledDate: " + backendObj.getScheduledDate() + " FromAccountNumber: "
				+ dbxObj.getFromAccountNumber() + " FromAccountCurrency: " + dbxObj.getFromAccountCurrency()
				+ " ToAccountCurrency: " + dbxObj.getTransactionCurrency() + " BeneBankName: "
				+ dbxObj.getBeneficiaryBankName() + " beneficiaryState: " + dbxObj.getBeneficiaryState());

		try {
			backendObj.setAmount(Double.parseDouble(backendObj.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return null;
		}

		try {
			LOG.debug("SbgInterBankFundTransferBusinessDelegateImplExtn::approveTransaction::scheduledDate: "
					+ backendObj.getScheduledDate());
			backendObj.setScheduledDate(SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss",
					SbgURLConstants.PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z, backendObj.getScheduledDate()));
		} catch (ParseException e) {
			LOG.error(
					"SbgInterBankFundTransferBusinessDelegateImplExtn::approveTransaction --> Error occured while formatting date",
					e);
			return null;
		}

		backendObj.setBeneficiaryBankName(dbxObj.getBeneficiaryBankName());
		backendObj.setConfirmationNumber(dbxObj.getConfirmationNumber());
		String confirmationNumber = dbxObj.getConfirmationNumber();
		fetchTranscationRefData(backendObj.getTransactionId().replace(Constants.REFERENCE_KEY, ""), request);
		backendObj.setBeneficiaryState(request.getParameter("beneficiaryState"));
		backendObj.setBeneficiaryAddressLine2(request.getParameter("beneficiaryAddressLine2"));

		request.addRequestParam_("uploadedattachments", getUploadedAttachments(confirmationNumber));

		DomesticPaymentAPIBackendDelegate paymentAPIBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(DomesticPaymentAPIBackendDelegate.class);
		return paymentAPIBackendDelegate.createTransactionWithoutApproval(backendObj, request, branchCode);
	}

	private JSONObject fetchTranscationRefData(String referenceId, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET;
		final String respDataFieldName = "interbankfundtransfersRefData";
		Map<String, Object> requestParams = new HashMap<String, Object>();
		JSONObject serviceResponse = null;
		String filter = "referenceId" + DBPUtilitiesConstants.EQUAL + referenceId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			serviceResponse = new JSONObject(fetchResponse);
			if (serviceResponse != null && serviceResponse.length() > 0) {
				if (serviceResponse.has(respDataFieldName)
						&& serviceResponse.getJSONArray(respDataFieldName).length() > 0) {
					JSONArray interBankfundtransfersRefData = serviceResponse
							.getJSONArray(respDataFieldName);
					JSONObject refDataRsp = interBankfundtransfersRefData.getJSONObject(0);
					request.addRequestParam_("purposeCode", refDataRsp.optString("purposecode"));
					request.setAttribute("purposeCode", refDataRsp.optString("purposecode"));
					request.addRequestParam_("beneficiaryReference", refDataRsp.optString("benerefeno"));
					request.setAttribute("beneficiaryReference", refDataRsp.optString("benerefeno"));
					request.addRequestParam_("complianceCode", refDataRsp.optString("compliancecode"));
					request.setAttribute("complianceCode", refDataRsp.optString("compliancecode"));
					request.addRequestParam_("statementReference", refDataRsp.optString("statementrefno"));
					request.setAttribute("statementReference", refDataRsp.optString("statementrefno"));
					request.addRequestParam_("rfqDetails", refDataRsp.optString("rfqDetails"));
					request.setAttribute("rfqDetails", refDataRsp.optString("rfqDetails"));
					request.addRequestParam_("bopDetails", refDataRsp.optString("bopDetails"));
					request.setAttribute("bopDetails", refDataRsp.optString("bopDetails"));
					request.addRequestParam_("beneficiaryState", refDataRsp.optString("beneficiaryState"));
					request.setAttribute("beneficiaryState", refDataRsp.optString("beneficiaryState"));
					request.addRequestParam_("clearingCode", refDataRsp.optString("clearingCode"));
					request.setAttribute("clearingCode", refDataRsp.optString("clearingCode"));
					request.addRequestParam_("beneficiaryAddressLine2",
							refDataRsp.optString("beneficiaryAddressLine2"));
					request.setAttribute("beneficiaryAddressLine2", refDataRsp.optString("beneficiaryAddressLine2"));
				}
			} else {
				LOG.debug("fetchTranscationRefData::serviceResponse is empty");
			}
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the " + respDataFieldName,
					jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the " + respDataFieldName, exp.getMessage());
			return null;
		}
		return serviceResponse;
	}

	private String getUploadedAttachments(String transactionId) {
		try {
			LOG.debug("#### starting getUploadedAttachments for transactionId:" + transactionId);
			JSONObject jsonObject = getDocumentMetaDataByTransactionId(transactionId);
			JSONArray records = jsonObject.getJSONArray("documents");
			String docNameListStr = "";
			if (records != null) {

				for (int i = 0; i < records.length(); i++) {
					String docName = records.getJSONObject(i).getString("name");

					if (org.apache.commons.lang.StringUtils.isNotBlank(docNameListStr)
							&& org.apache.commons.lang.StringUtils.isNotBlank(docName)) {
						docNameListStr = docNameListStr + "," + docName;
					} else if (org.apache.commons.lang.StringUtils.isBlank(docNameListStr)) {
						docNameListStr = docName;
					}
				}
			}
			LOG.debug("#### getUploadedAttachments docNameListStr:" + docNameListStr);
			return docNameListStr;
		} catch (Exception e) {
			LOG.debug("#### getUploadedAttachments failed:", e);
			return "";
		}
	}

	private JSONObject getDocumentMetaDataByTransactionId(String transactionId)
			throws HttpCallException, DBPApplicationException {
		LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId()");

		String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
		String operationName = SbgURLConstants.OPERATION_DOCUMENT_WITH_CONTENT;

		StringBuilder sb = new StringBuilder();
		String transactionIdFilter = sb.append("transactionId").append(DBPUtilitiesConstants.EQUAL).append("'")
				.append(transactionId).append("'").toString();
		LOG.debug(
				"### RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() filter:" + transactionIdFilter);

		Map<String, Object> input = new HashMap<>();
		input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
		input.put(DBPUtilitiesConstants.SELECT, "name");

		String docWithoutContentResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
				.withOperationId(operationName).withRequestParameters(input)
				.withRequestHeaders(new HashMap<>()).build().getResponse();
		LOG.debug(
				"### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:"
						+ docWithoutContentResponse);
		// return HelperMethods.callApi(dcRequest, input,
		// HelperMethods.getHeaders(dcRequest), SBGConstants.DOCUMENT_INFO_WITHOUT_DOC);

		return new JSONObject(docWithoutContentResponse);
	}
}