package com.sunmi.assistant.dashboard.ui;

/**
 * @author yinhui
 * @date 2020-01-14
 */
public interface IMarkerFormatter {

    String valueFormat(float value);

    String timeFormat(long time);
}
