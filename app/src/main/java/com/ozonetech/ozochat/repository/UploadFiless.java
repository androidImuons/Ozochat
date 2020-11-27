package com.ozonetech.ozochat.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.network.FileUtils;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFiless {
    private MutableLiveData<UploadResponse> commonResponseMutableLiveData;
    private String tag = "UploadFiless";
    private UploadResponse commonResponse;

    public MutableLiveData<UploadResponse> sendFiles(Context context, String user_id, String group_id, String admin_id, ArrayList<String> filepath) {
        commonResponseMutableLiveData = new MutableLiveData<>();
        MultipartBody.Part body1 = null;
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < filepath.size(); i++) {
            Log.d(tag, "--upload url--" + filepath.get(i));
            parts.add(prepareFilePart(filepath.get(i)));
        }

        if (file_size > 10000) {
            Log.d(tag, "-----size send--" + file_size);
            commonResponse = new UploadResponse();
            commonResponse.setCode(404);
            commonResponse.setMessage("Maximum 10 MB Size Allowed");
            commonResponseMutableLiveData.setValue(commonResponse);
            return commonResponseMutableLiveData;
        } else {
            Log.d(tag, "-----size send--less" + file_size);
        }


        MyPreferenceManager session = new MyPreferenceManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_TOKEN));

        RequestBody reqGroupId = RequestBody.create(MultipartBody.FORM, group_id);
        RequestBody reqUserID = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody reqAdminID = RequestBody.create(MultipartBody.FORM, admin_id);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("group_id", reqGroupId);
        map.put("admin_id", reqAdminID);
        map.put("sender_id", reqUserID);

        Call<UploadResponse> call = apiService.uploadFiles(map, parts); //, body

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("response 200", "--2-" + new Gson().toJson(response.body()));
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                } else {
                    Log.d("response not 200", "--3-" + response.message());
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("response fail", "--on fail-" + t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }

    double file_size;

    private MultipartBody.Part prepareFilePart(String fileUri) {

        File file = new File(fileUri);
        MultipartBody.Part body = null;
        long length = file.length();
        length = length / 1024;
        file_size = file_size + length;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        body = MultipartBody.Part.createFormData("files[]", file.getName().replace(" ", "_"), requestFile);
        return body;
    }


    public MutableLiveData<UploadResponse> uploadGroupImage(Context context, String user_id, String group_id, String admin_id, String filepath) {
        commonResponseMutableLiveData = new MutableLiveData<>();
        MultipartBody.Part body1 = null;
        body1 = prepareImageFilePart(context,filepath);
        if (file_size > 10000) {
            Log.d(tag, "-----size send--" + file_size);
            commonResponse = new UploadResponse();
            commonResponse.setCode(404);
            commonResponse.setMessage("Maximum 10 MB Size Allowed");
            commonResponseMutableLiveData.setValue(commonResponse);
            return commonResponseMutableLiveData;
        } else {
            Log.d(tag, "----less-size send--" + file_size);
        }


        MyPreferenceManager session = new MyPreferenceManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_TOKEN));

        RequestBody reqGroupId = RequestBody.create(MultipartBody.FORM, group_id);
        RequestBody reqUserID = RequestBody.create(MultipartBody.FORM, user_id);
        RequestBody reqAdminID = RequestBody.create(MultipartBody.FORM, admin_id);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("group_id", reqGroupId);
//        map.put("admin_id", reqAdminID);
//        map.put("sender_id", reqUserID);

        Call<UploadResponse> call = apiService.uploadGroupImage(map, body1); //, body

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("response 200", "--Upload Img -" + new Gson().toJson(response.body()));
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                } else {
                    Log.d("response not 200", "--Upload Img-" + response.message());
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d(tag, "--on fail-" + t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }


    private MultipartBody.Part prepareImageFilePart(Context context, String fileUri) {

        File file = FileUtils.getFile(context, Uri.parse(fileUri));
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(context.getContentResolver().getType(Uri.parse(fileUri))),
                        file
                );
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);

//        File file = new File(fileUri);
//        MultipartBody.Part body = null;
//        long length = file.length();
//        length = length / 1024;
//        file_size = file_size + length;
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        body = MultipartBody.Part.createFormData("image", file.getName().replace(" ", "_"), requestFile);
//        return body;
    }
}
