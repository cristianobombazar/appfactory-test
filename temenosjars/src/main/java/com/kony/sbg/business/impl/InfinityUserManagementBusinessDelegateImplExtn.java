package com.kony.sbg.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.BundleConfigurationHandler;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.kms.KMSUtil;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.InfinityUserManagementBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.UserManagementBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.impl.InfinityUserManagementBusinessDelegateImpl;
import com.temenos.dbx.product.businessdelegate.api.KMSBusinessDelegate;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.CommunicationBackendDelegate;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.InfinityConstants;
public class InfinityUserManagementBusinessDelegateImplExtn extends InfinityUserManagementBusinessDelegateImpl{
	LoggerUtil logger = new LoggerUtil(InfinityUserManagementBusinessDelegateImplExtn.class);

	@Override
	public DBXResult generateInfinityUserActivationCodeAndUsername(Map<String, String> bundleConfigurations,
			Map<String, String> inputParams, Map<String, Object> headersMap) throws ApplicationException {
		boolean status = false;
		DBXResult result = new DBXResult();
		JsonObject resultObject = new JsonObject();
		try {

			InfinityUserManagementBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(InfinityUserManagementBackendDelegate.class);
			UserManagementBackendDelegate usermanagementBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(UserManagementBackendDelegate.class);

			logger.error("Infinity backend delegate : InfinityUserManagementBackendDelegate business delegate "
					+ backendDelegate);
			logger.debug("Infinity backend delegate : InfinityUserManagementBackendDelegate business delegate "
					+ backendDelegate);

			logger.error("Infinity backend delegate : UserManagementBackendDelegate business delegate "
					+ usermanagementBackendDelegate);
			logger.debug("Infinity backend delegate : UserManagementBackendDelegate business delegate "
					+ usermanagementBackendDelegate);

			String userId = inputParams.get(InfinityConstants.userId);
			String userName = inputParams.get(InfinityConstants.userName);
			boolean isOnBoradingFlow = Boolean.parseBoolean(inputParams.get(InfinityConstants.isOnBoradingFlow));
			boolean isProspectFlow = Boolean.parseBoolean(inputParams.get(InfinityConstants.isProspectFlow));

			DBXResult usernameresponse = backendDelegate
					.generateInfinityUserName(bundleConfigurations.get(BundleConfigurationHandler.USERNAME_LENGTH));
			DBXResult activatiocoderesponse = backendDelegate
					.generateActivationCode(bundleConfigurations.get(BundleConfigurationHandler.ACTIVATIONCODE_LENGTH));
			String activationCode = inputParams.get(InfinityConstants.password);
			String applicationid = inputParams.get(InfinityConstants.applicationid);
			if (StringUtils.isBlank(userName) && usernameresponse != null && usernameresponse.getResponse() != null) {
				userName = (String) usernameresponse.getResponse();
			}
			if (StringUtils.isBlank(activationCode) && activatiocoderesponse != null
					&& activatiocoderesponse.getResponse() != null) {
				activationCode = (String) activatiocoderesponse.getResponse();
			}
			if (StringUtils.isBlank(userName) || StringUtils.isBlank(activationCode)) {
				throw new ApplicationException(ErrorCodeEnum.ERR_10795);
			}

			inputParams.put(InfinityConstants.userName, userName);

			String phone = inputParams.get(DTOConstants.PHONE);
			String email = inputParams.get(DTOConstants.EMAIL);
			JsonObject customerCommunication = new JsonObject();
			JsonArray communicationArray = new JsonArray();
			if (StringUtils.isBlank(phone) || StringUtils.isBlank(email)) {
				CommunicationBackendDelegate communicationBackendDelegate = DBPAPIAbstractFactoryImpl
						.getBackendDelegate(CommunicationBackendDelegate.class);

				logger.error("Infinity backend delegate : CommunicationBackendDelegate business delegate "
						+ communicationBackendDelegate);
				logger.debug("Infinity backend delegate : CommunicationBackendDelegate business delegate "
						+ communicationBackendDelegate);

				CustomerCommunicationDTO customerCommunicationDTO = new CustomerCommunicationDTO();
				customerCommunicationDTO.setCustomer_id(userId);
				DBXResult communicationResponse = communicationBackendDelegate
						.getPrimaryMFACommunicationDetails(customerCommunicationDTO, headersMap);
				customerCommunication = ((JsonObject) communicationResponse.getResponse());
         if(customerCommunication!=null) {
				if (customerCommunication.has(DBPDatasetConstants.DATASET_CUSTOMERCOMMUNICATION)
						&& customerCommunication.get(DBPDatasetConstants.DATASET_CUSTOMERCOMMUNICATION).isJsonArray()) {
					communicationArray = customerCommunication.get(DBPDatasetConstants.DATASET_CUSTOMERCOMMUNICATION)
							.getAsJsonArray();
					for (JsonElement jsonelement : communicationArray) {
						JsonObject object = jsonelement.getAsJsonObject();
						if (StringUtils.isBlank(email)) {
							if ("COMM_TYPE_EMAIL".equalsIgnoreCase(JSONUtil.getString(object, "Type_id")))
								email = JSONUtil.getString(object, "Value");
						}
						if (StringUtils.isBlank(phone)) {
							if ("COMM_TYPE_PHONE".equalsIgnoreCase(JSONUtil.getString(object, "Type_id")))
								phone = JSONUtil.getString(object, "Value");
						}
					}
				}
              }
			}
			CustomerDTO customerInfo = new CustomerDTO();
			customerInfo.setId(userId);
			DBXResult customerResponse = usermanagementBackendDelegate.get(customerInfo, headersMap);
			if (customerResponse != null && customerResponse.getResponse() != null)
				customerInfo = (CustomerDTO) customerResponse.getResponse();

			if (!isOnBoradingFlow && !isProspectFlow && customerInfo.getUserName().equalsIgnoreCase(userId))
				customerInfo.setUserName(userName);

			String emailTemplate = inputParams.get(InfinityConstants.EMAIL_TEMPLATE);
			String smsTemplate = inputParams.get(InfinityConstants.SMS_TEMPLATE);

			if (!isProspectFlow) {
				UserManagementBackendDelegate userManagementBackendDelegate = DBPAPIAbstractFactoryImpl
						.getBackendDelegate(UserManagementBackendDelegate.class);
				userManagementBackendDelegate.createEntryForCredentailCheckerTable(bundleConfigurations, customerInfo,
						activationCode, headersMap);
			}

			if (!isOnBoradingFlow) {
				usermanagementBackendDelegate.updateCustomerDetails(customerInfo, headersMap);
				emailTemplate = DBPUtilitiesConstants.ENROLLMENT_USERNAME_TEMPLATE;
				smsTemplate = DBPUtilitiesConstants.ENROLLMENT_ACTIVATIONCODE_TEMPLATE;
			}
			customerInfo = (CustomerDTO) usermanagementBackendDelegate.get(customerInfo, headersMap).getResponse();
			customerInfo.setApplicationID(applicationid);
			if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(emailTemplate)) {
				sendGeneratedUsernameToEmail(bundleConfigurations, customerInfo, email, headersMap, emailTemplate);
			}
			if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(smsTemplate)) {
				sendGeneratedActivationcodeToMobile(phone, activationCode, headersMap, smsTemplate);
			}
			status = true;

