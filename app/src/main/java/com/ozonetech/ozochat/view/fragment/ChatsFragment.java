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
import com.ozonetech.ozochat.model.ChatRoom;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.adapter.ChatRoomsAdapter;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        userChatListModel= ViewModelProviders.of(ChatsFragment.this).get(UserChatListModel.class);
        myPreferenceManager = new MyPreferenceManager(getActivity());
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        renderUserChatList();
        return view;
    }

    private void renderUserChatList() {
        Map<String, String> chatMap = new HashMap<>();
        chatMap.put("sender_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        showProgressDialog("Please wait...");
        userChatListModel.getUserResentChat(getActivity(),userChatListModel.userRecentChatListener=this,chatMap);
    }

    private void setRecyclerView(ArrayList<ChatRoom> chatRoomList) {

        mAdapter = new ChatRoomsAdapter(getActivity(), chatRoomList);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        dataBinding.recyclerView.setAdapter(mAdapter);
        dataBinding.recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), dataBinding.recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomList.get(position);
                Intent intent = new Intent(getActivity(), UserChatActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getGroupId());
                intent.putExtra("name", chatRoom.getUsername());
                intent.putExtra("profilePic", chatRoom.getProfilePicture());
                intent.putExtra("mobileNo",chatRoom.getMobile());
                intent.putExtra("status","Online");
                intent.putExtra("flag","user");
                startActivity(intent);
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

    }

    private void getMessage() {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
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
    public void onUserRecentChatSuccess(LiveData<UserChatListModel> userChatListResponse) {
        userChatListResponse.observe(getViewLifecycleOwner(), new Observer<UserChatListModel>() {
            @Override
            public void onChanged(UserChatListModel userChatListModel) {
                hideProgressDialog();
                try {
                    if (userChatListModel.getSuccess()) {
                        Log.d("ChatFragment", "--Response : Code" + userChatListModel.getChatRoom());

                        if(userChatListModel.getChatRoom()!=null){
                            ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
                            chatRoomList= (ArrayList<ChatRoom>) userChatListModel.getChatRoom();
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
                        }else{
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