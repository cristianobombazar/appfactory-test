package com.kony.sbg.resources.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.backend.api.DomesticPayeeBackendDelegate;
import com.kony.sbg.backend.api.PayeeBackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.dto.SbgInterBankPayeeBackendDTO;
import com.kony.sbg.util.ObjectConverterForFetchPayees;
import com.kony.sbg.util.ObjectConverterInterBank;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InterBankPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.InterBankPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeDTO;
import com.temenos.dbx.product.payeeservices.resource.impl.InterBankPayeeResourceImpl;

public class InterBankPayeeResourceImplExtn extends InterBankPayeeResourceImpl {

	private static final Logger LOG = LogManager.getLogger(InterBankPayeeResourceImplExtn.class);

	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

	InterBankPayeeBusinessDelegate interBankPayeeDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(InterBankPayeeBusinessDelegate.class);
	InterBankPayeeBackendDelegate interBankPayeeBackendDelegate = DBPAPIAbstractFactoryImpl
			.getBackendDelegate(InterBankPayeeBackendDelegate.class);

	@Override
	public Result createPayee(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		// LOG.debug("@@ InterBankPayeeResourceImplExtn.createPayee --->
		// state:"+inputParams.get("state")+";
		// entityType:"+inputParams.get("entityType")+";
		// beneCode:"+inputParams.get("beneCode"));

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);

		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT);
		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = (String) inputParams.get("cif");

		if (StringUtils.isBlank(sharedCifs)) {
			sharedCifMap = authorizationChecksBusinessDelegate.getAuthorizedCifs(featureActions, request.getHeaderMap(),
					request);
			if (sharedCifMap == null || sharedCifMap.isEmpty()) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		} else {
			sharedCifMap = _getContractCifMap(sharedCifs);

			// Authorization for all the cifs present in input for which the payee needs to
			// be shared
			if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap,
					request.getHeaderMap(), request)) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}

		SbgInterBankPayeeBackendDTO payeeBackendDTO = ObjectConverterInterBank.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(userId);

		if (!payeeBackendDTO.isValidInput()) {
			LOG.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(payeeBackendDTO.getBeneficiaryName())
				|| (StringUtils.isBlank(payeeBackendDTO.getIban())
						&& StringUtils.isBlank(payeeBackendDTO.getAccountNumber()))
				|| StringUtils.isBlank(payeeBackendDTO.getSwiftCode())) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}

		if (!_isUniquePayee(request, payeeBackendDTO, sharedCifMap)) {
			LOG.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}

		// Create one payee at the backend
		DomesticPayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(DomesticPayeeBackendDelegate.class);

		Result result2 = payeeBackendDelegate.createPayee(payeeBackendDTO, customer, request);
		if (result2 == null) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result2, payeeBackendDTO.getDbpErrMsg());
		}

		String uniqueId = result2.getParamValueByName("PAYEE.ID");

		// Getting payeeId of created payee
		String payeeId = uniqueId; // interBankPayeeBackendDTO.getId();
		if (payeeId == null || payeeId.isEmpty()) {
			LOG.error("PayeeId is empty or null");
			return ErrorCodeEnum.ERR_12053.setErrorCode(result2);
		}

		InterBankPayeeDTO interBankPayeeDTO = new InterBankPayeeDTO();
		interBankPayeeDTO.setPayeeId(payeeId);
		interBankPayeeDTO.setCreatedBy(userId);

		// Creating payee and cif mappings at dbx table
		for (Map.Entry<String, List<String>> contractCif : sharedCifMap.entrySet()) {
			interBankPayeeDTO.setContractId((String) contractCif.getKey());
			List<String> coreCustomerIds = contractCif.getValue();
			for (int j = 0; j < coreCustomerIds.size(); j++) {
				interBankPayeeDTO.setCif(coreCustomerIds.get(j));
				interBankPayeeDelegate.createPayeeAtDBX(interBankPayeeDTO);
			}
		}
		result2.addParam(new Param("Id", payeeId));
		result2.addParam(new Param("id", payeeId));

		return result2;
	}

	private Map<String, List<String>> _getContractCifMap(String inputCifs) {
		Map<String, List<String>> contractCifMap = new HashMap<String, List<String>>();
		/*
		 * "sharedCifs": [
		 * {
		 * "contractId": "1324",
		 * "coreCustomerId": "1,2"
		 * },
		 * {
		 * "contractId": "1234",
		 * "coreCustomerId": "3,4"
		 * }
		 * ]
		 */
		JsonParser jsonParser = new JsonParser();
		JsonArray contractCifArray = (JsonArray) jsonParser.parse(inputCifs);
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Gson gson = new Gson();
		if (contractCifArray.isJsonArray()) {
			for (int i = 0; i < contractCifArray.size(); i++) {
				@SuppressWarnings("unchecked")
				Map<String, String> contractObject = (Map<String, String>) gson.fromJson(contractCifArray.get(i), type);
				if (StringUtils.isNotBlank(contractObject.get("coreCustomerId"))) {
					List<String> coreCustomerIds = Arrays.asList(contractObject.get("coreCustomerId").split(","));
					contractCifMap.put(contractObject.get("contractId"), coreCustomerIds);
				}
			}
		}
		return contractCifMap;
		/*
		 * result will be
		 * {
		 * "1324": ["1","2"],
		 * "1234": ["3","4"]
		 * }
		 */
	}

	/*
	 * Method to check if payee details are unique for given cifs
	 */
	private boolean _isUniquePayee(DataControllerRequest request, SbgInterBankPayeeBackendDTO inputDTO,
			Map<String, List<String>> cifMap) {
		Set<String> inputCifs = new HashSet<>();
		for (Map.Entry<String, List<String>> map : cifMap.entrySet()) {
			inputCifs.addAll(map.getValue());
		}

		List<InterBankPayeeDTO> payeeDTOs = interBankPayeeDelegate.fetchPayeesFromDBX(inputCifs);
		if (payeeDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return false;
		}
		if (payeeDTOs.isEmpty()) {
			LOG.error("No Payees Found");
			return true;
		}

		Set<String> payeeIds = payeeDTOs.stream().map(InterBankPayeeDTO::getPayeeId).distinct()
				.collect(Collectors.toSet());

		InterBankPayeeBackendDelegate intlPayeeBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(InterBankPayeeBackendDelegate.class);
		List<InterBankPayeeBackendDTO> backendDTOs = intlPayeeBackendDelegate.fetchPayees(payeeIds,
				request.getHeaderMap(), request);
		if (backendDTOs == null) {
			LOG.error("Error occurred while fetching payees from backend");
			return false;
		}

		if (backendDTOs.size() == 0) {
			LOG.error("No Payees Found");
			return true;
		}

		if (backendDTOs.get(0).getDbpErrMsg() != null && !backendDTOs.get(0).getDbpErrMsg().isEmpty()) {
			return false;
		}
		// String inputIBAN = inputDTO.getIban();
		String inputAccountNumber = inputDTO.getAccountNumber();
		String inputSwiftCode = inputDTO.getSwiftCode();
		for (int i = 0; i < backendDTOs.size(); i++) {
			InterBankPayeeBackendDTO backendDTO = backendDTOs.get(i);
			// String IBAN = backendDTO.getIban();
			String accountNumber = backendDTO.getAccountNumber();
			String swiftCode = backendDTO.getSwiftCode();
			if (inputSwiftCode.equals(swiftCode)
					&& (StringUtils.isBlank(inputAccountNumber) || inputAccountNumber.equals(accountNumber))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Result fetchAllMyPayees(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Result result = new Result();

		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_VIEW_RECEPIENT);
		// To get cifs which are authorized for the user
		Set<String> authorizedCifs = authorizationChecksBusinessDelegate
				.getUserAuthorizedCifsForFeatureAction(featureActions, request.getHeaderMap(), request);
		if (authorizedCifs == null || authorizedCifs.isEmpty()) {
			LOG.error("The logged in user doesn't have permission to perform this action");
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}

		// To fetch payees for authorized cifs
		List<InterBankPayeeDTO> interbankPayeeDTOs = interBankPayeeDelegate.fetchPayeesFromDBX(authorizedCifs);
		if (interbankPayeeDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
		}
		if (interbankPayeeDTOs.isEmpty()) {
			LOG.error("No Payees Found");
			return JSONToResult.convert(new JSONObject().put("externalaccount", new JSONArray()).toString());
		}

		// Getting unique payees with comma seperated cif list
		interbankPayeeDTOs = _getUniquePayees(interbankPayeeDTOs);

		// Getting a list of unique payeeIds
		Set<String> payeeIds = interbankPayeeDTOs.stream().map(InterBankPayeeDTO::getPayeeId)
				.collect(Collectors.toSet());
		// for ids
		String payeeFilter = "";
		if (payeeIds != null && payeeIds.size() > 0)
			for (String payeeId : payeeIds) {
				if (payeeFilter.isEmpty()) {
					payeeFilter = "Id eq " + payeeId;
					continue;
				}
				payeeFilter = payeeFilter + " or " + "Id" + " eq " + payeeId;
			}

		// Fetching backend records using payeeId
		// Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		LOG.debug("@@ InterBankPayeeResourceImplExtn.createPayee ---> payeeFilter:" + payeeFilter);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		PayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(PayeeBackendDelegate.class);
		SbgExternalPayeeBackendDTO payeeBackendDTO = ObjectConverterForFetchPayees.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(userId);
		result = payeeBackendDelegate.fetchPayees(payeeFilter, payeeBackendDTO, request.getHeaderMap(), request);

		// JSONObject resultJsonObject = addProofOfPaymentDetails(result);
		// Result resultNewResponse = new Result();
		// resultNewResponse.appendJson(resultJsonObject.toString());

		// String jsonDocStrResponse = ResultToJSON.convert(result);
		// LOG.debug("#### final response of external_payee:"+jsonDocStrResponse);

		// if (resultNewResponse == null) {
		// 	LOG.error("Error occurred while fetching payees from backend");
		// 	return ErrorCodeEnum.ERR_12058.setErrorCode(new Result());
		// }

		return result;
	}

	private List<InterBankPayeeDTO> _getUniquePayees(List<InterBankPayeeDTO> interBankPayeeDTOs) {
		Map<String, HashMap<String,String>> payeeMap = new HashMap<>();
		
		for(InterBankPayeeDTO payee: interBankPayeeDTOs){
			if(payeeMap.containsKey(payee.getPayeeId())) {
				HashMap<String,String> cifMap = payeeMap.get(payee.getPayeeId());
				String cifIds = payee.getCif();
				if(cifMap.containsKey(payee.getContractId())) {
					cifIds = cifMap.get(payee.getContractId()) + "," + payee.getCif();
				}
				cifMap.put(payee.getContractId(), cifIds);
				payeeMap.put(payee.getPayeeId(), cifMap);
			}
			else {
				HashMap<String,String> cifMap = new HashMap<String,String>();
				cifMap.put(payee.getContractId(), payee.getCif());
				payeeMap.put(payee.getPayeeId(), cifMap);
			}
		}
		
		for(int i = 0; i < interBankPayeeDTOs.size(); i++){
			InterBankPayeeDTO payee = interBankPayeeDTOs.get(i);
			if(payeeMap.containsKey(payee.getPayeeId())) {
				HashMap<String,String> cifMap = payeeMap.get(payee.getPayeeId());
				JSONArray cifArray = new JSONArray();
				int noOfCustomersLinked = 0;
				for (Map.Entry<String,String> entry : cifMap.entrySet()) {
					JSONObject cifObj = new JSONObject();
					cifObj.put("contractId", entry.getKey());
					cifObj.put("coreCustomerId", entry.getValue());
					noOfCustomersLinked += entry.getValue().split(",").length;
					cifArray.put(cifObj);
				}
				payee.setCif(cifArray.toString());
				payee.setNoOfCustomersLinked(String.valueOf(noOfCustomersLinked));
				payeeMap.remove(payee.getPayeeId());
			}
			else {
				interBankPayeeDTOs.remove(i);
				i--;
			}
		}
		
		return interBankPayeeDTOs;
	}

	private JSONObject addProofOfPaymentDetails(Result result) {

		try {

			String jsonExternalPayeeRsp = ResultToJSON.convert(result);
			JSONObject externalPayeeJsonObj = new JSONObject(jsonExternalPayeeRsp);
			JSONArray externalPayeeArray = externalPayeeJsonObj.getJSONArray("externalaccount");
			LOG.debug("#### externalPayeeArray:"+externalPayeeArray);
			for(int x = 0; x < externalPayeeArray.length(); x++) {
				JSONObject payeeObject = externalPayeeArray.getJSONObject(x);
				JSONObject constructProofOfpayment = constructProofOfpayment(payeeObject);
				if (constructProofOfpayment !=null) {
					externalPayeeArray.remove(x);
					externalPayeeArray.put(constructProofOfpayment);
				}

			}
			return externalPayeeJsonObj;
		} catch (Exception e) {
			LOG.debug("#### addProofOfPaymentDetails failed:", e);
			String jsonExternalPayeeRsp = ResultToJSON.convert(result);
			return new JSONObject(jsonExternalPayeeRsp);
		}
	}

	public static JSONObject constructProofOfpayment(JSONObject payeeJsonObject) {
		try {
			JSONArray popJsonArray = new JSONArray(payeeJsonObject.getString("proofOfPayment"));
			for (int i = 0; i < popJsonArray.length(); i++) {
				JSONObject conveted = popJsonArray.getJSONObject(i);
				LOG.debug("####"+conveted);
				if (conveted.get("popType").toString().equalsIgnoreCase("SMS")) {
					payeeJsonObject.put("smsProofOfPayment", conveted.toString());
				} else if (conveted.get("popType").toString().equalsIgnoreCase("Email")) {
					payeeJsonObject.put("emailProofOfPayment", conveted.toString());
				}
			}
			payeeJsonObject.remove("proofOfPayment");
			return payeeJsonObject;
		} catch (Exception e) {
			LOG.debug("#### constructProofOfpayment failed:", e);
			return null;
		}
	}

}
