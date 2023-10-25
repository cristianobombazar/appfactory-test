package com.logix.appfactory;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetVersionPostProcessor implements DataPostProcessor2 {

    @Override
    public Object execute(Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        result.addStringParam("com.logix.appfactory.GetVersionPostProcessor", "v2");
        return result;
    }
}
