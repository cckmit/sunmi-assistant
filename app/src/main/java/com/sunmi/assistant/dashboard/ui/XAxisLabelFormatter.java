package com.sunmi.assistant.dashboard.ui;

import android.content.Context;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sunmi.assistant.dashboard.Utils;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class XAxisLabelFormatter extends ValueFormatter {

    private Context context;

    public XAxisLabelFormatter(Context context) {
        this.context = context;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return Utils.convertXToXAxisName(context, value);
    }
}