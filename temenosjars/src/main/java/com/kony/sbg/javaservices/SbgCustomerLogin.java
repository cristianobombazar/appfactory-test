package com.kony.sbg.javaservices;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.sessionmanager.SessionManager;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.BCrypt;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EncryptionUtils;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.customersecurityservices.GetCustomerPreferencesConcurrent;
import com.kony.eum.dbputilities.customersecurityservices.PasswordHistoryManagement;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.exceptions.MetricsException;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CustomerGetByUserNameOperation;
import com.temenos.dbx.product.utils.InfinityConstants;

public class SbgCustomerLogin implements JavaService2 {
    private static LoggerUtil logger = new LoggerUtil(SbgCustomerLogin.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        logger = new LoggerUtil(SbgCustomerLogin.class);

        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        if (preProcess(inputParams, dcRequest, result)) {
            result = (Result) new CustomerGetByUserNameOperation().invoke(methodID, inputArray, dcRequest, dcResponse);
            logger.debug("SbgCustomerLogin ---> Customer result: "+ResultToJSON.convert(result));
            
            if (isAccessGrantNeeded(inputParams)
                    && StringUtils.isNotBlank(HelperMethods.getFieldValue(result, "CustomerType_id"))) {
            	logger.debug("SbgCustomerLogin ---> Executing postparam 1");
                // if (accessGranted(HelperMethods.getFieldValue(result, "CustomerType_id"), dcRequest, inputParams)) {
                result = postProcess(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
                // } else {
                // ErrorCodeEnum.ERR_10097.setErrorCode(result);
                // }
            } else {
                String reportingParams = dcRequest.getHeader("X-Kony-ReportingParams");
                if (isAccessGrantNeeded(inputParams) && StringUtils.isBlank(reportingParams)) {
                    ErrorCodeEnum.ERR_10098.setErrorCode(result);
                } else {
                	logger.debug("SbgCustomerLogin ---> Executing postparam 2");
                    result = postProcess(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
                }
            }
        }

        return result;
    }

    private boolean isAccessGrantNeeded(Map<String, String> inputParams) {
        String sharedSecret = inputParams.get("sharedSecret");
        return (StringUtils.isBlank(sharedSecret) || "$sharedSecret".equalsIgnoreCase(sharedSecret));
    }

    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result)
            throws HttpCallException {
        boolean status = true;

        logger.debug("SbgCustomerLogin ---> InputParams in the beggining of preProcess : " + inputParams);

        String authCode = inputParams.get("authCode");
        String username = inputParams.get("UserName");
        String password = inputParams.get("Password");
        String pin = inputParams.get(DBPUtilitiesConstants.PIN);
        String sharedSecret = inputParams.get("sharedSecret");
        String csrUserName = inputParams.get("CSRUserName");
        String sessionToken = inputParams.get("session_token");
        String prospectLogin = inputParams.get("prospect");
        String mfaName = getMfaName(dcRequest);
        
        Result result1 = null;

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(authCode)) {
        	Map<String, Object> tempParam = new HashMap<>();
        	tempParam.put("authCode", authCode);
        	String url = "/services/SbgPingAuthJson/getAccessToken";
        	Map<String, String> svcHeaders = new HashMap<String, String>();
        	result1 = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, tempParam, svcHeaders, url);
        	
        	String email = result1.getParamValueByName("email");
        	if(StringUtils.isNotBlank(email)) {
        		email = email.toLowerCase();
        		username = email;
        	}
        	sb.append("UserName").append(DBPUtilitiesConstants.EQUAL).append("'").append(email).append("'");
        	logger.debug("SbgCustomerLogin ---> email : " + email);
        	
        } else if (StringUtils.isNotBlank(password) && !"$password".equalsIgnoreCase(password)) {
            sb.append("UserName").append(DBPUtilitiesConstants.EQUAL).append("'").append(username).append("'");
        } else if (StringUtils.isNotBlank(pin) && !"$pin".equalsIgnoreCase(pin)) {
            sb.append("UserName").append(DBPUtilitiesConstants.EQUAL).append(username);
        } else if (StringUtils.isNotBlank(sharedSecret) && !"$sharedSecret".equalsIgnoreCase(sharedSecret)
                && (StringUtils.isBlank(csrUserName) || "$CSRUserName".equals(csrUserName))) {
            sb.append("UserName").append(DBPUtilitiesConstants.EQUAL).append("admin");
            username = "admin";
        } else if (StringUtils.isNotBlank(sharedSecret) && !"$sharedSecret".equalsIgnoreCase(sharedSecret)) {
            sb.append("UserName").append(DBPUtilitiesConstants.EQUAL).append(csrUserName);
            username = csrUserName;
        } else if (StringUtils.isNotBlank(sessionToken) && !"$session_token".equalsIgnoreCase(sessionToken)
                && (StringUtils.isBlank(password) || "$password".equalsIgnoreCase(password))
                && (StringUtils.isBlank(sharedSecret) || "$sharedSecret".equalsIgnoreCase(sharedSecret))
                && (StringUtils.isBlank(pin) || "$pin".equalsIgnoreCase(pin)
                        && (Boolean.parseBoolean(prospectLogin))
                        && (StringUtils.isNotEmpty(mfaName)) && mfaName.equalsIgnoreCase("Origination"))) {
            sb.append("Session_id").append(DBPUtilitiesConstants.EQUAL).append(sessionToken);
        } else if (StringUtils.isNotBlank(sessionToken) && !"$session_token".equalsIgnoreCase(sessionToken)
                && (StringUtils.isBlank(password) || "$password".equalsIgnoreCase(password))
                && (StringUtils.isBlank(sharedSecret) || "$sharedSecret".equalsIgnoreCase(sharedSecret))
                && (StringUtils.isBlank(pin) || "$pin".equalsIgnoreCase(pin))) 
        {

            if (AdminUtil.verifyCSRAssistToken(dcRequest, sessionToken)) {
                sb.append("UserName").append(DBPUtilitiesConstants.EQUAL)
                        .append(dcRequest.getAttribute("CSRAssist_Customer_username").toString());
                username = dcRequest.getAttribute("CSRAssist_Customer_username").toString();
            } else {
                ErrorCodeEnum.ERR_10099.setErrorCode(result,
                        String.valueOf(dcRequest.getAttribute("CSR_Response").toString()));
                status = false;
            }

        } else {
            ErrorCodeEnum.ERR_10083.setErrorCode(result);
            status = false;
        }

