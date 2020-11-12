package com.ozonetech.ozochat.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.FragmentChatsBinding;
import com.ozonetech.ozochat.listeners.UserRecentChatListener;
import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.adapter.ChatRoomsAdapter;
import com.ozonetech.ozochat.viewmodel.Contacts;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;

import static android.os.Looper.getMainLooper;


public class ChatsFragment extends BaseFragment implements UserRecentChatListener {

    FragmentChatsBinding dataBinding;
    private ChatRoomsAdapter mAdapter;
    private String tag = "ChatsFragment";
    UserChatListModel userChatListModel;
    MyPreferenceManager myPreferenceManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        userChatListModel = ViewModelProviders.of(ChatsFragment.this).get(UserChatListModel.class);
        myPreferenceManager = new MyPreferenceManager(getActivity());
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);

        return view;
    }

    private void renderUserChatList() {
        Map<String, String> chatMap = new HashMap<>();
//        chatMap.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        // chatMap.put("sender_id", "103");

        chatMap.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        showProgressDialog("Please wait...");
        userChatListModel.getUserResentChat(getActivity(), userChatListModel.userRecentChatListener = this, chatMap);
    }

    private void setRecyclerView(ArrayList<ChatRoom> chatRoomList) {

        //            "uid": 103,
        //            "admin_id": 100,
        //            "group_id": "GP1604989176399",
        //            "oneToOne": 0,
        //            "group_name": "teamgrp4",
        //            "username": "rahul",
        //            "message": "",
        //            "profile_image": "http://3.0.49.131/api/uploads/null",
        //            "title": "You Added 8669605501",
        //            "mobile": "7507828337"

        //            "uid": 103,
        //            "admin_id": 100,
        //            "group_id": "GP1604923790415",
        //            "oneToOne": 1,
        //            "group_name": "9922803527,8669605501",
        //            "username": "Amol",
        //            "profile_image": "http://3.0.49.131/api/uploads/null",
        //            "message": "",
        //            "title": "Hi there!",
        //            "mobile": "8669605501"


        ArrayList<Contacts> myContactsArrayList = new ArrayList<>();
        myContactsArrayList = myPreferenceManager.getArrayListContact("Contacts");
        Log.d(tag, "---myContactsArrayList : " + myContactsArrayList);

        for (int i = 0; i < chatRoomList.size(); i++) {

            if (chatRoomList.get(i).getOneToOne() == 1) {
                String contactMobileNo = chatRoomList.get(i).getMobile();

                boolean flag = true;
                for (int j = 0; j < myContactsArrayList.size(); j++) {

                    String myContactMobileNo = myContactsArrayList.get(j).getPhone();
                    if (myContactMobileNo.equalsIgnoreCase(contactMobileNo)) {
                        String myContactName = myContactsArrayList.get(j).getName();
                        chatRoomList.get(i).setUsername(myContactName);
                        flag = true;
                        break;
                    } else {
                        flag = false;
                    }
                }
                if (!flag) {
                    chatRoomList.get(i).setUsername(contactMobileNo);
                }

            } else if (chatRoomList.get(i).getOneToOne() == 0) {
                chatRoomList.get(i).setUsername(chatRoomList.get(i).getGroupName());
            }
        }



        mAdapter = new ChatRoomsAdapter(getActivity(), chatRoomList);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
        dataBinding.recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), dataBinding.recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                Log.d(tag, "---chat user click--");
                ChatRoom chatRoom = chatRoomList.get(position);
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getGroupId());
                intent.putExtra("name", chatRoom.getUsername());
                intent.putExtra("profilePic", chatRoom.getProfilePicture());
                intent.putExtra("mobileNo", chatRoom.getMobile());
                intent.putExtra("admin_id", chatRoom.getAdminId());
                intent.putExtra("status", "Online");
                intent.putExtra("flag", "user");
                intent.putExtra("activityFrom", "MainActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }


    @Override
    public void onResume() {
        super.onResume();
       /* myPreferenceManager = new MyPreferenceManager(getContext());
        Log.d(tag, "=====token--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        Log.d(tag, "=====id--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        if (MyApplication.getInstance().iSocket.connected()) {
          //  getMessage();
        } else {
            Log.d(tag, "=====not connected--");
        }
        chatViewModel.getchat(getContext(),myPreferenceManager.getUserId());*/
        //  renderUserChatList();
        getrecentChat();
    }

    private void getrecentChat() {
        JSONObject json = new JSONObject();
        try {
            json.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
            MyApplication.getInstance().getSocket().emit("recentChatEvent", json).on("recentChatEvent", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(tag, "--getMessage -data-array-" + args[0]);
                    JSONObject data = (JSONObject) args[0];
                    // final JSONArray result = (JSONArray) args[0];
                    new Handler(getMainLooper())
                            .post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(tag, "--recent  -data-array-" + data.toString());
                                            try {
                                                setList(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            );

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setList(JSONObject data) throws JSONException {
        if (data.getInt("status") == 200 && data.getInt("success") == 1) {
            ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
            JSONArray jsonArray = data.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setGroupId(jsonObject.getString("group_id"));
                Log.d(tag, "--group id-" + jsonObject.getString("group_id"));
                if (jsonObject.has("profile_image")) {
                    chatRoom.setProfilePicture(jsonObject.getString("profile_image"));
                } else {
                    chatRoom.setProfilePicture("");
                }
                chatRoom.setGroupName(jsonObject.getString("group_name"));
                chatRoom.setUsername(jsonObject.getString("group_name"));
                chatRoom.setAdminId(jsonObject.getInt("admin_id"));

                chatRoom.setMobile(jsonObject.getString("mobile"));
                chatRoom.setOneToOne(jsonObject.getInt("oneToOne"));

                chatRoom.setLastMessage("lastMessage ");
                chatRoom.setTimestamp("12 : 00 pm");
                chatRoom.setUnreadCount(i + 1);
                chatRoomList.add(chatRoom);
            }
            if (chatRoomList.size() != 0) {
                setRecyclerView(chatRoomList);
                dataBinding.llStartChat.setVisibility(View.GONE);
            } else {
                dataBinding.llStartChat.setVisibility(View.VISIBLE);
            }
        } else {
            dataBinding.llStartChat.setVisibility(View.VISIBLE);
            Log.d(tag, "----no recent--");
        }
    }

    @Override
    public void onUserRecentChatSuccess(LiveData<UserChatListModel> userChatListResponse) {
        userChatListResponse.observe(getViewLifecycleOwner(), new Observer<UserChatListModel>() {
            @Override
            public void onChanged(UserChatListModel userChatListModel) {
                hideProgressDialog();
                try {
                    if (userChatListModel.getSuccess()) {
                        Log.d("ChatFragment", "--Response : Code" + userChatListModel.getChatRoom());

                        if (userChatListModel.getChatRoom() != null) {
                            ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
                            chatRoomList = (ArrayList<ChatRoom>) userChatListModel.getChatRoom();
//                            for (int i = 1; i < chatRoomList.size(); i++) {
////                                ChatRoom chatRoom = new ChatRoom();
////                                chatRoom.setGroupId(chatRoomList.get(i).getGroupId());
////                                chatRoom.setProfilePicture(chatRoom.getProfilePicture());
////                                chatRoom.setUsername(chatRoomList.get(i).getUsername());
////                                chatRoom.setLastMessage("lastMessage " + String.valueOf(i));
////                                chatRoom.setTimestamp("12 : 00 pm");
////                                chatRoom.setUnreadCount(i + 1);
////                                chatRoomList.add(chatRoom);
////                            }
                            if (chatRoomList.size() != 0) {
                                setRecyclerView(chatRoomList);
                                dataBinding.llStartChat.setVisibility(View.GONE);
                            } else {
                                dataBinding.llStartChat.setVisibility(View.VISIBLE);
                            }
                        } else {
                            dataBinding.llStartChat.setVisibility(View.VISIBLE);
                        }


                    } else {
                        showSnackbar(dataBinding.flChat, userChatListModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }
}