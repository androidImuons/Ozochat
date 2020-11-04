package com.ozonetech.ozochat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.RecentChatList;

import java.util.HashMap;
import java.util.Map;

public class ChatListViewModel extends ViewModel {
    public LiveData<Object> commonResponse;
  //  public CommonResponse callback;

    public void getchat(Context context,String id) {
        Map<String, String> param = new HashMap<>();
        param.put("sender_id", id);
        if (commonResponse == null) {
            commonResponse = new MutableLiveData<Object>();
            //we will load it asynchronously from server in this method
            commonResponse = new RecentChatList().recentChat(param, context);
            //  callback.setSuccess(commonResponse);
        } else {
            commonResponse = new RecentChatList().recentChat(param, context);
            //  groupInterface.onSuccessCreateGroup(commonResponse);    }
        }
    }

}
