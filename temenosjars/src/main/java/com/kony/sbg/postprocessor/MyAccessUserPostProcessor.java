package com.kony.sbg.postprocessor;
import org.apache.log4j.Logger;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class MyAccessUserPostProcessor implements DataPostProcessor2{
	private static final Logger logger = Logger.getLogger(MyAccessUserPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) throws Exception {
		logger.debug("## MyAccessUserPostProcessor: ");
		String httpStatusCode=result.getParamValueByName("httpStatusCode").toString();
		String errmsg=result.getParamValueByName("errmsg").toString();
		logger.debug("## httpStatusCode"+httpStatusCode +"##errmsg##"+errmsg);
		if (httpStatusCode.contains("204") || errmsg.contains("empty response received")) {
			    result.removeParamByName("errmsg");
				result.addParam("Status", "Updated Sucessfully");
			} else {
				result.addParam("Status", "Updated Failed");

			}
		return result;
	}

}
