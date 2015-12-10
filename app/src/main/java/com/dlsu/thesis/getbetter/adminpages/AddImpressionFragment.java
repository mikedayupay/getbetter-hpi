package com.dlsu.thesis.getbetter.adminpages;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dlsu.thesis.getbetter.R;
import com.dlsu.thesis.getbetter.database.DataAdapter;
import com.dlsu.thesis.getbetter.objects.Symptom;


import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class AddImpressionFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private EditText inputMedicalTerm, inputScientificTerm, inputLocalTerm, inputTreatment, inputRemarks;
    private TextInputLayout inputLayoutMedicalTerm, inputLayoutScientificTerm, inputLayoutLocalTerm
            , inputLayoutTreatment, inputLayoutRemarks;
    private LinearLayout symptomContentFrame;
    private Button addImpressionSubmitBtn;

    private DataAdapter getBetterDb;
    private ArrayList<Integer> chiefComplaintId;
    private ArrayList<Integer> symptomIds;
    private ArrayList<Symptom> symptoms;


    public AddImpressionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chiefComplaintId = new ArrayList<>();
        symptomIds = new ArrayList<>();
        initializeDatabase();
        getSymptoms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_add_impression, container, false);

        inputLayoutMedicalTerm = (TextInputLayout)rootView.findViewById(R.id.input_layout_medical_term);
        inputLayoutScientificTerm = (TextInputLayout)rootView.findViewById(R.id.input_layout_scientific_term);
        inputLayoutLocalTerm = (TextInputLayout)rootView.findViewById(R.id.input_layout_local_term);
        inputLayoutTreatment = (TextInputLayout)rootView.findViewById(R.id.input_layout_treatment);
        inputLayoutRemarks = (TextInputLayout)rootView.findViewById(R.id.input_layout_remarks);
        inputMedicalTerm = (EditText)rootView.findViewById(R.id.input_medical_term);
        inputScientificTerm = (EditText)rootView.findViewById(R.id.input_scientific_term);
        inputLocalTerm = (EditText)rootView.findViewById(R.id.input_local_term);
        inputTreatment = (EditText)rootView.findViewById(R.id.input_treatment);
        inputRemarks = (EditText)rootView.findViewById(R.id.input_remarks);
        symptomContentFrame = (LinearLayout)rootView.findViewById(R.id.symptom_content_frame);
        addImpressionSubmitBtn = (Button)rootView.findViewById(R.id.add_impression_submit_btn);

        CheckBox[] checkBox = new CheckBox[symptoms.size()];
        for(int i = 0; i < symptoms.size(); i++) {
            checkBox[i] = new CheckBox(rootView.getContext());
            checkBox[i].setText(symptoms.get(i).getSymptomNameEnglish());
            checkBox[i].setChecked(false);
            checkBox[i].setTag(symptoms.get(i).getSymptomId());
            checkBox[i].setOnCheckedChangeListener(this);
            symptomContentFrame.addView(checkBox[i]);
        }

        addImpressionSubmitBtn.setOnClickListener(this);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


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

    private void getSymptoms() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        symptoms = getBetterDb.getSymptoms();
        getBetterDb.closeDatabase();
    }

    private void insertImpressionToDatabase() {

        try {
            getBetterDb.openDatabaseForWrite();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String medicalTerm = inputMedicalTerm.getText().toString();
        String scientificTerm = inputScientificTerm.getText().toString();
        String localTerm = inputLocalTerm.getText().toString();
        String treatment = inputTreatment.getText().toString();
        String remarks = inputRemarks.getText().toString();

        int newImpressionId;
        newImpressionId = getBetterDb.insertImpression(medicalTerm, scientificTerm, localTerm, treatment, remarks);
        Toast.makeText(getContext(), newImpressionId + "", Toast.LENGTH_SHORT).show();

        getBetterDb.matchImpressionToComplaints(newImpressionId, chiefComplaintId);
        getBetterDb.matchImpressionToSymptoms(newImpressionId, symptomIds);

        getBetterDb.closeDatabase();

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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView.isChecked()) {
            Toast.makeText(getContext(), buttonView.getText(), Toast.LENGTH_SHORT).show();
            symptomIds.add((Integer) buttonView.getTag());
        }else {
            Toast.makeText(getContext(), buttonView.getText() + " removed", Toast.LENGTH_SHORT).show();
            symptomIds.remove(symptomIds.indexOf((Integer) buttonView.getTag()));
        }
    }

    @Override
    public void onClick(View v) {

        if(chiefComplaintId.isEmpty() && symptomIds.isEmpty()) {
            Toast.makeText(getContext(), "Please check at least one chief complaint and symptom", Toast.LENGTH_LONG).show();
        } else {
            insertImpressionToDatabase();
            Toast.makeText(getContext(), "Successfully added new Impression", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
