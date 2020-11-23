package com.ozonetech.ozochat.viewmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupInfoResponse {
    @SerializedName("admin_id")
    @Expose
    private Integer adminId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
