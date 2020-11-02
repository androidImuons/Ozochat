package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("image")
    @Expose
    private String profilePic;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("otpVerify")
    @Expose
    private String otpVerify;
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("isLogin")
    @Expose
    private String isLogin;
    @SerializedName("token")
    @Expose
    private String token;


    public User(String uid, String username, String mobile, String profilePic, String otpVerify, String deviceId, String isLogin, String token) {
        this.uid = Integer.valueOf(uid);
        this.username = username;
        this.mobile = mobile;
        this.profilePic = profilePic;
        this.otpVerify = otpVerify;
        this.deviceId = deviceId;
        this.isLogin = isLogin;
        this.token = token;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public String getOtpVerify() {
        return otpVerify;
    }

    public void setOtpVerify(String otpVerify) {
        this.otpVerify = otpVerify;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
