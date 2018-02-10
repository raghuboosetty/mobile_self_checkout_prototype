package com.reonios.mscoprototype;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Product {

    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;

//    remove serialized attrs
    @SerializedName("quantity")
    @Expose
    private Integer quantity=0;
    @SerializedName("quantityPrice")
    @Expose
    private Double quantityPrice=0.0;

    public Product(String barcode, String price, String title, String body) {
        this.barcode = barcode;
        this.price = price;
        this.title = title;
        this.body = body;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getQuantityPrice() {
        return quantityPrice;
    }

    public void setQuantityPrice(Double quantityPrice) {
        this.quantityPrice = quantityPrice;
    }

}