package com.kony.sbg.business.impl;


import java.util.HashMap;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.kony.adminconsole.service.approvalmatrix.businessdelegate.impl.ApprovalMatrixBusinessDelegateImpl;
import com.dbp.core.constants.DBPConstants;
import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.temenos.dbx.product.approvalmatrixservices.dto.ApprovalMatrixDTO;
import com.temenos.dbx.product.approvalmatrixservices.dto.SignatoryGroupMatrixDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

@SuppressWarnings("deprecation")
public class ApprovalMatrixBusinessDelegateExtn  extends ApprovalMatrixBusinessDelegateImpl{
	private static final Logger LOG = LogManager.getLogger(ApprovalMatrixBusinessDelegateExtn.class);
	public JSONObject updateApprovalSignatoryMatrixTemplateEntry(List<ApprovalMatrixDTO> approvalMatrixTemplateList, int approvalMode) {
		
		JSONObject result = new JSONObject();

		StringBuilder approvalmatrixTemplateValues = new StringBuilder();
		StringBuilder signatoryGroupMatrixTemplateValues = new StringBuilder();
		ApprovalMatrixDTO temp = null;
		for (int i = 0; i < approvalMatrixTemplateList.size(); i++) {
			temp = approvalMatrixTemplateList.get(i);
			String approvalRule = temp.getApprovalruleId()!= null && !StringUtils.isEmpty(temp.getApprovalruleId())? "\""+temp.getApprovalruleId()+"\"" : null;
			// contractId,coreCustomerId,actionId,approvalruleId,limitTypeId,lowerlimit,upperlimit
			approvalmatrixTemplateValues.append("\"").append(temp.getContractId()).append("\"").append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getCifId()).append("\"").append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getActionId()).append("\"").append(";");
			approvalmatrixTemplateValues.append(approvalRule).append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getLimitTypeId()).append("\"").append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getLowerlimit()).append("\"").append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getUpperlimit()).append("\"").append(";");
			approvalmatrixTemplateValues.append("\"").append(temp.getIsGroupMatrix()).append("\"").append(",");

			

			SignatoryGroupMatrixDTO temp2 = temp.getSignatoryGroupMatrixDTO();
			signatoryGroupMatrixTemplateValues.append(temp2.getGroupList()).append(";");
			signatoryGroupMatrixTemplateValues.append(temp2.getGroupRule()).append("#");

		}

		approvalmatrixTemplateValues.deleteCharAt(approvalmatrixTemplateValues.length() - 1);
		signatoryGroupMatrixTemplateValues.deleteCharAt(signatoryGroupMatrixTemplateValues.length() - 1);
		if (callCreateApprovalMatrixTemplateStoredProc(approvalmatrixTemplateValues.toString(), signatoryGroupMatrixTemplateValues.toString(), approvalMode)) {
			result.put("Success", true);
			return result;
		} else {
			result.put("Success", false);
			result.put("ErrorCode", 1);
			return result;
		}

	}
	

	public Boolean callCreateApprovalMatrixTemplateStoredProc(String approvalmatrixTemplateValues, String approvalIds, int approvalMode) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CREATE_APPROVALMATRIXTEMPLATE_PROC;

		HashMap<String, Object> requestParameters = new HashMap<>();
		HashMap<String, Object> requestHeaders = new HashMap<>();

		requestParameters.put("_matrixValues", approvalmatrixTemplateValues);
		requestParameters.put("_matrixApprover", approvalIds);
		requestParameters.put("_isGroupMatrix", approvalMode);

		try {
			String approvalMatrixResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
					operationName, requestParameters, requestHeaders, "");
			JSONObject approvalMatrixResponseJson = new JSONObject(approvalMatrixResponse);
			if (approvalMatrixResponseJson.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) == 0) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			LOG.error("Unable to Update ApprovalMatrixTemplate Entry: " , e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at callCreateApprovalMatrixTemplateStoredProc method: " , e);
			return false;
		}

	}
}
