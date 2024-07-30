package com.example.discover.model;

public class CommentsModel {
    private String mCommentedBy;
    private String mComment;

    private String mCommentsCount;
    private long mCommentedAt;

    public CommentsModel(String mCommentedBy, String mComment, long mCommentedAt) {
        this.mCommentedBy = mCommentedBy;
        this.mComment = mComment;
        this.mCommentedAt = mCommentedAt;
    }

    public CommentsModel() {

    }

    public String getmCommentedBy() {
        return mCommentedBy;
    }

    public void setmCommentedBy(String mCommentedBy) {
        this.mCommentedBy = mCommentedBy;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public long getmCommentedAt() {
        return mCommentedAt;
    }

    public void setmCommentedAt(long mCommentedAt) {
        this.mCommentedAt = mCommentedAt;
    }

    public String getmCommentsCount() {
        return mCommentsCount;
    }

    public void setmCommentsCount(String mCommentsCount) {
        this.mCommentsCount = mCommentsCount;
    }
}
