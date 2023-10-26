package com.kony.sbg.util;

import com.dbp.core.constants.DBPConstants;
import com.kony.dbputilities.util.ErrorConstants;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public enum SbgErrorCodeEnum {

    ERR_100001(100001, ErrorConstants.BLOCK_APPROVED_APPLICANT),
    ERR_100002(100002, ErrorConstants.APPROVALMATRIX_NOT_CONFIGURED),
    ERR_100003(100003, ErrorConstants.ERROR_WHILE_GENERATING_JWT_TOKEN),
	ERR_100004(100004, "payee type is invalid"),
	ERR_100005(100005, "error in java service"),
	ERR_100006(100006, "error in resource java service"),
	ERR_100007(100007, "error in fetch java service"),
	ERR_100008(100008, "error while searching swift details"),
	ERR_100009(100009, "Error occured while checking uniqueness of Benecode"),
	ERR_100010(100010, "Missing mandatory parameters"),
	ERR_100011(100011, "Error occured while fetching international payees"),
	ERR_100012(100012, "Error occured while fetching external accounts"),
	ERR_100013(100013, "Error occured while generating beneficiary details"),
	ERR_100014(100014, "Error while calling json service"),
	ERR_100015(100015,"Failed to fetch international payee"),
	ERR_100016(100016,"Failed to fetch allowed currencies"),
	ERR_100017(100017,"Failed to fetch indicative rates"),
	ERR_100018(100018,"Error occured while validating limits"),
	ERR_100019(100019,"Failed to fetch customer account details :"),
	ERR_100020(100020,"Failed to Accept Quote"),	
	ERR_100021(100021,"Failed to Reject Quote"),
	ERR_100022(100022,"Failed to fetch tradable accounts"),
	ERR_100023(100023,"Failed to Fetch Quote"),
	ERR_100024(100024,"Error occured while fetching IBM authorization token"),
	ERR_100025(100025,"Payment submission failed"),
	ERR_100026(100026,"Error occured while reading environment properties"),
	ERR_100027(100027,"Error occured while formatting date"),
	ERR_100028(100028,"Approval failed as the Value date has passed, please decline this payment and recapture.\r\n" + 
			"If you have used Foreign Exchange then please contact your Foreign Exchange dealer directly or call Client Services on 08000 36 739 to manage your Foreign Exchange trade/s."),
	ERR_100029(100029,"Error occured while processing payment feedback request"),
	ERR_100030(100030,"Error occurred while parsing the XML"),
	ERR_100031(100031,"Error occurred while decoding base64"),
	ERR_100032(100032,"Requested Payment Reference Id not found"),
	ERR_100033(100033,"An unknown error occurred while processing the request"),
	ERR_100034(100034,"Error occurred while validating authentication of the request"),
	ERR_100035(100035,"Error occurred while updating transfer status"),
	ERR_100036(100036,"Error occurred while fetching contract customer details"),
	ERR_100037(100037,"Error occurred while fetching party microservice details"),
	ERR_100038(100038,"You have exceeded your limit. Please increase the limit to proceed with this payment."),
	ERR_100039(100039,"You have exceeded your weekly limit. Please increase the limit to proceed with this payment."),
	ERR_100040(100040,"You have exceeded your daily limit. Please increase the limit to proceed with this payment."),
	ERR_100041(100041,"The system encountered an error during limit verification. Contact your Service Manager."),
	ERR_100042(100042,"You have exceeded your role limit per transaction. Please increase the limit to proceed with this payment."),
	ERR_100043(100043,"You have exceeded your weekly role limit. Please increase the limit to proceed with this payment."),
	ERR_100044(100044,"You have exceeded your daily role limit. Please increase the limit to proceed with this payment."),
	ERR_100045(100045,"The system encountered an error during the role limit verification. Contact your Service Manager."),
	ERR_100046(100046,"You have exceeded your transaction limit. Please increase the limit to proceed with this payment."),
	ERR_100047(100047,"You have exceeded your weekly limit. Please increase the limit to proceed with this payment."),
	ERR_100048(100048,"You have exceeded your daily limit. Please increase the limit to proceed with this payment."),
	ERR_100049(100049,"The system encountered an error during limit verification. Contact you Service Manager."),
	ERR_100050(100050,"You have exceeded your account limit for the payment amount. Please increase the limit to proceed with this payment."),
	ERR_100051(100051,"You have exceeded your weekly account limit. Please increase the limit to proceed with this payment."),
	ERR_100052(100052,"You have exceeded your daily account limit. Please increase the limit to proceed with this payment."),	
	ERR_100053(100053,"Due to a technical error, your request could not be processed. Please try again later or contact your Service Manager"),
	ERR_100054(100054,"An unknown error occurred while processing the submit payment request"),
	ERR_100055(100055,"The cut-off time has been breached. Please amend your payment."),
	ERR_100056(100056,"An unknown error occurred while processing the transaction evaluation"),
	ERR_100057(100057,"An unknown error occurred while processing the evaluating parameters"),
	ERR_100058(100058,"An unknown error occurred while processing the evaluating transaction objects"),
	ERR_100059(100059,"We encountered a technical error while determining whether \r\n"
			+ "your payment is reportable. Please try again. If the problem \r\n"
			+ "persists, please contact your service team"),
	ERR_100060(100060,"An unknown error occurred while processing the request"),
	ERR_100061(100061,"An unknown error occurred while performing BoP Form validation"),
	ERR_100062(100062,"Invalid or incomplete Balance Of Payments (BOP) information.\r\n" + 
			"To proceed, kindly correct the regulatory report."),
	ERR_100063(100063,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 0"),
	ERR_100064(100064,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 1"),
	ERR_100065(100065,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 2"),
	ERR_100066(100066,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 3"),
	ERR_100067(100067,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 7"),
	ERR_100068(100068,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 9"),
	ERR_100069(100069,"Your request could not be processed as pricing has been stopped by Global Markets.Please contact your Service Manager and provide Error Code 10."),
	ERR_100070(100070,"Future-dated transactions are not permitted for your profile.Please contact your Service Manager and provide Error Code 12."),
	ERR_100071(100071,"FX trades are not permitted for your profile.Please contact your Service Manager and provide Error Code 16."),
	ERR_100072(100072,"FX trades are not permitted for your profile.Please contact your Service Manager and provide Error Code 19."),
	ERR_100073(100073,"Quote requests are not permitted for your profile.Please try the pre-booked option or contact your Service Manager and provide Error Code 29."),
	ERR_100074(100074,"Your request could not be processed. Please try again later or contact your Service Manager."),
	ERR_100075(100075,"Due to a technical error, your request could not be processed. Please try again later or contact your Service Manager and provide Error Code 0."),
	ERR_100076(100076,"Due to a technical error, your request could not be processed. Please try again later or contact your Service Manager and provide Error Code 1."),
	ERR_100077(100077,"Due to a system time-out, we are unable to process your request. Please try again later or contact your Service Manager and provide Error Code 3."),
	ERR_100078(100078,"We are unable to process your request. Please try again later or contact your bank representative and provide error code 7."),
	ERR_100079(100079,"FX trades are not permitted for your profile. Please contact your Service Manager and provide Error Code 19."),
	ERR_100080(100080,"Your request could not be processed. Please try again later or contact your Service Manager and provide Error Code"),

	ERR_100081(100081, "Error occurred while generating Payment details"),
	ERR_100082(100082,"Due to a technical error, your request could not be processed.Please try again later or contact your Service Manager and provide Error Code 17"),
	ERR_100083(100083, "Error occurred while Fecthing Extension Data"),

	ERR_100084(100084, "Failed to fetch public holidays"),
	ERR_100085(100085, "Failed to fetch public holidays"), //This one is for debugging - it will be removed | Mashilo Joseph
	ERR_100086(100086, "SECURITY ERROR - NOT AUTHORIZED"),

	ERR_100087(100087, "We are unable to process your request. Please try again later or contact your Client Access Representative and provide Error Code 7."),
	ERR_100088(100086, "No Foreign Exchange deals were found for the captured payment. Please contact your Service Manager and provide Error Code 31."),
	ERR_100089(100086, "An internal technical error exists while processing a request."),
	ERR_100090(100086, "No Foreign Exchange deals were found for the captured payment. Please contact your Service Manager and provide Error Code 39.");
    public static final String ERROR_CODE_KEY = DBPConstants.DBP_ERROR_CODE_KEY;
    public static final String ERROR_MESSAGE_KEY = DBPConstants.DBP_ERROR_MESSAGE_KEY;
    public static final String OPSTATUS_CODE = DBPConstants.FABRIC_OPSTATUS_KEY;
    public static final String HTTPSTATUS_CODE = DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY;
    public static final String ERROR_DETAILS = "errorDetails";
    
    private int errorCode;
    private String message;

    private SbgErrorCodeEnum(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCodeAsString() {
        return String.valueOf(errorCode);
    }

    public Result setErrorCode(Result result) {
        if (result == null) {
            result = new Result();
        }

        result.addParam(new Param(ERROR_CODE_KEY, this.getErrorCodeAsString(), MWConstants.INT));
        result.addParam(new Param(ERROR_MESSAGE_KEY, this.getMessage(), MWConstants.STRING));
        result.addParam(new Param(OPSTATUS_CODE, "0", MWConstants.INT));
        result.addParam(new Param(HTTPSTATUS_CODE, "0", MWConstants.INT));

        return result;
    }
    //if we need to modify error message corresponding existing error code then need to send new message as input.
    public Result setErrorMessage(Result result,String message) {
        if (result == null) {
            result = new Result();
        }

        result.addParam(new Param(ERROR_CODE_KEY, this.getErrorCodeAsString(), MWConstants.INT));
        result.addParam(new Param(ERROR_MESSAGE_KEY, message, MWConstants.STRING));
        result.addParam(new Param(OPSTATUS_CODE, "0", MWConstants.INT));
        result.addParam(new Param(HTTPSTATUS_CODE, "0", MWConstants.INT));

        return result;
    }
}
