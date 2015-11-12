package com.dlsu.thesis.getbetter.objects;

/**
 * GetBetter. Created by Mike Dayupay on 11/12/15.
 */
public class PatientAnswers {

    private int caseRecordId;
    private int symptomId;
    private String answer;

    public PatientAnswers(int caseRecordId, int symptomId, String answer) {
        this.caseRecordId = caseRecordId;
        this.symptomId = symptomId;
        this.answer = answer;
    }

    public int getCaseRecordId() {
        return caseRecordId;
    }

    public int getSymptomId() {
        return symptomId;
    }

    public String getAnswer() {
        return answer;
    }
}
