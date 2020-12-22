package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusImgModel {
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("uploadedFileUrl")
    @Expose
    private String uploadedFileUrl;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploadedFileUrl() {
        return uploadedFileUrl;
    }

    public void setUploadedFileUrl(String uploadedFileUrl) {
        this.uploadedFileUrl = uploadedFileUrl;
    }
}
