package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.ClubItemAdapter;
import com.stefanpopa.carloversapp.util.CallbackMethod;
import com.stefanpopa.carloversapp.util.FirebaseData;

import java.util.ArrayList;
import java.util.List;

public class FollowingClubsActivity extends AppCompatActivity {

    private String profileId;
    private RecyclerView recyclerView;
    private List<ClubItem> clubItems;
    private ClubItemAdapter clubItemAdapter;
    private UserProfile userProfile;
    private FirebaseFirestore db;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_clubs);

        close = findViewById(R.id.following_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();
        profileId = getIntent().getStringExtra("profileId");
        recyclerView = findViewById(R.id.following_clubs_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clubItems = new ArrayList<>();
        clubItemAdapter = new ClubItemAdapter(this, clubItems, userProfile, false);
        recyclerView.setAdapter(clubItemAdapter);

        db.collection("users").whereEqualTo("id", profileId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    userProfile = doc.toObject(UserProfile.class);
                    clubItemAdapter.renewUserProfile(userProfile);
                    Log.d("FOLLOWING_CLUBS_ACTIVITY", "USER :" + userProfile.toString());
                    getCarClubItems(new CallbackMethod() {
                        @Override
                        public void getCarClubItemsData(List<ClubItem> clubItemsData) {
                            clubItems = new ArrayList<>(clubItemsData);
                            Log.d("FOLLOWING_CLUBS_ACTIVITY", "CLUBITEMS: " + clubItems.toString());
                            clubItemAdapter.renewClubItems(clubItems);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FollowingClubsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        setUserProfileListener();

    }

    private void getCarClubItems(CallbackMethod callbackMethod) {
        List<Integer> followingClubs = userProfile.getFollowingClubs();
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
                Toast.makeText(FollowingClubsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUserProfileListener() {
        db.collection("users").whereEqualTo("id", profileId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("CLUBS_FRAGMENT", "LISTENER FAILED " + error.getMessage());
                }

                DocumentSnapshot doc = value.getDocuments().get(0);
                userProfile = doc.toObject(UserProfile.class);
                clubItemAdapter.renewUserProfile(userProfile);
                clubItemAdapter.notifyDataSetChanged();
                Log.d("CLUBS_FRAGMENT", "S-a actualizat UserProfile: " + userProfile.toString());
            }
        });
    }
}