package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.databinding.ToolbarConversationBinding;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.model.User;
import com.ozonetech.ozochat.view.adapter.ChatRoomThreadAdapter;

import java.util.ArrayList;

public class UserChatActivity extends AppCompatActivity {

    ActivityUserChatBinding dataBinding;
    ToolbarConversationBinding toolbarDataBinding;
    String contactName;
    String contactMobileNo;
    String contactStatus;
    String contactProfilePic;
    String chatRoomId;
    private ArrayList<Message> messageArrayList;
    private ChatRoomThreadAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding= DataBindingUtil.setContentView(UserChatActivity.this,R.layout.activity_user_chat);
        toolbarDataBinding=dataBinding.toolbarLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);

        Intent intent=getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        contactName=intent.getStringExtra("name");
        contactMobileNo=intent.getStringExtra("mobileNo");
        contactStatus=intent.getStringExtra("status");
        contactProfilePic = intent.getStringExtra("profilePic");
        init();
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

        for(int i=0;i<3;i++){
            Message msg = new Message();
            msg.setId(String.valueOf(i));
            msg.setMessage("hii "+i);
            msg.setCreatedAt("04:0"+i);
            User user = new User(String.valueOf(2),
                    "RUCHITA",
                    "ruchita@123");
            msg.setUser(user);
            messageArrayList.add(msg);
        }
        Message msg = new Message();
        msg.setId("3");
        msg.setMessage("hii");
        msg.setCreatedAt("04:00");
        User user = new User
                (String.valueOf(1),
                "MAYURI",
                "mayuri@123");
        msg.setUser(user);
        messageArrayList.add(msg);


        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getUid().toString();
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
    }

    private void sendMessage() {
        final String message = dataBinding.message.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        dataBinding.message.setText("");



        //mAdapter.notifyDataSetChanged();
      /*  if (mAdapter.getItemCount() > 1) {
            dataBinding.recyclerView.getLayoutManager().smoothScrollToPosition(dataBinding.recyclerView, null, mAdapter.getItemCount() - 1);
        }*/
    }

}