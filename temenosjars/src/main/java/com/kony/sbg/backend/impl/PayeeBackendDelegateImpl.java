package com.kony.sbg.backend.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.backend.api.PayeeBackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.dto.SbgInternationalPayeeBackendDTO;
import com.kony.sbg.util.ObjectConverter;
import com.kony.sbg.util.ObjectConverterForFetchPayees;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;


public class PayeeBackendDelegateImpl implements PayeeBackendDelegate {

	final Logger LOG = LogManager.getLogger(PayeeBackendDelegateImpl.class);

	@Override
	public Result createPayee(SbgInternationalPayeeBackendDTO payeeBackendDTO, Map<String, Object> headerParams, DataControllerRequest dcRequest) {

		LOG.debug("@@ PayeeBackendDelegateImpl.createPayee ---> beneCode:"+payeeBackendDTO.getBeneCode()+"; entityType:"+payeeBackendDTO.getEntityType());

		Map<String, Object> requestParameters = ObjectConverter.convert2Map(payeeBackendDTO);

		String payeeid = HelperMethods.getRandomNumericString(8);
		requestParameters.put(SbgURLConstants.IBAN, requestParameters.get(SbgURLConstants.SMALL_IBAN));

		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();

		Result result = new Result();

		try {
			if(preProcess(requestParameters, dcRequest, result)) {
				requestParameters.put("Id", payeeid);
				result = CommonUtils.callIntegrationService(dcRequest, requestParameters, svcHeaders, SbgURLConstants.SERVICE_SBGCRUD,
						SbgURLConstants.OPERATION_EXTERNALACCOUNT_CREATE, false);
				result.addParam(new Param("PAYEE.ID", payeeid));
			}

		}
		catch (Exception e) {
			LOG.error("Caught exception at createPayeeAtBackend: " + e);
			return SbgErrorCodeEnum.ERR_100003.setErrorCode(new Result());
		}

		return result;
	}	

	@Override
	public Result deletePayee(SbgInternationalPayeeBackendDTO PayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest) 
	{
		String serviceName = ServiceId.INTERNATIONAL_PAYEE_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTERNATIONAL_PAYEE_BACKEND_DELETE;

		Map<String, Object> requestParameters = ObjectConverter.convert2Map(PayeeBackendDTO);

		requestParameters.put(SbgURLConstants.IBAN, requestParameters.get(SbgURLConstants.SMALL_IBAN));
		requestParameters.put(SbgURLConstants.ID, requestParameters.get(SbgURLConstants.CONST_ID));

		Result result = new Result();

		try {

			String url = "/services/"+serviceName+"/"+operationName;
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, requestParameters, HelperMethods.getHeaders(dcRequest), url);			
		}
		catch (Exception e) {
			LOG.error("Caught exception at deletePayeeAtBackend: " + e);
			return null;
		}

