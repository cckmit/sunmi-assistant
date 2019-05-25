package com.sunmi.ipc.model;

/**
 * ap和云端时间组合
 * Created by YangShiJie on 2019/5/25.
 */
public class ApCloudTimeBean {
    private long startTime;
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
}
