package com.ozonetech.ozochat.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityMyStatusBinding;
import com.ozonetech.ozochat.model.StatusModel;
import com.ozonetech.ozochat.view.adapter.MyStatusViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class MyStatusActivity extends AppCompatActivity {

    ActivityMyStatusBinding binding;
    ArrayList<StatusModel> imgStatusArraylist;
    MyStatusViewAdapter myStatusViewAdapter;
    Options options;
    private final int requestCodePicker = 100;
    private  ArrayList<String> returnValue;
    private ArrayList<Uri> returnValueUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MyStatusActivity.this,R.layout.activity_my_status);
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        returnValue=new ArrayList<>();
        imgStatusArraylist = (ArrayList<StatusModel>) getIntent().getSerializableExtra("imgStatusArraylist");
        binding.toolbar.setTitle("My Status");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myStatusViewAdapter=new MyStatusViewAdapter(MyStatusActivity.this,imgStatusArraylist);
        binding.rvMyStatusList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvMyStatusList.setAdapter(myStatusViewAdapter);


        options = Options.init()
                .setRequestCode(requestCodePicker)
                .setCount(5)
                .setPreSelectedUrls(returnValue)
                .setExcludeVideos(false)
                .setVideoDurationLimitinSeconds(30)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/akshay/new");
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.setPreSelectedUrls(returnValue);
                Pix.start(MyStatusActivity.this, options);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodePicker) {
            if (resultCode == Activity.RESULT_OK) {
                returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                returnValueUri=new ArrayList<>();
                for (int i = 0; i < returnValue.size(); i++) {
                    Uri imageUri = Uri.fromFile(new File(returnValue.get(i)));
                    returnValueUri.add(imageUri);
                }

                gotoImgEditor(returnValueUri);

            }

        }

    }

    private void gotoImgEditor(ArrayList<Uri> returnValueUri) {
        Intent intent = new Intent(MyStatusActivity.this, StatusEditActivity.class);
        intent.putExtra("key", returnValueUri);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

