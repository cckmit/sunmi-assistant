package com.sunmi.assistant.dashboard.ui.chart;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public interface IMarkerFormatter {

    CharSequence valueFormat(float value);

    CharSequence xAxisFormat(float x);

    CharSequence timeFormat(long time);
}
