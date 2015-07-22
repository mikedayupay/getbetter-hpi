package com.dlsu.thesis.getbetter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.PatientContent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class NewPatientActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private Button setDateButton;
    private Button addPatientButton;
    private EditText firstNameInput;
    private EditText middleNameInput;
    private EditText lastNameInput;
    private EditText streetInput;
    private TextView displayDate;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private DataAdapter getBetterDb;
    private String[] barangayList;
    private String[] cityList;
    private String genderSelected;
    private String bloodTypeSelected;
    private String civilStatusSelected;
    private String barangaySelected;
    private String citySelected;
    private String birthDate;
    private PatientContent.Patient patient;

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        session = new UserSessionManager(getApplicationContext());

        if(session.checkLogin())
            finish();

        HashMap<String, String> user = session.getUserDetails();

        String title = user.get(UserSessionManager.KEY_HEALTH_CENTER);

        getActionBar().setTitle(title);

        firstNameInput = (EditText)findViewById(R.id.first_name_input);
        middleNameInput = (EditText)findViewById(R.id.middle_name_input);
        lastNameInput = (EditText)findViewById(R.id.last_name_input);
        streetInput = (EditText)findViewById(R.id.street_input);


        Spinner genderSpinner = (Spinner)findViewById(R.id.gender_spinner);
        Spinner bloodTypeSpinner = (Spinner)findViewById(R.id.blood_type_spinner);
        Spinner civilStatusSpinner = (Spinner)findViewById(R.id.civil_status_spinner);
        Spinner barangaySpinner = (Spinner)findViewById(R.id.barangay_spinner);
        Spinner citySpinner = (Spinner)findViewById(R.id.city_spinner);

        setDateButton = (Button)findViewById(R.id.set_date_button);
        addPatientButton = (Button)findViewById(R.id.add_patient_button);
        displayDate = (TextView)findViewById(R.id.birthdate_display);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        birthDate = day + "/" + month + "/" + year;

        showDate(year, month, day);

        initializeDatabase();
        initializeData();

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> bloodAdapter = ArrayAdapter.createFromResource(this,
                R.array.blood_types, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> civilStatusAdapter = ArrayAdapter.createFromResource(this,
                R.array.civil_statuses, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, barangayList);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cityList);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        civilStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);
        bloodTypeSpinner.setAdapter(bloodAdapter);
        civilStatusSpinner.setAdapter(civilStatusAdapter);
        barangaySpinner.setAdapter(barangayAdapter);
        citySpinner.setAdapter(cityAdapter);

        genderSpinner.setOnItemSelectedListener(this);
        bloodTypeSpinner.setOnItemSelectedListener(this);
        civilStatusSpinner.setOnItemSelectedListener(this);
        barangaySpinner.setOnItemSelectedListener(this);
        citySpinner.setOnItemSelectedListener(this);

        setDateButton.setOnClickListener(this);
        addPatientButton.setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.set_date_button) {

            showDialog(999);

        } else if (id == R.id.add_patient_button) {
            initializeDatabase();

            try {
                getBetterDb.openDatabaseForWrite();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String firstName = firstNameInput.getText().toString();
            String middleName = middleNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String street = streetInput.getText().toString();

            patient = new PatientContent.Patient(0, firstName, middleName, lastName, birthDate, genderSelected,
                    civilStatusSelected, bloodTypeSelected);
            patient.setHomeAddress(street, barangaySelected, citySelected);

            getBetterDb.newPatient(patient);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate (int year, int month, int day) {
        displayDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void initializeData () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> bl = getBetterDb.getBarangays();
        barangayList = new String[bl.size()];
        for (int i = 0; i < bl.size(); i++) {
            barangayList[i] = bl.get(i);
        }

        ArrayList<String> cl = getBetterDb.getCities();
        cityList = new String[cl.size()];
        for (int i = 0; i < cl.size(); i++) {
            cityList[i] = cl.get(i);
        }

        getBetterDb.closeDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_patient, menu);
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

        switch(parent.getId()) {
            case R.id.gender_spinner:
                genderSelected = (parent.getItemAtPosition(position)).toString();
                break;

            case R.id.blood_type_spinner:
                bloodTypeSelected = (parent.getItemAtPosition(position)).toString();
                break;

            case R.id.civil_status_spinner:
                civilStatusSelected = (parent.getItemAtPosition(position)).toString();
                break;

            case R.id.barangay_spinner:
                barangaySelected = (parent.getItemAtPosition(position)).toString();
                break;

            case R.id.city_spinner:
                citySelected = (parent.getItemAtPosition(position)).toString();
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        switch(parent.getId()) {
            case R.id.gender_spinner:
                genderSelected = (parent.getSelectedItem()).toString();
                break;

            case R.id.blood_type_spinner:
                bloodTypeSelected = (parent.getSelectedItem()).toString();
                break;

            case R.id.civil_status_spinner:
                civilStatusSelected = (parent.getSelectedItem()).toString();
                break;

            case R.id.barangay_spinner:
                barangaySelected = (parent.getSelectedItem()).toString();
                break;

            case R.id.city_spinner:
                citySelected = (parent.getSelectedItem()).toString();
                break;

        }
    }
}
