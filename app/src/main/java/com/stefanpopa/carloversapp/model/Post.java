package com.stefanpopa.carloversapp.model;


import com.google.firebase.Timestamp;

public class Post {
    private String imageUrl;
    private String userId;
    private String description;
    private String username;
    private Timestamp timeAdded;

    public Post(){

    }

    public Post(String imageUrl, String userId, String description, String username, Timestamp timeAdded) {
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.description = description;
        this.username = username;
        this.timeAdded = timeAdded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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
}
