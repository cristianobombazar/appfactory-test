package com.kony.sbg.mappers;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.infinity.dbx.temenos.payeeservices.backenddelegate.api.impl.InternationalPayeeBackendDelegateImplExtn;
import com.kony.sbg.backend.api.CurrencyBackendDelegate;
import com.kony.sbg.backend.api.DomesticPayeeBackendDelegate;
import com.kony.sbg.backend.api.DomesticPaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.ManageBeneficiaryBackendDelegate;
import com.kony.sbg.backend.api.OwnAccountPaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.PayeeBackendDelegate;
import com.kony.sbg.backend.api.PaymentAPIBackendDelegate;
import com.kony.sbg.backend.api.PaymentFeedbackBackendDelegate;
import com.kony.sbg.backend.api.PublicHolidayBackendDelegate;
import com.kony.sbg.backend.api.RFQBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegate;
import com.kony.sbg.backend.api.RefDataBackendDelegateInterBank;
import com.kony.sbg.backend.api.RefDataBackendDelegateOwnAccount;
import com.kony.sbg.backend.api.SBGTransactionLimitsBackendDelegate;
import com.kony.sbg.backend.api.SbgInterBankFundTransferBackendDelegateExtn;
import com.kony.sbg.backend.api.SbgInternationalFundTransferBackendDelegateExtn;
import com.kony.sbg.backend.api.SbgOwnAccountFundTransferBackendDelegateExtn;
import com.kony.sbg.backend.api.SwiftCodeBackendDelegate;
import com.kony.sbg.backend.impl.ContractBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.CoreCustomerBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.CurrencyBackendDelegateImpl;
import com.kony.sbg.backend.impl.DomesticPaymentAPIBackendDelegateImpl;
import com.kony.sbg.backend.impl.InfinityUserManagementBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.InterBankPayeeBackendDelegateImpl;
import com.kony.sbg.backend.impl.ManageBeneficiaryBackendDelegateImpl;
import com.kony.sbg.backend.impl.OwnAccountPaymentAPIBackendDelegateImpl;
import com.kony.sbg.backend.impl.PartyAddressBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.PartyCommunicationBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.PartyInfinityUserManagementBDImplExtn;
import com.kony.sbg.backend.impl.PayeeBackendDelegateImpl;
import com.kony.sbg.backend.impl.PaymentAPIBackendDelegateImpl;
import com.kony.sbg.backend.impl.PaymentFeedbackBackendDelegateImpl;
import com.kony.sbg.backend.impl.ProfileManagementBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.PublicHolidayBackendDelegateImpl;
import com.kony.sbg.backend.impl.RFQBackendDelegateImpl;
import com.kony.sbg.backend.impl.RefDataBackendDelegateImpl;
import com.kony.sbg.backend.impl.RefDataBackendDelegateInterBankImpl;
import com.kony.sbg.backend.impl.RefDataBackendDelegateOwnAccountImpl;
import com.kony.sbg.backend.impl.SBGTransactionLimitsBackendDelegateImpl;
import com.kony.sbg.backend.impl.SbgInterBankFundTransferBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.SbgInternationalFundTransferBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.SbgOwnAccountFundTransferBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.SignatoryGroupBackendDelegateImplExtn;
import com.kony.sbg.backend.impl.SwiftCodeBackendDelegateImpl;
import com.kony.sbg.business.api.SbgInternationalPayeeBackendDelegate;
import com.kony.sbg.business.impl.SbgInternationalPayeeBackendDelegateImplExtn;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractBackendDelegate;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.CoreCustomerBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.AddressBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.InfinityUserManagementBackendDelegate;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InternationalPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.backenddelegate.impl.InternationalPayeeBackendDelegateImpl;
import com.temenos.dbx.product.signatorygroupservices.backenddelegate.api.SignatoryGroupBackendDelegate;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InternationalFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.OwnAccountFundTransferBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.CommunicationBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.ProfileManagementBackendDelegate;

