package com.stefanpopa.carloversapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.NewCarItem;

public class ConfirmAddCarActivity extends AppCompatActivity {

    private String letter;
    private String brand;
    private String model;
    private String year;
    private String version;
    private String engine;
    private String caracteristici;
    private String carPhoto;
    private String brandLogo;
    private TextView brand_view;
    private TextView model_view;
    private TextView year_view;
    private TextView version_view;
    private TextView engine_view;
    private ImageView carPhoto_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_add_car);

        letter = getIntent().getStringExtra("Letter");
        brand = getIntent().getStringExtra("Brand");
        model = getIntent().getStringExtra("Model");
        year = getIntent().getStringExtra("Year");
        version = getIntent().getStringExtra("Version");
        engine = getIntent().getStringExtra("Engine");
        caracteristici = getIntent().getStringExtra("Caracteristici");
        carPhoto = getIntent().getStringExtra("CarPhoto");
        brandLogo = getIntent().getStringExtra("BrandLogo");
        NewCarItem newCarItem = new NewCarItem(letter, brand, model, year, version, engine, caracteristici, carPhoto, brandLogo);
        Log.d("CONFIRM_ADD_CAR_ACTIVITY", "New car Item: " + newCarItem.toString());
        brand_view = findViewById(R.id.text_view_brand);
        model_view = findViewById(R.id.text_view_model);
        year_view = findViewById(R.id.text_view_year);
        version_view = findViewById(R.id.text_view_version);
        engine_view = findViewById(R.id.text_view_engine);
        carPhoto_view = findViewById(R.id.car_image_confirm);
        //TODO: set Text to binding textviews + UI caracteristici + add personal car image
    }
}