package com.kony.sbg.postprocessor.spotlight;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.sideloading.main.LoadSmeData;
import com.kony.sbg.sideloading.models.Contract;
import com.kony.sbg.sideloading.models.Customer;
import com.kony.sbg.sideloading.models.UserDetails;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.util.*;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.utils.DTOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.kony.dbputilities.util.StatusEnum.SID_CUS_ACTIVE;
import static com.kony.sbg.sideloading.network.PartyHelper.URL_CREATEUSERPARTYRECORD;
import static com.kony.sbg.sideloading.utils.CommonUtils.ZA_REGION_CODES;
import static com.kony.sbg.util.SbgErrorCodeEnum.ERR_100037;
import static com.kony.sbg.util.SbgURLConstants.*;
import static com.temenos.dbx.product.utils.DTOConstants.PARTY;

public class CreateUserPostProcessor implements DataPostProcessor2 {

    private static final Logger logger = LogManager.getLogger(CreateUserPostProcessor.class);

    @Override
    public Object execute(Result result,
            DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        logger.debug("#################### CreateUserPostProcessor : BEGIN");
        try {
            logger.debug("\t Creating Customer Party");
            String customerPartyId = createUserPartyMs(result, request);
            logger.debug("\t Created Customer Party: " + customerPartyId);

            logger.debug("\t Updating Customer");

            String message = updateCustomer(result, request);
            if (StringUtils.isNotEmpty(message)) {

                throw new Exception(message);
            }
            logger.debug("\t Updated Customer");

            logger.debug("\t Creating Backend Identifier");
            createBackendIdentifier(result, request, customerPartyId);
            logger.debug("\t Created Backend Identifier");

        } catch (Exception exception) {
            logger.debug("General Exception occured in CreateUserPostProcessor: ", exception);
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errmsg", exception.getMessage());
            result.addParam("dbpErrMsg",  SbgErrorCodeEnum.ERR_100033.getMessage());
            result.addParam("dbpErrCode", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
        } finally {
            logger.debug("#################### CreateUserPostProcessor : FINISH");
            return result;
        }
    }

    private static Customer getContractCustomer(DataControllerRequest request) throws Exception {
        Contract contract = getContract(request);
        Customer customer = new Customer();
        customer.setCoreCustomerId(contract.getCoreCustId());
        customer.setPostalAddressLine1(contract.getAddressLine1());
        customer.setPostalAddressLine2(contract.getAddressLine2());
        customer.setPostalAddressLine3(contract.getAddressLine3());
        customer.setPostalCityName(contract.getCityName());
        customer.setPostalState(contract.getState());
        customer.setCountryCode(contract.getCountry());
        customer.setPostalZipCode(contract.getZipCode());

        return customer;
    }

    private static Result getCustomerByCustomerId(String customerId,
            DataControllerRequest request) throws Exception {
        HashMap<String, Object> svcParams = new HashMap<>();
        svcParams.put("Customer_id", customerId);

        return HelperMethods.callApi(
                request,
                svcParams,
                HelperMethods.getHeaders(request),
                getURL(SERVICE_CUSTOMER_MANAGEMENT, OPERATION_GET_BASIC_INFO));
    }

    private static Contract getContract(DataControllerRequest request) throws Exception {
        Result contractResult = getContractById(request);
        JSONObject contractJson = new JSONObject(ResultToJSON.convert(contractResult));
        Contract contract = new Contract();
        JSONObject contractCustomer = contractJson.getJSONArray("contractCustomers").getJSONObject(0);
        JSONObject contractAddress = contractJson.getJSONArray("address").getJSONObject(0);

        contract.setCoreCustId(SBGUtil.getString(contractCustomer, "coreCustomerId"));
        contract.setContractName(SBGUtil.getString(contractResult, "name"));
        contract.setAddressLine1(SBGUtil.getString(contractAddress, "addressLine1"));
        contract.setAddressLine2(SBGUtil.getString(contractAddress, "addressLine2"));
        contract.setAddressLine3(SBGUtil.getString(contractAddress, "addressLine3"));
        contract.setCityName(SBGUtil.getString(contractAddress, "cityName"));
        contract.setState(SBGUtil.getString(contractAddress, "state"));
        contract.setCountry(SBGUtil.getString(contractAddress, "country"));
        contract.setZipCode(SBGUtil.getString(contractAddress, "zipCode"));

        return contract;
    }

    private static Result getContractById(DataControllerRequest request) throws Exception {
        HashMap<String, Object> svcParams = new HashMap<>();
        JSONArray companyListJson = new JSONArray(request.getParameter("companyList"));
        JSONObject company = companyListJson.getJSONObject(0);

        svcParams.put("contractId", SBGUtil.getString(company, "contractId"));

        return HelperMethods.callApi(
                request,
                svcParams,
                HelperMethods.getHeaders(request),
                getURL(SERVICE_CONTRACT_MANAGEMENT, OPERATION_GET_CONTRACT_DETAILS));
    }

    private static String getContractId(DataControllerRequest request) {
        JSONArray companyListJson = new JSONArray(request.getParameter("companyList"));
        JSONObject company = companyListJson.getJSONObject(0);

        return SBGUtil.getString(company, "contractId");
    }

    private String createUserPartyMs(Result result,
            DataControllerRequest request) throws Exception {

        logger.debug("\t Start getUserDetailsFromRequest ");
        UserDetails userDetails = UserDetails.getUserDetailsFromRequest(request);
        logger.debug("\t End getUserDetailsFromRequest: " + userDetails);

        logger.debug("\t Start getCustomer ");
        Customer customer = getContractCustomer(request);
        logger.debug("\t End getCustomer: " + customer);

        logger.debug("\t Start getUserPayload4Party ");
        JSONObject userPayload4Party = getUserPartyPayload(request, result, userDetails);
        logger.debug("\t End getUserPayload4Party: " + userPayload4Party);

        logger.debug("\t Start createUserPartyRecord ");
        JSONObject userPartyRecord = new JSONObject(
                ResultToJSON.convert(createUserPartyRecord(request, userPayload4Party)));
        ;
        logger.debug("\t End getUserPayload4Party: " + userPartyRecord);

        if (!StringUtils.isEmpty(userPartyRecord.toString())) {
            return (String) userPartyRecord.get("id");
        } else {
            throw new Exception(ERR_100037.getMessage());
        }
    }

    private String updateCustomer(Result result,
            DataControllerRequest request) throws Exception {

        HashMap<String, Object> svcParams = new HashMap<>();
        UserDetails userDetails = UserDetails.getUserDetailsFromRequest(request);

        svcParams.put("id", result.getParamValueByName("id"));
        svcParams.put("Status_id", SID_CUS_ACTIVE);
        svcParams.put("UserName", userDetails.getEmail());
        svcParams.put("isEnrolled", Boolean.TRUE.toString());
        svcParams.put("isEBTCaccepted", isAnyCustomerAcceptedTermsAndConditionsByContractId(request));

        Result updateCustomerResult = HelperMethods.callApi(
                request,
                svcParams,
                HelperMethods.getHeaders(request),
                getURL(SERVICE_SBGCRUD, OPERATION_CUSTOMER_UPDATE));

        logger.debug("----- updateCustomerResult: " + ResultToJSON.convert(updateCustomerResult));
        JSONObject updateCustomerJson = new JSONObject(ResultToJSON.convert(updateCustomerResult));
        boolean val = updateCustomerJson.get("opstatus").toString().equals("0");
        String errMsg = val ? null : updateCustomerJson.get("errmsg").toString();
        // String errMsg = updateCustomerJson.get("errmsg") != null ?
        // updateCustomerJson.get("errmsg").toString() : "Error on Customer update.";
        String message = errMsg.contains("Duplicate entry") ? "This user already exists." : errMsg;

        return message;
        // return updateCustomerJson.get("opstatus").toString().equals("0");
    }

    private void createBackendIdentifier(Result result,
            DataControllerRequest request,
            String customerPartyId) throws Exception {

        String customerId = SBGUtil.getString(result, "id");
        logger.debug("\t Creating Backend Identifier for Party");
        createPartyBackendIdentifier(request, customerPartyId, customerId);
        logger.debug("\t Created Backend Identifier for Party");
        logger.debug("\t Creating Backend Identifier for Company Party");
        createCompanyPartyBackendIdentifier(request, customerId);
        logger.debug("\t Created Backend Identifier for Company Party");
    }

    private static void createCompanyPartyBackendIdentifier(DataControllerRequest request,
            String customerId) throws Exception {

        String companyPartyId = getCompanyPartyId(request);

        BackendIdentifierDTO custBackendIdentifierDTO = new BackendIdentifierDTO();
        custBackendIdentifierDTO.setId(UUID.randomUUID().toString());
        custBackendIdentifierDTO.setIsNew(true);
        custBackendIdentifierDTO.setCustomer_id(customerId);
        custBackendIdentifierDTO.setSequenceNumber("1");
        custBackendIdentifierDTO.setBackendId(companyPartyId);
        custBackendIdentifierDTO.setBackendType(SBGConstants.COMPANYPARTYID);
        custBackendIdentifierDTO.setIdentifier_name("customer_id");
        custBackendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler
                .getValue(DBPUtilitiesConstants.BRANCH_ID_REFERENCE));

        logger.debug(
                "\t createBackendIdentifierEntry() ---> 1. companyBackendIdentifierDTO: " + custBackendIdentifierDTO);
        Map<String, Object> input = DTOUtils.getParameterMap(custBackendIdentifierDTO, true);
        if (!custBackendIdentifierDTO.persist(input, request.getHeaderMap())) {
            throw new Exception("Error on Company Party BackendIdentifier create.");
        }
        logger.debug(
                "\t createBackendIdentifierEntry() ---> 2. companyBackendIdentifierDTO: " + custBackendIdentifierDTO);
    }

