package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;

public interface CreateGroupInterface {
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse);

    void onSuccessLeftGroup(LiveData<LeftResponseModel> leftGroupResponse);

    void onSuccessGroupDetails(LiveData<GroupDetailModel> groupDetailResponase);

    void onSuccessAddToGroup(LiveData<AddMemberResponseModel> addMemberResponse);

    void onSuccessRemoveMember(LiveData<LeftResponseModel> removeMemberResponse);

    void onSuccessDeleteGroup(LiveData<LeftResponseModel> deleteGroupResponse);
}
