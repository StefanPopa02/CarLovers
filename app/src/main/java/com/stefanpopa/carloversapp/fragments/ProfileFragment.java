package com.stefanpopa.carloversapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.activities.AddCarActivity;
import com.stefanpopa.carloversapp.activities.FollowingClubsActivity;
import com.stefanpopa.carloversapp.activities.MainActivity;
import com.stefanpopa.carloversapp.activities.OptionsActivity;
import com.stefanpopa.carloversapp.activities.WelcomeActivity;
import com.stefanpopa.carloversapp.model.BrandItem;
import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.BrandAdapter;
import com.stefanpopa.carloversapp.ui.SliderAdapterProfile;
import com.stefanpopa.carloversapp.util.CallbackMethod;
import com.stefanpopa.carloversapp.util.UserApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button addCarBtn;
    private TextView profileUsername;
    private ImageView profileOptions;
    private CircleImageView profileImage;
    private TextView profileFullName;
    private TextView profileBiography;
    private Button profileFollowingBtn;
    private Spinner profileSpinnerCars;
    private SliderView profileCarImageSlider;
    private TextView profileCarBrand;
    private TextView profileCarModel;
    private TextView profileCarYear;
    private TextView profileCarVersion;
    private TextView profileCarEngine;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private String profileId;
    private UserProfile userProfile;
    private List<NewCarItem> userCars;
    private SliderAdapterProfile sliderAdapterProfile;
    private boolean guest = false;


    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(String profileId) {
        this.profileId = profileId;
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(profileId)) {
            guest = false;
        } else {
            guest = true;
        }

    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((WelcomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(3).setChecked(true);
        Log.d("PROFILE_FRAGMENT", "onResume called: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //TODO: Check if profileId not already set for another user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (profileId == null) {
            profileId = firebaseUser.getUid();
        }

        addCarBtn = view.findViewById(R.id.profile_add_car_btn);
        if (guest) {
            addCarBtn.setVisibility(View.GONE);
        }
        addCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddCarActivity.class));
                getActivity().finish();
            }
        });
        profileUsername = view.findViewById(R.id.club_page_fullname);
        profileOptions = view.findViewById(R.id.profile_options);
        profileImage = view.findViewById(R.id.profile_image);
        profileFullName = view.findViewById(R.id.profile_fullname);
        profileBiography = view.findViewById(R.id.profile_biography);
        profileFollowingBtn = view.findViewById(R.id.profile_following_clubs_btn);
        profileFollowingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, new ProfileFollowingFragment(userProfile), "PROFILE_FOLLOWING_FRAGMENT").addToBackStack(null).commit();
            }
        });
        profileSpinnerCars = view.findViewById(R.id.profile_spinner_owned_cars);
        profileCarImageSlider = view.findViewById(R.id.profile_car_image_slider);
        profileCarBrand = view.findViewById(R.id.profile_text_view_brand);
        profileCarModel = view.findViewById(R.id.profile_text_view_model);
        profileCarYear = view.findViewById(R.id.profile_text_view_year);
        profileCarVersion = view.findViewById(R.id.profile_text_view_version);
        profileCarEngine = view.findViewById(R.id.profile_text_view_engine);

        db = FirebaseFirestore.getInstance();

        getUserInfo();
        getUserCars(new CallbackMethod() {
            @Override
            public void getResultData(List<NewCarItem> userCars) {
                //Log.d("PROFILE_FRAGMENT", "AM PRIMIT ASYNC: "+userCars.toString());
                if (userCars.size() > 0) {
                    setSpinnerCarsAdapter(userCars);
                }
            }
        });

        profileOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });


        return view;
    }

    private void setSpinnerCarsAdapter(List<NewCarItem> userCars) {

        List<BrandItem> brandItems = new ArrayList<>();
        Map<BrandItem, NewCarItem> carItemMap = new HashMap<>();
        for (NewCarItem carItem : userCars) {
            BrandItem brandItem = new BrandItem(carItem.getBrand() + " " + carItem.getModel(), carItem.getBrandLogo());
            brandItems.add(brandItem);
            carItemMap.put(brandItem, carItem);
        }

        BrandAdapter carsSpinnerAdapter = new BrandAdapter(getContext(), (ArrayList<BrandItem>) brandItems);
        profileSpinnerCars.setAdapter(carsSpinnerAdapter);
        profileSpinnerCars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrandItem brandItem = (BrandItem) parent.getItemAtPosition(position);
                NewCarItem carInfo = carItemMap.get(brandItem);
                List<String> carPhotos = carInfo.getCarPhoto();
                setSliderCarsAdapter(carPhotos);
                setCarInfo(carInfo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setCarInfo(NewCarItem carInfo) {
        profileCarBrand.setText(carInfo.getBrand());
        profileCarModel.setText(carInfo.getModel());
        profileCarVersion.setText(carInfo.getVersion());
        profileCarYear.setText(carInfo.getYear());
        profileCarEngine.setText(carInfo.getEngine());
    }

    private void setSliderCarsAdapter(List<String> carPhotos) {
        sliderAdapterProfile = new SliderAdapterProfile(getContext());
        sliderAdapterProfile.renewItems(carPhotos);
        profileCarImageSlider.setSliderAdapter(sliderAdapterProfile);
    }

    private void getUserCars(CallbackMethod callbackMethod) {
        db.collection("UserCars").whereEqualTo("userId", profileId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    userCars = new ArrayList<>();
                    List<DocumentSnapshot> documentsResult = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : documentsResult) {
                        userCars.add(doc.toObject(NewCarItem.class));
                    }
                    for (NewCarItem carItem : userCars) {
                        Collections.reverse(carItem.getCarPhoto());
                        //Log.d("PROFILE_FRAGMENT", carItem.toString());
                    }
                    callbackMethod.getResultData(userCars);
                } else {
                    Toast.makeText(getContext(), "No user matching UID ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo() {
        db.collection("users").whereEqualTo("id", profileId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentsResult = task.getResult().getDocuments();
                    DocumentSnapshot doc = documentsResult.get(0);
                    userProfile = doc.toObject(UserProfile.class);
                    userProfile.setDocId(doc.getId());
                    if (profileId.equals(firebaseUser.getUid())) {
                        UserApi.getInstance().setUserProfile(userProfile);
                    }
                    Log.d("PROFILE_FRAGMENT", userProfile.toString());
                    profileUsername.setText(userProfile.getUsername());
                    profileBiography.setText(userProfile.getBio());
                    profileFullName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
                    Picasso.get().load(userProfile.getImageurl()).placeholder(R.drawable.ic_profile).into(profileImage);
                } else {
                    Toast.makeText(getContext(), "No user matching UID", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}