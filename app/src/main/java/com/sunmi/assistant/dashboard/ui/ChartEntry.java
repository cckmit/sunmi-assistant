package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.data.BarEntry;

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
}
