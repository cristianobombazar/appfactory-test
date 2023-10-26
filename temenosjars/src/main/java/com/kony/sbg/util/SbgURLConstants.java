package com.kony.sbg.util;

import java.util.HashMap;
import java.util.Map;

public final class SbgURLConstants {

	public static final String URL_CREATE_PAYEE = "/services/C360IntegrationService/GetProductList";
	public static final String SERVICE_SBGCRUDLAYER = "SBGCRUDLayer";
	public static final String OPERATION_EXTERNALACCOUNT_GET = "dbxdb_externalaccount_get";
	public static final String OPERATION_EXTERNALACCOUNT_CREATE = "dbxdb_externalaccount_create";
	public static final Map<String, String> BENE_HEADERS = new HashMap<>();

	static {
		BENE_HEADERS.put("beneficiaryName", "Beneficiary Name");
		BENE_HEADERS.put("entityType", "Beneficiary Type");
		BENE_HEADERS.put("accountNumber", "Account No/IBAN");
		BENE_HEADERS.put("beneCode", "Beneficiary code");
		BENE_HEADERS.put("swiftCode", "BIC/Swift Code");
		BENE_HEADERS.put("routingNumber", "Clearing code");
		BENE_HEADERS.put("bankName", "Bank name");
		BENE_HEADERS.put("phoneNumber", "Phone number");
		BENE_HEADERS.put("email", "Email address");
		BENE_HEADERS.put("address", "Address");
		BENE_HEADERS.put("status", "Status");
	}

	;
	public static final Map<String, Float> BENE_PDF_COL_WIDTH = new HashMap<>();

	static {
		BENE_PDF_COL_WIDTH.put("beneficiaryName", (float) 1);
		BENE_PDF_COL_WIDTH.put("entityType", (float) 1);
		BENE_PDF_COL_WIDTH.put("accountNumber", (float) 2);
		BENE_PDF_COL_WIDTH.put("beneCode", (float) 1);
		BENE_PDF_COL_WIDTH.put("swiftCode", (float) 1);
		BENE_PDF_COL_WIDTH.put("routingNumber", (float) 1);
		BENE_PDF_COL_WIDTH.put("bankName", (float) 1);
		BENE_PDF_COL_WIDTH.put("phoneNumber", (float) 1);
		BENE_PDF_COL_WIDTH.put("email", (float) 1);
		BENE_PDF_COL_WIDTH.put("address", (float) 2);
		BENE_PDF_COL_WIDTH.put("status", (float) 1);
	}

