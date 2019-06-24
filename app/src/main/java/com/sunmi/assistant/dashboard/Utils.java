package com.sunmi.assistant.dashboard;

import android.util.Pair;

import com.sunmi.assistant.R;

import java.util.Calendar;

import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;

/**
 * @author jacob
 * @since 2019-06-21
 */
public class Utils {

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

    public static String getTrendNameByTimeSpan(int timeSpan) {
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            return BaseApplication.getContext().getResources().getString(R.string.dashboard_month_ratio);
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
            return BaseApplication.getContext().getResources().getString(R.string.dashboard_week_ratio);
        } else {
            return BaseApplication.getContext().getResources().getString(R.string.dashboard_day_ratio);
        }
    }

    public static Pair<Long, Long> calcTimeSpan(int timeSpan) {
        long timeStart;
        long timeEnd;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(year, month, date);
        if (timeSpan == DashboardContract.TIME_SPAN_MONTH) {
            c.clear();
            c.set(year, month, 1);
            timeStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            timeEnd = c.getTimeInMillis();
        } else if (timeSpan == DashboardContract.TIME_SPAN_WEEK) {
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
        return new Pair<>(timeStart / 1000, timeEnd / 1000);
    }
}
