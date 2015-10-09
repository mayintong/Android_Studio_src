package com.cogent.BLE;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cogent.QQ.TestControl;

/**
 * Created by TongXinyu on 15/9/20.
 */



public class DeviceListAdapter {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<byte[]> mRecords;
    private ArrayList<Integer> mRSSIs;

    public DeviceListAdapter(Activity par) {
        super();
        mDevices  = new ArrayList<BluetoothDevice>();
        mRecords = new ArrayList<byte[]>();
        mRSSIs = new ArrayList<Integer>();
    }

    public ArrayList<BluetoothDevice> GetDevice()
    {
        return mDevices;
    }

    public ArrayList<Integer>  GetRSSI()
    {
        return mRSSIs;
    }

    public ArrayList<byte[]>  GetRecord()
    {
        return mRecords;
    }

    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        int index = mDevices.indexOf(device);
            if (index != -1) {
                mRSSIs.set(index, rssi);
                mRecords.set(index, scanRecord);
            } else {
                mDevices.add(device);
                mRSSIs.add(rssi);
                mRecords.add(scanRecord);
            }
    }

}
