package com.stefanpopa.carloversapp.util;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanpopa.carloversapp.model.UserProfile;

public class UserApi extends Application {
    private String username;
    private String userId;
    private static UserApi instance;
    private UserProfile userProfile;

    public UserApi() {
    }

    public static UserApi getInstance() {
        if (instance == null) {
            instance = new UserApi();
        }
        return instance;
    }

    public interface UsernameCallback{
        void isUsernameExist(String username_txt);
    }

    public void getUsername(UsernameCallback usernameCallback) {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                Log.d("USER_API", documentSnapshot.toString());
                                String username_data = (String) documentSnapshot.get("username");
                                Log.d("USER_API", "Username: " + username_data);
                                instance.setUsername(username_data);
                                usernameCallback.isUsernameExist(username_data);
                                break;
                            }
                        }
                    }
                });
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
