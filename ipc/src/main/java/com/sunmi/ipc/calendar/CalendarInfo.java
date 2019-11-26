package com.sunmi.ipc.calendar;

/**
 * Created by maning on 2017/7/19.
 * Item 显示日期的Bean : 包含阳历和阴历
 */

public class CalendarInfo {

    public long date;
    public boolean enable;
    public boolean point;

    public CalendarInfo() {
    }

    public CalendarInfo(long date, boolean enable, boolean point) {
        this.date = date;
        this.enable = enable;
        this.point = point;
    }
}
