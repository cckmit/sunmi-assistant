package com.sunmi.assistant.dashboard;

/**
 * @author yinhui
 * @since 2019-06-24
 */
public interface DataRefreshCallback {
    void onSuccess();

    void onFail();
}
