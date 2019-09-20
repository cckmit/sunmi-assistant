package com.sunmi.ipc.config;

/**
 * Created by YangShiJie on 2019/7/29.
 */
public class IpcConstants {
    //ipc 升级时间
    public static final int SS_UPGRADE_TIME = 150; //ss升级150秒
    public static final int FS_UPGRADE_TIME = 360; //fs升级360秒

    private static int totalEvents = 0x3800;
    public static final int getIpcSettingMessage = totalEvents++;
    public static final int refreshIpcList = totalEvents++;
    public static final int ipcDiscovered = totalEvents++;
    public static final int ipcNameChanged = totalEvents++;

}
