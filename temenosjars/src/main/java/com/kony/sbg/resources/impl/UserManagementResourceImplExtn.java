package com.kony.sbg.resources.impl;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.resource.impl.UserManagementResourceImpl;
import com.temenos.infinity.api.usermanagement.businessdelegate.api.UserManagementBusinessDelegate;
import com.temenos.infinity.api.usermanagement.constants.ErrorCodeEnum;
import com.temenos.infinity.api.usermanagement.dto.CustomerDetailsDTO;
public class UserManagementResourceImplExtn extends UserManagementResourceImpl{
	private static final Logger LOG = LogManager.getLogger(UserManagementResourceImplExtn.class);
	
	public Result updateCustomerDetails(CustomerDetailsDTO customerDetailsDTO, HashMap<String, Object> headerMap) {
		CustomerDetailsDTO customerDetailsOrderDTO = new CustomerDetailsDTO();
		Result result = new Result();
		String operation = customerDetailsDTO.getOperation();

		if (operation.contentEquals("Delete")) {
			String deleteCommunicationID = customerDetailsDTO.getDeleteCommunicationID();
			if (StringUtils.isBlank(deleteCommunicationID)) {
				return ErrorCodeEnum.ERR_20059.setErrorCode(new Result());
			}
		} else if (operation.contentEquals("DeleteAddress")) {
			String deleteAddressID = customerDetailsDTO.getDeleteAddressID();
			if (StringUtils.isBlank(deleteAddressID)) {
				return ErrorCodeEnum.ERR_20060.setErrorCode(new Result());
			}
		} else {

			String detailToBeUpdated = customerDetailsDTO.getDetailToBeUpdated();
			String phoneNumber = customerDetailsDTO.getPhoneNumber();
			String id = customerDetailsDTO.getId();
			String value = customerDetailsDTO.getValue();

			if (detailToBeUpdated.contentEquals("phoneNumbers")) {
				if (operation.contentEquals("Create")) {
					if (StringUtils.isBlank(phoneNumber)) {
						return ErrorCodeEnum.ERR_200514.setErrorCode(new Result());
					}
				}
				if (operation.contentEquals("Update")) {
					if (StringUtils.isBlank(phoneNumber)) {
						return ErrorCodeEnum.ERR_200514.setErrorCode(new Result());
					}
					if (StringUtils.isBlank(id)) {
						return ErrorCodeEnum.ERR_200515.setErrorCode(new Result());
					}
				}
			}

			if (detailToBeUpdated.contentEquals("EmailIds")) {
				if (operation.contentEquals("Create")) {
					if (StringUtils.isBlank(value)) {
						return ErrorCodeEnum.ERR_200513.setErrorCode(new Result());
					}
				}
				if (operation.contentEquals("Update")) {
					if (StringUtils.isBlank(value)) {
						return ErrorCodeEnum.ERR_200513.setErrorCode(new Result());
					}
					if (StringUtils.isBlank(id)) {
						return ErrorCodeEnum.ERR_200516.setErrorCode(new Result());
					}
				}
			}

		}
		try {
			UserManagementBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(UserManagementBusinessDelegate.class);
			customerDetailsOrderDTO = orderBusinessDelegate.updateCustomerDetails(customerDetailsDTO, headerMap);

			if (StringUtils.isBlank(customerDetailsOrderDTO.getOrderId())) {

				return ErrorCodeEnum.ERR_20057.setErrorCode(new Result());
			}
			if (StringUtils.isNotBlank(customerDetailsOrderDTO.getErrorMessage())) {
				String dbpErrCode = customerDetailsOrderDTO.getCode();
				String dbpErrMessage = customerDetailsOrderDTO.getErrorMessage();
				if (StringUtils.isNotBlank(dbpErrMessage)) {
					String msg = ErrorCodeEnum.ERR_20062.getMessage(customerDetailsOrderDTO.getOrderId(),
							customerDetailsOrderDTO.getStatus(), dbpErrMessage);
					return ErrorCodeEnum.ERR_20062.setErrorCode(new Result(), msg);
				}
			}
			JSONObject updatedCustomerDetailsDTO = new JSONObject(customerDetailsOrderDTO);
			result = JSONToResult.convert(updatedCustomerDetailsDTO.toString());
			
			//SBGCommonUtils._OlbAuditLogsClass(request,response,result,"USER","USER_UPDATE","ExternalUserManagement/operations/ExternalUsers_1/editUser","SUCCESS");
		} catch (Exception e) {
			LOG.error(e);
			LOG.debug("Failed to update customer details in OMS" + e);
			return ErrorCodeEnum.ERR_20058.setErrorCode(new Result());
		}
		return result;
	}
}
