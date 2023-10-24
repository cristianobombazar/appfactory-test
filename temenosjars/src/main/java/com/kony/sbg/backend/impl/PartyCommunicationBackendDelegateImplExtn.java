package com.kony.sbg.backend.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.util.PartyMappingsExtn;
import com.temenos.dbx.party.utils.PartyMappings;
import com.temenos.dbx.party.utils.PartyPropertyConstants;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyAddress;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.impl.CommunicationBackendDelegateImpl;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.HTTPOperations;
import com.temenos.dbx.usermanagement.backenddelegate.impl.PartyCommunicationBackendDelegateImpl;

public class PartyCommunicationBackendDelegateImplExtn extends PartyCommunicationBackendDelegateImpl {
	@Override
	public DBXResult get(CustomerCommunicationDTO dto, Map<String, Object> headerMap) {
		LoggerUtil logger = new LoggerUtil(PartyCommunicationBackendDelegateImplExtn.class);

		DBXResult dbxResult = new DBXResult();
		if (StringUtils.isBlank(dto.getCustomer_id())) {
			return dbxResult;
		}

		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setId(dto.getCustomer_id());
		customerDTO = (CustomerDTO) customerDTO.loadDTO();

		String partyID = null;
		if (customerDTO != null) {

			if (HelperMethods.getBusinessUserTypes().contains(customerDTO.getCustomerType_id())
					|| customerDTO.isCombinedUser()) {
				return new CommunicationBackendDelegateImpl().get(dto, headerMap);
			}

			BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
			backendIdentifierDTO.setCustomer_id(dto.getCustomer_id());
			backendIdentifierDTO.setBackendType(DTOConstants.PARTY);

			try {
				dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
						.get(backendIdentifierDTO, headerMap);
			} catch (ApplicationException e1) {
				// TODO Auto-generated catch block
				logger.error("Error while fetching backend identifier for customer ID " + dto.getCustomer_id());
			}
		} else {
			partyID = dto.getCustomer_id();
		}

		try {
			if (dbxResult.getResponse() != null) {
				BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
				partyID = identifierDTO.getBackendId();
				dbxResult = new DBXResult();
			}
			String partyURL = URLFinder.getServerRuntimeProperty(URLConstants.PARTY_HOST_URL)
					+ PartyURLFinder.getServiceUrl(PartyPropertyConstants.PARTY_COMMUNICATION, partyID);

			PartyUtils.addJWTAuthHeader(headerMap, AuthConstants.PRE_LOGIN_FLOW);
			DBXResult response = HTTPOperations.sendHttpRequest(HTTPOperations.operations.GET, partyURL, null,
					headerMap);

			JsonObject jsonObject = new JsonParser().parse((String) response.getResponse()).getAsJsonObject();
			if (jsonObject.has(DTOConstants.PARTY_ADDRESS)
					&& !jsonObject.get(DTOConstants.PARTY_ADDRESS).isJsonNull()) {
				List<PartyAddress> contactPoints = PartyAddress
						.loadFromJsonArray(jsonObject.get(DTOConstants.PARTY_ADDRESS).getAsJsonArray());
				List<CustomerCommunicationDTO> communicationDTOs = new ArrayList<CustomerCommunicationDTO>();
				for (PartyAddress contactPoint : contactPoints) {
					CustomerCommunicationDTO communicationDTO = new CustomerCommunicationDTO();
					if (contactPoint.getCommunicationNature().equals(DTOConstants.PHONE)) {
						communicationDTO.setType_id(HelperMethods.getCommunicationTypes().get(DTOConstants.PHONE));
						communicationDTO.setValue(contactPoint.getPhoneNo());
						communicationDTO.setPhoneCountryCode(contactPoint.getIddPrefixPhone());
						if (contactPoint.getPrimary().contentEquals("true")) {
							communicationDTO.setIsPrimary(true);
						} else {
							communicationDTO.setIsPrimary(false);
						}
						logger.debug("Party Phone Type:" + contactPoint.getAddressType());
						if (PartyMappings.getCustomerPhoneTypeMapping().containsKey(contactPoint.getAddressType())) {
							logger.debug("Party Phone Type Match Found:"
									+ PartyMappings.getCustomerPhoneTypeMapping().get(contactPoint.getAddressType()));
							communicationDTO.setExtension(
									PartyMappings.getCustomerPhoneTypeMapping().get(contactPoint.getAddressType()));
						} else {
							logger.debug("Party Phone Type Match Not Found:" + contactPoint.getAddressType());
							communicationDTO.setExtension(contactPoint.getAddressType());
						}
						communicationDTO.setId(contactPoint.getAddressesReference());
						communicationDTOs.add(communicationDTO);
					} else if (contactPoint.getCommunicationNature().equals(DTOConstants.ELECTRONIC)
							&& contactPoint.getCommunicationType().equals(DTOConstants.EMAIL)) {
						communicationDTO.setType_id(HelperMethods.getCommunicationTypes().get(DTOConstants.EMAIL));
						communicationDTO.setValue(contactPoint.getElectronicAddress());
						// added
						logger.debug("Party Email Type:" + contactPoint.getAddressType());
						if (PartyMappingsExtn.getCustomerEmailTypeMapping()
								.containsKey(contactPoint.getAddressType())) {
							logger.debug("Party Email Type Match Found:" + PartyMappingsExtn
									.getCustomerEmailTypeMapping().get(contactPoint.getAddressType()));
							communicationDTO.setExtension(
									PartyMappingsExtn.getCustomerEmailTypeMapping().get(contactPoint.getAddressType()));
						} else {
							logger.debug("Party Email Type Match Not Found:" + contactPoint.getAddressType());
							communicationDTO.setExtension(contactPoint.getAddressType());
						}

						if (contactPoint.getPrimary().contentEquals("true")) {
							communicationDTO.setIsPrimary(true);
						} else {
							communicationDTO.setIsPrimary(false);
						}

						communicationDTO.setId(contactPoint.getAddressesReference());
						communicationDTOs.add(communicationDTO);
					}
				}
				dbxResult.setResponse(communicationDTOs);
			}

		} catch (Exception e) {
			logger.error("Caught exception while getting Party Comunication Details: ", e);
			return dbxResult;
		}

		return dbxResult;
	}

}
