package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.party.utils.PartyConstants;
import com.temenos.dbx.party.utils.PartyPropertyConstants;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.kony.sbg.dto.PartyUtilsExtn;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyDTO;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.HTTPOperations;
import com.temenos.dbx.usermanagement.backenddelegate.impl.PartyProfileManagementBackendDelegateImpl;
import com.temenos.dbx.usermanagement.businessdelegate.api.PartyUserManagementBusinessDelegate;
import com.temenos.dbx.usermanagement.constants.PartyProfileDetailsConstants;
import com.temenos.dbx.usermanagement.dto.PartyDetails;
import com.temenos.dbx.usermanagement.dto.PartySearchDTO;

public class PartyProfileManagementBackendDelegateImplExtn extends PartyProfileManagementBackendDelegateImpl {

	private static LoggerUtil logger = new LoggerUtil(PartyProfileManagementBackendDelegateImplExtn.class);

	public DBXResult updateCustomer(CustomerDTO customerDTO, Map<String, Object> headerMap) {

		DBXResult dbxResult = new DBXResult();

		JsonObject resultJsonObject = new JsonObject();
		dbxResult.setResponse(resultJsonObject);

		BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
		backendIdentifierDTO.setCustomer_id(customerDTO.getId());
		backendIdentifierDTO.setBackendType(DTOConstants.PARTY);
		DBXResult backendResult = new DBXResult();
		try {
			backendResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
					.get(backendIdentifierDTO, headerMap);
		} catch (ApplicationException e1) {
			logger.error("Error while fetching backend identifier for customer ID " + customerDTO.getId());
		}

		PartyDTO partyDTO = new PartyDTO();

		String partyId = null;

		if (backendResult.getResponse() != null) {
			BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) backendResult.getResponse();

			partyId = identifierDTO.getBackendId();
		}

		if (StringUtils.isBlank(partyId)) {
			PartyUserManagementBusinessDelegate managementBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(PartyUserManagementBusinessDelegate.class);
			DBXResult response = new DBXResult();
			partyDTO.setPartyId(customerDTO.getId());
			response = managementBusinessDelegate.get(partyDTO, headerMap);
			if (response.getResponse() != null) {
				JsonObject jsonObject = (JsonObject) response.getResponse();
				if (jsonObject.has(PartyConstants.partyId) && !jsonObject.get(PartyConstants.partyId).isJsonNull()) {
					partyId = customerDTO.getId();
				}
			}
		}

		if (StringUtils.isBlank(partyId)) {

			PartyUserManagementBusinessDelegate managementBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(PartyUserManagementBusinessDelegate.class);
			DBXResult response = new DBXResult();
			JsonArray partyJsonArray = new JsonArray();
			Map<String, String> inputMap = new HashMap<String, String>();
			String companyId = EnvironmentConfigurationsHandler
					.getServerProperty(DBPUtilitiesConstants.BRANCH_ID_REFERENCE);
			inputMap.put("alternateIdentifierNumber", companyId + "-" + customerDTO.getId());
			inputMap.put("alternateIdentifierType", PartyConstants.BackOfficeIdentifier);
			PartySearchDTO searchDTO = buildSearchPartyDTO(inputMap);
			headerMap = PartyUtils.addJWTAuthHeader(null, headerMap, AuthConstants.PRE_LOGIN_FLOW);
			response = managementBusinessDelegate.searchParty(searchDTO, headerMap);
			if (response.getResponse() != null) {
				JsonObject party = (JsonObject) response.getResponse();
				partyJsonArray = party.has(PartyConstants.parties) && party.get(PartyConstants.parties).isJsonArray()
						? party.get(PartyConstants.parties).getAsJsonArray()
						: new JsonArray();
			}

			if (partyJsonArray.size() > 0) {
				JsonObject party = partyJsonArray.get(0).isJsonObject() ? partyJsonArray.get(0).getAsJsonObject()
						: new JsonObject();
				if (party.has(PartyConstants.partyId) && !party.get(PartyConstants.partyId).isJsonNull()) {
					partyId = party.get(PartyConstants.partyId).getAsString();
				}
			}
		}

