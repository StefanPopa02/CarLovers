package com.stefanpopa.carloversapp.model;

import java.util.List;

public class UserProfile {
    private String bio;
    private String email;
    private String firstName;
    private String id;
    private String imageurl;
    private String lastName;
    private String username;
    private List<Integer> followingClubs;
    private String docId;

    public UserProfile() {
    }

    public List<Integer> getFollowingClubs() {
        return followingClubs;
    }

    public void setFollowingClubs(List<Integer> followingClubs) {
        this.followingClubs = followingClubs;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "bio='" + bio + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", id='" + id + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", followingClubs=" + followingClubs +
                '}';
    }
}
