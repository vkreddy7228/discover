package com.example.discover.model;

public class UserModel {
    String mName;
    String mProfession;
    String mEmail;

    String mUserId;

    int mFollowersCount;
    int mFollowingCount;

    public int getmPostsCount() {
        return mPostsCount;
    }

    public void setmPostsCount(int mPostsCount) {
        this.mPostsCount = mPostsCount;
    }

    int mPostsCount;

    public int getmFollowersCount() {
        return mFollowersCount;
    }

    public void setmFollowersCount(int mFollowersCount) {
        this.mFollowersCount = mFollowersCount;
    }

    public int getmFollowingCount() {
        return mFollowingCount;
    }

    public void setmFollowingCount(int mFollowingCount) {
        this.mFollowingCount = mFollowingCount;
    }

    private String mPassword;

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getMProfilePhoto() {
        return mProfilePhoto;
    }

    public void setMProfilePic(String mProfilePhoto) {
        this.mProfilePhoto = mProfilePhoto;
    }

    String mProfilePhoto;

    public UserModel() {
    }

    public UserModel(String mName, String mProfession, String mEmail, String mPassword) {
        this.mName = mName;
        this.mProfession = mProfession;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProfession() {
        return mProfession;
    }

    public void setmProfession(String mProfession) {
        this.mProfession = mProfession;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
