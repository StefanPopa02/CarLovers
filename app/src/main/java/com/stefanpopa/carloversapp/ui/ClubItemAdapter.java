package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.ClubPageFragment;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClubItemAdapter extends RecyclerView.Adapter<ClubItemAdapter.ViewHolder> {

    private List<ClubItem> clubItems;
    private Context context;
    private boolean isFragment;
    private UserProfile userProfile;
    private FirebaseFirestore db;


    public ClubItemAdapter(Context context, List<ClubItem> clubItems, UserProfile userProfile, boolean isFragment) {
        this.context = context;
        this.clubItems = clubItems;
        this.isFragment = isFragment;
        this.userProfile = userProfile;
        db = FirebaseFirestore.getInstance();
    }

    public void renewUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void renewClubItems(List<ClubItem> clubItems) {
        this.clubItems = clubItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_rv_item, parent, false);
        return new ClubItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        //Log.d("CLUB_ITEM_ADAPTER", "userProfile in adapter: " + userProfile.toString());
        ClubItem clubItem = clubItems.get(position);

        holder.clubModel.setText(clubItem.getModel());
        holder.clubBrand.setText(clubItem.getBrand());
        Picasso.get().load(clubItem.getLogoImgUrl()).into(holder.clubProfilePic);

        holder.clubModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ClubPageFragment(clubItem, userProfile), "CLUBS_PAGE_FRAGMENT").addToBackStack(null).commit();
            }
        });

        holder.clubBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ClubPageFragment(clubItem, userProfile), "CLUBS_PAGE_FRAGMENT").addToBackStack(null).commit();
            }
        });

        holder.clubProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ClubPageFragment(clubItem, userProfile), "CLUBS_PAGE_FRAGMENT").addToBackStack(null).commit();
            }
        });

        holder.clubFollowBtn.setVisibility(View.VISIBLE);

        if (!userProfile.getFollowingClubs().contains(clubItem.getId())) {
            holder.clubFollowBtn.setText("FOLLOW");
        } else {
            holder.clubFollowBtn.setText("UNFOLLOW");
        }
        String userDocId = UserApi.getInstance().getUserProfile().getDocId();
        holder.clubFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection("users").document(userDocId);
                if (docRef != null) {
                    if (holder.clubFollowBtn.getText().toString().equals("FOLLOW")) {
                        docRef.update("followingClubs", FieldValue.arrayUnion(clubItem.getId()));
                    } else if (holder.clubFollowBtn.getText().toString().equals("UNFOLLOW")) {
                        docRef.update("followingClubs", FieldValue.arrayRemove(clubItem.getId()));
                    }
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return clubItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView clubProfilePic;
        public TextView clubBrand;
        public TextView clubModel;
        public Button clubFollowBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clubBrand = itemView.findViewById(R.id.club_brand);
            clubModel = itemView.findViewById(R.id.club_model);
            clubProfilePic = itemView.findViewById(R.id.club_image_profile);
            clubFollowBtn = itemView.findViewById(R.id.club_follow_btn);

        }
    }
}
