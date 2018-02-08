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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.reonios.msco.MaterialBarcodeScanner;
import com.reonios.msco.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

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
    public static final String BASE_URL = "https://msco.herokuapp.com/api/";
//    public static final String BARCODE_KEY = "BARCODE";
//    private Barcode barcodeResult;

//  Bluetooth LE Scanner
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 500;
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

    HashMap<String,Object[]> cartHashMap = new HashMap<>();

////  Cart items
//    ListView listView;
//    ArrayList<Product> itemProductList;
//    CustomAdapter customAdapter;
//    Cart cart;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find your views
        ImageButton bleImageButton1 = (ImageButton) findViewById(R.id.bleImageAd1);

        //Assign a listener to your button
        bleImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

////      Cart Elements
//        TextView tvTotal = (TextView) findViewById(R.id.total);
//        listView = (ListView) findViewById(R.id.listview);
//        itemProductList = new ArrayList<>();
//        customAdapter = new CustomAdapter(getApplicationContext(), itemProductList, tvTotal);
//        listView.setEmptyView(findViewById(android.R.id.empty));
//        listView.setAdapter(customAdapter);
//        cart = new Cart(customAdapter,itemProductList,tvTotal);

//      Permissions for Bluetooth, Location and Camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if ((this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                    (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            } else { initMsco(); }
        } else { initMsco(); }

//        if(savedInstanceState != null){
//            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
//            if(restoredBarcode != null){
////                tvBarcode.setText(restoredBarcode.rawValue);
//                barcodeResult = restoredBarcode;
//            }
//        }
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
        scanHandler.post(scanRunnable);

////      Barcode Scanner
//        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        assertNotNull(fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startScan();
//            }
//        });
    }

//    private void startScan() {
//        /**
//         * Build a new MaterialBarcodeScanner
//         */
//        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
//                .withActivity(MainActivity.this)
//                .withEnableAutoFocus(true)
//                .withBleepEnabled(true)
//                .withBackfacingCamera()
//                .withCenterTracker()
//                .withText("Scanning...")
//                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
//                    @Override
//                    public void onResult(Barcode barcode) {
//                        barcodeResult = barcode;
////                        TODO: find the different types of codes to scan. 5 is just assumed number.
//                        if (barcodeResult.rawValue.length() > 5) {
////                            result.setText(barcode.rawValue);
//                            getBarcodeDetails(barcodeResult.rawValue);
//                        }
//                        else Toast.makeText(MainActivity.this, "Invalid Scan! Please place the barcode parallel to camera.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .build();
//        materialBarcodeScanner.startScan();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(BARCODE_KEY, barcodeResult);
//        super.onSaveInstanceState(outState);
//    }

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

//    /**
//     * Retrofit API and cart functionality
//     */
//    void getBarcodeDetails(String barcodeRaw) {
//        final String resBarcode = barcodeRaw;
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RestApi service = retrofit.create(RestApi.class);
//        Call<Product> call = service.getProductDetails(resBarcode);
//
//        call.enqueue(new Callback<Product>() {
//            @Override
//            public void onResponse(Call<Product> call, Response<Product> response) {
//                try {
//                    String resPrice = response.body().getPrice();
//                    String resTitle = response.body().getTitle();
//                    String resDescription = response.body().getBody();
//
//                    Log.d("POST SCAN API: ","Price:" + resPrice + " Title: " + resTitle + " Description: " + resDescription);
//
//                    Product scannedProduct = new Product(resBarcode,resPrice,resTitle,resDescription);
//                    cart.addProduct(scannedProduct);
//
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "Item Not Found! Contact Sales Team.", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<Product> call, Throwable t) {}
//        });
//    }

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
                    String resBeaconUuid = response.body().getUuid();
                    String resBeaconMessage = response.body().getMessage();
                    String resBeaconLocation = response.body().getLocation();

                    Log.d("POST BEACON API: ","UUID:" + resBeaconUuid + " Message: " + resBeaconMessage);

                    Beacon beacon = new Beacon(resBeaconUuid,resBeaconMessage,resBeaconLocation);

                    TextView bleAd = (TextView) findViewById(R.id.bleAd);

                    bleAd.setVisibility(View.VISIBLE);

                    final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String name = (mSharedPreference.getString("example_text", "Your Name"));

                    String offersMessage = "Hey " + ", Welcome to " + beacon.getLocation() + "\n Check out our new Offers!";
                    if (name != "Your Name") {
                        offersMessage = "Hey " + name + ", Welcome to " + beacon.getLocation() + "\n Check out our new Offers!";
                    }

