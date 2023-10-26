package com.kony.sbg.postprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.kony.sbg.postprocessor.GetAccountsPostLoginObjectServicePostProcessor_new;
import com.temenos.infinity.api.arrangements.postprocessors.SaveAccountsInSessionTask;

public class AccountLoginPostProcessor_new implements ObjectServicePostProcessor {
	@SuppressWarnings("unchecked")
	@Override
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
			throws Exception {
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] {
				GetAccountsPostLoginObjectServicePostProcessor_new.class, SaveAccountsInSessionTask.class };
		ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
	}

}
