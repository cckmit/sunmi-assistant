package com.sunmi.assistant.data.response;

import com.google.gson.annotations.SerializedName;

public class OrderTotalAmountResp {

    /**
     * total_amount : 0
     * day_amount : 0
     * yesterday_amount : 0
     * week_amount : 0
     * last_week_amount : 0
     * month_amount : 0
     * last_month_amount : 0
     * day_rate :
     * week_rate :
     * month_rate :
     */

    @SerializedName("total_amount")
    private float totalAmount;
    @SerializedName("day_amount")
    private float dayAmount;
    @SerializedName("yesterday_amount")
    private float yesterdayAmount;
    @SerializedName("week_amount")
    private float weekAmount;
    @SerializedName("last_week_amount")
    private float lastWeekAmount;
    @SerializedName("month_amount")
    private float monthAmount;
    @SerializedName("last_month_amount")
    private float lastMonthAmount;
    @SerializedName("day_rate")
    private String dayRate;
    @SerializedName("week_rate")
    private String weekRate;
    @SerializedName("month_rate")
    private String monthRate;

    public float getTotalAmount() {
        return totalAmount;
    }

    public float getDayAmount() {
        return dayAmount;
    }

    public float getYesterdayAmount() {
        return yesterdayAmount;
    }

    public float getWeekAmount() {
        return weekAmount;
    }

    public float getLastWeekAmount() {
        return lastWeekAmount;
    }

    public float getMonthAmount() {
        return monthAmount;
    }

    public float getLastMonthAmount() {
        return lastMonthAmount;
    }

    public String getDayRate() {
        return dayRate;
    }

    public String getWeekRate() {
        return weekRate;
    }

    public String getMonthRate() {
        return monthRate;
    }
}
