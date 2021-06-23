package com.stefanpopa.carloversapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.FollowingClubsActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.ClubItemAdapter;
import com.stefanpopa.carloversapp.util.CallbackMethod;

import java.util.ArrayList;
import java.util.List;

public class ProfileFollowingFragment extends Fragment {

    private String profileId;
    private RecyclerView recyclerView;
    private List<ClubItem> clubItems;
    private ClubItemAdapter clubItemAdapter;
    private UserProfile userProfile;
    private FirebaseFirestore db;
    private ImageView close;

    public ProfileFollowingFragment() {

    }

    public ProfileFollowingFragment(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(3).setChecked(true);
        Log.d("PROFILE_FOLLOWING_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_following, container, false);
        db = FirebaseFirestore.getInstance();
        close = view.findViewById(R.id.following_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        profileId = userProfile.getId();
        recyclerView = view.findViewById(R.id.following_clubs_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clubItems = new ArrayList<>();
        clubItemAdapter = new ClubItemAdapter(getContext(), clubItems, userProfile, true);
        recyclerView.setAdapter(clubItemAdapter);

        getCarClubItems(new CallbackMethod() {
            @Override
            public void getCarClubItemsData(List<ClubItem> clubItemsData) {
                clubItems = new ArrayList<>(clubItemsData);
                Log.d("FOLLOWING_CLUBS_ACTIVITY", "CLUBITEMS: " + clubItems.toString());
                clubItemAdapter.renewClubItems(clubItems);
            }
        });

        return view;
    }

    private void getCarClubItems(CallbackMethod callbackMethod) {
        List<Integer> followingClubs = userProfile.getFollowingClubs();
        Log.d("PROFILE_FOLLOWING_FRAGMENT", "User profile: " + userProfile);
        if (followingClubs.size() > 0) {
            db.collection("carClubItems").whereIn("id", followingClubs).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<ClubItem> clubItems = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            clubItems.add(doc.toObject(ClubItem.class));
                        }
                        callbackMethod.getCarClubItemsData(clubItems);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            callbackMethod.getCarClubItemsData(new ArrayList<>());
        }
    }
}