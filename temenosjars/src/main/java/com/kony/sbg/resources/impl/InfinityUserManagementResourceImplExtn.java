package com.kony.sbg.resources.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractBackendDelegate;
import com.temenos.dbx.eum.product.contract.resource.api.ContractResource;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.api.InfinityUserManagementBusinessDelegate;
import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.eum.product.usermanagement.resource.impl.InfinityUserManagementResourceImpl;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.FeatureActionLimitsDTO;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;
import com.temenos.dbx.product.utils.InfinityConstants;
public class InfinityUserManagementResourceImplExtn extends InfinityUserManagementResourceImpl{
	LoggerUtil logger = new LoggerUtil(InfinityUserManagementResourceImplExtn.class);
	@Override
	public Object editInfinityUser(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		JsonObject jsonObject = new JsonObject();

		Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

		Iterator<String> iterator = request.getParameterNames();

		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				map.put(key, request.getParameter(key));
			}
		}

		if (!map.containsKey(InfinityConstants.userDetails)
				|| StringUtils.isBlank(map.get(InfinityConstants.userDetails))) {
			ErrorCodeEnum.ERR_10056.setErrorCode(result);
			return false;
		}

		String userDetails = map.get(InfinityConstants.userDetails);
		JsonElement userDetailsElement = new JsonParser().parse(userDetails);
		if (userDetailsElement.isJsonNull() || !userDetailsElement.isJsonObject()) {
			ErrorCodeEnum.ERR_10056.setErrorCode(result);
			return false;
		}

		Boolean isContractValidationRequired = true;

		Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(request);
		if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
			Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
			if (!userPermissions.contains("USER_MANAGEMENT")) {
				ErrorCodeEnum.ERR_10051.setErrorCode(result);
				return result;
			}
		} else {
			isContractValidationRequired = false;
		}

		JsonObject userDetailsJsonObject = userDetailsElement.getAsJsonObject();

		jsonObject.add(InfinityConstants.userDetails, userDetailsJsonObject);

		String signatoryGroups = map.get(InfinityConstants.signatoryGroups);
		jsonObject.addProperty(InfinityConstants.signatoryGroups, signatoryGroups);

		String id = userDetailsJsonObject.has(InfinityConstants.id)
				&& !userDetailsJsonObject.get(InfinityConstants.id).isJsonNull()
						? userDetailsJsonObject.get(InfinityConstants.id).getAsString()
						: null;

		logger.debug("Json request " + jsonObject.toString());
		if (validateinput(jsonObject, result, map, request, isContractValidationRequired, id)) {

			InfinityUserManagementBusinessDelegate infinityUserManagementBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(InfinityUserManagementBusinessDelegate.class);
			DBXResult dbxResult = infinityUserManagementBusinessDelegate.editInfinityUser(jsonObject,
					request.getHeaderMap());
			if (dbxResult.getResponse() != null) {
				JsonObject jsonResultObject = (JsonObject) dbxResult.getResponse();
				result = JSONToResult.convert(jsonResultObject.toString());
				updateSignatoryGroupEntry(id, jsonObject, result, request);
				//Audit logs code				
				SBGCommonUtils._OlbAuditLogsClass(request,response,result,"USER","USER_UPDATE","ExternalUserManagement/operations/ExternalUsers_1/editUser","SUCCESS");
				logger.debug("Json response " + ResultToJSON.convert(result).toString());
			}
		}

		return result;
	}
	private boolean validateinput(JsonObject jsonObject, Result result, Map<String, String> map,
			DataControllerRequest dcRequest, Boolean isContractValidationRequired, String id) {

		Map<String, Set<String>> customerAccountsMap = new HashMap<String, Set<String>>();

		String customerId = null;

		if (isContractValidationRequired) {
			customerId = HelperMethods.getCustomerIdFromSession(dcRequest);
		}

		String removedCompanies = map.get(InfinityConstants.removedCompanies);
		if (StringUtils.isNotBlank(removedCompanies)) {
			JsonElement removedCompaniesElement = new JsonParser().parse(removedCompanies);
			if (!removedCompaniesElement.isJsonNull() && removedCompaniesElement.isJsonArray()) {
				jsonObject.add(InfinityConstants.removedCompanies, removedCompaniesElement.getAsJsonArray());
			}
		}

		if (!map.containsKey(InfinityConstants.companyList)
				|| StringUtils.isBlank(map.get(InfinityConstants.companyList))) {
			return true;
		}

		String companyList = map.get(InfinityConstants.companyList);
		JsonElement companyListElement = new JsonParser().parse(companyList);
		if (companyListElement.isJsonNull() || !companyListElement.isJsonArray()) {
			ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid CompanyList");
			return false;
		}

		Map<String, Set<String>> customerContracts = new HashMap<String, Set<String>>();
		Map<String, Set<String>> contractCIFs = new HashMap<String, Set<String>>();
		Map<String, Map<String, Set<String>>> customerAccounts = new HashMap<String, Map<String, Set<String>>>();
		Map<String, Map<String, Set<String>>> contractAccounts = new HashMap<String, Map<String, Set<String>>>();
		Map<String, Set<String>> loggedInUserPermisions = new HashMap<String, Set<String>>();
		Map<String, Map<String, Map<String, Map<String, Double>>>> loggedInUserLimits = new HashMap<String, Map<String, Map<String, Map<String, Double>>>>();
		if (isContractValidationRequired) {
			getLoggedInUserContracts(customerId, customerContracts, dcRequest.getHeaderMap());
			getAccountsForCustomer(customerId, customerAccounts, dcRequest.getHeaderMap());
			getLoggedInUserPermissions(customerId, loggedInUserPermisions, loggedInUserLimits, dcRequest);
		}

		Map<String, String> serviceDefinitions = new HashMap<String, String>();

		ContractBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(ContractBackendDelegate.class);

		Map<String, FeatureActionLimitsDTO> featureActionsLimitsDTOs = new HashMap<String, FeatureActionLimitsDTO>();
		JsonArray excludedCompaniesArray = new JsonArray();
		JsonArray companiesArray = companyListElement.getAsJsonArray();
		for (int i = 0; i < companiesArray.size(); i++) {
			JsonObject companyJsonObject = companiesArray.get(i).getAsJsonObject();
			String contractId = null;
			String cif = null;
			boolean isPrimary;
			String serviceDefinition = null;
			String userRole = null;
			boolean autoSyncAccounts = false;
			if (companyJsonObject.has(InfinityConstants.contractId)
					&& !companyJsonObject.get(InfinityConstants.contractId).isJsonNull()) {
				contractId = companyJsonObject.get(InfinityConstants.contractId).getAsString();
			}

			if (companyJsonObject.has(InfinityConstants.cif)
					&& !companyJsonObject.get(InfinityConstants.cif).isJsonNull()) {
				cif = companyJsonObject.get(InfinityConstants.cif).getAsString();
			}

			if (companyJsonObject.has(InfinityConstants.isPrimary)
					&& !companyJsonObject.get(InfinityConstants.isPrimary).isJsonNull()) {
				isPrimary = Boolean.parseBoolean(companyJsonObject.get(InfinityConstants.isPrimary).getAsString());
			}

			if (companyJsonObject.has(InfinityConstants.serviceDefinition)
					&& !companyJsonObject.get(InfinityConstants.serviceDefinition).isJsonNull()) {
				serviceDefinition = companyJsonObject.get(InfinityConstants.serviceDefinition).getAsString();
			}

			serviceDefinitions.put(contractId, serviceDefinition);

			if (companyJsonObject.has(InfinityConstants.roleId)
					&& !companyJsonObject.get(InfinityConstants.roleId).isJsonNull()) {
				userRole = companyJsonObject.get(InfinityConstants.roleId).getAsString();
			}

			if (companyJsonObject.has(InfinityConstants.autoSyncAccounts)
					&& !companyJsonObject.get(InfinityConstants.autoSyncAccounts).isJsonNull()) {
				autoSyncAccounts = Boolean
						.parseBoolean(companyJsonObject.get(InfinityConstants.autoSyncAccounts).getAsString());
			}

			companyJsonObject.addProperty(InfinityConstants.autoSyncAccounts, autoSyncAccounts + "");

			// checking blanks for mandatory params
			if (HelperMethods.isBlank(contractId, cif, serviceDefinition, userRole)) {
				ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid company details");
				return false;
			}

			if (isContractValidationRequired) {
				// Contract and CIF validation for Logged in user
				if (isContractValidationRequired && (!customerContracts.containsKey(contractId)
						|| !customerContracts.get(contractId).contains(cif))) {
					companiesArray.remove(i);
					i--;
					continue;
				}
			}

			getContractCIFs(contractId, contractCIFs, dcRequest.getHeaderMap());

			// Contract and CIF validation
			if (!contractCIFs.containsKey(contractId) || !contractCIFs.get(contractId).contains(cif)) {
				companiesArray.remove(i);
				i--;
				continue;
			}

			JsonElement accountsEelement = companyJsonObject.get(InfinityConstants.accounts);

			if (accountsEelement.isJsonNull() || !accountsEelement.isJsonArray()) {
				ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid accounts");
				return false;
			}

			getAccountsForContract(contractId, contractAccounts, dcRequest.getHeaderMap());

			JsonArray accountsArray = accountsEelement.getAsJsonArray();
			Set<String> accountList = new HashSet<String>();

			Boolean isUserLevelAccountAccessDelegationEnabled = true;
			// get configuration for customer 360 or DB

			try {
				FeatureActionLimitsDTO coreCustomerFeatureActionDTO = backendDelegate.getRestrictiveFeatureActionLimits(
						serviceDefinition, contractId, userRole, cif, "", dcRequest.getHeaderMap(), true, "");
				featureActionsLimitsDTOs.put(cif, coreCustomerFeatureActionDTO);
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JsonArray excludedAccountsArray = companyJsonObject.has(InfinityConstants.excludedAccounts)
					&& companyJsonObject.get(InfinityConstants.excludedAccounts).isJsonArray()
							? companyJsonObject.get(InfinityConstants.excludedAccounts).getAsJsonArray()
							: new JsonArray();
			for (int j = 0; j < accountsArray.size(); j++) {
				JsonObject accountJsonObject = accountsArray.get(j).getAsJsonObject();
				String accountID = null;
				boolean isEnabled = true;
				if (accountJsonObject.has(InfinityConstants.accountId)
						&& !accountJsonObject.get(InfinityConstants.accountId).isJsonNull()) {
					accountID = accountJsonObject.get(InfinityConstants.accountId).getAsString();
				}

				if (accountJsonObject.has(InfinityConstants.isEnabled)
						&& !accountJsonObject.get(InfinityConstants.isEnabled).isJsonNull()) {
					isEnabled = Boolean.parseBoolean(accountJsonObject.get(InfinityConstants.isEnabled).getAsString());
				}

				// Account Validation

				// accounts belong to the contract and CIF
				if (!contractAccounts.containsKey(contractId) || !contractAccounts.get(contractId).containsKey(cif)
						|| !contractAccounts.get(contractId).get(cif).contains(accountID)) {
					accountsArray.remove(j);
					j--;
					continue;
				}

				// account delegation is at user level then accounts must be accessible by
				// logged in user
				if (isContractValidationRequired && isUserLevelAccountAccessDelegationEnabled
						&& (!customerAccounts.containsKey(contractId)
								|| !customerAccounts.get(contractId).containsKey(cif)
								|| !customerAccounts.get(contractId).get(cif).contains(accountID))) {
					accountsArray.remove(j);
					j--;
					continue;
				}

				if (isEnabled) {
					accountList.add(accountID);
				} else {
					excludedAccountsArray.add(accountsArray.get(j));
					accountsArray.remove(j);
					j--;
					continue;
				}
			}
			if (accountList.size() <= 0) {
				ErrorCodeEnum.ERR_10050.setErrorCode(result, "At least one account should be present");
				return false;
			}
			customerAccountsMap.put(cif, accountList);

			companyJsonObject.add(InfinityConstants.excludedAccounts, excludedAccountsArray);

		}

		jsonObject.add(InfinityConstants.companyList, companiesArray);

		boolean isAccountLevelAllowed = true;
		// isAccountLevelAllowed = AdminUtil.isAccountlevelPermissionsAllowed(request);

		if (!isAccountLevelAllowed) {
			map.remove(InfinityConstants.accountLevelPermissions);
		} else {
			String accountLevelPermissions = map.get(InfinityConstants.accountLevelPermissions);
			JsonElement accountLevelPermissionsElement = new JsonObject();
			try {
				accountLevelPermissionsElement = new JsonParser().parse(accountLevelPermissions);
			} catch (Exception e) {
			}
			if (!accountLevelPermissionsElement.isJsonNull() && accountLevelPermissionsElement.isJsonArray()) {

				JsonArray accountLevelPermissionsArray = accountLevelPermissionsElement.getAsJsonArray();

				for (int i = 0; i < accountLevelPermissionsArray.size(); i++) {

					JsonObject accountLevelPermissionsJsonObject = accountLevelPermissionsArray.get(i)
							.getAsJsonObject();

					String cif = accountLevelPermissionsJsonObject.get(InfinityConstants.cif).getAsString();

					JsonElement accountsElement = accountLevelPermissionsJsonObject.get(InfinityConstants.accounts);

					FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);

					if (!featureActionsLimitsDTOs.containsKey(cif)
							|| (isContractValidationRequired && !loggedInUserPermisions.containsKey(cif))) {
						accountLevelPermissionsArray.remove(i);
						i--;
						continue;
					}

					Map<String, Set<String>> accoutLevelActions = featureActionLimitsDTO.getFeatureaction();
					Map<String, Map<String, Map<String, String>>> monitoryActions = featureActionLimitsDTO
							.getMonetaryActionLimits();

					if (!accountsElement.isJsonNull() && accountsElement.isJsonArray()) {

						JsonArray accounts = accountsElement.getAsJsonArray();

						for (int j = 0; j < accounts.size(); j++) {

							JsonObject accountJsonObject = accounts.get(j).getAsJsonObject();
							String accountId = accountJsonObject.get(InfinityConstants.accountId).getAsString();

							if (!customerAccountsMap.containsKey(cif)
									|| !customerAccountsMap.get(cif).contains(accountId)) {
								accounts.remove(j);
								j--;
								continue;
							}

							JsonArray featurePermissions = accountJsonObject.get(InfinityConstants.featurePermissions)
									.getAsJsonArray();

							for (int k = 0; k < featurePermissions.size(); k++) {

								JsonObject featurePermissionsJsonObject = featurePermissions.get(k).getAsJsonObject();
								String featureId = featurePermissionsJsonObject.get(InfinityConstants.featureId)
										.getAsString();

								if (!accoutLevelActions.containsKey(featureId)
										&& !monitoryActions.containsKey(featureId)) {
									featurePermissions.remove(k);
									k--;
									continue;
								}

								JsonArray permissions = featurePermissionsJsonObject.get(InfinityConstants.permissions)
										.getAsJsonArray();

								for (int l = 0; l < permissions.size(); l++) {

									JsonObject permissonsJsonObject = permissions.get(l).getAsJsonObject();

									String actionId = null;
									if (permissonsJsonObject.has(InfinityConstants.id)) {
										actionId = permissonsJsonObject.get(InfinityConstants.id).getAsString();
									}

									if (StringUtils.isBlank(actionId)
											&& permissonsJsonObject.has(InfinityConstants.actionId)) {
										actionId = permissonsJsonObject.get(InfinityConstants.actionId).getAsString();
									}

									Boolean isEnabled = Boolean.parseBoolean(
											permissonsJsonObject.get(InfinityConstants.isEnabled).getAsString());

									Boolean isAccountLevel = "1".equals(
											JSONUtil.getString(featureActionLimitsDTO.getActionsInfo().get(actionId),
													InfinityConstants.isAccountLevel));

									if (((accoutLevelActions.get(featureId) == null
											|| !accoutLevelActions.get(featureId).contains(actionId))
											&& (monitoryActions.get(featureId) == null
													|| !monitoryActions.get(featureId).containsKey(actionId))
											&& !isAccountLevel) || !isEnabled
											|| (isContractValidationRequired
													&& !loggedInUserPermisions.get(cif).contains(actionId))) {
										permissions.remove(l);
										l--;
										continue;
									}
								}
							}
						}
					}
				}
				jsonObject.add(InfinityConstants.accountLevelPermissions, accountLevelPermissionsArray);
			}
		}

		String globalLevelPermissions = map.get(InfinityConstants.globalLevelPermissions);
		JsonElement globalLevelPermissionsElement = new JsonObject();
		try {
			globalLevelPermissionsElement = new JsonParser().parse(globalLevelPermissions);
		} catch (Exception e) {
		}

		if (!globalLevelPermissionsElement.isJsonNull() && globalLevelPermissionsElement.isJsonArray()) {

			JsonArray globalLevelPermissionsArray = globalLevelPermissionsElement.getAsJsonArray();
			for (int i = 0; i < globalLevelPermissionsArray.size(); i++) {
				JsonObject globalLevelPermissionJsonObject = globalLevelPermissionsArray.get(i).getAsJsonObject();

				String cif = globalLevelPermissionJsonObject.get(InfinityConstants.cif).getAsString();

				JsonElement featuresElement = globalLevelPermissionJsonObject.get(InfinityConstants.features);

				FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);

				if (!featureActionsLimitsDTOs.containsKey(cif)
						|| (isContractValidationRequired && !loggedInUserPermisions.containsKey(cif))) {
					globalLevelPermissionsArray.remove(i);
					i--;
					continue;
				}

				Map<String, Set<String>> globalLevelActions = featureActionLimitsDTO.getFeatureaction();

				if (!featuresElement.isJsonNull() && featuresElement.isJsonArray()) {

					JsonArray features = featuresElement.getAsJsonArray();

					for (int j = 0; j < features.size(); j++) {

						JsonObject featureJsonObject = features.get(j).getAsJsonObject();

						String featureId = featureJsonObject.get(InfinityConstants.featureId).getAsString();
						if (!globalLevelActions.containsKey(featureId)) {
							features.remove(j);
							j--;
							continue;
						}
						JsonArray permissions = featureJsonObject.get(InfinityConstants.permissions).getAsJsonArray();
						for (int k = 0; k < permissions.size(); k++) {
							JsonObject permissionJsonObject = permissions.get(k).getAsJsonObject();

							String permissionId = "";

							if (permissionJsonObject.has(InfinityConstants.id)) {
								permissionId = permissionJsonObject.get(InfinityConstants.id).getAsString();
							}

							if (StringUtils.isBlank(permissionId)
									&& permissionJsonObject.has(InfinityConstants.actionId)) {
								permissionId = permissionJsonObject.get(InfinityConstants.actionId).getAsString();
							}

							if (StringUtils.isBlank(permissionId)
									&& permissionJsonObject.has(InfinityConstants.permissionType)) {
								permissionId = permissionJsonObject.get(InfinityConstants.permissionType).getAsString();
							}

							Boolean isEnabled = Boolean
									.parseBoolean(permissionJsonObject.get(InfinityConstants.isEnabled).getAsString());
							Set<String> permissionsSet = globalLevelActions.get(featureId);
							if (!permissionsSet.contains(permissionId) || !isEnabled || (isContractValidationRequired
									&& !loggedInUserPermisions.get(cif).contains(permissionId))) {
								permissions.remove(k);
								k--;
								continue;
							}
						}
					}
				}
			}

			jsonObject.add(InfinityConstants.globalLevelPermissions, globalLevelPermissionsArray);
		}

		String transactionLimits = map.get(InfinityConstants.transactionLimits);
		JsonElement transactionLimitsElement = new JsonObject();
		try {
			transactionLimitsElement = new JsonParser().parse(transactionLimits);
		} catch (Exception e) {
		}

		if (!transactionLimitsElement.isJsonNull() && transactionLimitsElement.isJsonArray()) {

			JsonArray transactionLimitsArray = transactionLimitsElement.getAsJsonArray();
			for (int i = 0; i < transactionLimitsArray.size(); i++) {
				JsonObject transactionLimitsJsonObject = transactionLimitsArray.get(i).getAsJsonObject();

				String cif = transactionLimitsJsonObject.get(InfinityConstants.cif).getAsString();

				FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);

				if (!featureActionsLimitsDTOs.containsKey(cif)
						|| (isContractValidationRequired && !loggedInUserLimits.containsKey(cif))) {
					transactionLimitsArray.remove(i);
					i--;
					continue;
				}

				Map<String, Map<String, Map<String, String>>> transactionLimitsMap = featureActionLimitsDTO
						.getMonetaryActionLimits();

				JsonElement accountsElement = transactionLimitsJsonObject.get(InfinityConstants.accounts);

				if (!accountsElement.isJsonNull() && accountsElement.isJsonArray()) {

					JsonArray accounts = accountsElement.getAsJsonArray();

					for (int j = 0; j < accounts.size(); j++) {

						JsonObject accountJsonObject = accounts.get(j).getAsJsonObject();

						String accountId = accountJsonObject.get(InfinityConstants.accountId).getAsString();

						if (!customerAccountsMap.containsKey(cif) || !customerAccountsMap.get(cif).contains(accountId)
								|| (isContractValidationRequired
										&& !loggedInUserLimits.get(cif).containsKey(accountId))) {
							accounts.remove(j);
							j--;
							continue;
						}

						JsonArray featurePermissions = accountJsonObject.get(InfinityConstants.featurePermissions)
								.getAsJsonArray();

						for (int k = 0; k < featurePermissions.size(); k++) {
							JsonObject featurePermissionJsonObject = featurePermissions.get(k).getAsJsonObject();

							String feaureId = featurePermissionJsonObject.get(InfinityConstants.featureId)
									.getAsString();

							String actionId = featurePermissionJsonObject.get(InfinityConstants.actionId).getAsString();

							if (!transactionLimitsMap.containsKey(feaureId)
									|| !transactionLimitsMap.get(feaureId).containsKey(actionId)
									|| (isContractValidationRequired
											&& !loggedInUserLimits.get(cif).get(accountId).containsKey(actionId))) {
								featurePermissions.remove(k);
								k--;
								continue;
							}

							featurePermissionJsonObject.add(InfinityConstants.limitGroupId, featureActionLimitsDTO
									.getActionsInfo().get(actionId).get(InfinityConstants.limitGroupId));

							Map<String, String> limitMap = transactionLimitsMap.get(feaureId).get(actionId);

							JsonArray limits = featurePermissionJsonObject.get(InfinityConstants.limits)
									.getAsJsonArray();

							for (int l = 0; l < limits.size(); l++) {

								JsonObject limit = limits.get(l).getAsJsonObject();

								Double limit1 = new Double(0);
								Double limit2 = new Double(0);

								limit1 = Double.parseDouble(limit.get(InfinityConstants.value).getAsString());
								String limitId = limit.get(InfinityConstants.id).getAsString();

								Double limit3 = null;
								if (isContractValidationRequired) {
									limit3 = loggedInUserLimits.get(cif).get(accountId).get(actionId).get(limitId);
								}
								if (limitId.equals(InfinityConstants.PRE_APPROVED_DAILY_LIMIT)
										|| limitId.equals(InfinityConstants.AUTO_DENIED_DAILY_LIMIT)
										|| limitId.equals(InfinityConstants.DAILY_LIMIT)) {
									limit2 = Double.parseDouble((limitMap.containsKey(InfinityConstants.DAILY_LIMIT)
											&& StringUtils.isNotBlank(limitMap.get(InfinityConstants.DAILY_LIMIT)))
													? limitMap.get(InfinityConstants.DAILY_LIMIT)
													: "0.0");
								} else if (limitId.equals(InfinityConstants.PRE_APPROVED_WEEKLY_LIMIT)
										|| limitId.equals(InfinityConstants.AUTO_DENIED_WEEKLY_LIMIT)
										|| limitId.equals(InfinityConstants.WEEKLY_LIMIT)) {
									limit2 = Double.parseDouble((limitMap.containsKey(InfinityConstants.WEEKLY_LIMIT)
											&& StringUtils.isNotBlank(limitMap.get(InfinityConstants.WEEKLY_LIMIT)))
													? limitMap.get(InfinityConstants.WEEKLY_LIMIT)
													: "0.0");
								} else if (limitId.equals(InfinityConstants.PRE_APPROVED_TRANSACTION_LIMIT)
										|| limitId.equals(InfinityConstants.AUTO_DENIED_TRANSACTION_LIMIT)
										|| limitId.equals(InfinityConstants.MAX_TRANSACTION_LIMIT)) {
									limit2 = Double
											.parseDouble((limitMap.containsKey(InfinityConstants.MAX_TRANSACTION_LIMIT)
													&& StringUtils.isNotBlank(
															limitMap.get(InfinityConstants.MAX_TRANSACTION_LIMIT)))
																	? limitMap.get(
																			InfinityConstants.MAX_TRANSACTION_LIMIT)
																	: "0.0");
								}

								if (limit1 < limit2) {
									limit.addProperty(InfinityConstants.value,
											limit3 != null ? Math.min(limit1, limit3) + "" : limit1.toString());
								} else {
									limit.addProperty(InfinityConstants.value,
											limit3 != null ? Math.min(limit2, limit3) + "" : limit2.toString());
								}
							}
						}
					}
				}
			}

			jsonObject.add(InfinityConstants.transactionLimits, transactionLimitsArray);
		}

		return true;
	}
	private void updateSignatoryGroupEntry(String customerId, JsonObject jsonObject, Result result,
			DataControllerRequest request) {

		JsonArray signatoryGroups = new JsonArray();
		if (jsonObject.has(InfinityConstants.signatoryGroups)
				&& !jsonObject.get(InfinityConstants.signatoryGroups).isJsonNull()) {

			if (jsonObject.get(InfinityConstants.signatoryGroups).isJsonArray()) {
				signatoryGroups = jsonObject.get(InfinityConstants.signatoryGroups).getAsJsonArray();
			} else {
				try {
					signatoryGroups = new JsonParser()
							.parse(jsonObject.get(InfinityConstants.signatoryGroups).getAsString()).getAsJsonArray();
				} catch (Exception e) {
				}
			}
		}

		if (jsonObject.has(InfinityConstants.signatoryGroups) && signatoryGroups.size() == 0) {
			try {
				String src = "";
				if (jsonObject.has(InfinityConstants.signatoryGroups))
					src = jsonObject.get(InfinityConstants.signatoryGroups).getAsString();
				signatoryGroups = new JsonParser().parse(src).getAsJsonArray();
			} catch (Exception e) {
				signatoryGroups = new JsonArray();
			}
		}

		JSONArray signatoryGroups1 = new JSONArray();

		for (JsonElement element : signatoryGroups) {
			JsonObject signatory = element.getAsJsonObject();
			JSONObject signatory1 = new JSONObject();
			String contractId = signatory.get(InfinityConstants.contractId).getAsString();
			String cif = signatory.get(InfinityConstants.cif).getAsString();
			signatory1.put(InfinityConstants.coreCustomerId, cif);
			signatory1.put(InfinityConstants.cif, cif);
			signatory1.put(InfinityConstants.contractId, contractId);
			signatory1.put(InfinityConstants.customerId, customerId);
			JsonArray groups = signatory.get(InfinityConstants.groups).getAsJsonArray();
			String signatoryGroupId = "";
			if (groups.size() > 0) {
				for (JsonElement group : groups) {

					if (group.getAsJsonObject().has(InfinityConstants.signatoryGroupId)
							&& !group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).isJsonNull()
							&& StringUtils.isNotBlank(
									group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).getAsString())
							&& group.getAsJsonObject().has(InfinityConstants.isAssociated)
							&& !group.getAsJsonObject().get(InfinityConstants.isAssociated).isJsonNull()
							&& Boolean.parseBoolean(
									group.getAsJsonObject().get(InfinityConstants.isAssociated).getAsString())) {

						signatoryGroupId = group.getAsJsonObject().get(InfinityConstants.signatoryGroupId)
								.getAsString();
						signatory1.put(InfinityConstants.signatoryGroupId, signatoryGroupId);
						signatoryGroups1.put(signatory1);

					}
				}
			} else {
				signatory1.put(InfinityConstants.signatoryGroupId, signatoryGroupId);
				signatoryGroups1.put(signatory1);
			}

		}

		if (signatoryGroups1.length() > 0) {
			JSONObject signatory = new JSONObject();
			signatory.put(InfinityConstants.signatoryGroups, signatoryGroups1);
			DBPAPIAbstractFactoryImpl.getResource(SignatoryGroupResource.class)
					.updateSignatoryGroupForInfinityUser(signatory);
			// result.addAllDatasets(result2.getAllDatasets());
			// result.addAllParams(result2.getAllParams());
			// result.addAllRecords(result2.getAllRecords());
		}
	}

	private void getLoggedInUserContracts(String customerId, Map<String, Set<String>> customerContracts,
			Map<String, Object> headerMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(customerId)) {
			String filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL + customerId;
			map.put(DBPUtilitiesConstants.FILTER, filter);
			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap,
					URLConstants.CONTRACT_CUSTOMERS_GET);

			if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)) {
				JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject customerContract = jsonArray.get(i).getAsJsonObject();
						String contractId = customerContract.has(InfinityConstants.contractId)
								&& !customerContract.get(InfinityConstants.contractId).isJsonNull()
										? customerContract.get(InfinityConstants.contractId).getAsString()
										: null;

						String coreCustomerId = customerContract.has(InfinityConstants.coreCustomerId)
								&& !customerContract.get(InfinityConstants.coreCustomerId).isJsonNull()
										? customerContract.get(InfinityConstants.coreCustomerId).getAsString()
										: null;

						if (!customerContracts.containsKey(contractId)) {
							customerContracts.put(contractId, new HashSet<String>());
						}
						if (customerContracts.containsKey(contractId)
								&& !customerContracts.get(contractId).contains(coreCustomerId)) {
							customerContracts.get(contractId).add(coreCustomerId);
						}
					}
				}
			}
		}
	}
	private void getAccountsForCustomer(String customerId, Map<String, Map<String, Set<String>>> customerAccounts,
			Map<String, Object> headerMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(customerId)) {
			String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerId;
			map.put(DBPUtilitiesConstants.FILTER, filter);

			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap,
					URLConstants.CUSTOMERACCOUNTS_GET);

			if (jsonObject.has(DBPDatasetConstants.CUSTOMER_ACCOUNTS_DATASET)) {
				JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.CUSTOMER_ACCOUNTS_DATASET);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject account = jsonArray.get(i).getAsJsonObject();
						String contractId = account.has(InfinityConstants.contractId)
								&& !account.get(InfinityConstants.contractId).isJsonNull()
										? account.get(InfinityConstants.contractId).getAsString()
										: null;
						String coreCustomerId = account.has(InfinityConstants.coreCustomerId)
								&& !account.get(InfinityConstants.coreCustomerId).isJsonNull()
										? account.get(InfinityConstants.coreCustomerId).getAsString()
										: null;

						if (!customerAccounts.containsKey(contractId)) {
							customerAccounts.put(contractId, new HashMap<String, Set<String>>());
						}

						if (!customerAccounts.get(contractId).containsKey(coreCustomerId)) {
							customerAccounts.get(contractId).put(coreCustomerId, new HashSet<String>());
						}
						customerAccounts.get(contractId).get(coreCustomerId)
								.add(account.get("Account_id").getAsString());
					}
				}
			}
		}

	}

	private void getAccountsForContract(String contractId, Map<String, Map<String, Set<String>>> contractAccounts,
			Map<String, Object> headerMap) {

		if (!contractAccounts.containsKey(contractId)) {
			Map<String, Object> map = new HashMap<String, Object>();
			String filter = InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL + contractId;

			map.put(DBPUtilitiesConstants.FILTER, filter);

			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap,
					URLConstants.CONTRACTACCOUNT_GET);

			if (jsonObject.has(DBPDatasetConstants.DATASET_CONTRACTACCOUNT)) {
				JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACTACCOUNT);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject account = jsonArray.get(i).getAsJsonObject();

						String coreCustomerId = account.has(InfinityConstants.coreCustomerId)
								&& !account.get(InfinityConstants.coreCustomerId).isJsonNull()
										? account.get(InfinityConstants.coreCustomerId).getAsString()
										: null;

						if (!contractAccounts.containsKey(contractId)) {
							contractAccounts.put(contractId, new HashMap<String, Set<String>>());
						}

						if (!contractAccounts.get(contractId).containsKey(coreCustomerId)) {
							contractAccounts.get(contractId).put(coreCustomerId, new HashSet<String>());
						}
						contractAccounts.get(contractId).get(coreCustomerId)
								.add(account.get(InfinityConstants.accountId).getAsString());
					}
				}
			}
		}
	}
	private void getLoggedInUserPermissions(String customerId, Map<String, Set<String>> loggedInUserPermisions,
			Map<String, Map<String, Map<String, Map<String, Double>>>> loggedInUserLimits,
			DataControllerRequest dcRequest) {

		String filter = InfinityConstants.Customer_id + DBPUtilitiesConstants.EQUAL + customerId;
		Map<String, Object> input = new HashMap<String, Object>();
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, dcRequest.getHeaderMap(),
				URLConstants.CUSTOMER_ACTION_LIMITS_GET);
		if (response.has(DBPDatasetConstants.DATASET_CUSTOMERACTION)) {
			JsonElement jsonElement = response.get(DBPDatasetConstants.DATASET_CUSTOMERACTION);
			if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
				JsonArray actionsArray = jsonElement.getAsJsonArray();
				for (JsonElement element : actionsArray) {
					String coreCustomerId = element.getAsJsonObject().get(InfinityConstants.coreCustomerId)
							.getAsString();
					if (!loggedInUserPermisions.containsKey(coreCustomerId)) {
						loggedInUserPermisions.put(coreCustomerId, new HashSet<String>());
						loggedInUserLimits.put(coreCustomerId, new HashMap<String, Map<String, Map<String, Double>>>());
					}

					String account = element.getAsJsonObject().has(InfinityConstants.Account_id)
							&& !element.getAsJsonObject().get(InfinityConstants.Account_id).isJsonNull()
									? element.getAsJsonObject().get(InfinityConstants.Account_id).getAsString()
									: "";

					if (StringUtils.isNotBlank(account)
							&& !loggedInUserLimits.get(coreCustomerId).containsKey(account)) {
						loggedInUserLimits.get(coreCustomerId).put(account, new HashMap<String, Map<String, Double>>());
					}

					String actionId = element.getAsJsonObject().has(InfinityConstants.Action_id)
							&& !element.getAsJsonObject().get(InfinityConstants.Action_id).isJsonNull()
									? element.getAsJsonObject().get(InfinityConstants.Action_id).getAsString()
									: "";
					loggedInUserPermisions.get(coreCustomerId).add(actionId);

					if (StringUtils.isNotBlank(account)
							&& !loggedInUserLimits.get(coreCustomerId).get(account).containsKey(actionId)) {
						loggedInUserLimits.get(coreCustomerId).get(account).put(actionId,
								new HashMap<String, Double>());
					}
					String limitType = element.getAsJsonObject().has(InfinityConstants.LimitType_id)
							&& !element.getAsJsonObject().get(InfinityConstants.LimitType_id).isJsonNull()
									? element.getAsJsonObject().get(InfinityConstants.LimitType_id).getAsString()
									: "";

					Double value = null;
					try {
						value = element.getAsJsonObject().has(InfinityConstants.value)
								&& !element.getAsJsonObject().get(InfinityConstants.value).isJsonNull()
										? Double.parseDouble(
												element.getAsJsonObject().get(InfinityConstants.value).getAsString())
										: 0.0;
					} catch (Exception e) {
					}

					if (StringUtils.isNotBlank(limitType) && !loggedInUserLimits.get(coreCustomerId).get(account)
							.get(actionId).containsKey(limitType)) {
						loggedInUserLimits.get(coreCustomerId).get(account).get(actionId).put(limitType, value);
					}
				}
			}
		}
	}
	private void getContractCIFs(String contractId, Map<String, Set<String>> contractCIFs,
			Map<String, Object> headerMap) {

		if (!contractCIFs.containsKey(contractId)) {
			String filter = InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL + contractId;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(DBPUtilitiesConstants.FILTER, filter);
			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap,
					URLConstants.CONTRACTCORECUSTOMER_GET);
			if (jsonObject.has(DBPDatasetConstants.CONTRACT_CORE_CUSTOMERS)) {
				JsonElement jsonElement = jsonObject.get(DBPDatasetConstants.CONTRACT_CORE_CUSTOMERS);
				if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject contractCoreCustomer = jsonArray.get(i).getAsJsonObject();
						contractId = contractCoreCustomer.has(InfinityConstants.contractId)
								&& !contractCoreCustomer.get(InfinityConstants.contractId).isJsonNull()
										? contractCoreCustomer.get(InfinityConstants.contractId).getAsString()
										: null;
						String coreCustomerId = contractCoreCustomer.has(InfinityConstants.coreCustomerId)
								&& !contractCoreCustomer.get(InfinityConstants.coreCustomerId).isJsonNull()
										? contractCoreCustomer.get(InfinityConstants.coreCustomerId).getAsString()
										: null;
						if (!contractCIFs.containsKey(contractId)) {
							contractCIFs.put(contractId, new HashSet<String>());
						}
						contractCIFs.get(contractId).add(coreCustomerId);
					}
				}
			}
		}
	}
	@Override
	public Result editCustomRole(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		try {
			// code to check create custom role permission
			List<String> featureActionIdList = new ArrayList<>();
			featureActionIdList.add("CUSTOM_ROLES_CREATE");
			String featureActionId = CustomerSession.getPermittedActionIds(request, featureActionIdList);
			if (featureActionId == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}

			JsonObject jsonObject = new JsonObject();

			Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

			Iterator<String> iterator = request.getParameterNames();

			while (iterator.hasNext()) {
				String key = iterator.next();
				if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
						&& StringUtils.isNotBlank(request.getParameter(key))) {
					map.put(key, request.getParameter(key));
				}
			}

			Boolean isContractValidationRequired = true;

			if (!map.containsKey(InfinityConstants.customRoleDetails)
					|| StringUtils.isBlank(map.get(InfinityConstants.customRoleDetails))) {
				ErrorCodeEnum.ERR_10056.setErrorCode(result);
				return result;
			}

			String customRoleDetails = map.get(InfinityConstants.customRoleDetails);
			JsonElement customRoleDetailsElement = new JsonParser().parse(customRoleDetails);
			if (customRoleDetailsElement.isJsonNull() || !customRoleDetailsElement.isJsonObject()) {
				ErrorCodeEnum.ERR_10056.setErrorCode(result);
				return result;
			}

			JsonObject customRoleDetailsJsonObject = customRoleDetailsElement.getAsJsonObject();

			String id = customRoleDetailsJsonObject.has(InfinityConstants.id)
					&& !customRoleDetailsJsonObject.get(InfinityConstants.id).isJsonNull()
							? customRoleDetailsJsonObject.get(InfinityConstants.id).getAsString()
							: "";

			if (StringUtils.isBlank(id)) {
				return ErrorCodeEnum.ERR_21104.setErrorCode(new Result());
			}

			jsonObject.add(InfinityConstants.customRoleDetails, customRoleDetailsJsonObject);

			if (validateinput(jsonObject, result, map, request, isContractValidationRequired, null)) {
				InfinityUserManagementBusinessDelegate infinityUserManagementBusinessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(InfinityUserManagementBusinessDelegate.class);
				DBXResult dbxResult = infinityUserManagementBusinessDelegate.editCustomRole(jsonObject,
						request.getHeaderMap());
				if (dbxResult.getResponse() != null) {
					jsonObject = (JsonObject) dbxResult.getResponse();
					result = JSONToResult.convert(jsonObject.toString());
					String signatoryGroups = map.get(InfinityConstants.signatoryGroups);
					JsonElement signatoryGroupsElement = new JsonParser().parse(signatoryGroups);
					if (!signatoryGroupsElement.isJsonNull() && signatoryGroupsElement.isJsonArray()) {
						JsonArray customRoleSignatoryGroupsObject = signatoryGroupsElement.getAsJsonArray();
						updateSignatoryGroups(id, customRoleSignatoryGroupsObject, request);
						//audit logs for Roles
						SBGCommonUtils._OlbAuditLogsClass(request,response,result,"USER","UPDATE_USER_ROLES","ExternalUserManagement/operations/Roles/updateRole","SUCCESS");
					}
				}
			}

		} catch (Exception exp) {
			logger.error("Exception occured while invoking resources of editCustomRole", exp);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}
	private void updateSignatoryGroups(String customRoleId, JsonArray customRoleSignatoryGroupsObject,
			DataControllerRequest request) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		String customerId = "";
		try {
			customerId = request.getServicesManager().getIdentityHandler().getUserId();
		} catch (AppRegistryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (StringUtils.isBlank(customerId)) {
			return;
		}

		Map<String, Object> inputMap = new HashMap<String, Object>();

		for (JsonElement element : customRoleSignatoryGroupsObject) {
			JsonObject signatory = element.getAsJsonObject();
			String contractId = signatory.has(InfinityConstants.contractId)
					&& !signatory.get(InfinityConstants.contractId).isJsonNull()
							? signatory.get(InfinityConstants.contractId).getAsString()
							: null;

			String cif = signatory.has(InfinityConstants.cif) && !signatory.get(InfinityConstants.cif).isJsonNull()
					? signatory.get(InfinityConstants.cif).getAsString()
					: null;

			if (StringUtils.isBlank(contractId) || StringUtils.isBlank(cif) || !signatory.has(InfinityConstants.groups)
					|| !signatory.get(InfinityConstants.groups).isJsonArray())
				continue;
			inputMap.put(InfinityConstants.coreCustomerId, cif);
			inputMap.put(InfinityConstants.cif, cif);
			inputMap.put(InfinityConstants.contractId, contractId);
			String filter = InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cif
					+ DBPUtilitiesConstants.AND + InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL
					+ contractId;
			map.put(DBPUtilitiesConstants.FILTER, filter);
			JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, request.getHeaderMap(),
					URLConstants.SIGNATORYGROUP_GET);

			Set<String> signatoryGroups = new HashSet<String>();
			// Set<String> validSignatoryGroups = new HashSet<String>();
			if (jsonObject.has(DBPDatasetConstants.SIGNATORYGROUP)
					&& jsonObject.get(DBPDatasetConstants.SIGNATORYGROUP).isJsonArray()
					&& jsonObject.get(DBPDatasetConstants.SIGNATORYGROUP).getAsJsonArray().size() > 0) {
				JsonArray groups = jsonObject.get(DBPDatasetConstants.SIGNATORYGROUP).getAsJsonArray();
				for (JsonElement group : groups) {
					if (group.isJsonObject()) {
						if (group.getAsJsonObject().has(InfinityConstants.signatoryGroupId)
								&& !group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).isJsonNull()) {
							signatoryGroups
									.add(group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).getAsString());
						}
					}
				}
			}

			boolean isAssociatedAtleastOne = false;

			if (signatoryGroups.size() > 0) {

				// map = new HashMap<String, Object>();
				//
				// filter = InfinityConstants.customerId + DBPUtilitiesConstants.EQUAL +
				// customerId;
				// map.put(DBPUtilitiesConstants.FILTER, filter);
				//
				// jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map,
				// request.getHeaderMap(),
				// URLConstants.CUSTOMER_SIGNATORY_GROUP_GET);
				//
				// if (jsonObject.has(DBPDatasetConstants.CUSTOMER_SIGNATORY_GROUP)
				// && jsonObject.get(DBPDatasetConstants.CUSTOMER_SIGNATORY_GROUP).isJsonArray()
				// &&
				// jsonObject.get(DBPDatasetConstants.CUSTOMER_SIGNATORY_GROUP).getAsJsonArray().size()
				// > 0) {
				// JsonArray groups =
				// jsonObject.get(DBPDatasetConstants.CUSTOMER_SIGNATORY_GROUP).getAsJsonArray();
				// for (JsonElement group : groups) {
				// if (group.isJsonObject()) {
				// if (group.getAsJsonObject().has(InfinityConstants.signatoryGroupId)
				// &&
				// !group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).isJsonNull())
				// {
				// if (signatoryGroups.contains(group.getAsJsonObject()
				// .get(InfinityConstants.signatoryGroupId).getAsString())) {
				// validSignatoryGroups.add(group.getAsJsonObject()
				// .get(InfinityConstants.signatoryGroupId).getAsString());
				// }
				// }
				// }
				// }
				// }

				JsonArray groups = signatory.get(InfinityConstants.groups).getAsJsonArray();
				String signatoryGroupId = "";

				if (groups.size() > 0) {
					for (JsonElement group : groups) {
						if (group.getAsJsonObject().has(InfinityConstants.signatoryGroupId)
								&& !group.getAsJsonObject().get(InfinityConstants.signatoryGroupId).isJsonNull()
								&& StringUtils.isNotBlank(group.getAsJsonObject()
										.get(InfinityConstants.signatoryGroupId).getAsString())) {
							boolean isAssociated = false;
							signatoryGroupId = group.getAsJsonObject().get(InfinityConstants.signatoryGroupId)
									.getAsString();

							if (group.getAsJsonObject().has(InfinityConstants.isAssociated)
									&& !group.getAsJsonObject().get(InfinityConstants.isAssociated).isJsonNull()
									&& Boolean.parseBoolean(group.getAsJsonObject().get(InfinityConstants.isAssociated)
											.getAsString())) {

								isAssociated = true;
								isAssociatedAtleastOne = true;
								inputMap.clear();
								inputMap.put(DBPUtilitiesConstants.FILTER,
										InfinityConstants.customRoleId + DBPUtilitiesConstants.EQUAL + customRoleId
												+ DBPUtilitiesConstants.OR
												+ InfinityConstants.customroleSignatoryGroupId
												+ DBPUtilitiesConstants.EQUAL + customRoleId);
								ServiceCallHelper.invokeServiceAndGetJson(inputMap, request.getHeaderMap(),
										URLConstants.CUSTOMROLESIGNATORYGROUP_DELETE);
							}

							if (isAssociated && signatoryGroups.contains(signatoryGroupId)) {
								inputMap.clear();
								inputMap.put(InfinityConstants.customroleSignatoryGroupId,
										customRoleId + "_" + signatoryGroupId);
								inputMap.put(InfinityConstants.customRoleId, customRoleId);
								inputMap.put(InfinityConstants.signatoryGroupId, signatoryGroupId);
								ServiceCallHelper.invokeServiceAndGetJson(inputMap, request.getHeaderMap(),
										URLConstants.CUSTOMROLESIGNATORYGROUP_CREATE);
								isAssociated = false;
							}
						}
					}
				}
			}

			if (!isAssociatedAtleastOne) {
				inputMap.clear();
				inputMap.put(DBPUtilitiesConstants.FILTER,
						InfinityConstants.customRoleId + DBPUtilitiesConstants.EQUAL + customRoleId
								+ DBPUtilitiesConstants.OR + InfinityConstants.customroleSignatoryGroupId
								+ DBPUtilitiesConstants.EQUAL + customRoleId);
				ServiceCallHelper.invokeServiceAndGetJson(inputMap, request.getHeaderMap(),
						URLConstants.CUSTOMROLESIGNATORYGROUP_DELETE);
			}
		}
	}
	@Override
	public Object createInfinityUserWithContract(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();

		Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

		Iterator<String> iterator = request.getParameterNames();

		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				map.put(key, request.getParameter(key));
			}
		}

		String contractDetails = map.containsKey(InfinityConstants.contractDetails)
				&& map.get(InfinityConstants.contractDetails) != null ? map.get(InfinityConstants.contractDetails)
						: new JsonArray().toString();

		JsonObject contractJsonObject;

		JsonElement element = new JsonParser().parse(contractDetails);

		if (element.isJsonObject()) {
			contractJsonObject = element.getAsJsonObject();
			if (contractJsonObject.has(InfinityConstants.contractName)
					&& !contractJsonObject.get(InfinityConstants.contractName).isJsonNull()) {
				for (Entry<String, JsonElement> entry : contractJsonObject.entrySet()) {
					map.put(entry.getKey(), entry.getValue().getAsString());
				}

				ContractResource resource = DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
				try {
					result = resource.createContract(methodId, inputArray, request, response);
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.getErrorCodeEnum().setErrorCode(result);
				}

				if (!result.hasParamByName(InfinityConstants.contractId)
						|| StringUtils.isBlank(result.getParamValueByName(InfinityConstants.contractId))) {
					return result;
				}

				String contractId = result.getParamValueByName(InfinityConstants.contractId);

				String companyList = map.get(InfinityConstants.companyList);

				JsonArray companyListArray = new JsonArray();

				element = new JsonParser().parse(companyList);

				if (element.isJsonArray()) {
					companyListArray = element.getAsJsonArray();
					for (int i = 0; i < companyListArray.size(); i++) {
						JsonObject company = companyListArray.get(i).getAsJsonObject();
						if (!company.has(InfinityConstants.contractId)
								|| company.get(InfinityConstants.contractId).isJsonNull()
								|| StringUtils.isBlank(company.get(InfinityConstants.contractId).getAsString())) {
							company.addProperty(InfinityConstants.contractId, contractId);
						}
					}
				}

				map.put(InfinityConstants.companyList, companyListArray.toString());
			}
		}

		InfinityUserManagementResource userManagementResource = DBPAPIAbstractFactoryImpl
				.getResource(InfinityUserManagementResource.class);

		result = (Result) userManagementResource.createInfinityUser(methodId, inputArray, request, response);
		//Audit logs code				
		SBGCommonUtils._OlbAuditLogsClass(request,response,result,"USER","CREATE_USER","ExternalUserManagement/operations/ExternalUsers_1/createUser","SUCCESS");

		return result;
	}

}
