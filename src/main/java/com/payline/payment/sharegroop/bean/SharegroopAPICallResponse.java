package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.sharegroop.bean.payment.Data;

public class SharegroopAPICallResponse {
    private Boolean success;
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public static SharegroopAPICallResponse fromJson(String json ){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( json, SharegroopAPICallResponse.class );
    }
}
