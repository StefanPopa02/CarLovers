package com.stefanpopa.carloversapp.model;

import android.net.Uri;
import android.transition.Slide;

public class SliderItem {
    private Uri imageUri;
    private String imageUrl;

    public SliderItem() {
    }

    public SliderItem(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public SliderItem(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
