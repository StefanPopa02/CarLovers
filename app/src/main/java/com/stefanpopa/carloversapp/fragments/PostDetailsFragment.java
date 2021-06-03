package com.stefanpopa.carloversapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.Comment;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.CommentAdapter;
import com.stefanpopa.carloversapp.ui.PostAdapter;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailsFragment extends Fragment {

    private Post post;
    private ImageView backBtn;
    private RecyclerView recyclerViewImg;
    private PostAdapter postAdapter;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private CircleImageView profileImg;
    private EditText commentText;
    private TextView postCommentBtn;

    public PostDetailsFragment() {

    }

    public PostDetailsFragment(Post post) {
        this.post = post;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);
        Log.d("POST_DETAIL_FRAGMENT", "onResume called: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("POST_ADAPTER", "ON PAUSE CALLED IN CLUB PAGE");
//        PostAdapter.ViewHolder postAdapterVH = (PostAdapter.ViewHolder) recyclerViewImg.findViewHolderForAdapterPosition(0);
//        postAdapter.releasePlayer(postAdapterVH);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        backBtn = view.findViewById(R.id.post_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        profileImg = view.findViewById(R.id.image_profile);
        commentText = view.findViewById(R.id.add_comment);
        postCommentBtn = view.findViewById(R.id.post);
        Picasso.get().load(UserApi.getInstance().getUserProfile().getImageurl()).placeholder(R.drawable.ic_profile).into(profileImg);
        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(commentText.getText().toString())) {
                    Toast.makeText(getContext(), "Write something first", Toast.LENGTH_SHORT).show();
                } else {
                    postComment(commentText.getText().toString());
                }
            }
        });

        recyclerViewImg = view.findViewById(R.id.recycler_view_post);
        recyclerViewImg.setHasFixedSize(true);
        recyclerViewImg.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        postAdapter = new PostAdapter(getContext(), posts, true);
        recyclerViewImg.setAdapter(postAdapter);
        postAdapter.setFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());

        recyclerViewComments = view.findViewById(R.id.recycler_view_comments);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Comment> comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), comments);
        recyclerViewComments.setAdapter(commentAdapter);
        getPostComments();


        return view;
    }

    private void postComment(String commentMessage) {
        UserProfile loggedUser = UserApi.getInstance().getUserProfile();
        Comment comment = new Comment(loggedUser.getDocId(), loggedUser.getId(), post.getPostDocId(), commentMessage, new Timestamp(new Date()));
        FirebaseFirestore.getInstance().collection("Comments").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    commentText.setText("");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPostComments() {

        Query query = FirebaseFirestore.getInstance().collection("Comments").whereEqualTo("postDocId", post.getPostDocId());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("POST_DETAIL_FRAGMENT", "Listen failed.", error);
                    return;
                }
                if (value != null) {
                    List<Comment> comments = commentAdapter.getCommentList();
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("POST_DETAILS_FRAGMENT", "New comment: " + dc.getDocument().getData());
                                comments.add(dc.getDocument().toObject(Comment.class));
                                comments.get(comments.size()-1).setCommentDocId(dc.getDocument().getId());
                                break;
                            case MODIFIED:
                                Log.d("POST_DETAILS_FRAGMENT", "Modified comment: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d("POST_DETAILS_FRAGMENT", "Removed comment: " + dc.getDocument().getData());
                                break;
                        }
                    }
                    Collections.sort(comments, new Comparator<Comment>() {
                        @Override
                        public int compare(Comment o1, Comment o2) {
                            return o1.getTimeAdded().compareTo(o2.getTimeAdded());
                        }
                    });
                    commentAdapter.renewItems(comments);
                } else {
                    Log.d("POST_DETAIL_FRAGMENT", "Current data: null");
                }
            }
        });

    }
}