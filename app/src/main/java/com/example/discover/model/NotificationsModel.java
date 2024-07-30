package com.example.discover.model;

public class NotificationsModel {
    private String mNotificationBy;
    private long mNotificationAt;
    private String mType;
    private String mPostedBy;
    private String mPostId;
    private String mNotificationId;

    private boolean mOpened;

    public NotificationsModel() {
    }

    public String getmNotificationBy() {
        return mNotificationBy;
    }

    public void setmNotificationBy(String mNotificationBy) {
        this.mNotificationBy = mNotificationBy;
    }

    public long getmNotificationAt() {
        return mNotificationAt;
    }

    public void setmNotificationAt(long mNotificationAt) {
        this.mNotificationAt = mNotificationAt;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmPostedBy() {
        return mPostedBy;
    }

    public void setmPostedBy(String mPostedBy) {
        this.mPostedBy = mPostedBy;
    }

    public String getmPostId() {
        return mPostId;
    }

    public void setmPostId(String mPostId) {
        this.mPostId = mPostId;
    }

    public boolean ismOpened() {
        return mOpened;
    }

    public void setmOpened(boolean mOpened) {
        this.mOpened = mOpened;
    }

    public String getmNotificationId() {
        return mNotificationId;
    }

    public void setmNotificationId(String mNotificationId) {
        this.mNotificationId = mNotificationId;
    }
}
