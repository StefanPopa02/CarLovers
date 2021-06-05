package com.stefanpopa.carloversapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.maps.android.clustering.ClusterManager;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.MainActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClusterMarker;
import com.stefanpopa.carloversapp.model.Comment;
import com.stefanpopa.carloversapp.model.UserLocation;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.services.LocationService;
import com.stefanpopa.carloversapp.util.MyClusterManagerRenderer;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MapsFragment extends Fragment {

    private TextView textView;
    private FusedLocationProviderClient mFusedLocationClient;
    private String TAG = "MAPS_FRAGMENT";
    private UserLocation userLocation;
    private FirebaseFirestore db;
    private List<UserLocation> userLocationList;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private List<ClusterMarker> mClusterMarkers;
    private Intent serviceIntent;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //googleMap.setMyLocationEnabled(true);
            mGoogleMap = googleMap;
            //addMapMarkers();
        }
    };

    private void setCameraView() {
        if (userLocation != null) {
            double bottomBoundary = userLocation.getGeoPoint().getLatitude() - .1;
            double leftBoundary = userLocation.getGeoPoint().getLongitude() - .1;
            double topBoundary = userLocation.getGeoPoint().getLatitude() + .1;
            double rightBoundary = userLocation.getGeoPoint().getLongitude() + .1;
            mMapBoundary = new LatLngBounds(new LatLng(bottomBoundary, leftBoundary), new LatLng(topBoundary, rightBoundary));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        userLocationList = new ArrayList<>();
        mClusterMarkers = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called: ");
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);
        getLastKnownLocation();
        getUsersLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serviceIntent != null) {
            requireActivity().stopService(serviceIntent);
        }

    }

    private void addMapMarkers(UserLocation currentUserLocation) {

        if (mGoogleMap != null) {

            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getContext(),
                        mGoogleMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            Log.d(TAG, "addMapMarkers: userLocation: " + currentUserLocation);
            List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            userLocationList = new ArrayList<>();
            userLocationList.add(currentUserLocation);
            for (UserLocation userLocation : userLocationList) {
                tasks.add(db.collection("users").whereEqualTo("id", userLocation.getUserId()).get());
            }
            Tasks.whenAllSuccess(tasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                @Override
                public void onComplete(@NonNull Task<List<Object>> task) {
                    if (task.isSuccessful()) {
                        List<Object> results = task.getResult();
                        for (Object user : results) {
                            QuerySnapshot querySnapshot = (QuerySnapshot) user;
                            UserProfile currentUser = querySnapshot.getDocuments().get(0).toObject(UserProfile.class);
                            Log.d(TAG, "onComplete: USER: " + currentUser);
                            String snippet = "";
                            if (currentUser.getId().equals(FirebaseAuth.getInstance().getUid())) {
                                snippet = "This is you";
                            } else {
                                snippet = "Determine route to " + currentUser.getUsername() + "?";
                            }

                            String avatar = "default"; // set the default avatar
                            try {
                                avatar = currentUser.getImageurl();
                            } catch (NumberFormatException e) {
                                Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUserId() + ", setting default.");
                            }
                            ClusterMarker newClusterMarker = new ClusterMarker(
                                    new LatLng(currentUserLocation.getGeoPoint().getLatitude(), currentUserLocation.getGeoPoint().getLongitude()),
                                    currentUser.getUsername(),
                                    snippet,
                                    avatar,
                                    currentUser
                            );
                            mClusterManager.addItem(newClusterMarker);
                            mClusterMarkers.add(newClusterMarker);
                        }
                        mClusterManager.cluster();
                    }
                }
            });

            //setCameraView();
        }
    }

    private void getUsersLocation() {
        Query query = db.collection("UserLocation");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("POST_DETAIL_FRAGMENT", "Listen failed.", error);
                    return;
                }
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New userLocation: " + dc.getDocument().getData());
                                UserLocation currentUserLocation = dc.getDocument().toObject(UserLocation.class);
                                addMapMarkers(currentUserLocation);
//                                boolean OK = false;
//                                for (UserLocation userLocation : userLocationList) {
//                                    if (userLocation.getUserId().equals(currentUserLocation.getUserId())) {
//                                        userLocationList.remove(userLocation);
//                                        userLocationList.add(currentUserLocation);
//                                        OK = true;
//                                        break;
//                                    }
//                                }
//                                if (!OK) {
//                                    userLocationList.add(currentUserLocation);
//                                }
//                                OK = false;
//                                addMapMarkers(currentUserLocation);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified userLocation: " + dc.getDocument().getData());
                                currentUserLocation = dc.getDocument().toObject(UserLocation.class);
//                                for (UserLocation userLocation : userLocationList) {
//                                    if (userLocation.getUserId().equals(currentUserlocation.getUserId())) {
//                                        userLocationList.remove(userLocation);
//                                        userLocationList.add(currentUserlocation);
//                                        break;
//                                    }
//                                }

                                for (ClusterMarker clusterMarker : mClusterMarkers) {
                                    if (clusterMarker.getUser().getId().equals(currentUserLocation.getUserId())) {
                                        LatLng updatedLatLng = new LatLng(
                                                currentUserLocation.getGeoPoint().getLatitude(),
                                                currentUserLocation.getGeoPoint().getLongitude()
                                        );
                                        clusterMarker.setPosition(updatedLatLng);
                                        if (mClusterManagerRenderer != null) {
                                            mClusterManagerRenderer.setUpdateMarker(clusterMarker);
                                        }
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed userLocation: " + dc.getDocument().getData());
                                break;
                        }
                    }

                }
            }
        });
    }

    private void saveUserLocation() {
        if (userLocation != null) {
            DocumentReference docRef = db.collection("UserLocation").document(FirebaseAuth.getInstance().getUid());
            docRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "onComplete: UserLocation saved to db! " + userLocation.toString());
                }
            });
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation called");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude() + " longitude: " + geoPoint.getLongitude());
                    userLocation = new UserLocation();
                    userLocation.setGeoPoint(geoPoint);
                    userLocation.setTimestamp(null);
                    userLocation.setUserId(FirebaseAuth.getInstance().getUid());
                    saveUserLocation();
                    startLocationService();
                    setCameraView();
                }
            }
        });
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            serviceIntent = new Intent(getContext(), LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(serviceIntent);
            } else {
                getContext().startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.stefanpopa.carloversapp.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

}