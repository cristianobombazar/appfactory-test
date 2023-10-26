
package com.kony.sbg.backend.impl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.eum.product.contract.backenddelegate.impl.ContractBackendDelegateImpl;
import com.temenos.dbx.product.businessdelegate.api.DependentActionsBusinessDelegate;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.FeatureActionLimitsDTO;
import com.temenos.dbx.product.utils.InfinityConstants;

public class ContractBackendDelegateImplExtn extends ContractBackendDelegateImpl {
	LoggerUtil logger = new LoggerUtil(ContractBackendDelegateImpl.class);

	@Override
	public FeatureActionLimitsDTO getRestrictiveFeatureActionLimits(String serviceDefinitionId, String contractId,
			String roleId, String coreCustomerId, String userId, Map<String, Object> headersMap, boolean isSuperAdmin,
			String accessPolicyIdList) throws ApplicationException {
		FeatureActionLimitsDTO dto = new FeatureActionLimitsDTO();
		Map<String, Object> procParams = new HashMap<>();
		String locale = "en-GB";
		if (headersMap.containsKey("locale") && headersMap.get("locale") != null
				&& StringUtils.isNotBlank(headersMap.get("locale").toString())) {
			String finallocale = headersMap.get("locale").toString();
			String[] localearray = finallocale.split("_");
			if (localearray.length <= 1)
				locale = finallocale;
			else {
				StringBuilder sb = new StringBuilder();
				sb.append(localearray[0]);
				sb.append("-");
				sb.append(localearray[1]);
				locale = sb.toString();
			}
		}
		procParams.put("_locale", locale);
		procParams.put("_userId", StringUtils.isNotBlank(userId) ? userId : "");
		procParams.put("_serviceDefinitionId", StringUtils.isNotBlank(serviceDefinitionId) ? serviceDefinitionId : "");
		procParams.put("_roleId", StringUtils.isNotBlank(roleId) ? roleId : "");
		procParams.put("_coreCustomerId", StringUtils.isNotBlank(coreCustomerId) ? coreCustomerId : "");
		procParams.put("_accessPolicyIdList", String.valueOf(accessPolicyIdList));
		try {
			JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(procParams, headersMap,
					URLConstants.FETCH_RESTRICTIVE_FEATUREACTIONLIMITS_PROC);
			if (JSONUtil.hasKey(response, DBPDatasetConstants.DATASET_RECORDS)
					&& response.get(DBPDatasetConstants.DATASET_RECORDS).getAsJsonArray().size() > 0) {
				dto = getNormalizedFeatureActionLimits(isSuperAdmin, locale,
						response.get(DBPDatasetConstants.DATASET_RECORDS).getAsJsonArray(), headersMap);
			}
		} catch (Exception e) {
			logger.error("Exception occured while fetching the restrictive feature action limits" + e.getMessage());
			throw new ApplicationException(ErrorCodeEnum.ERR_10766);
		}
		return dto;
	}

