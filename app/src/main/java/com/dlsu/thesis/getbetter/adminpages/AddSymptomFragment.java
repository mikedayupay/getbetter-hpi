package com.dlsu.thesis.getbetter.adminpages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dlsu.thesis.getbetter.R;
import com.dlsu.thesis.getbetter.database.DataAdapter;

import java.sql.SQLException;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSymptomFragment extends Fragment implements View.OnClickListener {

    private EditText inputSymptomEnglish, inputSymptomTagalog, inputQuestionEnglish, inputQuestionTagalog
            , inputActionNeeded, inputAnswerPhrase, inputEmotionTag;
    private Button addSymptomBtn;

    private DataAdapter getBetterDb;


    public AddSymptomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeDatabase();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_add_symptom, container, false);

        inputSymptomEnglish = (EditText)rootView.findViewById(R.id.input_symptom_english);
        inputSymptomTagalog = (EditText)rootView.findViewById(R.id.input_symptom_tagalog);
        inputQuestionEnglish = (EditText)rootView.findViewById(R.id.input_question_english);
        inputQuestionTagalog = (EditText)rootView.findViewById(R.id.input_question_tagalog);
        inputActionNeeded = (EditText)rootView.findViewById(R.id.input_action_needed);
        inputAnswerPhrase = (EditText)rootView.findViewById(R.id.input_answer_phrase);
        inputEmotionTag = (EditText)rootView.findViewById(R.id.input_emotion_tag);
        addSymptomBtn = (Button)rootView.findViewById(R.id.add_symptom_btn);

        addSymptomBtn.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {

        insertSymptomToDatabase();
        Toast.makeText(getContext(), "Successfully inserted new Symptom", Toast.LENGTH_SHORT).show();

    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(getActivity());

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertSymptomToDatabase () {

        try {
            getBetterDb.openDatabaseForWrite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String symptomEnglish = inputSymptomEnglish.getText().toString();
        String symptomTagalog = inputSymptomTagalog.getText().toString();
        String questionEnglish = inputQuestionEnglish.getText().toString();
        String questionTagalog = inputQuestionTagalog.getText().toString();
        String actionNeeded = inputActionNeeded.getText().toString();
        String answerPhrase = inputAnswerPhrase.getText().toString();
        String emotionTag = inputEmotionTag.getText().toString();

        getBetterDb.insertSymptom(symptomEnglish, symptomTagalog, questionEnglish, questionTagalog,
                actionNeeded, answerPhrase, emotionTag);

        getBetterDb.closeDatabase();
    }
}
