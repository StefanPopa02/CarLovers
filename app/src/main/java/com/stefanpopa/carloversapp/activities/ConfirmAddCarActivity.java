package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.SliderItem;
import com.stefanpopa.carloversapp.ui.SliderAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private TextView caracteristici_view;
    private ImageView carPhoto_view;
    private Button back_btn;
    private Button close_btn;
    private Button confirm_btn;
    private Uri imageUri;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private ProgressDialog pd;
    private SliderView sliderView;
    private Button addImgBtn;
    private Button removeImgBtn;
    private List<SliderItem> sliderItems;
    private SliderAdapter sliderAdapter;
    private List<SliderItem> defaultSliderItem;
    private List<String> finalCarPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_add_car);
        sliderItems = new ArrayList<>();
        finalCarPhotos = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("UserCars");
        back_btn = findViewById(R.id.back_to_add_car_btn);
        close_btn = findViewById(R.id.close_confirm_activity_btn);
        confirm_btn = findViewById(R.id.confirm_add_car_btn);
        addImgBtn = findViewById(R.id.add_image_btn);
        removeImgBtn = findViewById(R.id.remove_image_btn);

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setFixAspectRatio(true).setAspectRatio(4,3).setMinCropResultSize(128, 96).setMaxCropResultSize(8192, 6144).start(ConfirmAddCarActivity.this);
            }
        });
        removeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderItems.size() > 1) {
                    sliderAdapter.deleteItem(sliderView.getCurrentPagePosition());
                    sliderItems = sliderAdapter.getItems();
                } else if (sliderItems.size() == 1) {
                    sliderItems = new ArrayList<>();
                    sliderAdapter.renewItems(defaultSliderItem);
                }
            }

        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmAddCarActivity.this, AddCarActivity.class));
                finish();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmAddCarActivity.this, WelcomeActivity.class));
                finish();
            }
        });

        letter = getIntent().getStringExtra("Letter");
        brand = getIntent().getStringExtra("Brand");
        model = getIntent().getStringExtra("Model");
        year = getIntent().getStringExtra("Year");
        version = getIntent().getStringExtra("Version");
        engine = getIntent().getStringExtra("Engine");
        caracteristici = getIntent().getStringExtra("Caracteristici");
        carPhoto = getIntent().getStringExtra("CarPhoto");
        finalCarPhotos.add(carPhoto);
        brandLogo = getIntent().getStringExtra("BrandLogo");
        NewCarItem newCarItem = new NewCarItem(letter, brand, model, year, version, engine, caracteristici, finalCarPhotos, brandLogo);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUserData(newCarItem);
            }
        });

        Log.d("CONFIRM_ADD_CAR_ACTIVITY", "New car Item: " + newCarItem.toString());
        brand_view = findViewById(R.id.text_view_brand);
        model_view = findViewById(R.id.text_view_model);
        year_view = findViewById(R.id.text_view_year);
        version_view = findViewById(R.id.text_view_version);
        engine_view = findViewById(R.id.text_view_engine);
        caracteristici_view = findViewById(R.id.text_view_caracteristici);

        brand_view.setText(brand);
        model_view.setText(model);
        year_view.setText(year);
        version_view.setText(version);
        engine_view.setText(engine);
        caracteristici_view.setText(caracteristici);

        sliderView = findViewById(R.id.imageSlider);
        sliderAdapter = new SliderAdapter(this);
        SliderItem sliderItem = new SliderItem();
        sliderItem.setImageUrl(carPhoto);
        defaultSliderItem = new ArrayList<>();
        defaultSliderItem.add(sliderItem);
        sliderAdapter.renewItems(defaultSliderItem);
        sliderView.setSliderAdapter(sliderAdapter);
    }

    private void uploadUserData(NewCarItem newCarItem) {
        pd.show();
        if (sliderItems.size() > 0) {
            List<Task<Uri>> taskArrayList = new ArrayList<>();
            for (SliderItem sliderItem : sliderItems) {
                taskArrayList.add(uploadImageTask(sliderItem.getImageUri()));
            }
            Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                @Override
                public void onComplete(@NonNull Task<List<Object>> task) {
                    if (task.isSuccessful()) {
                        finalCarPhotos = new ArrayList<>();
                        List<Object> resultList = task.getResult();
                        for (Object resultUri : resultList) {
                            Uri downloadUri = (Uri) resultUri;
                            finalCarPhotos.add(downloadUri.toString());
                            Log.d("CONFIRM_ADD_CAR", "DownUri: " + downloadUri);
                        }
                        newCarItem.setCarPhoto(finalCarPhotos);
                        newCarItem.setUserId(FirebaseAuth.getInstance().getUid());
                        newCarItem.setTimeAdded(new Timestamp(new Date()));
                        mRef.add(newCarItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                pd.dismiss();
                                startActivity(new Intent(ConfirmAddCarActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(ConfirmAddCarActivity.this, "Failed to post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ConfirmAddCarActivity.this, "Failed to post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            newCarItem.setUserId(FirebaseAuth.getInstance().getUid());
            newCarItem.setTimeAdded(new Timestamp(new Date()));

            mRef.add(newCarItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    pd.dismiss();
                    startActivity(new Intent(ConfirmAddCarActivity.this, WelcomeActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ConfirmAddCarActivity.this, "Failed to post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Task<Uri> uploadImageTask(Uri imageUri) {
        StorageReference filePath = FirebaseStorage.getInstance().getReference("UserCars").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        StorageTask uploadTask = filePath.putFile(imageUri);
        return uploadTask.continueWithTask(task -> {
            return filePath.getDownloadUrl();
        });
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            sliderItems.add(0, new SliderItem(imageUri));
            sliderAdapter.renewItems(sliderItems);

        } else {
            Toast.makeText(this, "Something bad happened, Try Again!", Toast.LENGTH_SHORT).show();
        }
    }
}