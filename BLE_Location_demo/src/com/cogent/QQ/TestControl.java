package com.cogent.QQ;

/**
 * Created by TongXinyu on 15/9/19.
 */
public class TestControl {

    private static boolean PositionFlag = false;/* Show current Position */
    private static boolean ParseLocationInfo = false;
    private static boolean StepInfoFlag = false;/* Show Step Info? */
    private static boolean WiFiEnableFlag = false;/* WiFi Enable? */
    private static boolean BLEEnableFlag = true;/* BLE Enable? */
    private static String BLEScanFilter = "InFocus";/* We will only record the BLE containing this key word */
    private static boolean BLEInfoFlag = true;/* Show BLE debug Info? */

    public static boolean GetPositionFlag() {
        return PositionFlag;
    }
    public static boolean GetWiFiEnableFlag() { return WiFiEnableFlag;}
    public static boolean GetStepInfoFlag() {
        return StepInfoFlag;
    }
    public static boolean GetBLEInfoFlag() {return BLEInfoFlag;}
    public static boolean GetBLEEnableFlag() { return BLEEnableFlag;}
    public static boolean GetParseLocationInfo() {
        return ParseLocationInfo;
    }
    public static String GetBLEScanFilter() {return BLEScanFilter;}
}
