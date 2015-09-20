 package com.cogent.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HttpUtil {
	

	public static String sendPost(final String spec,final String Data,final String sessionId) {
    	try{
    		
            // 鏍规嵁鍦板潃鍒涘缓URL瀵硅薄  
            URL url = new URL(spec);  
            // 鏍规嵁URL瀵硅薄鎵撳紑閾炬帴  
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();  
            // 璁剧疆璇锋眰鐨勬柟寮� 
            urlConnection.setRequestMethod("POST");    
            // 浼犻�鐨勬暟鎹� 
            String data = Data;
            System.out.println(data);
            // 璁剧疆璇锋眰鐨勫ご  
            urlConnection.setRequestProperty("Connection", "keep-alive");  
            // 璁剧疆璇锋眰鐨勫ご  
            urlConnection.setRequestProperty("Content-Type",  
                    "application/x-www-form-urlencoded");  
            // 璁剧疆璇锋眰鐨勫ご  
            urlConnection.setRequestProperty("Content-Length",  
                    String.valueOf(data.getBytes().length));  
            // 璁剧疆璇锋眰鐨勫ご  
            urlConnection  
                    .setRequestProperty("User-Agent",  
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            if (sessionId.equals("") == false)
            {
            	String session = "PHPSESSID="+sessionId;
            	urlConnection.setRequestProperty("Cookie", session);
            	System.out.println(session);
            }
            urlConnection.setDoOutput(true); // 鍙戦�POST璇锋眰蹇呴』璁剧疆鍏佽杈撳嚭  
            urlConnection.setDoInput(true); // 鍙戦�POST璇锋眰蹇呴』璁剧疆鍏佽杈撳叆  
                                            //setDoInput鐨勯粯璁ゅ�灏辨槸true  
            //鑾峰彇杈撳嚭娴� 
            OutputStream os = urlConnection.getOutputStream();  
            os.write(data.getBytes());  
            os.flush();  
            if (urlConnection.getResponseCode() == 200) {  
            	System.out.println("200ok");
            	//String session_value = urlConnection.getHeaderField("Set-Cookie");
            	
                // 鑾峰彇鍝嶅簲鐨勮緭鍏ユ祦瀵硅薄  
                InputStream is = urlConnection.getInputStream();  
                // 鍒涘缓瀛楄妭杈撳嚭娴佸璞� 
                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                // 瀹氫箟璇诲彇鐨勯暱搴� 
                int len = 0;  
                // 瀹氫箟缂撳啿鍖� 
                byte buffer[] = new byte[1024];  
                // 鎸夌収缂撳啿鍖虹殑澶у皬锛屽惊鐜鍙� 
                while ((len = is.read(buffer)) != -1) {  
                    // 鏍规嵁璇诲彇鐨勯暱搴﹀啓鍏ュ埌os瀵硅薄涓� 
                    baos.write(buffer, 0, len);  
                }  
                // 閲婃斁璧勬簮  
                is.close();  
                baos.close();  
                // 杩斿洖瀛楃涓� 
                final String result = new String(baos.toByteArray());  
                System.out.println(result);
                return result;
                //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                /*CalendarFragment.this.getActivity().runOnUiThread(new Runnable() {  
                    @Override  
                    public void run() {  
                        // 鍦ㄨ繖閲屾妸杩斿洖鐨勬暟鎹啓鍦ㄦ帶浠朵笂 浼氬嚭鐜颁粈涔堟儏鍐靛凹  
                    	 Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();  
                    }  
                }); */ 
  
            } else {  
                //Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show(); 
            	return "Connection Error";
            }  
        } catch (Exception e) {  
            e.printStackTrace();
            return "Connection Error";
        } 
    	//return "Error";
    }
	
	public static Bitmap sendGet(final String spec) {
		
        Bitmap bitmap = null;
        
    	try{
	  
	            URL url = new URL(spec);  
	            System.out.println(spec);
	            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();  
	
	            urlConnection.setDoInput(true);
	            urlConnection.connect();
	            
	            InputStream is = urlConnection.getInputStream();
	            
	            bitmap = BitmapFactory.decodeStream(is);
	                
	            is.close();  
  
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                System.out.println("[getNetWorkBitmap->]MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("[getNetWorkBitmap->]IOException");
                e.printStackTrace();
            }
        return bitmap;
    }
	
	public static String parseJson(String Jsonstring,String key){
		try {  
	            JSONObject jsonObject = new JSONObject(Jsonstring);  
	            return jsonObject.getString(key);  
  
	    } catch (JSONException e) {  
	            e.printStackTrace();  
	    }
		return null;
	}
	
	public static int parseJsonint(String Jsonstring,String key){
		try {  
	            JSONObject jsonObject = new JSONObject(Jsonstring);  
	            return jsonObject.getInt(key);  
  
	    } catch (JSONException e) {  
	            e.printStackTrace();  
	    }
		return 0;
	}
	
	public static String parseJsons(String Jsonstring,String key1,String key2){
		try {  
	            JSONObject jsonObject = new JSONObject(Jsonstring);  
	            JSONObject jsonObjects = jsonObject.getJSONObject(key1);  
	            return jsonObjects.getString(key2);  
	    } catch (JSONException e) {  
	            e.printStackTrace();  
	    }
		return null;
	}
	
	public static int parseJsonsdouble(String Jsonstring,String key1,String key2){
		try {  
	            JSONObject jsonObject = new JSONObject(Jsonstring);  
	            JSONObject jsonObjects = jsonObject.getJSONObject(key1);  
	            return jsonObjects.getInt(key2);  
	    } catch (JSONException e) {  
	            e.printStackTrace();  
	    }
		return 0;
	}
	
	public static String parse_error(int error_code){
		
		String error_descrip="";
		switch(error_code){
		case 101:
			error_descrip = "Format of user id not correct.";
			break;
			
		case 102:
			error_descrip = "Format of name not correct.";
			break;
			
		case 103:
			error_descrip = "Format of password not correct.";
			break;
			
		case 110:
			error_descrip = "User id has already been registered.";
			break;
			
		case 111:
			error_descrip = "Server database operation fail.";
			break;
			
		case 201:
			error_descrip = "User id not exists.";
			break;
			
		case 202:
			error_descrip = "Password not correct.";
			break;
			
		case 301:
			error_descrip = "New password format not correct.";
			break;
			
		case 302:
			error_descrip = "User authentication fail.";
			break;
			
		case 401:
			error_descrip = "User not login.";
			break;
			
		case 402:
			error_descrip = "Format of name not correct.";
			break;
			
		case 501:
			error_descrip = "User not login.";
			break;
			
		case 502:
			error_descrip = "Database operation fail.";
			break;
			
		case 601:
			error_descrip = "User not login.";
			break;
			
		case 602:
			error_descrip = "Can not find map info or database operation fail.";
			break;
			
		case 701:
			error_descrip = "User not login.";
			break;
			
		case 702:
			error_descrip = "Can not find position info or database operation fail.";
			break;
			
		case 801:
			error_descrip = "User not login.";
			break;
			
		case 802:
			error_descrip = "Can not find position info or database operation fail.";
			break;
			
		case 901:
			error_descrip = "User not login.";
			break;
			
		case 902:
			error_descrip = "Can not find name in database or database operation fail.";
			break;
			
		case 1001:
			error_descrip = "User not login.";
			break;
			
		case 1002:
			error_descrip = "Database operation fail.";
			break;
			
		case 1101:
			error_descrip = "User not login.";
			break;
			
		case 1102:
			error_descrip = "Database operation fail.";
			break;
			
		case 1201:
			error_descrip = "User not login.";
			break;
			
		case 1202:
			error_descrip = "Can not find map info or database operation fail.";
			break;
						
		}
		return error_descrip;
	
	}

}
