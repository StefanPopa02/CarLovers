package com.stefanpopa.carloversapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stefanpopa.carloversapp.R;

public class ConfirmAddCarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_add_car);
//        i.putExtra("Letter",letterFinal);
//        i.putExtra("Brand", clickedBrandNameFinal);
//        i.putExtra("Model", clickedModelNameFinal);
//        i.putExtra("Year", clickedYearNameFinal);
//        i.putExtra("Version", clickedVersionNameFinal);
//        i.putExtra("Engine", engineFinal);
//        i.putExtra("Caracteristici", caracteristiciFinal);
//        i.putExtra("CarPhoto", carImageUrlFinal);
//        i.putExtra("BrandLogo", carLogoUrlFinal);
        savedInstanceState.getString("");
    }
}