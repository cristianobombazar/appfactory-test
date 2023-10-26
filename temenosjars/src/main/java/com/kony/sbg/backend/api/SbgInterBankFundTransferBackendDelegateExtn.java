package com.kony.sbg.backend.api;


import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;

/**
 * Handles all backend operations on International Fund transaction
 * @author Mashilo Joseph Monene
 * extends {@link BackendDelegate}
 */

public interface SbgInterBankFundTransferBackendDelegateExtn extends InterBankFundTransferBackendDelegate {
	
	/**
	 * Method to call an Orchestration service- gets the Public holidays,CurrencyHolidays, Bussiness days details based on country and currency
	 * @param {@link headerParams}
     * @param {@link requestParameters}
     * @param {@link serviceName}
     * @param {@link operationName}
	 * @return {@link String}
	 */
	public String callRefDataOrchService(Map<String, Object> headerParams, Map<String, Object> requestParameters,
			String serviceName, String operationName) throws Exception;
}