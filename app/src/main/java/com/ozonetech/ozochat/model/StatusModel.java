package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusModel {

    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("bg_color")
    @Expose
    private String bgColor;
    @SerializedName("status_privacy")
    @Expose
    private String statusPrivacy;
    @SerializedName("uploadedFileUrl")
    @Expose
    private String uploadedFileUrl;

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getStatusPrivacy() {
        return statusPrivacy;
    }

    public void setStatusPrivacy(String statusPrivacy) {
        this.statusPrivacy = statusPrivacy;
    }

    public String getUploadedFileUrl() {
        return uploadedFileUrl;
    }

    public void setUploadedFileUrl(String uploadedFileUrl) {
        this.uploadedFileUrl = uploadedFileUrl;
    }

}
