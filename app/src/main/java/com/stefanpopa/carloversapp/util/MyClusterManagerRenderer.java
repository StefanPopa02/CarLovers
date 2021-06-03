package com.stefanpopa.carloversapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

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

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

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
        if (item.getIconPicture().equals("default")) {
            String url = "https://firebasestorage.googleapis.com/v0/b/carloversapp-16ea7.appspot.com/o/UserProfilePhotos%2Ficon_no_profile.png?alt=media&token=3438aae4-51d5-4876-8578-079709552461";
            Picasso.get().load(url).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: default profile icon");
                    Bitmap icon = iconGenerator.makeIcon();
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                    markerOptions.title(item.getTitle());
                }

                @Override
                public void onError(Exception e) {
                }
            });
        } else {
            Picasso.get().load(item.getIconPicture()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: custom profile icon");
                    Bitmap icon = iconGenerator.makeIcon();
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                    markerOptions.title(item.getTitle());
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }

    }

    @Override
    protected void onClusterItemRendered(@NonNull ClusterMarker item, @NonNull Marker markerOptions) {
        Log.d("MY_CLUSTER_MANAGER_RENDERER", "onClusterItemRendered: profilepicUrl: " + item.getIconPicture());
        if (item.getIconPicture().equals("default")) {
            String url = "https://firebasestorage.googleapis.com/v0/b/carloversapp-16ea7.appspot.com/o/UserProfilePhotos%2Ficon_no_profile.png?alt=media&token=3438aae4-51d5-4876-8578-079709552461";
            Picasso.get().load(url).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: default profile icon");
                    Bitmap icon = iconGenerator.makeIcon();
                    markerOptions.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    markerOptions.setTitle(item.getTitle());
                }

                @Override
                public void onError(Exception e) {
                }
            });
        } else {
            Picasso.get().load(item.getIconPicture()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: custom profile icon");
                    Bitmap icon = iconGenerator.makeIcon();
                    markerOptions.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    markerOptions.setTitle(item.getTitle());
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ClusterMarker> cluster) {
        return false;
    }
}
