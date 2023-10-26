package com.kony.sbg.backend.impl;
import java.util.Map;

import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.eum.product.contract.backenddelegate.impl.CoreCustomerBackendDelegateImpl;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.MembershipDTO;
public class CoreCustomerBackendDelegateImplExtn extends CoreCustomerBackendDelegateImpl{
	 LoggerUtil logger = new LoggerUtil(CoreCustomerBackendDelegateImpl.class);

	    @Override
	    public DBXResult searchCoreCustomers(MembershipDTO membershipDTO, Map<String, Object> headersMap)
	            throws ApplicationException {
	        /*DBXResult responseDTO = new DBXResult();
	        Map<String, Object> procParams = new HashMap<>();
	        procParams.put("_id", StringUtils.isNotBlank(membershipDTO.getId()) ? membershipDTO.getId() : "");
	        procParams.put("_name", StringUtils.isNotBlank(membershipDTO.getName()) ? membershipDTO.getName() : "");
	        procParams.put("_email", StringUtils.isNotBlank(membershipDTO.getEmail()) ? membershipDTO.getEmail() : "");
	        procParams.put("_phone", StringUtils.isNotBlank(membershipDTO.getPhone()) ? membershipDTO.getPhone() : "");
	        procParams.put("_dateOfBirth",
	                StringUtils.isNotBlank(membershipDTO.getDateOfBirth()) ? membershipDTO.getDateOfBirth() : "");
	        procParams.put("_status",
	                StringUtils.isNotBlank(membershipDTO.getStatus()) ? membershipDTO.getStatus() : "");
	        procParams.put("_city",
	                (membershipDTO.getAddress() != null && StringUtils.isNotBlank(membershipDTO.getAddress().getCityName()))
	                        ? membershipDTO.getAddress().getCityName()
	                        : "");
	        procParams.put("_country",
	                (membershipDTO.getAddress() != null && StringUtils.isNotBlank(membershipDTO.getAddress().getCountry()))
	                        ? membershipDTO.getAddress().getCountry()
	                        : "");
	        procParams.put("_zipCode",
	                (membershipDTO.getAddress() != null && StringUtils.isNotBlank(membershipDTO.getAddress().getZipCode()))
	                        ? membershipDTO.getAddress().getZipCode()
	                        : "");
	        if (StringUtils.isNotBlank(membershipDTO.getTaxId())) {
	            procParams.put("_taxId", StringUtils.isNotBlank(membershipDTO.getTaxId()) ? membershipDTO.getTaxId() : "");
	        }
	        JsonObject response = new JsonObject();
	        try {
	            String IS_Integrated = Boolean.toString(IntegrationTemplateURLFinder.isIntegrated);
	            if (StringUtils.isNotBlank(IS_Integrated) && IS_Integrated.equalsIgnoreCase("true")) {
	                response = searchCoreCustomerT24(procParams, headersMap);

	            } else {
	                response = ServiceCallHelper.invokeServiceAndGetJson(procParams, headersMap,
	                        URLConstants.MEMBERSHIP_CUSTOMER_SEARCH_PROC);
	            }
	            if (JSONUtil.hasKey(response, "records")) {
	                if (response.get("records").getAsJsonArray().size() > 0) {
	                    responseDTO.setResponse(response.get("records").getAsJsonArray());
	                }
	            } else {
	                logger.error("CoreCustomerBackendDelegateImpl : Backend response is not appropriate " + response);
	                throw new ApplicationException(ErrorCodeEnum.ERR_10756);
	            }
	        } catch (Exception e) {
	            logger.error("CoreCustomerBackendDelegateImpl : Exception occured while fetching the core customers"
	                    + e.getMessage());
	            throw new ApplicationException(ErrorCodeEnum.ERR_10756);
	        }
	        return responseDTO;*/
	        return null;
	    }

	  
}
