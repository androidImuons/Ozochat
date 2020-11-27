package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private DataObject dataObject;

    public List<DataObject> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataObject> dataList) {
        this.dataList = dataList;
    }

    @SerializedName("data")
    @Expose
    private List<DataObject> dataList;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("code")
    @Expose
    private int code;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
