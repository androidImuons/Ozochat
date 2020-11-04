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
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentChatList {
    private String tag="RecentChatList";

    private MutableLiveData<Object> rechentlivedata;
    Object verifiedContactsModel;
    public LiveData<Object> recentChat(Map <String,String>arrayListAge, Context context) {

        Log.d(tag,"-----row body--"+arrayListAge.toString());
        rechentlivedata = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager=new MyPreferenceManager(context);

        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.getResentChat(arrayListAge).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    verifiedContactsModel = response.body();
                    rechentlivedata.setValue(verifiedContactsModel);

                } else {
                    verifiedContactsModel = response.body();
                    rechentlivedata.setValue(verifiedContactsModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return rechentlivedata;
    }
}
