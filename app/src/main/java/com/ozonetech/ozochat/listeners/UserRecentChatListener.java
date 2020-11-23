package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.viewmodel.GroupDetailModel;
import com.ozonetech.ozochat.viewmodel.UserChatListModel;

public interface UserRecentChatListener {
    void onUserRecentChatSuccess(LiveData<UserChatListModel> userChatListResponse);

}
