package com.sunmi.ipc.ipcset;

/**
 * @author yinhui
 * @since 2019-07-15
 */
public class IpcSettingModel {

    public static final int ABNORMAL_DETECTION_DISABLE = 0;
    public static final int ABNORMAL_DETECTION_LOW = 1;
    public static final int ABNORMAL_DETECTION_MIDDLE = 2;
    public static final int ABNORMAL_DETECTION_HIGH = 3;

    public static final int NIGHT_VISION_AUTO = 0;
    public static final int NIGHT_VISION_ON = 1;
    public static final int NIGHT_VISION_OFF = 2;

    private String ipcName;
    private String ipcModel;
    private String ipcSn;
    private int soundAbnormalDetection;
    private int dynamicAbnormalDetection;
    private int detectionTimeMode;
    private long detectionStartTime;
    private long detectionEndTime;
    private int nightVisionMode;
    private boolean light;
    private boolean rotate;
    private String version;

}
