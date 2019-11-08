package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Data extends Order{
    private String id;
    private String platformId;
    private String amountConfirmed;
    private String delay;
    private String type;
    private String status;
    private String createdAt;


    public String getAmountConfirmed() {
        return amountConfirmed;
    }

    public void setAmountConfirmed(String amountConfirmed) {
        this.amountConfirmed = amountConfirmed;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    private String dueDate;


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
