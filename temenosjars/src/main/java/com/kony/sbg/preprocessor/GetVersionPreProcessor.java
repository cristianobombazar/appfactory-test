package com.kony.sbg.preprocessor;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetVersionPreProcessor implements DataPostProcessor2 {
    @Override
    public Object execute(Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        result.addStringParam("preProcessorVersion", "v2");
        return result;
    }
}