        inputParams.put("UserName", username);
        
        if(result1 != null) {
            inputParams.put("strongAuth", result1.getParamValueByName("strongAuth"));
            inputParams.put("strongAuthMethod", result1.getParamValueByName("strongAuthMethod"));
        }
        
        inputParams.put(DBPUtilitiesConstants.FILTER, sb.toString());

        logger.debug("InputParams from preProcess : " + inputParams);

        return status;
    }

    private Result postProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result,
            String methodID, DataControllerResponse dcResponse, Object[] inputArray)
            throws MetricsException, ParseException, HttpCallException {
        Result retVal = null;
        logger.debug("Input Params in the beggining of postProcess : " + inputParams);
        String password = inputParams.get("Password");
        String pin = inputParams.get(DBPUtilitiesConstants.PIN);
        String sharedSecret = inputParams.get("sharedSecret");
        String sessionToken = inputParams.get("session_token");
        String prospectLogin = inputParams.get("prospect");
        String authCode = inputParams.get("authCode");
        String mfaName = getMfaName(dcRequest);

        if (StringUtils.isNotBlank(authCode)) {
        	retVal = postProcessForEmail(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
        } else if (StringUtils.isNotBlank(pin) && !"$pin".equalsIgnoreCase(pin)) {
            retVal = postProcessForPin(inputParams, dcRequest, result, inputParams, methodID, dcResponse, inputArray);
        } else if (StringUtils.isNotBlank(sessionToken) && !"$session_token".equalsIgnoreCase(sessionToken)
                && (StringUtils.isBlank(password) || "$password".equalsIgnoreCase(password))
                && (StringUtils.isBlank(sharedSecret) || "$sharedSecret".equalsIgnoreCase(sharedSecret))
                && (StringUtils.isBlank(pin) || "$pin".equalsIgnoreCase(pin)
                        && (Boolean.parseBoolean(prospectLogin))
                        && (StringUtils.isNotEmpty(mfaName)) && mfaName.equalsIgnoreCase("Origination"))) {
            retVal = postProcessForOnboardingST(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
        } else if ((StringUtils.isNotBlank(sessionToken) && !"$session_token".equalsIgnoreCase(sessionToken))
                && (StringUtils.isBlank(password) || "$password".equalsIgnoreCase(password))
                && (StringUtils.isBlank(sharedSecret) || "$sharedSecret".equalsIgnoreCase(sharedSecret))
                && (StringUtils.isBlank(pin) || "$pin".equalsIgnoreCase(pin))) {
            retVal = postProcessForST(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
        } else if (StringUtils.isNotBlank(password) && !"$password".equalsIgnoreCase(password)) {
            retVal = postProcessForUserName(inputParams, dcRequest, result, methodID, dcResponse, inputArray);
        } else {
            retVal = postProcessForSharedSecret(sharedSecret, dcRequest, result, inputParams, methodID, dcResponse,
                    inputArray);
        }
        return retVal;
    }

    private Result postProcessForSharedSecret(String sharedSecret, DataControllerRequest dcRequest, Result result,
            Map<String, String> inputParams, String methodID, DataControllerResponse dcResponse, Object[] inputArray) {
        String sharedSecretFromDB = "";
        String key = "";
        String filter = "";
        Result retVal = new Result();
        Result systemConfig = new Result();

        filter = "PropertyName" + DBPUtilitiesConstants.EQUAL + URLConstants.DBP_API_ACCESS_TOKEN;
        try {
            systemConfig = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.SYSTEM_CONFIGURATION_GET);
        } catch (HttpCallException e) {
            logger.error("Caught exception while Getting System Configurations: ", e);
        }
        sharedSecretFromDB = HelperMethods.getFieldValue(systemConfig, "PropertyValue");
        try {
            key = URLFinder.getPathUrl(URLConstants.DBP_ACCESS_TOKEN, dcRequest);
        } catch (Exception e) {
            key = "";
            logger.error("Caught exception while Getting DBP_ACCCESS_TOKEN: ", e);
        }
        if (StringUtils.isBlank(key) || StringUtils.isBlank(sharedSecretFromDB) || StringUtils.isBlank(sharedSecret)) {
            ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
            return retVal;
        }
        try {
            sharedSecretFromDB = EncryptionUtils.decrypt(sharedSecretFromDB, key);
        } catch (Exception e) {

            logger.error("Caught exception while Decrypting: ", e);
        }
        if (sharedSecret.equals(sharedSecretFromDB)) {
            try {
                retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
            } catch (Exception e) {
                retVal = new Result();
                ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
                logger.error("Caught exception while setting SessionAttributes: ", e);
            }
        } else {
            ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
        }

        logger.debug("Response from postProcessForSharedSecret : " + ResultToJSON.convert(retVal));

        return retVal;
    }

    private Result postProcessForUserName(Map<String, String> inputParams, DataControllerRequest dcRequest,
            Result result, String methodID, DataControllerResponse dcResponse, Object[] inputArray)
            throws MetricsException, ParseException, HttpCallException {
        Result retVal = new Result();
        PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);
        String mfaName = getMfaName(dcRequest);
        logger.debug("Application name"+ mfaName);
        if (StringUtils.isNotBlank(pm.getDbpErrorCode())) {
            ErrorCodeEnum.ERR_10164.setErrorCode(retVal, pm.getDbpErrorCode(), pm.getDbpErrorMessage());
            return retVal;
        }

        String password = inputParams.get("Password");

        String prospectLogin = inputParams.get("prospect");

        if (StringUtils.isBlank(prospectLogin)) {
            prospectLogin = "false";
        }

        if (HelperMethods.hasRecords(result)) {

            Record user = result.getAllDatasets().get(0).getRecord(0);
            boolean eSignAgreementRequired = false;
            boolean isEagreementSigned = esignStatus(user);
            if (!isEagreementSigned && HelperMethods.isBusinessUserType(user.getParamValueByName("CustomerType_id"))) {
                eSignAgreementRequired = isUserEsignAgreementReq(user, dcRequest);
            }

            boolean isProspect = false;
            if (Boolean.parseBoolean(prospectLogin)
                    && user.getParamValueByName("CustomerType_id").equalsIgnoreCase("TYPE_ID_PROSPECT")
                    && (StringUtils.isNotEmpty(mfaName)) && mfaName.equalsIgnoreCase("Origination")) {
                isProspect = true;
            }
            
			if (StringUtils.isNotBlank(EnvironmentConfigurationsHandler.getValue(URLConstants.IS_SALESFORCE_INTEGRATED))
					&& EnvironmentConfigurationsHandler.getValue(URLConstants.IS_SALESFORCE_INTEGRATED)
							.equalsIgnoreCase("true")
					&& !isProspect) {
				if (StringUtils.isEmpty(user.getParamValueByName("SFDC_accountId"))) {
					ErrorCodeEnum.ERR_29025.setErrorCode(retVal);
					return retVal;
				}
			}
			
			String dbPassword = HelperMethods.getFieldValue(user, "Password");
			if(Boolean.parseBoolean(prospectLogin) && dbPassword.equalsIgnoreCase(InfinityConstants.defaultPassword)) {
				ErrorCodeEnum.ERR_29032.setErrorCode(retVal);
				return retVal;
			}

			boolean isPasswordExpired = isPasswordExpired(dcRequest, user, pm, isProspect);

            if (!Boolean.parseBoolean(prospectLogin)
                    && user.getParamValueByName("CustomerType_id").equalsIgnoreCase("TYPE_ID_PROSPECT")
                    && (StringUtils.isNotEmpty(mfaName)) && mfaName.equalsIgnoreCase("Origination")) {
                ErrorCodeEnum.ERR_10092.setErrorCode(retVal);
            } else if (!isProspect && isPasswordExpired) {
                ErrorCodeEnum.ERR_10135.setErrorCode(retVal);
            } else if (isUserExpired(user)) {
                ErrorCodeEnum.ERR_10087.setErrorCode(retVal);
            } else if (isUserLocked(user, dcRequest, pm) && !tryAutoLockReset(user, dcRequest, pm)) {
                int x = pm.getAutoUnLockPeriod();
                ErrorCodeEnum.ERR_10088.setErrorCode(retVal,
                        "Your profile is locked, it will be unlocked after " + x + " mins");
            } else if (isUserSuspended(user)) {
                ErrorCodeEnum.ERR_10089.setErrorCode(retVal);
            } else if (isUserNew(user, mfaName)) {
                ErrorCodeEnum.ERR_10090.setErrorCode(retVal);
                if (hasPassword(user) && !isEagreementSigned && eSignAgreementRequired) {
                    ErrorCodeEnum.ERR_10091.setErrorCode(retVal);
                }
            } else if (!isUserActive(user, mfaName)) {
                ErrorCodeEnum.ERR_10092.setErrorCode(retVal);
            } else {
                if (eSignAgreementRequired) {
                    if (!isEagreementSigned) {
                        ErrorCodeEnum.ERR_10093.setErrorCode(retVal);
                        retVal.addParam(new Param(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY, "200", "int"));
                        retVal.addParam(new Param("isEAgreementRequired", "true", "String"));
                        retVal.addParam(new Param("isEagreementSigned", "false", "String"));
                        retVal.addParam(new Param("isProspectExpired", "true", "String"));
                        return retVal;
                    }
                }

                if (validatePassword(dcRequest, result, password)) {
                    retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
                    Record record = retVal.getRecordById(DBPUtilitiesConstants.USR_ATTR);
                    record.addParam(new Param("isEAgreementRequired", "" + eSignAgreementRequired, "String"));
                    record.addParam(new Param("isEagreementSigned", "" + isEagreementSigned, "String"));
                    if (isProspect && isPasswordExpired) {
                        record.addParam(new Param("isProspectExpired", "true", "String"));
                        retVal.addParam(new Param("isProspectExpired", "true", "String"));
                    }

                    updateUserDetails(dcRequest, result, retVal);
                } else if (checkIsCoreIdentityScope(result)) {
                    ErrorCodeEnum.ERR_10094.setErrorCode(retVal);
                } else {
                    Record record = new Record();
                    record.setId(DBPUtilitiesConstants.USR_ATTR);
                    retVal.addRecord(record);
                    ErrorCodeEnum.ERR_10095.setErrorCode(retVal);

                    String lockcount = HelperMethods.getFieldValue(user, "lockCount");

                    updateLockCount(dcRequest, user, lockcount);
                    if (isUserLockedAfterUpdateCount(lockcount, pm)) {
                        int x = pm.getAutoUnLockPeriod();
                        ErrorCodeEnum.ERR_10088.setErrorCode(retVal,
                                "Your profile is locked, it will be unlocked after " + x + " mins");
                    }

                }

                if (isProspect && isPasswordExpired) {
                    retVal.addParam(new Param("isProspectExpired", "true", "String"));
                }
                retVal.addParam(new Param("isEAgreementRequired", "" + eSignAgreementRequired, "String"));
                retVal.addParam(new Param("isEagreementSigned", "" + isEagreementSigned, "String"));
                Param p = new Param(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY, "200", "int");
                retVal.addParam(p);
            }
        } else {
            Record record = new Record();
            record.setId(DBPUtilitiesConstants.USR_ATTR);
            retVal.addRecord(record);
            ErrorCodeEnum.ERR_10095.setErrorCode(retVal);
        }

        logger.debug("Response from postProcessForUserName : " + ResultToJSON.convert(retVal));
        return retVal;
    }
    
    private Result postProcessForEmail(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result, String methodID, DataControllerResponse dcResponse, Object[] inputArray) throws MetricsException, ParseException, HttpCallException {
        Result retVal = new Result();
        String email = inputParams.get("UserName");
        logger.debug("SbgCustomerLogin postProcessForEmail ---> email: "+email);

        if (HelperMethods.hasRecords(result)) {
            if (validateUserName(dcRequest, result, email).booleanValue()) {
            	retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
            	updateUserDetails(dcRequest, result, retVal);
            } else {
                Record record = new Record();
                record.setId(DBPUtilitiesConstants.USR_ATTR);
                retVal.addRecord(record);
                ErrorCodeEnum.ERR_10066.setErrorCode(retVal);
            	logger.debug("SbgCustomerLogin postProcessForEmail ---> failed validateusername");
            }
        } else {
            Record record = new Record();
            record.setId(DBPUtilitiesConstants.USR_ATTR);
            retVal.addRecord(record);
            ErrorCodeEnum.ERR_10083.setErrorCode(retVal);
        	logger.debug("SbgCustomerLogin postProcessForEmail ---> hasRecords failed");
        }
        return retVal;
      }

    private Boolean validateUserName(DataControllerRequest dcRequest, Result result, String userName) throws HttpCallException {
        String dbUserName = HelperMethods.getFieldValue(result, "UserName");
        boolean isUserNameValid = false;
        try {
          isUserNameValid = userName.equalsIgnoreCase(dbUserName);
        } catch (Exception exception) {}
        logger.debug("Response from isUserNameValid  : " + isUserNameValid);
        return isUserNameValid; 
      }

    private boolean isUserLockedAfterUpdateCount(String lockcount, PasswordHistoryManagement pm)
            throws HttpCallException {

        boolean isUserLockedAfterUpdateCount = false;
        if (StringUtils.isNotBlank(lockcount)) {
            int count = Integer.parseInt(lockcount);
            isUserLockedAfterUpdateCount = count >= pm.getAccountLockoutThreshold();
        } else
            isUserLockedAfterUpdateCount = false;

        logger.debug("Response from isUserLockedAfterUpdateCount : " + isUserLockedAfterUpdateCount);

        return isUserLockedAfterUpdateCount;

    }

    private boolean isPasswordExpired(DataControllerRequest dcRequest, Record record, PasswordHistoryManagement pm,
            boolean isProspect) throws HttpCallException {
        String customerId = record.getParam("id").getValue();
        return pm.isPasswordExpired(dcRequest, customerId, isProspect);
    }

    private boolean hasPassword(Record user) {
        logger.debug(
                "Response from hasPassword : " + StringUtils.isNotBlank(HelperMethods.getFieldValue(user, "Password")));
        return StringUtils.isNotBlank(HelperMethods.getFieldValue(user, "Password"));
    }

    private boolean esignStatus(Record user) {
        return "true".equalsIgnoreCase(HelperMethods.getFieldValue(user, "isEagreementSigned"));
    }

    private boolean isUserActive(Record user, String mfaName) throws HttpCallException {

        boolean isUserActive = false;
        String status_id = HelperMethods.getFieldValue(user, "Status_id");
        // Ignore below check if request is coming from Onboarding app
        if (status_id.equalsIgnoreCase("SID_CUS_ACTIVE")
                || (StringUtils.isNotEmpty(mfaName) && mfaName.equalsIgnoreCase("Origination"))) {
            isUserActive = true;
        } else {
            isUserActive = false;
        }
        logger.debug("Response from isUserActive : " + isUserActive);
        return isUserActive;
    }

    private boolean isUserSuspended(Record user) throws HttpCallException {

        boolean isUserSuspended = false;

        String status_id = HelperMethods.getFieldValue(user, "Status_id");
        if (status_id.equalsIgnoreCase(InfinityConstants.SID_CUS_SUSPENDED)) {
            isUserSuspended = true;
        } else {
            isUserSuspended = false;
        }

        logger.debug("Response from isUserSuspended : " + isUserSuspended);

        return isUserSuspended;
    }

    private boolean isUserNew(Record user, String mfaName) throws HttpCallException {

        boolean isUserNew = false;

        String status_id = HelperMethods.getFieldValue(user, "Status_id");
        if (status_id.equalsIgnoreCase("SID_CUS_NEW")) {
            isUserNew = true;
        } else {
            isUserNew = false;
        }

        if ((StringUtils.isNotEmpty(mfaName)) && mfaName.equalsIgnoreCase("Origination"))// Skip check for Onboarding app
            isUserNew = false;

        return isUserNew;
    }

    private boolean isUserEsignAgreementReq(Record user, DataControllerRequest dcRequest) throws HttpCallException {

        Map<String, String> input = new HashMap<>();
        input.put("_customerId", HelperMethods.getFieldValue(user, "id"));

        JsonObject response = HelperMethods.callApiJson(dcRequest, input, HelperMethods.getHeaders(dcRequest),
                URLConstants.CUSTOMER_EAGREEMENT_GET);
        if (!response.has("opstatus") || response.get("opstatus").getAsInt() != 0 || !response.has("records")) {
            logger.error("Exception occured while fetching the customer eagreement status");
            return false;
        }
        if (response.has("records") && response.get("records").getAsJsonArray().size() != 0
                && response.get("records").getAsJsonArray().get(0) != null && response.get("records").getAsJsonArray()
                        .get(0).getAsJsonObject().get("isEAgreementActive") != null) {
            return response.get("records").getAsJsonArray().get(0).getAsJsonObject().get("isEAgreementActive")
                    .getAsBoolean();
        }

        return false;
    }

    private Boolean checkIsCoreIdentityScope(Result result) {
        String isCoreIdentityScope = HelperMethods.getFieldValue(result, "IsCoreIdentityScope");

        if (("1".equals(isCoreIdentityScope) || "true".equalsIgnoreCase(isCoreIdentityScope))) {
            logger.debug("Response from checkIsCoreIdentityScope : " + true);
            return true;
        }
        logger.debug("Response from checkIsCoreIdentityScope : " + false);
        return false;
    }

    private Boolean validatePassword(DataControllerRequest dcRequest, Result result, String password)
            throws HttpCallException {
        String dbPassword = HelperMethods.getFieldValue(result, "Password");
        boolean isPasswordValid = false;
        try {
            isPasswordValid = BCrypt.checkpw(password, dbPassword);
        } catch (Exception e) {
        }
        logger.debug("Response from isPasswordValid  : " + isPasswordValid);
        return isPasswordValid;
    }

    private Result postProcessForST(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result,
			String methodID, DataControllerResponse dcResponse, Object[] inputArray) throws HttpCallException {
		Result retVal = new Result();
		if (HelperMethods.hasRecords(result)) {
			dcRequest.addRequestParam_("isCSRAssistMode", "true");
			retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
		} else {
			ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
		}
		logger.debug("Response from postProcessForST : " + ResultToJSON.convert(retVal));
		return retVal;
	}

    private Result postProcessForOnboardingST(Map<String, String> inputParams, DataControllerRequest dcRequest,
            Result result, String methodID, DataControllerResponse dcResponse, Object[] inputArray)
            throws HttpCallException {
        Result retVal = new Result();
        if (HelperMethods.hasRecords(result)) {
            Record user = result.getAllDatasets().get(0).getRecord(0);
            String cusSessionId = HelperMethods.getFieldValue(user, "Session_id");
            String inpSessionId = inputParams.get("session_token");
            if (!StringUtils.equals(inpSessionId, cusSessionId)) {
                ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
            } else {
                retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
            }
        } else {
            ErrorCodeEnum.ERR_10086.setErrorCode(retVal);
        }
        logger.debug("Response from postProcessForOnboardingST : " + ResultToJSON.convert(retVal));
        return retVal;
    }

    private Result postProcessForPin(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> inputParams2, String methodID, DataControllerResponse dcResponse, Object[] inputArray)
            throws MetricsException, ParseException, HttpCallException {
        Result retVal = new Result();
        if (HelperMethods.hasRecords(result)) {
            String deviceId = getDeviceId(dcRequest);
            if (!isDeviceRegistered(dcRequest, deviceId, HelperMethods.getFieldValue(result, "id"))) {
                ErrorCodeEnum.ERR_10117.setErrorCode(retVal);
            } else {

                String inputPin = inputParams.get("pin");
                String dbPing = HelperMethods.getFieldValue(result, "Pin");

                if (StringUtils.isNotBlank(dbPing) && StringUtils.isNotBlank(inputPin) && dbPing.equals(inputPin)) {
                    retVal = sessionAttributes(dcRequest, result, inputParams, methodID, dcResponse, inputArray);
                    updateUserDetails(dcRequest, result, retVal);
                } else {
                    ErrorCodeEnum.ERR_10118.setErrorCode(retVal);
                }
            }
        } else {
            ErrorCodeEnum.ERR_10119.setErrorCode(retVal);
        }

        logger.debug("Response from postProcessForPin : " + ResultToJSON.convert(retVal));
        return retVal;
    }

    private boolean isDeviceRegistered(DataControllerRequest dcRequest, String deviceId, String userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer_id").append(DBPUtilitiesConstants.EQUAL).append(userId).append(DBPUtilitiesConstants.AND)
                .append("Device_id").append(DBPUtilitiesConstants.EQUAL).append(deviceId)
                .append(DBPUtilitiesConstants.AND).append("Status_id").append(DBPUtilitiesConstants.EQUAL)
                .append("SID_DEVICE_REGISTERED");
        Result device = new Result();
        try {
            device = HelperMethods.callGetApi(dcRequest, sb.toString(), HelperMethods.getHeaders(dcRequest),
                    URLConstants.DEVICEREGISTRATION_GET);
        } catch (HttpCallException e) {

            logger.error("Caught exception while getting device registration: ", e);
        }

        logger.debug("Response from isDeviceRegistered  : " + HelperMethods.hasRecords(device));
        return HelperMethods.hasRecords(device);
    }

    private boolean isUserExpired(Record user) {

        String isEnrolled = HelperMethods.getFieldValue(user, "isEnrolled");
        String validDate = HelperMethods.getFieldValue(user, "ValidDate");
        if (("1".equals(isEnrolled) || "true".equalsIgnoreCase(isEnrolled))
                && new Date().after(HelperMethods.getFormattedTimeStamp(validDate))) {
            logger.debug("Response from isUserExpired : " + true);
            return true;
        } else {
            logger.debug("Response from isUserExpired : " + false);
            return false;
        }

    }

    private static void updateUserDetails(DataControllerRequest dcRequest, Result result, Result retVal) {
        Map<String, String> input = new HashMap<>();
        input.put("Lastlogintime", HelperMethods.getCurrentTimeStamp());
        if (StringUtils.isNotBlank(HelperMethods.getFieldValue(result, "CurrentLoginTime"))) {
            try {
                input.put("Lastlogintime", HelperMethods.convertDateFormat(
                        HelperMethods.getFieldValue(result, "CurrentLoginTime"), "yyyy-MM-dd'T'HH:mm:ss"));
            } catch (ParseException e) {
                logger.error("Caught exception while Converting date :", e);
            }
        }
        input.put("id", retVal.getRecordById(DBPUtilitiesConstants.USR_ATTR).getParam("customer_id").getValue());
        input.put("lockCount", "0");
        input.put("CurrentLoginTime", HelperMethods.getCurrentTimeStamp());
        input.put("Session_id", retVal.getRecordById("security_attributes").getParam("session_token").getValue());
        HelperMethods.callApiAsync(dcRequest, input, HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMER_UPDATE);
    }

    private static void updateLockCount(Map<String, String> input, DataControllerRequest dcRequest) {
        HelperMethods.callApiAsync(dcRequest, input, HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMER_UPDATE);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateLockCount(DataControllerRequest dcRequest, Record user, String lockcount)
            throws HttpCallException {
        String id = HelperMethods.getFieldValue(user, "id");
        int count = 1;

        if (StringUtils.isNotBlank(lockcount)) {
            count = Integer.parseInt(lockcount) + 1;
        }
        Map input = new HashMap();
        input.put("id", id);
        input.put("lockCount", String.valueOf(count));
        input.put("lockedOn", HelperMethods.getCurrentTimeStamp());
        updateLockCount(input, dcRequest);
    }

    private boolean isUserLocked(Record user, DataControllerRequest dcRequest, PasswordHistoryManagement pm)
            throws HttpCallException {

        boolean isUserLocked = false;
        String lockcount = HelperMethods.getFieldValue(user, "lockCount");
        if (StringUtils.isNotBlank(lockcount)) {
            int count = Integer.parseInt(lockcount);
            isUserLocked = (count + 1) >= pm.getAccountLockoutThreshold();
        }

        logger.debug("Response from isUserLocked : " + isUserLocked);

        return isUserLocked;
    }

    private boolean tryAutoLockReset(Record user, DataControllerRequest dcRequest, PasswordHistoryManagement pm)
            throws HttpCallException {

        String lockedOn = HelperMethods.getFieldValue(user, "lockedOn");
        int autoUnlockPeriod = pm.getAutoUnLockPeriod();
        Date today = HelperMethods.getFormattedTimeStamp(HelperMethods.getCurrentTimeStamp());
        Calendar cal = Calendar.getInstance();
        cal.setTime(HelperMethods.getFormattedTimeStamp(lockedOn));
        cal.add(Calendar.MINUTE, autoUnlockPeriod);
        if (-1 != autoUnlockPeriod && today.after(cal.getTime())) {
            logger.debug("Response from tryAutoLockReset : " + true);
            return true;
        }
        logger.debug("Response from tryAutoLockReset : " + false);
        return false;

    }

    private Result sessionAttributes(DataControllerRequest dcRequest, Result result, Map<String, String> inputParams,
            String methodID, DataControllerResponse dcResponse, Object[] inputArray) {
        Result retVal = result;
        Dataset ds = result.getDatasetById("customer");
        Record sessionAttr = new Record();
        sessionAttr.setId("security_attributes");

        Record usrAttr = ds.getRecord(0);

        usrAttr.setId(DBPUtilitiesConstants.USR_ATTR);
        usrAttr.addParam(new Param("customer_id", HelperMethods.getFieldValue(result, "id"), "String"));
        usrAttr.addParam(new Param("UserName", HelperMethods.getFieldValue(result, "UserName"), "String"));
        String token = SessionManager.createSession(usrAttr.getParam("customer_id").getValue());

        Result cusComm = new Result();

        inputParams.put("id", HelperMethods.getFieldValue(result, "id"));

        if (StringUtils.isNotBlank(getDeviceId(dcRequest))) {
            usrAttr.addParam(new Param(DBPUtilitiesConstants.IS_DEVICE_REGISTERED,
                    isDeviceRegistered(dcRequest, getDeviceId(dcRequest), inputParams.get("id")) + ""));
        }

        cusComm = (Result) new GetCustomerPreferencesConcurrent().invoke(methodID, inputArray, dcRequest, dcResponse);

        logger.debug("Response from CustomerPreferencesConcurrent : " + ResultToJSON.convert(cusComm));
        for (Param param : cusComm.getAllParams()) {
            usrAttr.addParam(param);
        }

        usrAttr.addParam("userFirstName", usrAttr.getParamValueByName("FirstName"));
        usrAttr.addParam("userLastName", usrAttr.getParamValueByName("LastName"));
        usrAttr.addParam("gender", usrAttr.getParamValueByName("Gender"));
        usrAttr.addParam("isPinSet", usrAttr.getParamValueByName("IsPinSet"));
        usrAttr.addParam("noofdependents", usrAttr.getParamValueByName("NoOfDependents"));
        usrAttr.addParam("spousefirstname", usrAttr.getParamValueByName("SpouseName"));
        usrAttr.addParam("userImage", usrAttr.getParamValueByName("UserImage"));
        usrAttr.addParam("ssn", usrAttr.getParamValueByName("Ssn"));
        usrAttr.addParam("taxid", usrAttr.getParamValueByName("Ssn"));
        usrAttr.addParam("maritalstatus", usrAttr.getParamValueByName("MaritalStatus_id"));
        usrAttr.addParam("lastlogintime", usrAttr.getParamValueByName("Lastlogintime"));
        usrAttr.addParam("isCombinedUser", usrAttr.getParamValueByName("isCombinedUser"));
        usrAttr.addParam("organizationType", usrAttr.getParamValueByName("organizationType"));
        usrAttr.addParam(new Param("CSR_User_Id",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSR_User_Id")) ? dcRequest.getAttribute("CSR_User_Id")
                        : "",
                "String"));
        usrAttr.addParam(new Param("CSR_Role",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSR_Role")) ? dcRequest.getAttribute("CSR_Role") : "",
                "String"));
        usrAttr.addParam(new Param("CSR_Name",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSR_Name")) ? dcRequest.getAttribute("CSR_Name") : "",
                "String"));
        usrAttr.addParam(new Param("CSR_Username",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSR_Username")) ? dcRequest.getAttribute("CSR_Username")
                        : "",
                "String"));
        usrAttr.addParam(new Param("user_type",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSRAssist_User_Type"))
                        ? dcRequest.getAttribute("CSRAssist_User_Type")
                        : "",
                "String"));
        usrAttr.addParam(new Param("CustomerUsername",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSRAssist_Customer_username"))
                        ? dcRequest.getAttribute("CSRAssist_Customer_username")
                        : "",
                "String"));
        usrAttr.addParam(new Param("CustomerId",
                StringUtils.isNotBlank(dcRequest.getAttribute("CSRAssist_Customer_id"))
                        ? dcRequest.getAttribute("CSRAssist_Customer_id")
                        : "",
                "String"));
        usrAttr.addParam(new Param("accountId",
                StringUtils.isNotBlank(dcRequest.getAttribute("accountId"))
                        ? dcRequest.getAttribute("accountId")
                        : "",
                "String"));
        usrAttr.addParam(new Param("customerTypeId",
                StringUtils.isNotBlank(HelperMethods.getFieldValue(result, "CustomerType_id"))
                        ? HelperMethods.getFieldValue(result, "CustomerType_id")
                        : "",
                "String"));

        sessionAttr.addParam(new Param("permissions",
                StringUtils.isNotBlank(dcRequest.getAttribute("permissions")) ? dcRequest.getAttribute("permissions")
                        : "",
                "String"));

        sessionAttr.addParam(new Param("features",
                StringUtils.isNotBlank(dcRequest.getAttribute("features")) ? dcRequest.getAttribute("features") : "",
                "String"));

        String isCSRAssistMode = new String();
        if (StringUtils.isNotBlank(dcRequest.getParameter("isCSRAssistMode"))) {
            isCSRAssistMode = dcRequest.getParameter("isCSRAssistMode");
        }

        if (StringUtils.isBlank(isCSRAssistMode)) {
            usrAttr.addParam(new Param("isCSRAssistMode", "false", "String"));
        } else {
            usrAttr.addParam(new Param("isCSRAssistMode", "true", "String"));
        }

        try {
            usrAttr.addParam(new Param("Lastlogintime", HelperMethods.convertDateFormat(
                    HelperMethods.getFieldValue(result, "CurrentLoginTime"), "yyyy-MM-dd'T'HH:mm:ss"), "String"));
        } catch (ParseException e) {

            logger.error("Caught exception while converting DateFormat: ", e);
        }

        String filterQuery = "Customer_id" + DBPUtilitiesConstants.EQUAL
                + HelperMethods.getFieldValue(usrAttr, "customer_id");
        Result backendIdentifiers = new Result();
        try {
            backendIdentifiers = HelperMethods.callGetApi(dcRequest, filterQuery,
                    HelperMethods.getHeaders(dcRequest), URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {

            logger.error("Caught exception while getting Backend Identifiers: ", e);
        }

        Map<String, String> backend_identifiers = new HashMap<String, String>();
        if (HelperMethods.hasRecords(backendIdentifiers)) {
            backend_identifiers = getBackendIdentifiers(backendIdentifiers);
            usrAttr.addParam(new Param("backendIdentifiers", backend_identifiers.get("backendIdentifier"), "String"));
            if (StringUtils.isNotEmpty(backend_identifiers.get("companyId")))
                usrAttr.addParam(new Param("companyId", backend_identifiers.get("companyId"), "String"));
        }
        if (StringUtils.isBlank(usrAttr.getParamValueByName("companyId"))) {
            usrAttr.addParam(new Param("companyId",
                    EnvironmentConfigurationsHandler.getValue(DBPUtilitiesConstants.BRANCH_ID_REFERENCE)));
        }

        Param sessionId = new Param("session_token", token, DBPConstants.FABRIC_STRING_CONSTANT_KEY);
        Param sessionTtl = new Param("session_ttl", null, DBPConstants.FABRIC_STRING_CONSTANT_KEY);
        sessionAttr.addParam(sessionId);
        sessionAttr.addParam(sessionTtl);
        sessionAttr.addParam(new Param("strongAuth", inputParams.get("strongAuth"), ""));
        sessionAttr.addParam(new Param("strongAuthMethod", inputParams.get("strongAuthMethod"), ""));
        
        retVal.addRecord(usrAttr);
        retVal.addRecord(sessionAttr);
        retVal.removeRecordById("customer");

        logger.debug("Response from sessionAttributes : " + ResultToJSON.convert(retVal));
        return retVal;
    }

    private Map<String, String> getBackendIdentifiers(Result backendIdentifiers) {

        Map<String, String> backend_identifiers = new HashMap<String, String>();
        String returnCompanyId = "";
        List<Record> identifiers = backendIdentifiers.getAllDatasets().get(0).getAllRecords();

        JSONObject json = new JSONObject();

        for (Record record : identifiers) {

            String backendType = HelperMethods.getFieldValue(record, "BackendType");

            Map<String, String> map = new HashMap<>();
            map.put("sequence_number", String.valueOf(HelperMethods.getFieldValue(record, "sequenceNumber")));
            map.put("BackendId", HelperMethods.getFieldValue(record, "BackendId"));
            map.put("identifier_name", HelperMethods.getFieldValue(record, "identifier_name"));
            String companyId = HelperMethods.getFieldValue(record, "CompanyId");
            map.put("CompanyId", companyId);
            map.put("contractId", HelperMethods.getFieldValue(record, "contractId"));
            map.put("contractTypeId", HelperMethods.getFieldValue(record, "contractTypeId"));
            if (StringUtils.isNotEmpty(companyId) && StringUtils.isEmpty(returnCompanyId))
                returnCompanyId = companyId;

            if (json.has(backendType)) {

                JSONArray value = json.getJSONArray(backendType);
                value.put(map);
            } else {

                JSONArray value = new JSONArray();
                value.put(map);
                json.put(backendType, value);
            }
        }
        backend_identifiers.put("backendIdentifier", String.valueOf(json));
        backend_identifiers.put("companyId", returnCompanyId);
        return backend_identifiers;
    }

    private String getDeviceId(DataControllerRequest dcRequest) {

        String reportingParams = dcRequest.getHeader(DBPUtilitiesConstants.X_KONY_REPORTING_PARAMS);
        try {
            reportingParams = URLDecoder.decode(reportingParams, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("Caught exception while Decoding Reporting Params : ", e);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(reportingParams);

            if (reportingParams.contains("did")) {
                return jsonObject.getString("did");
            }
        } catch (JSONException e) {
            logger.error("Caught exception while Getting DeviceId from reporting Params: ", e);
        }

        return "";
    }

    private String getMfaName(DataControllerRequest dcRequest) {
        String reportingParams = dcRequest.getHeader(DBPUtilitiesConstants.X_KONY_REPORTING_PARAMS);
        try {
            reportingParams = URLDecoder.decode(reportingParams, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error("Caught exception while Decoding Reporting Params : ", e);
        }
        try {
            JSONObject jsonObject = new JSONObject(reportingParams);
            if (reportingParams.contains("mfaname")) {
                return jsonObject.getString("mfaname");
            }
        } catch (JSONException e) {
            logger.error("Caught exception while Getting mfaname from reporting Params: ", e);
        }
        return "";
    }
}
