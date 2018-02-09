package com.reonios.mscoprototype;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;

/**
 * Created by reonios on 09/02/18.
 */

public class Offer {
    @SerializedName("storeId")
    @Expose
    private String storeId;

    @SerializedName("storeName")
    @Expose
    private String storeName;

    @SerializedName("offer")
    @Expose
    private String offer;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    Offer(String storeId, String storeName, String offer, String imageUrl) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.offer = offer;
        this.imageUrl = imageUrl;
    }

    String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }

    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getOffer() { return offer; }

    public void setOffer(String offer) { this.offer = offer; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
