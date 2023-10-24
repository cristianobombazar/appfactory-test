package com.kony.sbg.mappers;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.ProfileManagementBackendDelegate;
import com.kony.sbg.backend.impl.PartyProfileManagementBackendDelegateImplExtn;
import com.temenos.dbx.usermanagement.mapper.PartyUserManagementBackendDelegateMapper;

public class PartyUserManagementBackendDelegateMapperExtn extends PartyUserManagementBackendDelegateMapper {

	@Override
	public Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> getAPIMappings() {
		Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> map = new HashMap<>();
		map.put(ProfileManagementBackendDelegate.class, PartyProfileManagementBackendDelegateImplExtn.class);
		return map;
	}
}