    private static void createPartyBackendIdentifier(DataControllerRequest request,
            String customerPartyId,
            String customerId) throws Exception {
        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setIsNew(true);
        backendIdentifierDTO.setCustomer_id(customerId);
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setBackendId(customerPartyId);
        backendIdentifierDTO.setBackendType(PARTY);
        backendIdentifierDTO.setIdentifier_name("customer_id");
        backendIdentifierDTO.setCompanyId(EnvironmentConfigurationsHandler
                .getValue(DBPUtilitiesConstants.BRANCH_ID_REFERENCE));

        logger.debug("\t createBackendIdentifierEntry() ---> 1. partyBackendIdentifierDTO: " + backendIdentifierDTO);
        Map<String, Object> input = DTOUtils.getParameterMap(backendIdentifierDTO, true);
        if (!backendIdentifierDTO.persist(input, request.getHeaderMap())) {
            throw new Exception("Error on Party BackendIdentifier create.");
        }
        logger.debug("\t createBackendIdentifierEntry() ---> 2. partyBackendIdentifierDTO: " + backendIdentifierDTO);
    }

    private static String getCompanyPartyId(DataControllerRequest request) throws Exception {

        logger.debug("\t --------- START getCompanyPartyId");

        String contractId = getContractId(request);
        logger.debug("\t --------- contractId:" + contractId);

        String companyPartyId = getCoreCompanyPartyIdBackendIdentifier(request, contractId);
        if (StringUtils.isEmpty(companyPartyId)) {
            throw new Exception("Error on retrieve companyPartyId for contractId: " + contractId);
        }
        return companyPartyId;
    }

