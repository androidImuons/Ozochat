package com.ozonetech.ozochat.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityProfileBinding;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.FileUtils;
import com.ozonetech.ozochat.network.ViewUtils;
import com.ozonetech.ozochat.network.webservices.AppServices;
import com.ozonetech.ozochat.network.webservices.ServiceGenerator;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.dialog.EditDialog;
import com.ozonetech.ozochat.viewmodel.ProfileInfoViewModel;
import com.ozonetech.ozochat.viewmodel.ProfileUpdateViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUpdateActivity extends BaseActivity implements EditDialog.SendCallBack {

    Uri outPutfileUri = Uri.parse("");
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    String isAttachment = "0";

    ActivityProfileBinding binding;
    ProfileUpdateViewModel viewModel;
    private MyPreferenceManager myPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ProfileUpdateActivity.this, R.layout.activity_profile);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(ProfileUpdateActivity.this).get(ProfileUpdateViewModel.class);
        binding.setUpdate(viewModel);
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());

        setProfile();

    }

    private void setProfile() {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(getResources().getColor(R.color.colorBlack), 1.0f);
        roundingParams.setRoundAsCircle(true);
        binding.image.getHierarchy().setRoundingParams(roundingParams);

        if (myPreferenceManager != null) {
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME) != null && !myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME).equals("")) {
                binding.txtName.setText(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_NAME));
            }
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC) != null) {
                Log.d("image url", "--details-" + (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC)));
                binding.image.setImageURI(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC));
            }
            if (myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_ABOUT_US) != null) {
                binding.txtAboutUs.setText(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_ABOUT_US));
            }
            binding.txtPhoneNumber.setText(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));
        } else {
            Log.d("details", "--details-null");
        }
    }

    public void uploadImg(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_option_dialog);
        dialog.setTitle(getResources().getString(R.string.app_name));
        TextView camera = (TextView) dialog.findViewById(R.id.camera);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        TextView textViewCancel = (TextView) dialog.findViewById(R.id.cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                dialog.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGalleryPermission();
                dialog.dismiss();
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                    200);
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    201);
        } else {
            startGalleryIntent();
        }
    }

    private void startGalleryIntent() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    startCameraIntent();
                }
                break;
            case 201:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startGalleryIntent();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                outPutfileUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "attachment", null);
                    outPutfileUri = Uri.parse(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isAttachment = "1";
                binding.image.setImageURI(outPutfileUri);
                updateProfile();
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = null;

                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "attachment", null);
                outPutfileUri = Uri.parse(url);

                isAttachment = "1";
                binding.image.setImageURI(outPutfileUri);

                updateProfile();
            }
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setRoundAsCircle(true);
            binding.image.getHierarchy().setRoundingParams(roundingParams);
        }
    }

    private void updateProfile() {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            final Dialog dialog = ViewUtils.getProgressBar(this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppServices apiService = ServiceGenerator.createService(AppServices.class);
            RequestBody uid = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(myPreferenceManager.getUserId()));

            MultipartBody.Part imageUrl = null;
            RequestBody requestFile = null;
            if (isAttachment.equals("1")) {
                File file = FileUtils.getFile(this, outPutfileUri);
                imageUrl = prepareFilePart("image", outPutfileUri);
            }
            Map<String, RequestBody> map = new HashMap<>();
            map.put("uid", uid);

            Call call = apiService.REGISTRATION_RESPONSE_CALL(map, imageUrl);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    // Log.i("upload Response": response);
                    AppCommon.getInstance(ProfileUpdateActivity.this).clearNonTouchableFlags(ProfileUpdateActivity.this);
                    dialog.dismiss();
                    UploadResponse authResponse = (UploadResponse) response.body();
                    if (authResponse != null) {
                        Log.i("Response::", new Gson().toJson(authResponse));
                        if (authResponse.getSuccess() == true) {
                            showSnackbar(binding.rrLayer, authResponse.getMessage(), Snackbar.LENGTH_SHORT);
                            myPreferenceManager.setProfilePic(authResponse.getDataObject().getImage_url());
                            Log.d("image set", "---" + myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_PROFILE_PIC));

                        } else {
                            showSnackbar(binding.rrLayer, authResponse.getMessage(), Snackbar.LENGTH_SHORT);
                        }
                    } else {
                        AppCommon.getInstance(ProfileUpdateActivity.this).showDialog(ProfileUpdateActivity.this, authResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    Log.d("image fail", "----" + t.getMessage());
                    AppCommon.getInstance(ProfileUpdateActivity.this).clearNonTouchableFlags(ProfileUpdateActivity.this);
                    showSnackbar(binding.rrLayer, getResources().getString(R.string.ServerError), Snackbar.LENGTH_SHORT);
                }
            });
        }
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    public void updatename(View view){
        Bundle bundle = new Bundle();
        bundle.putString("name", String.valueOf(binding.txtName.getText().toString()));
        bundle.putString("param","username");
        EditDialog instance = EditDialog.getInstance(bundle);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
        instance.sendCallBack =(EditDialog.SendCallBack) ProfileUpdateActivity.this;
    }
    public void updatestatus(View view){
        Bundle bundle = new Bundle();
        bundle.putString("name", String.valueOf(binding.txtAboutUs.getText().toString()));
        bundle.putString("param","user_status");
        EditDialog instance = EditDialog.getInstance(bundle);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
        instance.sendCallBack =(EditDialog.SendCallBack) ProfileUpdateActivity.this;
    }

    @Override
    public void sendMessage(String msg, String pos) {
            updateNameAboutUs(msg,pos);
    }

    @Override
    public void sendError(String err) {

    }

    public void updateNameAboutUs(String msg, String pos) {
        if (AppCommon.getInstance(this).isConnectingToInternet(this)) {
            final Dialog dialog = ViewUtils.getProgressBar(this);
            AppCommon.getInstance(this).setNonTouchableFlags(this);
            AppServices apiService = ServiceGenerator.createService(AppServices.class, myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));

            Map<String, String> map = new HashMap<>();
            map.put("uid", myPreferenceManager.getUserId());
            map.put(pos, msg);

            Call call = apiService.updateUserInfo(map);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    // Log.i("upload Response": response);
                    AppCommon.getInstance(ProfileUpdateActivity.this).clearNonTouchableFlags(ProfileUpdateActivity.this);
                    dialog.dismiss();
                    Log.i("Response::", new Gson().toJson(response.body()));
                    CommonResponse authResponse = (CommonResponse) response.body();

                    if (authResponse != null) {
                        showSnackbar(binding.rrLayer, authResponse.getMessage(), Snackbar.LENGTH_SHORT);

                        if(pos.equals("user_status")){
                            myPreferenceManager.setAboutus(msg);
                         binding.txtAboutUs.setText(msg);
                        }else{
                            myPreferenceManager.setUserName(msg);
                            binding.txtName.setText(msg);
                        }

                    } else {
                        AppCommon.getInstance(ProfileUpdateActivity.this).showDialog(ProfileUpdateActivity.this, authResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    dialog.dismiss();
                    Log.d("name and about us", "----" + t.getMessage());
                    AppCommon.getInstance(ProfileUpdateActivity.this).clearNonTouchableFlags(ProfileUpdateActivity.this);
                    showSnackbar(binding.rrLayer, getResources().getString(R.string.ServerError), Snackbar.LENGTH_SHORT);
                }
            });


        }
    }
}