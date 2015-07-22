package com.dlsu.thesis.getbetter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dlsu.thesis.getbetter.R;
import com.dlsu.thesis.getbetter.objects.PatientContent;

import java.util.ArrayList;

/**
 * GetBetter. Created by Mike Dayupay on 7/17/15.
 */
public class PatientsAdapters extends ArrayAdapter<PatientContent.Patient> {

    public PatientsAdapters (Context context, ArrayList<PatientContent.Patient> patients) {
        super(context, 0, patients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PatientContent.Patient patient = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.patient_list_item,
                    parent, false);
        }

        TextView patientName = (TextView)convertView.findViewById(R.id.patient_name_list);
        TextView patientGender = (TextView)convertView.findViewById(R.id.patient_gender_list);

        patientName.setText(patient.getLastName() + ", " + patient.getFirstName());
        patientGender.setText(patient.getGender());

        return convertView;
    }
}
