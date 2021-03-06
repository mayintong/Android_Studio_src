package com.cogent.Communications;

import android.app.ActivityManager;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.cogent.QQ.App;

/**
 * Created by shawn on 3/27/15.
 */

public class RequestManager {
    public static RequestQueue mRequestQueue= Volley.newRequestQueue(App.getContext());
    
    private RequestManager() {
        // no instances
    }
    
    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

}

