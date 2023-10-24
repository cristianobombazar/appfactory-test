package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;

import com.kony.sbg.dto.DomesticFundTransferRefDataDTO;
import com.kony.sbg.dto.InterBankFundTransferRefDataDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.sbg.backend.impl.PayeeBackendDelegateImpl;
import com.kony.sbg.dto.InternationalFundTransferRefDataDTO;
import com.kony.sbg.dto.OwnAccountFundTransferRefDataDTO;
import com.kony.sbg.dto.SbgInternationalPayeeBackendDTO;

public class ObjectConverter {
	
	private	static Logger logger = LogManager.getLogger(PayeeBackendDelegateImpl.class);

	public static	Map<String, Object> convert2Map(SbgInternationalPayeeBackendDTO payeeBackendDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();
		
		retval.put("notes", payeeBackendDTO.getNotes());
		retval.put("isVerified", payeeBackendDTO.getIsVerified());
		retval.put("nickName", payeeBackendDTO.getNickName());
		retval.put("accountType", payeeBackendDTO.getAccountType());
		retval.put("swiftCode", payeeBackendDTO.getSwiftCode());
		retval.put("bankName", payeeBackendDTO.getBankName());
		retval.put("accountNumber", payeeBackendDTO.getAccountNumber());
		retval.put("sortCode", payeeBackendDTO.getSortCode());
		retval.put("routingNumber", payeeBackendDTO.getRoutingNumber());
		retval.put("isInternationalAccount", payeeBackendDTO.getIsInternationalAccount());
		retval.put("beneficiaryName", payeeBackendDTO.getBeneficiaryName());
		retval.put("IBAN", payeeBackendDTO.getIban());
		retval.put("isSameBankAccount", payeeBackendDTO.getIsSameBankAccount());
		retval.put("countryName", payeeBackendDTO.getCountryName());
		retval.put("phoneCountryCode", payeeBackendDTO.getPhoneCountryCode());
		retval.put("phoneNumber", payeeBackendDTO.getPhoneNumber());
		retval.put("phoneExtension", payeeBackendDTO.getPhoneExtension());
		retval.put("isBusinessPayee", payeeBackendDTO.getIsBusinessPayee());
		retval.put("firstName", payeeBackendDTO.getFirstName());
		retval.put("lastName", payeeBackendDTO.getLastName());
		retval.put("addressLine1", payeeBackendDTO.getAddressLine1());
		retval.put("city", payeeBackendDTO.getCity());
		retval.put("zipcode", payeeBackendDTO.getZipcode());
		retval.put("country", payeeBackendDTO.getCountry());
		retval.put("cif", payeeBackendDTO.getCif());
		retval.put("addressLine2", payeeBackendDTO.getAddressLine2());
		retval.put("email", payeeBackendDTO.getEmail());
		retval.put("entityType", payeeBackendDTO.getEntityType());
		retval.put("beneCode", payeeBackendDTO.getBeneCode());
		retval.put("state", payeeBackendDTO.getState());
		retval.put("residencyStatus",payeeBackendDTO.getResidencyStatus());
		
		logger.debug("ObjectConverter.convert2Map ---> beneCode:"+payeeBackendDTO.getBeneCode()+"; entityType:"+payeeBackendDTO.getEntityType());
		logger.debug("ObjectConverter.convert2Map ---> state:"+payeeBackendDTO.getState());
		return retval;
	}
	
