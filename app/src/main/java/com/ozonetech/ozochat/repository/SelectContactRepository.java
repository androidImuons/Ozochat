package com.ozonetech.ozochat.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectContactRepository {
    private String tag="SelectContactRepository";

    private MutableLiveData<VerifiedContactsModel> verifiedContactsResponse;
    VerifiedContactsModel verifiedContactsModel;

    public LiveData<VerifiedContactsModel> sendValidContacts( NumberListObject arrayListAge) {
        verifiedContactsResponse=new MutableLiveData<>();
        AppServices apiService = ServiceGenerator.createService(AppServices.class);
        apiService.getValidContacts(arrayListAge).enqueue(new Callback<VerifiedContactsModel>() {
            @Override
            public void onResponse(Call<VerifiedContactsModel> call, Response<VerifiedContactsModel> response) {
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
            public void onFailure(Call<VerifiedContactsModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return verifiedContactsResponse;
    }
}