    private static String getURL(String service, String operation) {
        String url = "/services/".concat(service).concat("/").concat(operation);
        logger.debug("\t getURL :: " + url);
        return url;
    }

    private static JSONObject getUserPartyPayload(DataControllerRequest request,
            Result result,
            UserDetails userDetails) throws Exception {
        logger.debug("\t ----- Start getUserPayload4Party -----");
        String customerId = result.getParamValueByName("id");
        logger.debug("\t --------- customerId: " + customerId);

        Result customerResult = getCustomerByCustomerId(customerId, request);

        JSONObject customerJson = new JSONObject(ResultToJSON.convert(customerResult));
        logger.debug("\t --------- customerJson: " + customerJson);

        JSONObject customerBasicInfo = customerJson.getJSONObject("customerbasicinfo_view");
        logger.debug("\t --------- customerBasicInfo: " + customerJson);

        Contract contract = getContract(request);
        logger.debug("\t --------- contract: " + contract);

        logger.debug("\t ----- Start to build userParty payload -----");

        JSONObject userParty = new JSONObject();
        userParty.put("dob", CommonUtils.getDobFormatted(userDetails.getDob()));
        logger.debug("\t --------- dob: " + userParty.getString("dob"));

        userParty.put("title", "Mr");
        userParty.put("firstName", userDetails.getFirstName());
        logger.debug("\t --------- firstName: " + userParty.getString("firstName"));

        userParty.put("middleName", userDetails.getMiddleName());
        logger.debug("\t --------- middleName: " + userParty.getString("middleName"));

        userParty.put("lastName", userDetails.getLastName());
        logger.debug("\t --------- lastName: " + userParty.getString("lastName"));

        userParty.put("fullName", userDetails.getFirstName() + " " + userDetails.getLastName());
        logger.debug("\t --------- fullName: " + userParty.getString("fullName"));

        userParty.put("identifierNumber", userDetails.getSsn());
        logger.debug("\t --------- identifierNumber: " + userParty.getString("identifierNumber"));

        userParty.put("docTagId", customerId);
        logger.debug("\t --------- docTagId: " + userParty.getString("docTagId"));

        userParty.put("idNumber", LoadSmeData.COMPANY_ID + "-" + customerId);
        logger.debug("\t --------- idNumber: " + userParty.getString("idNumber"));

        userParty.put("idSource", "TransactT24");
        userParty.put("email", userDetails.getEmail());
        logger.debug("\t --------- email: " + userParty.getString("email"));

        userParty.put("phoneNo", SBGUtil.getString(customerBasicInfo, "PrimaryPhoneNumber"));
        logger.debug("\t --------- phoneNo: " + userParty.getString("phoneNo"));

        userParty.put("addressline1",
                StringUtils.isNotEmpty(contract.getAddressLine1()) ? contract.getAddressLine1() : "5 Simmonds Street");
        logger.debug("\t --------- addressline1: " + userParty.getString("addressline1"));

        userParty.put("addressline2",
                StringUtils.isNotEmpty(contract.getAddressLine2()) ? contract.getAddressLine2() : "");
        logger.debug("\t --------- addressline2: " + userParty.getString("addressline2"));

        userParty.put("addressline3",
                StringUtils.isNotEmpty(contract.getAddressLine3()) ? contract.getAddressLine3() : "");
        logger.debug("\t --------- addressline3: " + userParty.getString("addressline3"));

        userParty.put("city", StringUtils.isNotEmpty(contract.getCityName()) ? contract.getCityName() : "Johannesburg");
        logger.debug("\t --------- city: " + userParty.getString("city"));

        String state = StringUtils.isNotEmpty(contract.getState()) ? contract.getState() : "Gauteng";
        userParty.put("district", state);
        logger.debug("\t --------- district: " + userParty.getString("district"));

        userParty.put("state", state);
        logger.debug("\t --------- state: " + userParty.getString("state"));

        String country = StringUtils.isNotEmpty(contract.getCountry()) ? contract.getCountry() : "ZA";
        userParty.put("country", country);
        logger.debug("\t --------- country: " + userParty.getString("country"));

        userParty.put("countryCode", StringUtils.isNotEmpty(contract.getCountry()) ? contract.getCountry() : "ZA");
        logger.debug("\t --------- countryCode: " + userParty.getString("countryCode"));

        userParty.put("zipcode", StringUtils.isNotEmpty(contract.getZipCode()) ? contract.getZipCode() : "2196");
        logger.debug("\t --------- zipcode: " + userParty.getString("zipcode"));

        userParty.put("partyType", "Organisation");
        userParty.put("entityName", contract.getContractName());
        logger.debug("\t --------- entityName: " + userParty.getString("entityName"));

        String subRegionCode = Objects.isNull(ZA_REGION_CODES.get(state)) ? "GT" : ZA_REGION_CODES.get(state);
        userParty.put("regionCode", country + "-" + subRegionCode);
        logger.debug("\t --------- regionCode: " + userParty.getString("regionCode"));

        userParty.put("subRegionCode", subRegionCode);
        logger.debug("\t --------- subRegionCode: " + userParty.getString("subRegionCode"));

        logger.debug("\t --------- userParty: " + userParty);
        logger.debug("\t ----- End to build userParty payload -----");

        return userParty;
    }

