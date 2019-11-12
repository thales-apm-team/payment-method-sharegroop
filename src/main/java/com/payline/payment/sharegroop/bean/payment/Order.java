package com.payline.payment.sharegroop.bean.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Order {
    private Integer amount;
    private String ux;
    private String currency;
    private String locale;
    private Boolean secure3D;
    private String delay;
    private String email;
    private String firstName;
    private String lastName;
    private String trackId;
    private Item[] items;

    /* TODO: lorsqu'on a une contrainte de couverture de code pour les TU, ce n'est pas forcément judicieux
    de créer les setters de façon systématique. Surtout que, dans le cas de ce genre d'objet, tu ne les utiliseras
    normalement jamais... leur présence déteriore la couverture de code, alors qu'ils ne sont jamais appelés. C'est dommage !
     */

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

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }




    public static Order fromJson(String json ){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return gson.fromJson( json, Order.class );
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .disableHtmlEscaping()
                .create();
        return gson.toJson( this );
    }
}
