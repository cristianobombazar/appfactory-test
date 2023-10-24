package com.kony.sbg.dto;

import com.dbp.core.api.DBPDTO;

public class PingTokenDTO implements DBPDTO {
	private static final long serialVersionUID = 4865903039190150223L;

	private String username;
	private String accesstoken;
	private String refreshtoken;
	private String expiresin;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAccesstoken() {
		return accesstoken;
	}
	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}
	public String getRefreshtoken() {
		return refreshtoken;
	}
	public void setRefreshtoken(String refreshtoken) {
		this.refreshtoken = refreshtoken;
	}
	public String getExpiresin() {
		return expiresin;
	}
	public void setExpiresin(String expiresin) {
		this.expiresin = expiresin;
	}

	public	String toString() {
		return "Username: "+username+"; expiresin: "+expiresin+"; refreshtoken: "+refreshtoken+"; accesstoken: "+accesstoken;
	}
}
