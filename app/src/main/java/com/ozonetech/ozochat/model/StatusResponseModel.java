package com.ozonetech.ozochat.model;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.listeners.StatusListener;
import com.ozonetech.ozochat.listeners.UserRecentChatListener;
import com.ozonetech.ozochat.repository.RecentChatList;
import com.ozonetech.ozochat.repository.StatusRepository;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;

import java.util.List;
import java.util.Map;

public class StatusResponseModel extends ViewModel {
    public LiveData<StatusResponseModel> statusGetResponse;
    public StatusListener statusListener;

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<StatusModel> data = null;

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

    public List<StatusModel> getData() {
        return data;
    }

    public void setData(List<StatusModel> data) {
        this.data = data;
    }

    public void getUserStatus(Context context, StatusListener statusListener, Map<String, String> statusMap) {

        if (statusGetResponse == null) {
            statusGetResponse = new MutableLiveData<StatusResponseModel>();
            statusGetResponse = new StatusRepository().getUserStatus(context,statusMap);
            statusListener.onGetUserStatusSuccess(statusGetResponse);
        } else {
            statusGetResponse = new StatusRepository().getUserStatus(context,statusMap);
            statusListener.onGetUserStatusSuccess(statusGetResponse);
        }
    }


}
