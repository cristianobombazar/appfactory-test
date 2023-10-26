package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.DomesticFundTransferRefDataDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RefDataDomesticBackendDelegate extends BackendDelegate{
	public DomesticFundTransferRefDataDTO insertAdditionalFields(String refID, DomesticFundTransferRefDataDTO domesticFundTransferRefDatadto);
	public Result getRFQDetails(String filter,DataControllerRequest request);
	public Result getApprovalAndRequestDetails(String filter,DataControllerRequest request);
}
