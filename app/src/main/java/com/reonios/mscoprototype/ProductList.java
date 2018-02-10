package com.reonios.mscoprototype;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by reonios on 10/02/18.
 */

public class ProductList {
    @SerializedName("itemProductList")
    @Expose
    private ArrayList<Product> itemProductList;
    public ProductList(ArrayList<Product> itemProductList) {
        this.itemProductList = itemProductList;
    }
}
