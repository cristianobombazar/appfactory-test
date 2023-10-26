package com.kony.sbg.mappers;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.DBPAPIMapper;
import com.dbp.core.api.Resource;

import com.kony.sbg.resources.impl.ApprovalMatrixResourceImplProExtn;
import com.kony.sbg.resources.impl.SignatoryGroupResourceImplProductExtn;
import com.temenos.dbx.product.approvalmatrixservices.resource.api.ApprovalMatrixResource;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;

public class LogsResourceMapperExtn implements DBPAPIMapper<Resource> {

	@Override
	public Map<Class<? extends Resource>, Class<? extends Resource>> getAPIMappings() {
		Map<Class<? extends Resource>, Class<? extends Resource>> map = new HashMap<>();
		
//		map.put(ApprovalMatrixResource.class, ApprovalMatrixResourceImplProExtn.class);
//		map.put(SignatoryGroupResource.class, SignatoryGroupResourceImplProductExtn.class);
		
		return map;
	}
}