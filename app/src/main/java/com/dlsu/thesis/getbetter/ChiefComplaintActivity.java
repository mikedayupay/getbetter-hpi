package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class ChiefComplaintActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Integer> chiefComplaintId = new ArrayList<>();
    PatientExpertSessionManager patientSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_complaint);

        patientSession = new PatientExpertSessionManager(getApplicationContext());
        HashMap<String, String> patient = patientSession.getPatientDetails();
        String title = patient.get(PatientExpertSessionManager.PATIENT_NAME);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Patient: " + title);

        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

    }

    public void onCheckBoxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {

            case R.id.check_fever:
                if(checked) {
                    chiefComplaintId.add(1);
                } else {
                    if(chiefComplaintId.contains(1))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(1)));
                }
                break;

            case R.id.check_pain:
                if(checked) {
                    chiefComplaintId.add(2);
                } else {
                    if(chiefComplaintId.contains(2))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(2)));
                }
                break;

            case R.id.check_injury_wound:
                if(checked) {
                    chiefComplaintId.add(3);
                } else {
                    if(chiefComplaintId.contains(3))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(3)));

                }
                break;

            case R.id.check_skin_problem:
                if(checked) {
                    chiefComplaintId.add(4);
                } else {
                    if(chiefComplaintId.contains(4))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(4)));
                }
                break;

            case R.id.check_respiratory:
                if(checked) {
                    chiefComplaintId.add(5);
                } else {
                    if(chiefComplaintId.contains(5))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(5)));
                }
                break;

            case R.id.check_bowel_vomiting:
                if(checked) {
                    chiefComplaintId.add(6);
                } else {
                    if(chiefComplaintId.contains(6))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(6)));
                }
                break;

            case R.id.check_general_unwellness:
                if(checked) {
                    chiefComplaintId.add(7);
                } else {
                    if(chiefComplaintId.contains(7))
                        chiefComplaintId.remove(chiefComplaintId.get(chiefComplaintId.indexOf(7)));
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chief_complaint, menu);
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

    @Override
    public void onClick(View v) {

        if(chiefComplaintId.isEmpty()) {
            Toast toast = Toast.makeText(this, "Please check at least one box", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Intent intent = new Intent(this, ExpertSystemActivity.class);
            intent.putIntegerArrayListExtra("chiefComplaintId", chiefComplaintId);
            startActivity(intent);
            finish();
        }

    }
}
