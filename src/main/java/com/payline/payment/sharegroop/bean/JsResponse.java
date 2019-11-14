package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsResponse {
    private String order;
    private String auth;
    private String status;
    private String email;
    private String amount;

    public String getOrder() {
        return order;
    }

    public String getAuth() {
        return auth;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getAmount() {
        return amount;
    }

    public static JsResponse fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, JsResponse.class );
    }
}
