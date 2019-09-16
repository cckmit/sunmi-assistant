package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class PieChartLabelFormatter extends ValueFormatter {

    @Override
    public String getPieLabel(float value, PieEntry entry) {
        return entry.getLabel() + ":" + (int) entry.getValue();
    }
}