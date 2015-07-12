package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dlsu.thesis.getbetter.database.DataAdapter;

import java.sql.SQLException;
import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public final static String EXTRA_MESSAGE = "com.dlsu.thesis.getbetter.TITLE";

    private DataAdapter getBetterDb;
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Spinner hcSpinner;
    private String[] healthCenter;
    private String healthCenterSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = (EditText)findViewById(R.id.username_input);
        passwordInput = (EditText)findViewById(R.id.password_input);

        initializeDatabase();
        initializeHealthCenterList();


        hcSpinner = (Spinner) findViewById(R.id.health_center_login);
        loginButton = (Button) findViewById(R.id.login_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, healthCenter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hcSpinner.setAdapter(adapter);

        hcSpinner.setOnItemSelectedListener(this);
        loginButton.setOnClickListener(this);

    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void initializeHealthCenterList () {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> hc = getBetterDb.getHealthCenters();
        healthCenter = new String[hc.size()];
        for (int i = 0; i < hc.size(); i++) {
            healthCenter[i] = hc.get(i);
        }

        getBetterDb.closeDatabase();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        healthCenterSelected = (parent.getItemAtPosition(position)).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        healthCenterSelected = (parent.getSelectedItem()).toString();
    }


    @Override
    public void onClick(View v) {

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String text = username + password + healthCenterSelected;
        //Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        //toast.show();

        Intent intent = new Intent(this, PatientListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, healthCenterSelected);
        startActivity(intent);


    }
}