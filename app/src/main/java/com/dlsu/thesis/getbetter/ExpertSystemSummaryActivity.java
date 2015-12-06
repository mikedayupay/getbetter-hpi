package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.PositiveResults;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class ExpertSystemSummaryActivity extends AppCompatActivity {

    private DataAdapter getBetterDb;
    private ArrayList<PositiveResults> positiveSymptoms;
    private ArrayList<Integer> chiefComplaintIds;
    private ArrayList<String> chiefComplaints;
    private TextView summaryHpiContent;

    private String patientName;
    private String patientGender;
    private String patientAge;
    private int caseRecordId;
    private int patientId;
    private int healthCenterId;

    PatientExpertSessionManager patientSession;
    UserSessionManager userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_system_summary);

        patientSession = new PatientExpertSessionManager(getApplicationContext());
        userSession = new UserSessionManager(getApplicationContext());

        HashMap<String, String> user = userSession.getUserDetails();
        //Log.d("healthCenter", user.get(UserSessionManager.KEY_HEALTH_CENTER));


        HashMap<String, String> patient = patientSession.getPatientDetails();
        caseRecordId = Integer.parseInt(patient.get(patientSession.CASE_RECORD_ID));
        patientId = Integer.parseInt(patient.get(patientSession.PATIENT_ID));
        patientName = patient.get(patientSession.PATIENT_NAME);
        patientAge = patient.get(patientSession.PATIENT_AGE);
        patientGender = patient.get(patientSession.PATIENT_GENDER);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Patient: " + patientName);

        final ArrayList<String> plausibleImpressions;
        ArrayList<String> ruledOutImpressions;
        String HPI = "";


        chiefComplaintIds = new ArrayList<>();

        Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);

        TextView summaryRuledOutImpressionList = (TextView)findViewById(R.id.summary_ruled_out_impression_list);
        TextView summaryPlausibleImpressionList = (TextView)findViewById(R.id.summary_plausible_impression_list);
        TextView summaryPositiveSymptomList = (TextView)findViewById(R.id.summary_positive_symptoms_list);
        summaryHpiContent = (TextView)findViewById(R.id.summary_hpi_content);
        Button backPatientButton = (Button)findViewById(R.id.summary_back_patient_button);

        Bundle extras = getIntent().getExtras();
        plausibleImpressions = extras.getStringArrayList("Plausible Impressions");
        ruledOutImpressions = extras.getStringArrayList("Ruled Out Impressions");
        chiefComplaintIds = extras.getIntegerArrayList("Chief Complaints");

        if (plausibleImpressions != null) {
            for(String plausible : plausibleImpressions) {
                summaryPlausibleImpressionList.append(plausible + "\n");
            }
        } else {
            summaryPlausibleImpressionList.setText("There are no plausible impressions");
        }

        if (ruledOutImpressions != null) {
            for(String ruledOut : ruledOutImpressions) {
                summaryRuledOutImpressionList.append(ruledOut + "\n");
            }
        } else {
            summaryRuledOutImpressionList.setText("There are no ruled out impressions");
        }


        initializeDatabase();
        getPositiveSymptoms();
        getPositiveSymptoms();
        getChiefComplaints();
        healthCenterId = getHealthCenterId(user.get(userSession.KEY_HEALTH_CENTER));

        Log.d("result size: ", positiveSymptoms.size() + "");
        HPI = generateIntroductionSentence();


        for(int i = 0; i < positiveSymptoms.size(); i++) {

            summaryPositiveSymptomList.append(positiveSymptoms.get(i).getPositiveName() + "\n");

            if (positiveSymptoms.get(i).getPositiveName() == "High Fever") {
                HPI += "His " + positiveSymptoms.get(i).getPositiveAnswerPhrase() + " ";
            } else {
                HPI += getPronoun(patientGender) + " " + positiveSymptoms.get(i).getPositiveAnswerPhrase() + " ";
            }
        }

        summaryHpiContent.setText(HPI);

        final String finalHPI = HPI;
        backPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientSession.endExpertSystem();
                insertHpiImpressionsToDatabase(plausibleImpressions, chiefComplaints, finalHPI,
                        caseRecordId, patientId, healthCenterId);
                finish();
            }
        });
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void getPositiveSymptoms () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        positiveSymptoms = new ArrayList<>();
        positiveSymptoms = getBetterDb.getPositiveSymptoms(caseRecordId);

        Log.d("before removing duplicates", positiveSymptoms.size()+"");



        getBetterDb.closeDatabase();
    }

    private int getHealthCenterId(String healthCenter) {

        int id;

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        id = getBetterDb.getHealthCenterId(healthCenter);

        getBetterDb.closeDatabase();
        return id;

    }

    private String generateIntroductionSentence () {

        String introductionSentence = "";

        introductionSentence = "A " + patientGender + " patient, " + patientName + ", who is " + patientAge + " years old, " +
                " is complaining about " + attachComplaints();


        return introductionSentence;
    }

    private String attachComplaints () {

        String complaints = "";


        switch (chiefComplaintIds.size()) {

            case 1: complaints += " " + chiefComplaints.get(0) + ". ";
                break;
            case 2: complaints += " " + chiefComplaints.get(0) + " and " + chiefComplaints.get(1) + ". ";
                break;
            case 3: complaints += " " + chiefComplaints.get(0) + ", " + chiefComplaints.get(1) + ", and " + chiefComplaints.get(2) + ". ";
                break;
        }


        return complaints;
    }



    public void getChiefComplaints () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chiefComplaints = new ArrayList<>();
        for(int i = 0; i < chiefComplaintIds.size(); i++) {
            chiefComplaints.add(getBetterDb.getChiefComplaints(chiefComplaintIds.get(i)));
        }

        Log.d("chief complaints", chiefComplaints.size() + "");

        getBetterDb.closeDatabase();
    }

    private String getSubject (String username, int genderId) {

        String subject = "";

        return subject;
    }

    private String getPhrase () {

        String phrase = "";

        return phrase;
    }

    private String getPronoun (String gender) {

        String pronoun = "";

        if(gender.equals("Male")) {
            pronoun = "He";
        } else if (gender.equals("Female")) {
            pronoun = "She";
        }

        return pronoun;
    }

    private void insertHpiImpressionsToDatabase (ArrayList<String> plausibleImpressions,
                                                 ArrayList<String> chiefComplaints, String hpiContent,
                                                 int caseRecordId, int patientId, int healthCenterId) {

        String pImpressions = "";
        String cComplaints = "";

        for (String impressions :
                plausibleImpressions) {
            pImpressions += impressions + " ";
        }

        for (String complaints :
                chiefComplaints) {
            cComplaints += complaints + " ";
        }

        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.insertCaseRecord(pImpressions, cComplaints, caseRecordId, patientId,
                healthCenterId, hpiContent);

    }


}
