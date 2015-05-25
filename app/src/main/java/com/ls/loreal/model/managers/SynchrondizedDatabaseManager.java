package com.ls.loreal.model.managers;

import com.ls.drupal.AbstractBaseDrupalEntity;
import com.ls.drupal.DrupalClient;
import com.ls.loreal.model.data.base.DatabaseFacade;

/**
 * Created on 25.05.2015. Use for database-based storage only
 */
public abstract class SynchrondizedDatabaseManager<ClassToManage, FetchRequestToManage extends AbstractBaseDrupalEntity> extends BaseItemManager<ClassToManage, FetchRequestToManage> {

    protected SynchrondizedDatabaseManager(DrupalClient client) {
        super(client);
    }

    @Override
    protected final boolean storeResponse(ClassToManage response, Object tag) {
        DatabaseFacade facade = DatabaseFacade.instance();
        synchronized (facade) {
            try {
                facade.open();
                facade.beginTransactions();
                boolean result = synchronizedStoreResponse(response, tag);
                if (result) {
                    facade.setTransactionSuccesfull();
                }
                facade.endTransactions();
                return result;
            } finally {
                facade.close();
            }
        }
    }

    @Override
    protected ClassToManage restoreResponse(Object tag) {
        DatabaseFacade facade = DatabaseFacade.instance();
        synchronized (facade) {
            try {
                facade.open();
                ClassToManage result = synchronizeddRetoreResponse(tag);
                return result;
            } finally {
                facade.close();
            }
        }
    }

    protected abstract boolean synchronizedStoreResponse(ClassToManage response, Object tag);

    protected abstract ClassToManage synchronizeddRetoreResponse(Object tag);
}
