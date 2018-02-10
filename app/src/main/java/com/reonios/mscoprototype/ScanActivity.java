package com.reonios.mscoprototype;

import android.Manifest;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.reonios.msco.MaterialBarcodeScanner;
import com.reonios.msco.MaterialBarcodeScannerBuilder;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertNotNull;

public class ScanActivity extends AppCompatActivity {

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private ArrayList<String> bleUuidArrayList = new ArrayList<>();
    private BleScan bleScan;
    private boolean isScanning = false;
    private int scan_interval_ms = 3000;
    private Handler scanHandler = new Handler();

    private static final String LOG_TAG = "ScanActivity";

//  Restro fit API
    public static final String BASE_URL = "https://msco.herokuapp.com/";
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String storeName = extras.getString("storeName");
            android.support.v7.app.ActionBar storeAb = getSupportActionBar();
            storeAb.setTitle(storeName);
            storeAb.setSubtitle(getString(R.string.title_activity_scan));

            final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String userName = (mSharedPreference.getString("example_text", "Your Name"));

            TextView bleAdShop = (TextView) findViewById(R.id.bleAdShop);
            bleAdShop.setVisibility(View.VISIBLE);
            bleAdShop.setText(getString(R.string.ble_ad_shop_part_1) + " " + userName + " " + getString(R.string.ble_ad_shop_part_2) + " " + storeName + getString(R.string.ble_ad_shop_part_3));

            ImageView bleImageAdShop = (ImageView) findViewById(R.id.bleImageAdShop);
            String imageUrl = extras.getString("imageUrl");
            Picasso.with(ScanActivity.this)
                    .load(imageUrl)
                    .into(bleImageAdShop);

            //      Bluetooth LE
            btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            btAdapter = btManager.getAdapter();
            if (!btAdapter.isEnabled()) {
                btAdapter.enable();
                Toast.makeText(ScanActivity.this, "Bluetooth Enabled", Toast.LENGTH_LONG).show();
            }

//          TODO: remove static assignment
            if (storeName.equals("H&M")) {
                bleScan = new BleScan(this, btAdapter, bleUuidArrayList);
//              bleScan.startBleScan();
                scanHandler.post(scanRunnable);
            }

        }


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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        final FloatingActionButton pay = (FloatingActionButton) findViewById(R.id.pay);
        assertNotNull(pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                RestApi service = retrofit.create(RestApi.class);
//                ProductList itemProductList = new ProductList(cart.itemProductList);
//                Call<ResponseBody> call = service.postBeaconDetails("9177167375", itemProductList);
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Log.i("POST RESPONSE: ", response.message());
//                    }
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    }
//                });

                cart.removeProducts();
                Toast.makeText(ScanActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();
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

//                    Log.d("POST SCAN API: ","Price:" + resPrice + " Title: " + resTitle + " Description: " + resDescription);

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

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                if (btAdapter != null) {
                    bleScan.startBleScan();
                }
            } else {
                if (btAdapter != null) {
                    bleScan.stopBleScan();
                }
            }
            isScanning = !isScanning;

//            Log.d(LOG_TAG, "ARRAY LIST" + bleUuidArrayList);
            for (int i = 0; i < bleUuidArrayList.size(); i++) {
                getCustomerDetails(bleUuidArrayList.get(i));
            }

            TextView bleAdShop = (TextView) findViewById(R.id.bleAdShop);
//            (TextUtils.isEmpty(bleAdShop.getText()))
            if ((bleAdShop.getText().toString().toLowerCase().contains(getResources().getString(R.string.ble_ad_shop_part_1).toLowerCase()))) {
//                Log.d(LOG_TAG, "BLE AD SHOP IS EMPTY");
                scanHandler.postDelayed(this, scan_interval_ms);
            } else {
//                Log.d(LOG_TAG, "SCAN STOPPED");
                bleScan.stopBleScan();
            }
        }
    };

    void getCustomerDetails(String beaconUuidRaw) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);
        Call<Beacon> call = service.getBeaconDetails(beaconUuidRaw);

        call.enqueue(new Callback<Beacon>() {
            @Override
            public void onResponse(Call<Beacon> call, Response<Beacon> response) {
                try {
                    if (response.body() != null) {
                        String resBeaconUuid = response.body().getUuid();
                        String resBeaconMessage = response.body().getMessage();
                        String resBeaconLocation = response.body().getLocation();
                        final ArrayList<Offer> resBeaconOffers = response.body().getOffers();

//                        Log.d("POST BEACON API: ", "UUID:" + resBeaconUuid + " Message: " + resBeaconMessage + " Offers: " + resBeaconOffers.get(0).getImageUrl() + " STORE NAME: " + resBeaconOffers.get(0).getStoreName());

                        Beacon beacon = new Beacon(resBeaconUuid, resBeaconMessage, resBeaconLocation, resBeaconOffers);

//                      TODO: remove hardcoded logic
                        if (!resBeaconLocation.equals("CCC")) {
                            TextView bleAdShop = (TextView) findViewById(R.id.bleAdShop);
                            bleAdShop.setText(resBeaconMessage);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(ScanActivity.this, "Beacon Not Found! Please Contact Support Team.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Beacon> call, Throwable t) {}
        });
    }
}
