package com.abir.voting.model;

public class VoterInfo {

    private String name;
    private String nid;
    private String imageUrl;
    private int votedCandidateId;
    private boolean hasFinger;

    public VoterInfo(String name, String nid, String imageUrl, int votedCandidateId, boolean hasFinger) {
        this.name = name;
        this.nid = nid;
        this.imageUrl = imageUrl;
        this.votedCandidateId = votedCandidateId;
        this.hasFinger = hasFinger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getVotedCandidateId() {
        return votedCandidateId;
    }

    public void setVotedCandidateId(int votedCandidateId) {
        this.votedCandidateId = votedCandidateId;
    }

    public boolean isHasFinger() {
        return hasFinger;
    }

    public void setHasFinger(boolean hasFinger) {
        this.hasFinger = hasFinger;
    }
}
