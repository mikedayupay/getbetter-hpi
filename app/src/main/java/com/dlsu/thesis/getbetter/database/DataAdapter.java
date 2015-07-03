package com.dlsu.thesis.getbetter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by daps on 7/3/15.
 */
public class DataAdapter {

    protected static final String TAG = "DataAdapter";

    private final Context myContext;
    private SQLiteDatabase getBetterDb;
    private DatabaseHelper getBetterDatabaseHelper;

    public DataAdapter (Context context) {
        this.myContext = context;
        getBetterDatabaseHelper  = new DatabaseHelper(myContext);
    }

    public DataAdapter createDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter openDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getReadableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    public void closeDatabase() {
        getBetterDatabaseHelper.close();
    }

    public boolean checkLogin (String username, String password) {

        String sql = "SELECT * FROM tbl_users WHERE email = " + username + " AND password = " + password;

        Cursor c = getBetterDb.rawQuery(sql, null);
        if(c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor getHealthCenters() {

        String sql = "SELECT * FROM tbl_health_centers";

        Cursor c = getBetterDb.rawQuery(sql, null);
        if(c != null) {
            c.moveToNext();
        }

        return c;
    }


}
