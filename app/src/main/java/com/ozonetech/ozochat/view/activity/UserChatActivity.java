package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.databinding.ToolbarConversationBinding;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.model.User;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.ChatRoomThreadAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class UserChatActivity extends AppCompatActivity {
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
    private String tag="UserChatActivity";
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding= DataBindingUtil.setContentView(UserChatActivity.this,R.layout.activity_user_chat);
        toolbarDataBinding=dataBinding.toolbarLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        contactName = intent.getStringExtra("name");
        contactMobileNo = intent.getStringExtra("mobileNo");
        contactStatus = intent.getStringExtra("status");
        contactProfilePic = intent.getStringExtra("profilePic");
        init();
        getMessage();
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


     /*   messageArrayList = new ArrayList<>();

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
        User user = new User(String.valueOf(1),
                "MAYURI",
                "mayuri@123");
        msg.setUser(user);
        messageArrayList.add(msg);


        // self user id is to identify the message owner
        String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);*/
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

  /*  mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            dataBinding.recyclerView.getLayoutManager().smoothScrollToPosition(dataBinding.recyclerView, null, mAdapter.getItemCount() - 1);
        }*/
    }

  private void  getMessage(){
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

    /*private Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSocket == null) return;
                    mSocket.emit("getMessages", prefManager.getMessageJSON());

                }
            });
        }
    };*/

    /*private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UserChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    Log.d(TAG ,"---get message--" + data.toString());
                   *//* try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
*//*
                  //  addMessage(username, message);
                }
            });
        }
    };
*/

/*
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            UserChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        if(null!=mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };
*/

/*
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            UserChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
*/

/*
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            UserChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
*/

/*
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
*/
}