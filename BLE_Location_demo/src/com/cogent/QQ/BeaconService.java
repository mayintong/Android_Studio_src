package com.cogent.QQ;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cogent.util.TaskUtil;
import com.cogent.Communications.BLIObserver;
import com.cogent.Communications.BLNotifier;

/**
 * Created by shawn on 3/15/15.
 */

public class BeaconService extends Service {
    private static final String DEBUG_TAG = "BeaconService";
    private static Context appContext;
    private BroadcastReceiver mConnReceiver;
    private BeaconScanner mBeaconScanner = null;
    private final IBinder mBinder = new BLBinder();

    public class BLBinder extends Binder {
        BeaconService getService() {
            return BeaconService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG, "bind");
        return mBinder;
    }

    public void startScanner() {
        Log.d(DEBUG_TAG, "start a scanner");
        // init a scanner
        mBeaconScanner = new BeaconScanner(appContext);
        mBeaconScanner.start();
    }

    public void stopScanner() {
        stopScan();
    }

    public void startScan() {
        if (mBeaconScanner != null) {
            mBeaconScanner.start();
        }
    }

    public void stopScan() {
        if (mBeaconScanner != null) {
            mBeaconScanner.stop();
        }
    }

    public BeaconScanner getScaner() {
        return mBeaconScanner;
    }

    public void registerObserver(BLIObserver obs) {
        BLNotifier.registerObserver(obs);
    }

    public void unregisterObserver(BLIObserver obs) {
        BLNotifier.unregisterObserver(obs);
    }

    private void checkServerReachability(Context context, Intent intent) {
        final Context taskContext = context;
        final Intent taskIntent = intent;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return TaskUtil.isServerReachable(taskContext, taskIntent);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                //Map<String, String> args = new HashMap<String, String>();
                //args.put(BLNotifier.ARG_SERVER_REACHABLE, String.valueOf(result));
               // BLNotifier.notifyUi(BLNotifier.TYPE_UPDATE_NETWORK, args);
            }
        }.execute();
    }

    @Override
    public void onCreate() {
        Log.d(DEBUG_TAG, "service onCreate");
        appContext = App.getContext();
        new BLNotifier(this.getMainLooper());
        mConnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //checkServerReachability(context, intent);
            }
        };
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

       // startScanner();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(DEBUG_TAG, "service is started");
        super.onStartCommand(intent, flags, startId);
        startForeground(0, null);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopScanner();

        if (mConnReceiver != null)
            unregisterReceiver(mConnReceiver);

        stopForeground(false);
        Log.d(DEBUG_TAG, "service is stopped");
    }
}
