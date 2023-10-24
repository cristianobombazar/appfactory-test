package com.kony.sbg.sideloading.models;

public class Accounts {
	private String coreCustomerId = null;
	private String accountId = null;
	private String accountType = null;
	private String accountName = null;
	private String productName = null;
	private String accountHolderName = null;
	private String accountStatus = null;
	private String arrangementId = null;
	private String OsdID = null;
	private String Currency = null;
	private String BranchName = null;
	private String BranchCode = null;
	private String SwiftCode = null;
	private String CoreBanking = null;
	private String AccountGroupCode = null;
	private String AccountStyleCode = null;
	private String AccountResidencyStatus = null;


	public String getCoreCustomerId() {
		return coreCustomerId;
	}
	public void setCoreCustomerId(String coreCustomerId) {
		this.coreCustomerId = coreCustomerId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getArrangementId() {
		return arrangementId;
	}
	public void setArrangementId(String arrangementId) {
		this.arrangementId = arrangementId;
	}
	public String getOsdID() {
		return OsdID;
	}
	public void setOsdID(String osdID) {
		OsdID = osdID;
	}
	public String getCurrency() {
		return Currency;
	}
	public void setCurrency(String currency) {
		Currency = currency;
	}
	public String getBranchName() {
		return BranchName;
	}
	public void setBranchName(String branchName) {
		BranchName = branchName;
	}
	public String getBranchCode() {
		return BranchCode;
	}
	public void setBranchCode(String branchCode) {
		BranchCode = branchCode;
	}
	public String getSwiftCode() {
		return SwiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		SwiftCode = swiftCode;
	}
	public String getCoreBanking() {
		return CoreBanking;
	}
	public void setCoreBanking(String coreBanking) {
		CoreBanking = coreBanking;
	}

	public String getAccountGroupCode() {
		return AccountGroupCode;
	}

	public void setAccountGroupCode(String accountGroupCode) {
		AccountGroupCode = accountGroupCode;
	}

	public String getAccountStyleCode() {
		return AccountStyleCode;
	}

	public void setAccountStyleCode(String accountStyleCode) {
		AccountStyleCode = accountStyleCode;
	}

	public String getAccountResidencyStatus() {
		return AccountResidencyStatus;
	}

	public void setAccountResidencyStatus(String accountResidencyStatus) {
		AccountResidencyStatus = accountResidencyStatus;
	}

	public String toString() {
		return "Accounts Data -> coreCustomerId: "+coreCustomerId;
	}
}
