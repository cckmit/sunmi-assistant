package com.sunmi.assistant.data.response;

public class OrderTotalRefundsResp {

    private int refund_count;
    private int day_refund;
    private int week_refund;
    private int month_refund;
    private String day_rate;
    private String week_rate;
    private String month_rate;

    public int getRefund_count() {
        return refund_count;
    }

    public int getDay_refund() {
        return day_refund;
    }

    public int getWeek_refund() {
        return week_refund;
    }

    public int getMonth_refund() {
        return month_refund;
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
