package com.cogent.QQ;

import com.android.volley.VolleyError;
import com.cogent.util.ContactUtils;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import net.yoojia.imagemap.ImageMap;
import net.yoojia.imagemap.core.Bubble;
import net.yoojia.imagemap.core.CircleShape;
import net.yoojia.imagemap.core.Lines;
import net.yoojia.imagemap.core.PolyShape;
import net.yoojia.imagemap.core.PolygonalLineShape;
import net.yoojia.imagemap.core.Shape;
import net.yoojia.imagemap.core.ShapeExtension;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;


import com.cogent.Communications.Communications;
import com.cogent.DataBase.BLConstants;
import com.cogent.DataBase.DBHelper;
import com.cogent.ViewMenu.ViewTabber;

import com.cogent.Communications.BLNotifier;
import com.cogent.Communications.BLIObserver;
import com.cogent.util.TaskUtil;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
public class LocationActivity extends BaseActivity implements BLIObserver {
    private static final String DEBUG_TAG = "LocationActivity";
    protected static BeaconService mBoundService = null;
    private ImageMap map;
    private Boolean isOnline = true;
    ViewTabber tabbar;
    public Handler mHandler;

    private StepCalculater StepCal;
    private ImageView ivDeleteText;
    private EditText etSearch;
    private Button btn_search;
    private DBHelper dbHelper;
    private CircleShape[] black;
    private int black_count = 0;
    private Lines[] lines;
    private int lines_count = 0;
    float scalesize = 1;
    private int current_map = -1;
    private int mapid = 0;
    private int cur_x = 0;
    private int cur_y = 0;
    private String cur_rss="0,0,0";
    public int cnt = 3;

    static public Map  PeopleToShow;

    PointF[] tmpp = new PointF[3];

    public class route{
        PointF[] KeyPoint;
        int num_KeyPoint;
        route(PointF[] temp,int num){
           KeyPoint=new PointF[num];
            KeyPoint=temp;
            num_KeyPoint=num;
        }

    }
    route MyRoute;
    //drawline dline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate");
        mBoundService = WelcomeActivity.getBoundService();
        StepCal = new StepCalculater(this);
        StepCal.startstep();
        WelcomeActivity.registerObserver(this);
        setContentView(R.layout.tab_view);
        ContactUtils.init(this);
        tabbar = new ViewTabber(this);
        dbHelper = new DBHelper(this);
        //dline=new drawline(this);
        //setContentView( dline);
        lines = new Lines[100];

        PeopleToShow=new HashMap();

        PeopleToShow.put("MapId",1);
        PeopleToShow.put("myttest1","800,1000,"+"Test1");
        PeopleToShow.put("mytest2","500,500,"+"Test2");


        tmpp[0] = new PointF((float)300.0,(float)300.0);
        tmpp[1] = new PointF((float)200.0,(float)300.0);
        tmpp[2] = new PointF((float)200.0,(float)400.0);


        MyRoute=new route(tmpp,3);
        map = (ImageMap) findViewById(R.id.imagemap);
        initView();

