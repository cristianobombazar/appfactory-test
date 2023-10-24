/**
 * @author srinag.boda
 *
 */

package com.kony.sbg.javaservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.utils.InfinityConstants;
import com.temenos.infinity.api.arrangements.config.ServerConfigurations;

public class GetAccountsOperation implements JavaService2 {

	// private static final Logger LOG =
	// LogManager.getLogger(GetAccountsOperation.class);
	// private final static String AUTH =
	// "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGYWJyaWMiLCJleHAiOjE4MTkxNzA1NTUsImlhdCI6MTU4NzU0NzEwNn0.chZWZ4KPduQaATRh3EWKM4pXkk_VpzHnISIkGitb5OAPYYDq740eVdo_aeqyiQbLrzk74JBnMJx7XI4PzrQfW7ZzHeGff4Xkx_7fiKWCuyx0cc_T8f_a2GX9zRibj42ahd1mV7A8neg1HbEAsZS4X2RN_RrLRBf6jduigU2YSIkJhN6wx0XHlzbUryxIZchCKQ74p4q8HOb77XbtToJXfBGRJMwONk1TRObMEbSZJUr488vQlgj6Iq8lCQEY_NMaAI1P-YHGxgD6jLxmkdAYt7ho63B7DhvNCw6kUJjM-zkbJ5sZCPXA-jPE8nbXrLnePvecfej2rqL9LxFJyhaxdA";

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		Map<String, String> params = HelperMethods.getInputParamMap(inputArray);
		// Initializing of Accounts through Abstract factory method
		ServicesManager servicesManager;
		String customerID = null;
		String loginUserId = null;

		boolean isSuperAdmin = false;
		Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(request);
		if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
			loginUserId = HelperMethods.getCustomerIdFromSession(request);
		} else {
			loginUserId = params.containsKey(InfinityConstants.id)
					&& StringUtils.isNotBlank(params.get(InfinityConstants.id)) ? params.get(InfinityConstants.id)
							: request.getParameter(InfinityConstants.id);
			isSuperAdmin = true;
		}

		if (!isSuperAdmin && StringUtils.isNotBlank(loginUserId)) {
			servicesManager = request.getServicesManager();
			// Get Customer id and type for business user
			customerID = (String) servicesManager.getIdentityHandler().getUserAttributes().get("customer_id");
		}

		if (StringUtils.isNotBlank(loginUserId)) {
			customerID = loginUserId;
		}

		// If product line is null then set default to ACCOUNT
		String productLineId = request.getParameter("productLineId");
		if (StringUtils.isBlank(productLineId)) {
			productLineId = "ACCOUNTS";
		}
		String ARRANGEMENTS_BACKEND = ServerConfigurations.ARRANGEMENTS_BACKEND.getValueIfExists();
		if (ARRANGEMENTS_BACKEND != null) {
			if (ARRANGEMENTS_BACKEND.equals("MOCK")) {
				Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
				inputParams.put("userId", customerID);
				inputArray[1] = inputParams;
				GetAccounts mockAccounts = new GetAccounts();
				Object mockAccountsResult = mockAccounts.invoke(methodID, inputArray, request, response);
				return mockAccountsResult;
			}
		}
		return result;
	}
}
