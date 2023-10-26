package com.kony.sbg.backend.api;

import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;
import com.kony.sbg.dto.SbgInterBankPayeeBackendDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface DomesticPayeeBackendDelegate extends BackendDelegate { 
	
	public Result createPayee(SbgInterBankPayeeBackendDTO interBankPayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);

	public Result deletePayee(SbgInterBankPayeeBackendDTO PayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);
	
	public Result fetchPayeesFromDBXOrch(SbgExternalPayeeBackendDTO PayeeBackendDTO,
			Map<String, Object> headerParams, DataControllerRequest dcRequest);

	public Result fetchPayees(String filter,SbgExternalPayeeBackendDTO PayeeBackendDTO, Map<String, Object> headerMap,
			DataControllerRequest request);
}
