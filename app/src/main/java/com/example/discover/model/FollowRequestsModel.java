package com.example.discover.model;

public class FollowRequestsModel {
    int dUserProfileImage;
    String dFollowRequestText;

    public FollowRequestsModel(int dUserProfileImage, String dFollowRequestText) {
        this.dUserProfileImage = dUserProfileImage;
        this.dFollowRequestText = dFollowRequestText;
    }

    public int getUserProfileImage() {
        return dUserProfileImage;
    }

    public void setUserProfileImage(int dUserProfile) {
        this.dUserProfileImage = dUserProfile;
    }

    public String getFollowRequestText() {
        return dFollowRequestText;
    }

    public void setFollowRequestText(String dFollowRequestText) {
        this.dFollowRequestText = dFollowRequestText;
    }
}
