package com.kony.sbg.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.temenos.dbx.product.transactionservices.dto.TransferDTO;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(Include.NON_NULL)
public class DomesticFundTransferDTO extends TransferDTO implements DBPDTO {
    private static final long serialVersionUID = -3912113288301575348L;
    private String iban;
    private String bicCode;
    private String bankName;
    private String bankId;
    private String feeCurrency;
    private String beneficiaryName;
    private String paymentType;
    private String feeAmount;
    private String beneficiaryAddressNickName;
    private String beneficiaryAddressLine1;
    private String beneficiaryCity;
    private String beneficiaryZipcode;
    private String beneficiarycountry;
    private String intermediaryBicCode;
    private String clearingCode;
    private String beneficiaryBankName;
    private String beneficiaryAddressLine2;
    private String beneficiaryPhone;
    private String beneficiaryEmail;
    private String beneficiaryState;
    private String paymentmethod;
    private String paymentStatus;
    @JsonAlias({"errorMessage", "errmsg"})
    private String dbpErrMsg;

    public DomesticFundTransferDTO() {
    }

    public DomesticFundTransferDTO(String transactionId, String referenceId, String message, String confirmationNumber, String status, String requestId, String featureActionId, String transactionType, String companyId, String roleId, String transactionCurrency, String fromAccountCurrency, String frequencyTypeId, String frequencyType, String fromAccountNumber, String toAccountNumber, double amount, String notes, String transactionsNotes, String transactionts, String frequencyEndDate, String numberOfRecurrences, String scheduledDate, String createdby, String modifiedby, String createdts, String lastmodifiedts, String synctimestamp, boolean softdeleteflag, String processingDate, String personId, String fromNickName, String fromAccountType, String day1, String day2, String toAccountType, String payPersonName, String securityQuestion, String SecurityAnswer, String checkImageBack, String payeeName, String profileId, String cardNumber, String cardExpiry, String isScheduled, String frequencyStartDate, String deliverBy, String payeName, String iban, String swiftCode, String bicCode, String bankName, String bankId, String feeCurrency, String beneficiaryName, String paidBy, String paymentType, String feeAmount, String beneficiaryAddressNickName, String beneficiaryAddressLine1, String beneficiaryCity, String beneficiaryZipcode, String beneficiarycountry, String dbpErrCode, String dbpErrMsg, String overrides, String charges, String validate, String totalAmount, String exchangeRate, String serviceCharge, String convertedAmount, String transactionAmount, String overrideList, String creditValueDate, String intermediaryBicCode, String errorDetails, String messageDetails, String clearingCode, String beneficiaryBankName, String beneficiaryAddressLine2, String beneficiaryPhone, String beneficiaryEmail, String beneficiaryState, String paymentmethod, String paymentStatus) {
        this.iban = iban;
        this.bicCode = bicCode;
        this.bankName = bankName;
        this.bankId = bankId;
        this.feeCurrency = feeCurrency;
        this.beneficiaryName = beneficiaryName;
        this.paymentType = paymentType;
        this.feeAmount = feeAmount;
        this.beneficiaryAddressNickName = beneficiaryAddressNickName;
        this.beneficiaryAddressLine1 = beneficiaryAddressLine1;
        this.beneficiaryCity = beneficiaryCity;
        this.beneficiaryZipcode = beneficiaryZipcode;
        this.beneficiarycountry = beneficiarycountry;
        this.intermediaryBicCode = intermediaryBicCode;
        this.clearingCode = clearingCode;
        this.beneficiaryBankName = beneficiaryBankName;
        this.beneficiaryAddressLine2 = beneficiaryAddressLine2;
        this.beneficiaryPhone = beneficiaryPhone;
        this.beneficiaryEmail = beneficiaryEmail;
        this.beneficiaryState = beneficiaryState;
    }

