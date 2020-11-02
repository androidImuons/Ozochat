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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.databinding.ToolbarConversationBinding;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
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

public class UserChatActivity extends AppCompatActivity implements CommonResponseInterface,CreateGroupInterface {
    private static final String TAG = UserChatActivity.class.getName();
    MyPreferenceManager prefManager;

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
    private Socket mSocket;
    UserChatViewModel chatViewModel;
    MyPreferenceManager myPreferenceManager;
    private String group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(UserChatActivity.this, R.layout.activity_user_chat);
        toolbarDataBinding = dataBinding.toolbarLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        prefManager=new MyPreferenceManager(UserChatActivity.this);

        chatViewModel = ViewModelProviders.of(UserChatActivity.this).get(UserChatViewModel.class);
        dataBinding.setUserChat(chatViewModel);
        chatViewModel.callback = (CommonResponseInterface) this;
        chatViewModel.groupInterface = (CreateGroupInterface) this;

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        contactName = intent.getStringExtra("name");
        contactMobileNo = intent.getStringExtra("mobileNo");
        contactStatus = intent.getStringExtra("status");
        contactProfilePic = intent.getStringExtra("profilePic");
        init();



    }

    private void checkGroup() {
        JsonArray jsonArray = new JsonArray();
        JsonObject admin = new JsonObject();
        JsonArray memerArray = new JsonArray();

        JsonObject member = new JsonObject();

        admin.addProperty("admin", prefManager.getUserDetails().get(myPreferenceManager.KEY_USER_MOBILE));
        member.addProperty("mobile", contactMobileNo);
        memerArray.add(member);
        JsonObject memersObjeect = new JsonObject();
        memersObjeect.add("members", memerArray);
        JsonObject groupname = new JsonObject();
        groupname.addProperty("group_name", "");

        jsonArray.add(admin);
        jsonArray.add(memersObjeect);
        jsonArray.add(groupname);
        chatViewModel.createGroup(getApplicationContext(), jsonArray);
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
       // getMessage();
        checkGroup();
    }

    private void sendMessage() {
        final String message = dataBinding.message.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", prefManager.getUserDetails().get(MyPreferenceManager.KEY_USER_ID));
            jsonObject.put("group_id", group_id);
            jsonObject.put("message", dataBinding.message.getText().toString());
            Log.d(tag, "---send message parameter-- user_id : "+prefManager.getUserDetails().get(MyPreferenceManager.KEY_USER_ID)+
                    " \ngroup_id : "+group_id +"\n message : "+message);

            MyApplication.getInstance().getSocket().emit("sendMessage", jsonObject).on("sendMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "---send message--");

                    getMessage();

                  //  getMessage();

                  /*  mAdapter.notifyDataSetChanged();

                    if (mAdapter.getItemCount() > 1) {
                        dataBinding.recyclerView.getLayoutManager().smoothScrollToPosition(dataBinding.recyclerView, null, mAdapter.getItemCount() - 1);
                    }*/

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataBinding.message.setText("");


    }

    private void getMessage() {
        Log.d(tag, "--getMessage called");

        JSONObject json = new JSONObject();
        try {
            json.put("group_id", group_id);

           // json.put("user_id", chatRoomId);
           //  json.put("group_id","GP1604310627098");
            Log.d(tag, "---get message para \n group_id : "+group_id);

            MyApplication.getInstance().getSocket().emit("getMessages", json).on("getMessages", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray data = (JSONArray) args[0];
                    final JSONArray result = (JSONArray) args[0];
                    Log.d(tag, "--getMessage -data-array-" + data.toString());
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            setRecyclerView(data);


                        }
                    });

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setRecyclerView(JSONArray data) {
        messageArrayList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {

            try {
                JSONObject messageObj = data.getJSONObject(i);
                Message message = new Message();
                message.setId(messageObj.getInt("id"));
                message.setUserId(messageObj.getInt("user_id"));
                message.setGroupId(messageObj.getString("group_id"));
                message.setMessage(messageObj.getString("message"));
                message.setCreated(messageObj.getString("created"));

                messageArrayList.add(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        String selfUserId = MyApplication.getInstance().getPrefManager().getUserId();
        mAdapter = new ChatRoomThreadAdapter(UserChatActivity.this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onCommoStarted() {

    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> userProfileResponse) {
        userProfileResponse.observe(UserChatActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {

            }
        });

    }

    @Override
    public void onCommonFailure(String message) {

    }

    @Override
    public void onSuccessCreateGroup(LiveData<CreateGRoupREsponse> gRoupREsponse) {
        gRoupREsponse.observe(UserChatActivity.this, new Observer<CreateGRoupREsponse>() {
            @Override
            public void onChanged(CreateGRoupREsponse gRoupREsponse) {
                if (gRoupREsponse.getSuccess()){
                    group_id=gRoupREsponse.getData().get(0).getGroupId();
                    getMessage();
                }
            }
        });
    }
}