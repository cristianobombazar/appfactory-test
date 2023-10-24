package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import com.dbp.core.util.JSONUtils;
import com.kony.sbg.business.impl.SbgInternationalPayeeBackendDelegateImplExtn;
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
import com.kony.sbg.backend.api.PayeeBackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.dto.SbgInternationalPayeeBackendDTO;
import com.kony.sbg.util.ObjectConverter;
import com.kony.sbg.util.ObjectConverterForFetchPayees;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InternationalPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.InternationalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeDTO;
import com.temenos.dbx.product.payeeservices.resource.impl.InternationalPayeeResourceImpl;

public class InternationalPayeeResourceImplExtn extends InternationalPayeeResourceImpl {
	
	private static final Logger LOG = LogManager.getLogger(InternationalPayeeResourceImplExtn.class);

	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	
	InternationalPayeeBusinessDelegate payeeDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(InternationalPayeeBusinessDelegate.class);
	//InternationalPayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InternationalPayeeBackendDelegate.class);
	
	@Override
   	public Result createPayee(String methodID, Object[] inputArray, DataControllerRequest request, 
   			DataControllerResponse response) 
	{
		@SuppressWarnings("unchecked")
   		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		LOG.debug("@@ InternationalPayeeResourceImplExtn.createPayee ---> state:"+inputParams.get("state")+"; entityType:"+inputParams.get("entityType"));   		
   		
   		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
   		String userId = CustomerSession.getCustomerId(customer);
   		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> userId: "+userId);
   		
		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT);
   		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = (String) inputParams.get("cif");
		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> sharedCifs: "+sharedCifs);
		
		if(StringUtils.isBlank(sharedCifs)) {
			sharedCifMap = authorizationChecksBusinessDelegate.getAuthorizedCifs(featureActions, request.getHeaderMap(), request);
			if(sharedCifMap == null || sharedCifMap.isEmpty()) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
		else {
			sharedCifMap = _getContractCifMap(sharedCifs);
					
			//Authorization for all the cifs present in input for which the payee needs to be shared
			if(!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap, request.getHeaderMap(), request)) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
   		
		SbgInternationalPayeeBackendDTO payeeBackendDTO = ObjectConverter.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(userId);

   		LOG.debug("@@ InternationalPayeeResourceImplExtn.createPayee ---> state:"+payeeBackendDTO.getState()+"; entityType:"+payeeBackendDTO.getEntityType());
   		
   		if(!payeeBackendDTO.isValidInput()) {
			LOG.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}
   		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> DTO validated");
   		
   		if(StringUtils.isBlank(payeeBackendDTO.getBeneficiaryName()) 
				|| (StringUtils.isBlank(payeeBackendDTO.getIban()) && StringUtils.isBlank(payeeBackendDTO.getAccountNumber()))
				|| StringUtils.isBlank(payeeBackendDTO.getSwiftCode())) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}
		  		
		if(!_isUniquePayee(request, payeeBackendDTO, sharedCifMap))
		{
			LOG.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}
		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> verified uniqueness ");
		
   		//Create one payee at the backend
		PayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(PayeeBackendDelegate.class);
		
