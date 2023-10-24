package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.InterBankFundTransferRefDataDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RefDataBackendDelegateInterBank extends BackendDelegate{
	public InterBankFundTransferRefDataDTO insertAdditionalFields(String refID,InterBankFundTransferRefDataDTO interbankFundTransferRefDatadto);
	public Result getRFQDetails(String filter,DataControllerRequest request);
	public Result getApprovalAndRequestDetails(String filter,DataControllerRequest request);
}
