package com.sunmi.assistant.data.response;

public class OrderTotalAmountResp {

    private float total_amount;
    private float day_amount;
    private float week_amount;
    private float month_amount;
    private String day_rate;
    private String week_rate;
    private String month_rate;

    public float getTotal_amount() {
        return total_amount;
    }

    public float getDay_amount() {
        return day_amount;
    }

    public float getWeek_amount() {
        return week_amount;
    }

    public float getMonth_amount() {
        return month_amount;
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
