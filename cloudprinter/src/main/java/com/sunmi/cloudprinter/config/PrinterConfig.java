package com.sunmi.cloudprinter.config;

import android.content.Context;

import sunmi.common.base.BaseConfig;

/**
 * Description:
 * Created by bruce on 2019/4/9.
 */
public class PrinterConfig extends BaseConfig {
    public static String IOT_CLOUD_URL = "";
    public static String IOT_H5_URL = "";

    @Override
    protected void initDev(Context context, String env) {
        IOT_CLOUD_URL = "http://dev.webapi.sunmi.com/webapi/cloudprinter-h5/web/shop/1.0/?service=";
        IOT_H5_URL = "http://cph5.dev.sunmi.com/";
    }

    @Override
    protected void initTest(Context context, String env) {
        IOT_CLOUD_URL = "http://test.webapi.sunmi.com/webapi/cloudprinter-h5/web/shop/1.0/?service=";
        IOT_H5_URL = "http://cph5.test.sunmi.com/";
    }

    @Override
    protected void initRelease(Context context, String env) {
        IOT_CLOUD_URL = "http://webapi.sunmi.com/webapi/iot/web/merchant/1.0/?service=";
        IOT_H5_URL = "http://h5.sunmi.com/cloud-print/index.html";
    }

    @Override
    protected void initUat(Context context, String env) {
        IOT_CLOUD_URL = "http://uat.webapi.sunmi.com/webapi/cloudprinter-h5/web/shop/1.0/?service=";
        IOT_H5_URL = "http://cph5.uat.sunmi.com/";
    }

}
