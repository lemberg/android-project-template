package com.ls.templateproject.model.plain.managers;

import com.ls.http.base.client.LSClient;
import com.ls.templateproject.model.data.base.DatabaseFacade;

import android.support.annotation.NonNull;

/**
 * Created on 25.05.2015. Use for database-based storage only
 */
public abstract class SynchronizedDatabaseManager<ClassToManage, ParametersClass, TagClass> extends BaseItemManager<ClassToManage, ParametersClass, TagClass> {

    protected SynchronizedDatabaseManager(@NonNull final LSClient client) {
        super(client);
    }

    @Override
    protected final boolean storeResponse(ClassToManage response, TagClass tag) {
        final DatabaseFacade facade = DatabaseFacade.instance();
        synchronized (facade) {
            try {
                facade.open();
                facade.beginTransactions();
                boolean result = synchronizedStoreResponse(response, tag);
                if (result) {
                    facade.setTransactionSuccesfull();
                }
                return result;
            } finally {
                facade.endTransactions();
                facade.close();
            }
        }
    }

    @Override
    protected ClassToManage restoreResponse(TagClass tag) {
        final DatabaseFacade facade = DatabaseFacade.instance();
        synchronized (facade) {
            try {
                facade.open();
                ClassToManage result = synchronizedRestoreResponse(tag);
                return result;
            } finally {
                facade.close();
            }
        }
    }

    protected abstract boolean synchronizedStoreResponse(ClassToManage response, TagClass tag);

    protected abstract ClassToManage synchronizedRestoreResponse(TagClass tag);
}
