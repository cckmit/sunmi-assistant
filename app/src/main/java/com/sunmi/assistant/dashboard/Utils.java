package com.sunmi.assistant.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;

import com.sunmi.assistant.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-21
 */
public class Utils {

    public static final String DATA_NONE = "--";
    public static final String DATA_ZERO = "0";
    public static final String DATA_ZERO_RATIO = "0%";

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_HOUR_MINUTE = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_DATE_TIME = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private static final DecimalFormat FORMAT_MAX_SINGLE_DECIMAL = new DecimalFormat("#.#");
    private static final DecimalFormat FORMAT_THOUSANDS_DOUBLE_DECIMAL = new DecimalFormat(",###,##0.00");
    private static final DecimalFormat FORMAT_THOUSANDS = new DecimalFormat(",###,###");

    private static final Object LOCK = new Object();

    public static final float THRESHOLD_MILLION = 1_000_000;

    public static final float THRESHOLD_10THOUSAND = 10_000;
    public static final float THRESHOLD_100MILLION = 100_000_000;

    private static final float THRESHOLD_PERCENT = 0.01f;
    private static final float THRESHOLD_PERCENT_MIN = 0.00005f;
    private static final float THRESHOLD_THOUSANDTH = 0.0001f;
    private static final float THRESHOLD_THOUSANDTH_MIN = 0.000005f;

    private static final int MULTIPLIER_HUNDRED = 100;
    private static final int MULTIPLIER_THOUSAND = 1000;

    private static final float SMALL_TEXT_SIZE = 0.6f;

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
            return new Pair<>(-2, 26);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return new Pair<>(100, 108);
        } else {
            temp.setTimeInMillis(System.currentTimeMillis());
            return new Pair<>(9997, temp.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
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

    /**
     * 格式化销售额数据
     *
     * @param context   上下文
     * @param value     值
     * @param isFloat   是否为浮点型，例：销售额为True，人数为False。
     * @param highlight 是否突出显示数据
     * @return 格式化的字符串
     */
    public static CharSequence formatNumber(Context context, double value, boolean isFloat, boolean highlight) {
        String result;
        if (value < 0) {
            result = DATA_NONE;
        } else if (!CommonHelper.isGooglePlay()) {
            // 国内版数据展示规则
            if (value > THRESHOLD_100MILLION) {
                result = context.getString(R.string.str_num_100_million, value / THRESHOLD_100MILLION);
            } else if (value > THRESHOLD_10THOUSAND) {
                result = context.getString(R.string.str_num_10_thousands, value / THRESHOLD_10THOUSAND);
            } else {
                result = String.format(Locale.getDefault(), isFloat ? "%.2f" : "%.0f", value);
            }
        } else {
            // 海外版数据展示规则
            if (value > THRESHOLD_MILLION) {
                result = FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(value / THRESHOLD_MILLION) + "m";
            } else {
                result = isFloat ? FORMAT_THOUSANDS_DOUBLE_DECIMAL.format(value)
                        : FORMAT_THOUSANDS.format(value);
            }
        }

        if (!highlight || CommonHelper.isGooglePlay() || value <= THRESHOLD_10THOUSAND) {
            return result;
        }

        int len = result.length();
        SpannableString s = new SpannableString(result);
        s.setSpan(new RelativeSizeSpan(SMALL_TEXT_SIZE), len - 1, len, 0);
        return s;
    }

    /**
     * 格式化标准百分比数据
     *
     * @param value     值
     * @param isRate    是否是比率数据。转化率，进店率等为True；人数占比为False。
     * @param highlight 是否突出显示数据
     * @return 格式化的字符串
     */
    public static CharSequence formatPercent(float value, boolean isRate, boolean highlight) {
        String result;
        if (value < 0) {
            result = DATA_NONE;
        } else if (isRate) {
            if (value >= THRESHOLD_THOUSANDTH_MIN && value < THRESHOLD_THOUSANDTH) {
                result = String.format(Locale.getDefault(), "%.2f‰", value * MULTIPLIER_THOUSAND);
            } else {
                result = String.format(Locale.getDefault(), "%.2f%%", value * MULTIPLIER_HUNDRED);
            }
        } else {
            if (value >= THRESHOLD_PERCENT_MIN && value < THRESHOLD_PERCENT) {
                result = String.format(Locale.getDefault(), "%.2f%%", value * MULTIPLIER_HUNDRED);
            } else {
                result = String.format(Locale.getDefault(), "%.0f%%", value * MULTIPLIER_HUNDRED);
            }
        }
        if (!highlight) {
            return result;
        }

        int len = result.length();
        SpannableString s = new SpannableString(result);
        s.setSpan(new RelativeSizeSpan(SMALL_TEXT_SIZE), len - 1, len, 0);
        return s;
    }

    public static CharSequence formatFrequency(Context context, float value, int period, boolean highlight) {
        String base;
        if (period == Constants.TIME_PERIOD_MONTH) {
            base = context.getString(R.string.dashboard_card_customer_frequency_data_month);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            base = context.getString(R.string.dashboard_card_customer_frequency_data_week);
        } else {
            base = context.getString(R.string.dashboard_card_customer_frequency_data_day);
        }

        String result;
        if (value < 0) {
            result = DATA_NONE;
        } else {
            result = String.format(Locale.getDefault(), base, FORMAT_MAX_SINGLE_DECIMAL.format(value));
        }
        if (!highlight) {
            return result;
        }

        int startLen = base.indexOf("%s");
        int endLen = base.length() - startLen - 2;
        SpannableString s = new SpannableString(result);

        s.setSpan(new RelativeSizeSpan(SMALL_TEXT_SIZE), 0, startLen, 0);
        s.setSpan(new RelativeSizeSpan(SMALL_TEXT_SIZE), s.length() - endLen, s.length(), 0);
        return s;
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

    public static boolean hasFloating(int source) {
        return (source & Constants.DATA_SOURCE_FLOATING) != 0;
    }

}
