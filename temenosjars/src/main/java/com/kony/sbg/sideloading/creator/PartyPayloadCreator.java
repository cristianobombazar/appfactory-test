package com.kony.sbg.sideloading.creator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.models.Customer;

public class PartyPayloadCreator {

	private	String contractName = null;
	
	
	@SuppressWarnings("unchecked")
	public	JSONObject getPayload4Contract(String contName, String corecustcif) {
		
		this.contractName = contName;
		
		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		
		JSONObject payload = getPartyBasicInfo(customer);
		payload.put("nationalities", 			createNationalities());
		payload.put("partyLanguages",			createLanguages());
		payload.put("citizenships", 			createCititzenships());
		payload.put("personPositions", 			createPersonPositions());
		payload.put("partyLifeCycles", 			createLifeCycles());
		payload.put("otherRiskIndicators",		createOtherRiskIndicators());
		payload.put("residences", 				createResidences());
		payload.put("vulnerabilities", 			createVulnerabilities());
		payload.put("otherNames", 				createOtherNames(customer));
		payload.put("partyIdentifiers", 		createPartyIdentifiers(customer));
		payload.put("alternateIdentities",		createAlternativeIdentifiers(customer));
		payload.put("addresses",				createPartyAddresses());
		
		return payload;
		
	}
	
	@SuppressWarnings("unchecked")
	private	JSONObject getPartyBasicInfo(Customer customer) {
		JSONObject payload	= new JSONObject();

		payload.put("dateOfBirth","1950-05-05");
		payload.put("cityOfBirth", "Newyork");
		payload.put("countryOfBirth", "SA");
		payload.put("gender", "Male");
		payload.put("maritalStatus", "Married");
		payload.put("defaultLanguage", "English");
		payload.put("noOfDependents", "1");
		payload.put("reasonForNoCitizenship", "Migration from another country");
		payload.put("partyType", "Individual");
		payload.put("partyStatus", "Prospect");
		payload.put("title", "Mr");
		payload.put("firstName", "");
		payload.put("middleName", "");
		payload.put("lastName", customer.getCoreCustomerName());
		payload.put("nickName", customer.getCoreCustomerName());
		payload.put("suffix", "M.T.");
		payload.put("alias", customer.getCoreCustomerName());
		payload.put("entityName", contractName);
		payload.put("nameStartDate", "2050-05-05");
		payload.put("organisationLegalType", "Legal");
		payload.put("incorporationCountry", "SA");
		payload.put("dateOfIncorporation", "2020-05-05");
		payload.put("nameOfIncorporationAuthority", "");
		payload.put("legalForm", "Listed Company");
		payload.put("numberOfEmployees", "500");
		payload.put("birthProvince", "");
		
		return payload;
	}
	
