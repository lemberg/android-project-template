package com.ls.templateproject.model.data.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseFacade {

    private static DatabaseFacade instance;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;
    private int openCounter = 0;


    public static DatabaseFacade instance(Context theContext) {
        if (instance == null) {
            instance = new DatabaseFacade(theContext);
        }

        return instance;
    }


    public static DatabaseFacade instance() {
        if (instance == null) {
            throw new IllegalStateException("Called method on uninitialized database facade");
        }

        return instance;
    }


    private DatabaseFacade(Context theContext) {
        this.context = theContext;
    }


    public void beginTransactions() {
        db.beginTransaction();
    }


    public void setTransactionSuccesfull() {
        db.setTransactionSuccessful();
    }


    public void endTransactions() {
        db.endTransaction();
    }


    public synchronized DatabaseFacade open()
            throws SQLException {

        if (openCounter == 0) {
            this.dbHelper = new DatabaseHelper(this.context);
            this.db = this.dbHelper.getWritableDatabase();
        }

        this.openCounter++;
        return this;
    }


    public synchronized void close() {
        if (openCounter == 1) {
            this.db.close();
            this.dbHelper.close();
        }

        openCounter--;
    }


    public boolean containsRecord(String theTable, String theWhereClause, String[] selectionArgs, String[] theColumns) {
        boolean result = false;

        Cursor cursor = db.query(true,
                theTable,
                theColumns,
                theWhereClause,// selection
                selectionArgs,// selection args
                null,// groupBy
                null,// having
                null,// order by
                null);// limit

        result = cursor.getCount() > 0;

        cursor.close();
        cursor = null;

        return result;
    }


    public Cursor query(String theQuery, String[] selectionArgs) {
        return db.rawQuery(theQuery, selectionArgs);
    }


    /**
     * @param conflictResolveAlgorithm use SQLiteDatabase constant like <code>SQLiteDatabase.CONFLICT_REPLACE<code/>
     */
    public long save(String theTable, ContentValues theValues, int conflictResolveAlgorithm) {
        return db.insertWithOnConflict(theTable, null, theValues, conflictResolveAlgorithm);
    }

    public long save(String theTable, ContentValues theValues) {
        return db.insert(theTable, null, theValues);
    }

    /**
     * @return returns number of rows affected
     */
    public int update(String theTable, String theWhereClause, String[] whereArgs, ContentValues theValues) {
        return db.update(theTable, theValues, theWhereClause, whereArgs);
    }


    public int delete(String theTable, String theWhereClause, String[] whereArgs) {
        return db.delete(theTable, theWhereClause, whereArgs);
    }


    public Cursor getAllRecords(String theTable, String[] theColumns, String theSelection, String[] selectionArgs) {
        Cursor cursor = db.query(true,
                theTable,
                theColumns,
                theSelection,// selection
                selectionArgs,// selection args
                null,// groupBy
                null,// having
                null,// order by
                null);// limit

        return cursor;
    }

    public int clearTable(String theTable) {
        return this.delete(theTable, null, null);
    }

}
