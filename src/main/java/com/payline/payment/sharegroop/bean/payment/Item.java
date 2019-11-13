package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Item {
    private String trackId;
    private String name;
    private String description;
    private Integer amount;
    private Integer quantity;

    public String getTrackId() {
        return trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public static Item fromJson(String json ){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( json, Item.class );
    }
}
