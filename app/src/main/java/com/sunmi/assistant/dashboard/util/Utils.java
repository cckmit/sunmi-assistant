package com.sunmi.assistant.dashboard.util;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.sunmi.assistant.R;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sunmi.common.model.Interval;
import sunmi.common.utils.CommonHelper;

/**
 * @author yinhui
 * @since 2019-06-21
 */
public class Utils {

    public static final String TAG = "Dashboard";

    public static final String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_API_TIME = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_API_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_DATE_MARKER = "MM.dd";

    public static final String DATA_NONE = "--";
    public static final String DATA_ZERO = "0";
    public static final String DATA_ZERO_RATIO = "0%";

    public static final long MILLIS_OF_DAY = 86_400_000;
    public static final long MILLIS_OF_HOUR = 3_600_000;

    private static final DecimalFormat FORMAT_MAX_SINGLE_DECIMAL = new DecimalFormat("#.#");
    private static final DecimalFormat FORMAT_THOUSANDS_DOUBLE_DECIMAL = new DecimalFormat(",###,##0.00");
    private static final DecimalFormat FORMAT_THOUSANDS = new DecimalFormat(",###,###");

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

    private static Map<String, DateTimeFormatter> formatterMap = new HashMap<>();

    private static DateTimeFormatter getTimeFormat(String pattern) {
        DateTimeFormatter formatter = formatterMap.get(pattern);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(pattern);
            formatterMap.put(pattern, formatter);
        }
        return formatter;
    }

    /**
     * 解析时间字符串为时间戳
     *
     * @param pattern 字符串模式
     * @param str     时间字符串
     * @return 时间戳
     * @throws DateTimeException 解析异常
     */
    public static long parseTime(String pattern, String str) {
        LocalDateTime time = LocalDateTime.parse(str, getTimeFormat(pattern));
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 格式化时间戳
     *
     * @param pattern   字符串模式
     * @param timestamp 时间戳
     * @return 格式化的时间字符串
     */
    public static String formatTime(String pattern, long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime time = LocalDateTime.ofInstant(instant, zone);
        return time.format(getTimeFormat(pattern));
    }

    /**
     * 根据时间维度计算该时间段的起止时间戳
     *
     * @param period    时间维度
     * @param timestamp 时间戳
     * @return 返回时间戳所在时间维度的起止时间
     */
    public static Interval getPeriodTimestamp(int period, long timestamp) {
        temp.setFirstDayOfWeek(Calendar.MONDAY);
        temp.setTimeInMillis(timestamp);
        long timeStart;
        long timeEnd;
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH);
        int date = temp.get(Calendar.DATE);
        temp.clear();
        temp.set(year, month, date);

        if (period == Constants.TIME_PERIOD_DAY) {
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.DATE, 1);
            timeEnd = temp.getTimeInMillis();
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
            int index = temp.getFirstDayOfWeek() - dayOfWeek;
            temp.add(Calendar.DATE, index > 0 ? index - DAYS_OF_WEEK : index);
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.DATE, DAYS_OF_WEEK);
            timeEnd = temp.getTimeInMillis();
        } else {
            temp.set(Calendar.DATE, 1);
            timeStart = temp.getTimeInMillis();
            temp.add(Calendar.MONTH, 1);
            timeEnd = temp.getTimeInMillis();
        }
        return new Interval(timeStart, timeEnd);
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
        if (period == Constants.TIME_PERIOD_DAY) {
            return new Pair<>(-2, 26);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            return new Pair<>(100, 108);
        } else {
            temp.setTimeInMillis(System.currentTimeMillis());
            return new Pair<>(9997, temp.getActualMaximum(Calendar.DAY_OF_MONTH) + 10001);
        }
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
        if (period == Constants.TIME_PERIOD_DAY) {
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

    public static String getWeekName(Context context, int timeIndex) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        return sWeekName[timeIndex % DAYS_OF_WEEK];
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
        if (!CommonHelper.isGooglePlay()) {
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

    /**
     * 格式化进店频次数据
     *
     * @param context   上下文
     * @param value     值
     * @param period    时间维度
     * @param highlight 是否突出显示数据
     * @return 格式化的字符串
     */
    public static CharSequence formatFrequency(Context context, float value, int period, boolean highlight) {
        String base;
        if (period == Constants.TIME_PERIOD_MONTH) {
            base = context.getString(R.string.dashboard_unit_frequency_data_month);
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            base = context.getString(R.string.dashboard_unit_frequency_data_week);
        } else {
            base = context.getString(R.string.dashboard_unit_frequency_data_day);
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

    public static int getGradientColor(@ColorInt int startColor, @ColorInt int endColor, float fraction) {
        fraction = Math.min(1f, Math.max(fraction, 0f));
        int startAlpha = (startColor & 0xff000000) >>> 24;
        int startRed = (startColor & 0xff0000) >>> 16;
        int startGreen = (startColor & 0xff00) >>> 8;
        int startBlue = (startColor & 0xff);
        int endAlpha = (endColor & 0xff000000) >>> 24;
        int endRed = (endColor & 0xff0000) >>> 16;
        int endGreen = (endColor & 0xff00) >>> 8;
        int endBlue = (endColor & 0xff);
        int alpha = (int) (startAlpha + (endAlpha - startAlpha) * fraction) & 0xff;
        int red = (int) (startRed + (endRed - startRed) * fraction) & 0xff;
        int green = (int) (startGreen + (endGreen - startGreen) * fraction) & 0xff;
        int blue = (int) (startBlue + (endBlue - startBlue) * fraction) & 0xff;
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static void setupLineChart(LineChart chart) {
        Context context = chart.getContext();
        // 设置通用图表
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);

        // 设置X轴
        XAxis lineXAxis = chart.getXAxis();
        lineXAxis.setDrawAxisLine(true);
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setTextSize(10f);
        lineXAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置Y轴
        YAxis lineYAxis = chart.getAxisLeft();
        lineYAxis.setDrawAxisLine(false);
        lineYAxis.setGranularityEnabled(true);
        lineYAxis.setGranularity(1f);
        lineYAxis.setTextSize(10f);
        lineYAxis.setTextColor(ContextCompat.getColor(context, R.color.text_disable));
        lineYAxis.setAxisMinimum(0f);
        lineYAxis.setDrawGridLines(true);
        lineYAxis.setGridColor(ContextCompat.getColor(context, R.color.black_10));
        lineYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        lineYAxis.setYOffset(-5f);
        lineYAxis.setXOffset(-1f);
    }

    public static void setupLineChartDataSet(Context context, LineDataSet set, int color) {
        float dashLength = CommonHelper.dp2px(context, 4f);
        float dashSpaceLength = CommonHelper.dp2px(context, 2f);
        set.setColor(color);
        set.setHighLightColor(color);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setLineWidth(2f);
        set.setHighlightLineWidth(1f);
        set.enableDashedHighlightLine(dashLength, dashSpaceLength, 0);
        set.setLineContinuous(false);
        set.setLinePhase(1f);
    }

}
