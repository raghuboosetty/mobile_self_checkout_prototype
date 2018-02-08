package com.reonios.mscoprototype;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    Beacon(String uuid, String message, String location) {
        this.uuid = uuid;
        this.message = message;
        this.location = location;
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
}
