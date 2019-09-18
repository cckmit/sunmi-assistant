package com.sunmi.assistant.data.response;

import com.google.gson.annotations.SerializedName;

public class OrderTotalCountResp {

    /**
     * total_count : 0
     * day_count : 0
     * yesterday_count : 0
     * week_count : 0
     * last_week_count : 0
     * month_count : 0
     * last_month_count : 0
     * day_rate :
     * week_rate :
     * month_rate :
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("day_count")
    private int dayCount;
    @SerializedName("yesterday_count")
    private int yesterdayCount;
    @SerializedName("week_count")
    private int weekCount;
    @SerializedName("last_week_count")
    private int lastWeekCount;
    @SerializedName("month_count")
    private int monthCount;
    @SerializedName("last_month_count")
    private int lastMonthCount;
    @SerializedName("day_rate")
    private String dayRate;
    @SerializedName("week_rate")
    private String weekRate;
    @SerializedName("month_rate")
    private String monthRate;

    public int getTotalCount() {
        return totalCount;
    }

    public int getDayCount() {
        return dayCount;
    }

    public int getYesterdayCount() {
        return yesterdayCount;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public int getLastWeekCount() {
        return lastWeekCount;
    }

    public int getMonthCount() {
        return monthCount;
    }

    public int getLastMonthCount() {
        return lastMonthCount;
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
