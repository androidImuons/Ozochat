package com.ozonetech.ozochat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadFilesResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;


    public List<DataObject> getDataList() {
        return dataObject;
    }

    public void setDataList(List<DataObject> dataList) {
        this.dataObject = dataList;
    }

    @SerializedName("data")
    @Expose
    private List<DataObject> dataObject;

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

//    public DataObject getDataObject() {
//        return dataObject;
//    }
//
//    public void setDataObject(DataObject dataObject) {
//        this.dataObject = dataObject;
//    }

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
