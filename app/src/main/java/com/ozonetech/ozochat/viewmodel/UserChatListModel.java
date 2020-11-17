package com.ozonetech.ozochat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.UserRecentChatListener;
import com.ozonetech.ozochat.database.entity.ChatRoom;
import com.ozonetech.ozochat.model.NumberListObject;
import com.ozonetech.ozochat.repository.RecentChatList;
import com.ozonetech.ozochat.repository.SelectContactRepository;

import java.util.List;
import java.util.Map;

public class UserChatListModel extends ViewModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ChatRoom> chatRoom = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChatRoom> getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(List<ChatRoom> chatRoom) {
        this.chatRoom = chatRoom;
    }



    public LiveData<UserChatListModel> userChatListResponse;
    public UserRecentChatListener userRecentChatListener;

    public void getUserResentChat(Context context,UserRecentChatListener userRecentChatListener,Map<String, String> chatMap) {

        if (userChatListResponse == null) {
            userChatListResponse = new MutableLiveData<UserChatListModel>();
            userChatListResponse = new RecentChatList().getUserResentChat(chatMap, context);
            userRecentChatListener.onUserRecentChatSuccess(userChatListResponse);
        } else {
            userChatListResponse = new RecentChatList().getUserResentChat(chatMap, context);
            userRecentChatListener.onUserRecentChatSuccess(userChatListResponse);
        }
    }

    public LiveData<VerifiedContactsModel> commonResponse;

    public ContactsListener contactsListener;


    public void sendContacts(Context context, ContactsListener contactsListener, NumberListObject arrayListAge) {

        if (commonResponse == null) {
            commonResponse = new MutableLiveData<VerifiedContactsModel>();
            //we will load it asynchronously from server in this method
            commonResponse = new SelectContactRepository().sendValidContacts(arrayListAge);
            contactsListener.onGetContactsSuccess(commonResponse);
        } else {
            commonResponse = new SelectContactRepository().sendValidContacts(arrayListAge);
            contactsListener.onGetContactsSuccess(commonResponse);
        }
    }

}
