package com.kony.sbg.postprocessor;

import com.kony.sbg.util.KmfUtils;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class SpotLightAuthPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception 
	{
		KmfUtils.printInputs(null, request, response);
		// TODO Auto-generated method stub
		return result;
	}

}
