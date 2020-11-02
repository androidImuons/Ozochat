package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.databinding.ToolbarConversationBinding;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.model.User;
import com.ozonetech.ozochat.network.AppCommon;
import com.ozonetech.ozochat.network.MyPreference;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.ChatRoomThreadAdapter;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import butterknife.internal.Utils;
import io.socket.client.On;
import io.socket.emitter.Emitter;

public class UserChatActivity extends AppCompatActivity implements CommonResponseInterface {

    ActivityUserChatBinding dataBinding;
    ToolbarConversationBinding toolbarDataBinding;
    String contactName;
    String contactMobileNo;
    String contactStatus;
    String contactProfilePic;
    String chatRoomId;
    private ArrayList<Message> messageArrayList;
    private ChatRoomThreadAdapter mAdapter;
    private String tag = "UserChatActivity";
    UserChatViewModel chatViewModel;
    MyPreferenceManager myPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(UserChatActivity.this, R.layout.activity_user_chat);
        toolbarDataBinding = dataBinding.toolbarLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        chatViewModel = ViewModelProviders.of(UserChatActivity.this).get(UserChatViewModel.class);
        dataBinding.setUserChat(chatViewModel);
        chatViewModel.callback = (CommonResponseInterface) this;

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        contactName = intent.getStringExtra("name");
        contactMobileNo = intent.getStringExtra("mobileNo");
        contactStatus = intent.getStringExtra("status");
        contactProfilePic = intent.getStringExtra("profilePic");
        myPreferenceManager = new MyPreferenceManager(getApplicationContext());
        init();

        checkGroup();

    }

    private void checkGroup() {
        JSONArray jsonArray = new JSONArray();
        JSONObject admin = new JSONObject();
        JSONArray memerArray = new JSONArray();

        JSONObject member = new JSONObject();

        try {
            admin.put("admin", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));
            member.put("mobile", contactMobileNo);
            memerArray.put(member);
            JSONObject memersObjeect = new JSONObject();
            memersObjeect.put("members", memerArray);
            JSONObject groupname=new JSONObject();
            groupname.put("group_name","");

            jsonArray.put(admin);
            jsonArray.put(memersObjeect);
            jsonArray.put(groupname);
            chatViewModel.createGroup(getApplicationContext(), jsonArray);
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private void init() {


        toolbarDataBinding.actionBarTitle1.setText(contactName);
        Glide.with(this)
                .load(contactProfilePic)
                .placeholder(R.drawable.profile_icon)
                .into(toolbarDataBinding.conversationContactPhoto);
        toolbarDataBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dataBinding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }


        messageArrayList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Message msg = new Message();
            msg.setId(String.valueOf(i));
            msg.setMessage("hii " + i);
            msg.setCreatedAt("04:0" + i);
          /*  User user = new User(String.valueOf(2),
                    "RUCHITA",
                    "ruchita@123");*/
            // msg.setUser(user);
            messageArrayList.add(msg);
        }
        Message msg = new Message();
        msg.setId("3");
        msg.setMessage("hii");
        msg.setCreatedAt("04:00");
        /*User user = new User(String.valueOf(1),
                "MAYURI",
                "mayuri@123");*/
        // msg.setUser(user);
        messageArrayList.add(msg);


        // self user id is to identify the message owner
        //  String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        // mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
    }

    private void sendMessage() {
        final String message = dataBinding.message.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", chatRoomId);
            jsonObject.put("group_id","GP1604310627098");
            jsonObject.put("message", dataBinding.message.getText().toString());
            MyApplication.getInstance().getSocket().emit("sendMessage", jsonObject).on("sendMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "---send message--");
                    getMessage();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataBinding.message.setText("");


    }

    private void getMessage() {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", chatRoomId);
            MyApplication.getInstance().getSocket().emit("getMessages", json).on("getMessages", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray data = (JSONArray) args[0];
                    final JSONArray result = (JSONArray) args[0];
                      new Handler(getMainLooper())
                            .post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(tag, "--getMessage -data-array-" + data.toString());
                                        }
                                    }

                            );

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCommoStarted() {

    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> userProfileResponse) {
        userProfileResponse.observe(UserChatActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {
                getMessage();
            }
        });

    }

    @Override
    public void onCommonFailure(String message) {

    }
}