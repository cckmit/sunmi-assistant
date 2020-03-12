package com.datelibrary.utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.IsoFields;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by codbking on 2016/12/15.
 */

public class DateUtils {

    //获取小时
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    //获取分钟
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    //获取周
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取周
    public static int getWeek(int year, int moth, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, moth - 1, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取年
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    //获取月
    public static int getMoth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    //获取日
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    public static Date getDate(int year, int moth, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, moth - 1, day, hour, minute);
        return calendar.getTime();
    }

    /**
     * 获取当前时间所在年的第几周
     */
    public static int getWeekOfYear(Date date) {
        Instant time = Instant.ofEpochMilli(date.getTime());
        LocalDate localDate = time.atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    /**
     * 获取当前时间所在年的最大周数
     */
    public static int getMaxWeekNumOfYear(int year) {
        LocalDate date = LocalDate.of(year, 12, 31);
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    /**
     * 获取某年的第几周的开始日期
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        LocalDate date = LocalDate.now()
                .withYear(year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(ChronoField.DAY_OF_WEEK, 1);
        return new Date(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    /**
     * 获取某年的第几周的结束日期
     **/
    public static Date getLastDayOfWeek(int year, int week) {
        LocalDate date = LocalDate.now()
                .withYear(year)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(ChronoField.DAY_OF_WEEK, 7);
        return new Date(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    // 获取当前时间所在周的开始日期
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    // 获取当前时间所在周的结束日期
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

}
