package com.dlsu.thesis.getbetter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dlsu.thesis.getbetter.objects.Users;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public ArrayList<String> getHealthCenters() {

        ArrayList<String> healthCenter = new ArrayList<>();
        String sql = "SELECT * FROM tbl_health_centers";

        Cursor c = getBetterDb.rawQuery(sql, null);


        while (c.moveToNext()) {
            healthCenter.add(c.getString(c.getColumnIndexOrThrow("health_center_name")));
        }

        return healthCenter;
    }

    public int getHealthCenterId(String healthCenter) {

        int result;
        String sql = "SELECT _id FROM tbl_health_centers WHERE health_center_name = " + healthCenter;

        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getInt(c.getColumnIndex("_id"));

        return result;
    }

    public ArrayList<Users> getPatients (int healthCenterId) {

        ArrayList<Users> results = new ArrayList<Users>();
        String sql = "Select u.first_name AS first_name, u.middle_name AS middle_name, " +
                "u.last_name AS last_name, u.birthdate AS birthdate, g.gender_name AS gender, " +
                "c.civil_status_name AS civil_status, u.blood_type AS blood_type " +
                "FROM tbl_users AS u, tbl_genders AS g, tbl_civil_statuses AS c" +
                "WHERE u.gender_id = g._id AND u.civil_status_id = c._id AND " +
                "role_id = 6 AND default_health_center = " + healthCenterId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
            Users user = new Users(c.getString(c.getColumnIndex("first_name")),
                    c.getString(c.getColumnIndex("middle_name")),
                    c.getString(c.getColumnIndex("last_name")),
                    c.getString(c.getColumnIndex("birthdate")),
                    c.getString(c.getColumnIndex("gender")),
                    c.getString(c.getColumnIndex("civil_status")),
                    c.getString(c.getColumnIndex("blood_type")));

            results.add(user);
        }

        return results;

    }

    public ArrayList<String> getBarangays () {

        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_barangays ORDER BY barangay_name";

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("barangay_name")));
        }

        return results;
    }

    public ArrayList<String> getCities () {

        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_cities ORDER BY city_name";

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("city_name")));
        }

        return results;
    }

    public void newPatient (Users user) {

        int genderId;
        int civilId;
        String genderSql = "SELECT _id FROM tbl_genders WHERE gender_name = " + user.getGender();

        Cursor cGender = getBetterDb.rawQuery(genderSql, null);
        cGender.moveToFirst();
        genderId = cGender.getInt(cGender.getColumnIndex("_id"));

        String civilSql = "SELECT _id FROM tbl_civil_statuses WHERE civil_status_name = " + user.getCivilStatus();

        Cursor cCivil = getBetterDb.rawQuery(civilSql, null);
        cCivil.moveToFirst();
        civilId = cCivil.getInt(cCivil.getColumnIndex("_id"));


        String sql = "INSERT INTO tbl_users_upload (first_name, middle_name, " +
                "last_name, birthdate, gender_id, civil_status_id, role_id, blood_type)" +
                " VALUES(" + user.getFirstName() + ", " + user.getMiddleName() + ", " +
                user.getLastName() + ", " + user.getBirthdate() + ", " + genderId + ", " +
                civilId + ", " + 6 + ", " + user.getBloodType() + ")";

        getBetterDb.execSQL(sql);

    }


}
