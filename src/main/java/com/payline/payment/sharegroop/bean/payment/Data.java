package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.sharegroop.bean.payment.Order;

public class Data {
    private String id;
    private String platformId;
    private Order order;

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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public static Data fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, Data.class );
    }
}
