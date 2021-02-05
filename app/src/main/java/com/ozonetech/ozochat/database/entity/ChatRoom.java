package com.ozonetech.ozochat.database.entity;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ozonetech.ozochat.R;

import java.io.Serializable;

@Entity
public class ChatRoom implements Serializable {

   // @PrimaryKey(autoGenerate = true)

    private int id;
    @ColumnInfo(name = "user_contact_no")
    @SerializedName("user_contact_no")
    @Expose
    private String user_contact_no;


    @ColumnInfo(name = "uid")
    @SerializedName("uid")
    @Expose
    private Integer uid;

    @ColumnInfo(name = "adminId")
    @SerializedName("admin_id")
    @Expose
    private Integer adminId;

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "groupId")
    @SerializedName("group_id")
    @Expose
    private String groupId;

    @ColumnInfo(name = "oneToOne")
    @SerializedName("oneToOne")
    @Expose
    private Integer oneToOne;

    @ColumnInfo(name = "username")
    @SerializedName("username")
    @Expose
    private String username;

    @ColumnInfo(name = "profilePicture")
    @SerializedName("profile_image")
    @Expose
    private String profilePicture;

    @ColumnInfo(name = "message")
    @SerializedName("message")
    @Expose
    private String lastMessage;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    private String title;

    @ColumnInfo(name = "mobile")
    @SerializedName("mobile")
    @Expose
    private String mobile;

    @ColumnInfo(name = "groupName")
    @SerializedName("group_name")
    @Expose
    private String groupName;

    @ColumnInfo(name = "groupImage")
    @SerializedName("group_image")
    @Expose
    private String groupImage;


    @BindingAdapter({"profilePicture"})
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_icon)
                .into(view);

    }

    @SerializedName("usermobile")
    @Expose
    private String usermobile;

    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("last_seen")
    @Expose
    private String timestamp;

    @SerializedName("msg_counter")
    @Expose
    private Integer unreadCount;

    @SerializedName("status")
    @Expose
    private String status;



    public String getUser_contact_no() {
        return user_contact_no;
    }

    public void setUser_contact_no(String user_contact_number) {
        this.user_contact_no = user_contact_number;
    }


    public ChatRoom() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(Integer oneToOne) {
        this.oneToOne = oneToOne;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /*public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
*/

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
