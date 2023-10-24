package com.kony.sbg.backend.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.sbg.backend.api.SBGTransactionLimitsBackendDelegate;
import com.kony.sbg.business.api.SBGServicesBusinessDelegate;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;

public class SBGTransactionLimitsBackendDelegateImpl implements SBGTransactionLimitsBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(SBGTransactionLimitsBackendDelegateImpl.class);

	@Override
	public Double fetchconvertedAmount(String currency, String amount, DataControllerRequest request) {		
		LOG.debug("Entry --> SBGTransactionLimitsBackendDelegateImpl::fetchconvertedAmount");
		String midRateBaseTransfer = "";
		String midRateTransferBase = "";
		double midRateBaseTransferDbl = 1.0;
		double midRateTransferBaseDbl = 1.0;
		try {
			ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ApplicationBusinessDelegate.class);
			SBGServicesBusinessDelegate sbgServices = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(SBGServicesBusinessDelegate.class);
			String baseCurrency = application.getBaseCurrencyFromCache();
			String currencyPairs = baseCurrency + "/" + currency;
			//JSONObject response = getIndicativeRates(request);
			JSONObject response = sbgServices.getIndicativeRatesFromCache(request);
			LOG.debug("SBGTransactionLimitsBackendDelegateImpl::response:: " + response);
			if (response != null && response.has("indicativeRates")
					&& response.getJSONArray("indicativeRates").length() > 0) {
				JSONArray indicativeRates = response.getJSONArray("indicativeRates");

				/*
				 * Logic: Conversion should happen from transfer currency to base currency. If
				 * baseCurrency/transactionCurrency is found then divide the mid-rate else swap
				 * currencies i.e. transactionCurrency/baseCurrency and if it's found then
				 * multiply mid-rate
				 */

				Optional<Object> ratesObj = StreamSupport.stream(indicativeRates.spliterator(), true)
						.filter(item -> ((JSONObject) item).getString("currencyPair").equalsIgnoreCase(currencyPairs))
						.findFirst();
				JSONObject currencyPairsRsp;
				if (ratesObj.isPresent()) {
					currencyPairsRsp = (JSONObject) ratesObj.get();
					LOG.debug("Base to transfer currency pair response found : " + currencyPairsRsp);
					midRateBaseTransfer = currencyPairsRsp.getString("midRate");
					midRateBaseTransferDbl = Double.parseDouble(midRateBaseTransfer);
					LOG.debug("Converted midrate into double for currency pair[ " + currencyPairs + "]: "
							+ midRateBaseTransferDbl);
				} else {
					String currencyReversePairs = currency + "/" + baseCurrency;
					Optional<Object> metadataEntity1 = StreamSupport.stream(indicativeRates.spliterator(), true)
							.filter(item -> ((JSONObject) item).getString("currencyPair")
									.equalsIgnoreCase(currencyReversePairs))
							.findFirst();
					if (metadataEntity1.isPresent()) {
						JSONObject reverseCurrencyPairsObj = (JSONObject) metadataEntity1.get();
						LOG.debug("Transfer to base currency pair response found : " + reverseCurrencyPairsObj);
						midRateTransferBase = reverseCurrencyPairsObj.getString("midRate");
						midRateTransferBaseDbl = Double.parseDouble(midRateTransferBase);
						LOG.debug("Converted midrate into double for currency pair[ " + currencyPairs + "]: "
								+ midRateTransferBaseDbl);
					}
				}

				double amountValue = Double.parseDouble(amount);
				if (StringUtils.isBlank(midRateTransferBase) == false) {
					LOG.debug("Converted amount for T2B: " + (amountValue * midRateTransferBaseDbl));
					return amountValue * midRateTransferBaseDbl;
				} else if (StringUtils.isBlank(midRateBaseTransfer) == false) {
					LOG.debug("Converted amount for B2T: " + (amountValue / midRateBaseTransferDbl));
					return amountValue / midRateBaseTransferDbl;
				}
			}
		} catch (Exception e) {
			LOG.error("Exception in TransactionLimitsBackendDelegateImplSBGExtn: " + e.getMessage());			
			return null;
		}
		return null;
	}

	/** Moving it to SBGServicesBusinessDelegate */
	/* protected JSONObject getIndicativeRates(DataControllerRequest request) {
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		String serviceName = SbgURLConstants.SERVICE_SBGMAIBMGATEWAY;
		String operationName = SbgURLConstants.OPERATION_GETINDICATIVERATES;
		JSONObject serviceResponse = null;
		String authorization = "";
		try {

			Result resultCache = SBGCommonUtils.cacheFetch("Authorization");
			if (resultCache != null && resultCache.hasParamByName("Authorization")) {
				authorization = resultCache.getParamValueByName("Authorization").toString();
			} else {
				LOG.error("IBM-Gateway authentication failed");
				//serviceResponse.put("errorMsg", "Failed to fetch IBM OAuth Token");
				return serviceResponse;
			}

			requestParameters.put("priceSegment",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_PRICE_SEGMENT, request));
			requestParameters.put("currencyPairs",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_PARAM_API_CURRENCY_PAIR, request));
			requestHeaders.put("x-channel-id",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_CHANNEL_ID, request));
			requestHeaders.put("x-country-code",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.SBG_HEADER_API_COUNTRY_CODE, request));
			requestHeaders.put("x-req-id", SBGCommonUtils.generateRandomUUID());
			requestHeaders.put("x-req-timestamp", SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			requestHeaders.put("X-IBM-Client-Id",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_ID, request));
			requestHeaders.put("X-IBM-Client-Secret",
					SBGCommonUtils.getServerPropertyValue(SbgURLConstants.X_IBM_CLIENT_SECRET, request));
			requestHeaders.put("Authorization", authorization);
			requestHeaders.put("x-fapi-interaction-id", SBGCommonUtils.generateRandomUUID().toString());

			LOG.debug("Request Input of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + requestParameters.toString());
			LOG.debug("Request Header of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + requestHeaders.toString());

			String indicativeRates = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(requestHeaders).build().getResponse();
			serviceResponse = new JSONObject(indicativeRates);
			LOG.debug("Response of Service : " + SbgURLConstants.SERVICE_SBGMAIBMGATEWAY + " Operation : "
					+ SbgURLConstants.OPERATION_GETINDICATIVERATES + ":: " + indicativeRates);

		} catch (Exception e) {
			LOG.error("Caught exception at  get converted value: ", e);
			//serviceResponse.put("errorMsg", e.getMessage());
		}

		return serviceResponse;
	}*/

}
