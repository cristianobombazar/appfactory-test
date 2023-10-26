package com.kony.sbg.backend.api;

import java.util.HashMap;

import org.json.JSONObject;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public interface ManageBeneficiaryBackendDelegate extends BackendDelegate {

	JSONObject fetchAllInternationalPayeesForCif(HashMap<String, Object> requestParameters, boolean isDomestic);

	JSONObject fetchAllBeneCodesForPayees(HashMap<String, Object> requestParameters);

}
