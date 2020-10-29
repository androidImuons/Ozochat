package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NumberListObject {
    @SerializedName("numberList")
    @Expose
    private ArrayList<MobileObject> mobile;

    public ArrayList<MobileObject> getMobile() {
        return mobile;
    }

    public void setMobile(ArrayList<MobileObject> mobile) {
        this.mobile = mobile;
    }
}
