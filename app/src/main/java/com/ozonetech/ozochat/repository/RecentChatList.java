package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentChatList {
    private String tag="RecentChatList";

    private MutableLiveData<UserChatListModel> userChatListResponse;
    UserChatListModel userChatListModel;

    public LiveData<UserChatListModel> getUserResentChat(Map<String,String> arrayListAge, Context context) {

        Log.d(tag,"-----row body--"+arrayListAge.toString());
        userChatListResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager=new MyPreferenceManager(context);

        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.getUserResentChat(arrayListAge).enqueue(new Callback<UserChatListModel>() {
            @Override
            public void onResponse(Call<UserChatListModel> call, Response<UserChatListModel> response) {
                Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    userChatListModel = response.body();
                    userChatListResponse.setValue(userChatListModel);

                } else {
                    userChatListModel = response.body();
                    userChatListResponse.setValue(userChatListModel);
                    Log.d(tag,"- unsuccess-"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<UserChatListModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return userChatListResponse;
    }

}
