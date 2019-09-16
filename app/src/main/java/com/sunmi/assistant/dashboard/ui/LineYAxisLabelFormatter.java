package com.sunmi.assistant.dashboard.ui;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class LineYAxisLabelFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return String.format(Locale.getDefault(), "%d%%", (int) (value * 100));
    }
}