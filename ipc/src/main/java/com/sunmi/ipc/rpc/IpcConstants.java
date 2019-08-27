package com.sunmi.ipc.rpc;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IpcConstants {

    private static int totalEvents = 0x3800;
    public static final int ipcDiscovered = totalEvents++;
    public static final int refreshIpcList = totalEvents++;
    public static final int getIpcSettingMessage = totalEvents++;

    public static final int getWifiList = 0x3118;
    public static final int setIPCWifi = 0x3116;
    public static final int getIpcConnectApMsg = 0x3117;
    public static final int getApStatus = 0x3119;
    public static final int getIpcToken = 0x3124;
    public static final int bindIpc = 0x3059;
    public static final int getIsWire = 0x3126;

    public static final int getSdStatus = 0x3102;
    public static final int fsZoom = 0x3104;
    public static final int fsFocus = 0x3105;
    public static final int fsAutoFocus = 0x3106;
    public static final int fsReset = 0x3107;
    public static final int fsIrMode = 0x3108;
    public static final int fsGetStatus = 0x3109;
    public static final int fsSetLine = 0x3162;
    public static final int ipcUpgrade = 0x3140;
    public static final int getIpcNightIdeRotation = 0x305c;
    public static final int setIpcNightIdeRotation = 0x305d;
    public static final int getIpcDetection = 0x3120;
    public static final int setIpcDetection = 0x3121;

}