        mHandler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch(msg.what)
                {
                    case 1:
                        //map.clearShapes();
                        Log.e("asbx",""+cnt);
//                        String[] tmp = new String[100];
//                        tmp[0] = "0,500,"+cnt+"00";
//                        tmp[1] = "0,300,"+cnt+"00";
//                        //tmp[2] = "0,100,"+cnt+"00";
//                        tmp[2] = 0 + "," + (300 + StepCal.get_step_offset_X() * 10) + "," + (300 + StepCal.get_step_offset_Y() * 10);
                        //parseLocation(1 + "," + (300 + StepCal.get_step_offset_X() * 10) + "," + (300 + StepCal.get_step_offset_Y() * 10), 2);
                        PeopleToShow.put("user",(300 + StepCal.get_step_offset_X() * 10) + "," + (300 + StepCal.get_step_offset_Y() * 10)+","+"User");

                        parseLocation(PeopleToShow, 2);
                        //cnt = (cnt + 1) % 10;
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int i = 1400;
                Looper.prepare();
                isOnline=false;
                if (isOnline) {

                    SystemClock.sleep(3000);
                    mBoundService.startScanner();
                    System.out.println(btn_search.getText().toString().trim());
                    Map<String, String> query_pos_map = new HashMap<String, String>();
                    //query_pos_map.put(BLConstants.ARG_USER_ID, etSearch.getText().toString().trim());
                    query_pos_map.put("rss", etSearch.getText().toString().trim());
                    mComm.doVolleyPost(BLConstants.API_TEST5, query_pos_map, Communications.TAG_QUERY_POSITION);
                    mComm.doVolleyPost(BLConstants.API_TEST_CONNECT, null, Communications.TAG_TEST_CONNECT);

                    //parseLocation(mapid+","+100+","+100,2);
                    // parseLocation(mapid+","+(100+StepCal.get_step_offset_X()*10)+","+(100+StepCal.get_step_offset_Y()*10),2);
                }
                else {
                    while (i-- != 0) {
                        SystemClock.sleep(1500);
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
        thread.start();
    }
//    public float[] CreateDrawableLine ( float[] param){
//        int len=param.length;
//        float temp[]=new float[len];
//        float temp0[]=new float[2*len];
//        int j=2;
//        if(param[len-1]==param[len-3]){
//            temp[1]=param[len-1]-5;
//            temp[0]=param[len-2];}
//        else{
//            temp[1]=param[len-1];
//            temp[0]=param[len-2]-5;
//        }
//        for(int i=len-3;i>=3;i--){
//            temp[j+1]=param[i]-5;
//            temp[j]=param[i-1]-5;
//            j++;
//        }
//
//        if(param[1]==param[3]){
//            temp[len-1]=param[1]-5;
//            temp[len-2]=param[0];}
//        else{
//            temp[param.length-1]=param[1];
//            temp[param.length-2]=param[0]-5;
//        }
//
//        for(int k=0;k<=param.length-1;k++){
//            temp0[k]=param[k];
//            temp[k+param.length]=temp[k];
//            k++;
//        }
//
//         return temp0;
//
//    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(DEBUG_TAG, "onResume");
        //if (mBoundService != null)
            //mBoundService.startScan(cur_rss);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG, "onPause");
        if (mBoundService != null)
            mBoundService.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "onDestroy");
        WelcomeActivity.unregisterObserver(this);
    }

    public void initView() {
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        //btn_search = (ImageView) findViewById(R.id.btnSearch);
        btn_search = (Button) findViewById(R.id.btnSearch);
        btn_search = (Button) findViewById(R.id.btnSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);

        ivDeleteText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                }
            }
        });

        // set the offline map
        Drawable d = getResources().getDrawable(R.drawable.register_bg);

        Bitmap OfflineMap = ((BitmapDrawable)d).getBitmap();
        scalesize = TaskUtil.calcScaleSize(OfflineMap);
        Bitmap resizedBmp = TaskUtil.reSizeBitmap(OfflineMap, scalesize);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        dbHelper.insertOrUpdate(mapid, mapid, "", scalesize, os.toByteArray());
        map.setMapBitmap(resizedBmp);

        black = new CircleShape[100];
