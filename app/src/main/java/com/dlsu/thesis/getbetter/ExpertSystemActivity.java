package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Impressions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ExpertSystemActivity extends Activity {

    private ArrayList<Integer> chiefComplaintId;
    private TextView probingTitle;
    private TextView probingImpression;
    private TextView symptomsList;
    private TextView impressionList;

    private DataAdapter getBetterDb;
    private String[] impressions;
    private String[] symptoms;
    private ArrayList<Impressions> impressionsSymptoms;
    private int currentImpressionIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_expert_system);

        Bundle extras = getIntent().getExtras();

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

            ArrayList<String> hc = getBetterDb.getSymptoms(impressionId);
            symptoms = new String[hc.size()];
            for (int j = 0; j < hc.size(); j++) {
                symptoms[j] = hc.get(j);
            }

            getBetterDb.closeDatabase();


            impressionsSymptoms.add(new Impressions(impressions[i], symptoms));

        }


        currentImpressionIndex = 0;
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

        probingTitle = (TextView)findViewById(R.id.probing_title);
        probingImpression = (TextView)findViewById(R.id.probing_impression);
        symptomsList = (TextView)findViewById(R.id.symptoms_list);

        probingImpression.setText(impressionsSymptoms.get(currentImpressionIndex).getImpression());
        for(int i = 0; i < impressionsSymptoms.get(currentImpressionIndex).getSymptoms().length;i++)
            symptomList.append(impressionsSymptoms.get(currentImpressionIndex).getSymptoms()[i].toString() + "\n");

        symptomsList.setText(symptomList.toString());

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
        currentImpressionIndex++;

        if(currentImpressionIndex >= impressionsSymptoms.size()) {
            currentImpressionIndex = impressionsSymptoms.size() - 1;

            exitExpertSystem();
        } else {

            reloadExpertSystem();
        }

    }

    public void goPrevious(View view) {
        currentImpressionIndex--;

        if(currentImpressionIndex < 0) {
            currentImpressionIndex = 0;
        } else {
            reloadExpertSystem();
        }

    }

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
