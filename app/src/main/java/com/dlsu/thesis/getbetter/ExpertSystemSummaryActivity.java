package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.PositiveResults;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class ExpertSystemSummaryActivity extends Activity {

    private DataAdapter getBetterDb;
    private ArrayList<PositiveResults> positiveSymptoms;
    private ArrayList<Integer> chiefComplaintIds;
    private ArrayList<String> chiefComplaints;
    private TextView summaryHpiContent;

    private String patientName;
    private String patientGender;
    private String patientAge;
    private String caseRecordId;

    PatientExpertSessionManager patientSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_system_summary);

        patientSession = new PatientExpertSessionManager(getApplicationContext());

        HashMap<String, String> patient = patientSession.getPatientDetails();
        caseRecordId = patient.get(patientSession.CASE_RECORD_ID);
        patientName = patient.get(patientSession.PATIENT_NAME);
        patientAge = patient.get(patientSession.PATIENT_AGE);
        patientGender = patient.get(patientSession.PATIENT_GENDER);

        ArrayList<String> plausibleImpressions;
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
        Log.d("result size: ", positiveSymptoms.size() + "");
        HPI = generateIntroductionSentence();
        summaryHpiContent.setText(HPI);
        for(int i = 0; i < positiveSymptoms.size(); i++) {
            summaryPositiveSymptomList.append(positiveSymptoms.get(i).getPositiveName() + "\n");
            summaryHpiContent.append(positiveSymptoms.get(i).getPositiveAnswerPhrase() + "\n");
        }

        backPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientSession.endExpertSystem();
                Intent intent = new Intent(getApplicationContext(), PatientListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

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
        positiveSymptoms = getBetterDb.getPositiveSymptoms(Integer.parseInt(caseRecordId));

        Log.d("before removing duplicates", positiveSymptoms.size()+"");



        getBetterDb.closeDatabase();
    }

    private String generateIntroductionSentence () {

        String introductionSentence = "";

        introductionSentence = "The patient, " + patientName + ", " + patientAge + " years old, " +
                patientGender + ", is complaining about ";

        for(int i = 0; i < chiefComplaints.size(); i++) {
            introductionSentence += chiefComplaints.get(i) + " and ";
        }

        return introductionSentence;
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

    private void insertHpiImpressionsToDatabase (ArrayList<String> plausibleImpressions, String hpiContent) {

        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