//
//        Log.e("Step X Y", StepCal.get_step_offset_X() + "," + StepCal.get_step_offset_Y());
//        String[] tmp =  new String[1];
//        tmp[0] = mapid + "," + StepCal.get_step_offset_X() + "," + StepCal.get_step_offset_Y();
//        parseLocation(tmp, 2);

        btn_search.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mComm.doVolleyPost(BLConstants.API_TEST_CONNECT, null, Communications.TAG_TEST_CONNECT);
                //Log.e("Step Counter", StepCal.getsteps() + "");
                //test
                getMapInfo(1);
                Log.e("Test 001",":"+etSearch.getText().toString().trim()+":");
                if(etSearch.getText().toString().trim()!=null) {
                    String[] tmp = new String[3];
                    tmp[0] = "0,500,"+etSearch.getText().toString().trim()+"00";
                    tmp[1] = "0,300,"+etSearch.getText().toString().trim()+"00";
                    tmp[2] = "0,100,"+etSearch.getText().toString().trim()+"00";
                    //parseLocation(tmp, 2);
                }
                else{
                    refreshUI();
                }

                if (isOnline) {
                    /*
                    System.out.println(btn_search.getText().toString().trim());
                    Map<String, String> query_pos_map = new HashMap<String, String>();
                    //query_pos_map.put(BLConstants.ARG_USER_ID, etSearch.getText().toString().trim());
                    query_pos_map.put("rss", etSearch.getText().toString().trim());
                    mComm.doVolleyPost(BLConstants.API_TEST5, query_pos_map, Communications.TAG_QUERY_POSITION);
                    */


                } else {/*
                    Log.e("Step X Y", StepCal.get_step_offset_X() + "," + StepCal.get_step_offset_Y());
                    String[] tmp2 =  new String[1];
                    tmp2[0] = mapid + "," + StepCal.get_step_offset_X() + "," + StepCal.get_step_offset_Y();
                    parseLocation(tmp2, 2);
                    */
//
                }
            }
        });
    }

    public void goBack(View view) {
        //onBackPressed();
        //tabbar.switchViewTab(0);
        tabbar.viewContainer.flipToView(0);
        tabbar.setViewTab(0);
    }

    class bledevice {
        String devicemac = "";
        int rec_rssi = 0;
    }
    @Override
    public void onFail(String tag, String response){
        Log.e("EEEEEEEEE", "abcd");
        //parseLocation("0,250,250", 2);
    }
    @Override
    public void onErrorResponse(String tag, VolleyError volleyError) {
        //Log.e("LLLLLLLLL", volleyError.getMessage(), volleyError);
        if (tag.equals(Communications.TAG_TEST_CONNECT)){
            Log.e("Connect Failed","==============");
            if(isOnline)
                Log.e("Start Step Calculator","================");
            //StepCal.startstep(cur_x,cur_y);
            isOnline = false;

        }

    }
    @Override
    public void onSuccess(String tag, String response){
        if (tag.equals(Communications.TAG_QUERY_MAP)) {
            String mapurl = BLConstants.API_TEST4 +response;//HttpUtil.parseJson(response, BLConstants.ARG_MAP_URL);
            downloadMap(mapurl);
            //showPostion(100,100);
        }
        if (tag.equals(Communications.TAG_TEST_CONNECT)){
            Log.e("Connect Success","=============");
            if(!isOnline)
                Log.e("Stop Step Calculator", "==================");
            StepCal.stopstep();
            isOnline = true;
        }
        if (tag.equals(Communications.TAG_QUERY_POSITION)) {
            if(!response.isEmpty()){
                Log.e("WWWWWWWWW","Location_onSuccess_QueryPosition_parseLocation");
                cur_x = Integer.parseInt(response.split(",")[1]);
                cur_y = Integer.parseInt(response.split(",")[2]);
                String[] tmp =  new String[1];
                tmp[0] =response;
                //parseLocation(tmp, BLNotifier.TYPE_MANUAL_UPDATE_LOCATION);
            }
            /*
            if (response.isEmpty()) {
                tmpmap.put("mapid", response.split(",")[0]);
                mComm.doVolleyPost(BLConstants.API_TEST5, tmpmap, Communications.TAG_QUERY_MAP);
                Log.d(DEBUG_TAG, tag + ": empty response");
            }
            else {
                Log.e("WWWWWWWWW","Location_onSuccess_QueryPosition_parseLocation");
                parseLocation(response, BLNotifier.TYPE_MANUAL_UPDATE_LOCATION);
            }*/
        }
    }
    @Override
    public void refreshUI(){
        map.clearShapes();
    }
    @Override
    public void onImageResponse(String tag, Bitmap response) {
        /*/test
        Log.d(DEBUG_TAG, "Resize the map.");
        scalesize = TaskUtil.calcScaleSize(response);
        Bitmap resizedBmp = TaskUtil.reSizeBitmap(response, scalesize);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        resizedBmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        dbHelper.insertOrUpdate(mapid, mapid, "",scalesize, os.toByteArray());

        map.setMapBitmap(resizedBmp);
        //test end */

        if (tag.equals(Communications.TAG_DOWNLOAD_MAP)) {
            if (response == null || response.getHeight() == 0 || response.getWidth() == 0)
                Log.d(DEBUG_TAG, "null bitmap");
            else
            {
                Log.d(DEBUG_TAG, "Resize the map.");
                scalesize = TaskUtil.calcScaleSize(response);
                Bitmap resizedBmp = TaskUtil.reSizeBitmap(response, scalesize);
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                resizedBmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                dbHelper.insertOrUpdate(mapid, mapid, "",scalesize, os.toByteArray());

                map.setMapBitmap(resizedBmp);
            }
        }

    }

    public void getMapInfo(final int mapid)
    {
        Log.d(DEBUG_TAG, "Query map by map ID: " + mapid);
        Map<String, String> query_map = new HashMap<String, String>();
        query_map.put(BLConstants.ARG_MAP_ID, Integer.toString(mapid));
        //query_map.put("mapid", mapid+"");
        mComm.doVolleyPost(BLConstants.API_QUERY_MAP_BY_MAPID, query_map, Communications.TAG_QUERY_MAP);
    }

    public void downloadMap(final String mapurl){
        Log.d(DEBUG_TAG, "Download map with url: ." + mapurl);
        mComm.doVolleyImageRequest(mapurl, Communications.TAG_DOWNLOAD_MAP);
    }
    public void addLine(float sx, float sy, float ex, float ey) {
        lines[lines_count] = new Lines("Lines"+lines_count, 0xFFFF3E96); //Color.BLUE，圆点的颜色
        lines[lines_count].setValues(String.format("%.5f,%.5f,%.5f,%.5f,%.5f",sx*scalesize, sy*scalesize, ex*scalesize, ey*scalesize, 10.0)); //设置圆点的位置和大小
        map.addShape(lines[lines_count]); //加到地图上
        lines_count = (lines_count + 1) % 100;
    }
    public void addLines(PointF[] points) {  //  0xFFFF3E96 挺好看的
        int i;
        float t = (float)6.5;
        addCircle(points[0].x, points[0].y, t, 0xFFFF3E96, "turning_point");
        for (i = 0; i<points.length - 1; i++) {
            addLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y);
            addCircle(points[i+1].x, points[i+1].y, t, 0xFFFF3E96, "turning_point");
        }
    }

    public void Handle_Route(route tmp){
        if(tmp.num_KeyPoint>1) {
//            Log.e("fuck",(300 + StepCal.get_step_offset_X() * 10)+","+(300 + StepCal.get_step_offset_Y() * 10));
//
//            Log.e("fuck2",tmp. KeyPoint[1].x+","+tmp. KeyPoint[1].y);
//            Log.e("fuck3",len+"");
            if (((300 + StepCal.get_step_offset_X() * 10)>= tmp.KeyPoint[1].x-30 && tmp.KeyPoint[1].x+30>=(300 + StepCal.get_step_offset_X() * 10)) && ((300 + StepCal.get_step_offset_Y() * 10) <= tmp. KeyPoint[1].y+30 &&  tmp. KeyPoint[1].y-30<=(300 + StepCal.get_step_offset_Y() * 10)) ){

                for (int i = 0; i <= tmp.num_KeyPoint - 2; i++) {
                    tmp. KeyPoint[i] = tmp. KeyPoint[i + 1];
                }
               tmp.num_KeyPoint--;
            }
            addLines(tmp.KeyPoint);
        }else{
//            Log.e("fuck",(300 + StepCal.get_step_offset_X() * 10)+","+(300 + StepCal.get_step_offset_Y() * 10));
//
//            Log.e("fuck2",tmp. KeyPoint[1].x+","+tmp. KeyPoint[1].y);
//            Log.e("fuck3",len+"");
                if (((300 + StepCal.get_step_offset_X() * 10)>= tmp.KeyPoint[1].x-30 && tmp.KeyPoint[1].x+30>=(300 + StepCal.get_step_offset_X() * 10)) && ((300 + StepCal.get_step_offset_Y() * 10) <= tmp. KeyPoint[1].y+30 &&  tmp. KeyPoint[1].y-30<=(300 + StepCal.get_step_offset_Y() * 10)) ) {
                    Toast.makeText(getApplicationContext(), "已找到好友！",
                            Toast.LENGTH_SHORT).show();
                }else{return;}
            }


        }


