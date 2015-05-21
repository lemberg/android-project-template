package com.ls.loreal;

import android.app.Application;
import android.content.Context;

/**
 * Created on 21.05.2015.
 */
public class LorealApplication extends Application{

    private static  Context sharedContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedContext = this;
    }

    public static Context getSharedContext() {
        return sharedContext;
    }
}
