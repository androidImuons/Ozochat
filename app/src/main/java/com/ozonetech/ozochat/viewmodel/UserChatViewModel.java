package com.ozonetech.ozochat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.SelectContactRepository;
import com.ozonetech.ozochat.view.activity.DetailViewUpdateActivity;

import org.json.JSONArray;

import java.util.Map;

public class UserChatViewModel extends ViewModel {

    public LiveData<CreateGRoupREsponse> commonResponse;
    public CommonResponseInterface callback;
    public CreateGroupInterface groupInterface;

    public LiveData<LeftResponseModel> leftGroupResponse;
    public LiveData<LeftResponseModel> removeMemberResponse;
    public LiveData<AddMemberResponseModel> addMemberResponse;
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


    public void leftGroup(Context context, JsonArray jsonArray,CreateGroupInterface groupInterface) {

        if (leftGroupResponse == null) {
            leftGroupResponse = new MutableLiveData<LeftResponseModel>();
            //we will load it asynchronously from server in this method
            leftGroupResponse = new CreateGroupRepository().leftGroup(jsonArray,context);
            groupInterface.onSuccessLeftGroup(leftGroupResponse);
        }else{
            leftGroupResponse = new CreateGroupRepository().leftGroup(jsonArray,context);
            groupInterface.onSuccessLeftGroup(leftGroupResponse);
        }
    }


    //addMemberToGroup
    public void addMemberToGroup(Context context, JsonArray jsonArray,CreateGroupInterface groupInterface) {

        if (addMemberResponse == null) {
            addMemberResponse = new MutableLiveData<AddMemberResponseModel>();
            //we will load it asynchronously from server in this method
            addMemberResponse = new CreateGroupRepository().addMemberToGroup(jsonArray,context);
            groupInterface.onSuccessAddToGroup(addMemberResponse);
        }else{
            addMemberResponse = new CreateGroupRepository().addMemberToGroup(jsonArray,context);
            groupInterface.onSuccessAddToGroup(addMemberResponse);
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


    public void removeMember(Context context, JsonArray jsonArray, CreateGroupInterface groupInterface) {
        if (removeMemberResponse == null) {
            removeMemberResponse = new MutableLiveData<LeftResponseModel>();
            //we will load it asynchronously from server in this method
            removeMemberResponse = new CreateGroupRepository().removeMember(jsonArray,context);
            groupInterface.onSuccessRemoveMember(removeMemberResponse);
        }else{
            removeMemberResponse = new CreateGroupRepository().removeMember(jsonArray,context);
            groupInterface.onSuccessRemoveMember(removeMemberResponse);
        }
    }
}
