package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.Comment;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.util.CallbackMethod;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void renewItems(List<Comment> commentList) {
        this.commentList = new ArrayList<>(commentList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.comment.setText(comment.getCommentMessage());
        getUserProfile(comment.getUserDocId(), new CallbackMethod() {
            @Override
            public void getUserProfileData(UserProfile userProfile) {
                String fullNameUser = userProfile.getFirstName() + " " + userProfile.getLastName();
                holder.fullName.setText(fullNameUser);
                Picasso.get().load(userProfile.getImageurl()).placeholder(R.drawable.ic_profile).into(holder.imgProfile);
            }
        });
    }

    private void getUserProfile(String userDocId, CallbackMethod callbackMethod) {
        FirebaseFirestore.getInstance().collection("users").document(userDocId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserProfile user = task.getResult().toObject(UserProfile.class);
                    callbackMethod.getUserProfileData(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void addItem(Comment comment) {
        commentList.add(comment);
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imgProfile;
        public TextView fullName;
        public TextView comment;

        public CommentViewHolder(@NonNull View itemView) {

            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile);
            fullName = itemView.findViewById(R.id.fullname);
            comment = itemView.findViewById(R.id.comment);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeComment(getBindingAdapterPosition());
                    return true;
                }
            });
        }

        private void removeComment(int bindingAdapterPosition) {
            Comment comment = commentList.get(bindingAdapterPosition);
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getUserId())) {
                commentList.remove(comment);
                FirebaseFirestore.getInstance().collection("Comments").document(comment.getCommentDocId()).delete();
                notifyDataSetChanged();
            }
        }
    }
}
