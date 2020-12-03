package com.ozonetech.ozochat.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Message implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int rendId;

    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "group_id")
    @SerializedName("group_id")
    @Expose
    private String groupId;

    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @ColumnInfo(name = "message")
    @SerializedName("message")
    @Expose
    private String message;

    @ColumnInfo(name = "created")
    @SerializedName("created")
    @Expose
    private String created;

    @ColumnInfo(name = "sender_mobile")
    @SerializedName("sender_mobile")
    @Expose
    private String sender_mobile;

    @ColumnInfo(name = "sender_name")
    @SerializedName("sender_name")
    @Expose
    private String sender_name;

    public boolean isIs_contact() {
        return is_contact;
    }

    public void setIs_contact(boolean is_contact) {
        this.is_contact = is_contact;
    }

    private boolean is_contact;

    public String getSender_mobile() {
        return sender_mobile;
    }

    public void setSender_mobile(String sender_mobile) {
        this.sender_mobile = sender_mobile;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getRendId() {
        return rendId;
    }

    public void setRendId(int rendId) {
        this.rendId = rendId;
    }
}