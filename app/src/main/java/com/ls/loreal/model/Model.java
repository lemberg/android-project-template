package com.ls.loreal.model;

import com.ls.drupal.DrupalClient;
import com.ls.loreal.LorealConfig;
import com.ls.loreal.LorealApplication;

import android.content.Context;

/**
 * Created on 21.05.2015.
 */
public class Model {
    private static class ModelHolder
    {
        static Model instance = new Model();
    }

    public final static Model instance()
    {
        return ModelHolder.instance;
    }

    private DrupalClient client;

    private Model()
    {
        Context context = LorealApplication.getSharedContext();
        client = new DrupalClient(LorealConfig.BASE_URL,context);
    }

}
