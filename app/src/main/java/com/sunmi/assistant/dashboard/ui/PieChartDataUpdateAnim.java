package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

/**
 * @author yinhui
 * @since 2019-07-02
 */
public class PieChartDataUpdateAnim extends ChartDataChangeAnimation<PieEntry, PieData> {

    public PieChartDataUpdateAnim(int duration, Chart<PieData> chart,
                                  List<PieEntry> oldData, List<PieEntry> newData) {
        super(duration, chart, oldData, newData);
    }

    @Override
    public PieEntry newEntry(PieEntry entry, float newValue) {
        return new PieEntry(newValue, entry.getLabel());
    }
}
