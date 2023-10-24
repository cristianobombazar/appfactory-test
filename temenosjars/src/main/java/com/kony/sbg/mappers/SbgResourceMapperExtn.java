package com.kony.sbg.mappers;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.DBPAPIMapper;
import com.dbp.core.api.Resource;
import com.kony.adminconsole.service.approvalmatrix.resource.api.ApprovalMatrixResource;
import com.kony.adminconsole.service.featuresandactions.resource.api.FeaturesAndActionsResource;
import com.kony.sbg.resources.api.CurrencyResource;
import com.kony.sbg.resources.api.EvaluateTransactionResource;
import com.kony.sbg.resources.api.ManageBeneficiaryResource;
import com.kony.sbg.resources.api.PaymentFeedbackResource;
import com.kony.sbg.resources.api.RFQResource;
import com.kony.sbg.resources.api.SbgDomesticFundTransferResource;
import com.kony.sbg.resources.api.SwiftCodeUserManagementResource;
import com.kony.sbg.resources.api.TransactionResourceLimits;
import com.kony.sbg.resources.impl.ApprovalMatrixResourceImplExtn;
import com.kony.sbg.resources.impl.ApprovalMatrixResourceImplProExtn;
import com.kony.sbg.resources.impl.ApprovalRequestResourceImplExtn;
import com.kony.sbg.resources.impl.CurrencyResourceImpl;
import com.kony.sbg.resources.impl.CustomerImageResourceImplExtn;
import com.kony.sbg.resources.impl.EvaluateTransactionResourceImpl;
import com.kony.sbg.resources.impl.ExternalPayeeResourceImplExtn;
import com.kony.sbg.resources.impl.FeaturesAndActionsResourceImplExtn;
import com.kony.sbg.resources.impl.InfinityUserManagementResourceImplExtn;
import com.kony.sbg.resources.impl.InternationalPayeeResourceImplExtn;
import com.kony.sbg.resources.impl.ManageBeneficiaryResourceImpl;
import com.kony.sbg.resources.impl.PaymentFeedbackResourceImpl;
import com.kony.sbg.resources.impl.RFQResourceImpl;
import com.kony.sbg.resources.impl.SbgApprovalQueueResourceImplExt;
import com.kony.sbg.resources.impl.SbgDomesticFundTransferResourceImplExtn;
import com.kony.sbg.resources.impl.SbgInternationalFundTransferResourceImplExtn;
import com.kony.sbg.resources.impl.SignatoryGroupResourceImplExtn;
import com.kony.sbg.resources.impl.SignatoryGroupResourceImplProductExtn;
import com.kony.sbg.resources.impl.SwiftCodeUserManagementResourceImpl;
import com.kony.sbg.resources.impl.TransactionResourceLimitsImpl;
import com.kony.sbg.resources.api.*;
import com.kony.sbg.resources.impl.*;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerImageResource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalRequestResource;
import com.temenos.dbx.product.payeeservices.resource.api.ExternalPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InterBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InternationalPayeeResource;
import com.temenos.dbx.product.transactionservices.resource.api.InterBankFundTransferResource;
import com.temenos.dbx.product.transactionservices.resource.api.InternationalFundTransferResource;
import com.temenos.dbx.product.transactionservices.resource.api.OwnAccountFundTransferResource;

public class SbgResourceMapperExtn implements DBPAPIMapper<Resource> {

	@Override
	public Map<Class<? extends Resource>, Class<? extends Resource>> getAPIMappings() {
		Map<Class<? extends Resource>, Class<? extends Resource>> map = new HashMap<>();

		map.put(SwiftCodeUserManagementResource.class, SwiftCodeUserManagementResourceImpl.class);
		map.put(ExternalPayeeResource.class, ExternalPayeeResourceImplExtn.class);
		map.put(InternationalPayeeResource.class, InternationalPayeeResourceImplExtn.class);
		map.put(ManageBeneficiaryResource.class, ManageBeneficiaryResourceImpl.class);
		map.put(CurrencyResource.class, CurrencyResourceImpl.class);
		map.put(TransactionResourceLimits.class, TransactionResourceLimitsImpl.class);
		map.put(InternationalFundTransferResource.class, SbgInternationalFundTransferResourceImplExtn.class);
		map.put(RFQResource.class, RFQResourceImpl.class);
		map.put(ApprovalQueueResource.class, SbgApprovalQueueResourceImplExt.class);
		map.put(PaymentFeedbackResource.class, PaymentFeedbackResourceImpl.class);
		//map.put(ArrangementsResource.class, ArrangementsResourceImplExtn.class);
		map.put(EvaluateTransactionResource.class, EvaluateTransactionResourceImpl.class);
		map.put(FeaturesAndActionsResource.class, FeaturesAndActionsResourceImplExtn.class);
		map.put(com.kony.adminconsole.service.signatorygroup.resource.api.SignatoryGroupResource.class, SignatoryGroupResourceImplExtn.class);
		map.put(ApprovalMatrixResource.class, ApprovalMatrixResourceImplExtn.class);
		map.put(InfinityUserManagementResource.class, InfinityUserManagementResourceImplExtn.class);
		map.put(CustomerImageResource.class, CustomerImageResourceImplExtn.class);
		map.put(ApprovalRequestResource.class, ApprovalRequestResourceImplExtn.class);		
		//moved from LogsResourceMapper and the following 2 resources were extended for adding audit logs
		map.put(ApprovalMatrixResource.class, ApprovalMatrixResourceImplProExtn.class);
		map.put(com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource.class, SignatoryGroupResourceImplProductExtn.class);

		map.put(InterBankFundTransferResource.class, SbgDomesticFundTransferResourceImplExtn.class);
		map.put(SbgDomesticFundTransferResource.class, SbgDomesticFundTransferResourceImplExtn.class);

		map.put(InterBankFundTransferResource.class, InterBankPayeeResourceImplExtn.class);
		map.put(InterBankPayeeResource.class, InterBankPayeeResourceImplExtn.class);

		map.put(ApprovalQueueResourceExt.class, SbgApprovalQueueResourceImplExt.class);

		map.put(PublicHolidayResource.class, PublicHolidayResourceImpl.class);
		map.put(SubmitDocToFileNetResource.class, SubmitDocToFileNetResourceImpl.class);

		map.put(OwnAccountFundTransferResource.class, SbgOwnAccountTransferResourceImplExtn.class);

		return map;
	}
}
