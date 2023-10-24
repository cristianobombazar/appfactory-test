package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;

public interface SBGTransactionLimitsBackendDelegate extends BackendDelegate {

	public Double fetchconvertedAmount(String currency, String amount, DataControllerRequest request);

}
