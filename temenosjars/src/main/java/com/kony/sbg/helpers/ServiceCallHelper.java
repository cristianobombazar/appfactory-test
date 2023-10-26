package com.kony.sbg.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.sbg.util.URLConstants;
import com.kony.sbg.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

@SuppressWarnings("deprecation")
public class ServiceCallHelper {

	private static LoggerUtil logger = new LoggerUtil(ServiceCallHelper.class);
	private static final String SEPARATOR = "/";

	private ServiceCallHelper() {
	}

	public static Result invokeServiceAndGetResultSBG(DataControllerRequest dcRequest, Map<String, Object> inputParams,
			Map<String, String> headerParams, String url) throws HttpCallException {
		String serviceURL = URLFinder.getPathUrl(url);
		if (StringUtils.isBlank(serviceURL)) {
			serviceURL = url;
		}
		String serviceName = getServiceName(serviceURL);
		String operationName = getOperationName(serviceURL, dcRequest);
		String objectName = getObjectName(serviceURL);
		try {
			Result result = DBPServiceInvocationWrapper.invokeServiceAndGetResult(serviceName, objectName,
					operationName, inputParams, convertMap(headerParams), dcRequest);
			printLog(objectName + "_" + serviceName + "_" + operationName, inputParams, headerParams, result, null);
			return result;
		} catch (Exception e) {
			logger.error("Exception while calling service " + URLFinder.getPathUrl(url), e);
		}
		return getExceptionMsgAsResult(url);
	}

	public static Result invokeServiceAndGetResult(DataControllerRequest dcRequest, Map<String, Object> inputParams,
			Map<String, String> headerParams, String url) throws HttpCallException {
		String serviceURL = URLFinder.getPathUrl(url);
		if (StringUtils.isBlank(serviceURL)) {
			serviceURL = url;
		}
		String serviceName = getServiceName(serviceURL);
		String operationName = getOperationName(serviceURL, dcRequest);
		String objectName = getObjectName(serviceURL);
		try {
			Result result = DBPServiceInvocationWrapper.invokeServiceAndGetResult(serviceName, objectName,
					operationName, inputParams, convertMap(headerParams), dcRequest);
			printLog(objectName + "_" + serviceName + "_" + operationName, inputParams, headerParams, result, null);
			return result;
		} catch (Exception e) {
			logger.error("Exception while calling service " + URLFinder.getPathUrl(url), e);
		}
		return getExceptionMsgAsResult(url);
	}

	public static String getServiceName(String serviceURL) {
		String basePart = "/services/data/v1/";
		if (serviceURL.contains(basePart)) {
			return serviceURL.substring(basePart.length(), serviceURL.indexOf("/", basePart.length()));
		}
		return serviceURL.substring(serviceURL.indexOf("/", 1) + 1, serviceURL.lastIndexOf("/"));
	}

	public static String getOperationName(String serviceURL, DataControllerRequest dcRequest) {
		String operationName = StringUtils.substringAfterLast(serviceURL, SEPARATOR);
		if (operationName.contains("{schema_name}_")) {
			operationName = operationName.replace("{schema_name}", getDatabaseSchemaName(dcRequest));
		}
		return StringUtils.isBlank(operationName) ? null : operationName;
	}

	public static String getObjectName(String serviceURL) {
		String basePart = "/services/data/v1/";
		if (serviceURL.contains(basePart)) {
			int start = 0;
			if (serviceURL.contains("/operations/")) {
				start = serviceURL.indexOf("/operations/") + "/operations/".length();
			} else {
				start = serviceURL.indexOf("/objects/") + "/objects/".length();
			}
			return serviceURL.substring(start, serviceURL.lastIndexOf("/"));
		}
		return null;
	}

	private static Map<String, Object> convertMap(Map<String, String> map) {
		Map<String, Object> resultMap = new HashMap<>();
		if (map == null || map.isEmpty()) {
			return resultMap;
		}
		for (Entry<String, String> entry : map.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}

	private static void printLog(String URL, @SuppressWarnings("rawtypes") Map inputParams,
			@SuppressWarnings("rawtypes") Map headerParams, Result result, String response) {

		if (logger == null) {
			logger = new LoggerUtil(ServiceCallHelper.class);
		}
		if (inputParams != null) {
			logger.debug("InputParams for call " + URL + " : " + inputParams);
		}
		if (headerParams != null) {
			logger.debug("HeaderParams for call " + URL + " : " + headerParams);
		}
		if (result != null) {
			logger.debug("Response from call " + URL + " : " + ResultToJSON.convert(result));
		} else {
			logger.debug("Response from call " + URL + " : " + response);
		}
	}

	private static Result getExceptionMsgAsResult(String serviceURL) {
		Result result = new Result();
		StringBuilder message = new StringBuilder();
		message.append("Exception occured while invoking service with [ServiceId_ObjectId_OperationId] [")
				.append(serviceURL).append("]");
		result.addParam("errmsg", message.toString());
		return result;
	}

	private static CharSequence getDatabaseSchemaName(DataControllerRequest dcRequest) {
		return URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest);
	}
}