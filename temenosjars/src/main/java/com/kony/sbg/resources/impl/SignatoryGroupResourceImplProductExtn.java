package com.kony.sbg.resources.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;
import com.temenos.dbx.product.signatorygroupservices.resource.impl.SignatoryGroupResourceImpl;

public class SignatoryGroupResourceImplProductExtn extends SignatoryGroupResourceImpl {
	private static final Logger LOG = LogManager.getLogger(SignatoryGroupResourceImplProductExtn.class);
	
	@Override
	public Result createSignatoryGroup(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		LOG.debug("entry ------------>createSignatoryGroup()");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = new HashMap<String, Object>();
		Result result = new Result();

		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(SignatoryGroupBusinessDelegate.class);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		String userfullName = CustomerSession.getCustomerCompleteName(customer);
		String currentdate = HelperMethods.getFormattedTimeStamp(new Date(), "yyyy-MM-dd HH:mm:ss.S");
		LOG.debug("SignatoryGroupResourceImplProductExtn.createSignatoryGroup() ---> Customer Map in product layer: "+customer);

		JSONObject signatoryGroup = null;
		String coreCustomerId = inputParams.get(Constants.CORECUSTOMERID) == null ? ""
				: inputParams.get(Constants.CORECUSTOMERID).toString();
		String contractId = inputParams.get(Constants.CONTRACTID) == null ? ""
				: inputParams.get(Constants.CONTRACTID).toString();
		String signatoryGroupName = inputParams.get(Constants.SIGNATORYGROUPNAME) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPNAME).toString();
		String signatoryGroupDescription = inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION).toString();
		JSONArray signatories = null;

		if (!CustomerSession.IsAPIUser(customer)) {
			if (StringUtils.isNotBlank(contractId) && StringUtils.isNotBlank(coreCustomerId)) {
				boolean isValid = signatoryGroupBusinessDelegate.checkContractCorecustomer(contractId, coreCustomerId,
						userId, headersMap);
				if (!isValid) {
					return ErrorCodeEnum.ERR_21030.setErrorCode(new Result());
				}
			}
		}

		String sigArray = inputParams.get(Constants.SIGNATORIES) == null ? ""
				: inputParams.get(Constants.SIGNATORIES).toString();
		signatories = new JSONArray(sigArray);

		if (StringUtils.isBlank(coreCustomerId)) {
			return ErrorCodeEnum.ERR_21025.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(contractId)) {
			return ErrorCodeEnum.ERR_21024.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(signatoryGroupName)) {
			return ErrorCodeEnum.ERR_21022.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(sigArray)) {
			return ErrorCodeEnum.ERR_21026.setErrorCode(new Result());
		}

		if (containSpecialChars(signatoryGroupName) || containSpecialChars(signatoryGroupDescription)) {
			return ErrorCodeEnum.ERR_21023.setErrorCode(new Result());
		}

		signatoryGroupName = signatoryGroupName.substring(0, Math.min(signatoryGroupName.length(), 50));
		signatoryGroupDescription = signatoryGroupDescription.substring(0,
				Math.min(signatoryGroupDescription.length(), 200));

		boolean isNameDuplicate = signatoryGroupBusinessDelegate.isGroupNameDuplicate(signatoryGroupName,
				coreCustomerId, contractId, headersMap);
		if (!isNameDuplicate) {
			String customerId;
			JSONObject record;
			boolean isUserThere = false;
			boolean isSignatoryAlreadyPresentInAnotherGroup = false;
			for (int i = 0; i < signatories.length(); i++) {
				isUserThere = false;
				customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();
				List<SignatoryGroupDTO> signatoryGroups = signatoryGroupBusinessDelegate
						.getSignatoryUsers(coreCustomerId, contractId, headersMap, request);
				JSONArray rulesJSONArr = new JSONArray(signatoryGroups);
				for (int j = 0; j < rulesJSONArr.length(); j++) {
					record = rulesJSONArr.getJSONObject(j);
					if (record.get("userId").toString().equalsIgnoreCase(customerId)) {
						isUserThere = true;
						break;
					}
				}
				if (!isUserThere) {
					return ErrorCodeEnum.ERR_21018.setErrorCode(new Result());
				}

				isSignatoryAlreadyPresentInAnotherGroup = signatoryGroupBusinessDelegate
						.isSignatoryAlreadyPresentInAnotherGroup(customerId, coreCustomerId, contractId, headersMap);
				if (isSignatoryAlreadyPresentInAnotherGroup) {
					return ErrorCodeEnum.ERR_21021.setErrorCode(new Result());
				}
			}

			LOG.debug("SignatoryGroupResourceImplProductExtn.createSignatoryGroup() ---> userId: "+userId+"; contractId: "+contractId+"; coreCustomerId: "+coreCustomerId);
			
			try {
				signatoryGroup = signatoryGroupBusinessDelegate.createSignatoryGroup(signatoryGroupName,
						signatoryGroupDescription, coreCustomerId, contractId, signatories, userId);
			} catch (Exception e1) {
				LOG.error("Error while creating SignatoryGroupDTO from SignatoryGroupBusinessDelegate : " + e1);
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
			}
			try {
				result = JSONToResult.convert(signatoryGroup.toString());
				result.addParam(Constants.CREATEDBY, userfullName);
				result.addParam(Constants.CREATEDON, currentdate);				
				this._logSignatoryGroupStatus(request,response,result);			
				return result;
			} catch (Exception e) {
				LOG.error("Error while converting response SignatoryGroupDTO to result " + e);
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
			}
		} else {
			return ErrorCodeEnum.ERR_21020.setErrorCode(new Result());
		}
	}

	@Override
	public Result updateSignatoryGroup(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		LOG.debug("entry ------------>updateSignatoryGroup()");
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = new HashMap<String, Object>();
		Result result = new Result();

		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(SignatoryGroupBusinessDelegate.class);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);

		JSONObject signatoryGroup = null;

		String signatoryGroupId = inputParams.get("signatoryGroupId") == null ? ""
				: inputParams.get("signatoryGroupId").toString();
		if (StringUtils.isBlank(signatoryGroupId)) {
			return ErrorCodeEnum.ERR_21027.setErrorCode(new Result());
		}

		String signatoryGroupDescription = "";
		String signatoryGroupName = "";

		SignatoryGroupDTO signatoryDTO = signatoryGroupBusinessDelegate.fetchSignatoryGroupDetails(signatoryGroupId,
				headersMap);
		if (signatoryDTO == null) {
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}

		String contractId = signatoryDTO.getContractId();
		String coreCustomerId = signatoryDTO.getCoreCustomerId();

		signatoryGroupDescription = inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION).toString();

		if (inputParams.containsKey(Constants.SIGNATORYGROUPNAME)) {
			signatoryGroupName = inputParams.get(Constants.SIGNATORYGROUPNAME).toString();
		}
		if (StringUtils.isBlank(signatoryGroupName)) {
			return ErrorCodeEnum.ERR_21022.setErrorCode(new Result());
		}

		if (containSpecialChars(signatoryGroupName) || containSpecialChars(signatoryGroupDescription)) {
			return ErrorCodeEnum.ERR_21023.setErrorCode(new Result());
		}

		signatoryGroupName = signatoryGroupName.substring(0, Math.min(signatoryGroupName.length(), 50));
		signatoryGroupDescription = signatoryGroupDescription.substring(0,
				Math.min(signatoryGroupDescription.length(), 200));

		List<SignatoryGroupDTO> signatoryList = new ArrayList<>();
		try {
			signatoryList = signatoryGroupBusinessDelegate.fetchSignatoryGroups(coreCustomerId, contractId, headersMap);
		} catch (Exception e) {
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}

		if (_isDuplicateGroupName(signatoryList, signatoryGroupId, signatoryGroupName)) {
			return ErrorCodeEnum.ERR_21020.setErrorCode(new Result());
		}

		JSONArray signatories = null;
		String sigArray = inputParams.get(Constants.SIGNATORIES) == null ? "[]"
				: inputParams.get(Constants.SIGNATORIES).toString();
		signatories = new JSONArray(sigArray);
		String customerId;
		JSONObject record;
		boolean isUserThere = false;
		for (int i = 0; i < signatories.length(); i++) {
			isUserThere = false;
			customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();
			List<SignatoryGroupDTO> signatoryGroups = null;
			if (signatories.getJSONObject(i).has("isUserRemoved")
					&& signatories.getJSONObject(i).get("isUserRemoved").toString().equalsIgnoreCase("true")) {
				continue;
			}
			signatoryGroups = signatoryGroupBusinessDelegate.getSignatoryUsers(coreCustomerId, contractId, headersMap,
					request);
			JSONArray rulesJSONArr = new JSONArray(signatoryGroups);
			for (int j = 0; j < rulesJSONArr.length(); j++) {
				record = rulesJSONArr.getJSONObject(j);
				if (record.get("userId").toString().equalsIgnoreCase(customerId)) {
					isUserThere = true;
					break;
				}
			}
			if (!isUserThere) {
				return ErrorCodeEnum.ERR_21018.setErrorCode(new Result());
			}
		}

		try {
			signatoryGroup = signatoryGroupBusinessDelegate.updateSignatoryGroup(signatoryGroupId, signatoryGroupName,
					signatoryGroupDescription, coreCustomerId, contractId, signatories, userId);
		} catch (Exception e1) {
			LOG.error("Error while creating SignatoryGroupDTO from SignatoryGroupBusinessDelegate : " + e1);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		try {
			signatoryGroup.put("status", "true");
			signatoryGroup.put("signatoryGroupId", signatoryGroupId);
			result = JSONToResult.convert(signatoryGroup.toString());
			this._logSignatoryGroupStatus(request,response,result);	
			return result;
		} catch (Exception e) {
			LOG.error("Error while converting response SignatoryGroupDTO to result " + e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}

	}

	@Override
	public Result deleteSignatoryGroup(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		LOG.debug("entry ------------>deleteSignatoryGroup()");
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = new HashMap<>();
		Result result = new Result();

		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(SignatoryGroupBusinessDelegate.class);

		String signatoryGroupId = inputParams.get(Constants.SIGNATORYGROUPID) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPID).toString();
		SignatoryGroupDTO signatoryDTO = signatoryGroupBusinessDelegate.fetchSignatoryGroupDetails(signatoryGroupId,
				headersMap);
		if (signatoryDTO == null) {
			return ErrorCodeEnum.ERR_20042.setErrorCode(new Result());
		}
		boolean eligibility = signatoryGroupBusinessDelegate.isEligibleForDelete(signatoryGroupId);
		if (!eligibility) {
			return ErrorCodeEnum.ERR_29031.setErrorCode(new Result());
		}
		Boolean deleted;
		try {
			deleted = signatoryGroupBusinessDelegate.deleteSignatoryGroup(signatoryGroupId, headersMap);
		} catch (Exception e1) {
			LOG.error("Error while deleting SignatoryGroup from SignatoryGroupBusinessDelegate : " + e1);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		try {
			result = new Result();
			result.addParam("status", deleted.toString());
			this._logSignatoryGroupStatus(request,response,result);	
			return result;
		} catch (Exception e) {
			LOG.error("Error while converting response to result " + e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
	}

	private boolean containSpecialChars(String name) {
		Pattern regex = Pattern.compile("[+=\\\\|<>^*%]");
		if (regex.matcher(name).find()) {
			return true;
		}
		return false;
	}

	private boolean _isDuplicateGroupName(List<SignatoryGroupDTO> signatoryList, String signatoryId,
			String signatoryName) {
		for (SignatoryGroupDTO sig : signatoryList) {
			if (sig.getSignatoryGroupId() != null && !sig.getSignatoryGroupId().equals(signatoryId)) {
				if (sig.getSignatoryGroupName().equals(signatoryName)) {
					return true;
				}
			}
		}
		return false;
	}
	public void _logSignatoryGroupStatus(DataControllerRequest request,DataControllerResponse response,Result result) {
		LOG.debug("entry ------------>_logSignatoryGroupStatus()");
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		JsonObject customParams = new JsonObject();
		String eventType = "APPROVAL_MATRIX";
		String eventSubType = "UPDATE_SIGNATORY_GROUP";
		String producer = "SignatoryObject/operations/SignatoryGroup/updateSignatoryGroup";
		String statusId = "Success";
			
		EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer,statusId, null,
				CustomerSession.getCustomerId(customer), null, customParams);
	}

	@Override
	public Result fetchSignatoryGroupDetails(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
        Map<String, Object> headersMap = new HashMap<String, Object>();
		Result result = new Result();
		Map<String,Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		
		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(SignatoryGroupBusinessDelegate.class);

		String signatoryGroupId = inputParams.get(Constants.SIGNATORYGROUPID)== null ? "" :inputParams.get(Constants.SIGNATORYGROUPID).toString() ;
		if(StringUtils.isBlank(signatoryGroupId)) {
			return ErrorCodeEnum.ERR_21027.setErrorCode(new Result());
		}
		List<SignatoryGroupDTO> signatoryGroupDetail=null;
		try{
			signatoryGroupDetail = signatoryGroupBusinessDelegate.fetchSignatoryDetails(signatoryGroupId,headersMap);	
			if(signatoryGroupDetail==null) {
				return ErrorCodeEnum.ERR_21028.setErrorCode(new Result());
			}
			if(signatoryGroupDetail.isEmpty()) {
				return ErrorCodeEnum.ERR_21031.setErrorCode(new Result());
			}
			if( !CustomerSession.IsAPIUser(customer) ) {
				boolean valid= hasAccessToCoreCustomer(userId,signatoryGroupDetail.get(0).getCoreCustomerId(),headersMap);
				if(!valid) {
					return ErrorCodeEnum.ERR_21032.setErrorCode(new Result());
				}
			}
			JSONObject resultObj=convertDetails(signatoryGroupDetail);
			result = JSONToResult.convert(resultObj.toString());
			return result;
		}catch(Exception e) {
			LOG.error("Error while converting response SignatoryGroupDetailDTO to result " + e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
	}

	 private boolean hasAccessToCoreCustomer(String userId, String coreCustomerId, Map<String,Object> headersMap) {
		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class).
													getBusinessDelegate(SignatoryGroupBusinessDelegate.class);
		JSONArray corecustomers= new JSONArray();
		try {
			corecustomers = signatoryGroupBusinessDelegate.getCorecustomersForUser(userId, headersMap);
			if(corecustomers==null) {
				return Boolean.FALSE;
			}
		}catch (Exception e) {
			return Boolean.FALSE;
		}
		
		boolean valid = Boolean.FALSE;
		for( int i=0 ; i< corecustomers.length();i++) {
			if(corecustomers.getString(i).equalsIgnoreCase(coreCustomerId)) {
				valid= Boolean.TRUE;
			}
		};
		return valid;	
	}

	private JSONObject convertDetails(List<SignatoryGroupDTO> signatories) {
		SignatoryGroupDTO dto= new SignatoryGroupDTO();
		
		JSONObject signatorydetail = new JSONObject();
		dto =signatories.get(0);
		signatorydetail.put("signatoryGroupId", dto.getSignatoryGroupId());
		signatorydetail.put("signatoryGroupName", dto.getSignatoryGroupName());
		signatorydetail.put("signatoryGroupDescription", dto.getSignatoryGroupDescription());
		signatorydetail.put("coreCustomerId", dto.getCoreCustomerId());
		signatorydetail.put("coreCustomerName", dto.getCoreCustomerName());
		signatorydetail.put("createdBy", StringUtils.isBlank(dto.getCreatedby()) ? "-" : dto.getCreatedby().trim());
		signatorydetail.put("createdOn", dto.getCreatedts());
		signatorydetail.put("lastModified", dto.getLastmodifiedts());
		JSONArray array = new JSONArray();
				
		for(SignatoryGroupDTO sigDto : signatories) {
			if(sigDto.getCustomerSignatoryGroupId()!=null) {
				JSONObject signatory = new JSONObject();
				signatory.put("signatoryId", sigDto.getCustomerSignatoryGroupId());
				signatory.put("customerId", sigDto.getCustomerId());
				signatory.put("customerName", sigDto.getFullName().trim());
				signatory.put("userName", sigDto.getUserName());
				signatory.put("role", sigDto.getCustomerRole());
				signatory.put("addedts", sigDto.getSignatoryaddedts());			
				
				array.put(signatory);
			}
		}
		signatorydetail.put("signatories", array);   
		return signatorydetail;
	}
}
