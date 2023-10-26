package com.kony.sbg.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kony.sbg.preprocessor.MyAccessAuthPreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;

public class PingTokenValidator {
	
	private static final Logger logger = Logger.getLogger(PingTokenValidator.class);

	public static boolean isValidToken(String token, DataControllerRequest dcRequest, String endPoint) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			String keyId = jwt.getKeyId();
			JSONObject jwks = getJWKSKeys(keyId, dcRequest, endPoint);
			verifySignature(jwt, jwks);
			return true;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureVerificationException e) {
			//e.printStackTrace();
			logger.error("PingTokenValidator.isValidToken --- 1 EXCEPTION: "+e.getMessage());
			return false;
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("PingTokenValidator.isValidToken --- 2 EXCEPTION: "+e.getMessage());
			return false;
		}
	}

	private static void verifySignature(DecodedJWT jwt, JSONObject jwks)
			throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureVerificationException {
		PublicKey publicKey = getPublicKey(jwks);
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
		algorithm.verify(jwt);
	}

	private static PublicKey getPublicKey(JSONObject jwks) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String modulus = jwks.getString("n");
		String exponent = jwks.getString("e");
		byte[] modulusByte = Base64.getUrlDecoder().decode(modulus);
		BigInteger modulusAsBigInt = new BigInteger(1, modulusByte);

		byte[] exponentByte = Base64.getUrlDecoder().decode(exponent);
		BigInteger exponentAsBigInt = new BigInteger(1, exponentByte);

		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusAsBigInt, exponentAsBigInt);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pub = factory.generatePublic(spec);
		return pub;
	}

	private static JSONObject getJWKSKeys(String keyId, DataControllerRequest dcRequest, String endPoint)
			throws Exception {
		JSONObject jwks = null;
		jwks = GetJSONWebKeySet(dcRequest, endPoint);
		JSONArray JE = jwks == null ? null : jwks.getJSONArray("keys");
		int count = JE == null ? 0 : JE.length();
		for (int i = 0; i < count; i++) {
			JSONObject obj = JE.getJSONObject(i);
			if (obj.has("kid")) {
				if (StringUtils.equalsIgnoreCase((String) obj.get("kid"), keyId)) {
					return obj;
				}
			}
		}
		return new JSONObject();
	}

	private static JSONObject GetJSONWebKeySet(DataControllerRequest dcRequest, String endPoint) throws Exception {
		try {
			String PING_ISS_URL = "https://enterprisestssit.standardbank.co.za";
			PING_ISS_URL = SBGCommonUtils.getServerPropertyValue("PING_ISS_URL", dcRequest);
			URL url = new URL(PING_ISS_URL + endPoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Infinity");
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuffer sb = new StringBuffer();
			String output = "";
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObject = new JSONObject(sb.toString());
			conn.disconnect();
			return jsonObject;
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			logger.error("PingTokenValidator.GetJSONWebKeySet --- 1 EXCEPTION: "+e.getMessage());
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("PingTokenValidator.GetJSONWebKeySet --- 2 EXCEPTION: "+e.getMessage());
		}
		return null;
	}
}