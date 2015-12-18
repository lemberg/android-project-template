package com.ls.templateproject;

import com.ls.templateproject.model.data.base.DatabaseFacade;
import com.ls.templateproject.model.plain.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

/**
 * Created on 21.05.2015.
 */
public final class TemplateApplication extends MultiDexApplication {

    private static Context sharedContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedContext = this;
        DatabaseFacade.instance(sharedContext);
        Model.instance(sharedContext);
        //Setting client is optional: you can perform this action in case is authentication is
        // required in order to load images
        //DrupalImageView.setupSharedClient(Model.instance().getClient());
    }

    @NonNull
    public static Context getSharedContext() {
        if (sharedContext == null) {
            throw new IllegalStateException(
                    "getSharedContext() called before Application.onCreate()");
        }
        return sharedContext;
    }
}
