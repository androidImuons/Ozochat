package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.StatusResponseModel;

public interface StatusListener {
    void onGetUserStatusSuccess(LiveData<StatusResponseModel> statusGetResponse);
}
