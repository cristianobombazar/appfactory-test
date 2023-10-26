package com.kony.sbg.sideloading.creator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.models.Accounts;
import com.kony.sbg.sideloading.models.Customer;

public class ArrangementPayloadCreator {

	private	Accounts account = null;
	
	@SuppressWarnings("unchecked")
	public	JSONObject getPayload4Accounts(String corecustcif, Accounts acct) {
		
		this.account = acct;
		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		
		JSONObject payload = getBasicInfo(customer);
		payload.put("company", 				createCompany(customer));
		payload.put("interests",			createInterests());
		payload.put("accountArrangement",	createAccountArrangement());
		payload.put("depositArrangement",	createDepositArrangement());
		payload.put("lendingArrangement", 	createLendingArrangement());
		payload.put("roles", 				createRoles(customer));
		payload.put("alternateReferences",	createAlternateReferences(customer));
		
		return payload;		
	}
	
	@SuppressWarnings("unchecked")
	private	JSONObject getBasicInfo(Customer customer) {
		JSONObject obj	= new JSONObject();

		obj.put("extArrangementId", account.getAccountId());
		obj.put("accountCategory", "6001");
		obj.put("arrangementStatus", "ACTIVE");
		obj.put("arrangementStatusDate", "2021-04-24");
		obj.put("country", "US");
		obj.put("creationDate", "2021-04-24");
		obj.put("currency", account.getCurrency());
		obj.put("externalIndicator", false);
		obj.put("linkedReference", LoadSmeData.COMPANY_ID+"-"+account.getAccountId());
		obj.put("productLine", "ACCOUNTS");
		obj.put("productGroup", "CURRENT.ACCOUNT");
		obj.put("isPortFolioAccount", false);
		obj.put("iban", account.getAccountId());
		obj.put("estmtEnabled", false);
		obj.put("extensionData", new JSONObject());
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	private	JSONObject createCompany(Customer customer) {	
		JSONObject obj = new JSONObject();
		
		obj.put("companyReference", customer.getRegistrationNo());
		obj.put("defaultLanguageReference", "1");
		obj.put("extensionData", new JSONObject());

		return obj;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createInterests() {		
		JSONObject obj = new JSONObject();
		obj.put("interestProperty", "CRINTEREST");
		obj.put("paymentFrequency", "Monnthly");
		obj.put("effectiveRate", "2");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONObject createAccountArrangement() {	
		JSONObject obj = new JSONObject();
		
		obj.put("closureDate", "2099-01-01");

		return obj;
	}

	@SuppressWarnings("unchecked")
	private	JSONObject createDepositArrangement() {	
		JSONObject obj = new JSONObject();
		
		obj.put("maturityDate", "2099-01-01");

		return obj;
	}

	private	JSONObject createLendingArrangement() {	
		JSONObject obj = new JSONObject();
		
		//NOT REQUIRED SINCE ACCOUNTS ARE CURRENT ACCOUNTS
		//obj.put("maturityDate", "2025-04-24");
		//obj.put("termAmount", 	"20000");
		//obj.put("sanctionedDate", "2020-04-24");

		return obj;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createRoles(Customer customer) {	
		JSONObject obj = new JSONObject();
		obj.put("partyId", LoadSmeData.COMPANY_ID+"-"+customer.getCoreCustomerId());
		obj.put("partyRole", "Owner");
		obj.put("extensionData", new JSONObject());

		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createAlternateReferences(Customer customer) {		
		JSONObject obj = new JSONObject();
		obj.put("alternateType", customer.getOsdID());
		obj.put("alternateId", "MPID/T24.IBAN");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}
}
