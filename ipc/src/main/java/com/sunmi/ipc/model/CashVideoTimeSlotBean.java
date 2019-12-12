package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-05.
 */
public class CashVideoTimeSlotBean {


    @SerializedName("timeslots")
    private List<Long> timeSlots;

    public List<Long> getTimeslots() {
        return timeSlots;
    }

    public void setTimeslots(List<Long> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
