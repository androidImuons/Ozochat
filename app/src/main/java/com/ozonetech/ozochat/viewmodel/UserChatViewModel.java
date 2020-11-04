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

public class UserChatViewModel extends ViewModel {

    public LiveData<CreateGRoupREsponse> commonResponse;
    public CommonResponseInterface callback;
    public CreateGroupInterface groupInterface;

    public void createGroup(Context context, JsonArray arrayListAge) {

        if (commonResponse == null) {
            commonResponse = new MutableLiveData<CreateGRoupREsponse>();
            //we will load it asynchronously from server in this method
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge,context);
            groupInterface.onSuccessCreateGroup(commonResponse);
        }else{
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge,context);
            groupInterface.onSuccessCreateGroup(commonResponse);    }
    }

}
