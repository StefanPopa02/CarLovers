package com.stefanpopa.carloversapp.util;

import android.app.Application;

import com.stefanpopa.carloversapp.model.ClubItem;

import java.util.List;

public class FirebaseData {
    private static FirebaseData instance;
    private List<ClubItem> clubItems;
    private ClubItem selectedClubItem;

    private FirebaseData(){

    }

    public static FirebaseData getInstance(){
        if(instance == null){
            instance = new FirebaseData();
        }
        return instance;
    }

    public List<ClubItem> getClubItems() {
        return clubItems;
    }

    public void setClubItems(List<ClubItem> clubItems) {
        this.clubItems = clubItems;
    }

    public ClubItem getSelectedClubItem() {
        return selectedClubItem;
    }

    public void setSelectedClubItem(ClubItem selectedClubItem) {
        this.selectedClubItem = selectedClubItem;
    }
}
