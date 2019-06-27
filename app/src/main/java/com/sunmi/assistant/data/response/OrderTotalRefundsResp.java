package com.sunmi.assistant.data.response;

public class OrderTotalRefundsResp {

    private int refund_count;
    private String day_rate;
    private String week_rate;
    private String month_rate;

    public int getRefund_count() {
        return refund_count;
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
