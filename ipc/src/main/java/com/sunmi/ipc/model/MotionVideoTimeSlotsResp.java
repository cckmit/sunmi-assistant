package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-12-09
 */
public class MotionVideoTimeSlotsResp {

    @SerializedName("time_slots")
    private List<Long> timeSlots;

    public List<Long> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<Long> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
