package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;

public class PartyMappingsExtn {

	private static Map<String, String> partyPhoneAdressTypes = null;

	public static Map<String, String> getPartyPhoneAddressTypeMapping() {

		if (partyPhoneAdressTypes == null) {
			partyPhoneAdressTypes = new HashMap<String, String>();
			partyPhoneAdressTypes.put("Work", "Office");
			partyPhoneAdressTypes.put("Home", "Home");
			partyPhoneAdressTypes.put("Mobile", "Residence");
			// changed
			partyPhoneAdressTypes.put("Other", "Other");
			// changed
			partyPhoneAdressTypes.put("Personal", "Residence");
		}

		return partyPhoneAdressTypes;
	}

	private static Map<String, String> customerPhoneTypeMapping = null;

	// added
	public static Map<String, String> getCustomerEmailTypeMapping() {

		if (customerPhoneTypeMapping == null) {
			customerPhoneTypeMapping = new HashMap<String, String>();
			customerPhoneTypeMapping.put("Residence", "Personal");
			customerPhoneTypeMapping.put("Office", "Work");
			customerPhoneTypeMapping.put("Home", "Home");
			customerPhoneTypeMapping.put("Other", "Other");
		}

		return customerPhoneTypeMapping;
	}

	private static Map<String, String> partycontactAddressTypeMapping = null;
	
	public static Map<String, String> getPartycontactAddressTypeMapping() {

		if (partycontactAddressTypeMapping == null) {
			partycontactAddressTypeMapping = new HashMap<String, String>();
			partycontactAddressTypeMapping.put("ADR_TYPE_CURRENT", "Residence");
			partycontactAddressTypeMapping.put("ADR_TYPE_HOME", "Home");
			partycontactAddressTypeMapping.put("ADR_TYPE_MAILING", "Residence");
			partycontactAddressTypeMapping.put("ADR_TYPE_WORK", "Office");
			//added
			partycontactAddressTypeMapping.put("ADR_TYPE_OTHER", "Other");
			partycontactAddressTypeMapping.put("Mobile", "Residence");
		}

		return partycontactAddressTypeMapping;
	}

	private static Map<String, String> customerAddressTypeMapping = null;

	public static Map<String, String> getcustomerAddressTypeMapping() {

		if (customerAddressTypeMapping == null) {
			customerAddressTypeMapping = new HashMap<String, String>();
			customerAddressTypeMapping.put("Home", "ADR_TYPE_HOME");
			customerAddressTypeMapping.put("Office", "ADR_TYPE_WORK");
			customerAddressTypeMapping.put("Residence", "ADR_TYPE_CURRENT");
			//added
			customerAddressTypeMapping.put("Other", "ADR_TYPE_OTHER");
		}

		return customerAddressTypeMapping;
	}
}
