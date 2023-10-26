package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;
import com.dbp.core.error.DBPApplicationException;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface RetrieveDocumentResource extends Resource {

    public Result readDocumentDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
                                         DataControllerResponse dcResponse) throws ApplicationException, HttpCallException, DBPApplicationException;

}
