package com.dlsu.thesis.getbetter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dlsu.thesis.getbetter.objects.PatientContent;
import com.dlsu.thesis.getbetter.objects.Question;

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

    public DataAdapter openDatabaseForRead() throws SQLException {

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

    public DataAdapter openDatabaseForWrite() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getWritableDatabase();
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

        String sql = "SELECT * FROM tbl_users WHERE email = '" + username + "' AND pass = '" + password + "'";

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
        String sql = "SELECT _id FROM tbl_health_centers WHERE health_center_name = '" + healthCenter + "'";

        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getInt(c.getColumnIndex("_id"));

        return result;
    }

    public ArrayList<PatientContent.Patient> getPatients (int healthCenterId) {

        ArrayList<PatientContent.Patient> results = new ArrayList<PatientContent.Patient>();
        String sql = "SELECT u._id AS id, u.first_name AS first_name, u.middle_name AS middle_name, " +
                "u.last_name AS last_name, u.birthdate AS birthdate, g.gender_name AS gender, " +
                "c.civil_status_name AS civil_status, u.blood_type AS blood_type " +
                "FROM tbl_users AS u, tbl_genders AS g, tbl_civil_statuses AS c " +
                "WHERE u.gender_id = g._id AND u.civil_status_id = c._id AND " +
                "role_id = 6 AND default_health_center = " + healthCenterId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
             PatientContent.Patient patient = new PatientContent.Patient(c.getInt(c.getColumnIndex("id")),
                     c.getString(c.getColumnIndex("first_name")),
                    c.getString(c.getColumnIndex("middle_name")),
                    c.getString(c.getColumnIndex("last_name")),
                    c.getString(c.getColumnIndex("birthdate")),
                    c.getString(c.getColumnIndex("gender")),
                    c.getString(c.getColumnIndex("civil_status")),
                    c.getString(c.getColumnIndex("blood_type")));

            results.add(patient);
        }

        return results;

    }

//    public Users getPatient (final long id) {
//
//        String sql = "SELECT * FROM tbl_users WHERE _id = " + id;
//
//        Cursor c = getBetterDb.rawQuery(sql, null);
//        c.moveToFirst();
//        Users result = new Users(c.getString(c.getColumnIndex("first_name")),
//                c.getString(c.getColumnIndex("middle_name")),
//                c.getString(c.getColumnIndex("last_name")),
//                c.getString(c.getColumnIndex("birthdate")),
//                c.getString(c.getColumnIndex("gender")),
//                c.getString(c.getColumnIndex("civil_status")),
//                c.getString(c.getColumnIndex("blood_type")));
//
//        return result;
//    }

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

    public void newPatient (PatientContent.Patient patient, int healthCenterId) {

        int genderId;
        int civilId;
        String genderSql = "SELECT _id FROM tbl_genders WHERE gender_name = '" + patient.getGender() + "'";

        Cursor cGender = getBetterDb.rawQuery(genderSql, null);
        cGender.moveToFirst();
        genderId = cGender.getInt(cGender.getColumnIndex("_id"));

        String civilSql = "SELECT _id FROM tbl_civil_statuses WHERE civil_status_name = '" + patient.getCivilStatus() + "'";

        Cursor cCivil = getBetterDb.rawQuery(civilSql, null);
        cCivil.moveToFirst();
        civilId = cCivil.getInt(cCivil.getColumnIndex("_id"));



        String sql = "INSERT INTO tbl_users_upload (first_name, middle_name, " +
                "last_name, birthdate, gender_id, civil_status_id, role_id, blood_type, default_health_center)" +
                " VALUES('" + patient.getFirstName() + "', '" + patient.getMiddleName() + "', '" +
                patient.getLastName() + "', '" + patient.getBirthdate() + "', " + genderId + ", " +
                civilId + ", " + 6 + ", '" + patient.getBloodType() + "', " + healthCenterId + ")";

        getBetterDb.execSQL(sql);

        String sql2 = "INSERT INTO tbl_users (first_name, middle_name, " +
                "last_name, birthdate, gender_id, civil_status_id, role_id, blood_type, default_health_center)" +
                " VALUES('" + patient.getFirstName() + "', '" + patient.getMiddleName() + "', '" +
                patient.getLastName() + "', '" + patient.getBirthdate() + "', " + genderId + ", " +
                civilId + ", " + 6 + ", '" + patient.getBloodType() + "', " + healthCenterId + ")";

        getBetterDb.execSQL(sql2);

    }

    public ArrayList<String> getImpressions (int complaintId) {

        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT medical_term FROM tbl_case_impression AS i, tbl_impressions_of_complaints AS s " +
        "WHERE i._id = s.impression_id AND s.complaint_id = " + complaintId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while(c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("medical_term")));
        }

        return results;
    }

    public ArrayList<Question> getQuestions (String symptomName) {

        ArrayList<Question> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_symptom_questions WHERE symptom_id = (SELECT _id " +
                "FROM tbl_symptom_list WHERE symptom_name_english = '" + symptomName + "')";

        Cursor c = getBetterDb.rawQuery(sql, null);

        while (c.moveToNext()) {
            Question question = new Question(c.getInt(c.getColumnIndexOrThrow("question_id")),
                    c.getString(c.getColumnIndexOrThrow("english_question")),
                    c.getString(c.getColumnIndexOrThrow("tagalog_question")),
                    c.getString(c.getColumnIndexOrThrow("responses")),
                    c.getString(c.getColumnIndexOrThrow("action_needed")));

            results.add(question);
        }

        return results;
    }

    public int getImpressionId(String impression) {

        int impressionId;
        String sql = "SELECT _id FROM tbl_case_impression WHERE medical_term = '" + impression + "'";

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();
        impressionId = c.getInt(c.getColumnIndexOrThrow("_id"));

        return impressionId;
    }

    public ArrayList<String> getSymptoms(int impressionId) {

        ArrayList<String> results = new ArrayList<>();
        String sql = "SELECT symptom_name_english " +
                "FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id";

        Cursor c = getBetterDb.rawQuery(sql, null);
        while(c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("symptom_name_english")));
        }

        return results;

    }
}