			resultObject.addProperty(InfinityConstants.activationCode, activationCode);
			resultObject.addProperty(InfinityConstants.userId, customerInfo.getUserName());

		} catch (ApplicationException e) {
			logger.error(
					"InfinityUserManagementBusinessDelegateImpl : Exception occured while generating username and activation code "
							+ e.getMessage());
			throw new ApplicationException(e.getErrorCodeEnum());
		} catch (Exception e) {
			logger.error(
					"InfinityUserManagementBusinessDelegateImpl : Exception occured while generating username and activation code "
							+ e.getMessage());
			throw new ApplicationException(ErrorCodeEnum.ERR_10795);
		}
		resultObject.addProperty(InfinityConstants.status, status);
		result.setResponse(resultObject);
		return result;
	}
	private void sendGeneratedUsernameToEmail(Map<String, String> configurations, CustomerDTO customerInfo,
			String email, Map<String, Object> headersMap, String templateName) throws ApplicationException {
		try {
			KMSBusinessDelegate kmsBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(KMSBusinessDelegate.class);
			Map<String, Object> input = new HashMap<>();
			input.put("FirstName", customerInfo.getFirstName());
			input.put("EmailType", templateName);
			input.put("LastName", customerInfo.getLastName());
			JSONObject addContext = new JSONObject();
			addContext.put("resetPasswordLink", EnvironmentConfigurationsHandler.getValue("DBP_OLB_BASE_URL"));
			addContext.put("userName", customerInfo.getUserName());
			if (customerInfo.getApplicationID() != null || StringUtils.isNotBlank(customerInfo.getApplicationID())) {
				addContext.put("applicationID", customerInfo.getApplicationID());
			}
			addContext.put("activationCodeExpiry",
					String.valueOf(
							(Integer.parseInt(configurations.get(BundleConfigurationHandler.ACTIVATIONCODE_EXPIRYTIME))
									/ 1440)));
			logger.debug("Context" + addContext);
			input.put("AdditionalContext", KMSUtil.getOTPContent(null, null, addContext));
			input.put("Email", email);
			kmsBusinessDelegate.sendKMSEmail(input, headersMap);
		} catch (Exception e) {
			logger.error("Exception occured while sending email " + e.getMessage());
			throw new ApplicationException(ErrorCodeEnum.ERR_10796);
		}
	}
	private void sendGeneratedActivationcodeToMobile(String phone, String activationCode,
			Map<String, Object> headersMap, String templateName) throws ApplicationException {
		try {
			KMSBusinessDelegate kmsBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(KMSBusinessDelegate.class);
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("Phone", phone);
			inputParams.put("otp", activationCode);
			inputParams.put("MessageType", templateName);
			kmsBusinessDelegate.sendKMSSMS(inputParams, headersMap);
		} catch (Exception e) {
			logger.error("Exception occured while sending SMS" + e.getMessage());
			throw new ApplicationException(ErrorCodeEnum.ERR_10797);
		}

	}
}
