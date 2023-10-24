package com.kony.sbg.sideloading.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.sideloading.utils.Console;

public class PartyHelper {
	
	public	static String	PARTY_AUTH_KEY				= "";
	
	private	final static String URL_CREATEPARTYRECORD		= LoadSmeData.DOMAIN+"/services/PartyMS/CreateParty";
	private	final static String URL_CREATEPHYSICALADDR 		= LoadSmeData.DOMAIN+"/services/PartyMS/CreatePhysicalAddress";
	private	final static String URL_CREATECOMMADDRESS 		= LoadSmeData.DOMAIN+"/services/PartyMS/CreateCommunicationAddress";
	//private final static String URL_CREATEUSERPARTYRECORD	= LoadSmeData.DOMAIN+"/services/PartyMS/CreateUserPartyWithAddress";
	public final static String URL_CREATEUSERPARTYRECORD	= LoadSmeData.DOMAIN+"/services/SbgPartyMS/SbgCreateUserPartyWithAddress";
	
	private	static int	RETRY_COUNTER	= 0;

	public static JSONObject createPartyRecord(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createPartyRecord");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_CREATEPARTYRECORD, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidPartyResponse(party)) {
				++RETRY_COUNTER;
				Console.print("CreateParty.createPartyRecord() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createPartyRecord(payload);
			}
			
			RETRY_COUNTER = 0;

			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createPhysicalAddress(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createPhysicalAddress");
			RETRY_COUNTER = 0;
			return retval;
		}
		
		try {
			String response		= invokeApi(URL_CREATEPHYSICALADDR, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidAddressResponse(party)) {
				++RETRY_COUNTER;
				Console.print("CreateParty.createPhysicalAddress() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createPhysicalAddress(payload);
			}
			
			RETRY_COUNTER = 0;
			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createCommunicationAddress(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createCommunicationAddress");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_CREATECOMMADDRESS, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidAddressResponse(party)) {
				++RETRY_COUNTER;
				Console.print("CreateParty.createCommunicationAddress() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createCommunicationAddress(payload);
			}
			
			RETRY_COUNTER = 0;
			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createUserPartyRecord(JSONObject payload) {
		
		if(RETRY_COUNTER == LoadSmeData.RETRY_ATTEMPTS) {
			JSONObject retval = new JSONObject();
			retval.put("RETRY.FAILURE", "RETRY ATTEMPTS HAS REACHED TO ITS MAXIMUM for createUserPartyRecord");
			RETRY_COUNTER = 0;
			return retval;
		}

		try {
			String response		= invokeApi(URL_CREATEUSERPARTYRECORD, payload);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			if(!CommonUtils.isValidPartyResponse(party)) {
				++RETRY_COUNTER;
				Console.print("CreateParty.createUserPartyRecord() ---> Party API failed. Reattempting for "+RETRY_COUNTER);
				return createUserPartyRecord(payload);
			}
			
			RETRY_COUNTER = 0;

			return party;
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
			conn.setRequestProperty("X-API-Key", PARTY_AUTH_KEY);
			
			conn.setDoOutput(true);
			//conn.setDoInput(true);
			//conn.setUseCaches(false);
			
			StringBuilder postData = new StringBuilder();
			postData.append(payload);
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			conn.getOutputStream().write(postDataBytes);
	
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	
			String output;
			//Console.print("CreateParty.invokeApi() ---> Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//Console.print(output);
				response.append(output);
			}
			Console.print("CreateParty.invokeApi() ---> Response .... "+response);
	
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
