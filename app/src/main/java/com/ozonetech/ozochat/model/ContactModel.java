package com.ozonetech.ozochat.model;

import android.widget.ImageView;

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
public class ContactModel implements Serializable {
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    private String name;

    @PrimaryKey()
    @ColumnInfo(name = "uid")
    @SerializedName("uid")
    @Expose
    private Integer uid;

    @ColumnInfo(name = "imageurl")
    @SerializedName("imageUrl")
    @Expose
    private String profilePicture;

    @ColumnInfo(name = "number")
    @SerializedName("number")
    @Expose
    private String phone;

    @ColumnInfo(name = "status")
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

}
