package com.sunmi.assistant.data.response;

public class OrderTotalCountResp {

    private int total_count;
    private int day_count;
    private int week_count;
    private int month_count;
    private String day_rate;
    private String week_rate;
    private String month_rate;

    public int getTotal_count() {
        return total_count;
    }

    public int getDay_count() {
        return day_count;
    }

    public int getWeek_count() {
        return week_count;
    }

    public int getMonth_count() {
        return month_count;
    }

    public String getDay_rate() {
        return day_rate;
    }

    public String getWeek_rate() {
        return week_rate;
    }

    public String getMonth_rate() {
        return month_rate;
    }
}