	public static	SbgInternationalPayeeBackendDTO convert2DTO(Map<String, Object> map) {
		
		SbgInternationalPayeeBackendDTO dto = new SbgInternationalPayeeBackendDTO();
		
		dto.setNotes((String)map.get("notes"));
		dto.setIsVerified((String)map.get("isVerified"));
		dto.setNickName((String)map.get("nickName"));
		dto.setAccountType((String)map.get("accountType"));
		dto.setSwiftCode((String)map.get("swiftCode"));
		dto.setBankName((String)map.get("bankName"));
		dto.setAccountNumber((String)map.get("accountNumber"));
		dto.setSortCode((String)map.get("sortCode"));
		dto.setRoutingNumber((String)map.get("routingNumber"));
		dto.setIsInternationalAccount((String)map.get("isInternationalAccount"));
		dto.setBeneficiaryName((String)map.get("beneficiaryName"));
		dto.setIban((String)map.get("IBAN"));
		dto.setIsSameBankAccount((String)map.get("isSameBankAccount"));
		dto.setCountryName((String)map.get("countryName"));
		dto.setPhoneCountryCode((String)map.get("phoneCountryCode"));
		dto.setPhoneNumber((String)map.get("phoneNumber"));
		dto.setPhoneExtension((String)map.get("phoneExtension"));
		dto.setIsBusinessPayee((String)map.get("isBusinessPayee"));
		dto.setFirstName((String)map.get("firstName"));
		dto.setLastName((String)map.get("lastName"));
		dto.setAddressLine1((String)map.get("addressLine1"));
		dto.setCity((String)map.get("city"));
		dto.setZipcode((String)map.get("zipcode"));
		dto.setCountry((String)map.get("country"));
		dto.setBankName((String)map.get("bankName"));
		dto.setCif((String)map.get("cif"));
		dto.setAddressLine2((String)map.get("addressLine2"));
		dto.setEmail((String)map.get("email"));
		dto.setEntityType((String)map.get("entityType"));
		dto.setBeneCode((String)map.get("beneCode"));
		dto.setState((String)map.get("state"));
		dto.setResidencyStatus((String)map.get("residencyStatus"));
		logger.debug("ObjectConverter.convert2Map ---> state:"+dto.getState());
		logger.debug("ObjectConverter.convert2DTO ---> beneCode:"+dto.getBeneCode()+"; entityType:"+dto.getEntityType());
		logger.debug("ObjectConverter.convert2DTO ---> residencyStatus:"+dto.getResidencyStatus());
		return dto;
	}
	
	public static Map<String, Object> convertTransferObject2Map(InternationalFundTransferRefDataDTO payeeBackendDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();

		retval.put("referenceId", payeeBackendDTO.getReferenceId());
		retval.put("purposecode", payeeBackendDTO.getPurposecode());
		retval.put("compliancecode", payeeBackendDTO.getCompliancecode());
		retval.put("statementrefno", payeeBackendDTO.getStatementrefno());
		retval.put("benerefeno", payeeBackendDTO.getBenerefeno());
		retval.put("rfqDetails", payeeBackendDTO.getRfqDetails());
		retval.put("beneficiaryState", payeeBackendDTO.getBeneficiaryState());
		retval.put("bopDetails", payeeBackendDTO.getBopDetails());
		retval.put("clearingCode", payeeBackendDTO.getClearingCode());
		retval.put("beneficiaryAddressLine2", payeeBackendDTO.getBeneficiaryAddressLine2());
		retval.put("confirmationNumber", payeeBackendDTO.getConfirmationNumber());
		retval.put("exconApproval", payeeBackendDTO.getExconApproval());
		retval.put("beneficiaryCategory", payeeBackendDTO.getBeneficiaryCategory());
		retval.put("beneCode", payeeBackendDTO.getBeneCode());
		retval.put("fromAccountName", payeeBackendDTO.getFromAccountName());
		logger.debug("ObjectConverter::convertTransferObject2Map:ReferenceID: " + payeeBackendDTO.getReferenceId());
		return retval;
	}

	public static Map<String, Object> convertTransferObject2Map(DomesticFundTransferRefDataDTO payeeBackendDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();

		retval.put("referenceId", payeeBackendDTO.getReferenceId());
		retval.put("purposecode", payeeBackendDTO.getPurposecode());
		retval.put("compliancecode", payeeBackendDTO.getCompliancecode());
		retval.put("statementrefno", payeeBackendDTO.getStatementrefno());
		retval.put("benerefeno", payeeBackendDTO.getBenerefeno());
		retval.put("rfqDetails", payeeBackendDTO.getRfqDetails());
		retval.put("beneficiaryState", payeeBackendDTO.getBeneficiaryState());
		retval.put("bopDetails", payeeBackendDTO.getBopDetails());
		retval.put("clearingCode", payeeBackendDTO.getClearingCode());
		retval.put("beneficiaryAddressLine2", payeeBackendDTO.getBeneficiaryAddressLine2());
		retval.put("confirmationNumber", payeeBackendDTO.getConfirmationNumber());
		logger.debug("ObjectConverter::convertTransferObject2Map:ReferenceID: " + payeeBackendDTO.getReferenceId());
		return retval;
	}
	
