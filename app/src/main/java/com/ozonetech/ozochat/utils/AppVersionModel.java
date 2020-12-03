package com.ozonetech.ozochat.utils;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppVersionModel extends AppBaseModel {

    String version_name;
    long version_code;
    String version_desc;
    String update_type="F";
    String title;
    String app_link;

    @SerializedName("success")
    @Expose
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message;

    public String getVersion_name() {
        return getValidString(version_name);
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public long getVersion_code() {
        return version_code;
    }

    public void setVersion_code(long version_code) {
        this.version_code = version_code;
    }

    public String getVersion_desc() {
        return getValidString(version_desc);
    }

    public void setVersion_desc(String version_desc) {
        this.version_desc = version_desc;
    }

    public String getUpdate_type() {
        return getValidString(update_type);
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }

    public boolean isForceUpdate() {
        return getUpdate_type().equals("F");
    }

    public String getTitle() {
        return getValidString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApp_link() {
        return getValidString(app_link);
    }

    public void setApp_link(String app_link) {
        this.app_link = app_link;
    }
}
