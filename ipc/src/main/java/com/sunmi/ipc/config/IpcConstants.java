package com.sunmi.ipc.config;

/**
 * Created by YangShiJie on 2019/7/29.
 */
public class IpcConstants {
    //ipc 升级时间
    public static final String SS_UPGRADE_TIME = "3"; //ss升级分钟
    public static final String FS_UPGRADE_TIME = "6"; //fs升级分钟

    private static int totalEvents = 0x3800;
    public static final int getIpcSettingMessage = totalEvents++;
    public static final int refreshIpcList = totalEvents++;
    public static final int ipcDiscovered = totalEvents++;
    public static final int ipcNameChanged = totalEvents++;
    public static final int getSdcardStatus = totalEvents++;//画面调整前获取sd卡状态

}