	public static	InternationalFundTransferRefDataDTO convertTransferObject2DTO(Map<String, Object> map) {
		
		InternationalFundTransferRefDataDTO dto = new InternationalFundTransferRefDataDTO();
		
		dto.setReferenceId((String)map.get("referenceId"));
		dto.setPurposecode((String)map.get("purposecode"));
		dto.setCompliancecode((String)map.get("compliancecode"));
		dto.setStatementrefno((String)map.get("statementrefno"));
		dto.setBenerefeno((String)map.get("benerefeno"));
		dto.setRfqDetails((String)map.get("rfqDetails"));
		logger.error("ObjectConverter.convert2Map ---> payeeBackendDTO:"+dto.getReferenceId());
		
		return dto;
	}

	public static Map<String, Object> convertTransferObject2Map(InterBankFundTransferRefDataDTO payeeBackendDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();

		retval.put("referenceId", payeeBackendDTO.getReferenceId());
		retval.put("purposecode", payeeBackendDTO.getPurposecode());
		retval.put("compliancecode", payeeBackendDTO.getCompliancecode());
		retval.put("statementrefno", payeeBackendDTO.getStatementrefno());
		retval.put("benerefeno", payeeBackendDTO.getBenerefeno());
		retval.put("rfqDetails", payeeBackendDTO.getRfqDetails());
		retval.put("beneficiaryState", payeeBackendDTO.getBeneficiaryState());
		retval.put("bopDetails", payeeBackendDTO.getBopDetails());
		retval.put("clearingCode", payeeBackendDTO.getClearingCode());
		retval.put("beneficiaryAddressLine2", payeeBackendDTO.getBeneficiaryAddressLine2());
		retval.put("confirmationNumber", payeeBackendDTO.getConfirmationNumber());
		retval.put("beneficiaryPhone", payeeBackendDTO.getBeneficiaryPhone());
		retval.put("beneficiaryEmail", payeeBackendDTO.getBeneficiaryEmail());
		retval.put("proofOfPayment", payeeBackendDTO.getProofOfPayment());
		retval.put("beneficiaryCategory", payeeBackendDTO.getBeneficiaryCategory());
		retval.put("beneCode", payeeBackendDTO.getBeneCode());
		retval.put("fromAccountName", payeeBackendDTO.getFromAccountName());

		logger.debug("ObjectConverter::convertTransferObject2Map:ReferenceID: " + payeeBackendDTO.getReferenceId());
		return retval;
	}

	public static Map<String, Object> convertTransferObject2Map(OwnAccountFundTransferRefDataDTO refDataDTO) {
		Map<String, Object> retval = new HashMap<String, Object>();

		retval.put("referenceId", refDataDTO.getReferenceId());
		retval.put("purposecode", refDataDTO.getPurposecode());
		retval.put("compliancecode", refDataDTO.getCompliancecode());
		retval.put("statementrefno", refDataDTO.getStatementrefno());
		retval.put("benerefeno", refDataDTO.getBenerefeno());
		retval.put("rfqDetails", refDataDTO.getRfqDetails());
		retval.put("beneficiaryState", refDataDTO.getBeneficiaryState());
		retval.put("bopDetails", refDataDTO.getBopDetails());
		retval.put("clearingCode", refDataDTO.getClearingCode());
		retval.put("beneficiaryAddressLine2", refDataDTO.getBeneficiaryAddressLine2());
		retval.put("confirmationNumber", refDataDTO.getConfirmationNumber());
		retval.put("paymentType", refDataDTO.getPaymentType());
		retval.put("exconApproval", refDataDTO.getExconApproval());
		retval.put("fromAccountName", refDataDTO.getFromAccountName());
		logger.debug("ObjectConverter::convertTransferObject2Map:ReferenceID: " + refDataDTO.getReferenceId());
		return retval;
	}
}
