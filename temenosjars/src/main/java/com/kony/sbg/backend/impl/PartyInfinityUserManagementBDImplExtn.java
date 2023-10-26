package com.kony.sbg.backend.impl;

import java.util.Map;

import com.google.gson.JsonObject;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.InfinityUserManagementBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.InfinityUserManagementBackendDelegateImpl;
import com.temenos.dbx.product.dto.DBXResult;
//import com.temenos.dbx.usermanagement.backenddelegate.impl.PartyInfinityUserManagementBackendDelegateImpl;
//import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.InfinityUserManagementBackendDelegateImpl;
import com.temenos.dbx.usermanagement.backenddelegate.impl.PartyInfinityUserManagementBackendDelegateImpl;

public class PartyInfinityUserManagementBDImplExtn extends PartyInfinityUserManagementBackendDelegateImpl {

	  @Override
	    public DBXResult editInfinityUser(JsonObject jsonObject, Map<String, Object> headerMap) {

	        InfinityUserManagementBackendDelegate backendDelegate = new InfinityUserManagementBackendDelegateImpl();
	        DBXResult dbxResult = backendDelegate.createInfinityUser(jsonObject, headerMap);

//	        JsonObject userDetails = jsonObject.has(InfinityConstants.userDetails)
//	                && jsonObject.get(InfinityConstants.userDetails).isJsonObject()
//	                        ? jsonObject.get(InfinityConstants.userDetails).getAsJsonObject()
//	                        : null;
//	        String id = userDetails.has(InfinityConstants.id) && !userDetails.get(InfinityConstants.id).isJsonNull()
//	                ? userDetails.get(InfinityConstants.id).getAsString()
//	                : null;
	 //       if (StringUtils.isNotBlank(id) && userDetails != null)
	 //           updateCustomer(userDetails, headerMap);
	        return dbxResult;
	    }

}
