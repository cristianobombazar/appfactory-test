package com.kony.sbg.sideloading.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.kony.sbg.sideloading.main.UpdateExistingAccountData;
import com.kony.sbg.util.SBGConstants;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.models.Accounts;
import com.kony.sbg.sideloading.models.Customer;
import com.kony.sbg.sideloading.models.UserContracts;
import com.kony.sbg.sideloading.models.UserDetails;

import static com.kony.sbg.util.SBGConstants.EMPTY_STRING;

public class CommonUtils {

	public static Map<String, String> ZA_REGION_CODES = new HashMap<>();
	
	public	static Random RANDOM = new Random();
	
	static {
		ZA_REGION_CODES.put("Eastern Cape", "EC");
		ZA_REGION_CODES.put("Free State", "FS");
		ZA_REGION_CODES.put("Gauteng", "GP");
		ZA_REGION_CODES.put("Kwazulu-Natal", "KZN");
		ZA_REGION_CODES.put("Limpopo", "LP");
		ZA_REGION_CODES.put("Mpumalanga", "MP");
		ZA_REGION_CODES.put("Northern Cape", "NC");
		ZA_REGION_CODES.put("North-West", "NW");
		ZA_REGION_CODES.put("Western Cape", "WC");
	}
	
	@SuppressWarnings("unchecked")
	public	static JSONObject getPayload4Party(String contName, String corecustcif) {
		
		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		JSONObject payload = new JSONObject();
		
		payload.put("firstName", customer.getTradingName());
		payload.put("lastName", "");
		payload.put("nickName", customer.getCoreCustomerName());
		payload.put("entityName", customer.getTradingName());
		payload.put("othername", customer.getTradingName());
		payload.put("holderName", customer.getCoreCustomerName());
		payload.put("documentTagId", customer.getCoreCustomerId());
		payload.put("partyType", "Organisation");
		payload.put("nameStartDate", (new SimpleDateFormat("yyyy-MM-dd").format(new Date()))); 
		payload.put("identityNumber", LoadSmeData.COMPANY_ID+"-"+customer.getCoreCustomerId());
		
		//Console.print("CommonUtils.getPayload4ContractInFabric ---> payload: "+payload);
		return payload;
	}
	
	@SuppressWarnings("unchecked")
	public	static JSONObject getCustomerPayload4Party(String contName, String corecustcif) {

		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		JSONObject payload = new JSONObject();

		payload.put("title", "Mr");
		payload.put("firstName", customer.getContactFirstName());
		payload.put("middleName", "");
		payload.put("lastName", customer.getContactLastName());
		payload.put("nickName", customer.getCoreCustomerName());
		payload.put("entityName", contName);
		payload.put("othername", customer.getTradingName());
		payload.put("holderName", customer.getCoreCustomerName());
		payload.put("documentTagId", customer.getCoreCustomerId());
		payload.put("partyType", "Organisation");
		payload.put("nameStartDate", (new SimpleDateFormat("yyyy-MM-dd").format(new Date())));

		payload.put("fullName", customer.getContactFirstName() + " " + customer.getContactLastName());
		payload.put("dob", getDobFormatted("2000-01-01"));
		//payload.put("ssn", userDetails.getSsn());
		//payload.put("dlno", userDetails.getDrivingLicenseNumber());

		payload.put("docTagId", customer.getCoreCustomerId());
		payload.put("idNumber", LoadSmeData.COMPANY_ID+"-"+customer.getCoreCustomerId());
		payload.put("idSource", "TransactT24");

		String ccode = customer.getContactPhoneCountryCode();
		if(ccode != null && !ccode.startsWith("+")) {
			ccode = "+"+ccode;
		}

		payload.put("email", customer.getContactEmail());
		payload.put("phoneCountryCode", ccode);
		payload.put("phoneNo", ccode+"-"+customer.getContactPhoneNumber());

		payload.put("postalAddressline1", customer.getPostalAddressLine1());
		payload.put("postalAddressline2", customer.getPostalAddressLine2());
		payload.put("postalAddressline3", customer.getPostalAddressLine3());
		payload.put("postalCity", customer.getPostalCityName());
		payload.put("postalZipcode", customer.getPostalZipCode());

		payload.put("addressline1", customer.getLegaladdressLine1());
		payload.put("addressline2", customer.getLegaladdressLine2());
		payload.put("addressline3", customer.getLegaladdressLine3());
		payload.put("city", customer.getLegalcityName());
		payload.put("district", customer.getPostalState());
		payload.put("country", customer.getCountryCode());
		payload.put("countryCode", customer.getCountryCode());
		payload.put("zipcode", customer.getLegalzipCode());

		String state = customer.getLegalstate();
		payload.put("regionCode", customer.getCountryCode()+"-"+ZA_REGION_CODES.get(state));
		payload.put("subRegionCode", ZA_REGION_CODES.get(state));

		payload.put("identifierNumber", customer.getRegistrationNo());
		payload.put("state", customer.getLegalstate());

		//Console.print("CommonUtils.getPayload4ContractInFabric ---> payload: "+payload);
		return payload;
	}

