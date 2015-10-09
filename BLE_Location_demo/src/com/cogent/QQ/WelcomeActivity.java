package com.cogent.QQ;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;

import com.cogent.Communications.BLIObserver;

/**欢迎动画activity*/
public class WelcomeActivity extends Activity {
    private static final String DEBUG_TAG = "WelcomeActivity";
    protected static BeaconService mBoundService = null;
    private static List <BLIObserver> mObserverList = new CopyOnWriteArrayList<BLIObserver>();

    private static ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            BeaconService mBoundService = ((BeaconService.BLBinder)service).getService();
            WelcomeActivity.setBoundService(mBoundService);
            if (mBoundService != null)
                WelcomeActivity.syncWithService(mBoundService);
        }

        public void onServiceDisconnected(ComponentName className) {
            WelcomeActivity.setBoundService(null);
        }
    };
    
    public static BeaconService getBoundService() {
        return mBoundService;
    }

    public static void setBoundService(BeaconService boundService) {
        mBoundService = boundService;
    }
    
    protected static void syncWithService(BeaconService service) {
        if (service == null)
            return;

        if (!mObserverList.isEmpty()) {
            for (BLIObserver obs : mObserverList) {
                mBoundService.registerObserver(obs);
            }
        }
    }
    
    protected static void registerObserver(BLIObserver obs) {
        mObserverList.add(obs);

        if (mBoundService != null)
            mBoundService.registerObserver(obs);
    }

    protected static void unregisterObserver(BLIObserver obs) {
        mObserverList.remove(obs);

        if (mBoundService != null)
            mBoundService.unregisterObserver(obs);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        startBindServiceThread();
    }

    private void startBindServiceThread() {
        Thread bindServiceThread = new Thread() {
            @Override
            public void run() {
                try {
                    Intent serviceIntent = new Intent(WelcomeActivity.this, BeaconService.class);
                    startService(serviceIntent);
                    if (bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)) {
                        //need to wait some seconds to get the instance
                        while (mBoundService == null)
                            Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(DEBUG_TAG, "======start LoginActivity");
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        bindServiceThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}