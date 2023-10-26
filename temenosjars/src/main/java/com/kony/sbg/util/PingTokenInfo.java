package com.kony.sbg.util;

public class PingTokenInfo {
	String refreshToken 	= null;
	String accessToken 		= null;
	long tokenExpiryInSec	= 0;
	
	public	PingTokenInfo(String rtoken, String atoken, String expiry) {
		this.refreshToken 	= rtoken;
		this.accessToken	= atoken;
		
		int expiresIn = 299;
        try {expiresIn = Integer.parseInt(expiry);}catch(Exception e) {}
		tokenExpiryInSec	= (System.currentTimeMillis()/1000) + expiresIn - 30;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getTokenExpiryInSec() {
		return tokenExpiryInSec;
	}

	public void setTokenExpiryInSec(long tokenExpiryInSec) {
		this.tokenExpiryInSec = tokenExpiryInSec;
	}

	public String toString() {
		return refreshToken+"; tokenExpiryInSec: "+tokenExpiryInSec+"; accessToken: "+accessToken;
	}
}
