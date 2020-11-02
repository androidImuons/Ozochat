package com.ozonetech.ozochat.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupRepository {
    private String tag="CreateGroupRepository";

    private MutableLiveData<CommonResponse> verifiedContactsResponse;
    CommonResponse verifiedContactsModel;

    public LiveData<CommonResponse> createGroup(JSONArray arrayListAge) {
        verifiedContactsResponse = new MutableLiveData<>();
        AppServices apiService = ServiceGenerator.createService(AppServices.class);
        apiService.createGroup(arrayListAge).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    verifiedContactsModel = response.body();
                    verifiedContactsResponse.setValue(verifiedContactsModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                } else {
                    verifiedContactsModel = response.body();
                    verifiedContactsResponse.setValue(verifiedContactsModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return verifiedContactsResponse;
    }

}
