package com.sunmi.assistant.dashboard.data;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public interface Callback<T> {

    void onLoaded(T result);

    void onFail();
}
