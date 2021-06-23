package com.stefanpopa.carloversapp.model;

public class ClubMember {
    private String userId;
    private String username;
    private String fullname;
    private boolean isAdmin;
    private String imageProfileUrl;

    public ClubMember() {

    }

    public ClubMember(String userId, String username, String fullname, boolean isAdmin, String imageProfileUrl) {
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
        this.isAdmin = isAdmin;
        this.imageProfileUrl = imageProfileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getImageProfileUrl() {
        return imageProfileUrl;
    }

    public void setImageProfileUrl(String imageProfileUrl) {
        this.imageProfileUrl = imageProfileUrl;
    }
}
