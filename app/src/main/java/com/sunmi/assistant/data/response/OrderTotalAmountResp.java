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
    private double totalAmount;
    @SerializedName("day_amount")
    private double dayAmount;
    @SerializedName("yesterday_amount")
    private double yesterdayAmount;
    @SerializedName("week_amount")
    private double weekAmount;
    @SerializedName("last_week_amount")
    private double lastWeekAmount;
    @SerializedName("month_amount")
    private double monthAmount;
    @SerializedName("last_month_amount")
    private double lastMonthAmount;
    @SerializedName("day_rate")
    private String dayRate;
    @SerializedName("week_rate")
    private String weekRate;
    @SerializedName("month_rate")
    private String monthRate;

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDayAmount() {
        return dayAmount;
    }

    public double getYesterdayAmount() {
        return yesterdayAmount;
    }

    public double getWeekAmount() {
        return weekAmount;
    }

    public double getLastWeekAmount() {
        return lastWeekAmount;
    }

    public double getMonthAmount() {
        return monthAmount;
    }

    public double getLastMonthAmount() {
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
