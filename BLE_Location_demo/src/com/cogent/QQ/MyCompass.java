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

import java.util.List;

public class MyCompass extends Activity implements SensorEventListener
{
    //private CompassView    _compassView;
    private boolean        mRegisteredSensor;
    //SensorManager
    private SensorManager mSensorManager;
    public float x;
    /** Called when the activity is first created. */
    public MyCompass(Context context) {
        mRegisteredSensor = false;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        Sensor sensor = sensors.get(0);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }



    @Override
    protected void onPause()
    {
        if (mRegisteredSensor)
        {
            //gisterListener
            //ҪunregisterListener
            mSensorManager.unregisterListener(this);
            mRegisteredSensor = false;
        }
        super.onPause();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            x = event.values[SensorManager.DATA_X];

            //_compassView.setDegree(x);

        }
    }
}