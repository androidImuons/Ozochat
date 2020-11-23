package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityDetailViewUpdateBinding;
import com.ozonetech.ozochat.databinding.ContentMainBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewBinding;
import com.ozonetech.ozochat.databinding.WidgetHeaderViewTopBinding;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;

import java.util.ArrayList;

public class DetailViewUpdateActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener , CreateGroupInterface {

    private boolean isHideToolbarView = false;
    ActivityDetailViewUpdateBinding dataBinding;
    WidgetHeaderViewTopBinding widgetHeaderViewTopBinding;
    WidgetHeaderViewBinding widgetHeaderViewBinding;
    ContentMainBinding contentMainBinding;
    String contactName, last_seen, contactProfilePic;
    int groupChat, admin_id;
    MyPreferenceManager myPreferenceManager;
    UserChatViewModel chatViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(DetailViewUpdateActivity.this, R.layout.activity_detail_view_update);
        chatViewModel = ViewModelProviders.of(DetailViewUpdateActivity.this).get(UserChatViewModel.class);
        chatViewModel.groupInterface = (CreateGroupInterface) this;

        myPreferenceManager = new MyPreferenceManager(DetailViewUpdateActivity.this);
        widgetHeaderViewBinding = dataBinding.floatHeaderView;
        widgetHeaderViewTopBinding = dataBinding.toolbarHeaderView;
        contentMainBinding = dataBinding.updationLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        dataBinding.toolbar.setTitle("");
        setSupportActionBar(dataBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        contactName = intent.getStringExtra("contactName");
        contactProfilePic = intent.getStringExtra("contactProfilePic");
        last_seen = intent.getStringExtra("last_seen");
        groupChat = intent.getIntExtra("groupChat", 2);
        admin_id = intent.getIntExtra("admin_id", 0);
        initUi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUi() {
        dataBinding.appbar.addOnOffsetChangedListener(this);

        Glide.with(this)
                .load(contactProfilePic)
                .placeholder(R.drawable.profile_icon)
                .into(dataBinding.image);
        widgetHeaderViewTopBinding.name.setText(contactName);
        if (groupChat == 0) {
            widgetHeaderViewTopBinding.lastSeen.setText("");
        } else {
            widgetHeaderViewTopBinding.lastSeen.setText(last_seen);

        }

        widgetHeaderViewBinding.name.setText(contactName);
        if (groupChat == 0) {
            widgetHeaderViewTopBinding.lastSeen.setText("");
        } else {
            widgetHeaderViewBinding.lastSeen.setText(last_seen);
        }

        if (groupChat == 0) {
            contentMainBinding.rlContentMain.setVisibility(View.VISIBLE);
            int user_id = Integer.parseInt(myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
            if (admin_id == user_id) {
                contentMainBinding.llAddPeople.setVisibility(View.VISIBLE);
            } else {
                contentMainBinding.llAddPeople.setVisibility(View.GONE);
            }

        } else {
            contentMainBinding.rlContentMain.setVisibility(View.GONE);
        }


        contentMainBinding.llAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent= new Intent(DetailViewUpdateActivity.this)
            }
        });

        contentMainBinding.cvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //"[ {"member": [ {"mobile": "9509504456"} ]}, {"groupId":"GP1603952201631"} ]"
                JsonArray jsonArray = new JsonArray();
                JsonArray memerArray = new JsonArray();
                JsonObject member = new JsonObject();
                member.addProperty("mobile", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));
                memerArray.add(member);

                JsonObject groupId = new JsonObject();
                groupId.addProperty("groupId","GP1603952201631" );

                JsonObject memersObjeect = new JsonObject();
                memersObjeect.add("member", memerArray);

                jsonArray.add(memersObjeect);
                jsonArray.add(groupId);

                chatViewModel.leftGroup(getApplicationContext(), jsonArray);

            }
        });

        ArrayList<Contacts> myContactsArrayList = new ArrayList<>();
        myContactsArrayList = myPreferenceManager.getArrayListContact(myPreferenceManager.KEY_CONTACTS);



    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            widgetHeaderViewTopBinding.headerView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            widgetHeaderViewTopBinding.headerView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse) {

    }

    @Override
    public void onSuccessLeftGroup(LiveData<CommonResponse> leftGroupResponse) {
        leftGroupResponse.observe(DetailViewUpdateActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {

            }
        });
    }
}