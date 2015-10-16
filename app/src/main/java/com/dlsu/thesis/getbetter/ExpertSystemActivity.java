package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Impressions;
import com.dlsu.thesis.getbetter.objects.Question;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ExpertSystemActivity extends Activity {

    private TextView probingTitle;
    private TextView probingImpression;
    private TextView symptomsList;
    private TextView impressionList;
    private TextView ruledOutImpressions;
    private TextView displayQuestion;
    private TextView positiveSymptomsList;
//    private ViewGroup layout_questions_inner_container;

    private DataAdapter getBetterDb;
    private String[] impressions;
    private String[] symptoms;
    private ArrayList<Impressions> impressionsSymptoms;
    private int currentImpressionIndex;
    private int currentSymptomIndex;
    private Question question;
    private ArrayList<String> ruledOutImpressionList;
    private ArrayList<String> positiveSymptoms;
    private ArrayList<String> ruledOutSymptomList;
    private ArrayList<Integer> chiefComplaintId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expert_system);

        Bundle extras = getIntent().getExtras();

        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        positiveSymptoms = new ArrayList<>();

        chiefComplaintId = new ArrayList<>();
        if(extras != null) {
            chiefComplaintId = extras.getIntegerArrayList("chiefComplaintId");
        }


        initializeDatabase();
        initializeImpressionList();

        impressionList = (TextView)findViewById(R.id.impressions_list);
        StringBuilder listOfImpressions = new StringBuilder();
        for(int i = 0; i < impressions.length; i++) {
            listOfImpressions.append(impressions[i] + "\n");
        }

        impressionList.setText(listOfImpressions.toString());


        impressionsSymptoms = new ArrayList<>();
        int impressionId;
        for(int i = 0; i < impressions.length; i++) {

            try {
                getBetterDb.openDatabaseForRead();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            impressionId = getBetterDb.getImpressionId(impressions[i]);

            ArrayList<String> symptoms = getBetterDb.getSymptoms(impressionId);

            getBetterDb.closeDatabase();


            impressionsSymptoms.add(new Impressions(impressions[i], symptoms));

        }


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

        ArrayList<String> hc = new ArrayList<>();
        for(int i = 0; i < chiefComplaintId.size(); i++)
            hc = getBetterDb.getImpressions(chiefComplaintId.get(i));

        hc = new ArrayList<>(new LinkedHashSet<>(hc)); // Remove duplicates

        impressions = new String[hc.size()];
        for (int i = 0; i < hc.size(); i++) {
            impressions[i] = hc.get(i);
        }

        getBetterDb.closeDatabase();

    }

    public void reloadExpertSystem() {

        StringBuilder symptomList = new StringBuilder();
        StringBuilder positiveSympList = new StringBuilder();
        String symptom = "";

        if(positiveSymptoms.contains(impressionsSymptoms.get(currentImpressionIndex).
                getSymptoms().get(currentSymptomIndex).toString()) || ruledOutSymptomList.
                contains(impressionsSymptoms.get(currentImpressionIndex).
                        getSymptoms().get(currentSymptomIndex).toString())) {

            impressionsSymptoms.get(currentImpressionIndex).getSymptoms().remove(currentSymptomIndex);
            impressionsSymptoms.get(currentImpressionIndex).getSymptoms().trimToSize();

        } else {

            symptom = impressionsSymptoms.get(currentImpressionIndex).getSymptoms().get(currentSymptomIndex).toString();
            getQuestions(symptom);
        }

        probingTitle = (TextView)findViewById(R.id.probing_title);
        probingImpression = (TextView)findViewById(R.id.probing_impression);
        symptomsList = (TextView)findViewById(R.id.symptoms_list);
        displayQuestion = (TextView)findViewById(R.id.question_label);
        positiveSymptomsList = (TextView)findViewById(R.id.positive_symptom_list);

        displayQuestion.setText(question.getEnglishQuestion());
        probingImpression.setText(impressionsSymptoms.get(currentImpressionIndex).getImpression());
        for(int i = 0; i < impressionsSymptoms.get(currentImpressionIndex).getSymptoms().size();i++)
            symptomList.append(impressionsSymptoms.get(currentImpressionIndex).getSymptoms().get(i).toString() + "\n");

        for (int i = 0; i < positiveSymptoms.size(); i++) {
            positiveSympList.append(positiveSymptoms.get(i).toString() + "\n");
        }

        symptomsList.setText(symptomList.toString());
        positiveSymptomsList.setText(positiveSympList.toString());

    }

    public void checkForRuledOutImpressions() {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expert_system, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goNext(View view) {
        currentSymptomIndex++;


        if(currentSymptomIndex >= impressionsSymptoms.get(currentImpressionIndex).getSymptoms().size()) {
            currentSymptomIndex = 0;
            currentImpressionIndex++;

            if(currentImpressionIndex >= impressionsSymptoms.size()) {
                currentImpressionIndex = impressionsSymptoms.size() - 1;

                exitExpertSystem();
            } else {

                reloadExpertSystem();
            }

        } else {
            reloadExpertSystem();
        }

    }

    public void goPrevious(View view) {
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

    public void onRadioButtonClicked (View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_yes:
                if(checked) {
                    positiveSymptoms.add(impressionsSymptoms.get(currentImpressionIndex).
                            getSymptoms().get(currentSymptomIndex).toString());
                }

                break;

            case R.id.radio_no:
                if(checked) {
                    ruledOutSymptomList.add(impressionsSymptoms.get(currentImpressionIndex).
                            getSymptoms().get(currentSymptomIndex).toString());
                }

                break;
        }

    }

    public void getQuestions (String symptom) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        question = getBetterDb.getQuestion(symptom);
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
