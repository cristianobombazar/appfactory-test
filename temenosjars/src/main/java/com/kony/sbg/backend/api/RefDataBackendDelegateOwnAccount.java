package com.kony.sbg.backend.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.OwnAccountFundTransferRefDataDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface RefDataBackendDelegateOwnAccount extends BackendDelegate{
	public OwnAccountFundTransferRefDataDTO insertAdditionalFields(String refID,OwnAccountFundTransferRefDataDTO ownAccountFundTransferRefDatadto);
	public Result getRFQDetails(String filter,DataControllerRequest request,String serviceName, String operationName);
	public Result getApprovalAndRequestDetails(String filter,DataControllerRequest request, String serviceName, String operationName);
}
