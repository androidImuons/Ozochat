package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Size;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityStatusEditBinding;
import com.ozonetech.ozochat.databinding.StatusToolbarBinding;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.StatusCaptionAdapter;
import com.ozonetech.ozochat.view.adapter.UserStatusAdapter;
import com.ozonetech.ozochat.view.adapter.UserStatusSelectorAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StatusEditActivity extends AppCompatActivity implements UserStatusAdapter.StatusAdapterListerner , StatusCaptionAdapter.StatusCaptionInterface {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(StatusEditActivity.this, R.layout.activity_status_edit);
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

        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(StatusEditActivity.this,LinearLayoutManager.HORIZONTAL,false);
        binding.rvEtCaption.setLayoutManager(linearLayoutManager);
        captionArraylist=new ArrayList<>();
        for(int i=0;i<captionArraylist.size();i++){
            captionArraylist.add("");
        }
        statusCaptionAdapter=new StatusCaptionAdapter(StatusEditActivity.this,captionArraylist,this::editCaption);
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
                adapter.addImage(returnValue,this::onStatusViewClicked);
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
}