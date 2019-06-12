package com.sunmi.cloudprinter.bean;

import android.webkit.JavascriptInterface;

import sunmi.common.utils.SecurityUtils;

public class PrinterJSCall {

    private String userId;
    private String merchantId;
    private String sn;
    private int channelId;

    public PrinterJSCall(String userId, String merchantId, String sn, int channelId) {
        this.userId = userId;
        this.merchantId = merchantId;
        this.sn = sn;
        this.channelId = channelId;
    }

    @JavascriptInterface
    public String getInfo() {
        return String.format("{\"userId\":\"%s\",\"merchantId\":\"%s\",\"sn\":\"%s\",\"token\":\"%s\",\"channelId\":\"%s\"}",
                userId, merchantId, sn, SecurityUtils.md5(userId), channelId);
    }
}
