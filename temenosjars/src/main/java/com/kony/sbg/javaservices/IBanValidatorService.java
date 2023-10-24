package com.kony.sbg.javaservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class IBanValidatorService implements JavaService2 {

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			
			String iban = StringUtils.isNotBlank(inputParams.get("iban")) ? inputParams.get("iban") : "";
			if(Character.isDigit(iban.charAt(0))) {
				result.addParam("errmsg", "");
				result.addParam("accountnumber", iban);
				result.addParam("status", "successfull");
			}else {
			Boolean IbanValid=checkIBAN(iban);
			if(IbanValid) {
				result.addParam("errmsg", "");
				result.addParam("iban", iban);
				result.addParam("isIBANValid", "true");
				result.addParam("status", "successfull");
			}else {
				result.addParam("errmsg", "IBAN number must be alphanumeric");
				result.addParam("iban", iban);
				result.addParam("isIBANValid", "false");
				result.addParam("status", "successfull");
			}
			}
		} catch (Exception e) {
			result.addParam("status", "failed");
			result.addParam("errmsg", e.getMessage());
		} 
		return result;
	}
	
	public static Boolean checkIBAN(String number) {
		IBANCheckDigit a = new IBANCheckDigit();
		Boolean b= a.isValid(number);
		return b;
		}

}
