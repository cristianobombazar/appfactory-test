package com.kony.sbg.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.sbg.resources.api.ApprovalQueueResourceExt;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SbgFetchAllMyPendingRequestOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(SbgFetchAllMyPendingRequestOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result;
		try {
			// ApprovalQueueResource approvalQueueResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalQueueResource.class);
			
            ApprovalQueueResourceExt approvalQueueResource = (ApprovalQueueResourceExt) ((ResourceFactory) DBPAPIAbstractFactoryImpl
                    .getInstance().getFactoryInstance(ResourceFactory.class))
                    .getResource(ApprovalQueueResourceExt.class);


            LOG.debug("approvalQueueResource = " + approvalQueueResource);
            
            result = approvalQueueResource.fetchAllMyPendingRequests(methodId, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchAllMyPendingRequests: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}
}