	;
	public static final int MAX_COL_PORTRAIT = 6;
	public static final String BENEFICIARY_STATUS = "Active";
	public static final String SBG_PARAM_API_PRICE_SEGMENT = "SBG_PARAM_API_PRICE_SEGMENT";
	public static final String SBG_HEADER_API_CHANNEL_ID = "SBG_HEADER_API_CHANNEL_ID";
	public static final String SBG_HEADER_API_COUNTRY_CODE = "SBG_HEADER_API_COUNTRY_CODE";
	public static final String SERVICE_SBGMAIBMGATEWAY = "SBGMAIBMGATEWAY";
	public static final String SERVICE_SBGMAIBMGATEWAY2 = "SBGMAIBMGATEWAY2";
	public static final String OPERATION_GETINDICATIVERATES = "getIndicativeRates";
	public static final String IBAN = "IBAN";
	public static final String SMALL_IBAN = "iban";
	public static final String ID = "Id";
	public static final String CONST_ID = "id";
	public static final String USER_ID = "userId";
	public static final String COMPANYID = "companyId";
	public static final String ISBUSINESS = "isBusinessPayee";
	public static final String EXTERNAL_PAYEE_ORCH_SERVICE = "dbpExternalPayeesOrch";
	public static final String EXTERNAL_PAYEE_ORCH_OPERATION = "getExternalPayees";
	// getBalance params
	public static final String URL_GET_BALANCE = "/services/SBGMAIBMGATEWAY2/FetchBalance";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String AUTHORIZATION = "Authorization";
	public static final String X_IBM_CLIENT_ID = "X-IBM-CLIENT-ID";
	public static final String X_IBM_CLIENT_ID_VALUE = "dcb68db2a80a2a88f0989bfb68a61c06";
	public static final String X_IBM_CLIENT_SECRET = "X-IBM-CLIENT-SECRET";
	public static final String X_IBM_CLIENT_SECRET_VALUE = "80048151b3278f28c3bed4769a8458d4";
	public static final String ACCOUNT_NUMBERS = "accountNumbers";
	public static final String CB_SID = "cbsid";
	public static final String BIC = "bic";
	public static final String FETCHBALANCE_ACC_SID = "accountSystemId";
	public static final String FETCHBALANCE_SWIFTCODE = "swiftCode";
	public static final String CURRENCY = "currency";
	public static final String ACCOUNT_IDENTIFICATION = "accountIdentification";
	public static final String BALANCE_TYPES = "balanceTypes";
	public static final String ACCOUNT_NUMBER = "accountNumber";
	public static final String FILTER = "$filter";
	public static final String SBG_HEADER_API_PRODUCT_CODE = "SBG_HEADER_API_PRODUCT_CODE";
	public static final String OPERATION_GETALLOWEDCURRENCIES = "getAllowedCurrencies";
	public static final String SBG_PARAM_API_CURRENCY_PAIR = "SBG_PARAM_API_CURRENCY_PAIR";
	public static final String SBG_PARAM_API_SENDER_ID = "SBG_PARAM_API_SENDER_ID";
	public static final String SBG_PARAM_API_SOURCE_SYSTEM = "SBG_PARAM_API_SOURCE_SYSTEM";
	public static final String SBG_PARAM_API_APPLICATION = "SBG_PARAM_API_APPLICATION";
	public static final String SBG_PARAM_API_APPLICATION_SESSION_ID = "SBG_PARAM_API_APPLICATION_SESSION_ID";
	public static final String SBG_PARAM_API_SOURCE_LOCATION = "SBG_PARAM_API_SOURCE_LOCATION";
	public static final String SBG_PARAM_API_VERSION = "SBG_PARAM_API_VERSION";
	// get tradable accounts
	public static final String URL_GET_TRADABLE_ACCOUNTS = "/services/SBG_RFQ/GetTradableAccounts";
	public static final String X_CHANNEL_ID = "X-CHANNEL-ID";
	// public static final String X_REQ_ID="X-REQ-ID";
	public static final String TRD_ACC_REQ_NAME = "TRD_ACC_REQ_NAME";
	public static final String TRD_ACC_RES_NAME = "TRD_ACC_RES_NAME";
	public static final String ACC_QUO_REQ_NAME = "ACC_QUO_REQ_NAME";
	public static final String ACC_QUO_RES_NAME = "ACC_QUO_RES_NAME";
	public static final String GET_QUO_REQ_NAME = "GET_QUO_REQ_NAME";
	public static final String GET_QUO_RES_NAME = "GET_QUO_RES_NAME";
	public static final String REJ_QUO_REQ_NAME = "REJ_QUO_REQ_NAME";
	public static final String REJ_QUO_RES_NAME = "REJ_QUO_RES_NAME";

	// RFQ URLS
	public static final String URL_FETCH_QUOTES = "/services/SBG_RFQ/GetRFQ";
	public static final String URL_ACCEPT_QUOTES = "/services/SBG_RFQ/AcceptRFQ";
	public static final String URL_REJECT_QUOTES = "/services/SBG_RFQ/RejectRFQ";

	public static final String GET_MYACCESS_USERS = "/services/SBGMyAccessJsonServices/GetMyAccessAllUsers";

	public static final String UPDATE_MYACCESS_USER = "/services/SBGMyAccessJsonServices/UpdateMyAccessUser";

