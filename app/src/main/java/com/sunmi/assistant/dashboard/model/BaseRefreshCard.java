package com.sunmi.assistant.dashboard.model;

import android.util.Pair;

import com.sunmi.assistant.dashboard.DashboardContract;
import com.sunmi.assistant.dashboard.DataRefreshHelper;

/**
 * @author jacob
 * @since 2019-06-21
 */
public class BaseRefreshCard<T> {

    public static final int STATE_INIT = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_NEED_REFRESH = 2;

    public int timeSpan = DashboardContract.TIME_SPAN_INIT;
    public Pair<Long, Long> timeSpanPair;
    public int state = STATE_INIT;

    private DataRefreshHelper<T> helper;

    BaseRefreshCard(DataRefreshHelper<T> refresh) {
        this.helper = refresh;
    }

    public void setTimeSpan(int timeSpan, Pair<Long, Long> timeSpanPair) {
        if (this.timeSpan == timeSpan) {
            return;
        }
        this.timeSpan = timeSpan;
        this.timeSpanPair = timeSpanPair;
        this.state = STATE_NEED_REFRESH;
    }

    public void refresh(boolean updateTitle) {
        //noinspection unchecked
        helper.refresh((T) this, updateTitle);
    }
}
