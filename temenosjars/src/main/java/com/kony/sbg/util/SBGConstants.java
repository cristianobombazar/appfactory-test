package com.kony.sbg.util;

public class SBGConstants {
	public static final String IBAN = "IBAN";
	public static final String EMPTY_STRING = "";
	public static final String SPACE_STRING = " ";
	public static final String FULL_STOP_STRING = ".";
	public static final String SMALL_IBAN = "iban";
	public static final String DB_SERVICE_NAME = "SBGCRUDLayer";
	public static final String DB_OPERATION_NAME = "dbxdb_externalaccount_create";
	public static final String ID = "Id";
	public static final String CONST_ID = "id";
	public static final String USER_ID = "userId";
	public static final String COMPANYID = "companyId";
	public static final String ISBUSINESS = "isBusinessPayee";
	public static final String REFDATA_SERVICESDOWN = "The system has encountered an error. Please try the payment again or contact Standard Bank Payment Support on 011223345.";
	public static final String REFDATA_ORCHESTRATION = "RefDataOrchestration";
	public static final String REFDATA_ORCH_OPERATION = "RefDataOrchOperation";

	public static final String REFDATA_ORCHSEQ 		= "RefDataOrchSeq";
	public static final String REFDATA_ORCHSEQOPR 	= "RefDataOrchSeqOpr";
	public static final String REFDATA_PUBLIC_HOLIDAY_ERROR_MESSAGE = "The selected value date falls on a public holiday. Please select the next business day as your value date."; //SBG-6672 old message: "The value date is invalid because it is a public holiday. You must select the Value Date for this Instruction to the next permissible Business Day.";
	public static final String REFDATA_CURRENCY_HOLIDAY_ERROR_MESSAGE = "The selected value date is a currency holiday. Please select the next business day as your value date."; //SBG-6672 old message: The value date is invalid because it is a currency holiday. You must change the value date for this instruction to the next permissible business day.
	public static final String REFDATA_WEEKEND_ERROR_MESSAGE = "The selected value date falls on a non-business day. Please select the next business day as your value date."; //SBG-6672 old message: "The Value Date is invalid because it is a non-Business Day and will lead to payment failure. You must move the Value Date to the next Business Day.";
	public static final String REFDATA_CUTOFF_TIME_ERROR_MESSAGE = "The cut-off time has been breached. Please select the next business day as your value date."; //SBG-6672 old message: "The cut-off time for this Value Date has passed. You must change the Value Date for this Instruction to the next permissible Business Day.";
	public static final String REFDATA_LEAD_DAYS_ERROR_MESSAGE = "The selected value date is invalid for %CURRENCY_CODE%. Please select the next business day as your value date."; //SBG-6672 old message: "The Value date is invalid for currency %CURRENCY_CODE%, you can choose the next business date as the value date or any valid future date.";
	public static final String REFDATA_SCHEDULED_DATE_ERROR_MESSAGE = "Invalid scheduled date format.";
	public static final String REFDATA_EXTENSION_DATE_ERROR_MESSAGE = "Error occurred while fetching extension data";
	public static final String REFDATA_CURRENCY_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE = "The system has encountered an error. Please try the payment again or contact Standard Bank Payment Support on 011223345.";
	public static final String REFDATA_PUBLIC_HOLIDAY_SERVICE_FAILED_ERROR_MESSAGE = "The system has encountered an error. Please try the payment again or contact Standard Bank Payment Support on 011223345.";
	public static final String REFDATA_WEEKEND_DAYS_SERVICE_FAILED_ERROR_MESSAGE = "The system has encountered an error. Please try the payment again or contact Standard Bank Payment Support on 011223345.";
	public static final String REFDATA_CUTOFF_TIME_SERVICE_FAILED_ERROR_MESSAGE = "The system has encountered an error. Please try the payment again or contact Standard Bank Payment Support on 011223345.";
	public static final String REFDATA_WEEK_OFF = "WEEKLY OFF"; 
	public static final String REFDATA_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DAY_FORMAT = "EEEE";
	public static final String SUCCESS = "Success";
	public static final String FAILED = "Failed";
	public static final String IS_VALID = "isValid";
	public static final String MESSAGE = "message";
	public static final String STATUS = "status";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final byte BIC_COUNTRY_CODE_START_INDEX = 5;
	public static final byte BIC_COUNTRY_CODE_END_INDEX = 6;
	public static final String REFDATA_PUBLIC_HOLIDAYS_VALIDATION_SUCCESS_MESSAGE = "Public holidays Validation successful.";
	public static final String REFDATA_NON_BUSINESS_DAYS_VALIDATION_SUCCESS_MESSAGE = "Non Business days Validation successful.";
	public static final String REFDATA_CURRENCY_HOLIDAYS_VALIDATION_SUCCESS_MESSAGE = "Currency holidays Validation successful.";
	public static final String REFDATA_CUTOFF_TIME_VALIDATION_SUCCESS_MESSAGE = "Cut-off time Validation successful.";
	public static final String EVENT_TYPE = "FX_MANAGEMENT";//RFQ
	public static final String ACCEPT_EVENT_SUBTYPE = "RFQ_DEAL_ACCEPT";
	public static final String REJECT_EVENT_SUBTYPE = "RFQ_DEAL_REJECT";
	public static final String ACCEPT_PRODUCER = "RFQObjects/PUT(AcceptQuote)";
	public static final String REJECT_PRODUCER = "RFQObjects/DELETE(RejectQuote)";	
	public static final String BOP_EVENT_TYPE = "BOP";
	public static final String BOP_EVENT_SUBTYPE = "BOP_EVALUATION_RESULT";
	public static final String BOP_RESULT_PRODUCER = "BOPObjects/evaluate";	
	public static final String TRANSACTION_STATUS_SENT_MESSAGE = "Payment succesfully submitted.";	
	public static final String TRANSACTION_STATUS_SENT_MESSAGE_OWN_ACC = "Transfer succesfully submitted.";
	public static final String TRANSACTION_STATUS_PENDING_MESSAGE = "Success. Your payment needs approval.";
	public static final String TRANSACTION_STATUS_PENDING_MESSAGE_OWN_ACC = "Success. Your transfer needs approval.";
	public static final String INDICATIVE_RATES = "INDICATIVE_RATES";
	public static final String AUTHORIZATION = "Authorization";
	public static final String AUTHORIZATION_EXP = "AuthorizationEXP";
	public static final String BEARER = "Bearer";

