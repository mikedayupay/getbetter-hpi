package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Impressions;
import com.dlsu.thesis.getbetter.objects.PatientAnswers;
import com.dlsu.thesis.getbetter.objects.Symptom;
import com.dlsu.thesis.getbetter.objects.SymptomFamily;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ExpertSystemActivity extends Activity {

    private RadioGroup radioGroupResponses;

    private int currentImpressionIndex;
    private int currentSymptomIndex;
    private String caseRecordId;
    private int symptomFamilyId;
    private boolean flag;
    private boolean clickFlag = true;

    private Symptom positiveSymptom, ruledOutSymptom;
    private SymptomFamily generalQuestion;
    private DataAdapter getBetterDb;

    private ArrayList<Impressions> impressionsSymptoms;
    private ArrayList<String> ruledOutImpressionList, plausibleImpressionList,
            positiveSymptomList, ruledOutSymptomList;
    private ArrayList<Symptom> questions;
    private ArrayList<Integer> chiefComplaintId;
    private ArrayList<PatientAnswers> answers;

    PatientExpertSessionManager patientSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expert_system);

        Bundle extras = getIntent().getExtras();
        patientSession = new PatientExpertSessionManager(getApplicationContext());

        HashMap<String, String> patient = patientSession.getPatientDetails();
        caseRecordId = patient.get(PatientExpertSessionManager.CASE_RECORD_ID);

        Log.d("case record id", caseRecordId + "");

        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        plausibleImpressionList = new ArrayList<>();
        positiveSymptomList = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        radioGroupResponses = (RadioGroup)findViewById(R.id.radio_group_responses);


        chiefComplaintId = new ArrayList<>();
        if(extras != null) {
            chiefComplaintId = extras.getIntegerArrayList("chiefComplaintId");
        }

        initializeDatabase();
        resetDatabaseFlags();
        initializeImpressionList();
        updateAnsweredStatusSymptomFamily();


        currentImpressionIndex = 0;
        currentSymptomIndex = 0;
        getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
        Log.d("questions size", questions.size() + "");
        Log.d("current symptom", questions.get(currentSymptomIndex).getSymptomNameEnglish());
        Log.d("impression size", impressionsSymptoms.size() + "");
        reloadExpertSystem();
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeImpressionList () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        impressionsSymptoms = new ArrayList<>();
        for(int i = 0; i < chiefComplaintId.size(); i++)
            impressionsSymptoms.addAll(getBetterDb.getImpressions(chiefComplaintId.get(i)));

        impressionsSymptoms = new ArrayList<>(new LinkedHashSet<>(impressionsSymptoms)); // Remove duplicates

        for(int i = 0; i < impressionsSymptoms.size(); i++) {

            ArrayList<Symptom> symptoms = getBetterDb.getSymptoms(impressionsSymptoms.get(i).getImpressionId());
            impressionsSymptoms.get(i).setSymptoms(symptoms);
        }

        getBetterDb.closeDatabase();
    }

    public void reloadExpertSystem() {

        String questionAsked;
        TextView probingTitle = (TextView)findViewById(R.id.probing_title);
        TextView probingImpression = (TextView)findViewById(R.id.probing_impression);
        TextView symptomsList = (TextView)findViewById(R.id.symptoms_list);
        TextView displayQuestion = (TextView)findViewById(R.id.question_label);
        TextView positiveSymptomsList = (TextView)findViewById(R.id.positive_symptom_list);
        TextView impressionList = (TextView)findViewById(R.id.impressions_list);
        TextView ruledOutImpressions = (TextView)findViewById(R.id.ruled_out_impressions_list);

        //Log.d("symptom family id", questions.get(currentSymptomIndex).getSymptomFamilyId() + "");
        if(isSymptomFamilyQuestionAnswered(questions.get(currentSymptomIndex).getSymptomFamilyId())) {
            questionAsked = questions.get(currentSymptomIndex).getQuestionEnglish();
            flag = true;
        } else {
            getGeneralQuestion(questions.get(currentSymptomIndex).getSymptomFamilyId());
            questionAsked = generalQuestion.getGeneralQuestionEnglish();
            symptomFamilyId = generalQuestion.getSymptomFamilyId();
            flag = false;
        }

        displayQuestion.setText(questionAsked);
        probingImpression.setText(impressionsSymptoms.get(currentImpressionIndex).getImpression());

        impressionList.setText("");
        ruledOutImpressions.setText("");
        symptomsList.setText("");
        positiveSymptomsList.setText("");

        for(int i = 0; i < impressionsSymptoms.size(); i++) {
            impressionList.append(impressionsSymptoms.get(i).getImpression() + "\n");
        }

        for (String impressions :
                ruledOutImpressionList) {
            ruledOutImpressions.append(impressions + "\n");
        }

        for(int i = 0; i < impressionsSymptoms.get(currentImpressionIndex).getSymptoms().size(); i++) {
            symptomsList.append(impressionsSymptoms.get(currentImpressionIndex).getSymptoms().
                    get(i).getSymptomNameEnglish() + "\n");
        }

        for (int i = 0; i < positiveSymptomList.size(); i++) {
            positiveSymptomsList.append(positiveSymptomList.get(i) + "\n");
        }
    }

    public void getQuestions(int impressionId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        questions.clear();
        questions.addAll(getBetterDb.getQuestions(impressionId));
        Log.d("questions size", questions.size() + "");
        getBetterDb.closeDatabase();
    }

    public void getGeneralQuestion (int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        generalQuestion = getBetterDb.getGeneralQuestion(symptomFamilyId);
        getBetterDb.closeDatabase();
    }

    public void checkForRuledOutImpression(int impressionId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.d("Impression id", impressionId + "");
        ArrayList<String> hardSymptoms = getBetterDb.getHardSymptoms(impressionId);

        for(String hard : hardSymptoms) {
            Log.d("Hard symptoms: ", hard);
        }

        for(int i = 0; i < ruledOutSymptomList.size(); i++)
            Log.d("ruled out", ruledOutSymptomList.get(i));

        if(ruledOutSymptomList.containsAll(hardSymptoms)) {
            ruledOutImpressionList.add(impressionsSymptoms.get(currentImpressionIndex).getImpression());
            Toast.makeText(this, "ruled out impression: " + impressionsSymptoms.
                    get(currentImpressionIndex).getImpression(), Toast.LENGTH_LONG).show();
        } else {
            plausibleImpressionList.add(impressionsSymptoms.get(currentImpressionIndex).getImpression());
            Toast.makeText(this, "plausible impression: " + impressionsSymptoms.
                    get(currentImpressionIndex).getImpression(), Toast.LENGTH_LONG).show();
        }

        getBetterDb.closeDatabase();
    }

    public void goNext (View view) {

        if(clickFlag) {
            clickFlag = false;
            radioGroupResponses.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radio_yes) {
                        if (flag) {
                            positiveSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                            positiveSymptomList = new ArrayList<>(new LinkedHashSet<>(positiveSymptomList));
                            updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                            answers.add(new PatientAnswers(Integer.parseInt(caseRecordId), questions.get(currentSymptomIndex).getSymptomId(), "Yes"));
                        } else {
                            updateAnsweredStatusSymptomFamily(1);
                        }
                    } else if (checkedId == R.id.radio_no) {
                        if (flag) {
                            ruledOutSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                            ruledOutSymptomList = new ArrayList<>(new LinkedHashSet<>(ruledOutSymptomList));
                            updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                            answers.add(new PatientAnswers(Integer.parseInt(caseRecordId), questions.get(currentSymptomIndex).getSymptomId(), "No"));
                        } else {
                            updateAnsweredStatusSymptomFamily(0);
                            updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                            ruledOutSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                            ruledOutSymptomList = new ArrayList<>(new LinkedHashSet<>(ruledOutSymptomList));
                            answers.add(new PatientAnswers(Integer.parseInt(caseRecordId), questions.get(currentSymptomIndex).getSymptomId(), "No"));
                        }
                    }
                }
            });


            radioGroupResponses.clearCheck();


            if(flag) {

                currentSymptomIndex++;

                if(currentSymptomIndex >= questions.size()) {

                    checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                    currentSymptomIndex = 0;
                    currentImpressionIndex++;


                    if(currentImpressionIndex >= impressionsSymptoms.size()) {
                        currentImpressionIndex = impressionsSymptoms.size() - 1;

                        exitExpertSystem();
                    } else {
                        getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                        while (questions.size() == 0) {
                            currentImpressionIndex++;

                            if(currentImpressionIndex >= impressionsSymptoms.size()) {
                                currentImpressionIndex = impressionsSymptoms.size() - 1;

                                exitExpertSystem();
                            }
                            checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                        }

                        reloadExpertSystem();
                    }

                } else {
                    reloadExpertSystem();
                }

            } else {
                if(!isSymptomFamilyPositive(questions.get(currentSymptomIndex).getSymptomFamilyId())){
                    currentSymptomIndex++;
                    if(currentSymptomIndex >= questions.size()) {

                        checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                        currentSymptomIndex = 0;
                        currentImpressionIndex++;


                        if(currentImpressionIndex >= impressionsSymptoms.size()) {
                            currentImpressionIndex = impressionsSymptoms.size() - 1;

                            exitExpertSystem();
                        } else {
                            getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                            while (questions.size() == 0) {
                                currentImpressionIndex++;
                                checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                                if(currentImpressionIndex >= impressionsSymptoms.size()) {
                                    currentImpressionIndex = impressionsSymptoms.size() - 1;

                                    exitExpertSystem();
                                }
                            }

                            reloadExpertSystem();
                        }

                    } else {
                        reloadExpertSystem();
                    }

                } else {

                    reloadExpertSystem();
                }

            }
            clickFlag = true;
        }

    }

    public void goPrevious(View view) {

        radioGroupResponses.clearCheck();
        currentSymptomIndex--;

        if(currentSymptomIndex < 0) {
            currentSymptomIndex = 0;
            currentImpressionIndex--;

            if(currentImpressionIndex < 0) {
                currentImpressionIndex = 0;
            } else {
                reloadExpertSystem();
            }

        } else {
            reloadExpertSystem();
        }
    }

    public void updateAnsweredFlagPositive(int symptomId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.updateAnsweredFlagPositive(symptomId);
        getBetterDb.closeDatabase();
    }

    public void updateAnsweredStatusSymptomFamily() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < chiefComplaintId.size(); i++) {
            getBetterDb.updateAnsweredStatusSymptomFamily(chiefComplaintId.get(i));
        }

        getBetterDb.closeDatabase();

    }

    public void updateAnsweredStatusSymptomFamily(int answer) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.updateAnsweredStatusSymptomFamily(generalQuestion.getSymptomFamilyId(), answer);
    }

    public boolean isSymptomFamilyQuestionAnswered(int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = getBetterDb.symptomFamilyIsAnswered(symptomFamilyId);
        getBetterDb.closeDatabase();
        return value;
    }

    public boolean isSymptomFamilyPositive (int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = getBetterDb.symptomFamilyAnswerStatus(symptomFamilyId);
        getBetterDb.closeDatabase();
        return value;
    }

    public void exitExpertSystem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExpertSystemActivity.this);
        builder.setTitle("All possible impressions have been diagnosed. Submit your answers?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAnswersToDatabase();

                Intent intent = new Intent(ExpertSystemActivity.this, ExpertSystemSummaryActivity.class);
                intent.putStringArrayListExtra("Plausible Impressions", plausibleImpressionList);
                intent.putStringArrayListExtra("Ruled Out Impressions", ruledOutImpressionList);
                intent.putIntegerArrayListExtra("Chief Complaints", chiefComplaintId);
                startActivity(intent);
                finish();
            }

        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void resetDatabaseFlags() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.resetSymptomAnsweredFlag();
        getBetterDb.resetSymptomFamilyFlags();
        getBetterDb.closeDatabase();
    }

    public void saveAnswersToDatabase() {

        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.insertAnswersToDatabase(answers);
        getBetterDb.closeDatabase();
        
    }
}
