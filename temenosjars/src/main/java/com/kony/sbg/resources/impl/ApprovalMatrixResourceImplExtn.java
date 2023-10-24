package com.kony.sbg.resources.impl;

import com.kony.adminconsole.service.approvalmatrix.resource.impl.ApprovalMatrixResourceImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.adminconsole.commons.utils.CommonUtilities;
import com.kony.adminconsole.commons.utils.FabricConstants;
import com.kony.adminconsole.handler.AuditHandler;
import com.kony.adminconsole.service.approvalmatrix.businessdelegate.api.ApprovalMatrixBusinessDelegate;
import com.kony.adminconsole.utilities.ActivityStatusEnum;
import com.kony.adminconsole.utilities.DBPServices;
import com.kony.adminconsole.utilities.ErrorCodeEnum;
import com.kony.adminconsole.utilities.EventEnum;
import com.kony.adminconsole.utilities.ModuleNameEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalmatrixservices.dto.ApprovalMatrixDTO;
import com.temenos.dbx.product.approvalmatrixservices.dto.SignatoryGroupMatrixDTO;
import com.temenos.dbx.product.constants.Constants;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
public class ApprovalMatrixResourceImplExtn extends ApprovalMatrixResourceImpl{
private static final Logger LOG = Logger.getLogger(ApprovalMatrixResourceImplExtn.class);
	
	
	private static final String INPUT_CONTRACT_ID = "contractId";
	private static final String INPUT_ACCOUNT_ID = "accountId";
	private static final String INPUT_FEATURE_ID = "featureId";
	private static final String INPUT_ACTION_ID = "actionId";
	private static final String INPUT_IS_GROUP_MATRIX = "isGroupMatrix";
	private static final String INPUT_LIMIT_TYPE_ID = "limitTypeId";
	private static final String INPUT_LIMITS = "limits";
	private static final String INPUT_SG_ID = "signatoryGroupId";
	private static final String INPUT_CIF = "cif";
	private static final String INPUT_DISABLE = "disable";
	
	ApprovalMatrixBusinessDelegate approvalmatrixBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
    .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(ApprovalMatrixBusinessDelegate.class);

	@Override
	public Result updateApprovalRuleSGLevel(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
			DataControllerResponse response) {
		LOG.debug("Entry-------->updateApprovalRuleSGLevel()");
		Result result = new Result();
		String cif  = StringUtils.EMPTY;
		String actionId = StringUtils.EMPTY;
		String limitTypeId = StringUtils.EMPTY;
		String limits = StringUtils.EMPTY;
		String isGroupMatrix = StringUtils.EMPTY;
		String contractId = StringUtils.EMPTY;
		try {
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_CIF))) {
	            ErrorCodeEnum.ERR_22108.setErrorCode(result);
	            return result;
	        }
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_CONTRACT_ID))) {
	            ErrorCodeEnum.ERR_22097.setErrorCode(result);
	            return result;
	        }
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_ACTION_ID))) {
	            ErrorCodeEnum.ERR_20866.setErrorCode(result);
	            return result;
	        }
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_LIMIT_TYPE_ID))) {
	            ErrorCodeEnum.ERR_22109.setErrorCode(result);
	            return result;
	        }
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_LIMITS))) {
	            ErrorCodeEnum.ERR_22110.setErrorCode(result);
	            return result;
	        }
	    	
			contractId = requestInstance.getParameter(INPUT_CONTRACT_ID);
			cif  = requestInstance.getParameter(INPUT_CIF);
			String accountId = StringUtils.isBlank(requestInstance.getParameter(INPUT_ACCOUNT_ID)) ? StringUtils.EMPTY : requestInstance.getParameter(INPUT_ACCOUNT_ID) ;
			isGroupMatrix = StringUtils.isBlank(requestInstance.getParameter(INPUT_IS_GROUP_MATRIX))? "1" : requestInstance.getParameter(INPUT_IS_GROUP_MATRIX);
			actionId = requestInstance.getParameter(INPUT_ACTION_ID);
			limitTypeId = requestInstance.getParameter(INPUT_LIMIT_TYPE_ID);
			limits = requestInstance.getParameter(INPUT_LIMITS);

	    	String dbpServicesClaimsToken = DBPServices.getDBPServicesClaimsToken(requestInstance);
	    	Map<String, Object> postParametersMap = new HashMap<>();
	    	postParametersMap.put("cif", cif);
	    	postParametersMap.put("contractId", contractId);
	    	postParametersMap.put("accountId", accountId);
	    	postParametersMap.put("actionId", actionId);
	    	postParametersMap.put("limitTypeId", limitTypeId);
	    	postParametersMap.put("limits", stringifyForVelocityTemplate(limits));
	    	postParametersMap.put("isGroupMatrix", isGroupMatrix);
	        
	        JSONObject serviceResponse =
	        		approvalmatrixBusinessDelegate.updateApprovalRuleSGLevel(postParametersMap, dbpServicesClaimsToken);
	        if (serviceResponse == null || !serviceResponse.has(FabricConstants.OPSTATUS)
                    || serviceResponse.getInt(FabricConstants.OPSTATUS) != 0) {
                ErrorCodeEnum.ERR_22106.setErrorCode(result);
                result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.APPROVALMATRIX, EventEnum.UPDATE,
                        ActivityStatusEnum.FAILED, "Failed to Update ApprovalRule at Signatory Group Level, cifId :"+cif);
                return result;
            } else if (serviceResponse.has("dbpErrMsg")) {
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            	result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                return result;
            } else {
            	LOG.debug("Entry-------->updateApprovalRuleSGLevel()"+serviceResponse.toString());
            	 AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.APPROVALMATRIX, EventEnum.UPDATE,
                         ActivityStatusEnum.SUCCESSFUL, "Updated ApprovalRule at Signatory Group Level is SuccessFull, cifId :"+cif);
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            }
		}catch (Exception e) {
            LOG.error("Unexpected Error in updateApprovalRuleSGLevel", e);
            result.addParam(new Param("FailureReason", e.getMessage(), FabricConstants.STRING));
            ErrorCodeEnum.ERR_22106.setErrorCode(result);
            AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.APPROVALMATRIX, EventEnum.UPDATE,
                    ActivityStatusEnum.FAILED, "Failed to Update ApprovalRule at Signatory Group  Level, cifId :"+cif);
        }

        return result;
        
	}
	private  String stringifyForVelocityTemplate(String str) {
		if (StringUtils.isBlank(str)) {

			return "\"\"";
		}else if(str.contains("\\")) {
			str= str.replace("\\","\\\\" );
		}
		return "\"" + str.replace("\"", "\\\"") + "\"";
	}
	
