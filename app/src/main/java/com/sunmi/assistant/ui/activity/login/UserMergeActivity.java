package com.sunmi.assistant.ui.activity.login;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * 账号合并
 */
@EActivity(resName = "activity_user_merge")
public class UserMergeActivity extends BaseActivity {

    @ViewById(resName = "webView")
    WebView webView;

    @Extra
    String url;

    private static String APK_TAG = "&isApk=true";

    @Click(resName = "btnLeft")
    public void onClick(View v) {
        finish();
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        loadWebView(url + APK_TAG);
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                tvTitle.setText(title);
            }
        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        //webSetting.setDomStorageEnabled(true);
        // webSetting.setAppCachePath(cacheDirPath);
        // webSetting.setAppCacheMaxSize(1024*1024*5);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideLoadingDialog();
            }
        });
    }

}
