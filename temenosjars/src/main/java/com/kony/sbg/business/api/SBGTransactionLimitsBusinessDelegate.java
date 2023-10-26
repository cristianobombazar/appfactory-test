package com.kony.sbg.business.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public interface SBGTransactionLimitsBusinessDelegate extends BusinessDelegate {

	public TransactionStatusDTO validateForLimitsSBG(String customerId, String companyId, String accountId,
			String featureActionID, Double amount, TransactionStatusEnum transactionStatus, String date,
			String transactionCurrency, String serviceCharge, DataControllerRequest request);

}
