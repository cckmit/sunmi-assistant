package com.sunmi.assistant.dashboard.model;

import android.util.Pair;

import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DataRefreshCallback;
import com.sunmi.assistant.dashboard.DataRefreshHelper;

/**
 * @author jacob
 * @since 2019-06-21
 */
public class BaseRefreshCard<T> {

    public static final int STATE_INIT = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAILED = 3;

    public static final int FLAG_INIT = 0;
    public static final int FLAG_NORMAL = 1;
    public static final int FLAG_NEED_REFRESH = 2;

    public int timeSpan = DashboardContract.TIME_SPAN_INIT;
    public Pair<Long, Long> timeSpanPair;
    public int state = STATE_INIT;
    public int flag = FLAG_INIT;

    public DataRefreshHelper<T> helper;
    public DataRefreshCallback callback;

    BaseRefreshCard(DataRefreshHelper<T> refresh) {
        this.helper = refresh;
    }

    public void setCallback(DataRefreshCallback callback) {
        this.callback = callback;
    }

    public void setTimeSpan(int timeSpan, Pair<Long, Long> timeSpanPair) {
        if (this.timeSpan == timeSpan) {
            return;
        }
        this.timeSpan = timeSpan;
        this.timeSpanPair = timeSpanPair;
        refresh();
    }

    public void refresh() {
        //noinspection unchecked
        helper.refresh((T) this);
    }
}
