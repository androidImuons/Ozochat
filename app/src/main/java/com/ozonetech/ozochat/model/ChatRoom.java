package com.ozonetech.ozochat.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.R;

import java.io.Serializable;

public class ChatRoom implements Serializable {

    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("image")
    @Expose
    private String profilePicture;
    @SerializedName("uid")
    @Expose
    private Integer uid;

    public Integer getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Integer admin_id) {
        this.admin_id = admin_id;
    }

    @SerializedName("admin_id")
    @Expose
    private Integer admin_id;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @BindingAdapter({"profilePicture"})
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_icon)
                .into(view);

    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public Integer getUid() {
        return uid;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }


    String lastMessage="last message";
     String   timestamp="12:00 pm";
    int unreadCount=5;

    public ChatRoom() {
    }

    public ChatRoom(String groupId,String profilePicture, String username, String lastMessage, String timestamp, int unreadCount) {
        this.groupId = groupId;
        this.profilePicture= profilePicture;
        this.username = username;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
