package com.kony.sbg.sideloading.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.sbg.sideloading.main.UpdateExistingAccountData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.sideloading.utils.Console;

public class ArrangementHelper {

	//INTERNAL DEV
	//private	final static String DNS	= "https://sbg-dev.temenos-cloud.net:443";
	//private	final static String	KEY = "LnEBgDljTC3UARLnCmhwo3nmxHqE4fUy56xVWSsC";
	
	//STANDARD BANK DEV
	//private	final static String DNS		= "https://standardbankdev.temenos-cloud.net";
	//private	final static String	KEY		= "tPir3u3P1h13gMBbBe0WR9SA78SXC3Gf8LFzZlUg";
	
	public	static String	ARRANGEMENT_AUTH_KEY	= "";
	
	private	final static String	AUTH 	= "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGYWJyaWMiLCJleHAiOjE4MTkxNzA1NTUsImlhdCI6MTU4NzU0NzEwNn0.chZWZ4KPduQaATRh3EWKM4pXkk_VpzHnISIkGitb5OAPYYDq740eVdo_aeqyiQbLrzk74JBnMJx7XI4PzrQfW7ZzHeGff4Xkx_7fiKWCuyx0cc_T8f_a2GX9zRibj42ahd1mV7A8neg1HbEAsZS4X2RN_RrLRBf6jduigU2YSIkJhN6wx0XHlzbUryxIZchCKQ74p4q8HOb77XbtToJXfBGRJMwONk1TRObMEbSZJUr488vQlgj6Iq8lCQEY_NMaAI1P-YHGxgD6jLxmkdAYt7ho63B7DhvNCw6kUJjM-zkbJ5sZCPXA-jPE8nbXrLnePvecfej2rqL9LxFJyhaxdA";
	
	private	final static String URL_CREATEAMSPARTY		= LoadSmeData.DOMAIN+"/services/SbgArrangementMS/SbgCreateArrangementParty";
	private	final static String URL_CREATEAMSACCOUNT 	= LoadSmeData.DOMAIN+"/services/SbgArrangementMS/SbgCreateArrangementAccount";
	private	final static String URL_GETMSACCOUNT 	= UpdateExistingAccountData.DOMAIN+"/services/SbgArrangementMS/SbgGetAllAccounts";
//	private	final static String URL_GETMSACCOUNT 	= LoadSmeData.DOMAIN+"/services/SbgArrangementMS/getAccountByAccountId";
	private	final static String URL_GETDBXDBACCOUNT 	= UpdateExistingAccountData.DOMAIN+"/services/SbgSideLoadingRDBServices/dbxdb_accounts_get";
	private	final static String URL_UPDATEMSACCOUNT 	= UpdateExistingAccountData.DOMAIN+"/services/SbgArrangementMS/SbgUpdateArrangementAccount";
	//private	final static String URL_CREATEAMSPARTY		= LoadSmeData.DOMAIN+"/services/ArrangementMS/CreateArrangementParty";
	//private	final static String URL_CREATEAMSACCOUNT 	= LoadSmeData.DOMAIN+"/services/ArrangementMS/CreateArrangementAccount";
	//private	final static String URL_CREATEAMSACCOUNT 	= DNS+"/services/data/v1/ArrangementObj/operations/ArrangementModel/CreateAccount";
	
	private	static int	RETRY_COUNTER	= 0;

