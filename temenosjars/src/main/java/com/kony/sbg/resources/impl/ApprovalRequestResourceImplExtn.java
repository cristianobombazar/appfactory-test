package com.kony.sbg.resources.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.approvalservices.resource.impl.ApprovalRequestResourceImpl;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureActionBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.LimitGroupBusinessDelegate;
import com.temenos.dbx.product.commons.dto.ApplicationDTO;
import com.temenos.dbx.product.commons.dto.FeatureActionDTO;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commons.dto.LimitGroupDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
public class ApprovalRequestResourceImplExtn extends ApprovalRequestResourceImpl {
	  private static final Logger LOG = LogManager.getLogger(ApprovalRequestResourceImpl.class);
	  
	  public Result getCounts(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
		  LOG.debug("ApprovalRequestResourceImplExtn-->getCounts");
	    Result result = new Result();
	    Map<String, Object> customer = CustomerSession.getCustomerMap(request);
	    String customerId = CustomerSession.getCustomerId(customer);
	    List<String> approveActionIds = Arrays.asList(new String[] { 
	          "BULK_PAYMENT_REQUEST_APPROVE", "BILL_PAY_APPROVE", "P2P_APPROVE", "ACH_FILE_APPROVE", "ACH_COLLECTION_APPROVE", "ACH_PAYMENT_APPROVE", "INTER_BANK_ACCOUNT_FUND_TRANSFER_APPROVE", "INTRA_BANK_FUND_TRANSFER_APPROVE", "DOMESTIC_WIRE_TRANSFER_APPROVE", "INTERNATIONAL_WIRE_TRANSFER_APPROVE", 
	          "INTERNATIONAL_ACCOUNT_FUND_TRANSFER_APPROVE", "TRANSFER_BETWEEN_OWN_ACCOUNT_APPROVE", "INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE", "TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL_APPROVE", "INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL_APPROVE", "INTRA_BANK_FUND_TRANSFER_CANCEL_APPROVE", "CHEQUE_BOOK_REQUEST_APPROVE" });
	    List<String> createActionIds = Arrays.asList(new String[] { 
	          "BULK_PAYMENT_REQUEST_SUBMIT", "BILL_PAY_CREATE", "P2P_CREATE", "ACH_FILE_UPLOAD", "ACH_COLLECTION_CREATE", "ACH_PAYMENT_CREATE", "INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE", "INTRA_BANK_FUND_TRANSFER_CREATE", "DOMESTIC_WIRE_TRANSFER_CREATE", "INTERNATIONAL_WIRE_TRANSFER_CREATE", 
	          "INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE", "TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE", "INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CANCEL", "TRANSFER_BETWEEN_OWN_ACCOUNT_CANCEL", "INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL", "INTRA_BANK_FUND_TRANSFER_CANCEL", "CHEQUE_BOOK_REQUEST_CREATE" });
	    String permittedApproveActionIds = CustomerSession.getPermittedActionIds(request, approveActionIds);
	    permittedApproveActionIds = approveActionIds.toString();
	    permittedApproveActionIds = permittedApproveActionIds.replaceAll("\\s", "");
	    permittedApproveActionIds = permittedApproveActionIds.substring(1, permittedApproveActionIds.length() - 1);
	    String permittedCreateActionIds = CustomerSession.getPermittedActionIds(request, createActionIds);
	    if (StringUtils.isEmpty(permittedApproveActionIds) && StringUtils.isEmpty(permittedCreateActionIds)) {
	      LOG.error("feature List is missing");
	      return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
	    } 
	    String localeId = request.getParameter("languageCode");
	    if (StringUtils.isEmpty(localeId)) {
	      LOG.error("languageCode List is missing");
	      return ErrorCodeEnum.ERR_29008.setErrorCode(new Result());
	    } 
	    ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = (ApprovalQueueBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	    List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", String.join(",", new CharSequence[] { permittedApproveActionIds }));
	    if (mainRequests == null) {
	      LOG.error("Error occurred while fetching requests for counts");
	      return ErrorCodeEnum.ERR_29024.setErrorCode(new Result());
	    } 
	    List<ApprovalRequestDTO> records;
	    try {
	      records = JSONUtils.parseAsList(JSONUtils.stringify(mainRequests), ApprovalRequestDTO.class);
	    } catch (IOException e) {
	      LOG.error("Error occurred while fetching requests for counts", e);
	      return ErrorCodeEnum.ERR_29024.setErrorCode(new Result());
	    } 
	    FeatureActionBusinessDelegate featureActionBusinessDelegate = (FeatureActionBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(FeatureActionBusinessDelegate.class);
	    List<FeatureActionDTO> featureactions = featureActionBusinessDelegate.fetchFeatureActionsWithLimitGroupDetails();
	     records = (new FilterDTO()).merge(records, featureactions, "featureActionId=featureActionId", "featureName,featureActionName,limitGroupName");
	    Map<String, Map<String, JSONObject>> features = new HashMap<>();
	    ApplicationBusinessDelegate applicationBusinessDelegate = (ApplicationBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
	    ApplicationDTO applicationDTO = applicationBusinessDelegate.properties();
	    Boolean isSelfApproveEnable = Boolean.valueOf((applicationDTO != null) ? applicationDTO.isSelfApprovalEnabled() : Boolean.TRUE.booleanValue());

	    /// uditha CQL-12882

		  List<Record> listPending = new ArrayList<>();
		  try {
			  LOG.info("call to fetchRecordsPendingForMyApprovalCount");
			  Result resultPending = new SbgApprovalQueueResourceImplExt().fetchRecordsPendingForMyApprovalCount(mainRequests, inputArray, request, response);
			  LOG.info("end call to fetchRecordsPendingForMyApprovalCount");
			  if (null != resultPending) {
				  listPending = resultPending.getDatasetById("records").getAllRecords();
			  }
		  } catch (Exception e) {
			  LOG.error("ERROR listpending = " + e.getMessage());
		  }
		// uditha CQL-12882 end
	    for (ApprovalRequestDTO dto : records) {
	      if (StringUtils.isEmpty(dto.getTransactionId()))
	        continue; 
	      if (dto.getLimitGroupId() == null) {
	        dto.setLimitGroupId("OTHER");
	        dto.setLimitGroupName("Other");
	      } 
	      if (features.get(dto.getLimitGroupId()) == null)
	        features.put(dto.getLimitGroupId(), new HashMap<>()); 
	      Map<String, JSONObject> featureActions = features.get(dto.getLimitGroupId());
			String featureActionId = dto.getFeatureActionId();
			if (featureActions.get(featureActionId) == null) {
	        JSONObject jSONObject = new JSONObject();
	        FeatureActionDTO featureAction = featureActionBusinessDelegate.getFeatureActionById(featureActionId);
	        jSONObject.put("featureActionId", featureActionId);
	        jSONObject.put("featureActionName", featureAction.getFeatureActionName());
	        jSONObject.put("actionType", featureAction.getTypeId());
	        jSONObject.put("featureName", SBGCommonUtils.getFeatureName(dto.getFeatureName(), featureActionId));
	        jSONObject.put("myApprovalsPending", 0);
	        jSONObject.put("myApprovalsHistory", 0);
	        jSONObject.put("myRequestsPending", 0);
	        jSONObject.put("myRequestHistory", 0);
	        featureActions.put(featureActionId, jSONObject);
	      } 
	      JSONObject countObject = featureActions.get(featureActionId);
	      if ("true".equalsIgnoreCase(dto.getAmICreator()))
	        if ("Pending".equalsIgnoreCase(dto.getStatus())) {
	          countObject.put("myRequestsPending", countObject.getInt("myRequestsPending") + 1);
	        } else {
	          countObject.put("myRequestHistory", countObject.getInt("myRequestHistory") + 1);
	        }  
	      if ("true".equalsIgnoreCase(dto.getActedByMeAlready()) && !TransactionStatusEnum.WITHDRAWN.getStatus().equalsIgnoreCase(dto.getStatus())) {
	        countObject.put("myApprovalsHistory", countObject.getInt("myApprovalsHistory") + 1);
	        continue;
	      }
			if ("Pending".equalsIgnoreCase(dto.getStatus()) && "true".equalsIgnoreCase(dto.getAmIApprover()) && "false"
					.contentEquals(dto.getActedByMeAlready()) && (
					!isSelfApproveEnable.booleanValue() || !"true".equalsIgnoreCase(dto.getAmICreator()))) {
				// uditha CQL-12882
				boolean recordExist = listPending.stream().filter(
						t -> (t.getParamValueByName("requestId").equalsIgnoreCase(dto.getRequestId()) &&
								t.getParamValueByName("transactionId").equalsIgnoreCase(dto.getTransactionId())
						)).findFirst().isPresent();
				if (recordExist) {
					countObject.put("myApprovalsPending", countObject.getInt("myApprovalsPending") + 1);
				}
			}
	    } 
	    LimitGroupBusinessDelegate limitGroupBusinessDelegate = (LimitGroupBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(LimitGroupBusinessDelegate.class);
	    List<LimitGroupDTO> limitGroupDTOs = limitGroupBusinessDelegate.fetchLimitGroupsWithLanguageId(localeId);
	    LimitGroupDTO otherGroupDTO = new LimitGroupDTO("OTHER", "Other", "Other Payments");
	    limitGroupDTOs.add(otherGroupDTO);
	    JSONArray limitGroups = new JSONArray();
	    for (LimitGroupDTO dto : limitGroupDTOs) {
	      JSONObject limitGroupObject = new JSONObject();
	      limitGroupObject.put("limitgroupId", dto.getLimitGroupId());
	      limitGroupObject.put("limitgroupName", dto.getLimitGroupName());
	      Map<String, JSONObject> featureActions = features.get(dto.getLimitGroupId());
	      if (featureActions != null && featureActions.size() > 0) {
	        limitGroupObject.put("featureActions", new ArrayList(featureActions.values()));
	      } else {
	        limitGroupObject.put("featureActions", new ArrayList());
	      } 
	      limitGroups.put(limitGroupObject);
	    } 
	    JSONObject resultObject = new JSONObject();
	    resultObject.put("Counts", limitGroups);
	    result = JSONToResult.convert(resultObject.toString());
	    return result;
	  }
	}

