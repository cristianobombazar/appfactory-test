package com.kony.sbg.resources.impl;

import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.fileutil.MimeTypeValidator;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.dto.CustomerImageDTO;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.api.CustomerImageBusinessDelegate;
import com.temenos.dbx.eum.product.usermanagement.resource.impl.CustomerImageResourceImpl;

public class CustomerImageResourceImplExtn extends CustomerImageResourceImpl{
	LoggerUtil logger = new LoggerUtil(CustomerImageResourceImplExtn.class);
	@Override
	public Result updateCustomerImage(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		logger = new LoggerUtil(CustomerImageResourceImpl.class);
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Result getCustomerImageResult = getCustomerImage(methodID, inputArray, dcRequest, dcResponse);

		if (!(getCustomerImageResult.getNameOfAllParams().contains(DBPUtilitiesConstants.USER_IMAGE))) {
			ErrorCodeEnum.ERR_10194.setErrorCode(result);
			return result;
		}

		boolean updateCustomerImageStatus = false;

		if (inputParams.containsKey(DBPUtilitiesConstants.USER_IMAGE)
				&& StringUtils.isNotBlank(inputParams.get(DBPUtilitiesConstants.USER_IMAGE))) {

			String image = inputParams.get(DBPUtilitiesConstants.USER_IMAGE);

			try {
				byte[] decoded = Base64.getDecoder().decode(image);
				MimeTypeValidator mimeTypeValidator = new MimeTypeValidator(decoded);
				updateCustomerImageStatus = mimeTypeValidator.hasValidImageMimeType();
			} catch (Exception e) {
				logger.error("Exception occured while validating the input user image" + e.getMessage());
				ErrorCodeEnum.ERR_10194.setErrorCode(result);
				return result;
			}
		}
		if (!updateCustomerImageStatus) {
			ErrorCodeEnum.ERR_10194.setErrorCode(result);
			return result;
		}

		CustomerImageDTO customerImageDTO = new CustomerImageDTO();
		customerImageDTO.setUserImage(inputParams.get(DBPUtilitiesConstants.USER_IMAGE));
		if (StringUtils.isBlank(getCustomerImageResult.getParamValueByName(DBPUtilitiesConstants.USER_IMAGE))) {
			CustomerImageBusinessDelegate customerImageBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(CustomerImageBusinessDelegate.class);
			updateCustomerImageStatus = customerImageBusinessDelegate.createCustomerImage(customerImageDTO,
					HelperMethods.getHeaders(dcRequest));
		} else {
			CustomerImageBusinessDelegate customerImageBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(CustomerImageBusinessDelegate.class);
			updateCustomerImageStatus = customerImageBusinessDelegate.updateCustomerImage(customerImageDTO,
					HelperMethods.getHeaders(dcRequest));
		}
		if (updateCustomerImageStatus) {
			result.addParam(new Param("success", "success"));
		} else {
			ErrorCodeEnum.ERR_10194.setErrorCode(result);
		}
		SBGCommonUtils._OlbAuditLogsClass(dcRequest,dcResponse,result,"ACCOUNT_ACTION","ACCOUNT_UPDATE","ExternalUserManagement/operations/ExternalUsers_2/updateUserProfileImage","SUCCESS");

		return result;
	}
}
