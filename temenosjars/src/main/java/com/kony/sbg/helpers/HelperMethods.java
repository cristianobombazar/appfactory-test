package com.kony.sbg.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import com.dbp.core.constants.DBPConstants;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.helpers.ServiceCallHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public final class HelperMethods {

	@SuppressWarnings("unchecked")
	public static Result callApiSBG(DataControllerRequest dcRequest, @SuppressWarnings("rawtypes") Map inputParams,
			Map<String, String> headerParams, String url) throws HttpCallException {
		return ServiceCallHelper.invokeServiceAndGetResultSBG(dcRequest, inputParams, headerParams, url);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getInputParamMap(Object[] inputArray) {
		return (HashMap<String, String>) inputArray[1];
	}

	public static String getFieldValue(Result result, String fieldName) {
		String id = "";
		if (HelperMethods.hasRecords(result)) {
			Dataset ds = result.getAllDatasets().get(0);
			id = getParamValue(ds.getRecord(0).getParam(fieldName));
		}
		return id;
	}

	public static boolean hasRecords(Result result) {
		if (hasError(result) || null == result.getAllDatasets() || result.getAllDatasets().isEmpty()) {
			return false;
		}
		Dataset ds = result.getAllDatasets().get(0);
		return null != ds && null != ds.getAllRecords() && ds.getAllRecords().size() > 0;
	}

	public static String getParamValue(Param p) {
		String value = "";
		if (null != p) {
			value = p.getValue();
		}
		return (null == value) ? "" : value;
	}

	public static boolean hasError(Result result) {
		boolean status = false;
		if (null == result || null != result.getParamByName(DBPUtilitiesConstants.VALIDATION_ERROR)
				|| null != result.getParamByName(DBPConstants.DBP_ERROR_CODE_KEY)) {
			status = true;
		}
		return status;
	}

	public static Map<String, String> getHeaders(DataControllerRequest dcRequest) {
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION,
				dcRequest.getHeader(DBPUtilitiesConstants.X_KONY_AUTHORIZATION));
		headerMap.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		return headerMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Result callGetApi(DataControllerRequest dcRequest, String filterQuery, Map<String, String> header,
			String url) throws HttpCallException {
		Map inputParams = new HashMap();

		inputParams.put(DBPUtilitiesConstants.FILTER, filterQuery);
		return callApi(dcRequest, inputParams, header, url);
	}

	@SuppressWarnings("unchecked")
	public static Result callApi(DataControllerRequest dcRequest, @SuppressWarnings("rawtypes") Map inputParams,
			Map<String, String> headerParams, String url) throws HttpCallException {
		return ServiceCallHelper.invokeServiceAndGetResult(dcRequest, inputParams, headerParams, url);
	}

	public static String getFieldValue(Record record, String fieldName) {
		String id = "";
		if (null != record) {
			id = getParamValue(record.getParam(fieldName));
		}
		return id;
	}

	public static String convertDateFormat(String dob, String to) throws ParseException {
		if (dob == null || "".equals(dob)) {
			return null;
		}
		Date date = getFormattedTimeStamp(dob);
		return HelperMethods.getFormattedTimeStamp(date, to);
	}

	private static SimpleDateFormat[] expectedFormats = new SimpleDateFormat[] {
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS"),
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd"),
			new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("dd MMM yy HH:mm") };

	public static Date getFormattedTimeStamp(String dt) {

		for (int i = 0; i < expectedFormats.length; i++) {
			try {
				return expectedFormats[i].parse(dt);
			} catch (Exception e) {
			}
		}
		return new Date();
	}

	public static String getFormattedTimeStamp(Date dt, String format) {
		String dtFormat = "yyyy-MM-dd'T'HH:mm:ss";
		if (StringUtils.isNotBlank(format)) {
			dtFormat = format;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(dtFormat);
		return formatter.format(dt);
	}

	static Map<String, String> customerTypes = null;

	public static Map<String, String> getCustomerTypes() {
		if (customerTypes == null) {
			customerTypes = new HashMap<>();

			customerTypes.put("Micro Business", "TYPE_ID_MICRO_BUSINESS");
			customerTypes.put("Micro Business Banking", "TYPE_ID_MICRO_BUSINESS");
			customerTypes.put("Prospect", "TYPE_ID_PROSPECT");
			customerTypes.put("Customer", "TYPE_ID_RETAIL");
			customerTypes.put("Retail Banking", "TYPE_ID_RETAIL");
			customerTypes.put("Small Business", "TYPE_ID_SMALL_BUSINESS");
			customerTypes.put("Small Business Banking", "TYPE_ID_SMALL_BUSINESS");
			customerTypes.put("Retail Customer", "TYPE_ID_RETAIL");
			customerTypes.put("Retail", "TYPE_ID_RETAIL");
			customerTypes.put("Business", "TYPE_ID_BUSINESS");
		}

		return customerTypes;
	}
}