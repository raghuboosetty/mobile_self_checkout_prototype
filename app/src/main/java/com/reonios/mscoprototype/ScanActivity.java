package com.reonios.mscoprototype;

import android.Manifest;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.reonios.msco.MaterialBarcodeScanner;
import com.reonios.msco.MaterialBarcodeScannerBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertNotNull;

public class ScanActivity extends AppCompatActivity {
//  Restro fit API
    public static final String BASE_URL = "https://msco.herokuapp.com/api/";
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    //  Cart items
    ListView listView;
    ArrayList<Product> itemProductList;
    CustomAdapter customAdapter;
    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


//      Cart Elements
        TextView tvTotal = (TextView) findViewById(R.id.total);
        listView = (ListView) findViewById(R.id.listview);
        itemProductList = new ArrayList<>();
        customAdapter = new CustomAdapter(getApplicationContext(), itemProductList, tvTotal);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(customAdapter);
        cart = new Cart(customAdapter,itemProductList,tvTotal);


        //      Permissions for Bluetooth, Location and Camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if ((this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            } else { initScan(); }
        } else { initScan(); }

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
//                tvBarcode.setText(restoredBarcode.rawValue);
                barcodeResult = restoredBarcode;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

//  TODO: User need to give all the permissions to run the app. Instead it should be based on the permission user gave.
//  Initializes Bluetooth in background and Barcode scanner button
    private void initScan(){
//      Barcode Scanner
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assertNotNull(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ScanActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
//                        TODO: find the different types of codes to scan. 5 is just assumed number.
                        if (barcodeResult.rawValue.length() > 5) {
//                            result.setText(barcode.rawValue);
                            getBarcodeDetails(barcodeResult.rawValue);
                        }
                        else Toast.makeText(ScanActivity.this, "Invalid Scan! Please place the barcode parallel to camera.", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    /**
     * Retrofit API and cart functionality
     */
    void getBarcodeDetails(String barcodeRaw) {
        final String resBarcode = barcodeRaw;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);
        Call<Product> call = service.getProductDetails(resBarcode);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                try {
                    String resPrice = response.body().getPrice();
                    String resTitle = response.body().getTitle();
                    String resDescription = response.body().getBody();

                    Log.d("POST SCAN API: ","Price:" + resPrice + " Title: " + resTitle + " Description: " + resDescription);

                    Product scannedProduct = new Product(resBarcode,resPrice,resTitle,resDescription);
                    cart.addProduct(scannedProduct);

                } catch (Exception e) {
                    Toast.makeText(ScanActivity.this, "Item Not Found! Contact Sales Team.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {}
        });
    }
}
