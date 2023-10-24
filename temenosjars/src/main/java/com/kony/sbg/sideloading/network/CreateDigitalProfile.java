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
import com.kony.sbg.sideloading.utils.Console;

public class CreateDigitalProfile {

	//INTERNAL DEV
	//private	final static String DNS	= "https://sbg-dev.temenos-cloud.net:443";
	
	//STANDARD BANK DEV
	//private	final static String DNS	= "https://standardbankdev.temenos-cloud.net";
	
	//private	final static String URL	= LoadSmeData.DOMAIN+"/services/dbpTestScripts/CreateDemoUsers";
	private	final static String URL_CREATE_CONTRACT_USER	= LoadSmeData.DOMAIN+"/services/SbgTestScripts/CreateContractAndUsers";
	private	final static String URL_CREATE_CONTRACT_ONLY	= LoadSmeData.DOMAIN+"/services/SbgCreateDigitalProfile/SbgCreateSmeContract";
	private	final static String URL_CREATE_USER_ONLY		= LoadSmeData.DOMAIN+"/services/SbgCreateDigitalProfile/SbgCreateSmeUser";
//	private	final static String URL_CREATE_CONTRACT_ONLY	= LoadSmeData.DOMAIN+"/services/SbgTestScripts/CreateSbgSmeContract";
//	private	final static String URL_CREATE_USER_ONLY		= LoadSmeData.DOMAIN+"/services/SbgTestScripts/CreateSbgSmeUser";

	public static JSONObject createDigitalProfileRecord(JSONObject payload) {
		
		try {
			String response		= invokeApi(payload, URL_CREATE_CONTRACT_USER);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createDigitalContract(JSONObject payload) {
		
		try {
			String response		= invokeApi(payload, URL_CREATE_CONTRACT_ONLY);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static JSONObject createDigitalUser(JSONObject payload) {
		
		try {
			String response		= invokeApi(payload, URL_CREATE_USER_ONLY);
			JSONParser parser 	= new JSONParser();
			JSONObject party	= (JSONObject)parser.parse(response);
			
			return party;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private	static String invokeApi(JSONObject payload, String serviceUrl) {
		StringBuilder response = new StringBuilder();
		try {
		URL url = new URL(serviceUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Content-Type", "application/json");
		
		conn.setDoOutput(true);
		//conn.setDoInput(true);
		//conn.setUseCaches(false);
		
		conn.setConnectTimeout(1000 * 300);
		conn.setReadTimeout(1000 * 300);
		
		StringBuilder postData = new StringBuilder();
		postData.append(payload);
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");
		conn.getOutputStream().write(postDataBytes);

		if (conn.getResponseCode() != 200) {
			Console.print("CreateDigitalProfile.invokeApi() ---> Response code: "+conn.getResponseCode());
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		//Console.print("CreateDigitalProfile.invokeApi() ---> Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			//Console.print(output);
			response.append(output);
		}
		//Console.print("CreateDigitalProfile.invokeApi() ---> Response .... "+response);

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
