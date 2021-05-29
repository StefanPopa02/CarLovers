package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.ClubPageFragment;
import com.stefanpopa.carloversapp.fragments.PostDetailsFragment;
import com.stefanpopa.carloversapp.model.Post;

import java.util.List;

public class SliderPostAdapter extends SliderViewAdapter<SliderPostAdapter.SliderPostAdapterViewHolder> {

    private Context context;
    private List<String> imageUrl;
    private Post currentPost;

    public SliderPostAdapter(Context context, Post post) {
        this.context = context;
        this.currentPost = post;
        imageUrl = post.getImageUrl();
    }

    @Override
    public SliderPostAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_slider_image_post, null);
        return new SliderPostAdapter.SliderPostAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderPostAdapterViewHolder viewHolder, int position) {

        if (imageUrl != null) {
            String currentImageUrl = imageUrl.get(position);
            Picasso.get().load(currentImageUrl)
                    .resize(1920, 1440)
                    //.onlyScaleDown()
                    //.fit()
                    //.centerCrop()
                    .placeholder(R.drawable.no_car_img)
                    .into(viewHolder.imageViewBackground);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new PostDetailsFragment(currentPost), "CLUBS_POST_DETAIL_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        });


    }

    @Override
    public int getCount() {
        return imageUrl.size();
    }

    public class SliderPostAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public SliderPostAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.car_image_post);
            this.itemView = itemView;
        }
    }
}
