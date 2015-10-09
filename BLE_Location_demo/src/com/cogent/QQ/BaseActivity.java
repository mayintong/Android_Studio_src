package com.cogent.QQ;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.VolleyError;


import com.cogent.Communications.Communications;
import com.cogent.Communications.RequestManager;
import com.cogent.DataBase.BLConstants;
import com.cogent.util.HttpUtil;

/**
 * Created by shawn on 4/2/15.
 */

public class BaseActivity extends Activity implements 
        Communications.ResponseListener,
        Communications.ErrorResponseListener {
    private static final String DEBUG_TAG = "BaseActivity";
    protected Communications mComm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mComm = new Communications(this);
        mComm.setOnResponseListener(this);
        mComm.setOnErrorResponseListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestManager.cancelAll(this);
    }
    @Override
    public void onSuccess(String tag, String response) {}
    @Override
    public void onFail(String tag, String response) {
        if (tag.equals(Communications.TAG_LOGIN)
            || tag.equals(Communications.TAG_REGISTER)
            || tag.equals(Communications.TAG_QUERY_POSITION)) {
            int error_code = HttpUtil.parseJsonint(response, BLConstants.ARG_ERROR_CODE);
            String error_descrip = tag + BLConstants.MSG_FAIL_DESC + HttpUtil.parse_error(error_code);
            refreshUI();
            showNotification(error_descrip);
        }
    }
    @Override
    public void refreshUI(){}
    @Override
    public void onImageResponse(String tag, Bitmap response) {}
    @Override
    public void onResponse(String tag, String response) {
        Log.d(DEBUG_TAG, "TAG:" + tag + "--Response:" + response);
        
        //String result = HttpUtil.parseJson(response, BLConstants.ARG_REQ_RESULT);
        Log.e("XXXXXXXXXXXX",response);
        //onSuccess(tag,response);
       if(response!=null)

           //Boolean parse_result = response.equals(BLConstants.MSG_PASS);

           //if (parse_result)
               onSuccess(tag, response);
           else
               onFail(tag, response);

        //onSuccess(tag,response);


    }

    @Override
    public void onErrorResponse(String tag, VolleyError volleyError) {
        Log.e(DEBUG_TAG, volleyError.getMessage(), volleyError);

        if (tag.equals(Communications.TAG_LOGIN)
            || tag.equals(Communications.TAG_REGISTER)) {
            String notification = BLConstants.MSG_CONN_ERROR;
            refreshUI();
            showNotification(notification);
        }
    }
    
    protected void showNotification(String notification) {
        Toast.makeText(App.getContext(), notification, Toast.LENGTH_LONG).show();
    }
    
    protected void showNotification(int resID) {
        Toast.makeText(App.getContext(), resID, Toast.LENGTH_LONG).show();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_dialog_title)
                    .setMessage(R.string.exit_dialog_message)
                    .setNegativeButton(R.string.negative_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            })
                    .setPositiveButton(R.string.positive_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intent_service = new Intent(BaseActivity.this, BeaconService.class);
                                    stopService(intent_service);
                                    finish();
                                    //强制退出整个系统，不会调用onDestroy
                                    //android.os.Process.killProcess(android.os.Process.myPid());
                                    //System.exit(0);
                                }
                            }).show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
