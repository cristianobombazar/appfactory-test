package com.kony.sbg.postprocessor;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountStylesFilterPostProcessor implements DataPostProcessor2 {

    private static final Logger logger = LogManager.getLogger(AccountStylesFilterPostProcessor.class);

    @Override
    public Object execute( Result result, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {

        try {

            Dataset newDataset = new Dataset("AccountStyles");
            Result resultNewResponse=new Result();
            List<Record> recordList=new ArrayList<>();

            String code = dataControllerRequest.getParameter("code");

            Dataset dataset = result.getDatasetById("AccountStyles");

            if(org.apache.commons.lang3.StringUtils.isNotBlank(code)) {

                if (dataset != null && !dataset.getAllRecords().isEmpty()) {

                    for (Record record : dataset.getAllRecords()) {
                        String mockRecodeCode = record.getParamValueByName("code");
                        if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(mockRecodeCode) && code.equalsIgnoreCase(mockRecodeCode)) {
                            recordList.add(record);
                        }
                    }

                    newDataset.addAllRecords(recordList);
                    resultNewResponse.addDataset(newDataset);
                }
            }else {
                resultNewResponse = result;
            }
            logger.debug("#################### AccountStylesFilterPostProcessor : jsonObj: " + ResultToJSON.convert(resultNewResponse));
            return resultNewResponse;

        } catch (JSONException exception) {
            logger.error("JSONException occured in AccountStylesFilterPostProcessor: ", exception);
            return result;
        } catch (Exception exception) {
            logger.error("General Exception occured in AccountStylesFilterPostProcessor: ", exception);
            return result;
        }
    }
}
