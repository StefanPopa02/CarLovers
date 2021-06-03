package com.stefanpopa.carloversapp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.FullScreenActivity;
import com.stefanpopa.carloversapp.fragments.PostDetailsFragment;
import com.stefanpopa.carloversapp.model.MediaObject;
import com.stefanpopa.carloversapp.model.Post;

import java.util.ArrayList;
import java.util.List;

public class SliderPostAdapter extends SliderViewAdapter<SliderPostAdapter.SliderPostAdapterViewHolder> {

    private Context context;
    private List<String> imageUrl;
    private Post currentPost;
    private List<String> videosUrl;
    private List<MediaObject> media;
    public SimpleExoPlayer player;


    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void renewPost(Post post) {
        this.currentPost = post;
        imageUrl = post.getImageUrl();
        videosUrl = post.getVideosUrl();
        media = new ArrayList<>();
        if (videosUrl != null) {
            for (String str : videosUrl) {
                MediaObject video = new MediaObject();
                video.setVideoUrl(str);
                media.add(video);
            }
        }
        if (imageUrl != null) {
            for (String str : imageUrl) {
                MediaObject image = new MediaObject();
                image.setImgUrl(str);
                media.add(image);
            }
        }
        Log.d("SLIDER_POST_ADAPTER", "MEDIA CONTINE: " + media.toString());
        notifyDataSetChanged();
    }

    public SliderPostAdapter(Context context) {
        this.context = context;
    }

    @Override
    public SliderPostAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_slider_image_post, null);
        return new SliderPostAdapter.SliderPostAdapterViewHolder(inflate);
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
                    viewHolder.playerView.setPlayer(player);

                    Uri uri = Uri.parse(currentVideoUrl);
                    MediaItem mediaItem = MediaItem.fromUri(uri);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
                    player.setPlayWhenReady(false);
                    //player.release();
                    viewHolder.fullscreenBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("SLIDER_ADD_POST_ADAPTER", "FULLSCREEN CLICKED!");
                            Intent i = new Intent(context, FullScreenActivity.class);
                            Long positionSeek = player.getCurrentPosition();
                            Log.d("SLIDER_POST_ADAPTER", "positionSeek: " + positionSeek);
                            i.putExtra("seek", positionSeek);
                            i.putExtra("Uri", currentVideoUrl);
                            player.pause();
                            player.setPlayWhenReady(false);
                            player.release();
                            context.startActivity(i);
                        }
                    });
                } catch (Exception e) {
                    Log.d("SLIDER_POST_ADAPTER", "ERROR: " + e.getMessage());
                }
            }
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
        return media.size();
    }

    public class SliderPostAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        PlayerView playerView;
        ImageView fullscreenBtn;

        public SliderPostAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.car_image_post);
            playerView = itemView.findViewById(R.id.exoplayer_item);
            fullscreenBtn = playerView.findViewById(R.id.exo_fullscreen_icon);
            player = new SimpleExoPlayer.Builder(context).build();
            this.itemView = itemView;
        }
    }


}
