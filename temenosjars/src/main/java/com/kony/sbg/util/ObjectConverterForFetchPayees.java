package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.sbg.backend.impl.PayeeBackendDelegateImpl;
import com.kony.sbg.dto.SbgExternalPayeeBackendDTO;

public class ObjectConverterForFetchPayees {
	private	static Logger logger = LogManager.getLogger(PayeeBackendDelegateImpl.class);

	public static	Map<String, Object> convert2Map(SbgExternalPayeeBackendDTO payeeBackendDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();
		/*id,userId,bankId,nickName,firstName,
		lastName,routingNumber,accountNumber,accountType,notes,
		countryName,swiftCode,user_Account,beneficiaryName,
		isInternationalAccount,bankName,isSameBankAccount,softDelete,
		isVerified,createdOn,externalaccount,iban,sortCode,
		phoneCountryCode,phoneNumber,phoneExtension,addressNickName,
		addressLine1,city,zipcode,country,externalaccountcol,cif,
		isBusinessPayee,companyId,dbpErrCode,dbpErrMsg,payeeId,noOfCustomersLinked,
		addressLine2,email,phone*/
		retval.put("id", payeeBackendDTO.getId());
		retval.put("userId", payeeBackendDTO.getUserId());
		retval.put("bankId", payeeBackendDTO.getBankName());
		retval.put("nickName", payeeBackendDTO.getNickName());
		retval.put("firstName", payeeBackendDTO.getFirstName());
		retval.put("lastName", payeeBackendDTO.getLastName());
		retval.put("routingNumber", payeeBackendDTO.getRoutingNumber());
		retval.put("accountNumber", payeeBackendDTO.getAccountNumber());
		retval.put("accountType", payeeBackendDTO.getAccountType());
		retval.put("notes", payeeBackendDTO.getNotes());
		retval.put("countryName", payeeBackendDTO.getCountryName());
		retval.put("swiftCode", payeeBackendDTO.getSwiftCode());
		retval.put("user_Account", payeeBackendDTO.getUser_Account());
		retval.put("beneficiaryName", payeeBackendDTO.getBeneficiaryName());
		retval.put("isInternationalAccount", payeeBackendDTO.getIsInternationalAccount());
		retval.put("bankName", payeeBackendDTO.getBankName());
		retval.put("isSameBankAccount", payeeBackendDTO.getIsSameBankAccount());
		retval.put("softDelete", payeeBackendDTO.getSoftDelete());
		retval.put("isVerified", payeeBackendDTO.getIsVerified());		
		retval.put("createdOn", payeeBackendDTO.getCreatedOn());	
		retval.put("externalaccount", payeeBackendDTO.getExternalaccount());
		retval.put("iban", payeeBackendDTO.getIban());
		retval.put("sortCode", payeeBackendDTO.getSortCode());
		retval.put("phoneCountryCode", payeeBackendDTO.getPhoneCountryCode());
		retval.put("phoneNumber", payeeBackendDTO.getPhoneNumber());
		retval.put("phoneExtension", payeeBackendDTO.getPhoneExtension());
		retval.put("addressNickName", payeeBackendDTO.getAddressNickName());		
		retval.put("addressLine1", payeeBackendDTO.getAddressLine1());
		retval.put("city", payeeBackendDTO.getCity());
		retval.put("zipcode", payeeBackendDTO.getZipcode());
		retval.put("country", payeeBackendDTO.getCountry());
		retval.put("externalaccountcol", payeeBackendDTO.getExternalaccountcol());
		retval.put("cif", payeeBackendDTO.getCif());		
		retval.put("isBusinessPayee", payeeBackendDTO.getIsBusinessPayee());
		retval.put("companyId", payeeBackendDTO.getCompanyId());
		retval.put("dbpErrCode", payeeBackendDTO.getDbpErrCode());
		retval.put("dbpErrMsg", payeeBackendDTO.getDbpErrMsg());
		retval.put("payeeId", payeeBackendDTO.getPayeeId());
		retval.put("noOfCustomersLinked", payeeBackendDTO.getNoOfCustomersLinked());		
		retval.put("addressLine2", payeeBackendDTO.getAddressLine2());
		retval.put("email", payeeBackendDTO.getEmail());
		retval.put("entityType", payeeBackendDTO.getEntityType());
		retval.put("beneCode", payeeBackendDTO.getBeneCode());
		retval.put("phone", payeeBackendDTO.getPhone());
		retval.put("state", payeeBackendDTO.getState());
		retval.put("residencyStatus",payeeBackendDTO.getResidencyStatus());
		retval.put("beneLimit", payeeBackendDTO.getBeneLimit());
		retval.put("paymentCurrency", payeeBackendDTO.getPaymentCurrency());
		retval.put("beneficiaryReference", payeeBackendDTO.getBeneficiaryReference());
		retval.put("statementReference", payeeBackendDTO.getStatementReference());
		retval.put("proofOfPayment", payeeBackendDTO.getProofOfPayment());
		retval.put("cellphone", payeeBackendDTO.getCellphone());
		retval.put("emailAddress", payeeBackendDTO.getEmailAddress());
		
		logger.debug("ObjectConverter.convert2Map ---> beneCode:"+payeeBackendDTO.getBeneCode()+"; entityType:"+payeeBackendDTO.getEntityType());
		
		return retval;
	}
	
