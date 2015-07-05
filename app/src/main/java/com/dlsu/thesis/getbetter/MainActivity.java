package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dlsu.thesis.getbetter.database.DataAdapter;

import java.sql.SQLException;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText usernameInput = (EditText)findViewById(R.id.userNameInput);
        EditText passwordInput = (EditText)findViewById(R.id.passWordInput);


        DataAdapter getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            getBetterDb.openDatabase();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cHealthCenters = getBetterDb.getHealthCenters();

        cHealthCenters.moveToFirst();

        String[] healthCenter = new String[cHealthCenters.getCount()];

        for(int i = 0; i < cHealthCenters.getCount(); i++) {
            healthCenter[i] = cHealthCenters.getString(cHealthCenters.getColumnIndexOrThrow("health_center_name"));
            cHealthCenters.moveToNext();

        }

        Spinner hcSpinner = (Spinner)findViewById(R.id.health_center_login);
        Button loginButton = (Button)findViewById(R.id.loginButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, healthCenter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hcSpinner.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
