package com.cogent.QQ;

/**
 * Created by mayintong on 2015/8/6.
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public class MyCompass extends Activity implements SensorEventListener
{
    //private CompassView    _compassView;
    private boolean        mRegisteredSensor;
    //定义SensorManager
    private SensorManager mSensorManager;
    public float x;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    /** Called when the activity is first created. */
    public MyCompass(Context context) {
        mRegisteredSensor = false;
        //取得SensorManager实例
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);//.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                Sensor.TYPE_MAGNETIC_FIELD);
    }



    @Override
    protected void onPause()
    {
        if (mRegisteredSensor)
        {
            //如果调用了registerListener
            //这里我们需要unregisterListener来卸载/取消注册
            mSensorManager.unregisterListener(this);
            mRegisteredSensor = false;
        }
        super.onPause();
    }
    //当进准度发生改变时
    //sensor->传感器
    //accuracy->精准度
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
    // 当传感器在被改变时触发
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // 接受方向感应器的类型
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }

        calculateOrientation();
    }


    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        x=values[0];
//        Log.i("TAG", values[0] + "");
//        if (values[0] >= -5 && values[0] < 5) {
//            //azimuthAngle.setText("正北");
//        } else if (values[0] >= 5 && values[0] < 85) {
//            Log.i("TAG", "en");
//            //azimuthAngle.setText("东北");
//        } else if (values[0] >= 85 && values[0] <= 95) {
//            Log.i("TAG", "ee");
//            //azimuthAngle.setText("正东");
//        } else if (values[0] >= 95 && values[0] < 175) {
//            Log.i("TAG", "es");
//            //azimuthAngle.setText("东南");
//        } else if ((values[0] >= 175 && values[0] <= 180)
//                || (values[0]) >= -180 && values[0] < -175) {
//            Log.i("TAG", "正南");
//            //azimuthAngle.setText("正南");
//        } else if (values[0] >= -175 && values[0] < -95) {
//            Log.i("TAG", "ss");
//            //azimuthAngle.setText("西南");
//        } else if (values[0] >= -95 && values[0] < -85) {
//            Log.i("TAG", "ww");
//            //azimuthAngle.setText("正西");
//        } else if (values[0] >= -85 && values[0] < -5) {
//            Log.i("TAG", "wn");
//            //azimuthAngle.setText("西北");
//        }
    }
}