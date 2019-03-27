package com.sunmi.cloudprinter.constant;

import java.util.UUID;

public class Constants {


    public static final UUID SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTER_UUID = UUID.fromString("0000993f-0000-1000-8000-00805f9b34fb");
    public static final String JS_INTERFACE_NAME = "SunmiJSBridge";
    public static final int SRV2CLI_SEND_SN = 0;
    public static final int SRV2CLI_SEND_WIFI_ERROR = 1;
    public static final int SRV2CLI_SEND_WIFI_AP = 2;
    public static final int SRV2CLI_SEND_WIFI_AP_COMPLETELY = 3;
    public static final int SRV2CLI_SEND_ALREADY_CONNECTED_WIFI = 4;
    public static final int CLI2SRV_CMD_SN = 5;
    public static final int CLI2SRV_CMD_WIFI = 6;
    public static final int CLI2SRV_CMD_CONNECT_WIFI = 7;
    public static final int CLI2SRV_GET_ALREADY_CONNECTED_WIFI = 8;
    public static final int SRV2CLI_SEND_PROTOCOL_VERSION = 9;

}
