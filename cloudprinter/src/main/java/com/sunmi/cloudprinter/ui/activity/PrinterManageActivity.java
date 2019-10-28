package com.sunmi.cloudprinter.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.PrinterJSCall;
import com.sunmi.cloudprinter.config.PrinterConfig;
import com.sunmi.cloudprinter.constant.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

@EActivity(resName = "activity_printer_manage")
public class PrinterManageActivity extends BaseActivity {
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "webView")
    SMWebView webView;

    @Extra
    String sn;
    @Extra
    String userId;
    @Extra
    String shopId;
    @Extra
    int channelId;

    private static long TOTAL_TIMEOUT = 12_000;

    CountDownTimer timeoutTimer;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        PrinterJSCall jsCall = new PrinterJSCall(userId, shopId, sn, channelId);
        webView.addJavascriptInterface(jsCall, Constants.JS_INTERFACE_NAME);
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
        loadWebView(PrinterConfig.IOT_H5_URL);
    }

    @Click(resName = "img_back")
    public void backClick() {
        onBackPressed();
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
                timerStart();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                timerCancel();
            }


            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideLoadingDialog();
                timerCancel();
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                           SslError error) {
                hideLoadingDialog();
                timerCancel();
                super.onReceivedSslError(view, handler, error);
            }
        });
    }

    private void timerStart() {
        if (timeoutTimer == null) {
            timeoutTimer = new CountDownTimer(TOTAL_TIMEOUT, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished < 4000) {
                        shortTip(R.string.tip_network_poor);
                    }
                }

                @Override
                public void onFinish() {
                    if (PrinterManageActivity.this.webView.getProgress() < 100) {
                        hideLoadingDialog();
                        loadPageTimeout();
                    }
                }
            };
        }
        timeoutTimer.start();
    }

    private void timerCancel() {
        hideLoadingDialog();
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
        }
    }

    @UiThread
    void loadPageTimeout() {
        new CommonDialog.Builder(this)
                .setTitle(R.string.tip_load_page_timeout)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

}
