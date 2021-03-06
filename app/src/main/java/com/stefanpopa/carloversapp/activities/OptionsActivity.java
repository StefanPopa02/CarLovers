package com.stefanpopa.carloversapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.stefanpopa.carloversapp.R;

import org.w3c.dom.Text;

public class OptionsActivity extends AppCompatActivity {

    private TextView editProfile;
    private TextView signOut;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        editProfile = findViewById(R.id.options_edit_profile);
        signOut = findViewById(R.id.options_logout);

        Toolbar toolbar = findViewById(R.id.options_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, EditProfileActivity.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });


    }
}