	public static final String PROD_TYPE = "PRODUCT_TYPE";
	public static final String DEALTA_DIRECTION = "DEALTA_DIRECTION";
	public static final String SBG_PARAM_PAYMENT_NAME = "SBG_PARAM_PAYMENT_NAME";
	public static final String SBG_PARAM_PAYMENT_PROVIDER = "SBG_PARAM_PAYMENT_PROVIDER";
	public static final String PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
	public static final Map<String, String> CHARGE_BEARER = new HashMap<>();

	static {
		CHARGE_BEARER.put("OUR", "DEBT");
		CHARGE_BEARER.put("BEN", "CRED");
		CHARGE_BEARER.put("SHA", "SHAR");
	}

	;
	public static final String SERVICE_SBG_PAYMENT = "SBGPaymentProxy";
	// public static final String SERVICE_SBG_DOMESTIC_PAYMENT = "SbgDomesticUrgPayment";
	public static final String OPERATION_SUBMITPAYMENT = "submitPayment";
	// public static final String OPERATION_SUBMITDOMESTICPAYMENT = "submitUrgentPayment";
	public static final String OPERATION_INTERNATIONALFUNDTRANSFERSREFDATA_UPDATE = "dbxdb_internationalfundtransfersRefData_update";
	public static final String OPERATION_DOMESTICFUNDTRANSFERSREFDATA_UPDATE = "dbxdb_domesticfundtransfersRefData_update";
	public static final String OPERATION_INTERNATIONALFUNDTRANSFERSREFDATA_GET = "dbxdb_internationalfundtransfersRefData_get";
	public static final String OPERATION_DOMESTICFUNDTRANSFERSREFDATA_GET = "dbxdb_domesticfundtransfersRefData_get";
	public static final String BENE_INDIVIDUAL = "INDIVIDUAL";
	public static final String BENE_ENTITY = "ENTITY";
	public static final String SERVICE_DBPRBLOCALSERVICESDB = "dbpRbLocalServicesdb";
	public static final String OPERATION_DBXDB_COUNTRY_GET = "dbxdb_country_get";
	public static final String SERVICE_SBGCRUD = "SBGCRUD";
	public static final String OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_CREATE = "dbxdb_internationalfundtransfersRefData_create";
	public static final String OPERATION_DBXDB_DOMESTICFUNDTRANSFERSREFDATA_CREATE = "dbxdb_domesticfundtransfersRefData_create";
	public static final String OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_CREATE = "dbxdb_internationalTransferStatus_create";
	public static final String OPERATION_DBXDB_DOMESTICTRANSFERSTATUS_CREATE = "dbxdb_domesticTransferStatus_create";
	public static final String OPERATION_DBXDB_INTERNATIONALTRANSFERSTATUS_GET = "dbxdb_internationalTransferStatus_get";
	public static final String OPERATION_DBXDB_DOMESTICTRANSFERSTATUS_GET = "dbxdb_domesticTransferStatus_get";
	public static final String OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSREFDATA_GET = "dbxdb_internationalfundtransfersRefData_get";
	public static final String OPERATION_DBXDB_DOMESTICFUNDTRANSFERSREFDATA_GET = "dbxdb_domesticfundtransfersRefData_get";
	public static final String OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRER_GET = "dbxdb_internationalfundtransfers_get";
	public static final String OPERATION_DBXDB_INTERNATIONALFUNDTRANSFERSRERACCOUNT_GET = "dbxdb_internationalfundtransfersaccount_view_get";
	public static final String OPERATION_DBXDB_INTERBANKTRANSFER_VIEW_GET = "dbxdb_interbanktransfer_view_get";
	public static final String OPERATION_DBXDB_DOMESTICFUNDTRANSFERSRER_GET = "dbxdb_domesticfundtransfers_get";
	public static final String OPERATION_DBXDB_CONTRACTCUSTOMERS_GET = "dbxdb_contractcustomers_get";
	public static final String OPERATION_DBXDB_BACKENDIDENTIFIER_GET = "dbxdb_backendidentifier_get";
	public static final String SERVICE_PARTYMSSERVICE = "PartyMSService";
	public static final String OPERATION_GETPARTYDETAILS = "getPartyDetails";
	public static final String OPERATION_GETPARTYDETAILSBYID = "getPartyDetailsById";
	public static final String BRANCH_ID_REFERENCE = "BRANCH_ID_REFERENCE";
	public static final String BACKOFFICEIDENTIFIER = "BACKOFFICEIDENTIFIER";
	public static final String PARTYMS_AUTHORIZATION_KEY = "PARTYMS_AUTHORIZATION_KEY";
	public static final String SBG_BOP_RDS_INSTANCE_URL = "SBG_BOP_RDS_INSTANCE_URL";
	public static final String SBG_BOP_RDS_USERNAME = "SBG_BOP_RDS_USERNAME";
	public static final String SBG_BOP_RDS_PASSWORD = "SBG_BOP_RDS_PASSWORD";
	public static final String SBG_BOP_PACKAGE_NAME = "SBG_BOP_PACKAGE_NAME";
	public static final String SBG_BOP_CHANNEL_NAMES = "SBG_BOP_CHANNEL_NAMES";
	public static final String OPERATION_DBXDB_SBG_SWIFTCODE_GET = "dbxdb_sbg_swiftcode_get";
	public static final String OPERATION_DBXDB_SBG_BANKBRANCH_GET = "dbxdb_sbg_bankBranch_get";
	public static final String PING_CLIENT_ID = "PING_CLIENT_ID";
	public static final String PING_ISS_URL = "PING_ISS_URL";
	public static final String EVALUATIONRESULT = "EvaluationResult";
	public static final String BIC_TRANSFORMATION = "BIC_TRANSFORMATION";
	public static final int BIC_TRANSFORMATION_MIN_LENGTH = 8;
	public static final String URL_SUBMIT_PAYMENT = "/services/SBGPaymentProxy/submitPayment";
	public static final String DBX_HOST_URL = "DBX_HOST_URL";
	public static final String URL_SEPERATOR_COLON = ":";
	public static final String URL_SEPERATOR_FORWARD_SLASH = "/";
	public static final int INDEX_NOT_FOUND = -1;
	public static final String URL_BOP_JSON_SERVICE = "/services/BoPJsonService/";
	public static final String SBG_PARAM_CACHE_TIME = "SBG_PARAM_CACHE_TIME";

