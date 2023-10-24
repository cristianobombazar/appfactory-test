package com.kony.sbg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.sbg.resources.impl.BeneficiaryPaymentPdfGeneratorServiceImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SbgAlertUtil {

    private static final Logger LOG = Logger.getLogger(SbgAlertUtil.class);

    private static List getPopEmailOrPhoneList(String proofOfPayment, Enum popType) {
//        LOG.debug(" start getPopValue");
        List values = new ArrayList();
        try {
            if (SBGCommonUtils.isStringNotEmpty(proofOfPayment)) {
                JSONArray list = new JSONArray(proofOfPayment.replace("\\", ""));
                if (null != list) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject pop = list.getJSONObject(i);
                        if (null != pop) {
                            String type = pop.getString("popType");
                            if (null != type) {
                                if (popType.toString().equalsIgnoreCase(type)) {
                                    values.add(pop.getString("popValue"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("SbgAlertUtil getPopValue error " + e.getMessage());
//            LOG.debug(" getPopValue error = " + e.getMessage());
        }
        return values;
    }

    enum PopType {
        email,
        sms
    }


    public static void sendPopSMS(DataControllerRequest request, String proofOfPayment, PopSmsDataObject data) {
//        LOG.debug(" start sendSMS");
        List listPhoneNo = getPopEmailOrPhoneList(proofOfPayment, PopType.sms);

        listPhoneNo.forEach(
                phoneNo -> {
                    try {
                        if (SBGCommonUtils.isStringNotEmpty(phoneNo)) {
                            StringBuffer messageSms = new StringBuffer();
                            String msg = getValue(data.getMessage());
                            String status = getValue(data.getStatus());
                            messageSms.append(msg);
                            messageSms.append(" Status: " + status);

                            Map inputParams = new HashMap();
                            inputParams.put("content", "\"" + messageSms.toString() + "\"");
                            inputParams.put("value", phoneNo);

                            Result res = HelperMethods.callApi(request, inputParams, HelperMethods.getHeaders(request),
                                    SbgURLConstants.SERVICES_SBGCMS_SEND_SMS);
//                            LOG.debug("sms = " + inputParams.get("content") + " | phone "+ phoneNo + "errorcode = " + res.getParamValueByName("sbgerrcode") + " | errMsg = " + res.getParamValueByName("sbgerrmsg"));
                        }
                    } catch (Exception e) {
//                        LOG.debug(" PoP SMS sending error : " + e.getMessage());
                        LOG.error("PoP SMS sending error : " + e.getMessage());
                    }
                }
        );
    }

    static class PopSmsDataObject {
        String message, status;

        public PopSmsDataObject() {
        }

        public PopSmsDataObject(String message, String status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static PopSmsDataObject getPopSmsDataObjectPaymentSubmit(Result result) {
        PopSmsDataObject data = new PopSmsDataObject();
        try {
            String status = result.getParamValueByName("status");
            data.setStatus(status);


            String amount = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("amount"), SBGConstants.EMPTY_STRING);
            String currency = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("transactionCurrency"), SBGConstants.EMPTY_STRING);
            String fromAccountNumber = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("fromAccountNumber"), SBGConstants.EMPTY_STRING);

            String fromName = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("fromAccountNumber"), SBGConstants.EMPTY_STRING);
            String toAccount = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("toAccountNumber"), SBGConstants.EMPTY_STRING);
            String dateTime = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("scheduledDate"), SBGConstants.EMPTY_STRING);
            String reference = SBGCommonUtils.getValueOrGivenValue(result.getParamValueByName("statementReference"), SBGConstants.EMPTY_STRING);

            StringBuffer message = new StringBuffer("Standard Bank: Dear Client, payment made from ");

            message.append(fromName);
            message.append(" to account ");
            message.append(toAccount);
            message.append(" on ");
            message.append(dateTime);
            message.append(" Amount ");
            message.append(currency + amount);
            message.append(". " + reference);

            data.setMessage(message.toString());

        } catch (Exception e) {
            LOG.error("getSmsData error " + e.getMessage());
//            LOG.debug(" getSmsData error " + e.getMessage());

            return data;
        }

        return data;
    }

    public static PopSmsDataObject getPopSmsDataObject(String status, String message) {
        PopSmsDataObject popSmsDataObject = new PopSmsDataObject();
        popSmsDataObject.setStatus(status);
        popSmsDataObject.setMessage(message);
        return popSmsDataObject;
    }

    private static String getValue(String value) {
        if (SBGCommonUtils.isStringNotEmpty(value)) {
            return value;
        } else {
            return "-";
        }
    }

    public static void sendPopEmail(DataControllerRequest request, String proofOfPayment, Result transactionResult) {
//        LOG.debug(" start sendEmail");
        List listEmail = getPopEmailOrPhoneList(proofOfPayment, PopType.email);

        listEmail.forEach(
                email -> {
                    try {
                        if (SBGCommonUtils.isStringNotEmpty(email)) {
                            BeneficiaryPaymentPdfGeneratorServiceImpl beneficiaryPaymentPdfGeneratorService = new BeneficiaryPaymentPdfGeneratorServiceImpl();
                            byte[] bytes = null;
                            ObjectMapper mapper = new ObjectMapper();
                            Map resultMap = new HashMap();
                            transactionResult.getAllParams().forEach(
                                    p -> {
                                        resultMap.put(p.getName(), p.getValue());
                                    });
                            String json = mapper.writeValueAsString(resultMap);

                            bytes = beneficiaryPaymentPdfGeneratorService.printPDFForBeneficiary(json);
                            String base64StringPDF;
                            if (null != bytes) {
                                base64StringPDF = Base64.getEncoder().encodeToString(bytes);
                                Map inputParams = new HashMap();
                                inputParams.put("attachment", base64StringPDF);
                                inputParams.put("to", email);
                                Result res = HelperMethods.callApi(request, inputParams, HelperMethods.getHeaders(request),
                                        SbgURLConstants.SERVICES_SBGSERVICES_SEND_EMAIL);
//                                LOG.debug("base64"+ base64StringPDF +" status = " + res.getParamValueByName("status") + " | errorcode = " + res.getParamValueByName("dbpErrCode") + " | errMsg = " + res.getParamValueByName("dbpErrMsg"));
                            }

                        }
                    } catch (Exception e) {
                        LOG.error("PoP Pdf generation Email sending error " + e.getMessage());
//                        LOG.debug(" PoP Pdf generation Email sending error " + e.getMessage());
                    }
                }
        );
    }

    public static String getProofOfPayment(String transactionId, DataControllerRequest dcRequest) {
        String proofOfPayment = SBGConstants.EMPTY_STRING;
        try {
            HashMap<String, Object> requestHeaders = new HashMap<>();
            Map<String, Object> inputparams = new HashMap<>();
            inputparams.put("transaction_id", transactionId);

            Result response = HelperMethods.callApi(dcRequest, inputparams, HelperMethods.getHeaders(dcRequest),
                    SbgURLConstants.SERVICES_GET_PROOF_OF_PAYMENT);
            if (null != response) {
                Dataset records = response.getDatasetById("records");
                if (null != records) {
                    List<String> popValueList = new ArrayList<>();
                    records.getAllRecords().forEach(
                            r -> {
                                String pop = r.getParamValueByName("proofOfPayment");
                                if (SBGCommonUtils.isStringNotEmpty(pop)) {
                                    popValueList.add(pop);

                                }
                            }
                    );

                    if (!popValueList.isEmpty()) {
                        return popValueList.get(0);
                    }

                }
            }

        } catch (Exception e) {
            LOG.error("Enter to Exception of getProofOfPayment:", e);
        }
        return proofOfPayment;
    }
}

