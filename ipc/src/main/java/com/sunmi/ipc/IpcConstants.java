package com.sunmi.ipc;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IpcConstants {
    public static String IPC_IP = "";
    public static String IPC_SN = "";

    private static int totalEvents = 0x3800;
    public static final int ipcDiscovered = totalEvents++;
    public static final int getWifiList = 0x3118;
    public static final int setIPCWifi = 0x3116;
    public static final int getApStatus = 0x3119;
    public static final int getIpcToken = 0x3124;

}
