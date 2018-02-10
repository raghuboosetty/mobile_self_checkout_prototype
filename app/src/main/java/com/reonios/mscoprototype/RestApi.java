package com.reonios.mscoprototype;

import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by reonios on 5/10/16.
 */
public interface RestApi {
    @GET("api/locate/{id}/barcode")
    Call<Product> getProductDetails(@Path("id") String barcode);

    @GET("api/locate/{id}/beacon")
    Call<Beacon> getBeaconDetails(@Path("id") String uuid);

    @FormUrlEncoded
    @POST("api/pos/{id}/cart")
    Call<ResponseBody> postBeaconDetails(@Path("id") String phone, @Field("itemProductList[]") ProductList itemProductList);
}
