package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;

public interface DomesticPaymentAPIBackendDelegate extends BackendDelegate {

	public InterBankFundTransferDTO createTransactionWithoutApproval(InterBankFundTransferBackendDTO input,
			DataControllerRequest request, String... branchCode);

}
