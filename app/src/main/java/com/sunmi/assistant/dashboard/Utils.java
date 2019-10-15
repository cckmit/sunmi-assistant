package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author yinhui
 * @since 2019-06-21
 */
public class Utils {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_HOUR_MINUTE = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_DATE_TIME = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private static final int PERIOD_WEEK_OFFSET = 100;
    private static final int PERIOD_MONTH_OFFSET = 10000;

    private static SparseArray<Pair<Long, Long>> sPeriodCache = new SparseArray<>(3);
    private static Calendar sLastCalendar = Calendar.getInstance();
    private static Calendar sTempCalendar = Calendar.getInstance();

    @Deprecated
    public static String getTrendNameByPeriod(Context context, int period) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return context.getResources().getString(R.string.dashboard_day_ratio);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return context.getResources().getString(R.string.dashboard_week_ratio);
        } else {
            return context.getResources().getString(R.string.dashboard_month_ratio);
        }
    }

    @Deprecated
    public static Pair<Long, Long> getPeriodTimestamp(int period) {
        sTempCalendar = Calendar.getInstance();
        if (sLastCalendar != null
                && (sTempCalendar.get(Calendar.DAY_OF_YEAR) != sLastCalendar.get(Calendar.DAY_OF_YEAR)
                || sTempCalendar.get(Calendar.YEAR) != sLastCalendar.get(Calendar.YEAR)
                || sTempCalendar.get(Calendar.ERA) != sLastCalendar.get(Calendar.ERA))) {
            sPeriodCache.clear();
        }
        sLastCalendar = sTempCalendar;
        sTempCalendar = Calendar.getInstance();
        Pair<Long, Long> periodTimestamp = sPeriodCache.get(period);
        if (periodTimestamp != null) {
            return periodTimestamp;
        }
        long timeStart;
        long timeEnd;
        Calendar c = sTempCalendar;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(year, month, date);
        if (period == Constants.TIME_PERIOD_MONTH) {
            c.clear();
            c.set(year, month, 1);
            timeStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            timeEnd = c.getTimeInMillis();
        } else if (period == Constants.TIME_PERIOD_WEEK) {
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

    /**
     * 根据时间维度获取折线图和柱状图X轴值范围，其中：
     * 1~25表示：天维度的的00:00~24:00
     * 101~107表示：周维度的周一到周日
     * 10001~100030表示：月维度的1~30日
     *
     * @param period 时间维度
     * @return X轴值范围
     */
    public static Pair<Integer, Integer> calcChartXAxisRange(int period) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return new Pair<>(0, 26);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return new Pair<>(100, 108);
        } else {
            Calendar c = Calendar.getInstance();
            return new Pair<>(10000, c.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
        }
    }

    @Deprecated
    public static float encodeChartXAxisFloat(int period, long timestamp) {
        sTempCalendar.setTimeInMillis(timestamp * 1000);
        if (period == Constants.TIME_PERIOD_MONTH) {
            return (float) (sTempCalendar.get(Calendar.DAY_OF_MONTH) + 10000);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            int index = sTempCalendar.get(Calendar.DAY_OF_WEEK);
            index = (index + 5) % 7;
            return index + 101;
        } else {
            return sTempCalendar.get(Calendar.HOUR_OF_DAY) + 1;
        }
    }

    /**
     * 根据时间维度获取折线图和柱状图X轴值范围，其中：
     * 1~25表示：天维度的的00:00~24:00
     * 101~107表示：周维度的周一到周日
     * 10001~100030表示：月维度的1~30日
     *
     * @param timeIndexFromServer 从服务器获取的时间序列值，目前1代表第一个值（00:00、周一、1日）
     * @return X轴值范围
     */
    public static float encodeChartXAxisFloat(int period, int timeIndexFromServer) {
        if (period == Constants.TIME_PERIOD_TODAY) {
            return timeIndexFromServer;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return PERIOD_WEEK_OFFSET + timeIndexFromServer;
        } else {
            return PERIOD_MONTH_OFFSET + timeIndexFromServer;
        }
    }

    public static String convertFloatToXAxisName(float value, String[] weekName) {
        if (value > PERIOD_MONTH_OFFSET) {
            return String.valueOf((int) (value - PERIOD_MONTH_OFFSET));
        } else if (value > PERIOD_WEEK_OFFSET) {
            return weekName[(int) (value - PERIOD_WEEK_OFFSET - 1)];
        } else {
            return String.format(Locale.getDefault(), "%02.0f:00", value - 1);
        }
    }

    public static String convertFloatToMarkerName(float value, String[] weekName) {
        if (value > PERIOD_MONTH_OFFSET) {
            return String.valueOf((int) (value - PERIOD_MONTH_OFFSET));
        } else if (value > PERIOD_WEEK_OFFSET) {
            return weekName[(int) (value - PERIOD_WEEK_OFFSET - 1)];
        } else {
            return String.format(Locale.getDefault(), "%02.0f:00-%02.0f:00", value - 1, value);
        }
    }

    public static String getHourMinute(long timestamp) {
        synchronized (DATE_FORMAT_HOUR_MINUTE) {
            return DATE_FORMAT_HOUR_MINUTE.format(new Date(timestamp));
        }
    }

    public static String getDateTime(long timestamp) {
        synchronized (DATE_FORMAT_DATE_TIME) {
            return DATE_FORMAT_DATE_TIME.format(new Date(timestamp));
        }
    }

    public static String getMonthName(List<BarEntry> data) {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        if (day == 1 && data.size() >= 28) {
            month = (month + 11) % 12;
        } else if (day >= 28 && data.size() == 1) {
            month = (month + 1) % 12;
        }
        return String.format(Locale.getDefault(), "%02d-", month + 1);
    }

    public static boolean hasSaas(int source) {
        return (source & Constants.DATA_SOURCE_SAAS) != 0;
    }

    public static boolean hasFs(int source) {
        return (source & Constants.DATA_SOURCE_FS) != 0;
    }

    public static boolean hasCustomer(int source) {
        return (source & Constants.DATA_SOURCE_CUSTOMER) != 0;
    }

}
