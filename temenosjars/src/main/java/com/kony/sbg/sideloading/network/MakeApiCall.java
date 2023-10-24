package com.kony.sbg.sideloading.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.kony.sbg.sideloading.utils.Console;

public class MakeApiCall {

	public static void main(String[] args) {
		invokeApi();
	}

	private	static void invokeApi() {
		try {
			URL url = new URL("https://api-gatewaynp.standardbank.co.za/npextorg/extnonprod/ping/notifications/email");
			//URL url = new URL("https://sbg-dev.temenos-cloud.net:443/services/PartyMSServiceDevTest/DevGetPartyByPartyIdV4");
			//URL url = new URL("https://standardbankdev.temenos-cloud.net:443/services/PartyMSServiceDevTest/DevGetPartyByPartyIdV5");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/json");
			
			//conn.setRequestProperty("X-API-Key", "cqa4aFqBCaaagll9VtU58b0AXf1bKTG5E2zEXHe8"); //internal dev
			//conn.setRequestProperty("X-API-Key", "plPksJ8hmY9XGMLmt9PKS7BHS9i16uQQ8VV2mjOB"); //sbg dev
			//conn.setRequestProperty("X-Kony-Authorization", "eyAidHlwIjogImp3dCIsICJhbGciOiAiUlMyNTYiIH0.eyAiX2VtYWlsIjogIlJhamVzaC5rdW1hckB0ZW1lbm9zLmNvbSIsICJfdmVyIjogInYxLjEiLCAiaXNzIjogImh0dHBzOi8vMTAwMDAwMDM0LmF1dGgudGVtZW5vcy1jbG91ZC5uZXQiLCAiX3Njb3BlIjogImciLCAiX2lzc21ldGEiOiAiL21ldGFkYXRhL0dMN0tRbnZBbW1TSmdCQVdDZGNka3c9PSIsICJfYXBwIjogIjU5MWI0OWE4LWRjYWItNDg3OC05MWI0LTJmNGYxMmExN2UwMSIsICJfc2Vzc2lvbl9pZCI6ICIwOTk3MDA1OC04NmEzLTQyYjEtOGQ1Yy03MTdlYzhjMWQ0NjEiLCAiX3B1aWQiOiA0LCAiX2F1dGh6IjogImV5SndaWEp0YVhOemFXOXVjeUk2ZTMwc0luSnZiR1Z6SWpwYlhYMCIsICJfaWRwIjogIkRieFVzZXJMb2dpbiIsICJleHAiOiAxNjU0NjMyMDU3LCAiaWF0IjogMTY1NDYzMDg1NywgIl9zZXNzaW9uX3RpZCI6ICIxMDAwMDAwMzQiLCAiX3Byb3ZfdXNlcmlkIjogIjkzNjMzMTQyNzIiLCAianRpIjogIjMxOGI2M2FkLWM2N2ItNGExNy1hNTcyLTNiZmY3YTAyZDBlOSIsICJfYWNzIjogIjEwMDAwMDAzNCIsICJfcHJvdmlkZXJzIjogWyAiRGJ4VXNlckxvZ2luIiBdIH0.k7Z-Y8oUdzQnaZ8iOmdZzxL11Xr9ttoS_dl1Zy6MQxkKtys3CCPinmy4Sz1wQDSie5aCrnvMq3jUEUxHVAu4VrLCbw43TYc62Upi5l2T61_8raCawBdmnxs5Qf6VMTuQZGsNXtcUlD3b-JTn1ceMdiJEb7DPYkuBSeKsxiBwXnl5_v4Edv_Nw95S33fg_iwM6LvVIc_l5rfvLDicQctE4-mc4wr3CAGCj243jymhD7picsE3O6Ob4yZa1rZtXfgcABW21nm8WQzDl5AMQHt2BL-ThE224ohNFGd4ChcmkbV5DQwyYqecK3RAkAfBQAOhoQdLVfVkRSc3RBxBXEkXoA");
			
			conn.setRequestProperty("X-IBM-Client-Id", "dcb68db2a80a2a88f0989bfb68a61c06"); 
			conn.setRequestProperty("X-IBM-Client-Secret", "80048151b3278f28c3bed4769a8458d4"); 
			conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImRpZC0yIiwicGkuYXRtIjoieHkwaCJ9.eyJzY3AiOltdLCJjbGllbnRfaWQiOiJkYTNhM2FhNi05MWZkLTRmNjgtOGVkOC1hNWM2ODNlM2U3ZTMiLCJpc3MiOiJodHRwczovL2VudGVycHJpc2VzdHNzaXQuc3RhbmRhcmRiYW5rLmNvLnphIiwianRpIjoiWXVCTU9hUGVoV1lCTkVER3pJeW80TSIsInN1YiI6ImRhM2EzYWE2LTkxZmQtNGY2OC04ZWQ4LWE1YzY4M2UzZTdlMyIsIm5iZiI6MTY2NDQ2NTg1NCwiaWQiOiJkYTNhM2FhNi05MWZkLTRmNjgtOGVkOC1hNWM2ODNlM2U3ZTMiLCJpYXQiOjE2NjQ0NjU4NTQsImV4cCI6MTY2NDQ2OTQ1NH0.gD24nQkcAdwT4rF4qJkE29JVHf0KuwzmKxz5T5eO2tfsPoD6zXbmrq8sD7I7YK-xTm3iBqlMF73yHq9Wh6zQGJP4SnYl7sJFdNd8xci1LPIz6f3PTgIg-8RtmTaYvXaOM2h4ZkemPOrq80613HsX-2mSPXtIxgJCFBPNhjzogIQwZ0LWjvwdLXWRzi7JVwGoN8jW6Fz00P5_OCQ19uz-HoG956-v62f5lEW9T_htzTzEFcKLIxEoRctZW292b3PocnVen4rgxA9gO5ITEfFORt7Qd2zx6nLP7j6xOhKdNHhU9hN3ICaX6fQbKaGhKJ6NwYDNdd_He8-k90UqGOIwmw"); 
			conn.setRequestProperty("x-fapi-interaction-id", "80048151b3278f28c3bed4769a8458d4"); 
			
			conn.setDoOutput(true);
			//conn.setDoInput(true);
			//conn.setUseCaches(false);
			
			StringBuilder postData = new StringBuilder();
			//postData.append("{\"partyid\":\"2216543671\"}");
			//postData.append("{\"partyid\":\"2216165962\"}");
			postData.append(getPayload4Cms());
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			conn.getOutputStream().write(postDataBytes);
	
//			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ conn.getResponseCode());
//			}
	
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getPayload4Cms() throws Exception {
		String str = "{\"requestHeader\":{\"timestamp\":\"2022-09-02T11:58:50.146Z\",\"traceMessageId\":\"57e91b0c-8010-4cbf-bb01-1347487119e4\",\"sender\":{\"senderId\":\"BOLP\"}},\"message\":{\"sourceCountry\":\"ZA\",\"senderParty\":{\"senderAddress\":\"bolp@standardbank.co.za\"},\"messageContent\":{\"header\":{\"content\":\"SignInSuccessful\"},\"body\":{\"textSection\":{\"textType\":\"TEXT\",\"content\":\"DearChrystallVallen,<br/><br class=\\\"\\\"/>YourPaymenthasbeensentforauthorisation.<br class=\\\"\\\"/><br class=\\\"\\\"/>BeneficiaryName:BHKSN<br class=\\\"\\\"/>Paymentamount:ZAR0.10<br class=\\\"\\\"/>Valuedate:2022-09-28<br class=\\\"\\\"/>BeneficiaryA/CNo.:XXXX2345<br class=\\\"\\\"/>ReferenceNo.:Z4DOCEBVPXYJUOVP<br class=\\\"\\\"/><br class=\\\"\\\"/>Kindregards<br class=\\\"\\\"/>StandardBank\"}},\"destinationParty\":{\"address\":[{\"value\":\"chrystall.vallen@standardbank.co.za\"}]}}}}";
		
		str = "{}";
		
		JSONParser jp = new JSONParser();
		JSONObject jo = (JSONObject)jp.parse(str);
		
		return jo.toJSONString();
		//return str;
	}
}
