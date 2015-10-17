package com.dlsu.thesis.getbetter.objects;

import java.util.ArrayList;

/**
 * GetBetter. Created by Mike Dayupay on 9/21/15.
 */
public class Impressions {

    private String impression;
    private ArrayList<Symptom> symptoms;

    public Impressions(String impression, ArrayList<Symptom> symptoms) {
        this.impression = impression;
        this.symptoms = symptoms;
    }

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Symptom> symptoms) {
        this.symptoms = symptoms;
    }
}