//                  Initializing the ImageView
                    bleImageAd1 = (ImageView) findViewById(R.id.bleImageAd1);
                    bleImageAd2 = (ImageView) findViewById(R.id.bleImageAd2);
                    bleImageAd3 = (ImageView) findViewById(R.id.bleImageAd3);
                    bleImageAd4 = (ImageView) findViewById(R.id.bleImageAd4);
                    bleImageAd5 = (ImageView) findViewById(R.id.bleImageAd5);

//                  Loading Image from URL
                    Picasso.with(MainActivity.this)
                            .load("http://www.samplesalesites.com/wp-content/uploads/2010/10/vintage_chanel.jpg")
                            .into(bleImageAd1);

                    Picasso.with(MainActivity.this)
                            .load("https://shopunder.com/blog/wp-content/uploads/2017/08/Prada-Sale.jpg")
                            .into(bleImageAd2);

                    Picasso.with(MainActivity.this)
                            .load("https://www.filepicker.io/api/file/QOvnEnZDSy2dwF2MHDGg/convert?quality=60&crop=0,0,0,0")
                            .into(bleImageAd3);

                    Picasso.with(MainActivity.this)
                            .load("https://smartcanucks.ca/wp-content/uploads/2014/06/lacoste-sale.jpg")
                            .into(bleImageAd4);

                    Picasso.with(MainActivity.this)
                            .load("http://eastgateshopping.co.uk/assets/images/Shops/h-m.jpg")
                            .into(bleImageAd5);

//                  https://shopunder.com/blog/wp-content/uploads/2017/08/Prada-Sale.jpg
//                  https://www.filepicker.io/api/file/QOvnEnZDSy2dwF2MHDGg/convert?quality=60&crop=0,0,0,0
//                  https://www.shefinds.com/files/2011/05/portero-louis-vuitton-sale.jpg
//                  https://smartcanucks.ca/wp-content/uploads/2014/06/lacoste-sale.jpg
//                  https://www.shefinds.com/files/2011/05/portero-louis-vuitton-sale.jpg
//                  http://eastgateshopping.co.uk/assets/images/Shops/h-m.jpg

                    bleAd.setText( offersMessage );



                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Item Not Found! Contact Sales Team.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Beacon> call, Throwable t) {}
        });
    }

    /**
     * Bluetooth LE Scanner
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //UUID detection
                String uuid = hexString.substring(0, 8) + "-" +
                        hexString.substring(8, 12) + "-" +
                        hexString.substring(12, 16) + "-" +
                        hexString.substring(16, 20) + "-" +
                        hexString.substring(20, 32);

                // major
                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                // minor
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                Log.i(LOG_TAG, "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor);

                TextView bleAd = (TextView) findViewById(R.id.bleAd);
                if(TextUtils.isEmpty(bleAd.getText())){
                    Log.i(LOG_TAG, "API Request");
                    getCustomerDetails(uuid);
                }

            }
        }
    };

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                if (btAdapter != null) {
                    btAdapter.stopLeScan(leScanCallback);
                }
            } else {
                if (btAdapter != null) {
                    btAdapter.startLeScan(leScanCallback);
                }
            }
            isScanning = !isScanning;
            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
