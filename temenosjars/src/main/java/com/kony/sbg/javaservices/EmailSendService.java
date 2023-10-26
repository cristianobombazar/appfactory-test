package com.kony.sbg.javaservices;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SbgURLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EmailSendService implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(EmailSendService.class);

    @Override
    public Object invoke(String arg0, Object[] arg1, DataControllerRequest dcRequest, DataControllerResponse res)
            throws Exception {
        Result result = new Result();
        boolean finalStatus = true;

        try {

            String toEmail = dcRequest.getParameter("to");
            String attachment = dcRequest.getParameter("attachment");


            if (SBGCommonUtils.isStringNotEmpty(toEmail) && SBGCommonUtils.isStringNotEmpty(attachment)) {
                Map<String, Object> inputparams = new HashMap<>();
                inputparams.put("senderAddress", "bolp@standardbank.co.za");
                inputparams.put("content", "Payment Notification");
                inputparams.put("message", "Please see the attached proof of payment document");
//                inputparams.put("destinationAddress", "uditha88@gmail.com");
                inputparams.put("destinationAddress", toEmail);
//                inputparams.put("attachment", "^%^%TYT*&^");
                inputparams.put("attachment", attachment);
                inputparams.put("attachmentType", "pdf");


                Result response = HelperMethods.callApi(dcRequest, inputparams, HelperMethods.getHeaders(dcRequest),
                        SbgURLConstants.SEND_EMAIL_WITH_ATTACHMENT_SERVICE_URL);

            } else {
                ErrorCodeEnum.ERR_10063.setErrorCode(result, "To-Email & Attachment needed");
                finalStatus = false;
            }
        } catch (Exception e) {
            ErrorCodeEnum.ERR_10063.setErrorCode(result, "Email sending Exception. " + e.getMessage());
            finalStatus = false;
        }
        result.addParam(new Param("status", Boolean.toString(finalStatus)));
        return result;
    }

}
