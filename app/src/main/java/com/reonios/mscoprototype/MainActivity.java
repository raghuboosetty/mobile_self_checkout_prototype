package com.reonios.mscoprototype;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.reonios.msco.MaterialBarcodeScanner;
import com.reonios.msco.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertNotNull;

public class MainActivity extends AppCompatActivity {
    //  Restro fit API
    public static final String BASE_URL = "https://msco.herokuapp.com/";

    //  Bluetooth LE Scanner
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private ArrayList<String> bleUuidArrayList = new ArrayList<>();
    private BleScan bleScan;

    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 3000;
    private boolean isScanning = false;
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String LOG_TAG = "MainActivity";
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;


    //  Declaring our ImageView
//  TODO: use lists instead of individual ImageView
    private ImageView bleImageAd1;
    private ImageView bleImageAd2;
    private ImageView bleImageAd3;
    private ImageView bleImageAd4;
    private ImageView bleImageAd5;

    private TextView bleTextAd1;
    private TextView bleTextAd2;
    private TextView bleTextAd3;
    private TextView bleTextAd4;
    private TextView bleTextAd5;

    private TextView bleSubTextAd1;
    private TextView bleSubTextAd2;
    private TextView bleSubTextAd3;
    private TextView bleSubTextAd4;
    private TextView bleSubTextAd5;

    private LinearLayout bleAd1;
    private LinearLayout bleAd2;
    private LinearLayout bleAd3;
    private LinearLayout bleAd4;
    private LinearLayout bleAd5;

    HashMap<String,Object[]> cartHashMap = new HashMap<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      Permissions for Bluetooth, Location and Camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if ((this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            } else { initMsco(); }
        } else { initMsco(); }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(this, SettingsActivity.class));
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //  TODO: User need to give all the permissions to run the app. Instead it should be based on the permission user gave.
//  Initializes Bluetooth in background and Barcode scanner button
    private void initMsco(){
//      Bluetooth LE
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
            Toast.makeText(MainActivity.this, "Bluetooth Enabled", Toast.LENGTH_LONG).show();
        }

        bleScan = new BleScan(this, btAdapter, bleUuidArrayList);
//        bleScan.startBleScan();
        scanHandler.post(scanRunnable);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;
