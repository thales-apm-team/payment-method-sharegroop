package com.payline.payment.sharegroop.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonService {
    private final Gson gson;

    // --- Singleton Holder pattern + initialization BEGIN
    private JsonService() {
        gson = new GsonBuilder()
                .create();
    }

    private static class Holder {
        private static final JsonService instance = new JsonService();
    }

    public static JsonService getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public String toJson(Object o) {
        return gson.toJson(o);
    }
}
