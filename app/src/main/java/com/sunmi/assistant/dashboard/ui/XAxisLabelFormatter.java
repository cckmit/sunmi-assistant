package com.sunmi.assistant.dashboard.ui;

import android.content.Context;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.Utils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class XAxisLabelFormatter extends ValueFormatter {

    private static String[] sWeekName;

    public XAxisLabelFormatter(Context context) {
        sWeekName = context.getResources().getStringArray(R.array.week_name);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return Utils.decodeBarChartXAxisFloat(value, sWeekName);
    }
}