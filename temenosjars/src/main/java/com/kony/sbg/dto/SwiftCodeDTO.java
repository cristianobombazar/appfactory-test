package com.kony.sbg.dto;

import com.dbp.core.api.DBPDTO;

public class SwiftCodeDTO implements DBPDTO {
	  private static final long serialVersionUID = 4865903039190150223L;
	  
	  private String swiftcode;
	  private String bankname;
	  private String countryname;
	  private String branchname;
	  private String location;
	  private String isdomestic;

	  private String cityName;
	public String getSwiftcode() {
		return swiftcode;
	}
	public void setSwiftcode(String swiftcode) {
		this.swiftcode = swiftcode;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getCountryname() {
		return countryname;
	}
	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setIsDomestic(String isdomestic){
		this.isdomestic = isdomestic;
	}

	public String getIsDomestic(){
		return isdomestic;
	}
}
