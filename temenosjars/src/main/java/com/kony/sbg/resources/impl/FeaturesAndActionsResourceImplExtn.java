package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.adminconsole.commons.utils.CommonUtilities;
import com.kony.adminconsole.commons.utils.FabricConstants;
import com.kony.adminconsole.commons.utils.ThreadExecutor;
import com.kony.adminconsole.core.security.LoggedInUserHandler;
import com.kony.adminconsole.core.security.UserDetailsBean;
import com.kony.adminconsole.exception.ApplicationException;
import com.kony.adminconsole.handler.AuditHandler;
import com.kony.adminconsole.service.featuresandactions.businessdelegate.api.FeaturesAndActionsBusinessDelegate;
import com.kony.adminconsole.service.featuresandactions.dto.ActionDependencyDTO;
import com.kony.adminconsole.service.featuresandactions.dto.ActionsDTO;
import com.kony.adminconsole.service.featuresandactions.dto.FeatureDTO;
import com.kony.adminconsole.service.featuresandactions.resource.impl.FeaturesAndActionsResourceImpl;
import com.kony.adminconsole.utilities.ActivityStatusEnum;
import com.kony.adminconsole.utilities.ErrorCodeEnum;
import com.kony.adminconsole.utilities.EventEnum;
import com.kony.adminconsole.utilities.ModuleNameEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class FeaturesAndActionsResourceImplExtn extends FeaturesAndActionsResourceImpl{
	private static final String INPUT_CIF = "cif";
	private static final Logger LOG = Logger.getLogger(FeaturesAndActionsResourceImplExtn.class);
	FeaturesAndActionsBusinessDelegate featuresAndActionsBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(FeaturesAndActionsBusinessDelegate.class);
	String cif  = StringUtils.EMPTY;
	@Override
	public Result editFeatureAndActions(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		LOG.debug("entry ---->editFeatureAndActions()");
		@SuppressWarnings("unchecked")
		Map<String, String> inputParams = (HashMap<String, String>) inputArray[1];
		
		UserDetailsBean loggedInUserDetails;
		try {
			loggedInUserDetails = LoggedInUserHandler.getUserDetails(request);
			inputParams.put("modifiedby", loggedInUserDetails.getUserName());
		} catch (ApplicationException e1) {
			//e1.printStackTrace();
		}
		
		FeatureDTO featureDTOs = validateInput(request);
		if(featureDTOs.getErrCode() != null) {
			return featureDTOs.getErrCode().setErrorCode(new Result());
		}
		String serviceFee = request.getParameter("serviceFee");
		String featureId = request.getParameter("featureId");
		String statusId = request.getParameter("statusId");
		
		inputParams.put("serviceFee",serviceFee);
		inputParams.put("statusId",statusId);
		inputParams.put("featureId",featureId);
		inputParams.put("lastmodifiedts", CommonUtilities.getISOFormattedLocalTimestamp());
		
		FeatureDTO featureDTO = new FeatureDTO();
		try {
			featureDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FeatureDTO.class);
		} catch (IOException e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_21908.setErrorCode(new Result());
		}
		
		featureDTO = featuresAndActionsBusinessDelegate.editFeatureDetails(featureDTO);
		if(featureDTO == null) {
			LOG.error("Unable to edit the feature");
			return ErrorCodeEnum.ERR_21927.setErrorCode(new Result());
		}
		
		JSONArray featureDisplay = new JSONArray();
		try {
			featureDisplay = new JSONArray(request.getParameter("featureDisplay"));
		} catch (Exception e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_21930.setErrorCode(new Result());
		}
		
		if(featureDisplay.length() > 0) {
			if(!updateFeatureDisplayName(featureDTO,featureDisplay)) {
				LOG.error("Error while editing feature display name and description ");
				return ErrorCodeEnum.ERR_21931.setErrorCode(new Result());
			}
		}
		
		JSONArray actions = new JSONArray();
		try {
			actions = new JSONArray(request.getParameter("actions"));
		} catch (Exception e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_21932.setErrorCode(new Result());
		}
		
		ActionsDTO actionsDTO = new ActionsDTO();
		try {
			actionsDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), ActionsDTO.class);
		} catch (IOException e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_21908.setErrorCode(new Result());
		}
		
		if (actions.length() > 0) {
			if (!editActionDetails(actionsDTO, actions)) {
				LOG.error("Caught exception while converting input params to DTO: ");
				return ErrorCodeEnum.ERR_21933.setErrorCode(new Result());
			}
				if (!editActionDependency(actionsDTO, actions, featureId,statusId)) {
					LOG.error("Caught exception while converting input params to DTO: "); 
					return ErrorCodeEnum.ERR_21933.setErrorCode(new Result());
				}
		}
		Map<String,String> actionStatusMap = getActionStatus(featureId);
		result = JSONToResult.convert(new JSONObject(featureDTO).toString());
		result.addParam(new Param("status", "Success", FabricConstants.STRING));
		LOG.debug("editFeatureAndActions=========status "+FabricConstants.STRING);
		cif  = request.getParameter(INPUT_CIF);
		 AuditHandler.auditAdminActivity(request, ModuleNameEnum.FACILITY, EventEnum.UPDATE,
                 ActivityStatusEnum.SUCCESSFUL, "Successfully updated feature and facilities");
    
		Dataset actionStatus = getActionDataSet(actionStatusMap, actions);
		result.addDataset(actionStatus);
		return result;
	}
	private FeatureDTO validateInput(DataControllerRequest request) {
		FeatureDTO featureDTO = new FeatureDTO();
		String featureId = request.getParameter("featureId");
		if (StringUtils.isBlank(featureId)) {
			LOG.error("Id cannot be null ");
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21928);
			return featureDTO;
		}
		
		String serviceFee = request.getParameter("serviceFee");
		if (StringUtils.isNotBlank(serviceFee)) 
		{
			try {
			Double.parseDouble(serviceFee);
			}
			catch(Exception e) {
			LOG.error("serviceFee is not numeric");
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21929);
			return featureDTO;
			}
			
		}
		if (StringUtils.isBlank(serviceFee) || (Double.parseDouble(serviceFee) < 0.0))
		{
			//LOG.error("serviceFee cannot be null ");
			LOG.error("serviceFee cannot be null or negative");
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21929);
			return featureDTO;
		}
		
		/*if (StringUtils.isBlank(serviceFee)) {
			LOG.error("serviceFee cannot be null ");
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21929);
			return featureDTO;
		}*/
		
		String statusId = request.getParameter("statusId");
		if ( StringUtils.isBlank(statusId) ||(!statusId.equalsIgnoreCase("SID_FEATURE_ACTIVE") && !statusId.equalsIgnoreCase("SID_FEATURE_INACTIVE"))) {
			LOG.error("status should be SID_FEATURE_ACTIVE or SID_FEATURE_INACTIVE");
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21915);
			return featureDTO;
		}
		
		JSONArray featureDisplay = new JSONArray();
		try {
			featureDisplay = new JSONArray(request.getParameter("featureDisplay"));
		} catch (Exception e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21930);
			return featureDTO;
		}
		for(int index = 0;index < featureDisplay.length();index++) {
			JSONObject featureDisplayObj = featureDisplay.optJSONObject(index);
			String localeId = featureDisplayObj.optString("localeId");
			String displayName = featureDisplayObj.optString("displayName");
			String displayDescription = featureDisplayObj.optString("displayDescription");

			if (StringUtils.isBlank(localeId)) {
				LOG.error("Locale id cannot be empty");
				featureDTO.setErrCode(ErrorCodeEnum.ERR_21985); 
				return featureDTO;
			}

			if (StringUtils.isBlank(displayName) || containSpecialChars(displayName)) {
				LOG.error("Feature display name cannot be empty");
				featureDTO.setErrCode(ErrorCodeEnum.ERR_21986);
				return featureDTO;
			}
			
			if (StringUtils.isBlank(displayDescription) || containSpecialChars(displayDescription)) {
				LOG.error("Feature display description cannot be empty");
				featureDTO.setErrCode(ErrorCodeEnum.ERR_21987);
				return featureDTO;
			}
		}
		
		JSONArray actions = new JSONArray();
		try {
			actions = new JSONArray(request.getParameter("actions"));
		} catch (Exception e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			featureDTO.setErrCode(ErrorCodeEnum.ERR_21932); 
			return featureDTO;
		}
		
		if(actions != null && actions.length() > 0) {
			for(int index = 0;index < actions.length();index++) {
				JSONObject actionObject = actions.optJSONObject(index);
				String actionId = actionObject.optString("actionId");
				if (StringUtils.isBlank(actionId)) {
					LOG.error("Action Id is mandatory input");
					featureDTO.setErrCode(ErrorCodeEnum.ERR_21988); 
					return featureDTO;
				}

				String actionStatus = actionObject.optString("statusId");
				if ( StringUtils.isBlank(actionStatus) ||(!actionStatus.equalsIgnoreCase("SID_ACTION_ACTIVE") && !actionStatus.equalsIgnoreCase("SID_ACTION_INACTIVE"))) {
					LOG.error("status should be SID_ACTION_ACTIVE or SID_ACTION_INACTIVE");
					featureDTO.setErrCode(ErrorCodeEnum.ERR_21989); 
					return featureDTO;
				}
				JSONArray limits = actionObject.getJSONArray("limits");
				if(limits != null && limits.length() > 0) {
					if(!validateLimits(limits)) {
						LOG.error("Error while updating the action details");
						featureDTO.setErrCode(ErrorCodeEnum.ERR_21990); 
						return featureDTO;
					}
				}
				
				JSONArray actionDisplay = actionObject.getJSONArray("actionDisplay");
			if(actionDisplay != null && actionDisplay.length() >0) {
				for(int actionIndex = 0;actionIndex < actionDisplay.length();actionIndex++) {
					JSONObject actionDisplayObj = actionDisplay.optJSONObject(actionIndex);
					String localeId = actionDisplayObj.optString("localeId");
					String displayName = actionDisplayObj.optString("displayName");
					String displayDescription = actionDisplayObj.optString("displayDescription");

					if (StringUtils.isBlank(localeId)) {
						LOG.error("Locale id cannot be empty");
						featureDTO.setErrCode(ErrorCodeEnum.ERR_21985); 
						return featureDTO;
					}

					if (StringUtils.isBlank(displayName) || containSpecialChars(displayName)) {
						LOG.error("Action display name cannot be empty");
						featureDTO.setErrCode(ErrorCodeEnum.ERR_21986); 
						return featureDTO;
					}
					
					if (StringUtils.isBlank(displayDescription) || containSpecialChars(displayDescription)) {
						LOG.error("Action display description cannot be empty");
						featureDTO.setErrCode(ErrorCodeEnum.ERR_21987); 
						return featureDTO;
					}
				}
				
			}
			}
		}
		return featureDTO;
	}
	/**
	 * Method to update the display name and description of the feature
	 * @param featureDTO contains the details of the feature
	 * @param featureDisplay contains the details like name.
	 * @return true on a successful edit
	 */
	private boolean updateFeatureDisplayName(FeatureDTO featureDTO, JSONArray featureDisplay) {
		
		if(featureDisplay != null && featureDisplay.length() > 0) {
			String id = featureDTO.getId();
			for(int index = 0;index < featureDisplay.length();index++) {
				JSONObject featureDisplayObj = featureDisplay.optJSONObject(index);
				String localeId = featureDisplayObj.optString("localeId");
				String displayName = featureDisplayObj.optString("displayName");
				String displayDescription = featureDisplayObj.optString("displayDescription");
				featureDTO.setId(id);
				featureDTO.setLocaleId(localeId);
				featureDTO.setDisplayName(displayName);
				featureDTO.setDisplayDescription(displayDescription);
				featureDTO = featuresAndActionsBusinessDelegate.editFeatureDisplayNameDetails(featureDTO);
				if(featureDTO == null) {
					LOG.error("Error while editing display details");
					return false;
				}
			}
		}
		
		return true;
	}
	/**
	 * Updates the action status.
	 * @param actionsDTO contains the action details
	 * @param actions JSONArray of the details of the action
	 * @return true on successful update
	 */
	private boolean editActionDetails(ActionsDTO actionsDTO, JSONArray actions) {
		if(actions != null && actions.length() > 0) {
			for(int index = 0;index < actions.length();index++) {
				JSONObject actionObject = actions.optJSONObject(index);
				String actionId = actionObject.optString("actionId");
				if (StringUtils.isBlank(actionId)) {
					LOG.error("Action Id is mandatory input");
					return false;
				}
				actionsDTO.setId(actionId);

				JSONArray limits = actionObject.getJSONArray("limits");
				if(limits != null && limits.length() > 0) {
					if(!validateActionLimits(limits, actionsDTO)) {
						LOG.error("Error while updating the action details");
						return false;
					}
				}
				
				JSONArray actionDisplay = actionObject.getJSONArray("actionDisplay");
				if(!updateActionDisplayName(actionsDTO, actionDisplay)) {
					LOG.error("Error while updating the action details");
					return false;
				}
				
			
			}
		}
		return true;
	}
	private boolean editActionDependency(ActionsDTO actionsDTO, JSONArray actions, String featureId,String featureStatus) {

		List<ActionDependencyDTO> actionDependencies = featuresAndActionsBusinessDelegate
				.fetchActionDependencies(featureId);
		if (actionDependencies == null) {
			return false;
		}

		Map<String, List<String>> inActivateActionsMap = getInActivateAction(actionDependencies);
        Map<String,Map<String,String>> activateMap = getActivateAction(actionDependencies);
        List<String> activateList = new ArrayList<>();
        List<String> inActivateList = new ArrayList<>();
        for(int index=0; index < actions.length();index++) {
        	JSONObject actionObject = actions.optJSONObject(index);
        	String actionId = actionObject.optString("actionId");
        	String actionStatus = actionObject.optString("statusId");
        	if(actionStatus.equals("SID_ACTION_ACTIVE")) {
        		activateList.add(actionId);
        	}else if(actionStatus.equals("SID_ACTION_INACTIVE")) {
        		inActivateList.add(actionId);
        	}
        }
        inActivateList = getInactivateList(inActivateList,inActivateActionsMap);
        Set<String> inactivateSet = new HashSet<>(inActivateList);
        activateList.removeAll(inActivateList);
        activateList = getActivateList(activateList,activateMap);
        Set<String> activateSet = new HashSet<>(activateList);
        if(!editActionStatus(actionsDTO,inactivateSet,"SID_ACTION_INACTIVE")) {
        	return false;
        }
		if (("SID_FEATURE_ACTIVE").equals(featureStatus)) {
			if (!editActionStatus(actionsDTO, activateSet, "SID_ACTION_ACTIVE")) {
				return false;
			}
        }
		return true;
	}
	/**
	 * Fetches the action and their respective status based on the feature ID 
	 * @param featureId
	 * @return
	 */
	private Map<String,String> getActionStatus(String featureId){
		Map<String,String> actionStatusMap = new HashMap<String, String>();
		List<ActionsDTO> actions = featuresAndActionsBusinessDelegate.getActionDetails(featureId);
		if(actions == null) {
			return null;
		}
		for(ActionsDTO action : actions) {
			actionStatusMap.put(action.getId(),action.getStatusId());
		}
		return actionStatusMap;
	}
	private Dataset getActionDataSet(Map<String,String> actionStatusMap,JSONArray actions) {
		Dataset actionStatus = new Dataset("actionStatus");
		for (int index = 0; index < actions.length(); index++) {
			JSONObject actionObject = actions.optJSONObject(index);
			String actStatus = actionObject.optString("statusId");
			String actionId = actionObject.optString("actionId");
			Record update = new Record();
			update.addParam(new Param("id", actionId));
			if (actionStatusMap != null && actionStatusMap.get(actionId) != null && 
					actionStatusMap.get(actionId).equals(actStatus)) {
				update.addParam(new Param("updatedStatus", "success"));
			} else {
				update.addParam(new Param("updatedStatus", "failure"));
			}
			actionStatus.addRecord(update);
		}
		return actionStatus;
	}
	private boolean containSpecialChars(String name) {
		Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>^*%!-]");
		if (regex.matcher(name).find()) {
		    return true;
		} 
		return false;
	}
	private boolean validateLimits(JSONArray limits) {
		if(limits != null && limits.length() > 0) {
			Double minTxVal = 0.0, maxTxVal = 0.0, dailyTxVal = 0.0, weeklyTxVal = 0.0;
			for(int index = 0;index < limits.length(); index++) {
				JSONObject limitObj = limits.optJSONObject(index);
				String type = limitObj.optString("type");
				String value = limitObj.optString("value");

				if (StringUtils.isBlank(value)) {
					LOG.error("Limit cannot be empty");
					return false;
				}

				if (StringUtils.equals(type, "MIN_TRANSACTION_LIMIT")) {
					minTxVal = Double.parseDouble(value);
				} else if (StringUtils.equals(type, "MAX_TRANSACTION_LIMIT")) {
					maxTxVal = Double.parseDouble(value);
				} else if (StringUtils.equals(type, "DAILY_LIMIT")) {
					dailyTxVal = Double.parseDouble(value);
				} else if (StringUtils.equals(type, "WEEKLY_LIMIT")) {
					weeklyTxVal = Double.parseDouble(value);
				}
				
			}
			if (0 < minTxVal && minTxVal <= maxTxVal && maxTxVal <= dailyTxVal && dailyTxVal <= weeklyTxVal) {
				//return true;
			}else {
				LOG.error("Error while updating the action limits");
				return false;
			}
		}
		return true;
	}
	/**
	 * Validates and updates the action limits
	 * @param limits - contains the limit of the action
	 * @param actionsDTO - contains the action details
	 * @return true on successful update of the action limits
	 */
	private boolean validateActionLimits(JSONArray limits, ActionsDTO actionsDTO) {
		ActionsDTO limitsDTO = getActionLimits(actionsDTO.getId());
		boolean flag = false;
		if(limits != null && limits.length() > 0) {
			Double minTxVal = 0.0, maxTxVal = 0.0, dailyTxVal = 0.0, weeklyTxVal = 0.0;
			for(int index = 0;index < limits.length(); index++) {
				JSONObject limitObj = limits.optJSONObject(index);
				String type = limitObj.optString("type");
				String value = limitObj.optString("value");

				if (StringUtils.equals(type, "MIN_TRANSACTION_LIMIT")) {
					minTxVal = Double.parseDouble(value);
					actionsDTO.setMinTxLimit(minTxVal);
				} else if (StringUtils.equals(type, "MAX_TRANSACTION_LIMIT")) {
					maxTxVal = Double.parseDouble(value);
					actionsDTO.setMaxTxLimit(maxTxVal);
					if (limitsDTO.getMaxTxLimit() > maxTxVal) {
                          flag = true;
					}
				} else if (StringUtils.equals(type, "DAILY_LIMIT")) {
					dailyTxVal = Double.parseDouble(value);
					actionsDTO.setDailyLimit(dailyTxVal);
					if (limitsDTO.getDailyLimit() > dailyTxVal) {
                        flag = true;
					}
				} else if (StringUtils.equals(type, "WEEKLY_LIMIT")) {
					weeklyTxVal = Double.parseDouble(value);
					actionsDTO.setWeeklyLimit(weeklyTxVal);
					if (limitsDTO.getWeeklyLimit() > weeklyTxVal) {
                        flag = true;
					}
				}
				
			}
			if (0 < minTxVal && minTxVal <= maxTxVal && maxTxVal <= dailyTxVal && dailyTxVal <= weeklyTxVal) {
				//return true;
			}else {
				LOG.error("Error while updating the action limits");
				return false;
			}
			if(!featuresAndActionsBusinessDelegate.editActionLimits(actionsDTO)) {
				LOG.error("Error while updating the action limits");
				return false;
			}
			
			if(flag) {
				if(!actionLimitUpdates(actionsDTO)) {
					LOG.error("Error while updating the action limits");
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Updates the action display name and description.
	 * @param actionsDTO
	 * @param actionDisplay
	 * @return true on successful update
	 */
    private boolean updateActionDisplayName(ActionsDTO actionsDTO, JSONArray actionDisplay) {
		
		if(actionDisplay != null && actionDisplay.length() > 0) {
			String id = actionsDTO.getId();
			for(int index = 0;index < actionDisplay.length();index++) {
				JSONObject actionDisplayObj = actionDisplay.optJSONObject(index);
				String localeId = actionDisplayObj.optString("localeId");
				String displayName = actionDisplayObj.optString("displayName");
				String displayDescription = actionDisplayObj.optString("displayDescription");
				actionsDTO.setId(id);
				actionsDTO.setLocaleId(localeId);
				actionsDTO.setDisplayName(displayName);
				actionsDTO.setDisplayDescription(displayDescription);
				actionsDTO = featuresAndActionsBusinessDelegate.editActionDisplayName(actionsDTO);
				if(actionsDTO == null ) {
					LOG.error("Error while updating the action details");
					return false;
				}
			}
		}
		
		return true;
	}
    private Map<String,List<String>> getInActivateAction(List<ActionDependencyDTO> actionDependenciesDTOs) {

		Map<String,List<String>> inActivateActionsMap = new HashMap<>();
		for(ActionDependencyDTO action : actionDependenciesDTOs) {
			if(inActivateActionsMap.containsKey(action.getActionName())) {
				List<String> dependent = inActivateActionsMap.get(action.getActionName());
				dependent.add(action.getDependencyAction());
			}else {
				List<String> dependent = new ArrayList<>();
				dependent.add(action.getDependencyAction());
				inActivateActionsMap.put(action.getActionName(),dependent);
			}
		}
		
		return inActivateActionsMap;
	}
	
	private Map<String,Map<String,String>> getActivateAction(List<ActionDependencyDTO> actionDependenciesDTOs){
		Map<String,Map<String,String>> activateActionsMap = new HashMap<>();
		for(ActionDependencyDTO action : actionDependenciesDTOs) {
			if(activateActionsMap.containsKey(action.getDependencyAction())) {
				Map<String,String> dependentStatus = activateActionsMap.get(action.getDependencyAction());
				dependentStatus.put(action.getActionName(),action.getActionStatus());
			}else {
				Map<String,String> dependentStatus = new HashMap<>();
				dependentStatus.put(action.getActionName(),action.getActionStatus());
				activateActionsMap.put(action.getDependencyAction(),dependentStatus);
			}
		}
		return activateActionsMap;
	}
	private List<String> getInactivateList(List<String> inActivateList,Map<String,List<String>> inActivateActionsMap){
		
		List<String> inactivate = inActivateList;
		try {
			for(String action : inActivateList) {
				if(inActivateActionsMap.containsKey(action)) {
					inactivate.addAll(inActivateActionsMap.get(action));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inactivate;
	}
	
	private List<String> getActivateList(List<String> activateList,Map<String,Map<String,String>> activateMap){
		List<String> activate = activateList;
		try {
			for(String action : activateList) {
				if(activateMap.containsKey(action)) {
					Map<String,String> statusMap = activateMap.get(action);
					for (Map.Entry<String, String> actionStatus : statusMap.entrySet()) {
						if (!activate.contains(actionStatus.getKey())
								&& actionStatus.getValue().equals("SID_ACTION_INACTIVE")) {
							activate.remove(action);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return activate;
	}
	private boolean editActionStatus(ActionsDTO actionsDTO,Set<String> actions,String status) {
		
		for (String action : actions) {
			actionsDTO.setId(action);
			actionsDTO.setStatusId(status);
			actionsDTO = featuresAndActionsBusinessDelegate.editActionDetails(actionsDTO);
			if (actionsDTO == null) {
				LOG.error("Error while updating the action details");
				return false;
			}

		}
		return true;
	}
	private ActionsDTO getActionLimits(String actionId) {
		ActionsDTO limitsDTO = new ActionsDTO();
		List<ActionsDTO> limitsList = featuresAndActionsBusinessDelegate.getActionsLimits(actionId);
		for(ActionsDTO action : limitsList) {
			if(action.getLimitTypeId().equals("MAX_TRANSACTION_LIMIT")) {
				limitsDTO.setMaxTxLimit(Float.parseFloat(action.getValue()));
			}else if(action.getLimitTypeId().equals("DAILY_LIMIT")) {
				limitsDTO.setDailyLimit(Float.parseFloat(action.getValue()));
			}else if(action.getLimitTypeId().equals("WEEKLY_LIMIT")) {
				limitsDTO.setWeeklyLimit(Float.parseFloat(action.getValue()));
			}
		}
		return limitsDTO;
	}
	private boolean actionLimitUpdates(ActionsDTO actionsDTO) {
		Callable<Boolean> updateLimits = new Callable<Boolean>() {
			 @Override
			 public Boolean call() throws Exception{
				 return featuresAndActionsBusinessDelegate.updateLimits(actionsDTO);
			 }
		}; try {
            ThreadExecutor.execute(updateLimits);
        } catch (InterruptedException e) {
            LOG.error("actionLimitUpdates throw error ", e);
            Thread.currentThread().interrupt();
        }
		return true;
	}
}
