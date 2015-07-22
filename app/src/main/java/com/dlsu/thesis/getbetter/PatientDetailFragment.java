package com.dlsu.thesis.getbetter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.PatientContent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a single Patient detail screen.
 * This fragment is either contained in a {@link PatientListActivity}
 * in two-pane mode (on tablets) or a {@link PatientDetailActivity}
 * on handsets.
 */
public class PatientDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private PatientContent.Patient mItem;

    private TextView patientName;
    private TextView patientAge;
    private TextView patientGender;
    private TextView patientCivilStatus;
    private TextView patientBloodType;
    private TextView patientAddress;

    private DataAdapter getBetterDb;
    UserSessionManager session;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PatientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        String healthCenterName = user.get(UserSessionManager.KEY_HEALTH_CENTER);
        int healthCenterId;
        initializeDatabase();

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        healthCenterId = getBetterDb.getHealthCenterId(healthCenterName);
        ArrayList<PatientContent.Patient> patients = new ArrayList<PatientContent.Patient>();
        patients = getBetterDb.getPatients(healthCenterId);

        for(int i = 0; i < patients.size(); i++)
            PatientContent.addPatient(patients.get(i));

        getBetterDb.getPatients(healthCenterId);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PatientContent.PATIENT_MAP.get(getArguments().getLong(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_detail, container, false);

        String name = mItem.getFirstName() + " " + mItem.getMiddleName() + " " + mItem.getLastName();

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            patientName = ((TextView)rootView.findViewById(R.id.patient_name));
            patientName.setText(name);

            patientAge = ((TextView)rootView.findViewById(R.id.patient_age));
            patientAge.setText(mItem.getBirthdate());

            patientGender = ((TextView)rootView.findViewById(R.id.patient_gender));
            patientGender.setText(mItem.getGender());

            patientCivilStatus = ((TextView)rootView.findViewById(R.id.patient_civil_status));
            patientCivilStatus.setText(mItem.getCivilStatus());

            patientBloodType = ((TextView)rootView.findViewById(R.id.patient_blood_type));
            patientBloodType.setText(mItem.getBloodType());


        }

        return rootView;
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(getActivity());

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
