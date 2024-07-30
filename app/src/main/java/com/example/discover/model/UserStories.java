package com.example.discover.model;

public class UserStories {
    private String mStoryImage;
    private long mStoryAt;

    public String getmStoryImage() {
        return mStoryImage;
    }

    public void setmStoryImage(String mStoryImage) {
        this.mStoryImage = mStoryImage;
    }

    public long getmStoryAt() {
        return mStoryAt;
    }

    public void setmStoryAt(long mStoryAt) {
        this.mStoryAt = mStoryAt;
    }

    public UserStories() {
    }

    public UserStories(String mStoryImage, long mStoryAt) {
        this.mStoryImage = mStoryImage;
        this.mStoryAt = mStoryAt;
    }
}
