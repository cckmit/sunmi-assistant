package com.sunmi.assistant.dashboard;

import android.util.Pair;

import java.util.Calendar;

/**
 * @author jacob
 * @since 2019-06-21
 */
public class Utils {

    public static String getTrendNameByTimeSpan(int timeSpan) {
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            return "月环比";
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            return "周环比";
        } else {
            return "日环比";
        }
    }

    public static Pair<Long, Long> calcTimeSpan(int timeSpan) {
        long timeStart;
        long timeEnd;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(year, month, date);
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            c.clear();
            c.set(year, month, 1);
            timeStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            timeEnd = c.getTimeInMillis();
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int offset = c.getFirstDayOfWeek() - dayOfWeek;
            c.add(Calendar.DATE, offset > 0 ? offset - 7 : offset);
            timeStart = c.getTimeInMillis();
            c.add(Calendar.DATE, 7);
            timeEnd = c.getTimeInMillis();
        } else {
            timeStart = c.getTimeInMillis();
            c.add(Calendar.DATE, 1);
            timeEnd = c.getTimeInMillis();
        }
        return new Pair<>(timeStart, timeEnd);
    }
}
