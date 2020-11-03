package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupCreateRecord {
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("g_id")
    @Expose
    private Integer gId;
    @SerializedName("group_id")
    @Expose
    private String groupId;

    public Integer getAdmin_user_id() {
        return admin_user_id;
    }

    public void setAdmin_user_id(Integer admin_user_id) {
        this.admin_user_id = admin_user_id;
    }

    @SerializedName("admin_user_id")
    @Expose
    private Integer admin_user_id;


    private final static long serialVersionUID = -8103292797437305922L;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getGId() {
        return gId;
    }

    public void setGId(Integer gId) {
        this.gId = gId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
