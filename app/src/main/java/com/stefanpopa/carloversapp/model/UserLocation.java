package com.stefanpopa.carloversapp.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
    private GeoPoint geoPoint;
    private @ServerTimestamp
    Date timestamp;
    private String userId;

    public UserLocation() {
    }

    public UserLocation(GeoPoint geoPoint, Date timestamp, String userId) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geoPoint=" + geoPoint +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                '}';
    }
}
