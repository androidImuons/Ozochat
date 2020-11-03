package com.ozonetech.ozochat.listeners;

import androidx.lifecycle.LiveData;

import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.viewmodel.VerifiedContactsModel;

public interface ContactsListener {
   public void onGetContactsSuccess(LiveData<VerifiedContactsModel> verifiedContactsResponse);


    void onCreateGroupSuccess(LiveData<CreateGRoupREsponse> createGroupResponse);
}
