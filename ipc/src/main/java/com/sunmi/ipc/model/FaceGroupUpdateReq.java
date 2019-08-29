package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.util.Constants;

/**
 * @author yinhui
 * @date 2019-08-16
 */
public class FaceGroupUpdateReq {
    /**
     * company_id : 6759
     * shop_id : 8699
     * group_id : 481
     * name : name
     * mark : blacklist
     * capacity : 10000
     * threshold : 0
     * period : 0
     * alarm_notified : 1
     */

    @SerializedName("company_id")
    private int companyId;
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("name")
    private String name;
    @SerializedName("mark")
    private String mark;
    @SerializedName("capacity")
    private int capacity;
    @SerializedName("threshold")
    private int threshold;
    @SerializedName("period")
    private int period;
    @SerializedName("alarm_notified")
    private int alarmNotified;

    public FaceGroupUpdateReq(int companyId, int shopId, FaceGroup model) {
        this.companyId = companyId;
        this.shopId = shopId;
        this.groupId = model.getGroupId();
        this.name = model.getGroupName();
        this.mark = model.getMark();
        this.capacity = model.getCapacity();
        this.threshold = model.getThreshold();
        this.period = model.getPeriod();
        this.alarmNotified = model.getAlarmNotified();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setThreshold(int times, int days) {
        this.threshold = times;
        this.period = days * Constants.SECONDS_PER_DAY;
    }

    public void setAlarmNotified(int alarmNotified) {
        this.alarmNotified = alarmNotified;
    }
}
