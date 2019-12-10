package com.sunmi.ipc.config;

/**
 * Created by YangShiJie on 2019/7/29.
 */
public class IpcConstants {
    private static int totalEvents = 0x3800;
    public static final int getIpcSettingMessage = totalEvents++;
    public static final int refreshIpcList = totalEvents++;
    public static final int ipcDiscovered = totalEvents++;
    public static final int ipcNameChanged = totalEvents++;
    public static final int getSdcardStatus = totalEvents++;//画面调整前获取sd卡状态
    public static final int ipcRelaunchSuccess = totalEvents++;

    public static final int IPC_VERSION_NO_SDCARD_CHECK = 10200;
    public static final int IPC_WIRED_NETWORK = 1;
    public static final int IPC_WIRELESS_NETWORK = 2;

    public static final int CASH_VIDEO_ALL = 0;
    public static final int CASH_VIDEO_NORMAL = 1;
    public static final int CASH_VIDEO_ABNORMAL = 2;

    public static final int MOTION_DETECTION_SOURCE_ALL = 0;
    public static final int MOTION_DETECTION_SOURCE_VIDEO = 1;
    public static final int MOTION_DETECTION_SOURCE_SOUND = 2;
    public static final int MOTION_DETECTION_SOURCE_BOTH = 3;

    public static final int IPC_MANAGE_TYPE_CLOUD = 1;
    public static final int IPC_MANAGE_TYPE_CASH = 2;
    public static final int IPC_MANAGE_TYPE_DETECT = 3;

}
