package com.reonios.mscoprototype;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by reonios on 09/02/18.
 */

public class BleScan {
    public Activity activity;

    //  Bluetooth LE Scanner
    private ArrayList<String> bleUuidArrayList;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String LOG_TAG = "MainActivity";


    public BleScan(Activity _activity, BluetoothAdapter btAdapter, ArrayList<String> bleUuidArrayList) {
        this.activity = _activity;
        this.btAdapter = btAdapter;
        this.bleUuidArrayList = bleUuidArrayList;
    }

    // scanHandler.post(scanRunnable);
    public void startBleScan() {
        bleUuidArrayList.clear();
        btAdapter.startLeScan(leScanCallback);
    }
    public void stopBleScan() { btAdapter.stopLeScan(leScanCallback); }

    //  Bluetooth LE Scanner
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
//                Log.i(LOG_TAG, "DEVICE: " + device + "\\RSSI: " + rssi + "\\SCANRECORD" + scanRecord + " \\DISTANCE:" + calculateBeaconDistance(-59, rssi));

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

//                Log.i(LOG_TAG, "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor);

//                TextView bleAd = (TextView) activity.findViewById(R.id.bleAd);
//                if (TextUtils.isEmpty(bleAd.getText()) && !bleUuidArrayList.contains(uuid)) {
                    bleUuidArrayList.add(uuid);
//                    getCustomerDetails(uuid);
//                }
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

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    };

    protected double calculateBeaconDistance(float txPower, double rssi) {

        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

}
