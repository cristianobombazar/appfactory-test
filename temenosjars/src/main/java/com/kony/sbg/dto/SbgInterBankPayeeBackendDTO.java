package com.kony.sbg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeBackendDTO;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SbgInterBankPayeeBackendDTO extends InterBankPayeeBackendDTO {
    private static final long serialVersionUID = 4874020940595684685L;

    private String entityType;
    private String beneCode;
    private String state;
    private String beneLimit, paymentCurrency, beneficiaryReference, statementReference, proofOfPayment, cellphone, emailAddress;
    private boolean isApproved;

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
        return beneCode.toUpperCase();
    }

    public void setBeneCode(String beneficiaryCode) {
        this.beneCode = beneficiaryCode;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public SbgInterBankPayeeBackendDTO() {
        /* default constructor */
        super();
    }

    public SbgInterBankPayeeBackendDTO(String id, String userId, String accountNumber, String accountType,
                                       String bankName, String beneficiaryName, String countryName, String createdOn, String firstName,
                                       String isInternationalAccount, String isSameBankAccount, String isVerified, String lastName,
                                       String nickName, String notes, String routingNumber, String swiftCode, String isBusinessPayee, String cif,
                                       String softDelete, String externalAccount, String iban, String sortCode, String phoneCountryCode,
                                       String phoneNumber, String phoneExtension, String addressNickName, String addressLine1, String city,
                                       String zipcode, String country, String customerId, String companyId, String externalAccountCol,
                                       String dbpErrCode, String dbpErrMsg, String deletedRecords, String payeeId, String noOfCustomersLinked,
                                       String addressLine2, String email, String phone, String entityType, String beneCode, String state, boolean isApproved) {
        super(id, userId, accountNumber, accountType, bankName, beneficiaryName, countryName, createdOn, firstName,
                isInternationalAccount, isSameBankAccount, isVerified, lastName, nickName, notes, routingNumber,
                swiftCode, isBusinessPayee, cif, softDelete, externalAccount, iban, sortCode, phoneCountryCode,
                phoneNumber, phoneExtension, addressNickName, addressLine1, city, zipcode, country, customerId,
                companyId, externalAccountCol, dbpErrCode, dbpErrMsg, deletedRecords, payeeId, noOfCustomersLinked,
                addressLine2, email, phone);

        this.entityType = entityType;
        this.beneCode = beneCode;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        SbgInterBankPayeeBackendDTO other = (SbgInterBankPayeeBackendDTO) o;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + ((beneCode == null) ? 0 : beneCode.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    public boolean isValidInput() {
        // if(StringUtils.isNotBlank(this.accountNumber) &&
        // !ValidationUtils.isValidAccountNumber(this.accountNumber) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.iban) &&
        // !ValidationUtils.isValidAccountNumber(this.iban) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.beneficiaryName) &&
        // !ValidationUtils.isValidName(this.beneficiaryName) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.nickName) &&
        // !ValidationUtils.isValidName(this.nickName) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.addressLine1) &&
        // !ValidationUtils.isValidText(this.addressLine1) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.addressLine2) &&
        // !ValidationUtils.isValidText(this.addressLine2) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.city) &&
        // !ValidationUtils.isValidText(this.city) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.country) &&
        // !ValidationUtils.isValidText(this.country) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.zipcode) &&
        // !ValidationUtils.isValidText(this.zipcode) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.email) &&
        // !ValidationUtils.isValidEmail(this.email) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.phone) &&
        // !ValidationUtils.isValidPhoneNumber(this.phone) ) {
        // return false;
        // }
        // if(StringUtils.isNotBlank(this.swiftCode) &&
        // !"N/A".equalsIgnoreCase(this.swiftCode) &&
        // !ValidationUtils.isValidSwiftCode(this.swiftCode) ) {
        // return false;
        // }
        return true;
    }
}
