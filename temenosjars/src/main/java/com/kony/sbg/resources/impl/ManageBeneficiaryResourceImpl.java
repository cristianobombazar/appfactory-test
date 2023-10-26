package com.kony.sbg.resources.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.business.api.ManageBeneficiaryBusinessDelegate;
import com.kony.sbg.resources.api.ManageBeneficiaryResource;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class ManageBeneficiaryResourceImpl implements ManageBeneficiaryResource {

	private static final Logger logger = Logger.getLogger(ManageBeneficiaryResourceImpl.class);

	@Override
	public Result checkBeneCodeExists(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException {
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		JSONObject requestPayload = new JSONObject();
		if (StringUtils.isNotBlank(inputParams.get("contractId"))
				&& StringUtils.isNotBlank(inputParams.get("corecustomerid"))
				&& StringUtils.isNotBlank(inputParams.get("benecode"))) {
			requestPayload.put("contractId", inputParams.get("contractId"));
			requestPayload.put("corecustomerid", inputParams.get("corecustomerid"));
			requestPayload.put("benecode", inputParams.get("benecode").toUpperCase());
		} else {
			logger.error("Missing values for mandatory parameters");
			return SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
		}

		try {
			ManageBeneficiaryBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ManageBeneficiaryBusinessDelegate.class);
			result = businessDelegate.checkBeneCodeExists(requestPayload, dcRequest);
			logger.info("checkBeneCodeExists Result: " + result.toString());
		} catch (Exception exp) {
			logger.error("Error in checkBeneCodeExists resource impl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100009.setErrorCode(result);
		}
		return result;
	}

	@Override
	public Result generateBeneficiaryDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		if (validateInputDetails(inputParams, dcRequest, result) == false) {
			logger.error("Basic validation failed for generate beneficiaries");
			return result;
		}

		try {
			ManageBeneficiaryBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ManageBeneficiaryBusinessDelegate.class);
			result = businessDelegate.generateBeneficiaryDetails(inputParams, dcRequest);
			logger.info("generateBeneficiaryDetails resource impl Result: " + result.toString());
		} catch (Exception exp) {
			logger.error("Error in generateBeneficiaryDetails resource impl: " + exp.getMessage());
			return SbgErrorCodeEnum.ERR_100013.setErrorCode(result);
		}

		return result;
	}

	protected boolean validateInputDetails(Map<String, String> inputParams, DataControllerRequest dcRequest,
			Result result) {
		boolean status = true;
		if (validateFileType(inputParams, dcRequest, result)) {
			if (!StringUtils.isNotBlank(inputParams.get("payeeIds"))
					&& !StringUtils.isNotBlank(inputParams.get("headers"))) {
				result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
				status = false;
			} else {
				if (!isInputArrayEmpty(inputParams.get("payeeIds")) || !isInputArrayEmpty(inputParams.get("headers"))) {
					result = SbgErrorCodeEnum.ERR_100010.setErrorCode(result);
					status = false;
				}
			}
		} else {
			status = false;
		}

		return status;
	}

	protected boolean validateFileType(Map<String, String> inputParams, DataControllerRequest dcRequest,
			Result result) {
		String fileType = inputParams.get("fileType");
		boolean status = false;
		if (StringUtils.isNotBlank(fileType)) {
			/* Supporting only pdf for now */
			switch (fileType) {
			case "pdf":
				status = true;
			}
		}
		if (!status) {
			HelperMethods.setValidationMsg("file type is either empty or not supported", dcRequest, result);
		}
		return status;
	}

	protected boolean isInputArrayEmpty(String input) {
		boolean status = false;
		List<String> payeeIds = new ArrayList<String>();
		JSONArray jsonArr = new JSONArray(input);
		Iterator<Object> iterator = jsonArr.iterator();
		while (iterator.hasNext()) {
			payeeIds.add(iterator.next().toString());
		}

		if (payeeIds.size() > 0) {
			status = true;
		}

		return status;
	}

}
