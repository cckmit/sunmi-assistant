package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 * Created by bruce on 2019/8/14.
 */
public class VideoTimeSlotBean implements Comparable<VideoTimeSlotBean> {

    /**
     * start_time : 1565512348
     * end_time : 1565539176
     */
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;

    private boolean isApPlay;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isApPlay() {
        return isApPlay;
    }

    public void setApPlay(boolean apPlay) {
        isApPlay = apPlay;
    }

    @Override
    public int compareTo(VideoTimeSlotBean o) {
        if (this.startTime >= o.getStartTime()) {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "VideoTimeSlotBean{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", isApPlay=" + isApPlay +
                '}';
    }

}
