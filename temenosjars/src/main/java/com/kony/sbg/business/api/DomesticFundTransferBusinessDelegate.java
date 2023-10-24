package com.kony.sbg.business.api;

import com.dbp.core.api.BusinessDelegate;
import com.kony.sbg.dto.DomesticFundTransferDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;

import java.util.List;

public interface DomesticFundTransferBusinessDelegate extends BusinessDelegate {
    DomesticFundTransferDTO createTransactionAtDBX(DomesticFundTransferDTO var1);

    DomesticFundTransferDTO updateTransactionAtDBX(DomesticFundTransferDTO var1);

    DomesticFundTransferDTO updateStatus(String var1, String var2, String var3);

    boolean updateRequestId(String var1, String var2);

    DomesticFundTransferDTO fetchTranscationEntry(String var1);

    boolean deleteTransactionAtDBX(String var1);

    DomesticFundTransferDTO fetchExecutedTranscationEntry(String var1, List<String> var2, String var3);

    void executeTransactionAfterApproval(String var1, String var2, DataControllerRequest var3);

    List<ApprovalRequestDTO> fetchDomesticTransactionsWithApprovalInfo(List<BBRequestDTO> var1, DataControllerRequest var2);

    DomesticFundTransferDTO createPendingTransaction(DomesticFundTransferDTO var1, DataControllerRequest var2);

    DomesticFundTransferDTO approveTransaction(String var1, DataControllerRequest var2, String var3);

    DomesticFundTransferDTO rejectTransaction(String var1, String var2, DataControllerRequest var3);

    DomesticFundTransferDTO withdrawTransaction(String var1, String var2, DataControllerRequest var3);

    DomesticFundTransferDTO fetchTransactionById(String var1, DataControllerRequest var2);

    List<ApprovalRequestDTO> fetchDomesticTransactionsForApprovalInfo(String var1, DataControllerRequest var2);

    DomesticFundTransferDTO editPendingTransaction(DomesticFundTransferDTO var1, DataControllerRequest var2);

    DomesticFundTransferDTO cancelTransactionWithApproval(String var1, String var2, DataControllerRequest var3);

    DomesticFundTransferDTO approveCancellation(String var1, DataControllerRequest var2);

    DomesticFundTransferDTO rejectCancellation(String var1, DataControllerRequest var2);

    DomesticFundTransferDTO withdrawCancellation(String var1, DataControllerRequest var2);

    DomesticFundTransferDTO deleteTransactionWithApproval(String var1, String var2, String var3, String var4, DataControllerRequest var5);

    DomesticFundTransferDTO approveDeletion(String var1, String var2, String var3, DataControllerRequest var4);

    DomesticFundTransferDTO rejectDeletion(String var1, DataControllerRequest var2);

    DomesticFundTransferDTO withdrawDeletion(String var1, DataControllerRequest var2);

    void cancelTransactionAfterApproval(String var1, String var2, DataControllerRequest var3);

    DomesticFundTransferDTO updateStatusUsingTransactionId(String var1, String var2, String var3);
}

