package com.example.discover.model;

import java.util.ArrayList;

public class StoryModel {
    private String mStoryBy;
    private long mStoryAt;
    private ArrayList<UserStories> mUserStoriesList;

    public StoryModel() {
    }

    public String getmStoryBy() {
        return mStoryBy;
    }

    public void setmStoryBy(String mStoryBy) {
        this.mStoryBy = mStoryBy;
    }

    public long getmStoryAt() {
        return mStoryAt;
    }

    public void setmStoryAt(long mStoryAt) {
        this.mStoryAt = mStoryAt;
    }

    public ArrayList<UserStories> getmUserStoriesList() {
        return mUserStoriesList;
    }

    public void setmUserStoriesList(ArrayList<UserStories> mUserStoriesList) {
        this.mUserStoriesList = mUserStoriesList;
    }
}
