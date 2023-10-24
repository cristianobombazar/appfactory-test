package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;

public interface OwnAccountPaymentAPIBackendDelegate extends BackendDelegate {

	public OwnAccountFundTransferDTO createTransactionWithoutApproval(OwnAccountFundTransferBackendDTO input,
			DataControllerRequest request);

}
