package com.cogent.QQ;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.R.string;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.View.OnClickListener;

public class SuperWiFi extends LocationActivity{

    int TESTTIME;
    int TESTTIMENOW;
    int TIME_INTERVAL;
    int AP_NUM;
    int max_num_of_ap;

    String AP_LIST[];
    int AP_scanned_flag[];
    int AP_ONTO_LOCATION[];
    int AP_SCAN_HISTORY[][];
    private int[] APRSS;


    static final String TAG = "SuperWiFi";
    static SuperWiFi wifi = null;
    static Object sync = new Object();

    /////
    WifiManager wm = null;
    private ScanResult mScanResult;
    private StringBuffer sb=new StringBuffer();
    private List<ScanResult> wifitable;
    //Vector<String> AP_SCAN_HISTORY[]=new Vector[10];
    //Vector  AP_SCAN_HISTORY=new Vector(9);

    //////
    private Vector<String> scanned = null;
    boolean isScanning = false;

    private FileOutputStream out;
    private int p;
    public SuperWiFi(Context context)
    {

        //max_num_of_ap=0;
        this.wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();
		/*for(int i=0;i<=AP_NUM-1;i++){
			AP_LIST[i]="myt"+i;              //初始化AP_LIST!!!


		}*/
		/*
		AP_LIST[0]="MobileWiFi-bec3";
		AP_LIST[1]="MobileWiFi-be7c";
		AP_LIST[2]="MobileWiFi0";
		AP_LIST[3]="MobileWiFi1";
		AP_LIST[4]="MobileWiFi2"; */


    }
    public SuperWiFi(Context context,int testtime,int time_interval)
    {
        this.TESTTIME=testtime;
        this.TIME_INTERVAL=time_interval;
        this.wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();

    }

    public void set_para(int testtime,int timeinterval){
        AP_NUM=-1;

        AP_LIST=new String[100];
        AP_scanned_flag=new int[100];
        AP_ONTO_LOCATION=new int[100];
        AP_SCAN_HISTORY=new int[100][100];
        APRSS=new int[100];
        TESTTIME=testtime;
        TESTTIMENOW=0;
        TIME_INTERVAL=timeinterval;
        for(int i=0;i<=99;i++){
            AP_LIST[i]="NULL";
            AP_scanned_flag[i]=0;
            AP_SCAN_HISTORY[i][0]=-999;
            AP_ONTO_LOCATION[i]=0;

        }
        TESTTIME=testtime;

        TIME_INTERVAL=timeinterval;

        //AP_NUM=5;

        //AP_LIST=new String[AP_NUM];
        //AP_ONTO_LOCATION=new int[AP_NUM];
        //AP_SCAN_HISTORY=new int[AP_NUM][TESTTIME];

        //APRSS=new int[AP_NUM];

        //AP_LIST[0]="MobileWiFi-bec3";
        //AP_LIST[1]="MobileWiFi-be7c";
        //AP_LIST[2]="MobileWiFi-be86";
        //AP_LIST[3]="MobileWiFi-be79";
        //AP_LIST[4]="MobileWiFi-be81";
        //



    }
    public void ScanRss(){
        //Log.i("wifi","?!");
        startScan();
    }

    public boolean isscan(){
        return isScanning;

    }


    public Vector<String> getRSSlist(){

        return scanned;

    }

    public static SuperWiFi getInstance(Context context)
    {
        if(wifi==null)
        {
            synchronized(sync)
            {
                if(wifi==null)
                {
                    wifi = new SuperWiFi(context);
                }
            }
        }

        return wifi;
    }

