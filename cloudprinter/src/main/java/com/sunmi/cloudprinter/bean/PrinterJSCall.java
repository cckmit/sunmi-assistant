package com.sunmi.cloudprinter.bean;

import android.webkit.JavascriptInterface;

import sunmi.common.utils.SecurityUtils;

public class PrinterJSCall {

    private String userId;
    private String merchantId;
    private String sn;

    public PrinterJSCall(String userId, String merchantId, String sn) {
        this.userId = userId;
        this.merchantId = merchantId;
        this.sn = sn;
    }

    @JavascriptInterface
    public String getInfo() {

        return String.format("{\"userId\":\"%s\",\"merchantId\":\"%s\",\"sn\":\"%s\",\"token\":\"%s\"}",
                userId, merchantId, sn, SecurityUtils.md5(userId));
    }
}