	public static final String OPERATION_DBXDB_SBG_PINGTOKEN_GET = "dbxdb_SbgPingTokens_get";
	public static final String OPERATION_DBXDB_SBG_PINGTOKEN_CREATE = "dbxdb_SbgPingTokens_create";
	public static final String OPERATION_DBXDB_SBG_PINGTOKEN_UPDATE = "dbxdb_SbgPingTokens_update";

	public static final String OPERATION_DBXDB_SBG_B2BPINGTOKEN_GET = "dbxdb_SbgB2BPingToken_get";
	public static final String OPERATION_DBXDB_SBG_B2BPINGTOKEN_CREATE = "dbxdb_SbgB2BPingToken_create";

	public static final String OPERATION_DBXDB_SBG_REFDATACACHE_GET = "dbxdb_SbgRefDataCache_get";
	public static final String OPERATION_DBXDB_SBG_REFDATACACHE_CREATE = "dbxdb_SbgRefDataCache_create";
	public static final String OPERATION_DBXDB_SBG_REFDATACACHE_UPDATE = "dbxdb_SbgRefDataCache_update";

	public static final String URL_PREBOOK_DEALS = "/services/PreBookedDeals/getPreBookedDeals";
	public static final String PREBOOK_CHANNEL_ID = "PREBOOK-CHANNEL-ID";

	public static final String SERVICE_DOCUMENT_GET = "/services/DocumentStorage/{schema_name}_documents_get";

