package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


public class SharegroopErrorResponse {

    private String status;

    private List<String> errors;

    public String getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static SharegroopErrorResponse fromJson(String json ){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( json, SharegroopErrorResponse.class );
    }
}
