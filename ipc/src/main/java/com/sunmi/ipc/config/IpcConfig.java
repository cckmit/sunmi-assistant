package com.sunmi.ipc.config;

import android.content.Context;

import sunmi.common.base.BaseConfig;

/**
 * Description:
 * Created by bruce on 2019/4/9.
 */
public class IpcConfig extends BaseConfig {
    public static String IPC_CLOUD_URL = "";
    public static String MQTT_HOST = "";
    public static String MQTT_PORT = "";

    @Override
    protected void initDev(Context context, String env) {
        IPC_CLOUD_URL = "http://47.96.240.44:35150/";
        MQTT_HOST = "47.96.240.44";
        MQTT_PORT = "30412";
    }

    @Override
    protected void initTest(Context context, String env) {
//        IPC_CLOUD_URL = "http://47.96.240.44:35150/";
//        MQTT_HOST = "47.96.240.44";
//        MQTT_PORT = "30412";
        IPC_CLOUD_URL = "http://47.99.16.199:30401/";
        MQTT_HOST = "47.99.16.199";
        MQTT_PORT = "30412";
    }

    @Override
    protected void initRelease(Context context, String env) {
        IPC_CLOUD_URL = "http://47.99.16.199:30401/";
        MQTT_HOST = "47.99.16.199";
        MQTT_PORT = "30412";
    }

}
