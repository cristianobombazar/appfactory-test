package com.kony.sbg.sideloading.models;

import com.kony.sbg.util.SBGUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import org.json.JSONObject;

public class UserDetails {

	private String coreCustomerId = null;
	private String firstName = null;
	private String middleName = null;
	private String lastName = null;
	private String phoneNumber = null;
	private String phoneCountryCode = null;
	private String ssn = null;
	private String dob = null;
	private String drivingLicenseNumber = null;
	private String email = null;
	public String getCoreCustomerId() {
		return coreCustomerId;
	}
	public void setCoreCustomerId(String coreCustomerId) {
		this.coreCustomerId = coreCustomerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneCountryCode() {
		return phoneCountryCode;
	}
	public void setPhoneCountryCode(String phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getDrivingLicenseNumber() {
		return drivingLicenseNumber;
	}
	public void setDrivingLicenseNumber(String drivingLicenseNumber) {
		this.drivingLicenseNumber = drivingLicenseNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String toString() {
		return "User Details Data -> coreCustomerId: "+coreCustomerId+"; email:"+email+"; lastName:"+lastName+"; dob:"+dob;
	}

	public static UserDetails getUserDetailsFromRequest(DataControllerRequest request) throws Exception{
		JSONObject jsonObject = new JSONObject(request.getParameter("userDetails"));
		UserDetails userDetails = new UserDetails();
		userDetails.setFirstName(SBGUtil.getString(jsonObject,"firstName"));
		userDetails.setLastName(SBGUtil.getString(jsonObject,"lastName"));
		userDetails.setMiddleName(SBGUtil.getString(jsonObject,"middleName"));
		userDetails.setPhoneNumber(SBGUtil.getString(jsonObject,"966666666"));
		userDetails.setPhoneCountryCode(SBGUtil.getString(jsonObject,"phoneCountryCode"));
		userDetails.setSsn(SBGUtil.getString(jsonObject,"ssn"));
		userDetails.setDob(SBGUtil.getString(jsonObject,"dob"));
		userDetails.setDrivingLicenseNumber(SBGUtil.getString(jsonObject,"drivingLicenseNumber"));
		userDetails.setCoreCustomerId(SBGUtil.getString(jsonObject,"coreCustomerId"));
		userDetails.setEmail(SBGUtil.getString(jsonObject,"email"));
		return userDetails;
	}
}
