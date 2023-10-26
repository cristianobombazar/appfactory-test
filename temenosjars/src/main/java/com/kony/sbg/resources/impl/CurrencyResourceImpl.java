package com.kony.sbg.resources.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.business.api.CurrencyBusinessDelegate;
import com.kony.sbg.resources.api.CurrencyResource;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CurrencyResourceImpl implements CurrencyResource {

	private static final Logger LOG = Logger.getLogger(CurrencyResourceImpl.class);

	@Override
	public Result getAllowedCurrencies(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		JSONObject requestPayload = new JSONObject();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		try {
			if (StringUtils.isNotBlank(inputParams.get("fromAccountNumber"))) {
				/*
				 * Fetch account details from session and write logic to find the BIC from Swift
				 * Code
				 */
				LOG.info("Account number is not empty: " + inputParams.get("fromAccountNumber"));
				String country = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE,
						dcRequest);
				String productCode = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_PRODUCT_CODE,
						dcRequest);
				requestPayload.put("Country", country);
				requestPayload.put("ProductCode", productCode);
				CurrencyBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(CurrencyBusinessDelegate.class);
				LOG.info("#####RequestPayload: " + requestPayload);
				result = businessDelegate.getAllowedCurrencies(requestPayload, dcRequest);
				LOG.info("#####Result in AllowedCurrencies ResourceImpl: " + result);
			} else {
				LOG.error("Account number is empty!");
				result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
			}

		} catch (Exception exp) {
			LOG.error("Error in CurrencyResourceImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
		}

		return result;
	}

	@Override
	public Result getDomesticCurrencies(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		JSONObject requestPayload = new JSONObject();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		try {
			if (StringUtils.isNotBlank(inputParams.get("fromAccountNumber"))) {
				/*
				 * Fetch account details from session and write logic to find the BIC from Swift
				 * Code
				 */
				LOG.info("Account number is not empty: " + inputParams.get("fromAccountNumber"));
				String country = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE,
						dcRequest);
				String productCode = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.DOM_SBG_HEADER_API_PRODUCT_CODE,
						dcRequest);

				requestPayload.put("Country", country);
				requestPayload.put("ProductCode", productCode);
				CurrencyBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(CurrencyBusinessDelegate.class);
				LOG.info("#####RequestPayload: " + requestPayload);
				result = businessDelegate.getDomesticCurrencies(requestPayload, dcRequest);
				LOG.info("#####Result in DomesticCurrencies ResourceImpl: " + result);
			} else {
				LOG.error("Account number is empty!");
				result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
			}

		} catch (Exception exp) {
			LOG.error("Error in CurrencyResourceImpl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100016.setErrorCode(result);
		}

		return result;
	}

}
