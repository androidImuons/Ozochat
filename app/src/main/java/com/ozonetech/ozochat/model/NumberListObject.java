package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NumberListObject {
    @SerializedName("numberList")
    @Expose
    private ArrayList<MobileObject> mobile;

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    @SerializedName("sender_id")
    @Expose
    private String sender_id;

    public ArrayList<MobileObject> getMobile() {
        return mobile;
    }

    public void setMobile(ArrayList<MobileObject> mobile) {
        this.mobile = mobile;
    }
}
