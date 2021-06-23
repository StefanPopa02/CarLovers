package com.stefanpopa.carloversapp.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;
import java.util.Date;

public class Meeting {
    private String address;
    private String dateAndTime;
    private String description;
    private LatLng latLng;
    private @ServerTimestamp
    Date timestamp;
    private String userId;
    private String username;
    private MyCalendar calendar;

    public Meeting() {
    }

    public Meeting(String dateAndTime, String description, LatLng latLng, Date timestamp, String userId) {
        this.dateAndTime = dateAndTime;
        this.description = description;
        this.latLng = latLng;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public MyCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(MyCalendar calendar) {
        this.calendar = calendar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
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
        return "Meeting{" +
                "address='" + address + '\'' +
                ", dateAndTime='" + dateAndTime + '\'' +
                ", description='" + description + '\'' +
                ", latLng=" + latLng +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }
}
