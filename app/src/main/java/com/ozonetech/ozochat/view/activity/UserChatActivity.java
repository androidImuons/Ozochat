package com.ozonetech.ozochat.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.ozonetech.ozochat.model.DataObject;
import com.ozonetech.ozochat.network.DbService;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.database.ChatDatabase;
import com.ozonetech.ozochat.databinding.ActivityUserChatBinding;
import com.ozonetech.ozochat.databinding.ToolbarConversationBinding;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.listeners.UploadFilsListner;
import com.ozonetech.ozochat.model.AddMemberResponseModel;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.LeftResponseModel;
import com.ozonetech.ozochat.model.Message;
import com.ozonetech.ozochat.model.UploadFilesResponse;
import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.network.SoketService;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.adapter.ChatRoomThreadAdapter;
import com.ozonetech.ozochat.view.dialog.MoreOptionDialog;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatViewModel;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UserChatActivity extends BaseActivity implements CommonResponseInterface,
        CreateGroupInterface, ChatRoomThreadAdapter.onMessageContactClick, ContactsListener, UploadFilsListner {
    private static final String TAG = UserChatActivity.class.getName();
    private static final int IMAGE_PICKER_SELECT = 1;
    private static final int VIDEO_PICKER_SELECT = 2;
    private static final int AUDIO_PICKER_SELECT = 3;
    private static final int CAMERA_PIC_REQUEST = 4;
    private static final int REQUEST_RECORDING = 5;
    private static final int FILE_PICKER_SELECT = 6;
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
    private String userStatus;
    private ChatDatabase chatDatabase;
    String getGroup_id;
    private ArrayList<MediaFile> files;


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
        userStatus = intent.getStringExtra("userStatus");
        //userStatus = "left";
        if (userStatus.equalsIgnoreCase("Active")) {
            dataBinding.llBottom.setVisibility(View.VISIBLE);
            dataBinding.txtLeft.setVisibility(View.GONE);
        } else {
            dataBinding.llBottom.setVisibility(View.GONE);
            dataBinding.txtLeft.setVisibility(View.VISIBLE);
        }

        if (activityFrom.equalsIgnoreCase("MainActivity")) {
            admin_id = intent.getIntExtra("admin_id", 0);
            chatRoomId = intent.getStringExtra("chat_room_id");
            group_id = intent.getStringExtra("chat_room_id");
            groupChat = intent.getIntExtra("oneToOne", 2);
            if (groupChat == 0) {
                contactProfilePic = intent.getStringExtra("group_image");
            } else if (groupChat == 1) {
                contactProfilePic = intent.getStringExtra("profilePic");
            }
            String timeStamp = intent.getStringExtra("last_seen");
            last_seen = "last seen " + getTimeStampFormat(timeStamp);
        } else {
            last_seen = "";
            groupChat = intent.getIntExtra("oneToOne", 2);
            if (start_flag.equals("group")) {
                admin_id = intent.getIntExtra("admin_id", 0);
                group_id = intent.getStringExtra("chat_room_id");
                contactProfilePic = intent.getStringExtra("group_image");
                contactMobileNo = "";
                contactStatus = "";
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
        chatDatabase = ChatDatabase.getInstance(this);
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
        messageArrayList = new ArrayList<>();
        toolbarDataBinding.actionBarTitle1.setText(contactName);
        toolbarDataBinding.actionBarTitle2.setText(last_seen);
        Glide.with(this)
                .load(contactProfilePic)
                .placeholder(R.drawable.person_icon)
                .into(toolbarDataBinding.conversationContactPhoto);
        toolbarDataBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessage();
                mAdapter.clearMediaPlayer();
                finish();
            }
        });


        toolbarDataBinding.rlToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserChatActivity.this, DetailViewUpdateActivity.class);
                intent.putExtra("contactName", contactName);
                intent.putExtra("last_seen", last_seen);
                intent.putExtra("contactProfilePic", contactProfilePic);
                intent.putExtra("groupChat", groupChat);
                intent.putExtra("group_id", group_id);
                intent.putExtra("admin_id", admin_id);
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
//                Intent intent=new Intent(UserChatActivity.this,UploadStatus.class);
//                startActivity(intent);
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
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
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
        dataBinding.recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);

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

        final String message1 = dataBinding.message.getText().toString().trim();

        if (TextUtils.isEmpty(message1)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (MyApplication.getInstance().iSocket.connected()) {
                Log.d(tag, "-----is connectttd---->" + MyApplication.getInstance().iSocket.id());
                triggerSendMessage(dataBinding.message.getText().toString());
            } else {
                is_call = false;
                Log.d(tag, "-----not connectttd");
                Date date = new Date();
                long unixTime = date.getTime() / 1000L;
                Message message = new Message();
                int index = 0;
                List<Message> messageArrayList = chatDatabase.chatMessageDao().getAll(group_id);
                if (messageArrayList.size() != 0) {
                    index = messageArrayList.get(messageArrayList.size() - 1).getId();
                }
                Log.d(tag, "unsaved message get id" + index);
                message.setId(index + 1);
                message.setUserId(admin_id);
                message.setSender_id(prefManager.getUserId());
                message.setGroupId(group_id);
                message.setMessage(dataBinding.message.getText().toString());
                message.setCreated(getTimeStampFormat(String.valueOf(unixTime)));
                message.setSender_mobile(prefManager.getUserDetails().get(prefManager.KEY_USER_MOBILE));
                message.setSender_name(prefManager.getUserDetails().get(prefManager.KEY_USER_NAME));
                message.setType("txt");
                message.setStatus(false);
                chatDatabase.getInstance(this).chatMessageDao().insert(message);
                dataBinding.message.setText("");
                getAllLocalData();

            }
        }
    }

    private void triggerSendMessage(String s) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sender_id", MyApplication.getInstance().getPrefManager().getUserId());
            jsonObject.put("user_id", admin_id);
            jsonObject.put("group_id", group_id);
            jsonObject.put("message", s);
            Log.d(tag, "---send message parameter-- user_id :" + jsonObject);
            if (MyApplication.getInstance().iSocket.connected()) {
                Log.d(tag, "-----is connectttd");
            } else {
                Log.d(tag, "-----not connectttd");
            }


            MyApplication.getInstance().getSocket().emit("sendMessage", jsonObject).on("sendMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "----send message--433--" + args[0]);
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
            json.put("user_id", prefManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
            Log.d(tag, "---getMessages " + json);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setRecyclerView(JSONArray data) {
        String newGroupId = addChar(group_id, 0);
        newGroupId = addChar(newGroupId, newGroupId.length());

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "DELETE FROM message where group_id = " + newGroupId);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // int count=  chatDatabase.getInstance(UserChatActivity.this).chatMessageDao().deleteGroupChat(query);
                    //  Log.d(tag,"----total number item delete--"+count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        messageArrayList = new ArrayList<>();
        for (int i = data.length() - 1; i >= 0; i--) {
            try {
                JSONObject messageObj = data.getJSONObject(i);
                if (!messageObj.getString("message").equals("")) {
                    Message message = new Message();
                    message.setId(messageObj.getInt("id"));
                    message.setUserId(messageObj.getInt("user_id"));
                    message.setSender_id(String.valueOf(messageObj.getInt("sender_id")));
                    message.setGroupId(messageObj.getString("group_id"));
                    message.setMessage(messageObj.getString("message"));
                    message.setCreated(messageObj.getString("created"));
                    message.setSender_mobile(messageObj.getString("sender_mobile"));
                    message.setSender_name(messageObj.getString("sender_name"));

                    if (messageObj.has("file")) {
                        message.setFile(messageObj.getString("file"));
                    } else {
                        message.setFile(null);
                    }
                    message.setStatus(true);
                    //  chatDatabase.getInstance(this).chatMessageDao().insert(message);
                    messageArrayList.add(insertOrUpdate(message));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getAllLocalData();
        // showData();
    }

    private Message insertOrUpdate(Message item) {
        List<Message> itemsFromDB = chatDatabase.chatMessageDao().getItemById(item.getId());
        // chatDatabase.chatMessageDao().insert(item);

        if (itemsFromDB.isEmpty()) {
            chatDatabase.chatMessageDao().insert(item);
        } else {
            chatDatabase.chatMessageDao().updated_time(item.getCreated(), item.getId());
        }

        return item;
    }


    private void showData() {
        String selfUserId = MyApplication.getInstance().getPrefManager().getUserId();
        mAdapter = new ChatRoomThreadAdapter(UserChatActivity.this, messageArrayList, selfUserId);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(UserChatActivity.this, LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
        dataBinding.recyclerView.scrollToPosition(messageArrayList.size() - 1);
    }

    private void getAllLocalData() {
        String newGroupId = addChar(group_id, 0);
        newGroupId = addChar(newGroupId, newGroupId.length());

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT * FROM message where group_id = " + newGroupId);
        if (chatDatabase.getInstance(UserChatActivity.this).chatMessageDao().getGroupList(query).size() != 0) {
            messageArrayList =
                    new ArrayList<Message>(chatDatabase.getInstance(UserChatActivity.this).chatMessageDao().getGroupList(query));
            Log.d(tag, "----local db message--" + messageArrayList.size());
        }
        showData();


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        //showData();

    }

    public String addChar(String str, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, "'");
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getMessage();
        Intent intent = new Intent(UserChatActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        mAdapter.clearMediaPlayer();
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
                    Log.d(tag, "-----on success create group" + gRoupREsponse.getData().get(0));
                    group_id = gRoupREsponse.getData().get(0).getGroupId();
                    admin_id = gRoupREsponse.getData().get(0).getAdminUserId();
                    getMessage();
                }

            }
        });
    }

    @Override
    public void onSuccessLeftGroup(LiveData<LeftResponseModel> leftGroupResponse) {

    }

    @Override
    public void onSuccessGroupDetails(LiveData<GroupDetailModel> groupDetailResponase) {
    }

    @Override
    public void onSuccessAddToGroup(LiveData<AddMemberResponseModel> addMemberResponse) {

    }

    @Override
    public void onSuccessRemoveMember(LiveData<LeftResponseModel> removeMemberResponse) {

    }

    @Override
    public void onSuccessDeleteGroup(LiveData<LeftResponseModel> deleteGroupResponse) {

    }

    PopupWindow popupWindow;

    private void openDialog() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_dialog_options_menu, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        LinearLayout layoutGallery = popupView.findViewById(R.id.layoutGallery);
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .enableImageCapture(true)
                        .setShowVideos(true)
                        .setMaxSelection(10)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, IMAGE_PICKER_SELECT);

            }
        });

        LinearLayout layout_files = popupView.findViewById(R.id.layout_file);
        layout_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_file = new Intent(getApplicationContext(), FilePickerActivity.class);
                intent_file.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .enableImageCapture(false)
                        .setShowVideos(false)
                        .setShowFiles(true)
                        .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                                "zip", "tar", "gz", "rar", "7z", "torrent",
                                "doc", "docx", "odt", "ott",
                                "ppt", "pptx", "pps",
                                "xls", "xlsx", "ods", "ots")
                        .setMaxSelection(10)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent_file, FILE_PICKER_SELECT);

            }
        });

        LinearLayout layoutAudio = popupView.findViewById(R.id.layoutAudio);
        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_file = new Intent(getApplicationContext(), FilePickerActivity.class);
                intent_file.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .enableImageCapture(false)
                        .setShowVideos(false)
                        .setShowFiles(false)
                        .setShowAudios(true)
                        .setMaxSelection(10)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent_file, AUDIO_PICKER_SELECT);

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

            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            Contacts contactsViewModel = new Contacts();
            contactsViewModel.uploadFilsListner = (UploadFilsListner) UserChatActivity.this;
            files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            //Do something with files
            contactsViewModel.uploadfiles(UserChatActivity.this, contactsViewModel.contactsListener = UserChatActivity.this, prefManager.getUserId(), group_id, String.valueOf(admin_id), files);

