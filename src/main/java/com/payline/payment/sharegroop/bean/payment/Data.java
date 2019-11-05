package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Data extends Order{
    private String id;
    private String platformId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public static Data fromJson(String json) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson(json, Data.class);
    }
}
