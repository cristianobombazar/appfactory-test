package com.kony.sbg.resources.api;

import com.dbp.core.api.Resource;

public interface SbgAccountSortingResource extends Resource {

    String fetchAccountList(String accountID);

}