    public com.kony.sbg.dto.DomesticFundTransferDTO updateValues(com.kony.sbg.dto.DomesticFundTransferDTO dto) {
        super.updateValues(dto);
        dto.iban = this.getIban();
        dto.bicCode = this.getBicCode();
        dto.bankName = this.getBankName();
        dto.bankId = this.getBankId();
        dto.feeCurrency = this.getFeeCurrency();
        dto.beneficiaryName = this.getBeneficiaryName();
        dto.paymentType = this.getPaymentType();
        dto.feeAmount = this.getFeeAmount();
        dto.beneficiaryAddressNickName = this.getBeneficiaryAddressNickName();
        dto.beneficiaryAddressLine1 = this.getBeneficiaryAddressLine1();
        dto.beneficiaryCity = this.getBeneficiaryCity();
        dto.beneficiaryZipcode = this.getBeneficiaryZipcode();
        dto.beneficiarycountry = this.getBeneficiarycountry();
        dto.intermediaryBicCode = this.getIntermediaryBicCode();
        dto.clearingCode = this.getClearingCode();
        dto.beneficiaryBankName = this.getBeneficiaryBankName();
        dto.beneficiaryAddressLine2 = this.getBeneficiaryAddressLine2();
        dto.beneficiaryPhone = this.getBeneficiaryPhone();
        dto.beneficiaryEmail = this.getBeneficiaryEmail();
        dto.beneficiaryPhone = this.getBeneficiaryPhone();
        dto.beneficiaryState = this.getBeneficiaryState();
        dto.paymentStatus = this.getPaymentStatus();
        return dto;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getpaymentmethod() {
        return this.paymentmethod;
    }

    public void setpaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBicCode() {
        return this.bicCode;
    }

    public void setBicCode(String bicCode) {
        this.bicCode = bicCode;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankId() {
        return this.bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getFeeCurrency() {
        return this.feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public String getBeneficiaryName() {
        return this.beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getFeeAmount() {
        return this.feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getBeneficiaryAddressNickName() {
        return this.beneficiaryAddressNickName;
    }

    public void setBeneficiaryAddressNickName(String beneficiaryAddressNickName) {
        this.beneficiaryAddressNickName = beneficiaryAddressNickName;
    }

    public String getBeneficiaryAddressLine1() {
        return this.beneficiaryAddressLine1;
    }

    public void setBeneficiaryAddressLine1(String beneficiaryAddressLine1) {
        this.beneficiaryAddressLine1 = beneficiaryAddressLine1;
    }

    public String getBeneficiaryCity() {
        return this.beneficiaryCity;
    }

    public void setBeneficiaryCity(String beneficiaryCity) {
        this.beneficiaryCity = beneficiaryCity;
    }

    public String getBeneficiaryZipcode() {
        return this.beneficiaryZipcode;
    }

    public void setBeneficiaryZipcode(String beneficiaryZipcode) {
        this.beneficiaryZipcode = beneficiaryZipcode;
    }

    public String getBeneficiarycountry() {
        return this.beneficiarycountry;
    }

    public void setBeneficiarycountry(String beneficiarycountry) {
        this.beneficiarycountry = beneficiarycountry;
    }

    public String getIntermediaryBicCode() {
        return this.intermediaryBicCode;
    }

    public void setIntermediaryBicCode(String intermediaryBicCode) {
        this.intermediaryBicCode = intermediaryBicCode;
    }

    public String getClearingCode() {
        return this.clearingCode;
    }

    public void setClearingCode(String clearingCode) {
        this.clearingCode = clearingCode;
    }

    public String getBeneficiaryBankName() {
        return this.beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public String getBeneficiaryAddressLine2() {
        return this.beneficiaryAddressLine2;
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

    public String getBeneficiaryState() {
        return this.beneficiaryState;
    }

    public void setBeneficiaryState(String beneficiaryState) {
        this.beneficiaryState = beneficiaryState;
    }

    public int hashCode() {
        super.hashCode();
        int prime = 1;
        int result = 1;
        result = 31 * result + (this.SecurityAnswer == null ? 0 : this.SecurityAnswer.hashCode());
        long temp = Double.doubleToLongBits(this.amount);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + (this.iban == null ? 0 : this.iban.hashCode());
        result = 31 * result + (this.bicCode == null ? 0 : this.bicCode.hashCode());
        result = 31 * result + (this.bankName == null ? 0 : this.bankName.hashCode());
        result = 31 * result + (this.bankId == null ? 0 : this.bankId.hashCode());
        result = 31 * result + (this.feeCurrency == null ? 0 : this.feeCurrency.hashCode());
        result = 31 * result + (this.beneficiaryName == null ? 0 : this.beneficiaryName.hashCode());
        result = 31 * result + (this.paymentType == null ? 0 : this.paymentType.hashCode());
        result = 31 * result + (this.feeAmount == null ? 0 : this.feeAmount.hashCode());
        result = 31 * result + (this.beneficiaryAddressNickName == null ? 0 : this.beneficiaryAddressNickName.hashCode());
        result = 31 * result + (this.beneficiaryAddressLine1 == null ? 0 : this.beneficiaryAddressLine1.hashCode());
        result = 31 * result + (this.beneficiaryCity == null ? 0 : this.beneficiaryCity.hashCode());
        result = 31 * result + (this.beneficiaryZipcode == null ? 0 : this.beneficiaryZipcode.hashCode());
        result = 31 * result + (this.beneficiarycountry == null ? 0 : this.beneficiarycountry.hashCode());
        result = 31 * result + (this.intermediaryBicCode == null ? 0 : this.intermediaryBicCode.hashCode());
        result = 31 * result + (this.clearingCode == null ? 0 : this.clearingCode.hashCode());
        result = 31 * result + (this.beneficiaryBankName == null ? 0 : this.beneficiaryBankName.hashCode());
        result = 31 * result + (this.beneficiaryAddressLine2 == null ? 0 : this.beneficiaryAddressLine2.hashCode());
        result = 31 * result + (this.beneficiaryPhone == null ? 0 : this.beneficiaryPhone.hashCode());
        result = 31 * result + (this.beneficiaryState == null ? 0 : this.beneficiaryState.hashCode());
        result = 31 * result + (this.beneficiaryEmail == null ? 0 : this.beneficiaryEmail.hashCode());
        result = 31 * result + (this.paymentStatus == null ? 0 : this.paymentStatus.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DomesticFundTransferDTO{" +
                "iban='" + iban + '\'' +
                ", bicCode='" + bicCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankId='" + bankId + '\'' +
                ", feeCurrency='" + feeCurrency + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", feeAmount='" + feeAmount + '\'' +
                ", beneficiaryAddressNickName='" + beneficiaryAddressNickName + '\'' +
                ", beneficiaryAddressLine1='" + beneficiaryAddressLine1 + '\'' +
                ", beneficiaryCity='" + beneficiaryCity + '\'' +
                ", beneficiaryZipcode='" + beneficiaryZipcode + '\'' +
                ", beneficiarycountry='" + beneficiarycountry + '\'' +
                ", intermediaryBicCode='" + intermediaryBicCode + '\'' +
                ", clearingCode='" + clearingCode + '\'' +
                ", beneficiaryBankName='" + beneficiaryBankName + '\'' +
                ", beneficiaryAddressLine2='" + beneficiaryAddressLine2 + '\'' +
                ", beneficiaryPhone='" + beneficiaryPhone + '\'' +
                ", beneficiaryEmail='" + beneficiaryEmail + '\'' +
                ", beneficiaryState='" + beneficiaryState + '\'' +
                ", paymentmethod='" + paymentmethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", dbpErrMsg='" + dbpErrMsg + '\'' +
                '}';
    }
}