public class SbgBackendMapperExtn implements DBPAPIMapper<BackendDelegate> {

	@Override
	public Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> getAPIMappings() {
		Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> map = new HashMap<>();
		map.put(SwiftCodeBackendDelegate.class, SwiftCodeBackendDelegateImpl.class);
		map.put(PayeeBackendDelegate.class, PayeeBackendDelegateImpl.class);
		map.put(ManageBeneficiaryBackendDelegate.class, ManageBeneficiaryBackendDelegateImpl.class);		
		map.put(CurrencyBackendDelegate.class, CurrencyBackendDelegateImpl.class);
		map.put(SbgInternationalFundTransferBackendDelegateExtn.class, SbgInternationalFundTransferBackendDelegateImplExtn.class);
		map.put(InternationalFundTransferBackendDelegate.class, SbgInternationalFundTransferBackendDelegateImplExtn.class);
		map.put(RefDataBackendDelegate.class, RefDataBackendDelegateImpl.class);
		map.put(RFQBackendDelegate.class, RFQBackendDelegateImpl.class);
		map.put(PaymentAPIBackendDelegate.class, PaymentAPIBackendDelegateImpl.class);
		map.put(PaymentFeedbackBackendDelegate.class, PaymentFeedbackBackendDelegateImpl.class);
		map.put(SBGTransactionLimitsBackendDelegate.class, SBGTransactionLimitsBackendDelegateImpl.class);
		map.put(InfinityUserManagementBackendDelegate.class, PartyInfinityUserManagementBDImplExtn.class);
		map.put(SignatoryGroupBackendDelegate.class, SignatoryGroupBackendDelegateImplExtn.class);
		map.put(ContractBackendDelegate.class, ContractBackendDelegateImplExtn.class);
		map.put(CoreCustomerBackendDelegate.class, CoreCustomerBackendDelegateImplExtn.class);
		map.put(ProfileManagementBackendDelegate.class, ProfileManagementBackendDelegateImplExtn.class);
		map.put(InfinityUserManagementBackendDelegate.class, InfinityUserManagementBackendDelegateImplExtn.class);
		
		map.put(DomesticPaymentAPIBackendDelegate.class, DomesticPaymentAPIBackendDelegateImpl.class);
		// map.put(InterBankFundTransferBackendDelegate.class, DomesticPaymentAPIBackendDelegateImpl.class);

		map.put(DomesticPayeeBackendDelegate.class, InterBankPayeeBackendDelegateImpl.class);

		map.put(RefDataBackendDelegateInterBank.class, RefDataBackendDelegateInterBankImpl.class);
		map.put(PublicHolidayBackendDelegate.class, PublicHolidayBackendDelegateImpl.class);
		map.put(CommunicationBackendDelegate.class,PartyCommunicationBackendDelegateImplExtn.class);
		map.put(AddressBackendDelegate.class,PartyAddressBackendDelegateImplExtn.class);
		map.put(SbgInterBankFundTransferBackendDelegateExtn.class, SbgInterBankFundTransferBackendDelegateImplExtn.class);
		map.put(InterBankFundTransferBackendDelegate.class, SbgInterBankFundTransferBackendDelegateImplExtn.class);



		map.put(SbgOwnAccountFundTransferBackendDelegateExtn.class, SbgOwnAccountFundTransferBackendDelegateImplExtn.class);
		map.put(OwnAccountFundTransferBackendDelegate.class, SbgOwnAccountFundTransferBackendDelegateImplExtn.class);
		map.put(RefDataBackendDelegateOwnAccount.class, RefDataBackendDelegateOwnAccountImpl.class);
		map.put(OwnAccountPaymentAPIBackendDelegate.class, OwnAccountPaymentAPIBackendDelegateImpl.class);

		map.put(InternationalPayeeBackendDelegateImplExtn.class, SbgInternationalPayeeBackendDelegateImplExtn.class );
		map.put(InternationalPayeeBackendDelegate.class, SbgInternationalPayeeBackendDelegateImplExtn.class );

		return map;
	}

}
