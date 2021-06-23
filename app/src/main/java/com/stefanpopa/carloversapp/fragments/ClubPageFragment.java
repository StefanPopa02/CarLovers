package com.stefanpopa.carloversapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.PostActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.ClubMember;
import com.stefanpopa.carloversapp.model.Comment;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.ClubMemberAdapter;
import com.stefanpopa.carloversapp.ui.PostAdapter;
import com.stefanpopa.carloversapp.util.FirebaseData;
import com.stefanpopa.carloversapp.util.UserApi;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClubPageFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private TextView selectedMenuText;
    private ClubItem clubItem;
    private ImageView clubPageLogo;
    private TextView clubPageFullName;
    private UserProfile userProfile;
    private Button addPostBtn;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> posts;
    private FirebaseFirestore db;
    private ClubMemberAdapter clubMemberAdapter;

    public ClubPageFragment(ClubItem clubItem) {
        this.clubItem = clubItem;
    }

    public ClubPageFragment() {

    }

    public ClubPageFragment(ClubItem clubItem, UserProfile userProfile) {
        this.clubItem = clubItem;
        this.userProfile = userProfile;
    }


    @Override
    public void onResume() {

        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);
        Log.d("CLUB_PAGE_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CLUB_PAGE_FRAGMENT", "onCreateView Called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club_page, container, false);
        db = FirebaseFirestore.getInstance();
        posts = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view_club_page);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), posts, false);
        postAdapter.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
        postAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        recyclerView.setAdapter(postAdapter);
        //setCurrentPagePosts();

        selectedMenuText = view.findViewById(R.id.club_page_selected_menu);
        clubPageLogo = view.findViewById(R.id.club_page_logo);
        clubPageFullName = view.findViewById(R.id.club_page_fullname);
        addPostBtn = view.findViewById(R.id.club_page_post_add_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                if (selectedMenuText.getText().equals("Latest Posts")) {
                    intent.putExtra("currentPage", "Posts");
                } else if (selectedMenuText.getText().equals("Announces")) {
                    intent.putExtra("currentPage", "Announces");
                }

                FirebaseData.getInstance().setSelectedClubItem(clubItem);
                UserApi.getInstance().setUserProfile(userProfile);
                startActivity(intent);
            }
        });

        if (clubItem != null) {
            Picasso.get().load(clubItem.getLogoImgUrl()).placeholder(R.drawable.abarth).into(clubPageLogo);
            clubPageFullName.setText(clubItem.getFullName());
        }

        bottomNavigationView = view.findViewById(R.id.top_club_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.club_page_posts:
                        addPostBtn.setVisibility(View.VISIBLE);
                        selectedMenuText.setText("Latest Posts");
                        postAdapter = new PostAdapter(getContext(), posts, false);
                        postAdapter.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
                        postAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
                        recyclerView.setAdapter(postAdapter);
                        setCurrentPagePosts();
                        break;

                    case R.id.club_page_announces:
                        selectedMenuText.setText("Announces");
                        //TODO: set recyclerViewAdapter
                        if (!userProfile.getUserType().equals("admin")) {
                            addPostBtn.setVisibility(View.GONE);
                        } else {
                            addPostBtn.setVisibility(View.VISIBLE);
                        }
                        posts.clear();
                        postAdapter = new PostAdapter(getContext(), posts, false);
                        postAdapter.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
                        postAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
                        recyclerView.setAdapter(postAdapter);
                        setCurrentPageAnnounces();
                        break;

                    case R.id.club_page_members:
                        addPostBtn.setVisibility(View.GONE);
                        selectedMenuText.setText("Members");
                        List<ClubMember> members = new ArrayList<>();
                        clubMemberAdapter = new ClubMemberAdapter(getContext(), members);
                        clubMemberAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
                        recyclerView.setAdapter(clubMemberAdapter);
                        setCurrentPageMembers();
                        break;

                }

                return true;
            }
        });

        Log.d("CLUB_PAGE_FRAGMENT", "BottomNav: " + String.valueOf(bottomNavigationView.getSelectedItemId()));
        if (bottomNavigationView.getSelectedItemId() != 0) {
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
        }

        return view;
    }

    private void setCurrentPageMembers() {
        List<ClubMember> members = new ArrayList<>();
        db.collection("users").whereArrayContains("followingClubs", clubItem.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        UserProfile user = doc.toObject(UserProfile.class);
                        Log.d("CLUB_PAGE_FRAGMENT", "MEMBER: " + user.toString());
                        Log.d("CLUB_PAGE_FRAGMENT", "carId " + clubItem.getId() + " userId " + user.getId());
                        db.collection("ClubAdmins").whereEqualTo("carClubId", clubItem.getId()).whereEqualTo("userId", user.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        members.add(0, new ClubMember(user.getId(), user.getUsername(), user.getFirstName() + " " + user.getLastName(), true, user.getImageurl()));
                                        Log.d("CLUB_PAGE_FRAGMENT", "ADMIN found");
                                    } else {
                                        Log.d("CLUB_PAGE_FRAGMENT", "USER found");
                                        members.add(new ClubMember(user.getId(), user.getUsername(), user.getFirstName() + " " + user.getLastName(), false, user.getImageurl()));
                                    }
                                    clubMemberAdapter.renewItems(members);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Log.d("CLUB_PAGE_FRAGMENT", "ALL MEMBERS: " + members);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCurrentPageAnnounces() {
        List<Post> postsData = new ArrayList<>();
        db.collection("Announces").whereEqualTo("clubId", clubItem.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        postsData.add(doc.toObject(Post.class));
                        postsData.get(postsData.size() - 1).setPostDocId(doc.getId());
                    }
                    Collections.sort(posts, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return o1.getTimeAdded().compareTo(o2.getTimeAdded());
                        }
                    });
                    //Collections.reverse(posts);
                    postAdapter.renewPosts(postsData);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCurrentPagePosts() {
        List<Post> postsData = new ArrayList<>();
        db.collection("Posts").whereEqualTo("clubId", clubItem.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        postsData.add(doc.toObject(Post.class));
                        postsData.get(postsData.size() - 1).setPostDocId(doc.getId());
                    }
                    Collections.sort(posts, new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return o1.getTimeAdded().compareTo(o2.getTimeAdded());
                        }
                    });
                    Collections.reverse(posts);
                    List<String> videosUrl = new ArrayList<>();
                    videosUrl.add("https://firebasestorage.googleapis.com/v0/b/carloversapp-16ea7.appspot.com/o/Videos%2Fb6.mp4?alt=media&token=b601ba4e-871c-4732-893b-790cc9834719");
                    postAdapter.renewPosts(postsData);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        Log.d("POST_ADAPTER", "ON PAUSE CALLED IN CLUB PAGE");
//        PostAdapter.ViewHolder postAdapterVH = (PostAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition();
//        postAdapter.releasePlayer(postAdapterVH);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d("POST_ADAPTER", "ON DESTROY CALLED IN CLUB PAGE");
//        PostAdapter.ViewHolder postAdapterVH = (PostAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
//        postAdapter.releasePlayer(postAdapterVH);
        super.onDestroyView();
    }
}