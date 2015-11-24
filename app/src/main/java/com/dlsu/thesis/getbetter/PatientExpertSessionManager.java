package com.dlsu.thesis.getbetter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * GetBetter. Created by Mike Dayupay on 11/21/15.
 */
public class PatientExpertSessionManager {

    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "ExpertSystemPref";

    public static final String PATIENT_ID = "patientId";

    public static final String CASE_RECORD_ID = "caseRecordId";

    public static final String PATIENT_NAME = "patientName";

    public static final String PATIENT_AGE = "patientAge";

    public static final String PATIENT_GENDER = "patientGender";

    public PatientExpertSessionManager (Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);

        editor = pref.edit();
    }

    public void createPatientExpertSession (String patientId, String caseRecordId, String patienName,
                                            String patientAge, String patientGender) {

        editor.putString(PATIENT_ID, patientId);
        editor.putString(CASE_RECORD_ID, caseRecordId);
        editor.putString(PATIENT_NAME, patienName);
        editor.putString(PATIENT_AGE, patientAge);
        editor.putString(PATIENT_GENDER, patientGender);
        editor.commit();
    }

    public HashMap<String, String> getPatientDetails () {

        HashMap<String, String> patient = new HashMap<>();

        patient.put(PATIENT_ID, pref.getString(PATIENT_ID, null));

        patient.put(CASE_RECORD_ID, pref.getString(CASE_RECORD_ID, null));

        patient.put(PATIENT_NAME, pref.getString(PATIENT_NAME, null));

        patient.put(PATIENT_AGE, pref.getString(PATIENT_AGE, null));

        patient.put(PATIENT_GENDER, pref.getString(PATIENT_GENDER, null));

        return patient;
    }

    public void endExpertSystem () {

        editor.clear();
        editor.commit();

        Intent intent = new Intent(_context, PatientListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(intent);
    }
}
