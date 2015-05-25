package com.ls.druplaproject;

import com.ls.druplaproject.model.Model;
import com.ls.druplaproject.model.data.base.DatabaseFacade;

import android.app.Application;
import android.content.Context;

/**
 * Created on 21.05.2015.
 */
public class DrupalApplication extends Application{

    private static  Context sharedContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedContext = this;
        DatabaseFacade.instance(sharedContext);//Initialize database facade
        Model.instance(sharedContext);//Initialize model
    }

    public static Context getSharedContext() {
        return sharedContext;
    }
}
