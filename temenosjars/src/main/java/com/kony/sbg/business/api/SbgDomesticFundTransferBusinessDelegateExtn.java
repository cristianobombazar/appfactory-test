package com.kony.sbg.business.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import org.json.JSONObject;

/**
 *  Handles all the operations on International Fund transaction
 *  @author kh1755
 *  extends {@link BusinessDelegate}
 */
public interface SbgDomesticFundTransferBusinessDelegateExtn extends DomesticFundTransferBusinessDelegate {

	/**
	 * Method to get the reference data validations validate the scheduled payment date
	 * @param request
	 * @return {@link JSONObject}
	 */
	public JSONObject sbgValidations(DataControllerRequest request);
	
	public JSONObject fetchTransactionEntryFiltered(String filter);
	
}
