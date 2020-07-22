package com.payline.payment.sharegroop.bean.notification;

import com.google.gson.Gson;

public class SharegroopNotificationResponse {

    private String event;
    private String id;
    private String date;

    public String getEvent() { return event; }

    public String getId() { return id; }

    public String getDate() { return date; }

    public static SharegroopNotificationResponse fromJson(String json ){ return new Gson().fromJson( json, SharegroopNotificationResponse.class ); }
}