//            for (int i=0;i<files.size();i++){
//                Log.d(tag, "----dummay data--");
//                Date date = new Date();
//                long unixTime = date.getTime() / 1000L;
//                Message message = new Message();
//                int index = 0;
//                List<Message> messageArrayList = chatDatabase.chatMessageDao().getAll(group_id);
//                if (messageArrayList.size() != 0) {
//                    index = messageArrayList.get(messageArrayList.size() - 1).getId();
//                }
//                Log.d(tag, "unsaved message get id" + index);
//                message.setId(index + 1);
//                message.setUserId(admin_id);
//                message.setSender_id(prefManager.getUserId());
//                message.setGroupId(group_id);
//                message.setMessage(null);
//                message.setCreated("");
//                message.setSender_mobile(prefManager.getUserDetails().get(prefManager.KEY_USER_MOBILE));
//                message.setSender_name(prefManager.getUserDetails().get(prefManager.KEY_USER_NAME));
//                message.setFile(files.get(i).getPath());
//                message.setStorageFile(files.get(i).getPath());
//                message.setType(files.get(i).getMimeType());
//                message.setStatus(false);
//               // this.messageArrayList.add(message);
//            }
            //showData();


//            switch (requestCode) {
//                case IMAGE_PICKER_SELECT:
//                    contactsViewModel.uploadfiles(UserChatActivity.this, contactsViewModel.contactsListener = UserChatActivity.this, prefManager.getUserId(), group_id, String.valueOf(admin_id), files);
//                    break;
//                case FILE_PICKER_SELECT:
//                    contactsViewModel.uploadfiles(UserChatActivity.this, contactsViewModel.contactsListener = UserChatActivity.this, prefManager.getUserId(), group_id, String.valueOf(admin_id), files);
//                    break;
//                case AUDIO_PICKER_SELECT:
//                    contactsViewModel.uploadfiles(UserChatActivity.this, contactsViewModel.contactsListener = UserChatActivity.this, prefManager.getUserId(), group_id, String.valueOf(admin_id), files);
//                    break;
//            }

        }
    }

    public String getRealPathFromURI(Uri uri) {

        Log.d(tag, "---" + uri);
        Bitmap bitmap = null;
        Uri outPutfileUri = null;
        try {
            if (uri.toString().contains("image")) {
                //handle image
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "attachment", null);
                outPutfileUri = Uri.parse(url);
            } else if (uri.toString().contains("video")) {
                outPutfileUri = uri;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outPutfileUri.toString();
    }

    boolean is_call;

    @Override
    public void onSocketConnect(boolean flag) {
        super.onSocketConnect(flag);
        if (flag) {
            Log.d(tag, "----- connect socket- yes-");
            if (start_flag.equals("group")) {
                getAllLocalData();
                getMessage();
            } else {
                Intent intent = getIntent();
                if (intent.hasExtra("admin_id")) {
                    getAllLocalData();
                    getMessage();
                } else {
                    checkGroup();
                }
            }
            if (is_call) {
                Intent intent = new Intent(UserChatActivity.this, DbService.class);
                startService(intent);
            }
            is_call = false;


        } else {
            Log.d(tag, "-----re connect socket--");
            SoketService.instance.connectConnection();
            Log.d(tag, "---get local data--");
            if (!is_call) {
                getAllLocalData();
                is_call = true;
            }
        }
    }


    @Override
    public void onContactClick(Message message) {

        Bundle bundle = new Bundle();
        bundle.putString("name", message.getSender_name());
        bundle.putString("mobile", message.getSender_mobile());
        bundle.putString("group", message.getGroupId());
        bundle.putString("u_id", String.valueOf(message.getUserId()));
        bundle.putBoolean("is_contact", message.isIs_contact());
        MoreOptionDialog instance = MoreOptionDialog.getInstance(bundle);

        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());

    }

    @Override
    public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse) {

    }

    @Override
    public void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse) {

    }

    @Override
    public void onGroupImgUploadSuccess(LiveData<UploadResponse> uploadGroupImgResponse) {

    }

    @Override
    public void onSuccessResponse(LiveData<UploadFilesResponse> uploadGroupImgResponse) {
        uploadGroupImgResponse.observe(UserChatActivity.this, new Observer<UploadFilesResponse>() {
            @Override
            public void onChanged(UploadFilesResponse uploadFilesResponse) {
                if (uploadFilesResponse.getSuccess()) {
                    StoreImage(uploadFilesResponse.getDataList());

                } else {
                    Log.d(tag, "---image upload fail--" + uploadFilesResponse.getMessage());
                }
            }
        });
    }

    private void StoreImage(List<DataObject> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            Date date = new Date();
            long unixTime = date.getTime() / 1000L;
            Log.d(tag, "-947--" + files.get(i).getThumbnail());
            Log.d(tag, "-getmimi--" + files.get(i).getMimeType());
            Log.d(tag, "-getmimi--" + files.get(i).getMediaType());
            Log.d(tag, "-thb--" + files.get(i).getName());
            Message message = new Message();

            message.setId(dataList.get(i).getMsg_id());
            message.setUserId(admin_id);
            message.setSender_id(prefManager.getUserId());
            message.setGroupId(group_id);
            message.setMessage(null);
            message.setCreated(getTimeStampFormat(String.valueOf(unixTime)));
            message.setSender_mobile(prefManager.getUserDetails().get(prefManager.KEY_USER_MOBILE));
            message.setSender_name(prefManager.getUserDetails().get(prefManager.KEY_USER_NAME));
            message.setFile(dataList.get(i).getUploadedFileUrl());
            message.setStorageFile(files.get(i).getPath());
            message.setType(files.get(i).getMimeType());
            message.setStatus(true);
            ChatDatabase.getInstance(getApplicationContext()).chatMessageDao().insert(message);
        }
        getMessage();
    }


}