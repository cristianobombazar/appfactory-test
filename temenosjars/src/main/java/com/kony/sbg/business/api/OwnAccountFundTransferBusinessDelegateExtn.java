package com.kony.sbg.business.api;

import org.json.JSONObject;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InternationalFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.OwnAccountFundTransferBusinessDelegate;

/**
 *  Handles all the operations on International Fund transaction
 *  @author kh1755
 *  extends {@link BusinessDelegate}
 */
public interface OwnAccountFundTransferBusinessDelegateExtn  extends OwnAccountFundTransferBusinessDelegate {

	/**
	 * Method to get the reference data validations validate the scheduled payment date
	 * @param request
	 * @return {@link JSONObject}
	 */
	public JSONObject sbgValidations(DataControllerRequest request);
	
	public JSONObject fetchTransactionEntryFiltered(String filter);
	
}
