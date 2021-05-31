package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.PostDetailsFragment;
import com.stefanpopa.carloversapp.model.MediaObject;
import com.stefanpopa.carloversapp.model.Post;

import java.util.ArrayList;
import java.util.List;

public class SliderAddPostAdapter extends SliderViewAdapter<SliderAddPostAdapter.SliderPostAdapterViewHolder> {

    private Context context;
    private List<MediaObject> media;

    public void deleteItem(int position){
        this.media.remove(position);
        notifyDataSetChanged();
    }

    public void renewItems(List<MediaObject> media){
        this.media = media;
        notifyDataSetChanged();
    }

    public List<MediaObject> getItems(){
        return this.media;
    }

    public SliderAddPostAdapter(Context context) {
        this.context = context;
        this.media = new ArrayList<>();
        }

    @Override
    public SliderPostAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_slider_image_post, null);
        return new SliderAddPostAdapter.SliderPostAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderPostAdapterViewHolder viewHolder, int position) {

        if (media != null) {
            String currentImageUrl = media.get(position).getImgUrl();
            String currentVideoUrl = media.get(position).getVideoUrl();
            Log.d("SLIDER_POST_ADAPTER", "CURRENT IMG: " + currentImageUrl + " CURRENT VIDEO: " + currentVideoUrl);
            if (currentImageUrl != null) {
                viewHolder.imageViewBackground.setVisibility(View.VISIBLE);
                viewHolder.playerView.setVisibility(View.GONE);
                Picasso.get().load(currentImageUrl)
                        .resize(1920, 1440)
                        //.onlyScaleDown()
                        //.fit()
                        //.centerCrop()
                        .placeholder(R.drawable.no_car_img)
                        .into(viewHolder.imageViewBackground);
            } else if (currentVideoUrl != null) {
                try {
                    Log.d("SLIDER_POST_ADAPTER", "VIDEO EXISTS");
                    viewHolder.imageViewBackground.setVisibility(View.GONE);
                    viewHolder.playerView.setVisibility(View.VISIBLE);
                    Uri uri = Uri.parse(currentVideoUrl);
                    SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
                    viewHolder.playerView.setPlayer(player);
                    MediaItem mediaItem = MediaItem.fromUri(uri);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    //player.play();
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
                    //player.release();
                } catch (Exception e) {
                    Log.d("SLIDER_POST_ADAPTER", "ERROR: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public int getCount() {
        return media.size();
    }

    public class SliderPostAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        PlayerView playerView;

        public SliderPostAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.car_image_post);
            playerView = itemView.findViewById(R.id.exoplayer_item);
            this.itemView = itemView;
        }
    }
}

