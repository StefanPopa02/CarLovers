package com.stefanpopa.carloversapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.smarteist.autoimageslider.SliderView;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.ClubItem;
import com.stefanpopa.carloversapp.model.MediaObject;
import com.stefanpopa.carloversapp.model.Post;
import com.stefanpopa.carloversapp.model.UserProfile;
import com.stefanpopa.carloversapp.ui.SliderAdapterProfile;
import com.stefanpopa.carloversapp.ui.SliderAddPostAdapter;
import com.stefanpopa.carloversapp.util.FirebaseData;
import com.stefanpopa.carloversapp.util.UserApi;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private String imageUrl;
    private Uri imageUri;
    private ImageView close;
    private ImageView addVideoBtn;
    private TextView post;
    private SocialAutoCompleteTextView description;
    private FirebaseFirestore db;
    private CollectionReference mRef;
    private CollectionReference hashTagsRef;
    private SliderView sliderView;
    private SliderAddPostAdapter sliderViewAdapter;
    private List<MediaObject> media;
    private ImageView addImgBtn;
    private ImageView removeImgBtn;
    private String currentDocName;
    private int PICK_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        media = new ArrayList<>();
        currentDocName = getIntent().getStringExtra("currentPage");
        close = findViewById(R.id.close);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        db = FirebaseFirestore.getInstance();
        mRef = db.collection(currentDocName);
        hashTagsRef = db.collection("HashTags");
        sliderView = findViewById(R.id.imageAdded);
        setSliderViewAdapter();

        addVideoBtn = findViewById(R.id.activity_post_add_video_btn);
        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        addImgBtn = findViewById(R.id.activity_edit_add_btn);
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setFixAspectRatio(true).setAspectRatio(4, 3).setMinCropResultSize(128, 96).setMaxCropResultSize(8192, 6144).start(PostActivity.this);
            }
        });

        removeImgBtn = findViewById(R.id.activity_post_remove_btn);
        removeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (media.size() > 0) {
                    sliderViewAdapter.deleteItem(sliderView.getCurrentPagePosition());
                    media = sliderViewAdapter.getItems();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, WelcomeActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //Picasso.get().load(R.drawable.no_image).resize(1920, 1440).into(imageAdded);

    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);
    }

    private void setSliderViewAdapter() {
        sliderViewAdapter = new SliderAddPostAdapter(this);
        sliderView.setSliderAdapter(sliderViewAdapter);
    }

    private Task<Uri> uploadImageTask(String mediaUri) {
        String extension = getExt(Uri.parse(mediaUri));
        StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + extension);
        StorageTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
        return uploadTask.continueWithTask(task -> {
            return filePath.getDownloadUrl();
        });
    }

    private Task<Uri> uploadVideoTask(String mediaUri) {
        String extension = getExt(Uri.parse(mediaUri));
        StorageReference filePath = FirebaseStorage.getInstance().getReference("Videos").child(System.currentTimeMillis() + "." + extension);
        StorageTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
        return uploadTask.continueWithTask(task -> {
            return filePath.getDownloadUrl();
        });
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        ClubItem clubItem = FirebaseData.getInstance().getSelectedClubItem();
        UserProfile userProfile = UserApi.getInstance().getUserProfile();

        if (media.size() > 0) {
            List<Task<Uri>> imageTaskArrayList = new ArrayList<>();
            List<Task<Uri>> videoTaskArrayList = new ArrayList<>();
            for (MediaObject mediaObject : media) {
                if (mediaObject.getImgUrl() != null) {
                    imageTaskArrayList.add(uploadImageTask(mediaObject.getImgUrl()));
                } else if (mediaObject.getVideoUrl() != null) {
                    videoTaskArrayList.add(uploadVideoTask(mediaObject.getVideoUrl()));
                }
            }
            Tasks.whenAllSuccess(imageTaskArrayList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                @Override
                public void onComplete(@NonNull Task<List<Object>> task) {
                    if (task.isSuccessful()) {
                        List<String> finalPostPhotos = new ArrayList<>();
                        List<Object> resultList = task.getResult();
                        for (Object resultUri : resultList) {
                            Uri downloadUri = (Uri) resultUri;
                            finalPostPhotos.add(downloadUri.toString());
                        }
                        Post post = new Post();
                        post.setImageUrl(finalPostPhotos);
                        post.setUserProfileImgUrl(userProfile.getImageurl());
                        post.setClubId(clubItem.getId());
                        post.setNo_of_likes(0);
                        post.setClubFullname(clubItem.getFullName());
                        post.setUserId(userProfile.getId());
                        post.setUserDocId(userProfile.getDocId());
                        post.setUserFullname(userProfile.getFirstName() + " " + userProfile.getLastName());
                        post.setUsername(userProfile.getUsername());
                        post.setDescription(description.getText().toString());
                        post.setTimeAdded(new Timestamp(new Date()));

                        Tasks.whenAllSuccess(videoTaskArrayList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Object>> task) {
                                if (task.isSuccessful()) {
                                    List<String> finalPostVideos = new ArrayList<>();
                                    List<Object> resultList = task.getResult();
                                    for (Object resultUri : resultList) {
                                        Uri downloadUri = (Uri) resultUri;
                                        finalPostVideos.add(downloadUri.toString());
                                    }
                                    post.setVideosUrl(finalPostVideos);
                                    mRef.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            pd.dismiss();
                                            //Matching hashtags cu postId
                                            List<String> hashTags = description.getHashtags();
                                            if (!hashTags.isEmpty()) {
                                                String postId = documentReference.getId();
                                                HashMap<String, Object> map = new HashMap<>();
                                                for (String tag : hashTags) {
                                                    map.put("tag", tag.toLowerCase());
                                                    map.put("postId", postId);
                                                    hashTagsRef.document(tag.toLowerCase()).collection("hashTagPosts").document(postId).set(map);
                                                }
                                            }
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(PostActivity.this, "Failed to post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Post post = new Post();
            post.setImageUrl(null);
            post.setClubId(clubItem.getId());
            post.setNo_of_likes(0);
            post.setClubFullname(clubItem.getFullName());
            post.setUserProfileImgUrl(userProfile.getImageurl());
            post.setUserId(userProfile.getId());
            post.setUserDocId(userProfile.getDocId());
            post.setUserFullname(userProfile.getFirstName() + " " + userProfile.getLastName());
            post.setUsername(userProfile.getUsername());
            post.setDescription(description.getText().toString());
            post.setTimeAdded(new Timestamp(new Date()));

            mRef.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    pd.dismiss();
                    //Matching hashtags cu postId
                    List<String> hashTags = description.getHashtags();
                    if (!hashTags.isEmpty()) {
                        String postId = documentReference.getId();
                        HashMap<String, Object> map = new HashMap<>();
                        for (String tag : hashTags) {
                            map.put("tag", tag.toLowerCase());
                            map.put("postId", postId);
                            hashTagsRef.document(tag.toLowerCase()).collection("hashTagPosts").document(postId).set(map);
                        }
                    }
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(PostActivity.this, "Failed to post " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            MediaObject mediaObject = new MediaObject();
            mediaObject.setImgUrl(imageUri.toString());
            media.add(mediaObject);
            sliderViewAdapter.renewItems(media);
            //Picasso.get().load(imageUri).resize(1920, 1440).placeholder(R.drawable.no_image).into(imageAdded);
        } else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {
            assert data != null;
            Cursor returnCursor =
                    getContentResolver().query(data.getData(), null, null, null, null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            int size = (int) returnCursor.getLong(sizeIndex);
            int sizeMb = size / (1024 * 1024);
            Log.d("POST_ACTIVITY", "VIDEO ARE DIMENSIUNEA: " + sizeMb);
            if (sizeMb > 30) {
                Toast.makeText(this, "File is too large!", Toast.LENGTH_SHORT).show();
            } else {
                MediaObject mediaObject = new MediaObject();
                mediaObject.setVideoUrl(data.getData().toString());
                media.add(mediaObject);
                sliderViewAdapter.renewItems(media);
            }
        } else {
            Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, WelcomeActivity.class));
            finish();
        }
    }
}