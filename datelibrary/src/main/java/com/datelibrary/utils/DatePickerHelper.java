package com.datelibrary.utils;

import android.content.Context;

import com.datelibrary.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sunmi.common.utils.DateTimeUtils;

/**
 * Created by codbking on 2016/8/10.
 */
public class DatePickerHelper {

    private Context context;
    //开始年
    private int YEAR_START;
    //开始月
    private int MONTH_START;
    //开始天
    private int DAY_START;
    //开始周
    private int WEEK_START;
    //开始小时
    private int HOUR_START;
    //开始分钟
    private int MINUTE_START;
    //开始时间
    private Date startDate = new Date();
    //年份限制，上下5年
    private int yearLimt = 5;
    private int yearEnd;
    private int weekEnd;
    private int monthEnd;
    private int dayEnd;

    private ArrayList<Integer> tem = new ArrayList<>();
    private ArrayList<String> dispalyTem = new ArrayList<>();
    private List<String> week = new ArrayList<>(53);
    private String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public enum Type {
        YEAR,
        MOTH,
        DAY,
        WEEK,
        HOUR,
        MINUTE
    }

    public DatePickerHelper(Context context, Date date) {
        this.context = context;
        this.yearEnd = DateUtils.getYear(date);
        this.weekEnd = DateUtils.getWeekOfYear(date);
        this.monthEnd = DateUtils.getMoth(date);
        this.dayEnd = DateUtils.getDay(date);
        init();
    }

    private void init() {
        Date date = startDate;
        //获取年 月 日 时 分
        YEAR_START = DateUtils.getYear(date);
        MONTH_START = DateUtils.getMoth(date);
        DAY_START = DateUtils.getDay(date);
        WEEK_START = DateUtils.getWeekOfYear(date);
        HOUR_START = DateUtils.getHour(date);
        MINUTE_START = DateUtils.getMinute(date);
    }

    //设置初始化时间
    public void setStartDate(Date date, int yearLimt) {

        this.startDate = date;
        this.yearLimt = yearLimt;

        if (this.startDate == null) {
            this.startDate = new Date();
        }
        init();
    }

    public int getToady(Type type) {
        switch (type) {
            case YEAR:
                return YEAR_START;
            case MOTH:
                return MONTH_START;
            case DAY:
                return DAY_START;
            case WEEK:
                return WEEK_START;
            case HOUR:
                return HOUR_START;
            case MINUTE:
                return MINUTE_START;
        }
        return 0;
    }

    public String[] getDisplayValue(Integer[] arr, String per) {
        dispalyTem.clear();
        for (Integer i : arr) {
            String value = i < 10 ? ("0" + i) : "" + i;
            dispalyTem.add(value + per);
        }
        return dispalyTem.toArray(new String[0]);
    }

    public Integer[] genMonth(int year) {
        if (year == yearEnd) {
            List<Integer> months = new ArrayList<>();
            for (int i = 0; i < monthEnd; i++) {
                months.add(i + 1);
            }
            return months.toArray(new Integer[0]);
        } else {
            return new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        }
    }

    public Integer[] genMonth() {
        return genMonth(YEAR_START);
    }

    public Integer[] genHour() {
        return genArr(24, true);
    }

    public Integer[] genMinut() {
        return genArr(60, true);
    }

    public Integer[] genArr(int size, boolean isZero) {
        tem.clear();
        for (int i = isZero ? 0 : 1; i < (isZero ? size : size + 1); i++) {
            tem.add(i);
        }
        return tem.toArray(new Integer[0]);
    }

    //生成年
    public Integer[] genYear() {
        tem.clear();
        for (int i = yearEnd - yearLimt; i < yearEnd; i++) {
            tem.add(i);
        }
        tem.add(yearEnd);
        return tem.toArray(new Integer[0]);
    }

    public String[] genWeek(int year) {
        week.clear();
        int count;
        if (year == yearEnd) {
            count = weekEnd;
        } else {
            count = DateUtils.getMaxWeekNumOfYear(year);
        }
        for (int i = 0; i < count; i++) {
            String pattern = context.getString(R.string.pick_date_format_per_week);
            String firstDay = DateTimeUtils.formatDate(pattern, DateUtils.getFirstDayOfWeek(year, i));
            String endDay = DateTimeUtils.formatDate(pattern, DateUtils.getLastDayOfWeek(year, i));
            week.add(context.getString(R.string.pick_per_week, i + 1, firstDay, endDay));
        }
        return week.toArray(new String[0]);
    }

    public String[] genWeek() {
        return genWeek(YEAR_START);
    }

    public Integer[] genDay(int year, int moth) {
        Calendar calendar = Calendar.getInstance();
        if (year == yearEnd && moth == monthEnd) {
            calendar.set(year, moth - 1, dayEnd);
        } else {
            calendar.set(year, moth, 1);
            calendar.add(Calendar.DATE, -1);
        }
        int day = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(calendar.getTime()));
        return genArr(day, false);
    }

    public Integer[] genDay() {
        return genDay(YEAR_START, MONTH_START);
    }


    public int findIndextByValue(int value, Integer[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (value == arr[i]) {
                return i;
            }
        }
        return -1;
    }


    public String getDisplayWeek(int year, int moth, int day) {
        return weeks[DateUtils.getWeek(year, moth, day) - 1];
    }

    public String getDisplayStartWeek() {
        return getDisplayWeek(YEAR_START, MONTH_START, DAY_START);
    }

}
