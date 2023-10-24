package com.kony.sbg.mappers;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.BusinessDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.kony.adminconsole.service.approvalmatrix.businessdelegate.api.ApprovalMatrixBusinessDelegate;
import com.kony.kmsinvoke.businessdelegate.api.SendEmailBusinessDelegate;
import com.kony.kmsinvoke.businessdelegate.api.SendSmsBusinessDelegate;
import com.kony.sbg.business.api.*;
import com.kony.sbg.business.impl.*;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.api.InfinityUserManagementBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.TransactionLimitsBusinessDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InterBankFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InternationalFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.OwnAccountFundTransferBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.kony.sbg.business.impl.SBGTransactionsLimitBusinessDelegateImplExtn;
import com.dbp.transactionslimitengine.businessdelegate.api.TransactionsLimitBusinessDelegate;

public class SbgBusinessMapperExtn implements DBPAPIMapper<BusinessDelegate> {

	@Override
	public Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> getAPIMappings() {
		Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> map = new HashMap<>();

		map.put(SwiftCodeBusinessDelegate.class, SwiftCodeBusinessDelegateImpl.class);
		map.put(ManageBeneficiaryBusinessDelegate.class, ManageBeneficiaryBusinessDelegateImpl.class);
		map.put(CurrencyBusinessDelegate.class, CurrencyBusinessDelegateImpl.class);
		map.put(SbgInternationalFundTransferBusinessDelegateExtn.class,
				SbgInternationalFundTransferBusinessDelegateImplExtn.class);
		map.put(InternationalFundTransferBusinessDelegate.class,
				SbgInternationalFundTransferBusinessDelegateImplExtn.class);
		map.put(SBGTransactionLimitsBusinessDelegate.class, SBGTransactionLimitsBusinessDelegateImpl.class);
		map.put(RFQBusinessDelegate.class, RFQBusinessDelegateImpl.class);
		map.put(PaymentFeedbackBusinessDelegate.class, PaymentFeedbackBusinessDelegateImpl.class);
		map.put(SendSmsBusinessDelegate.class, SendSmsBusinessDelegateImplExtn.class);
		map.put(SendEmailBusinessDelegate.class, SendEmailBusinessDelegateImplExtn.class);
		map.put(TransactionLimitsBusinessDelegate.class, TransactionLimitsBusinessDelegateImplSbgExtn.class);
		
		map.put(TransactionsLimitBusinessDelegate.class, SBGTransactionsLimitBusinessDelegateImplExtn.class);
		
		map.put(EvaluateTransactionBusinessDelegate.class, EvaluateTransactionBusinessDelegateImpl.class);
		map.put(SBGServicesBusinessDelegate.class, SBGServicesBusinessDelegateImpl.class);
		map.put(CustomerActionsBusinessDelegate.class, CustomerActionsBusinessDelegateImplExtn.class);
		map.put(InfinityUserManagementBusinessDelegate.class, InfinityUserManagementBusinessDelegateImplExtn.class);
		map.put(ApprovalMatrixBusinessDelegate.class, ApprovalMatrixBusinessDelegateExtn.class);

		map.put(SbgInterBankFundTransferBusinessDelegateExtn.class,
				SbgInterBankFundTransferBusinessDelegateImplExtn.class);

		map.put(InterBankFundTransferBusinessDelegate.class,
				SbgInterBankFundTransferBusinessDelegateImplExtn.class);

		map.put(PublicHolidayBusinessDelegate.class, PublicHolidayBusinessDelegateImpl.class);

		map.put(OwnAccountFundTransferBusinessDelegateExtn.class,
				SbgOwnAccountFundTransferBusinessDelegateImplExtn.class);
		map.put(OwnAccountFundTransferBusinessDelegate.class,
				SbgOwnAccountFundTransferBusinessDelegateImplExtn.class);

		return map;
	}

}