    public boolean setNetwork(String name)
    {
        //make sure wifi is enabled
        if(wm==null)
            return false;
        try
        {
            if(!wm.isWifiEnabled())//这里修改了
            {
                wm.setWifiEnabled(true);
            }

            //get network
            List<WifiConfiguration> wc = wm.getConfiguredNetworks();


            //set specific network
            Iterator<WifiConfiguration> it = wc.iterator();
            while(it.hasNext())
            {
                WifiConfiguration network = it.next();

                if(network.SSID.equals(name))//找到指定SSID的网络后，断开该网络，再重连
                {
                    wm.disconnect();
                    wm.enableNetwork(network.networkId, true);
                    wm.reconnect();
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
        finally
        {
        }

        return false;

    }

    public ArrayList<String> getAPHistory()
    {
        List<WifiConfiguration> networks = this.wm.getConfiguredNetworks();
        ArrayList<String> netList = new ArrayList<String>();
        for(WifiConfiguration network:networks){
            netList.add(network.SSID);
        }
        return netList;
    }

    public Vector<String> getScanned()
    {
        if(this.isScanning==false)
        {
            this.isScanning = true;
            startScan();
        }
        return this.scanned;
    }
    ///
	/*public void getAllNetWorkList(){
		// 每次点击扫描之前清空上一次的扫描结果
		if(sb!=null){
			sb=new StringBuffer();
		}
		//开始扫描网络
		wm.startScan();
		list=wm.getScanResults();
		if(list!=null){
			for(int i=0;i<list.size();i++){
				//得到扫描结果
				mScanResult=list.get(i);
				sb=sb.append(mScanResult.BSSID+"  ").append(mScanResult.SSID+"   ")
						.append(mScanResult.capabilities+"   ").append(mScanResult.frequency+"   ")
						.append(mScanResult.level+"\n\n");
			}
			allNetWork.setText("扫描到的wifi网络：\n" + sb.toString());
		}
	}*/






    /////
    private void startScan()//
    {



        this.isScanning = true;
        scanned.clear();

        Thread scanThread = new Thread(new Runnable()
        {

            public void run() {
                // TODO Auto-generated method stub
                //performScan();
                scanned.clear();
                for(int j=0;j<=99;j++){
                    APRSS[9]=0;
                }
                p=1;

                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日    HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);

				/*
				for(int k=1;k<=AP_NUM;k++){
					write2file("RSS-IWCTAP"+k+".txt","testID: "+testID+" TestTime: "+str+" BEGIN\n");
				}*/

                //wm.startScan();
                //wifitable = wm.getScanResults();

                while(p<=TESTTIME)
                {
                    performScan();
//					try {
//						Thread.sleep(1000);//每3000扫描一次，直到出错
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

                    p=p+1;
                }
                for(int i=1;i<=AP_NUM+1;i++){
                    scanned.add(
                            "location:  "+AP_ONTO_LOCATION[i-1]+"\n"+
                                    "SSID:  "+AP_LIST[i-1]+"\n"+"Scan_History:  ");
                    for(int j=0;j<=TESTTIME-1;j++){
                        scanned.add(" "+AP_SCAN_HISTORY[i-1][j]);
                    }

                    //+"RSS_Value:  "+APRSS[i-1]/TESTTIME+"\n"
                    scanned.add("\n");
                    scanned.add("\n");
                    scanned.add("\n");
					/*
					Iterator<String> it = AP_SCAN_HISTORY[i-1].iterator();
					while(it.hasNext())
					{
						String temp = it.next();
						scanned.add(temp+" ");
					}
					scanned.add("\n");
					scanned.add("\n");
					scanned.add("\n");*/


                }


				/*for(int k=1;k<=AP_NUM;k++){
					write2file("RSS-IWCTAP"+k+".txt","testID:"+testID+"END\n");
				}*/
                TESTTIMENOW=0;
                isScanning=false;

            }

        });


        scanThread.start();
    }

    private void performScan()
    {
       // Log.i("wifi","perform");
        if(wm==null) {
           // Log.i("wifi","fuck");
            return;
        }
        try
        {
            //Log.i("wifi","!!!");
            if(!wm.isWifiEnabled())
            {
                wm.setWifiEnabled(true);
               // Log.i("wifi", "enabled");
            }

			/*if(p==1){
				for(int kk=1;kk<=4;kk++){
					wm.startScan();
					try {
						Thread.sleep(2000);//每3000扫描一次，直到出错
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					wm.getScanResults();
				}

			}*/

            wm.startScan();

            //Log.i("wifi","start");
            try {
                Thread.sleep(TIME_INTERVAL);//每TIME_INTERVAL扫描一次，直到出错
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            //this.scanned.clear();
            int flag=0;
            List<ScanResult> sr = wm.getScanResults();
            Iterator<ScanResult> it = sr.iterator();
            for(int k1=0;k1<=AP_NUM;k1++){
                AP_scanned_flag[k1]=0;

            }
            while(it.hasNext())
            {
                ScanResult ap = it.next();
                flag=0;
               // Log.i("wifi",ap.SSID);
                for(int k1=0;k1<=AP_NUM;k1++){
                    if (ap.SSID.equals(AP_LIST[k1])){

                        APRSS[k1]=APRSS[k1]+ap.level;
                        AP_SCAN_HISTORY[k1][TESTTIMENOW]=ap.level;
                        AP_scanned_flag[k1]=1;
                        flag=1;
                    }


                }
                if(flag==0){
                    AP_NUM++;
                    AP_LIST[AP_NUM]=ap.SSID;
                    APRSS[AP_NUM]=ap.level;
                    AP_SCAN_HISTORY[AP_NUM][TESTTIMENOW]=ap.level;
                    AP_scanned_flag[AP_NUM]=1;
                }

            }
            for(int k1=0;k1<=AP_NUM;k1++){
                if(AP_scanned_flag[k1]==0){
                    AP_SCAN_HISTORY[k1][TESTTIMENOW]=0;
                }

            }


            TESTTIMENOW++;
        }


        //this.isScanning=false;

        catch (Exception e)
        {

            this.isScanning = false;
            this.scanned.clear();
            Log.i(TAG, e.toString());
        }
    }

    void write2file(String filename, String a){

        try {
            final String path = Environment.getExternalStorageDirectory()
                    .getPath()
                    +"/"+filename;
            //final File fileName = new File(path2);
            File file = new File(path);
            if (!file.exists()){
                file.createNewFile();}

            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
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

}
