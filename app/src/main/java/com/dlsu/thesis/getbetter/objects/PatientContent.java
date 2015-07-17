package com.dlsu.thesis.getbetter.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GetBetter. Created by Mike Dayupay on 7/16/15.
 */
public class PatientContent {


    public static List<Patient> PATIENTS = new ArrayList<Patient>();

    public static Map<Integer, Patient> PATIENT_MAP = new HashMap<Integer, Patient>();

    public static void addPatient (Patient patient) {
        PATIENTS.add(patient);
        PATIENT_MAP.put(patient.getId(), patient);
    }

    public static class Patient extends Users {

        private String bloodType;

        public Patient (int id, String firstName, String middleName, String lastName, String birthdate,
                        String gender, String civilStatus, String bloodType) {

            super(id, firstName, middleName, lastName, birthdate, gender, civilStatus);
            this.bloodType = bloodType;
        }

        public String getBloodType() {
            return bloodType;
        }
    }



}
