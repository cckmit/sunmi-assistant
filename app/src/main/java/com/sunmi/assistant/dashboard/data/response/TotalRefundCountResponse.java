package com.sunmi.assistant.dashboard.data.response;

public class TotalRefundCountResponse {

    private int refund_count;
    private float day_rate;
    private float week_rate;
    private float month_rate;

    public int getRefund_count() {
        return refund_count;
    }

    public float getDay_rate() {
        return day_rate;
    }

    public float getWeek_rate() {
        return week_rate;
    }

    public float getMonth_rate() {
        return month_rate;
    }
}
