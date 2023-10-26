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
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.AddressBackendDelegateImpl;
import com.temenos.dbx.party.utils.PartyPropertyConstants;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.Country;
import com.temenos.dbx.product.dto.CustomerAddressViewDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyAddress;
import com.temenos.dbx.product.dto.Region;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.HTTPOperations;
import com.temenos.dbx.usermanagement.backenddelegate.impl.PartyAddressBackendDelegateImpl;

public class PartyAddressBackendDelegateImplExtn extends PartyAddressBackendDelegateImpl {
	@Override
	public List<CustomerAddressViewDTO> getCustomerAddress(String customerId, Map<String, Object> headerMap)
			throws ApplicationException {

		LoggerUtil logger = new LoggerUtil(PartyCommunicationBackendDelegateImplExtn.class);

		DBXResult dbxResult = new DBXResult();

		List<CustomerAddressViewDTO> dtoList = new ArrayList<>();

		String partyID = null;
		if (StringUtils.isBlank(customerId)) {
			return dtoList;
		}

		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setId(customerId);
		customerDTO = (CustomerDTO) customerDTO.loadDTO();

		if (customerDTO != null) {

			if (HelperMethods.getBusinessUserTypes().contains(customerDTO.getCustomerType_id())
					|| customerDTO.isCombinedUser()) {
				return new AddressBackendDelegateImpl().getCustomerAddress(customerId, headerMap);
			}

			BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
			backendIdentifierDTO.setCustomer_id(customerId);
			backendIdentifierDTO.setBackendType(DTOConstants.PARTY);

			try {
				dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
						.get(backendIdentifierDTO, headerMap);
			} catch (ApplicationException e1) {
				// TODO Auto-generated catch block
				logger.error("Error while fetching backend identifier for customer ID " + customerId);
			}

		} else {
			partyID = customerId;
		}
		try {
			if (dbxResult.getResponse() != null) {
				BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
				partyID = identifierDTO.getBackendId();
			}
			String partyURL = URLFinder.getServerRuntimeProperty(URLConstants.PARTY_HOST_URL)
					+ PartyURLFinder.getServiceUrl(PartyPropertyConstants.PARTY_ADDRESS, partyID);

			PartyUtils.addJWTAuthHeader(headerMap, AuthConstants.PRE_LOGIN_FLOW);
			DBXResult response = HTTPOperations.sendHttpRequest(HTTPOperations.operations.GET, partyURL, null,
					headerMap);

			JsonObject jsonObject = new JsonParser().parse((String) response.getResponse()).getAsJsonObject();

			if (jsonObject.has(DTOConstants.PARTY_ADDRESS)
					&& !jsonObject.get(DTOConstants.PARTY_ADDRESS).isJsonNull()) {
				List<PartyAddress> contactAddresses = PartyAddress
						.loadFromJsonArray(jsonObject.get(DTOConstants.PARTY_ADDRESS).getAsJsonArray());

				for (PartyAddress contactAddress : contactAddresses) {
					if (contactAddress.getCommunicationNature() != null
							&& contactAddress.getCommunicationNature().equals(DTOConstants.PHYSICAL)) {

						CustomerAddressViewDTO addressViewDTO = new CustomerAddressViewDTO();
						addressViewDTO.setAddressId(contactAddress.getAddressesReference());
						logger.debug("Party Address Type:" + contactAddress.getAddressType());
						if (PartyMappingsExtn.getcustomerAddressTypeMapping()
								.containsKey(contactAddress.getAddressType())) {
							logger.debug("Party Address Type Match Found:" + PartyMappingsExtn
									.getcustomerAddressTypeMapping().get(contactAddress.getAddressType()));
							addressViewDTO.setAddressType(PartyMappingsExtn.getcustomerAddressTypeMapping()
									.get(contactAddress.getAddressType()));
						} else {
							logger.debug("Party Address Type Match Not Found:" + contactAddress.getAddressType());
							addressViewDTO.setAddressType(contactAddress.getAddressType());
						}
						addressViewDTO.setAddressLine1(contactAddress.getBuildingName());
						addressViewDTO.setAddressLine2(contactAddress.getStreetName());
						addressViewDTO.setZipCode(contactAddress.getPostalOrZipCode());
						addressViewDTO.setRegionId(contactAddress.getRegionCode());
						addressViewDTO.setRegionCode(contactAddress.getRegionCode());
						addressViewDTO.setCountryId(contactAddress.getCountryCode());
						addressViewDTO.setCityId(contactAddress.getTown());

						if (StringUtils.isNotBlank(addressViewDTO.getRegionId())) {
							Region region = (Region) new Region().loadDTO(addressViewDTO.getRegionId());
							if (region != null) {
								addressViewDTO.setRegionName(region.getName());
								if (StringUtils.isBlank(addressViewDTO.getCountryId())) {
									addressViewDTO.setCountryId(region.getCountry_id());
								}
							} else {
								addressViewDTO.setRegionName(contactAddress.getCountrySubdivision());
							}
						}

						if (StringUtils.isNotBlank(addressViewDTO.getCountryId())) {
							Country country = (Country) new Country().loadDTO(addressViewDTO.getCountryId());
							if (country != null) {
								addressViewDTO.setCountryCode(country.getCode());
								addressViewDTO.setCountryName(country.getName());
							} else {
								addressViewDTO.setCountryCode(addressViewDTO.getCountryId());
								addressViewDTO.setCountryName(addressViewDTO.getCountryId());
							}
						}

//                        addressViewDTO.setCityName(getCityName(contactAddress.getTown(),headerMap));
						addressViewDTO.setCityName(contactAddress.getTown());

						if (Boolean.parseBoolean(contactAddress.getPrimary())) {
							addressViewDTO.setIsPrimary("true");
						} else {
							addressViewDTO.setIsPrimary("false");
						}

						dtoList.add(addressViewDTO);
					}
				}

			}
		} catch (Exception e) {
			logger.error("Caught exception while getting Party Address Details: ", e);
			return dtoList;
		}

		return dtoList;
	}

}
