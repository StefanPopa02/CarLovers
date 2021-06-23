package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.ClubsFragment;
import com.stefanpopa.carloversapp.fragments.MapsFragment;
import com.stefanpopa.carloversapp.fragments.MeetingsFragment;
import com.stefanpopa.carloversapp.fragments.NewsFragment;
import com.stefanpopa.carloversapp.fragments.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

import static com.stefanpopa.carloversapp.util.Constants.ERROR_DIALOG_REQUEST;
import static com.stefanpopa.carloversapp.util.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.stefanpopa.carloversapp.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class WelcomeActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private String TAG;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Map<String, Float> fragmentsOrder = new HashMap<>();
        fragmentsOrder.put("NEWS_FRAGMENT", 1.0f);
        fragmentsOrder.put("CLUBS_FRAGMENT", 2.0f);
        fragmentsOrder.put("CLUBS_FEED_FRAGMENT", 2.1f);
        fragmentsOrder.put("CLUBS_PAGE_FRAGMENT", 2.2f);
        fragmentsOrder.put("CLUBS_POST_DETAIL_FRAGMENT", 2.3f);
        fragmentsOrder.put("MEETINGS_FRAGMENT", 3.0f);
        fragmentsOrder.put("MAPS_FRAGMENT", 3.1f);
        fragmentsOrder.put("PROFILE_FRAGMENT", 4.0f);
        fragmentsOrder.put("PROFILE_FOLLOWING_FRAGMENT", 4.1f);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.news_home:
                        selectorFragment = new NewsFragment();
                        TAG = "NEWS_FRAGMENT";
                        break;
                    case R.id.clubs_home:
                        selectorFragment = new ClubsFragment();
                        TAG = "CLUBS_FRAGMENT";
                        break;
                    case R.id.meetings_home:
                        selectorFragment = new MapsFragment();
                        TAG = "MEETINGS_FRAGMENT";
                        break;
                    case R.id.profile_home:
                        selectorFragment = new ProfileFragment();
                        TAG = "PROFILE_FRAGMENT";
                        break;
                }


                if (selectorFragment != null) {
                    try {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        if (fragmentsOrder.get(currentFragment.getTag()) > fragmentsOrder.get(TAG)) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_left).addToBackStack(null).replace(R.id.fragment_container, selectorFragment, TAG).commit();
                        } else if (fragmentsOrder.get(currentFragment.getTag()) < fragmentsOrder.get(TAG)) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right).addToBackStack(null).replace(R.id.fragment_container, selectorFragment, TAG).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, selectorFragment, TAG).commit();
                        }
                    } catch (NullPointerException e) {
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, selectorFragment, TAG).commit();
                    }
                }

                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new NewsFragment(), "NEWS_FRAGMENT").commit();

    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK() {
        int playServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(WelcomeActivity.this);

        if (playServicesAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(playServicesAvailable)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(WelcomeActivity.this, playServicesAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {

                } else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        bottomNavigationView.setSelectedItemId(currentFragment.getId());
        Log.d("WELCOME_ACTIVITY", "OnResume called: " + currentFragment.getTag());
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {

            } else {
                getLocationPermission();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
            //finish();
        }
    }
}