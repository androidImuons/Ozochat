package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.UploadFilesResponse;
import com.ozonetech.ozochat.model.UploadResponse;

public interface UploadFilsListner {
    public void onSuccessResponse(LiveData<UploadFilesResponse> uploadGroupImgResponse);
}
