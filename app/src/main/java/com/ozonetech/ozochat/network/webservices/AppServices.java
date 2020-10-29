package com.ozonetech.ozochat.network.webservices;

import com.google.gson.JsonObject;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppServices {

    @POST("validMembers")
    Call<VerifiedContactsModel> getValidContacts(@Body NumberListObject ages);

}
/*  @Headers({"Accept: application/json",
            "Content-Type: application/json"})
    @POST("validMembers")
    Call<VerifiedContactsModel> getValidContacts(@Body NumberListObject numberListObject);*/