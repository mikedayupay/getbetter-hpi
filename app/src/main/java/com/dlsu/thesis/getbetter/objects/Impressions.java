package com.dlsu.thesis.getbetter.objects;

import java.util.ArrayList;

/**
 * GetBetter. Created by Mike Dayupay on 9/21/15.
 */
public class Impressions {

    private String impression;
    private ArrayList<String> symptoms;

    public Impressions(String impression, ArrayList<String> symptoms) {
        this.impression = impression;
        this.symptoms = symptoms;
    }

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<String> symptoms) {
        this.symptoms = symptoms;
    }
}