		return result;
	}

	private boolean preProcess(Map<String,Object> inputParams, DataControllerRequest dcRequest, Result result) throws HttpCallException {
		boolean status = true;

		/* if the user id is not fetched from reqParam then look for it in session */
		HelperMethods.removeNullValues(inputParams);
		String userFilter = "";
		/* fetch from input params */
		String userId = (String)inputParams.get(DBPInputConstants.USER_ID);
		String companyId = (String)inputParams.get(DBPUtilitiesConstants.COMPANYID);
		String isBusinessPayee = (String)inputParams.get(DBPUtilitiesConstants.IS_BUSINESS_PAYEE);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String createdOn = dateFormat.format(new Date());

		/* if input params does not have this fetch from session */
		if(StringUtils.isBlank(userId))
			userId = HelperMethods.getUserIdFromSession(dcRequest);
		if(StringUtils.isBlank(companyId))
			companyId = HelperMethods.getOrganizationIDForUser(userId,dcRequest);
		if(StringUtils.isBlank(isBusinessPayee))
			isBusinessPayee = "0";

		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put(DBPUtilitiesConstants.FILTER, "Customer_id eq " + userId);

		String isSameBankAccnt = (String)inputParams.get(DBPUtilitiesConstants.IS_SAME_BANK_ACCNT);
		String isIntAccount =    (String)inputParams.get(DBPUtilitiesConstants.IS_INT_ACCNT);
		inputParams.put(DBPUtilitiesConstants.USER_ID, userId);
		inputParams.put(DBPUtilitiesConstants.CREATED_ON, createdOn);
		if("1".equals(isBusinessPayee))
			inputParams.put(DBPUtilitiesConstants.P_ORGANIZATION_ID, companyId);

		if (StringUtils.isNotBlank((String)inputParams.get(DBPInputConstants.BANK_ID))) {
			inputParams.put(DBPUtilitiesConstants.BANK_ID, inputParams.get(DBPInputConstants.BANK_ID));
		}
		if (StringUtils.isNotBlank((String)inputParams.get(DBPInputConstants.USR_ACCNT))) {
			inputParams.put(DBPUtilitiesConstants.USR_ACCNT, inputParams.get(DBPInputConstants.USR_ACCNT));
		}
		if (!StringUtils.isNotBlank(isIntAccount)) {
			inputParams.put(DBPUtilitiesConstants.IS_SAME_BANK_ACCNT, "false");
		}
		if (!StringUtils.isNotBlank(isSameBankAccnt)) {
			inputParams.put(DBPUtilitiesConstants.IS_INT_ACCNT, "false");
		}

		String iBAN = (String) inputParams.get(SbgURLConstants.IBAN);
		if(StringUtils.isBlank(iBAN))
			iBAN = (String)inputParams.get(SbgURLConstants.SMALL_IBAN);
		if (StringUtils.isNotBlank(iBAN)) {
			inputParams.put(SbgURLConstants.IBAN, iBAN);
		}

		/* remove product related fields */
		inputParams.remove(SbgURLConstants.USER_ID);
		inputParams.remove(SbgURLConstants.COMPANYID);
		inputParams.remove(SbgURLConstants.ISBUSINESS);

		return status;
	}

	@Override
	public Result fetchPayeesFromDBXOrch(SbgExternalPayeeBackendDTO PayeeBackendDTO, Map<String, Object> headerParams,
			DataControllerRequest dcRequest) {
		String serviceName =SbgURLConstants.EXTERNAL_PAYEE_ORCH_SERVICE;
		String operationName = SbgURLConstants.EXTERNAL_PAYEE_ORCH_OPERATION;
		LOG.debug("@@ fetchPayeesFromDBXOrch. ServiceName"+serviceName+"operationNAme"+operationName);


		Result result=new Result();
		Map<String, Object> requestParameters = ObjectConverterForFetchPayees.convert2Map(PayeeBackendDTO);
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();

		try {
			result = CommonUtils.callIntegrationService(dcRequest, requestParameters, svcHeaders, serviceName,
					operationName, true);
			LOG.debug("@@ fetchPayeesFromDBXOrch. result"+result);
			LOG.debug("@@ fetchPayeesFromDBXOrch. list"+result.getAllDatasets());
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch payees: " + e);
			return SbgErrorCodeEnum.ERR_100006.setErrorCode(new Result());
			//return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeesFromDBXOrch: " + e);
			return SbgErrorCodeEnum.ERR_100006.setErrorCode(new Result());
			//return null;
		}
		return result;
	}


	@Override
	public Result fetchPayees(String filter,SbgExternalPayeeBackendDTO PayeeBackendDTO, 
			Map<String, Object> headerParams, DataControllerRequest dcRequest) {
		//List<SbgExternalPayeeBackendDTO> backendPayeeDTOs = new ArrayList<SbgExternalPayeeBackendDTO>();
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName =SbgURLConstants.OPERATION_EXTERNALACCOUNT_GET;
		LOG.error("@@ fetch payees: operationName ==" + operationName);
		HashMap<String, Object> svcHeaders = new HashMap<String, Object>();
		HashMap<String, Object> svcParameters = new HashMap<String, Object>();
		svcParameters.put(SbgURLConstants.FILTER,filter);
		Map<String, Object> requestParameters = ObjectConverterForFetchPayees.convert2Map(PayeeBackendDTO);		
		Result result = null;
		try {
			result = CommonUtils.callIntegrationService(dcRequest, svcParameters, svcHeaders, serviceName,
					operationName, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("Failed to fetch international payees: " + e);
			return SbgErrorCodeEnum.ERR_100015.setErrorCode(result);
		}
		return result;
	}



}
