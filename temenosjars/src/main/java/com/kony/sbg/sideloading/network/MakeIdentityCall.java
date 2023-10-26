package com.kony.sbg.sideloading.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.kony.sbg.sideloading.utils.Console;

public class MakeIdentityCall {

	public static void main(String[] args) {
		invokeApi();
	}

	private	static void invokeApi() {
		try {
		URL url = new URL("https://api-gatewaynp.standardbank.co.za/npextorg/extnonprod/sysauth/oauth2/token");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("charset", "utf-8");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("X-IBM-Client-Id", "dcb68db2a80a2a88f0989bfb68a61c06"); 
		conn.setRequestProperty("X-IBM-Client-Secret", "80048151b3278f28c3bed4769a8458d4"); 
		
		conn.setUseCaches(false);
		
		String urlParameters  = "grant_type=client_credentials&scope=retail%20prod%20write";
		
		byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int postDataLength = postData.length;
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		
		conn.getOutputStream().write(postData);

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String output;
		Console.print("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			Console.print(output);
		}

		conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
