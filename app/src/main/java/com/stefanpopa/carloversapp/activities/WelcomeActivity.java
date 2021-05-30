package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.ClubsFragment;
import com.stefanpopa.carloversapp.fragments.MeetingsFragment;
import com.stefanpopa.carloversapp.fragments.NewsFragment;
import com.stefanpopa.carloversapp.fragments.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Map<String, Float> fragmentsOrder = new HashMap<>();
        fragmentsOrder.put("NEWS_FRAGMENT", 1.0f);
        fragmentsOrder.put("CLUBS_FRAGMENT", 2.0f);
        fragmentsOrder.put("CLUBS_PAGE_FRAGMENT", 2.1f);
        fragmentsOrder.put("CLUBS_POST_DETAIL_FRAGMENT", 2.2f);
        fragmentsOrder.put("MEETINGS_FRAGMENT", 3.0f);
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
                        selectorFragment = new MeetingsFragment();
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
                    }catch (NullPointerException e){
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, selectorFragment, TAG).commit();
                    }
                }

                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new NewsFragment(), "NEWS_FRAGMENT").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        bottomNavigationView.setSelectedItemId(currentFragment.getId());
        Log.d("WELCOME_ACTIVITY", "OnResume called: " + currentFragment.getTag());
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