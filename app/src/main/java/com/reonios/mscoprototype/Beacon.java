package com.reonios.mscoprototype;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by reonios on 07/02/18.
 */

public class Beacon {
    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("offers")
    @Expose
    private ArrayList<Offer> offers;

    Beacon(String uuid, String message, String location, ArrayList<Offer> offers) {
        this.uuid = uuid;
        this.message = message;
        this.location = location;
        this.offers = offers;
    }

    String getUuid() {
        return uuid;
    }

    public void setBeaconUuid(String uuid) { this.uuid = uuid; }

    String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }

    String getLocation() { return location; }

    public void setLocation(String location) {
        this.location = location;
    }

    ArrayList<Offer> getOffers() { return offers; }

    public void setOffers(String location) {
        this.offers = offers;
    }
}