		if (StringUtils.isBlank(partyId)) {
			ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject, "party backendIdentifier not found");
			resultJsonObject.addProperty("errmsg", "party backendIdentifier not found");
			return dbxResult;
		}

		try {
			PartyDetails partyDetails = null;
			if (customerDTO.getSource() != null) {
				if (customerDTO.getSource().contentEquals(PartyProfileDetailsConstants.PARAM_SOURCE)) {
					if (customerDTO.getOperation().contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_CREATE)
							|| customerDTO.getOperation()
									.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_UPDATE)) {
						partyDetails = PartyUtils.buildPartyDTOforEmailPhone(customerDTO);
						dbxResult = updatePartyProfile(partyDetails, headerMap, partyId, customerDTO);
						return dbxResult;
					} else if (customerDTO.getOperation()
							.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_CREATEADDRESS)
							|| customerDTO.getOperation()
									.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_UPDATEADDRESS)) {
						partyDetails = PartyUtils.buildPartyDTOforAddress(customerDTO, partyId, headerMap);
						dbxResult = updatePartyProfile(partyDetails, headerMap, partyId, customerDTO);
						return dbxResult;
					} else if (customerDTO.getOperation()
							.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_DELETE)
							|| customerDTO.getOperation()
									.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_DELETEADDRESS)) {
						dbxResult = deletePartyAddress(headerMap, partyId, customerDTO);
						return dbxResult;
					}

				}
			}
		} catch (Exception e) {
			logger.error("Caught exception while updating Party: ", e);
			ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject, "Unable to update Party");
			resultJsonObject.addProperty("errmsg", "Unable to update Party ");
			return dbxResult;
		}

		if (StringUtils.isNotBlank(customerDTO.getOperation())
				&& (customerDTO.getOperation().contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_DELETE)
						|| customerDTO.getOperation()
								.contentEquals(PartyProfileDetailsConstants.PARAM_OPERATION_DELETEADDRESS))) {
			dbxResult = deletePartyAddress(headerMap, partyId, customerDTO);
			return dbxResult;
		}

		try {
			PartyUtils.addJWTAuthHeader(headerMap, AuthConstants.PRE_LOGIN_FLOW);

			String partyURL = URLFinder.getServerRuntimeProperty(URLConstants.PARTY_HOST_URL)
					+ PartyURLFinder.getServiceUrl(PartyPropertyConstants.PARTY_GET, partyId);

			DBXResult response = HTTPOperations.sendHttpRequest(HTTPOperations.operations.GET, partyURL, null,
					headerMap);
			JsonObject jsonObject = new JsonParser().parse((String) response.getResponse()).getAsJsonObject();
			logger.debug("jsonObject party get: "+ jsonObject);

			partyDTO.loadFromJson(jsonObject);
		} catch (Exception e) {
			logger.error("Caught exception while getting Party: ", e);
			ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject, "Unable to get Party ");
			resultJsonObject.addProperty("errmsg", "Unable to get Party ");
			return dbxResult;
		}

		try {
			partyDTO = PartyUtilsExtn.buildPartyDTO(customerDTO, partyDTO);

		} catch (Exception e) {
			logger.error("Caught exception while updating party from Customer DTO: ", e);
			ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject, "Unable to convert Customer to Party");
			resultJsonObject.addProperty("errmsg", "Unable to convert Customer to Party");
			return dbxResult;
		}
		String party = partyDTO.toStringJson().toString();

		logger.debug("PartyDTO for update Party Service is : " + party);

		String partyURL = URLFinder.getServerRuntimeProperty(URLConstants.PARTY_HOST_URL)
				+ PartyURLFinder.getServiceUrl(PartyPropertyConstants.PARTY_UPDATE, partyId);

		DBXResult response = HTTPOperations.sendHttpRequest(HTTPOperations.operations.PUT, partyURL, party, headerMap);

		try {
			JsonElement jsonElement = new JsonParser().parse((String) response.getResponse());
			JsonObject jsonObject;

			jsonObject = jsonElement.isJsonObject() ? jsonElement.getAsJsonObject()
					: jsonElement.getAsJsonArray().get(0).getAsJsonObject();

			if (jsonObject.has("id")) {
				resultJsonObject.addProperty("success", "success");
				resultJsonObject.addProperty("Status", "Operation successful");
				resultJsonObject.addProperty("status", "Operation successful");
			} else {
				ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject,
						"Unable to update Party " + jsonObject.get("message").getAsString());
				resultJsonObject.addProperty("errmsg",
						"Unable to update Party " + jsonObject.get("message").getAsString());

				return dbxResult;
			}

		} catch (Exception e) {
			logger.error("Caught exception while updating Party: ", e);
			ErrorCodeEnum.ERR_10218.setErrorCode(resultJsonObject, "Unable to update Party");
			resultJsonObject.addProperty("errmsg", "Unable to update Party ");
			return dbxResult;
		}

		return dbxResult;
	}

	private PartySearchDTO buildSearchPartyDTO(Map<String, String> inputMap) {
		PartySearchDTO partySearchDTO = new PartySearchDTO();
		DTOUtils.loadInputIntoDTO(partySearchDTO, inputMap, false);
		return partySearchDTO;
	}

}
