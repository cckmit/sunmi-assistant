package com.sunmi.cloudprinter.bean;

import android.webkit.JavascriptInterface;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.SecurityUtils;
import sunmi.common.view.webview.BaseJSCall;
import sunmi.common.view.webview.SMWebView;

public class PrinterJSCall extends BaseJSCall {

    private String userId;
    private String shopId;
    private String sn;
    private int channelId;

    public PrinterJSCall(BaseActivity activity, SMWebView webView, String userId, String shopId, String sn, int channelId) {
        super(activity, webView);
        this.userId = userId;
        this.shopId = shopId;
        this.sn = sn;
        this.channelId = channelId;
    }

    @JavascriptInterface
    public String getInfo() {
        return String.format("{\"userId\":\"%s\",\"merchantId\":\"%s\",\"sn\":\"%s\",\"token\":\"%s\",\"channelId\":\"%s\"}",
                userId, shopId, sn, SecurityUtils.md5(userId), channelId);
    }

}