	public	static JSONObject getUserPayload4Party(Customer customer, UserDetails userDetails) {
		return getUserPayload4Party(customer, userDetails, null);
	}

	@SuppressWarnings("unchecked")
	public	static JSONObject getUserPayload4Party(Customer customer, UserDetails userDetails, UserContracts userContracts) {

		JSONObject payload = new JSONObject();

		payload.put("title", "Mr");
		payload.put("firstName", customer.getContactFirstName());
		payload.put("middleName", customer.getContactFirstName());
		payload.put("lastName", customer.getContactLastName());
		payload.put("fullName", customer.getContactFirstName() + " " + customer.getContactLastName());
		payload.put("dob", userDetails != null && userDetails.getDob() != null ? getDobFormatted(userDetails.getDob()) : "1999-01-01");
		payload.put("ssn", customer.getTaxID());
		payload.put("dlno", "");
		payload.put("partyType", "Individual");

		payload.put("entityName", customer.getCoreCustomerName());
		payload.put("othername", customer.getContactFirstName() + " " + customer.getContactLastName());
		payload.put("holderName", customer.getContactFirstName() + " " + customer.getContactLastName());

		payload.put("identifierNumber", customer.getTaxID());
		payload.put("docTagId", "");
		payload.put("idNumber", LoadSmeData.COMPANY_ID+"-"+customer.getCoreCustomerId());
		payload.put("idSource", "TransactT24");

		payload.put("email", customer.getContactEmail());

		String ccode = customer.getContactPhoneCountryCode();
		if(ccode != null && !ccode.startsWith("+")) {
			ccode = "+"+ccode;
		}

		payload.put("phoneCountryCode", ccode);
		payload.put("phoneNo", ccode+"-"+customer.getContactPhoneNumber());
		payload.put("phoneNumber", ccode+"-"+customer.getContactPhoneNumber());

		payload.put("legalAddressline1", customer.getPostalAddressLine1());
		payload.put("legalAddressline2", customer.getPostalAddressLine2());
		payload.put("legalAddressline3", customer.getPostalAddressLine3());
		payload.put("legalCity", customer.getPostalCityName());
		payload.put("legalZipcode", customer.getPostalZipCode());

		payload.put("addressline1", customer.getLegaladdressLine1());
		payload.put("addressline2", customer.getLegaladdressLine2());
		payload.put("addressline3", customer.getLegaladdressLine3());
		payload.put("city", customer.getLegalcityName());
		payload.put("district", customer.getLegalstate());
		payload.put("country", customer.getLegalcountry());
		payload.put("countryCode", customer.getCountryCode());
		payload.put("zipcode", customer.getLegalzipCode());

		String state = customer.getLegalstate();
		payload.put("regionCode", customer.getCountryCode()+"-"+ZA_REGION_CODES.get(state));
		payload.put("subRegionCode", ZA_REGION_CODES.get(state));

		payload.put("state", customer.getLegalstate());

		//Console.print("CommonUtils.getPayload4ContractInFabric ---> payload: "+payload);
		return payload;
	}

	@SuppressWarnings("unchecked")
	public	static JSONObject getPayload4PhysicalAddress(String pid, String contName, String corecustcif) {
		
		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		JSONObject payload = new JSONObject();
		
		payload.put("partyId", pid);
		payload.put("email", customer.getLegalEmail());
		payload.put("phoneNo", customer.getLegalPhoneNumber());
		payload.put("address1", customer.getLegaladdressLine1());
		payload.put("address2", customer.getLegaladdressLine2());
		payload.put("address3", customer.getLegaladdressLine3());
		payload.put("city", customer.getLegalcityName());
		payload.put("state", customer.getLegalstate());
		payload.put("zipcode", customer.getLegalzipCode());

		return payload;
	}

