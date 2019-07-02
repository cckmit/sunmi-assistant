package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class BarChartDataUpdateAnim extends ChartDataChangeAnimation<BarEntry, BarData> {

    public BarChartDataUpdateAnim(int duration, Chart<BarData> chart,
                                  List<BarEntry> oldData, List<BarEntry> newData) {
        super(duration, chart, oldData, newData);
    }

    @Override
    public BarEntry newEntry(BarEntry entry, float newValue) {
        return new BarEntry(entry.getX(), newValue);
    }
}