package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Item {
    private String trackId;
    private String name;
    private String description;
    private Integer amount;
    private Integer quantity;

    /* TODO: lorsqu'on a une contrainte de couverture de code pour les TU, ce n'est pas forcément judicieux
    de créer les setters de façon systématique. Surtout que, dans le cas de ce genre d'objet, tu ne les utiliseras
    normalement jamais... leur présence déteriore la couverture de code, alors qu'ils ne sont jamais appelés. C'est dommage !
     */

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static Item fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, Item.class );
    }
}
