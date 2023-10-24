package com.kony.sbg.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.backend.api.PayeeBackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.util.ObjectConverterForFetchPayees;
import com.kony.sbg.util.SBGConstants;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.ExternalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.resource.api.InterBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InternationalPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.IntraBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.impl.ExternalPayeeResourceImpl;
public class ExternalPayeeResourceImplExtn extends ExternalPayeeResourceImpl{
	
	private static final Logger LOG = LogManager.getLogger(ExternalPayeeResourceImplExtn.class);
	
	ExternalPayeeBusinessDelegate externalPayeeDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(ExternalPayeeBusinessDelegate.class);
//	
//	ExternalPayeeBackendDelegate externalPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
//			.getFactoryInstance(BackendDelegateFactory.class).getBackendDelegate(ExternalPayeeBackendDelegate.class);
//
//	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
//			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

	@Override
    public Result createPayee(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		LOG.debug("ExternalPayeeResourceImplExtn.createPayee ---> ");
		
		String isInternationalAccount = (String) inputParams.get("isInternationalAccount");
		String isSameBankAccount = (String) inputParams.get("isSameBankAccount");
		LOG.debug("ExternalPayeeResourceImplExtn.createPayee ---> isSameBankAccount:"+isSameBankAccount+"; isInternationalAccount:"+isInternationalAccount);
		LOG.debug("@@ ExternalPayeeResourceImplExtn.createPayee ---> beneCode:"+inputParams.get("beneCode")+"; entityType:"+inputParams.get("entityType"));   		
		LOG.debug("@@ ExternalPayeeResourceImplExtn.createPayee ---> residencyStatus:"+inputParams.get("residencyStatus"));

		if("false".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InterBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InterBankPayeeResource.class);
            result  = payeeResource.createPayee(methodId, inputArray, request, response);
		}
		else if("true".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InternationalPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InternationalPayeeResource.class);
			result  = payeeResource.createPayee(methodId, inputArray, request, response);

		}
		else if("true".equals(isSameBankAccount) && "false".equals(isInternationalAccount)) {
        	IntraBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(IntraBankPayeeResource.class);
            result  = payeeResource.createPayee(methodId, inputArray, request, response);
		}
		else {
			LOG.error("ExternalPayeeResourceImplExtn ---> Payee Type is not valid.");
			return SbgErrorCodeEnum.ERR_100004.setErrorCode(new Result());
		}
		return result;
    }
	@Override
	public Result fetchAllMyPayees(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		//Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		//String userId = CustomerSession.getCustomerId(customer);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
   		String userId = CustomerSession.getCustomerId(customer);
   		SbgExternalPayeeBackendDTO payeeBackendDTO = ObjectConverterForFetchPayees.convert2DTO(inputParams);
		payeeBackendDTO.setUserId(userId);
		PayeeBackendDelegate payeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(PayeeBackendDelegate.class);
		
		result= payeeBackendDelegate.fetchPayeesFromDBXOrch(payeeBackendDTO, customer, request);
		if(result == null) {
			LOG.error(" @@ no records found");
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, payeeBackendDTO.getDbpErrMsg());
		}
		LOG.error(" @@ jsonArray: "+result);	
		List<SbgExternalPayeeBackendDTO> externalPayeeDTOs = new ArrayList<SbgExternalPayeeBackendDTO>();
		
		JSONArray jsonArray =CommonUtils.convertDatasetToJSONArray(result.getDatasetById("externalaccount"));
		
			try {
				externalPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), SbgExternalPayeeBackendDTO.class);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			LOG.error(" @@ jsonArray: "+jsonArray);	

//		//Applying filters like offset,limit,sort etc
		FilterDTO filterDTO = new FilterDTO();
		//String sortParam="bankName";
		try {
			filterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
		LOG.error("Exception occurred while fetching params: ",e);
          return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}		
		externalPayeeDTOs = filterDTO.filter(externalPayeeDTOs);
		LOG.error(" @@ externalPayeeDTOs: "+externalPayeeDTOs);		
		try {
	        JSONArray records = new JSONArray(externalPayeeDTOs);
	        JSONObject resultObject = new JSONObject();
	        
	        resultObject.put("externalaccount",records);
	        result = JSONToResult.convert(resultObject.toString());
		}
        catch(Exception exp) {
            LOG.error("Exception occurred while converting DTO to result: ",exp);
            return ErrorCodeEnum.ERR_12048.setErrorCode(new Result());
        }
		return result;
	}

    @Override
    public Result deletePayee(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
    	@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		String isInternationalAccount = (String) inputParams.get("isInternationalAccount");
		String isSameBankAccount = (String) inputParams.get("isSameBankAccount");
		
		if("false".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InterBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InterBankPayeeResource.class);
            result  = payeeResource.deletePayee(methodId, inputArray, request, response);
		}
		else if("true".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InternationalPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InternationalPayeeResource.class);
			result  = payeeResource.deletePayee(methodId, inputArray, request, response);
			
		}
		else if("true".equals(isSameBankAccount) && "false".equals(isInternationalAccount)) {
        	IntraBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(IntraBankPayeeResource.class);
            result  = payeeResource.deletePayee(methodId, inputArray, request, response);
		}
		else {
			LOG.error("Payee Type is not valid.");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
    }

}
