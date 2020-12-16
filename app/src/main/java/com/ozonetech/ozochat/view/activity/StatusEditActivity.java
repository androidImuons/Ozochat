package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Size;

import com.bumptech.glide.Glide;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityStatusEditBinding;
import com.ozonetech.ozochat.databinding.StatusToolbarBinding;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.UserStatusAdapter;
import com.ozonetech.ozochat.view.adapter.UserStatusSelectorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StatusEditActivity extends AppCompatActivity {

    ActivityStatusEditBinding binding;
    StatusToolbarBinding statusToolbarBinding;
    UserStatusAdapter adapter;
    UserStatusSelectorAdapter selectorAdapter;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    private MyPreferenceManager myPreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(StatusEditActivity.this,R.layout.activity_status_edit);
        statusToolbarBinding = binding.includeToolbar;
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            returnValue = (ArrayList<String>) getIntent().getSerializableExtra("key");
        }
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());

        initView();

    }

    private void initView() {

        Glide.with(this)
                .load(myPreferenceManager.getUserDetails().get(MyPreferenceManager.KEY_PROFILE_PIC))
                .placeholder(R.drawable.person_icon)
                .into(statusToolbarBinding.thumbnail);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(StatusEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewSelector.setLayoutManager(mLayoutManager);
        selectorAdapter = new UserStatusSelectorAdapter(StatusEditActivity.this);
        selectorAdapter.addImage(returnValue);
        binding.recyclerViewSelector.setAdapter(selectorAdapter);

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(StatusEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(mLayoutManager1);
        adapter = new UserStatusAdapter(StatusEditActivity.this);
        adapter.addImage(returnValue);
        binding.recyclerView.setAdapter(adapter);

    }
}