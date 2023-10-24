package com.kony.sbg.business.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.sbg.business.api.EvaluateTransactionBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

import za.co.synthesis.finsurvlocal.FinsurvLocal;
import za.co.synthesis.finsurvlocal.types.ArtefactType;
import za.co.synthesis.finsurvlocal.utils.JsonUtils;
import za.co.synthesis.rule.core.ResultEntry;

public class EvaluateTransactionBusinessDelegateImpl implements EvaluateTransactionBusinessDelegate {

	private static final Logger LOG = Logger.getLogger(EvaluateTransactionBusinessDelegateImpl.class);

	public static String channelSampleDir = "ChannelArtefacts/";
	public static String channelConfSampleDir = "ChannelConfig/ChannelConf_001.json";
	public static String ReportsMetaDir = "ReportsArtifacts/meta.json";
	public static String ReportsReportDir = "ReportsArtifacts/report.json";
	@Override
	public Result evaluateTransaction(JSONObject requestPayload, DataControllerRequest dcRequest)
			throws ApplicationException {
		LOG.debug("Entry --> EvaluateTransactionBusinessDelegateImpl::evaluateTransaction");
		Map<String, Object> evalOutput = null;
		Result result = new Result();
		FinsurvLocal finsurv = null;
		finsurv = getFinsurvInstance(result);
		if (finsurv != null && finsurv.getChannelNames().isEmpty() == false) {
			String channel = finsurv.getChannelNames().get(0);
			LOG.debug("channel: " + channel);
			Map<String,Object> evalInput= createParam(requestPayload, result);
			LOG.debug("evaluateTransaction==evalInput: " + evalInput);
			evalOutput = finsurv.evaluateBopReport(channel,evalInput, true, true);
			LOG.debug("evaluateTransaction==evalOutput: " + evalOutput);
			
			if(evalOutput == null || evalOutput.size() == 0) {
				LOG.error("EvaluateTransactionBusinessDelegateImpl::evaluateTransaction ---> No decision found for given bop data: ");
				result = SbgErrorCodeEnum.ERR_100059.setErrorCode(result);
				return result;
			}			
		} else {
			LOG.error("Finsurv instance is null or channel name is empty");
			result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
			return result;
		}
		
		boolean isReportable = SBGCommonUtils.isBoPReportable(evalOutput);
		LOG.error("EvaluateTransactionBusinessDelegateImpl::evaluateTransaction ---> isReportable: "+isReportable);
		if(isReportable) {
			result.addParam(new Param("ISREPORTABLE", "YES", ""));
			String reportsResult = SBGCommonUtils.getReportData(requestPayload,evalOutput,ReportsMetaDir,ReportsReportDir,dcRequest,result); 
			LOG.error("EvaluateTransactionBusinessDelegateImpl::evaluateTransaction ---> reportsResult: "+reportsResult);
			String reportsEncodeResult="",Status="Error";
			if(reportsResult!=null) {
				Base64.Encoder encoder = Base64.getEncoder();  
				reportsEncodeResult =encoder.encodeToString(reportsResult.toString().getBytes()); 
				LOG.error("EvaluateTransactionBusinessDelegateImpl::evaluateTransaction ---> reportsEncodeResult: "+reportsEncodeResult);
				Status="True";
			}
			result = convertResponseToResult(evalOutput, result,reportsEncodeResult,Status);
		} else {
			result.addParam(new Param("ISREPORTABLE", "NO", ""));
			
			//[AH] THE BELOW CODE IS REDUNDENT AND WILL BE REMOVED LATER. IN CASE OF ISREPORTABLE=FALSE, THE BOP FORM LINK WILL NOT APPEAR IN UI
			//TOD0: REMOVE THE BELOW CODE AFTER 12TH OCTOBER 
			String reportsResult = SBGCommonUtils.getReportData(requestPayload,evalOutput,ReportsMetaDir,ReportsReportDir,dcRequest,result);  
			String reportsEncodeResult="",Status="Error";
			if(reportsResult!=null) {
				Base64.Encoder encoder = Base64.getEncoder();  
				reportsEncodeResult =encoder.encodeToString(reportsResult.toString().getBytes()); 
				Status="True";
			}
			result = convertResponseToResult(evalOutput, result,reportsEncodeResult,Status);
		}
		LOG.debug("evaluateTransaction::Result:: " + ResultToJSON.convert(result));
		return result;
	}

