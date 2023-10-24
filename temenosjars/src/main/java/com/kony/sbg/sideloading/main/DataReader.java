package com.kony.sbg.sideloading.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.kony.sbg.sideloading.models.Accounts;
import com.kony.sbg.sideloading.models.Contract;
import com.kony.sbg.sideloading.models.Customer;
import com.kony.sbg.sideloading.models.DesignatedContact;
import com.kony.sbg.sideloading.models.UserContracts;
import com.kony.sbg.sideloading.models.UserDetails;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.sideloading.utils.Console;

public class DataReader {

	public	static Map<String, Contract> loadContractData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		Map<String, Contract> contractMap = new HashMap<String, Contract>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);
						
						String contractName = data[0];
						String coreCustID	= data[1];
						
						if(!CommonUtils.isStringEmpty(contractName)) 	{	contractName = contractName.trim(); }
						if(!CommonUtils.isStringEmpty(coreCustID)) 		{	coreCustID = coreCustID.trim(); 	}
						
						if(CommonUtils.isStringEmpty(contractName) || CommonUtils.isStringEmpty(coreCustID)) {
							throw new Exception("INVALID DATA EXISTS IN CONTRACT CSV FILE AT LINE# "+lineNumber);
						}
						
						Contract obj = new Contract();
						obj.setContractName(contractName);
						obj.setCoreCustId(coreCustID);
						
						Console.print("Contract data: "+obj);
						contractMap.put(contractName, obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return contractMap;
	}

	public	static Map<String, Customer> loadCustomerData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		Map<String, Customer> map = new HashMap<String, Customer>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);
						
						String coreCustomerId = data[0];
						
						if(!CommonUtils.isStringEmpty(coreCustomerId)) 	{	coreCustomerId = coreCustomerId.trim(); }
						
						if(CommonUtils.isStringEmpty(coreCustomerId)) {
							throw new Exception("INVALID DATA EXISTS IN CUSTOMER CSV FILE AT LINE# "+lineNumber);
						}

						Customer obj = new Customer();
						obj.setCoreCustomerId(coreCustomerId);
						obj.setRegistrationNo(data[1]);
						obj.setTradingName(data[2]);
						obj.setOsdID(data[3]);
						obj.setCustomerRegisteredName(data[4]);
						obj.setCountryCode(data[5]);
						obj.setBaseCurrency(data[6]);
						obj.setEntityType(data[7]);
						obj.setResidencyStatus(data[8]);
						obj.setInstitutionalSector(data[9]);
						obj.setIndustrialClassification(data[10]);
						obj.setVATNo(data[11]);
						obj.setTaxID(data[12]);
						obj.setIsPrimary(data[13]);
						obj.setCoreCustomerName(data[14]);
						obj.setIsBusiness(data[15]);
						obj.setLegaladdressLine1(data[16]);
						obj.setLegaladdressLine2(data[17]);
						obj.setLegaladdressLine3(data[18]);
						obj.setLegalcityName(data[19]);
						obj.setLegalstate(data[20]);
						obj.setLegalcountry(data[21]);
						obj.setLegalzipCode(data[22]);
						obj.setLegalPhoneCountryCode(data[23]);
						obj.setLegalPhoneNumber(data[24]);
						obj.setLegalEmail(data[25]);
						obj.setPostalAddressLine1(data[26]);
						obj.setPostalAddressLine2(data[27]);
						obj.setPostalAddressLine3(data[28]);
						obj.setPostalCityName(data[29]);
						obj.setPostalState(data[30]);
						obj.setPostalCountry(data[31]);
						obj.setPostalZipCode(data[32]);
						obj.setContactFirstName(data[33]);
						obj.setContactLastName(data[34]);
						obj.setContactPhoneCountryCode(data[35]);
						obj.setContactPhoneNumber(data[36]);
						obj.setContactEmail(data[37]);
						
						Console.print("Customer data: "+obj);
						map.put(coreCustomerId, obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return map;
	}

	public	static List<Accounts> loadAccountsData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		List<Accounts> map = new ArrayList<Accounts>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);
						
						String coreCustId = data[0];
						String accountId = data[1];
						if(!CommonUtils.isStringEmpty(coreCustId)) 	{	coreCustId = coreCustId.trim(); }
						if(!CommonUtils.isStringEmpty(accountId)) 	{	accountId = accountId.trim(); 	}
						
						if(CommonUtils.isStringEmpty(coreCustId) || CommonUtils.isStringEmpty(accountId)) {
							throw new Exception("INVALID DATA EXISTS IN ACCOUNTS CSV FILE AT LINE# "+lineNumber);
						}
						
						Accounts obj = new Accounts();
						obj.setCoreCustomerId(coreCustId);
						obj.setAccountId(accountId);
						obj.setAccountType(data[2]);
						obj.setAccountName(data[3]);
						obj.setProductName(data[4]);
						obj.setAccountHolderName(data[5]);
						obj.setAccountStatus(data[6]);
						obj.setArrangementId(data[7]);
						obj.setOsdID(data[8]);
						obj.setCurrency(data[9]);
						obj.setBranchName(data[10]);
						obj.setBranchCode(data[11]);
						obj.setSwiftCode(data[12]);
						obj.setCoreBanking(data[13]);
						obj.setAccountStyleCode(data[14]);
						obj.setAccountResidencyStatus(data[15]);
						obj.setAccountGroupCode(data[16]);

						Console.print("Accounts data: "+obj);
						map.add(obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return map;
	}

	public	static List<UserDetails> loadUserDetailsData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		List<UserDetails> map = new ArrayList<UserDetails>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);
						
						String coreCustID	= data[0];
						String emailId		= data[9];
						if(!CommonUtils.isStringEmpty(coreCustID)) 	{	coreCustID = coreCustID.trim(); }
						if(!CommonUtils.isStringEmpty(emailId)) 		{	emailId = emailId.trim(); 		}
						
						if(CommonUtils.isStringEmpty(coreCustID) || CommonUtils.isStringEmpty(emailId)) {
							throw new Exception("INVALID DATA EXISTS IN USER DETAILS CSV FILE AT LINE# "+lineNumber);
						}
						
						UserDetails obj = new UserDetails();
						obj.setCoreCustomerId(coreCustID);
						obj.setFirstName(data[1]);
						obj.setMiddleName(data[2]);
						obj.setLastName(data[3]);
						obj.setPhoneNumber(data[4]);
						obj.setPhoneCountryCode(data[5]);
						obj.setSsn(data[6]);
						obj.setDob(data[7]);
						obj.setDrivingLicenseNumber(data[8]);
						obj.setEmail(emailId);
						
						Console.print("User Details data: "+obj);
						map.add(obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return map;
	}

	public	static List<UserContracts> loadUserContractsData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		List<UserContracts> map = new ArrayList<UserContracts>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);

						String coreCustID	= data[0];
						String emailId		= data[6];
						if(!CommonUtils.isStringEmpty(coreCustID)) 	{	coreCustID = coreCustID.trim(); }
						if(!CommonUtils.isStringEmpty(emailId)) 	{	emailId = emailId.trim(); 		}
						
						if(CommonUtils.isStringEmpty(coreCustID) || CommonUtils.isStringEmpty(emailId)) {
							throw new Exception("INVALID DATA EXISTS IN USER CONTRACT CSV FILE AT LINE# "+lineNumber);
						}

						UserContracts obj = new UserContracts();
						obj.setCoreCustomerId(coreCustID);
						obj.setCompanyName(data[1]);
						obj.setContractName(data[2]);
						obj.setIsPrimary(data[3]);
						obj.setRoleId(data[4]);
						obj.setAccounts(data[5]);
						obj.setEmail(emailId);
				
						Console.print("User Contract data: "+obj);
						map.add(obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return map;
	}

	public	static List<DesignatedContact> loadDesignatedContactData(String filePath) throws Exception {
		int index = 0;
		int lineNumber = 0;
		Scanner scanner = null;
		List<DesignatedContact> map = new ArrayList<DesignatedContact>();
		
		try {
			scanner = new Scanner(new File(filePath));
			scanner.useDelimiter(LoadSmeData.DELIMITER);
			
			while(scanner.hasNextLine()) {
				++lineNumber;
				if(index == 0) {
					scanner.nextLine();
				} else {
					String line = scanner.nextLine();
					if(line != null && line.trim().length() > 0) {
						String data[] = line.split(LoadSmeData.DELIMITER);

						String coreCustID	= data[0];
						String emailId		= data[7];
						String indicator	= data[10];
						if(!CommonUtils.isStringEmpty(coreCustID)) 	{	coreCustID = coreCustID.trim(); }
						if(!CommonUtils.isStringEmpty(emailId)) 		{	emailId = emailId.trim(); 		}
						if(!CommonUtils.isStringEmpty(indicator)) 	{	indicator = indicator.trim(); 	}
						
						if(CommonUtils.isStringEmpty(coreCustID) || 
							CommonUtils.isStringEmpty(emailId) || 
							CommonUtils.isStringEmpty(indicator)) 
						{
							throw new Exception("INVALID DATA EXISTS IN DESIGNATED PERSON CSV FILE AT LINE# "+lineNumber);
						}

						DesignatedContact obj = new DesignatedContact();
						obj.setCoreCustomerId(coreCustID);
						obj.setDesignatedFirstName(data[1]);
						obj.setDesignatedLastName(data[2]);
						obj.setDesignatedDateOfBirth(data[3]);
						obj.setDesignatedSsn(data[4]);
						obj.setDesignatedPhoneCountryCode(data[5]);
						obj.setDesignatedPhoneNumber(data[6]);
						obj.setDesignatedEmail(emailId);
						obj.setDesignatedMobileCountryCode(data[8]);
						obj.setDesignatedMobileNumber(data[9]);
						obj.setDesignatedIndicator(indicator);
				
						Console.print("Designated Contact data: "+obj);
						map.add(obj);
					}
				}	
				++index;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(scanner != null) 
				scanner.close();
		}
		
		return map;
	}
}

