package com.kony.sbg.backend.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.transactionservices.backenddelegate.extn.InternationalFundTransferBackendDelegateImplExtn;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.sbg.backend.api.SbgInternationalFundTransferBackendDelegateExtn;
import com.kony.sbg.util.SBGCommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;

public class SbgInternationalFundTransferBackendDelegateImplExtn extends
		InternationalFundTransferBackendDelegateImplExtn implements SbgInternationalFundTransferBackendDelegateExtn {

	private static final Logger LOG = LogManager.getLogger(SbgInternationalFundTransferBackendDelegateImplExtn.class);

	@Override
	public InternationalFundTransferDTO validateTransaction(InternationalFundTransferBackendDTO input,
			DataControllerRequest request) {

		InternationalFundTransferDTO internationalFundTransferDTO = new InternationalFundTransferDTO();
		String referenceId = CommonUtils.generateUniqueIDHyphenSeperated(0, 16);
		LOG.debug("##ReferenceID Generated: " + referenceId.toUpperCase());
		internationalFundTransferDTO.setReferenceId(referenceId.toUpperCase());
		internationalFundTransferDTO.setTotalAmount(request.getParameter("amount"));
		internationalFundTransferDTO.setStatus("success");
		internationalFundTransferDTO.setCreditValueDate(SBGCommonUtils.getCurrentTimeStamp("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		internationalFundTransferDTO.setMessageDetails("Validation done at infinity");
		return internationalFundTransferDTO;
	}

	/*
	 * Kumara Swamy : method to Call the Fabric Service and fetch Response
	 */
	public String callRefDataOrchService(Map<String, Object> headerParams, Map<String, Object> requestParameters,
			String serviceName, String operationName) throws Exception {
		String refDataResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
				.withOperationId(operationName).withRequestParameters(requestParameters)
				.withRequestHeaders(headerParams).build().getResponse();
		LOG.info("[SbgInternationalFundTransferBackendDelegateImplExtn] callRefDataOrchService: " + refDataResponse);
		return refDataResponse;
	}

}
