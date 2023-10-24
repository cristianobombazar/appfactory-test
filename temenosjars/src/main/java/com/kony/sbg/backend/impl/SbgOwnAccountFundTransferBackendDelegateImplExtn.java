package com.kony.sbg.backend.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.transactionservices.backenddelegate.extn.OwnAccountFundTransferBackendDelegateImplExtn;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.backend.api.SbgOwnAccountFundTransferBackendDelegateExtn;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;

public class SbgOwnAccountFundTransferBackendDelegateImplExtn extends
		OwnAccountFundTransferBackendDelegateImplExtn implements SbgOwnAccountFundTransferBackendDelegateExtn {

	private static final Logger LOG = LogManager.getLogger(SbgOwnAccountFundTransferBackendDelegateImplExtn.class);

	@Override
	public OwnAccountFundTransferDTO validateTransaction(OwnAccountFundTransferBackendDTO input,
			DataControllerRequest request) {

		OwnAccountFundTransferDTO ownAccountFundTransferDTO = new OwnAccountFundTransferDTO();
		String referenceId = CommonUtils.generateUniqueIDHyphenSeperated(0, 16);
		LOG.debug("##ReferenceID Generated: " + referenceId.toUpperCase());
		ownAccountFundTransferDTO.setReferenceId(referenceId.toUpperCase());
		ownAccountFundTransferDTO.setTotalAmount(request.getParameter("amount"));
		ownAccountFundTransferDTO.setStatus("success");
		ownAccountFundTransferDTO.setCreditValueDate(SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		ownAccountFundTransferDTO.setMessageDetails("Validation done at infinity");
		return ownAccountFundTransferDTO;
	}

	/*
	 * Mashilo Joseph Monene : method to Call the Fabric Service and fetch Response
	 */
	public String callRefDataOrchService(Map<String, Object> headerParams, Map<String, Object> requestParameters,
			String serviceName, String operationName) throws Exception {
		String refDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
				.withOperationId(operationName).withRequestParameters(requestParameters)
				.withRequestHeaders(headerParams).build().getResponse();
		LOG.info("[SbgOwnAccountFundTransferBackendDelegateImplExtn] callRefDataOrchService: " + refDataResponse);
		return refDataResponse;
	}

}
