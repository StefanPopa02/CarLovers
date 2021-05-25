package com.stefanpopa.carloversapp.util;

import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.UserProfile;

import java.util.List;

public interface CallbackMethod {
    default void getResultData(List<NewCarItem> userCars) {

    }

    default void getUserProfileData(UserProfile userProfile) {

    }

    default void getCarClubItemsData(List<ClubItem> clubItems) {

    }
}