private SignatoryGroupMatrixDTO _updateParticipatingGroups(SignatoryGroupMatrixDTO signatoryGroupMatrixDTO) {
		
		Set<Integer> participatingGroupIndex = new HashSet<Integer>();
		
		String groupList = signatoryGroupMatrixDTO.getGroupList();
		String groupRule = signatoryGroupMatrixDTO.getGroupRule();
		
		if(StringUtils.isEmpty(groupList) || StringUtils.isEmpty(groupRule) || !Pattern.matches("(.)*[1-9](.)*",groupRule)) {
			signatoryGroupMatrixDTO.setGroupList("[]");
			signatoryGroupMatrixDTO.setGroupRule("[]");
			return signatoryGroupMatrixDTO;
		}
		
		groupList = groupList.replaceAll("\\s+","");
		groupList = groupList.substring(1, groupList.length()-1);
		List<String> groups = Arrays.asList(groupList.split(","));
		
		groupRule = groupRule.replaceAll("\\s+","");
		groupRule = groupRule.replace("[", "");
		groupRule = groupRule.substring(0, groupRule.length() - 2);
		String strList[] = groupRule.split("],");

		int rowLength = strList.length;
		int colLength = strList[0].split(",").length;

		List<List<String>> groupRules = new ArrayList<List<String>>();
		for (int i = 0; i < rowLength; i++) {
			String single_int[] = strList[i].split(",");
			groupRules.add(Arrays.asList(single_int));
			for (int j = 0; j < colLength; j++) {
				if(Integer.parseInt(single_int[j]) != 0) {
					participatingGroupIndex.add(j);
				}
			}
		}
		
		String[] finalGroupList = new String[participatingGroupIndex.size()];
		String[][] finalGroupRule = new String[rowLength][participatingGroupIndex.size()]; 
		int k = 0;
		for (int i = 0; i < colLength; i++) {
			if(participatingGroupIndex.contains(i)) {
				finalGroupList[k] = groups.get(i);
				
				for (int j = 0; j < rowLength; j++) {
					finalGroupRule[j][k] = groupRules.get(j).get(i);
				}
				
				k++;
			}
		}
		
		signatoryGroupMatrixDTO.setGroupList("[" + String.join(",", finalGroupList) + "]");
		signatoryGroupMatrixDTO.setGroupRule(Arrays.deepToString(finalGroupRule));
		
		Arrays.deepToString(finalGroupRule);
		return signatoryGroupMatrixDTO;
	}

