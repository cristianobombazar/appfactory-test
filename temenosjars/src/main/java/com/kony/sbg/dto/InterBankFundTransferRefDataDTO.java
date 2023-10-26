package com.kony.sbg.dto;

import com.dbp.core.api.DBPDTO;

public class InterBankFundTransferRefDataDTO implements DBPDTO {

	private static final long serialVersionUID = 4865903039190150223L;
	private String referenceId;
	private String purposecode;
	private String compliancecode;
	private String statementrefno;
	private String benerefeno;
	private String rfqDetails;
	private String beneficiaryState;
	private String submitPaymentId;
	private String submitPaymentResponse;
	private String bopDetails;
	private String clearingCode;
	private String beneficiaryAddressLine2;
	private String confirmationNumber;

	private String beneficiaryPhone ;
	private String beneficiaryEmail ;

	private String proofOfPayment;
	private String beneficiaryCategory ;
	private String beneCode ;
	private String fromAccountName;

	public String getProofOfPayment() {
		return proofOfPayment;
	}

	public void setProofOfPayment(String proofOfPayment) {
		this.proofOfPayment = proofOfPayment;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	public String getRfqDetails() {
		return rfqDetails;
	}

	public void setRfqDetails(String rfqDetails) {
		this.rfqDetails = rfqDetails;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getPurposecode() {
		return purposecode;
	}

	public void setPurposecode(String purposecode) {
		this.purposecode = purposecode;
	}

	public String getCompliancecode() {
		return compliancecode;
	}

	public void setCompliancecode(String compliancecode) {
		this.compliancecode = compliancecode;
	}

	public String getStatementrefno() {
		return statementrefno;
	}

	public void setStatementrefno(String statementrefno) {
		this.statementrefno = statementrefno;
	}

	public String getBenerefeno() {
		return benerefeno;
	}

	public void setBenerefeno(String benerefeno) {
		this.benerefeno = benerefeno;
	}

	public String getBeneficiaryState() {
		return beneficiaryState;
	}

	public void setBeneficiaryState(String beneficiaryState) {
		this.beneficiaryState = beneficiaryState;
	}

	public String getSubmitPaymentId() {
		return submitPaymentId;
	}

	public void setSubmitPaymentId(String submitPaymentId) {
		this.submitPaymentId = submitPaymentId;
	}

	public String getSubmitPaymentResponse() {
		return submitPaymentResponse;
	}

	public void setSubmitPaymentResponse(String submitPaymentResponse) {
		this.submitPaymentResponse = submitPaymentResponse;
	}

	public String getBopDetails() {
		return bopDetails;
	}

	public void setBopDetails(String bopDetails) {
		this.bopDetails = bopDetails;
	}

	public String getClearingCode() {
		return clearingCode;
	}

	public void setClearingCode(String clearingCode) {
		this.clearingCode = clearingCode;
	}

	public String getBeneficiaryAddressLine2() {
		return beneficiaryAddressLine2;
	}


	public String getBeneficiaryCategory() {
		return this.beneficiaryCategory;
	}

	public void setBeneficiaryCategory(String beneficiaryCategory) {
		this.beneficiaryCategory = beneficiaryCategory;
	}


	public String getBeneCode() {
		return this.beneCode;
	}

	public void setBeneCode(String beneCode) {
		this.beneCode = beneCode;
	}

	public String getFromAccountName(){
		return this.fromAccountName;
	}

	public void setFromAccountName(String fromAccountName){
		this.fromAccountName = fromAccountName;
	}

	@Override
	public String toString() {
		return "InterBankFundTransferRefDataDTO{" +
				"referenceId='" + referenceId + '\'' +
				", purposecode='" + purposecode + '\'' +
				", compliancecode='" + compliancecode + '\'' +
				", statementrefno='" + statementrefno + '\'' +
				", benerefeno='" + benerefeno + '\'' +
				", rfqDetails='" + rfqDetails + '\'' +
				", beneficiaryState='" + beneficiaryState + '\'' +
				", submitPaymentId='" + submitPaymentId + '\'' +
				", submitPaymentResponse='" + submitPaymentResponse + '\'' +
				", bopDetails='" + bopDetails + '\'' +
				", clearingCode='" + clearingCode + '\'' +
				", beneficiaryAddressLine2='" + beneficiaryAddressLine2 + '\'' +
				", confirmationNumber='" + confirmationNumber + '\'' +
				", beneficiaryPhone='" + beneficiaryPhone + '\'' +
				", beneficiaryEmail='" + beneficiaryEmail + '\'' +
				", proofOfPayment='" + proofOfPayment + '\'' +
				", beneficiaryCategory='" + beneficiaryCategory + '\'' +
				", beneCode='" + beneCode + '\'' +
				", fromAccountName='" + fromAccountName + '\'' + 
				'}';
	}

	public void setBeneficiaryAddressLine2(String beneficiaryAddressLine2) {
		this.beneficiaryAddressLine2 = beneficiaryAddressLine2;
	}


	public String getBeneficiaryPhone() {
		return this.beneficiaryPhone;
	}

	public void setBeneficiaryPhone(String beneficiaryPhone) {
		this.beneficiaryPhone = beneficiaryPhone;
	}

	public String getBeneficiaryEmail() {
		return this.beneficiaryEmail;
	}

	public void setBeneficiaryEmail(String beneficiaryEmail) {
		this.beneficiaryEmail = beneficiaryEmail;
	}


}
