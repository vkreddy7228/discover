package com.example.discover.model;

public class FollowersModel {
    private String mFollowedBy;

    public String getMFollowedBy() {
        return mFollowedBy;
    }

    public void setMFollowedBy(String mFollowedBy) {
        this.mFollowedBy = mFollowedBy;
    }

    public FollowersModel(String mFollowedBy) {
        this.mFollowedBy = mFollowedBy;
    }

    public FollowersModel() {

    }
}
