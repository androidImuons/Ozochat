package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileObject {
    @SerializedName("mobile")
    @Expose
    private String mobiles;

    public MobileObject(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }
}
