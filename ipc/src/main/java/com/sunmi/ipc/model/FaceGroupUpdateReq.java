package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

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

    public FaceGroupUpdateReq(int companyId, int shopId, int groupId, String name, String mark, int capacity, int threshold, int period, int alarmNotified) {
        this.companyId = companyId;
        this.shopId = shopId;
        this.groupId = groupId;
        this.name = name;
        this.mark = mark;
        this.capacity = capacity;
        this.threshold = threshold;
        this.period = period;
        this.alarmNotified = alarmNotified;
    }
}
