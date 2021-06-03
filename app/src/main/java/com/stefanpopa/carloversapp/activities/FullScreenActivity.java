package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.stefanpopa.carloversapp.R;

public class FullScreenActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    ImageView fullscreenBtn;
    boolean fullscreen = false;
    private Uri uri;
    private long remainingPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        uri = Uri.parse(getIntent().getStringExtra("Uri"));
        if(savedInstanceState!=null){
            remainingPosition = savedInstanceState.getLong("seek", 0);
        }

        playerView = findViewById(R.id.player_view);
        fullscreenBtn = playerView.findViewById(R.id.exo_fullscreen_icon);
        player = new SimpleExoPlayer.Builder(this).build();
        fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen = true;
                }
            }
        });

        playerView.setPlayer(player);
        Log.d("FULLSCREENACTIVITY", "REMAININGPOS :" + remainingPosition);

        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.seekTo(remainingPosition);
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        remainingPosition = player.getCurrentPosition();
        Log.d("FULLSCREEN_ACTIVITY", "REMAINING POS: " + remainingPosition);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("seek", player.getCurrentPosition());
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {
        player.release();
        super.onDestroy();
    }
}