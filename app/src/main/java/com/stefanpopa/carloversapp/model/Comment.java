package com.stefanpopa.carloversapp.model;

import com.google.firebase.Timestamp;

public class Comment {
    private String commentDocId;
    private String userDocId;
    private String userId;
    private String postDocId;
    private String commentMessage;
    private Timestamp timeAdded;

    public Comment() {

    }

    public Comment(String userDocId, String userId, String postDocId, String commentMessage, Timestamp timeAdded) {
        this.userDocId = userDocId;
        this.userId = userId;
        this.postDocId = postDocId;
        this.commentMessage = commentMessage;
        this.timeAdded = timeAdded;
    }

    public String getCommentDocId() {
        return commentDocId;
    }

    public void setCommentDocId(String commentDocId) {
        this.commentDocId = commentDocId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostDocId() {
        return postDocId;
    }

    public void setPostDocId(String postDocId) {
        this.postDocId = postDocId;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "userDocId='" + userDocId + '\'' +
                ", userId='" + userId + '\'' +
                ", postDocId='" + postDocId + '\'' +
                ", commentMessage='" + commentMessage + '\'' +
                ", timeAdded=" + timeAdded +
                '}';
    }
}
