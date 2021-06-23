package com.stefanpopa.carloversapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.PostActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;

public class MeetingsFragment extends Fragment {

    private Button addPostBtn;

    public MeetingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);
        Log.d("PROFILE_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View infl = inflater.inflate(R.layout.fragment_meetings, container, false);
        addPostBtn = infl.findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
                getActivity().finish();
            }
        });
        return infl;

    }
}