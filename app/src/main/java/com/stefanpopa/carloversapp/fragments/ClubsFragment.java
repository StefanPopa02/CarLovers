package com.stefanpopa.carloversapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.ClubItemAdapter;
import com.stefanpopa.carloversapp.util.CallbackMethod;
import com.stefanpopa.carloversapp.util.FirebaseData;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClubsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerViewClubItems;
    private ClubItemAdapter clubItemAdapter;
    private SocialAutoCompleteTextView socialAutoCompleteTextView;
    private List<ClubItem> clubItems;
    private UserProfile userProfile;
    private FirebaseFirestore db;
    private TextView allFollowingPostsBtn;

    public ClubsFragment() {
    }

    public static ClubsFragment newInstance(String param1, String param2) {
        ClubsFragment fragment = new ClubsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);
        Log.d("CLUBS_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);
        db = FirebaseFirestore.getInstance();
        allFollowingPostsBtn = view.findViewById(R.id.posts_following_clubs);
        allFollowingPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ClubFeedFragment(), "CLUBS_FEED_FRAGMENT").addToBackStack(null).commit();
            }
        });
        socialAutoCompleteTextView = view.findViewById(R.id.clubs_search_bar);
        recyclerViewClubItems = view.findViewById(R.id.clubs_recycler_view);
        recyclerViewClubItems.setHasFixedSize(true);
        recyclerViewClubItems.setLayoutManager(new LinearLayoutManager(getContext()));

        UserProfile rememberedUserProfile = UserApi.getInstance().getUserProfile();
        clubItems = new ArrayList<>();
        clubItemAdapter = new ClubItemAdapter(getContext(), clubItems, userProfile, true);
        clubItemAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        recyclerViewClubItems.setAdapter(clubItemAdapter);


        if (rememberedUserProfile != null) {
            userProfile = rememberedUserProfile;
            clubItemAdapter.renewUserProfile(rememberedUserProfile);
            Log.d("CLUBS_FRAGMENT", "REMEMBERED USER: " + rememberedUserProfile.toString());
            if (FirebaseData.getInstance().getClubItems() == null) {
                getCarClubItems(new CallbackMethod() {
                    @Override
                    public void getCarClubItemsData(List<ClubItem> clubItemsData) {
                        FirebaseData.getInstance().setClubItems(clubItemsData);
                        clubItems = new ArrayList<>(clubItemsData);
                        Log.d("CLUBS_FRAGMENT", clubItems.toString());
                        clubItemAdapter.renewClubItems(clubItems);
                    }
                });
            } else {
                clubItems = new ArrayList<>(FirebaseData.getInstance().getClubItems());
                Log.d("CLUBS_FRAGMENT_REMEMBERED", clubItems.toString());
                clubItemAdapter.renewClubItems(clubItems);
            }
        } else {
            getUserProfile(new CallbackMethod() {
                @Override
                public void getUserProfileData(UserProfile userProfileData) {
                    userProfile = userProfileData;
                    clubItemAdapter.renewUserProfile(userProfileData);
                    UserApi.getInstance().setUserProfile(userProfileData);
                    getCarClubItems(new CallbackMethod() {
                        @Override
                        public void getCarClubItemsData(List<ClubItem> clubItemsData) {
                            clubItems = new ArrayList<>(clubItemsData);
                            clubItemAdapter.renewClubItems(clubItems);
                        }
                    });
                }
            });
        }

        setUserProfileListener();

        socialAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<ClubItem> newList = clubItems.stream().filter(item -> sub(item.getFullName().trim().toUpperCase(), s.toString().trim().toUpperCase())).collect(Collectors.toList());
                clubItemAdapter.renewClubItems(newList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void setUserProfileListener() {
        db.collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void getUserProfile(CallbackMethod callbackMethod) {
        db.collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                    UserProfile userProfile = doc.toObject(UserProfile.class);
                    userProfile.setDocId(doc.getId());
                    callbackMethod.getUserProfileData(userProfile);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCarClubItems(CallbackMethod callbackMethod) {
        db.collection("carClubItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    }

    public static boolean sub(String string, String substring) {
        int index = 0;
        for (char character : substring.toCharArray()) {
            index = string.indexOf(character, index);
            if (index == -1)
                return false;
        }
        return true;
    }
}