package com.dlsu.thesis.getbetter.objects;

/**
 * GetBetter. Created by Mike Dayupay on 9/17/15.
 */
public class Question {

    private int questionId;
    private String englishQuestion;
    private String tagalogQuestion;
    private String responses;
    private String actionNeeded;

    public Question(int questionId, String englishQuestion, String tagalogQuestion, String responses, String actionNeeded) {
        this.questionId = questionId;
        this.englishQuestion = englishQuestion;
        this.tagalogQuestion = tagalogQuestion;
        this.responses = responses;
        this.actionNeeded = actionNeeded;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getEnglishQuestion() {
        return englishQuestion;
    }

    public void setEnglishQuestion(String englishQuestion) {
        this.englishQuestion = englishQuestion;
    }

    public String getTagalogQuestion() {
        return tagalogQuestion;
    }

    public void setTagalogQuestion(String tagalogQuestion) {
        this.tagalogQuestion = tagalogQuestion;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public String getActionNeeded() {
        return actionNeeded;
    }

    public void setActionNeeded(String actionNeeded) {
        this.actionNeeded = actionNeeded;
    }
}
