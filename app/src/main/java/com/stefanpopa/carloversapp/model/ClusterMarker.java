package com.stefanpopa.carloversapp.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private String iconPicture;
    private UserProfile user;
    private boolean isMeeting;
    private Meeting meeting;

    public ClusterMarker(LatLng position, String title, String snippet, String iconPicture, UserProfile user) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.user = user;
        this.isMeeting = false;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public boolean isMeeting() {
        return isMeeting;
    }

    public void setMeeting(boolean meeting) {
        isMeeting = meeting;
    }

    public ClusterMarker(LatLng position, String title, String snippet, String iconPicture, boolean isMeeting) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.isMeeting = isMeeting;
    }

    public ClusterMarker() {
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(String iconPicture) {
        this.iconPicture = iconPicture;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ClusterMarker{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                ", iconPicture=" + iconPicture +
                ", user=" + user +
                '}';
    }
}
