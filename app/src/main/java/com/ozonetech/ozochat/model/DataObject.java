package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataObject {
    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("group_id")
    @Expose
    private String group_id;

    @SerializedName("uploadedFileUrl")
    @Expose
    private String uploadedFileUrl;

    @SerializedName("mimetype")
    @Expose
    private String mimetype;


    public Integer getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Integer msg_id) {
        this.msg_id = msg_id;
    }

    @SerializedName("msg_id")
    @Expose
    private Integer msg_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUploadedFileUrl() {
        return uploadedFileUrl;
    }

    public void setUploadedFileUrl(String uploadedFileUrl) {
        this.uploadedFileUrl = uploadedFileUrl;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
