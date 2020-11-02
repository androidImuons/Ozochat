package com.ozonetech.ozochat.network.webservices;

import com.google.gson.JsonArray;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.LoginResponse;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.model.OTPResponse;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface AppServices {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> EnterMobileApi(@FieldMap Map<String, String> entityMap);

    @FormUrlEncoded
    @POST("verifyOtp")
    Call<OTPResponse> verifyOtpCall(@FieldMap Map<String, String> entityMap);

    @Multipart
    @POST("upload")
    Call<UploadResponse> REGISTRATION_RESPONSE_CALL(
       /*     @Part("uid") RequestBody uid,
            @Part("username") RequestBody userName,
            @Part MultipartBody.Part image*/

            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part file);


    @POST("validMembers")
    Call<VerifiedContactsModel> getValidContacts(@Body NumberListObject ages);
   // Content-Type: text/plain
    //"[\n    {\"admin\":7087741183},\n    {\"members\": [\n        {\"mobile\": \"9509504456\"},\n\t\t{\"mobile\": \"7488713416\"},\n\t\t{\"mobile\": \"7008774118\"}\n\t]},\n    {\"group_name\":\"ABC\"}\n]",
   @Headers({
           //"Accept: application/json",
           "Content-Type: application/json"
   })
   @POST("create-group")
    Call<CommonResponse> createGroup(@Body JSONArray array);


}
