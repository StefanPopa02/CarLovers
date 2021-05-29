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
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.PostActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.PostAdapter;
import com.stefanpopa.carloversapp.util.FirebaseData;
import com.stefanpopa.carloversapp.util.UserApi;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
        ((WelcomeActivity)getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);
        Log.d("CLUB_PAGE_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        setCurrentPagePosts();

        selectedMenuText = view.findViewById(R.id.club_page_selected_menu);
        clubPageLogo = view.findViewById(R.id.club_page_logo);
        clubPageFullName = view.findViewById(R.id.club_page_fullname);
        addPostBtn = view.findViewById(R.id.club_page_post_add_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
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
                        break;
                    case R.id.club_page_members:
                        selectedMenuText.setText("Members");
                        //TODO: set recyclerViewAdapter
                        break;

                }

                return true;
            }
        });

        return view;
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

}