package com.stefanpopa.carloversapp.model;


import com.google.firebase.Timestamp;

import java.util.List;

public class Post {
    private String postDocId;
    private List<String> imageUrl;
    private int clubId;
    private int no_of_likes;
    private String clubFullname;
    private String userId;
    private String userDocId;
    private String userFullname;
    private String userProfileImgUrl;
    private String description;
    private String username;
    private Timestamp timeAdded;


    public Post() {

    }

    public Post(List<String> imageUrl, int clubId, int no_of_likes, String clubFullname, String userId, String userFullname, String description, String username, Timestamp timeAdded) {
        this.imageUrl = imageUrl;
        this.clubId = clubId;
        this.no_of_likes = no_of_likes;
        this.clubFullname = clubFullname;
        this.userId = userId;
        this.userFullname = userFullname;
        this.description = description;
        this.username = username;
        this.timeAdded = timeAdded;
    }

    public String getUserProfileImgUrl() {
        return userProfileImgUrl;
    }

    public void setUserProfileImgUrl(String userProfileImgUrl) {
        this.userProfileImgUrl = userProfileImgUrl;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public int getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(int no_of_likes) {
        this.no_of_likes = no_of_likes;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getPostDocId() {
        return postDocId;
    }

    public void setPostDocId(String postDocId) {
        this.postDocId = postDocId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getClubFullname() {
        return clubFullname;
    }

    public void setClubFullname(String clubFullname) {
        this.clubFullname = clubFullname;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postDocId='" + postDocId + '\'' +
                ", imageUrl=" + imageUrl +
                ", clubId=" + clubId +
                ", no_of_likes=" + no_of_likes +
                ", clubFullname='" + clubFullname + '\'' +
                ", userId='" + userId + '\'' +
                ", userDocId='" + userDocId + '\'' +
                ", userFullname='" + userFullname + '\'' +
                ", userProfileImgUrl='" + userProfileImgUrl + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", timeAdded=" + timeAdded +
                '}';
    }
}
