package com.kony.sbg.backend.api;

import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.kony.sbg.dto.SwiftCodeDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;


public interface SwiftCodeBackendDelegate extends BackendDelegate{

	
	Result getSwiftCodeResponse(SwiftCodeDTO swiftCodeDTO, Map<String, Object> headerMap,
			DataControllerRequest dcRequest);

}
