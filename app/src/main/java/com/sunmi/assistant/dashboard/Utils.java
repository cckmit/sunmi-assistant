package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;

import com.sunmi.assistant.R;

import java.text.ParseException;
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

    private static final Object LOCK = new Object();

    private static final int PERIOD_WEEK_OFFSET = 100;
    private static final int PERIOD_MONTH_OFFSET = 10000;

    private static final int DAYS_OF_WEEK = 7;

    private static String[] sWeekName;

    private static Calendar temp = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat tempFormat = new SimpleDateFormat();

    public static Pair<Long, Long> getPeriodTimestamp(int period) {
        temp.setTimeInMillis(System.currentTimeMillis());
        long timeStart;
        long timeEnd;
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH);
        int date = temp.get(Calendar.DATE);
        temp.clear();
        temp.setFirstDayOfWeek(Calendar.MONDAY);
        temp.set(year, month, date);

        if (period == Constants.TIME_PERIOD_TODAY) {
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.DATE, 1);
            timeEnd = temp.getTimeInMillis();
        } else if (period == Constants.TIME_PERIOD_YESTERDAY) {
            temp.add(Calendar.DATE, -1);
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.DATE, 1);
            timeEnd = temp.getTimeInMillis();
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
            int offset = temp.getFirstDayOfWeek() - dayOfWeek;
            temp.add(Calendar.DATE, offset > 0 ? offset - DAYS_OF_WEEK : offset);
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.DATE, DAYS_OF_WEEK);
            timeEnd = temp.getTimeInMillis();
        } else {
            temp.set(Calendar.DATE, 1);
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.MONTH, 1);
            timeEnd = temp.getTimeInMillis();
        }
        return new Pair<>(timeStart, timeEnd);
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
            temp.setTimeInMillis(System.currentTimeMillis());
            return new Pair<>(10000, temp.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
        }
    }

    public static long getStartTime(int period) {
        temp.setTimeInMillis(System.currentTimeMillis());
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH);
        int day = temp.get(Calendar.DATE);
        temp.clear();

        if (period == Constants.TIME_PERIOD_TODAY) {
            temp.set(year, month, day);
            return temp.getTimeInMillis();

        } else if (period == Constants.TIME_PERIOD_YESTERDAY) {
            temp.set(year, month, day);
            temp.add(Calendar.DATE, -1);
            return temp.getTimeInMillis();

        } else if (period == Constants.TIME_PERIOD_WEEK) {
            temp.setFirstDayOfWeek(Calendar.MONDAY);
            temp.set(year, month, day);
            int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
            int offset = temp.getFirstDayOfWeek() - dayOfWeek;
            temp.add(Calendar.DATE, offset > 0 ? offset - 7 : offset);
            return temp.getTimeInMillis();

        } else if (period == Constants.TIME_PERIOD_MONTH) {
            temp.set(year, month, 1);
            return temp.getTimeInMillis();
        }
        return 0;
    }

    /**
     * 根据时间维度获取折线图和柱状图X轴值范围，其中：
     * 1~25表示：天维度的的00:00~24:00
     * 101~107表示：周维度的周一到周日
     * 10001~100030表示：月维度的1~30日
     *
     * @param timestamp Unix时间戳
     * @return X轴值范围
     */
    public static float encodeChartXAxisFloat(int period, long timestamp) {
        temp.setTimeInMillis(timestamp);
        if (period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY) {
            return temp.get(Calendar.HOUR_OF_DAY) + 1;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            temp.setFirstDayOfWeek(Calendar.MONDAY);
            int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK) - 1;
            return PERIOD_WEEK_OFFSET + (dayOfWeek < 1 ? dayOfWeek + DAYS_OF_WEEK : dayOfWeek);
        } else {
            return PERIOD_MONTH_OFFSET + temp.get(Calendar.DATE);
        }
    }

    public static String convertXToXAxisName(Context context, float value) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        if (value > PERIOD_MONTH_OFFSET) {
            return String.valueOf((int) (value - PERIOD_MONTH_OFFSET));
        } else if (value > PERIOD_WEEK_OFFSET) {
            return sWeekName[(int) (value - PERIOD_WEEK_OFFSET) % DAYS_OF_WEEK];
        } else {
            return String.format(Locale.getDefault(), "%02.0f:00", value - 1);
        }
    }

    public static String convertXToMarkerName(Context context, int period, long time) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        temp.setTimeInMillis(time);
        if (period == Constants.TIME_PERIOD_TODAY || period == Constants.TIME_PERIOD_YESTERDAY) {
            int hour = temp.get(Calendar.HOUR_OF_DAY);
            return String.format(Locale.getDefault(), "%02d:00-%02d:00", hour, hour + 1);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            temp.setFirstDayOfWeek(Calendar.MONDAY);
            return sWeekName[temp.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            int month = temp.get(Calendar.MONTH) + 1;
            int day = temp.get(Calendar.DATE);
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

    public static String getWeekName(Context context, int timeIndex) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        return sWeekName[timeIndex % DAYS_OF_WEEK];
    }

    public static long parseDateTime(String pattern, String str) throws ParseException {
        synchronized (LOCK) {
            tempFormat.applyPattern(pattern);
            return tempFormat.parse(str).getTime();
        }
    }

    public static String formatDateTime(String pattern, long timestamp) {
        synchronized (LOCK) {
            tempFormat.applyPattern(pattern);
            return tempFormat.format(new Date(timestamp));
        }
    }

    public static boolean hasAuth(int source) {
        return (source & Constants.DATA_SOURCE_AUTH) != 0;
    }

    public static boolean hasImport(int source) {
        return (source & Constants.DATA_SOURCE_IMPORT) != 0;
    }

    public static boolean hasFs(int source) {
        return (source & Constants.DATA_SOURCE_FS) != 0;
    }

    public static boolean hasCustomer(int source) {
        return (source & Constants.DATA_SOURCE_CUSTOMER) != 0;
    }

    public static boolean hasFloating(int source){
        return (source & Constants.DATA_SOURCE_FLOATING) != 0;
    }

}
