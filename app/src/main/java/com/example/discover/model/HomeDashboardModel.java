package com.example.discover.model;

public class HomeDashboardModel {
    int dProfileIcon, dPostImage, dSave;
    String dLikes, dComments, dShares, dUserName, dUserBio, dUserNameDescription, dPostDescription;

    public HomeDashboardModel(int dProfileIcon, int dPostImage, int dSave, String dLikes, String dComments, String dShares,
                              String dUserName, String dUserBio, String dUserNameDescription, String dPostDescription) {
        this.dProfileIcon = dProfileIcon;
        this.dPostImage = dPostImage;
        this.dLikes = dLikes;
        this.dComments = dComments;
        this.dShares = dShares;
        this.dUserName = dUserName;
        this.dUserBio = dUserBio;
        this.dUserNameDescription = dUserNameDescription;
        this.dSave = dSave;
        this.dPostDescription = dPostDescription;
    }

    public int getProfileIcon() {
        return dProfileIcon;
    }

    public void setProfileIcon(int dProfileIcon) {
        this.dProfileIcon = dProfileIcon;
    }

    public int getPostImage() {
        return dPostImage;
    }

    public void setPostImage(int dPostImage) {
        this.dPostImage = dPostImage;
    }

    public String getLikes() {
        return dLikes;
    }

    public void setLikes(String dLikes) {
        this.dLikes = dLikes;
    }

    public String getComments() {
        return dComments;
    }

    public void setComments(String dComments) {
        this.dComments = dComments;
    }

    public String getShares() {
        return dShares;
    }

    public void setShares(String dShares) {
        this.dShares = dShares;
    }

    public String getUserName() {
        return dUserName;
    }

    public void setUserName(String dUserName) {
        this.dUserName = dUserName;
    }

    public String getUserBio() {
        return dUserBio;
    }

    public void setUserBio(String dUserBio) {
        this.dUserBio = dUserBio;
    }

    public String getUserNameDescription() {
        return dUserNameDescription;
    }

    public void setUserNameDescription(String dUserNameDescription) {
        this.dUserNameDescription = dUserNameDescription;
    }

    public String getPostDescription() {
        return dPostDescription;
    }

    public void setPostDescription(String dPostDescription) {
        this.dPostDescription = dPostDescription;
    }

    public int getSave() {
        return dSave;
    }

    public void setSave(int dSave) {
        this.dSave = dSave;
    }
}