		Result result2 = payeeBackendDelegate.createPayee(payeeBackendDTO, customer, request);
		if(result2 == null) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result2, payeeBackendDTO.getDbpErrMsg());
		}
		
		String uniqueId = result2.getParamValueByName("PAYEE.ID");
   		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> payee created ... uniqueId:"+uniqueId);
   		
   		//Getting payeeId of created payee
   		String payeeId = uniqueId; //internationalPayeeBackendDTO.getId();
   		if(payeeId == null || payeeId.isEmpty()) {
   			LOG.error("PayeeId is empty or null");
   			return ErrorCodeEnum.ERR_12053.setErrorCode(result2);
   		}
   		//LOG.debug("@@ createPayee.InternationalPayeeResourceImplExtn ---> payeeId: "+payeeId);
   		
   		InternationalPayeeDTO internationalPayeeDTO = new InternationalPayeeDTO();
   		internationalPayeeDTO.setPayeeId(payeeId);
   		internationalPayeeDTO.setCreatedBy(userId);
   		
   		//Creating payee and cif mappings at dbx table
   		for (Map.Entry<String,List<String>> contractCif : sharedCifMap.entrySet())  {
   			internationalPayeeDTO.setContractId((String) contractCif.getKey());
               List<String> coreCustomerIds = contractCif.getValue(); 
               for(int j = 0; j < coreCustomerIds.size(); j++) {
            	   internationalPayeeDTO.setCif(coreCustomerIds.get(j));
               	payeeDelegate.createPayeeAtDBX(internationalPayeeDTO);
   			}
       	} 
   		result2.addParam(new Param("Id", payeeId));
   		result2.addParam(new Param("id", payeeId));
  		
   		return result2;
   	}
	
 	private Map<String, List<String>> _getContractCifMap(String inputCifs) {
   		Map<String, List<String>> contractCifMap = new HashMap<String, List<String>>();

       JsonParser jsonParser = new JsonParser();
       JsonArray contractCifArray = (JsonArray) jsonParser.parse(inputCifs);
       Type type = new TypeToken<Map<String, String>>() {}.getType();
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
	}

	private boolean _isUniquePayee(DataControllerRequest request, SbgInternationalPayeeBackendDTO inputDTO, Map<String, List<String>> cifMap) {
		Set<String> inputCifs = new HashSet<>();
		for(Map.Entry<String, List<String>> map : cifMap.entrySet()){
			inputCifs.addAll(map.getValue());
		}
		
		List<InternationalPayeeDTO> payeeDTOs = payeeDelegate.fetchPayeesFromDBX(inputCifs);
		if(payeeDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return false;
		}
		if(payeeDTOs.isEmpty()) {
			LOG.error("No Payees Found");
	        return true;
		}
		
		Set<String> payeeIds = payeeDTOs.stream().map(InternationalPayeeDTO::getPayeeId).distinct().collect(Collectors.toSet());
		
		InternationalPayeeBackendDelegate intlPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InternationalPayeeBackendDelegate.class);
		List<InternationalPayeeBackendDTO> backendDTOs = intlPayeeBackendDelegate.fetchPayees(payeeIds, request.getHeaderMap(), request);
		if(backendDTOs == null) {
			LOG.error("Error occurred while fetching payees from backend");
			return false;
		}
		
		if(backendDTOs.size() == 0){
			LOG.error("No Payees Found");
	        return true;
        }
		
		if(backendDTOs.get(0).getDbpErrMsg() != null && !backendDTOs.get(0).getDbpErrMsg().isEmpty()) {
			return false;
		}
		//String inputIBAN = inputDTO.getIban();
		String inputAccountNumber = inputDTO.getAccountNumber();
		String inputSwiftCode = inputDTO.getSwiftCode();
		for(int i = 0; i < backendDTOs.size(); i++) {
			InternationalPayeeBackendDTO backendDTO = backendDTOs.get(i);
			//String IBAN = backendDTO.getIban();
			String accountNumber = backendDTO.getAccountNumber();
			String swiftCode = backendDTO.getSwiftCode();
			if(inputSwiftCode.equals(swiftCode) && (StringUtils.isBlank(inputAccountNumber) || inputAccountNumber.equals(accountNumber))) {
				return false;
			}
		}		
		return true;
	}

    /*@Override
	public Result deletePayee(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		String payeeId = null;
		if(inputParams.get("payeeId") != null) {
			payeeId = inputParams.get("payeeId").toString();
		}
		
		if(payeeId == null || payeeId.isEmpty()) {
			LOG.error("Missing payeeId");
			return ErrorCodeEnum.ERR_12054.setErrorCode(new Result());
		}
		
		// Authorization check for cifs for which the payee is deleted
//		Set<String> authorizedCifs = authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(Arrays.asList(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_DELETE_RECEPIENT), request.getHeaderMap(), request);
//		if(authorizedCifs == null || authorizedCifs.isEmpty()) {
//			LOG.error("The logged in user doesn't have permission to perform this action");
//			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
//		}
		
		//Checking if payee exists with that payeeId or not
		InternationalPayeeDTO internationalPayeeDTO = new InternationalPayeeDTO();
		internationalPayeeDTO.setPayeeId(payeeId);
		List<InternationalPayeeDTO> internationalPayeeDTOs = payeeDelegate.fetchPayeeByIdAtDBX(internationalPayeeDTO);
		if(internationalPayeeDTOs == null) {
			LOG.error("Failed to fetch payee");
			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
		}
		
		if(internationalPayeeDTOs.isEmpty()) {
			LOG.error("Records doesn't exist");
			return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
		}

//		Set<String> cifs = internationalPayeeDTOs.stream().map(InternationalPayeeDTO::getCif).collect(Collectors.toSet());
//		cifs.retainAll(authorizedCifs);
//		if(cifs.isEmpty()) {
//			LOG.error("The logged in user doesn't have permission to perform this action");
//			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
//		}
		
		//Deleting payee at the backend
		SbgInternationalPayeeBackendDTO payeeBackendDTO = ObjectConverter.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(internationalPayeeDTOs.get(0).getCreatedBy());
		
		PayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(PayeeBackendDelegate.class);
		result = payeeBackendDelegate.deletePayee(payeeBackendDTO, inputParams, request);
		
		payeeBackendDTO.setId(payeeId);
		
		//Deleting payee at dbx
		payeeDelegate.deletePayeeAtDBX(internationalPayeeDTO);
		
		return result;
	}*/
    @Override
   	public Result fetchAllMyPayees(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
       	@SuppressWarnings("unchecked")
   		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
   		Result result = new Result();

   		//Map<String, Object> customer = CustomerSession.getCustomerMap(request);
   		//TODO - Fetch cif of logged user 
   		//String userId = CustomerSession.getCustomerId(customer);

   		List<String> featureActions = new ArrayList<String>();
   		featureActions.add(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_VIEW_RECEPIENT);
   		//To get cifs which are authorized for the user
   		Set<String> authorizedCifs = authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(featureActions, request.getHeaderMap(), request);
   		if(authorizedCifs == null || authorizedCifs.isEmpty()) {
   			LOG.error("The logged in user doesn't have permission to perform this action");
   			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
   		}
   		
   		//To fetch payees for authorized cifs
   		List<InternationalPayeeDTO> internationalPayeeDTOs = payeeDelegate.fetchPayeesFromDBX(authorizedCifs);
   		if(internationalPayeeDTOs == null) {
   			LOG.error("Error occurred while fetching payees from dbx ");
   			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
   		}
   		if(internationalPayeeDTOs.isEmpty()) {
   			LOG.error("No Payees Found");
   	        return JSONToResult.convert(new JSONObject().put("externalaccount", new JSONArray()).toString());
   		}
   		
   		//Getting unique payees with comma seperated cif list
   		internationalPayeeDTOs = _getUniquePayees(internationalPayeeDTOs);
   		
   		//Getting a list of unique payeeIds
   		Set<String> payeeIds = internationalPayeeDTOs.stream().map(InternationalPayeeDTO::getPayeeId).collect(Collectors.toSet());
   			//for ids
   		String payeeFilter = "";
   	 if (payeeIds != null && payeeIds.size() > 0)
         for (String payeeId : payeeIds) {
           if (payeeFilter.isEmpty()) {
             payeeFilter = "Id eq " + payeeId;
             continue;
           } 
           payeeFilter = payeeFilter + " or " + "Id" + " eq " + payeeId;
         }  
   				
   		//Fetching backend records using payeeId
   		//Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		LOG.debug("@@ InternationalPayeeResourceImplExtn.createPayee ---> payeeFilter:"+payeeFilter);   		
   		
   		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
   		String userId = CustomerSession.getCustomerId(customer);
   		PayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(PayeeBackendDelegate.class);
   		SbgExternalPayeeBackendDTO payeeBackendDTO = ObjectConverterForFetchPayees.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(userId);
   		result = payeeBackendDelegate.fetchPayees(payeeFilter,payeeBackendDTO, request.getHeaderMap(), request);
   		
   		if(result == null) {
   			LOG.error("Error occurred while fetching payees from backend");
   			return ErrorCodeEnum.ERR_12058.setErrorCode(new Result());
   		}
   		
//   		if(internationalPayeeBackendDTOs.size() == 0){
//   			LOG.error("No Payees Found");
//   	        return JSONToResult.convert(new JSONObject().put("externalaccount", new JSONArray()).toString());
//           }			
//
//   		if(internationalPayeeBackendDTOs.get(0).getDbpErrMsg() != null && !(internationalPayeeBackendDTOs.get(0).getDbpErrMsg()).isEmpty()) {
//   			return ErrorCodeEnum.ERR_00000.setErrorCode(result, internationalPayeeBackendDTOs.get(0).getDbpErrMsg());
//   		}
//   		
//   		//Merge bankend payees with dbx payees using Id and payeeId fields and populating cif information from dbx payee
//   		internationalPayeeBackendDTOs = (new FilterDTO()).merge(internationalPayeeBackendDTOs, internationalPayeeDTOs, "id=payeeId", "cif");
//   		if(internationalPayeeBackendDTOs == null) {
//   			LOG.error("Error occurred while merging payee details");
//   			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
//   		}
   		
   		//Applying filters like offset,limit,sort etc
//   		FilterDTO filterDTO = new FilterDTO();
//   		try {
//   			filterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
//   		} catch (IOException e) {
//   			LOG.error("Exception occurred while fetching params: ",e);
//               return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
//   		} catch (java.io.IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//   		internationalPayeeBackendDTOs = filterDTO.filter(internationalPayeeBackendDTOs);
//   				
//   		try {
//   			JSONArray resArray = new JSONArray(internationalPayeeBackendDTOs);
//               JSONObject resultObj = new JSONObject();
//               resultObj.put("externalaccount",resArray);
//               result = JSONToResult.convert(resultObj.toString());
//   		}
//   		catch(Exception exp) {
//   			LOG.error("Error occurred while defining resources for fetch all templates", exp);
//   			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
//   		}
   		return result;
       }
    private List<InternationalPayeeDTO> _getUniquePayees(List<InternationalPayeeDTO> internationalPayeeDTOs) {
		Map<String, HashMap<String,String>> payeeMap = new HashMap<>();
		
		for(InternationalPayeeDTO payee: internationalPayeeDTOs){
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
		
		for(int i = 0; i < internationalPayeeDTOs.size(); i++){
			InternationalPayeeDTO payee = internationalPayeeDTOs.get(i);
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
				internationalPayeeDTOs.remove(i);
				i--;
			}
		}
		
		return internationalPayeeDTOs;
	}

	public Result editPayee(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {

		//InternationalPayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InternationalPayeeBackendDelegate.class);

		Map<String, Object> inputParams = (HashMap)inputArray[1];
		Result result = new Result();
		String payeeId = (String)inputParams.get("payeeId");
		if (org.apache.commons.lang.StringUtils.isBlank(payeeId)) {
			LOG.error("Missing payeeId");
			return ErrorCodeEnum.ERR_12054.setErrorCode(new Result());
		} else {
			new HashMap();
			Map<String, List<String>> removedCifsMap = new HashMap();
			Map<String, List<String>> addedCifsMap = new HashMap();
			new HashMap();
			String editedCifs = (String)inputParams.get("cif");
			Map editedCifsMap;
			if (org.apache.commons.lang.StringUtils.isBlank(editedCifs)) {
				inputParams.put("cif", "{}");
				editedCifsMap = this.authorizationChecksBusinessDelegate.getAuthorizedCifs(Arrays.asList("INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT"), request.getHeaderMap(), request);
				if (editedCifsMap == null || editedCifsMap.isEmpty()) {
					LOG.error("The logged in user doesn't have permission to perform this action");
					return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
				}
			} else {
				editedCifsMap = this._getContractCifMap(editedCifs);
				if (!this.authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(Arrays.asList("INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT"), editedCifsMap, request.getHeaderMap(), request)) {
					LOG.error("The logged in user doesn't have permission to perform this action");
					return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
				}
			}

			InternationalPayeeDTO internationalPayeeDTO = new InternationalPayeeDTO();
			internationalPayeeDTO.setPayeeId(payeeId);
			List<InternationalPayeeDTO> internationalPayeeDTOs = this.payeeDelegate.fetchPayeeByIdAtDBX(internationalPayeeDTO);
			if (internationalPayeeDTOs == null) {
				LOG.error("Failed to fetch payee");
				return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
			} else if (internationalPayeeDTOs.isEmpty()) {
				LOG.error("Records doesn't exist");
				return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
			} else {
				Set<String> payeeIds = new HashSet();
				payeeIds.add(payeeId);
				SbgInternationalPayeeBackendDelegateImplExtn payeeBackendDelegatelocal= new SbgInternationalPayeeBackendDelegateImplExtn();
				List<InternationalPayeeBackendDTO> backendDTOs = payeeBackendDelegatelocal.fetchPayees(payeeIds, request.getHeaderMap(), request);

				LOG.debug("###### InternationalPayeeResourceImplExtn fetchPayees response:"+backendDTOs+" size:"+backendDTOs.size());

				if (backendDTOs == null) {
					LOG.error("Error occurred while fetching payees from backend");
					return ErrorCodeEnum.ERR_12058.setErrorCode(new Result());
				} else if (backendDTOs.size() == 0) {
					LOG.error("No Payees Found");
					return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
				} else if (((InternationalPayeeBackendDTO)backendDTOs.get(0)).getDbpErrMsg() != null && !((InternationalPayeeBackendDTO)backendDTOs.get(0)).getDbpErrMsg().isEmpty()) {
					return ErrorCodeEnum.ERR_00000.setErrorCode(result, ((InternationalPayeeBackendDTO)backendDTOs.get(0)).getDbpErrMsg());
				} else {
					Map<String, List<String>> associatedCifsMap = this._getAssociatedCifsMap(internationalPayeeDTOs, request);
					if (org.apache.commons.lang.StringUtils.isNotBlank(editedCifs)) {
						this._setAddedAndRemovedCifMaps(editedCifsMap, addedCifsMap, removedCifsMap, associatedCifsMap);
						if (addedCifsMap.isEmpty() && removedCifsMap.isEmpty()) {
							editedCifs = null;
						}
					}
					LOG.debug("###### InternationalPayeeResourceImplExtn 01");
					String createdBy = internationalPayeeDTOs.get(0).getCreatedBy();
					new SbgInternationalPayeeBackendDTO();

					SbgInternationalPayeeBackendDTO internationalPayeeBackendDTO;
					LOG.debug("###### InternationalPayeeResourceImplExtn 02");
					try {
						internationalPayeeBackendDTO = JSONUtils.parse((new JSONObject(inputParams)).toString(), SbgInternationalPayeeBackendDTO.class);

						LOG.debug("###### InternationalPayeeResourceImplExtn internationalPayeeBackendDTO:"+"/email:"+internationalPayeeBackendDTO.getEmail()
						+"/state:"+internationalPayeeBackendDTO.getState()+"/residencyStatus:"+internationalPayeeBackendDTO+"/addressline2:"+internationalPayeeBackendDTO.getAddressLine2()
						+"/city:"+internationalPayeeBackendDTO.getCity());
						LOG.debug("###### InternationalPayeeResourceImplExtn 03");
						internationalPayeeBackendDTO.setUserId(createdBy);
					} catch (IOException var24) {
						LOG.error("Error occured while fetching the input params: " + var24);
						return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
					}

					if (!internationalPayeeBackendDTO.isValidInput()) {
						LOG.error("Not a valid input");
						return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
					} else if (!this._isUniquePayeeForEdit(internationalPayeeBackendDTO, backendDTOs.get(0), addedCifsMap, associatedCifsMap, editedCifsMap, editedCifs, request)) {
						LOG.error("Duplicate Payee or Error occured while checking for uniqueness.");
						return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
					} else {
						LOG.debug("###### InternationalPayeeResourceImplExtn 04");
						inputParams.values().removeAll(Collections.singleton((Object)null));
						if (inputParams.size() > 2) {
							internationalPayeeBackendDTO = (SbgInternationalPayeeBackendDTO) payeeBackendDelegatelocal.editPayee(internationalPayeeBackendDTO, request.getHeaderMap(), request);
							LOG.debug("###### InternationalPayeeResourceImplExtn 08");
							if (internationalPayeeBackendDTO == null) {
								LOG.error("Error occured while updating payee at backend");
								return ErrorCodeEnum.ERR_12055.setErrorCode(new Result());
							}

							if (internationalPayeeBackendDTO.getDbpErrMsg() != null && !internationalPayeeBackendDTO.getDbpErrMsg().isEmpty()) {
								return ErrorCodeEnum.ERR_00000.setErrorCode(result, internationalPayeeBackendDTO.getDbpErrMsg());
							}
						}

						Iterator var19 = removedCifsMap.entrySet().iterator();

						Map.Entry contractCif;
						List coreCustomerIds;
						int j;
						while(var19.hasNext()) {
							contractCif = (Map.Entry)var19.next();
							internationalPayeeDTO.setContractId((String)contractCif.getKey());
							coreCustomerIds = (List)contractCif.getValue();

							for(j = 0; j < coreCustomerIds.size(); ++j) {
								internationalPayeeDTO.setCif((String)coreCustomerIds.get(j));
								this.payeeDelegate.deletePayeeAtDBX(internationalPayeeDTO);
							}
						}

						internationalPayeeDTO.setCreatedBy(createdBy);
						var19 = addedCifsMap.entrySet().iterator();

						while(var19.hasNext()) {
							contractCif = (Map.Entry)var19.next();
							internationalPayeeDTO.setContractId((String)contractCif.getKey());
							coreCustomerIds = (List)contractCif.getValue();

							for(j = 0; j < coreCustomerIds.size(); ++j) {
								internationalPayeeDTO.setCif((String)coreCustomerIds.get(j));
								this.payeeDelegate.createPayeeAtDBX(internationalPayeeDTO);
							}
						}

						try {
							JSONObject requestObj = new JSONObject(internationalPayeeBackendDTO);
							result = JSONToResult.convert(requestObj.toString());
							return result;
						} catch (JSONException var23) {
							LOG.error("Error occured while converting the response to Result: ", var23);
							return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
						}
					}
				}
			}
		}
	}

	private Map<String, List<String>> _getAssociatedCifsMap(List<InternationalPayeeDTO> payeeDTOs, DataControllerRequest request) {
		Set<String> authorizedCifs = this.authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(Arrays.asList("INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT"), request.getHeaderMap(), request);
		Map<String, List<String>> associatedCifsMap = new HashMap();

		for(int i = 0; i < payeeDTOs.size(); ++i) {
			if (authorizedCifs.contains(((InternationalPayeeDTO)payeeDTOs.get(i)).getCif())) {
				if (associatedCifsMap.containsKey(((InternationalPayeeDTO)payeeDTOs.get(i)).getContractId())) {
					List<String> cifs = new ArrayList((Collection)associatedCifsMap.get(((InternationalPayeeDTO)payeeDTOs.get(i)).getContractId()));
					cifs.add(((InternationalPayeeDTO)payeeDTOs.get(i)).getCif());
					associatedCifsMap.put(((InternationalPayeeDTO)payeeDTOs.get(i)).getContractId(), cifs);
				} else {
					associatedCifsMap.put(((InternationalPayeeDTO)payeeDTOs.get(i)).getContractId(), Arrays.asList(((InternationalPayeeDTO)payeeDTOs.get(i)).getCif().split(",")));
				}
			}
		}

		return associatedCifsMap;
	}

	private void _setAddedAndRemovedCifMaps(Map<String, List<String>> inputCifsMap, Map<String, List<String>> addedCifsMap, Map<String, List<String>> removedCifsMap, Map<String, List<String>> associatedCifsMap) {
		Iterator var5 = inputCifsMap.entrySet().iterator();

		Map.Entry contractCif;
		while(var5.hasNext()) {
			contractCif = (Map.Entry)var5.next();
			if (associatedCifsMap.containsKey(contractCif.getKey())) {
				List<String> associatedCifs = (List)associatedCifsMap.get(contractCif.getKey());
				List<String> inputCifs = (List)contractCif.getValue();
				List<String> addedCifs = new ArrayList(inputCifs);
				addedCifs.removeAll(associatedCifs);
				if (addedCifs.size() > 0) {
					addedCifsMap.put(contractCif.getKey().toString(), addedCifs);
				}

				List<String> removedCifs = new ArrayList(associatedCifs);
				removedCifs.removeAll(inputCifs);
				if (removedCifs.size() > 0) {
					removedCifsMap.put(contractCif.getKey().toString(), removedCifs);
				}
			} else {
				addedCifsMap.put(contractCif.getKey().toString(), (List<String>) contractCif.getValue());
			}
		}

		var5 = associatedCifsMap.entrySet().iterator();

		while(var5.hasNext()) {
			contractCif = (Map.Entry)var5.next();
			if (!inputCifsMap.containsKey(contractCif.getKey())) {
				removedCifsMap.put(contractCif.getKey().toString(), (List<String>) contractCif.getValue());
			}
		}

	}

	private boolean _isUniquePayeeForEdit(SbgInternationalPayeeBackendDTO inputDTO, InternationalPayeeBackendDTO backendDTO, Map<String, List<String>> addedCifsMap, Map<String, List<String>> associatedCifsMap, Map<String, List<String>> editedCifsMap, String editedCifs, DataControllerRequest request) {
		boolean validationFieldsChanged = false;
		String backendIban = backendDTO.getIban();
		if (!org.apache.commons.lang.StringUtils.isBlank(inputDTO.getIban()) && !inputDTO.getIban().equals(backendIban)) {
			if (!validationFieldsChanged) {
			}

			validationFieldsChanged = true;
		} else {
			inputDTO.setIban(backendIban);
			validationFieldsChanged = validationFieldsChanged;
		}

		String backendAccountNumber = backendDTO.getAccountNumber();
		if (!org.apache.commons.lang.StringUtils.isBlank(inputDTO.getAccountNumber()) && !inputDTO.getAccountNumber().equals(backendAccountNumber)) {
			if (!validationFieldsChanged) {
			}

			validationFieldsChanged = true;
		} else {
			inputDTO.setAccountNumber(backendAccountNumber);
			validationFieldsChanged = validationFieldsChanged;
		}

		String backendSwiftCode = backendDTO.getSwiftCode();
		if (!org.apache.commons.lang.StringUtils.isBlank(inputDTO.getSwiftCode()) && !inputDTO.getSwiftCode().equals(backendSwiftCode)) {
			if (!validationFieldsChanged) {
			}

			validationFieldsChanged = true;
		} else {
			inputDTO.setSwiftCode(backendSwiftCode);
			validationFieldsChanged = validationFieldsChanged;
		}

		if (validationFieldsChanged) {
			if (org.apache.commons.lang.StringUtils.isBlank(editedCifs)) {
				if (!associatedCifsMap.isEmpty() && !this._isUniquePayee(request, inputDTO, associatedCifsMap)) {
					return false;
				}
			} else if (!editedCifsMap.isEmpty() && !this._isUniquePayee(request, inputDTO, editedCifsMap)) {
				return false;
			}
		} else if (org.apache.commons.lang.StringUtils.isNotBlank(editedCifs) && !addedCifsMap.isEmpty() && !this._isUniquePayee(request, inputDTO, addedCifsMap)) {
			return false;
		}

		return true;
	}
}
