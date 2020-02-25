package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.util.Utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public class TimeMarkerFormatter implements IMarkerFormatter {

    public static final int VALUE_TYPE_INTEGER = 0;
    public static final int VALUE_TYPE_RATE = 1;

    public static final int TIME_TYPE_HOUR = 1;
    public static final int TIME_TYPE_HOUR_SPAN = 2;
    public static final int TIME_TYPE_WEEK = 3;
    public static final int TIME_TYPE_DATE = 4;
    public static final int TIME_TYPE_DATE_SPAN = 5;

    private final String[] weekName;
    private final String timeLabel;

    private int valueType = VALUE_TYPE_INTEGER;
    private int timeType = TIME_TYPE_HOUR;

    private Calendar temp = Calendar.getInstance();

    public TimeMarkerFormatter(Context context) {
        weekName = context.getResources().getStringArray(R.array.week_name);
        timeLabel = context.getString(R.string.dashboard_card_marker_time);
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    @Override
    public CharSequence valueFormat(Context context, float value) {
        CharSequence result;
        switch (valueType) {
            case VALUE_TYPE_INTEGER:
                result = String.format(Locale.getDefault(), "%.0f", value);
                break;
            case VALUE_TYPE_RATE:
                result = Utils.formatPercent(value, true, true);
                break;
            default:
                result = "";
        }
        return result;
    }

    @Override
    public CharSequence xAxisFormat(Context context, float x) {
        return "";
    }

    @Override
    public CharSequence timeFormat(Context context, long time) {
        String valueStr = "";
        temp.setTimeInMillis(time);
        int hour, day, month;
        switch (timeType) {
            case TIME_TYPE_HOUR:
                hour = temp.get(Calendar.HOUR_OF_DAY);
                valueStr = String.format(Locale.getDefault(), "%02d:00", hour);
                break;
            case TIME_TYPE_HOUR_SPAN:
                hour = temp.get(Calendar.HOUR_OF_DAY);
                valueStr = String.format(Locale.getDefault(), "%02d:00-%02d:00", hour, hour + 1);
                break;
            case TIME_TYPE_WEEK:
                valueStr = weekName[temp.get(Calendar.DAY_OF_WEEK) - 1];
                break;
            case TIME_TYPE_DATE:
                month = temp.get(Calendar.MONTH) + 1;
                day = temp.get(Calendar.DATE);
                valueStr = String.format(Locale.getDefault(), "%02d.%02d", month, day);
                break;
            case TIME_TYPE_DATE_SPAN:
                month = temp.get(Calendar.MONTH) + 1;
                day = temp.get(Calendar.DATE);
                int offset = (8 - temp.get(Calendar.DAY_OF_WEEK)) % 7;
                temp.add(Calendar.DATE, offset);
                int nextMonth = temp.get(Calendar.MONTH) + 1;
                int nextDay = temp.get(Calendar.DATE);
                valueStr = String.format(Locale.getDefault(), "%02d.%02d-%02d.%02d", month, day, nextMonth, nextDay);
                break;
            default:
        }

        return timeLabel + " " + valueStr;
    }
}
