package com.ozonetech.ozochat.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozonetech.ozochat.MyApplication;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.FragmentChatsBinding;
import com.ozonetech.ozochat.model.ChatRoom;
import com.ozonetech.ozochat.network.MyPreference;
import com.ozonetech.ozochat.utils.MyPreferenceManager;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.adapter.ChatRoomsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

import static android.os.Looper.getMainLooper;


public class ChatsFragment extends BaseFragment {

    FragmentChatsBinding dataBinding;
    private ChatRoomsAdapter mAdapter;
    private String tag = "ChatsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setId(String.valueOf(i));
            chatRoom.setProfilePicture("https://api.androidhive.info/images/nature/" + i + ".jpg");
            chatRoom.setName("name" + String.valueOf(i));
            chatRoom.setLastMessage("lastMessage " + String.valueOf(i));
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

        return view;
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
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                intent.putExtra("profilePic", chatRoom.getProfilePicture());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    MyPreferenceManager myPreferenceManager;

    @Override
    public void onResume() {
        super.onResume();
        myPreferenceManager = new MyPreferenceManager(getContext());
        Log.d(tag, "=====token--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_TOKEN));
        Log.d(tag, "=====id--"+myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_USER_ID));
        if (MyApplication.getInstance().iSocket.connected()) {
            Log.d(tag, "=====connected--");
            getMessage();
        } else {
            Log.d(tag, "=====not connected--");
        }

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

}