protected List<ApprovalMatrixDTO> _populateApprovalSignatoryMatrixDetails(DataControllerRequest request, List<String> accounts) {
	try {
		List<ApprovalMatrixDTO> approvalMatrixDTOList = new ArrayList<ApprovalMatrixDTO>();		
		JSONArray limits = new JSONArray(request.getParameter(Constants.LIMITS));

        for( int accountIndex = 0; accountIndex < accounts.size(); accountIndex++) {
			for (int i = 0; i < limits.length(); i++) {		
				ApprovalMatrixDTO approvalMatrixDTO = new ApprovalMatrixDTO();
				approvalMatrixDTO.setContractId(request.getParameter(Constants.CONTRACTID));
				approvalMatrixDTO.setCifId(request.getParameter(Constants.CIF));
				approvalMatrixDTO.setAccountId(accounts.get(accountIndex));
				approvalMatrixDTO.setActionId(request.getParameter(Constants.ACTIONID));
				approvalMatrixDTO.setLimitTypeId(request.getParameter(Constants.LIMITTYPEID));
				approvalMatrixDTO.setIsGroupMatrix("1");
				JSONObject limit = limits.getJSONObject(i);
				approvalMatrixDTO.setLowerlimit(limit.getString(Constants.LOWERLIMIT));
				approvalMatrixDTO.setUpperlimit(limit.getString(Constants.UPPERLIMIT));
				
					SignatoryGroupMatrixDTO signatoryGroupMatrixDTO = new SignatoryGroupMatrixDTO();
					signatoryGroupMatrixDTO.setGroupList(limit.getString(Constants.GROUPLIST));
					signatoryGroupMatrixDTO.setGroupRule(limit.getString(Constants.GROUPRULE));
					signatoryGroupMatrixDTO = _updateParticipatingGroups(signatoryGroupMatrixDTO);
                    if(signatoryGroupMatrixDTO.getGroupRule().replaceAll("\\s", "").equals("[]") || signatoryGroupMatrixDTO.getGroupList().replaceAll("\\s", "").equals("[]")) {
                        approvalMatrixDTO.setApprovalruleId(Constants.NO_APPROVAL);
                        signatoryGroupMatrixDTO.setGroupList("[]");
                        signatoryGroupMatrixDTO.setGroupRule("[]");
                    }
		
				approvalMatrixDTO.setSignatoryGroupMatrixDTO(signatoryGroupMatrixDTO);
				approvalMatrixDTOList.add(i,approvalMatrixDTO);			
			}           	
        }

		return approvalMatrixDTOList;
	} catch (JSONException e) {			
		LOG.error(" failed at _populateApprovalMatrixDetails" , e);
		return null;
	}
	catch (Exception e) {			
		LOG.error(" failed at _populateApprovalMatrixDetails" , e);
		return null;
	}
}

protected List<ApprovalMatrixDTO> _populateApprovalSignatoryMatrixTemplateDetails(DataControllerRequest request) {
	try {
		List<ApprovalMatrixDTO> templateDTOList = new ArrayList<ApprovalMatrixDTO>();
		JSONArray limits = new JSONArray(request.getParameter(Constants.LIMITS));

		for (int i = 0; i < limits.length(); i++) {
			ApprovalMatrixDTO templateDTO = new ApprovalMatrixDTO();
			templateDTO.setContractId(request.getParameter(Constants.CONTRACTID));
			templateDTO.setCifId(request.getParameter(Constants.CIF));
			templateDTO.setActionId(request.getParameter(Constants.ACTIONID));
			templateDTO.setLimitTypeId(request.getParameter(Constants.LIMITTYPEID));
			templateDTO.setIsGroupMatrix("1");
			JSONObject limit = limits.getJSONObject(i);
			templateDTO.setLowerlimit(limit.getString(Constants.LOWERLIMIT));
			templateDTO.setUpperlimit(limit.getString(Constants.UPPERLIMIT));

			SignatoryGroupMatrixDTO signatoryGroupMatrixDTO = new SignatoryGroupMatrixDTO();
			signatoryGroupMatrixDTO.setGroupList(limit.getString(Constants.GROUPLIST));
			signatoryGroupMatrixDTO.setGroupRule(limit.getString(Constants.GROUPRULE));
            signatoryGroupMatrixDTO = _updateParticipatingGroups(signatoryGroupMatrixDTO);
            if(signatoryGroupMatrixDTO.getGroupRule().replaceAll("\\s", "").equals("[]") || signatoryGroupMatrixDTO.getGroupList().replaceAll("\\s", "").equals("[]")) {
                templateDTO.setApprovalruleId(Constants.NO_APPROVAL);
                signatoryGroupMatrixDTO.setGroupList("[]");
                signatoryGroupMatrixDTO.setGroupRule("[]");
            }
			templateDTO.setSignatoryGroupMatrixDTO(signatoryGroupMatrixDTO);
			templateDTOList.add(i, templateDTO);
		}

		return templateDTOList;
	} catch (JSONException e) {
		LOG.error(" failed at _populateApprovalSignatoryMatrixTemplateDetails", e);
		return null;
	} catch (Exception e) {
		LOG.error(" failed at _populateApprovalSignatoryMatrixTemplateDetails", e);
		return null;
	}
}

}
