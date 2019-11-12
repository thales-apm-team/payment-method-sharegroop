package com.payline.payment.sharegroop.bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.sharegroop.bean.payment.Data;

public class SharegroopAPICallResponse {
    private Boolean success;
    private Data data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static SharegroopAPICallResponse fromJson(String json ){
        // TODO: je ne vois aucune date dans les attributs Java de cette classe ou de Data : à quoi sert le setDateFormat() ci-dessous ?
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, SharegroopAPICallResponse.class );
    }
}
