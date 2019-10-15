package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.assistant.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private static final long MILLIS_PER_HOUR = 3600;
    private static final long MILLIS_PER_DAY = 3600 * 24;

    private static String[] sWeekName;

    private static SparseArray<Pair<Long, Long>> sPeriodCache = new SparseArray<>(3);
    private static Calendar sLastCalendar = Calendar.getInstance();
    private static Calendar sTempCalendar = Calendar.getInstance();

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
        if (period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY) {
            return new Pair<>(0, 26);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return new Pair<>(100, 108);
        } else {
            Calendar c = Calendar.getInstance();
            return new Pair<>(10000, c.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
        }
    }

    /**
     * 根据时间维度获取折线图和柱状图X轴值范围，其中：
     * 1~25表示：天维度的的00:00~24:00
     * 101~107表示：周维度的周一到周日
     * 10001~100030表示：月维度的1~30日
     *
     * @param timeIndex 从服务器获取的时间序列值，目前1代表第一个值（00:00、周一、1日）
     * @return X轴值范围
     */
    public static float encodeChartXAxisFloat(int period, int timeIndex) {
        if (period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY) {
            return timeIndex;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return PERIOD_WEEK_OFFSET + timeIndex;
        } else {
            return PERIOD_MONTH_OFFSET + timeIndex;
        }
    }

    public static String convertXToXAxisName(Context context, float value) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        if (value > PERIOD_MONTH_OFFSET) {
            return String.valueOf((int) (value - PERIOD_MONTH_OFFSET));
        } else if (value > PERIOD_WEEK_OFFSET) {
            return sWeekName[(int) (value - PERIOD_WEEK_OFFSET - 1)];
        } else {
            return String.format(Locale.getDefault(), "%02.0f:00", value - 1);
        }
    }

    public static String convertXToMarkerName(Context context, int period, long time) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        sTempCalendar.clear();
        sTempCalendar.setTimeInMillis(time * 1000);
        if (period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY) {
            int hour = sTempCalendar.get(Calendar.HOUR_OF_DAY);
            return String.format(Locale.getDefault(), "%02d:00-%02d:00", hour, hour + 1);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            sTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            int dayOfWeek = sTempCalendar.get(Calendar.DAY_OF_WEEK) - 2;
            return sWeekName[dayOfWeek < 0 ? dayOfWeek + 7 : dayOfWeek];
        } else {
            int month = sTempCalendar.get(Calendar.MONTH) + 1;
            int day = sTempCalendar.get(Calendar.DATE);
            return String.format(Locale.getDefault(), "%02d-%02d", month, day);
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

    public static long getTime(int period, int timeIndex, int size) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        c.clear();
        if (period == Constants.TIME_PERIOD_TODAY) {
            c.set(year, month, day);
            return c.getTimeInMillis() / 1000 + (timeIndex - 1) * MILLIS_PER_HOUR;
        } else if (period == Constants.TIME_PERIOD_YESTERDAY) {
            c.set(year, month, day);
            c.add(Calendar.DATE, -1);
            return c.getTimeInMillis() / 1000 + (timeIndex - 1) * MILLIS_PER_HOUR;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.set(year, month, day);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int offset = c.getFirstDayOfWeek() - dayOfWeek;
            c.add(Calendar.DATE, offset > 0 ? offset - 7 : offset);
            return c.getTimeInMillis() / 1000 + (timeIndex - 1) * MILLIS_PER_DAY;
        } else {
            if (day == 1 && size >= 28) {
                month = (month + 11) % 12;
            } else if (day >= 28 && size == 1) {
                month = (month + 1) % 12;
            }
            c.set(year, month, timeIndex);
            return c.getTimeInMillis() / 1000;
        }
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
