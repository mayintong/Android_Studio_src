package com.cogent.QQ;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * Created by shawn on 3/16/15.
 */

public class App extends Application {
    private static Context mContext;
    private static String mCookie = "";
    
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLock  wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wl.acquire();
    }

    public static void setCookie(String cookie) {
        Log.d("App", "Cookie is : " + cookie);
        mCookie = cookie;
    }

    public static String getCookie() {
        Log.d("App", "Cookie is : " + mCookie);
        return mCookie;
    }
    
    public static Context getContext() {
        return mContext;
    }
    
}
