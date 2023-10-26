package com.kony.sbg.backend.api;

import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.dto.SbgInternationalPayeeBackendDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface PayeeBackendDelegate extends BackendDelegate { 
	
	public Result createPayee(SbgInternationalPayeeBackendDTO internationalpayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);

	public Result deletePayee(SbgInternationalPayeeBackendDTO PayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);
	
	public Result fetchPayeesFromDBXOrch(SbgExternalPayeeBackendDTO PayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);

	public Result fetchPayees(String filter,SbgExternalPayeeBackendDTO PayeeBackendDTO, Map<String, Object> headerMap,
			DataControllerRequest request);
}