    public static Result createUserPartyRecord(DataControllerRequest request, JSONObject payload) throws Exception {

        logger.debug("------ Start createUserPartyRecord");

        Map<String, String> svcHeaders = HelperMethods.getHeaders(request);
        String partyAuthKey = SBGCommonUtils.getServerPropertyValue(PARTYMS_AUTHORIZATION_KEY, request);
        svcHeaders.put("x-api-key", partyAuthKey);
        logger.debug("------ PARTY_AUTH_KEY: " + partyAuthKey);

        HashMap<String, Object> svcParams = new HashMap<>();
        payload.keys().forEachRemaining(key -> svcParams.put(key, payload.getString(key)));

        return HelperMethods.callApi(
                request,
                svcParams,
                svcHeaders,
                URL_CREATEUSERPARTYRECORD);

    }

    public static String getCoreCompanyPartyIdBackendIdentifier(DataControllerRequest request, String contractId)
            throws Exception {

        logger.debug("------ Start getCoreCompanyPartyIdBackendIdentifier");

        Map<String, Object> svcParams = new HashMap<>();

        String filter = "contractId".concat(DBPUtilitiesConstants.EQUAL).concat(contractId)
                .concat(DBPUtilitiesConstants.AND)
                .concat("BackendType").concat(DBPUtilitiesConstants.EQUAL).concat(SBGConstants.COMPANYPARTYID);

        logger.debug("------ filter: " + filter);

        svcParams.put(DBPUtilitiesConstants.FILTER, filter);

        Result result = HelperMethods.callApi(
                request,
                svcParams,
                HelperMethods.getHeaders(request),
                getURL(SERVICE_DBPRBLOCALSERVICESDB, OPERATION_DBXDB_BACKENDIDENTIFIER_GET));

        JSONObject resultJson = new JSONObject(ResultToJSON.convert(result));

        if (resultJson.has("backendidentifier")
                && resultJson.getJSONArray("backendidentifier").length() > 0) {
            JSONObject backendidentifier = resultJson.getJSONArray("backendidentifier").getJSONObject(0);
            logger.debug("------ backendidentifier: " + backendidentifier);
            logger.debug("------ BackendId: " + backendidentifier.get("BackendId"));
            return backendidentifier.get("BackendId").toString();

        } else {
            throw new Exception("Error on Company Party BackendIdentifier retrieve. filter: " + filter);
        }
    }

    private boolean isAnyCustomerAcceptedTermsAndConditionsByContractId(DataControllerRequest request)
            throws Exception {

        logger.debug("------ Start verifyTermsAndConditionsWasAssignByContract");

        HashMap<String, Object> svcParams = new HashMap<>();
        svcParams.put("contractId", getContractId(request));

        Result customersResult = HelperMethods.callApi(
                request,
                svcParams,
                HelperMethods.getHeaders(request),
                getURL(SERVICE_SBGCRUD, OPERATION_CUSTOMER_ACCEPTED_TERMS_AND_CONDITIONS_GET));

        JSONObject customersJson = new JSONObject(ResultToJSON.convert(customersResult));
        logger.debug("----- customersResult: " + customersJson);

        return customersJson.has("records") && customersJson.getJSONArray("records").length() > 0;
    }
}