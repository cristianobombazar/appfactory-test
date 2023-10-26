package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.InternationalFundTransferRefDataDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RefDataBackendDelegate extends BackendDelegate{
	public InternationalFundTransferRefDataDTO insertAdditionalFields(String refID,InternationalFundTransferRefDataDTO internationalFundTransferRefDatadto);
	public Result getRFQDetails(String filter,DataControllerRequest request,String serviceName, String operationName);
	public Result getApprovalAndRequestDetails(String filter,DataControllerRequest request, String serviceName, String operationName);
}
