package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.CommonResponse;


public interface CommonResponseInterface {
    void onCommoStarted();
    void onCommonSuccess(LiveData<CommonResponse> userProfileResponse);
    void onCommonFailure(String message);
}
