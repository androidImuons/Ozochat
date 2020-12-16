package com.ozonetech.ozochat.model;

import android.net.Uri;

import java.net.URI;

public class ImgCaptionModel {
    private int position;
    private String caption;
    private Uri imagePath;

    public ImgCaptionModel(int position, String caption, String  imgPath) {
        this.position = position;
        this.caption = caption;
        //String myUrl = "http://stackoverflow.com";
        //URI myURI = new URI(imgPath);
        this.imagePath = Uri.parse(imgPath);
    }

    public int getPosition() {
        return position;
    }

    public String getCaption() {
        return caption;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

}
