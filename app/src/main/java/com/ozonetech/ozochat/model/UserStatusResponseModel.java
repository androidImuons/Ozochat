package com.ozonetech.ozochat.model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.listeners.StatusListener;
import com.ozonetech.ozochat.repository.StatusRepository;
import com.ozonetech.ozochat.view.activity.StatusEditActivity;

import java.util.List;

import okhttp3.RequestBody;

public class UserStatusResponseModel extends ViewModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("imgData")
    @Expose
    private List<StatusImgModel> imgData = null;
    @SerializedName("txtData")
    @Expose
    private List<StatusTextModel> txtData = null;
    public LiveData<UserStatusResponseModel> userStatusResponse;

    public StatusListener statusListener;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StatusImgModel> getImgData() {
        return imgData;
    }

    public void setImgData(List<StatusImgModel> imgData) {
        this.imgData = imgData;
    }

    public List<StatusTextModel> getTxtData() {
        return txtData;
    }

    public void setTxtData(List<StatusTextModel> txtData) {
        this.txtData = txtData;
    }

    public void setUserStatus(Context context, RequestBody requestBody, StatusListener statusListener) {

        if(userStatusResponse == null){
            userStatusResponse = new MutableLiveData<UserStatusResponseModel>();
            userStatusResponse =new StatusRepository().setUserStatus(context,requestBody);
            statusListener.onSetUserStatusSuccess(userStatusResponse);
        }else {
            userStatusResponse =new StatusRepository().setUserStatus(context,requestBody);
            statusListener.onSetUserStatusSuccess(userStatusResponse);
        }
    }
}
