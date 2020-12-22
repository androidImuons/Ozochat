package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.StatusResponseModel;
import com.ozonetech.ozochat.model.UserStatusResponseModel;

public interface StatusListener {
    void onGetUserStatusSuccess(LiveData<StatusResponseModel> statusGetResponse);

    void onSetUserStatusSuccess(LiveData<UserStatusResponseModel> userStatusResponse);
}
