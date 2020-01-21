package com.sunmi.assistant.dashboard.ui.chart;

import android.content.Context;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public interface IMarkerFormatter {

    CharSequence valueFormat(Context context, float value);

    CharSequence xAxisFormat(Context context, float x);

    CharSequence timeFormat(Context context, long time);
}
