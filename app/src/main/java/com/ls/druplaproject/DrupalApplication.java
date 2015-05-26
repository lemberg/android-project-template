package com.ls.druplaproject;

import com.ls.druplaproject.model.Model;
import com.ls.druplaproject.model.data.base.DatabaseFacade;
import com.ls.util.image.DrupalImageView;

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
        DatabaseFacade.instance(sharedContext);
        Model.instance(sharedContext);
        //Setting client is optional: you can perform this action in case is autentication is required in order to load images
        DrupalImageView.setupSharedClient(Model.instance().getClient());
    }

    public static Context getSharedContext() {
        return sharedContext;
    }
}
