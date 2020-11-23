package com.ozonetech.ozochat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.SelectContactRepository;

import org.json.JSONArray;

import java.util.Map;

public class UserChatViewModel extends ViewModel {

    public LiveData<CreateGRoupREsponse> commonResponse;
    public CommonResponseInterface callback;
    public CreateGroupInterface groupInterface;

    public LiveData<CommonResponse> leftGroupResponse;
    public LiveData<GroupDetailModel> groupDetailResponse;

    public void createGroup(Context context, JsonArray arrayListAge) {

        if (commonResponse == null) {
            commonResponse = new MutableLiveData<CreateGRoupREsponse>();
            //we will load it asynchronously from server in this method
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge,context);
            groupInterface.onSuccessCreateGroup(commonResponse);
        }else{
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge,context);
            groupInterface.onSuccessCreateGroup(commonResponse);
        }
    }


    public void leftGroup(Context context, JsonArray jsonArray) {

        if (leftGroupResponse == null) {
            leftGroupResponse = new MutableLiveData<CommonResponse>();
            //we will load it asynchronously from server in this method
            leftGroupResponse = new CreateGroupRepository().leftGroup(jsonArray,context);
            groupInterface.onSuccessLeftGroup(leftGroupResponse);
        }else{
            leftGroupResponse = new CreateGroupRepository().leftGroup(jsonArray,context);
            groupInterface.onSuccessLeftGroup(leftGroupResponse);
        }
    }


    public void getGroupDetails(Context context, Map<String,String> groupMap,CreateGroupInterface groupInterface) {

        if (groupDetailResponse == null) {
            groupDetailResponse = new MutableLiveData<GroupDetailModel>();
            //we will load it asynchronously from server in this method
            groupDetailResponse = new CreateGroupRepository().getGroupDetails(groupMap,context);
            groupInterface.onSuccessGroupDetails(groupDetailResponse);
        }else{
            groupDetailResponse = new CreateGroupRepository().getGroupDetails(groupMap,context);
            groupInterface.onSuccessGroupDetails(groupDetailResponse);
        }
    }


}
