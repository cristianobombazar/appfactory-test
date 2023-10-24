package com.kony.sbg.sideloading.models;

public class UserContracts {

	private	String coreCustomerId	= null;
	private	String companyName		= null;
	private	String contractName		= null;
	private	String isPrimary		= null;
	private	String roleId			= null;
	private	String accounts			= null;
	private	String email			= null;
	
	public String getCoreCustomerId() {
		return coreCustomerId;
	}

	public void setCoreCustomerId(String coreCustomerId) {
		this.coreCustomerId = coreCustomerId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String toString() {
		return "User Contracts Data -> coreCustomerId: "+coreCustomerId+"; companyName:"+companyName+"; roleId:"+roleId+"; accounts:"+accounts+"; email:"+email;
	}
	
}
