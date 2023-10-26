package com.kony.sbg.postprocessor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;

public class PreBookPostProcessor implements DataPostProcessor2{
	private static final Logger LOG = Logger.getLogger(PreBookPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		//LOG.debug("###PreBookAPIPostprocessor==execute()==result"+result);
		Result res=new Result();
		Dataset preBookDeals=new Dataset("preBookDeals");
		if(result!=null) {
		Dataset fxTrades=result.getDatasetById("fxTrades");
		List<Record> fxTradesRecords=fxTrades.getAllRecords();
		//LOG.debug("###PreBookAPIPostprocessor==fxTradesRecords"+fxTradesRecords.toString());
	  for(int i=0;i<fxTradesRecords.size();i++) {
		Record DealsRecord=new Record();
		Record tradeReferenceDetails=fxTradesRecords.get(i).getRecordById("tradeReferenceDetails");		
		DealsRecord.addStringParam("fxCoverNumber", tradeReferenceDetails.getParamValueByName("fxCoverNumber"));
		Record partyDetails=fxTradesRecords.get(i).getRecordById("partyDetails");
			DealsRecord.addStringParam("accountName", partyDetails.getParamValueByName("accountName"));
		Record fxTransactionDetail=fxTradesRecords.get(i).getRecordById("fxTransactionDetail");
		DealsRecord.addStringParam("currencyPair", fxTransactionDetail.getParamValueByName("currencyPair"));
		DealsRecord.addStringParam("dealtCurrency", fxTransactionDetail.getParamValueByName("dealtCurrency"));
		DealsRecord.addStringParam("tradeDate", fxTransactionDetail.getParamValueByName("tradeDate"));
		DealsRecord.addStringParam("tradeType", fxTransactionDetail.getParamValueByName("tradeType"));
		DealsRecord.addStringParam("currencyPair", fxTransactionDetail.getParamValueByName("currencyPair"));
		DealsRecord.addStringParam("currencyPair", fxTransactionDetail.getParamValueByName("currencyPair"));

		LOG.debug("###PreBookAPIPostprocessor==fxTransactionDetail"+fxTransactionDetail.toString());
		Dataset fxTransactionLegs=fxTransactionDetail.getDatasetById("fxTransactionLegs");
		List<Record> fxTransactionLegsRecords=fxTransactionLegs.getAllRecords();
		DealsRecord.addStringParam("valueDate",fxTransactionLegsRecords.get(0).getParamValueByName("valueDate"));
		DealsRecord.addStringParam("dealtAmount",fxTransactionLegsRecords.get(0).getParamValueByName("dealtAmount"));
		
		if(StringUtils.isNotBlank(fxTransactionLegsRecords.get(0).getParamValueByName("optionalDate"))) {
		DealsRecord.addStringParam("optionalDate",fxTransactionLegsRecords.get(0).getParamValueByName("optionalDate"));
			}else {
			DealsRecord.addStringParam("optionalDate","");
				
		}
		DealsRecord.addStringParam("fxLegCoverNumber",fxTransactionLegsRecords.get(0).getParamValueByName("fxLegCoverNumber"));
		Record exchangeRate=fxTransactionLegs.getRecord(0).getRecordById("exchangeRate");
		
		DealsRecord.addStringParam("allInRate",exchangeRate.getParamValueByName("allInRate"));
		DealsRecord.addStringParam("spotRate",exchangeRate.getParamValueByName("spotRate"));
		
		Record fxTradeManagementDetail=fxTransactionLegs.getRecord(0).getRecordById("fxTradeManagementDetail");
		DealsRecord.addStringParam("availableBalanceInReceiverCurrency",fxTradeManagementDetail.getParamValueByName("availableBalanceInReceiverCurrency"));
		preBookDeals.addRecord(DealsRecord);
	   }
		
	res.addStringParam("disclaimer",result.getParamValueByName("disclaimer"));
	//added date and time for audit logs
	res.addDataset(preBookDeals);
		return res;
		}else {
			return result;
		}
		
	}
}
