package com.stefanpopa.carloversapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.AddMeetingActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.ClusterMarker;
import com.stefanpopa.carloversapp.model.Meeting;
import com.stefanpopa.carloversapp.model.UserLocation;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.services.LocationService;
import com.stefanpopa.carloversapp.util.MyClusterManagerRenderer;
import com.stefanpopa.carloversapp.util.UserApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private EditText inputSearchText;
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

        @SuppressLint("PotentialBehaviorOverride")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGoogleMap = googleMap;
            mGoogleMap.setOnInfoWindowClickListener(MapsFragment.this);
            initSearchText();
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
        inputSearchText = view.findViewById(R.id.input_search);
    }

    private void initSearchText() {
        inputSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate() {
        String searchString = inputSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "ADDRESS: " + address.toString());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            MarkerOptions options = new MarkerOptions().position(latLng).title(address.getAddressLine(0));
            options.snippet("Add new meeting?");
            addNewMeeting(options);
            //mGoogleMap.addMarker(options);
        }
    }

    private void addNewMeeting(MarkerOptions marker) {
        //daca e administrator
        UserApi.getInstance().setMarker(marker);
        startActivity(new Intent(getContext(), AddMeetingActivity.class));
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
        getMeetings();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serviceIntent != null) {
            requireActivity().stopService(serviceIntent);
        }

    }

    private void addMapMarkers(UserLocation currentUserLocation, Meeting currentMeeting) {

        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getContext(), mGoogleMap);
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        getContext(),
                        mGoogleMap,
                        mClusterManager
                );
                mGoogleMap.setOnMarkerClickListener(mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
                mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener() {
                    @Override
                    public void onClusterItemInfoWindowClick(ClusterItem item) {
                        ClusterMarker clusterMarkerClicked = getMarkerForClusterItem(item);
                        if (clusterMarkerClicked != null) {
                            Log.d(TAG, "onClusterItemInfoWindowClick: " + clusterMarkerClicked.toString());
                            onInfoWindowPressed(clusterMarkerClicked);
                        }
                    }
                });
            }

            if (currentUserLocation != null) {

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
                                    snippet = "Show " + currentUser.getUsername() + "'s profile?";
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
                                newClusterMarker.setMeeting(false);
                                mClusterManager.addItem(newClusterMarker);
                                mClusterMarkers.add(newClusterMarker);
                            }
                            mClusterManager.cluster();
                        }
                    }
                });
                //setCameraView();
            } else if (currentMeeting != null) {
                Log.d(TAG, "ADDED MEETING: " + currentMeeting.toString());
                db.collection("users").whereEqualTo("id", currentMeeting.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult().getDocuments().get(0);
                            UserProfile currentUser = result.toObject(UserProfile.class);
                            currentMeeting.setUsername(currentUser.getUsername());
                            String snippet = "Show more info about this meeting";
                            String avatar = "meeting";
                            ClusterMarker newClusterMarker = new ClusterMarker(
                                    new LatLng(currentMeeting.getLatLng().getLatitude(), currentMeeting.getLatLng().getLongitude()),
                                    "Meeting by " + currentUser.getUsername(),
                                    snippet,
                                    avatar,
                                    currentUser
                            );
                            newClusterMarker.setMeeting(true);
                            newClusterMarker.setMeeting(currentMeeting);
                            mClusterManager.addItem(newClusterMarker);
                            mClusterMarkers.add(newClusterMarker);
                            mClusterManager.cluster();
                        }
                    }
                });
            }
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
                                addMapMarkers(currentUserLocation, null);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified userLocation: " + dc.getDocument().getData());
                                currentUserLocation = dc.getDocument().toObject(UserLocation.class);

                                for (ClusterMarker clusterMarker : mClusterMarkers) {
                                    if (clusterMarker.getUser().getId().equals(currentUserLocation.getUserId()) && !clusterMarker.isMeeting()) {
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

    private void getMeetings() {
        Query query = db.collection("Meetings");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed at getMeetings().", error);
                    return;
                }
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New Meeting: " + dc.getDocument().getData());
                                Meeting currentMeeting = dc.getDocument().toObject(Meeting.class);
                                addMapMarkers(null, currentMeeting);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "MODIFIED MEETING LOCATION");
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.d(TAG, "onInfoWindowClick: clicked!");
        if (marker.getSnippet().equals("This is you")) {
            marker.hideInfoWindow();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public ClusterMarker getMarkerForClusterItem(ClusterItem clusterItem) {
        for (ClusterMarker clusterMarker : mClusterMarkers) {
            if (clusterItem.getSnippet().equals(clusterMarker.getSnippet())) {
                return clusterMarker;
            }
        }
        return null;
    }

    public void onInfoWindowPressed(ClusterMarker marker) {
        Log.d(TAG, "onInfoWindowClick: clicked!");
        if (!marker.isMeeting()) {
            if (marker.getSnippet().equals("This is you")) {
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.dismiss();
                                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                                        .add(R.id.fragment_container, new ProfileFragment(marker.getUser().getId()), "PROFILE_FRAGMENT")
                                        .addToBackStack(null)
                                        .commit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        } else if (marker.isMeeting()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.meeting_dialog, null);
            builder.setView(view);
            TextView address = view.findViewById(R.id.addressInput);
            TextView dateAndTime = view.findViewById(R.id.date_and_time_input);
            TextView description = view.findViewById(R.id.description_input);
            TextView createdBy = view.findViewById(R.id.posted_by_input);
            Meeting currentMeeting = marker.getMeeting();
            address.setText(currentMeeting.getAddress());
            dateAndTime.setText(currentMeeting.getDateAndTime());
            description.setText(currentMeeting.getDescription());
            createdBy.setText(currentMeeting.getUsername());
            Button reminderBtn = view.findViewById(R.id.reminder_btn);
            Button cancelBtn = view.findViewById(R.id.cancel_btn);
            final AlertDialog alert = builder.create();
            alert.show();
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            reminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCalendarEvent(currentMeeting);
                }
            });
        }
    }

    private void addCalendarEvent(Meeting meeting) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, "CarLovers Meeting");
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, meeting.getCalendar().getTimeInMillis());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, meeting.getDescription());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, meeting.getAddress());
        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(TAG, "onMarkerClick: clicked!");
        Toast.makeText(getContext(), "Marker clicked!", Toast.LENGTH_SHORT).show();
        return true;
    }
}