package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;

import java.util.Map;

public interface PaymentAPIBackendDelegate extends BackendDelegate {

	public InternationalFundTransferDTO createTransactionWithoutApproval(InternationalFundTransferBackendDTO input,
			DataControllerRequest request, Map<String, Object> inputParams);

}
