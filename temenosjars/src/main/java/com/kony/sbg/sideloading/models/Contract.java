package com.kony.sbg.sideloading.models;

public class Contract {
	
	private	String contractName = null;
	private	String coreCustId	= null;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String cityName;
	private String state;
	private String country;
	private String zipCode;



	public String getContractName() {
		return contractName;
	}
	
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public String getCoreCustId() {
		return coreCustId;
	}
	
	public void setCoreCustId(String coreCustId) {
		this.coreCustId = coreCustId;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public	String toString() {
		return "Contract Data:: CoreCustID -> "+coreCustId+"; ContractName: "+contractName;
	}
}
