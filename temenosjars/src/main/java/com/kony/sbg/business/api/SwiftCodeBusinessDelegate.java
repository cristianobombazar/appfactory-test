package com.kony.sbg.business.api;

import java.util.Map;

import com.dbp.core.api.BusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.kony.sbg.dto.SwiftCodeDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
public interface SwiftCodeBusinessDelegate extends BusinessDelegate  {

	Result getSwiftCodeDetails(SwiftCodeDTO swiftCodrDTO, Map<String, Object> headerMap, DataControllerRequest dcRequest) throws ApplicationException;
}
