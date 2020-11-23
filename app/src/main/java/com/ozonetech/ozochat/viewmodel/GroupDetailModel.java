package com.ozonetech.ozochat.viewmodel;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.listeners.UserRecentChatListener;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.RecentChatList;

import java.util.List;
import java.util.Map;

public class GroupDetailModel extends ViewModel{
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<GroupInfoResponse> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<GroupInfoResponse> getData() {
        return data;
    }

    public void setData(List<GroupInfoResponse> data) {
        this.data = data;
    }

}