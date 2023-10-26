package com.kony.sbg.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.sbg.mappers.SbgBackendMapperExtn;
import com.kony.sbg.mappers.SbgBusinessMapperExtn;
import com.kony.sbg.mappers.SbgResourceMapperExtn;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;

@IntegrationCustomServlet(servletName = "CustomSbgServletExtn", urlPatterns = {"CustomSbgServletExtn"})
public class CustomSbgServletExtn extends HttpServlet {
	
	private	Logger logger = Logger.getLogger(CustomSbgServletExtn.class);
	
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {

		logger.debug("CustomSbgServletExtn Init Start");
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
			.registerResourceMappings(new SbgResourceMapperExtn(), APIImplementationTypes.EXTENSION);
		
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
			.registerBusinessDelegateMappings(new SbgBusinessMapperExtn(), APIImplementationTypes.EXTENSION);
		
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
			.registerBackendDelegateMappings(new SbgBackendMapperExtn(), APIImplementationTypes.EXTENSION);
			
//		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
//		.registerResourceMappings(new LogsResourceMapperExtn(), APIImplementationTypes.EXTENSION);
	
		logger.debug("CustomSbgServletExtn Init End");
	}
}
