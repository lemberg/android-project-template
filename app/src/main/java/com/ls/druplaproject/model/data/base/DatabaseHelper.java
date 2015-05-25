package com.ls.druplaproject.model.data.base;


import com.ls.druplaproject.R;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DatabaseHelper
        extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "main_db";

    private static final int DATABASE_VERSION = 1;

    private final Resources appResources;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.appResources = context.getResources();
    }

    private static List<String> getInitDatabaseQueris(final Resources theAppResources) {
        final List<String> queryList = new ArrayList<String>();
     

        queryList.add(theAppResources.getString(R.string.create_table_stub_items));
        //TODO: add other tables here

        
        return queryList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Iterator<String> it = getInitDatabaseQueris(this.appResources).iterator();
        while (it.hasNext()) {
            String subquery = it.next();
            db.execSQL(subquery);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: handle data migration here
    }

}
