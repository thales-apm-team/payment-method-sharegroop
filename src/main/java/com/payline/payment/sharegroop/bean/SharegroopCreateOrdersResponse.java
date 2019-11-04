package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.sharegroop.bean.payment.Data;

public class SharegroopCreateOrdersResponse {
    private Boolean success;
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static SharegroopCreateOrdersResponse fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, SharegroopCreateOrdersResponse.class );
    }
}
