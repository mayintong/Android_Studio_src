package com.cogent.QQ;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.InputStream;
import java.io.OutputStream;
public class StepCalculater implements Runnable, SensorEventListener {
	/** Called when the activity is first created. */

	//设置LOG标签
	private Button mWriteButton, mStopButton;
	private boolean doWrite = false;
	private SensorManager sm;
	private float lowX = 0, lowY = 0, lowZ = 0;
	private final float FILTERING_VALAUE = 0.1f;

	private boolean stepcalculate;
	public int stepcounter,spacecounter;
	public MyCompass mycom;

	private boolean instep;
	public int step_south;
	public int step_north;
	public int step_east;
	public int step_west;

	private float[] accelerometerValues = new float[3];
	private float[] magneticFieldValues = new float[3];
	public StepCalculater(Context context) {

		//创建一个SensorManager来获取系统的传感器服务
		sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        /*
         * 最常用的一个方法 注册事件
         * 参数1 ：SensorEventListener监听器
         * 参数2 ：Sensor 一个服务可能有多个Sensor实现，此处调用getDefaultSensor获取默认的Sensor
         * 参数3 ：模式 可选数据变化的刷新频率
         * */
		//  注册加速度传感器
		sm.registerListener(this,
				sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);//.SENSOR_DELAY_NORMAL);
//		sm.registerListener(this,
//				sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//				Sensor.TYPE_MAGNETIC_FIELD);

		mycom=new MyCompass(context);
		stepcalculate=false;
		stepcounter=5;
		spacecounter=0;
		instep=false;
		//Log.i("1","1");
	}

	public void startstep(){
		step_south=0;
		step_north=0;
		step_east=0;
		step_west=0;
		stepcounter=0;
		spacecounter=0;
		instep=false;
		stepcalculate=true;
	}
	public void stopstep(){
		stepcalculate=false;
	}




	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//ACT.setText("onAccuracyChanged被触发");		
	}

	public void onSensorChanged(SensorEvent event) {
		String message = new String();
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//图解中已经解释三个值的含义
			float X = event.values[0];
			float Y = event.values[1];
			float Z = event.values[2];

			//Low-Pass Filter
			lowX = X * FILTERING_VALAUE + lowX * (1.0f - FILTERING_VALAUE);
			lowY = Y * FILTERING_VALAUE + lowY * (1.0f - FILTERING_VALAUE);
			lowZ = Z * FILTERING_VALAUE + lowZ * (1.0f - FILTERING_VALAUE);

			//High-pass filter
			float highX  = X -  lowX;
			float highY  = Y -  lowY;
			float highZ  = Z -  lowZ;
			double highA = Math.sqrt(highX * highX + highY * highY + highZ * highZ);

			if(stepcalculate){
				//Log.e("!!!!!!!!!!!!!!!!","3");
				if(instep){

					if(highY<0){
						spacecounter=spacecounter+1;
						if(spacecounter>17){
							instep=false;
							Log.e("!!!!!!!!!!!!!!!!","3");
							if(mycom.x>0 && mycom.x<45 || mycom.x<0 && mycom.x>-45)
							{
								step_north++;
							}
							else if(mycom.x>45 && mycom.x<135)
							{
								step_east++;
							}
							else if(mycom.x>135 && mycom.x<180 || mycom.x<0-135 && mycom.x>-180)
							{
								step_south++;
							}
							else if(mycom.x>-135 && mycom.x<-45)
							{
								step_west++;
							}
							stepcounter=stepcounter+1;
							Log.e("X",String.valueOf(get_step_offset_X()));
							Log.e("Y",String.valueOf(get_step_offset_Y()));
							Log.e("stepcounter",String.valueOf(stepcounter));
							Log.e("n",String.valueOf(step_north));
							Log.e("s",String.valueOf(step_south));
							Log.e("e",String.valueOf(step_east));
							Log.e("w",String.valueOf(step_west));

							//Log.e("!!!!!!!!!!!!!!!!","3");
						}
					}else{
						spacecounter=0;
					}
				}else{
					if(highY>=0){
						instep=true;
						spacecounter=0;
					}
				}
//				if(highA<3){
//				stepcounter=stepcounter+1;
//			   }
			}





			//DecimalFormat df = new DecimalFormat("#,##0.000");

			//message = df.format(highX) + "  ";
			//message += df.format(highY) + "  ";
			//message += df.format(highZ) + "  ";
			//message += df.format(highA) + "\n";
			//message = df.format(highA) + "\n";
			if (stepcalculate) {
				//write2file(""+highY+"\n");
				//writeFileSdcard("acc.txt", message);
				//Log.i("3","3");
			}
		}

		//Log.e("+++",String.valueOf(stepcounter));

//		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//			accelerometerValues = event.values;
//		}
//		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//			magneticFieldValues = event.values;
//		}
//
//		calculateOrientation();

	}

//	private void calculateOrientation() {
//		float[] values = new float[3];
//		float[] R = new float[9];
//		SensorManager.getRotationMatrix(R, null, accelerometerValues,
//				magneticFieldValues);
//		SensorManager.getOrientation(R, values);
//		values[0] = (float) Math.toDegrees(values[0]);
//
//		Log.i("TAG", values[0] + "");
//		if (values[0] >= -5 && values[0] < 5) {
//			//azimuthAngle.setText("正北");
//		} else if (values[0] >= 5 && values[0] < 85) {
//			Log.i("TAG", "东北");
//			//azimuthAngle.setText("东北");
//		} else if (values[0] >= 85 && values[0] <= 95) {
//			Log.i("TAG", "正东");
//			//azimuthAngle.setText("正东");
//		} else if (values[0] >= 95 && values[0] < 175) {
//			Log.i("TAG", "东南");
//			//azimuthAngle.setText("东南");
//		} else if ((values[0] >= 175 && values[0] <= 180)
//				|| (values[0]) >= -180 && values[0] < -175) {
//			Log.i("TAG", "正南");
//			//azimuthAngle.setText("正南");
//		} else if (values[0] >= -175 && values[0] < -95) {
//			Log.i("TAG", "西南");
//			//azimuthAngle.setText("西南");
//		} else if (values[0] >= -95 && values[0] < -85) {
//			Log.i("TAG", "正西");
//			//azimuthAngle.setText("正西");
//		} else if (values[0] >= -85 && values[0] < -5) {
//			Log.i("TAG", "西北");
//			//azimuthAngle.setText("西北");
//		}
//	}
	public int getsteps(){
		return stepcounter;
	}
	public int get_step_offset_X()
	{
		int x=step_east-step_west;

		return x;

	}

	public int get_step_offset_Y()
	{

		int y=step_north-step_south;
		return y;

	}
	private void write2file(String a){

		try {

			File file = new File("/sdcard/acc.txt");
			if (!file.exists()){
				file.createNewFile();}

			// 打开一个随机访问文件流，按读写方式   
			RandomAccessFile randomFile = new RandomAccessFile("/sdcard/acc.txt", "rw");
			// 文件长度，字节数   
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(a);
			//Log.e("!","!!");
			randomFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		// TODO Auto-generated method stub

	}





}