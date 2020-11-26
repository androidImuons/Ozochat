package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupCreateRecord {
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("admin_user_id")
    @Expose
    private Integer adminUserId;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("group_name")
    @Expose
    private String groupName;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Integer adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
