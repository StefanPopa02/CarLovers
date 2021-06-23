package com.stefanpopa.carloversapp.model;

import android.provider.MediaStore;

public class MediaObject {

    private String imgUrl;
    private String videoUrl;

    public MediaObject() {

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "MediaObject{" +
                "imgUrl='" + imgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
