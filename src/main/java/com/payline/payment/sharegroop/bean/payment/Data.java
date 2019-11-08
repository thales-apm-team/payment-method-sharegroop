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

    public String getTenantPlatform() {
        return tenantPlatform;
    }

    public void setTenantPlatform(String tenantPlatform) {
        this.tenantPlatform = tenantPlatform;
    }

    public String getNbShares() {
        return nbShares;
    }

    public void setNbShares(String nbShares) {
        this.nbShares = nbShares;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getEcard() {
        return ecard;
    }

    public void setEcard(String ecard) {
        this.ecard = ecard;
    }

    public String getAmountConfirmed() {
        return amountConfirmed;
    }

    public void setAmountConfirmed(String amountConfirmed) {
        this.amountConfirmed = amountConfirmed;
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
