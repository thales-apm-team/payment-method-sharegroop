package com.payline.payment.sharegroop.bean;

import com.payline.payment.sharegroop.bean.payment.Data;

import java.util.List;

public class SharegroopAPICallResponse {
    private Boolean success;
    private Data data;

    private String status;
    private List<String> errors;

    public Boolean getSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

}
