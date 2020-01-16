package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.Constants;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-16.
 */
public class XAxisFrequencyDistributionFormatter extends ValueFormatter {

    private Context context;
    private int max;

    public XAxisFrequencyDistributionFormatter(Context context) {
        this.context = context;
    }

    public void setPeriod(int period) {
        if (period == Constants.TIME_PERIOD_YESTERDAY) {
            max = 4;
        } else if (period == Constants.TIME_PERIOD_WEEK) {
            max = 10;
        } else {
            max = 15;
        }
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (value > max) {
            return context.getString(R.string.dashborad_card_customer_frequency_above, max);
        } else {
            return String.valueOf((int) value);
        }
    }
}