	@SuppressWarnings("unchecked")
	public	static JSONObject getPayload4CommunicationAddress(String pid, String physicalAddRef, String contName, String corecustcif) {
		
		Customer customer = LoadSmeData.customersMap.get(corecustcif);
		JSONObject payload = new JSONObject();
		
		payload.put("partyId", pid);
		payload.put("addressesReference", physicalAddRef);

		payload.put("email", customer.getLegalEmail());
		payload.put("phoneNo", customer.getLegalPhoneNumber());

		return payload;
	}

	@SuppressWarnings("unchecked")
	public	static JSONObject getPayload4ArrangementParty(String corecustcif) {
		
		JSONObject payload = new JSONObject();
		payload.put("partyid", LoadSmeData.COMPANY_ID+"-"+corecustcif);
		payload.put("pdate", (new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		return payload;
	}

	@SuppressWarnings("unchecked")
	public	static JSONObject getPayload4ArrangementAccount(String pid, String corecustcif, Accounts account) {
		
		JSONObject payload = new JSONObject();
		
		int extid = RANDOM.nextInt();
		String extIdStr = new String(""+extid);
		if(extIdStr.startsWith("-")) {
			extIdStr = extIdStr.substring(1);
		}

		payload.put("extArrangementId", extIdStr);	//customer.getCoreCustomerId());
		payload.put("linkRef", LoadSmeData.COMPANY_ID+"-"+account.getAccountId()); //companyid-account#
		payload.put("partyId", LoadSmeData.COMPANY_ID+"-"+corecustcif); //companyid-corecustid
		//payload.put("partyId", LoadSmeData.COMPANY_ID+"-"+pid); //customer.getCoreCustomerId()); //companyid-corecustid
		payload.put("companyReference", LoadSmeData.COMPANY_ID);
		
		payload.put("cbsid", account.getCoreBanking());
		payload.put("swiftcode", account.getSwiftCode());
		payload.put("accountType", account.getAccountType());
		payload.put("accountName", account.getAccountName());
		payload.put("productName", account.getProductName());
		payload.put("accountHolderName", account.getAccountHolderName());
		payload.put("currency", account.getCurrency());
		payload.put("branchName", account.getBranchName());
		payload.put("branchCode", account.getBranchCode());
		payload.put("osdid", account.getOsdID());
		String accountGroupCode=account.getAccountGroupCode();
		if (StringUtils.isNotBlank(accountGroupCode) && !accountGroupCode.equals("/")) {
			payload.put("accountGroupCode", accountGroupCode);
		}

		String accountStyleCode = account.getAccountStyleCode();
		if(StringUtils.isNotBlank(accountStyleCode) && !accountStyleCode.equals("/")) {
			payload.put("accountStyleCode", accountStyleCode);
		}

		String accountResidencyStatus = account.getAccountResidencyStatus();
		if(StringUtils.isNotBlank(accountResidencyStatus) && !accountResidencyStatus.equals("/")) {
			payload.put("accountResidencyStatus", accountResidencyStatus);
		}

		return payload;
	}

	public static boolean isValidPartyResponse(JSONObject response) {

		return isValidResponse(response, "id");
	}

	public static boolean isValidAddressResponse(JSONObject response) {
		if(response == null) {
			return false;
		}
		
		String id = (String)response.get("id"); 
		String addRef = (String)response.get("addressesReference"); 

		if(	id != null && id.trim().length() > 0 && 
			addRef != null && addRef.trim().length() > 0) 
		{
			return true;
		}
		
		return false;
	}

	public static boolean isValidAMSAccountResponse(JSONObject response) {
		if(response == null) {
			return false;
		}
		
		String id 		= (String)response.get("arrangementId"); 
		String status 	= (String)response.get("status");
		
		if(	id != null && id.trim().length() > 0 && 
			status != null && status.trim().length() > 0 && 
			status.equals("AMS-0017")) 
		{
			return true;
		}

		return false;
	}

	public static boolean isValidAmsPartyResponse(JSONObject response) {
		
		boolean isValid = isValidResponse(response, "partyId");
		Console.print("CommonUtils.isValidAmsPartyResponse() ---> isValid: "+isValid);
		try {
			if(!isValid) {
				Object resList = response.get("code");
				if(resList instanceof JSONArray) {
					JSONArray responseList = (JSONArray)resList;
					Console.print("CommonUtils.isValidAmsPartyResponse() ---> json array responseList: "+responseList);
					isValid = responseList.contains("AMS-0015") || responseList.contains("AMS-0017");
				}
//				JSONArray responseList = (JSONArray)response.get("resList");
//				Console.print("CommonUtils.isValidAmsPartyResponse() ---> json array responseList: "+responseList);
//				if(responseList != null && responseList.size() > 0) {
//					JSONObject obj 	= (JSONObject)responseList.get(0); 
//					String code = (String)obj.get("code");
//					if(code != null && "AMS-0015".equals(code)) {
//						//if the party is already created ... moving ahead
//						isValid = true;
//					}
//				}

				Console.print("CommonUtils.isValidAmsPartyResponse() ---> isValid: "+isValid);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return isValid;
	}
	
	public static boolean isValidResponse(JSONObject response, String key) {
		if(response == null) {
			return false;
		}
		
		String value = (String)response.get(key); 
		if(value != null && value.trim().length() > 0) {
			return true;
		}
		
		return false;
	}
	
	public	static String getDobFormatted(String dob) {
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			Date date1 = sdf1.parse(dob);

			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			return sdf2.format(date1);

		}catch(Exception e) {
			return dob;
		}
	}
	
	public	static void print(String s) {
		System.out.println(new Date()+"::"+s);
	}
	
	public static Boolean isStringEmpty(Object key) {
		if (key == null) {
			return true;
		}
		String str = key.toString();
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public	static void main(String s[]) {
		String amsPartyRes = "{\"opstatus\":8009,\"errmsg\":\"Backend request failed for service CreateArrangementParty with HTTP status code 400.\",\"responseList\":[{\"code\":\"AMS-0015\",\"message\":\"Party Details Record already exists\"}],\"httpStatusCode\":400}";
		JSONParser parser 	= new JSONParser();
		try {
			JSONObject retval	= (JSONObject)parser.parse(amsPartyRes);
			Console.print(isValidAmsPartyResponse(retval));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public	static JSONObject getPayload4ArrangementAccounts(String arrangementid) {

		JSONObject payload = new JSONObject();
		payload.put("partyid", UpdateExistingAccountData.COMPANY_ID+"-"+arrangementid);
		return payload;
	}

	public	static JSONObject getPayload4ArrangementAccountExtensionData(String extArrangementId, Accounts account, JSONObject extensionDataObj) {

		JSONObject payload = new JSONObject();


		payload.put("extArrangementId", extArrangementId);	//customer.getCoreCustomerId());

		payload.put("cbsid", extensionDataObj.get("cbsid"));
		payload.put("swiftcode", extensionDataObj.get("swiftcode"));
		payload.put("accountType", extensionDataObj.get("accountType"));
		payload.put("accountName", extensionDataObj.get("accountName"));
		payload.put("productName", extensionDataObj.get("productName"));
		payload.put("accountHolderName", extensionDataObj.get("accountHolderName"));
		payload.put("currency", extensionDataObj.get("currency"));
		payload.put("branchName", extensionDataObj.get("branchName"));
		payload.put("branchCode", extensionDataObj.get("branchCode"));
		payload.put("osdid", extensionDataObj.get("osdid"));
		String accountGroupCode=account.getAccountGroupCode();
		if (StringUtils.isNotBlank(accountGroupCode) && !accountGroupCode.equals("/")) {
			payload.put("accountGroupCode", accountGroupCode);
		}

		String accountStyleCode = account.getAccountStyleCode();
		if(StringUtils.isNotBlank(accountStyleCode) && !accountStyleCode.equals("/")) {
			payload.put("accountStyleCode", accountStyleCode);
		}

		String accountResidencyStatus = account.getAccountResidencyStatus();
		if(StringUtils.isNotBlank(accountResidencyStatus) && !accountResidencyStatus.equals("/")) {
			payload.put("accountResidencyStatus", accountResidencyStatus);
		}

		return payload;
	}
}
