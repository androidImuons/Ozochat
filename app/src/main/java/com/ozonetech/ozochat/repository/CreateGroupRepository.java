package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;

import org.json.JSONArray;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupRepository {
    private String tag = "CreateGroupRepository";

    private MutableLiveData<CreateGRoupREsponse> verifiedContactsResponse;
    CreateGRoupREsponse verifiedContactsModel;

    private MutableLiveData<LeftResponseModel> leftGroupResponse;
    LeftResponseModel leftGroupResponseModel;

    private MutableLiveData<GroupDetailModel> groupDetailResponse;
    GroupDetailModel groupDetailModel;

    public LiveData<CreateGRoupREsponse> createGroup(JsonArray arrayListAge, Context context) {
        Log.d(tag, "-----row body--" + arrayListAge.toString());
        verifiedContactsResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager = new MyPreferenceManager(context);

        AppServices apiService = ServiceGenerator.createService(AppServices.class, myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.createGroup(arrayListAge).enqueue(new Callback<CreateGRoupREsponse>() {
            @Override
            public void onResponse(Call<CreateGRoupREsponse> call, Response<CreateGRoupREsponse> response) {
                if (response.body() == null) {
                  return;
                }
                if (response.isSuccessful()) {
                    verifiedContactsModel = response.body();
                    verifiedContactsResponse.setValue(verifiedContactsModel);
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));

                } else {
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));
                    verifiedContactsModel = response.body();
                    verifiedContactsResponse.setValue(verifiedContactsModel);
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<CreateGRoupREsponse> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return verifiedContactsResponse;
    }

    public LiveData<LeftResponseModel> leftGroup(JsonArray jsonArray, Context context) {
        Log.d(tag, "-----row body--" + jsonArray.toString());
        leftGroupResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager = new MyPreferenceManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.leftGroup(jsonArray).enqueue(new Callback<LeftResponseModel>() {
            @Override
            public void onResponse(Call<LeftResponseModel> call, Response<LeftResponseModel> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.isSuccessful()) {
                    leftGroupResponseModel = response.body();
                    leftGroupResponse.setValue(leftGroupResponseModel);
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));
                } else {
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));
                    leftGroupResponseModel = response.body();
                    leftGroupResponse.setValue(leftGroupResponseModel);
                    Log.d(tag, "- 200---" + new Gson().toJson(response.body()));
                }
            }
            @Override
            public void onFailure(Call<LeftResponseModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return leftGroupResponse;
    }

    public LiveData<GroupDetailModel> getGroupDetails(Map<String,String> groupMap, Context context) {

        groupDetailResponse = new MutableLiveData<>();
        MyPreferenceManager myPreferenceManager=new MyPreferenceManager(context);

        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        apiService.getGroupInfo(groupMap).enqueue(new Callback<GroupDetailModel>() {
            @Override
            public void onResponse(Call<GroupDetailModel> call, Response<GroupDetailModel> response) {
                Log.d(tag,"- 200---"+new Gson().toJson(response.body()));

                if (response.isSuccessful()) {
                    groupDetailModel = response.body();
                    groupDetailResponse.setValue(groupDetailModel);

                } else {
                    groupDetailModel = response.body();
                    groupDetailResponse.setValue(groupDetailModel);
                    Log.d(tag,"- unsuccess-"+new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<GroupDetailModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return groupDetailResponse;
    }

}
