package com.sunmi.cloudprinter.constant;

import java.util.UUID;

public class Constants {

    public static final UUID SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTER_UUID = UUID.fromString("0000993f-0000-1000-8000-00805f9b34fb");
    public static final byte PRINTER_CMD_VERSION = 100;     //打印机命令的版本号
    public static final byte PRINTER_CMD_TAG1 = (byte) Integer.parseInt("AA", 16);//打印机命令的标识，第一个byte
    public static final byte PRINTER_CMD_TAG2 = (byte) Integer.parseInt("55", 16);//打印机命令的标识，第二个byte

    public static final String JS_INTERFACE_NAME = "SunmiJSBridge";
    public static final int SRV2CLI_SEND_SN = 0;   //发送sn号
    public static final int SRV2CLI_SEND_WIFI_ERROR = 1;   //发送wifi出错信息
    public static final int SRV2CLI_SEND_WIFI_AP = 2;     //发送 Wi-Fi ap信息
    public static final int SRV2CLI_SEND_WIFI_AP_COMPLETELY = 3;  //发送 Wi-Fi ap信息全部完成
    public static final int SRV2CLI_SEND_ALREADY_CONNECTED_WIFI = 4;   // 发送已经成功连接上指定AP的通知
    public static final int CLI2SRV_CMD_SN = 5;    // 请求SN号
    public static final int CLI2SRV_CMD_WIFI = 6;  // 请求获取Wi-Fi信息
    public static final int CLI2SRV_CMD_CONNECT_WIFI = 7;    //请求连接指定wifi
    public static final int CLI2SRV_GET_ALREADY_CONNECTED_WIFI = 8;   //收到成功连接上指定AP的通知
    public static final int SRV2CLI_SEND_PROTOCOL_VERSION = 9;     //发送协议版本号不匹配通知

    public static final int NOTIFICATION_PRINTER_ADDED = 600;     //添加打印机成功

}
