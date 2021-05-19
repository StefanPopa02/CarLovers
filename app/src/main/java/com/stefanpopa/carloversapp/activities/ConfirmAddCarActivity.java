package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.Post;
import com.theartofdev.edmodo.cropper.CropImage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_add_car);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("UserCars");
        back_btn = findViewById(R.id.back_to_add_car_btn);
        close_btn = findViewById(R.id.close_confirm_activity_btn);
        confirm_btn = findViewById(R.id.confirm_add_car_btn);
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
        brandLogo = getIntent().getStringExtra("BrandLogo");
        NewCarItem newCarItem = new NewCarItem(letter, brand, model, year, version, engine, caracteristici, carPhoto, brandLogo);
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
        carPhoto_view = findViewById(R.id.car_image_confirm);
        caracteristici_view = findViewById(R.id.text_view_caracteristici);

        brand_view.setText(brand);
        model_view.setText(model);
        year_view.setText(year);
        version_view.setText(version);
        engine_view.setText(engine);
        caracteristici_view.setText(caracteristici);
        Picasso.get().load(carPhoto).resize(500, 375).into(carPhoto_view);
        carPhoto_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(ConfirmAddCarActivity.this);
            }
        });

    }

    private void uploadUserData(NewCarItem newCarItem) {
        pd.show();
        if (imageUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference("UserCars").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    newCarItem.setCarPhoto(downloadUri.toString());
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(ConfirmAddCarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            carPhoto_view.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Something bad happened, Try Again!", Toast.LENGTH_SHORT).show();
        }
    }
}