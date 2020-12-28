package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityStatusEditBinding;
import com.ozonetech.ozochat.databinding.StatusToolbarBinding;
import com.ozonetech.ozochat.listeners.StatusListener;
import com.ozonetech.ozochat.model.StatusResponseModel;
import com.ozonetech.ozochat.model.UserStatusResponseModel;
import com.ozonetech.ozochat.network.FileUtils;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.StatusCaptionAdapter;
import com.ozonetech.ozochat.view.adapter.UserStatusAdapter;
import com.ozonetech.ozochat.view.adapter.UserStatusSelectorAdapter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StatusEditActivity extends BaseActivity implements UserStatusAdapter.StatusAdapterListerner,
        StatusCaptionAdapter.StatusCaptionInterface,
        StatusListener {

    ActivityStatusEditBinding binding;
    StatusToolbarBinding statusToolbarBinding;
    UserStatusAdapter adapter;
    UserStatusSelectorAdapter selectorAdapter;
    StatusCaptionAdapter statusCaptionAdapter;
    ArrayList<Uri> returnValue;
    Bitmap bitmap;
    private MyPreferenceManager myPreferenceManager;
    private Uri mCropImageUri;
    int selectedPosition = 0;
    ArrayList<String> captionArraylist;
    private ArrayList<File> imageFiles;
    private ArrayList<RequestBody> requestFiles;
    private ArrayList<String> fileNames;
    UserStatusResponseModel userStatusResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(StatusEditActivity.this, R.layout.activity_status_edit);
        userStatusResponseModel = ViewModelProviders.of(StatusEditActivity.this).get(UserStatusResponseModel.class);

        statusToolbarBinding = binding.includeToolbar;
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            returnValue = (ArrayList<Uri>) getIntent().getSerializableExtra("key");
        }
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());

        initView();

    }

    private void initView() {

        Glide.with(this)
                .load(myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_PROFILE_PIC))
                .placeholder(R.drawable.person_icon)
                .into(statusToolbarBinding.thumbnail);

        statusToolbarBinding.ivCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });


        // Uri imageUri = Uri.fromFile(new File(returnValue.get(0)));
        binding.currentStreamImage.setImageURI(returnValue.get(0));

       /* LinearLayoutManager mLayoutManager = new LinearLayoutManager(StatusEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewSelector.setLayoutManager(mLayoutManager);
        selectorAdapter = new UserStatusSelectorAdapter(StatusEditActivity.this);
        selectorAdapter.addImage(returnValue);
        binding.recyclerViewSelector.setAdapter(selectorAdapter);*/

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(StatusEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(mLayoutManager1);
        adapter = new UserStatusAdapter(StatusEditActivity.this);
        adapter.addImage(returnValue, this::onStatusViewClicked);
        binding.recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatusEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvEtCaption.setLayoutManager(linearLayoutManager);
        captionArraylist = new ArrayList<>();
        for (int i = 0; i < captionArraylist.size(); i++) {
            captionArraylist.add("");
        }
        statusCaptionAdapter = new StatusCaptionAdapter(StatusEditActivity.this, captionArraylist, this::editCaption);
        binding.rvEtCaption.setAdapter(statusCaptionAdapter);
        binding.rvEtCaption.setVisibility(View.GONE);

/*
        binding.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.caption.getText().toString().isEmpty() || binding.caption.getText().toString().equalsIgnoreCase("")){
                    captionArraylist.set(selectedPosition,binding.caption.getText().toString());
                }
            }
        });
*/

        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getAllUri().size() != 0) {
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);
                    ArrayList<String> filePaths = new ArrayList<>();
                    for (int i = 0; i < adapter.getAllUri().size(); i++) {
                        //File file = FileUtils.getFile(StatusEditActivity.this, adapter.getAllUri().get(i));
                        filePaths.add(getRealPathFromDocumentUri(StatusEditActivity.this, adapter.getAllUri().get(i)));
                    }
                    for (int i = 0; i < filePaths.size(); i++) {
                        File file = new File(filePaths.get(i));
                        builder.addFormDataPart("files[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                    }

                    builder.addFormDataPart("sender_id", myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_USER_ID))
                            .addFormDataPart("text", "Good Morning")
                            .addFormDataPart("bg_color", "green")
                            .addFormDataPart("caption", "[]");

                    RequestBody requestBody = builder.build();
                    Log.d("StatusEditActivity", "-- 200---" + requestBody.toString());

                    gotoSetUserStatus(requestBody);

                } else {
                    showSnackbar(binding.rlStatusEdit, "Please select the file to update status", Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    public static String getRealPathFromDocumentUri(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            Log.e(StatusEditActivity.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{imgId}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }


    private void gotoSetUserStatus(RequestBody requestBody) {
        showProgressDialog("Please wait...");
        userStatusResponseModel.setUserStatus(StatusEditActivity.this, requestBody, userStatusResponseModel.statusListener = this);
    }

    public void onSelectImageClick(View view) {
        //  Uri imageUri = Uri.fromFile(new File(returnValue.get(selectedPosition)));

        // For API >= 23 we need to check specifically that we have permissions to read external storage.
        if (CropImage.isReadExternalStoragePermissionsRequired(this, returnValue.get(selectedPosition))) {
            // request permissions and handle the result in onRequestPermissionsResult()
            mCropImageUri = returnValue.get(selectedPosition);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {
            // no permissions required or already grunted, can start crop image activity
            startCropImageActivity(returnValue.get(selectedPosition));
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                binding.currentStreamImage.setImageURI(result.getUri());

                returnValue.set(selectedPosition, result.getUri());
                adapter.addImage(returnValue, this::onStatusViewClicked);
                binding.recyclerView.setAdapter(adapter);

                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void onStatusViewClicked(int position) {
        selectedPosition = position;
        // Uri imageUri = Uri.fromFile(new File(returnValue.get(position)));
        binding.currentStreamImage.setImageURI(returnValue.get(position));

       /* binding.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captionArraylist.set(selectedPosition,binding.caption.getText().toString());
            }
        });

        if(captionArraylist.get(selectedPosition).equalsIgnoreCase("Add a caption....")){
            binding.caption.setHint("Add a caption....");
        }else {
            binding.caption.setText(captionArraylist.get(selectedPosition));
        }*/
    }

    @Override
    public void editCaption(int postion) {
       /* binding.rvEtCaption.setVisibility(View.GONE);
        binding.caption.setVisibility(View.VISIBLE);
        captionArraylist.set(postion,binding.caption.getText().toString());*/
    }

    @Override
    public void onGetUserStatusSuccess(LiveData<StatusResponseModel> statusGetResponse) {

    }

    @Override
    public void onSetUserStatusSuccess(LiveData<UserStatusResponseModel> userStatusResponse) {

        userStatusResponse.observe(StatusEditActivity.this, new Observer<UserStatusResponseModel>() {
            @Override
            public void onChanged(UserStatusResponseModel userStatusResponseModel) {

                //save access token
                hideProgressDialog();
                try {

                    if (userStatusResponseModel.getSuccess()) {
                        showSnackbar(binding.rlStatusEdit, userStatusResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        finish();
                    } else {
                        showSnackbar(binding.rlStatusEdit, userStatusResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }

                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }

            }
        });
    }
}


/*
                if (adapter.getAllUri().size() != 0) {
                    fileNames = new ArrayList<>();
                    imageFiles = new ArrayList<>();
                    requestFiles = new ArrayList<>();
                    for (int i = 0; i < adapter.getAllUri().size(); i++) {
                        if (adapter.getAllUri().get(i) != null) {
                            Uri imageUri = adapter.getAllUri().get(i);
                            Cursor cursor = StatusEditActivity.this.getContentResolver().query(adapter.getAllUri().get(i),
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                File imageFile = new File(picturePath);
                                imageFiles.add(imageFile);
                                fileNames.add(imageFile.getName());
                                RequestBody requestFile =
                                        RequestBody.create(
                                                MediaType.parse(StatusEditActivity.this.getContentResolver().getType(imageUri)),
                                                imageFile
                                        );
                                requestFiles.add(requestFile);
                                cursor.close();
                            }

                        }
                    }



                  //  builder.addFormDataPart("file", fileNames, requestFiles);
                    for(File filePath : imageFiles){
                        builder.addFormDataPart("files", filePath.getName(),
                                RequestBody.create(MediaType.parse("image/*"), filePath));
                    }

                }
*/