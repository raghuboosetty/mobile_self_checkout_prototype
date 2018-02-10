package com.reonios.mscoprototype;

import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by reonios on 5/18/16.
 */
public class Cart {
    CustomAdapter customAdapter;
    ArrayList<Product> itemProductList;
    TextView tvTotal;

//  TODO: Do not pass textView instead use Fragments
    public Cart(CustomAdapter customAdapter, ArrayList<Product> productList, TextView tvTotal){
        this.customAdapter = customAdapter;
        this.itemProductList = productList;
        this.tvTotal = tvTotal;
    }

    void addProduct(Product scannedProduct){
        Double totalPrice=0.0;
        Boolean newProduct = true;
        Double totalCartValue = 0.0;

        Iterator<Product> productIterator = itemProductList.iterator();
        while (productIterator.hasNext()) {
            Product product = productIterator.next();
            Integer productQuantity = product.getQuantity();

            if(scannedProduct.getBarcode().equalsIgnoreCase(product.getBarcode())) {
                newProduct = false;
                productQuantity++;
            }

            Double productPrice = Double.parseDouble(product.getPrice());
            Double productQuantityPrice = productPrice * productQuantity;
            totalPrice = totalPrice + productQuantityPrice;
            product.setQuantityPrice(productQuantityPrice);
            product.setQuantity(productQuantity);
            totalCartValue = totalCartValue + productQuantityPrice;

            if(!newProduct) {
                itemProductList.set(itemProductList.indexOf(product), product);
                customAdapter.notifyDataSetChanged();
            }
        }
        if(newProduct) {
            Double productPrice = Double.parseDouble(scannedProduct.getPrice());
            scannedProduct.setQuantity(1);
            scannedProduct.setQuantityPrice(productPrice);
            totalCartValue = totalCartValue + productPrice;
            itemProductList.add(scannedProduct);
            customAdapter.notifyDataSetChanged();
        }
        tvTotal.setText(totalCartValue.toString());
    }

    void removeProduct(Product removedProduct){
        Double totalPrice=0.0;
        Double totalCartValue = 0.0;

        Iterator<Product> productIterator = itemProductList.iterator();
        while (productIterator.hasNext()) {
            Product product = productIterator.next();
            Integer productQuantity = product.getQuantity();

            Double productPrice = Double.parseDouble(product.getPrice());
            Double productQuantityPrice = productPrice * productQuantity;
            totalPrice = totalPrice + productQuantityPrice;
            product.setQuantityPrice(productQuantityPrice);
            product.setQuantity(productQuantity);
            totalCartValue = totalCartValue + productQuantityPrice;

            itemProductList.set(itemProductList.indexOf(product), product);
            customAdapter.notifyDataSetChanged();
        }
        tvTotal.setText(totalCartValue.toString());
    }

    void removeProducts(){
        Double totalPrice=0.0;
        Double totalCartValue = 0.0;

        itemProductList.removeAll(itemProductList);
        customAdapter.notifyDataSetChanged();
        tvTotal.setText(totalCartValue.toString());
    }

    @Override
    public String toString() {
//        for (int i = 0; i < itemProductList.size(); i++) {

        JSONArray jsArray = new JSONArray(itemProductList);
        return jsArray.toString();
    }
}
