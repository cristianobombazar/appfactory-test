package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.sbg.backend.api.PaymentFeedbackBackendDelegate;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class PaymentFeedbackBackendDelegateImpl implements PaymentFeedbackBackendDelegate {

	private static final Logger LOG = Logger.getLogger(PaymentFeedbackBackendDelegateImpl.class);

	@Override
	public JSONObject maintainTranferStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_CREATE;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_CREATE + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in maintainTranferStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in maintainTranferStatusHistory : " + e);
			return null;
		}

		return responseObj;

	}

	@Override
	public JSONObject getInternationalFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getInternationalFundTransfers : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getInternationalFundTransfers : " + e);
			return null;
		}

		return responseObj;

	}

	@Override
	public JSONObject getStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_GET;
		JSONObject responseObj;

		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getTransferStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getTransferStatusHistory : " + e);
			return null;
		}

		return responseObj;

	}

	@Override
	public Result getMyAccessAllUsers(Map<String, Object> svcInputParams, Map<String, String> svcHeaders,
			DataControllerRequest dcRequest) {
		LOG.debug("getMyAccessAllUsers::::");
		String Url = SbgURLConstants.GET_MYACCESS_USERS;
		LOG.debug(" ##getMyAccessAllUsers :" + Url);
		Result result = new Result();
		try {
			LOG.debug("getMyAccessAllUsers::::1");
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, svcInputParams, svcHeaders, Url);
			LOG.debug(" ##getMyAccessAllUsers :" + result);

		} catch (Exception e) {
			LOG.debug(" getMyAccessAllUsers" + e);
			return SbgErrorCodeEnum.ERR_100053.setErrorCode(result);
		}
		return result;
	}

	@Override
	public Result updateMyAccessUser(Map<String, Object> inputParams, Map<String, String> headers,
			DataControllerRequest dcRequest) throws Exception {
		String Url = SbgURLConstants.UPDATE_MYACCESS_USER;
		LOG.debug(" ##updateMyAccessUser :" + Url);
		Result result = new Result();
		try {
			result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headers, Url);
			LOG.debug("updateMyAccessUser:" + result);
		} catch (Exception e) {
			LOG.debug("updateMyAccessUser");
			return SbgErrorCodeEnum.ERR_100020.setErrorCode(result);
		}
		return result;
	}

	@Override
	public JSONObject maintainDomesticTranferStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_CREATE;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_CREATE + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in maintainDomesticTranferStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in maintainDomesticTranferStatusHistory : " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject getInterBankFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getInterBankFundTransfers : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getInterBankFundTransfers : " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject getDomesticStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_GET;
		JSONObject responseObj;

		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getDomesticStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getDomesticStatusHistory : " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject maintainIATStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_CREATE;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_CREATE + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in maintainIATStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in maintainIATStatusHistory : " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject getOwnAccountFundTransfers(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET;
		JSONObject responseObj;
		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getOwnAccountFundTransfers : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getOwnAccountFundTransfers : " + e);
			return null;
		}

		return responseObj;
	}

	@Override
	public JSONObject getOwnAccountStatusHistory(HashMap<String, Object> requestParameters,
			HashMap<String, Object> requestHeaders) {
		String serviceName = SbgURLConstants.SERVICE_SBGCRUD;
		String operationName = SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_GET;
		JSONObject responseObj;

		try {
			String vendorResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			responseObj = new JSONObject(vendorResponse);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGCRUD + " Operation : "
					+ SbgURLConstants.OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_GET + ":: " + vendorResponse);

		} catch (JSONException e) {
			LOG.error("JSONException occurred in getTransferStatusHistory : " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Exception occurred in getTransferStatusHistory : " + e);
			return null;
		}

		return responseObj;
	}

}
