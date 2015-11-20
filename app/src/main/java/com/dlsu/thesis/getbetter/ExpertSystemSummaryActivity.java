package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.PositiveResults;

import java.sql.SQLException;
import java.util.ArrayList;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class ExpertSystemSummaryActivity extends Activity {

    private DataAdapter getBetterDb;
    private ArrayList<PositiveResults> positiveSymptoms;
    private int caseRecordId;
    private TextView summaryHpiContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_system_summary);

        ArrayList<String> plausibleImpressions;
        ArrayList<String> ruledOutImpressions;

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
        Log.d("result size: ", positiveSymptoms.size() + "");
        for(int i = 0; i < positiveSymptoms.size(); i++) {
            summaryPositiveSymptomList.append(positiveSymptoms.get(i).getPositiveName() + "\n");
            summaryHpiContent.append(positiveSymptoms.get(i).getPositiveAnswerPhrase() + "\n");
        }

        backPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        positiveSymptoms = getBetterDb.getPositiveSymptoms(1);

        Log.d("before removing duplicates", positiveSymptoms.size()+"");



        getBetterDb.closeDatabase();
    }

    private void generateIntroductionSentence () {


    }

    private String getSubject (String username, int genderId) {

        String subject = "";

        return subject;
    }

    private String getPhrase () {

        String phrase = "";

        return phrase;
    }

    private String getPronoun (int genderId) {

        String pronoun = "";

        if(genderId == 1) {
            pronoun = "He";
        } else {
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
