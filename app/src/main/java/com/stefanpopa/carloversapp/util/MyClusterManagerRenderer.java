package com.stefanpopa.carloversapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.ClusterMarker;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;
    private final String TAG = "MY_CLUSTER_MANAGER_RENDERER";
    private Context context;

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

        this.context = context;
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ClusterMarker item, @NonNull MarkerOptions markerOptions) {
        Log.d("MY_CLUSTER_MANAGER_RENDERER", "onClusterItemRendered: profilepicUrl: " + item.getIconPicture());
        imageView.setImageResource(R.drawable.ic_profile);
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        Log.d(TAG, "onBeforeClusterItemRendered: title: " + item.getTitle() + " snippet: " + item.getSnippet());
        markerOptions.title(item.getTitle());
    }

    @Override
    protected void onClusterItemRendered(@NonNull ClusterMarker item, @NonNull Marker markerOptions) {
        Log.d("MY_CLUSTER_MANAGER_RENDERER", "onClusterItemRendered: profilepicUrl: " + item.getIconPicture());

        String url = item.getIconPicture();
        if (url.equals("default")) {
            url = "https://firebasestorage.googleapis.com/v0/b/carloversapp-16ea7.appspot.com/o/UserProfilePhotos%2Ficon_no_profile.png?alt=media&token=3438aae4-51d5-4876-8578-079709552461";
        }

        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        Bitmap icon = iconGenerator.makeIcon();
                        markerOptions.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                        Log.d(TAG, "onClusterItemRendered: title: " + item.getTitle() + " snippet: " + item.getSnippet());
                        markerOptions.setTitle(item.getTitle());
                        markerOptions.setSnippet(item.getSnippet());
                        //markerOptions.showInfoWindow();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ClusterMarker> cluster) {
        return false;
    }

    public void setUpdateMarker(ClusterMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        if (marker != null) {
            marker.setPosition(clusterMarker.getPosition());
        }
    }
}
