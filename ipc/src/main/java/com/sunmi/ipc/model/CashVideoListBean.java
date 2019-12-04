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
     * abnormal_video_count : 3
     */

    @SerializedName("device_id")
    private int deviceId;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("abnormal_video_count")
    private int abnormalVideoCount;

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
}
