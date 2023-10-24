package com.kony.sbg.dto;

import com.temenos.dbx.product.payeeservices.dto.ExternalPayeeBackendDTO;

public class SbgExternalPayeeBackendDTO extends ExternalPayeeBackendDTO {

		private static final long serialVersionUID = -5264591122187648013L;

	    private String entityType;
	    private String beneCode;
	    private String state;
		private String beneLimit;
		private String paymentCurrency;
		private String beneficiaryReference;
		private String statementReference;
		private String proofOfPayment;
		private String cellphone;
		private String emailAddress;
		private boolean isApproved;

		private String residencyStatus;

	    public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
	    public String getEntityType() {
	    	return entityType;
	    }

	    public void setEntityType(String entityType) {
	    	this.entityType = entityType;
	    }

	    public String getBeneCode() {
	    	return beneCode;
	    }

	    public void setBeneCode(String beneficiaryCode) {
	    	this.beneCode = beneficiaryCode;
	    }

		public String getResidencyStatus() {
			return residencyStatus;
		}

		public void setResidencyStatus(String residencyStatus) {
			this.residencyStatus = residencyStatus;
		}

		public String getBeneLimit() {
			return beneLimit;
		}

		public void setBeneLimit(String beneLimit) {
			this.beneLimit = beneLimit;
		}

		public String getPaymentCurrency() {
			return paymentCurrency;
		}

		public void setPaymentCurrency(String paymentCurrency) {
			this.paymentCurrency = paymentCurrency;
		}

		public String getBeneficiaryReference() {
			return beneficiaryReference;
		}

		public void setBeneficiaryReference(String beneficiaryReference) {
			this.beneficiaryReference = beneficiaryReference;
		}

		public String getStatementReference() {
			return statementReference;
		}

		public void setStatementReference(String statementReference) {
			this.statementReference = statementReference;
		}

		public String getProofOfPayment() {
			return proofOfPayment;
		}

		public void setProofOfPayment(String proofOfPayment) {
			this.proofOfPayment = proofOfPayment;
		}

		public String getCellphone() {
			return cellphone;
		}

		public void setCellphone(String cellphone) {
			this.cellphone = cellphone;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}

		public boolean isApproved() {
			return isApproved;
		}

		public void setApproved(boolean approved) {
			isApproved = approved;
		}

	public SbgExternalPayeeBackendDTO() {
			super();
		}

		public SbgExternalPayeeBackendDTO(String id, String userId, String bankId, String nickName, String firstName,
				String lastName, String routingNumber, String accountNumber, String accountType, String notes,
				String countryName, String swiftCode, String user_Account, String beneficiaryName,
				String isInternationalAccount, String bankName, String isSameBankAccount, String softDelete,
				String isVerified, String createdOn, String externalaccount, String iban, String sortCode,
				String phoneCountryCode, String phoneNumber, String phoneExtension, String addressNickName,
				String addressLine1, String city, String zipcode, String country, String externalaccountcol, String cif,
				String isBusinessPayee, String companyId, String dbpErrCode, String dbpErrMsg, String payeeId, String noOfCustomersLinked,
				String addressLine2, String email, String phone, String entityType, String beneCode,String state,String residencyStatus) {

			super( 	id,userId,bankId,nickName,firstName,
					lastName,routingNumber,accountNumber,accountType,notes,
					countryName,swiftCode,user_Account,beneficiaryName,
					isInternationalAccount,bankName,isSameBankAccount,softDelete,
					isVerified,createdOn,externalaccount,iban,sortCode,
					phoneCountryCode,phoneNumber,phoneExtension,addressNickName,
					addressLine1,city,zipcode,country,externalaccountcol,cif,
					isBusinessPayee,companyId,dbpErrCode,dbpErrMsg,payeeId,noOfCustomersLinked,
					addressLine2,email,phone);
			
	        this.entityType = entityType;
	        this.beneCode 	= beneCode;
	        this.state 	= state;
			this.residencyStatus=residencyStatus;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			 result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
		     result = prime * result + ((beneCode == null) ? 0 : beneCode.hashCode());
		     result = prime * result + ((state == null) ? 0 : state.hashCode());
		     	
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SbgExternalPayeeBackendDTO other = (SbgExternalPayeeBackendDTO) obj;
			if (getId() == null) {
				if (other.getId() != null)
					return false;
			} else if (!getId().equals(other.getId()))
				return false;
			return true;
		}
		public boolean isValidInput() {
//	    	if(StringUtils.isNotBlank(this.accountNumber) && !ValidationUtils.isValidAccountNumber(this.accountNumber) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.iban) && !ValidationUtils.isValidAccountNumber(this.iban) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.beneficiaryName) && !ValidationUtils.isValidName(this.beneficiaryName) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.nickName) && !ValidationUtils.isValidName(this.nickName) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.addressLine1) && !ValidationUtils.isValidText(this.addressLine1) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.addressLine2) && !ValidationUtils.isValidText(this.addressLine2) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.city) && !ValidationUtils.isValidText(this.city) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.country) && !ValidationUtils.isValidText(this.country) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.zipcode) && !ValidationUtils.isValidText(this.zipcode) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.email) && !ValidationUtils.isValidEmail(this.email) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.phone) && !ValidationUtils.isValidPhoneNumber(this.phone) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.swiftCode) && !ValidationUtils.isValidSwiftCode(this.swiftCode) ) {
//	    		return false;
//	    	}
//	    	if(StringUtils.isNotBlank(this.routingNumber) && !ValidationUtils.isValidRoutingNumber(this.routingNumber) ) {
//	    		return false;
//	    	}
	    	return true;
	    }
	}


