package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-03.
 */
public class CashVideoListBean {
    /**
     * device_id : 36
     * total_count : 105
     * abnormal_video_count : 10
     * abnormal_behavior_video_count : 5
     */

    @SerializedName("device_id")
    private int deviceId;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("abnormal_video_count")
    private int abnormalVideoCount;
    @SerializedName("abnormal_behavior_video_count")
    private int abnormalBehaviorVideoCount;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getAbnormalVideoCount() {
        return abnormalVideoCount;
    }

    public void setAbnormalVideoCount(int abnormalVideoCount) {
        this.abnormalVideoCount = abnormalVideoCount;
    }

    public int getAbnormalBehaviorVideoCount() {
        return abnormalBehaviorVideoCount;
    }

    public void setAbnormalBehaviorVideoCount(int abnormalBehaviorVideoCount) {
        this.abnormalBehaviorVideoCount = abnormalBehaviorVideoCount;
    }
}
