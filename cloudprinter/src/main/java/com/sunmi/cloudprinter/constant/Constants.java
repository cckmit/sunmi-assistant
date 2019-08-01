package com.sunmi.cloudprinter.constant;

import java.util.UUID;

public class Constants {

    public static final UUID SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTER_UUID = UUID.fromString("0000993f-0000-1000-8000-00805f9b34fb");
    public static final byte PRINTER_CMD_VERSION = 100;     //打印机命令的版本号
    public static final byte PRINTER_CMD_TAG1 = (byte) Integer.parseInt("AA", 16);//打印机命令的标识，第一个byte
    public static final byte PRINTER_CMD_TAG2 = (byte) Integer.parseInt("55", 16);//打印机命令的标识，第二个byte

    public static final String JS_INTERFACE_NAME = "SunmiJSBridge";
    public static final int CMD_RESP_SN = 0;   //发送sn号
    public static final int CMD_RESP_GET_WIFI_ERROR = 1;   //发送wifi出错信息
    public static final int CMD_RESP_GET_WIFI_SUCCESS = 2;     //发送 Wi-Fi ap信息
    public static final int CMD_RESP_WIFI_AP_COMPLETELY = 3;  //发送 Wi-Fi ap信息全部完成
    public static final int CMD_RESP_WIFI_CONNECTED = 4;   // 发送已经成功连接上指定AP的通知
    public static final byte CMD_REQ_SN = 5;    // 请求SN号
    public static final byte CMD_REQ_WIFI_LIST = 6;  // 请求获取Wi-Fi信息
    public static final byte CMD_REQ_CONNECT_WIFI = 7;    //请求连接指定wifi
    public static final byte CMD_REQ_WIFI_CONNECTED = 8;   //收到成功连接上指定AP的通知
    public static final byte CMD_RESP_PROTOCOL_VERSION = 9;     //发送协议版本号不匹配通知
    public static final byte CMD_REQ_QUIT_CONFIG = 0x0a;     //请求退出配网过程
    public static final byte CMD_REQ_DELETE_WIFI_INFO = 0x0b;     //请求删除wifi 配置

    public static final int WIFI_START_ERROR = 2;
    public static final int WIFI_SCAN_ERROR = 3;
    public static final int WIFI_PAIRING_TIMEOUT = 4;
    public static final int WIFI_CONNECT_AP_ERROR = 5;
    public static final int WIFI_CONNECT_AP_TIMEOUT = 6;

    public static final int NOTIFICATION_PRINTER_ADDED = 600;     //添加打印机成功

}