//        }

        String accessResult = null;
        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "ACCESS_COARSE_LOCATION Permission Granted");
                    } else{
                        if (accessResult == null){ accessResult = "This app will not be able to discover beacons";
                        } else { accessResult += " and will not be able to discover beacons too!"; }
                    }
                } else if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "CAMERA Permission Granted");
                    } else{
                        if (accessResult == null){ accessResult = "This app will not be able to scan barcode";
                        } else { accessResult += " and will not be able to scan barcode too!"; }
                    }
                }
            }
        }
        if (accessResult != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error!");
            builder.setMessage(accessResult);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            builder.show();
        } else { initMsco(); }
    }

    /**
     * Retrofit API and BLE functionality
     */
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
                        if (resBeaconLocation.equals("CCC")) {

                            TextView bleAd = (TextView) findViewById(R.id.bleAd);
                            bleAd.setVisibility(View.VISIBLE);

//                      Get user details(name)
                            final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            String name = (mSharedPreference.getString("example_text", "Your Name"));

                            String offersMessage = "Hey There," + " Welcome to " + beacon.getLocation() + "\n Check out our new Offers!";

                            if (!(name.equals("Your Name")) && !(name.equals("John Smith")) && !(name.equals(""))) {
                                offersMessage = "Hey " + name + ", Welcome to " + beacon.getLocation() + "\n Check out our new Offers!";
                            }

                            bleAd1 = (LinearLayout) findViewById(R.id.bleAd1);
                            bleAd1.setVisibility(View.VISIBLE);
                            bleAd2 = (LinearLayout) findViewById(R.id.bleAd2);
                            bleAd2.setVisibility(View.VISIBLE);
                            bleAd3 = (LinearLayout) findViewById(R.id.bleAd3);
                            bleAd3.setVisibility(View.VISIBLE);
                            bleAd4 = (LinearLayout) findViewById(R.id.bleAd4);
                            bleAd4.setVisibility(View.VISIBLE);
                            bleAd5 = (LinearLayout) findViewById(R.id.bleAd5);
                            bleAd5.setVisibility(View.VISIBLE);

                            bleTextAd1 = (TextView) findViewById(R.id.bleTextAd1);
                            bleTextAd2 = (TextView) findViewById(R.id.bleTextAd2);
                            bleTextAd3 = (TextView) findViewById(R.id.bleTextAd3);
                            bleTextAd4 = (TextView) findViewById(R.id.bleTextAd4);
                            bleTextAd5 = (TextView) findViewById(R.id.bleTextAd5);

                            bleTextAd1.setText(resBeaconOffers.get(0).getStoreName());
                            bleTextAd2.setText(resBeaconOffers.get(1).getStoreName());
                            bleTextAd3.setText(resBeaconOffers.get(2).getStoreName());
                            bleTextAd4.setText(resBeaconOffers.get(3).getStoreName());
                            bleTextAd5.setText(resBeaconOffers.get(4).getStoreName());

                            bleSubTextAd1 = (TextView) findViewById(R.id.bleSubTextAd1);
                            bleSubTextAd2 = (TextView) findViewById(R.id.bleSubTextAd2);
                            bleSubTextAd3 = (TextView) findViewById(R.id.bleSubTextAd3);
                            bleSubTextAd4 = (TextView) findViewById(R.id.bleSubTextAd4);
                            bleSubTextAd5 = (TextView) findViewById(R.id.bleSubTextAd5);

                            bleSubTextAd1.setText(resBeaconOffers.get(0).getOffer());
                            bleSubTextAd2.setText(resBeaconOffers.get(1).getOffer());
                            bleSubTextAd3.setText(resBeaconOffers.get(2).getOffer());
                            bleSubTextAd4.setText(resBeaconOffers.get(3).getOffer());
                            bleSubTextAd5.setText(resBeaconOffers.get(4).getOffer());

//                  Initializing the ImageView
                            bleImageAd1 = (ImageButton) findViewById(R.id.bleImageAd1);
                            bleImageAd2 = (ImageButton) findViewById(R.id.bleImageAd2);
                            bleImageAd3 = (ImageButton) findViewById(R.id.bleImageAd3);
                            bleImageAd4 = (ImageButton) findViewById(R.id.bleImageAd4);
                            bleImageAd5 = (ImageButton) findViewById(R.id.bleImageAd5);

//                  Loading Image from URL
                            Picasso.with(MainActivity.this)
                                    .load(resBeaconOffers.get(0).getImageUrl())
                                    .into(bleImageAd1);

                            Picasso.with(MainActivity.this)
                                    .load(resBeaconOffers.get(1).getImageUrl())
                                    .into(bleImageAd2);

                            Picasso.with(MainActivity.this)
                                    .load(resBeaconOffers.get(2).getImageUrl())
                                    .into(bleImageAd3);

                            Picasso.with(MainActivity.this)
                                    .load(resBeaconOffers.get(3).getImageUrl())
                                    .into(bleImageAd4);

                            Picasso.with(MainActivity.this)
                                    .load(resBeaconOffers.get(4).getImageUrl())
                                    .into(bleImageAd5);

                            bleAd.setText(offersMessage);

                            //Assign a listener to your button
                            bleImageAd1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Start your second activity
                                    Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("storeName", resBeaconOffers.get(0).getStoreName());
                                    extras.putString("imageUrl", resBeaconOffers.get(0).getImageUrl());
                                    scanIntent.putExtras(extras);
                                    startActivity(scanIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            //Assign a listener to your button
                            bleImageAd2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Start your second activity
                                    Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("storeName", resBeaconOffers.get(1).getStoreName());
                                    extras.putString("imageUrl", resBeaconOffers.get(1).getImageUrl());
                                    scanIntent.putExtras(extras);
                                    startActivity(scanIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            //Assign a listener to your button
                            bleImageAd3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Start your second activity
                                    Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("storeName", resBeaconOffers.get(2).getStoreName());
                                    extras.putString("imageUrl", resBeaconOffers.get(2).getImageUrl());
                                    scanIntent.putExtras(extras);
                                    startActivity(scanIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            //Assign a listener to your button
                            bleImageAd4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Start your second activity
                                    Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("storeName", resBeaconOffers.get(3).getStoreName());
                                    extras.putString("imageUrl", resBeaconOffers.get(3).getImageUrl());
                                    scanIntent.putExtras(extras);
                                    startActivity(scanIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });

                            //Assign a listener to your button
                            bleImageAd5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Start your second activity
                                    Intent scanIntent = new Intent(MainActivity.this, ScanActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putString("storeName", resBeaconOffers.get(4).getStoreName());
                                    extras.putString("imageUrl", resBeaconOffers.get(4).getImageUrl());
                                    scanIntent.putExtras(extras);
                                    startActivity(scanIntent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });
                        }
                        else{
//                            TextView bleAdShop = (TextView) findViewById(R.id.bleAdShop);
//                            bleAdShop.setText(resBeaconMessage);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Beacon Not Found! Please Contact Support Team.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Beacon> call, Throwable t) {}
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

            for (int i = 0; i < bleUuidArrayList.size(); i++) {
                getCustomerDetails(bleUuidArrayList.get(i));
            }

            TextView bleAd = (TextView) findViewById(R.id.bleAd);
//            Log.d(LOG_TAG, bleAd.getText().toString());
//            TextUtils.isEmpty(bleAd.getText()) ||
            if (bleAd.getText().toString().equals(getResources().getString(R.string.ble_ad))) {
                scanHandler.postDelayed(this, scan_interval_ms);
            }
            else {
                bleScan.stopBleScan();
            }
        }
    };

}
