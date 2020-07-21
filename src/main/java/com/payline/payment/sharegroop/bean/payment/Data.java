package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Data extends Order{
    private String id;
    private String platformId;
    private String amountConfirmed;
    private String type;
    private String status;
    private String createdAt;
    private String tenantPlatform;
    private String nbShares;
    private String tenantId;
    private String integration;
    private String ecard;
    private String dueDate;
    private String notifyUrl;

    public String getTenantPlatform() {
        return tenantPlatform;
    }
    public String getNbShares() {
        return nbShares;
    }
    public String getTenantId() {
        return tenantId;
    }
    public String getNotifyUrl() {return notifyUrl;}
    public String getIntegration() {
        return integration;
    }
    public String getEcard() {
        return ecard;
    }
    public String getAmountConfirmed() {
        return amountConfirmed;
    }
    public String getType() {
        return type;
    }
    public String getStatus() {
        return status;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getDueDate() {
        return dueDate;
    }
    public String getId() {
        return id;
    }
    public String getPlatformId() {
        return platformId;
    }
    public static Data fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, Data.class);
    }
}
