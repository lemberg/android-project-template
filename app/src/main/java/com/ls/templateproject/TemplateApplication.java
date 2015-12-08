package com.ls.templateproject;

import com.ls.templateproject.model.plain.Model;
import com.ls.templateproject.model.data.base.DatabaseFacade;

import android.app.Application;
import android.content.Context;

/**
 * Created on 21.05.2015.
 */
public class TemplateApplication extends Application{

    private static  Context sharedContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedContext = this;
        DatabaseFacade.instance(sharedContext);
        Model.instance(sharedContext);
        //Setting client is optional: you can perform this action in case is autentication is required in order to load images
//        DrupalImageView.setupSharedClient(Model.instance().getClient());
    }

    public static Context getSharedContext() {
        return sharedContext;
    }
}
