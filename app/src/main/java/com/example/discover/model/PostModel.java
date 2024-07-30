package com.example.discover.model;

import java.util.Objects;

public class PostModel {
    private String mPostId;
    private String mPostImage;
    private String mPostedBy;
    private String mPostDesc;

    private String mCategory;

    private int mPostLikes; // count

    private int mCommentsCount;
    private long postedAt;

    public PostModel(String mPostId, String mPostImage, String mPostedBy, String mPostDesc, long postedAt) {
        this.mPostId = mPostId;
        this.mPostImage = mPostImage;
        this.mPostedBy = mPostedBy;
        this.mPostDesc = mPostDesc;
        this.postedAt = postedAt;
    }

    public PostModel() {

    }

    public String getmPostId() {
        return mPostId;
    }

    public void setmPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    public String getmPostImage() {
        return mPostImage;
    }

    public void setmPostImage(String mPostImage) {
        this.mPostImage = mPostImage;
    }

    public String getmPostedBy() {
        return mPostedBy;
    }

    public void setmPostedBy(String mPostedBy) {
        this.mPostedBy = mPostedBy;
    }

    public String getmPostDesc() {
        return mPostDesc;
    }

    public void setmPostDesc(String mPostDesc) {
        this.mPostDesc = mPostDesc;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public int getmPostLikes() {
        return mPostLikes;
    }

    public void setmPostLikes(int mPostLikes) {
        this.mPostLikes = mPostLikes;
    }

    public int getmCommentsCount() {
        return mCommentsCount;
    }

    public void setmCommentsCount(int mCommentsCount) {
        this.mCommentsCount = mCommentsCount;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
