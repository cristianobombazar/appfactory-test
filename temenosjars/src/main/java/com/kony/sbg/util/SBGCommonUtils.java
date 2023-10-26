package com.kony.sbg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kony.sbg.dto.ProofOfPaymentDTO;
import com.temenos.dbx.product.constants.FeatureAction;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.sbg.business.api.SbgInterBankFundTransferBusinessDelegateExtn;
import com.kony.sbg.business.impl.EvaluateTransactionBusinessDelegateImpl;
import com.kony.sbg.javaservices.FetchPingToken;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.ehcache.ResultCacheImpl;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;

public class SBGCommonUtils {
	private static final Logger LOG = Logger.getLogger(SBGCommonUtils.class);
	private static SbgInterBankFundTransferBusinessDelegateExtn sbgInterbankFundTransferDelegate = DBPAPIAbstractFactoryImpl
	.getBusinessDelegate(SbgInterBankFundTransferBusinessDelegateExtn.class);

	public static String getServerPropertyValue(String key, DataControllerRequest dcRequest) throws Exception {
		String value = null;
		value = com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler.getServerAppProperty(key,
				dcRequest);
		return value;
	}

	public static Object generateRandomUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String getCurrentTimeStamp(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = new Date();
		return formatter.format(date);

	}

	public static Long getCurrentTime(String format) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime();

	}
	public static Result cacheFetchPingToken(String key, DataControllerRequest dcRequest) throws Exception {
		Result result2 = new Result();
//		JsonObject jsonObject = new JsonObject();
//		// Sanity checks
//		if (key == null)
//			throw new Exception("Cache key must be provided");
//		String authorization = "";
//		Result resultCache = SBGCommonUtils.cacheFetch("Authorization");
//		if (resultCache != null && resultCache.hasParamByName("Authorization")) {
//			authorization = resultCache.getParamValueByName("Authorization").toString();
//			result2.addParam("Authorization", authorization);
//		} else {
//			jsonObject = FetchPingToken.invokeApi(dcRequest);
//			if (jsonObject == null) {
//				return result2;
//			}
//			String auth = "Bearer ";
//			String token = jsonObject.get("access_token").getAsString();
//			String authVal = auth + token;
//			result2.addParam("Authorization", authVal);
//			cacheInsert("Authorization", result2, 0);
//			// return result2;
//		}

		String accessToken		= FetchPingToken.getB2BAccessToken(dcRequest);
		result2.addParam(SBGConstants.AUTHORIZATION, accessToken);

		return result2;
	}
	/**
	 * Fetch an object from the KonyFabric cache
	 *
	 * @param String
	 *            Key for cache
	 * @return Result Result object
	 **/
	public static Result cacheFetch(String key) throws Exception {

		// Sanity checks
		if (key == null)
			throw new Exception("Cache key must be provided");

		// Fetch the result from cache
		ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
		Result result = (Result) resultCache.retrieveFromCache(key);
		return result;
	}

	/**
	 * Insert an object into the KonyFabric cache
	 *
	 * @param String
	 *            Key for cache
	 * @param Result
	 *            The result object to cache
	 * @param int
	 *            Lifetime to cache object for
	 **/
	public static void cacheInsert(String key, Result result, int life) throws Exception {

		// Sanity checks
		if (key == null)
			throw new Exception("Cache key must be provided");
		if (life == 0)
			life = 90;

		// Only bother if we have something to cache
		if (result != null) {

			// Insert the result into the cache
			ResultCache resultCache = (ResultCache) ResultCacheImpl.getInstance();
			resultCache.insertIntoCache(key, result);
		}
	}

	/**
	 * Call an integration service operation
	 *
	 * @param setRequest
	 *            TODO
	 * @param DataControllerRequest
	 *            The request object
	 * @param Hashmap
	 *            The hashmap containing the request input parameters
	 * @param Hashmap
	 *            The hashmap containing the request headers
	 * @param String
	 *            The name of the service operation to call
	 * @return Result Result object
	 **/
	public static Result callIntegrationService(DataControllerRequest request, Map<String, Object> params,
			Map<String, Object> headers, String serviceName, String operationName, boolean setRequest)
			throws Exception {

		// Create service data
		OperationData serviceData = createOperationData(request, serviceName, operationName);

		// Create service request
		ServiceRequest serviceRequest = createServiceRequest(request, serviceData, params, headers, setRequest);

		// Ok now call the service
		Result result = serviceRequest.invokeServiceAndGetResult();
		return result;
	}

	/**
	 * Create an Operation Data Object
	 *
	 * @param DataControllerRequest
	 *            Request object
	 * @param String
	 *            Service Name
	 * @param String
	 *            Operation Name
	 * @return OperationData An operation data object
	 **/
	private static OperationData createOperationData(DataControllerRequest request, String service, String operation)
			throws Exception {

		// Generate OperationData object
		OperationData serviceData = request.getServicesManager().getOperationDataBuilder().withServiceId(service)
				.withOperationId(operation).build();

		return serviceData;
	}

	/**
	 * Create a Service Request Object
	 *
	 * @param setRequest
	 *            TODO
	 * @param DataControllerRequest
	 *            Request object
	 * @param OperationData
	 *            Operation Data object
	 * @param Map<String,Object>
	 *            Input parameters map
	 * @param Map<String,Object>
	 *            Headers map
	 * @return ServiceRequest A service request object
	 **/
	private static ServiceRequest createServiceRequest(DataControllerRequest request, OperationData serviceData,
			Map<String, Object> params, Map<String, Object> headers, boolean setRequest) throws Exception {
		ServiceRequest serviceRequest;
		if (setRequest) {
			serviceRequest = request.getServicesManager().getRequestBuilder(serviceData).withInputs(params)
					.withDCRRequest(request).withHeaders(headers).build();
		} else {
			serviceRequest = request.getServicesManager().getRequestBuilder(serviceData).withInputs(params)
					.withHeaders(headers).build();
		}
		// Generate ServiceRequest object

		return serviceRequest;
	}

	public static String buildOdataCondition(String field, String operator, String value) {

		StringBuilder sb = new StringBuilder();
		sb.append(field);
		sb.append(" ");
		sb.append(operator);
		sb.append(" ");
		sb.append(value);
		return sb.toString();
	}

	// To Frame the ODataQuery filter for LIKE operator
	public static String buildOdataConditionForLikeOperator(String field, String value) {
		StringBuilder sb = new StringBuilder();
		//sb.append(" substringof('");
		sb.append(" startswith('");
		sb.append(field);
		sb.append("', '");
		sb.append(value);
		sb.append("') ");
		sb.append(" eq true ");
		return sb.toString();
	}

	public static Boolean isStringEmpty(Object key) {
		if (key == null) {
			return true;
		}
		String str = key.toString();
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static Boolean isStringNotEmpty(Object key) {
		return !isStringEmpty(key);
	}

	public static String getFormattedDate(String fromFormat, String toFormat, String date) throws ParseException {
		SimpleDateFormat fromFormatter = new SimpleDateFormat(fromFormat);
		SimpleDateFormat toFormatter = new SimpleDateFormat(toFormat);
		return toFormatter.format(fromFormatter.parse(date));
	}

	/**
	 * Decode JWT token
	 *
	 * @param token
	 *
	 * @return body as a string
	 **/
	public static String DecodeJWT(String pingtoken) throws Exception {
		String jwtToken = pingtoken;
		String[] split_string = jwtToken.split("\\.");
		// String base64EncodedHeader = split_string[0];
		// String base64EncodedSignature = split_string[2];
		String base64EncodedBody = split_string[1];
		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));
		return body;
	}

	/**
	 * ClientID validation
	 *
	 * @param client_id
	 *
	 * @return boolean value
	 * @throws Exception
	 **/
	public static boolean validateClientID(String clientId, DataControllerRequest request) throws Exception {

		String Client_ID_Value = getServerPropertyValue(SbgURLConstants.PING_CLIENT_ID, request);
		if (Client_ID_Value.equals(clientId)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ISS validation
	 *
	 * @param client_id
	 *
	 * @return boolean value
	 * @throws Exception
	 **/
	public static boolean validateISS(String Iss, DataControllerRequest request) throws Exception {
		String ISS_URL = getServerPropertyValue(SbgURLConstants.PING_ISS_URL, request);
		if (ISS_URL.equals(Iss)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get UTC time
	 *
	 * @return
	 * @throws ParseException
	 */
	public static Date getCurrentUtcTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = null;
		// use try catch block to parse date in UTC time zone
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(sdf.format(new Date()));
		}
		// catch block for handling ParseException
		catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		// pass UTC date to main method.
		return d1;
	}

	/**
	 * time stamp compare default timestamp format is "yyyy-MM-dd HH:mm:ss"
	 *
	 * @return boolean value
	 */
	public static boolean timeStampCompare(Date date) throws ParseException {
		DateFormat formatterUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
		String ExpDate = formatterUTC.format(date);

		Date UTCdate = null;
		String todayDate = null;
		try {
			UTCdate = SBGCommonUtils.getCurrentUtcTime();
			todayDate = formatterUTC.format(UTCdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp ts1 = Timestamp.valueOf(ExpDate);
		Timestamp ts2 = Timestamp.valueOf(todayDate);
		// compares ts1 with ts2
		int b3 = ts1.compareTo(ts2);
		if (b3 > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getEvaluationParamValue(Map<String, Object> evalOutput, String param) {
		String value = "";
		if (evalOutput != null && evalOutput.size() > 0) {
			Map<String, Object> evaluationResult = new HashMap<String, Object>();
			evaluationResult.put("EvaluationResult", evalOutput.get("EvaluationResult"));
			Map<String, Object> evaluations = (Map<String, Object>) evaluationResult.get("EvaluationResult");
			ArrayList<Map<String, Object>> evaluationList = (ArrayList<Map<String, Object>>) evaluations
					.get("Evaluations");
			for (Map<String, Object> evaluation : evaluationList) {
				Record record = new Record();
				for (Map.Entry<String, Object> entry : evaluation.entrySet()) {
					if (entry.getValue() != null) {
						if (entry.getKey().equals(param)) {
							value = entry.getValue().toString();
							//[AH] applying break to avoid looping thru all the params in decision
							break;
						}
					}
				}
				//[AH] applying break so that the data will be pulled from first evaluation decision only.
				break;
			}
		}
		return value;
	}

	public	static boolean isBoPReportable(Map<String, Object> evalOutput) {

		String decision 			= getEvaluationParamValue(evalOutput, "Decision");
		String reportingSide 		= getEvaluationParamValue(evalOutput, "ReportingSide");
		String reportingQualifier 	= getEvaluationParamValue(evalOutput, "ReportingQualifier");

		if("ReportToRegulator".equals(decision) && "DR".equals(reportingSide) && "BOPCUS".equals(reportingQualifier)) {
			return true;
		}

		if("ReportToRegulator".equals(decision) && "DR".equals(reportingSide) && "NON REPORTABLE".equals(reportingQualifier)) {
			return true;
		}

		if("ReportToRegulator".equals(decision) && "CR".equals(reportingSide)) {
			return false;
		}

		if("DoNotReport".equals(decision)) {
			return false;
		}

		if("Illegal".equals(decision) || "Obsolete".equals(decision) || "Invalid".equals(decision) || "Unknown".equals(decision)) {
			return false;
		}

		return false;
	}

	public static String getReportData(JSONObject requestPayload, Map<String, Object> evalOutput, String ReportsMetaDir,
			String ReportsReportDir, DataControllerRequest dcRequest, Result result) {
		// calling BOP report param

		Map<String, String> schemas = new HashMap<String, String>();
		schemas.put("Meta", readFromResource(ReportsMetaDir));
		schemas.put("Report", readFromResource(ReportsReportDir));
		LOG.error("SBGCommonUtils::getReportData ---> schemas: "+schemas);

		JSONObject reportResult=new JSONObject();
		try {
			Map<String, String> schemaInputPArams = getSchemaInputParams(requestPayload, evalOutput, dcRequest, result);
			LOG.error("SBGCommonUtils::getReportData ---> schemaInputPArams: "+schemaInputPArams);
			reportResult = buildSchema(schemas, schemaInputPArams);
			LOG.error("SBGCommonUtils::getReportData ---> reportResult: "+reportResult);
		}catch(Exception e){
			//e.printStackTrace();
			LOG.debug("SBGCommonUtils:::exce="+e.getStackTrace());
			LOG.debug("SBGCommonUtils:::message="+e.getMessage());
			LOG.debug("SBGCommonUtils:::message="+e.getLocalizedMessage());
		}
		LOG.debug("SBGCommonUtils:::report="+reportResult.toString());
		return reportResult.toString();
		// call ends
	}

	private	static Map<String, String> getSchemaInputParams(JSONObject requestPayload, Map<String, Object> evalOutput,
			DataControllerRequest dcRequest, Result result) {

		Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);

		Map<String, String> userDetails = getCoreCustomerId(CustomerSession.getCustomerId(customer), dcRequest);
		String corecustomerId = userDetails.get("coreCustomerId");

		//String OSD_Param = "BOP_"+corecustomerId+"_EXTENSIONDATA";
		//corecustomerId storing in cache for submit payment purpose
		Result result2 = new Result();
		result2.addParam("CoreCustomerId",corecustomerId);
		try {
			cacheInsert("CoreCustomerId",result2,0);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		String OSDData = null;
//		JSONObject json = null;
//		try {
//			OSDData = getClientAppValue(OSD_Param, dcRequest);
//			json = new JSONObject(OSDData);
//			// LOG.debug("SBGCommonUtils::::taxNumber"+json.getString("taxNumber"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		LOG.debug("SBGCommonUtils:::OSDData="+OSDData);

		JSONObject json = SBGUtil.getCompanyData4mParty(dcRequest);
		JSONObject extnData = json.getJSONObject("extensionData");
		//[AH] Not checking for null. In case of null, the resource layer itself would through 100037 error code.

		String debitbic = requestPayload.getString("debitAccountSwiftCode");
		String creditbic = requestPayload.getString("creditSwiftCode");
		String FlowCurrency = requestPayload.getString("creditCurrency");
		// from evaluation result
		String flow = SBGCommonUtils.getEvaluationParamValue(evalOutput, "Flow");
		String ReportingQualifier = SBGCommonUtils.getEvaluationParamValue(evalOutput, "ReportingQualifier");
		String getResException = SBGCommonUtils.getEvaluationParamValue(evalOutput, "ResException");
		String getNonResException = SBGCommonUtils.getEvaluationParamValue(evalOutput, "NonResException");
		String getLocationCountry = SBGCommonUtils.getEvaluationParamValue(evalOutput, "LocationCountry");
		String getNonResAccountType = SBGCommonUtils.getEvaluationParamValue(evalOutput, "NonResAccountType");
		String getResAccountType = SBGCommonUtils.getEvaluationParamValue(evalOutput, "ResAccountType");
		if(StringCompare(flow,"Outflow")) {
			flow="OUT";
		}else if(StringCompare(flow,"Inflow")) {
			flow="IN";
		}
		Map<String, String> inputparamsFromEvalRes = new HashMap<String, String>();
		inputparamsFromEvalRes.put("ReceivingCountry", creditbic.substring(4, 6));
		inputparamsFromEvalRes.put("OriginatingCountry", debitbic.substring(4, 6));
		inputparamsFromEvalRes.put("Flow", flow);
		inputparamsFromEvalRes.put("ReportingQualifier", ReportingQualifier);
		inputparamsFromEvalRes.put("OriginatingBank", debitbic);
		inputparamsFromEvalRes.put("ReceivingBank", creditbic);
		inputparamsFromEvalRes.put("FlowCurrency", FlowCurrency);
		inputparamsFromEvalRes.put("PaymentDate", DateFormat(requestPayload.getString("PaymentDate")));
		inputparamsFromEvalRes.put("PaymentAmount", requestPayload.getString("PaymentAmount"));
		inputparamsFromEvalRes.put("BeneAccountNumber", requestPayload.getString("BeneAccountNumber"));
		inputparamsFromEvalRes.put("AddressLine1", requestPayload.getString("AddressLine1"));
		inputparamsFromEvalRes.put("AddressLine2", requestPayload.getString("AddressLine2"));
		inputparamsFromEvalRes.put("City", requestPayload.getString("City"));
		inputparamsFromEvalRes.put("Country", requestPayload.getString("Country"));
		inputparamsFromEvalRes.put("PostalCode", requestPayload.getString("PostalCode"));
		inputparamsFromEvalRes.put("State", requestPayload.getString("State"));
		inputparamsFromEvalRes.put("beneName", requestPayload.getString("beneName"));
		inputparamsFromEvalRes.put("beneSurname", requestPayload.getString("beneSurname"));
		inputparamsFromEvalRes.put("beneType", requestPayload.getString("beneType"));
		inputparamsFromEvalRes.put("FromAccountNumber", requestPayload.getString("FromAccountNumber"));

		if (!SBGCommonUtils.isStringEmpty(getResException)) {
			inputparamsFromEvalRes.put("getResException", getResException);
		} else {
			inputparamsFromEvalRes.put("getResException", "null");
		}

		if (!SBGCommonUtils.isStringEmpty(getNonResException)) {
			inputparamsFromEvalRes.put("getNonResException", getNonResException);
		} else {
			inputparamsFromEvalRes.put("getNonResException", "null");
		}

		if (!SBGCommonUtils.isStringEmpty(getLocationCountry)) {
			inputparamsFromEvalRes.put("getLocationCountry", getLocationCountry);
		} else {
			inputparamsFromEvalRes.put("getLocationCountry", "null");
		}
		// inputparamsFromEvalRes.put("State", requestPayload.getString("State"));
		// input from OSD Data

		String cpe[] = getCodePhoneAddress(json.getJSONArray("addresses"));
		String phonenumber = cpe[1];
		if (phonenumber.length() > 9) {
			inputparamsFromEvalRes.put("contactPhoneNumber", phonenumber);
		} else {
			inputparamsFromEvalRes.put("contactPhoneNumber", appendZero2PhoneNo(phonenumber));
		}

//		String phonenumber = json.getString("contactPhoneNumber");
//		if (phonenumber.length() > 9) {
//			inputparamsFromEvalRes.put("contactPhoneNumber", json.getString("contactPhoneNumber"));
//		} else {
//			inputparamsFromEvalRes.put("contactPhoneNumber", appendZero2PhoneNo(json.getString("contactPhoneNumber")));
//		}

		inputparamsFromEvalRes.put("TaxNumber", extnData.getString("taxNumber"));
		inputparamsFromEvalRes.put("VATNumber", extnData.getString("vatNumber"));
		inputparamsFromEvalRes.put("EntityName", extnData.getString("entityName"));
		inputparamsFromEvalRes.put("IndustrialClassification", extnData.getString("industrialClassification"));
		inputparamsFromEvalRes.put("InstitutionalSector", extnData.getString("institutionalSector"));
		inputparamsFromEvalRes.put("RegistrationNumber", extnData.getString("registrationNumber"));
		inputparamsFromEvalRes.put("TradingName", extnData.getString("tradingName"));
		inputparamsFromEvalRes.put("contactFirstName", json.getString("firstName"));
		inputparamsFromEvalRes.put("contactLastName", json.getString("lastName"));
		inputparamsFromEvalRes.put("contactPhoneCountryCode", cpe[0]);

		inputparamsFromEvalRes.put("PostalAddress.AddressLine1", extnData.getString("legaladdressLine1"));
		inputparamsFromEvalRes.put("PostalAddress.AddressLine2", extnData.getString("legaladdressLine2"));
		inputparamsFromEvalRes.put("PostalAddress.suburb", extnData.getString("legaladdressLine3"));

		inputparamsFromEvalRes.put("PostalAddress.City", extnData.getString("legalcityName"));
		inputparamsFromEvalRes.put("PostalAddress.Country", extnData.getString("legalcountry"));
		inputparamsFromEvalRes.put("PostalAddress.PostalCode", extnData.getString("legalzipCode"));
		inputparamsFromEvalRes.put("PostalAddress.State", extnData.getString("legalstate"));
		inputparamsFromEvalRes.put("contactEmail", cpe[2]);
		inputparamsFromEvalRes.put("entityType", extnData.getString("entityType"));
			// for individual changes

		inputparamsFromEvalRes.put("DateOfBirth", "");// json.getString("DateOfBirth")

		inputparamsFromEvalRes.put("ForeignIDCountry", "");// json.getString("ForeignIDCountry")
		inputparamsFromEvalRes.put("Gender", "");// json.getString("Gender")
		inputparamsFromEvalRes.put("ForeignIDNumber", "");//// json.getString("ForeignIDNumber")
		inputparamsFromEvalRes.put("TempResPermitNumber", "");// json.getString("TempResPermitNumber")
		// inputParams.put("Individual.CustomsClientNumber", "CustomsClientNumber1");
		//inputparamsFromEvalRes.put("AccountIdentifier", "AccountIdentifier1");// json.getString("AccountIdentifier")
		inputparamsFromEvalRes.put("TaxClearanceCertificateIndicator", "");// json.getString("TaxClearanceCertificateIndicator")
		inputparamsFromEvalRes.put("PassportNumber", "");// json.getString("PassportNumber")
		inputparamsFromEvalRes.put("TaxClearanceCertificateReference", "");
		inputparamsFromEvalRes.put("IDNumber", "");
		inputparamsFromEvalRes.put("PassportCountry", "");
		// account identifier
		inputparamsFromEvalRes.put("getNonResAccountType", getNonResAccountType);
		inputparamsFromEvalRes.put("getResAccountType", getResAccountType);
		return inputparamsFromEvalRes;
	}

	public	static String[] getCodePhoneAddress(JSONArray address) {
		LOG.debug("SBGCommonUtils.getCodePhoneAddress ---> address: "+address);
		String retval[]	= {"","",""};

		String email 	= "";
		String phnum	= "";

		int len = address.length();
		for(int i=0 ; i<len ; ++i) {
			JSONObject jobj = (JSONObject)address.get(i);
			if("Electronic".equals(jobj.getString("communicationNature"))) {
				email = jobj.getString("electronicAddress");
			} else if("Phone".equals(jobj.getString("communicationNature")) || "Mobile".equals(jobj.getString("communicationType"))) {
				phnum = jobj.getString("phoneNo");
				if(SBGCommonUtils.isStringEmpty(phnum))
					phnum = "-";
			}
		}

		String p[] 	= phnum.split("-");
		retval[0]	= p[0];
		retval[1]	= p[1];
		retval[2]	= email;

		LOG.debug("SBGCommonUtils.getCodePhoneAddress ---> retval: "+retval[0]+";"+retval[1]+";"+retval[2]);
		return retval;
	}

	//function to append 0's before mobile# if the length of mobile number is less than 10
	private	static String appendZero2PhoneNo(String mobno) {
		String zeros = "0000000000";

		if(isStringEmpty(mobno)) {
			return zeros;
		}

		int moblen = mobno.length();
		return zeros.substring(moblen)+mobno;
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static String readFromResource(String fileName) {
		try {
			InputStream artefacartefactStreamtStream = EvaluateTransactionBusinessDelegateImpl.class.getClassLoader()
					.getResourceAsStream(fileName);
			return convertStreamToString(artefacartefactStreamtStream);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// schema builder
	private static JSONObject buildSchema(Map<String, String> schemas, Map<String, String> inputMapFromEvalRes) {
		Map<String, String> template = new HashMap<String, String>();
		Map<String, String> inputParams = new HashMap<String, String>();
		Boolean isResException = false; // getResException : null
		if (!"null".equals(inputMapFromEvalRes.get("getResException"))) {
			isResException = true;
		}
		Boolean isResEntity; // is debit party entity

		if (StringCompare(inputMapFromEvalRes.get("entityType"),"Entity")) {
			isResEntity = true;
		}else {
			isResEntity=false;
		}
		Boolean isNonResException = false; // getNonResException : null
		if (!"null".equals(inputMapFromEvalRes.get("getNonResException"))) {
			isNonResException = true;
		}
		Boolean isNonResEntity; // is credit party entity		
		if (StringCompare(inputMapFromEvalRes.get("beneType"),"Entity")) {
			isNonResEntity = true;
		}else {
			isNonResEntity=false;
		}
		{
			// #/definitions/Meta
			template.put("Meta", "true");
			inputParams.put("Meta.DealerType", "AD");
			inputParams.put("Meta.LUClient", "LUClient");
			inputParams.put("Meta.readOnly", "false");
		}
		LOG.error("SBGCommonUtils::buildSchema ---> isResException: "+isResException+"; isResEntity: "+isResEntity+"; isNonResException: "+isNonResException+"; isNonResEntity: "+isNonResEntity);
		{
			// #/definitions/Report
			template.put("Report", "true");
			inputParams.put("Report.DomesticCurrency", "ZAR");// "Default to ZAR (For South Africa) - derived from
																// ChannelName"
			inputParams.put("Report.ReceivingCountry", inputMapFromEvalRes.get("ReceivingCountry")); // 5th and 6th char
																										// // account
																										// BIC
			inputParams.put("Report.OriginatingCountry", inputMapFromEvalRes.get("OriginatingCountry")); // 5th and 6th
																											// ordering
																											// account
																											// BIC
			inputParams.put("Report.ReportingQualifier", inputMapFromEvalRes.get("ReportingQualifier")); // From
																											// Evaluation
																											// engine
			inputParams.put("Report.Flow", inputMapFromEvalRes.get("Flow")); // From Evaluation engine
			inputParams.put("Report.OriginatingBank", inputMapFromEvalRes.get("OriginatingBank")); // Ordering account
																									// BIC SWIFT address
			inputParams.put("Report.ReceivingBank", inputMapFromEvalRes.get("ReceivingBank")); // Receiving account BIC
																								// SWIFT address
			inputParams.put("Report.FlowCurrency", inputMapFromEvalRes.get("FlowCurrency")); // Payment currency
			inputParams.put("Report.ValueDate", inputMapFromEvalRes.get("PaymentDate")); // Payment value date
			inputParams.put("Report.TotalForeignValue", inputMapFromEvalRes.get("PaymentAmount")); // Payment amount
			// inputParams.put("Report.DrCr", "DrCr1");
			// inputParams.put("Report.CorrespondentBank", "CorrespondentBank1");
			inputParams.put("Report.TrnReference",CommonUtils.generateUniqueIDHyphenSeperated(0, 30));
			// inputParams.put("Report.ReplacementTransaction", "ReplacementTransaction1");
			// inputParams.put("Report.CorrespondentCountry", "CorrespondentCountry1");
			// inputParams.put("Report.HubCode", "HubCode1");
			// inputParams.put("Report.HubName", "HubName1");
			// inputParams.put("Report.TotalValue", "TotalValue1");
			// inputParams.put("Report.BranchName", "BranchName1");
			// inputParams.put("Report.ReplacementOriginalReference", "..Reference1");
			// inputParams.put("Report.BranchCode", "BranchCode1");
		}
		LOG.error("SBGCommonUtils::buildSchema ---> Level1 --- inputParams: "+inputParams);

		{
			template.put("Resident", "true");
			template.put("NonResident", "true");
		}
		if (isResException) {
			// #/definitions/Exception
			template.put("Exception", "true");
			inputParams.put("Exception.Country", inputMapFromEvalRes.get("getLocationCountry")); // getLocationCountry
			inputParams.put("Exception.ExceptionName", inputMapFromEvalRes.get("getResException")); // getResException
		} else if (isResEntity) {
			// #/definitions/Entity

			template.put("Entity", "true");
			// inputParams.put("Entity.CardNumber", "CardNumber1");
			inputParams.put("Entity.VATNumber", inputMapFromEvalRes.get("VATNumber"));
			inputParams.put("Entity.InstitutionalSector", inputMapFromEvalRes.get("InstitutionalSector"));
			inputParams.put("Entity.EntityName", inputMapFromEvalRes.get("EntityName"));
			inputParams.put("Entity.IndustrialClassification", inputMapFromEvalRes.get("IndustrialClassification"));
			//inputParams.put("Entity.AccountNumber", inputMapFromEvalRes.get("BeneAccountNumber"));
			inputParams.put("Entity.AccountNumber", inputMapFromEvalRes.get("FromAccountNumber"));
			// inputParams.put("Entity.CustomsClientNumber", "CustomsClientNumber1");
			inputParams.put("Entity.AccountIdentifier", inputMapFromEvalRes.get("getResAccountType"));
			LOG.debug("SBGCommonUtils::inputmap==:getResAccountType="+inputMapFromEvalRes.get("getResAccountType"));
			// inputParams.put("Entity.TaxClearanceCertificateIndicator",
			// "TaxClearanceCertificateIndicator1");
			// inputParams.put("Entity.SupplementaryCardIndicator",
			// "SupplementaryCardIndicator1");
			// inputParams.put("Entity.TaxClearanceCertificateReference",
			// "TaxClearanceCertificateReference1");
			inputParams.put("Entity.RegistrationNumber", inputMapFromEvalRes.get("RegistrationNumber"));
			inputParams.put("Entity.TradingName", inputMapFromEvalRes.get("TradingName"));
			inputParams.put("Entity.TaxNumber", inputMapFromEvalRes.get("TaxNumber"));
			inputParams.put("Entity.AccountName", inputMapFromEvalRes.get("EntityName"));// entityname

			// #/definitions/PostalAddress
			template.put("PostalAddress", "true");
			inputParams.put("PostalAddress.AddressLine2", inputMapFromEvalRes.get("PostalAddress.AddressLine2"));
			inputParams.put("PostalAddress.AddressLine1", inputMapFromEvalRes.get("PostalAddress.AddressLine1"));
			inputParams.put("PostalAddress.Suburb", inputMapFromEvalRes.get("PostalAddress.suburb"));
			inputParams.put("PostalAddress.PostalCode", inputMapFromEvalRes.get("PostalAddress.PostalCode"));
			inputParams.put("PostalAddress.City", inputMapFromEvalRes.get("PostalAddress.City"));
			inputParams.put("PostalAddress.Province", StringUtils.upperCase(inputMapFromEvalRes.get("PostalAddress.State")));

			// #/definitions/StreetAddress
			template.put("StreetAddress", "true");
			inputParams.put("StreetAddress.AddressLine2", inputMapFromEvalRes.get("PostalAddress.AddressLine2"));
			inputParams.put("StreetAddress.AddressLine1", inputMapFromEvalRes.get("PostalAddress.AddressLine1"));
			inputParams.put("StreetAddress.Suburb", inputMapFromEvalRes.get("PostalAddress.suburb"));
			inputParams.put("StreetAddress.PostalCode", inputMapFromEvalRes.get("PostalAddress.PostalCode"));
			inputParams.put("StreetAddress.City", inputMapFromEvalRes.get("PostalAddress.City"));
			inputParams.put("StreetAddress.Province", StringUtils.upperCase(inputMapFromEvalRes.get("PostalAddress.State")));

			// #/definitions/ContactDetails
			template.put("ContactDetails", "true");
			inputParams.put("ContactDetails.ContactSurname", inputMapFromEvalRes.get("contactLastName"));
			inputParams.put("ContactDetails.Email", inputMapFromEvalRes.get("contactEmail"));
			inputParams.put("ContactDetails.Telephone", inputMapFromEvalRes.get("contactPhoneNumber"));
			// inputParams.put("ContactDetails.Fax", "Fax1");
			inputParams.put("ContactDetails.ContactName", inputMapFromEvalRes.get("contactFirstName"));
		} else {
			// #/definitions/Individual
			template.put("Individual", "true");
			// inputParams.put("Individual.CardNumber", "CardNumber1");
			inputParams.put("Individual.DateOfBirth", inputMapFromEvalRes.get("DateOfBirth"));
			inputParams.put("Individual.VATNumber", inputMapFromEvalRes.get("VATNumber"));
			inputParams.put("Individual.ForeignIDCountry", inputMapFromEvalRes.get("ForeignIDCountry"));
			inputParams.put("Individual.Gender", inputMapFromEvalRes.get("Gender"));
			inputParams.put("Individual.ForeignIDNumber", inputMapFromEvalRes.get("ForeignIDNumber"));
			//inputParams.put("Individual.AccountNumber", inputMapFromEvalRes.get("BeneAccountNumber"));
			inputParams.put("Individual.AccountNumber", inputMapFromEvalRes.get("FromAccountNumber"));
			inputParams.put("Individual.Name", inputMapFromEvalRes.get("beneName"));
			inputParams.put("Individual.TempResPermitNumber", inputMapFromEvalRes.get("TempResPermitNumber"));
			// inputParams.put("Individual.CustomsClientNumber", "CustomsClientNumber1");
			inputParams.put("Individual.AccountIdentifier", inputMapFromEvalRes.get("getResAccountType"));
			inputParams.put("Individual.TaxClearanceCertificateIndicator",
					inputMapFromEvalRes.get("TaxClearanceCertificateIndicator"));
			inputParams.put("Individual.PassportNumber", inputMapFromEvalRes.get("PassportNumber"));
			// inputParams.put("Individual.SupplementaryCardIndicator",
			// "SupplementaryCardIndicator1");
			inputParams.put("Individual.TaxClearanceCertificateReference", "TaxClearanceCertificateReference1");
			inputParams.put("Individual.TaxNumber", inputMapFromEvalRes.get("TaxNumber"));
			inputParams.put("Individual.Surname", inputMapFromEvalRes.get("beneSurname"));// surname
			inputParams.put("Individual.AccountName", inputMapFromEvalRes.get("EntityName"));
			inputParams.put("Individual.IDNumber", inputMapFromEvalRes.get("IDNumber"));
			inputParams.put("Individual.PassportCountry", inputMapFromEvalRes.get("PassportCountry"));

			// #/definitions/PostalAddress
			template.put("PostalAddress", "true");
			inputParams.put("PostalAddress.AddressLine2", inputMapFromEvalRes.get("PostalAddress.AddressLine2"));
			inputParams.put("PostalAddress.AddressLine1", inputMapFromEvalRes.get("PostalAddress.AddressLine1"));
			inputParams.put("PostalAddress.Suburb", inputMapFromEvalRes.get("PostalAddress.suburb"));
			inputParams.put("PostalAddress.PostalCode", inputMapFromEvalRes.get("PostalAddress.PostalCode"));
			inputParams.put("PostalAddress.City", inputMapFromEvalRes.get("PostalAddress.City"));
			inputParams.put("PostalAddress.Province", StringUtils.upperCase(inputMapFromEvalRes.get("PostalAddress.State")));

			// #/definitions/StreetAddress
			template.put("StreetAddress", "true");
			inputParams.put("StreetAddress.AddressLine2", inputMapFromEvalRes.get("PostalAddress.AddressLine2"));
			inputParams.put("StreetAddress.AddressLine1", inputMapFromEvalRes.get("PostalAddress.AddressLine1"));
			inputParams.put("StreetAddress.Suburb", inputMapFromEvalRes.get("PostalAddress.suburb"));
			inputParams.put("StreetAddress.PostalCode", inputMapFromEvalRes.get("PostalAddress.PostalCode"));
			inputParams.put("StreetAddress.City", inputMapFromEvalRes.get("PostalAddress.City"));
			inputParams.put("StreetAddress.Province", StringUtils.upperCase(inputMapFromEvalRes.get("PostalAddress.State")));

			// #/definitions/ContactDetails
			template.put("ContactDetails", "true");
			inputParams.put("ContactDetails.ContactSurname", inputMapFromEvalRes.get("contactLastName"));
			inputParams.put("ContactDetails.Email", inputMapFromEvalRes.get("contactEmail"));
			inputParams.put("ContactDetails.Telephone", inputMapFromEvalRes.get("contactPhoneNumber"));
			// inputParams.put("ContactDetails.Fax", "Fax1");
			inputParams.put("ContactDetails.ContactName", inputMapFromEvalRes.get("contactFirstName"));
		}
		LOG.error("SBGCommonUtils::buildSchema ---> Level2 --- inputParams: "+inputParams);

		if (isNonResException) {
			// #/definitions/NRException
			template.put("NRException", "true");
			inputParams.put("NRException.ExceptionName", inputMapFromEvalRes.get("getNonResException")); // getNonResException
		} else if (isNonResEntity) {
			// #/definitions/NREntity
			template.put("NREntity", "true");
			// inputParams.put("NREntity.CardMerchantCode", "CardMerchantCode1");
			inputParams.put("NREntity.EntityName", inputMapFromEvalRes.get("beneName"));
			inputParams.put("NREntity.AccountIdentifier", inputMapFromEvalRes.get("getNonResAccountType"));
			LOG.debug("SBGCommonUtils::inputmap==:getNonResAccountType="+inputMapFromEvalRes.get("getNonResAccountType"));
			// inputParams.put("NREntity.CardMerchantName", "CardMerchantName1");
			//inputParams.put("NREntity.AccountNumber", inputMapFromEvalRes.get("FromAccountNumber"));
			inputParams.put("NREntity.AccountNumber", inputMapFromEvalRes.get("BeneAccountNumber"));

			// #/definitions/NRAddress
			template.put("NRAddress", "true");
			// inputParams.put("NRAddress.AddressLine3", "AddressLine31");
			inputParams.put("NRAddress.AddressLine2", inputMapFromEvalRes.get("AddressLine2"));
			inputParams.put("NRAddress.AddressLine1", inputMapFromEvalRes.get("AddressLine1"));
			inputParams.put("NRAddress.Suburb", inputMapFromEvalRes.get("Country"));
			inputParams.put("NRAddress.State", inputMapFromEvalRes.get("State"));
			inputParams.put("NRAddress.Country", inputMapFromEvalRes.get("Country"));
			inputParams.put("NRAddress.PostalCode", inputMapFromEvalRes.get("PostalCode"));
			inputParams.put("NRAddress.City", inputMapFromEvalRes.get("City"));
			LOG.debug("SBGCommonUtils::inputmap==:City="+inputMapFromEvalRes.get("City"));

		} else {
			// #/definitions/NRIndividual
			template.put("NRIndividual", "true");
			inputParams.put("NRIndividual.AccountIdentifier", inputMapFromEvalRes.get("getNonResAccountType"));
			LOG.debug("SBGCommonUtils::inputmap=invidual=:getNonResAccountType="+inputMapFromEvalRes.get("getNonResAccountType"));

			// inputParams.put("NRIndividual.PassportNumber", "");
			// inputParams.put("NRIndividual.Gender", "");
			inputParams.put("NRIndividual.Surname", inputMapFromEvalRes.get("beneSurname"));
			//inputParams.put("NRIndividual.AccountNumber", inputMapFromEvalRes.get("FromAccountNumber"));
			inputParams.put("NRIndividual.AccountNumber", inputMapFromEvalRes.get("BeneAccountNumber"));
			inputParams.put("NRIndividual.Name", inputMapFromEvalRes.get("beneName"));
			// inputParams.put("NRIndividual.PassportCountry", "");

			// #/definitions/NRAddress
			template.put("NRAddress", "true");
			// inputParams.put("NRAddress.AddressLine3", "AddressLine31");
			inputParams.put("NRAddress.AddressLine2", inputMapFromEvalRes.get("AddressLine2"));
			inputParams.put("NRAddress.AddressLine1", inputMapFromEvalRes.get("AddressLine1"));
			inputParams.put("NRAddress.Suburb", inputMapFromEvalRes.get("Country"));
			inputParams.put("NRAddress.State", inputMapFromEvalRes.get("State"));
			inputParams.put("NRAddress.Country", inputMapFromEvalRes.get("Country"));
			inputParams.put("NRAddress.PostalCode", inputMapFromEvalRes.get("PostalCode"));
			inputParams.put("NRAddress.City", inputMapFromEvalRes.get("City"));
		}

		// DRCR getReportingSide
		LOG.debug("SBGCommonUtils::inputParams==:="+inputParams.toString());
		JSONObject report = new JSONObject();
		for (String key : schemas.keySet()) {
			GenV3SchemaParser parser = GenV3SchemaParser.Initilize(schemas.get(key));
			String baseRef = (new JSONObject(schemas.get(key))).getString("$ref");
			report.put(key, parser.parse(baseRef, template, inputParams, false));
		}
		LOG.debug("SBGCommonUtils::report==:="+report);
		return report;
	}

	public static String getClientAppValue(String key, DataControllerRequest dcRequest) throws Exception {
		String value = null;
		value = com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler.getClientAppProperty(key,
				dcRequest);
		return value;
	}

	public static Map<String, String> getCoreCustomerId(String customerId, DataControllerRequest request) {
		Map<String, String> userDetails = new HashMap<>();
		Map<String, Object> requestParams = new HashMap<>();
		String filter = "", coreCustomerId = "", userName = "", userMailId = "";
		filter = "customerId " + DBPUtilitiesConstants.EQUAL + customerId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);

		JSONObject contractCustomerRsp = getContractCustomers(requestParams);

		if (contractCustomerRsp != null && contractCustomerRsp.has("contractcustomers")
				&& contractCustomerRsp.getJSONArray("contractcustomers").length() > 0) {
			JSONObject contractCustomer = contractCustomerRsp.getJSONArray("contractcustomers").getJSONObject(0);
			coreCustomerId = contractCustomer.optString("coreCustomerId");
		} else {
			// populateErrorDetails(resultDTO, SbgErrorCodeEnum.ERR_100036);
		}
		userDetails.put("userName", userName);
		userDetails.put("userEmail", userMailId);
		userDetails.put("coreCustomerId", coreCustomerId);

		return userDetails;
	}

	public static JSONObject getContractCustomersByCoreCustomerId(String coreCustomerId) throws Exception {
		Map<String, String> userDetails = new HashMap<>();
		Map<String, Object> requestParams = new HashMap<>();
		String filter = "coreCustomerId " + DBPUtilitiesConstants.EQUAL + coreCustomerId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);

		JSONObject contractCustomers = getContractCustomers(requestParams);
		if (Objects.isNull(contractCustomers)) {
			throw new Exception("Error on retrieve contractCustomer for coreCustomerID: " + coreCustomerId);
		}
		return contractCustomers;
	}

	public static JSONObject getContractCustomers(Map<String, Object> requestParams) {
		String serviceName = SbgURLConstants.SERVICE_DBPRBLOCALSERVICESDB;
		String operationName = SbgURLConstants.OPERATION_DBXDB_CONTRACTCUSTOMERS_GET;
		JSONObject serviceResponse = null;
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParams).build().getResponse();
			serviceResponse = new JSONObject(updateResponse);
		}

		catch (JSONException jsonExp) {
			return null;
		} catch (Exception exp) {
			return null;
		}

		return serviceResponse;
	}

	public static String nullCheckRequestParam(String param) {
		if (SBGCommonUtils.isStringEmpty(param) || "undefined".equals(param)) {
			param = " ";
		}
		return param;

	}
	public static Boolean StringCompare(String param1,String param2) {
		if(org.apache.commons.lang3.StringUtils.compare(param1,param2)==0) {
			return true;
		}else
		{
			return false;
		}

	}
	public static String DateFormat(String date) {
		 Date date1;
		 String formattedDate="";
			try {
				date1 = new SimpleDateFormat("dd/mm/yyyy").parse(date);
				  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
				  formattedDate = formatter.format(date1);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return formattedDate;
	}

	public static TransactionStatusEnum setTransactionStatusMessage(TransactionStatusEnum transactionStatus) {
		return setTransactionStatusMessageBase(transactionStatus, SBGConstants.TRANSACTION_STATUS_SENT_MESSAGE, SBGConstants.TRANSACTION_STATUS_PENDING_MESSAGE);
	}

	public static TransactionStatusEnum setTransactionStatusMessageOwnAccount(TransactionStatusEnum transactionStatus) {
		return setTransactionStatusMessageBase(transactionStatus, SBGConstants.TRANSACTION_STATUS_SENT_MESSAGE_OWN_ACC, SBGConstants.TRANSACTION_STATUS_PENDING_MESSAGE_OWN_ACC);
	}

	public static TransactionStatusEnum setTransactionStatusMessageBase(TransactionStatusEnum transactionStatus, String sentMsg, String pendingMsg) {
		LOG.debug("Entry --> SBGCommonUtils::setTransactionStatusMessage:: " + transactionStatus.getStatus());
		if (transactionStatus == TransactionStatusEnum.SENT) {
			transactionStatus.setMessage(sentMsg);
		} else if (transactionStatus == TransactionStatusEnum.PENDING) {
			transactionStatus.setMessage(pendingMsg);
		}
		LOG.debug("Exit --> SBGCommonUtils::setTransactionStatusMessage:: " + transactionStatus.getMessage());
		return transactionStatus;
	}

	public static TransactionStatusEnum setTransactionStatusMessage(TransactionStatusEnum transactionStatus, boolean type) {
		LOG.debug("Entry --> SBGCommonUtils::setTransactionStatusMessage:: " + transactionStatus.getStatus());
		if (transactionStatus == TransactionStatusEnum.SENT) {
			transactionStatus.setMessage(SBGConstants.TRANSACTION_STATUS_SENT_MESSAGE);
		} else if (transactionStatus == TransactionStatusEnum.PENDING) {
			transactionStatus.setMessage(SBGConstants.TRANSACTION_STATUS_PENDING_MESSAGE_DOM);
		}
		LOG.debug("Exit --> SBGCommonUtils::setTransactionStatusMessage:: " + transactionStatus.getMessage());
		return transactionStatus;
	}

	public static void _OlbAuditLogsClass(DataControllerRequest request,DataControllerResponse response,Result result,String eventType,String eventSubType,String producer,String statusId) {
		LOG.debug("entry ------------>_logSignatoryGroupStatus()");
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		JsonObject customParams = new JsonObject();
		EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer,statusId, null,
				CustomerSession.getCustomerId(customer), null, customParams);
	}

//	public static Result GetkeycloakPingToken(String string, DataControllerRequest request) {
//		Result result2 = new Result();
//		String accessToken		= FetchPingToken.getKeyCloakccessToken(request);
//		LOG.debug("FetchPingToken.GetkeycloakPingToken ---> accessToken: "+accessToken);
//		result2.addParam(SBGConstants.AUTHORIZATION, accessToken);
//		
//		return result2;
//	}
//	
	public static String getBICFromAE(DataControllerRequest request, String accountNo) {
		LOG.debug("&&&&&accountNo: " + accountNo);
		String customerId = null;
		String BIC = null;
		customerId = HelperMethods.getCustomerIdFromSession(request);
		LOG.debug("&&&&&customerId: " + customerId);
		if (StringUtils.isNotBlank(customerId)) {
			String userAccountDetails = (String) MemoryManager
					.getFromCache(DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + customerId);
			LOG.debug("&&&&&userAccountDetails: " + userAccountDetails);
			JsonArray jsonArray = new JsonParser().parse(userAccountDetails).getAsJsonObject()
					.get(DBPUtilitiesConstants.ACCOUNTS).getAsJsonArray();
			LOG.debug("&&&&&jsonArray: " + jsonArray);
			if (null == jsonArray || jsonArray.size() == 0) {
				LOG.error("&&&&&jsonArraynull");
				return BIC;
			}
			for (JsonElement element : jsonArray) {
				JsonObject object = element.getAsJsonObject();
				LOG.debug("&&&&&object: " + object);
				if (object.has("Account_id")) {
					String accountId = object.get("Account_id").getAsString();
					LOG.debug("&&&&&accountIdFromCache: " + accountId);
					if (StringUtils.equals(accountId, accountNo)) {
						if (object.has("extensionData")) {
							String extensionData = object.get("extensionData").getAsString();
							LOG.debug("&&&&&extensionDataCache: " + extensionData);
							if (StringUtils.isNotBlank(extensionData)) {
								JsonObject EDO = new JsonParser().parse(extensionData).getAsJsonObject();
								LOG.debug("&&&&&EDO: " + EDO);
								if (EDO.has("swiftcode")) {
									String swiftcode = EDO.get("swiftcode").getAsString();
									LOG.debug("&&&&&swiftcode: " + swiftcode);
									if (StringUtils.isNotBlank(swiftcode)) {
										BIC = swiftcode;
										break;
									} else {
										swiftcode = EDO.get("bic").getAsString();
										LOG.debug("&&&&&bic: " + swiftcode);
										if (StringUtils.isNotBlank(swiftcode)) {
											BIC = swiftcode;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return BIC;
	}


	public static String toLowecaseInvariant(String str){
		try {
			String lower = str.toLowerCase();
			return lower.substring(0, 1).toUpperCase() + lower.substring(1);
		} catch (Exception e) {
			return str;
		}
	}

	public static String getFeatureName(String featureName, String featureActionId) {
		if (FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE.equalsIgnoreCase(featureActionId)) {
			return SBGConstants.INTERNATIONAL_PAYMENT;
		} else if (FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE.equalsIgnoreCase(featureActionId)) {
			return SBGConstants.DOMESTIC_PAYMENT;
		} else if (FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE.equalsIgnoreCase(featureActionId)) {
			return SBGConstants.IATFX_PAYMENT;
		}
		return featureName;
	}

	public static ProofOfPaymentDTO[] convertPopJsonArrayToObject(String popJsonArray){
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(popJsonArray, ProofOfPaymentDTO[].class);
		} catch (Exception e) {
			LOG.debug("### request:"+popJsonArray+" for convertPopJsonArrayToObject failed:",e);
			return new ProofOfPaymentDTO[0];
		}
	}

	public static String getStringOrEmptyString(String value) {
		if (isStringNotEmpty(value))
			return value;
		else
			return SBGConstants.EMPTY_STRING;
	}

	public static String generateUniqueIDHyphenSeperated(int length, DataControllerRequest request) throws NoSuchAlgorithmException, NoSuchProviderException{
		StringBuilder sb = new StringBuilder("BLL");
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
		String transactionId = "";
		InterBankFundTransferDTO transfer = null;
        // BLLAANNNNAANNAAA

        List<Integer> elements = Arrays.asList(0,1,6,7,10,11,12);
        SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");	 
       
        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            if(elements.contains(i)){
                int rndCharAt = secureRandomGenerator.nextInt(CHAR_UPPER.length());
                char rndChar = CHAR_UPPER.charAt(rndCharAt);
                sb.append(rndChar);
            } else {
                int rndCharAt = secureRandomGenerator.nextInt(NUMBER.length());
                char rndChar = NUMBER.charAt(rndCharAt);
                sb.append(rndChar);
            }            
        }

		transactionId = sb.toString();

		transfer = sbgInterbankFundTransferDelegate.fetchTranscationEntry(transactionId);

		if(transfer != null){
			transactionId = generateUniqueIDHyphenSeperated(length, request);
		}

        return transactionId;
	}

	public static String getValueOrGivenValue(Object value, String givenValue) {
		if (SBGCommonUtils.isStringNotEmpty(value)) {
			return value.toString();
		} else {
			return givenValue;
		}
	}
}
