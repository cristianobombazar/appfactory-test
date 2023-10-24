/**
 * @author srinag.boda
 *
 */

package com.kony.sbg.config;

import com.temenos.infinity.api.commons.config.InfinityServices;

public enum APIConfigs implements InfinityServices {
	
	ARRANGEMENTSMICROSERVICESJSON_GETARRANGEMENTSBYACCOUNTID("SBGArrangementMicroService", "getArrangementsByAccountId");

    private String serviceName, operationName;

    private APIConfigs(String serviceName, String operationName) {
        this.serviceName = serviceName;
        this.operationName = operationName;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public String getOperationName() {
        return this.operationName;
    }

}