	public static JSONObject createArrangementParty(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createArrangementParty");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_CREATEAMSPARTY, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject retval	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidAmsPartyResponse(retval)) {
				++RETRY_COUNTER;
				Console.print("ArrangementHelper.createArrangementParty() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createArrangementParty(payload);
			}
			
			RETRY_COUNTER = 0;
			return retval;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createArrangementAccount(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createArrangementAccount");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_CREATEAMSACCOUNT, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject retval	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidAMSAccountResponse(retval)) {
				++RETRY_COUNTER;
				Console.print("ArrangementHelper.createArrangementAccount() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createArrangementAccount(payload);
			}
			
			RETRY_COUNTER = 0;
			return retval;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject updateArrangementAccount(JSONObject payload) {

		if(RETRY_COUNTER == UpdateExistingAccountData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for UpdateArrangementAccount");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_UPDATEMSACCOUNT, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject retval	= (JSONObject)parser.parse(response);

			if(!CommonUtils.isValidAMSAccountResponse(retval)) {
				++RETRY_COUNTER;
				Console.print("ArrangementHelper.UpdateArrangementAccount() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return updateArrangementAccount(payload);
			}

			RETRY_COUNTER = 0;
			return retval;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject getArrangementAccountByAccountId(JSONObject payload) {

		if(RETRY_COUNTER == UpdateExistingAccountData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for getArrangementAccountByAccountId");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApiForAccounts(URL_GETMSACCOUNT, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject retval	= (JSONObject)parser.parse(response);

			if(retval == null && !retval.containsKey("arrangements") ) {
				++RETRY_COUNTER;
				Console.print("ArrangementHelper.getArrangementAccountByAccountId() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return getArrangementAccountByAccountId(payload);
			}

			RETRY_COUNTER = 0;
			return retval;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getDbxdbAccountByAccountId(String accountID) {
		Console.print("### Starting ---> ArrangementHelper::getDbxdbAccountByAccountId()");

		JSONObject payload = new JSONObject();

		StringBuilder sb = new StringBuilder();
		String transactionIdFilter = sb.append("Account_id").append(DBPUtilitiesConstants.EQUAL).append("'").append(accountID).append("'").toString();
		Console.print("### ArrangementHelper::getDbxdbAccountByAccountId() filter:" + transactionIdFilter);

		payload.put(DBPUtilitiesConstants.FILTER, transactionIdFilter);
		payload.put(DBPUtilitiesConstants.SELECT, "arrangementId");

		try {
			String response		= invokeApi(URL_GETDBXDBACCOUNT, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject retval	= (JSONObject)parser.parse(response);

			if(retval !=null && retval.containsKey("accounts") ) {

				JSONArray accounts = (JSONArray) retval.get("accounts");
				JSONObject jsonAccountObject = (accounts.size() > 0)?(JSONObject) accounts.get(0):null;
				if(jsonAccountObject!=null && jsonAccountObject.containsKey("arrangementId")) {
					return jsonAccountObject.get("arrangementId").toString();
				}
			}

			Console.print("ArrangementHelper.getDbxdbAccountByAccountId() ---> API response  "+retval);


		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;

	}

	private	static String invokeApi(String serviceURL, JSONObject payload) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(serviceURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("X-API-Key", ARRANGEMENT_AUTH_KEY);
			// conn.setRequestProperty("Authorization", AUTH);
			conn.setRequestProperty("roleId", "ADMIN");
			
			conn.setDoOutput(true);
			//conn.setDoInput(true);
			//conn.setUseCaches(false);
			
			StringBuilder postData = new StringBuilder();
			postData.append(payload);
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			conn.getOutputStream().write(postDataBytes);
	
			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	
			String output;
			//Console.print("CreateArrangement.invokeApi() ---> Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//Console.print(output);
				response.append(output);
			}
			Console.print("ArrangementHelper.invokeApi() ---> Response .... "+response);
	
			br.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.toString();
	}


	private	static String invokeApiForAccounts(String serviceURL, JSONObject payload) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(serviceURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("x-api-key", ARRANGEMENT_AUTH_KEY);
			//conn.setRequestProperty("x-functions-key", ARRANGEMENT_AUTH_KEY);
			//conn.setRequestProperty("Authorization", AUTH);
			conn.setRequestProperty("roleId", "ADMIN");

			conn.setDoOutput(true);
			//conn.setDoInput(true);
			//conn.setUseCaches(false);

			StringBuilder postData = new StringBuilder();
			postData.append(payload);
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			conn.getOutputStream().write(postDataBytes);

			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			//Console.print("CreateArrangement.invokeApi() ---> Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//Console.print(output);
				response.append(output);
			}
			Console.print("ArrangementHelper.invokeApi() ---> Response .... "+response);

			br.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.toString();
	}

}

