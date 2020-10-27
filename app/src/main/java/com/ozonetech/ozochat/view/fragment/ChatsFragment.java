package com.ozonetech.ozochat.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.databinding.FragmentChatsBinding;
import com.ozonetech.ozochat.model.ChatRoom;
import com.ozonetech.ozochat.view.activity.UserChatActivity;
import com.ozonetech.ozochat.view.adapter.ChatRoomsAdapter;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends BaseFragment {

    FragmentChatsBinding dataBinding;
    private ChatRoomsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_chats,container,false);
        View view=dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        ArrayList<ChatRoom> chatRoomList=new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setId(String.valueOf(i));
            chatRoom.setProfilePicture("https://api.androidhive.info/images/nature/" + i + ".jpg");
            chatRoom.setName("name"+String.valueOf(i));
            chatRoom.setLastMessage("lastMessage "+String.valueOf(i));
            chatRoom.setTimestamp("12 : 00 pm");
            chatRoom.setUnreadCount(i+1);

            chatRoomList.add(chatRoom);
        }
        if(chatRoomList.size() != 0){
            setRecyclerView(chatRoomList);
            dataBinding.llStartChat.setVisibility(View.GONE);
        }else{
            dataBinding.llStartChat.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setRecyclerView(ArrayList<ChatRoom> chatRoomList) {

        mAdapter=new ChatRoomsAdapter(getActivity(),chatRoomList);
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
                intent.putExtra("profilePic",chatRoom.getProfilePicture());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }
}