package com.cogent.Communications;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Handler;
import android.os.Looper;

import com.cogent.Communications.BLIObserver;

/**
 * Created by shawn on 3/30/15.
 */

public class BLNotifier {
    public final static int TYPE_UPDATE_NETWORK = 0;
    public final static int TYPE_AUTO_UPDATE_LOCATION = 1;
    public final static int TYPE_MANUAL_UPDATE_LOCATION = 2;
    public final static int TYPE_SELF = 1;
    public final static int TYPE_OTHERS = 2;
    public final static int TYPE_BLE_NOT_SUPPORT = 3;
    public final static int TYPE_BLE_NOT_ENABLED = 4;
    
    public final static String ARG_SERVER_REACHABLE = "SERVER_REACHABLE";

    private static List <BLIObserver> mObserverList;
    private static Looper mLooper;

    public BLNotifier(Looper looper) {
        mObserverList = new CopyOnWriteArrayList<BLIObserver>();
        mLooper = looper;
    }

    public static void registerObserver(BLIObserver obs) {
        mObserverList.add(obs);
    }

    public static void unregisterObserver(BLIObserver obs) {
        mObserverList.remove(obs);
    }

    public static void notifyUi(int actionType, String args) {
        if (mObserverList == null || mObserverList.isEmpty())
            return;

        Handler handler = new Handler(mLooper);

        for (BLIObserver ob : mObserverList) {
            final BLIObserver mOb = ob;
            final int mActionType = actionType;
            final String mArgs = args;

            final Runnable uiUpdateThread = new Runnable() {
                public void run() {
                    mOb.onBLUpdate(mActionType, mArgs);
                }
            };

            handler.post(uiUpdateThread);
        }
    }
}
