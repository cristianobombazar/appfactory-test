package com.kony.sbg.resources.api;

import java.io.IOException;
import java.util.List;

import com.dbp.core.api.Resource;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;

import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;

public interface ApprovalQueueResourceExt extends ApprovalQueueResource, Resource {

    public Result fetchRequestHistory(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result rejectACHTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result approveACHTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result approveACHFileRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result rejectACHFileRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result approveGeneralTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result rejectGeneralTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result withdrawACHTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result withdrawACHFileRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result withdrawGeneralTransactionRequest(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    public void autoRejectPendingTransactionsInApprovalQueue(FabricRequestManager fabricRequestManager,
            FabricResponseManager fabricResponseManager);

    public Result fetchApprovers(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    public Result fetchRecordsPendingForMyApproval(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public Result fetchMyApprovalHistory(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public Result approve(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    public Result reject(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    public Result withdraw(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    public Result fetchAllMyPendingRequests(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public Result fetchMyRequestHistory(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public List<ApprovalRequestDTO> fetchAllRequests(List<BBRequestDTO> requests, DataControllerRequest dcr);

    public Result validateForApprovals(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public Result updateBackendIdInApprovalQueue(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws IOException;

    public Result renotifyPendingApprovalRequest(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);
}
