package com.kony.sbg.servlets;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;
import com.temenos.dbx.product.integration.IntegrationMappings;
import com.temenos.dbx.product.utils.ThreadExecutor;
import com.kony.sbg.mappers.PartyUserManagementBackendDelegateMapperExtn;

@IntegrationCustomServlet(servletName = "PartyUserManagementServletExtn", urlPatterns = {
		"PartyUserManagementServletExtn" })
public class PartyUserManagementServletExtn extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7996896027215639726L;

	@Override
	public void init() throws ServletException {
		String integrationName = IntegrationMappings.getInstance().getIntegrationName();
		if ("party".equalsIgnoreCase(integrationName)) {
			register();
		}
	}

	public static void register() {
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
				.registerBackendDelegateMappings(new PartyUserManagementBackendDelegateMapperExtn(),
						APIImplementationTypes.EXTENSION);
	}

	@Override
	public void destroy() {
		try {
			// Shutting down the customer 360 thread pool
			ThreadExecutor.getExecutor().shutdownExecutor();
		} catch (Exception e) {

		}
	}
}
