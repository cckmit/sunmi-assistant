package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public class MotionVideo {
    /**
     * id : 40
     * device_name : 绑定测试1
     * source : 1
     * detect_time : 1554201061
     * cdn_address : http://test.cdn.sunmi.com/VIDEO/IPC/d42eeebbc9a6c7ddce5db13a07943bb7cfcee7552045c1af4ff5aef65d681dd1
     * snapshot_address : http://test.cdn.sunmi.com/VIDEO/IPC/d42eeebbc9a6c7ddce5db13a07943bb7cfcee7552045c1af4ff5aef65d681dd1?x-oss-process=video/snapshot,t_2300,f_jpg,m_fast
     */

    @SerializedName("id")
    private int id;
    @SerializedName("device_name")
    private String deviceName;
    @SerializedName("source")
    private int source;
    @SerializedName("detect_time")
    private int detectTime;
    @SerializedName("cdn_address")
    private String cdnAddress;
    @SerializedName("snapshot_address")
    private String snapshotAddress;

    public int getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getSource() {
        return source;
    }

    public int getDetectTime() {
        return detectTime;
    }

    public String getCdnAddress() {
        return cdnAddress;
    }

    public String getSnapshotAddress() {
        return snapshotAddress;
    }
}
