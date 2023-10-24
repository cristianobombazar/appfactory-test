package com.kony.sbg.javaservices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kony.sbg.sideloading.utils.CommonUtils;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.log4j.Logger;

import java.util.Base64;


public class VolPayPain001IDService implements JavaService2 {

    private static final Logger LOGGER = Logger.getLogger(VolPayPain001IDService.class);
    public static final String CDT_TRF_TX_INF_THE_END_TAG = "</CdtTrfTxInf>";

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();

        boolean status = true;
        try {
            String xmlParam = request.getParameter("xml");
            String idsParam = request.getParameter("ids");

            if (SBGCommonUtils.isStringNotEmpty(xmlParam) && SBGCommonUtils.isStringNotEmpty(idsParam)) {
                String xmlWithIDs = getXmlWithIDs(xmlParam, idsParam);

                result.addParam("XMLWithIDs", xmlWithIDs);

                LOGGER.info("VolPay Pain001 XML file = " + xmlWithIDs.toString());
            }
        } catch (Exception e) {
            LOGGER.error("VolPay Pain001 XML generation failed : " + e.getMessage());
            status = false;
            result.addParam("error", "VolPay Pain001 XML generation failed : " + e.getMessage());

        }
        result.addParam("status", Boolean.toString(status));

        return result;
    }

    private static String getXmlWithIDs(String xml, String ids) {

        StringBuffer idNode = new StringBuffer();

        idNode.append("<SplmtryData>");
        idNode.append("<PlcAndNm>DocumentId</PlcAndNm>");
        idNode.append("<Envlp>");
        idNode.append("<any>");
        idNode.append(ids);
        idNode.append("</any>");
        idNode.append("</Envlp>");
        idNode.append("</SplmtryData>");
        idNode.append(CDT_TRF_TX_INF_THE_END_TAG);

        String xmlDecoded = new String(Base64.getDecoder().decode(xml)).replace("\n", "").replace("\r", "");
        String finalXml = xmlDecoded.replace(CDT_TRF_TX_INF_THE_END_TAG, idNode.toString());
        return finalXml;

    }
}
