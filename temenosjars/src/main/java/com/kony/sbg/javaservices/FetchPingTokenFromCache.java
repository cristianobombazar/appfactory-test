package com.kony.sbg.javaservices;

import com.konylabs.middleware.dataobject.Result;
import com.kony.sbg.util.SBGCommonUtils;
import com.kony.sbg.util.SBGUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;

public class FetchPingTokenFromCache implements JavaService2 {

	@Override
	public Object invoke(String arg0, Object[] arg1, DataControllerRequest arg2, DataControllerResponse arg3)
			throws Exception {
		Result result = SBGCommonUtils.cacheFetchPingToken("Authorization",arg2);;
		return result;
	}

}
