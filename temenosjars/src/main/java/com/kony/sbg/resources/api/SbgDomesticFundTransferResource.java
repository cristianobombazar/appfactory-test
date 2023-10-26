package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;import com.konylabs.middleware.controller.DataControllerRequest;import com.konylabs.middleware.controller.DataControllerResponse;import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.transactionservices.resource.api.InterBankFundTransferResource;

public interface SbgDomesticFundTransferResource extends InterBankFundTransferResource, Resource {
    Result createTransaction(String var1, Object[] var2, DataControllerRequest var3, DataControllerResponse var4);

    Result updateStatus(String var1, Object[] var2, DataControllerRequest var3, DataControllerResponse var4);

    Result processResponseFromLineOfBusiness(Result var1, DataControllerRequest var2, DataControllerResponse var3);

    Result editTransaction(String var1, Object[] var2, DataControllerRequest var3, DataControllerResponse var4);

    Result cancelScheduledTransactionOccurrence(String var1, Object[] var2, DataControllerRequest var3,
            DataControllerResponse var4);

    Result deleteTransaction(String var1, Object[] var2, DataControllerRequest var3, DataControllerResponse var4);
}
