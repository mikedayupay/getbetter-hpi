package com.dlsu.thesis.getbetter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dlsu.thesis.getbetter.objects.Impressions;
import com.dlsu.thesis.getbetter.objects.PatientAnswers;
import com.dlsu.thesis.getbetter.objects.PatientContent;
import com.dlsu.thesis.getbetter.objects.Symptom;
import com.dlsu.thesis.getbetter.objects.SymptomFamily;

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

    private static final String SYMPTOM_LIST = "tbl_symptom_list";
    private static final String SYMPTOM_FAMILY = "tbl_symptom_family";
    private static final String PATIENT_ANSWERS = "tbl_patient_answers";

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

    public ArrayList<Impressions> getImpressions (int complaintId) {

        ArrayList<Impressions> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_case_impression AS i, tbl_impressions_of_complaints AS s " +
        "WHERE i._id = s.impression_id AND s.complaint_id = " + complaintId;

        Cursor c = getBetterDb.rawQuery(sql, null);

        while(c.moveToNext()) {
            Impressions impressions = new Impressions(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("medical_term")),
                    c.getString(c.getColumnIndexOrThrow("scientific_name")),
                    c.getString(c.getColumnIndexOrThrow("local_name")),
                    c.getString(c.getColumnIndexOrThrow("treatment_protocol")),
                    c.getString(c.getColumnIndexOrThrow("remarks")));

            results.add(impressions);
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

    public ArrayList<Symptom> getSymptoms(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id";

        Cursor c = getBetterDb.rawQuery(sql, null);
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")));

            results.add(symptom);
        }

        return results;
    }

    public ArrayList<Symptom> getQuestions(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id AND s.is_answered = 0";

        Cursor c = getBetterDb.rawQuery(sql, null);
        Log.d("query count", c.getCount() + "");
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")));

            results.add(symptom);
        }

        return results;
    }

    public SymptomFamily getGeneralQuestion (int symptomFamilyId) {

        String sql = "SELECT * FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        SymptomFamily generalQuestion;
        generalQuestion = new SymptomFamily(c.getInt(c.getColumnIndexOrThrow("_id")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_english")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_tagalog")),
                c.getString(c.getColumnIndexOrThrow("general_question_english")),
                c.getString(c.getColumnIndexOrThrow("responses_english")),
                c.getInt(c.getColumnIndexOrThrow("related_chief_complaint_id")));

        return generalQuestion;
    }

    public boolean symptomFamilyIsAnswered (int symptomFamilyId) {

        Log.d("id", symptomFamilyId + "");
        String sql = "SELECT answered_flag FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();


        if(c.getCount() == 0) {
            return true;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answered_flag")) == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean symptomFamilyAnswerStatus (int symptomFamilyId) {

        Log.d("symptom family id", symptomFamilyId + "");
        String sql = "SELECT answer_status FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        if(c.getCount() == 0) {
            return false;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answer_status")) == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void updateAnsweredStatusSymptomFamily(int chiefComplaintId) {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 1);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SYMPTOM_FAMILY, values, "related_chief_complaint_id = " + chiefComplaintId, null);

        Log.d("updated rows symptom family flags", count + "");
    }

    public void updateAnsweredStatusSymptomFamily(int symptomFamilyId, int answer) {

        String sql = "UPDATE tbl_symptom_family SET answered_flag = 1, answer_status = " + answer +
                " WHERE _id = " + symptomFamilyId;

        getBetterDb.execSQL(sql);
    }

    public void updateAnsweredFlagPositive(int symptomId) {

        ContentValues values = new ContentValues();
        values.put("is_answered", 1);
        int count = getBetterDb.update(SYMPTOM_LIST, values, "_id = " + symptomId, null);

        Log.d("update rows flag positive", count + "");
    }

    public void resetSymptomAnsweredFlag () {

        ContentValues values = new ContentValues();
        values.put("is_answered", 0);
        int count = getBetterDb.update(SYMPTOM_LIST, values, null, null);

        Log.d("updated rows reset", count + "");
    }

    public void resetSymptomFamilyFlags() {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 0);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SYMPTOM_FAMILY, values, null, null);

        Log.d("updated rows reset", count +"");
    }

    public ArrayList<String> getHardSymptoms (int impressionId) {

        ArrayList<String> results = new ArrayList<>();

        String sql = "SELECT s.symptom_name_english AS symptom_name_english FROM tbl_symptom_list AS s, " +
                "tbl_symptom_of_impression AS i WHERE i.impression_id = " + impressionId +
                " AND i.hard_symptom = 1 AND i.symptom_id = s._id";
        Cursor c = getBetterDb.rawQuery(sql, null);

        while(c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("symptom_name_english")));
        }

        return results;
    }

    public void insertAnswersToDatabase (ArrayList<PatientAnswers> answers) {

        long rowId;
        for(int i = 0; i < answers.size(); i++) {

            ContentValues values = new ContentValues();
            values.put("case_record_id", answers.get(i).getCaseRecordId());
            values.put("question_id", answers.get(i).getSymptomId());
            values.put("answer", answers.get(i).getAnswer());

            rowId = getBetterDb.insert(PATIENT_ANSWERS, null, values);
            Log.d("row id inserted", rowId + "");
        }



    }
}
