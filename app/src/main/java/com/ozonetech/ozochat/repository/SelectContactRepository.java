package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
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

    private MutableLiveData<CreateGRoupREsponse> createGeoupResponse;
    CreateGRoupREsponse createGroupModel;

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

    public LiveData<CreateGRoupREsponse> createGroup(JsonArray arrayListAge, Context context) {

        Log.d(tag,"-----row body--"+arrayListAge.toString());
        createGeoupResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager=new MyPreferenceManager(context);

        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.createGroup(arrayListAge).enqueue(new Callback<CreateGRoupREsponse>() {
            @Override
            public void onResponse(Call<CreateGRoupREsponse> call, Response<CreateGRoupREsponse> response) {

                if (response.isSuccessful()) {
                    createGroupModel = response.body();
                    createGeoupResponse.setValue(createGroupModel);
                    Log.d(tag,"-  create true 200---"+new Gson().toJson(response.body()));

                } else {
                    createGroupModel = response.body();
                    createGeoupResponse.setValue(createGroupModel);
                    Log.d(tag,"- create response false 200---"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<CreateGRoupREsponse> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return createGeoupResponse;
    }

}
