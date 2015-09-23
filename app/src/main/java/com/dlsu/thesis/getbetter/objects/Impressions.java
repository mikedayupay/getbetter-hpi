package com.dlsu.thesis.getbetter.objects;

/**
 * GetBetter. Created by Mike Dayupay on 9/21/15.
 */
public class Impressions {

    private String impression;
    private String[] symptoms;

    public Impressions(String impression, String[] symptoms) {
        this.impression = impression;
        this.symptoms = symptoms;
    }

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public String[] getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String[] symptoms) {
        this.symptoms = symptoms;
    }
}
