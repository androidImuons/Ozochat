package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;

public interface CreateGroupInterface {
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse);

    void onSuccessLeftGroup(LiveData<CommonResponse> leftGroupResponse);
}
