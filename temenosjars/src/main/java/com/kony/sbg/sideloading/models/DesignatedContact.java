package com.kony.sbg.sideloading.models;

public class DesignatedContact {

	private	String coreCustomerId				= null;
	private	String designatedFirstName			= null;
	private	String designatedLastName			= null;
	private	String designatedDateOfBirth		= null;
	private	String designatedSsn				= null;
	private	String designatedPhoneCountryCode	= null;
	private	String designatedPhoneNumber		= null;
	private	String designatedEmail				= null;
	private	String designatedMobileCountryCode	= null;
	private	String designatedMobileNumber		= null;
	private	String designatedIndicator			= null;
	public String getCoreCustomerId() {
		return coreCustomerId;
	}
	public void setCoreCustomerId(String coreCustomerId) {
		this.coreCustomerId = coreCustomerId;
	}
	public String getDesignatedFirstName() {
		return designatedFirstName;
	}
	public void setDesignatedFirstName(String designatedFirstName) {
		this.designatedFirstName = designatedFirstName;
	}
	public String getDesignatedLastName() {
		return designatedLastName;
	}
	public void setDesignatedLastName(String designatedLastName) {
		this.designatedLastName = designatedLastName;
	}
	public String getDesignatedDateOfBirth() {
		return designatedDateOfBirth;
	}
	public void setDesignatedDateOfBirth(String designatedDateOfBirth) {
		this.designatedDateOfBirth = designatedDateOfBirth;
	}
	public String getDesignatedSsn() {
		return designatedSsn;
	}
	public void setDesignatedSsn(String designatedSsn) {
		this.designatedSsn = designatedSsn;
	}
	public String getDesignatedPhoneCountryCode() {
		return designatedPhoneCountryCode;
	}
	public void setDesignatedPhoneCountryCode(String designatedPhoneCountryCode) {
		this.designatedPhoneCountryCode = designatedPhoneCountryCode;
	}
	public String getDesignatedPhoneNumber() {
		return designatedPhoneNumber;
	}
	public void setDesignatedPhoneNumber(String designatedPhoneNumber) {
		this.designatedPhoneNumber = designatedPhoneNumber;
	}
	public String getDesignatedEmail() {
		return designatedEmail;
	}
	public void setDesignatedEmail(String designatedEmail) {
		this.designatedEmail = designatedEmail;
	}
	public String getDesignatedMobileCountryCode() {
		return designatedMobileCountryCode;
	}
	public void setDesignatedMobileCountryCode(String designatedMobileCountryCode) {
		this.designatedMobileCountryCode = designatedMobileCountryCode;
	}
	public String getDesignatedMobileNumber() {
		return designatedMobileNumber;
	}
	public void setDesignatedMobileNumber(String designatedMobileNumber) {
		this.designatedMobileNumber = designatedMobileNumber;
	}
	public String getDesignatedIndicator() {
		return designatedIndicator;
	}
	public void setDesignatedIndicator(String designatedIndicator) {
		this.designatedIndicator = designatedIndicator;
	}
	
	public String toString() {
		return "Designated Contact Data -> coreCustomerId"+coreCustomerId;
	}
	
}
