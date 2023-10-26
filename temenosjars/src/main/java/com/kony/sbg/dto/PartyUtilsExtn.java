package com.kony.sbg.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.sbg.util.PartyMappingsExtn;
import com.temenos.dbx.party.utils.PartyConstants;
import com.temenos.dbx.party.utils.PartyMappings;
import com.temenos.dbx.product.dto.AddressDTO;
import com.temenos.dbx.product.dto.CustomerAddressDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerEmploymentDetailsDTO;
import com.temenos.dbx.product.dto.Employment;
import com.temenos.dbx.product.dto.PartyAddress;
import com.temenos.dbx.product.dto.PartyDTO;
import com.temenos.dbx.product.dto.PartyIdentifier;
import com.temenos.dbx.product.dto.TaxDetails;
import com.temenos.dbx.product.utils.DTOConstants;

public class PartyUtilsExtn {

	private static LoggerUtil logger = new LoggerUtil(PartyUtilsExtn.class);

	public static PartyDTO buildPartyDTO(CustomerDTO customerDTO, PartyDTO initialPartyDTO) {

		PartyDTO finalPartyDTO = new PartyDTO();

		if (StringUtils.isNotBlank(customerDTO.getDateOfBirth()))
			finalPartyDTO.setDateOfBirth(customerDTO.getDateOfBirth());
		else
			finalPartyDTO.setDateOfBirth(initialPartyDTO.getDateOfBirth());

		if (StringUtils.isNotBlank(customerDTO.getCustomerType_id())
				&& customerDTO.getCustomerType_id().equals(HelperMethods.getCustomerTypes().get("Prospect"))) {
			if (StringUtils.isBlank(initialPartyDTO.getPartyStatus())
					|| !"Prospect".equalsIgnoreCase(initialPartyDTO.getPartyStatus())) {
				finalPartyDTO.setPartyStatus("Prospect");
			}
		} else {
			if (StringUtils.isBlank(initialPartyDTO.getPartyStatus())
					|| !"Active".equalsIgnoreCase(initialPartyDTO.getPartyStatus())) {
				finalPartyDTO.setPartyStatus("Active");
			}
		}

		finalPartyDTO.setPartyType("Individual");

		if (StringUtils.isNotBlank(customerDTO.getFirstName())) {
			finalPartyDTO.setFirstName(customerDTO.getFirstName());
			if (!Objects.equals(initialPartyDTO.getFirstName(), finalPartyDTO.getFirstName())) {
				finalPartyDTO.setNameStartDate(HelperMethods.getCurrentDate());
			} else {
				finalPartyDTO.setNameStartDate(initialPartyDTO.getNameStartDate());
			}
		} else {
			finalPartyDTO.setFirstName(initialPartyDTO.getFirstName());
			if (!Objects.equals(initialPartyDTO.getFirstName(), finalPartyDTO.getFirstName())) {
				finalPartyDTO.setNameStartDate(HelperMethods.getCurrentDate());
			} else {
				finalPartyDTO.setNameStartDate(initialPartyDTO.getNameStartDate());
			}
		}

		if (StringUtils.isNotBlank(customerDTO.getLastName())) {
			finalPartyDTO.setLastName(customerDTO.getLastName());
			if (!Objects.equals(initialPartyDTO.getLastName(), finalPartyDTO.getLastName())) {
				finalPartyDTO.setNameStartDate(HelperMethods.getCurrentDate());
			}else {
				finalPartyDTO.setNameStartDate(initialPartyDTO.getNameStartDate());
			}
		} else {
			finalPartyDTO.setLastName(initialPartyDTO.getLastName());
			if (!Objects.equals(initialPartyDTO.getLastName(), finalPartyDTO.getLastName())) {
				finalPartyDTO.setNameStartDate(HelperMethods.getCurrentDate());
			}else {
				finalPartyDTO.setNameStartDate(initialPartyDTO.getNameStartDate());
			}
		}

		if (StringUtils.isNotBlank(customerDTO.getGender()) && (StringUtils.isBlank(initialPartyDTO.getGender())
				|| !customerDTO.getGender().equalsIgnoreCase(initialPartyDTO.getGender()))) {
			finalPartyDTO.setGender(StringUtils.capitalize(customerDTO.getGender().toLowerCase()));
		}

		if (customerDTO.getCustomerCommuncation() != null) {

			List<PartyAddress> contactPoints = initialPartyDTO.getPartyAddress();
			PartyAddress contactPoint;
			for (CustomerCommunicationDTO communicationDTO : customerDTO.getCustomerCommuncation()) {
				contactPoint = new PartyAddress();
				if (communicationDTO.getType_id()
						.equals(HelperMethods.getCommunicationTypes().get(DTOConstants.EMAIL))) {
					boolean isNew = true;
					boolean isChanged = false;
					if (contactPoints != null) {
						String emailInputType;
						for (int i = 0; i < contactPoints.size(); i++) {
							isChanged = false;
							contactPoint = contactPoints.get(i);
							if ((contactPoint.getCommunicationNature().equals(DTOConstants.ELECTRONIC)
									&& contactPoint.getCommunicationType().equals(DTOConstants.EMAIL))
									&& (StringUtils.isNotBlank(communicationDTO.getId())
											&& StringUtils.isNotBlank(contactPoint.getAddressesReference())
											&& communicationDTO.getId().equals(contactPoint.getAddressesReference()))) {
								if ("false".equalsIgnoreCase(contactPoint.getPrimary())
										&& communicationDTO.getIsPrimary()) {
									contactPoint.setPrimary(communicationDTO.getIsPrimary().toString());
									isChanged = true;
								}

								emailInputType = StringUtils.isNotBlank(communicationDTO.getExtension())
										&& PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
												.containsKey(communicationDTO.getExtension())
														? PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
																.get(communicationDTO.getExtension())
														: communicationDTO.getExtension();

								logger.debug("Email Types Input:" + emailInputType + "->input->"
										+ communicationDTO.getExtension() + "->party->"
										+ contactPoint.getAddressType());
								if (!emailInputType.equalsIgnoreCase(contactPoint.getAddressType())) {
									contactPoint.setAddressType(emailInputType);
									isChanged = true;
								}

								if (!communicationDTO.getValue()
										.equalsIgnoreCase(contactPoint.getElectronicAddress())) {
									contactPoint.setElectronicAddress(communicationDTO.getValue());
									isChanged = true;
								}
								if (isChanged) {
									finalPartyDTO.setPartyAddress(contactPoint);
								}
								isNew = false;
								break;

							}
						}
					}

					if (isNew && communicationDTO.getIsNew()) {
						contactPoint = new PartyAddress();
						if (StringUtils.isNotBlank(communicationDTO.getValue())) {
							contactPoint.setCommunicationNature(DTOConstants.ELECTRONIC);
							contactPoint.setCommunicationType(DTOConstants.EMAIL);
							contactPoint.setElectronicAddress(communicationDTO.getValue());
						}
						if (PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
								.containsKey(communicationDTO.getExtension())) {
							logger.debug("Email Types Input for NEw:"
									+ PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
											.get(communicationDTO.getExtension())
									+ "->input->" + communicationDTO.getExtension() + "->party->"
									+ contactPoint.getAddressType());
							contactPoint.setAddressType(PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
									.get(communicationDTO.getExtension()));
						} else {
							logger.debug("Email Types Input for NEw no match:" + communicationDTO.getExtension()
									+ "->party->" + contactPoint.getAddressType());
							contactPoint.setAddressType(communicationDTO.getExtension());
						}
						contactPoint.setPrimary(String.valueOf(communicationDTO.getIsPrimary()));
						finalPartyDTO.setPartyAddress(contactPoint);
					}

				} else if (communicationDTO.getType_id()
						.equals(HelperMethods.getCommunicationTypes().get(DTOConstants.PHONE))) {
					boolean isNew = true;
					boolean isChanged = false;
					String phone = communicationDTO.getValue();
					String extenstion = communicationDTO.getPhoneCountryCode();
					if (phone.contains("-")) {
						extenstion = phone.substring(0, phone.indexOf('-'));
						phone = phone.substring(extenstion.length() + 1);
					}

					if (contactPoints != null) {
						String phoneInputType;
						for (int i = 0; i < contactPoints.size(); i++) {
							contactPoint = contactPoints.get(i);
							isChanged = false;
							if (contactPoint.getCommunicationNature().equals(DTOConstants.PHONE)
									&& (StringUtils.isNotBlank(communicationDTO.getId())
											&& StringUtils.isNotBlank(contactPoint.getAddressesReference())
											&& communicationDTO.getId().equals(contactPoint.getAddressesReference()))) {
								if ("false".equalsIgnoreCase(contactPoint.getPrimary())
										&& communicationDTO.getIsPrimary()) {
									contactPoint.setPrimary(communicationDTO.getIsPrimary().toString());
									isChanged = true;
								}
								phoneInputType = StringUtils.isNotBlank(communicationDTO.getExtension())
										? PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
												.containsKey(communicationDTO.getExtension())
														? PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
																.get(communicationDTO.getExtension())
														: communicationDTO.getExtension()
										: "";
								logger.debug("Phone Types Input:" + phoneInputType + "->input->"
										+ communicationDTO.getExtension() + "->party->"
										+ contactPoint.getAddressType());
								if (!phoneInputType.equalsIgnoreCase(contactPoint.getAddressType())) {
									isChanged = true;
									contactPoint.setAddressType(phoneInputType);
								}
								if (!extenstion.equalsIgnoreCase(contactPoint.getIddPrefixPhone())) {
									isChanged = true;
									contactPoint.setIddPrefixPhone(extenstion);
								}
								if (!phone.equalsIgnoreCase(contactPoint.getPhoneNo())) {
									isChanged = true;
									contactPoint.setPhoneNo(phone);
								}
								if (isChanged) {
									finalPartyDTO.setPartyAddress(contactPoint);
								}
								isNew = false;
								break;

							}
						}
					}

					if (isNew && communicationDTO.getIsNew()) {
						contactPoint = new PartyAddress();
						if (StringUtils.isNotBlank(phone)) {
							contactPoint.setCommunicationNature(DTOConstants.PHONE);
							contactPoint.setCommunicationType(PartyConstants.MOBILE);
							if (PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
									.containsKey(communicationDTO.getExtension())) {
								logger.debug("Phone Types Input for New:"
										+ PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
												.get(communicationDTO.getExtension())
										+ "->input->" + communicationDTO.getExtension() + "->party->"
										+ contactPoint.getAddressType());

								contactPoint.setAddressType(PartyMappingsExtn.getPartyPhoneAddressTypeMapping()
										.get(communicationDTO.getExtension()));
							} else {
								logger.debug("Phone Types Input for New No Match:" + "->input->"
										+ communicationDTO.getExtension() + "->party->"
										+ contactPoint.getAddressType());
								contactPoint.setAddressType(communicationDTO.getExtension());
							}
							contactPoint.setPhoneNo(phone);
							contactPoint.setPrimary(String.valueOf(communicationDTO.getIsPrimary()));
							contactPoint.setIddPrefixPhone(extenstion);
							finalPartyDTO.setPartyAddress(contactPoint);
						}
					}
				}
			}
		}

		if (customerDTO.getEmploymentDetails() != null) {

			for (CustomerEmploymentDetailsDTO customerEmploymentDetailsDTO : customerDTO.getEmploymentDetails()) {

				List<Employment> employments = initialPartyDTO.getEmployments();
				Employment employment = new Employment();

				boolean isNew = true;
				String employmentType = null;
				if (StringUtils.isNotBlank(customerEmploymentDetailsDTO.getEmploymentType())) {
					if ("SID_EMPLOYED".equals(customerEmploymentDetailsDTO.getEmploymentType())) {
						employmentType = "Salaried-Employee";
					} else if ("SID_STUDENT".equals(customerEmploymentDetailsDTO.getEmploymentType())) {
						employmentType = "Student";
					} else if ("SID_RETIRED".equals(customerEmploymentDetailsDTO.getEmploymentType())) {
						employmentType = "Retired";
					} else if ("SID_UNEMPLOYED".equals(customerEmploymentDetailsDTO.getEmploymentType())) {
						employmentType = "Individual";
					} else {
						employmentType = "Self-Employed";
					}
				}

				if (employments != null) {
					for (int i = 0; i < employments.size(); i++) {
						Employment emp = employments.get(i);
						if (StringUtils.isNotBlank(customerEmploymentDetailsDTO.getCreatedts())) {
							if (emp.getStartDate().equals(customerEmploymentDetailsDTO.getCreatedts())) {
								employment = employments.get(i);
								if (StringUtils.isBlank(employment.getType())
										|| (StringUtils.isNotBlank(employment.getType())
												&& !employment.getType().equals(employmentType))) {
									employment.setType(employmentType);
									finalPartyDTO.setEmployment(employment);
								}
								isNew = false;
								break;
							}
						} else {
							isNew = false;
						}
					}
				}

				if (isNew) {
					employment = new Employment();
					employment.setType(employmentType);
					employment.setStartDate(customerEmploymentDetailsDTO.getCreatedts());
					finalPartyDTO.setEmployment(employment);
				}
			}
		}

		if (customerDTO.getCustomerAddress() != null) {

			for (CustomerAddressDTO customerAddressDTO : customerDTO.getCustomerAddress()) {
				logger.debug("**CustomerAddressDetails:" + customerAddressDTO.getAddress_id() + ","
						+ customerAddressDTO.getIsNew() + "-" + customerAddressDTO.getIsChanged() + ","
						+ customerAddressDTO.getIsPrimary());
				logger.debug("**AddressDetails:" + customerAddressDTO.getAddressDTO().getId() + ","
						+ customerAddressDTO.getAddressDTO().getIsNew() + "-"
						+ customerAddressDTO.getAddressDTO().getIsChanged() + ","
						+ customerAddressDTO.getAddressDTO().getIsPreferredAddress());

				List<PartyAddress> contactAddresss = initialPartyDTO.getPartyAddress();
				PartyAddress contactAddress = new PartyAddress();
				boolean isNew = true;
				boolean isChanged = false;
				logger.debug("***type details:" + customerAddressDTO.getType_id()
						+ customerAddressDTO.getAddressDTO().getType());
				if (contactAddresss != null) {
					for (int i = 0; i < contactAddresss.size(); i++) {
						isChanged = false;
						if (contactAddresss.get(i).getCommunicationNature().equals(DTOConstants.PHYSICAL)
								&& StringUtils.isNotBlank(customerAddressDTO.getAddress_id())
								&& StringUtils.isNotBlank(contactAddresss.get(i).getAddressesReference())
								&& customerAddressDTO.getAddress_id()
										.equals(contactAddresss.get(i).getAddressesReference())) {
							contactAddress = contactAddresss.get(i);
							isNew = false;
							break;
						}
					}
				}
				logger.debug("**isNew" + isNew);
				logger.debug("customerAddressDTO.getIsNew()" + customerAddressDTO.getIsNew());

				if (!isNew) {
					AddressDTO addressDTO = customerAddressDTO.getAddressDTO();

					if (StringUtils.isBlank(contactAddress.getBuildingName())
							|| (StringUtils.isNotBlank(contactAddress.getBuildingName())
									&& StringUtils.isNotBlank(addressDTO.getAddressLine1())
									&& !addressDTO.getAddressLine1().equals(contactAddress.getBuildingName()))) {
						isChanged = true;
						contactAddress.setBuildingName(addressDTO.getAddressLine1());
					}
					logger.debug("getAddressLine2 getStreetName  " + addressDTO.getAddressLine2() + "   "
							+ contactAddress.getStreetName());
					String addressLine2 = StringUtils.isNotBlank(addressDTO.getAddressLine2())
							? addressDTO.getAddressLine2()
							: "";
					String streetName = StringUtils.isNotBlank(contactAddress.getStreetName())
							? contactAddress.getStreetName()
							: "";
					if (!Objects.equals(addressLine2, streetName)) {
						// if
						// (!Objects.equals(addressDTO.getAddressLine2(),contactAddress.getStreetName()))
						// {
						isChanged = true;
						contactAddress.setStreetName(addressDTO.getAddressLine2());
					}

					if (StringUtils.isBlank(contactAddress.getTown())
							|| (StringUtils.isNotBlank(contactAddress.getTown())
									&& StringUtils.isNotBlank(addressDTO.getCity_id())
									&& !addressDTO.getCity_id().equals(contactAddress.getTown()))) {
						isChanged = true;
						contactAddress.setTown(addressDTO.getCity_id());
					}

					if (StringUtils.isBlank(contactAddress.getTown())
							|| (StringUtils.isNotBlank(contactAddress.getTown())
									&& StringUtils.isNotBlank(addressDTO.getCityName())
									&& !addressDTO.getCityName().equals(contactAddress.getTown()))) {
						isChanged = true;
						contactAddress.setTown(addressDTO.getCityName());
					}

					if (StringUtils.isBlank(contactAddress.getPostalOrZipCode())
							|| (StringUtils.isNotBlank(contactAddress.getPostalOrZipCode())
									&& StringUtils.isNotBlank(addressDTO.getZipCode())
									&& !addressDTO.getZipCode().equals(contactAddress.getPostalOrZipCode()))) {
						isChanged = true;
						contactAddress.setPostalOrZipCode(addressDTO.getZipCode());
					}

					String regionId = addressDTO.getRegion_id();
					if (StringUtils.isNotBlank(regionId) && (StringUtils.isBlank(contactAddress.getRegionCode())
							|| !regionId.equalsIgnoreCase(contactAddress.getRegionCode()))) {
						isChanged = true;
						contactAddress.setRegionCode(regionId);
						if (regionId.contains("-")) {
							String[] parts = addressDTO.getRegion_id().split("-");
							contactAddress.setCountryCode(parts[0]);
							contactAddress.setCountrySubdivision(parts[1]);
						} else {
							if (StringUtils.isBlank(contactAddress.getCountrySubdivision())
									|| (StringUtils.isNotBlank(contactAddress.getCountrySubdivision())
											&& StringUtils.isNotBlank(addressDTO.getState())
											&& !addressDTO.getState().equals(contactAddress.getCountrySubdivision()))) {
								isChanged = true;
								contactAddress.setCountrySubdivision(addressDTO.getState());
							}
							if (StringUtils.isBlank(contactAddress.getCountrySubdivision()) || (StringUtils
									.isNotBlank(contactAddress.getCountrySubdivision())
									&& StringUtils.isNotBlank(addressDTO.getRegion_id())
									&& !addressDTO.getRegion_id().equals(contactAddress.getCountrySubdivision()))) {
								isChanged = true;
								contactAddress.setCountrySubdivision(addressDTO.getRegion_id());
							}
							if (StringUtils.isBlank(contactAddress.getCountryCode())
									|| !contactAddress.getCountryCode().equals(addressDTO.getCountry())) {
								isChanged = true;
								contactAddress.setCountryCode(addressDTO.getCountry());
							}
						}
					}
					logger.debug("***contactaddress.getPrimary before:" + contactAddress.getPrimary());

					if (Boolean.parseBoolean(contactAddress.getPrimary()) != customerAddressDTO.getIsPrimary()) {
						contactAddress.setPrimary(customerAddressDTO.getIsPrimary().toString());
						if (customerAddressDTO.getIsPrimary()) {
							isChanged = true;
						}
					}
					logger.debug("***contactaddress.getPrimary after:" + isChanged);

					String addressInputType = customerAddressDTO.getType_id();
					addressInputType = StringUtils.isNotBlank(addressInputType)
							? PartyMappingsExtn.getPartycontactAddressTypeMapping().containsKey(addressInputType)
									? PartyMappingsExtn.getPartycontactAddressTypeMapping().get(addressInputType)
									: addressInputType
							: "";

					logger.debug("***Address Types: changed" + addressInputType + "input->"
							+ customerAddressDTO.getType_id() + "party->" + contactAddress.getAddressType());
					if (!addressInputType.equalsIgnoreCase(contactAddress.getAddressType())) {
						contactAddress.setAddressType(addressInputType);
						isChanged = true;
					}

					if (isChanged) {
						contactAddress.setCommunicationNature(DTOConstants.PHYSICAL);
						contactAddress.setCommunicationType("MailingAddress");
						finalPartyDTO.setPartyAddress(contactAddress);
					}
				}

				else if (customerAddressDTO.getIsNew()) {
					logger.debug("***address is new:");
					contactAddress = new PartyAddress();
					AddressDTO addressDTO = customerAddressDTO.getAddressDTO();
					contactAddress.setBuildingName(addressDTO.getAddressLine1());
					contactAddress.setStreetName(addressDTO.getAddressLine2());
					if (StringUtils.isNotBlank(addressDTO.getCity_id())) {
						contactAddress.setTown(addressDTO.getCity_id());
					}

					if (StringUtils.isNotBlank(addressDTO.getCityName())) {
						contactAddress.setTown(addressDTO.getCityName());
					}
					contactAddress.setPostalOrZipCode(addressDTO.getZipCode());
					String regionId = addressDTO.getRegion_id();
					if (StringUtils.isNotBlank(regionId)) {
						isChanged = true;
						contactAddress.setRegionCode(regionId);
						if (regionId.contains("-")) {
							String[] parts = addressDTO.getRegion_id().split("-");
							contactAddress.setCountryCode(parts[0]);
							contactAddress.setCountrySubdivision(parts[1]);
						} else {
							if (StringUtils.isNotBlank(addressDTO.getState())) {
								contactAddress.setCountrySubdivision(addressDTO.getState());
							}
							if (StringUtils.isNotBlank(addressDTO.getRegion_id())) {
								contactAddress.setCountrySubdivision(addressDTO.getRegion_id());
							}
							if (StringUtils.isNotBlank(addressDTO.getCountry())) {
								contactAddress.setCountryCode(addressDTO.getCountry());
							}
						}
					}
					logger.debug("***contactaddress.getPrimary before:" + contactAddress.getPrimary()
							+ "customerAddressDTO.getIsPrimary()" + customerAddressDTO.getIsPrimary());
					if (Boolean.parseBoolean(contactAddress.getPrimary()) != customerAddressDTO.getIsPrimary()) {
						contactAddress.setPrimary(String.valueOf(customerAddressDTO.getIsPrimary()));
					}
					logger.debug("***contactaddress.getPrimary after:" + contactAddress.getPrimary()
							+ "customerAddressDTO.getIsPrimary()" + customerAddressDTO.getIsPrimary());
					if (PartyMappingsExtn.getPartycontactAddressTypeMapping()
							.containsKey(customerAddressDTO.getType_id())) {
						logger.debug("***Address Types New: changed"
								+ PartyMappingsExtn.getPartycontactAddressTypeMapping()
										.get(customerAddressDTO.getType_id())
								+ "input->" + customerAddressDTO.getType_id() + "party->"
								+ contactAddress.getAddressType());
						contactAddress.setAddressType(PartyMappingsExtn.getPartycontactAddressTypeMapping()
								.get(customerAddressDTO.getType_id()));
					} else {
						logger.debug("***Address Types new no match: changed" + "input->"
								+ customerAddressDTO.getType_id() + "party->" + contactAddress.getAddressType());
						contactAddress.setAddressType(customerAddressDTO.getType_id());
					}
					contactAddress.setCommunicationNature(DTOConstants.PHYSICAL);
					contactAddress.setCommunicationType("MailingAddress");
					finalPartyDTO.setPartyAddress(contactAddress);
				}
			}
			// }
		}

		if (StringUtils.isNotBlank(customerDTO.getiDType_id())) {
			List<PartyIdentifier> partyIdentifiers = new ArrayList<PartyIdentifier>();

			boolean isNew = true;
			boolean isChanged = false;
			PartyIdentifier partyIdentifier = new PartyIdentifier();

			if (initialPartyDTO.getPartyIdentifier() != null) {
				partyIdentifiers = initialPartyDTO.getPartyIdentifier();
				for (PartyIdentifier identifier : partyIdentifiers) {
					isChanged = false;
					if (identifier.getType()
							.equals(PartyMappings.getPartyIdentifierMapping().get(customerDTO.getiDType_id()))) {
						partyIdentifier = identifier;
						isNew = false;
						break;
					}
				}
			}
			if (!isNew) {

				if (StringUtils.isNotBlank(customerDTO.getiDExpiryDate())
						&& (StringUtils.isBlank(partyIdentifier.getExpiryDate())
								|| !partyIdentifier.getExpiryDate().equalsIgnoreCase(customerDTO.getiDExpiryDate()))) {
					isChanged = true;
					partyIdentifier.setExpiryDate(customerDTO.getiDExpiryDate());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDIssueDate())
						&& (StringUtils.isBlank(partyIdentifier.getIssuedDate())
								|| !partyIdentifier.getIssuedDate().equalsIgnoreCase(customerDTO.getiDIssueDate()))) {
					isChanged = true;
					partyIdentifier.setIssuedDate(customerDTO.getiDIssueDate());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDCountry())
						&& (StringUtils.isBlank(partyIdentifier.getIssuingCountry())
								|| !partyIdentifier.getIssuingCountry().equalsIgnoreCase(customerDTO.getiDCountry()))) {
					isChanged = true;
					partyIdentifier.setIssuingCountry(customerDTO.getiDCountry());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDState()) && (StringUtils
						.isBlank(partyIdentifier.getCountrySubdivision())
						|| !partyIdentifier.getCountrySubdivision().equalsIgnoreCase(customerDTO.getiDState()))) {
					isChanged = true;
					partyIdentifier.setCountrySubdivision(customerDTO.getiDState());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDValue())
						&& (StringUtils.isBlank(partyIdentifier.getIdentifierNumber())
								|| !partyIdentifier.getIdentifierNumber().equalsIgnoreCase(customerDTO.getiDValue()))) {
					isChanged = true;
					partyIdentifier.setIdentifierNumber(customerDTO.getiDValue());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDType_id())) {
					if (isChanged) {
						if (PartyMappings.getPartyIdentifierMapping().containsKey(customerDTO.getiDType_id())) {
							partyIdentifier
									.setType(PartyMappings.getPartyIdentifierMapping().get(customerDTO.getiDType_id()));
						} else {
							partyIdentifier.setType(customerDTO.getiDType_id());
						}
						finalPartyDTO.setPartyIdentifier(partyIdentifier);
					}
				}
			} else {
				partyIdentifier = new PartyIdentifier();
				partyIdentifier.setExpiryDate(customerDTO.getiDExpiryDate());
				partyIdentifier.setIssuedDate(customerDTO.getiDIssueDate());
				partyIdentifier.setIssuingCountry(customerDTO.getiDCountry());
				partyIdentifier.setCountrySubdivision(customerDTO.getiDState());
				partyIdentifier.setIdentifierNumber(customerDTO.getiDValue());
				if (PartyMappings.getPartyIdentifierMapping().containsKey(customerDTO.getiDType_id())) {
					partyIdentifier.setType(PartyMappings.getPartyIdentifierMapping().get(customerDTO.getiDType_id()));
				} else {
					partyIdentifier.setType(customerDTO.getiDType_id());
				}
				finalPartyDTO.setPartyIdentifier(partyIdentifier);
			}

		}

		if (StringUtils.isNotBlank(customerDTO.getDrivingLicenseNumber())) {
			List<PartyIdentifier> partyIdentifiers = new ArrayList<PartyIdentifier>();

			boolean isNew = true;
			boolean isChanged = false;
			PartyIdentifier partyIdentifier = new PartyIdentifier();

			if (initialPartyDTO.getPartyIdentifier() != null) {
				partyIdentifiers = initialPartyDTO.getPartyIdentifier();
				for (PartyIdentifier identifier : partyIdentifiers) {
					isChanged = false;
					if (StringUtils.isNotBlank(identifier.getType()) && identifier.getType()
							.equals(PartyMappings.getPartyIdentifierMapping().get("ID_DRIVING_LICENSE"))) {
						partyIdentifier = identifier;
						isNew = false;
						break;
					}
				}
			}

			if (!isNew) {

				if (StringUtils.isNotBlank(customerDTO.getiDExpiryDate())
						&& (StringUtils.isBlank(partyIdentifier.getExpiryDate())
								|| !partyIdentifier.getExpiryDate().equalsIgnoreCase(customerDTO.getiDExpiryDate()))) {
					isChanged = true;
					partyIdentifier.setExpiryDate(customerDTO.getiDExpiryDate());
				}
				if (StringUtils.isNotBlank(customerDTO.getiDIssueDate())
						&& (StringUtils.isBlank(partyIdentifier.getIssuedDate())
								|| !partyIdentifier.getIssuedDate().equalsIgnoreCase(customerDTO.getiDIssueDate()))) {
					isChanged = true;
					partyIdentifier.setIssuedDate(customerDTO.getiDIssueDate());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDCountry())
						&& (StringUtils.isBlank(partyIdentifier.getIssuingCountry())
								|| !partyIdentifier.getIssuingCountry().equalsIgnoreCase(customerDTO.getiDCountry()))) {
					isChanged = true;
					partyIdentifier.setIssuingCountry(customerDTO.getiDCountry());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDState()) && (StringUtils
						.isBlank(partyIdentifier.getCountrySubdivision())
						|| !partyIdentifier.getCountrySubdivision().equalsIgnoreCase(customerDTO.getiDState()))) {
					isChanged = true;
					partyIdentifier.setCountrySubdivision(customerDTO.getiDState());
				}

				if (StringUtils.isNotBlank(customerDTO.getiDValue())
						&& (StringUtils.isBlank(partyIdentifier.getIdentifierNumber())
								|| !partyIdentifier.getIdentifierNumber().equalsIgnoreCase(customerDTO.getiDValue()))) {
					isChanged = true;
					partyIdentifier.setIdentifierNumber(customerDTO.getiDValue());
				}
				if (isChanged) {
					partyIdentifier.setType(PartyMappings.getPartyIdentifierMapping().get("ID_DRIVING_LICENSE"));
					finalPartyDTO.setPartyIdentifier(partyIdentifier);
				}
			}

			else {
				partyIdentifier = new PartyIdentifier();
				partyIdentifier.setExpiryDate(customerDTO.getiDExpiryDate());
				partyIdentifier.setIssuedDate(customerDTO.getiDIssueDate());
				partyIdentifier.setIssuingCountry(customerDTO.getiDCountry());
				partyIdentifier.setCountrySubdivision(customerDTO.getiDState());
				partyIdentifier.setIdentifierNumber(customerDTO.getiDValue());
				partyIdentifier.setType(PartyMappings.getPartyIdentifierMapping().get("ID_DRIVING_LICENSE"));
				finalPartyDTO.setPartyIdentifier(partyIdentifier);
			}

		}

		if (StringUtils.isNotBlank(customerDTO.getSsn())) {
			List<PartyIdentifier> partyIdentifiers = new ArrayList<PartyIdentifier>();

			boolean isNew = true;
			PartyIdentifier partyIdentifier = new PartyIdentifier();

			if (initialPartyDTO.getPartyIdentifier() != null) {
				partyIdentifiers = initialPartyDTO.getPartyIdentifier();
				for (PartyIdentifier identifier : partyIdentifiers) {
					if (StringUtils.isNotBlank(identifier.getType())
							&& identifier.getType().equals(DTOConstants.SOCIAL_SECURITY_NUMBER)) {
						partyIdentifier = identifier;
						isNew = false;
						break;
					}
				}
			}

			if (!isNew) {
				if (StringUtils.isNotBlank(customerDTO.getSsn())) {
					if (StringUtils.isBlank(partyIdentifier.getIdentifierNumber())
							|| !partyIdentifier.getIdentifierNumber().equalsIgnoreCase(customerDTO.getSsn())) {
						partyIdentifier.setIdentifierNumber(customerDTO.getSsn());
						partyIdentifier.setType(DTOConstants.SOCIAL_SECURITY_NUMBER);
						finalPartyDTO.setPartyIdentifier(partyIdentifier);
					}
				}
			}

			else {
				partyIdentifier = new PartyIdentifier();
				partyIdentifier.setIdentifierNumber(customerDTO.getSsn());
				partyIdentifier.setType(DTOConstants.SOCIAL_SECURITY_NUMBER);
				finalPartyDTO.setPartyIdentifier(partyIdentifier);
			}

		}

		if (StringUtils.isNotBlank(customerDTO.getTaxID())) {

			List<TaxDetails> details = initialPartyDTO.getTaxDetails();
			boolean isNew = true;
			if (details != null) {
				for (TaxDetails detail : details) {
					if (detail.getTaxType().equals(DTOConstants.INCOME_TAX)) {
						if (!detail.getTaxId().equalsIgnoreCase(customerDTO.getTaxID())) {
							detail.setTaxId(customerDTO.getTaxID());
							finalPartyDTO.setTaxDetails(detail);
						}
						isNew = false;
						break;
					}
				}
			}

			if (isNew) {
				TaxDetails taxdetails = new TaxDetails();
				taxdetails.setTaxType(DTOConstants.INCOME_TAX);
				taxdetails.setTaxId(customerDTO.getTaxID());

				finalPartyDTO.setTaxDetails(taxdetails);
			}
		}

		return finalPartyDTO;
	}
}
