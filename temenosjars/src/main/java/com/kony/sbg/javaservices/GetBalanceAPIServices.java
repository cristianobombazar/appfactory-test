//package com.kony.sbg.javaservices;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.sql.Connection;
//
//import org.apache.log4j.Logger;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.kony.sbg.testservices.MakeIdentityCall;
//import com.konylabs.middleware.common.JavaService2;
//import com.konylabs.middleware.controller.DataControllerRequest;
//import com.konylabs.middleware.controller.DataControllerResponse;
//import com.konylabs.middleware.dataobject.JSONToResult;
//import com.konylabs.middleware.dataobject.Result;
//
//public class GetBalanceAPIServices implements JavaService2 {
//	private static final Logger LOG = Logger.getLogger(GetBalanceAPIServices.class);
//
//	public Connection connection = null;
//
//	@Override
//	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
//			DataControllerResponse response) throws Exception {
//		Result result = new Result();
//		JsonObject jsonObject = MakeIdentityCall.invokeApi();
//		LOG.error("@@=MakeAPi call from another java class: " + jsonObject);
//
//		if (jsonObject == null) {
//			LOG.error("@ FAILED TO RETREIVE ACCESS TOKEN FROM IBM GATEWAY");
//			return result;
//		}
//
//		// result.addParam("access_token",jsonObject.get("access_token").toString());
//		// result.addParam("expires_in",jsonObject.get("expires_in").toString());
//
//		String auth = "Bearer ";
//		String token = jsonObject.get("access_token").getAsString();
//		String authVal = auth + token;
//		LOG.error("@@=access_token" + authVal);
//
//		JsonObject balanceJsonOBJ = invokeBalanceApi(authVal);
//		LOG.error("@@=RESULT FOR BALANCE API" + balanceJsonOBJ);
//		result = JSONToResult.convert(balanceJsonOBJ.toString());
//		LOG.error("@@ Final Result:: " + result);
//		return result;
//
//	}
//
//	public JsonObject invokeBalanceApi(String authValue) throws IOException {
//		JsonObject jsonObject = new JsonObject();
//		String output = "";
//		// Map<String,String> map = new HashMap<>();
//
//		URL url = new URL("https://api-gatewaynp.standardbank.co.za/npextorg/extnonprod/branch_account/balances");
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		try {
//
//			LOG.error("@@==authValue==invokeBalanceApi" + authValue);
//			conn.setDoOutput(true);
//			conn.setInstanceFollowRedirects(false);
//
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("charset", "utf-8");
//			conn.setRequestProperty("Accept", "*/*");
//			conn.setRequestProperty("Content-Type", "application/json");
//			conn.setRequestProperty("X-IBM-Client-Id", "dcb68db2a80a2a88f0989bfb68a61c06");
//			conn.setRequestProperty("X-IBM-Client-Secret", "80048151b3278f28c3bed4769a8458d4");
//			conn.setRequestProperty("Authorization", authValue);
//
//			conn.setUseCaches(false);
//
//			String requestPayload = "{\r\n" + "    \"timestamp\": \"2021-11-23T11:54:55+03:00\",\r\n"
//					+ "    \"traceMessageId\": \"b4551770-4d1a-4ea5-a152-901ca059388f\",\r\n"
//					+ "    \"enterpriseTraceUUId\": \"e07ff043-510c-4de7-9ea9-d2cc257e9e12\",\r\n"
//					+ "    \"senderId\": \"C999734\",\r\n" + "    \"sourceSystem\": \"003\",\r\n"
//					+ "    \"sourceApplication\": \"043\",\r\n" + "    \"applicationSessionId\": \"SHABANIV\",\r\n"
//					+ "    \"sourceLocation\": \"1022\",\r\n" + "    \"version\": \"001\",\r\n"
//					+ "    \"requests\": [\r\n" + "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"107670\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"ZAR\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['1', '2']\"\r\n" + "        },\r\n"
//					+ "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"20592531\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"ZAR\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n"
//					+ "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"90390504\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"EUR\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n"
//					+ "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"90498550\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"USD\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n"
//					+ "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"90394690\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"USD\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n"
//					+ "        {\r\n" + "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"0000010006457906\",\r\n"
//					+ "                \"cbsid\": \"110\",\r\n" + "                \"bic\": \"SBZAZAJJ\",\r\n"
//					+ "                \"currency\": \"ZAR\"\r\n" + "            },\r\n"
//					+ "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n" + "        {\r\n"
//					+ "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"4451212566705213\",\r\n"
//					+ "                \"cbsid\": \"010\",\r\n" + "                \"bic\": \"SBZAZAJJ\",\r\n"
//					+ "                \"currency\": \"ZAR\"\r\n" + "            },\r\n"
//					+ "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        },\r\n" + "        {\r\n"
//					+ "            \"accountIdentification\": {\r\n"
//					+ "                \"accountNumber\": \"2226669\",\r\n" + "                \"cbsid\": \"001\",\r\n"
//					+ "                \"bic\": \"SBZAZAJJ\",\r\n" + "                \"currency\": \"ZAR\"\r\n"
//					+ "            },\r\n" + "            \"balanceTypes\": \"['0001', '0002']\"\r\n" + "        }\r\n"
//					+ "    ]\r\n" + "}";
//
//			byte[] postData = requestPayload.getBytes(StandardCharsets.UTF_8);
//			int postDataLength = postData.length;
//			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
//
//			conn.getOutputStream().write(postData);
//
//			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
//
//			}
//
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//
//			StringBuffer sb = new StringBuffer();
//
//			while ((output = br.readLine()) != null) {
//				sb.append(output);
//				// System.out.println(output);
//				// jsonObject = new JsonParser().parse(output).getAsJsonObject();
//				// System.out.println("&&jsonObject"+jsonObject.get("access_token"));
//			}
//
//			LOG.error("Response from GetBAlance Service: " + sb.toString());
//
//			jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
//			LOG.error("Parsed JSON String: " + jsonObject);
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			conn.disconnect();
//
//		}
//		LOG.error("@@return BAlance Result");
//		return jsonObject;
//
//	}
//
//	public JsonObject invokeApi() throws IOException {
//		LOG.error("@@invokeApi call ==");
//		JsonObject jsonObject = new JsonObject();
//		String output = "";
//		// Map<String,String> map = new HashMap<>();
//
//		URL url = new URL("https://api-gatewaynp.standardbank.co.za/npextorg/extnonprod/sysauth/oauth2/token");
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		try {
//
//			conn.setDoOutput(true);
//			conn.setInstanceFollowRedirects(false);
//
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("charset", "utf-8");
//			conn.setRequestProperty("Accept", "*/*");
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			conn.setRequestProperty("X-IBM-Client-Id", "dcb68db2a80a2a88f0989bfb68a61c06");
//			conn.setRequestProperty("X-IBM-Client-Secret", "80048151b3278f28c3bed4769a8458d4");
//
//			conn.setUseCaches(false);
//
//			String urlParameters = "grant_type=client_credentials&scope=retail%20prod%20write";
//
//			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
//			int postDataLength = postData.length;
//			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
//
//			conn.getOutputStream().write(postData);
//
//			if (conn.getResponseCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
//
//			}
//
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//
//			while ((output = br.readLine()) != null) {
//				LOG.error("@@invokeApi output for service" + output);
//				jsonObject = new JsonParser().parse(output).getAsJsonObject();
//				System.out.println("&&jsonObject" + jsonObject.get("access_token"));
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			conn.disconnect();
//
//		}
//		LOG.error("@@invokeApi result" + output);
//		return jsonObject;
//
//	}
//
//}
