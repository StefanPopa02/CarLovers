package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.NewCarItem;
import com.stefanpopa.carloversapp.model.SliderItem;
import com.stefanpopa.carloversapp.ui.SliderAdapterEditCar;
import com.stefanpopa.carloversapp.ui.SliderAdapterProfile;
import com.stefanpopa.carloversapp.util.UserApi;
import com.theartofdev.edmodo.cropper.CropImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EditProfileCarActivity extends AppCompatActivity {

    private ImageView closeBtn;
    private TextView editBtn;
    private ImageView addBtn;
    private ImageView removeBtn;
    private SliderView sliderView;
    private SliderAdapterEditCar sliderViewAdapter;
    private NewCarItem newCarItem;
    private List<String> currentCarPhotos;
    private List<SliderItem> sliderItems;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_car);
        db = FirebaseFirestore.getInstance();
        sliderItems = new ArrayList<>();
        closeBtn = findViewById(R.id.close);
        editBtn = findViewById(R.id.edit);
        addBtn = findViewById(R.id.activity_edit_add_btn);
        removeBtn = findViewById(R.id.activity_edit_remove_btn);
        sliderView = findViewById(R.id.imageAdded);
        sliderViewAdapter = new SliderAdapterEditCar(this);
        sliderView.setSliderAdapter(sliderViewAdapter);
        newCarItem = UserApi.getInstance().getCurrentEditCar();
        currentCarPhotos = new ArrayList<>(newCarItem.getCarPhoto());
        for (String str : currentCarPhotos) {
            sliderItems.add(new SliderItem(str));
        }
        sliderViewAdapter.renewItems(sliderItems);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderItems.size() > 0) {
                    sliderViewAdapter.deleteItem(sliderView.getCurrentPagePosition());
                    sliderItems = sliderViewAdapter.getItems();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setFixAspectRatio(true).setAspectRatio(4, 3).setMinCropResultSize(128, 96).setMaxCropResultSize(8192, 6144).start(EditProfileCarActivity.this);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editImages();
            }
        });
    }

    private void editImages() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        if (sliderItems.size() > 0) {
            List<Task<Uri>> taskArrayList = new ArrayList<>();
            boolean OK = false;
            for (String carPhoto : currentCarPhotos) {
                for (SliderItem sliderItem : sliderItems) {
                    if (sliderItem.getImageUrl() != null) {
                        if (sliderItem.getImageUrl().equals(carPhoto)) {
                            OK = true;
                        }
                    }
                }
                if (OK == false) {
                    deletePhoto(carPhoto);
                }
                OK = false;
            }

            for (SliderItem sliderItem : sliderItems) {
//                if (sliderItem.getImageUrl() != null) {
//                    if (!currentCarPhotos.contains(sliderItem.getImageUrl())) {
//                        deletePhoto(sliderItem.getImageUrl());
//                    }
//                } else
                if (sliderItem.getImageUri() != null) {
                    taskArrayList.add(uploadImageTask(sliderItem.getImageUri().toString()));
                }
            }
            Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                @Override
                public void onComplete(@NonNull Task<List<Object>> task) {
                    if (task.isSuccessful()) {
                        List<Object> resultList = task.getResult();
                        for (Object resultUri : resultList) {
                            Uri downloadUri = (Uri) resultUri;
                            addPhoto(downloadUri.toString());
                        }
                        pd.dismiss();
                        startActivity(new Intent(EditProfileCarActivity.this, WelcomeActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileCarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "You must have atleast one photo", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    private void addPhoto(String photo) {
        db.collection("UserCars").document(newCarItem.getDocId()).update("carPhoto", FieldValue.arrayUnion(photo));
    }

    private void deletePhoto(String imageUrl) {
        db.collection("UserCars").document(newCarItem.getDocId()).update("carPhoto", FieldValue.arrayRemove(imageUrl));
    }

    private Task<Uri> uploadImageTask(String imageUri) {
        StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(Uri.parse(imageUri)));
        StorageTask uploadTask = filePath.putFile(Uri.parse(imageUri));
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
            sliderItems.add(new SliderItem(result.getUri()));
            sliderViewAdapter.renewItems(sliderItems);
            //Picasso.get().load(imageUri).resize(1920, 1440).placeholder(R.drawable.no_image).into(imageAdded);
        } else {
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
        }
    }
}