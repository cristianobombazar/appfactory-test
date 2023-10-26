package com.kony.sbg.resources.api;

import com.dbp.core.api.BusinessDelegate;
import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

import java.util.Map;

public interface SubmitDocToFileNetResource extends Resource {
    Result submitDocToFileNet(DataControllerRequest dataControllerRequest, Map<String, String> inputParams);

    Result submitDocsToFileNet(DataControllerRequest dataControllerRequest, Map<String, String> inputParams);
}
