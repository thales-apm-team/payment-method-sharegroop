package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class ShareGroopErrorResponse {
    private String status;
    private String success;
    private String errors;

    public String getStatus() {
        return status;
    }

    public void setStatus(String satus) {
        this.status = satus;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public static ShareGroopErrorResponse fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, ShareGroopErrorResponse.class );
    }
}
