package com.payline.payment.sharegroop.bean;

public class JsResponse {
    private String order;
    private String auth;
    private String status;
    private String email;
    private String amount;

    public String getOrder() {
        return order;
    }

    public String getAuth() {
        return auth;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getAmount() {
        return amount;
    }

}
