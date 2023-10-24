package com.kony.sbg.resources.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.business.api.SwiftCodeBusinessDelegate;
import com.kony.sbg.dto.SwiftCodeDTO;
import com.kony.sbg.resources.api.SwiftCodeUserManagementResource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class SwiftCodeUserManagementResourceImpl implements SwiftCodeUserManagementResource {
	private static final Logger logger = Logger.getLogger(SwiftCodeUserManagementResourceImpl.class);

	@Override
	public Result getSwiftCodeDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException {
		// TODO Auto-generated method stub
		
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Result result = new Result();
		String swiftCode = StringUtils.isNotBlank(inputParams.get("swiftcode")) ? inputParams.get("swiftcode") : "";
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ swiftCode:::@@@" +swiftCode);
	
		SwiftCodeDTO SwiftCodeDTO = new SwiftCodeDTO();
		SwiftCodeDTO.setSwiftcode(swiftCode);
		SwiftCodeDTO.setBankname(inputParams.get("bankname"));
		SwiftCodeDTO.setCountryname(inputParams.get("countryname"));
		SwiftCodeDTO.setLocation(inputParams.get("location"));
		SwiftCodeDTO.setBranchname(inputParams.get("branchname"));
		SwiftCodeDTO.setCityName(inputParams.get("city"));
		SwiftCodeDTO.setIsDomestic(inputParams.get("isdomestic"));


		logger.debug("SwiftCodeUserManagementResourceImpl@@@ bankname:::@@@" +SwiftCodeDTO.getBankname());
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ countryname:::@@@" +SwiftCodeDTO.getCountryname());
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ location:::@@@" +SwiftCodeDTO.getLocation());
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ branchname:::@@@" +SwiftCodeDTO.getBranchname());
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ city:::@@@" +SwiftCodeDTO.getCityName());
		logger.debug("SwiftCodeUserManagementResourceImpl@@@ isdomestic:::@@@" +SwiftCodeDTO.getIsDomestic());

		try {
			SwiftCodeBusinessDelegate infinityUserManagementBusinessDelegate = (SwiftCodeBusinessDelegate) DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(SwiftCodeBusinessDelegate.class);
			result = infinityUserManagementBusinessDelegate.getSwiftCodeDetails(SwiftCodeDTO,
					dcRequest.getHeaderMap(), dcRequest);
		} catch (Exception e) {		
			result.addParam("errmsg", "Error while getting swift details in resource");
		}

		return result;
	}
}
