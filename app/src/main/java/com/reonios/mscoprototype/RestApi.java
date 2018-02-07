package com.reonios.mscoprototype;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by reonios on 5/10/16.
 */
public interface RestApi {
    @GET("locate/{id}/barcode")
    Call<Product> getProductDetails(@Path("id") String barcode);
}
