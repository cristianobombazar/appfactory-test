package com.kony.sbg.resources.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.adminconsole.commons.utils.CommonUtilities;
import com.kony.adminconsole.commons.utils.FabricConstants;
import com.kony.adminconsole.handler.AuditHandler;
import com.kony.adminconsole.service.signatorygroup.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.kony.adminconsole.service.signatorygroup.resource.impl.SignatoryGroupResourceImpl;
import com.kony.adminconsole.utilities.ActivityStatusEnum;
import com.kony.adminconsole.utilities.DBPServices;
import com.kony.adminconsole.utilities.ErrorCodeEnum;
import com.kony.adminconsole.utilities.EventEnum;
import com.kony.adminconsole.utilities.ModuleNameEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class SignatoryGroupResourceImplExtn extends SignatoryGroupResourceImpl{
private static final Logger LOG = Logger.getLogger(SignatoryGroupResourceImplExtn.class);
	
	private static final String INPUT_SG_NAME = "signatoryGroupName";
	private static final String INPUT_SG_DESC = "signatoryGroupDescription";
	private static final String INPUT_SG_ID = "signatoryGroupId";
	private static final String INPUT_CORE_CISTOMER_ID = "coreCustomerId";
	private static final String INPUT_CONTRACT_ID = "contractId";
	private static final String INPUT_SIGNATORIES = "signatories";
	private static final String INPUT_USERNAME = "userName";
	private static final String INPUT_CORE_CUSTOMER_IDS = "coreCustomerIds";
	private static final String INPUT_IS_GROUP_LEVEL = "isGroupLevel";
	
	SignatoryGroupBusinessDelegate signatorygroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
            .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(SignatoryGroupBusinessDelegate.class);
	
	@Override
	public Result createSignatoryGroup(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
			DataControllerResponse response) {
		LOG.debug("Entry------>createSignatoryGroup()");
		Result result = new Result();
		String signatoryGroupName = StringUtils.EMPTY;
		String coreCustomerId = StringUtils.EMPTY;
		String contractId = StringUtils.EMPTY;
		String signatoryGroupDescription = StringUtils.EMPTY;
		
		try {
			
	    	if (StringUtils.isBlank(requestInstance.getParameter(INPUT_SG_NAME))) {
	            ErrorCodeEnum.ERR_22096.setErrorCode(result);
	            return result;
	        }
	    	
	    	if (StringUtils.isBlank(requestInstance.getParameter(INPUT_CORE_CISTOMER_ID))) {
	            ErrorCodeEnum.ERR_22038.setErrorCode(result);
	            return result;
	        }
	    	
	    	if (StringUtils.isBlank(requestInstance.getParameter(INPUT_CONTRACT_ID))) {
	            ErrorCodeEnum.ERR_22097.setErrorCode(result);
	            return result;
	        }
	    	
	    	signatoryGroupName = requestInstance.getParameter(INPUT_SG_NAME);
	    	coreCustomerId = requestInstance.getParameter(INPUT_CORE_CISTOMER_ID);
	    	contractId = requestInstance.getParameter(INPUT_CONTRACT_ID);
	    	
	    	if(StringUtils.isNotBlank(requestInstance.getParameter(INPUT_SG_DESC))) {
	    		signatoryGroupDescription = requestInstance.getParameter(INPUT_SG_DESC);
	    	}
	    	
	    	
	    	String signatories = "[]";
	    	
	    	if(StringUtils.isNotBlank(requestInstance.getParameter(INPUT_SIGNATORIES))) {
	    		signatories = stringifyForVelocityTemplate (requestInstance.getParameter(INPUT_SIGNATORIES));
	    	}
	    	
	    	String dbpServicesClaimsToken = DBPServices.getDBPServicesClaimsToken(requestInstance);
	    	Map<String, Object> postParametersMap = new HashMap<>();
	        postParametersMap.put("signatoryGroupName", signatoryGroupName);
	        postParametersMap.put("coreCustomerId", coreCustomerId);
	        postParametersMap.put("contractId", contractId);
	        postParametersMap.put("signatoryGroupDescription", signatoryGroupDescription);
	        postParametersMap.put("signatories", signatories);
	        
	        JSONObject serviceResponse =
	        		signatorygroupBusinessDelegate.createSignatoryGroup(postParametersMap, dbpServicesClaimsToken);
	    	LOG.debug("Entry------>createSignatoryGroup()"+serviceResponse.toString());
	        if (serviceResponse == null || !serviceResponse.has(FabricConstants.OPSTATUS)
                    || serviceResponse.getInt(FabricConstants.OPSTATUS) != 0) {
                ErrorCodeEnum.ERR_22088.setErrorCode(result);
                result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.CREATE,
                        ActivityStatusEnum.FAILED, "Signatory Group Creation failed: coreCustomerId:"+signatoryGroupName
                        +" coreCustomerId: "+ coreCustomerId + " contractId: "+contractId);
                return result;
            } else if (serviceResponse.has("dbpErrMsg")) {
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            	result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                return result;
            } else {
            	LOG.debug("Entry------>createSignatoryGroup()"+serviceResponse.toString());
            	  AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.CREATE,
                          ActivityStatusEnum.SUCCESSFUL, "Signatory Group Created Successfully: coreCustomerId:"+signatoryGroupName
                          +" coreCustomerId: "+ coreCustomerId + " contractId: "+contractId);
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            }
		}catch (Exception e) {
            LOG.error("Unexpected Error in createSignatoryGroup", e);
            result.addParam(new Param("FailureReason", e.getMessage(), FabricConstants.STRING));
            ErrorCodeEnum.ERR_22088.setErrorCode(result);
            AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.CREATE,
                    ActivityStatusEnum.FAILED, "Signatory Group Creation failed: coreCustomerId:"+signatoryGroupName
                    +" coreCustomerId: "+ coreCustomerId + " contractId: "+contractId);
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
	@Override
	public Result updateSignatoryGroups(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
			DataControllerResponse response) {
		
		Result result = new Result();
		String signatoryGroupName = StringUtils.EMPTY;
		String signatoryGroupId = StringUtils.EMPTY;
		String signatoryGroupDescription = StringUtils.EMPTY;
		
		try {
			
			if (StringUtils.isBlank(requestInstance.getParameter(INPUT_SG_ID))) {
	            ErrorCodeEnum.ERR_22098.setErrorCode(result);
	            return result;
	        }
			
	    	if (StringUtils.isBlank(requestInstance.getParameter(INPUT_SG_NAME))) {
	            ErrorCodeEnum.ERR_22096.setErrorCode(result);
	            return result;
	        }
	    	
	    	signatoryGroupId = requestInstance.getParameter(INPUT_SG_ID);
	    	signatoryGroupName = requestInstance.getParameter(INPUT_SG_NAME);
	    	
	    	if(StringUtils.isNotBlank(requestInstance.getParameter(INPUT_SG_DESC))) {
	    		signatoryGroupDescription = requestInstance.getParameter(INPUT_SG_DESC);
	    	}
	    	
	    	String signatories = "[]";
	    	
	    	if(StringUtils.isNotBlank(requestInstance.getParameter(INPUT_SIGNATORIES))) {
	    		signatories = stringifyForVelocityTemplate (requestInstance.getParameter(INPUT_SIGNATORIES));
	    	}
	    	
	    	String dbpServicesClaimsToken = DBPServices.getDBPServicesClaimsToken(requestInstance);
	    	Map<String, Object> postParametersMap = new HashMap<>();
	    	postParametersMap.put("signatoryGroupId", signatoryGroupId);
	        postParametersMap.put("signatoryGroupName", signatoryGroupName);
	        postParametersMap.put("signatoryGroupDescription", signatoryGroupDescription);
	        postParametersMap.put("signatories", signatories);
	        
	        JSONObject serviceResponse =
	        		signatorygroupBusinessDelegate.updateSignatoryGroups(postParametersMap, dbpServicesClaimsToken);
	        if (serviceResponse == null || !serviceResponse.has(FabricConstants.OPSTATUS)
                    || serviceResponse.getInt(FabricConstants.OPSTATUS) != 0) {
                ErrorCodeEnum.ERR_22089.setErrorCode(result);
                result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.UPDATE,
                        ActivityStatusEnum.FAILED, "Signatory Group Update failed: signatoryGroupId:"+signatoryGroupId);
                return result;
            } else if (serviceResponse.has("dbpErrMsg")) {
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            	result.addParam(new Param("status", "Failure", FabricConstants.STRING));
                return result;
            } else {
            	
            	result = CommonUtilities.constructResultFromJSONObject(serviceResponse);
            	AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.UPDATE,
                        ActivityStatusEnum.SUCCESSFUL, "Signatory Group Updated: signatoryGroupId:"+signatoryGroupId);
         
            }
		}catch (Exception e) {
            LOG.error("Unexpected Error in updateSignatoryGroups", e);
            result.addParam(new Param("FailureReason", e.getMessage(), FabricConstants.STRING));
            ErrorCodeEnum.ERR_22089.setErrorCode(result);
            AuditHandler.auditAdminActivity(requestInstance, ModuleNameEnum.SIGNATORYGROUP, EventEnum.UPDATE,
                    ActivityStatusEnum.FAILED, "Signatory Group Update failed: signatoryGroupId:"+signatoryGroupId);
        }

        return result;
	}
}
