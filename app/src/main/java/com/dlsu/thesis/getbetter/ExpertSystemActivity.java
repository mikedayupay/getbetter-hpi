package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Impressions;
import com.dlsu.thesis.getbetter.objects.Question;
import com.dlsu.thesis.getbetter.objects.Symptom;
import com.dlsu.thesis.getbetter.objects.SymptomFamily;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ExpertSystemActivity extends Activity {

    private RadioGroup radioGroupResponses;

    private DataAdapter getBetterDb;
    private ArrayList<Impressions> impressionsSymptoms;
    private int currentImpressionIndex;
    private int currentSymptomIndex;
    private Question question;
    private SymptomFamily generalQuestion;
    private ArrayList<String> ruledOutImpressionList, possibleImpressionList;
    private ArrayList<Symptom> positiveSymptoms, ruledOutSymptomList;
    private ArrayList<Integer> positiveSymptomsIds, chiefComplaintId, ruledOutSymptomIds;
    private Symptom positiveSymptom, ruledOutSymptom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expert_system);

        Bundle extras = getIntent().getExtras();

        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        possibleImpressionList = new ArrayList<>();
        positiveSymptoms = new ArrayList<>();
        ruledOutSymptomIds = new ArrayList<>();
        positiveSymptomsIds = new ArrayList<>();
        radioGroupResponses = (RadioGroup)findViewById(R.id.radio_group_responses);


        chiefComplaintId = new ArrayList<>();
        if(extras != null) {
            chiefComplaintId = extras.getIntegerArrayList("chiefComplaintId");
        }

        initializeDatabase();
        initializeImpressionList();
        updateAnsweredStatusSymptomFamily();

        currentImpressionIndex = 0;
        currentSymptomIndex = 0;
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
            possibleImpressionList.add(impressionsSymptoms.get(i).getImpression());
        }

        getBetterDb.closeDatabase();
    }

    public void initializeQuestions() {

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

        if (isSymptomFamilyQuestionAnswered()) {
            questionAsked = impressionsSymptoms.get(currentImpressionIndex).getSymptoms().
                    get(currentSymptomIndex).getQuestionEnglish();
        } else {
            getGeneralQuestion(impressionsSymptoms.get(currentImpressionIndex).getSymptoms().
                    get(currentSymptomIndex).getSymptomFamilyId());
            questionAsked = generalQuestion.getGeneralQuestionEnglish();
            currentSymptomIndex--;
        }


        displayQuestion.setText(questionAsked);
        probingImpression.setText(impressionsSymptoms.get(currentImpressionIndex).getImpression());

        impressionList.setText("");
        ruledOutImpressions.setText("");
        symptomsList.setText("");
        positiveSymptomsList.setText("");

        for(String impressions: possibleImpressionList) {
            impressionList.append(impressions + "\n");
        }

        for (String impressions :
                ruledOutImpressionList) {
            ruledOutImpressions.append(impressions + "\n");
        }

        for(int i = 0; i < positiveSymptoms.size(); i++)
            Log.d("positiveSymptom", positiveSymptoms.get(i).getSymptomNameEnglish());


        for(int i = 0; i < impressionsSymptoms.get(currentImpressionIndex).getSymptoms().size();i++)
            symptomsList.append(impressionsSymptoms.get(currentImpressionIndex).getSymptoms().get(i).
                    getSymptomNameEnglish() + "\n");


        for (int i = 0; i < positiveSymptoms.size(); i++) {
            positiveSymptomsList.append(positiveSymptoms.get(i).getSymptomNameEnglish() + "\n");
        }

    }

    public void checkForRuledOutImpression(int impressionId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> hardSymptoms = getBetterDb.getHardSymptoms(impressionId);

        if(!(positiveSymptoms.containsAll(hardSymptoms))) {
            ruledOutImpressionList.add(impressionsSymptoms.get(currentImpressionIndex).getImpression());
            possibleImpressionList.remove(impressionsSymptoms.get(currentImpressionIndex).getImpression());
        }

        getBetterDb.closeDatabase();
    }


    public void goNext(View view) {

        radioGroupResponses.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_yes) {
                    if(isSymptomFamilyQuestionAnswered()) {
                        positiveSymptom = impressionsSymptoms.get(currentImpressionIndex).
                                getSymptoms().get(currentSymptomIndex);
                        positiveSymptomsIds.add(impressionsSymptoms.get(currentImpressionIndex).
                                getSymptoms().get(currentSymptomIndex).getSymptomId());
                    } else {
                        updateAnsweredStatusSymptomFamily(1);
                    }
                } else if (checkedId == R.id.radio_no) {
                    if(isSymptomFamilyQuestionAnswered()) {
                        ruledOutSymptom = impressionsSymptoms.get(currentImpressionIndex).
                                getSymptoms().get(currentSymptomIndex);
                        ruledOutSymptomIds.add(impressionsSymptoms.get(currentImpressionIndex).
                                getSymptoms().get(currentSymptomIndex).getSymptomId());
                    } else {
                        updateAnsweredStatusSymptomFamily(0);
                    }
                }
            }
        });

        radioGroupResponses.clearCheck();
        positiveSymptoms.add(positiveSymptom);
        ruledOutSymptomList.add(ruledOutSymptom);
        currentSymptomIndex++;

        if(currentSymptomIndex >= impressionsSymptoms.get(currentImpressionIndex).getSymptoms().size()) {

            currentSymptomIndex = 0;

            //skip question/symptom if it is already asked
            if(symptomIsAnswered() || !(symptomFamilyQuestionAnswerStatus())) {
                currentSymptomIndex++;
            }

            checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
            currentImpressionIndex++;

            if(currentImpressionIndex >= impressionsSymptoms.size()) {
                currentImpressionIndex = impressionsSymptoms.size() - 1;

                exitExpertSystem();
            } else {
                reloadExpertSystem();
            }

        } else {
            if(symptomIsAnswered() || !(symptomFamilyQuestionAnswerStatus())) {
                currentSymptomIndex++;
            }

            reloadExpertSystem();
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

    public boolean symptomIsAnswered() {

        return (positiveSymptomsIds.contains(impressionsSymptoms.get(currentImpressionIndex).
                getSymptoms().get(currentSymptomIndex).getSymptomId()) ||
                ruledOutSymptomIds.contains(impressionsSymptoms.get(currentImpressionIndex).
                        getSymptoms().get(currentSymptomIndex).getSymptomId()));
    }


//    public void onRadioButtonClicked (View view) {
//
//        boolean checked = ((RadioButton) view).isChecked();
//
//
//        switch (view.getId()) {
//            case R.id.radio_yes:
//
//                if(checked) {
//                    tempStorage = impressionsSymptoms.get(currentImpressionIndex).
//                            getSymptoms().get(currentSymptomIndex);
//
//                    answerStatus = true;
//                }
//
//                break;
//
//            case R.id.radio_no:
//                if(checked) {
//
//                    tempStorage = impressionsSymptoms.get(currentImpressionIndex).
//                            getSymptoms().get(currentSymptomIndex);
//
//                    answerStatus = false;
//                }
//                break;
//        }
//    }

//    public void getQuestions (String symptom) {
//
//        try {
//            getBetterDb.openDatabaseForRead();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        question = getBetterDb.getQuestion(symptom);
//        getBetterDb.closeDatabase();
//    }

    public void updateAnsweredStatusSymptomFamily() {

        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int complaintId: chiefComplaintId) {
            getBetterDb.updateAnsweredStatusSymptomFamily(complaintId);
        }

        getBetterDb.closeDatabase();

    }

    public void updateAnsweredStatusSymptomFamily(int answer) {

        int id = impressionsSymptoms.get(currentImpressionIndex).getSymptoms().
                get(currentSymptomIndex + 1).getSymptomFamilyId();
        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.updateAnsweredStatusSymptomFamily(id, answer);
        getBetterDb.closeDatabase();
    }

    public boolean isSymptomFamilyQuestionAnswered() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean isAnswered = getBetterDb.symptomFamilyIsAnswered(impressionsSymptoms.get(currentImpressionIndex).
                getSymptoms().get(currentSymptomIndex).getSymptomFamilyId());

        getBetterDb.closeDatabase();
        return isAnswered;
    }

    public boolean symptomFamilyQuestionAnswerStatus() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean answerStatus = getBetterDb.symptomFamilyAnswer(impressionsSymptoms.
                get(currentImpressionIndex).getSymptoms().get(currentSymptomIndex).getSymptomFamilyId());

        getBetterDb.closeDatabase();
        return answerStatus;
    }

    public void getGeneralQuestion(int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        generalQuestion = getBetterDb.getGeneralQuestion(symptomFamilyId);
        getBetterDb.closeDatabase();
    }

//    public void displayQuestions() {
//
//        layout_questions_inner_container = (ViewGroup)findViewById(R.id.layout_questions_inner_container);
//
//
//
//    }

    public void exitExpertSystem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExpertSystemActivity.this);
        builder.setTitle("All possible impressions have been diagnosed. Submit your answers?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
}
