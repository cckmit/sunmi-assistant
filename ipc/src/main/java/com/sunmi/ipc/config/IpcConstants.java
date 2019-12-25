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

    public static final String IPC_VERSION_NO_SDCARD_CHECK = "1.2.0";
    public static final String IPC_VERSION_VIDEO_ADJUST = "1.2.5";
    public static final int IPC_CONFIG_MODE_WIRED = 1;//有线网络配置
    public static final int IPC_CONFIG_MODE_AP = 2;//ap模式配置

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
