package com.stefanpopa.carloversapp.util;

import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.UserProfile;

import java.util.List;

public interface CallbackMethod {
    default void getResult(List<NewCarItem> userCars) {

    }

    default void getUserProfile(UserProfile userProfile) {

    }
}