	private FeatureActionLimitsDTO getNormalizedFeatureActionLimits(boolean isSuperAdmin, String locale,
			JsonArray jsonarray, Map<String, Object> headersMap) throws ApplicationException {
		DependentActionsBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(DependentActionsBusinessDelegate.class);
		DBXResult response = businessDelegate.getDependentActions(headersMap);
		Map<String, Set<String>> dependentActions = (Map<String, Set<String>>) response.getResponse();
		Map<String, JsonObject> limitGroupDetails = fetchLimitGroupDescription(locale, headersMap);
		FeatureActionLimitsDTO dto = new FeatureActionLimitsDTO();
		Map<String, Set<String>> featureaction = new HashMap<>();
		Map<String, JsonObject> featureInfo = new HashMap<>();
		Map<String, JsonObject> actionsInfo = new HashMap<>();
		Map<String, Map<String, Map<String, String>>> monetaryActionLimits = new HashMap<>();
		for (JsonElement jsonelement : jsonarray) {
			JsonObject jsonobject = jsonelement.getAsJsonObject();
			if (!isSuperAdmin && !"SID_ACTION_ACTIVE".equalsIgnoreCase(JSONUtil.getString(jsonobject, "featureStatus"))
					&& !"SID_ACTION_ACTIVE".equalsIgnoreCase(JSONUtil.getString(jsonobject, "actionStatus")))
				continue;
			if (!featureInfo.containsKey(JSONUtil.getString(jsonobject, "featureId"))) {
				JsonObject feature = new JsonObject();
				feature.addProperty("featureId", JSONUtil.getString(jsonobject, "featureId"));
				feature.addProperty("featureName", JSONUtil.getString(jsonobject, "featureName"));
				feature.addProperty("featureDescription", JSONUtil.getString(jsonobject, "featureDescription"));
				feature.addProperty("featureStatus", JSONUtil.getString(jsonobject, "featureStatus"));
				featureInfo.put(JSONUtil.getString(jsonobject, "featureId"), feature);
			}
			if (!actionsInfo.containsKey(JSONUtil.getString(jsonobject, "actionId"))) {
				JsonObject action = new JsonObject();
				action.addProperty("actionId", JSONUtil.getString(jsonobject, "actionId"));
				action.addProperty("actionName", JSONUtil.getString(jsonobject, "actionName"));
				action.addProperty("actionDescription", JSONUtil.getString(jsonobject, "actionDescription"));
				action.addProperty("isAccountLevel", JSONUtil.getString(jsonobject, "isAccountLevel"));
				action.addProperty("actionStatus", JSONUtil.getString(jsonobject, "actionStatus"));
				action.addProperty("accessPolicyId", JSONUtil.getString(jsonobject, "accessPolicyId"));
				action.addProperty("actionLevelId", JSONUtil.getString(jsonobject, "actionLevelId"));
				action.addProperty("limitGroupId", JSONUtil.getString(jsonobject, "limitGroupId"));
				action.addProperty("typeId", JSONUtil.getString(jsonobject, "typeId"));
				if (dependentActions.get(JSONUtil.getString(jsonobject, "actionId")) != null)
					action.addProperty("dependentActions",
							dependentActions.get(JSONUtil.getString(jsonobject, "actionId")).toString());
				actionsInfo.put(JSONUtil.getString(jsonobject, "actionId"), action);
				if (StringUtils.isNotBlank(JSONUtil.getString(jsonobject, "limitGroupId"))) {
					for (Entry<String, JsonElement> limitsEntry : limitGroupDetails
							.get(JSONUtil.getString(jsonobject, "limitGroupId")).entrySet()) {
						action.addProperty(limitsEntry.getKey(), limitsEntry.getValue().getAsString());
					}
				}
			}
			Set<String> actionsSet = new HashSet<>();
			if (featureaction.containsKey(JSONUtil.getString(jsonobject, "featureId"))) {
				actionsSet = featureaction.get(JSONUtil.getString(jsonobject, "featureId"));
			}
			actionsSet.add(JSONUtil.getString(jsonobject, "actionId"));
			featureaction.put(JSONUtil.getString(jsonobject, "featureId"), actionsSet);
			if (HelperMethods.FEATUREACTION_TYPE.MONETARY.toString()
					.equalsIgnoreCase(JSONUtil.getString(jsonobject, "typeId"))) {
				Map<String, Map<String, String>> actionLimitsMap = new HashMap<>();
				Map<String, String> limits = new HashMap<>();
				if (monetaryActionLimits.containsKey(JSONUtil.getString(jsonobject, "featureId"))) {
					actionLimitsMap = monetaryActionLimits.get(JSONUtil.getString(jsonobject, "featureId"));
				}
				if (actionLimitsMap.containsKey(JSONUtil.getString(jsonobject, "actionId"))) {
					limits = actionLimitsMap.get(JSONUtil.getString(jsonobject, "actionId"));
				}
				limits.put(JSONUtil.getString(jsonobject, "limitTypeId"), getMostRestrictiveLimitValue(jsonobject));
				actionLimitsMap.put(JSONUtil.getString(jsonobject, "actionId"), limits);
				monetaryActionLimits.put(JSONUtil.getString(jsonobject, "featureId"), actionLimitsMap);
			}
		}
		dto.setMonetaryActionLimits(monetaryActionLimits);
		dto.setActionsInfo(actionsInfo);
		dto.setFeatureInfo(featureInfo);
		dto.setFeatureaction(featureaction);
		return dto;
	}

	private Map<String, JsonObject> fetchLimitGroupDescription(String localeId, Map<String, Object> headersMap) {
		String filter = InfinityConstants.localeId + DBPUtilitiesConstants.EQUAL + localeId;
		Map<String, JsonObject> limitGroupMap = new HashMap<String, JsonObject>();
		Map<String, Object> input = new HashMap<String, Object>();
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, headersMap,
				URLConstants.LIMIT_GROUP_DESCRIPTION_GET);
		if (response.has(DBPDatasetConstants.DATASET_LIMIT_GROUP_DESCRIPTION)) {
			JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_LIMIT_GROUP_DESCRIPTION);
			if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
				for (JsonElement element : jsonElement.getAsJsonArray()) {
					JsonObject limitGroupNameAndDescription = new JsonObject();
					limitGroupNameAndDescription.addProperty("limitGroupName",
							JSONUtil.getString(element.getAsJsonObject(), "displayName"));
					limitGroupNameAndDescription.addProperty("limitGroupDescription",
							JSONUtil.getString(element.getAsJsonObject(), "displayDescription"));
					limitGroupMap.put(JSONUtil.getString(element.getAsJsonObject(), "limitGroupId"),
							limitGroupNameAndDescription);
				}
			}
		}
		return limitGroupMap;
	}

	private String getMostRestrictiveLimitValue(JsonObject jsonObject) {
		String fiLimitValue = JSONUtil.getString(jsonObject, "fiLimitValue");
		String serviceLimitValue = JSONUtil.getString(jsonObject, "serviceLimitValue");
		String roleLimitValue = JSONUtil.getString(jsonObject, "groupLimitValue");
		String coreCustomerLimitValue = JSONUtil.getString(jsonObject, "coreCustomerLimitValue");
		String minimumValue = fiLimitValue;
		if (StringUtils.isNotBlank(serviceLimitValue))
			minimumValue = String
					.valueOf(Math.min(Double.parseDouble(minimumValue), Double.parseDouble(serviceLimitValue)));
		if (StringUtils.isNotBlank(roleLimitValue))
			minimumValue = String
					.valueOf(Math.min(Double.parseDouble(minimumValue), Double.parseDouble(roleLimitValue)));
		if (StringUtils.isNotBlank(coreCustomerLimitValue))
			minimumValue = String
					.valueOf(Math.min(Double.parseDouble(minimumValue), Double.parseDouble(coreCustomerLimitValue)));
		return minimumValue;
	}

}