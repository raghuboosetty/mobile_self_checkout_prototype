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

    public Beacon(String uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public String getUuid() {
        return uuid;
    }

    public void setBeaconUuid(String uuid) { this.uuid = uuid; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
