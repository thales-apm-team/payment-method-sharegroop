package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Order {
    private Integer amount;
    private String ux;
    private String currency;
    private String locale;
    private Boolean secure3D;
    private String delay;
    private String email;
    private String firstName;
    private String lastName;
    private String trackId;
    private Item[] items;

    public Integer getAmount() {
        return amount;
    }

    public String getUx() {
        return ux;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLocale() {
        return locale;
    }

    public Boolean getSecure3D() {
        return secure3D;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTrackId() {
        return trackId;
    }

    public Item[] getItems() {
        return items;
    }

    public String getDelay() {
        return delay;
    }

    public static Order fromJson(String json ){
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( json, Order.class );
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        return gson.toJson( this );
    }
}
