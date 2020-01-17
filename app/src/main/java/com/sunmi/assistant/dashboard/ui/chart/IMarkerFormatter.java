package com.sunmi.assistant.dashboard.ui.chart;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public interface IMarkerFormatter {

    String valueFormat(float value);

    String xAxisFormat(float x);

    String timeFormat(long time);
}
