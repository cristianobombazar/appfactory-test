package com.kony.sbg.sideloading.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.util.SBGUtil;

public class JsonHelper {
	
	private static LoggerUtil logger = new LoggerUtil(JsonHelper.class);
	
	public	static JsonObject convertMap2Json(Map<String, String> map) {
		
		logger.debug("JsonHelper.convertMap2Json() ---> map: "+map);
		
		JsonObject retval = new JsonObject();
		
		for (String key : map.keySet()) {
	        String val = map.get(key);
	        //logger.debug("convertMap2Json() ---> key: "+key+"; val: "+val);
	        
	        retval.addProperty(key, val);
	    }
		
		//logger.debug("JsonHelper.convertMap2Json() ---> retval: "+retval);
		return retval;
	}

	public	static Map<String, String> convertJson2Map4Contract(JsonObject jsonObj) {
		
		Map<String, String> retval = new HashMap<String, String>();

		retval.put("coreCustomerIdFromParty", jsonObj.get("coreCustomerIdFromParty").toString());
		retval.put("address", jsonObj.get("address").toString());
		retval.put("serviceDefinitionName", jsonObj.get("serviceDefinitionName").toString());
		retval.put("isDefaultActionsEnabled", jsonObj.get("isDefaultActionsEnabled").toString());
		retval.put("authorizedSignatory", jsonObj.get("authorizedSignatory").toString());
		retval.put("authorizedSignatoryRoles", jsonObj.get("authorizedSignatoryRoles").toString());
		retval.put("serviceDefinitionId", jsonObj.get("serviceDefinitionId").toString());
		retval.put("contractName", jsonObj.get("contractName").toString());
		retval.put("contractCustomers", jsonObj.get("contractCustomers").toString());
		retval.put("communication", jsonObj.get("communication").toString());
		
//		Iterator it = jsonObj.entrySet().iterator();
//		while(it.hasNext()) {
//			String key = ((JsonElement)it.next()).toString();
//			String val = jsonObj.get(key).toString();
//			
//			logger.debug("convertJson2Map() ---> key: "+key+"; val: "+val);
//			retval.put(key, val);
//		}

		return retval;
	}

	public	static Map<String, String> convertJson2Map4Users(JsonObject jsonObj) {
		
		Map<String, String> retval = new HashMap<String, String>();

		retval.put("username", jsonObj.get("username").getAsString().trim());
		retval.put("title", jsonObj.get("title").getAsString().trim());
		retval.put("firstName", jsonObj.get("firstName").getAsString().trim());

		if(jsonObj.get("middleName") == null) {
			retval.put("middleName", "");
		} else {
			retval.put("middleName", jsonObj.get("middleName").getAsString().trim());
		}
		retval.put("lastName", jsonObj.get("lastName").getAsString().trim());
		retval.put("fullName", jsonObj.get("fullName").getAsString().trim());

		retval.put("dob", jsonObj.get("dob").getAsString().trim());

		if(jsonObj.get("ssn") == null) {
			retval.put("ssn", "");
		} else {
			retval.put("ssn", jsonObj.get("ssn").getAsString().trim());
		}
		if(jsonObj.get("dlno") == null) {
			retval.put("dlno", "");
		} else {
			retval.put("dlno", jsonObj.get("dlno").getAsString().trim());
		}
		retval.put("email", jsonObj.get("email").getAsString().trim());

		retval.put("phoneCountryCode", jsonObj.get("phoneCountryCode").getAsString().trim());
		retval.put("phoneNumber", jsonObj.get("phoneNumber").getAsString().trim());

		retval.put("identifierNumber", jsonObj.get("identifierNumber").getAsString().trim());
		retval.put("docTagId", jsonObj.get("docTagId").getAsString().trim());
		retval.put("idNumber", jsonObj.get("idNumber").getAsString().trim());
		retval.put("idSource", jsonObj.get("idSource").getAsString().trim());

		retval.put("roleId", jsonObj.get("roleId").getAsString().trim());
		//retval.put("allowedAccounts", jsonObj.get("allowedAccounts").getAsString().trim());
		retval.put("allowedAccounts", SBGUtil.getString(jsonObj, "allowedAccounts"));
		retval.put("userPartyId", jsonObj.get("userPartyId").getAsString().trim());

		retval.put("addressline1", jsonObj.get("addressline1").getAsString().trim());
		retval.put("addressline2", jsonObj.get("addressline2").getAsString().trim());
		retval.put("addressline3", jsonObj.get("addressline3").getAsString().trim());
		retval.put("city", jsonObj.get("city").getAsString().trim());
		retval.put("district", jsonObj.get("district").getAsString().trim());
		retval.put("state", jsonObj.get("state").getAsString().trim());
		retval.put("country", jsonObj.get("country").getAsString().trim());
		retval.put("countryCode", jsonObj.get("countryCode").getAsString().trim());
		retval.put("zipcode", jsonObj.get("zipcode").getAsString().trim());
		
		return retval;
	}
}
