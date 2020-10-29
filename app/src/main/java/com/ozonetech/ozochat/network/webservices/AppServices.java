package com.ozonetech.ozochat.network.webservices;

import com.ozonetech.ozochat.model.LoginResponse;
import com.ozonetech.ozochat.model.OTPResponse;
import com.ozonetech.ozochat.model.UploadResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
            @Part("uid") RequestBody uid,
            @Part("username") RequestBody userName,
            @Part MultipartBody.Part image
    );


    @POST("validMembers")
    Call<VerifiedContactsModel> getValidContacts(@Body NumberListObject ages);

}
