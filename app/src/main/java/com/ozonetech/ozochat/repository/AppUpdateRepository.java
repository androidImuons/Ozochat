package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.AppVersionModel;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdateRepository {
    MutableLiveData<AppVersionModel> commonResponseLiveData;
    AppVersionModel commonResponse;
    private String tag="AppUpdateRepository";

    public LiveData<AppVersionModel> appUpdate(Context context, String code){
        MyPreferenceManager myPreferenceManager=new MyPreferenceManager(context);
        commonResponseLiveData = new MutableLiveData<>();
        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        HashMap<String,String>param=new HashMap<>();
        Log.d(tag,"--param-"+code);
        param.put("apkversion",code);
        apiService.updateApp(param).enqueue(new Callback<AppVersionModel>() {
            @Override
            public void onResponse(Call<AppVersionModel> call, Response<AppVersionModel> response) {
                if (response.isSuccessful()) {
                    commonResponse = response.body();
                    commonResponseLiveData.setValue(commonResponse);
                    Log.d(tag,"- success-"+new Gson().toJson(response.body()));
                } else {
                    commonResponse = response.body();
                    commonResponseLiveData.setValue(commonResponse);
                    Log.d(tag,"- unsuccess-"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<AppVersionModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return commonResponseLiveData;
    }

}