	public static final String SERVICE_DOCUMENT_STORAGE = "DocumentStorage";
	public static final String OPERATION_DOCUMENT_WITHOUT_CONTENT = "dbxdb_all_documents_meta_data_get";
	public static final String OPERATION_SBT_DOCUMENT_UPDATE = "dbxdb_sbg_documents_update";
	public static final String OPERATION_SBT_DOCUMENT_GET = "dbxdb_sbg_documents_get";

	public static final String OPERATION_DOCUMENT_WITH_CONTENT = "dbxdb_documents_get";

	public static final String PING_AUTHORIZATION_CODE = "PING_AUTHORIZATION_CODE";
	public static final String PING_REDIRECT_URI = "PING_REDIRECT_URI";
	public static final String PING_OPEN_CLIENT_ID = "PING_OPEN_CLIENT_ID";
	public static final String PING_REFRESH_CODE = "PING_REFRESH_CODE";
	public static final String PING_CONTENT_TYPE = "PING_CONTENT_TYPE";
	public static final String PING_OPEN_CLIENT_SECRET = "PING_OPEN_CLIENT_SECRET";
	public static final String SERVICE_PAYMENT_RDB_SERVICE = "PaymentRDBServices";
	public static final String OPERATION_TRANSACTION_BY_TRANSACTIONID = "dbxdb_transferactivity_by_transactionId";
	public static final String OPERATION_AUDITDATA_BY_TRANSACTIONID = "dbxdb_auditdata_by_transactionId";

	public static final String MSAUTHORIZATION = "MSAUTHORIZATION";
	public static final String ARRANGEMENTMAXATTEMPTS = "ARRANGEMENTMAXATTEMPTS";

	public static final String OPERATION_SUBMITDOMESTICPAYMENT = "submitUrgentPayment";
	public static final String SERVICE_SBG_DOMESTIC_PAYMENT = "SbgDomesticUrgPaymentService";

	public static final String OPERATION_INTERBANKFUNDTRANSFERSREFDATA_GET = "dbxdb_interbankfundtransfersRefData_get";
	public static final String OPERATION_INTERBANKFUNDTRANSFERSREFDATA_CREATE = "dbxdb_interbankfundtransfersRefData_create";
	public static final String OPERATION_INTERBANKFUNDTRANSFERSREFDATA_UPDATE = "dbxdb_interbankfundtransfersRefData_update";
	public static final String OPERATION_DBXDB_INTERBANKFUNDTRANSFERSREFDATA_CREATE = "dbxdb_interbankfundtransfersRefData_create";
	public static final String OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRER_GET = "dbxdb_interbankfundtransfers_get";
	public static final String OPERATION_DBXDB_INTERBANKFUNDTRANSFERSRERACCOUNT_GET = "dbxdb_interbankfundtransfersaccount_view_get";

	public static final String SERVICE_DOCUMENT_JSON_SERVICE = "HCLDocumentService";
	public static final String OPERATION_DOCUMENT_MALWARE_SCAN = "scanDocumentMalware";

	public static final String OPERATION_GETPUBLIC_HOLIDAYS = "RefData-readPublicHolidays";

		public static final String ACCOUNT_DB_SERVICE = "AccountDbService";

	public static final String ACCOUNT_LIST_SORT_BY_USER = "dbxdb_recent_account_list_sort_by_user";
	public static final String SEND_EMAIL_WITH_ATTACHMENT_SERVICE_URL = "services/SBGCMS/SendEmailWithAttachment";
	public static final String SUBMIT_DOC_TO_FILENET = "/services/HCLDocumentService/submitDocToFileNet";

	public static final String DOM_X_IBM_CLIENT_ID = "DOM-X-IBM-CLIENT-ID";
	public static final String DOM_X_IBM_CLIENT_SECRET = "DOM-X-IBM-CLIENT-SECRET";
	public static final String DOM_SBG_HEADER_API_PRODUCT_CODE = "DOM_SBG_HEADER_API_PRODUCT_CODE";