	private Result convertResponseToResult(Map<String, Object> evalOutput, Result result,String reportsEncodeResult,String status) {
		LOG.debug("Entry --> convertResponseToResult");
		Dataset dataSet = new Dataset("evaluateDecision");
		try {
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
							/*Flow should be OUT & IN temporary fix*/
							if (entry.getKey().equals("Flow")) {
								if (entry.getValue().equals("Outflow")) {
									record.addParam(entry.getKey(), "OUT");
								} else if (entry.getValue().equals("Inflow")) {
									record.addParam(entry.getKey(), "IN");
								} else {
									record.addParam(entry.getKey(), entry.getValue().toString());
								}
							} else {
								record.addParam(entry.getKey(), entry.getValue().toString());
							}
						} else {
							record.addParam(entry.getKey(), (String) entry.getValue());
						}
					}
					dataSet.addRecord(record);
				}
			}
			result.addDataset(dataSet);
			//calling report method and adding to reports in result
					  
			result.addParam("reports",reportsEncodeResult);
			result.addParam("Status",status);
		} catch (Exception exp) {
			LOG.error("Exception occurred in convertResponseToResult: " + exp);
			result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
		}
		return result;
	}
	
	private Map<String, Object> createParam(JSONObject requestPayload, Result result) {
		LOG.debug("Entry --> EvaluateTransactionBusinessDelegateImpl::createParam");
		
		Map<String, Object> debitAccountHolderStatusObj= new HashMap<>();
		debitAccountHolderStatusObj.put("AccountHolderStatus", requestPayload.getString("debitAccountHolderStatus"));
		Map<String, Object> creditAccountHolderStatusObj= new HashMap<>();
		creditAccountHolderStatusObj.put("AccountHolderStatus", requestPayload.getString("creditAccountHolderStatus"));
		
		LOG.debug("debitAccountHolderStatusObj"+debitAccountHolderStatusObj);
		LOG.debug("creditAccountHolderStatusObj"+creditAccountHolderStatusObj);
		
		Map<String, Object> inputData = new HashMap<String, Object>();
		inputData.put("drBIC", requestPayload.getString("debitAccountSwiftCode"));
		inputData.put("drCurrency", requestPayload.getString("debitCurrency"));
		inputData.put("drResidenceStatus", requestPayload.getString("debitResidenceStatus"));
		inputData.put("drBankAccType", requestPayload.getString("debitAccountType"));
		inputData.put("drOptionalParams",debitAccountHolderStatusObj);
		
		inputData.put("crBIC", requestPayload.getString("creditSwiftCode"));
		inputData.put("crCurrency", requestPayload.getString("creditCurrency"));
		inputData.put("crResidenceStatus", requestPayload.getString("creditResidenceStatus"));
		inputData.put("crBankAccType", requestPayload.getString("creditAccountType"));
		inputData.put("crOptionalParams",creditAccountHolderStatusObj);
		
		LOG.debug("createParam::MapRequest built successfully: " + inputData.toString());
		return inputData;
	}

	private String populateChannelConf(String channelConf, Result result) {
		LOG.debug("Entry --> populateChannelConf");
		String response = "";
		Map<String, Object> channelConfMap = null;
		try {
			channelConfMap = JsonUtils.jsonStrToMap(channelConf);
		} catch (Exception e) {
			LOG.error("Exception occurred while converting channel configuration string to map:  " + e);
			result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
			return response;
		}

		for (Map.Entry<String, Object> entry : channelConfMap.entrySet()) {
			String channelName = entry.getKey();
			LOG.debug("populateChannelConf::Key From Map: " + channelName);
			Map<String, Object> channelConfiguration = null;

			if (!channelName.isEmpty() && entry.getValue() instanceof Map) {
				channelConfiguration = (Map<String, Object>) entry.getValue();
				Map<String, Object> artefactMap = channelConfiguration
						.containsKey(FinsurvLocal.GenericInstanceParameterEnum.ARTEFACTS.getValue())
								? (Map<String, Object>) channelConfiguration
										.get(FinsurvLocal.GenericInstanceParameterEnum.ARTEFACTS.getValue())
								: new HashMap<String, Object>();

				for (ArtefactType artefactType : ArtefactType.values()) {
					LOG.debug("populateChannelConf::ArtefactType::" + artefactType.getName());
					String content = null;
					String artefactFile = channelSampleDir + channelName + "/" + artefactType.filename;
					LOG.debug("populateChannelConf::artefactFile: " + artefactFile);
					try {
						InputStream artefactStream = EvaluateTransactionBusinessDelegateImpl.class.getClassLoader()
								.getResourceAsStream(artefactFile);
						if (artefactStream != null) {
							try {
								StringWriter writer = new StringWriter();
								IOUtils.copy(artefactStream, writer, "UTF-8");
								content = writer.toString();
							} catch (IOException e) {
								LOG.debug("Exception occurred while converting InputStream to String: " + e);
								result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
								return response;
							}
						}
					} catch (Exception err) {
						LOG.error("Unable to load file " + err);
						result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
						return response;
					}

					if (content instanceof String) {
						artefactMap.put(artefactType.getName(), content);
					}
				}
				channelConfiguration.put(FinsurvLocal.GenericInstanceParameterEnum.ARTEFACTS.getValue(), artefactMap);
			}
		}
		response = JsonUtils.mapToJsonStr(channelConfMap);
		return response;
	}

	@Override
	public boolean validateBoPForm(String bopDetails, Result result, DataControllerRequest dcRequest) {
		LOG.debug("Entry --> validateBoPForm");
		boolean validationSuccessful = false;
		String url = getHostUrlPort(dcRequest);
		if (SBGCommonUtils.isStringEmpty(url)) {
			result = SbgErrorCodeEnum.ERR_100026.setErrorCode(result);
			return validationSuccessful;
		}
		FinsurvLocal finsurv = getFinsurvInstance(result);
		if (finsurv != null && finsurv.getChannelNames().isEmpty() == false) {
			String channel = finsurv.getChannelNames().get(0);
			List<ResultEntry> validationOutput = null;
			try {
				URL url1 = new URL(url);
				LOG.debug("Protocol: " + url1.getProtocol() + " Host: " + url1.getHost() + "Port: " + url1.getPort());
				validationOutput = finsurv.validateBopReport(channel, convertBase64EncodetoString(bopDetails),
						url1.getProtocol(), url1.getHost(), Integer.toString(url1.getPort()),
						SbgURLConstants.URL_BOP_JSON_SERVICE);
				LOG.debug("validateBoPForm:::validationOutput: " + validationOutput);
			} catch (Exception e) {
				LOG.error("Exception occurred while validating BoP Report: " + e);
				result = SbgErrorCodeEnum.ERR_100061.setErrorCode(result);
				return validationSuccessful;
			}
			if (finsurv.isValidationSuccessful(validationOutput, true)) {				
				validationSuccessful = true;
				LOG.debug("validateBoPForm:::validationSuccessful: " + validationSuccessful);
			} else {
				LOG.error("BOP Form Validation Failed");
				result = SbgErrorCodeEnum.ERR_100062.setErrorCode(result);
			}
		} else if (result.hasParamByName(DBPConstants.DBP_ERROR_CODE_KEY)) {
			return validationSuccessful;
		} else {
			result = SbgErrorCodeEnum.ERR_100061.setErrorCode(result);
			return validationSuccessful;
		}
		LOG.debug("Entry --> validateBoPForm");
		return validationSuccessful;
	}

	private FinsurvLocal getFinsurvInstance(Result result) {
		String channelConfig = "";
		FinsurvLocal finsurv = null;

		InputStream channelConfigStream = EvaluateTransactionBusinessDelegateImpl.class.getClassLoader()
				.getResourceAsStream(channelConfSampleDir);
		if (channelConfigStream != null) {
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(channelConfigStream, writer, "UTF-8");
			} catch (IOException e) {
				LOG.error("Exception occurred while converting InputStream to String: " + e);
				result = SbgErrorCodeEnum.ERR_100060.setErrorCode(result);
			}
			channelConfig = writer.toString();
			LOG.debug("ChannelConfig: " + channelConfig);
		} else {
			LOG.error("Channel Config file stream is null");
			result = SbgErrorCodeEnum.ERR_100060.setErrorCode(result);
			return finsurv;
		}

		if (SBGCommonUtils.isStringEmpty(channelConfig) == false) {
			String updatedChannelConfig = populateChannelConf(channelConfig, result);
			if (result.hasParamByName(DBPConstants.DBP_ERROR_CODE_KEY)
					|| SBGCommonUtils.isStringEmpty(updatedChannelConfig)) {
				LOG.error("Error occurred while populating channel config");
				return finsurv;
			}
			try {
				finsurv = new FinsurvLocal(updatedChannelConfig);
			} catch (Exception e) {
				LOG.error("Exception occurred while creating instance of Finsurv: " + e);
				result = SbgErrorCodeEnum.ERR_100056.setErrorCode(result);
				return finsurv;
			}
		}
		return finsurv;
	}

	private String convertBase64EncodetoString(String value) {
		LOG.debug("Entry --> convertBase64EncodetoString");
		String decodedDetails = "";
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(value);
			String decodedBase64 = new String(decodedBytes);
			decodedDetails = URLDecoder.decode(decodedBase64, StandardCharsets.UTF_8.toString());
			LOG.debug("convertBase64EncodetoString::decodedBopDetails: " + decodedDetails);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error occured while decoding details", e);
		}

		return decodedDetails;
	}
	
	private String getHostUrlPort(DataControllerRequest dcRequest) {
		String url = "";
		try {
			url = SBGCommonUtils.getServerPropertyValue(SbgURLConstants.DBX_HOST_URL, dcRequest);
		} catch (Exception e) {
			LOG.error("Exception occurred while reading envrionment property: " + e);
			return null;
		}
		return url;
	}
	
}
