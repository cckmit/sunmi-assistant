package com.sunmi.assistant.dashboard.ui.chart;

import android.support.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.sunmi.assistant.dashboard.util.Utils;

/**
 * @author yinhui
 * @date 2019-10-15
 */
public class ChartEntry extends BarEntry {

    private long time;

    public ChartEntry(float x, float y, long time) {
        super(x, y);
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    @NonNull
    public String toString() {
        return super.toString() + "; time:" + Utils.formatTime(Utils.FORMAT_DATE_TIME, time);
    }
}
