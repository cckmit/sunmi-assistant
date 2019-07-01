package com.sunmi.assistant.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;

/**
 * @author yinhui
 * @since 2019-06-21
 */
public class Utils {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DAY_IN_WEEK_FORMAT = new SimpleDateFormat("u");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DAY_IN_MONTH_FORMAT = new SimpleDateFormat("d");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat HOUR_MINUTE_TIME = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_HOUR_MINUTE_TIME = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private static SparseArray<Pair<Long, Long>> sPeriodCache = new SparseArray<>(3);

    public static BaseRequest createRequestBody(String params) {
        String timeStamp = DateTimeUtils.currentTimeSecond() + "";
        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String isEncrypted = "0";
        String sign = SafeUtils.md5(params + isEncrypted +
                timeStamp + randomNum + SafeUtils.md5(CommonConfig.CLOUD_TOKEN));
        return new BaseRequest.Builder()
                .setTimeStamp(timeStamp)
                .setRandomNum(randomNum)
                .setIsEncrypted(isEncrypted)
                .setParams(params)
                .setSign(sign)
                .setLang("zh").createBaseRequest();
    }

    public static String getTrendNameByTimeSpan(Context context, int timeSpan) {
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            return context.getResources().getString(R.string.dashboard_month_ratio);
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            return context.getResources().getString(R.string.dashboard_week_ratio);
        } else {
            return context.getResources().getString(R.string.dashboard_day_ratio);
        }
    }

    public static Pair<Long, Long> getPeriodTimestamp(int period) {
        Pair<Long, Long> periodTimestamp = sPeriodCache.get(period);
        if (periodTimestamp != null) {
            return periodTimestamp;
        }
        long timeStart;
        long timeEnd;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(year, month, date);
        if (period == DashboardContract.TIME_SPAN_MONTH) {
            c.clear();
            c.set(year, month, 1);
            timeStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            timeEnd = c.getTimeInMillis();
        } else if (period == DashboardContract.TIME_SPAN_WEEK) {
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
        periodTimestamp = new Pair<>(timeStart / 1000, timeEnd / 1000);
        sPeriodCache.put(period, periodTimestamp);
        return periodTimestamp;
    }

    public static float encodeBarChartXAxisFloat(int timeSpan, long timestamp) {
        DateFormat format;
        int offset;
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            format = DAY_IN_MONTH_FORMAT;
            offset = 10000;
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            format = DAY_IN_WEEK_FORMAT;
            offset = 99;
        } else {
            format = HOUR_FORMAT;
            offset = 0;
        }
        return Float.valueOf(format.format(new Date(timestamp * 1000))) + offset;
    }

    public static String decodeBarChartXAxisFloat(float value, String[] weekName) {
        if (value >= 10000) {
            return String.valueOf((int) (value - 10000));
        } else if (value >= 100) {
            return weekName[(int) (value - 100)];
        } else {
            return String.format(Locale.getDefault(), "%02.0f:00", value);
        }
    }

    public static String getHourMinuteTime(long timestamp) {
        return HOUR_MINUTE_TIME.format(new Date(timestamp));
    }

    public static String getDateHourMinuteTime(long timestamp) {
        return DATE_HOUR_MINUTE_TIME.format(new Date(timestamp));
    }
}
