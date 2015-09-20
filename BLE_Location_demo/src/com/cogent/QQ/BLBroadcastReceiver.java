package com.cogent.QQ;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cogent.QQ.BeaconService;

/**
 * Created by shawn on 3/15/15.
 */

public class BLBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startServiceIntent = new Intent(context, BeaconService.class);
        context.startService(startServiceIntent);
    }
}
