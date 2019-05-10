package com.sunmi.assistant.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.webview.SMWebView;

@EActivity(R.layout.activity_sunmi_store)
public class SunmiStoreActivity extends BaseActivity {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.webView)
    SMWebView webView;

    @Extra
    String url;


    @AfterViews
    protected void init() {

        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
        webView.setWebViewClient(new WebViewClient());
        loadWebView(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);// 设置允许访问文件数据
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小--关键点
        webView.getSettings().setLoadWithOverviewMode(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new WebViewClient() {
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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                hideLoadingDialog();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
        });
    }


}