	public static	SbgExternalPayeeBackendDTO convert2DTO(Map<String, Object> map) {
		
		SbgExternalPayeeBackendDTO dto = new SbgExternalPayeeBackendDTO();
		
		dto.setNotes((String)map.get("id")); 
		dto.setNotes((String)map.get("userId")); 
		dto.setNotes((String)map.get("bankId")); 
		dto.setNotes((String)map.get("nickName")); 
		dto.setNotes((String)map.get("firstName")); 
		dto.setNotes((String)map.get("lastName")); 
		dto.setNotes((String)map.get("routingNumber")); 
		dto.setNotes((String)map.get("accountNumber")); 
		dto.setNotes((String)map.get("accountType")); 
		dto.setNotes((String)map.get("notes")); 
		dto.setNotes((String)map.get("countryName")); 
		dto.setNotes((String)map.get("swiftCode")); 
		dto.setNotes((String)map.get("user_Account")); 
		dto.setNotes((String)map.get("beneficiaryName")); 
		dto.setNotes((String)map.get("isInternationalAccount")); 
		dto.setNotes((String)map.get("bankName")); 
		dto.setNotes((String)map.get("isSameBankAccount")); 
		dto.setNotes((String)map.get("softDelete")); 
		dto.setNotes((String)map.get("isVerified")); 	
		dto.setNotes((String)map.get("createdOn")); 	
		dto.setNotes((String)map.get("externalaccount")); 
		dto.setNotes((String)map.get("iban")); 
		dto.setNotes((String)map.get("sortCode")); 
		dto.setNotes((String)map.get("phoneCountryCode")); 
		dto.setNotes((String)map.get("phoneNumber")); 
		dto.setNotes((String)map.get("phoneExtension")); 
		dto.setNotes((String)map.get("addressNickName")); 		
		dto.setNotes((String)map.get("addressLine1")); 
		dto.setNotes((String)map.get("city")); 
		dto.setNotes((String)map.get("zipcode")); 
		dto.setNotes((String)map.get("country")); 
		dto.setNotes((String)map.get("externalaccountcol")); 
		dto.setNotes((String)map.get("cif")); 		
		dto.setNotes((String)map.get("isBusinessPayee")); 
		dto.setNotes((String)map.get("companyId")); 
		dto.setNotes((String)map.get("dbpErrCode")); 
		dto.setNotes((String)map.get("dbpErrMsg")); 
		dto.setNotes((String)map.get("payeeId")); 
		dto.setNotes((String)map.get("noOfCustomersLinked")); 		
		dto.setNotes((String)map.get("addressLine2")); 
		dto.setNotes((String)map.get("email")); 
		dto.setNotes((String)map.get("entityType")); 
		dto.setNotes((String)map.get("beneCode")); 
		dto.setNotes((String)map.get("phone"));
		dto.setNotes((String)map.get("state"));
		dto.setNotes((String)map.get("residencyStatus"));
		dto.setNotes((String)map.get("beneLimit"));
		dto.setNotes((String)map.get("paymentCurrency"));
		dto.setNotes((String)map.get("beneficiaryReference"));
		dto.setNotes((String)map.get("statementReference"));
		dto.setNotes((String)map.get("proofOfPayment"));
		dto.setNotes((String)map.get("cellphone"));
		dto.setNotes((String)map.get("emailAddress"));

		logger.debug("ObjectConverter.convert2DTO ---> beneCode:"+dto.getBeneCode()+"; entityType:"+dto.getEntityType());
		return dto;
	}

}
