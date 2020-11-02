package com.ozonetech.ozochat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.SelectContactRepository;

import org.json.JSONArray;

public class UserChatViewModel extends ViewModel {

    public LiveData<CommonResponse> commonResponse;
    public CommonResponseInterface callback;


    public void createGroup(Context context, JSONArray arrayListAge) {

        if (commonResponse == null) {
            commonResponse = new MutableLiveData<CommonResponse>();
            //we will load it asynchronously from server in this method
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge);
            callback.onCommonSuccess(commonResponse);
        }else{
            commonResponse = new CreateGroupRepository().createGroup(arrayListAge);
            callback.onCommonSuccess(commonResponse);    }
    }

}
