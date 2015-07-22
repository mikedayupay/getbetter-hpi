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
import android.widget.Toast;

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

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());

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
            getBetterDb.openDatabaseForRead();
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

    private boolean checkLogin (String username, String password) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getBetterDb.checkLogin(username, password);
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
        //String text = username + password + healthCenterSelected;
        //Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        //toast.show();

        if(username.trim().length() > 0 && password.trim().length() > 0) {

            if(checkLogin(username, password)) {

                session.createUserLoginSession(username, healthCenterSelected);

                Intent intent = new Intent(this, PatientListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Username/Password is incorrect",
                        Toast.LENGTH_LONG).show();
            }
        }else {

            Toast.makeText(getApplicationContext(),
                    "Please enter username and password",
                    Toast.LENGTH_LONG).show();
        }

//        Intent intent = new Intent(this, PatientListActivity.class);
//        intent.putExtra(EXTRA_MESSAGE, healthCenterSelected);
//        startActivity(intent);
    }
}