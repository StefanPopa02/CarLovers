package com.stefanpopa.carloversapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.PostAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClubFeedFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private RecyclerView recyclerViewHashTags;
    private PostAdapter postAdapter;
    private List<Post> posts;

    public ClubFeedFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);
        Log.d("CLUBS_FEED_FRAGMENT", "onResume called: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_feed, container, false);
        recyclerViewPosts = view.findViewById(R.id.clubs_recycler_view);
        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts, false);
        postAdapter.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
        postAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        recyclerViewPosts.setAdapter(postAdapter);
        getUserPosts();
        return view;
    }

    private void getUserPosts() {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    UserProfile user = task.getResult().getDocuments().get(0).toObject(UserProfile.class);
                    Log.d("CLUB_FEED_FRAGMENT", "USER GASIT: " + user.toString());
                    List<Integer> followingClubs = user.getFollowingClubs();
                    FirebaseFirestore.getInstance().collection("Posts").whereIn("clubId", followingClubs).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                List<Post> latestPosts = new ArrayList<>();
                                for (DocumentSnapshot doc : docs) {
                                    latestPosts.add(doc.toObject(Post.class));
                                    latestPosts.get(latestPosts.size() - 1).setPostDocId(doc.getId());
                                }
                                Log.d("CLUB_FEED_ADAPTER", "LATEST POSTS: " + latestPosts);
                                Collections.sort(posts, new Comparator<Post>() {
                                    @Override
                                    public int compare(Post o1, Post o2) {
                                        return o1.getTimeAdded().compareTo(o2.getTimeAdded());
                                    }
                                });
                                postAdapter.renewPosts(latestPosts);
                            }
                        }
                    });
                }
            }
        });
    }
}