	@SuppressWarnings("unchecked")
	private	JSONArray createNationalities() {		
		JSONObject obj = new JSONObject();
		obj.put("country", "SA");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createLanguages() {		
		JSONObject obj = new JSONObject();
		obj.put("language", "English");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createCititzenships() {		
		JSONObject obj = new JSONObject();
		obj.put("countryOfCitizenship", "SA");
		obj.put("endDate", "2050-05-05");
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createPersonPositions() {		
		JSONObject obj = new JSONObject();
		obj.put("personPositionType", "Politically Exposed-PEP");
		obj.put("countryOfPosition", "US");
		obj.put("effectiveFromDate", "2020-05-05");
		obj.put("effectiveToDate", "2050-05-05");
		obj.put("expiryDate", "2050-05-05");
		obj.put("comments", "");
		obj.put("lastUpdated", "2020-05-05");
		obj.put("informationSource", "News");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createLifeCycles() {		
		JSONObject obj = new JSONObject();
		obj.put("type", "Recognised");
		obj.put("startDate", "2020-05-05");
		obj.put("endDate", "2050-05-05");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createOtherRiskIndicators() {		
		JSONObject obj = new JSONObject();
		obj.put("type", "Adverse Media");
		obj.put("doesRiskApply", true);
		obj.put("explanationIfDoesnotApply", "");
		obj.put("explanationIfApplies", "");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createResidences() {		
		JSONObject obj = new JSONObject();

		obj.put("type", "Residence");
		obj.put("country", "SA");
		obj.put("status", "Renter");
		obj.put("region", "johannesburg");
		obj.put("statutoryRequirementMet", true);
		obj.put("statusComments", "");
		obj.put("endDate", "2050-05-05");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createVulnerabilities() {		
		JSONObject obj = new JSONObject();

		obj.put("vulnerabilityType", "Physical");
		obj.put("vulnerabilityComments", "By Birth");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createOtherNames(Customer customer) {		
		JSONObject obj = new JSONObject();

		obj.put("nameType", "LegalName");
		obj.put("fromDate", "1950-05-05");
		obj.put("name", customer.getTradingName());
		obj.put("nameLanguage", "English");
		obj.put("toDate", "2050-05-05");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createPartyIdentifiers(Customer customer) {		
		JSONObject obj = new JSONObject();

		obj.put("type", "Passport");
		obj.put("status", "New");
		obj.put("issuingAuthority", "Ministry of Foreign Affairs");
		obj.put("identifierNumber", LoadSmeData.COMPANY_ID);
		obj.put("issuedDate", "1950-05-05");
		obj.put("expiryDate", "2050-05-05");
		obj.put("issuingCountry", "SA");
		obj.put("holderName", customer.getCoreCustomerName());
		obj.put("documentTagId", customer.getCoreCustomerId());
		obj.put("primary", true);
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createAlternativeIdentifiers(Customer customer) {		
		JSONObject obj = new JSONObject();

		obj.put("identityType", "BackOfficeIdentifier");
		obj.put("identityNumber", LoadSmeData.COMPANY_ID+"-"+customer.getCoreCustomerId());
		obj.put("identitySource", "TransactT24");
		obj.put("startDate", "2020-05-05");
		obj.put("extensionData", new JSONObject());
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(obj);
		
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private	JSONArray createPartyAddresses() {
		JSONArray jsonArray = new JSONArray();
		
		jsonArray.add(createPhysicalAddresses());
		jsonArray.add(createPhoneAddresses());
		jsonArray.add(createElectronicAddresses());
		
		return jsonArray;
	}
	
	
	@SuppressWarnings("unchecked")
	private	JSONObject createPhysicalAddresses() {		
		JSONObject obj = new JSONObject();

		obj.put("communicationNature", "Physical");
		obj.put("communicationType", "MailingAddress");
		obj.put("addressType", "Office");
		obj.put("primary", "true");
		obj.put("electronicAddress", "srinag.boda@temenos.com");
		obj.put("iddPrefixPhone", "1");
		obj.put("phoneNo", "1234567890");
		obj.put("countryCode", "US");
		obj.put("flatNumber", "151");
		obj.put("floor", "2");
		obj.put("buildingNumber", "3");
		obj.put("buildingName", "Copenagen Flats");
		obj.put("streetName", "Grand Avenue");
		obj.put("town", "San leandro");
		obj.put("countrySubdivision", "California");
		obj.put("postalOrZipCode", "510-286-4444");
		obj.put("validatedBy", "Smarty Streets");
		obj.put("postBoxNumber", "A3700");
		obj.put("usePurpose", "Home Address");
		obj.put("regionCode", "015");
		obj.put("district", "Oakland");
		obj.put("department", "Sales");
		obj.put("subDepartment", "Sales Support");
		obj.put("landmark", "San leandro community center");
		obj.put("website", "temenos.com");
		obj.put("externalReference", "test");

		
		JSONObject addressFreeFormat = new JSONObject();
		addressFreeFormat.put("addressLine", "");
		
		JSONArray addressFreeFormatArray = new JSONArray();
		addressFreeFormatArray.add(addressFreeFormat);
		
		obj.put("addressFreeFormat", addressFreeFormatArray);

		obj.put("extensionData", new JSONObject());
		
		return obj;
	}

	@SuppressWarnings("unchecked")
	private	JSONObject createPhoneAddresses() {		
		JSONObject obj = new JSONObject();
		
		obj.put("addressesReference", "AD22160S19GD");
		obj.put("communicationNature", "Phone");
		obj.put("communicationType", "Mobile");
		obj.put("primary", true);
		obj.put("phoneNo", "+91-1234567890");
		obj.put("extensionData", new JSONObject());

		return obj;
	}

	@SuppressWarnings("unchecked")
	private	JSONObject createElectronicAddresses() {	
		JSONObject obj = new JSONObject();
		
		obj.put("addressesReference", "AD22160S19GD");
		obj.put("communicationNature", "Electronic");
		obj.put("communicationType", "Email");
		obj.put("primary", true);
		obj.put("electronicAddress", "abc@xyz.com");
		obj.put("extensionData", new JSONObject());

		return obj;
	}
	
}
