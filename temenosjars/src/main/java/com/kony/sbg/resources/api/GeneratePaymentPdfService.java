package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface GeneratePaymentPdfService extends Resource {
    public Result getPaymentPDF(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
                                DataControllerResponse dcResponse) throws Exception;
}
