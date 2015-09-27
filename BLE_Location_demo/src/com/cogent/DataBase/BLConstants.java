package com.cogent.DataBase;

/**
 * Created by shawn on 3/30/15.
 */

public interface BLConstants {
    //Server
    public static final String URL_SERVER = "61.129.93.20";
    public static final String URL_COMPANY = "10.116.57.136";
    public static final String URL_Service = "http://202.120.36.137:5000/";//"http://192.168.1.100/";//"http://61.129.93.20/xtalball/Beacon/Services/";
    public static final int PORT_SERVER = 5000;
    public static final String KEY_SESSION = "PHPSESSID";

    //APIs
    public static final String API_TEST = URL_Service + "user";
    public static final String API_TEST2 = "http://www.baidu.com";
    public static final String API_TEST3 = URL_Service + "static/images/1.bmp";
    public static final String API_TEST4 = URL_Service + "locimg/";//"static/images/";
    public static final String API_TEST5 = URL_Service + "Lyy_test_rss";//"upload_rss/";
    public static final String API_TEST_CONNECT = URL_Service + "Lyy_is_connect/";

    public static final String API_REGISTER = URL_Service + "register";
    public static final String API_REGISTER_BY_PHONE = URL_Service + "registerByPhone";
    public static final String API_LOGIN = URL_Service + "login";
    public static final String API_CHANGE_PASSWORD = URL_Service + "changepassword";
    public static final String API_CHANGE_INFO = URL_Service + "changeuserinfo";
    public static final String API_QUERY_INFO = URL_Service + "queryuserinfo";
    public static final String API_QUERY_MAP = URL_Service + "querymap";
    public static final String API_QUERY_MAP_BY_MAPID = URL_Service + "Lyy_test_map";//"image_request";//"querymapbymapid";
    public static final String API_QUERY_POSITION = URL_Service + "queryposition";
    public static final String API_QUERY_POSITION_BY_MAC = URL_Service + "querypositionbymac";
    public static final String API_QUERY_POSITION_BY_UID = URL_Service + "image_request";//"querypositionbyuid";
    public static final String API_QUERY_POSITION_BY_NAME = URL_Service + "querypositionbyname";
    public static final String API_QUERY_DEVICES_INFO = URL_Service + "querydevicesbymapid";
    public static final String API_SEND_VERTIFICATION_CODE = URL_Service + "getCaptcha";
    public static final String API_POST_BATCH_TRACK_DATA = URL_Service + "posttrack";
    public static final String API_POST_SINGLE_TRACK_DATA = URL_Service + "postdata";
    public static final String API_REQ_FRIEND = URL_Service + "req_friend_position/";

    // arguments
    public static final String ARG_USER_INFO = "userinfo";
    public static final String ARG_USER_ID = "uid";
    public static final String ARG_USER_PWD = "password";
    public static final String ARG_USER_NEW_PWD = "newpassword";
    public static final String ARG_USER_NAME = "name";
    public static final String ARG_USER_GENDER = "gender";
    public static final String ARG_USER_PHONE = "phone";
    public static final String ARG_USER_EMAIL = "email";
    public static final String ARG_POSITION = "position";
    public static final String ARG_POSITION_X = "x";
    public static final String ARG_POSITION_Y = "y";
    public static final String ARG_POSITION_MSG = "message";
    public static final String ARG_MAP_ID = "mapid";
    public static final String ARG_MAP_MSG = "map_msg";
    public static final String ARG_MAP_SITE_ID = "siteid";
    public static final String ARG_MAP_VERSION = "version";
    public static final String ARG_MAP_DESCRIPTION = "description";
    public static final String ARG_MAP_URL = "mapurl";
    public static final String ARG_LOCAL_TIME = "localtime";
    public static final String ARG_DEV_MAC = "devicemac";

    public static final String ARG_REQ_RESULT = "result";
    public static final String ARG_ERROR_CODE = "error_code";


    // messages
    public static final String MSG_PASS = "pass";
    public static final String MSG_FAIL_DESC = " fail, ";
    public static final String MSG_REG_OK = "Register success,please use the account to login!";
    public static final String MSG_CONN_ERROR = "Connection Error, please check your Internet connection";
}
