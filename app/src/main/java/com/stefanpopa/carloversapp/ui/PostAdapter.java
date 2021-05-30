package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.fragments.ClubPageFragment;
import com.stefanpopa.carloversapp.fragments.PostDetailsFragment;
import com.stefanpopa.carloversapp.fragments.ProfileFragment;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.util.CallbackMethod;
import com.stefanpopa.carloversapp.util.FirebaseData;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private FirebaseUser firebaseUser;
    private boolean isPostDetail;

    public PostAdapter(Context context, List<Post> posts, boolean isPostDetail) {
        this.context = context;
        this.posts = posts;
        this.isPostDetail = isPostDetail;
    }

    public void renewPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_page_post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        if (isPostDetail) {
            holder.postMore.setVisibility(View.GONE);
        } else {
            holder.postMore.setVisibility(View.VISIBLE);
            holder.postMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("POST_ADAPTER", "POPUP clicked");
                    Toast.makeText(context, "Popup clicked", Toast.LENGTH_SHORT).show();
                    PopupMenu popupMenu = new PopupMenu(context, holder.postMore);
                    popupMenu.inflate(R.menu.more_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_delete:
                                    deletePost(post);
                                    return true;
                                default:
                                    return false;
                            }

                        }
                    });
                    popupMenu.show();
                }
            });
        }

        if (post.getImageUrl() != null) {
            holder.sliderView.setSliderAdapter(new SliderPostAdapter(context, post));
        } else {
            holder.sliderView.setVisibility(View.GONE);
        }
        Picasso.get().load(post.getUserProfileImgUrl()).placeholder(R.drawable.ic_profile).into(holder.postImageProfile);
        holder.postImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ProfileFragment(FirebaseAuth.getInstance().getUid()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.postAuthorFullname.setText(post.getUserFullname());
        holder.postAuthorFullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ProfileFragment(FirebaseAuth.getInstance().getUid()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.postUsername.setText(post.getUsername());
        holder.postUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ProfileFragment(FirebaseAuth.getInstance().getUid()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.postClubName.setText(post.getClubFullname());
        holder.postClubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClubItem(post.getClubId(), new CallbackMethod() {
                    @Override
                    public void getSelectedCarClubItem(ClubItem clubItem) {
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.fragment_container, new ClubPageFragment(clubItem, UserApi.getInstance().getUserProfile()), "CLUBS_PAGE_FRAGMENT").addToBackStack(null).commit();
                    }
                });

            }
        });
        holder.postQuestion.setText(post.getDescription());
        holder.postNoOfLikes.setText(post.getNo_of_likes() + " likes");
        isPostLiked(holder, post.getPostDocId());
        holder.postLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POST_ADAPTER", "LIKED BTN CLICKED");
                if (holder.postLike.getTag().equals("like")) {
                    FirebaseFirestore.getInstance().collection("Likes").document(post.getPostDocId()).update("userIds", FieldValue.arrayUnion(FirebaseAuth.getInstance().getUid()));
                } else if (holder.postLike.getTag().equals("liked")) {
                    FirebaseFirestore.getInstance().collection("Likes").document(post.getPostDocId()).update("userIds", FieldValue.arrayRemove(FirebaseAuth.getInstance().getUid()));
                }
            }
        });
        if (isPostDetail) {
            //holder.postComments.setVisibility(View.GONE);
            holder.postNoOfComments.setVisibility(View.GONE);
        } else {
            holder.postComments.setVisibility(View.VISIBLE);
            holder.postComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //FrameLayout frameLayout = ((WelcomeActivity) context).findViewById(R.id.fragment_container);
                    //frameLayout.removeAllViews();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                            .add(R.id.fragment_container, new PostDetailsFragment(post), "CLUBS_POST_DETAIL_FRAGMENT")
                            .addToBackStack(null)
                            .commit();
                }
            });
            holder.postNoOfComments.setVisibility(View.VISIBLE);
            getNrOfComments(post, holder);
            holder.postNoOfComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                            .add(R.id.fragment_container, new PostDetailsFragment(post), "CLUBS_POST_DETAIL_FRAGMENT")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

    }

    private void deletePost(Post post) {
        FirebaseFirestore.getInstance().collection("Posts").document(post.getPostDocId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    posts.remove(post);
                    notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getClubItem(int clubId, CallbackMethod callbackMethod) {

        FirebaseFirestore.getInstance().collection("carClubItems").whereEqualTo("id", clubId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    callbackMethod.getSelectedCarClubItem(docs.get(0).toObject(ClubItem.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    private void getNrOfComments(Post post, ViewHolder holder) {
        Log.d("POST_ADAPTER", post.toString());
        FirebaseFirestore.getInstance().collection("Comments").whereEqualTo("postDocId", post.getPostDocId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int nrcomms = task.getResult().getDocuments().size();
                    Log.d("POST_ADAPTER", "nrcomms: " + nrcomms);
                    String nrComments = nrcomms + " comments";
                    holder.postNoOfComments.setText(nrComments);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isPostLiked(ViewHolder holder, String postId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Likes").document(postId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("POST_ADAPTER", "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Log.d("POST_ADAPTER", "Current data: " + value.getData());
                    List<String> currentUserIdsLike = (List<String>) value.get("userIds");
                    holder.postNoOfLikes.setText(currentUserIdsLike.size() + " likes");
                    if (currentUserIdsLike.contains(FirebaseAuth.getInstance().getUid())) {
                        Log.d("POST_ADAPTER", "Userul a dat like");
                        holder.postLike.setImageResource(R.drawable.ic_liked);
                        holder.postLike.setTag("liked");
                    } else {
                        Log.d("POST_ADAPTER", "Userul NU a dat like");
                        holder.postLike.setImageResource(R.drawable.ic_like);
                        holder.postLike.setTag("like");
                    }
                } else {
                    Log.d("POST_ADAPTER", "Current data: null");
                    holder.postLike.setImageResource(R.drawable.ic_like);
                    holder.postLike.setTag("like");
                    Map<String, List<String>> customMap = new HashMap<>();
                    customMap.put("userIds", new ArrayList<>());
                    documentReference.set(customMap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postMore;
        public CircleImageView postImageProfile;
        public TextView postUsername;
        public TextView postClubName;
        public TextView postQuestion;
        public ImageView postImage;
        public ImageView postLike;
        public ImageView postComments;
        public TextView postNoOfLikes;
        public TextView postAuthorFullname;
        public SocialTextView postHashtags;
        public TextView postNoOfComments;
        public SliderView sliderView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postMore = itemView.findViewById(R.id.club_page_post_more);
            postImageProfile = itemView.findViewById(R.id.club_page_post_image_profile);
            postUsername = itemView.findViewById(R.id.club_page_post_username);
            postClubName = itemView.findViewById(R.id.club_page_post_club_name);
            postQuestion = itemView.findViewById(R.id.club_page_post_question);
            postLike = itemView.findViewById(R.id.club_page_post_like);
            postComments = itemView.findViewById(R.id.club_page_post_comments);
            postNoOfLikes = itemView.findViewById(R.id.club_page_post_no_of_likes);
            postNoOfComments = itemView.findViewById(R.id.club_page_post_no_of_comments);
            postAuthorFullname = itemView.findViewById(R.id.club_page_post_author);
            postHashtags = itemView.findViewById(R.id.club_page_post_hashtags);
            sliderView = itemView.findViewById(R.id.club_page_post_image);
        }
    }


}
