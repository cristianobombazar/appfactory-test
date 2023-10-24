package com.kony.sbg.javaservices;

import com.google.gson.Gson;
import com.kony.sbg.resources.impl.GeneratePaymentPdfServiceImpl;
import com.kony.sbg.util.SbgErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

public class TransferActivitiesPDFCreationService implements JavaService2 {

    private static final Logger LOG = Logger.getLogger(TransferActivitiesPDFCreationService.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {

        LOG.debug("#### Starting --> TransferActivitiesPDFCreationService");
        Result result = new Result();
        try {
//            Gson gson = new Gson();

//            LOG.debug("methodID :- "+methodID);
//            System.out.println("methodID :- "+methodID);
//
//            String inputArrayS = gson.toJson(inputArray);
//            LOG.debug("Input Array:- "+inputArrayS);
//            System.out.println("Input Array:- "+inputArrayS);
//
//            String dcRequestS = gson.toJson(dcRequest);
//            LOG.debug("dcRequestS :- "+dcRequestS);
//            System.out.println("dcRequestS :- "+dcRequestS);
//
//            String dcResponseS = gson.toJson(dcResponse);
//            LOG.debug("dcResponseS :- "+dcResponseS);
//            System.out.println("dcResponseS :- "+dcResponseS);

            GeneratePaymentPdfServiceImpl generatePaymentPdfServiceImpl = new GeneratePaymentPdfServiceImpl();
            result = generatePaymentPdfServiceImpl.getPaymentPDF(methodID, inputArray, dcRequest, dcResponse);

            String base64String = result.getParamByName("paymentReportPdf").getValue();
            LOG.debug("Base64String for generated PDF :- "+base64String);
            System.out.println("Base64String for generated PDF :- "+base64String);

        } catch (Exception exp) {
            LOG.debug("Exception occurred in RetrieveDocumentService: ", exp);
            LOG.error("Exception occurred in RetrieveDocumentService: " + exp.getStackTrace());
            result.addParam("opstatus", String.valueOf(SbgErrorCodeEnum.ERR_100033.getErrorCode()));
            result.addParam("errMsg", SbgErrorCodeEnum.ERR_100033.getMessage());
        }
        return result;
    }
}
