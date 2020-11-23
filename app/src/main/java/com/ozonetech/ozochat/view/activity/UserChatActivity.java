package com.ozonetech.ozochat.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.devlomi.record_view.OnBasketAnimationEnd;
//import com.devlomi.record_view.OnRecordClickListener;
//import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
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
import com.ozonetech.ozochat.network.SoketService;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.ChatRoomThreadAdapter;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserChatActivity extends BaseActivity implements CommonResponseInterface, CreateGroupInterface,ChatRoomThreadAdapter.onMessageContactClick {
    private static final String TAG = UserChatActivity.class.getName();
    private static final int IMAGE_PICKER_SELECT = 1;
    private static final int VIDEO_PICKER_SELECT = 2;
    private static final int AUDIO_PICKER_SELECT = 3;
    private static final int CAMERA_PIC_REQUEST = 4;
    private static final int REQUEST_RECORDING = 5;
    private static String today;
    MyPreferenceManager prefManager;
    ActivityUserChatBinding dataBinding;
    ToolbarConversationBinding toolbarDataBinding;
   public int groupChat;
    String contactName;
    String contactMobileNo;
    String contactStatus;
    String contactProfilePic;
    String chatRoomId;
    String last_seen;
    private ArrayList<Message> messageArrayList;
    private ChatRoomThreadAdapter mAdapter;
    private String tag = "UserChatActivity";
    private Socket mSocket;
    UserChatViewModel chatViewModel;
    MyPreferenceManager myPreferenceManager;
    private String group_id;
    private int admin_id;
    private String start_flag;
    private String activityFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(UserChatActivity.this, R.layout.activity_user_chat);
        toolbarDataBinding = dataBinding.toolbarLayout;
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        prefManager = new MyPreferenceManager(UserChatActivity.this);
        chatViewModel = ViewModelProviders.of(UserChatActivity.this).get(UserChatViewModel.class);
        dataBinding.setUserChat(chatViewModel);
        chatViewModel.callback = (CommonResponseInterface) this;
        chatViewModel.groupInterface = (CreateGroupInterface) this;
        Intent intent = getIntent();
        activityFrom = intent.getStringExtra("activityFrom");
        start_flag = intent.getStringExtra("flag");
        contactName = intent.getStringExtra("name");

        if(activityFrom.equalsIgnoreCase("MainActivity")){
            admin_id = intent.getIntExtra("admin_id", 0);
            chatRoomId = intent.getStringExtra("chat_room_id");
            group_id = intent.getStringExtra("chat_room_id");
            groupChat= intent.getIntExtra("oneToOne",2);
            if(groupChat==0){
                contactProfilePic=intent.getStringExtra("group_image");
            }else if(groupChat == 1){
                contactProfilePic=intent.getStringExtra("profilePic");
            }
            String timeStamp=intent.getStringExtra("last_seen");
            last_seen="last seen "+getTimeStampFormat(timeStamp);
        }else {
            last_seen="";
            groupChat= intent.getIntExtra("oneToOne",2);
            if (start_flag.equals("group")) {
                admin_id = intent.getIntExtra("admin_id", 0);
                group_id = intent.getStringExtra("chat_room_id");
                contactMobileNo = "";
                contactStatus = "";
                contactProfilePic = "";
                chatRoomId = "";
            } else {
                chatRoomId = intent.getStringExtra("chat_room_id");
                group_id = intent.getStringExtra("chat_room_id");
                contactMobileNo = intent.getStringExtra("mobileNo");
                contactStatus = intent.getStringExtra("status");
                contactProfilePic = intent.getStringExtra("profilePic");
                if (intent.hasExtra("admin_id")) {
                    admin_id = intent.getIntExtra("admin_id", 0);
                }
            }
        }
        init();
    }

    public static String getTimeStampFormat(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
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
        toolbarDataBinding.actionBarTitle2.setText(last_seen);
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

        toolbarDataBinding.rlToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(UserChatActivity.this,DetailViewUpdateActivity.class);
                intent.putExtra("contactName",contactName);
                intent.putExtra("last_seen",last_seen);
                intent.putExtra("contactProfilePic",contactProfilePic);
                intent.putExtra("groupChat",groupChat);
                intent.putExtra("admin_id",admin_id);
                startActivity(intent);
            }
        });

        dataBinding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        dataBinding.ivAttachAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        dataBinding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        dataBinding.ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, REQUEST_RECORDING);
            }
        });

        dataBinding.recordButton.setRecordView(dataBinding.recordView);
        dataBinding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
               // String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");

                Log.d("RecordTime", String.valueOf(recordTime));
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        dataBinding.recordButton.setListenForRecord(false);

        //ListenForRecord must be false ,otherwise onClick will not be called
        dataBinding.recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserChatActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton","RECORD BUTTON CLICKED");
            }
        });

        dataBinding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

        dataBinding.recordView.setCancelBounds(8);//dp
        dataBinding.recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        dataBinding.recordView.setSlideToCancelText("TEXT");

        //disable Sounds
        dataBinding.recordView.setSoundEnabled(false);

        //prevent recording under one Second (it's false by default)
        dataBinding.recordView.setLessThanSecondAllowed(false);

        //set Custom sounds onRecord
        //you can pass 0 if you don't want to play sound in certain state
        dataBinding.recordView.setCustomSounds(R.raw.record_start,R.raw.record_finished,0);

        //change slide To Cancel Text Color
        dataBinding.recordView.setSlideToCancelTextColor(Color.parseColor("#ff0000"));
        //change slide To Cancel Arrow Color
        dataBinding.recordView.setSlideToCancelArrowColor(Color.parseColor("#ff0000"));
        //change Counter Time (Chronometer) color
        dataBinding.recordView.setCounterTimeColor(Color.parseColor("#ff0000"));

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (start_flag.equals("group")) {
            getMessage();
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra("admin_id")) {
                getMessage();
            } else {
                checkGroup();
            }
        }
    }

    private void sendMessage() {
        if (MyApplication.getInstance().iSocket.connected()) {
            Log.d(tag, "-----is connectttd---->"+MyApplication.getInstance().iSocket.id());
        } else {
            Log.d(tag, "-----not connectttd");
        }
        final String message = dataBinding.message.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }else{
           triggerSendMessage();
        }
    }

    private void triggerSendMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sender_id", MyApplication.getInstance().getPrefManager().getUserId());
            jsonObject.put("user_id", admin_id);
            jsonObject.put("group_id", group_id);
            jsonObject.put("message", dataBinding.message.getText().toString());
            Log.d(tag, "---send message parameter-- user_id :" + jsonObject);
            if (MyApplication.getInstance().iSocket.connected()) {
                Log.d(tag, "-----is connectttd");
            } else {
                Log.d(tag, "-----not connectttd");
            }
            MyApplication.getInstance().getSocket().emit("sendMessage", jsonObject).on("sendMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "---send message--" + args[0]);
                    getMessage();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataBinding.message.setText("");

    }

    private void getMessage() {
        if (MyApplication.getInstance().iSocket.connected()) {
            Log.d(tag, "-----is connectttd");
        } else {
            Log.d(tag, "-----not connectttd");
        }
        Log.d(tag, "--getMessage called");

        JSONObject json = new JSONObject();
        try {
            json.put("group_id", group_id);
            Log.d(tag, "---get message para  group_id : " + group_id);
            MyApplication.getInstance().getSocket().emit("getMessages", json).on("getMessages", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray data = (JSONArray) args[0];
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
        for (int i = data.length() - 1; i >= 0; i--) {
            try {
                JSONObject messageObj = data.getJSONObject(i);
                if (!messageObj.getString("message").equals("")){
                    Message message = new Message();
                    message.setId(messageObj.getInt("id"));
                    message.setUserId(messageObj.getInt("sender_id"));
                    message.setGroupId(messageObj.getString("group_id"));
                    message.setMessage(messageObj.getString("message"));
                    message.setCreated(messageObj.getString("created"));
                    message.setSender_mobile(messageObj.getString("sender_mobile"));
                    message.setSender_name(messageObj.getString("sender_name"));
                    messageArrayList.add(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String selfUserId = MyApplication.getInstance().getPrefManager().getUserId();
        mAdapter = new ChatRoomThreadAdapter(UserChatActivity.this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
        dataBinding.recyclerView.scrollToPosition(messageArrayList.size() - 1);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserChatActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
                if (gRoupREsponse == null) {
                    finish();
                }
                if (gRoupREsponse.getSuccess().equals(null)) {
                    finish();
                }
                if (gRoupREsponse.getSuccess()) {
                    group_id = gRoupREsponse.getData().get(0).getGroupId();
                    admin_id = gRoupREsponse.getData().get(0).getAdmin_user_id();
                    //triggerSendMessage();
                    getMessage();
                }

            }
        });
    }

    @Override
    public void onSuccessLeftGroup(LiveData<CommonResponse> leftGroupResponse) {

    }


    private void openDialog() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_dialog_options_menu, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        LinearLayout layoutGallery = popupView.findViewById(R.id.layoutGallery);
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
            }
        });

        LinearLayout layoutVideo = popupView.findViewById(R.id.layoutVideo);
        layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, VIDEO_PICKER_SELECT);
            }
        });

        LinearLayout layoutAudio = popupView.findViewById(R.id.layoutAudio);
        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, AUDIO_PICKER_SELECT);
            }
        });
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(dataBinding.rlUserChat, Gravity.BOTTOM, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();
            if (requestCode == IMAGE_PICKER_SELECT) {
                Toast.makeText(UserChatActivity.this, "Coming Soon ! ", Toast.LENGTH_LONG).show();
            } else if (requestCode == VIDEO_PICKER_SELECT) {
                Toast.makeText(UserChatActivity.this, "Coming Soon ! ", Toast.LENGTH_LONG).show();
            } else if (requestCode == AUDIO_PICKER_SELECT) {
                Toast.makeText(UserChatActivity.this, "Coming Soon ! ", Toast.LENGTH_LONG).show();
            } else if (requestCode == CAMERA_PIC_REQUEST) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                // dataBinding.ivPic.setImageBitmap(image);
            } else if (requestCode == REQUEST_RECORDING) {
                Uri savedUri = data.getData();
                Toast.makeText(UserChatActivity.this,
                        "Saved: " + savedUri.getPath(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSocketConnect(boolean flag) {
        super.onSocketConnect(flag);
        if (flag){
            Log.d(tag,"----- connect socket- yes-");
            if (start_flag.equals("group")) {
                getMessage();
            } else {
                Intent intent = getIntent();
                if (intent.hasExtra("admin_id")) {
                    getMessage();
                } else {
                    checkGroup();
                }
            }

        }else{
            Log.d(tag,"-----re connect socket--");
            SoketService.instance.connectConnection();
        }
    }


    @Override
    public void onContactClick(Message message) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent
                .putExtra(ContactsContract.Intents.Insert.NAME, "Contact Name")
                .putExtra(ContactsContract.Intents.Insert.PHONE, "5555555555");

        startActivityForResult(contactIntent, 1);
    }
}