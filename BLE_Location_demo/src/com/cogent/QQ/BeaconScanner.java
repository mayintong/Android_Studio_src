package com.cogent.QQ;

import java.util.Map;
import java.util.HashMap;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.VolleyError;
import java.text.SimpleDateFormat;

import com.cogent.Communications.RequestManager;
import com.cogent.util.HttpUtil;
import com.cogent.Communications.BLNotifier;
import com.cogent.DataBase.BLConstants;
import com.cogent.Communications.Communications;

//package com.example.adr_client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

/**
 * Created by shawn on 3/18/15.
 */

public class BeaconScanner implements
        Communications.ResponseListener,
        Communications.ErrorResponseListener{
    private static final String DEBUG_TAG = "Scanner";
    private Context mContext;
    private Communications mComm;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScannerAvailable = true;
    private boolean mScanning = false;
    private boolean mLogin = false;

    private String record_mac = "";
    private String post_mac = "";
    private int[] test_rssi = new int[500];
    private String[] test_mac = new String[500];
    private int i = 0;

    private long current_timer;
    private long last_timer;
    ////
    static Object sync = new Object();
    static int TESTTIME=25;
    static int TESTINTERVAL=1000;
    private String[] APLIST = new String[10];
    private Vector<String> scanned = null;
    private int[] APRSS=new int[10];
    private int p;
    private int APNUM=10;
    WifiManager wm = null;

    ////
    public BeaconScanner(Context context) {
        mContext = context;
       // wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mComm = new Communications(this);
        mComm.setOnResponseListener(this);
        mComm.setOnErrorResponseListener(this);
    }

    public void start(String rss) {
        //checkScanner();


        this.mScanning=false;
        scanLeDevice(rss);
//        if (!App.getCookie().isEmpty())
//            mLogin = true;
//
//        Log.d(DEBUG_TAG, "start Beacon scanner");
//        Log.d(DEBUG_TAG, "mScannerAvailable is " + mScannerAvailable);
//        if (mScannerAvailable && mLogin) {
//            if (!mScanning) {
//                scanLeDevice(true);
//            } else {
//                Log.d(DEBUG_TAG, "Scanner already started");
//            }
//        } else {
//            Log.d(DEBUG_TAG, "create scanner failed");
//        }
    }

    public void stop() {
        Log.d(DEBUG_TAG, "stop Beacon scanner");
        if (mScanning) {
            RequestManager.cancelAll(this);
            scanLeDevice("-");
        } else {
            Log.d(DEBUG_TAG, "Scanner is not running");
        }
    }

//    private void checkScanner() {
//        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            mScannerAvailable = false;
//            BLNotifier.notifyUi(BLNotifier.TYPE_BLE_NOT_SUPPORT, null);
//        }
//
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            // TODO jump to bluetooth setting
//            mScannerAvailable = false;
//            BLNotifier.notifyUi(BLNotifier.TYPE_BLE_NOT_ENABLED, null);
//        }
//    }

    private void scanLeDevice(String rss) {
        if (rss!="-" && !mScanning) {
            mScanning = true;
            current_timer = System.currentTimeMillis();          ;
            Map<String, String> track_map = new HashMap<String, String>();
            track_map.put("rss", rss);
            mComm.doVolleyPost(BLConstants.API_TEST5, track_map, Communications.TAG_SINGLE_TRACK);
        } else {
            mScanning = false;
            // mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.d(DEBUG_TAG, "stop scanning");
        }
    }





    public String analyse(String[] mac,int[] rssi) {

        int rssi_test ;
        String mac_test ;
        int num = 1;

        int [] rssi_i = new int[50];
        String [] mac_i = new String[50];
        int j[] = new int[50];

        rssi_i[0] = rssi[0];
        mac_i[0] = mac[0];
        j[0] = 1;

        int z ;
        while(num <= i ){

            z = 0;

            while(z < 49)
            {

                //System.out.println(mac_i[z]);
                if(mac_i[z] == null)
                {
                    mac_i[z] = mac[num];
                    rssi_i[z] = rssi[num];
                    j[z]++;
                    break;
                }
                else if(mac_i[z].equals(mac[num]))
                {
                    //System.out.println(mac_i[z]);
                    //System.out.println(rssi_i[z]);
                    //System.out.println(rssi[z]);
                    rssi_i[z] += + rssi[num];
                    //System.out.println(rssi_i[z]);
                    j[z]++;
                    //System.out.println(j[z]);
                    break;
                }
                z++;
            }

            num++;
            //String printf =num + "  "+ mac[num] +"  " + rssi[num];
            //System.out.println(printf);
        }

        z = 1;
        rssi_test = rssi_i[0]/j[0];
        mac_test = mac_i[0];
        while( z <= 49 ){
            if(mac_i[z] != null)
            {

                if(rssi_test < (rssi_i[z]/j[z]))
                {
                    mac_test = mac_i[z];
                    rssi_test = (rssi_i[z]/j[z]);
                }
                z++;
            }
            else
                break;
        }
        //while()
        i = 0;
        String print = "mac:"+ mac_test +"   rssi:" + rssi_test;
        System.out.println(print);
        //record_mac = mac_test;
        if (!record_mac.equals(mac_test)){
            record_mac = mac_test;

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = sDateFormat.format(new java.util.Date());

            Map<String, String> track_map = new HashMap<String, String>();
            track_map.put(BLConstants.ARG_LOCAL_TIME, date);
            track_map.put(BLConstants.ARG_DEV_MAC, record_mac);

            Log.d(DEBUG_TAG, "start tracking...");
            mComm.doVolleyPost(BLConstants.API_POST_SINGLE_TRACK_DATA, track_map, Communications.TAG_SINGLE_TRACK);
        }
        return mac_test;
    }

    @Override
    public void onSuccess(String tag, String response) {
        if (tag.equals(Communications.TAG_SINGLE_TRACK)) {

            BLNotifier.notifyUi(BLNotifier.TYPE_AUTO_UPDATE_LOCATION, response);
        }

    }

    @Override
    public void onFail(String tag, String response) {
        if (tag.equals(Communications.TAG_SINGLE_TRACK)) {
            int error_code = HttpUtil.parseJsonint(response, BLConstants.ARG_ERROR_CODE);
            String error_descrip = tag + BLConstants.MSG_FAIL_DESC + HttpUtil.parse_error(error_code);
            System.out.println(error_descrip);
        }
    }
    @Override
    public void refreshUI(){}
    @Override
    public void onImageResponse(String tag, Bitmap response) {}
    @Override
    public void onResponse(String tag, String response) {
        Log.d(DEBUG_TAG, "TAG:" + tag + "---Response:" + response);
        /*

        String result = HttpUtil.parseJson(response, BLConstants.ARG_REQ_RESULT);
        Boolean parse_result = result.equals(BLConstants.MSG_PASS);

        if (parse_result)
            onSuccess(tag, response);
        else
            onFail(tag, response);
        */
    }

    @Override
    public void onErrorResponse(String tag, VolleyError volleyError) {
        Log.e(DEBUG_TAG, volleyError.getMessage(), volleyError);
    }
}
