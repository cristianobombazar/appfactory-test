package com.kony.sbg.business.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.backend.api.OwnAccountPaymentAPIBackendDelegate;
import com.kony.sbg.business.api.OwnAccountFundTransferBusinessDelegateExtn;
import com.kony.sbg.util.RefDataCacheHelper;
import com.kony.sbg.util.RefDataValidator;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.OwnAccountFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.impl.OwnAccountFundTransferBusinessDelegateImpl;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.UploadedAttachments;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

public class SbgOwnAccountFundTransferBusinessDelegateImplExtn extends OwnAccountFundTransferBusinessDelegateImpl
        implements OwnAccountFundTransferBusinessDelegateExtn {
    private static final Logger LOG = LogManager.getLogger(SbgOwnAccountFundTransferBusinessDelegateImplExtn.class);
    ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(ApplicationBusinessDelegate.class);
    OwnAccountFundTransferBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
            .getBackendDelegate(OwnAccountFundTransferBackendDelegate.class);

    @Override
    public OwnAccountFundTransferDTO createPendingTransaction(OwnAccountFundTransferDTO input,
            DataControllerRequest request) {

        ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ApplicationBusinessDelegate.class);
        if (!application.getIsStateManagementAvailableFromCache()) {
            /*
             * Appending REF to referenceID is creating issue while fetching pending
             * approvals as referenceID don't match
             */
            input.setReferenceId(input.getTransactionId());
            return input;
        }

        return input;
    }

    public JSONObject sbgValidations(DataControllerRequest request) {
		LOG.info("##[SbgOwnAccountFundTransferBusinessDelegateImplExtn] Resource sbgValidations");
		String refDataResponse = null;
		JSONObject sbgValidationsResponseObj = new JSONObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
		String strScheduledDate = null;
		Date scheduledDate = null;
		JSONObject responseObj = null;
		try {
			try {
				refDataResponse = getRefData(request);
				LOG.info("[SbgOwnAccountFundTransferBusinessDelegateImplExtn] refDataResponse: " + refDataResponse);
				if(SBGConstants.FALSE.equalsIgnoreCase(refDataResponse)) {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_EXTENSION_DATE_ERROR_MESSAGE);
				}
				responseObj = new JSONObject(refDataResponse);
				strScheduledDate = request.getParameter("scheduledDate");
				if (StringUtils.isNotBlank(strScheduledDate)) {
					scheduledDate = dateFormat.parse(strScheduledDate);
					LOG.info("[SbgOwnAccountFundTransferBusinessDelegateImplExtn] scheduledDate: " + scheduledDate);
				} else {
					return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SCHEDULED_DATE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				LOG.error("[SbgOwnAccountFundTransferBusinessDelegateImplExtn]  Error: " + e.getMessage());
				return setErrorResult(sbgValidationsResponseObj, SBGConstants.REFDATA_SERVICESDOWN);
			}

			SimpleDateFormat sdf = new SimpleDateFormat(SBGConstants.REFDATA_DATE_FORMAT);
			return RefDataValidator.verifyRefData(responseObj, sdf.parse(sdf.format(new Date())), scheduledDate, request.getParameter("transactionCurrency"));
		} catch (Exception e) {
			LOG.error("[SbgOwnAccountFundTransferBusinessDelegateImplExtn] sbgValidations(-) Error: "
					+ e.getMessage());
			return setErrorResult(sbgValidationsResponseObj, e.getMessage());
		}
	}

    public JSONObject fetchTransactionEntryFiltered(String filter) {
		JSONObject serviceResponse = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_OWNACCOUNTTRANSFERS_GET;
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
			LOG.error("SbgOwnAccountFundTransferBusinessDelegateImplExtn::fetchTransactionEntryFiltered" + e);
			return null;
		}
		return serviceResponse;
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
		return RefDataCacheHelper.getRefDataByKey(request, country, currency);
	}

	@Override
	public OwnAccountFundTransferDTO fetchTransactionById(String referenceId,
			DataControllerRequest dataControllerRequest) {
		OwnAccountFundTransferDTO filterData= new OwnAccountFundTransferDTO();
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:fetchTransactionById");
		try {
		if (!application.getIsStateManagementAvailableFromCache()) {
			LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:referenceId"+referenceId);
			OwnAccountFundTransferDTO transEntry =fetchTranscationEntry(referenceId);
			transEntry.setAmount(Double.parseDouble(transEntry.getTransactionAmount()));
			return transEntry;
		}
		
		OwnAccountFundTransferDTO backendData =backendDelegate.fetchTransactionById(referenceId, dataControllerRequest);
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:backendData.getAmount()"+backendData.getAmount() +"backendData.getTransactionAmount()"+ backendData.getTransactionAmount());
		backendData.setAmount(Double.parseDouble(backendData.getTransactionAmount()));
		OwnAccountFundTransferDTO dbxData = fetchExecutedTranscationEntry(referenceId, null, null);
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:dbxData.getAmount()"+dbxData.getAmount() +"dbxData.getTransactionAmount()"+ dbxData.getTransactionAmount());
		dbxData.setAmount(Double.parseDouble(dbxData.getTransactionAmount()));
		if(backendData == null || StringUtils.isEmpty(backendData.getDbpErrMsg())) {
			LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:null");
			return dbxData;
		}
		
		//return (new FilterDTO()).merge( Arrays.asList(dbxData),Arrays.asList(backendData),"confirmationNumber=transactionId", "").get(0);
		 filterData =(new FilterDTO()).merge( Arrays.asList(dbxData),Arrays.asList(backendData),"confirmationNumber=transactionId", "").get(0);
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn:filterData.getAmount()"+filterData.getAmount() +"filterData.getTransactionAmount()"+ filterData.getTransactionAmount());
		filterData.setAmount(Double.parseDouble(filterData.getTransactionAmount()));
		return filterData;
		}
		catch (Exception e) {
			LOG.error("SbgOwnAccountFundTransferBusinessDelegateImplExtn:fetchTransactionById: "
					+ e.getMessage());
		}
		return filterData;
	}

	/*
	 * Mashilo Joseph Monene : method to Set Error mesage to a JSON Object and return
	 */
	private JSONObject setErrorResult(JSONObject sbgValidationsResponseObj, String errorMessage) {
		sbgValidationsResponseObj.put(SBGConstants.IS_VALID, SBGConstants.FALSE);
		sbgValidationsResponseObj.put(SBGConstants.MESSAGE, errorMessage);
		LOG.debug("[SbgOwnAccountFundTransferBusinessDelegateImplExtn] setErrorResult(-) sbgValidationsResponseObj: "
				+ sbgValidationsResponseObj.toString());

		return sbgValidationsResponseObj;
	}// setErrorResult(-)

	/*
	 * Overridden to disable T24 service call & replaced transactionId with
	 * confirmationNumber in if .. filter
	 */
	@Override
	public List<ApprovalRequestDTO> fetchOwnAccountFundTransactionsWithApprovalInfo(List<BBRequestDTO> requests,
			DataControllerRequest dcr) {
		Set<String> ownAccountTransIds = new HashSet<String>();
		List<ApprovalRequestDTO> transactions = new ArrayList<ApprovalRequestDTO>();

		if (CollectionUtils.isEmpty(requests))
			return transactions;

		for (BBRequestDTO bBRequestDTO : requests) {
			if (StringUtils.isNotBlank(bBRequestDTO.getTransactionId()))
				ownAccountTransIds.add(bBRequestDTO.getTransactionId());
		}

		String filter = "";

		if (!application.getIsStateManagementAvailableFromCache()) {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL
					+ String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL,
							ownAccountTransIds);
			transactions = fetchOwnAccountFundTransactionsForApprovalInfo(filter, dcr);
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
							ownAccountTransIds);
			transactions = fetchOwnAccountFundTransactionsForApprovalInfo(filter, dcr);
			List<ApprovalRequestDTO> backendData = backendDelegate
					.fetchBackendTransactionsForApproval(ownAccountTransIds, dcr);
			if (CollectionUtils.isNotEmpty(backendData)) {
				transactions = (new FilterDTO()).merge(transactions, backendData, "confirmationNumber=transactionId",
						"");
			}
			transactions = (new FilterDTO()).merge(transactions, requests, "confirmationNumber=transactionId",
					"transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
			// backendDelegate.getBeneBankAddress(transactions, dcr);
		}

		transactions = retrieveAttachments(transactions, dcr);
		return transactions;
	}

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
				LOG.error("Unable to retieve attachments", e);
			} catch (Exception e) {
				LOG.error("Unable to retieve attachments", e);
			}

		}
		return transactionsList;
	}

	/*
	 * In bbrequest transaction ID is having value equal to confirmation number of
	 * ownAccountfundtransfer table. Replacing transactionId with confirmation
	 * number in filter query
	 */
	@Override
	public OwnAccountFundTransferDTO fetchTranscationEntry(String transactionId) {
		List<OwnAccountFundTransferDTO> ownAccountfundtransferdto = null;
		String bankName = "";
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn.fetchTranscationEntry() --- START:: transactionId: " + transactionId);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_OWNACCOUNTTRANSFERS_GET;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + transactionId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();

			LOG.debug("fetchTranscationEntry() --- fetchResponse: " + fetchResponse);
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray ownAccountfundJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);

			ownAccountfundtransferdto = JSONUtils.parseAsList(ownAccountfundJsonArray.toString(),
					OwnAccountFundTransferDTO.class);
			
			if (ownAccountfundJsonArray.length() > 0) {
				JSONObject obj = ownAccountfundJsonArray.getJSONObject(0);
				if (obj.has("bankName")) {
					bankName = obj.getString("bankName");
				}				
			}
			LOG.debug("fetchTranscationEntry() --- DTO Created: BankName: " + bankName);
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the ownAccountfundtransaction", jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the ownAccountfundtransaction", exp.getMessage());
			return null;
		}
		
		/*Modifying Nirthika*/
		if (ownAccountfundtransferdto != null && ownAccountfundtransferdto.size() != 0) {
			OwnAccountFundTransferDTO ownAccountFundTransferDTO2 = ownAccountfundtransferdto.get(0);			
			// ownAccountFundTransferDTO2.setBeneficiaryBankName(bankName);
			//return ownAccountfundtransferdto.get(0);
			return ownAccountFundTransferDTO2;
		} else {
			LOG.debug("fetchTranscationEntry() --- ZERO records found: ");
		}

		return null;
	}

	@Override
	public OwnAccountFundTransferDTO updateStatus(String transactionId, String status, String confirmationNumber) {

		LOG.debug("updateStatus() ===> transactionId:" + transactionId + "; status:" + status + "; confirmationNumber:"
				+ confirmationNumber);
		return updateStatusUsingConfirmationNumber(transactionId, status);

	}

	private OwnAccountFundTransferDTO updateStatusUsingConfirmationNumber(String confirmationNumber, String status) {
		OwnAccountFundTransferDTO transactionDTO = null;

		LOG.debug("updateStatusUsingConfirmationNumber() ===> status:" + status + "; confirmationNumber:"
				+ confirmationNumber);

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_UPDATE_TRANSACTION_STATUS_PROC;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_featureId", "TRANSFER_BETWEEN_OWN_ACCOUNT");
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
					transactionDTO = new OwnAccountFundTransferDTO();
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
	public OwnAccountFundTransferDTO approveTransaction(String referenceId, DataControllerRequest request,
			String frequency) {
		LOG.debug("Entry --> SbgOwnAccountFundTransferBusinessDelegateImplExtn::approveTransaction::referenceId: " + referenceId);
		OwnAccountFundTransferBackendDTO backendObj = new OwnAccountFundTransferBackendDTO();
		OwnAccountFundTransferDTO dbxObj = fetchTransactionById(referenceId, request);		
		backendObj = backendObj.convert(dbxObj);
		
		LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn::approveTransaction ConfirmationNumber: "
				+ dbxObj.getConfirmationNumber() + " ReferenceID: " + dbxObj.getReferenceId() + " TransactionID: "
				+ dbxObj.getTransactionId() + " TransactionAmount: " + dbxObj.getTransactionAmount()
				+ " ScheduledDate: " + backendObj.getScheduledDate() + " FromAccountNumber: "
				+ dbxObj.getFromAccountNumber() + " FromAccountCurrency: " + dbxObj.getFromAccountCurrency()
				+ " ToAccountCurrency: " + dbxObj.getTransactionCurrency());
		
		try {
			backendObj.setAmount(Double.parseDouble(backendObj.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return null;
		}
		
		try {
			LOG.debug("SbgOwnAccountFundTransferBusinessDelegateImplExtn::approveTransaction::scheduledDate: " + backendObj.getScheduledDate());
			backendObj.setScheduledDate(SBGCommonUtils.getFormattedDate("yyyy-MM-dd HH:mm:ss", SbgURLConstants.PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z, backendObj.getScheduledDate()));
		} catch (ParseException e) {
			LOG.error("SbgOwnAccountFundTransferBusinessDelegateImplExtn::approveTransaction --> Error occured while formatting date", e);
			return null;
		}
		
		backendObj.setConfirmationNumber(dbxObj.getConfirmationNumber());
		String confirmationNumber = dbxObj.getConfirmationNumber();
		fetchTranscationRefData(backendObj.getTransactionId(), request);

		// request.addRequestParam_("uploadedattachments",getUploadedAttachments(confirmationNumber));
		request.addRequestParam_("uploadedattachments", getDocIdsByTransactionId(confirmationNumber));
		
		OwnAccountPaymentAPIBackendDelegate paymentAPIBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(OwnAccountPaymentAPIBackendDelegate.class);
		OwnAccountFundTransferDTO resultDTO = paymentAPIBackendDelegate.createTransactionWithoutApproval(backendObj, request);		
	    
		LOG.debug("resultDTO.getDbpErrCode()" +resultDTO.getDbpErrCode());
		LOG.debug("resultDTO.getDbpErrMsg()" +resultDTO.getDbpErrMsg());
		LOG.debug("resultDTO.getReferenceId()" +resultDTO.getReferenceId());
		LOG.debug("resultDTO.getConfirmationNumber()" +resultDTO.getConfirmationNumber());
		return resultDTO;
	}

	private String getDocIdsByTransactionId(String transactionId) {
		LOG.debug("### Starting ---> getDocIdsByTransactionId");

		try {
			String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
			String operationName = SbgURLConstants.OPERATION_SBT_DOCUMENT_GET;

			StringBuilder sb = new StringBuilder();
			String transactionIdFilter = sb.append("transaction_id").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();

			Map<String, Object> input = new HashMap<>();
			input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
			input.put(DBPUtilitiesConstants.SELECT, "document_id");

			String docResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(input)
					.withRequestHeaders(new HashMap<>()).build().getResponse();
			JSONObject jsonObject = new JSONObject(docResponse);

			JSONArray records = jsonObject.getJSONArray("sbg_documents");
			String docIdListStr = "";
			if (records != null) {

				for (int i = 0; i < records.length(); i++) {
					String docId = records.getJSONObject(i).getString("document_id");

					if (org.apache.commons.lang.StringUtils.isNotBlank(docIdListStr) && org.apache.commons.lang.StringUtils.isNotBlank(docId)) {
						docIdListStr = docIdListStr + "," + docId;
					} else if (org.apache.commons.lang.StringUtils.isBlank(docIdListStr)) {
						docIdListStr = docId;
					}
				}
			}
			LOG.debug("#### getDocIdsByTransactionId getUploadedAttachments docIdListStr:" + docIdListStr);
			return docIdListStr;
		} catch (Exception e) {
			LOG.error("error retrieve doc ids");
			return "";
		}

	}

	private JSONObject fetchTranscationRefData(String referenceId, DataControllerRequest request) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		JSONObject serviceResponse = null;
		String filter = "referenceId" + DBPUtilitiesConstants.EQUAL + referenceId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();			
			serviceResponse = new JSONObject(fetchResponse);
			if (serviceResponse != null && serviceResponse.length() > 0) {
				if (serviceResponse.has("ownAccountfundtransfersRefData") && serviceResponse.getJSONArray("ownAccountfundtransfersRefData").length() > 0) {					
					JSONArray ownAccountfundtransfersRefData = serviceResponse.getJSONArray("ownAccountfundtransfersRefData");					
					JSONObject refDataRsp = ownAccountfundtransfersRefData.getJSONObject(0);
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
					request.addRequestParam_("beneficiaryAddressLine2", refDataRsp.optString("beneficiaryAddressLine2"));
					request.setAttribute("beneficiaryAddressLine2", refDataRsp.optString("beneficiaryAddressLine2"));
				}
			} else {
				LOG.debug("fetchTranscationRefData::serviceResponse is empty");
			}
		}

		catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the ownAccountfundtransfersRefData",
					jsonExp.getMessage());
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while fetching the ownAccountfundtransfersRefData", exp.getMessage());
			return null;
		}
		return serviceResponse;
	}

	private JSONObject getDocumentMetaDataByTransactionId(String transactionId) throws HttpCallException, DBPApplicationException {
		LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId()");

		String serviceName = SbgURLConstants.SERVICE_DOCUMENT_STORAGE;
		String operationName = SbgURLConstants.OPERATION_DOCUMENT_WITH_CONTENT;

		StringBuilder sb = new StringBuilder();
		String transactionIdFilter = sb.append("transactionId").append(DBPUtilitiesConstants.EQUAL).append("'").append(transactionId).append("'").toString();
		LOG.debug("### RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() filter:" + transactionIdFilter);

		Map<String, Object> input = new HashMap<>();
		input.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
		input.put(DBPUtilitiesConstants.SELECT, "name");

		String docWithoutContentResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
				.withOperationId(operationName).withRequestParameters(input)
				.withRequestHeaders(new HashMap<>()).build().getResponse();
		LOG.debug("### Starting ---> RetrieveDocumentResourceImpl::getDocumentMetaDataByTransactionId() docWithoutContentResponse:" + docWithoutContentResponse);
		//return HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), SBGConstants.DOCUMENT_INFO_WITHOUT_DOC);

		return new JSONObject(docWithoutContentResponse);
	}

	private String getUploadedAttachments(String transactionId) {
		try {
			LOG.debug("#### starting getUploadedAttachments for transactionId:"+transactionId);
			JSONObject jsonObject = getDocumentMetaDataByTransactionId(transactionId);
			JSONArray records = jsonObject.getJSONArray("documents");
			String docNameListStr= "";
			if (records != null) {

				for (int i = 0; i < records.length(); i++) {
					String docName= records.getJSONObject(i).getString("name");

					if(org.apache.commons.lang.StringUtils.isNotBlank(docNameListStr) && org.apache.commons.lang.StringUtils.isNotBlank(docName)){
						docNameListStr = docNameListStr + "," +docName;
					}else if(org.apache.commons.lang.StringUtils.isBlank(docNameListStr)){
						docNameListStr=docName;
					}
				}
			}
			LOG.debug("#### getUploadedAttachments docNameListStr:"+docNameListStr);
			return docNameListStr;
		} catch (Exception e) {
			LOG.debug("#### getUploadedAttachments failed:",e);
			return "";
		}
	}
}