	// filenet
	public static final String DBXDB_SBG_DOCUMENTS_CREATE = "dbxdb_sbg_documents_create";
	public static final String OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_CREATE = "dbxdb_interbankTransferStatus_create";
	public static final String OPERATION_DBXDB_INTERBANKTRANSFERSTATUS_GET = "dbxdb_interbankTransferStatus_get";

	public static final String SERVICES_SBGCMS_SEND_SMS = "/services/SBGCMS/SendSMS";
	public static final String SERVICES_SBGSERVICES_SEND_EMAIL = "/services/SBGServices/sendEmail";

	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_GET = "dbxdb_ownaccounttransfers_get";
	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRERACCOUNT_GET = "dbxdb_ownaccounttransfersaccount_view_get";
	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSRER_CREATE = "dbxdb_ownaccounttransfers_create";

	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_CREATE = "dbxdb_ownaccounttransfersRefData_create";
	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_GET = "dbxdb_ownaccounttransfersRefData_get";
	public static final String OPERATION_DBXDB_OWNACCOUNTFUNDTRANSFERSREFDATA_UPDATE = "dbxdb_ownaccounttransfersRefData_update";
	public static final String OPERATION_SUBMIT_IAT_PAYMENT = "submitIATPayment";

	public static final String IAT_PAYMENT_TYPE_DOM = "Domestic Transfer";
	public static final String IAT_PAYMENT_TYPE_FX = "International Transfer";

	public static final String IAT_DOM_PRODUCT_CODE = "InterAccountTransferBase";
	public static final String IAT_FX_PRODUCT_CODE = "InterAccountTransferFX";

	public static final String IAT_X_IBM_CLIENT_ID = "IAT-X-IBM-CLIENT-ID";
	public static final String IAT_X_IBM_CLIENT_SECRET = "IAT-X-IBM-CLIENT-SECRET";

	public static final String X_IBM_CLIENT_ID_KEY = "X-IBM-Client-Id";
	public static final String X_IBM_CLIENT_SECRET_KEY = "X-IBM-Client-Secret";
	public static final String X_FAPI_INTERACTION_ID_KEY = "x-fapi-interaction-id";

	public static final String OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_CREATE = "dbxdb_ownAccountTransferStatus_create";
	public static final String OPERATION_DBXDB_OWNACCOUNTTRANSFERSTATUS_GET = "dbxdb_ownAccountTransferStatus_get";

	public static final String ACCOUNT_SERVICE = "SbgAccountsService";
	public static final String ACCOUNT_GROUPCODE_SERVICES = "getAccountGroupCodes";
	public static final String ACCOUNT_STYLECODE_SERVICES = "getAccountStyles";

	public static final String SERVICES_GET_PROOF_OF_PAYMENT = "/services/SBGCRUD/dbxdb_get_proofOfPayment";

	public static final String SERVICE_CUSTOMER_MANAGEMENT = "CustomerManagement";
	public static final String OPERATION_GET_BASIC_INFO = "GetBasicInfo";
	public static final String SERVICE_CONTRACT_MANAGEMENT = "ContractManagement";
	public static final String OPERATION_GET_CONTRACT_DETAILS = "getContractDetails";
	public static final String OPERATION_CUSTOMER_UPDATE = "dbxdb_customer_update";
	public static final String SERVICE_CRUD_LAYER = "CRUDLayer";
	public static final String OPERATION_CUSTOMER_ACCEPTED_TERMS_AND_CONDITIONS_GET = "dbxdb_customer_accepted_terms_and_conditions_get";
	public static final String EXT_ACCOUNTS_GET = "/services/SBGCRUD/{schema_name}_externalaccount_get";
	public static final String EXT_ACCOUNTS_UPDATE = "/services/SBGCRUD/{schema_name}_externalaccount_update";

	public static final String SERVICE_INTERNATIONA_PAYEE = "SbgInternationalPayeeLOB";
	public static final String OPERATION_INTERNATIONA_PAYEE_EDIT = "SbgInternationalPayeeEdit";


}
