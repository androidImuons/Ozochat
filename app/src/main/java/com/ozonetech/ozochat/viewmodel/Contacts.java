package com.ozonetech.ozochat.viewmodel;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.listeners.CommonResponseInterface;
import com.ozonetech.ozochat.listeners.ContactsListener;
import com.ozonetech.ozochat.listeners.CreateGroupInterface;
import com.ozonetech.ozochat.model.CommonResponse;
import com.ozonetech.ozochat.model.CreateGRoupREsponse;
import com.ozonetech.ozochat.model.NumberListObject;

import com.ozonetech.ozochat.model.UploadResponse;
import com.ozonetech.ozochat.repository.CreateGroupRepository;
import com.ozonetech.ozochat.repository.SelectContactRepository;
import com.ozonetech.ozochat.repository.UploadFiless;
import com.ozonetech.ozochat.view.activity.SelectContactActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contacts extends ViewModel implements Serializable {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("imageUrl")
    @Expose
    private String profilePicture;
    @SerializedName("number")
    @Expose
    private String phone;
    String status;
    private Boolean isAdmin;

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @BindingAdapter({"profilePicture"})
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.person_icon)
                .into(view);
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LiveData<CreateGRoupREsponse> createGroupResponse;
    public LiveData<VerifiedContactsModel> commonResponse;
    public LiveData<UploadResponse> uploadGroupImgResponse;

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


    public void createGroup(Context context, ContactsListener contactsListener, JsonArray jsonArray) {
        if (createGroupResponse == null) {
            createGroupResponse = new MutableLiveData<CreateGRoupREsponse>();
            //we will load it asynchronously from server in this method
            createGroupResponse = new SelectContactRepository().createGroup(jsonArray, context);
            contactsListener.onCreateGroupSuccess(createGroupResponse);
        } else {
            createGroupResponse = new SelectContactRepository().createGroup(jsonArray, context);
            contactsListener.onCreateGroupSuccess(createGroupResponse);
        }
    }

    public void uploadCreatedGroupPic(Context context, ContactsListener contactsListener, String user_id, String group_id, String admin_id, String groupImgPath) {
        if(uploadGroupImgResponse == null){
            uploadGroupImgResponse = new MutableLiveData<UploadResponse>();
            uploadGroupImgResponse = new UploadFiless().uploadGroupImage(context, user_id, group_id, admin_id, groupImgPath);
            contactsListener.onGroupImgUploadSuccess(uploadGroupImgResponse);
        }else{
            uploadGroupImgResponse = new UploadFiless().uploadGroupImage(context, user_id, group_id, admin_id, groupImgPath);
            contactsListener.onGroupImgUploadSuccess(uploadGroupImgResponse);
        }



    }
}