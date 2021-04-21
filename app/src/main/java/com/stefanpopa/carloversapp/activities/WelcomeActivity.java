package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.fragments.ClubsFragment;
import com.stefanpopa.carloversapp.fragments.MeetingsFragment;
import com.stefanpopa.carloversapp.fragments.NewsFragment;
import com.stefanpopa.carloversapp.fragments.ProfileFragment;

public class WelcomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.news_home:
                        selectorFragment = new NewsFragment();
                        break;
                    case R.id.clubs_home:
                        selectorFragment = new ClubsFragment();
                        break;
                    case R.id.meetings_home:
                        selectorFragment = new MeetingsFragment();
                        break;
                    case R.id.profile_home:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewsFragment()).commit();
    }
}