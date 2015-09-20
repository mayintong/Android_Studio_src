package com.cogent.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.cogent.DataBase.BLConstants;
import com.cogent.QQ.App;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.WindowManager;

/**
 * Created by shawn on 3/20/15.
 */

public class TaskUtil {
    public static boolean isServerReachable(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        //netInfo could be null if network is switching, so wait up to 5 seconds
        for (int i = 0; i < 5; i++) {
            netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            if (checkHttpConnection(10 * 1000))
                return true;

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean checkHttpConnection(int timeout) {
        try {
            URI uri = new URI("http", null, BLConstants.URL_SERVER, BLConstants.PORT_SERVER, null, null, null);
            URL url = uri.toURL();
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "ibeacon");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(timeout);
            urlc.connect();
            //treat it as OK if no exception is thrown
            //if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK)
            urlc.disconnect();
            return true;
        } catch (MalformedURLException e) {
            Log.w("checkHttpConnection", "MalformedURLException");
        } catch (IOException e) {
            Log.w("checkHttpConnection", "IOException");
        } catch (URISyntaxException e) {
            Log.w("checkHttpConnection", "URISyntaxException:" + e.getReason());
        }

        return false;
    }

    public static float calcScaleSize(Bitmap bitmap) {
        float density_dpi;
        float density;
        int xdpi;
        int ydpi;
        float scalesize;

        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);

        density = metric.density;
        density_dpi = (density * 160)/240;

        xdpi = metric.widthPixels;
        ydpi = metric.heightPixels - 140;

        float scaleWidth = ((float)xdpi) / bmpWidth;
        float scaleHeight = (((float)ydpi)-120) / bmpHeight;
        scalesize = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
        System.out.println("scalesize" + scalesize);
        
        return scalesize;
    }
    
    public static Bitmap reSizeBitmap(Bitmap bitmap, float scalesize) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();

        matrix.postScale(scalesize, scalesize);
        
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight,
                matrix, true);
        
        return  resizeBmp;
    }
}
