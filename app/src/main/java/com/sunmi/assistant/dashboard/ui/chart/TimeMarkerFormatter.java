package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;

import com.sunmi.assistant.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public class TimeMarkerFormatter implements IMarkerFormatter {

    public static final int VALUE_TYPE_INTEGER = 0;
    public static final int VALUE_TYPE_DECIMAL_1 = 1;
    public static final int VALUE_TYPE_DECIMAL_2 = 2;
    public static final int VALUE_TYPE_RATE = 10;

    public static final int TIME_TYPE_HOUR = 1;
    public static final int TIME_TYPE_HOUR_SPAN = 2;
    public static final int TIME_TYPE_WEEK = 3;
    public static final int TIME_TYPE_DATE = 4;
    public static final int TIME_TYPE_DATE_SPAN = 5;

    private final String[] weekName;

    private int valueType = VALUE_TYPE_INTEGER;
    private String valueFormat = "%s";
    private int timeType = TIME_TYPE_HOUR;
    private String timeFormat;

    private Calendar temp = Calendar.getInstance();

    public TimeMarkerFormatter(Context context) {
        timeFormat = context.getString(R.string.dashboard_card_marker_time) + " %s";
        weekName = context.getResources().getStringArray(R.array.week_name);
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public void setValueFormat(String format) {
        this.valueFormat = format;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public void setTimeFormat(String format) {
        this.timeFormat = format;
    }

    @Override
    public CharSequence valueFormat(float value) {
        String valueStr;
        switch (valueType) {
            case VALUE_TYPE_INTEGER:
                valueStr = String.format(Locale.getDefault(), "%.0f", value);
                break;
            case VALUE_TYPE_DECIMAL_1:
                valueStr = String.format(Locale.getDefault(), "%.1f", value);
                break;
            case VALUE_TYPE_DECIMAL_2:
                valueStr = String.format(Locale.getDefault(), "%.2f", value);
                break;
            case VALUE_TYPE_RATE:
                valueStr = String.format(Locale.getDefault(), "%.2f%%", value * 100);
                break;
            default:
                valueStr = "";
        }
        return String.format(Locale.getDefault(), valueFormat, valueStr);
    }

    @Override
    public CharSequence xAxisFormat(float x) {
        return "";
    }

    @Override
    public CharSequence timeFormat(long time) {
        String valueStr = "";
        temp.setTimeInMillis(time);

        if (timeType == TIME_TYPE_HOUR) {
            int hour = temp.get(Calendar.HOUR_OF_DAY);
            valueStr = String.format(Locale.getDefault(), "%02d:00", hour);

        } else if (timeType == TIME_TYPE_HOUR_SPAN) {
            int hour = temp.get(Calendar.HOUR_OF_DAY);
            valueStr = String.format(Locale.getDefault(), "%02d:00-%02d:00", hour, hour + 1);

        } else if (timeType == TIME_TYPE_WEEK) {
            valueStr = weekName[temp.get(Calendar.DAY_OF_WEEK) - 1];

        } else if (timeType == TIME_TYPE_DATE) {
            int month = temp.get(Calendar.MONTH) + 1;
            int day = temp.get(Calendar.DATE);
            valueStr = String.format(Locale.getDefault(), "%02d.%02d", month, day);
        } else if (timeType == TIME_TYPE_DATE_SPAN) {
            int month = temp.get(Calendar.MONTH) + 1;
            int day = temp.get(Calendar.DATE);
            int offset = (8 - temp.get(Calendar.DAY_OF_WEEK)) % 7;
            temp.add(Calendar.DATE, offset);
            int nextMonth = temp.get(Calendar.MONTH) + 1;
            int nextDay = temp.get(Calendar.DATE);
            valueStr = String.format(Locale.getDefault(), "%02d.%02d-%02d.%02d", month, day, nextMonth, nextDay);
        }

        return String.format(Locale.getDefault(), timeFormat, valueStr);
    }
}
