package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Data {
    private String id;
    private String platformId;
    private Integer amount;
    private String ux;
    private String currency;
    private String locale;
    private Boolean secure3D;
    private String email;
    private String firstName;
    private String lastName;
    private String trackId;
    private Item[] items;

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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getUx() {
        return ux;
    }

    public void setUx(String ux) {
        this.ux = ux;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getSecure3D() {
        return secure3D;
    }

    public void setSecure3D(Boolean secure3D) {
        this.secure3D = secure3D;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public static Data fromJson(String json) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson(json, Data.class);
    }
}
