package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.StatusResponseModel;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusRepository {
    private static String TAG = StatusRepository.class.getSimpleName();
    private MutableLiveData<StatusResponseModel> statusGetResponse;
    StatusResponseModel statusResponseModel;

    public LiveData<StatusResponseModel> getUserStatus(Context context, Map<String, String> statusMap) {

        statusGetResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager = new MyPreferenceManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class);
        apiService.getUserStatus(statusMap).enqueue(new Callback<StatusResponseModel>() {
            @Override
            public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.isSuccessful()) {
                    statusResponseModel = response.body();
                    statusGetResponse.setValue(statusResponseModel);
                    Log.d(TAG, "-- 200---" + new Gson().toJson(response.body()));
                } else {
                    Log.d(TAG, "-- 200---" + new Gson().toJson(response.body()));
                    statusResponseModel = response.body();
                    statusGetResponse.setValue(statusResponseModel);
                    Log.d(TAG, "-- 200---" + new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<StatusResponseModel> call, Throwable t) {
                Log.d(TAG, "--------------" + t.getMessage());
            }
        });
        return statusGetResponse;
    }

}