//    public void addLine(double sx, double sy, double ex, double ey) {
//        lines[lines_count] = new ThickLines("Lines"+black_count, Color.BLUE); //Color.BLUE，圆点的颜色
//        lines[lines_count].setValues(String.format("%.5f,%.5f,%.5f,%.5f",sx*scalesize, sy*scalesize, ex*scalesize, ey*scalesize)); //设置圆点的位置和大小
//        map.addShape(lines[lines_count]); //加到地图上
//        lines_count = (lines_count + 1) % 100;
//    }

    public void addCircle(float x, float y, float rad, int color, String tag ){
        black[black_count] = new CircleShape(tag+black_count, color); //Color.BLUE，圆点的颜色
        if (tag == "position") {
            black[black_count].setValues(String.format("%.5f,%.5f,%.5f,%d", x * scalesize, y * scalesize, rad * scalesize, 100)); //设置圆点的位置和大小
            map.addShapeAndRefToBubble(black[black_count]); //加到地图上
        }
        else {
            black[black_count].setValues(String.format("%.5f,%.5f,%.5f,%d", x * scalesize, y * scalesize, rad * scalesize, 255)); //设置圆点的位置和大小
            map.addShape(black[black_count]);
        }
        black_count = (black_count + 1) % 100;
    }






//    public boolean showPostion(double x, double y){ //该方法可以实现一个圆点，用于和bubble进行绑定，并且最终显示在地图上
//        black[black_count] = new CircleShape("No"+black_count, Color.TRANSPARENT); //Color.BLUE，圆点的颜色
//
//        black[black_count].setValues(String.format("%.5f,%.5f,15",x*scalesize,y*scalesize)); //设置圆点的位置和大小
//        map.addShapeAndRefToBubble(black[black_count]); //加到地图上
//        black_count = (black_count + 1) % 100;
//        return true;
//    }
    public boolean showPostion(double x, double y){ //该方法可以实现一个圆点，用于和bubble进行绑定，并且最终显示在地图上

        addCircle((float)x , (float)y, (float)15, Color.BLACK, "position");
        return true;
    }

    //PeopleToShow
    private void parseLocation(Map args, final int type) {
//        int positionX[],positionY[],i;
        //String[] tmp;
        Log.d(DEBUG_TAG, "parseLocation type: " + type);
//        positionX = new int[args.size()];
//        positionY = new int[args.size()];
        String Temp[];
        mapid = Integer.parseInt(args.get("MapId").toString());//HttpUtil.parseJsonsdouble(args,BLConstants.ARG_POSITION,BLConstants.ARG_MAP_ID);
//        for (i = 0;i<args.length;i++) {
//            tmp = args[i].split(",");
//            positionX[i] = Integer.parseInt(tmp[1]);//HttpUtil.parseJsonsdouble(args,BLConstants.ARG_POSITION,BLConstants.ARG_POSITION_X);
//            positionY[i] = Integer.parseInt(tmp[2]);//HttpUtil.parseJsonsdouble(args,BLConstants.ARG_POSITION,BLConstants.ARG_POSITION_Y);
//
//        }



            /*
            if (type == BLNotifier.TYPE_AUTO_UPDATE_LOCATION) {
                String messages = HttpUtil.parseJson(args[0], BLConstants.ARG_POSITION_MSG);
                if (!messages.isEmpty())
                    showNotification(messages);
            }
            */
        Log.e("Map id =", String.valueOf(mapid));
        Log.e("Cur_Map id =", String.valueOf(current_map));
        if (mapid != current_map )//&& mapid != 0)
        {
            current_map = mapid;
            Bitmap findmap = dbHelper.queryMap(mapid);
            if(findmap == null)
            {
                Log.e("Geting Map Info",mapid+"");
                getMapInfo(mapid);
            }
            else
            {
                Log.e(DEBUG_TAG, "Find map in local database.");
                scalesize = dbHelper.queryScalesize(mapid);
                map.setMapBitmap(findmap);
            }
        }

        map.setOnShapeClickListener(new ShapeExtension.OnShapeActionListener() {
            @Override
            public void onShapeClick(Shape shape, float xOnImage, float yOnImage) {
                String msg = "Shape " + shape.tag + " clicked !";
                Log.e("wewe", "qweqweqweqwe");
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
//显示toast信息
                toast.show();
            }
        });
//        float[] pts={50,50,400,50,
//               400,600,
//                50,600,
//               50,595,395,595,395,45,50,55   };

        map.clearShapes();

        Handle_Route(MyRoute);


//        PolygonalLineShape red = new PolygonalLineShape("red", Color.BLACK);
//        red.setValues(pts);
//        map.addShape(red);

//        Shape orange = new PolyShape("orange",0xff8226);
//        //orange.setValues(CreateDrawableLine(pts));
//        orange.setValues(pts);
//        map.addShape(orange);
        for (Object key: args.keySet() ) {
            if(key=="MapId") continue;
            Temp= args.get(key).toString().split(",");
            //final String text=Temp[2];
            Log.e("text",Temp[2]);
            final String text=Temp[2];


                /*
                if (i == args.length) {

                    View bubble = getLayoutInflater().inflate(R.layout.popup, null);
                    map.setBubbleView(bubble,new Bubble.RenderDelegate() {
                        @Override
                        public void onDisplay(Shape shape, View bubbleView) {
                            ImageView logo = (ImageView) bubbleView.findViewById(R.id.logo); //通过bubbleView得到相应的控件
                            if (type == BLNotifier.TYPE_AUTO_UPDATE_LOCATION)
                                logo.setImageResource(R.drawable.location_icon_purple);
                            else if (type == BLNotifier.TYPE_MANUAL_UPDATE_LOCATION)
                                logo.setImageResource(R.drawable.location_icon_yellow);
                        }
                    });
                    Log.e("Position Showing", 10000 + " , " + 10000);
                    showPostion(700.0,700.0);
                    break;

                }
                */
            View bubble = getLayoutInflater().inflate(R.layout.popup, null);

            map.setBubbleView(bubble, new Bubble.RenderDelegate() {
                @Override
                public void onDisplay(Shape shape, View bubbleView) {
                    ImageView logo = (ImageView) bubbleView.findViewById(R.id.logo); //通过bubbleView得到相应的控件
                    TextView name = (TextView) bubbleView.findViewById(R.id.bname);
                    name.setText(text);
                    if (type == BLNotifier.TYPE_AUTO_UPDATE_LOCATION)
                        logo.setImageResource(R.drawable.location_icon_purple);
                    else if (type == BLNotifier.TYPE_MANUAL_UPDATE_LOCATION)
                        logo.setImageResource(R.drawable.location_icon_yellow);
                }
            });


            //Log.e("Position Showing", positionX[i] + " , " + positionY[i]);
            showPostion(Integer.parseInt(Temp[0]),Integer.parseInt(Temp[1]));
        }
    }

    public void onBLUpdate(int notificationType, String args) {
        /*switch (notificationType) {
            case BLNotifier.TYPE_BLE_NOT_SUPPORT:
                showNotification(R.string.ble_not_supported);
                break;
            case BLNotifier.TYPE_BLE_NOT_ENABLED:
                showNotification(R.string.error_bluetooth_not_supported);
                break;
            case BLNotifier.TYPE_AUTO_UPDATE_LOCATION:
                if (!args.isEmpty())
                    parseLocation(args, notificationType);
                break;
            default:
                break;
        }*/
    }
}
