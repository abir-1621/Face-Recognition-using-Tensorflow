package com.abir.voting.model;

public class Candidate {

    private int candidateID;
    private String imageURL;
    private String candidateName;
    private int voteCounted;


    public Candidate() {
    }

    public Candidate(int candidateID, String imageURL) {
        this.candidateID = candidateID;
        this.imageURL = imageURL;

    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
}
