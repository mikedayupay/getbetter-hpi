package com.dlsu.thesis.getbetter.objects;

/**
 * GetBetter. Created by Mike Dayupay on 10/17/15.
 */
public class SymptomFamily {

    private int symptomFamilyId;
    private String symptomFamilyNameEnglish;
    private String symptomFamilyNameTagalog;
    private String generalQuestionEnglish;
    private String responsesEnglish;

    public SymptomFamily(int symptomFamilyId, String symptomFamilyNameEnglish,
                         String symptomFamilyNameTagalog, String generalQuestionEnglish,
                         String responsesEnglish) {

        this.symptomFamilyId = symptomFamilyId;
        this.symptomFamilyNameEnglish = symptomFamilyNameEnglish;
        this.symptomFamilyNameTagalog = symptomFamilyNameTagalog;
        this.generalQuestionEnglish = generalQuestionEnglish;
        this.responsesEnglish = responsesEnglish;
    }

    public int getSymptomFamilyId() {
        return symptomFamilyId;
    }

    public void setSymptomFamilyId(int symptomFamilyId) {
        this.symptomFamilyId = symptomFamilyId;
    }

    public String getSymptomFamilyNameEnglish() {
        return symptomFamilyNameEnglish;
    }

    public void setSymptomFamilyNameEnglish(String symptomFamilyNameEnglish) {
        this.symptomFamilyNameEnglish = symptomFamilyNameEnglish;
    }

    public String getSymptomFamilyNameTagalog() {
        return symptomFamilyNameTagalog;
    }

    public void setSymptomFamilyNameTagalog(String symptomFamilyNameTagalog) {
        this.symptomFamilyNameTagalog = symptomFamilyNameTagalog;
    }

    public String getGeneralQuestionEnglish() {
        return generalQuestionEnglish;
    }

    public void setGeneralQuestionEnglish(String generalQuestionEnglish) {
        this.generalQuestionEnglish = generalQuestionEnglish;
    }

    public String getResponsesEnglish() {
        return responsesEnglish;
    }

    public void setResponsesEnglish(String responsesEnglish) {
        this.responsesEnglish = responsesEnglish;
    }
}
