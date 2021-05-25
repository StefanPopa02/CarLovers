package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.util.CallbackMethod;
import com.stefanpopa.carloversapp.util.UserApi;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView closeBtn;
    private TextView saveBtn;
    private CircleImageView profilePic;
    private TextView changePhotoBtn;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText bio;
    private FirebaseFirestore db;
    private String profileId;
    private UserProfile userProfile;
    private Uri mImageUri;
    private String userDocId;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        db = FirebaseFirestore.getInstance();
        profileId = FirebaseAuth.getInstance().getUid();
        closeBtn = findViewById(R.id.edit_profile_close);
        saveBtn = findViewById(R.id.edit_profile_save);
        profilePic = findViewById(R.id.edit_profile_image_profile);
        changePhotoBtn = findViewById(R.id.edit_profile_change_photo);
        firstName = findViewById(R.id.edit_profile_first_name);
        lastName = findViewById(R.id.edit_profile_last_name);
        bio = findViewById(R.id.edit_profile_bio);

        getUserInfo(new CallbackMethod() {
            @Override
            public void getUserProfileData(UserProfile userProfile) {
                firstName.setText(userProfile.getFirstName());
                lastName.setText(userProfile.getLastName());
                bio.setText(userProfile.getBio());
                Picasso.get().load(userProfile.getImageurl()).placeholder(R.drawable.ic_profile).into(profilePic);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        pd.show();
        if (mImageUri != null) {
            uploadImageTask(mImageUri).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("firstName", firstName.getText().toString().trim());
                    map.put("lastName", lastName.getText().toString().trim());
                    map.put("bio", bio.getText().toString());
                    List<Task<Void>> tasksUpdate = new ArrayList<>();
                    tasksUpdate.add(db.collection("users").document(userDocId).update(map));
                    tasksUpdate.add(db.collection("users").document(userDocId).update("imageurl", downloadUri.toString()));
                    Tasks.whenAllSuccess(tasksUpdate).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Object>> task) {
                            if (task.isSuccessful()) {
                                getUserInfo(new CallbackMethod() {
                                    @Override
                                    public void getUserProfileData(UserProfile userProfile) {
                                        firstName.setText(userProfile.getFirstName());
                                        lastName.setText(userProfile.getLastName());
                                        bio.setText(userProfile.getBio());
                                        Picasso.get().load(userProfile.getImageurl()).placeholder(R.drawable.ic_profile).into(profilePic);
                                        pd.dismiss();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("firstName", firstName.getText().toString());
            map.put("lastName", lastName.getText().toString());
            map.put("bio", bio.getText().toString());
            db.collection("users").document(userDocId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        getUserInfo(new CallbackMethod() {
                            @Override
                            public void getUserProfileData(UserProfile userProfile) {
                                firstName.setText(userProfile.getFirstName());
                                lastName.setText(userProfile.getLastName());
                                bio.setText(userProfile.getBio());
                                Picasso.get().load(userProfile.getImageurl()).placeholder(R.drawable.ic_profile).into(profilePic);
                                pd.dismiss();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
    }

    private void getUserInfo(CallbackMethod callbackMethod) {
        db.collection("users").whereEqualTo("id", profileId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documentsResult = task.getResult().getDocuments();
                    DocumentSnapshot doc = documentsResult.get(0);
                    userDocId = doc.getId();
                    userProfile = doc.toObject(UserProfile.class);
                    userProfile.setDocId(userDocId);
                    UserApi.getInstance().setUserProfile(userProfile);
                    Log.d("PROFILE_FRAGMENT", userProfile.toString());
                    callbackMethod.getUserProfileData(userProfile);
                } else {
                    Toast.makeText(EditProfileActivity.this, "No user matching UID", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            Picasso.get().load(mImageUri).placeholder(R.drawable.ic_profile).into(profilePic);
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private Task<Uri> uploadImageTask(Uri imageUri) {
        StorageReference filePath = FirebaseStorage.getInstance().getReference("UserProfilePhotos").child(System.currentTimeMillis() + ".jpeg");
        StorageTask uploadTask = filePath.putFile(imageUri);
        return uploadTask.continueWithTask(task -> {
            return filePath.getDownloadUrl();
        });
    }
}