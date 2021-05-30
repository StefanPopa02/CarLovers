package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.PostDetailsFragment;
import com.stefanpopa.carloversapp.fragments.ProfileFragment;
import com.stefanpopa.carloversapp.model.ClubMember;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClubMemberAdapter extends RecyclerView.Adapter<ClubMemberAdapter.ViewHolder> {

    private Context context;
    private List<ClubMember> members;

    public ClubMemberAdapter(Context context, List<ClubMember> members) {
        this.context = context;
        this.members = members;
    }

    public void renewItems(List<ClubMember> members){
        this.members = new ArrayList<>(members);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_page_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClubMember clubMember = members.get(position);
        holder.fullName.setText(clubMember.getFullname());
        holder.username.setText(clubMember.getUsername());
        Picasso.get().load(clubMember.getImageProfileUrl()).placeholder(R.drawable.ic_profile).into(holder.imageProfile);
        if (clubMember.isAdmin()) {
            holder.userRole.setVisibility(View.VISIBLE);
        } else {
            holder.userRole.setVisibility(View.GONE);
        }
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .add(R.id.fragment_container, new ProfileFragment(clubMember.getUserId()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .add(R.id.fragment_container, new ProfileFragment(clubMember.getUserId()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.fullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .add(R.id.fragment_container, new ProfileFragment(clubMember.getUserId()), "PROFILE_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageProfile;
        private TextView username;
        private TextView fullName;
        private TextView userRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullname);
            userRole = itemView.findViewById(R.id.user_status);
        }
    }
}