	public static final String BENE_EVENT_TYPE = "INTERNATIONAL_PAYEE";//RFQ
	public static final String BENE_ADD_SUBTYPE = "BENE_ADDED";
	public static final String BENE_DELETE_SUBTYPE = "BENE_DELETED";
	public static final String BENE_ADD_PRODUCER = "PayeeManagement/operations/Payees/createPayee";
	public static final String BENE_DELETE_PRODUCER = "PayeeManagement/operations/Payees/deletePayee";
	
	public static final String USER_INFO_OPENID = "/services/SbgPingAuthJson/getUserInfoOpenID";
	public static final String DOCUMENT_INFO_WITHOUT_DOC = "/services/DocumentStorage/dbxdb_all_documents_meta_data_get";
	public static final String DOCUMENT_INFO_WITH_DOC = "/services/DocumentStorage/dbxdb_documents_get";
	
	public static final String PROP_B2BPINGTOKEN_SAVEINDB	= "B2BPINGTOKEN_SAVEINDB";

	//Operations
	public static final String DB_DOMESTICFUNDTRANSFERS_CREATE = "dbxdb_domesticfundtransfers_create";
	public static final String DB_DOMESTICFUNDTRANSFERS_UPDATE = "DB_DOMESTICFUNDTRANSFERS_UPDATE";
	public static final String DB_DOMESTICFUNDTRANSFERS_GET = "DB_DOMESTICFUNDTRANSFERS_GET";
	public static final String DB_DOMESTICFUNDTRANSFERS_DELETE = "DB_DOMESTICFUNDTRANSFERS_DELETE";
	public static final String DOMESTIC_ACC_FUND_TRANSFER_BACKEND = "DOMESTIC_ACC_FUND_TRANSFER_BACKEND";
	public static final String DOMESTIC_ACC_FUND_TRANSFER_BACKEND_EDIT = "DOMESTIC_ACC_FUND_TRANSFER_BACKEND_EDIT";
	public static final String DOMESTIC_ACC_FUND_TRANSFER_BACKEND_DELETE = "DOMESTIC_ACC_FUND_TRANSFER_BACKEND_DELETE";
	public static final String DOMESTIC_ACC_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE = "DOMESTIC_ACC_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE";


	//Services
	public static final String DOMESTIC_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE = "DOMESTIC_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE";

	//featureActions
	public static final String INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE = "INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE";
	public static final String DOMESTIC_PAYMENT = "Domestic payment";
	public static final String INTERNATIONAL_PAYMENT = "International payment";
	public static final String IATFX_PAYMENT = "Foreign currency inter-account transfer";
	public static final String IAT_PAYMENT = "Domestic currency inter-account transfer";

	public static final String COMPANYPARTYID	= "COMPANYPARTYID";
	
	//----> Integer constants
	public static final long 	MILLIS_IN_A_DAY 	= 1000 * 60 * 60 * 24;
	public static final int		SECONDS_IN_A_DAY 	= 60 * 60 * 24;

	public static final String URGENT_PAYMENT_TYPE = "Urgent";
	public static final String NORMAL_PAYMENT_TYPE = "Normal";

	public static final String URGENT_PRODUCT_CODE = "URG";
	public static final String NORMAL_PRODUCT_CODE = "NOR";
	public static final String PENDINGAPPROVAL = "Pending approval";
	public static final String TRANSACTION_STATUS_PENDING_MESSAGE_DOM = "Payment succesfully submitted for approval.";

	public static final String SAVE_PAYEE = "Save Payee";
	public static final String SAVE_PAYEE_MSG = "Beneficiary has been successfully saved.";

	public static final String IATFX_PRODUCT_CODE = "XIAT";
	public static final String IAT_PRODUCT_CODE = "IAT";
	public static final String EXCON_APPROVAL = "EXCON_APPROVAL";
}
